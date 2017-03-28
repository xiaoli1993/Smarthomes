package com.nuowei.smarthome.modle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright ©深圳市海曼科技有限公司
 */

public class THPGwDevice extends PLBase{

    /**
     * PL : {"2.1.1.3.4":[{"index":4100,"zigbeeMac":"7B683953C9435000","name":"THP_7B6839","factoryID":1,"deviceType":21,"swVersion":"","onlineStatus":false,"batteryPercent":0,"THP":{"batteryAlm":false,"temperature":"23.42","t_ckup":"78.00","t_cklow":"2.00","humidity":"48.13","h_ckup":"88.00","h_cklow":"35.00","ckvalid":1,"onoff":2,"pressure":"NA"},"sensorcount":5}]}
     */

    private PLBean PL;

    public PLBean getPL() {
        return PL;
    }

    public void setPL(PLBean PL) {
        this.PL = PL;
    }

    public static class PLBean {
        @SerializedName("2.1.1.3.4")
        private List<OIDBean> OID;

        public List<OIDBean> getOID() {
            return OID;
        }

        public void setOID(List<OIDBean> OID) {
            this.OID = OID;
        }

        public static class OIDBean {
            /**
             * index : 4100
             * zigbeeMac : 7B683953C9435000
             * name : THP_7B6839
             * factoryID : 1
             * deviceType : 21
             * swVersion :
             * onlineStatus : false
             * batteryPercent : 0
             * THP : {"batteryAlm":false,"temperature":"23.42","t_ckup":"78.00","t_cklow":"2.00","humidity":"48.13","h_ckup":"88.00","h_cklow":"35.00","ckvalid":1,"onoff":2,"pressure":"NA"}
             * sensorcount : 5
             */

            private int index;
            private String zigbeeMac;
            private String name;
            private int factoryID;
            private int deviceType;
            private String swVersion;
            private boolean onlineStatus;
            private int batteryPercent;
            private THPBean THP;
            private int sensorcount;

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public String getZigbeeMac() {
                return zigbeeMac;
            }

            public void setZigbeeMac(String zigbeeMac) {
                this.zigbeeMac = zigbeeMac;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getFactoryID() {
                return factoryID;
            }

            public void setFactoryID(int factoryID) {
                this.factoryID = factoryID;
            }

            public int getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(int deviceType) {
                this.deviceType = deviceType;
            }

            public String getSwVersion() {
                return swVersion;
            }

            public void setSwVersion(String swVersion) {
                this.swVersion = swVersion;
            }

            public boolean isOnlineStatus() {
                return onlineStatus;
            }

            public void setOnlineStatus(boolean onlineStatus) {
                this.onlineStatus = onlineStatus;
            }

            public int getBatteryPercent() {
                return batteryPercent;
            }

            public void setBatteryPercent(int batteryPercent) {
                this.batteryPercent = batteryPercent;
            }

            public THPBean getTHP() {
                return THP;
            }

            public void setTHP(THPBean THP) {
                this.THP = THP;
            }

            public int getSensorcount() {
                return sensorcount;
            }

            public void setSensorcount(int sensorcount) {
                this.sensorcount = sensorcount;
            }

            public static class THPBean {
                /**
                 * batteryAlm : false
                 * temperature : 23.42
                 * t_ckup : 78.00
                 * t_cklow : 2.00
                 * humidity : 48.13
                 * h_ckup : 88.00
                 * h_cklow : 35.00
                 * ckvalid : 1
                 * onoff : 2
                 * pressure : NA
                 */

                private boolean batteryAlm;
                private String temperature;
                private String t_ckup;
                private String t_cklow;
                private String humidity;
                private String h_ckup;
                private String h_cklow;
                private int ckvalid;
                private int onoff;
                private String pressure;

                public boolean isBatteryAlm() {
                    return batteryAlm;
                }

                public void setBatteryAlm(boolean batteryAlm) {
                    this.batteryAlm = batteryAlm;
                }

                public String getTemperature() {
                    return temperature;
                }

                public void setTemperature(String temperature) {
                    this.temperature = temperature;
                }

                public String getT_ckup() {
                    return t_ckup;
                }

                public void setT_ckup(String t_ckup) {
                    this.t_ckup = t_ckup;
                }

                public String getT_cklow() {
                    return t_cklow;
                }

                public void setT_cklow(String t_cklow) {
                    this.t_cklow = t_cklow;
                }

                public String getHumidity() {
                    return humidity;
                }

                public void setHumidity(String humidity) {
                    this.humidity = humidity;
                }

                public String getH_ckup() {
                    return h_ckup;
                }

                public void setH_ckup(String h_ckup) {
                    this.h_ckup = h_ckup;
                }

                public String getH_cklow() {
                    return h_cklow;
                }

                public void setH_cklow(String h_cklow) {
                    this.h_cklow = h_cklow;
                }

                public int getCkvalid() {
                    return ckvalid;
                }

                public void setCkvalid(int ckvalid) {
                    this.ckvalid = ckvalid;
                }

                public int getOnoff() {
                    return onoff;
                }

                public void setOnoff(int onoff) {
                    this.onoff = onoff;
                }

                public String getPressure() {
                    return pressure;
                }

                public void setPressure(String pressure) {
                    this.pressure = pressure;
                }
            }
        }
    }
}
