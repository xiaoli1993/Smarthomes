package com.nuowei.smarthome.modle;

/**
 * @Author : 肖力
 * @Time :  2017/3/28 15:58
 * @Description :
 * @Modify record :
 */

public class ListMain {
    private String deviceMac;
    private String subMac;
    private boolean isSub;

    public ListMain(String deviceMac, String deviceMac1, boolean issub) {
        this.deviceMac = deviceMac1;
        this.subMac = deviceMac;
        this.isSub = issub;
    }

    public boolean isSub() {
        return isSub;
    }

    public void setSub(boolean sub) {
        isSub = sub;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getSubMac() {
        return subMac;
    }

    public void setSubMac(String subMac) {
        this.subMac = subMac;
    }
}
