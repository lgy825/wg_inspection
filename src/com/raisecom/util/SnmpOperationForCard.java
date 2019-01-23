package com.raisecom.util;

import com.raisecom.ems.templet.server.driver.Driver;
import com.raisecom.nms.platform.cnet.ObjService;

/**
 * Created by ligy-008494 on 2019/1/23.
 */
public class SnmpOperationForCard {

    //获取板卡的cpu使用情况
    public static String getCardCpu(ObjService objService,String slotId){
        String instance=slotId+".0";
        String tableName="rcCpuMonTable";
        ObjService params=objService.clone();
        ObjService result=getMibNodesFromCard(tableName,instance,params);
        if(result==null){
            return "";
        }else{
            String cpu=result.objectAt("RowSet", 0).getStringValue("rcCpuUsage1Second");
            return cpu;
        }
    }

    //获取板卡的内存使用率
    public static String getRamUsage(ObjService objService,String slotId){
        String instance=slotId+".0";
        String tableName="rcCpuMonTable";
        ObjService params=objService.clone();
        ObjService result=getMibNodesFromCard(tableName,instance,params);
        if(result==null){
            return "";
        }else{
            String total=result.objectAt("RowSet", 0).getStringValue("rcMemoryTotoalMemory");
            String available=result.objectAt("RowSet", 0).getStringValue("rcMemoryAvailableMemory");
            String usage="";
            if((total!=null && !"NULL".equalsIgnoreCase(total))  &&  (available!=null && !"NULL".equalsIgnoreCase(available))  ){
                usage=Math.ceil((Integer.parseInt(total)-Integer.parseInt(available))/Integer.parseInt(total)*100)+"";
            }
            return usage;
        }
    }


    /**
     * 读取表的值
     *
     * @param table
     * @param index
     * @return
     */
    private static ObjService getMibNodesFromCard(String table, String index, ObjService options) {
        try {
            ObjService snmpParams = options.clone();
            String tableName = table;
            snmpParams.setValue("TableName", tableName);
            snmpParams.setValue("ValueOnly", "true");
            if (!"".equals(index) || index==null) {
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
}
