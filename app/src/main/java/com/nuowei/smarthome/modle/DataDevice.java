package com.nuowei.smarthome.modle;/**
 * Created by hp on 2016/8/18.
 */

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/8/18 14:53
 */
public class DataDevice extends DataSupport {
    private long id;
    private Date date;
    private WifiDevice wifiDevice;
    private SubDevice subDevice;
    private String Deviceid;
    private String Zigbeemac;
    private String Temperatureor;
    private String Humidity;
    private String Body_loc_key;
    private String name;
    private String year;
    private String month;
    private String day;
    private String HH;
    private String mm;
    private String ss;
    private String MessageID;
    private String create_date;
    private String Username;
    private String is_push;
    private String is_read;

    public String getDeviceid() {
        return Deviceid;
    }

    public void setDeviceid(String deviceid) {
        Deviceid = deviceid;
    }

    public String getZigbeemac() {
        return Zigbeemac;
    }

    public void setZigbeemac(String zigbeemac) {
        Zigbeemac = zigbeemac;
    }

    public String getTemperatureor() {
        return Temperatureor;
    }

    public void setTemperatureor(String temperatureor) {
        Temperatureor = temperatureor;
    }

    public String getHumidity() {
        return Humidity;
    }

    public void setHumidity(String humidity) {
        Humidity = humidity;
    }

    public String getBody_loc_key() {
        return Body_loc_key;
    }

    public void setBody_loc_key(String body_loc_key) {
        Body_loc_key = body_loc_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHH() {
        return HH;
    }

    public void setHH(String HH) {
        this.HH = HH;
    }

    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public String getMessageID() {
        return MessageID;
    }

    public void setMessageID(String messageID) {
        MessageID = messageID;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getIs_push() {
        return is_push;
    }

    public void setIs_push(String is_push) {
        this.is_push = is_push;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
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

    public SubDevice getSubDevice() {
        return subDevice;
    }

    public void setSubDevice(SubDevice subDevice) {
        this.subDevice = subDevice;
    }
}
