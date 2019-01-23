package com.raisecom.exportExcelDemo;


import com.raisecom.bean.ONUInfo;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.util.EPONCommonDBUtil;

import java.sql.*;
import java.util.*;

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
        //缓存数据
        Vector<ObjService> verctor = resultONUInfo.getAllChildObjects();
        Map<Integer,ObjService> map = new HashMap();
        for(ObjService obj : verctor){
            Integer iRCNENODEID = obj.getIntValue("IRCNETNODEID");
            map.put(iRCNENODEID,obj);
        }

        //连接数据库 将数据放入List中
        try {
            while (result.next()) {
                ONUInfo onuInfo = new ONUInfo();
                onuInfo.setIrcnetnodeid(result.getInt("IRCNETNODEID"));
                if(map.containsKey(result.getInt("IRCNETNODEID"))){
                    ObjService obj = map.get(result.getInt("IRCNETNODEID"));
                    onuInfo.setIpaddress(obj.getStringValue("IPADDRESS"));
                    onuInfo.setFriendlyName(obj.getStringValue("FRIENDLY_NAME"));
                    onuInfo.setSubnetype(obj.getStringValue("iRCNETypeID"));
                    onuInfo.setSoftware(obj.getStringValue("SOFTWARE_VER"));
                    onuInfo.setMacaddress(obj.getStringValue("MACADDRESS"));
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
