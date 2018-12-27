package com.raisecom.common;

import com.raisecom.ems.platform.util.FrameworkConstants;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public interface GPONConstants extends FrameworkConstants {
    public static String         MIB_CONFIG_FILE_GPONCONFIG  = "com/raisecom/ems/gpon/server/modules/cfgmgt/profile/2.30/GponConfig.xml";
    public static String  SYN_ONU_WORK_STATUS_TABLE = "SynOnuWorkStatus";
    public static String         MIB_CONFIG_FILE_ISCOM5508GP_CONFIG  = "com/raisecom/ems/gpon/server/modules/cfgmgt/profile/2.30/5508GpCommon.xml";

    public static String DATA  = "onu_gp_data";
    public static String VIDEO = "onu_gp_voice";
    public static String CATV  = "onu_gp_catv";

}
