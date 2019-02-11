package com.raisecom.exportExcelDemo;


import com.raisecom.bean.CardInfo;
import com.raisecom.bean.OLTInfo;
import com.raisecom.bean.ONUInfo;
import com.raisecom.common.logging.LogFactory;
import com.raisecom.common.logging.Logger;
import com.raisecom.db.InitSelfmDBPoolTask;
import com.raisecom.nms.platform.client.ResourceManager;
import com.raisecom.util.EPONConstants;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.*;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Created by liujs-008398 on 2018-12-28.
 */
public class Main {
    private static final Logger logger = LogFactory.getLogger("selfm");
    private static ResourceBundle bundle = EPONConstants.EPON_RB;
    public static void main(String[] args) throws Exception {
        boolean isCon= InitSelfmDBPoolTask.execute();
        if(isCon){
            //FromDbToExcel("2108");
            FromDBToONUExcel("2108");
            //FromDBToCardExcel("2108");
            logger.log(300,"数据库初始化成功");
        }else{
            logger.log(300,"数据库初始化失败");
        }
    }

    //板卡导出Excel
    public static void FromDBToCardExcel(String str){
        try {
            List<CardInfo> list = CardInfoService.getAllByDb(str);
            // 创建可写入的Excel工作簿
            WritableWorkbook wwb = null;

            //文件名为时间精确到秒
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            String dataStr = sdf.format(new Date());

            String fileName = "D://" + dataStr + ".xls";
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            //以fileName为文件名来创建一个Workbook
            wwb = Workbook.createWorkbook(file);
            //生成名为“第一页”的工作表，参数0表示这是第一页

            WritableSheet ws = wwb.createSheet("Test Sheet 1", 0);
            //设置列宽默认宽度
            ws.getSettings().setDefaultColumnWidth(15);
            //设置指定列的宽度

            ws.setColumnView(9,40);
            ws.setColumnView(10,40);
            //设置字体 TIMES是字体大小，9，BOLD是判断是否为斜体,
            WritableFont fontTitle = new WritableFont(WritableFont.TIMES, 9, WritableFont.NO_BOLD);
            //定义格式
            WritableCellFormat formatTitle = new WritableCellFormat(fontTitle);
            //表头设置背景为灰色
            formatTitle.setBackground(Colour.GRAY_25);
            //自动换行
            formatTitle.setWrap(true);
            //formatTitle.setAlignment(Alignment.CENTRE); //设置把水平对齐方式指定为居中
            formatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);//把垂直对齐方式指定为居中
            //查询数据库中所有的数据

            //插入表头。行号，默认从0开始，列号从0开始，
            Label friendlyName = new Label(0, 0, ResourceManager.getString(bundle,"FRIENDLY_NAME"),formatTitle);
            Label ipAddr = new Label(1, 0, ResourceManager.getString(bundle,"Address"),formatTitle);
            Label ircnetype = new Label(2, 0, ResourceManager.getString(bundle,"type_Id"),formatTitle);
            Label cardType = new Label(3, 0, ResourceManager.getString(bundle,"card_type"),formatTitle);
            Label slotId = new Label(4, 0, ResourceManager.getString(bundle,"slot_id"),formatTitle);
            Label status = new Label(5, 0, ResourceManager.getString(bundle,"card_status"),formatTitle);

            Label cpu = new Label(6, 0, ResourceManager.getString(bundle,"CPU"),formatTitle);
            Label ram = new Label(7, 0, ResourceManager.getString(bundle,"RAM"),formatTitle);
            Label temperature = new Label(8, 0, ResourceManager.getString(bundle,"Temperature"),formatTitle);
            Label power = new Label(9, 0, ResourceManager.getString(bundle,"Voltage"),formatTitle);
            Label ver = new Label(10, 0, ResourceManager.getString(bundle,"Ver"),formatTitle);




            //将Label 添加到工作表
            ws.addCell(friendlyName);
            ws.addCell(ipAddr);
            ws.addCell(ircnetype);
            ws.addCell(cardType);
            ws.addCell(slotId);
            ws.addCell(status);
            ws.addCell(cpu);
            ws.addCell(ram);
            ws.addCell(temperature);
            ws.addCell(power);
            ws.addCell(ver);



            //将数据库数据加入工作表

            WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle);

