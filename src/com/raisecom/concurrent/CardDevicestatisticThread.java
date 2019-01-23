package com.raisecom.concurrent;

import com.raisecom.bean.CardInfo;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.*;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by ligy-008494 on 2019/1/22.
 */
public class CardDevicestatisticThread implements Callable<Boolean> {
    private ObjService objService;

    public CardDevicestatisticThread(ObjService objService){
        this.objService=objService;
    }
    @Override
    public Boolean call() throws Exception {
        CardInfo cardInfo=new CardInfo();
        String oltId = objService.getStringValue("IRCNETNODEID");
        String configFile=objService.getStringValue("ConfigFile");
        ObjService options = SnmpParamsHelper.getOption(oltId);
        options.setValue("ConfigFile",configFile);
        //获取板卡相关信息
        List<ObjService> objServices = EPONCommonDBUtil.getInstance().getCardInfoFromDBByOltNeId(oltId);
        for(ObjService objService:objServices){
            String cardId=objService.getStringValue("CARD_ID");
            String inMib=objService.getStringValue("INDEX_IN_MIB");

            cardInfo.setCardId(cardId);
            //电压
            String voltage = SnmpOperationUtil.getVoltage(inMib,options);
            cardInfo.setPower(processResult(voltage));
            //cpu的使用率
            String cpu= SnmpOperationForCard.getCardCpu(options,inMib);
            cardInfo.setCpu(processResult(cpu));
            //内存的使用率

            String ram=SnmpOperationForCard.getRamUsage(options,inMib);
            cardInfo.setRam(processResult(ram));
            //温度
            String cardTemperature = SnmpOperationUtil.getTemperature4OLT(options);
            cardInfo.setTemperature(cardTemperature);
            //入库
            SqlMappingCardUtil.insertDispectCardInfo(cardInfo);

        }
        return true;
    }
    public  String processResult(String s) {
        if(s==null||"".equals(s)||"null".equalsIgnoreCase(s)){
            return "--";
        }else{
            return s;
        }
    }
}
