package com.nuowei.smarthome.modle;/**
 * Created by hp on 2016/8/17.
 */

/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/8/17 14:46
 */
public class ChoiceAddDevice {
    private int deviceid;
    private String devicename;
    private boolean iswifi;

    public ChoiceAddDevice(int deviceid, String devicename, boolean iswifi) {
        this.deviceid = deviceid;
        this.devicename = devicename;
        this.iswifi = iswifi;
        
    }



    public int getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(int deviceid) {
        this.deviceid = deviceid;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public boolean iswifi() {
        return iswifi;
    }

    public void setIswifi(boolean iswifi) {
        this.iswifi = iswifi;
    }
}
