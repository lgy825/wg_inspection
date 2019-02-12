package com.raisecom.exportExcelDemo;

import com.raisecom.bean.OLTInfo;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.util.EPONCommonDBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by liujs-008398 on 2018-12-28.
 */
public class OLTInfoService {

    public static String substraction(String count1,String count2){
        Integer inresult = Integer.valueOf(count1) - Integer.valueOf(count2);
        return inresult.toString();

    }


    public static List<OLTInfo> getAllByDb(List<String> str)  throws Exception{
        String temp = "";
        String tempstr = "";
        for(int i = 0 ; i < str.size() ; i++){
            if(str.size() == 1 ){
                temp = "'/ne="+str.get(i).toString()+"'";
                tempstr = "'" + str.get(i).toString() +"'";
            }
            else {
                if(i == str.size()-1){
                    temp += "'/ne=" + str.get(i).toString() + "'";
                    tempstr += "'" + str.get(i).toString() +"'" ;
                }else{
                    temp += "'/ne=" + str.get(i).toString() + "',";
                    tempstr += "'" + str.get(i).toString() +"',";
                }
            }
       }
//        String sql = "select * from OLT_STATISTICS_INFO where IRCNETNODEID ="+ str ;
//        String onuCountsql = "SELECT iRCNETypeID ,COUNT(*) AS Device_Number " +
//                "FROM rcnetnode WHERE managed_url= '/ne="+str+"' GROUP BY iRCNETypeID ";
//        String onlineCountsql = "select iRCNETypeID ,COUNT(*) AS Online_Number " +
//                "FROM rcnetnode WHERE ISPINGOK = '1' and managed_url= '/ne="+str+"' GROUP BY iRCNETypeID ";
//        String offlineCountsql = "select iRCNETypeID ,COUNT(*) AS Offline_Number " +
//                "FROM rcnetnode WHERE ISPINGOK = '0' and managed_url= '/ne="+str+"' GROUP BY iRCNETypeID ";

        String sql = "select * from OLT_STATISTICS_INFO where IRCNETNODEID in ("+ tempstr +")";
        String onuCountsql = "SELECT iRCNETypeID ,COUNT(*) AS Device_Number " +
                "FROM rcnetnode WHERE managed_url in ("+ temp +")"+" GROUP BY iRCNETypeID ";
        String onlineCountsql = "select iRCNETypeID ,COUNT(*) AS Online_Number " +
                "FROM rcnetnode WHERE ISPINGOK = '1' and managed_url in ("+ temp +")"+" GROUP BY iRCNETypeID ";
        String offlineCountsql = "select iRCNETypeID ,COUNT(*) AS Offline_Number " +
                "FROM rcnetnode WHERE ISPINGOK = '0' and managed_url in ("+ temp +")"+" GROUP BY iRCNETypeID ";
        List<OLTInfo> list = new ArrayList<OLTInfo>();
        String onu_count = "";
        String epon_onu_count = "";
        String gpon_onu_count = "";
        String unknown_onu_count = "";
        String unknowne_onu_count = "";
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet result=null;
        try {
            conn = DBConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            result = pstmt.executeQuery();
            ObjService countResult = EPONCommonDBUtil.executeQuery(onuCountsql);
            ObjService onlineResult = EPONCommonDBUtil.executeQuery(onlineCountsql);

            //连接数据库 将数据放入List中

            while (result.next()) {
                OLTInfo oltInfo = new OLTInfo();
                oltInfo.setIrcnetnodeid(result.getInt("IRCNETNODEID"));
                oltInfo.setFriendly_name(result.getString("Friendly_Name"));
                oltInfo.setIrcnetypeid(result.getString("iRCNETypeID"));
                oltInfo.setIpaddress(result.getString("IPADDRESS"));
                oltInfo.setHostname(result.getString("HOSTNAME"));
                oltInfo.setSmc(result.getString("SMC"));
                oltInfo.setCpu(result.getString("CPU"));
                oltInfo.setTemperature(result.getString("TEMPERATURE"));
                oltInfo.setPower(result.getString("POWER"));
                oltInfo.setFan(result.getString("FAN"));
                oltInfo.setSoftware_ver(result.getString("SOFTWARE_VER"));
                oltInfo.setBussiness_card_amount(result.getString("BUSSINESS_CARD_AMOUNT"));
                oltInfo.setVlan_optimize(result.getString("VLAN_OPTIMIZE"));
                oltInfo.setRam(result.getString("RAM"));
                oltInfo.setSys_uptime(result.getString("SYS_UPTIME"));
                oltInfo.setSwitched_count(result.getInt("SWITCHED_COUNT"));
                oltInfo.setReboot_count(result.getInt("REBOOT_COUNT"));
                oltInfo.setOlt_power(result.getString("OLT_POWER"));
                oltInfo.setPort_is_solate(result.getString("PORT_IS_SOLATE"));
                String eponOnCount = "0";
                String gponOnCount = "0";
                String ungOnCount = "0";
                String uneOnCount = "0";
                String eponOnuCount = "0";
                String gponOnuCount = "0";
                String gUnknown = "0";
                String eUnKnown = "0";
                for(int i = 0;i < countResult.objectSize("row");i ++){

                    ObjService objService = countResult.objectAt("row",i);
                    String iRCNETypeID = objService.getStringValue("iRCNETypeID");
                    if("EPON_ONU".equals(iRCNETypeID)){
                        eponOnuCount = objService.getStringValue("Device_Number");
                    }else if("GPON_ONU".equals(iRCNETypeID)){
                        gponOnuCount = objService.getStringValue("Device_Number");
                    }else if("UNKNOWN".equals(iRCNETypeID)){
                        gUnknown = objService.getStringValue("Device_Number");
                    }else{
                        eUnKnown = objService.getStringValue("Device_Number");
                    }

                    int count = onlineResult.objectSize("row");

                    if(i<count){
                        ObjService onlineOBj = onlineResult.objectAt("row",i);
                        String deviceType = onlineOBj.getStringValue("iRCNETypeID");
                        if("EPON_ONU".equals(deviceType)){
                            eponOnCount = onlineOBj.getStringValue("Online_Number");
                        }else if("GPON_ONU".equals(deviceType)){
                            gponOnCount = onlineOBj.getStringValue("Online_Number");
                        }else if("UNKNOWN".equals(deviceType)){
                            ungOnCount = onlineOBj.getStringValue("Online_Number");
                        }else{
                            uneOnCount = onlineOBj.getStringValue("Online_Number");
                        }
                    }
                }
                String deviceNumstr = "Device_Number: ";
                String onlineCountstr = "Online_Number: ";
                String offlineCountstr = "Offline_Number: ";
                epon_onu_count = "Device Type: EPON_ONU " +deviceNumstr+eponOnuCount +" "+onlineCountstr +eponOnCount+" "+offlineCountstr+substraction(eponOnuCount,eponOnCount)+"\r\n" ;
                gpon_onu_count = "Device Type: GPON_ONU " +deviceNumstr+gponOnuCount +" "+onlineCountstr +gponOnCount+" "+offlineCountstr+substraction(gponOnuCount,gponOnCount)+"\r\n" ;
                unknown_onu_count = "Device Type: UNKNOWN " +deviceNumstr+gUnknown +" "+onlineCountstr +ungOnCount+" "+offlineCountstr+substraction(gUnknown,ungOnCount)+"\r\n" ;
                unknowne_onu_count = "Device Type: UNKNOWN(E) " +deviceNumstr+eUnKnown +" "+onlineCountstr +uneOnCount+" "+offlineCountstr+substraction(eUnKnown,uneOnCount)+"\r\n" ;
                onu_count = epon_onu_count + gpon_onu_count + unknown_onu_count + unknowne_onu_count ;
                oltInfo.setOnu_count_info(onu_count);
                list.add(oltInfo);
            }
        } catch (Exception e) {

        } finally {
            DBConnectionManager.getInstance().free(conn, pstmt, result);
        }

        return list;
    }


}
