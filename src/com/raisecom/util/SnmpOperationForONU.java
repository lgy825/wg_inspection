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
            if(list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    snmpParams.setValue("ValueOnly", true);
                    gponresult = getMibNodesFromONU(tableName, list.get(i), snmpParams);
                }
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
    //ONU下挂 MAC 地址数
    public static String getONUHangMacConut(String instance, String iRCNETypeID, ObjService options) {
        ObjService snmpParams = options.clone();
        String tableName = "";
        String hangCount = "";
        ObjService gponresult = new ObjService();
        //ObjService eponResult = new ObjService();
        if ("GPON_ONU".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = "rcGponOnuMacAddrTable";
            List<String> list = getDeviceInstance((ObjService) options.clone(), IfIndexHelperGp.getPortInstance(instance, 0), ".1.3.6.1.4.1.8886.18.3.6.5.1.1.1");
            if(list != null && list.size() > 0){
                for (int i = 0; i < list.size(); i++) {
                    snmpParams.setValue("ValueOnly", true);
                    gponresult = getMibNodesFromONU(tableName, list.get(i), snmpParams);
                }
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

    //获取环路端口索引
    public static String getONULoopPort(String onuInstance, String iRCNETypeID, ObjService options) {
        ObjService snmpParams = options.clone();
        String baseOID = "";
       if ("GPON_onu".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
           baseOID = ".1.3.6.1.4.1.8886.18.3.6.22.1.1.1";
        }else{
           baseOID = ".1.3.6.1.4.1.8886.18.2.6.10.1.1.1";
        }
        List<String> portIdList = getDeviceInstance(snmpParams, IfIndexHelperGp.getPortInstance(onuInstance, 0), baseOID);
        String str="";
        for(int i=0;i<portIdList.size();i++){
            str+=IfIndexHelper.getPortIdFromPortIndex(portIdList.get(i));
            if(i<portIdList.size()-1){
                str+=",";
            }
        }
        return str;
    }

    //获取端口状态
    public static String getONUPortStatus(String onuInstance, String iRCNETypeID, ObjService options) {
        String baseOID="";
        String tableName="";
        ObjService snmpParams = options.clone();
        if ("GPON_onu".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            baseOID = ".1.3.6.1.4.1.8886.18.3.6.5.1.1.1";
            tableName="rcGponOnuEthPortTable";
        }else{
            baseOID = ".1.3.6.1.4.1.8886.18.2.6.3.1.1.1";
            tableName="rcEponOnuCtcEthPortTable";
        }
        List<String> portIndexs = getDeviceInstance(snmpParams, IfIndexHelperGp.getPortInstance(onuInstance, 0), baseOID);
        //根据索引获取相应的值
        //snmpParams.setValue("ValueOnly", true);
        ObjService result=null;
        String str="";
        ObjService params=options.clone();
        if(portIndexs.size()>0){
            for(int i=0;i<portIndexs.size();i++){
                String portIndex=portIndexs.get(i);
                result = getMibNodesFromONU(tableName, portIndex, params);
                if(result!=null){
                    ObjService portInfo=result.objectAt("RowSet",0);
                    String index="";
                    String state="";
                    String speed="";
                    if("rcEponOnuCtcEthPortTable".equalsIgnoreCase(tableName)){
                        index=portInfo.getStringValue("rcEponOnuEthPortIndex");
                        state=portInfo.getStringValue("rcEponOnuCtcEthPortAdminState");
                        speed=portInfo.getStringValue("rcEponOnuEthPortDuplexSpeedSet");
                    }else{
                        index=portInfo.getStringValue("rcGponOnuEthPortIndex");
                        state=portInfo.getStringValue("rcGponOnuEthPortAdminState");
                        speed=portInfo.getStringValue("rcGponOnuEthPortDuplexSpeedGet");
                    }
                    index=IfIndexHelper.getPortDisplayName(index);
                    if("0".equalsIgnoreCase(state)){
                        state="unknown";
                    }else if("1".equalsIgnoreCase(state)){
                        state="disable";
                    }else if ("2".equalsIgnoreCase(state)){
                        state="enable";
                    }
                    if(getDuplexSpeed().containsKey(speed)){
                        speed=getDuplexSpeed().get(speed);
                    }
                    str+="portId:"+index+" "+"LINK:"+state+" "+"Speed:"+speed+"\r\n";
                }
            }
        }
        return str;
    }
    public static List<String> getDeviceInstance(ObjService params, String onuInstance, String oid){

        ArrayList<String> list = new ArrayList();
        boolean flag = true;

        ObjService result = new ObjService();
        String instance = onuInstance;

        while(true){
            params.remove("RowSet");
            ObjService rowSet = new ObjService("RowSet");
            rowSet.setValue(oid + "." + instance,"");
            params.addContainedObject(rowSet);
            ObjService res=GeneralSnmpOperator.snmpGetNext(params);
            if(!res.getStringValue("ErrCode").equalsIgnoreCase("0")){
                break;
            }
            ObjService obj = res.objectAt("RowSet", 0);
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

    //
    public static Map<String,String>  getDuplexSpeed(){

        Map<String,String> map=new HashMap<>();
        map.put("1","auto");
        map.put("2","half_10");
        map.put("3","full_10");
        map.put("4","half_100");
        map.put("5","full_100");
        map.put("6","half_1000");
        map.put("7","full_1000");
        map.put("8","1000");

        return map;
    }
}
