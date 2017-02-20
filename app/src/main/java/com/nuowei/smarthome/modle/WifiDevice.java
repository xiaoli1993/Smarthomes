package com.nuowei.smarthome.modle;/**
 * Created by hp on 2016/8/18.
 */

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.xlink.wifi.sdk.XDevice;

/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/8/18 14:52
 */
public class WifiDevice extends DataSupport {
    private long id;
    private Date date;
    private List<DataDevice> dataDevice = new ArrayList<DataDevice>();
    private List<SubDevice> subDevice = new ArrayList<SubDevice>();

    private String softwareVer;//软件版本
    private String timeZone;//时区
    private String ethMac;//mac地址
    private String accessKey;//密码
    private String hardwareVer;//硬件版本
    private int type;//设备类型
    private String devicename;//设备名字
    private int deviceState;//设备状态
    private boolean isupdevice;//是否升级
    private int deviceId;
    private String productId;

    public int getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(int deviceState) {
        this.deviceState = deviceState;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public boolean isupdevice() {
        return isupdevice;
    }

    public void setIsupdevice(boolean isupdevice) {
        this.isupdevice = isupdevice;
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

    public List<DataDevice> getDataDevice() {
        return dataDevice;
    }

    public void setDataDevice(List<DataDevice> dataDevice) {
        this.dataDevice = dataDevice;
    }

    public List<SubDevice> getSubDevice() {
        return subDevice;
    }

    public void setSubDevice(List<SubDevice> subDevice) {
        this.subDevice = subDevice;
    }

    public String getSoftwareVer() {
        return softwareVer;
    }

    public void setSoftwareVer(String softwareVer) {
        this.softwareVer = softwareVer;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getEthMac() {
        return ethMac;
    }

    public void setEthMac(String ethMac) {
        this.ethMac = ethMac;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getHardwareVer() {
        return hardwareVer;
    }

    public void setHardwareVer(String hardwareVer) {
        this.hardwareVer = hardwareVer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }
}
