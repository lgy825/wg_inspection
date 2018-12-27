package com.raisecom.util;

import com.raisecom.common.GPONConstants;
import com.raisecom.ems.platform.util.ResourceUrlParser;
import com.raisecom.ems.templet.client.util.SnmpUtilities;
import com.raisecom.ems.templet.server.driver.Driver;
import com.raisecom.nms.platform.client.Poster;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.version.EPONVersionIf;
import com.raisecom.version.VersionElement;

import java.util.*;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class VersionUtil {

    private static ObjService nodeInfo;
    // 存放设备的软件版本和网管自定义的软件版本元数据的对应关系
    public static Hashtable<String, VersionElement> version = new Hashtable();
    private static String devType = "";
    private static String devVer = "";
    private static String oltType = "";

    private static Hashtable<String, String> maxVersionMapping = new Hashtable();
    private static List<String> verList = new ArrayList<String>();


    // 初始化version变量
    @SuppressWarnings("unchecked")
    private static void init() {

        if (version.size() != 0)
            return;
        // OEM兼容修改，首先从数据库中读取，如果没有读到，则从配置文件中读取。
        if (!(getDevVersionFromDB() && version.size() != 0)) {
            EPONConstants.logger.error(" ####  Get versions map failure!   NULL");

        }
        compareVersion(verList);
    }
    public static synchronized String getOltVersionByNeID(String id) {
        getDevVersion(id);
        nodeInfo = getFromDB(id, "rcnetnode");
        String verstr = getNEVersion(devVer, nodeInfo);//huxielong devVer:ISCOM5508-EPSC_ROAP_2.62.b.1_20180424
        if (verstr == null || verstr.equals(""))
            return "1.20";
        else
            return verstr;
    }

    private static synchronized void getDevVersion(String url) {

        oltType = "";
        devVer = "";
        devType = "";
        String olt_id = url;
        if(olt_id.indexOf( ResourceUrlParser.ne) != -1)
            olt_id = ResourceUrlParser.getIdByLabel(url, ResourceUrlParser.ne);
        if (isONU(url)) {// ONU需要去读取OLT的版本号；但是ONU的网元类型需要读取ONU自己的网元类型表。
            ObjService nodeInfoOnu;
            nodeInfo = getFromDB(olt_id, "rcnetnode");
            String onuInstance = IfIndexHelper.urlToInstance(url);
            String sql = "INDEX_IN_MIB = '" + onuInstance + "' and MANAGED_URL = '" + ResourceUrlParser.ne + olt_id + "'";
            nodeInfoOnu = getFromDBOnCondition("rcnetnode", sql);
            if (nodeInfo == null || nodeInfoOnu == null)
                return;
            // 获得ONU的网元类型 eg:ISCOM5104
            String devTypeId = nodeInfoOnu.getStringValue("iRCNETypeID");
//			获取软件版本
            String olt_ver = VersionUtil.getOltVersionByNeID(olt_id);
            if(isGponOnu(devTypeId))
            {
                devType = getGponOnuType(olt_id, onuInstance);
            }else if(isEponOnu(devTypeId)){
                // 新管理方式下获取ONU的类型：RC(1), OEM(2),OTHERS(3)
                ObjService typeInfo = getEponOnuDevVendorType(olt_id,onuInstance);
                if(typeInfo == null){
                    Poster.error(EPONConstants.EPON_RB.getString("ONU_NOT_EXIST"));
                }
                String typeValue =  typeInfo.getStringValue("rcEponOnuDevVendorType");

                // 改造后的EPON_ONU类型通过subnetype反查ModelId sxc modify 20150319
                String realOnuType = nodeInfoOnu.getStringValue("SUBNETYPE");
//				epon onu通用类型当软件版本为2.61及以后时走新的添加分支
                if(realOnuType.equals("EPON_ONU")){
                    if(Float.parseFloat(olt_ver)>2.61){
                        devType = getNewEponOnuType(olt_id, onuInstance,typeValue);
                    }else{
                        devType = getEponOnuType(olt_id, onuInstance,typeValue);
                    }
                }else if((null == realOnuType) || "".equals(realOnuType) ){
                    if(Float.parseFloat(olt_ver)>2.61){
//						devType = getNewEponOnuType(olt_id, onuInstance,"1");
                        devType = getNewEponOnuType(olt_id, onuInstance,EPONConstants.OTHERS_ONU_TYPE);
                    }else{
                        devType = getEponOnuType(olt_id, onuInstance,EPONConstants.OTHERS_ONU_TYPE);
                    }
                }else{
                    // 获得ONU的网元类型的ModelId eg 510400
                    devType = getOnuTypeInMibFromDb(realOnuType);
                }
            }else{
                // 获得ONU的网元类型的ModelId eg 510400
                devType = getOnuTypeInMibFromDb(devTypeId);
            }
            // olt的类型就是OLT用OLT的网元ID去网元表里查到的OLT类型
            oltType = nodeInfo.getStringValue("iRCNETypeID");
        } else {

//			nodeInfo = NodeCache.getInstance().getObject(url);
            nodeInfo = getFromDB(url, "rcnetnode");
            if (nodeInfo == null)
                return;
            devType = nodeInfo.getStringValue("iRCNETypeID");

            oltType = devType;

        }

        String devVerT = nodeInfo.getStringValue("SOFTWARE_VER");
        if(devVerT.equalsIgnoreCase("null") || devVerT == null || devVerT.equals("")){
            devVer = getDevVerFromDevice(olt_id);
            if(devVer != null){
                if(!devVer.equals(devVerT)){
//					updateDevVertoDb(olt_id, devVer);
                }
            }else{
                devVer = devVerT;
            }
        }else{
            devVer = devVerT;
        }
    }
    private static boolean getDevVersionFromDB() {

        String tabelName = "versions_mapping";
        List<ObjService> result = null;
        try {
            result = EPONCommonDBUtil.getInstance().getAllDataFromDB("id, rcnetypeid, software_ver, ver,VER_MAP_CLASS", tabelName, "1=1");
        } catch (Exception e) {
            EPONConstants.logger.error(e);
            return false;
        }

        for (int i = 0; result != null && i < result.size(); i++) {
            ObjService ver_map = result.get(i);
            VersionElement elem = new VersionElement();
            elem.setNeVersion(ver_map.getStringValue("VER"));
            elem.setVerClass(ver_map.getStringValue("VER_MAP_CLASS"));
            version.put(ver_map.getStringValue("RCNETYPEID") + "#" + ver_map.getStringValue("SOFTWARE_VER"), elem);
            verList.add(ver_map.getStringValue("RCNETYPEID") + "#" + ver_map.getStringValue("VER"));
        }

        return true;
    }
    private static String getNEVersion(String DeviceVersion, ObjService obj) {

        if (version.size() == 0)
            init();
        VersionElement elem = getVersionElement(oltType + "#" + DeviceVersion);

        if (elem == null) {
            EPONConstants.logger.debug("Version Element is null !!! ");
            String ver = getBaseVersion(DeviceVersion, oltType);
            if (ver == null || ver.equals(""))
                //如果为null判断系列

                return "";
            else
                return ver;
        }

        if (!"".equals(elem.getVerClass())) {
            String vClass = elem.getVerClass();
            try {
                EPONVersionIf ver = (EPONVersionIf) Class.forName(vClass).newInstance();
                return ver.getVersion(obj,elem);
            } catch (Exception e) {
                EPONConstants.logger.error(e);
                return "";
            }
        }
        return elem.getNeVersion();
    }

    //  获取每个版本系列的最大版本。
    private static String getBaseVersion(String neVersion, String olt_type) {
        EPONConstants.logger.debug("VersionUtil getBaseVersion with neVersion:" + neVersion + " olt_type:"+ olt_type);
        String version = null;
        //sijl modify 20160107 OLT未同步完成时，TL1命令故障定位传入的neVersion为null，修改以避免空指针异常
        if(neVersion !=null){
            String[] vers = neVersion.split("_");
            if (vers.length > 1) {// OLT版本号
                if(!vers[1].toUpperCase().contains("OAP")){//临时的解决方案，2.0版本号导致
                    String ver = vers[1].substring(0, vers[1].indexOf(".", 2));
                    float vFloat = Float.parseFloat(ver);
                    int vInt = (int) (vFloat);
                    if (vInt > 1) {
                        version = maxVersionMapping.get(olt_type + "#" + vInt);
                    } else {
                        version = maxVersionMapping.get(olt_type + "#" + "1");
                    }
                }else{
                    String ver = vers[2].substring(0, vers[2].indexOf(".", 2));
                    float vFloat = Float.parseFloat(ver);
                    int vInt = (int) (vFloat * 1);
                    if (vInt > 1) {
                        version = maxVersionMapping.get(olt_type + "#" + vInt);
                    } else {
                        version = maxVersionMapping.get(olt_type + "#" + "1");
                    }
                    //sijl add 20140807 当OLT是2.x版本且versions_mapping中未注册该OLT的版本 则将默认版本设置为2.0
                    if(vInt>=2&&version==null){
                        version = maxVersionMapping.get(olt_type + "#" + 2);
                    }
                }
            } else {
                // ONU的版本兼容，目前没有实现。
            }
        }

        return version;
    }

    private static VersionElement getVersionElement(String devVersion) {
        if (version.size() == 0)
            init();
        Iterator<String> it = version.keySet().iterator();
        while (it.hasNext()) {
            String ver = (String) it.next();
            if (devVersion.startsWith(ver)) {
                return version.get(ver);
            }
        }

        return null;

    }
    private static boolean isONU(String url)
    {
        return url.indexOf("shelf") != -1 ;
    }

    public static ObjService getFromDB(String url, String table) {
        if(!table.equals("rcnetnode"))
        {
            return null;
        }
        ObjService node = null;
        try {
            node = EPONCommonDBUtil.getInstance().getAllObjServicesFromDB("IRCNETNODEID,iRCNETypeID,SOFTWARE_VER,INDEX_IN_MIB,MANAGED_URL", "rcnetnode",
                    "IRCNETNODEID = '" + url + "'");
        } catch (Exception e) {
            return null;
        }
        if (node == null || node.getAllChildObjects().size() == 0) {
            EPONConstants.logger.debug("url " + url + " is not exist ! ");
            return null;
        } else {
            return node.objectAt("RowObj", 0);
        }
    }

    private static String getDevVerFromDevice(String oltId){
        ObjService snmpParameter;
        String ver = "";
        try{
            snmpParameter = SnmpParamsHelper.getOption(oltId);
        }catch(Exception e){
            EPONConstants.logger.error(e);
            EPONConstants.logger.error("get snmp parameters or dev software version fail !!!");
            return null;
        }
        snmpParameter.setValue("ConfigFile", "com/raisecom/ems/eponcommon/server/modules/digger/cfgmgt/mibcfgfile/ISCOMPONResourceDigger.xml");
        snmpParameter.setValue("TableName", "rcSwitchInformation");
        ObjService osResult = null;
        try{
            Driver driver = Driver.getDriver(snmpParameter);
            osResult = driver.getValue(snmpParameter);
            if (osResult.getStringValue("ErrCode").equalsIgnoreCase("0")){
                ver = osResult.objectAt("RowSet", 0).getStringValue("SOFTWARE_VER");
                if(!(ver.equals("") || ver.equals("NULL"))){
                    return ver;
                }
            }
        }catch(Error e){
        }catch(Exception e){
        }
        return null;
    }
    public static ObjService getFromDBOnCondition(String table, String condition) {

        ObjService node = null;
        try {
            node = EPONCommonDBUtil.getInstance().getAllObjServicesFromDB("IRCNETNODEID, iRCNETypeID, SUBNETYPE",table, condition);
        } catch (Exception e) {
            EPONConstants.logger.error("get onu ircnetypeid failure! " + e);
            return null;
        } finally {
        }
        if (node == null || node.getAllChildObjects().size() == 0) {
            EPONConstants.logger.debug(condition + "doesn't exist!");
            return null;
        } else {
            return node.objectAt("RowObj", 0);
        }

    }

    public static boolean isGponOnu(String devTypeId)
    {
        return devTypeId.startsWith("GPON_ONU") || devTypeId.startsWith("ONU_GP");
    }

    /**
     * 获取GPON ONU的统一类型
     * 暂时固定为 onu_gp_data
     * GPON ONU按功能分为onu_gp_data、onu_gp_video、onu_gp_catv
     * 根据GPON ONU类型组合shelfview路径
     * @param olt_id
     * @param onuInstance
     * @return
     */
    private static String getGponOnuType(String olt_id, String onuInstance)
    {
        ObjService bindPortInfo = getGponOnuPortNum(olt_id, onuInstance);
        if(bindPortInfo == null){
            return null;
        }
        String ethNum = bindPortInfo.getStringValue("rcGponOnuServiceProfileSlotEthNum");
        String potsNum = bindPortInfo.getStringValue("rcGponOnuServiceProfileSlotPotsNum");
        String catvNum = bindPortInfo.getStringValue("rcGponOnuServiceProfileSlotCatvNum");
        Map<Integer, String> onuGpFun = getGpOnuFunMap();
        String shelfview = "";
        //修改veip、pots、catv菜单可以共存
        shelfview += onuGpFun.get(1)+";";
        if(Integer.parseInt(ethNum) != 0){
            shelfview += onuGpFun.get(1) + ";";
        }if(Integer.parseInt(potsNum) != 0){
        shelfview += onuGpFun.get(2) + ";";
    }if(Integer.parseInt(catvNum) != 0){
        shelfview += onuGpFun.get(3) + ";";
    }if("".equals(shelfview)){
        shelfview += onuGpFun.get(1);
    }else{
        shelfview = shelfview.substring(0, shelfview.length()-1);
    }
        return shelfview;
    }

    /**
     * @param olt_id
     * @param onuInstance
     */
    private static ObjService getGponOnuPortNum(String olt_id, String onuInstance) {
        ObjService params = SnmpParamsHelper.getOption(olt_id);
        ObjService conditionObj = SnmpParamsHelper.getOption(olt_id);
        conditionObj.setValue("ConfigFile", "com/raisecom/ems/gpon/server/modules/cfgmgt/profile/2.30/GponConfig.xml");
        conditionObj.setValue("TableName", "rcGponONUBindProfileTable");
        conditionObj.setValue("Instance", IfIndexHelperV2.OnuIndexOld2New(onuInstance));
        ObjService osResult = SnmpUtilities.SnmpOperation(conditionObj, "getValue");

        if (osResult.getStringValue("ErrCode").equalsIgnoreCase("0"))
        {
            String serviceProfileId = osResult.objectAt("RowSet", 0).getStringValue("rcGponONUSvrprofileId");
            //业务模板获取不到认为ONU不存在
            if("null".equalsIgnoreCase(serviceProfileId)){
                Poster.error(EPONConstants.EPON_RB.getString("GPON_ONU_NOT_EXIST"));
                return null;
            }
            conditionObj.setValue("TableName", "rcGponOnuBindServiceProfileSlotTable");
            conditionObj.setValue("Instance", serviceProfileId+".255");
            osResult = SnmpUtilities.SnmpOperation(conditionObj, "getValue");
            if (osResult.getStringValue("ErrCode").equalsIgnoreCase("0")){
                ObjService rowSet = osResult.objectAt("RowSet", 0);
                return rowSet;
            }
        }
        return null;
    }

    private static Map<Integer, String> getGpOnuFunMap() {
        Map<Integer, String> onuGpFun = new HashMap();
        onuGpFun.put(1, GPONConstants.DATA);
        onuGpFun.put(2, GPONConstants.VIDEO);
        onuGpFun.put(3, GPONConstants.CATV);
        return onuGpFun;
    }

    /**
     * 版本兼容EPON_ONU
     * @param devTypeId
     * @return
     */
    public static boolean isEponOnu(String devTypeId)
    {
        return devTypeId.startsWith("EPON_ONU");
    }

    /**
     * 获取EPON_ONU的统一新类型,由于新的epon onu通用类型添加线路模板所以单独走不同的逻辑
     * EPON ONU按功能分为rc_ep_data、rc_ep_video、rc_ep_catv
     * 根据EPON ONU类型组合shelfview路径
     * @param olt_id
     * @param onuInstance
     * @return shelfview路径组合，以分号隔开
     */
    private static ObjService getEponOnuDevVendorType(String olt_id, String onuInstance){
        ObjService conditionObj = SnmpParamsHelper.getOption(olt_id);
        conditionObj.setValue("ConfigFile", "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/2.50/ONUCommon.xml");
        conditionObj.setValue("TableName", "rcEponOnuTypeValueTable");
        conditionObj.setValue("Instance", onuInstance);
        ObjService result =SnmpUtilities.SnmpOperation(conditionObj, "getValue");
        return result.objectAt("RowSet", 0);
    }

    //	通用epon onu添加shlefview获取方法
    private static String getNewEponOnuType(String olt_id, String onuInstance, String typeValue)
    {
        ObjService bindPortInfo = getNewEponOnuPortNum(olt_id, onuInstance);
        if(bindPortInfo == null){
            Poster.error(EPONConstants.EPON_RB.getString("ONU_NOT_EXIST"));
            return null;
        }
        String ethNum = bindPortInfo.getStringValue("rcEponOnuSvrTemplateUniEthNum");
        String potsNum = bindPortInfo.getStringValue("rcEponOnuSvrTemplatePotsNum");
        String tdmNum = bindPortInfo.getStringValue("rcEponOnuSvrTemplateTdmNum");
        Map<Integer, String> onuEpFun = getEpOnuFunMap();
        String shelfview = "";
        if(Integer.parseInt(ethNum) != 0){
            if(typeValue.equals(EPONConstants.RC_ONU_TYPE)){
                shelfview += onuEpFun.get(10) + ";" + onuEpFun.get(13)+ ";";
            }else if(typeValue.equals(EPONConstants.OEM_ONU_TYPE)){
                shelfview += onuEpFun.get(4) + ";";
            }else if(typeValue.equals(EPONConstants.OTHERS_ONU_TYPE)){
                shelfview += onuEpFun.get(1) + ";";
            }else{
                shelfview += onuEpFun.get(1) + ";";
            }
        }
        if(Integer.parseInt(potsNum) != 0){

            if(typeValue.equals(EPONConstants.RC_ONU_TYPE)){
                shelfview += onuEpFun.get(11) + ";";
            }else if(typeValue.equals(EPONConstants.OEM_ONU_TYPE)){
                shelfview += onuEpFun.get(5) + ";";
            }else if(typeValue.equals(EPONConstants.OTHERS_ONU_TYPE)){
                shelfview += onuEpFun.get(2) + ";";
            }else{
                shelfview += onuEpFun.get(2) + ";";
            }
        }
        if(Integer.parseInt(tdmNum) != 0){

            if(typeValue.equals(EPONConstants.RC_ONU_TYPE)){
                shelfview += onuEpFun.get(12) + ";";
            }else if(typeValue.equals(EPONConstants.OEM_ONU_TYPE)){
                shelfview += onuEpFun.get(6) + ";";
            }else if(typeValue.equals(EPONConstants.OTHERS_ONU_TYPE)){
                shelfview += onuEpFun.get(3) + ";";
            }else{
                shelfview += onuEpFun.get(3) + ";";
            }
        }
        if("".equals(shelfview)){
            shelfview += onuEpFun.get(10)+ ";" + onuEpFun.get(13)+ ";";
        }else{
            shelfview = shelfview.substring(0, shelfview.length()-1);
        }
        return shelfview;
    }

    /**获取EPON_ONU的统一类型
     *EPON ONU按功能分为ctc_ep_data、ctc_ep_video、ctc_ep_catv
     * 根据EPON ONU类型组合shelfview路径
     * @param olt_id
     * @param onuInstance
     * @param typeValue
     * @return shelfview路径组合，以分号隔开
     */
    private static String getEponOnuType(String olt_id, String onuInstance, String typeValue)
    {
        ObjService bindPortInfo = getEponOnuPortNum(olt_id, onuInstance);
        String ethNum = "0";
        String portNum = "0";
        if(bindPortInfo.objectSize("SvrProfile") == 1){
            ethNum = bindPortInfo.objectAt("SvrProfile", 0).getStringValue("rcEponOnuSvrTemplateUniEthNum");
        }
        if(bindPortInfo.objectSize("VoipProfile") == 1){
            portNum = bindPortInfo.objectAt("VoipProfile", 0).getStringValue("rcEponOnuVoipProfilePotsNum");
        }
        Map<Integer, String> onuEpFun = getEpOnuFunMap();
        String shelfview = "";
        if(Integer.parseInt(ethNum) != 0){
            if(typeValue.equals(EPONConstants.RC_ONU_TYPE)){
                shelfview += onuEpFun.get(7) + ";";
            }else if(typeValue.equals(EPONConstants.OEM_ONU_TYPE)){
                shelfview += onuEpFun.get(4) + ";";
            }else if(typeValue.equals(EPONConstants.OTHERS_ONU_TYPE)){
                shelfview += onuEpFun.get(1) + ";";
            }else{
                shelfview += onuEpFun.get(1) + ";";
            }
        }
        if(Integer.parseInt(portNum) != 0){

            if(typeValue.equals(EPONConstants.RC_ONU_TYPE)){
                shelfview += onuEpFun.get(8) + ";";
            }else if(typeValue.equals(EPONConstants.OEM_ONU_TYPE)){
                shelfview += onuEpFun.get(5) + ";";
            }else if(typeValue.equals(EPONConstants.OTHERS_ONU_TYPE)){
                shelfview += onuEpFun.get(2) + ";";
            }else{
                shelfview += onuEpFun.get(2) + ";";
            }
        }
        if("".equals(shelfview)){
            shelfview += onuEpFun.get(1);
        }else{
            shelfview = shelfview.substring(0, shelfview.length()-1);
        }
        return shelfview;
    }

    /**
     *通过ONU索引获取ONU绑定的业务模板、语音模板信息
     * @param olt_id
     * @param onuInstance
     * @returnONU绑定的业务模板SvrProfile、语音模板VoipProfile  ObjService
     */
    public static ObjService getEponOnuPortNum(String olt_id, String onuInstance) {

        ObjService result = new ObjService();
        ObjService onuResult = getOnuCreateInfo(olt_id, onuInstance); //
        if (onuResult.getStringValue("ErrCode").equalsIgnoreCase("0"))
        {
            if(onuResult.objectSize("RowSet") ==1){ //获取到了ONU创建信息
                String svrProfileId = onuResult.objectAt("RowSet", 0).getStringValue("rcEponONUSvrProfileId");
                if(!svrProfileId.equals("0")){ //ONU绑定了业务模板
                    ObjService svrProfileResult = getServiceProfileByProfileId(olt_id, svrProfileId);
                    if (svrProfileResult.getStringValue("ErrCode").equalsIgnoreCase("0")){
                        if(svrProfileResult.objectSize("RowSet") ==1){ //获取到了业务模板信息
                            ObjService svrRowSet = svrProfileResult.objectAt("RowSet", 0);
                            svrRowSet.setClsName("SvrProfile");
                            result.addContainedObject(svrRowSet);
                            String voipProfileId = svrRowSet.getStringValue("rcEponOnuSvrTemplateVoipProfileId");
                            if(!voipProfileId.equals("0")){ //业务模板绑定了语音模板
                                ObjService voipProfileResult = getVoipProfileByProfileId(olt_id, voipProfileId);
                                if(voipProfileResult.objectSize("RowSet") == 1){ //获取到了语音模板信息
                                    ObjService voipRowSet = voipProfileResult.objectAt("RowSet", 0);
                                    voipRowSet.setClsName("VoipProfile");
                                    result.addContainedObject(voipRowSet);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * * @param olt_id
     * @param onuInstance
     * @return 根据ONU索引获取ONU创建信息
     */
    private static ObjService getOnuCreateInfo(String olt_id, String onuInstance) {
        ObjService result;
        ObjService params = SnmpParamsHelper.getOption(olt_id);
        params.setValue("ConfigFile", "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/1.44/5800Common.xml");
        params.setValue("TableName", "CmdCreatEPONONU");
        String OltSoftVer = VersionUtil.getOltVersionByNeID(olt_id);
        if(Float.parseFloat(OltSoftVer) >= 2.0){
            params.setValue("Instance", IfIndexHelperV2.OnuIndexOld2New(onuInstance));
        }else{
            params.setValue("Instance", onuInstance);
        }
        Driver driver = Driver.getDriver(params);
        result = driver.getValue(params);
        return result;
    }

    /**
     * @param olt_id
     * @param serviceProfileId
     * @return 根据业务模板Id获取对应业务模板信息
     */
    private static ObjService getServiceProfileByProfileId(String olt_id, String serviceProfileId) {
        ObjService params = SnmpParamsHelper.getOption(olt_id);
        params.setValue("ConfigFile", "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/1.44/5800Common.xml");
        params.setValue("TableName", "eponOnuSvrTemplate");
        ObjService rowSet= new ObjService("Instance");
        rowSet.setValue(serviceProfileId, "");
        params.addContainedObject(rowSet);
        Driver driver = Driver.getDriver(params);
        ObjService result = driver.getValue(params);
        return result;
    }

    /**
     * @param olt_id
     * @param voipProfileId
     * @return 根据语音模板Id获取对应语音模板信息
     */
    private static ObjService getVoipProfileByProfileId(String olt_id, String voipProfileId) {
        ObjService params = SnmpParamsHelper.getOption(olt_id);
        params.setValue("ConfigFile", "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/1.44/5800Common.xml");
        params.setValue("TableName", "eponOnuVoipProfile");
        ObjService rowSet= new ObjService("Instance");
        rowSet.setValue(voipProfileId, "");
        params.addContainedObject(rowSet);
        Driver driver = Driver.getDriver(params);
        ObjService result = driver.getValue(params);
        return result;
    }

    private static Map<Integer, String> getEpOnuFunMap() {
        Map<Integer, String> onuEpFun = new HashMap<Integer, String>();
        onuEpFun.put(1, EPONConstants.DATA);
        onuEpFun.put(2, EPONConstants.VIDEO);
        onuEpFun.put(3, EPONConstants.CATV);
        onuEpFun.put(4, EPONConstants.OEM_DATA);
        onuEpFun.put(5, EPONConstants.OEM_VIDEO);
        onuEpFun.put(6, EPONConstants.OEM_CATV);
        onuEpFun.put(7, EPONConstants.RC_DATA);
        onuEpFun.put(8, EPONConstants.RC_VIDEO);
        onuEpFun.put(9, EPONConstants.RC_CATV);
        onuEpFun.put(10, EPONConstants.RC_ONU_DATA);
        onuEpFun.put(11, EPONConstants.RC_ONU_POTS);
        onuEpFun.put(12, EPONConstants.RC_ONU_TDM);
        onuEpFun.put(13, EPONConstants.RC_ONU_OTHER);
        return onuEpFun;
    }
    /**
     * 2.61版本之后通用onu类型添加时获取端口数目
     * @param olt_id
     * @param onuInstance
     */
    private static ObjService getNewEponOnuPortNum(String olt_id, String onuInstance) {
        ObjService conditionObj = SnmpParamsHelper.getOption(olt_id);
        conditionObj.setValue("ConfigFile", "com/raisecom/ems/epon/server/modules/cfgmgt/profile/2.62/EponConfig.xml");
        conditionObj.setValue("TableName", "rcEponONUEntry");
        conditionObj.setValue("Instance", IfIndexHelperV2.OnuIndexOld2New(onuInstance));
        ObjService osResult = SnmpUtilities.SnmpOperation(conditionObj, "getValue");

        if (osResult.getStringValue("ErrCode").equalsIgnoreCase("0"))
        {
            String serviceProfileId = osResult.objectAt("RowSet", 0).getStringValue("rcEponONUSvrprofileId");
            conditionObj.setValue("ConfigFile", "com/raisecom/ems/epon/server/modules/cfgmgt/profile/2.61/EponConfig.xml");
            conditionObj.setValue("TableName", "rcEponOnuSvrTemplateTable");
            conditionObj.setValue("Instance", serviceProfileId);
            osResult = SnmpUtilities.SnmpOperation(conditionObj, "getValue");
            if (osResult.getStringValue("ErrCode").equalsIgnoreCase("0")){
                ObjService rowSet = osResult.objectAt("RowSet", 0);
                return rowSet;
            }
        }
        return null;
    }

    public static String getOnuTypeInMibFromDb(String neTypeID) {

        ObjService node = null;
        try {
            String tempStr1 = neTypeID;
            tempStr1 = dealwithNEType(tempStr1,"");
            String tempStr2 = neTypeID;
            tempStr2 = dealwithNEType(tempStr2,"-");
            String tempStr3 = "ISCOM"+tempStr2;
            String tempStr4 =neTypeID;
            tempStr4 = tempStr4.replaceFirst("ISCOM", "");
            node = EPONCommonDBUtil.getInstance().getAllObjServicesFromDB("TYPE_IN_MIB", "rcnetype",
                    "iRCNETypeID in ('" + tempStr1 + "','"+tempStr2+"','"+neTypeID+"','"+tempStr3 +"','"+tempStr4+"')");
        } catch (Exception e) {
            return null;
        } finally {
//			operator.free();
        }
        if (node == null || node.getAllChildObjects().size() == 0) {
            EPONConstants.logger.debug("iRCNETypeID " + neTypeID + " is not exist ! ");
            return null;
        } else {
            return node.objectAt("RowObj", 0).getStringValue("TYPE_IN_MIB");
        }

    }

    // 比较版本号，将相应的信息缓存到相应的maxVersionMapping中。
    private static void compareVersion(List<String> list) {
        for (String s : list) {
            String[] ss = s.split("#");
            String olt_type = ss[0];
            String olt_ver = ss[1];
            try{
                String baseVer = (int) (Float.parseFloat(olt_ver)) + "";
                if (maxVersionMapping.get(olt_type + "#" + baseVer) != null) {
                    int r = compare(olt_ver, maxVersionMapping.get(olt_type + "#" + baseVer));
                    if (r > 0){
                        maxVersionMapping.put(olt_type + "#" + baseVer, olt_ver);
                    }
                } else {
                    maxVersionMapping.put(olt_type + "#" + baseVer, olt_ver);
                }
            }catch(Exception e){
                EPONConstants.logger.error(olt_type + ":"+ olt_ver + " cache failed!\t\n" + e);
            }
        }
    }


    private static int compare(String o1, String o2) {
        int vInt1 = (int) (Float.parseFloat(o1));
        int vInt2 = (int) (Float.parseFloat(o2));
        if (vInt1 != vInt2) {
            return vInt1 - vInt2;
        } else {
            String s1 = o1.substring(o1.indexOf(".") + 1);
            String s2 = o2.substring(o2.indexOf(".") + 1);
            int v1 = Integer.parseInt(s1);
            int v2 = Integer.parseInt(s2);
            return v1 - v2;
        }
    }

    private static String dealwithNEType(String neType, String replaceStr){
        neType = neType.replaceAll("\\(", replaceStr);
        neType = neType.replaceAll("\\)", "");
        return neType;
    }


}
