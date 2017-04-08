package com.nuowei.smarthome.util;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.activity.LoginActivity;
import com.nuowei.smarthome.activity.MainActivity;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.modle.EventIsOnline;
import com.nuowei.smarthome.modle.EventNotifyData;
import com.nuowei.smarthome.modle.Notifications;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.bean.EventNotify;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/21 08:53
 * @Description :
 */
public class MyUtil {
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * 获取deviceid  json数组
     *
     * @return
     */
    public static JSONArray getdeviceid() {
        JSONArray deviceid = new JSONArray();
        List<XlinkDevice> listDev = DeviceManage.getInstance().getDevices();
//        for (int i = 0; i < device.size(); i++) {
//            MyApplication.getLogger().json(device.get(i).getxDevice());
//            if (device.get(i).getDeviceId() > 1000) {
//                deviceid.put(device.get(i).getDeviceId());
//                MyApplication.getLogger().d(device.get(i).getDeviceId()+"");
//            }
//        }
        for (XlinkDevice device : listDev) {
            deviceid.put(device.getDeviceId());
        }
        return deviceid;
    }


    /**
     * id	是	设备ID
     * mac	是	设备MAC地址
     * is_active	是	是否激活，布尔值，true或false
     * active_date	是	激活时间，例：2015-10-09T08 : 15 : 40.843Z
     * is_online	是	是否在线，布尔值，true或false
     * last_login	是	最近登录时间，例：2015-10-09T08 : 15 : 40.843Z
     * active_code	是	激活码
     * authorize_code	是	认证码
     * mcu_mod	是	MCU型号
     * mcu_version	是	MCU版本号
     * firmware_mod	是	固件型号
     * firmware_version	是	固件版本号
     * product_id	是	所属的产品ID
     * access_key	是	设备本地密码
     * role	是	用户和设备的订阅关系，admin还user
     *
     * @return
     */

