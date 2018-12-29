package com.raisecom.exportExcelDemo;

import com.raisecom.bean.OLTInfo;
import com.raisecom.db.JdbcUtils_DBCP;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.nms.util.ErrorObjService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujs-008398 on 2018-12-28.
 */
public class OLTInfoService {

    public static List<OLTInfo> getAllByDb() throws SQLException {
        String sql = "select * from OLT_STATISTICS_INFO";

        List<OLTInfo> list = new ArrayList<OLTInfo>();
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            result = pstmt.executeQuery();
//            data = result.getMetaData();
//            int columns = data.getColumnCount();
            while (result.next()) {

                OLTInfo oltInfo = new OLTInfo();
                oltInfo.setIrcnetnodeid(result.getInt("IRCNETNODEID"));
                oltInfo.setFriendly_name(result.getString("FRIENDLY_NAME"));
                oltInfo.setIrcnetypeid(result.getString("iRCNETypeID"));
                oltInfo.setIpaddress(result.getString("IPADDRESS"));
                oltInfo.setHostname(result.getString("HOSTNAME"));
                oltInfo.setSmc(result.getString("SMC"));
                oltInfo.setCpu(result.getString("CPU"));
                oltInfo.setTemperature(result.getString("TEMPERATURE"));
                oltInfo.setPower(result.getString("TEMPERATURE"));
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
                oltInfo.setOnu_count_info(result.getString("ONU_COUNT_INFO"));
                list.add(oltInfo);
            }
        } catch (Exception e) {

        } finally {
            DBConnectionManager.getInstance().free(conn, pstmt, result);
        }

        return list;
    }


}
