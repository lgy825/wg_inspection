package com.raisecom.concurrent;

import com.raisecom.bean.OLTInfo;
import com.raisecom.nms.platform.client.ResourceManager;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.*;

import java.util.concurrent.Callable;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class DeviceStatisticOperatorThread implements Callable<Boolean> {
    private ObjService objService;

    public DeviceStatisticOperatorThread(ObjService objService){
        this.objService=objService;
    }

    /**
     * 查看OLT信息并且入库
     * @param ip
     * @param netype
     * @param version
     * @return
     */
    public Boolean call(){
        OLTInfo oltInfo=new OLTInfo();

        try{
            String oltname = objService.getStringValue("FRIENDLY_NAME");
            String hostname = objService.getStringValue("HOSTNAME");
            String oltid=objService.getStringValue("IRCNETNODEID");
            String netype=objService.getStringValue("IRCNETYPEID");
            String ip=objService.getStringValue("IPADDRESS");
            String version  = VersionUtil.getOltVersionByNeID(oltid);
            String configFile=objService.getStringValue("ConfigFile");
            ObjService options = SnmpParamsHelper.getOption(oltid);
            options.setValue("ConfigFile",configFile);
            options.setValue("version",version);
            options.setValue("oltId",oltid);
            //2. 查询OLT 是否在线
            boolean oltOnlineFlag = SnmpOperationUtil.isOltOnline(options);
            if(!oltOnlineFlag){
                String sql = "insert into OLT_STATISTICS_INFO(IRCNETNODEID,FRIENDLY_NAME,iRCNETypeID,IPADDRESS,HOSTNAME) "+
                        "values ('"+oltid+"','"+oltname+"','"+netype+"','"+ip+"','"+hostname+"') ON DUPLICATE KEY UPDATE FRIENDLY_NAME = '" + oltname +"', iRCNETypeID = '" + netype +"',IPADDRESS ='" + ip +"',HOSTNAME ='" + hostname+"'";
                try {
                    EPONCommonDBUtil.getInstance().executeSql(sql, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                }
                return false;
            }
            oltInfo.setIrcnetnodeid(Integer.parseInt(oltid));
            oltInfo.setIrcnetypeid(netype);
            oltInfo.setFriendly_name(oltname);
            oltInfo.setHostname(hostname);
            oltInfo.setIpaddress(ip);
            //查询温度
            String tmpt=SnmpOperationUtil.getTemperature4OLT(options);
            oltInfo.setTemperature( processResult(tmpt));

            //查询OLT 软件版本
            String versfw = SnmpOperationUtil.getSoftwareVer4OLT(options);
            oltInfo.setSoftware_ver(processResult(versfw));

            //查询OLT CPU/RAM利用率
            ObjService cpuramUsage = SnmpOperationUtil.getCpuRamUsage4OLT(options);
            String cpuUsage = cpuramUsage.getStringValue("CPU_USAGE");
            oltInfo.setCpu(processResult(cpuUsage));
            String ramUsage = cpuramUsage.getStringValue("MEMORY_USAGE");
            oltInfo.setRam( processResult(ramUsage));

            //查询OLT 电源状态
            String powerStatus = SnmpOperationUtil.getPowerStatus4OLT(options);
            oltInfo.setPower(processResult(powerStatus));

            //查询VLAN广播域是否能优化缩小
            String vlanOptimize = SnmpOperationUtil.getVlanOptimize(options);
            oltInfo.setVlan_optimize(processResult(vlanOptimize));

            //查询OLT 业务板卡与主控板卡数量
            String[] cardAmount = SnmpOperationUtil.getCardAmount4OLT(options);
            String bussinessCardDesc = cardAmount[0];
            bussinessCardDesc = bussinessCardDesc.substring(1, bussinessCardDesc.length()-1);
            oltInfo.setBussiness_card_amount(processResult(bussinessCardDesc));
            String smcDesc = cardAmount[1];
            oltInfo.setSmc(processResult(smcDesc));

            //查看风扇的状态
            //查询OLT 风扇状态
            String fanStatus = SnmpOperationUtil.getFanStatus4OLT(options);
            oltInfo.setFan(processResult(fanStatus));

            //当前主控运行时间
//            String sysUpTime=SnmpOperationUtil.getsysUpTime(options);
//            oltInfo.setSys_uptime(processResult(sysUpTime));

            //主备倒换次数
            String switchedCount=SnmpOperationUtil.getSwitchedCount(options);
            oltInfo.setSwitched_count(Integer.parseInt(processResult(switchedCount)));

            //主控电压
            String controlVoltage = SnmpOperationUtil.getControlVoltage(options);
            oltInfo.setOlt_power(processResult(controlVoltage));

            //PON口隔离
            String ponPortIsolation = SnmpOperationUtil.getPonPortIsolation(options);
            oltInfo.setPort_is_solate(processResult(ponPortIsolation));

            //ONU数量统计
            String onuCount = SnmpOperationUtil.getONUCount(options);
            oltInfo.setOnu_count_info(processResult(onuCount));

            //入库
            SqlMappingUtil.insertDevice(oltInfo);
        }catch(Exception e){

        }
        return true;
    }

    public  String processResult(String s) {
        if(s==null||"".equals(s)||"null".equalsIgnoreCase(s)){
            return "NONE";
        }else{
            return s;
        }
    }

}
