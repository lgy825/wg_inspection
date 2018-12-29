package com.raisecom.bean;

/**
 * Created by liujs-008398 on 2018-12-28.
 */
public class OLTInfo {
    private Integer ircnetnodeid ;
    private String friendly_name ;
    private String ircnetypeid ;
    private String ipaddress ;
    private String hostname ;
    private String smc ;
    private String cpu ;
    private String temperature ;
    private String power ;
    private String fan ;
    private String software_ver ;
    private String bussiness_card_amount ;
    private String vlan_optimize ;
    private String ram ;
    private String sys_uptime ;
    private Integer switched_count ;
    private Integer reboot_count ;
    private String olt_power ;
    private String port_is_solate ;
    private String onu_count_info ;


    @Override
    public String toString() {
        return "OLTInfo{" +
                "ircnetnodeid=" + ircnetnodeid +
                ", friendly_name='" + friendly_name + '\'' +
                ", ircnetypeid='" + ircnetypeid + '\'' +
                ", ipaddress='" + ipaddress + '\'' +
                ", hostname='" + hostname + '\'' +
                ", smc='" + smc + '\'' +
                ", cpu='" + cpu + '\'' +
                ", temperature='" + temperature + '\'' +
                ", power='" + power + '\'' +
                ", fan='" + fan + '\'' +
                ", software_ver='" + software_ver + '\'' +
                ", bussiness_card_amount='" + bussiness_card_amount + '\'' +
                ", vlan_optimize='" + vlan_optimize + '\'' +
                ", ram='" + ram + '\'' +
                ", sys_uptime='" + sys_uptime + '\'' +
                ", switched_count=" + switched_count +
                ", reboot_count=" + reboot_count +
                ", olt_power='" + olt_power + '\'' +
                ", port_is_solate='" + port_is_solate + '\'' +
                ", onu_count_info='" + onu_count_info + '\'' +
                '}';
    }

    public Integer getIrcnetnodeid() {
        return ircnetnodeid;
    }

    public void setIrcnetnodeid(Integer ircnetnodeid) {
        this.ircnetnodeid = ircnetnodeid;
    }

    public String getFriendly_name() {
        return friendly_name;
    }

    public void setFriendly_name(String friendly_name) {
        this.friendly_name = friendly_name;
    }

    public String getIrcnetypeid() {
        return ircnetypeid;
    }

    public void setIrcnetypeid(String ircnetypeid) {
        this.ircnetypeid = ircnetypeid;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getSmc() {
        return smc;
    }

    public void setSmc(String smc) {
        this.smc = smc;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
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

    public String getFan() {
        return fan;
    }

    public void setFan(String fan) {
        this.fan = fan;
    }

    public String getSoftware_ver() {
        return software_ver;
    }

    public void setSoftware_ver(String software_ver) {
        this.software_ver = software_ver;
    }

    public String getBussiness_card_amount() {
        return bussiness_card_amount;
    }

    public void setBussiness_card_amount(String bussiness_card_amount) {
        this.bussiness_card_amount = bussiness_card_amount;
    }

    public String getVlan_optimize() {
        return vlan_optimize;
    }

    public void setVlan_optimize(String vlan_optimize) {
        this.vlan_optimize = vlan_optimize;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getSys_uptime() {
        return sys_uptime;
    }

    public void setSys_uptime(String sys_uptime) {
        this.sys_uptime = sys_uptime;
    }

    public Integer getSwitched_count() {
        return switched_count;
    }

    public void setSwitched_count(Integer switched_count) {
        this.switched_count = switched_count;
    }

    public Integer getReboot_count() {
        return reboot_count;
    }

    public void setReboot_count(Integer reboot_count) {
        this.reboot_count = reboot_count;
    }

    public String getOlt_power() {
        return olt_power;
    }

    public void setOlt_power(String olt_power) {
        this.olt_power = olt_power;
    }

    public String getPort_is_solate() {
        return port_is_solate;
    }

    public void setPort_is_solate(String port_is_solate) {
        this.port_is_solate = port_is_solate;
    }

    public String getOnu_count_info() {
        return onu_count_info;
    }

    public void setOnu_count_info(String onu_count_info) {
        this.onu_count_info = onu_count_info;
    }
}
