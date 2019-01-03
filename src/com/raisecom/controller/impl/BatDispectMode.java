package com.raisecom.controller.impl;

import com.raisecom.common.logging.LogFactory;
import com.raisecom.common.logging.Logger;
import com.raisecom.controller.DeviceTask;
import com.raisecom.controller.DispectMode;
import com.raisecom.db.InitSelfmDBPoolTask;
import com.raisecom.db.JdbcUtils_DBCP;
import com.raisecom.exportExcelDemo.Main;
import com.raisecom.nms.platform.client.ResourceManager;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.util.EPONCommonDBUtil;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;


/**
 * Created by ligy-008494 on 2018/11/20.
 */
public class BatDispectMode implements DispectMode {



    private static final Logger logger = LogFactory.getLogger("selfm");
    public static void main(String[] args) {
        DispectMode dispectMode=new BatDispectMode();
        boolean isCon=InitSelfmDBPoolTask.execute();
        if(isCon){
            dispectMode.processDispect(null);
            logger.log(300,"数据库初始化成功");
        }else{
            logger.log(300,"数据库初始化失败");
        }

    }

    @Override
    public void processDispect(ObjService objService) {

        String path = "../wg_inspection/src/config/deviceinfo.json";
        JSONArray optionIps = null;
        JSONArray optionSubnet = null;
        try {
            String input = FileUtils.readFileToString(new File(path), "UTF-8");
            JSONObject jsonObject = new JSONObject(input);

            if (jsonObject != null) {
                optionIps = jsonObject.getJSONArray("optionIp");
                optionSubnet=jsonObject.getJSONArray("optionSubnet");
            }
            List<String> list=new ArrayList<>();
            Iterator<Object> ips = optionIps.iterator();
            Iterator<Object> subnets = optionSubnet.iterator();
            while (subnets.hasNext()) {
                JSONObject subnet = (JSONObject) subnets.next();
                list.addAll(getIpAddreForSubnet(subnet.get("sign").toString()));
            }
            while (ips.hasNext()) {
                JSONObject btn = (JSONObject) ips.next();
                list.add(btn.get("ip").toString());
            }
            DeviceTask deviceTask=new OLTDeviceContrller();
            boolean isSuccessful=deviceTask.processStatistics(list);
            if(isSuccessful){
                Main.FromDbToExcel();
                System.out.print("导出成功");
            }else{
                System.out.print("巡检失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  List<String> getIpAddreForSubnet(String subnet){
        String sql="SELECT IPADDRESS FROM rcnetnode,rcnetype,topo_mainview_symbol " +
                "topo WHERE rcnetype.NE_CATEGORY_ID IN ('1', '5')" +
                "AND rcnetnode.ircnetypeid = rcnetype.ircnetypeid " +
                "AND rcnetnode.ircnetnodeid = topo.ne_id" +
                " AND topo.topo_type_id LIKE '11_%'"+
                "AND topo.map_HIERARCHY LIKE '%,"+subnet+",%';";

        List<String> list= EPONCommonDBUtil.selectDataBy(sql);
        return list;
    }


}
