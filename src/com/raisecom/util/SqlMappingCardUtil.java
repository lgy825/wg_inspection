package com.raisecom.util;

import com.raisecom.bean.CardInfo;

/**
 * Created by ligy-008494 on 2019/1/23.
 */
public class SqlMappingCardUtil {
    public static void insertDispectCardInfo(CardInfo cardInfo){
        String firstStr="insert into card_statistics_info(";
        String midStr=")values (";
        String lastStr=")ON DUPLICATE KEY UPDATE ";
        String sql="";

        if(cardInfo!=null && cardInfo.getCardId()!=null){
            firstStr+="CARD_ID";
            midStr+="'"+cardInfo.getCardId()+"'";
            if(cardInfo.getCpu()!=null){
                firstStr+=",CPU";
                midStr+=",'"+cardInfo.getCpu();
                lastStr+="CPU = '" +cardInfo.getCpu();
            }
            if(cardInfo.getIrcnetoltId()!=null){
                firstStr+=",IRCNETOLTID";
                midStr+="','"+cardInfo.getIrcnetoltId();
                lastStr+="',IRCNETOLTID = '" +cardInfo.getIrcnetoltId();
            }
            if(cardInfo.getRam()!=null){
                firstStr+=",MEMORY";
                midStr+="','"+cardInfo.getRam();
                lastStr+="',MEMORY = '" +cardInfo.getRam();
            }
            if(cardInfo.getTemperature()!=null){
                firstStr+=",TEMPERATURE";
                midStr+="','"+cardInfo.getTemperature();
                lastStr+="',TEMPERATURE = '" +cardInfo.getTemperature();
            }
            if(cardInfo.getPower()!=null){
                firstStr+=",POWER";
                midStr+="','"+cardInfo.getPower();
                lastStr+="',POWER = '" +cardInfo.getPower();
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
