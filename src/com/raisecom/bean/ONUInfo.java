package com.raisecom.bean;

/**
 * Created by ligy-008494 on 2019/1/17.
 */
public class ONUInfo {

    private String Ircnetnodeid;//网元id
    private String status;//状态
    private String lastDownCause;//上一次离线原因
    private String distance;//距离
    private String receivedPower;//接收光功率
    private String onuHangMacCount;//onu下挂mac地址数
    private String loopPort;//环路端口
    private String portStatus;//环路状态

    @Override
    public String toString() {
        return "ONUInfo{" +
                "Ircnetnodeid='" + Ircnetnodeid + '\'' +
                ", status='" + status + '\'' +
                ", lastDownCause='" + lastDownCause + '\'' +
                ", distance='" + distance + '\'' +
                ", receivedPower='" + receivedPower + '\'' +
                ", onuHangMacCount='" + onuHangMacCount + '\'' +
                ", loopPort='" + loopPort + '\'' +
                ", portStatus='" + portStatus + '\'' +
                '}';
    }

    public String getIrcnetnodeid() {
        return Ircnetnodeid;
    }

    public void setIrcnetnodeid(String ircnetnodeid) {
        Ircnetnodeid = ircnetnodeid;
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
