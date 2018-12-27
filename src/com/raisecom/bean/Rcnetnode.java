package com.raisecom.bean;

/**
 * Created by ligy-008494 on 2018/11/20.
 */
public class Rcnetnode {

    private String IRCNETNODEID;
    private String friendlyName;
    private String hostName;
    private String vers;
    private String iRCNETypeID;
    private String readCom;
    private String writeCom;
    private String timeOut;
    private String port;
    private String ipAddr;
    private String retry;
    private String softVers;
    public String getIRCNETNODEID() {
        return IRCNETNODEID;
    }

    public void setIRCNETNODEID(String IRCNETNODEID) {
        this.IRCNETNODEID = IRCNETNODEID;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getVers() {
        return vers;
    }

    public void setVers(String vers) {
        this.vers = vers;
    }

    public String getiRCNETypeID() {
        return iRCNETypeID;
    }

    public void setiRCNETypeID(String iRCNETypeID) {
        this.iRCNETypeID = iRCNETypeID;
    }

    public String getReadCom() {
        return readCom;
    }

    public void setReadCom(String readCom) {
        this.readCom = readCom;
    }

    public String getWriteCom() {
        return writeCom;
    }

    public void setWriteCom(String writeCom) {
        this.writeCom = writeCom;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public String getSoftVers() {
        return softVers;
    }

    public void setSoftVers(String softVers) {
        this.softVers = softVers;
    }

    @Override
    public String toString() {
        return "Rcnetnode{" +
                "IRCNETNODEID='" + IRCNETNODEID + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
                ", hostName='" + hostName + '\'' +
                ", vers='" + vers + '\'' +
                ", iRCNETypeID='" + iRCNETypeID + '\'' +
                ", readCom='" + readCom + '\'' +
                ", writeCom='" + writeCom + '\'' +
                ", timeOut='" + timeOut + '\'' +
                ", port='" + port + '\'' +
                ", ipAddr='" + ipAddr + '\'' +
                ", retry='" + retry + '\'' +
                ", softVers='" + softVers + '\'' +
                '}';
    }




}
