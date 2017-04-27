package com.nuowei.smarthome.modle;/**
 * Created by hp on 2016/8/18.
 */

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;


/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/8/18 14:52
 */
public class XlinkDevice extends DataSupport {
    private long id;
    private Date date;
    private List<DataDevice> dataDevice = new ArrayList<DataDevice>();
    private List<SubDevice> subDevice = new ArrayList<SubDevice>();

    private String softwareVer;// 软件版本
    private String timeZone;// 时区
    private String deviceMac;// mac地址
    private String accessKey;// 密码
    private String hardwareVer;// 硬件版本
    private int deviceType;// 设备类型
    private String deviceName;// 设备名字
    private int deviceState;// 设备状态
    private boolean isupdevice;// 是否升级
    private long deviceId;//设备ID
    private String productId;//设备PID
    private String xDevice;//xDevice
    private int defence;//网关布撤防状态

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
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

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
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

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(int deviceState) {
        this.deviceState = deviceState;
    }

    public boolean isupdevice() {
        return isupdevice;
    }

    public void setIsupdevice(boolean isupdevice) {
        this.isupdevice = isupdevice;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public XDevice getxDevice() {
        JSONObject obj = null;
        try {
            obj = new JSONObject(xDevice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return XlinkAgent.JsonToDevice(obj);
    }

    public void setxDevice(String xDevice) {
        this.xDevice = xDevice;
    }
}
