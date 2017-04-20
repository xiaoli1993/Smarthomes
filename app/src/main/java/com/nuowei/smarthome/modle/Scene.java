package com.nuowei.smarthome.modle;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * @Author : 肖力
 * @Time :  2017/4/19 10:34
 * @Description :
 * @Modify record :
 */

public class Scene {


    /**
     * name : scene
     * sceneID : 1
     * time : {"month":255,"day":255,"hour":255,"min":255,"type":255,"wkflag":255}
     * timeEnable : 1
     * countTime : 0
     * load : ["1.65282"]
     */
    @Expose
    private String gwMac;
    private String name;
    private int sceneID;
    private TimeBean time;
    private int timeEnable;
    private int countTime;
    private List<String> load;

    public String getGwMac() {
        return gwMac;
    }

    public void setGwMac(String gwMac) {
        this.gwMac = gwMac;
    }

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
         * month : 255
         * day : 255
         * hour : 255
         * min : 255
         * type : 255
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
