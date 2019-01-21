package com.raisecom.util;

import com.raisecom.ems.templet.client.util.SnmpUtilities;
import com.raisecom.ems.templet.server.driver.Driver;
import com.raisecom.nms.platform.cnet.ObjService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ligy-008494 on 2019/1/17.
 */
public class SnmpOperationForONU {


    //获取ONU的在线状态
    public static String getONUStatusForParam(String instance, String iRCNETypeID, ObjService options) {
        ObjService snmpParams = options.clone();
        String tableName = "";
        String status = "";
        if ("EPON_ONU".equals(iRCNETypeID) || "UNKNOWN(E)".equals(iRCNETypeID)) {
            tableName = "rcEponONUTable";
        } else if ("GPON_onu".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = "rcGponONUTable";
        }
        snmpParams.setValue("ValueOnly", true);
        ObjService result = getMibNodesFromONU(tableName, instance, snmpParams);
        if (result == null) {
            return "";
        } else {
            for (int i = 0; i < result.objectSize("RowSet"); i++) {
                if (tableName.equals("rcEponONUTable")) {
                    status = result.objectAt("RowSet", i).getStringValue("rcEponONUWorkStatus");
                } else {
                    status = result.objectAt("RowSet", i).getStringValue("rcGponONUWorkStatus");
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

        ObjService snmpParams = options.clone();
        String tableName = "";
        String lastDownCause = "";
        if ("EPON_ONU".equals(iRCNETypeID) || "UNKNOWN(E)".equals(iRCNETypeID)) {
            tableName = "rcEponONUTable";
        } else if ("GPON_onu".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = "rcGponONUTable";
        }
        snmpParams.setValue("ValueOnly", true);
        ObjService result = getMibNodesFromONU(tableName, instance, snmpParams);
        if (result == null) {
            return "";
        } else {
            for (int i = 0; i < result.objectSize("RowSet"); i++) {
                if (tableName.equals("rcEponONUTable")) {
                    lastDownCause = eponOffnlineReason(result.objectAt("RowSet", i).getStringValue("rcEponONUOfflineReason"));

                } else {
                    lastDownCause = gponOffnlineReason(result.objectAt("RowSet", i).getStringValue("rcGponONUOfflineReason"));
                }
            }
            return lastDownCause;
        }
    }

    public static String getONUDistance(String instance, String iRCNETypeID, ObjService options) {

        return null;
    }

    public static String getONUReceivedPower(String instance, String iRCNETypeID, ObjService options) {

        ObjService snmpParams = options.clone();
        String tableName = "";
        String receivedPower = "";
        if ("EPON_ONU".equals(iRCNETypeID) || "UNKNOWN(E)".equals(iRCNETypeID)) {
            tableName = "rcEponONUTable";
        } else if ("GPON_onu".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = "rcGponONUTable";
        }
        snmpParams.setValue("ValueOnly", true);
        ObjService result = getMibNodesFromONU(tableName, instance, snmpParams);
        if (result == null) {
            return "";
        } else {
            for (int i = 0; i < result.objectSize("RowSet"); i++) {
                if (tableName.equals("rcEponONUTable")) {
                    receivedPower = eponOffnlineReason(result.objectAt("RowSet", i).getStringValue("rcEponONUOfflineReason"));

                } else {
                    receivedPower = gponOffnlineReason(result.objectAt("RowSet", i).getStringValue("rcGponOnuPonRxPower"));
                }
            }
            return receivedPower;
        }
    }

    public static String getONUHangMacConut(String instance, String iRCNETypeID, ObjService options) {

        return null;
    }

    public static String getONULoopPort(String onuInstance, String iRCNETypeID, ObjService options) {
        ObjService snmpParams = options.clone();
        String tableName = "";
        String loopPort="";
        if ("EPON_ONU".equals(iRCNETypeID) || "UNKNOWN(E)".equals(iRCNETypeID)) {
            tableName = "rcEponOnuCtcLoopDetectPortTable";
        } else if ("GPON_onu".equals(iRCNETypeID) || "UNKNOWN".equals(iRCNETypeID)) {
            tableName = " rcGponOnuLoopbackDetectionPortTable";
        }
        snmpParams.setValue("ValueOnly", true);
        //ObjService result = getMibNodesFromONU(tableName, instance, snmpParams);
        String  instance=IfIndexHelper.getPortInstance(onuInstance,0+"");
        snmpParams.setValue("TableName", tableName);
        snmpParams.setValue("ValueOnly", "true");
        snmpParams.setValue("Instance", instance);
        ObjService objService=null;
        while(true){
            objService=SnmpUtilities.GeneralSnmpOperation(snmpParams, "snmpGetNext");
            if (!objService.getStringValue("ErrCode").equalsIgnoreCase("0")) {
                break;
            }
        }
        return null;

    }

    public static String getONUPortStatus(String instance, String iRCNETypeID, ObjService options) {
        return null;
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
