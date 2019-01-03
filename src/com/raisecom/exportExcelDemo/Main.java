package com.raisecom.exportExcelDemo;

import com.raisecom.bean.OLTInfo;
import com.raisecom.common.logging.LogFactory;
import com.raisecom.common.logging.Logger;
import com.raisecom.db.InitSelfmDBPoolTask;
import jxl.CellView;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.*;


import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Vector;

/**
 * Created by liujs-008398 on 2018-12-28.
 */
public class Main {
    private static final Logger logger = LogFactory.getLogger("selfm");
    public static void main(String[] args){
        boolean isCon= InitSelfmDBPoolTask.execute();
        if(isCon){
            FromDbToExcel();
            logger.log(300,"数据库初始化成功");
        }else{
            logger.log(300,"数据库初始化失败");
        }
    }
    public static void FromDbToExcel() {
//        String sql = "select * from OLT_STATISTICS_INFO";
//        ObjService result = EPONCommonDBUtil.executeQuery(sql);


        try {
            List<OLTInfo> list = OLTInfoService.getAllByDb();
            WritableWorkbook wwb = null;

            // 创建可写入的Excel工作簿
            String fileName = "D://oltInfo.xls";
            File file=new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            //以fileName为文件名来创建一个Workbook
            wwb = Workbook.createWorkbook(file);

            // 创建工作表
            WritableFont fontTitle = new WritableFont(WritableFont.TIMES, 8, WritableFont.NO_BOLD);
//            fontTitle.setColour(jxl.format.Colour.RED);
            WritableCellFormat formatTitle = new WritableCellFormat(fontTitle);
            formatTitle.setWrap(true);
            formatTitle.setAlignment(Alignment.CENTRE); //设置垂直对齐

            CellView navCellView = new CellView();
            //navCellView.setAutosize(true); //设置自动大小
            navCellView.setSize(3500);
            WritableSheet ws = wwb.createSheet("Test Shee 1", 0);

            //查询数据库中所有的数据

            //要插入到的Excel表格的行号，默认从0开始
            Label friendlyName = new Label(0, 0, "局点",formatTitle);//表示第
            Label typeId = new Label(1, 0, "OLT设备类型",formatTitle);
            Label address = new Label(2, 0, "OLT设备IP地址",formatTitle);
            Label smc = new Label(3, 0, "SMC",formatTitle);
            Label ram = new Label(4, 0, "内存(%)",formatTitle);
            Label cpu = new Label(5, 0, "CPU(%)",formatTitle);
            Label temperature = new Label(6, 0, "温度(℃)",formatTitle);
            Label power = new Label(7, 0, "电源",formatTitle);
            Label fan = new Label(8, 0, "风扇",formatTitle);
            Label ver = new Label(9, 0, "OLT版本信息",formatTitle);
            Label businessCardAccount = new Label(10, 0, "OLT业务板卡类型和数量统计",formatTitle);
            Label vlan_optimize = new Label(11, 0, "VLAN广播域是否能优化缩小",formatTitle);
            Label sys_uptime = new Label(12, 0, "系统运行时间",formatTitle);
            Label switched_count = new Label(13, 0, "主备倒换次数",formatTitle);
            Label reboot_count = new Label(14, 0, "主控异常重启次数",formatTitle);
            Label olt_power = new Label(15, 0, "主控电压",formatTitle);
            Label port_is_solate = new Label(16, 0, "PON口隔离",formatTitle);
            Label onu_count_info = new Label(17, 0, "ONU数量统计",formatTitle);
            //ws.setColumnView(0,500,formatTitle); //设置col显示样式
            //ws.setRowView(0, 1600, false); //设置行高


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
            ws.addCell(sys_uptime);
            ws.addCell(switched_count);
            ws.addCell(reboot_count);
            ws.addCell(olt_power);
            ws.addCell(port_is_solate);
            ws.addCell(onu_count_info);

            navCellView.setFormat(formatTitle);
            for(int i = 0; i < 18 ;i++){
                ws.setColumnView(i,navCellView);
            }


            for (int i = 0; i < list.size(); i++) {

                Label friendlyName_i = new Label(0, i+1, list.get(i).getFriendly_name()+"",formatTitle);
                Label typeId_i = new Label(1, i+1, list.get(i).getIrcnetypeid(),formatTitle);
                Label address_i = new Label(2, i+1, list.get(i).getIpaddress(),formatTitle);
                Label smc_i = new Label(3, i+1,list.get(i).getSmc(),formatTitle);
                Label ram_i = new Label(4, i+1,list.get(i).getRam(),formatTitle);
                Label cpu_i = new Label(5, i+1,list.get(i).getCpu(),formatTitle);
                ws.addCell(friendlyName_i);
                ws.addCell(typeId_i);
                ws.addCell(address_i);
                ws.addCell(smc_i);
                ws.addCell(ram_i);
                ws.addCell(cpu_i);
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
