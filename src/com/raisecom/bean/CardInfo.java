package com.raisecom.bean;

/**
 * Created by ligy-008494 on 2019/1/23.
 */
public class CardInfo {
    private String friendlyName;
    private String ipAddr;
    private String cardId;
    private String ircnetype;
    private String cardType;
    private String slotId;
    private String status;
    private String cpu;
    private String ram;
    private String temperature;
    private String power;
    private String ver;

    @Override
    public String toString() {
        return "CardInfo{" +
                "friendlyName='" + friendlyName + '\'' +
                ", ipAddr='" + ipAddr + '\'' +
                ", cardId='" + cardId + '\'' +
                ", ircnetype='" + ircnetype + '\'' +
                ", cardType='" + cardType + '\'' +
                ", slotId='" + slotId + '\'' +
                ", status='" + status + '\'' +
                ", cpu='" + cpu + '\'' +
                ", ram='" + ram + '\'' +
                ", temperature='" + temperature + '\'' +
                ", power='" + power + '\'' +
                ", ver='" + ver + '\'' +
                '}';
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getIrcnetype() {
        return ircnetype;
    }

    public void setIrcnetype(String ircnetype) {
        this.ircnetype = ircnetype;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
}