            formatTitle1.setWrap(true);
            //formatTitle1.setAlignment(Alignment.CENTRE); //设置把水平对齐方式指定为居中
            formatTitle1.setVerticalAlignment(VerticalAlignment.CENTRE);//把垂直对齐方式指定为居中
            for (int i = 0; i < list.size(); i++) {

                Label friendlyName_i = new Label(0, i+1, list.get(i).getFriendlyName(),formatTitle1);
                Label ipAddr_i = new Label(1, i+1, list.get(i).getIpAddr(),formatTitle1);
                Label ircnetype_i = new Label(2, i+1, list.get(i).getIrcnetype(),formatTitle1);
                Label cardType_i = new Label(3, i+1,list.get(i).getCardType(),formatTitle1);
                Label slotId_i = new Label(4, i+1,list.get(i).getSlotId(),formatTitle1);
                Label status_i = new Label(5, i+1,list.get(i).getStatus(),formatTitle1);
                Label cpu_i = new Label(6, i+1,list.get(i).getCpu(),formatTitle1);
                Label ram_i = new Label(7, i+1,list.get(i).getRam(),formatTitle1);
                Label temperature_i = new Label(8, i+1,list.get(i).getTemperature(),formatTitle1);
                Label power_i = new Label(9, i+1,list.get(i).getPower(),formatTitle1);
                Label ver_i = new Label(10, i+1,list.get(i).getVer(),formatTitle1);


                ws.addCell(friendlyName_i);
                ws.addCell(ipAddr_i);
                ws.addCell(ircnetype_i);
                ws.addCell(cardType_i);
                ws.addCell(slotId_i);
                ws.addCell(status_i);
                ws.addCell(cpu_i);
                ws.addCell(ram_i);
                ws.addCell(temperature_i);
                ws.addCell(power_i);
                ws.addCell(ver_i);


            }


