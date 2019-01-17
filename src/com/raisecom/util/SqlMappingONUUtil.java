package com.raisecom.util;

import com.raisecom.bean.ONUInfo;

/**
 * Created by ligy-008494 on 2019/1/17.
 */
public class SqlMappingONUUtil {

    public void insertDispectONUInfo(ONUInfo onuInfo){
        String firstStr="insert into onu_statistics_info(";
        String midStr=")values (";
        String lastStr=")ON DUPLICATE KEY UPDATE ";
        String sql="";

        if(onuInfo!=null && onuInfo.getIrcnetnodeid()!=null){
            firstStr+="IRCNETNODEID";
            midStr+=onuInfo.getIrcnetnodeid();
            if(onuInfo.getStatus()!=null){
                firstStr+=",STAYUS";
                midStr+=",'"+onuInfo.getStatus();
                lastStr+="STAYUS = '" +onuInfo.getStatus();
            }
            if(onuInfo.getLastDownCause()!=null){
                firstStr+=",LAST_DOWN_CAUSE";
                midStr+=",'"+onuInfo.getLastDownCause();
                lastStr+="LAST_DOWN_CAUSE = '" +onuInfo.getLastDownCause();
            }
            if(onuInfo.getDistance()!=null){
                firstStr+=",DISTANSE";
                midStr+=",'"+onuInfo.getDistance();
                lastStr+="DISTANSE = '" +onuInfo.getDistance();
            }
            if(onuInfo.getReceivedPower()!=null){
                firstStr+=",RECEIVED_POWER";
                midStr+=",'"+onuInfo.getReceivedPower();
                lastStr+="RECEIVED_POWER = '" +onuInfo.getReceivedPower();
            }
            if(onuInfo.getOnuHangMacCount()!=null){
                firstStr+=",ONU_HANG_MAC";
                midStr+=",'"+onuInfo.getOnuHangMacCount();
                lastStr+="ONU_HANG_MAC = '" +onuInfo.getOnuHangMacCount();
            }
            if(onuInfo.getLoopPort()!=null){
                firstStr+=",LOOP_PORT";
                midStr+=",'"+onuInfo.getLoopPort();
                lastStr+="LOOP_PORT = '" +onuInfo.getLoopPort();
            }
            if(onuInfo.getPortStatus()!=null){
                firstStr+=",PORT_STATUS";
                midStr+=",'"+onuInfo.getPortStatus();
                lastStr+="PORT_STATUS = '" +onuInfo.getPortStatus();
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
