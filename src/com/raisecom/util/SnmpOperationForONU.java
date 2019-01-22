package com.raisecom.util;

import com.raisecom.adapter.IfIndexHelperGp;
import com.raisecom.ems.templet.client.util.SnmpUtilities;
import com.raisecom.ems.templet.server.driver.Driver;
import com.raisecom.ems.templet.server.snmp.GeneralSnmpOperator;
import com.raisecom.nms.platform.cnet.ObjService;

import java.util.*;

/**
 * Created by ligy-008494 on 2019/1/17.
 */
public class SnmpOperationForONU {


    //获取ONU的在线状态
    public static String getONUStatusForParam(String instance, String iRCNETypeID, ObjService options) {
        String ver = options.getStringValue("ver");
        if(ver.startsWith("2.") || ver.startsWith("3.")){
            instance = IfIndexHelperV2.OnuIndexOld2New(instance);
        }
        ObjService snmpParams = options.clone();
        String tableName = "";
        String status = "";
        if ("GPON_ONU".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = "rcGponONUTable";
        } else {
            tableName = "rcEponONUTable";
        }
        snmpParams.setValue("ValueOnly", true);
        ObjService result = getMibNodesFromONU(tableName, instance, snmpParams);
        if (result == null) {
            return "";
        } else {
            for (int i = 0; i < result.objectSize("RowSet"); i++) {
                if (tableName.equals("rcGponONUTable")) {
                    status = result.objectAt("RowSet", i).getStringValue("rcGponONUWorkStatus");
                } else {
                    status = result.objectAt("RowSet", i).getStringValue("rcEponONUWorkStatus");
                }
            }
            return status;
        }
    }


    /**
     * 读取表的值
     *
     * @param table
     * @param index
     * @return
     */
    private static ObjService getMibNodesFromONU(String table, String index, ObjService options) {
        // TODO Auto-generated method stub

        try {
            ObjService snmpParams = options.clone();

            String tableName = table;
            snmpParams.setValue("TableName", tableName);
            snmpParams.setValue("ValueOnly", "true");
            if (!"".equals(index)) {
                snmpParams.setValue("Instance", index);
            }
            Driver driver = Driver.getDriver(snmpParams);
            ObjService res = driver.getValue(snmpParams);
            if (res == null) {
                return null;
            }
            int size = res.objectSize("RowSet");
            if (size == 0) {
                return null;
            } else {
                return res;
            }
        } catch (Exception e) {
            return null;
        }
    }

    //获取ONU上一次掉线原因
    public static String getONULastDownCause(String instance, String iRCNETypeID, ObjService options) {
        String ver = options.getStringValue("ver");
        if(ver.startsWith("2.") || ver.startsWith("3.")){
            instance = IfIndexHelperV2.OnuIndexOld2New(instance);
        }
        ObjService snmpParams = options.clone();
        String tableName = "";
        String lastDownCause = "";
        if ("GPON_onu".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = "rcGponONUTable";
        } else {
            tableName = "rcEponONUTable";
        }
        snmpParams.setValue("ValueOnly", true);
        ObjService result = getMibNodesFromONU(tableName, instance, snmpParams);
        if (result == null) {
            return "";
        } else {
            for (int i = 0; i < result.objectSize("RowSet"); i++) {
                if (tableName.equals("rcGponONUTable")) {
                    lastDownCause = gponOffnlineReason(result.objectAt("RowSet", i).getStringValue("rcGponONUOfflineReason"));
                } else {
                    lastDownCause = eponOffnlineReason(result.objectAt("RowSet", i).getStringValue("rcEponONUOfflineReason"));
                }
            }
            return lastDownCause;
        }
    }

