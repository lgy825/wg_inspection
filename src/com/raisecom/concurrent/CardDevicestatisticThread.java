package com.raisecom.concurrent;

import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.EPONCommonDBUtil;

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
        String oltId = objService.getStringValue("IRCNETNODEID");

        //获取板卡相关信息
        List<ObjService> objServices = EPONCommonDBUtil.getInstance().getCardInfoFromDBByOltNeId(oltId);
        for(ObjService objService:objServices){
            String cardId=objService.getStringValue("cardId");
            String inMib=objService.getStringValue("inMib");

            //

        }
        return null;
    }
}