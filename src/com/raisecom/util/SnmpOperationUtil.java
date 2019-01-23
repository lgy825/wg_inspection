package com.raisecom.util;import com.raisecom.ems.templet.server.driver.Driver;import com.raisecom.nms.platform.client.ResourceManager;import com.raisecom.nms.platform.cnet.ObjService;import com.raisecom.nms.snmp.SnmpException;import com.raisecom.nms.snmp.SnmpVar;import java.math.BigDecimal;import java.util.HashMap;import java.util.List;import java.util.Map;import java.util.Vector;/** * Created by ligy-008494 on 2019/1/11. */public class SnmpOperationUtil {    //private ObjService options = null;    //private static  String   configFile = "com/raisecom/profile/";    private static String controlCardID = "";    /**     * 查询OLT温度、板卡温度     * @return     */    public static String getTemperature4OLT(ObjService objService) {        // TODO Auto-generated method stub        ObjService res = getMibNodesFromOLT("raisecomShelfTable","",objService);        if(res==null){            return "";        }else{            return res.objectAt("RowSet", 0).getStringValue("raisecomShelfTemperature");        }    }    /**     * 查询OLT 电源状态     * @param version     * @return     */    public static String getPowerStatus4OLT(ObjService objService) {        // TODO Auto-generated method stub        String state = "";        String slot = "";        String version = objService.getStringValue("version");        Map<String, String> powerMap = new HashMap<String, String>();        int normal = 0;        ObjService res = getMibNodesFromOLT("raisecomPowerOutputTable","",objService);        if(res==null){            return "";        }else{            if(version.startsWith("2.")){                for (int i = 0; i < res.objectSize("RowSet"); i++) {                    state = res.objectAt("RowSet", i).getStringValue("raisecomPowerStatus");                    if("1".equals(state)){                        slot = res.objectAt("RowSet", i).getStringValue("raisecomPowerDeviceIndex");                        powerMap.put(slot, state);                    }                }                return powerMap.size()>1? ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "DOUBLE_PWR"):ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "SINGLE_PWR");            }else{                for (int i = 0; i < res.objectSize("RowSet"); i++) {                    state = res.objectAt("RowSet", i).getStringValue("raisecomPowerStatus");                    if("1".equals(state)){                        normal++;                    }                }                return normal>1?ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "DOUBLE_PWR"):ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "SINGLE_PWR");            }        }    }    /**     * 查询VLAN广播域是否能优化缩小     * @param version     * @return     */    public static String getVlanOptimize(ObjService objService) {        // TODO Auto-generated method stub        String vlanMode = "";        String vlanList = "";        ObjService res = getMibNodesFromOLT("rcVlanCfgPortTable","",objService);        if(res==null){            return "";        }else{            for(int i = 0; i < res.objectSize("RowSet"); i++) {                vlanMode = res.objectAt("RowSet", i).getStringValue("rcPortMode");                if("2".equals(vlanMode)){//trunk mode                    vlanList = res.objectAt("RowSet", i).getStringValue("rcPortTrunkAllowVlanList");                    if(vlanList.contains("1")){                        return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "PART_CAN_OPTIMIZE");                    }                }            }        }        return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "NO_OPTIMIZE");    }    /**     * 判断OLT是否在线     * @return     */    public static boolean isOltOnline(ObjService objService) {        // TODO Auto-generated method stub        ObjService res = getMibNodesFromOLT("rfc1213 System Group","",objService);        if(res==null){            return false;        }else{            return true;        }    }    public  String processResult(String s) {        if(s==null||"".equals(s)||"null".equalsIgnoreCase(s)){            return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "NONE");        }else{            return s;        }    }    /**     * 查询OLT 风扇状态     * @param version     * @return     */    public static String getFanStatus4OLT(ObjService objService) {        // TODO Auto-generated method stub        String state = "";        boolean flag = false;        ObjService res = getMibNodesFromOLT("raisecomFanTable","",objService);        if(res==null){            return "";        }else{            for(int i = 0; i < res.objectSize("RowSet"); i++) {                state = res.objectAt("RowSet", i).getStringValue("raisecomFanStatus");                if("1".equals(state)){                    flag = true;                    break;                }            }        }        String index = Integer.parseInt(controlCardID)*10000000+".0.8";        String ver = "";        res = getMibNodesFromOLT("raisecomSWFileTable",index,objService);        if(res==null){            return "";        }else{            ver=  res.objectAt("RowSet", 0).getStringValue("raisecomSWFileVersion");            ver = ver.substring(1);            try {                if(Float.parseFloat(ver)<1.4){                    return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "CPLD_LOW_VERSION");                }else{                    return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "NO_FAN");                }            } catch (Exception e) {                return "";            }        }    }    /**     * 查询olt版本     * @return     */    public static String getSoftwareVer4OLT(ObjService objService) {        // TODO Auto-generated method stub        ObjService res = getMibNodesFromOLT("SwitchInformation","",objService);        if(res==null){            return "";        }else{            return res.objectAt("RowSet", 0).getStringValue("rcSwitchRoseVersion");        }    }    /**     * 读取表的值     * @param table     * @param index     * @return     */    private static ObjService getMibNodesFromOLT(String table, String index,ObjService options) {        // TODO Auto-generated method stub        try{            ObjService snmpParams =options.clone();            String tableName = table;            snmpParams.setValue("TableName", tableName);            snmpParams.setValue("ValueOnly", "true");            if(!"".equals(index)){                snmpParams.setValue("Instance", index);            }            Driver driver = Driver.getDriver(snmpParams);            ObjService res = driver.getValue(snmpParams);            if(res==null){                return null;            }            int size = res.objectSize("RowSet");            if(size==0){                return null;            }else{                return res;            }        }catch (Exception e){            return null;        }    }    public static ObjService getCpuRamUsage4OLT(ObjService objService) {        // TODO Auto-generated method stub        ObjService result_para = new ObjService();        try {            String netype=objService.getStringValue("NEType");            String version = objService.getStringValue("version");            float verF = Float.valueOf(version);            if(netype.startsWith("ISCOM6820")||netype.startsWith("ISCOM5" +                    "" +                    "508")||netype.startsWith("ISCOM5504")){                result_para = getOneCardOltCpuRam(objService);//不区分GPON、EPON            }else if(verF < new Float(2.0)){                result_para = getEponOlt1xCpuRam(objService);            }else{                result_para = getEponOlt2xCpuRam(objService);            }            return result_para;        } catch (Exception e) {            //EPONConstants.logger.error("###### DeviceStatisticOperatorThread getCpuRamUsage4OLT get CPU and Memory Utilization failed,exception "+e+ " ######");            result_para.setValue("CPU_USAGE", "--");            result_para.setValue("MEMORY_USAGE",  "--");            return result_para;        }    }    /**     * 读取GPON设备CPU RAM     * @param smnpParamters     */    public static ObjService getOneCardOltCpuRam(ObjService objService) {        // TODO Auto-generated method stub        ObjService smnpParamters = objService.clone();        ObjService result_para = new ObjService();        String configFile = "com/raisecom/ems/gpon/server/modules/cfgmgt/profile/2.30/5508GpSwitch.xml";        String tableName = "raisecomShelfTable2";        smnpParamters.setValue("ConfigFile", configFile);        smnpParamters.setValue("TableName", tableName);        smnpParamters.setValue("ValueOnly",true);        //读取主控槽位        Driver driver = Driver.getDriver(smnpParamters);        ObjService results = driver.getValue(smnpParamters);        if(results == null){            result_para.setValue("CPU_USAGE", "--");            result_para.setValue("MEMORY_USAGE",  "--");            return result_para;        }        String errorCode = results.getStringValue("ErrCode");        if("0".equalsIgnoreCase(errorCode))        {            int privarySlot = 0;            int secondSlot = 0;            try{                privarySlot= results.objectAt("RowSet", 0).getIntValue("raisecomShelfPrimaryNMSSlotId");                secondSlot= results.objectAt("RowSet", 0).getIntValue("raisecomShelfSecondaryNMSSlotId");            }catch(Exception e){                result_para.setValue("CPU_USAGE", "--");                result_para.setValue("MEMORY_USAGE",  "--");                return result_para;            }            smnpParamters.setValue("Instance", privarySlot+".0");            tableName = "rcCpuMemoryMonTable";            smnpParamters.setValue("ConfigFile", configFile);            smnpParamters.setValue("TableName", tableName);            results = driver.getValue(smnpParamters);            if(results == null){                result_para.setValue("CPU_USAGE", "--");                result_para.setValue("MEMORY_USAGE",  "--");                return result_para;            }            errorCode = results.getStringValue("ErrCode");            if("0".equalsIgnoreCase(errorCode))            {                String cupUsage = results.objectAt("RowSet", 0).getStringValue("rcCpuUsage1Second");                int totoalMemory =0;                int rcMemoryAvailableMemory =0;                float memUsage =0;                try {                    if(!cupUsage.equals("NULL") || !(results.objectAt("RowSet", 0).getValue("rcMemoryTotoalMemory").equals("NULL")) || !(results.objectAt("RowSet", 0).getValue("rcMemoryAvailableMemory").equals("NULL"))){                        totoalMemory = results.objectAt("RowSet", 0).getIntValue("rcMemoryTotoalMemory");                        rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("rcMemoryAvailableMemory");                        memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;                    }else{                        smnpParamters.setValue("Instance", secondSlot +".0");                        tableName = "raisecomCpuV2Table";                        smnpParamters.setValue("ConfigFile", configFile);                        smnpParamters.setValue("TableName", tableName);                        //读取CPU RAM                        results = driver.getValue(smnpParamters);                        if(results == null){                            result_para.setValue("CPU_USAGE", "--");                            result_para.setValue("MEMORY_USAGE",  "--");                            return result_para;                        }                        errorCode = results.getStringValue("ErrCode");                        if("0".equalsIgnoreCase(errorCode))                        {                            cupUsage = results.objectAt("RowSet", 0).getStringValue("rcCpuUsage1Second");                            if(results.objectAt("RowSet", 0).getStringValue("rcMemoryTotoalMemory").equalsIgnoreCase("NULL")){                                totoalMemory = 0;                            }else{                                totoalMemory = results.objectAt("RowSet", 0).getIntValue("rcMemoryTotoalMemory");                            }                            if(results.objectAt("RowSet", 0).getStringValue("rcMemoryAvailableMemory").equalsIgnoreCase("NULL")){                                rcMemoryAvailableMemory = 0;                            }else{                                rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("rcMemoryAvailableMemory");                            }                            if(totoalMemory == 0){                                memUsage = 0;                            }else{                                memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;                            }                        }                    }                } catch (Exception e) {                    result_para.setValue("CPU_USAGE", "--");                    result_para.setValue("MEMORY_USAGE",  "--");                    return result_para;                }                result_para.setValue("CPU_USAGE", new BigDecimal(cupUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());                result_para.setValue("MEMORY_USAGE", new BigDecimal(memUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());            }        }        return result_para;    }    /**     * 读取1.x设备CPU RAM     * @param smnpParamters     */    public static ObjService getEponOlt1xCpuRam(ObjService objService) {        ObjService smnpParamters = objService;        ObjService result_para = new ObjService();        String configFile=objService.getStringValue("ConfigFile");        String tableName = "rcCardProtectTable";        smnpParamters.setValue("ConfigFile", configFile);        smnpParamters.setValue("TableName", tableName);        smnpParamters.setValue("ValueOnly",true);        Driver driver = Driver.getDriver(smnpParamters);        ObjService results = driver.getValue(smnpParamters);        if(results == null){            result_para.setValue("CPU_USAGE", "--");            result_para.setValue("MEMORY_USAGE",  "--");            return result_para;        }        String errorCode = results.getStringValue("ErrCode");        if("0".equalsIgnoreCase(errorCode))        {            String state = results.objectAt("RowSet", 0).getStringValue("rcCardProtectGroupOperState");            int privarySlot= results.objectAt("RowSet", 0).getIntValue("rcCardProtectGroupMajorCardSlotIndex");            int sencondrySlot= results.objectAt("RowSet", 0).getIntValue("rcCardProtectGroupMinorCardSlotIndex");            int rightSlot = privarySlot;            if(state.equals("1")){                rightSlot = privarySlot;            }else if(state.equals("2")){                rightSlot = sencondrySlot;            }else{                result_para.setValue("CPU_USAGE", "--");                result_para.setValue("MEMORY_USAGE",  "--");                return result_para;            }            smnpParamters.setValue("Instance", rightSlot*10000000+".0");            tableName = "raisecomCpuV2Table";            smnpParamters.setValue("ConfigFile", configFile);            smnpParamters.setValue("TableName", tableName);            results = driver.getValue(smnpParamters);            if(results == null){                result_para.setValue("CPU_USAGE", "--");                result_para.setValue("MEMORY_USAGE",  "--");                return result_para;            }            errorCode = results.getStringValue("ErrCode");            if("0".equalsIgnoreCase(errorCode))            {                String cupUsage = results.objectAt("RowSet", 0).getStringValue("raisecomCpuUsage1SecondV2");                int totoalMemory =0;                int rcMemoryAvailableMemory =0;                float memUsage =0;                try {                    if(results.objectAt("RowSet", 0).getStringValue("raisecomTotalMemoryV2").equalsIgnoreCase("NULL")){                        totoalMemory = 0;                    }else{                        totoalMemory = results.objectAt("RowSet", 0).getIntValue("raisecomTotalMemoryV2");                    }                    if(results.objectAt("RowSet", 0).getStringValue("raisecomAvailableMemoryV2").equalsIgnoreCase("NULL")){                        rcMemoryAvailableMemory = 0;                    }else{                        rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("raisecomAvailableMemoryV2");                    }                    if(totoalMemory == 0){                        memUsage = 0;                    }else{                        memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;                    }                } catch (Exception e) {                    // TODO: handle exception                    result_para.setValue("CPU_USAGE", "--");                    result_para.setValue("MEMORY_USAGE",  "--");                    return result_para;                }                result_para.setValue("CPU_USAGE", new BigDecimal(cupUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());                result_para.setValue("MEMORY_USAGE", new BigDecimal(memUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());            }        }        return result_para;    }    /**     * 读取2.x设备CPU RAM     * @param smnpParamters     */    public static ObjService getEponOlt2xCpuRam(ObjService objService) {        ObjService smnpParamters = objService.clone();        ObjService result_para = new ObjService();        //String configFile = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/2.20/6800Common.xml";        String configFile=objService.getStringValue("ConfigFile");        String tableName = "rcCardProtectTable";        smnpParamters.setValue("ConfigFile", configFile);        smnpParamters.setValue("TableName", tableName);        smnpParamters.setValue("ValueOnly",true);        //读取主控槽位        Driver driver = Driver.getDriver(smnpParamters);        //ObjService res = driver.getValue(smnpParamters);        ObjService results = new ObjService();        try {            results = driver.getValue(smnpParamters);            if(results.objectSize("RowSet") == 0){                result_para.setValue("CPU_USAGE", "--");                result_para.setValue("MEMORY_USAGE",  "--");                return result_para;            }        } catch (Exception e1) {            // TODO Auto-generated catch block        }        String errorCode = results.getStringValue("ErrCode");        if("0".equalsIgnoreCase(errorCode))        {            String state = results.objectAt("RowSet", 0).getStringValue("rcCardProtectGroupOperState");            int privarySlot= results.objectAt("RowSet", 0).getIntValue("rcCardProtectGroupMajorCardSlotIndex");            int sencondrySlot= results.objectAt("RowSet", 0).getIntValue("rcCardProtectGroupMinorCardSlotIndex");            int rightSlot = privarySlot;            if(state.equals("1")){                rightSlot = privarySlot;            }else if(state.equals("2")){                rightSlot = sencondrySlot;            }else{                result_para.setValue("CPU_USAGE", "--");                result_para.setValue("MEMORY_USAGE",  "--");                //EPONConstants.logger.error(this.getClass().getName()+"--card state is wrong)");                return result_para;            }            smnpParamters.setValue("Instance", rightSlot+".0");            tableName = "raisecomCpuV2Table";            //configFile = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/2.20/6800Switch.xml";            smnpParamters.setValue("ConfigFile", configFile);            smnpParamters.setValue("TableName", tableName);            try {                //读取privarySlot CPU RAM                results = driver.getValue(smnpParamters);                if(results.objectSize("RowSet") == 0){                    result_para.setValue("CPU_USAGE", "--");                    result_para.setValue("MEMORY_USAGE",  "--");                    return result_para;                }            } catch (Exception e2) {                result_para.setValue("CPU_USAGE", "--");                result_para.setValue("MEMORY_USAGE",  "--");                // TODO Auto-generated catch block                return result_para;            }            errorCode = results.getStringValue("ErrCode");            if("0".equalsIgnoreCase(errorCode))            {                String cupUsage = results.objectAt("RowSet", 0).getStringValue("raisecomCpuUsage1SecondV2");                result_para.setValue("CPU_USAGE", new BigDecimal(cupUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());                int totoalMemory =0;                int rcMemoryAvailableMemory =0;                float memUsage =0;                try {                    if((results.objectAt("RowSet", 0).getValue("raisecomTotalMemoryV2").equals("NULL")) || (results.objectAt("RowSet", 0).getValue("raisecomAvailableMemoryV2").equals("NULL"))){                        result_para.setValue("MEMORY_USAGE",  "--");                    }else{                        totoalMemory = results.objectAt("RowSet", 0).getIntValue("raisecomTotalMemoryV2");                        rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("raisecomAvailableMemoryV2");                        if(totoalMemory == 0){                            memUsage = 0;                        }else{                            memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;                        }                        result_para.setValue("MEMORY_USAGE", new BigDecimal(memUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());                    }                } catch (Exception e) {                    // TODO: handle exception                    result_para.setValue("CPU_USAGE", "--");                    result_para.setValue("MEMORY_USAGE",  "--");                    return result_para;                }            }        }else        {            result_para.setValue("CPU_USAGE", "--");            result_para.setValue("MEMORY_USAGE",  "--");            return result_para;        }        return result_para;    }    /**     * 获得业务板卡与主控板卡数量信息     * @return     */    public static String[] getCardAmount4OLT(ObjService objService) {        // TODO Auto-generated method stub        Map<String, Integer> cardTypeCount = new HashMap();        Map<String,String> bCardTypeMap=cacheBussinessCardType();        Map<String,String> cCardTypeMap=cacheControlCardType();        String typeId = "";        String state = "";        String typeName = "";        String[] result = {"",""};        int counter = 0;        String cardDesc = "";        String controlCardId = "";        int controlCardNormal = 0;        String version = objService.getStringValue("version");        ObjService res = getMibNodesFromOLT("raisecomSlotTable","",objService);        if(res.objectSize("RowSet") == 0){            return result;        }else{            if(version.startsWith("2.")){                for (int i = 0; i < res.objectSize("RowSet"); i++) {                    typeId = res.objectAt("RowSet", i).getStringValue("raisecomSlotExpectCardType");                    state = res.objectAt("RowSet", i).getStringValue("raisecomSlotActCardState");//		 				if(typeName==null ||"".equals(typeName)||"null".equalsIgnoreCase(typeName)){                    if(cCardTypeMap.containsKey(typeId)){                        if("10".equals(state)){//2.X standby(10)                            controlCardNormal++;                        }else if("9".equals(state)){//2.X inservice(9)                            controlCardNormal++;                            controlCardId = res.objectAt("RowSet", i).getStringValue("raisecomSlotId");                        }else{                            //controlCardAbnormal++;                        }                    }else if(bCardTypeMap.containsKey(typeId)&&"9".equals(state)){                        typeName=bCardTypeMap.get(typeId);                        if(cardTypeCount.containsKey(typeName)){                            counter = cardTypeCount.get(typeName);                            counter++;                            cardTypeCount.put(typeName, counter);                        }else{                            cardTypeCount.put(typeName,1);                        }                    }                }            }else{                for (int i = 0; i < res.objectSize("RowSet"); i++) {                    typeId = res.objectAt("RowSet", i).getStringValue("raisecomSlotExpectCardType");                    state = res.objectAt("RowSet", i).getStringValue("raisecomSlotActCardState");//	 				if(typeName==null || "".equals(typeName)||"null".equalsIgnoreCase(typeName)){//	 					typeName=cCardTypeMap.get(typeId);                    if(cCardTypeMap.containsKey(typeId)){                        if("2".equals(state)){//1.X normal(2)                            controlCardNormal++;                            controlCardId = res.objectAt("RowSet", i).getStringValue("raisecomSlotId");                        }else{                            //controlCardAbnormal++;                        }                    }else if(bCardTypeMap.containsKey(typeId)&&"2".equals(state)){//1.X normal(2)                        typeName=bCardTypeMap.get(typeId);                        if(cardTypeCount.containsKey(typeName)){                            counter = cardTypeCount.get(typeName);                            counter++;                            cardTypeCount.put(typeName, counter);                        }else{                            cardTypeCount.put(typeName,1);                        }                    }                }            }        }        cardDesc  = cardTypeCount.toString();        cardDesc = cardDesc .replaceAll("=", ":");        result[0]=cardDesc;        if(controlCardNormal==2){            result[1]=ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "DOUBLE_SMC");        }else{            result[1]=ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "SINGE_SMC");        }        controlCardID=controlCardId;        return result;    }    //主控运行时间    public static String getsysUpTime(ObjService objService){        return null;    }    //主备倒换次数    public static String getSwitchedCount(ObjService objService) {        String count = "";        ObjService res = getMibNodesFromOLT("rcHighAvailabilityTable","",objService);        if (res == null) {            return null;        } else {            for (int i = 0; i < res.objectSize("RowSet"); i++) {                count = res.objectAt("RowSet", i).getStringValue("rcHighAvailabilitySwitchedCount");            }            return count;        }    }    //主控异常重启次数    public static String getSysRebCountCount(ObjService objService){        return null;    }    //主控电压    public static String getControlVoltage(ObjService objService){        String masterSlotID = "";        String standbySlotID = "";        String slotID = "";        String powerType = "";        String volValue = "";        String transpowerType ="";        String controlVoltage = "";        ObjService resultSlotID = getMibNodesFromOLT("raisecomShelfTable2","",objService);        ObjService resultVol = getMibNodesFromOLT("raisecomCardPowerTable","",objService);        if (resultSlotID == null) {            return null;        } else {            for (int i = 0; i < resultSlotID.objectSize("RowSet"); i++) {                ObjService objService1=resultSlotID.objectAt("RowSet", i);                masterSlotID=objService1.getStringValue("raisecomShelfPrimaryNMSSlotId");                standbySlotID=objService1.getStringValue("raisecomShelfSecondaryNMSSlotId");            }        }        if (resultVol == null) {            return null;        } else {            for (int i = 0; i < resultVol.objectSize("RowSet"); i++) {                String slotIDstr = "SlotId :";                slotID = resultVol.objectAt("RowSet", i).getStringValue("raisecomCardPowerSlotId");                String powerTypestr = "PowerType :";                String volValuestr = "Voltage(mv) :";                if(masterSlotID.equals(slotID) || standbySlotID.equals(slotID)){                    slotIDstr += slotID;                    powerType = resultVol.objectAt("RowSet", i).getStringValue("raisecomCardPowerType");                    transpowerType = convertionVol(powerType);                    volValue = resultVol.objectAt("RowSet", i).getStringValue("raisecomCardPowerVolValue");                    powerTypestr += transpowerType ;                    volValuestr += volValue+"\r\n";                    controlVoltage += slotIDstr + " " + powerTypestr + " " + volValuestr ;                }            }        }        return controlVoltage;    }    //PON口隔离    public static String getPonPortIsolation(ObjService objService){        String oltId=objService.getValue("oltId").toString();        Map<String,String> map = getCardRelationByOltid(oltId);        String portList = "";        String transportList = "";        String portIsolate = "";        ObjService res = getMibNodesFromOLT("rcPortIsolateTable","",objService);        if (res == null) {            return null;        } else {            for (int i = 0; i < res.objectSize("RowSet"); i++) {                String groupID = "GroupId :";                groupID += res.objectAt("RowSet", i).getStringValue("rcPortIsolateGroupId");                portList = res.objectAt("RowSet", i).getStringValue("rcPortIsolateGroupPortList");                transportList = ParseAdapterUtil.setValue(portList);                //1.分割字符串                String portStr = "PortList :";                String[] inMib = transportList.split(",");                for(int j = 0;j < inMib.length;j ++){                    String index = inMib[j].substring(0,inMib[j].indexOf("/"));                    if(map.containsKey(index)){                        String temp = map.get(index);                        //2.判断GPON、EPON                        if("isGponCard:true".equals(temp)){                            inMib[j] = "gpon-olt "+ inMib[j];                        }else{                            inMib[j] = "epon-olt "+ inMib[j];                        }                    }                    //3.拼List串                    portStr+=inMib[j]+" ";                }//                if(i != res.objectSize("RowSet")-1){//                    portStr += portStr;//                }                //4.拼总串                portIsolate += groupID +" "+ portStr +"\r\n";            }        }        return portIsolate;    }    //ONU数量统计    public static String getONUCount(ObjService objService){        return null;    }    /**     * 将数据库中的业务板卡类型对应关系缓存起来     */    private static Map<String,String> cacheBussinessCardType() {       Map<String,String> bCardTypeMap=new HashMap<>();        String sql = "select CARD_TYPE_ID,CARD_TYPE_NAME  from card_type where CARD_BUSINESS_TYPE_ID in (2,5,6,7,8)";        String card_id = "";        String card_name = "";        try {            ObjService objcard = EPONCommonDBUtil.getInstance().selectObject(sql, null);            for (int i = 0; i < objcard.objectSize("RowObj"); i++) {                card_id = objcard.objectAt("RowObj", i).getStringValue("CARD_TYPE_ID");                card_name = objcard.objectAt("RowObj", i).getStringValue("CARD_TYPE_NAME");                bCardTypeMap.put(card_id,card_name);            }        } catch (Exception e) {            // TODO Auto-generated catch block        }        return  bCardTypeMap;    }    /**     * 将数据库中的主控板卡类型对应关系缓存起来     */    private static Map<String,String> cacheControlCardType() {        // TODO Auto-generated method stub        String sql = "select CARD_TYPE_ID,CARD_TYPE_NAME  from card_type where CARD_BUSINESS_TYPE_ID = 9";        String card_id = "";        String card_name = "";        Map<String,String> cCardTypeMap=new HashMap<>();        try {            ObjService objcard = EPONCommonDBUtil.getInstance().selectObject(sql, null);            for (int i = 0; i < objcard.objectSize("RowObj"); i++) {                card_id = objcard.objectAt("RowObj", i).getStringValue("CARD_TYPE_ID");                card_name = objcard.objectAt("RowObj", i).getStringValue("CARD_TYPE_NAME");                cCardTypeMap.put(card_id,card_name);            }        } catch (Exception e) {            // TODO Auto-generated catch block        }        return cCardTypeMap;    }    //通过oltID获取板卡信息    public static Map<String,String> getCardRelationByOltid(String oltId){        Map<String,String> map = new HashMap<>();        String sql = "select c.INDEX_IN_MIB,ct.EXTPROPERTIES from card c,card_type ct " +                "where c.CARD_TYPE_ID=ct.CARD_TYPE_ID and c.NETYPE_ID = ct.NETYPE_ID and c.IRCNETNODEID = ?";        ObjService result = EPONCommonDBUtil.executeQuery(sql,new String[]{oltId});        Vector<ObjService> list = result.getAllChildObjects();        for(ObjService obj : list){            String slot = (String)obj.getCurrentHashtable().get("INDEX_IN_MIB");            String isGpon = (String)obj.getCurrentHashtable().get("EXTPROPERTIES");            map.put(slot,isGpon);        }        return map;    }    //电压枚举值    public static String convertionVol(String volValue){        Map<String,String> map = new HashMap<String,String>();        String tempVolvalue ="";        map.put("1","0.75V");        map.put("2","0.9V");        map.put("3","1V");        map.put("4","1.2V");        map.put("5","1.5V");        map.put("6","1.8V");        map.put("7","2.5V");        map.put("8","3.3V");        map.put("9","4.8V");        map.put("10","5V");        map.put("11","5.2V");        map.put("12","12V");        map.put("13","unknow");        map.put("14","null");        map.put("15","1.25V");        map.put("16","1.9V");        if(map.containsKey(volValue)){            tempVolvalue = map.get(volValue);        }        return tempVolvalue ;    }    //板卡电压    public static String getVoltage(String inMib, ObjService options) {        String slotID = "";        String powerType = "";        String volValue = "";        String transpowerType ="";        String voltage = "";        ObjService resultVol = getMibNodesFromOLT("raisecomCardPowerTable","",options);        if (resultVol == null) {            return null;        } else {            for (int i = 0; i < resultVol.objectSize("RowSet"); i++) {                String slotIDstr = "SlotId :";                slotID = resultVol.objectAt("RowSet", i).getStringValue("raisecomCardPowerSlotId");                String powerTypestr = "PowerType :";                String volValuestr = "Voltage(mv) :";                if(inMib.equals(slotID) ) {                    slotIDstr += slotID;                    powerType = resultVol.objectAt("RowSet", i).getStringValue("raisecomCardPowerType");                    transpowerType = convertionVol(powerType);                    volValue = resultVol.objectAt("RowSet", i).getStringValue("raisecomCardPowerVolValue");                    powerTypestr += transpowerType;                    volValuestr += volValue + "\r\n";                    voltage += slotIDstr + " " + powerTypestr + " " + volValuestr;                }            }        }        return voltage;    }    //板卡CPU使用率    public static String getCardCPU(String inMib, ObjService options) {        return null;    }    //板卡RAM使用率    public static String getCardRAM(String inMib, ObjService options) {        return null;    }}