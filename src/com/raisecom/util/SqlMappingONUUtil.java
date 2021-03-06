package com.raisecom.util;

import com.raisecom.bean.ONUInfo;

/**
 * Created by ligy-008494 on 2019/1/17.
 */
public class SqlMappingONUUtil {

    public static void insertDispectONUInfo(ONUInfo onuInfo){
        String firstStr="insert into onu_statistics_info(";
        String midStr=")values (";
        String lastStr=")ON DUPLICATE KEY UPDATE ";
        String sql="";

        if(onuInfo!=null && onuInfo.getIrcnetnodeid()!=null){
            firstStr+="IRCNETNODEID";
            midStr+=onuInfo.getIrcnetnodeid();
            if(onuInfo.getStatus()!=null){
                firstStr+=",STATUS";
                midStr+=",'"+onuInfo.getStatus();
                lastStr+="STATUS = '" +onuInfo.getStatus();
            }
            if(onuInfo.getIrcnetoltId()!=null){
                firstStr+=",IRCNETOLTID";
                midStr+="','"+onuInfo.getIrcnetoltId();
                lastStr+="',IRCNETOLTID = '" +onuInfo.getIrcnetoltId();
            }
            if(onuInfo.getLastDownCause()!=null){
                firstStr+=",LAST_DOWN_CAUSE";
                midStr+="','"+onuInfo.getLastDownCause();
                lastStr+="',LAST_DOWN_CAUSE = '" +onuInfo.getLastDownCause();
            }
            if(onuInfo.getDistance()!=null){
                firstStr+=",DISTANCE";
                midStr+="','"+onuInfo.getDistance();
                lastStr+="',DISTANCE = '" +onuInfo.getDistance();
            }
            if(onuInfo.getReceivedPower()!=null){
                firstStr+=",RECEIVED_POWER";
                midStr+="','"+onuInfo.getReceivedPower();
                lastStr+="',RECEIVED_POWER = '" +onuInfo.getReceivedPower();
            }
            if(onuInfo.getOnuHangMacCount()!=null){
                firstStr+=",ONU_HANG_MAC";
                midStr+="','"+onuInfo.getOnuHangMacCount();
                lastStr+="',ONU_HANG_MAC = '" +onuInfo.getOnuHangMacCount();
            }
            if(onuInfo.getLoopPort()!=null){
                firstStr+=",LOOP_PORT";
                midStr+="','"+onuInfo.getLoopPort();
                lastStr+="',LOOP_PORT = '" +onuInfo.getLoopPort();
            }
            if(onuInfo.getPortStatus()!=null){
                firstStr+=",PORT_STATUS";
                midStr+="','"+onuInfo.getPortStatus();
                lastStr+="',PORT_STATUS = '" +onuInfo.getPortStatus();
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
