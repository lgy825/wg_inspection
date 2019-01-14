package com.raisecom.controller.impl;

import com.raisecom.concurrent.XPONThreadPool;
import com.raisecom.controller.DeviceTask;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.securitymgt.client.util.SecurityManagerCenter;
import com.raisecom.nms.securitymgt.client.util.SecurityUtils;
import com.raisecom.util.EPONCommonDBUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by ligy-008494 on 2019/1/14.
 */
public class ONUDeviceController implements DeviceTask {

    @Override
    public boolean processStatistics(List<String> addres) {
        //1.通过Ip获取所要巡检的OLT设备信息
        List<ObjService> rcnetnodes = getOLTInfoByIP(addres);
        XPONThreadPool xponPool = XPONThreadPool.getNewPool("DeviceStatistic", 5);
        Map<String, Future<Boolean>> results = new HashMap<String, Future<Boolean>>();
        for (ObjService olt : rcnetnodes) {

        }


        return false;
    }

    public static List<ObjService> getOLTInfoByIP(List<String> addres) {

        List<ObjService> list = new ArrayList<>();
        for (String addr : addres) {
            String sql = "SELECT rn.IRCNETNODEID, rn.iRCNETypeID, rn.READCOM, rn.WRITECOM, rn.IRCNETNODEID, rn.TIMEOUT,  rn.IPADDRESS" +
                    " FROM rcnetnode AS rn,rcnetype AS rt WHERE rn.IPADDRESS = '" + addr + "'" +
                    " AND rn.managed_mode = '1'" +
                    " AND rn.iRCNETypeID = rt.iRCNETypeID" +
                    " AND rt.NE_CATEGORY_ID = 1;";
            ObjService objService = EPONCommonDBUtil.selectDataByParam(sql);
            list.add(objService);
        }
        return list;
    }

    public List<String> getONUInstanceFromDBByOltNeID(String neID){
        List<String> onuInstance = new ArrayList<>();
        List<ObjService> vobj = new ArrayList<>();
        String condition = SecurityManagerCenter.getInstance().getDomainAllNEIDString();
        try {
            if (condition.equals(SecurityUtils.ALL))
            {
                vobj = EPONCommonDBUtil.getInstance().getONUInstanceFromDBByOltNeId(neID);
            } else if (condition.equals(SecurityUtils.NONE))
            {
                return null;
            } else
            {
                vobj = EPONCommonDBUtil.getInstance().getONUInstanceFromDBByOltNeIdAndAuthority(neID,condition);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.error("DeviceInspectTDataTreeBuilder getONUInstanceFromDBByOltNeID for neid " + neID+" error "+e);
        }
        for(int i = 0; i < vobj.size(); i++){
            onuInstance.add(vobj.get(i).getStringValue("INDEX_IN_MIB"));
        }
        return onuInstance;
    }
}
