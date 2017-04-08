package com.nuowei.smarthome.modle;

/**
 * @Author : 肖力
 * @Time :  2017/4/8 11:45
 * @Description :
 * @Modify record :
 */

public class Action {
    public Action(String action, String gwMac, String subMac, int deviceType, boolean isGw, boolean isChoise) {
        this.action = action;
        this.gwMac = gwMac;
        this.subMac = subMac;
        this.deviceType = deviceType;
        this.isGw = isGw;
        this.isChoise = isChoise;
    }

    private String action;
    private String gwMac;
    private String subMac;
    private int deviceType;
    private boolean isGw;
    private boolean isChoise;



    public String getGwMac() {
        return gwMac;
    }

    public void setGwMac(String gwMac) {
        this.gwMac = gwMac;
    }

    public String getSubMac() {
        return subMac;
    }

    public void setSubMac(String subMac) {
        this.subMac = subMac;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isGw() {
        return isGw;
    }

    public void setGw(boolean gw) {
        isGw = gw;
    }

    public boolean isChoise() {
        return isChoise;
    }

    public void setChoise(boolean choise) {
        isChoise = choise;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
