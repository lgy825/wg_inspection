package com.raisecom.concurrent;

import com.raisecom.ems.templet.server.driver.Driver;
import com.raisecom.nms.platform.client.ResourceManager;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.EPONCommonDBUtil;
import com.raisecom.util.EPONConstants;
import com.raisecom.util.SnmpParamsHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class DeviceStatisticOperatorThread implements Callable<Boolean> {
    private ObjService objService;
    private String version="";
    private ObjService options = null;
    private String configFile = "com/raisecom/profile/";
    private Map<String, String> bCardTypeMap = new HashMap<String, String>();
    private Map<String, String> cCardTypeMap = new HashMap<String, String>();
    private String controlCardID = "";

    String oltid="";
    String ip="";
    String netype="";

    public DeviceStatisticOperatorThread(ObjService objService){
        this.objService=objService;
    }

    /**
     * 查看OLT信息并且入库
     * @param ip
     * @param netype
     * @param version
     * @return
     */
    public Boolean call(){
        String cpuUsage = null,ramUsage = null,tmpt = null,powerStatus = null,fanStatus = null,versfw = null,vlanOptimize = null;
        try{
            String oltname = objService.getStringValue("FRIENDLY_NAME");
            String hostname = objService.getStringValue("HOSTNAME");
            oltid=objService.getStringValue("IRCNETNODEID");
            netype=objService.getStringValue("IRCNETYPEID");
            ip=objService.getStringValue("IPADDRESS");
            version = objService.getStringValue("SOFTWARE_VER");
            configFile=objService.getStringValue("configFile");
            options = SnmpParamsHelper.getOption(oltid);
            //2. 查询OLT 是否在线
            boolean oltOnlineFlag = isOltOnline();
            if(!oltOnlineFlag){
                String sql = "insert into OLT_STATISTICS_INFO(IRCNETNODEID,FRIENDLY_NAME,iRCNETypeID,IPADDRESS,HOSTNAME) "+
                        "values ('"+oltid+"','"+oltname+"','"+netype+"','"+ip+"','"+hostname+"') ON DUPLICATE KEY UPDATE FRIENDLY_NAME = '" + oltname +"', iRCNETypeID = '" + netype +"',IPADDRESS ='" + ip +"',HOSTNAME ='" + hostname+"'";
                try {
                    EPONCommonDBUtil.getInstance().executeSql(sql, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    //EPONConstants.logger.error(this.getClass().getName()+" excute sql :" + sql +" exception!");
                }
                return false;
            }
            try{
                //3. 查询OLT 温度
                tmpt = getTemperature4OLT();
                tmpt = processResult(tmpt);
            }catch(Exception e){
                //EPONConstants.logger.error(this.getClass().getSimpleName()+"Exception"+e);
            }
            try{
                //4. 查询OLT 软件版本
                versfw = getSoftwareVer4OLT();
                versfw = processResult(versfw);
            }catch(Exception e){
                //EPONConstants.logger.error(this.getClass().getSimpleName()+"Exception"+e);
            }
            try{
                ObjService cpuramUsage = getCpuRamUsage4OLT(version);
                cpuUsage = cpuramUsage.getStringValue("CPU_USAGE");
                cpuUsage = processResult(cpuUsage);
                ramUsage = cpuramUsage.getStringValue("MEMORY_USAGE");
                ramUsage = processResult(ramUsage);
            }catch(Exception e){
                //EPONConstants.logger.error(this.getClass().getSimpleName()+"Exception"+e);
            }
            try{
                //7. 查询OLT 电源状态
                powerStatus = getPowerStatus4OLT(version);
                powerStatus = processResult(powerStatus);
            }catch(Exception e){
                //EPONConstants.logger.error(this.getClass().getSimpleName()+"Exception"+e);
            }
            try{
                //8. 查询OLT 风扇状态
                fanStatus = getFanStatus4OLT(version);
                fanStatus = processResult(fanStatus);
            }catch(Exception e){
                //EPONConstants.logger.error(this.getClass().getSimpleName()+"Exception"+e);
            }
            try{
                //9. 查询VLAN广播域是否能优化缩小
                vlanOptimize = getVlanOptimize(version);
                vlanOptimize = processResult(vlanOptimize);
            }catch(Exception e){
                //EPONConstants.logger.error(this.getClass().getSimpleName()+"Exception"+e);
            }
            EPONConstants.logger.info(this.getClass().getName()+" for olt "+ip+" inpsection end...");
        }catch(Exception e){
            //EPONConstants.logger.error(this.getClass().getName()+" call()");
            //EPONConstants.logger.error(e.getMessage());
        }
        return true;
    }

    private ObjService getCpuRamUsage4OLT(String version) {
        // TODO Auto-generated method stub
        ObjService result_para = new ObjService();
        EPONConstants.logger.debug("###### DeviceStatisticOperatorThread getCpuRamUsage4OLT start version="+version+" ######");
        try {
            float verF = Float.valueOf(version);
//			if(verF > new Float(2.2)&&netype.equalsIgnoreCase("ISCOM5508GP")){
            if(netype.startsWith("ISCOM6820")||netype.startsWith("ISCOM5508")||netype.startsWith("ISCOM5504")){
                result_para = getOneCardOltCpuRam();//不区分GPON、EPON
            }else if(verF < new Float(2.0)){
                EPONConstants.logger.debug("###### DeviceStatisticOperatorThread getCpuRamUsage4OLT start getEponOlt1xCpuRam() !!!######");
                result_para = getEponOlt1xCpuRam();
            }else{
                //EPONConstants.logger.debug("###### DeviceStatisticOperatorThread getCpuRamUsage4OLT start getEponOlt2xCpuRam() !!!######");
                result_para = getEponOlt2xCpuRam();
            }
            return result_para;
        } catch (Exception e) {
            //EPONConstants.logger.error("###### DeviceStatisticOperatorThread getCpuRamUsage4OLT get CPU and Memory Utilization failed,exception "+e+ " ######");
            result_para.setValue("CPU_USAGE", "--");
            result_para.setValue("MEMORY_USAGE",  "--");
            return result_para;
        }
    }


    /**
     * 读取GPON设备CPU RAM
     * @param smnpParamters
     */
    private ObjService getOneCardOltCpuRam() {
        // TODO Auto-generated method stub
        ObjService smnpParamters = options.clone();
        ObjService result_para = new ObjService();
        String configFile = "com/raisecom/ems/gpon/server/modules/cfgmgt/profile/2.30/5508GpSwitch.xml";
        String tableName = "raisecomShelfTable2";
        smnpParamters.setValue("ConfigFile", configFile);
        smnpParamters.setValue("TableName", tableName);
        smnpParamters.setValue("ValueOnly",true);
        //读取主控槽位
        Driver driver = Driver.getDriver(smnpParamters);
        ObjService results = driver.getValue(smnpParamters);
        if(results == null){
            result_para.setValue("CPU_USAGE", "--");
            result_para.setValue("MEMORY_USAGE",  "--");
            //EPONConstants.logger.error(this.getClass().getSimpleName()+"getGponOltCpuRam() option:"+smnpParamters.toString());
            return result_para;
        }
        String errorCode = results.getStringValue("ErrCode");
        if("0".equalsIgnoreCase(errorCode))
        {
            int privarySlot = 0;
            int secondSlot = 0;
            try{
                privarySlot= results.objectAt("RowSet", 0).getIntValue("raisecomShelfPrimaryNMSSlotId");
                secondSlot= results.objectAt("RowSet", 0).getIntValue("raisecomShelfSecondaryNMSSlotId");
            }catch(Exception e){
                //EPONConstants.logger.error(this.getClass().getName()+"::getOneCardOltCpuRam()::"+e.getMessage());
                result_para.setValue("CPU_USAGE", "--");
                result_para.setValue("MEMORY_USAGE",  "--");
                return result_para;
            }
            smnpParamters.setValue("Instance", privarySlot+".0");
            tableName = "rcCpuMemoryMonTable";
            smnpParamters.setValue("ConfigFile", configFile);
            smnpParamters.setValue("TableName", tableName);
            results = driver.getValue(smnpParamters);
            if(results == null){
                result_para.setValue("CPU_USAGE", "--");
                result_para.setValue("MEMORY_USAGE",  "--");
               // EPONConstants.logger.error(this.getClass().getName()+" option:"+smnpParamters.toString());
                return result_para;
            }
            errorCode = results.getStringValue("ErrCode");
            if("0".equalsIgnoreCase(errorCode))
            {
                String cupUsage = results.objectAt("RowSet", 0).getStringValue("rcCpuUsage1Second");
                int totoalMemory =0;
                int rcMemoryAvailableMemory =0;
                float memUsage =0;
                try {
                    if(!cupUsage.equals("NULL") || !(results.objectAt("RowSet", 0).getValue("rcMemoryTotoalMemory").equals("NULL")) || !(results.objectAt("RowSet", 0).getValue("rcMemoryAvailableMemory").equals("NULL"))){
                        totoalMemory = results.objectAt("RowSet", 0).getIntValue("rcMemoryTotoalMemory");
                        rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("rcMemoryAvailableMemory");
                        memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;
                    }else{
                        smnpParamters.setValue("Instance", secondSlot +".0");
                        tableName = "raisecomCpuV2Table";
                        smnpParamters.setValue("ConfigFile", configFile);
                        smnpParamters.setValue("TableName", tableName);
                        //读取CPU RAM
                        results = driver.getValue(smnpParamters);
                        if(results == null){
                            result_para.setValue("CPU_USAGE", "--");
                            result_para.setValue("MEMORY_USAGE",  "--");
                            //EPONConstants.logger.error(this.getClass().getName()+" option:"+smnpParamters.toString());
                            return result_para;
                        }
                        errorCode = results.getStringValue("ErrCode");
                        if("0".equalsIgnoreCase(errorCode))
                        {
                            cupUsage = results.objectAt("RowSet", 0).getStringValue("rcCpuUsage1Second");
                            totoalMemory =0;
                            rcMemoryAvailableMemory =0;
                            memUsage =0;
                            if(results.objectAt("RowSet", 0).getStringValue("rcMemoryTotoalMemory").equalsIgnoreCase("NULL")){
                                totoalMemory = 0;
                            }else{
                                totoalMemory = results.objectAt("RowSet", 0).getIntValue("rcMemoryTotoalMemory");
                            }
                            if(results.objectAt("RowSet", 0).getStringValue("rcMemoryAvailableMemory").equalsIgnoreCase("NULL")){
                                rcMemoryAvailableMemory = 0;
                            }else{
                                rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("rcMemoryAvailableMemory");
                            }
//							memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;
                            if(totoalMemory == 0){
                                memUsage = 0;
                            }else{
                                memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;
                            }
                        }
                    }
                } catch (Exception e) {
                    //EPONConstants.logger.error(this.getClass().getName()+ " CPU and Memory Utilization SNMP Operation exception,"+e);
                    result_para.setValue("CPU_USAGE", "--");
                    result_para.setValue("MEMORY_USAGE",  "--");
                    return result_para;
                }
                result_para.setValue("CPU_USAGE", new BigDecimal(cupUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                result_para.setValue("MEMORY_USAGE", new BigDecimal(memUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                //EPONConstants.logger.info("Get msgMemoryUsageTable Success msgMemoryUsage: "+memUsage);
            }else
            {
                //EPONConstants.logger.error(this.getClass().getName()+" CPU and Memory Utilization SNMP Operation failed,"+results);
            }
        }else
        {
            //EPONConstants.logger.error(this.getClass().getName()+" CPU and Memory Utilization SNMP Operation failed,"+results);
        }

        return result_para;

    }


    /**
     * 读取1.x设备CPU RAM
     * @param smnpParamters
     */
    private ObjService getEponOlt1xCpuRam() {
        ObjService smnpParamters = options.clone();
        ObjService result_para = new ObjService();
        String configFile = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/1.43/5508Common.xml";

        String tableName = "rcCardProtectTable";
        smnpParamters.setValue("ConfigFile", configFile);
        smnpParamters.setValue("TableName", tableName);
        smnpParamters.setValue("ValueOnly",true);
        EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt1xCpuRam(),tableName=rcCardProtectTable start "+smnpParamters.toString()+" ######");
        Driver driver = Driver.getDriver(smnpParamters);
        ObjService results = driver.getValue(smnpParamters);
        if(results == null){
            result_para.setValue("CPU_USAGE", "--");
            result_para.setValue("MEMORY_USAGE",  "--");
            //EPONConstants.logger.error(this.getClass().getName()+"--Class:getEponOlt1xCpuRam get rcCardProtectTable option:"+smnpParamters.toString());
            return result_para;
        }
        //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt1xCpuRam(),tableName=rcCardProtectTable end "+results.toString()+" ######");
        String errorCode = results.getStringValue("ErrCode");
        if("0".equalsIgnoreCase(errorCode))
        {
            String state = results.objectAt("RowSet", 0).getStringValue("rcCardProtectGroupOperState");
            int privarySlot= results.objectAt("RowSet", 0).getIntValue("rcCardProtectGroupMajorCardSlotIndex");
            int sencondrySlot= results.objectAt("RowSet", 0).getIntValue("rcCardProtectGroupMinorCardSlotIndex");
            int rightSlot = privarySlot;
            if(state.equals("1")){
                rightSlot = privarySlot;
            }else if(state.equals("2")){
                rightSlot = sencondrySlot;
            }else{
                result_para.setValue("CPU_USAGE", "--");
                result_para.setValue("MEMORY_USAGE",  "--");
                //EPONConstants.logger.error(this.getClass().getName()+"--card state is wrong)");
                return result_para;
            }

            configFile = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/ONUCommon.xml";
            smnpParamters.setValue("Instance", rightSlot*10000000+".0");
            tableName = "raisecomCpuV2Table";
            smnpParamters.setValue("ConfigFile", configFile);
            smnpParamters.setValue("TableName", tableName);
            //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt1xCpuRam(),tableName=raisecomCpuV2Table start "+smnpParamters.toString()+" ######");
            results = driver.getValue(smnpParamters);
            if(results == null){
                result_para.setValue("CPU_USAGE", "--");
                result_para.setValue("MEMORY_USAGE",  "--");
                //EPONConstants.logger.error(this.getClass().getName()+"--Class:getEponOlt1xCpuRam raisecomCpuV2Table option:"+smnpParamters.toString());
                return result_para;
            }
            //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt1xCpuRam(),tableName=raisecomCpuV2Table end "+results.toString()+" ######");
            errorCode = results.getStringValue("ErrCode");
            if("0".equalsIgnoreCase(errorCode))
            {
                String cupUsage = results.objectAt("RowSet", 0).getStringValue("raisecomCpuUsage1SecondV2");
                int totoalMemory =0;
                int rcMemoryAvailableMemory =0;
                float memUsage =0;
                try {
                    if(results.objectAt("RowSet", 0).getStringValue("raisecomTotalMemoryV2").equalsIgnoreCase("NULL")){
                        totoalMemory = 0;
                    }else{
                        totoalMemory = results.objectAt("RowSet", 0).getIntValue("raisecomTotalMemoryV2");
                    }
                    if(results.objectAt("RowSet", 0).getStringValue("raisecomAvailableMemoryV2").equalsIgnoreCase("NULL")){
                        rcMemoryAvailableMemory = 0;
                    }else{
                        rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("raisecomAvailableMemoryV2");
                    }
//					totoalMemory = results.objectAt("RowSet", 0).getIntValue("raisecomTotalMemoryV2");
//					rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("raisecomAvailableMemoryV2");
                    if(totoalMemory == 0){
                        memUsage = 0;
                    }else{
                        memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                    //EPONConstants.logger.error(this.getClass().getName()+" CPU and Memory Utilization SNMP Operation exception,"+e);
                    result_para.setValue("CPU_USAGE", "--");
                    result_para.setValue("MEMORY_USAGE",  "--");
                    return result_para;
                }
                result_para.setValue("CPU_USAGE", new BigDecimal(cupUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                result_para.setValue("MEMORY_USAGE", new BigDecimal(memUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                //EPONConstants.logger.info("Get msgMemoryUsageTable Success msgMemoryUsage: "+memUsage);
            }else
            {
                //EPONConstants.logger.error(this.getClass().getName()+"getEponOlt1xCpuRam CPU and Memory Utilization SNMP Operation failed,"+results.toString());
            }
        }else
        {
            //EPONConstants.logger.error(this.getClass().getName()+"getEponOlt1xCpuRam CPU and Memory Utilization SNMP Operation failed,"+results.toString());
        }
        //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt1xCpuRam() end result_para = "+result_para.toString()+" ######");
        return result_para;
    }


    /**
     * 读取2.x设备CPU RAM
     * @param smnpParamters
     */
    private ObjService getEponOlt2xCpuRam() {
        ObjService smnpParamters = options.clone();
        ObjService result_para = new ObjService();
        String configFile = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/2.20/6800Common.xml";
        String tableName = "rcCardProtectTable";
        smnpParamters.setValue("ConfigFile", configFile);
        smnpParamters.setValue("TableName", tableName);
        smnpParamters.setValue("ValueOnly",true);
        //读取主控槽位
        //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=rcCardProtectTable start "+smnpParamters.toString()+" ######");
        Driver driver = Driver.getDriver(smnpParamters);
        ObjService results = new ObjService();
        try {
            results = driver.getValue(smnpParamters);
            //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=rcCardProtectTable, results is  ######" + results.toString());
            if(results.objectSize("RowSet") == 0){
                result_para.setValue("CPU_USAGE", "--");
                result_para.setValue("MEMORY_USAGE",  "--");
                //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=rcCardProtectTable end, RowSet.size=0 ######");
                return result_para;
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            //EPONConstants.logger.error("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=rcCardProtectTable SNMP Operation failed ######" + e1);
        }
        //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=rcCardProtectTable end ######");
        String errorCode = results.getStringValue("ErrCode");
        if("0".equalsIgnoreCase(errorCode))
        {
            String state = results.objectAt("RowSet", 0).getStringValue("rcCardProtectGroupOperState");
            int privarySlot= results.objectAt("RowSet", 0).getIntValue("rcCardProtectGroupMajorCardSlotIndex");
            int sencondrySlot= results.objectAt("RowSet", 0).getIntValue("rcCardProtectGroupMinorCardSlotIndex");
            int rightSlot = privarySlot;
            if(state.equals("1")){
                rightSlot = privarySlot;
            }else if(state.equals("2")){
                rightSlot = sencondrySlot;
            }else{
                result_para.setValue("CPU_USAGE", "--");
                result_para.setValue("MEMORY_USAGE",  "--");
                //EPONConstants.logger.error(this.getClass().getName()+"--card state is wrong)");
                return result_para;
            }
            smnpParamters.setValue("Instance", rightSlot+".0");
            tableName = "raisecomCpuV2Table";
            configFile = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/2.20/6800Switch.xml";
            smnpParamters.setValue("ConfigFile", configFile);
            smnpParamters.setValue("TableName", tableName);

            try {
                //读取privarySlot CPU RAM
                //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=raisecomCpuV2Table, privarySlot start "+smnpParamters.toString()+" ######");
                results = driver.getValue(smnpParamters);
                //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=raisecomCpuV2Table, privarySlot results is  ######" + results.toString());
                if(results.objectSize("RowSet") == 0){
                    result_para.setValue("CPU_USAGE", "--");
                    result_para.setValue("MEMORY_USAGE",  "--");
                    //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=raisecomCpuV2Table end, privarySlot RowSet.size=0 ######");
                    return result_para;
                }
            } catch (Exception e2) {
                result_para.setValue("CPU_USAGE", "--");
                result_para.setValue("MEMORY_USAGE",  "--");
                // TODO Auto-generated catch block
                //EPONConstants.logger.error("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=raisecomCpuV2Table privarySlot SNMP Operation failed ######" + e2);
                return result_para;
            }
            errorCode = results.getStringValue("ErrCode");
            if("0".equalsIgnoreCase(errorCode))
            {
                String cupUsage = results.objectAt("RowSet", 0).getStringValue("raisecomCpuUsage1SecondV2");
                result_para.setValue("CPU_USAGE", new BigDecimal(cupUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                int totoalMemory =0;
                int rcMemoryAvailableMemory =0;
                float memUsage =0;
                try {
                    if((results.objectAt("RowSet", 0).getValue("raisecomTotalMemoryV2").equals("NULL")) || (results.objectAt("RowSet", 0).getValue("raisecomAvailableMemoryV2").equals("NULL"))){
                        result_para.setValue("MEMORY_USAGE",  "--");
                    }else{
                        totoalMemory = results.objectAt("RowSet", 0).getIntValue("raisecomTotalMemoryV2");
                        rcMemoryAvailableMemory = results.objectAt("RowSet", 0).getIntValue("raisecomAvailableMemoryV2");
                        if(totoalMemory == 0){
                            memUsage = 0;
                        }else{
                            memUsage = ((float)(totoalMemory-rcMemoryAvailableMemory)/(float)totoalMemory)*100;
                        }
                        result_para.setValue("MEMORY_USAGE", new BigDecimal(memUsage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());

                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    //EPONConstants.logger.error("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=raisecomCpuV2Table secondarySlot SNMP Operation failed ######" + e);
                    result_para.setValue("CPU_USAGE", "--");
                    result_para.setValue("MEMORY_USAGE",  "--");
                    return result_para;
                }


                //EPONConstants.logger.info("Get msgMemoryUsageTable Success msgMemoryUsage: "+memUsage);
            }else
            {
                //PONConstants.logger.error(this.getClass().getName()+"getEponOlt2xCpuRam CPU and Memory Utilization SNMP Operation failed,"+results.toString());
            }
        }else
        {
            result_para.setValue("CPU_USAGE", "--");
            result_para.setValue("MEMORY_USAGE",  "--");
           // EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam(),tableName=rcCardProtectTable end, RowSet.size=0 ######");
            return result_para;
        }
       // EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getEponOlt2xCpuRam() end, result_para="+result_para.toString()+" ######");
        return result_para;
    }
    /**
     * 判断OLT是否在线
     * @return
     */
    private boolean isOltOnline() {
        // TODO Auto-generated method stub
        ObjService res = getMibNodesFromOLT("rfc1213 System Group","");
        if(res==null){
            return false;
        }else{
            return true;
        }
    }

    private String processResult(String s) {
        if(s==null||"".equals(s)||"null".equalsIgnoreCase(s)){
            return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "NONE");
        }else{
            return s;
        }
    }


    /**
     * 查询VLAN广播域是否能优化缩小
     * @param version
     * @return
     */
    private String getVlanOptimize(String version) {
        // TODO Auto-generated method stub
        String vlanMode = "";
        String vlanList = "";
        ObjService res = getMibNodesFromOLT("rcVlanCfgPortTable","");
        if(res==null){
            return "";
        }else{
            for(int i = 0; i < res.objectSize("RowSet"); i++) {
                vlanMode = res.objectAt("RowSet", i).getStringValue("rcPortMode");
                if("2".equals(vlanMode)){//trunk mode
                    vlanList = res.objectAt("RowSet", i).getStringValue("rcPortTrunkAllowVlanList");
                    if(vlanList.contains("1")){
                        return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "PART_CAN_OPTIMIZE");
                    }
                }
            }
        }
        return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "NO_OPTIMIZE");
    }

    /**
     * 查询OLT 风扇状态
     * @param version
     * @return
     */
    private String getFanStatus4OLT(String version) {
        // TODO Auto-generated method stub
        String state = "";
        boolean flag = false;
        ObjService res = getMibNodesFromOLT("raisecomFanTable","");
        if(res==null){
            return "";
        }else{
            for(int i = 0; i < res.objectSize("RowSet"); i++) {
                state = res.objectAt("RowSet", i).getStringValue("raisecomFanStatus");
                if("1".equals(state)){
                    flag = true;
                    break;
                }
            }
        }
        if(flag){
            //return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "FAN_NORMAL");
        }

        if("".equals(controlCardID)){
            return "";
        }
        String index = Integer.parseInt(controlCardID)*10000000+".0.8";
        String ver = "";
        res = getMibNodesFromOLT("raisecomSWFileTable",index);
        if(res==null){
            return "";
        }else{
            ver=  res.objectAt("RowSet", 0).getStringValue("raisecomSWFileVersion");
            ver = ver.substring(1);
            try {
                if(Float.parseFloat(ver)<1.4){
                    return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "CPLD_LOW_VERSION");
                }else{
                    return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "NO_FAN");
                }
            } catch (Exception e) {
                //EPONConstants.logger.error("DeviceStatisticOperatorThread.java--Class:"+this.getClass().getName()+" getFanStatus4OLT excetion!"+e);
                return "";
            }

        }
    }

    /**
     * 查询OLT 电源状态
     * @param version
     * @return
     */
    private String getPowerStatus4OLT(String version) {
        // TODO Auto-generated method stub
        String state = "";
        String slot = "";
        Map<String, String> powerMap = new HashMap<String, String>();
        int normal = 0;
        ObjService res = getMibNodesFromOLT("raisecomPowerOutputTable","");
        if(res==null){
            return "";
        }else{
            if(version.startsWith("2.")){
                for (int i = 0; i < res.objectSize("RowSet"); i++) {
                    state = res.objectAt("RowSet", i).getStringValue("raisecomPowerStatus");
                    if("1".equals(state)){
                        slot = res.objectAt("RowSet", i).getStringValue("raisecomPowerDeviceIndex");
                        powerMap.put(slot, state);
                    }
                }
                return powerMap.size()>1?ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "DOUBLE_PWR"):ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "SINGLE_PWR");
            }else{
                for (int i = 0; i < res.objectSize("RowSet"); i++) {
                    state = res.objectAt("RowSet", i).getStringValue("raisecomPowerStatus");
                    if("1".equals(state)){
                        normal++;
                    }
                }
                return normal>1?ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "DOUBLE_PWR"):ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "SINGLE_PWR");
            }
        }
    }

    /**
     * 查询OLT CPU利用率
     * @param version
     * @return
     */
    private String getCpuUsage4OLT(String version) {
        // TODO Auto-generated method stub
        ObjService res = getMibNodesFromOLT("rcCpuMonTable","");
        if(res==null){
            return "";
        }else{
            return res.objectAt("RowSet", 0).getStringValue("raisecomCPUUtilizationTotal");
        }
    }

    /**
     * 查询olt版本
     * @return
     */
    private String getSoftwareVer4OLT() {
        // TODO Auto-generated method stub
        ObjService res = getMibNodesFromOLT("SwitchInformation","");
        if(res==null){
            return "";
        }else{
            return res.objectAt("RowSet", 0).getStringValue("rcSwitchRoseVersion");
        }
    }
    /**
     * 查询OLT温度
     * @return
     */
    private String getTemperature4OLT() {
        // TODO Auto-generated method stub
        ObjService res = getMibNodesFromOLT("raisecomShelfTable","");
        if(res==null){
            return "";
        }else{
            return res.objectAt("RowSet", 0).getStringValue("raisecomShelfTemperature");
        }

    }
    /**
     * 读取表的值
     * @param table
     * @param index
     * @return
     */
    private ObjService getMibNodesFromOLT(String table, String index) {
        // TODO Auto-generated method stub
        try{
            ObjService snmpParams =this.options.clone();
            String tableName = table;
            snmpParams.setValue("TableName", tableName);
            snmpParams.setValue("ConfigFile", configFile);
            snmpParams.setValue("ValueOnly", "true");
            if(!"".equals(index)){
                snmpParams.setValue("Instance", index);
            }
            //EPONConstants.logger.debug("###### " + this.getClass().getName()+ " getMibNodesFromOLT() version="+version+"+ snmpParams="+snmpParams+" ######");
            Driver driver = Driver.getDriver(snmpParams);
            ObjService res = driver.getValue(snmpParams);
            if(res==null){
                //EPONConstants.logger.error("###### "+this.getClass().getName()+" getMibNodesFromOLT is null !! snmpParams:"+snmpParams.toString());
                return null;
            }
            int size = res.objectSize("RowSet");
            if(size==0){
                //EPONConstants.logger.info(this.getClass().getName()+" olt("+snmpParams.getStringValue("IPAddress")+") get table "+tableName+ " for index "+index+" rowset is 0");
                return null;
            }else{
                return res;
            }
        }catch (Exception e){
            //EPONConstants.logger.error(this.getClass().getSimpleName()+".getMibNodesFromOLT("+"table"+table+"index"+index+")");
            //EPONConstants.logger.error(e.getMessage());
            return null;
        }
    }

}
