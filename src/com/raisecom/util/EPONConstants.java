package com.raisecom.util;

import com.raisecom.common.logging.LogFactory;
import com.raisecom.common.logging.Logger;
import com.raisecom.ems.platform.util.FrameworkConstants;
import com.raisecom.nms.platform.client.ResourceManager;

import java.util.ResourceBundle;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class EPONConstants implements FrameworkConstants {
    public static Logger logger               = LogFactory.getLogger("EPON");

    public static Logger         EPON_ONU_auto_logger               = LogFactory.getLogger("EPON_ONU_auto");

    public static Logger         Trap_Operator_logger = LogFactory.getLogger("EPON_Trap_Operator");

    public static ResourceBundle EPON_RB              = ResourceManager.getResourceBundle("config/Resource");

    public static ResourceBundle EPON_CFM             = ResourceManager.getResourceBundle("com/raisecom/ems/xpon/packetswitch/resource/Resource");

    public static final String   PATH                 = "com/raisecom/ems/epon/client/profile";

    public static String         profile_path_card    = "com/raisecom/ems/epon/client/profile/card";

    public static String         profile_path_element = "com/raisecom/ems/epon/client/profile/element";

    public static String         profile_path_device  = "com/raisecom/ems/epon/client/profile/device";

    public static String         profile_path_port    = "com/raisecom/ems/epon/client/profile/port";

    public static String         MODEL_BUILDER_CLASS  = "com.raisecom.ems.epon.server.model.EponModelBuilder";

    public static String         RES_5504A_MIB_FILE   = "com/raisecom/ems/epon/digger/Epon5504AResourceDigger.xml";

    public static String         RES_5504B_MIB_FILE   = "com/raisecom/ems/epon/digger/Epon5504BResourceDigger.xml";

    //com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/5504Common.xml
    public static String         MIB_CONFIG_FILE_5504Common  = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/5504Common.xml"; //原来的EPONV2.xml

    public static String         MIB_CONFIG_FILE_5504Switch  = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/5504Switch.xml"; //原来的IscomSwitch.xml

    public static String         MIB_CONFIG_FILE_5800Common  = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/5800Common.xml";//原来的ISCOM5600.xml

    public static String         MIB_CONFIG_FILE_6800Common  = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/2.0/6800Common.xml";

    public static String         MIB_CONFIG_FILE_V1_30_5800Common  = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/1.30/5800Common.xml";//V1.30版本配置文件
    public static String         MIB_CONFIG_FILE_V1_31_5800Common  = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/1.31/5800Common.xml";

    public static String         MIB_CONFIG_FILE_CREATEONU_V1_32_ONUCommon  = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/1.32/ONUCommon.xml";
    public static String   MIB_CONFIG_FILE_CREATEONU_V2_30_GPONONUCommon="com/raisecom/ems/gpon/server/modules/cfgmgt/profile/2.30/GponConfig.xml";
    public static String         MIB_CONFIG_FILE_5800Switch = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/5800Switch.xml"; //原来的ISCOM2924GF.xml

    public static String         MIB_CONFIG_FILE_ONUCommon = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/ONUCommon.xml";  // 新增ONU公共部分的配置文件

    public static String         MIB_CONFIG_FILE_LLID = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/Llid.xml";  // 新增ONU公共部分的配置文件

//    public static String         MIB_CONFIG_FILE_EPONV2 = "com/raisecom/ems/eponcommon/server/modules/cfgmgt/profile/eponv2.rmp";  // eponv2.rmp

    public static String         ONU_MIB_TABLE_NAME   = "rcEponONUTable1";

    public static String         ONU_MIB_TABLE_NAME_2_0   = "CmdCreatONU";

    public static String          SYN_ONU_WORK_STATUS_TABLE = "SynOnuWorkStatus";

    public static String         ONUDPROTECTGROUP_MIB_TABLE_NAME   = "rcEponOnuDProtectGroupTable";

    public static final int      ONU_POTS_BASE        = 80;

    public static final int      ONU_CATV_BASE        = 160;

    public static final int      ONU_RF_BASE        = 162;

    public static final int      ONU_E1_BASE          =144;

    public static final int      PON_MAX_BW           = 1003520;

    public static ResourceBundle PON_MIB_RB           = ResourceManager.getResourceBundle("com.raisecom.ems.epon.client.resource.pon.Resource");

    public static String         CFG_MENU             = "CFG_MENU";

    public static String         CFG_URLS             = "CFG_URLS";

    public static String         CFG_ID               = "CFG_ID";

    public static ResourceBundle EPON_PACKETSWITCH          = ResourceManager.getResourceBundle("com.raisecom.ems.xpon.packetswitch.resource.Resource");

    //设备巡检配置位置国际化位置
    public static ResourceBundle EPON_CFG_RB          = ResourceManager.getResourceBundle("com.raisecom.ems.eponcommon.client.cfgmgt.resource.Resource");

    public static ResourceBundle EPON_STATISTICS_RB          = ResourceManager.getResourceBundle("com.raisecom.ems.eponcommon.client.cfgmgt.resource.Resource");

    public static String         EPON_CFG_LABEL       = ResourceManager.getString(EPON_CFG_RB, "title.EPON_CFG_MGT");

    public static ResourceBundle EPONCOMMON_CLIENT_CFGMGT_PROFILE_RB  = ResourceManager.getResourceBundle("com.raisecom.ems.eponcommon.client.cfgmgt.profile.default_resource");

    public static ResourceBundle OAM_MIB_RB = ResourceManager.getResourceBundle("com.raisecom.ems.epon.client.resource.oam.Resource");

    public static ResourceBundle SNMP_MIB_RB = ResourceManager.getResourceBundle("com.raisecom.ems.iscom5108.server.resource.base.Resource");

    public static ResourceBundle HGU_CFG_RB = ResourceManager.getResourceBundle("com.raisecom.ems.onu.hgu.client.resource.Resource");

    public static final String POWER_NULL = "0";

    public static final String POWER_AC = "1";

    public static final String POWER_DC = "2";

    public static final int max  = 64;

    public static final String POWER_AC_II = "3";

    public static final String POWER_DC48V = "4";

    public static final String INDEX_IN_MIB_OF_5800EB = ".1.3.6.1.4.1.8886.18.4.6";

    public static final String INDEX_IN_MIB_OF_6800SHDL = ".1.3.6.1.4.1.8886.18.4.19";

    public static final String SLOT_NUM_DESC_FILE = "com/raisecom/ems/eponcommon/server/modules/trap/util/SlotNumDesc.xml";

    public static final String SlotNumLabel = "SlotNum";

    public static final String AUTHORITY_SYN_CONFIG = "0701";

    public static final String AUTHORITY_VIEW_CONFIG = "0702";

    public static final String AUTHORITY_MODIFY_CONFIG = "0703";

    public static final String AUTHORITY_PRE_CONFIG = "0704";

    public static final String AUTHORITY_BATCH_CONFIG = "0705";

    public static final String AUTHORITY_DELETE_LOCAL_CARD = "0130";

    public static final int EPON_MAX_ONU_REGISTER_NUM = 64;

    public static final String XPON_THREAD_PROFIX = "XPON_";

    public static final String EPON_ONU_UNI    = ResourceManager.getString(PON_MIB_RB, "EPON_ONU_UNI_ID");

    public static final String VOIP_XML_CONFIG_UNSEND    = "0";

    public static final String VOIP_XML_CONFIG_SENDED    = "1";

    public static final String VOIP_XML_CONFIG_SUCCESS    = "2";

    public static final String VOIP_XML_CONFIG_UNSUCCESS    = "3";

    public static final String WAN_XML_CONFIG_SENDED = "6";

    public static final String WAN_XML_CONFIG_SUCCESS    = "7";

    public static final String WAN_XML_CONFIG_UNSUCCESS    = "7";


    public static final String RC_ONU_TYPE  = "1";

    public static final String OEM_ONU_TYPE  = "2";

    public static final String OTHERS_ONU_TYPE  = "3";

    public static String DATA  = "ctc_ep_data";

    public static String VIDEO = "ctc_ep_voice";

    public static String CATV  = "ctc_ep_catv";

    public static String OEM_DATA  = "oem_ep_data";

    public static String OEM_VIDEO = "oem_ep_voice";

    public static String OEM_CATV  = "oem_ep_catv";

    public static String RC_DATA  = "rc_ep_data";

    public static String RC_VIDEO = "rc_ep_voice";

    public static String RC_CATV  = "rc_ep_catv";

    public static String RC_ONU_POTS = "rc_onu_ep_pots";

    public static String RC_ONU_TDM  = "rc_onu_ep_tdm";

    public static String RC_ONU_DATA = "rc_onu_ep_data";

    public static String RC_ONU_OTHER  = "rc_onu_ep_other";

    public static final String EPON_ONU_NETYYPE = "EPON_ONU";

    public static final String GPON_ONU_NETYYPE = "GPON_ONU";

}
