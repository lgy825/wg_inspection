package com.raisecom.controller.impl;

import com.raisecom.controller.DeviceTask;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.EPONCommonDBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ligy-008494 on 2019/1/14.
 */
public class ONUDeviceController implements DeviceTask {

    @Override
    public boolean processStatistics(List<String> addres) {
        //1.通过Ip获取所要巡检的OLT设备信息
        List<ObjService> rcnetnodes=getOLTInfoByIP(addres);

        return false;
    }

    public static List<ObjService> getOLTInfoByIP(List<String> addres){

        List<ObjService> list=new ArrayList<>();
        for(String addr:addres){
            String sql="SELECT rn.IRCNETNODEID, rn.iRCNETypeID, rn.READCOM, rn.WRITECOM, rn.IRCNETNODEID, rn.TIMEOUT,  rn.IPADDRESS" +
                    " FROM rcnetnode AS rn,rcnetype AS rt WHERE rn.IPADDRESS = '"+addr+"'" +
                    " AND rn.managed_mode = '1'" +
                    " AND rn.iRCNETypeID = rt.iRCNETypeID" +
                    " AND rt.NE_CATEGORY_ID = 1;";
            ObjService objService= EPONCommonDBUtil.selectDataByParam(sql);
            list.add(objService);
        }
        return list;
    }
}
