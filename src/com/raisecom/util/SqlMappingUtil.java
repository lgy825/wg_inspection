package com.raisecom.util;

import com.raisecom.bean.OLTInfo;

/**
 * Created by ligy-008494 on 2019/1/11.
 */
public class SqlMappingUtil {

    public static void insertDevice(OLTInfo oltInfo){
        String firstStr="insert into OLT_STATISTICS_INFO(";
        String lastStr=")ON DUPLICATE KEY UPDATE FRIENDLY_NAME = '";
        String sql="";
        if(oltInfo!=null && oltInfo.getIrcnetnodeid()!=null){
            firstStr+="IRCNETNODEID";
            lastStr+="FRIENDLY_NAME="+oltInfo.getIrcnetnodeid();
            if(oltInfo.getFriendly_name()!=null){
                firstStr+=",FRIENDLY_NAME";
                lastStr+="FRIENDLY_NAME = '" +oltInfo.getFriendly_name();
            }
            if(oltInfo.getIrcnetypeid()!=null){
                firstStr+=",iRCNETypeID";
                lastStr+="iRCNETypeID = '" +oltInfo.getIrcnetypeid();
            }
            if(oltInfo.getIpaddress()!=null){
                firstStr+=",IPADDRESS";
                lastStr+="IPADDRESS = '" +oltInfo.getIpaddress();
            }
            if(oltInfo.getHostname()!=null){
                firstStr+=",HOSTNAME";
                lastStr+="HOSTNAME = '" +oltInfo.getHostname();
            }
            if(oltInfo.getCpu()!=null){
                firstStr+=",CPU";
                lastStr+="CPU = '" +oltInfo.getCpu();
            }
            if(oltInfo.getRam()!=null){
                firstStr+=",RAM";
                lastStr+="RAM = '" +oltInfo.getRam();
            }
            if(oltInfo.getTemperature()!=null){
                firstStr+=",TEMPERATURE";
                lastStr+="TEMPERATURE = '" +oltInfo.getTemperature();
            }
            if(oltInfo.getPower()!=null){
                firstStr+=",POWER";
                lastStr+="POWER = '" +oltInfo.getPower();
            }
            if(oltInfo.getSoftware_ver()!=null){
                firstStr+=",SOFTWARE_VER";
                lastStr+="SOFTWARE_VER = '" +oltInfo.getSoftware_ver();
            }
            if(oltInfo.getVlan_optimize()!=null){
                firstStr+=",VLAN_OPTIMIZE";
                lastStr+="VLAN_OPTIMIZE = '" +oltInfo.getVlan_optimize();
            }
            if(oltInfo.getSys_uptime()!=null){
                firstStr+=",SYS_UPTIME";
                lastStr+="SYS_UPTIME = '" +oltInfo.getSys_uptime();
            }
            if(oltInfo.getSwitched_count()!=null){
                firstStr+=",SWITCHED_COUNT";
                lastStr+="SWITCHED_COUNT = '" +oltInfo.getSwitched_count();
            }
            if(oltInfo.getReboot_count()!=null){
                firstStr+=",REBOOT_COUNT";
                lastStr+="REBOOT_COUNT = '" +oltInfo.getReboot_count();
            }
            if(oltInfo.getOlt_power()!=null){
                firstStr+=",OLT_POWER";
                lastStr+="OLT_POWER = '" +oltInfo.getOlt_power();
            }
            if(oltInfo.getPort_is_solate()!=null){
                firstStr+=",PORT_IS_SOLATE";
                lastStr+="PORT_IS_SOLATE = '" +oltInfo.getPort_is_solate();
            }
            sql+=firstStr+lastStr;
        }
        try {
            EPONCommonDBUtil.getInstance().executeSql(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
