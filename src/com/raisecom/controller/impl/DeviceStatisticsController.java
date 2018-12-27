package com.raisecom.controller.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.raisecom.concurrent.DeviceStatisticOperatorThread;
import com.raisecom.concurrent.XPONThreadPool;
import com.raisecom.nms.platform.client.ResourceManager;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.util.ErrorObjService;
import com.raisecom.util.EPONCommonDBUtil;
import com.raisecom.util.EPONConstants;
import com.raisecom.util.VersionUtil;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class DeviceStatisticsController  {

    private static DeviceStatisticsController deviceStatisticsController = new DeviceStatisticsController();
    private String configFile = "com/raisecom/profile/";
    private static Map<String, String> bCardTypeMap = new HashMap<>();
    private static Map<String, String> cCardTypeMap = new HashMap<>();

    public static DeviceStatisticsController getInstance(){
        return deviceStatisticsController;
    }

    private DeviceStatisticsController(){

    }

    public ErrorObjService processStatistics4Olt(ObjService objs)
    {
        if(bCardTypeMap.isEmpty())
            cacheBussinessCardType();
        if(cCardTypeMap.isEmpty())
            cacheControlCardType();
        String netype = "";
        String ip = "";
        String version = "";
        String oltid ="";
        boolean flag = false;
        ErrorObjService result = new ErrorObjService();

        XPONThreadPool xponPool = XPONThreadPool.getNewPool("DeviceStatistic", 5);
        Map<String,Future<Boolean>> results = new HashMap<>();
        ObjService obj = new ObjService();
        clearTable("NE_REMOTE_RESOURCE_RESULT");
        clearTable("NE_REMOTE_RESOURCE");
        for (int i = 0; i < objs.objectSize("RowSet"); i++) {
            obj = objs.objectAt("RowSet", i);
            oltid = obj.getStringValue("IRCNETNODEID");
            deleteData("OLT_STATISTICS_INFO","IRCNETNODEID",oltid);
            ip = obj.getStringValue("IPADDRESS");
            netype = obj.getStringValue("iRCNETypeID");
            version = obj.getStringValue("version");
            //更改后的树上没有了version信息，这里查询
            if("".equals(version)){
                version = VersionUtil.getOltVersionByNeID(oltid);
            }
            if(version.startsWith("2.") || version.startsWith("3.")){
                configFile= "com/raisecom/profile/2.X/oltStatisticsConfig_Mib.xml";
            }else{
                configFile= "com/raisecom/profile/1.X/oltStatisticsConfig_Mib.xml";
            }
//            Future<Boolean> fs = xponPool.submitTask(new DeviceStatisticOperatorThread(oltid,ip,netype,version,bCardTypeMap, cCardTypeMap,configFile));
//            results.put(ip,fs);
        }

        for (String key :results.keySet()) {//get()操作会等待线程完成
            try {
                Future<Boolean> fs = results.get(key);
                flag = fs.get();
                if(flag == false){
                    //changj ， 因为远程调用会超时，所以这里返回的值没有意义
                    EPONConstants.logger.error("Class:"+this.getClass().getName()+" startDoStatistic4Olt "+key +" failed!");
                    continue;
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                EPONConstants.logger.error("DeviceStatistics-- Class:"+this.getClass().getName()+" startDoStatistic4Olt "+key+" exception!e:"+e);
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                EPONConstants.logger.error("DeviceStatistics-- Class:"+this.getClass().getName()+" startDoStatistic4Olt "+key+" exception!e:"+e);
            }
        }

        xponPool.shutDown();
        clearTable("OLT_STATISTICS_SUMMARY");
        insertOLTStatisticsSummary();
        return result;

    }
    private void insertOLTStatisticsSummary() {
        // TODO Auto-generated method stub
        String sqlBase = "select count(1) from OLT_STATISTICS_INFO where ";
        String condition4Smc = "SMC = '"+ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "SINGE_SMC")+"'";
        String tem = EPONCommonDBUtil.getInstance().getEponMaxTemperature();
        String condition4Temperature = "TEMPERATURE >"+tem;
        String condition4Power = "POWER = '"+ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "SINGLE_PWR")+"'";
        String condition4Nofan =  "FAN = '"+ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "NO_FAN")+"'";
        String condition4CpldFan =  "FAN = '"+ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "CPLD_LOW_VERSION")+"'";
        String ram = EPONCommonDBUtil.getInstance().getEponMaxMemory();
        String condition4Memory = "RAM >" + ram;
        String result4Smc = "";
        String result4Temperature = "";
        String result4Power = "";
        String result4Nofan = "";
        String result4CpldFan = "";
        String result4Menory = "";
        try {
            ObjService objOlt = EPONCommonDBUtil.getInstance().selectObject(sqlBase+condition4Smc, null).objectAt("RowObj", 0);
            result4Smc = objOlt.getStringValue("count(1)");
            objOlt = EPONCommonDBUtil.getInstance().selectObject(sqlBase+condition4Temperature, null).objectAt("RowObj", 0);
            result4Temperature = objOlt.getStringValue("count(1)");
            objOlt = EPONCommonDBUtil.getInstance().selectObject(sqlBase+condition4Power, null).objectAt("RowObj", 0);
            result4Power = objOlt.getStringValue("count(1)");
            objOlt = EPONCommonDBUtil.getInstance().selectObject(sqlBase+condition4Nofan, null).objectAt("RowObj", 0);
            result4Nofan = objOlt.getStringValue("count(1)");
            objOlt = EPONCommonDBUtil.getInstance().selectObject(sqlBase+condition4CpldFan, null).objectAt("RowObj", 0);
            result4CpldFan = objOlt.getStringValue("count(1)");
            objOlt = EPONCommonDBUtil.getInstance().selectObject(sqlBase+condition4Memory, null).objectAt("RowObj", 0);
            result4Menory = objOlt.getStringValue("count(1)");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.error("DeviceStatistics-- Class:"+this.getClass().getName()+ " insertOLTStatisticsSummary excetion!");
        }
        String sql = "insert into OLT_STATISTICS_SUMMARY (INFO,SSMC,HTEMPERATURE,SPOWER,NFAN,LFAN) values ('"+
                ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "DEVICE_AMOUNT")+"','"+result4Smc+"','"+result4Temperature+"','"+result4Power+"','"+result4Nofan+"','"+result4CpldFan+"')";
        try {
            EPONCommonDBUtil.getInstance().executeSql(sql, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.error("DeviceStatistics-- Class:"+this.getClass().getName()+ " excute sql :" + sql +" exception!");
        }
    }

    /**
     * 将数据库中的业务板卡类型对应关系缓存起来
     */
    private void cacheBussinessCardType() {
        // TODO Auto-generated method stub
        String sql = "select CARD_TYPE_ID,CARD_TYPE_NAME  from card_type where CARD_BUSINESS_TYPE_ID in (2,5,6,7,8)";
        String card_id = "";
        String card_name = "";
        try {
            ObjService objcard = EPONCommonDBUtil.getInstance().selectObject(sql, null);
            for (int i = 0; i < objcard.objectSize("RowObj"); i++) {
                card_id = objcard.objectAt("RowObj", i).getStringValue("CARD_TYPE_ID");
                card_name = objcard.objectAt("RowObj", i).getStringValue("CARD_TYPE_NAME");
                bCardTypeMap.put(card_id,card_name);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.error("DeviceStatistics-- Class:"+this.getClass().getName()+" cacheBussinessCardType excetion!"+e);
        }

    }
    /**
     * 将数据库中的主控板卡类型对应关系缓存起来
     */
    private void cacheControlCardType() {
        // TODO Auto-generated method stub
        String sql = "select CARD_TYPE_ID,CARD_TYPE_NAME  from card_type where CARD_BUSINESS_TYPE_ID = 9";
        String card_id = "";
        String card_name = "";
        try {
            ObjService objcard = EPONCommonDBUtil.getInstance().selectObject(sql, null);
            for (int i = 0; i < objcard.objectSize("RowObj"); i++) {
                card_id = objcard.objectAt("RowObj", i).getStringValue("CARD_TYPE_ID");
                card_name = objcard.objectAt("RowObj", i).getStringValue("CARD_TYPE_NAME");
                cCardTypeMap.put(card_id,card_name);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.error("DeviceStatistics-- Class:"+this.getClass().getName()+" cacheControlCardType excetion!"+e);
        }

    }


    /**
     * 清除掉表中的数据
     * @param string
     */
    private void clearTable(String tablename) {
        // TODO Auto-generated method stub
        String sql = "TRUNCATE TABLE "+tablename;
        try {
            EPONCommonDBUtil.getInstance().executeSql(sql, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.error("DeviceStatistics-- Class:"+this.getClass().getName()+" excute sql:"+sql + " exception : "+e);
        }
    }

    private void deleteData(String tablename, String key, String value ) {
        // TODO Auto-generated method stub
        String sql = "delete from "+tablename+" where "+key+ "= '"+value+"'";
        try {
            EPONCommonDBUtil.getInstance().executeSql(sql, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.error("DeviceStatistics-- Class:"+this.getClass().getName()+" excute sql:"+sql + " exception : "+e);
        }
    }
}

