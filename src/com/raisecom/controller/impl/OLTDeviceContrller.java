package com.raisecom.controller.impl;

import com.raisecom.bean.Rcnetnode;
import com.raisecom.concurrent.DeviceStatisticOperatorThread;
import com.raisecom.concurrent.XPONThreadPool;
import com.raisecom.controller.DeviceTask;
import com.raisecom.db.InitSelfmDBPoolTask;
import com.raisecom.db.JdbcUtils_DBCP;
import com.raisecom.ems.templet.server.driver.*;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.util.EPONCommonDBUtil;
import com.raisecom.util.EPONConstants;

import java.sql.*;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by ligy-008494 on 2018/11/20.
 */
public class OLTDeviceContrller implements DeviceTask {

    @Override
    public boolean processStatistics(List<String> addres) {
        if(addres==null){
            return false;
        }
        //1.通过Ip获取所要巡检的OLT设备信息
        List<ObjService> rcnetnodes=getOLTInfoByIP(addres);
        //ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        XPONThreadPool xponPool = XPONThreadPool.getNewPool("DeviceStatistic", 5);
        String configFile = "";
        Map<String,Future<Boolean>> results = new HashMap<String,Future<Boolean>>();
        if(addres!=null){
             for(ObjService rcnetnode:rcnetnodes){
                 String softVer=rcnetnode.getStringValue("SOFTWARE_VER");
                 String ver=getVersBySoftVer(softVer);
                 String ip=rcnetnode.getStringValue("IPADDRESS");
                 if(ver.startsWith("2.") || ver.startsWith("3.")){
                     configFile= "com/raisecom/profile/2.X/oltStatisticsConfig_Mib.xml";
                 }else{
                     configFile= "com/raisecom/profile/1.X/oltStatisticsConfig_Mib.xml";
                 }
                 rcnetnode.setValue("configFile",configFile);
                 Future<Boolean> fs = xponPool.submitTask(new DeviceStatisticOperatorThread(rcnetnode));
                 results.put(ip,fs);
             }
        }
        boolean flag = false;
        for (String key :results.keySet()) {//get()操作会等待线程完成
            try {
                Future<Boolean> fs = results.get(key);
                flag = fs.get();
                if (flag == false) {
                    continue;
                }
            }catch (InterruptedException e) {
                return false;
            }catch (ExecutionException e) {
                return false;
            }
        }
        xponPool.shutDown();
        return true;
    }

    public static List<ObjService> getOLTInfoByIP(List<String> addres){

        List<ObjService> list=new ArrayList<>();
        for(String addr:addres){
            String sql="SELECT rn.iRCNETypeID, rn.READCOM, rn.WRITECOM, rn.IRCNETNODEID, rn.VERS, rn.TIMEOUT, rn.PORT, rn.IPADDRESS, rn.RETRY,rn.SOFTWARE_VER,rn.FRIENDLY_NAME,rn.HOSTNAME" +
                    " FROM rcnetnode AS rn,rcnetype AS rt WHERE rn.IPADDRESS = '"+addr+"'" +
                    " AND rn.managed_mode = '1'" +
                    " AND rn.iRCNETypeID = rt.iRCNETypeID" +
                    " AND rt.NE_CATEGORY_ID = 1;";
            ObjService objService=EPONCommonDBUtil.selectDataByParam(sql);
            list.add(objService);
        }
        return list;
    }


    public static String  getVersBySoftVer(String softVer){
        String ver="";
        if(softVer!=null){
            String[] temp=softVer.split("ROAP_");
            ver=temp[1];
        }
        return ver;
    }

}