            //写进文档
            wwb.write();
            // 关闭Excel工作簿对象
            System.out.println("数据导出成功!");
            wwb.close();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    //ONU导出Excel
    public static void FromDBToONUExcel(String str) throws Exception {
        try {
            List<ONUInfo> list = ONUInfoService.getAllByDb(str);
            // 创建可写入的Excel工作簿
            WritableWorkbook wwb = null;
            //数据总数
            int dataCount = list.size();
            //每页显示数据数量
            int pagesize = 31;
            //分页数
            int pageNum ;
            //计算分页总数
            if (dataCount % pagesize == 0) {
                pageNum = dataCount / pagesize;
            } else {
                pageNum = dataCount / pagesize + 1;
            }

            //文件名为时间精确到秒
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            String dataStr = sdf.format(new Date());

            String fileName = "D://" + dataStr + ".xls";
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            //以fileName为文件名来创建一个Workbook
            wwb = Workbook.createWorkbook(file);
            //生成名为“第一页”的工作表，参数0表示这是第一页

                for(int i = 0;i < pageNum ; i++){
                    WritableSheet ws = wwb.createSheet("Test Sheet "+ i, i);
                    //设置列宽默认宽度
                    ws.getSettings().setDefaultColumnWidth(15);
                    //设置指定列的宽度
                    ws.setColumnView(2,25);
                    ws.setColumnView(12,40);

                    //设置字体 TIMES是字体大小，9，BOLD是判断是否为斜体,
                    WritableFont fontTitle = new WritableFont(WritableFont.TIMES, 9, WritableFont.NO_BOLD);
                    //定义格式
                    WritableCellFormat formatTitle = new WritableCellFormat(fontTitle);
                    //表头设置背景为灰色
                    formatTitle.setBackground(Colour.GRAY_25);
                    //自动换行
                    formatTitle.setWrap(true);
                    //formatTitle.setAlignment(Alignment.CENTRE); //设置把水平对齐方式指定为居中
                    formatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);//把垂直对齐方式指定为居中
                    //查询数据库中所有的数据

                    //插入表头。行号，默认从0开始，列号从0开始，
                    Label ircnetnode = new Label(0, 0, ResourceManager.getString(bundle,"Friendly_Name"),formatTitle);
                    Label ipaddress = new Label(1, 0, ResourceManager.getString(bundle,"Address"),formatTitle);
                    Label friendly_name = new Label(2, 0, ResourceManager.getString(bundle,"FRIENDLY_NAME"),formatTitle);
                    Label subnetype = new Label(3, 0, ResourceManager.getString(bundle,"Model"),formatTitle);
                    Label ver = new Label(4, 0, ResourceManager.getString(bundle,"Ver"),formatTitle);
                    Label macaddress = new Label(5, 0, ResourceManager.getString(bundle,"MACAddress"),formatTitle);

                    Label status = new Label(6, 0, ResourceManager.getString(bundle,"status"),formatTitle);
                    Label last_down_cause = new Label(7, 0, ResourceManager.getString(bundle,"last_down_cause"),formatTitle);
                    Label distance = new Label(8, 0, ResourceManager.getString(bundle,"distance"),formatTitle);
                    Label received_power = new Label(9, 0, ResourceManager.getString(bundle,"received_power"),formatTitle);
                    Label hang_mac_count = new Label(10, 0, ResourceManager.getString(bundle,"hang_mac_count"),formatTitle);
                    Label loop_port = new Label(11, 0, ResourceManager.getString(bundle,"loop_port"),formatTitle);
                    Label port_status = new Label(12, 0, ResourceManager.getString(bundle,"port_status"),formatTitle);



                    //将Label 添加到工作表
                    ws.addCell(ircnetnode);
                    ws.addCell(ipaddress);
                    ws.addCell(friendly_name);
                    ws.addCell(subnetype);
                    ws.addCell(ver);
                    ws.addCell(macaddress);
                    ws.addCell(status);
                    ws.addCell(last_down_cause);
                    ws.addCell(distance);
                    ws.addCell(received_power);
                    ws.addCell(hang_mac_count);
                    ws.addCell(loop_port);
                    ws.addCell(port_status);


                    //将数据库数据加入工作表

                    WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle);

                    formatTitle1.setWrap(true);
                    //formatTitle1.setAlignment(Alignment.CENTRE); //设置把水平对齐方式指定为居中
                    formatTitle1.setVerticalAlignment(VerticalAlignment.CENTRE);//把垂直对齐方式指定为居中
                    int temp = pagesize *(i + 1) ;
                    //最后一页加判断
                    if(i == pageNum - 1){
                        temp = dataCount ;
                    }
                    for ( int j = pagesize * i , k = 0;j < temp ;j ++ ,k++) {

                            Label ircnetnode_i = new Label(0, k + 1, list.get(j).getIrcnetnodeid().toString(), formatTitle1);
                            Label ipaddress_i = new Label(1, k + 1, list.get(j).getIpaddress(), formatTitle1);
                            Label friendly_name_i = new Label(2, k + 1, list.get(j).getFriendlyName(), formatTitle1);
                            Label subnetype_i = new Label(3, k + 1, list.get(j).getSubnetype(), formatTitle1);
                            Label ver_i = new Label(4, k + 1, list.get(j).getSoftware(), formatTitle1);
                            Label macaddress_i = new Label(5, k + 1, list.get(j).getMacaddress(), formatTitle1);
                            Label status_i = new Label(6, k + 1, list.get(j).getStatus(), formatTitle1);
                            Label last_down_cause_i = new Label(7, k + 1, list.get(j).getLastDownCause(), formatTitle1);
                            Label distance_i = new Label(8, k + 1, list.get(j).getDistance(), formatTitle1);
                            Label received_power_i = new Label(9, k + 1, list.get(j).getReceivedPower(), formatTitle1);
                            Label hang_mac_count_i = new Label(10, k + 1, list.get(j).getOnuHangMacCount(), formatTitle1);
                            Label loop_port_i = new Label(11, k + 1, list.get(j).getLoopPort(), formatTitle1);
                            Label port_status_i = new Label(12, k + 1, list.get(j).getPortStatus(), formatTitle1);

                            ws.addCell(ircnetnode_i);
                            ws.addCell(ipaddress_i);
                            ws.addCell(friendly_name_i);
                            ws.addCell(subnetype_i);
                            ws.addCell(ver_i);
                            ws.addCell(macaddress_i);
                            ws.addCell(status_i);
                            ws.addCell(last_down_cause_i);
                            ws.addCell(distance_i);
                            ws.addCell(received_power_i);
                            ws.addCell(hang_mac_count_i);
                            ws.addCell(loop_port_i);
                            ws.addCell(port_status_i);

                    }
            }


