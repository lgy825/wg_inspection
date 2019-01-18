package com.raisecom.util;

import com.raisecom.ems.templet.client.util.SnmpUtilities;
import com.raisecom.ems.templet.server.driver.Driver;
import com.raisecom.nms.platform.cnet.ObjService;

/**
 * Created by ligy-008494 on 2019/1/17.
 */
public class SnmpOperationForONU {


    //获取ONU的在线状态
    public static String getONUStatusForParam(String instance, ObjService objService){
        ObjService snmpParams = objService.clone();
        String tableName="";
        String iRcnetypeId=snmpParams.getStringValue("iRcnetypeId");
        if("EPON_onu".equals(iRcnetypeId) || "UNKOW(E)".equals(iRcnetypeId)){
            tableName="rcEponONUTable";
        }else if("EPON_onu".equals(iRcnetypeId) || "UNKOW(E)".equals(iRcnetypeId)){
            tableName="rcGponONUTable";
        }
        snmpParams.setValue("TableName",tableName);
        ObjService instances = new ObjService("Instance");
        instances.setValue(instance,"");
        snmpParams.addContainedObject(instances);
        snmpParams.setValue("ValueOnly",true);
        ObjService result = SnmpUtilities.SnmpOperation(snmpParams,"getValue");
        return null;
    }

    /**
     * 读取表的值
     * @param table
     * @param index
     * @return
     */
    private static ObjService getMibNodesFromOLT(String table, String index,ObjService options) {
        // TODO Auto-generated method stub
        try{
            ObjService snmpParams =options.clone();

            String tableName = table;
            snmpParams.setValue("TableName", tableName);
            snmpParams.setValue("ValueOnly", "true");
            if(!"".equals(index)){
                snmpParams.setValue("Instance", index);
            }
            Driver driver = Driver.getDriver(snmpParams);
            ObjService res = driver.getValue(snmpParams);
            if(res==null){
                return null;
            }
            int size = res.objectSize("RowSet");
            if(size==0){
                return null;
            }else{
                return res;
            }
        }catch (Exception e){
            return null;
        }
    }
}
