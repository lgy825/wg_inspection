package com.raisecom.controller.impl;

import com.raisecom.bean.Contact;
import com.raisecom.controller.DeviceTask;
import com.raisecom.controller.DispectMode;
import com.raisecom.db.InitSelfmDBPoolTask;
import com.raisecom.exportExcelDemo.Main;
import com.raisecom.nms.platform.client.LogManager;
import com.raisecom.nms.platform.client.Logger;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.EPONCommonDBUtil;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Created by ligy-008494 on 2018/11/20.
 */
public class BatDispectMode implements DispectMode {

    Logger log = LogManager.getLogger("BatDispectMode");
    File path = new File(System.getProperty("user.dir"));
    public static void main(String[] args) {
        DispectMode dispectMode=new BatDispectMode();
        boolean isCon=InitSelfmDBPoolTask.execute();
        if(isCon){
            dispectMode.processDispect(null);
        }
    }

    @Override
    public void processDispect(ObjService objService) {

//        String path = "../wg_inspection/src/config/deviceinfo.json";
//        JSONArray optionIps = null;
//        JSONArray optionSubnet = null;
        try {
//            String input = FileUtils.readFileToString(new File(path), "UTF-8");
//            JSONObject jsonObject = new JSONObject(input);
//
//            if (jsonObject != null) {
//                optionIps = jsonObject.getJSONArray("optionIp");
//                optionSubnet=jsonObject.getJSONArray("optionSubnet");
//            }
//            List<String> list=new ArrayList<>();
//            Iterator<Object> ips = optionIps.iterator();
//            Iterator<Object> subnets = optionSubnet.iterator();
//            while (subnets.hasNext()) {
//                JSONObject subnet = (JSONObject) subnets.next();
//                list.addAll(getIpAddreForSubnet(subnet.get("sign").toString()));
//            }
//            while (ips.hasNext()) {
//                JSONObject btn = (JSONObject) ips.next();
//                list.add(btn.get("ip").toString());
//            }
            //解析配置文件：
            // 读取XML文档，封装对象
            Contact contact = new Contact();
            SAXReader reader = new SAXReader();
            String strs=path+"/"+"config.xml";
            Document doc=reader.read(new File(strs));
            // 读取contact标签
            Iterator<Element> it = doc.getRootElement().elementIterator("contact");
            String str="";
            while (it.hasNext()) {
                Element elem = it.next();
                // 创建Contact
                str=elem.elementText("ipAddr");
                contact.setCount(elem.elementText("count"));
                contact.setInspectType(elem.elementText("inspectType"));
            }
            List<String> list=new ArrayList<>();
            String[] strings=str.split(",");
            for(String string:strings){
                list.add(string);
            }
            contact.setIpAddr(list);
            DeviceTask deviceTask=new OLTDeviceContrller();
            boolean isSuccessful=deviceTask.processStatistics(contact);
            if(isSuccessful){
                System.out.print("巡检成功");
            }else{
                System.out.print("巡检失败");
            }
        } catch (Exception e) {
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
