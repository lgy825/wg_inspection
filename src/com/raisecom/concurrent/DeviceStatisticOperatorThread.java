package com.raisecom.concurrent;

import com.raisecom.bean.OLTInfo;
import com.raisecom.nms.platform.client.ResourceManager;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.EPONCommonDBUtil;
import com.raisecom.util.EPONConstants;
import com.raisecom.util.SnmpOperationUtil;
import com.raisecom.util.SqlMappingUtil;

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
            String version = objService.getStringValue("SOFTWARE_VER");
            //2. 查询OLT 是否在线
            boolean oltOnlineFlag = SnmpOperationUtil.isOltOnline(objService);
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
            oltInfo.setIpaddress(ip);
            //查询温度
            String tmpt=SnmpOperationUtil.getTemperature4OLT(objService);
            oltInfo.setTemperature( processResult(tmpt));

            //查询OLT 软件版本
            String versfw = SnmpOperationUtil.getSoftwareVer4OLT(objService);
            oltInfo.setSoftware_ver(processResult(versfw));

            //查询OLT CPU/RAM利用率
            ObjService cpuramUsage = SnmpOperationUtil.getCpuRamUsage4OLT(objService);
            String cpuUsage = cpuramUsage.getStringValue("CPU_USAGE");
            oltInfo.setCpu(processResult(cpuUsage));
            String ramUsage = cpuramUsage.getStringValue("MEMORY_USAGE");
            oltInfo.setRam( processResult(ramUsage));

            //查询OLT 电源状态
            String powerStatus = SnmpOperationUtil.getPowerStatus4OLT(objService);
            oltInfo.setPower(processResult(powerStatus));

            //查询VLAN广播域是否能优化缩小
            String vlanOptimize = SnmpOperationUtil.getVlanOptimize(objService);
            oltInfo.setVlan_optimize(processResult(vlanOptimize));

            //查看风扇的状态
            //查询OLT 风扇状态
            String fanStatus = SnmpOperationUtil.getFanStatus4OLT(objService);
            oltInfo.setFan(processResult(fanStatus));

            //查询OLT 业务板卡与主控板卡数量
            String[] cardAmount = SnmpOperationUtil.getCardAmount4OLT(objService);
            String bussinessCardDesc = cardAmount[0];
            bussinessCardDesc = bussinessCardDesc.substring(1, bussinessCardDesc.length()-1);
            oltInfo.setBussiness_card_amount(processResult(bussinessCardDesc));
            String smcDesc = cardAmount[1];
            oltInfo.setSmc(processResult(smcDesc));

            //当前主控运行时间
            String sysUpTime=SnmpOperationUtil.getsysUpTime(objService);
            oltInfo.setSys_uptime(processResult(sysUpTime));

            //主备倒换次数
            String switchedCount=SnmpOperationUtil.getSwitchedCount(objService);
            oltInfo.setSwitched_count(Integer.parseInt(processResult(switchedCount)));

            //主控异常重启次数
            String exceptionSysRebCount=SnmpOperationUtil.getSysRebCountCount(objService);
            oltInfo.setReboot_count(Integer.parseInt(processResult(exceptionSysRebCount)));

            //主控电压
            String controlVoltage = SnmpOperationUtil.getControlVoltage(objService);
            oltInfo.setOlt_power(processResult(controlVoltage));

            //PON口隔离
            String ponPortIsolation = SnmpOperationUtil.getPonPortIsolation(objService);
            oltInfo.setPort_is_solate(processResult(ponPortIsolation));

            //ONU数量统计
            String onuCount = SnmpOperationUtil.getONUCount(objService);
            oltInfo.setOnu_count_info(processResult(onuCount));
            SqlMappingUtil.insertDevice(oltInfo);

            //入库
            SqlMappingUtil.insertDevice(oltInfo);
        }catch(Exception e){

        }
        return true;
    }

    public  String processResult(String s) {
        if(s==null||"".equals(s)||"null".equalsIgnoreCase(s)){
            return ResourceManager.getString(EPONConstants.EPON_STATISTICS_RB, "NONE");
        }else{
            return s;
        }
    }

}
