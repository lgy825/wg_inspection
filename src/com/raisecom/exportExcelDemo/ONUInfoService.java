package com.raisecom.exportExcelDemo;


import com.raisecom.bean.ONUInfo;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.util.EPONCommonDBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujs-008398 on 2018-12-28.
 */
public class ONUInfoService {


    public static List<ONUInfo> getAllByDb(String str)  throws Exception{
        String sqlONU = "select IRCNETNODEID,IPADDRESS,FRIENDLY_NAME,iRCNETypeID,IFNULL(SOFTWARE_VER,'--') AS SOFTWARE_VER,IFNULL(MACADDRESS,'--') AS MACADDRESS  from rcnetnode " +
                "WHERE MANAGED_URL = '/ne=" +str +"'";
        String sql = "select * from ONU_STATISTICS_INFO " ;

        List<ONUInfo> list = new ArrayList<ONUInfo>();
        Connection conn = DBConnectionManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet result = pstmt.executeQuery();
        ObjService resultONUInfo = EPONCommonDBUtil.executeQuery(sqlONU);



        //连接数据库 将数据放入List中
        try {
            while (result.next()) {

                ONUInfo onuInfo = new ONUInfo();
                onuInfo.setIrcnetnodeid(result.getInt("IRCNETNODEID"));
                for(int i = 0;i < resultONUInfo.objectSize("row"); i ++) {
                    ObjService objService = resultONUInfo.objectAt("row", i);
                    Integer iRCNENODEID = objService.getIntValue("IRCNETNODEID");
                    if(iRCNENODEID.equals(result.getInt("IRCNETNODEID")) || iRCNENODEID == result.getInt("IRCNETNODEID")){
                        onuInfo.setIpaddress(objService.getStringValue("IPADDRESS"));
                        onuInfo.setFriendlyName(objService.getStringValue("FRIENDLY_NAME"));
                        onuInfo.setSubnetype(objService.getStringValue("iRCNETypeID"));
                        onuInfo.setSoftware(objService.getStringValue("SOFTWARE_VER"));
                        onuInfo.setMacaddress(objService.getStringValue("MACADDRESS"));
                        continue;
                    }
                }
                onuInfo.setStatus(result.getString("STATUS"));
                onuInfo.setLastDownCause(result.getString("LAST_DOWN_CAUSE"));
                onuInfo.setDistance(result.getString("DISTANCE"));
                onuInfo.setReceivedPower(result.getString("RECEIVED_POWER"));
                onuInfo.setOnuHangMacCount(result.getString("ONU_HANG_MAC"));
                onuInfo.setLoopPort(result.getString("LOOP_PORT"));
                onuInfo.setPortStatus(result.getString("PORT_STATUS"));

                list.add(onuInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnectionManager.getInstance().free(conn, pstmt, result);
        }

        return list;
    }


}
