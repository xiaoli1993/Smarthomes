package com.nuowei.smarthome.modle;/**
 * Created by hp on 2016/8/18.
 */

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/8/18 14:52
 */
public class SubDevice extends DataSupport {
    private long id;
    private Date date;
    private WifiDevice wifiDevice;
    private List<DataDevice> subDevice = new ArrayList<DataDevice>();

    public List<DataDevice> getSubDevice() {
        return subDevice;
    }

    private boolean onlineStatus;
    private boolean Relayonoff;
    private boolean Usbonoff;
    private boolean RGBonoff;
    private int deviceType;
    private int index;
    private int level;
    private String devicename;
    private String deviceState;
    private String zigbeeMac;
    private String temperature;
    private String humidity;

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public boolean isRelayonoff() {
        return Relayonoff;
    }

    public void setRelayonoff(boolean relayonoff) {
        Relayonoff = relayonoff;
    }

    public boolean isUsbonoff() {
        return Usbonoff;
    }

    public void setUsbonoff(boolean usbonoff) {
        Usbonoff = usbonoff;
    }

    public boolean isRGBonoff() {
        return RGBonoff;
    }

    public void setRGBonoff(boolean RGBonoff) {
        this.RGBonoff = RGBonoff;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(String deviceState) {
        this.deviceState = deviceState;
    }

    public String getZigbeeMac() {
        return zigbeeMac;
    }

    public void setZigbeeMac(String zigbeeMac) {
        this.zigbeeMac = zigbeeMac;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setSubDevice(List<DataDevice> subDevice) {
        this.subDevice = subDevice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public WifiDevice getWifiDevice() {
        return wifiDevice;
    }

    public void setWifiDevice(WifiDevice wifiDevice) {
        this.wifiDevice = wifiDevice;
    }
}
