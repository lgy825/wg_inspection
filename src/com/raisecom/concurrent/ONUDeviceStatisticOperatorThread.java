package com.raisecom.concurrent;

import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.*;

import java.security.spec.ECParameterSpec;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by ligy-008494 on 2019/1/16.
 */
public class ONUDeviceStatisticOperatorThread implements Callable<Boolean> {

    private ObjService objService;
    public ONUDeviceStatisticOperatorThread(ObjService objService){
        this.objService=objService;
    }
    @Override
    public Boolean call() throws Exception {

        try {
            String oltId=objService.getStringValue("IRCNETNODEID");
            String softVer=objService.getStringValue("SOFTWARE_VER");
            String ver= ParseAdapterUtil.getVersBySoftVer(softVer);
            String configFile=objService.getStringValue("ConfigFile");
            //取出OLTSNMP操作参数
            ObjService options = SnmpParamsHelper.getOption(oltId);
            options.setValue("ConfigFile",configFile);
            //取出在线的ONU的信息


            List<ObjService> objServices = EPONCommonDBUtil.getInstance().getONUInstanceOnlineFromDBByOltNeId(oltId);
            for(int i=0;i<objServices.size();i++){
                String instance=objServices.get(i).getStringValue("INDEX_IN_MIB");
                Boolean aBoolean= IfIndexHelperV2.isNewOnuIndex(instance);

                if(ver.startsWith("2.") || ver.startsWith("3.")){
                    instance= IfIndexHelperV2.OnuIndexOld2New(instance);
                }

                //ONU的在线状态
                String status= SnmpOperationForONU.getONUStatusForParam(instance,options);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
