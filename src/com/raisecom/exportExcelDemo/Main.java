package com.raisecom.exportExcelDemo;

import com.raisecom.bean.OLTInfo;
import com.raisecom.common.logging.LogFactory;
import com.raisecom.common.logging.Logger;
import com.raisecom.db.InitSelfmDBPoolTask;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

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
            WritableSheet ws = wwb.createSheet("Test Shee 1", 0);

            //查询数据库中所有的数据

            //要插入到的Excel表格的行号，默认从0开始
            Label nodeId= new Label(0, 0, "nodeId");//表示第
            Label typeId= new Label(1, 0, "typeId");
            Label address= new Label(2, 0, "address");
            Label hostName= new Label(3, 0, "hostName");
            Label smc= new Label(4, 0, "smc");
            Label cpu= new Label(5, 0, "cpu");

            ws.addCell(nodeId);
            ws.addCell(typeId);
            ws.addCell(address);
            ws.addCell(hostName);
            ws.addCell(smc);
            ws.addCell(cpu);


            for (int i = 0; i < list.size(); i++) {

                Label nodeId_i = new Label(0, i+1, list.get(i).getIrcnetnodeid()+"");
                Label typeId_i = new Label(1, i+1, list.get(i).getFriendly_name());
                Label address_i = new Label(2, i+1, list.get(i).getIpaddress());
                Label hostName_i = new Label(3, i+1, list.get(i).getHostname()+"");
                Label smc_i = new Label(4, i+1,list.get(i).getSmc());
                Label cpu_i = new Label(5, i+1,list.get(i).getCpu());

                ws.addCell(nodeId_i);
                ws.addCell(typeId_i);
                ws.addCell(address_i);
                ws.addCell(hostName_i);
                ws.addCell(smc_i);
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
