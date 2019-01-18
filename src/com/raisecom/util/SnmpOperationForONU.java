package com.raisecom.util;

import com.raisecom.ems.templet.client.util.SnmpUtilities;
import com.raisecom.nms.platform.cnet.ObjService;

/**
 * Created by ligy-008494 on 2019/1/17.
 */
public class SnmpOperationForONU {


    //获取ONU的在线状态
    public static String getONUStatusForParam(String instance, ObjService objService){
        ObjService snmpParams = objService.clone();
        String tableName="rcEponONUTable";
        snmpParams.setValue("TableName",tableName);
        ObjService instances = new ObjService("Instance");
        instances.setValue(instance,"");
        snmpParams.addContainedObject(instances);
        snmpParams.setValue("ValueOnly",true);
        ObjService result = SnmpUtilities.SnmpOperation(snmpParams,"getValue");
        return null;
    }
}
