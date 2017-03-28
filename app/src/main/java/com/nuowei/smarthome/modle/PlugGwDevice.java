package com.nuowei.smarthome.modle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright ©深圳市海曼科技有限公司
 */

public class PlugGwDevice extends PLBase {
    /**
     * CID : 30022
     * PL : {"2.1.1.3.1":[{"swVersion":"","zigbeeMac":"4B342F1FC9435000","deviceType":67,"factoryID":1,"index":4098,"name":"WDoor_4B342F","onlineStatus":1,"relayonoff":0,"usbonoff":0}]}
     * RC : 1
     * SID :
     * SN : 8
     * TEID : 100012461
     */

    private PLBean PL;

    public PLBean getPL() {
        return PL;
    }

    public void setPL(PLBean PL) {
        this.PL = PL;
    }

    public static class PLBean {
        @SerializedName("2.1.1.3.1")
        private List<OIDBean> OID;

        public List<OIDBean> getOID() {
            return OID;
        }

        public void setOID(List<OIDBean> OID) {
            this.OID = OID;
        }

        public static class OIDBean {
            /**
             * swVersion :
             * zigbeeMac : 4B342F1FC9435000
             * deviceType : 67
             * factoryID : 1
             * index : 4098
             * name : WDoor_4B342F
             * onlineStatus : 1
             * relayonoff : 0
             * usbonoff : 0
             */

            private String swVersion;
            private String zigbeeMac;
            private int deviceType;
            private int factoryID;
            private int index;
            private String name;
            private int onlineStatus;
            @Expose
            private int relayonoff;
            @Expose
            private int usbonoff;
            @Expose
            private String power_w;
            @Expose
            private String power_kwh;

            public String getSwVersion() {
                return swVersion;
            }

            public void setSwVersion(String swVersion) {
                this.swVersion = swVersion;
            }

            public String getZigbeeMac() {
                return zigbeeMac;
            }

            public void setZigbeeMac(String zigbeeMac) {
                this.zigbeeMac = zigbeeMac;
            }

            public int getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(int deviceType) {
                this.deviceType = deviceType;
            }

            public int getFactoryID() {
                return factoryID;
            }

            public void setFactoryID(int factoryID) {
                this.factoryID = factoryID;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getOnlineStatus() {
                return onlineStatus;
            }

            public void setOnlineStatus(int onlineStatus) {
                this.onlineStatus = onlineStatus;
            }

            public int getRelayonoff() {
                return relayonoff;
            }

            public void setRelayonoff(int relayonoff) {
                this.relayonoff = relayonoff;
            }

            public int getUsbonoff() {
                return usbonoff;
            }

            public void setUsbonoff(int usbonoff) {
                this.usbonoff = usbonoff;
            }
        }
    }
}
