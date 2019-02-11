package com.raisecom.controller.impl;

import com.raisecom.bean.Contact;
import com.raisecom.concurrent.CardDevicestatisticThread;
import com.raisecom.concurrent.DeviceStatisticOperatorThread;
import com.raisecom.concurrent.ONUDeviceStatisticOperatorThread;
import com.raisecom.concurrent.XPONThreadPool;
import com.raisecom.controller.DeviceTask;

import com.raisecom.exportExcelDemo.Main;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.EPONCommonDBUtil;
import com.raisecom.util.ParseAdapterUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by ligy-008494 on 2018/11/20.
 */
public class OLTDeviceContrller implements DeviceTask {

    @Override
    public boolean processStatistics(Contact contact) {
        if(contact==null){
            return false;
        }
        //1.通过Ip获取所要巡检的OLT设备信息
        List<ObjService> rcnetnodes=getOLTInfoByIP(contact.getIpAddr());

        XPONThreadPool xponPool = XPONThreadPool.getNewPool("DeviceStatistic", 10);
        String configFile = "";
        Map<String,Future<Boolean>> results = new HashMap();
        List<String> oltIds=new ArrayList<>();
        if(rcnetnodes!=null){
             for(ObjService rcnetnode:rcnetnodes){
                 if(rcnetnode.storeValue.size()>0){
                     String softVer=rcnetnode.getStringValue("SOFTWARE_VER");
                     String ver= ParseAdapterUtil.getVersBySoftVer(softVer);
                     String ip=rcnetnode.getStringValue("IPADDRESS");
                     oltIds.add(rcnetnode.getStringValue("IRCNETNODEID"));
                     if(ver.startsWith("2.") || ver.startsWith("3.")){
                         configFile= "com/raisecom/profile/2.X/oltStatisticsConfig_Mib.xml";
                     }else{
                         configFile= "com/raisecom/profile/1.X/oltStatisticsConfig_Mib.xml";
                     }
                     rcnetnode.setValue("configFile",configFile);
                     Future<Boolean> fs=null;
                     if("OLT".equalsIgnoreCase(contact.getInspectType())){
                         fs = xponPool.submitTask(new DeviceStatisticOperatorThread(rcnetnode));
                     }else if("ONU".equalsIgnoreCase(contact.getInspectType())){
                         fs = xponPool.submitTask(new ONUDeviceStatisticOperatorThread(rcnetnode));
                     }else if("CARD".equalsIgnoreCase(contact.getInspectType())){
                         fs = xponPool.submitTask(new CardDevicestatisticThread(rcnetnode));
                     }
                     results.put(ip,fs);
                 }

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
        if("OLT".equalsIgnoreCase(contact.getInspectType())){
            //Main.FromDbToExcel(oltIds);
        }else if("ONU".equalsIgnoreCase(contact.getInspectType())){
            //Main.FromDBToONUExcel("2125");
        }else{
            //Main.FromDBToCardExcel("2125");
        }
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






}
