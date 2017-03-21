package com.nuowei.smarthome.modle;/**
 * Created by hp on 2016/8/13.
 */

/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/8/13 10:34
 */
public class IpMac {
    /**
     * ret : 1
     * start : -1
     * end : -1
     * country : 中国
     * province : 广东
     * city : 深圳
     * district :
     * isp :
     * type :
     * desc :
     */

    private int ret;
    private int start;
    private int end;
    private String country;
    private String province;
    private String city;
    private String district;
    private String isp;
    private String type;
    private String desc;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
