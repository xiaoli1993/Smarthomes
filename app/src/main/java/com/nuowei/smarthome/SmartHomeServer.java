package com.nuowei.smarthome;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.PlugGwDevice;
import com.nuowei.smarthome.modle.RGBGwDevice;
import com.nuowei.smarthome.modle.RefreshToken;
import com.nuowei.smarthome.modle.SensorGwDevice;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.THPGwDevice;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.util.Time;

import org.apache.http.Header;
import org.json.JSONException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author : 肖力
 * @Time :  2017/3/28 13:49
 * @Description :
 * @Modify record :
 */

public class SmartHomeServer extends Service {
    private IBinder binder = new SmartHomeServer.LoPongBinder();
    private static Context context;
    private boolean isRegisterBroadcast = false;

    private final Timer timer = new Timer();
    private TimerTask task;
    private boolean isRunTask = true;

    public class LoPongBinder extends Binder {
        // 返回本地服务
        SmartHomeServer getService() {
            return SmartHomeServer.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRegisterBroadcast = true;
        registerReceiver(mBroadcastReceiver, MyUtil.regFilter());
//        Account account = AccountPersist.getInstance().getActiveAccountInfo(
//                this);
//        try {
//            int codeStr1 = (int) Long.parseLong(account.rCode1);
//            int codeStr2 = (int) Long.parseLong(account.rCode2);
//            Log.e("result", "codeStr1=" + codeStr1 + "---------" + "codeStr2="
//                    + codeStr2 + "account.three_number=" + account.three_number);
//            if (account != null) {
//                boolean result = P2PHandler.getInstance().p2pConnect(
//                        account.three_number, codeStr1, codeStr2);
//                Log.e("result", "result=" + result);
//                if (result) {
//                    videoconnect.getInstance().initEseeSDK();
//                    new P2PConnect(getApplicationContext());
//                    MainThread.getInstance(context).go();
//                } else {
//                }
//            } else {
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return START_REDELIVER_INTENT;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    try {
                        HttpManage.getInstance().onRefreshs(SmartHomeServer.this, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                MyApplication.getLogger().e("失败" + error.getMsg() + "s:" + error.getCode());
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                Gson gson = new Gson();
                                RefreshToken refreshToken = gson.fromJson(response, RefreshToken.class);
                                String accessToken = refreshToken.getAccess_token();
                                String refresh_token = refreshToken.getRefresh_token();
                                MyApplication.getMyApplication().setAccessToken(accessToken);
                                MyApplication.getMyApplication().setRefresh_token(refresh_token);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            String macs = intent.getStringExtra(Constants.DEVICE_MAC);
            // 收到pipe包
            if (action.equals(Constants.BROADCAST_DEVICE_SYNC)) {
                String data = intent.getStringExtra(Constants.DATA);
                addSubDevice(macs, data);
            } else if (action.equals(Constants.BROADCAST_RECVPIPE_SYNC)) {
                String data = intent.getStringExtra(Constants.DATA);
                addSubDevice(macs, data);
            } else if (action.equals(Constants.BROADCAST_DEVICE_CHANGED)) {

            } else if (action.equals(Constants.BROADCAST_CONNENCT_SUCCESS)) {

            } else if (action.equals(Constants.BROADCAST_CONNENCT_FAIL)) {

            } else if (action.equals(Constants.BROADCAST_SEND_OVERTIME)) {

            } else if (action.equals(Constants.BROADCAST_SEND_SUCCESS)) {

            }
        }

        private void addSubDevice(String macs, String data) {
            XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(macs);
            Gson gason = new Gson();
            try {
                SensorGwDevice sensorGwDevice = gason.fromJson(data, SensorGwDevice.class);
                SubDevice subDevice = new SubDevice();
                subDevice.setDate(new Date());
                subDevice.setDeviceMac(macs);
                subDevice.setZigbeeMac(sensorGwDevice.getPL().getSensors().get(0).getZigbeeMac());
                subDevice.setDeviceName(sensorGwDevice.getPL().getSensors().get(0).getName());
                subDevice.setDeviceType(sensorGwDevice.getPL().getSensors().get(0).getDeviceType());
                subDevice.setOnlineStatus(sensorGwDevice.getPL().getSensors().get(0).isOnlineStatus());
                subDevice.setDeviceOnoff(sensorGwDevice.getPL().getSensors().get(0).getSensor().getActAlm().get(0).getOnoff());
                subDevice.setLastDate(Time.LongToDate(sensorGwDevice.getPL().getSensors().get(0).getSensor().getActAlm().get(0).getTime()));
                subDevice.setBatteryAlm(sensorGwDevice.getPL().getSensors().get(0).getSensor().isBatteryAlm());
                subDevice.setBatteryPercent(sensorGwDevice.getPL().getSensors().get(0).getBatteryPercent());
                subDevice.setIndex(sensorGwDevice.getPL().getSensors().get(0).getIndex());
                subDevice.setWifiDevice(xlinkDevice);
                SubDeviceManage.getInstance().addDevice(subDevice);
            } catch (Exception e) {
            }
            try {
                THPGwDevice thpGwDevice = gason.fromJson(data, THPGwDevice.class);
                SubDevice subDevice = new SubDevice();
                subDevice.setDate(new Date());
                subDevice.setDeviceMac(macs);
                subDevice.setZigbeeMac(thpGwDevice.getPL().getOID().get(0).getZigbeeMac());
                subDevice.setDeviceName(thpGwDevice.getPL().getOID().get(0).getName());
                subDevice.setDeviceType(thpGwDevice.getPL().getOID().get(0).getDeviceType());
                subDevice.setOnlineStatus(thpGwDevice.getPL().getOID().get(0).isOnlineStatus());
                subDevice.setTemp(thpGwDevice.getPL().getOID().get(0).getTHP().getHumidity());
                subDevice.setHumidity(thpGwDevice.getPL().getOID().get(0).getTHP().getHumidity());
                subDevice.settCkLow(thpGwDevice.getPL().getOID().get(0).getTHP().getT_cklow());
                subDevice.settCkUp(thpGwDevice.getPL().getOID().get(0).getTHP().getT_ckup());
                subDevice.sethCkLow(thpGwDevice.getPL().getOID().get(0).getTHP().getH_cklow());
                subDevice.sethCkUp(thpGwDevice.getPL().getOID().get(0).getTHP().getH_ckup());
                subDevice.setThpCkOnoff(thpGwDevice.getPL().getOID().get(0).getTHP().getCkvalid());
                subDevice.setBatteryAlm(thpGwDevice.getPL().getOID().get(0).getTHP().isBatteryAlm());
                subDevice.setBatteryPercent(thpGwDevice.getPL().getOID().get(0).getBatteryPercent());
                subDevice.setIndex(thpGwDevice.getPL().getOID().get(0).getIndex());
                subDevice.setWifiDevice(xlinkDevice);
                SubDeviceManage.getInstance().addDevice(subDevice);
            } catch (Exception e) {
            }
            try {
                RGBGwDevice rgbGwDevice = gason.fromJson(data, RGBGwDevice.class);
                SubDevice subDevice = new SubDevice();
                subDevice.setDate(new Date());
                subDevice.setDeviceMac(macs);
                subDevice.setZigbeeMac(rgbGwDevice.getPL().getOID().get(0).getZigbeeMac());
                subDevice.setDeviceName(rgbGwDevice.getPL().getOID().get(0).getName());
                subDevice.setDeviceType(rgbGwDevice.getPL().getOID().get(0).getDeviceType());
                if (rgbGwDevice.getPL().getOID().get(0).getOnlineStatus() == 1) {
                    subDevice.setOnlineStatus(true);
                } else {
                    subDevice.setOnlineStatus(false);
                }
                subDevice.setRgbOnoff(rgbGwDevice.getPL().getOID().get(0).getOnoff());
                subDevice.setIndex(rgbGwDevice.getPL().getOID().get(0).getIndex());
                subDevice.setRgblevel(rgbGwDevice.getPL().getOID().get(0).getLevel());
                subDevice.setR(rgbGwDevice.getPL().getOID().get(0).getColorRed());
                subDevice.setG(rgbGwDevice.getPL().getOID().get(0).getColorGreen());
                subDevice.setB(rgbGwDevice.getPL().getOID().get(0).getColorBlue());
                subDevice.setWifiDevice(xlinkDevice);
                SubDeviceManage.getInstance().addDevice(subDevice);
            } catch (Exception e) {
            }
            try {
                PlugGwDevice plugGwDevice = gason.fromJson(data, PlugGwDevice.class);
                SubDevice subDevice = new SubDevice();
                subDevice.setDate(new Date());
                subDevice.setDeviceMac(macs);
                subDevice.setZigbeeMac(plugGwDevice.getPL().getOID().get(0).getZigbeeMac());
                subDevice.setDeviceName(plugGwDevice.getPL().getOID().get(0).getName());
                subDevice.setDeviceType(plugGwDevice.getPL().getOID().get(0).getDeviceType());
                if (plugGwDevice.getPL().getOID().get(0).getOnlineStatus() == 1) {
                    subDevice.setOnlineStatus(true);
                } else {
                    subDevice.setOnlineStatus(false);
                }
                subDevice.setRelayOnoff(plugGwDevice.getPL().getOID().get(0).getRelayonoff());
                try {
                    subDevice.setUsbOnoff(plugGwDevice.getPL().getOID().get(0).getUsbonoff());
                } catch (Exception e) {
                }
                subDevice.setWifiDevice(xlinkDevice);
                SubDeviceManage.getInstance().addDevice(subDevice);
            } catch (Exception e) {
            }

        }

    };

    @Override
    public void onCreate() {
        context = getApplicationContext();
        task = new TimerTask() {
            @Override
            public void run() {
                if (isRunTask) {
                    mHandler.sendEmptyMessage(1);
                }
            }
        };
        timer.schedule(task, 1000 * 60 * 60, 1000 * 60 * 60);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterBroadcast) {
            unregisterReceiver(mBroadcastReceiver);
        }
        timer.cancel();
//        videoconnect.exitEseeSDK();
//        MainThread.getInstance(context).kill();
//        new Thread() {
//            public void run() {
//                P2PHandler.getInstance().p2pDisconnect();
//                Intent i=new Intent();
//                i.setAction("DISCONNECT");
//                context.sendBroadcast(i);
//            };
//        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
