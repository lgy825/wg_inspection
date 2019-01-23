package com.raisecom.concurrent;

import com.raisecom.bean.ONUInfo;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.*;

import javax.print.DocFlavor;
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
        ONUInfo onuInfo = new ONUInfo();
        try {
            String oltId = objService.getStringValue("IRCNETNODEID");
            String softVer = objService.getStringValue("SOFTWARE_VER");
            String ver = ParseAdapterUtil.getVersBySoftVer(softVer);
            String configFile = objService.getStringValue("ConfigFile");
            //取出OLTSNMP操作参数
            ObjService options = SnmpParamsHelper.getOption(oltId);
            options.setValue("ConfigFile",configFile);
            options.setValue("ver",ver);
            //取出在线的ONU的信息
            List<ObjService> objServices = EPONCommonDBUtil.getInstance().getONUInstanceOnlineFromDBByOltNeId(oltId);
            for(int i = 0;i < objServices.size();i ++){
                String instance = objServices.get(i).getStringValue("INDEX_IN_MIB");
                String IRCNETNODEID=objServices.get(i).getStringValue("IRCNETNODEID");
                String iRCNETypeID = objServices.get(i).getStringValue("iRCNETypeID");
                String distance=objServices.get(i).getStringValue("DISTANCE");

                onuInfo.setIrcnetnodeid(Integer.parseInt(IRCNETNODEID));
                onuInfo.setDistance(distance);
                //1.ONU的在线状态
                String status = SnmpOperationForONU.getONUStatusForParam(instance,iRCNETypeID,options);
                if(status == null || "NULL".equals(status)){
                    onuInfo.setStatus("--");
                    onuInfo.setLastDownCause("--");
                    onuInfo.setDistance("--");
                    onuInfo.setReceivedPower("--");
                    onuInfo.setOnuHangMacCount("--");
                    onuInfo.setLoopPort("--");
                    onuInfo.setPortStatus("--");
                    SqlMappingONUUtil.insertDispectONUInfo(onuInfo);
                    continue;
                }
                if("1".equals(status)){
                    onuInfo.setStatus("online");
                }else if("2".equals(status)){
                    onuInfo.setStatus("pending");
                }else{
                    onuInfo.setStatus("offline");
                }

                //2.上一次离线原因
                String lastDownCause = SnmpOperationForONU.getONULastDownCause(instance,iRCNETypeID,options);
                onuInfo.setLastDownCause(processResult(lastDownCause));

                //4.ONU接收光功率
                String receivedPower = SnmpOperationForONU.getONUReceivedPower(instance,iRCNETypeID,options);
                if(receivedPower == null || "NULL".equals(receivedPower)){
                    onuInfo.setReceivedPower("--");
                }else {
                    onuInfo.setReceivedPower(processResult(receivedPower));
                }
                //5.ONU下挂 mac地址数
                 String onuHangMacCount = SnmpOperationForONU.getONUHangMacConut(instance,iRCNETypeID,options);
                if(onuHangMacCount == null || "NULL".equals(onuHangMacCount)){
                    onuInfo.setOnuHangMacCount("--");
                }else {
                    onuInfo.setOnuHangMacCount(processResult(onuHangMacCount));
                }
                //6.环路端口
                String loopPort = SnmpOperationForONU.getONULoopPort(instance,iRCNETypeID,options);
                onuInfo.setLoopPort(processResult(loopPort));
                //7.端口状态
                String portStatus = SnmpOperationForONU.getONUPortStatus(instance,iRCNETypeID,options);
                onuInfo.setPortStatus(processResult(portStatus));
                //入库
                SqlMappingONUUtil.insertDispectONUInfo(onuInfo);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public  String processResult(String s) {
        if(s == null || "".equals(s) || "null".equalsIgnoreCase(s)){
            return "--";
        }else{
            return s;
        }
    }

}