            //写进文档
            wwb.write();
            // 关闭Excel工作簿对象
            System.out.println("数据导出成功!");
            wwb.close();
        }catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }
    //从数据库导出OLT Excel
    public static void FromDbToExcel(String str) {
//        String sql = "select * from OLT_STATISTICS_INFO";
//        ObjService result = EPONCommonDBUtil.executeQuery(sql);


        try {
            List<OLTInfo> list = OLTInfoService.getAllByDb(str);
            // 创建可写入的Excel工作簿
            WritableWorkbook wwb = null;

            //文件名为时间精确到秒
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
            String dataStr=sdf.format(new Date());

            String fileName = "D://"+dataStr+".xls";
            File file=new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            //以fileName为文件名来创建一个Workbook
            wwb = Workbook.createWorkbook(file);

            //生成名为“第一页”的工作表，参数0表示这是第一页

            WritableSheet ws = wwb.createSheet("Test Sheet 1", 0);
            //设置列宽默认宽度
            ws.getSettings().setDefaultColumnWidth(15);
            //设置指定列的宽度
            ws.setColumnView(9,35);
            ws.setColumnView(13,35);
            ws.setColumnView(14,40);
            ws.setColumnView(15,65);
            //设置字体 TIMES是字体大小，9，BOLD是判断是否为斜体,
            WritableFont fontTitle = new WritableFont(WritableFont.TIMES, 9, WritableFont.NO_BOLD);
            //定义格式
            WritableCellFormat formatTitle = new WritableCellFormat(fontTitle);
            //表头设置背景为灰色
            formatTitle.setBackground(Colour.GRAY_25);
            //自动换行
            formatTitle.setWrap(true);
            //formatTitle.setAlignment(Alignment.CENTRE); //设置把水平对齐方式指定为居中
            formatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);//把垂直对齐方式指定为居中
            //查询数据库中所有的数据

            //插入表头。行号，默认从0开始，列号从0开始，
            Label friendlyName = new Label(0, 0, ResourceManager.getString(bundle,"Friendly_Name"),formatTitle);
            Label typeId = new Label(1, 0, ResourceManager.getString(bundle,"type_Id"),formatTitle);
            Label address = new Label(2, 0, ResourceManager.getString(bundle,"Address"),formatTitle);
            Label smc = new Label(3, 0, ResourceManager.getString(bundle,"SMC"),formatTitle);
            Label ram = new Label(4, 0, ResourceManager.getString(bundle,"RAM"),formatTitle);
            Label cpu = new Label(5, 0, ResourceManager.getString(bundle,"CPU"),formatTitle);
            Label temperature = new Label(6, 0, ResourceManager.getString(bundle,"Temperature"),formatTitle);
            Label power = new Label(7, 0, ResourceManager.getString(bundle,"Power"),formatTitle);
            Label fan = new Label(8, 0, ResourceManager.getString(bundle,"Fan"),formatTitle);
            Label ver = new Label(9, 0, ResourceManager.getString(bundle,"Ver"),formatTitle);
            Label businessCardAccount = new Label(10, 0, ResourceManager.getString(bundle,"BusinessCardAccount"),formatTitle);
            Label vlan_optimize = new Label(11, 0, ResourceManager.getString(bundle,"vlan_optimize"),formatTitle);
            //Label sys_uptime = new Label(12, 0, ResourceManager.getString(bundle,"sys_uptime"),formatTitle);
            Label switched_count = new Label(12, 0, ResourceManager.getString(bundle,"switched_count"),formatTitle);
            //Label reboot_count = new Label(14, 0, ResourceManager.getString(bundle,"reboot_count"),formatTitle);
            Label olt_power = new Label(13, 0, ResourceManager.getString(bundle,"olt_power"),formatTitle);
            Label port_is_solate = new Label(14, 0, ResourceManager.getString(bundle,"port_is_solate"),formatTitle);
            Label onu_count_info = new Label(15, 0, ResourceManager.getString(bundle,"onu_count_info"),formatTitle);


            //将Label 添加到工作表
            ws.addCell(friendlyName);
            ws.addCell(typeId);
            ws.addCell(address);
            ws.addCell(smc);
            ws.addCell(ram);
            ws.addCell(cpu);
            ws.addCell(temperature);
            ws.addCell(power);
            ws.addCell(fan);
            ws.addCell(ver);
            ws.addCell(businessCardAccount);
            ws.addCell(vlan_optimize);
            //ws.addCell(sys_uptime);
            ws.addCell(switched_count);
            //ws.addCell(reboot_count);
            ws.addCell(olt_power);
            ws.addCell(port_is_solate);
            ws.addCell(onu_count_info);

            //将数据库数据加入工作表

            WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle);

            formatTitle1.setWrap(true);
            //formatTitle1.setAlignment(Alignment.CENTRE); //设置把水平对齐方式指定为居中
            formatTitle1.setVerticalAlignment(VerticalAlignment.CENTRE);//把垂直对齐方式指定为居中
            for (int i = 0; i < list.size(); i++) {

                Label friendlyName_i = new Label(0, i+1, list.get(i).getFriendly_name(),formatTitle1);
                Label typeId_i = new Label(1, i+1, list.get(i).getIrcnetypeid(),formatTitle1);
                Label address_i = new Label(2, i+1, list.get(i).getIpaddress(),formatTitle1);
                Label smc_i = new Label(3, i+1,list.get(i).getSmc(),formatTitle1);
                Label ram_i = new Label(4, i+1,list.get(i).getRam(),formatTitle1);
                Label cpu_i = new Label(5, i+1,list.get(i).getCpu(),formatTitle1);
                Label temperature_i = new Label(6, i+1,list.get(i).getTemperature(),formatTitle1);
                Label power_i = new Label(7, i+1,list.get(i).getPower(),formatTitle1);
                Label fan_i = new Label(8, i+1,list.get(i).getFan(),formatTitle1);
                Label software_ver_i = new Label(9, i+1,list.get(i).getSoftware_ver(),formatTitle1);
                Label bussiness_card_amount_i = new Label(10, i+1,list.get(i).getBussiness_card_amount(),formatTitle1);
                Label vlan_optimize_i = new Label(11, i+1,list.get(i).getVlan_optimize(),formatTitle1);
                //Label sys_uptime_i = new Label(12, i+1,list.get(i).getSys_uptime(),formatTitle1);
                Label switched_count_i = new Label(12, i+1,list.get(i).getSwitched_count().toString(),formatTitle1);
                //Label reboot_count_i = new Label(14, i+1,list.get(i).getReboot_count().toString(),formatTitle1);
                Label olt_power_i = new Label(13, i+1,list.get(i).getOlt_power(),formatTitle1);
                Label port_is_solate_i = new Label(14, i+1,list.get(i).getPort_is_solate(),formatTitle1);
                Label onu_count_info_i = new Label(15, i+1,list.get(i).getOnu_count_info(),formatTitle1);
                ws.addCell(friendlyName_i);
                ws.addCell(typeId_i);
                ws.addCell(address_i);
                ws.addCell(smc_i);
                ws.addCell(ram_i);
                ws.addCell(cpu_i);
                ws.addCell(temperature_i);
                ws.addCell(power_i);
                ws.addCell(fan_i);
                ws.addCell(software_ver_i);
                ws.addCell(bussiness_card_amount_i);
                ws.addCell(vlan_optimize_i);
                //ws.addCell(sys_uptime_i);
                ws.addCell(switched_count_i);
                //ws.addCell(reboot_count_i);
                ws.addCell(olt_power_i);
                ws.addCell(port_is_solate_i);
                ws.addCell(onu_count_info_i);

            }


            //写进文档
            wwb.write();
            // 关闭Excel工作簿对象
            System.out.println("数据导出成功!");
            wwb.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
