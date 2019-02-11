package com.raisecom.bean;

import java.util.List;

/**
 * Created by ligy-008494 on 2019/1/23.
 */
public class Contact {
    private List<String> ipAddr;
    private String count;
    private String inspectType;

    public List<String> getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(List<String> ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInspectType() {
        return inspectType;
    }

    public void setInspectType(String inspectType) {
        this.inspectType = inspectType;
    }
}
