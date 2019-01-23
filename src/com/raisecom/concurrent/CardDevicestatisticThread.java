package com.raisecom.concurrent;

import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.EPONCommonDBUtil;
import com.raisecom.util.SnmpOperationUtil;
import com.raisecom.util.SnmpParamsHelper;

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
        ObjService options = SnmpParamsHelper.getOption(oltId);
        //获取板卡相关信息
        List<ObjService> objServices = EPONCommonDBUtil.getInstance().getCardInfoFromDBByOltNeId(oltId);
        for(ObjService objService:objServices){
            String cardId=objService.getStringValue("cardId");
            String inMib=objService.getStringValue("inMib");

            //电压
            String voltage = SnmpOperationUtil.getVoltage(options);
            //cpu的使用率

            //内存的使用率

            //温度


        }
        return null;
    }
}
