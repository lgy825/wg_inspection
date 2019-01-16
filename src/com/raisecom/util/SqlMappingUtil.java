package com.raisecom.util;

import com.raisecom.bean.OLTInfo;

/**
 * Created by ligy-008494 on 2019/1/11.
 */
public class SqlMappingUtil {

    public static void insertDevice(OLTInfo oltInfo){
        String firstStr="insert into OLT_STATISTICS_INFO(";
        String midStr=")values (";
        String lastStr=")ON DUPLICATE KEY UPDATE ";
        String sql="";
        if(oltInfo!=null && oltInfo.getIrcnetnodeid()!=null){
            firstStr+="IRCNETNODEID";
            midStr+=oltInfo.getIrcnetnodeid();
            if(oltInfo.getFriendly_name()!=null){
                firstStr+=",FRIENDLY_NAME";
                midStr+=",'"+oltInfo.getFriendly_name();
                lastStr+="FRIENDLY_NAME = '" +oltInfo.getFriendly_name();
            }
            if(oltInfo.getIrcnetypeid()!=null){
                firstStr+=",iRCNETypeID";
                midStr+="','"+oltInfo.getIrcnetypeid();
                lastStr+="',iRCNETypeID = '" +oltInfo.getIrcnetypeid();
            }
            if(oltInfo.getIpaddress()!=null){
                firstStr+=",IPADDRESS";
                midStr+="','"+oltInfo.getIpaddress();
                lastStr+="',IPADDRESS = '" +oltInfo.getIpaddress();
            }
            if(oltInfo.getHostname()!=null){
                firstStr+=",HOSTNAME";
                midStr+="','"+oltInfo.getHostname();
                lastStr+="',HOSTNAME = '" +oltInfo.getHostname();
            }
            if(oltInfo.getCpu()!=null){
                firstStr+=",CPU";
                midStr+="','"+oltInfo.getCpu();
                lastStr+="',CPU = '" +oltInfo.getCpu();
            }
            if(oltInfo.getRam()!=null){
                firstStr+=",RAM";
                midStr+="','"+oltInfo.getRam();
                lastStr+="',RAM = '" +oltInfo.getRam();
            }
            if(oltInfo.getTemperature()!=null){
                firstStr+=",TEMPERATURE";
                midStr+="','"+oltInfo.getTemperature();
                lastStr+="',TEMPERATURE = '" +oltInfo.getTemperature();
            }
            if(oltInfo.getPower()!=null){
                firstStr+=",POWER";
                midStr+="','"+oltInfo.getPower();
                lastStr+="',POWER = '" +oltInfo.getPower();
            }
            if(oltInfo.getSoftware_ver()!=null){
                firstStr+=",SOFTWARE_VER";
                midStr+="','"+oltInfo.getSoftware_ver();
                lastStr+="',SOFTWARE_VER = '" +oltInfo.getSoftware_ver();
            }
            if(oltInfo.getVlan_optimize()!=null){
                firstStr+=",VLAN_OPTIMIZE";
                midStr+="','"+oltInfo.getVlan_optimize();
                lastStr+="',VLAN_OPTIMIZE = '" +oltInfo.getVlan_optimize();
            }

            if(oltInfo.getSys_uptime()!=null){
                firstStr+=",SYS_UPTIME";
                midStr+="','"+oltInfo.getSys_uptime();
                lastStr+="',SYS_UPTIME = '" +oltInfo.getSys_uptime();
            }
            if(oltInfo.getSwitched_count()!=null){
                firstStr+=",SWITCHED_COUNT";
                midStr+="','"+oltInfo.getSwitched_count();
                lastStr+="',SWITCHED_COUNT = '" +oltInfo.getSwitched_count();
            }
            if(oltInfo.getReboot_count()!=null){
                firstStr+=",REBOOT_COUNT";
                midStr+="','"+oltInfo.getReboot_count();
                lastStr+="',REBOOT_COUNT = '" +oltInfo.getReboot_count();
            }
            if(oltInfo.getOlt_power()!=null){
                firstStr+=",OLT_POWER";
                midStr+="','"+oltInfo.getOlt_power();
                lastStr+="',OLT_POWER = '" +oltInfo.getOlt_power();
            }
            if(oltInfo.getPort_is_solate()!=null){
                firstStr+=",PORT_IS_SOLATE";
                midStr+="','"+oltInfo.getPort_is_solate();
                lastStr+="',PORT_IS_SOLATE = '" +oltInfo.getPort_is_solate();
            }
            if(oltInfo.getOnu_count_info()!=null){
                firstStr+=",ONU_COUNT_INFO";
                midStr+="','"+oltInfo.getOnu_count_info();
                lastStr+="',ONU_COUNT_INFO = '" +oltInfo.getOnu_count_info();
            }
            sql+=firstStr+midStr+"'"+lastStr+"'";
        }
        try {
            EPONCommonDBUtil.getInstance().executeSql(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