    //ONU接收到的光功率
    public static String getONUReceivedPower(String instance, String iRCNETypeID, ObjService options) {

        ObjService snmpParams = options.clone();
        String tableName = "";
        String receivedPower = "";
        ObjService gponresult = new ObjService();
        //ObjService eponResult = new ObjService();
        if ("GPON_ONU".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {

            tableName = "rcGponOnuPonMngTable";
            List<String> list = getDeviceInstance((ObjService) options.clone(), IfIndexHelperGp.getPortInstance(instance, 0), ".1.3.6.1.4.1.8886.18.3.6.3.1.1.1");
            for (int i = 0; i < list.size(); i++) {
                snmpParams.setValue("ValueOnly", true);
                snmpParams.setValue("Instance", list.get(i));
                gponresult = getMibNodesFromONU(tableName, list.get(i), snmpParams);
            }
            if (gponresult == null) {
                return "";
            } else {
                for (int j = 0; j < gponresult.objectSize("RowSet"); j++) {
                    receivedPower = gponresult.objectAt("RowSet", j).getStringValue("rcGponOnuPonRxPower");
                }
                return receivedPower;
            }

        } else {
            tableName = "rcEponTransceiverMonitorTable";
            instance = "5"+"."+instance;
            ObjService eponResult = getMibNodesFromONU(tableName, instance, snmpParams);
            if (eponResult == null) {
                return "";
            } else {
                for (int i = 0; i < eponResult.objectSize("RowSet"); i++) {
                    receivedPower = eponResult.objectAt("RowSet", i).getStringValue("rcEponTransceiverMonitorValue");
                }
                return receivedPower;
            }
        }

}
    //ONU下挂MAC 地址数
    public static String getONUHangMacConut(String instance, String iRCNETypeID, ObjService options) {
        ObjService snmpParams = options.clone();
        String tableName = "";
        String hangCount = "";
        ObjService gponresult = new ObjService();
        //ObjService eponResult = new ObjService();
        if ("GPON_ONU".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = "rcGponOnuMacAddrTable";
            List<String> list = getDeviceInstance((ObjService) options.clone(), IfIndexHelperGp.getPortInstance(instance, 0), ".1.3.6.1.4.1.8886.18.3.6.3.1.1.1");
            for (int i = 0; i < list.size(); i++) {
                snmpParams.setValue("ValueOnly", true);
                snmpParams.setValue("Instance", list.get(i));
                gponresult = getMibNodesFromONU(tableName, list.get(i), snmpParams);
            }
            if (gponresult == null) {
                return "";
            } else {
                for (int j = 0; j < gponresult.objectSize("RowSet"); j++) {
                    hangCount = gponresult.objectAt("RowSet", j).getStringValue("rcGponOnuMacAddrCount");
                }
                return hangCount;
            }

        } else {
            tableName = "rcEponOnuMacStatTable";
            ObjService eponResult = getMibNodesFromONU(tableName, instance, snmpParams);
            if (eponResult == null) {
                return "";
            } else {
                for (int i = 0; i < eponResult.objectSize("RowSet"); i++) {
                    hangCount = eponResult.objectAt("RowSet", i).getStringValue("rcEponOnuMacNum");
                }
                return hangCount;
            }
        }


    }

    public static String getONULoopPort(String onuInstance, String iRCNETypeID, ObjService options) {
        ObjService snmpParams = options.clone();
        String tableName = "";
        String loopPort="";
       if ("GPON_onu".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = " rcGponOnuLoopbackDetectionPortTable";
        }else{
            tableName = "rcEponOnuCtcLoopDetectPortTable";
        }
        String  instance=IfIndexHelper.getPortInstance(onuInstance,0+"");
        snmpParams.setValue("TableName", tableName);
        snmpParams.setValue("ValueOnly", "true");
        List<String> list=new ArrayList<>();
        String baseOID = ".1.3.6.1.4.1.8886.18.2.6.10.1.1.1";
        StringBuilder sb = new StringBuilder(instance);
        String ins = sb.replace(5,7,"000").toString();
        while(true){
            snmpParams.remove("RowSet");
            ObjService rowSet = new ObjService("RowSet");
            rowSet.setValue(baseOID + "." + ins,"");
            snmpParams.addContainedObject(rowSet);
            ObjService res=GeneralSnmpOperator.snmpGetNext(snmpParams);
            if (res.getStringValue("ErrCode").equalsIgnoreCase("0")) {
                ObjService objService1=res.objectAt("RowSet",0);
                String key = objService1.getCurrentHashtable().keySet().toArray()[0].toString();
                String index=objService1.getStringValue(key);
                ins=index;
                if(instance.startsWith(index.substring(0,3))){}
                if(!key.startsWith(baseOID)){
                    break;
                }
                if(index==null || "NULL".equalsIgnoreCase(index)){
                    break;
                }else{
                    String retStr=IfIndexHelper.getPortIdFromPortIndex(index);
                    list.add(retStr);
                }
            }else{
                break;
            }
        }
        if(list==null){
            return "--";
        }else{
            String str="";
            for(int i=0;i<list.size();i++){
                str+=list.get(i);
                if(i<list.size()-1){
                    str+=",";
                }
            }
            return str;
        }
    }

    public static String getONUPortStatus(String instance, String iRCNETypeID, ObjService options) {
        return null;
    }
    public static List<String> getDeviceInstance(ObjService params, String onuInstance, String oid){

        ArrayList<String> list = new ArrayList<String>();
        boolean flag = true;

        ObjService result = new ObjService();
        String instance = onuInstance;

        while(flag){
            params.remove("RowSet");
            ObjService rowset = new ObjService("RowSet");
            rowset.setValue(oid + "." + instance, "");


            params.addContainedObject(rowset);
            result = SnmpUtilities.GeneralSnmpOperation(params, "snmpGetNext");
            if(!result.getStringValue("ErrCode").equalsIgnoreCase("0")){
                break;
            }
            ObjService obj = result.objectAt("RowSet", 0);
            String key = obj.getCurrentHashtable().keySet().toArray()[0].toString();
            if(!key.substring(0, key.lastIndexOf(".")).equalsIgnoreCase(oid)){
                break;
            }
            String compareInstance = onuInstance.substring(0,onuInstance.length()-3);
            String keyInstance = obj.getStringValue(key);
            if(keyInstance.substring(0, keyInstance.length()-3).equalsIgnoreCase(compareInstance)){
                list.add(obj.getStringValue(key));
                instance = obj.getStringValue(key);
            }else
                break;
        }
        return list;
    }

    public static String eponOffnlineReason(String lastDownCause) {
        Map<String, String> map = new HashMap();
        String templastDownCause = "";

        map.put("1", "unknown");
        map.put("2", "dyingGasp");
        map.put("3", "backboneFiberCut");
        map.put("4", "branchFiberCut");
        map.put("5", "oamDisconnect");
        map.put("6", "duplicateReg");
        map.put("7", "oltDeregOperation");
        map.put("8", "rttTooBig");
        map.put("9", "timeDriftTooBig");
        map.put("10", "rateLimitDrop");
        map.put("11", "onuRequest");
        map.put("12", "mpcpTimeout");
        map.put("13", "firstBackboneFiberCut");
        map.put("14", "firstBranchFiberCut");
        map.put("15", "secondBackboneFiberCut");
        map.put("16", "secondBranchFiberCut");
        map.put("17", "secondFirstBackboneFiberCut");
        if (map.containsKey(lastDownCause)) {
            templastDownCause = map.get(lastDownCause);
        }
        return templastDownCause;
    }
    public static String gponOffnlineReason(String lastDownCause){
        Map<String,String> map = new HashMap<String,String>();
        String templastDownCause ="";

        map.put("1","unknown");
        map.put("2","fwClicmd");
        map.put("3","hostRequest");
        map.put("4","alarm");
        map.put("5","unexpectedSnPlm");
        map.put("6","dyingGaspReceived");
        map.put("7","emergencyStop");
        map.put("8","wrongOnuId");
        map.put("9","ploamError");
        map.put("10","rangingFlag");
        map.put("11","duplicatedOnuId");
        map.put("12","backboneFiberCut");
        map.put("13","branchFiberCut");
        map.put("14","firstBackboneFiberCut");
        map.put("15","firstBranchFiberCut");
        map.put("16","secondBackboneFiberCut");
        map.put("17","secondBranchFiberCut");
        map.put("18","secondFirstBackboneFiberCut");
        map.put("19","noFindSuitableAutoAuthProfile");
        map.put("20","acktimeout");
        map.put("21","sfi");
        map.put("22","tiwi");
        map.put("23","passwordauthentication");
        map.put("24","onualarm");
        map.put("25","loki");
        map.put("26","rerangefailure");
        map.put("27","userReboot");
        map.put("28","rogueOnu");
        if(map.containsKey(lastDownCause)){
            templastDownCause = map.get(lastDownCause);
        }
        return templastDownCause ;
    }
}
