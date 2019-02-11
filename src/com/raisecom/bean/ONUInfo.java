package com.raisecom.bean;

/**
 * Created by ligy-008494 on 2019/1/17.
 */
public class ONUInfo {

    private Integer Ircnetnodeid ; //网元id
    private String ipaddress ; //网元ip
    private String friendlyName ; //ONU id
    private String subnetype ; //型号
    private String software ; //版本信息
    private String macaddress ; //mac地址
    private String status ; //状态
    private String lastDownCause ; //上一次离线原因
    private String distance ; //距离
    private String receivedPower ; //接收光功率
    private String onuHangMacCount ; //onu下挂mac地址数
    private String loopPort ; //环路端口
    private String portStatus ; //环路状态
    private String ircnetoltId;//

    public String getIrcnetoltId() {
        return ircnetoltId;
    }

    public void setIrcnetoltId(String ircnetoltId) {
        this.ircnetoltId = ircnetoltId;
    }

    @Override
    public String toString() {
        return "ONUInfo{" +
                "Ircnetnodeid=" + Ircnetnodeid +
                ", ipaddress='" + ipaddress + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
                ", subnetype='" + subnetype + '\'' +
                ", software='" + software + '\'' +
                ", macaddress='" + macaddress + '\'' +
                ", status='" + status + '\'' +
                ", lastDownCause='" + lastDownCause + '\'' +
                ", distance='" + distance + '\'' +
                ", receivedPower='" + receivedPower + '\'' +
                ", onuHangMacCount='" + onuHangMacCount + '\'' +
                ", loopPort='" + loopPort + '\'' +
                ", portStatus='" + portStatus + '\'' +
                '}';
    }

    public Integer getIrcnetnodeid() {
        return Ircnetnodeid;
    }

    public void setIrcnetnodeid(Integer ircnetnodeid) {
        Ircnetnodeid = ircnetnodeid;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getSubnetype() {
        return subnetype;
    }

    public void setSubnetype(String subnetype) {
        this.subnetype = subnetype;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastDownCause() {
        return lastDownCause;
    }

    public void setLastDownCause(String lastDownCause) {
        this.lastDownCause = lastDownCause;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getReceivedPower() {
        return receivedPower;
    }

    public void setReceivedPower(String receivedPower) {
        this.receivedPower = receivedPower;
    }

    public String getOnuHangMacCount() {
        return onuHangMacCount;
    }

    public void setOnuHangMacCount(String onuHangMacCount) {
        this.onuHangMacCount = onuHangMacCount;
    }

    public String getLoopPort() {
        return loopPort;
    }

    public void setLoopPort(String loopPort) {
        this.loopPort = loopPort;
    }

    public String getPortStatus() {
        return portStatus;
    }

    public void setPortStatus(String portStatus) {
        this.portStatus = portStatus;
    }
}
