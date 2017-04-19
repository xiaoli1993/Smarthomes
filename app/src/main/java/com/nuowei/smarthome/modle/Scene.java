package com.nuowei.smarthome.modle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Author : 肖力
 * @Time :  2017/4/19 10:34
 * @Description :
 * @Modify record :
 */

public class Scene extends PLBase {


    /**
     * PL : {"2.1.1.3.3.250":[{"name":"场景","sceneID":2,"time":{"month":0,"day":0,"hour":14,"min":40,"type":1,"wkflag":255},"timeEnable":0,"countTime":51449,"load":["4099.65280","4097.65025","4098.65281"]}]}
     */

    private PLBean PL;
    @Expose
    private String gwMac;

    public String getGwMac() {
        return gwMac;
    }

    public void setGwMac(String gwMac) {
        this.gwMac = gwMac;
    }

    public PLBean getPL() {
        return PL;
    }

    public void setPL(PLBean PL) {
        this.PL = PL;
    }

    public static class PLBean {
        @SerializedName("2.1.1.3.3.250")
        private List<SceneBean> Scene;

        public List<SceneBean> getScene() {
            return Scene;
        }

        public void setScene(List<SceneBean> Scene) {
            this.Scene = Scene;
        }

        public static class SceneBean {
            /**
             * name : 场景
             * sceneID : 2
             * time : {"month":0,"day":0,"hour":14,"min":40,"type":1,"wkflag":255}
             * timeEnable : 0
             * countTime : 51449
             * load : ["4099.65280","4097.65025","4098.65281"]
             */

            private String name;
            private int sceneID;
            private TimeBean time;
            private int timeEnable;
            private int countTime;
            private List<String> load;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getSceneID() {
                return sceneID;
            }

            public void setSceneID(int sceneID) {
                this.sceneID = sceneID;
            }

            public TimeBean getTime() {
                return time;
            }

            public void setTime(TimeBean time) {
                this.time = time;
            }

            public int getTimeEnable() {
                return timeEnable;
            }

            public void setTimeEnable(int timeEnable) {
                this.timeEnable = timeEnable;
            }

            public int getCountTime() {
                return countTime;
            }

            public void setCountTime(int countTime) {
                this.countTime = countTime;
            }

            public List<String> getLoad() {
                return load;
            }

            public void setLoad(List<String> load) {
                this.load = load;
            }

            public static class TimeBean {
                /**
                 * month : 0
                 * day : 0
                 * hour : 14
                 * min : 40
                 * type : 1
                 * wkflag : 255
                 */

                private int month;
                private int day;
                private int hour;
                private int min;
                private int type;
                private int wkflag;

                public int getMonth() {
                    return month;
                }

                public void setMonth(int month) {
                    this.month = month;
                }

                public int getDay() {
                    return day;
                }

                public void setDay(int day) {
                    this.day = day;
                }

                public int getHour() {
                    return hour;
                }

                public void setHour(int hour) {
                    this.hour = hour;
                }

                public int getMin() {
                    return min;
                }

                public void setMin(int min) {
                    this.min = min;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public int getWkflag() {
                    return wkflag;
                }

                public void setWkflag(int wkflag) {
                    this.wkflag = wkflag;
                }
            }
        }
    }
}
