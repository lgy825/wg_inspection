package com.raisecom.exportExcelDemo;

import com.raisecom.bean.CardInfo;
import com.raisecom.nms.util.DBConnectionManager;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by liujs-008398 on 2019-01-23.
 */
public class CardInfoService {

    public static List<CardInfo> getAllByDb(String str)  throws Exception{
        String sqlCard = "SELECT rc.FRIENDLY_NAME,rc.IPADDRESS,rc.iRCNETypeID,ct.CARD_TYPE_NAME," +
                "c.CARD_NAME,c.CARD_STATUS,IFNULL(c.SOFTWARE_VER,'--') AS SOFTWARE_VER," +
                "cs.CPU,cs.MEMORY,cs.TEMPERATURE,cs.POWER " +
                "from card c,card_type ct,card_statistics_info cs,rcnetnode rc " +
                "where c.CARD_TYPE_ID=ct.CARD_TYPE_ID AND c.CARD_ID=cs.CARD_ID " +
                "AND rc.IRCNETNODEID=c.IRCNETNODEID AND rc.iRCNETypeID = ct.NETYPE_ID AND rc.IRCNETNODEID='"+ str +"'";
        String sql = "select * from CARD_STATISTICS_INFO " ;

        List<CardInfo> list = new ArrayList<CardInfo>();
        Connection conn = DBConnectionManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sqlCard);
        ResultSet result = pstmt.executeQuery();

        //连接数据库 将数据放入List中
        try {
            while (result.next()) {
                CardInfo cardInfo = new CardInfo();
                cardInfo.setFriendlyName(result.getString("FRIENDLY_NAME"));
                cardInfo.setIpAddr(result.getString("IPADDRESS"));
                cardInfo.setIrcnetype(result.getString("iRCNETypeID"));
                cardInfo.setCardType(result.getString("CARD_TYPE_NAME"));
                cardInfo.setSlotId(result.getString("CARD_NAME"));
                cardInfo.setStatus(result.getString("CARD_STATUS"));
                cardInfo.setVer(result.getString("SOFTWARE_VER"));

                cardInfo.setCpu(result.getString("CPU"));
                cardInfo.setRam(result.getString("MEMORY"));
                cardInfo.setTemperature(result.getString("TEMPERATURE"));
                cardInfo.setPower(result.getString("POWER"));


                list.add(cardInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnectionManager.getInstance().free(conn, pstmt, result);
        }

        return list;
    }
}