    public static XlinkDevice subscribeToDevice(JSONObject jsonObj) {
        XlinkDevice device = new XlinkDevice();
        int devicetype = 0;
        String deviceName = "";
        int version = 0;
        try {
            String product_id = jsonObj.getString("product_id");
            int mcu_version = jsonObj.getInt("mcu_version");
            String mac = jsonObj.getString("mac");
            int firmware_version = jsonObj.getInt("firmware_version");
            int access_key = jsonObj.getInt("access_key");
            long deviceid = jsonObj.getLong("id");
            try {
                if (product_id.equals(Constants.ZIGBEE_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY;
                    version = 2;
                } else if (product_id.equals(Constants.PLUGIN_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN;
                    version = 1;
                } else if (product_id.equals(Constants.METRTING_PLUGIN_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN;
                    version = 2;
                } else if (product_id.equals(Constants.AIR_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_AIR;
                    version = 2;
                } else if (product_id.equals(Constants.REMOTE_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_RC;
                    version = 2;
                } else if (product_id.equals(Constants.GAS_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_GAS;
                    version = 2;
                }
                deviceName = jsonObj.getString("name");
                if (MyUtil.isEmptyString(deviceName)) {
                    if (product_id.equals(Constants.ZIGBEE_PRODUCTID)) {
                        deviceName = "ZGW_" + mac;
                    } else if (product_id.equals(Constants.PLUGIN_PRODUCTID)) {
                        deviceName = "PLUG_" + mac;
                    } else if (product_id.equals(Constants.METRTING_PLUGIN_PRODUCTID)) {
                        deviceName = "EWPLUG_" + mac;
                    } else if (product_id.equals(Constants.AIR_PRODUCTID)) {
                        deviceName = "AIR_" + mac;
                    } else if (product_id.equals(Constants.REMOTE_PRODUCTID)) {
                        deviceName = "RC_" + mac;
                    } else if (product_id.equals(Constants.GAS_PRODUCTID)) {
                        deviceName = "Gas_" + mac;
                    }
                }
            } catch (Exception e) {
                if (product_id.equals(Constants.ZIGBEE_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY;
                    deviceName = "ZGW_" + mac;
                    version = 2;
                } else if (product_id.equals(Constants.PLUGIN_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN;
                    deviceName = "PLUG_" + mac;
                    version = 1;
                } else if (product_id.equals(Constants.METRTING_PLUGIN_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN;
                    deviceName = "EWPLUG_" + mac;
                    version = 2;
                } else if (product_id.equals(Constants.AIR_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_AIR;
                    deviceName = "AIR_" + mac;
                    version = 2;
                } else if (product_id.equals(Constants.REMOTE_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_RC;
                    deviceName = "RC_" + mac;
                    version = 2;
                } else if (product_id.equals(Constants.GAS_PRODUCTID)) {
                    devicetype = Constants.DEVICE_TYPE.DEVICE_WIFI_GAS;
                    deviceName = "Gas_" + mac;
                    version = 2;
                }
            }
            JSONObject obj = new JSONObject();
            obj.put("protocol", 1);
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("macAddress", mac);
            deviceJson.put("deviceID", deviceid);
            deviceJson.put("version", version);
            deviceJson.put("mcuHardVersion", mcu_version);
            deviceJson.put("mucSoftVersion", firmware_version);
            deviceJson.put("productID", product_id);
            deviceJson.put("accesskey", access_key);
            deviceJson.put("deviceName", deviceName);
            obj.put("device", deviceJson);
//            XDevice xdevice = XlinkAgent.JsonToDevice(obj);
            device.setxDevice(obj.toString());
            device.setDeviceType(devicetype);
            device.setDeviceName(deviceName);
            device.setDeviceMac(mac);
            device.setAccessKey(access_key + "");
            device.setDate(new Date());
            device.setDeviceId(deviceid);
            device.setProductId(product_id);
            DeviceManage.getInstance().addDevice(device);
            return device;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * 获取报警信息
     *
     * @param loc_key 报警loc_key
     * @return
     */
    public static String GetAlarm(String loc_key) {
        int type;
        String Alarm = "";
        String loc_keya[] = loc_key.split("_");
        try {
            type = Integer.parseInt(loc_keya[2]);
            Alarm = loc_keya[0] + loc_keya[1];
        } catch (Exception e) {
            Alarm = loc_keya[0];
        }
        return Alarm;
    }

    /**
     * 报警数据返回 转换成type
     *
     * @param loc_key 报警loc_key
     * @return
     */
    public static int Gettype(String loc_key) {
        int type = 0;
        String loc_keya[] = loc_key.split("_");
        try {
            type = Integer.parseInt(loc_keya[2]);
        } catch (Exception e) {
            type = Integer.parseInt(loc_keya[1]);
        }
        return type;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmptyString(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断List是否为空
     *
     * @param list 字符串
     * @return 是否为空
     */
    public static boolean isEmptyList(List list) {
        return list == null && list.isEmpty();
    }


    public static IntentFilter regFilter() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.BROADCAST_RECVPIPE);
        myIntentFilter.addAction(Constants.BROADCAST_DEVICE_CHANGED);
        myIntentFilter.addAction(Constants.BROADCAST_DEVICE_SYNC);
        myIntentFilter.addAction(Constants.BROADCAST_RECVPIPE_SYNC);
        myIntentFilter.addAction(Constants.BROADCAST_CONNENCT_SUCCESS);
        myIntentFilter.addAction(Constants.BROADCAST_CONNENCT_FAIL);
        myIntentFilter.addAction(Constants.BROADCAST_SEND_OVERTIME);
        myIntentFilter.addAction(Constants.BROADCAST_SEND_SUCCESS);
        return myIntentFilter;
    }

    /**
     * 判断程序是否处于后台云行
     *
     * @param context
     * @return
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;

    }

    /**
     * 后台推送消息
     * <p>
     * //     * @param deviceName 设备名字
     * //     * @param deviceType 设备类型
     *
     * @param deviceMac 设备MAC
     * @param zigbeeMac Zigbee设备MAC
     * @param Ticker    标题
     * @param Title     头部
     * @param Content   内容
     * @param soundUri  音乐URL
     * @param icon      头像
     * @param ID        消息ID
     */
    private static void backgroundDisplay(String deviceMac, String zigbeeMac, String Ticker, long When, String Title, String Content, Uri soundUri, int icon, int ID) {
        Intent intent = new Intent(MyApplication.getMyApplication(), MainActivity.class);
//        intent.putExtra(Constants.DEVICE_NAME, deviceName);
//        intent.putExtra(Constants.DEVICE_TYPES, deviceType);
        intent.putExtra(Constants.GATEWAY_MAC, deviceMac);
        intent.putExtra(Constants.ZIGBEE_MAC, zigbeeMac);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getMyApplication(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long[] vibrate = {100, 1000, 1000 * 30, 1000};
        Notification notification = new Notification.Builder(MyApplication.getMyApplication())//实例化Builder
                .setTicker(Ticker)//在状态栏显示的标题
                .setWhen(When)//设置显示的时间，默认就是currentTimeMillis()
                .setContentTitle(Title)//设置标题
                .setContentText(Content)//设置内容
                .setSound(soundUri)
                .setVibrate(vibrate)
                .setLights(0x00FF00, 300, 1000)
                .setSmallIcon(icon) //设置图标
                .setWhen(System.currentTimeMillis()) //发送时间
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)//设置是否自动按下过后取消
                .setOngoing(false)//设置为true时就不能删除  除非使用notificationManager.cancel(1)方法
                .build();//创建Notification
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_INSISTENT;
        NotificationManager nManager = (NotificationManager) MyApplication.getMyApplication().getSystemService(MyApplication.getMyApplication().NOTIFICATION_SERVICE);
        nManager.notify(ID, notification);// id是应用中通知的唯一标识
    }

    /**
     * 前台显示
     * <p>
     * //     * @param deviceName 设备名字
     * //     * @param deviceType 设备类型
     *
     * @param deviceMac 设备MAC
     * @param zigbeeMac Zigbee设备MAC
     * @param Title     头部
     * @param Content   内容
     * @param icon      头像
     */
    private static void broughtDisplay(final String deviceMac, final String zigbeeMac, String Title, String Content, int icon) {

        MyApplication.getLogger().i("activity:" + MyApplication.getMyApplication().getCurrentActivity()
        );
        Alerter.create(MyApplication.getMyApplication().getCurrentActivity())
                .setTitle(Title)
                .setText(Content)
                .setDuration(10000)
                .setIcon(icon)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MyApplication.getMyApplication(), MainActivity.class);
//                        intent.putExtra(Constants.DEVICE_NAME, deviceName);
//                        intent.putExtra(Constants.DEVICE_TYPES, deviceType);
                        intent.putExtra(Constants.GATEWAY_MAC, deviceMac);
                        intent.putExtra(Constants.ZIGBEE_MAC, zigbeeMac);
                        MyApplication.getMyApplication().getCurrentActivity().startActivity(intent);
                    }
                })
                .show();
    }

    /**
     * 反映射获取bodyLocKey
     *
     * @param bodyLocKey
     * @return
     */
    public static int getBodyString(String bodyLocKey) {
        try {
            Field field = R.string.class.getField(bodyLocKey);
            int i = field.getInt(new R.drawable());
            return i;
        } catch (Exception e) {
            MyApplication.getLogger().e(e.toString());
            return R.string.unknow;
        }
    }

    public static int getDeviceIcon() {
        return R.mipmap.ic_launcher;
    }

    /**
     * 显示报警消息
     *
     * @param eventNotify
     */
    public static void showAlarm(EventNotify eventNotify) {
        byte[] new_bts = Arrays.copyOfRange(eventNotify.notifyData, 2,
                eventNotify.notifyData.length);
        try {
            String res = new String(new_bts, "UTF-8");
            MyApplication.getLogger().json(res);
            Gson gson = new Gson();
            try {
                EventNotifyData eventNotifyData = gson.fromJson(res, EventNotifyData.class);
                Notifications notification = gson.fromJson(eventNotifyData.getValue(), Notifications.class);
                String Title = notification.getNotification().getTitle();
                List<String> bodyLocArgs = notification.getNotification().getBody_loc_args();
                String Sound = notification.getNotification().getSound();
                String bodyLocKey = notification.getNotification().getBody_loc_key();
                XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(eventNotify.formId);
                Uri soundUri;
                if (Sound.equals("alarm.mp3")) {
                    soundUri = Uri.parse("android.resource://" + MyApplication.getMyApplication().getPackageName() + "/" + R.raw.alarm);
                } else if (Sound.equals("alarm_119.mp3")) {
                    soundUri = Uri.parse("android.resource://" + MyApplication.getMyApplication().getPackageName() + "/" + R.raw.alarm_119);
                } else {
                    soundUri = Uri.parse("android.resource://" + MyApplication.getMyApplication().getPackageName() + "/" + R.raw.message);
                }
                String zigbeeMac = "";
                String sAgeFormatString = MyApplication.getMyApplication().getResources().getString(getBodyString(bodyLocKey));
                String Content = String.format(sAgeFormatString, bodyLocArgs.get(0
                ));

                if (xlinkDevice.getDeviceType() == Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY) {
                    zigbeeMac = notification.getZigbeeMac();
                }
                if (isApplicationBroughtToBackground(MyApplication.getMyApplication().getApplicationContext())) {
                    try {
                        backgroundDisplay(xlinkDevice.getDeviceMac(), zigbeeMac, Title + MyApplication.getMyApplication().getString(R.string.alarm), Time.stringToLong(bodyLocArgs.get(0), "yyyy-MM-dd HH:mm:ss"), Title, Content, soundUri, getDeviceIcon(), eventNotify.messageId);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    broughtDisplay(xlinkDevice.getDeviceMac(), zigbeeMac, Title, Content, getDeviceIcon());
                }

            } catch (Exception e) {
            }
            try {
                EventIsOnline eventIsOnline = gson.fromJson(res, EventIsOnline.class);
            } catch (Exception e) {

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void saveDataDevice(String Title, List<String> bodyLocArgs, String bodyLocKey, XlinkDevice xlinkDevice, String zigbeeMac, String ID) {
//        DataDevice datadevice = new DataDevice();
//        datadevice.setDeviceMac(xlinkDevice.getDeviceMac());
//        datadevice.setMessageID(ID);
//        datadevice.setMessageType(listsize.get(i).getType());
//        datadevice.setNotifyType(listsize.get(i).getNotify_type());
//        datadevice.setPush(listsize.get(i).isIs_push());
//        datadevice.setRead(listsize.get(i).isIs_read());
//        datadevice.setAlertName(listsize.get(i).getAlert_name());
//        datadevice.setAlertValue(listsize.get(i).getAlert_value());
//        datadevice.setCreateDate(listsize.get(i).getCreate_date());
//        datadevice.setUserid(MyApplication.getMyApplication().getAppid() + "");
//        datadevice.setUserName(userName);
//        datadevice.setXlinkDevice(xlinkDevice);
//        datadevice.setDeviceId(listsize.get(i).getFrom() + "");
//        datadevice.setDeviceMac(xlinkDevice.getDeviceMac());
//        datadevice.setActionName(messagesconte.getNotification().getTitle());
//        datadevice.setDate(date);
//        datadevice.setYear(Time.dateToString(date, "yyyy"));
//        datadevice.setMonth(Time.dateToString(date, "MM"));
//        datadevice.setDay(Time.dateToString(date, "dd"));
//        datadevice.setHH(Time.dateToString(date, "HH"));
//        datadevice.setMm(Time.dateToString(date, "mm"));
//        datadevice.setSs(Time.dateToString(date, "ss"));
//        datadevice.setBodyLocKey(loc_key);
//        datadevice.setSubType(MyUtil.Gettype(loc_key) + "");

    }

    /**
     * 场景动作
     *
     * @param context
     * @param loadaction 执行动作
     * @return
     */
    public static int Sceneactivon(Context context, String loadaction) {
        if (loadaction.equals(context.getResources().getString(R.string.Disarm))) {
            return 0xFF00;
        } else if (loadaction.equals(context.getResources().getString(R.string.AtHome))) {
            return 0xFF02;
        } else if (loadaction.equals(context.getResources().getString(R.string.OutAlert))) {
            return 0xFF01;
        } else if (loadaction.equals(context.getResources().getString(R.string.Arming))) {
            return 0xFF01;
        } else if (loadaction.equals(context.getResources().getString(R.string.poweroff))) {
            return 0xFF00;
        } else if (loadaction.equals(context.getResources().getString(R.string.poweron))) {
            return 0xFF01;
        } else if (loadaction.equals(context.getResources().getString(R.string.usboff))) {
            return 0x00FF;
        } else if (loadaction.equals(context.getResources().getString(R.string.usbon))) {
            return 0x01FF;
        } else if (loadaction.equals(context.getResources().getString(R.string.PNUN))) {
            return 0x0101;
        } else if (loadaction.equals(context.getResources().getString(R.string.PNUF))) {
            return 0x0001;
        } else if (loadaction.equals(context.getResources().getString(R.string.PFUN))) {
            return 0x0100;
        } else if (loadaction.equals(context.getResources().getString(R.string.PFUF))) {
            return 0x0000;
        } else if (loadaction.equals(context.getResources().getString(R.string.turned_on))) {
            return 0xFE01;
        } else if (loadaction.equals(context.getResources().getString(R.string.turned_off))) {
            return 0xFF00;
        } else {
            try {
                String[] s = loadaction.split(":");
                if (s[0].equals(context.getResources().getString(R.string.Brightness))) {
                    int a = Integer.parseInt(s[1]);
                    return (a * 255 / 100) * 255 + 0x01;
                } else {
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            }
        }
//        else if (loadaction.equals(context.getResources().getString(R.string.Out_alert))) {
//
//        } else if (loadaction.equals(context.getResources().getString(R.string.Disarm_alarm))) {
//
//        }
    }

    /**
     * 获取场景执行动作
     *
     * @param context
     * @param type       设备类型
     * @param loadaction 执行动作
     * @return
     */
    public static String getSceneactivon(Context context, int type, int loadaction) {
        if (type == Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY) {
            if (loadaction == 0xff00) {
                return context.getResources().getString(R.string.Disarm);
            } else if (loadaction == 0xff01) {
                return context.getResources().getString(R.string.OutAlert);
            } else if (loadaction == 0xff02) {
                return context.getResources().getString(R.string.AtHome);
            } else {
                return context.getResources().getString(R.string.Disarm);
            }
        } else if (type == Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN || type == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN) {
            if (loadaction == 0x0000) {
                return context.getResources().getString(R.string.PFUF);
            } else if (loadaction == 0x0100) {
                return context.getResources().getString(R.string.PFUN);
            } else if (loadaction == 0x0001) {
                return context.getResources().getString(R.string.PNUF);
            } else if (loadaction == 0x0101) {
                return context.getResources().getString(R.string.PNUN);
            } else if (loadaction == 0xff00) {
                return context.getResources().getString(R.string.poweroff);
            } else if (loadaction == 0xff01) {
                return context.getResources().getString(R.string.poweron);
            } else if (loadaction == 0x00ff) {
                return context.getResources().getString(R.string.usboff);
            } else if (loadaction == 0x01ff) {
                return context.getResources().getString(R.string.usbon);
            } else {
                return context.getResources().getString(R.string.poweron);
            }
        } else if (type == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN) {
            if (loadaction == 0xFF00) {
                return context.getResources().getString(R.string.poweroff);
            } else if (loadaction == 0xFF01) {
                return context.getResources().getString(R.string.poweron);
            } else {
                return context.getResources().getString(R.string.poweron);
            }
        } else if (type == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB) {
            if (loadaction == 0xFE01) {
                return context.getResources().getString(R.string.turned_on);
            } else if (loadaction == 0xFF00) {
                return context.getResources().getString(R.string.turned_off);
            } else {
                loadaction = (((loadaction - 0x01) / 255) * 100) / 255;
                return context.getResources().getString(R.string.turned_on)
                        + " | " + context.getResources().getString(R.string.Brightness)
                        + ":" + loadaction;
            }
        } else if (type == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS || type == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR) {
            if (loadaction == 0xFF00) {
                return context.getResources().getString(R.string.Disarm);
            } else if (loadaction == 0xFF01) {
                return context.getResources().getString(R.string.Arming);
            } else {
                return context.getResources().getString(R.string.Disarm);
            }
        } else {
            return context.getResources().getString(R.string.Disarm);
        }
    }
}
