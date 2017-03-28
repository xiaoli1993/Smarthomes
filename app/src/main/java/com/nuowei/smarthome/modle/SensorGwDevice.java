package com.nuowei.smarthome.modle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright ©深圳市海曼科技有限公司
 */

public class SensorGwDevice extends PLBase {

    private PLBean PL;

    public PLBean getPL() {
        return PL;
    }

    public void setPL(PLBean PL) {
        this.PL = PL;
    }

    public static class PLBean {
        @SerializedName("2.1.1.3.4")
        private List<SensorsBean> Sensors;

        public List<SensorsBean> getSensors() {
            return Sensors;
        }

        public void setSensors(List<SensorsBean> Sensors) {
            this.Sensors = Sensors;
        }

        public static class SensorsBean {
            /**
             * index : 4101
             * zigbeeMac : 0646CB0B006F0D00
             * name : Smoke_0646CB
             * factoryID : 1
             * deviceType : 20
             * swVersion :
             * onlineStatus : false
             * batteryPercent : 0
             * sensor : {"onoff":2,"batteryAlm":false,"newActAlm":true,"setActAlmEn":{"enable":true,"sh":0,"sm":0,"eh":0,"em":0,"wf":255},"actAlm":[{"time":1482819310,"SC":"set","onoff":0}]}
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
            private SensorBean sensor;
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

            public SensorBean getSensor() {
                return sensor;
            }

            public void setSensor(SensorBean sensor) {
                this.sensor = sensor;
            }

            public int getSensorcount() {
                return sensorcount;
            }

            public void setSensorcount(int sensorcount) {
                this.sensorcount = sensorcount;
            }

            public static class SensorBean {
                /**
                 * onoff : 2
                 * batteryAlm : false
                 * newActAlm : true
                 * setActAlmEn : {"enable":true,"sh":0,"sm":0,"eh":0,"em":0,"wf":255}
                 * actAlm : [{"time":1482819310,"SC":"set","onoff":0}]
                 */

                private int onoff;
                private boolean batteryAlm;
                private boolean newActAlm;
                private SetActAlmEnBean setActAlmEn;
                private List<ActAlmBean> actAlm;

                public int getOnoff() {
                    return onoff;
                }

                public void setOnoff(int onoff) {
                    this.onoff = onoff;
                }

                public boolean isBatteryAlm() {
                    return batteryAlm;
                }

                public void setBatteryAlm(boolean batteryAlm) {
                    this.batteryAlm = batteryAlm;
                }

                public boolean isNewActAlm() {
                    return newActAlm;
                }

                public void setNewActAlm(boolean newActAlm) {
                    this.newActAlm = newActAlm;
                }

                public SetActAlmEnBean getSetActAlmEn() {
                    return setActAlmEn;
                }

                public void setSetActAlmEn(SetActAlmEnBean setActAlmEn) {
                    this.setActAlmEn = setActAlmEn;
                }

                public List<ActAlmBean> getActAlm() {
                    return actAlm;
                }

                public void setActAlm(List<ActAlmBean> actAlm) {
                    this.actAlm = actAlm;
                }

                public static class SetActAlmEnBean {
                    /**
                     * enable : true
                     * sh : 0
                     * sm : 0
                     * eh : 0
                     * em : 0
                     * wf : 255
                     */

                    private boolean enable;
                    private int sh;
                    private int sm;
                    private int eh;
                    private int em;
                    private int wf;

                    public boolean isEnable() {
                        return enable;
                    }

                    public void setEnable(boolean enable) {
                        this.enable = enable;
                    }

                    public int getSh() {
                        return sh;
                    }

                    public void setSh(int sh) {
                        this.sh = sh;
                    }

                    public int getSm() {
                        return sm;
                    }

                    public void setSm(int sm) {
                        this.sm = sm;
                    }

                    public int getEh() {
                        return eh;
                    }

                    public void setEh(int eh) {
                        this.eh = eh;
                    }

                    public int getEm() {
                        return em;
                    }

                    public void setEm(int em) {
                        this.em = em;
                    }

                    public int getWf() {
                        return wf;
                    }

                    public void setWf(int wf) {
                        this.wf = wf;
                    }
                }

                public static class ActAlmBean {
                    /**
                     * time : 1482819310
                     * SC : set
                     * onoff : 0
                     */

                    private long time;
                    private String SC;
                    private int onoff;

                    public long getTime() {
                        return time;
                    }

                    public void setTime(long time) {
                        this.time = time;
                    }

                    public String getSC() {
                        return SC;
                    }

                    public void setSC(String SC) {
                        this.SC = SC;
                    }

                    public int getOnoff() {
                        return onoff;
                    }

                    public void setOnoff(int onoff) {
                        this.onoff = onoff;
                    }
                }
            }
        }
    }
}
