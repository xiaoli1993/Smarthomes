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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.activity.MainActivity;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.modle.EventIsOnline;
import com.nuowei.smarthome.modle.EventNotifyData;
import com.nuowei.smarthome.modle.Notifications;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.utils.Utils;
import com.tapadoo.alerter.Alerter;
import com.wevey.selector.dialog.DialogInterface;
import com.wevey.selector.dialog.NormalAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.xlink.wifi.sdk.bean.EventNotify;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/21 08:53
 * @Description :
 */
public class MyUtil {
    public static int getColor(Context context, int res) {
        Resources r = context.getResources();
        return r.getColor(res);
    }

    public static float getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }


    public static int px(float dipValue) {
        Resources r = Resources.getSystem();
        final float scale = r.getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    //获取显示版本
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取版本信息
    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static StateListDrawable getRoundSelectorDrawable(int alpha, int color, int radir) {
        Drawable pressDrawable = getRoundDrawalbe(alpha, color, radir);
        Drawable normalDrawable = getRoundDrawalbe(color, radir);
        return getStateListDrawable(pressDrawable, normalDrawable);
    }

    //获取带透明度的圆角矩形
    public static Drawable getRoundDrawalbe(int alpha, int color, int radir) {
        int normalColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        Drawable normalDrawable = getRoundDrawalbe(normalColor, radir);
        return normalDrawable;
    }


    //根据颜色获取圆角矩形
    public static Drawable getRoundDrawalbe(int color, int radir) {
        radir = px(radir);
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{color, color});
        drawable.setCornerRadius(radir);
        return drawable;
    }

    public static StateListDrawable getStateListDrawable(Drawable pressDrawable, Drawable normalDrawable) {
        int pressed = android.R.attr.state_pressed;
        int select = android.R.attr.state_selected;
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{pressed}, pressDrawable);
        drawable.addState(new int[]{select}, pressDrawable);
        drawable.addState(new int[]{}, normalDrawable);
        return drawable;
    }

    public static StateListDrawable getSelectDrawable(int color) {
        int select = android.R.attr.state_selected;
        StateListDrawable drawable = new StateListDrawable();

        GradientDrawable drawable2 = new GradientDrawable();
        drawable2.setShape(GradientDrawable.OVAL);
        drawable2.setColor(color);

        drawable.addState(new int[]{select}, drawable2);
        drawable.addState(new int[]{}, null);

        return drawable;
    }

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
        myIntentFilter.addAction(Constants.BROADCAST_SEND_SUCCESS);
        myIntentFilter.addAction(Constants.CHANGE_URL);
        return myIntentFilter;
    }

    public static int getIconImage(int deviceid) {
        switch (deviceid) {
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:
                return R.drawable.device_light;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                return R.drawable.device_door;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                return R.drawable.device_water;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                return R.drawable.device_pir;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                return R.drawable.device_smoke;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                return R.drawable.device_thp;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                return R.drawable.device_gas;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                return R.drawable.device_co;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                return R.drawable.device_sos;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                return R.drawable.device_sw;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                return R.drawable.device_plug;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                return R.drawable.device_plug;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY:
                return R.drawable.device_gw;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN:
                return R.drawable.device_plug;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN:
                return R.drawable.device_plug;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_AIR:
                return R.drawable.device_ipc;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_IPC:
                return R.drawable.device_ipc;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_RC:
                return R.drawable.device_ipc;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_GAS:
                return R.drawable.device_ipc;
            default:
                return R.drawable.device_ipc;
        }
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
                String Content = String.format(sAgeFormatString, bodyLocArgs.get(0));

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

    private static final int Period_Sign = 0x80;
    private static final int Monday = 0x01;
    private static final int Tuesday = 0x02;
    private static final int Wednesday = 0x04;
    private static final int Thursday = 0x08;
    private static final int Friday = 0x10;
    private static final int Saturday = 0x20;
    private static final int Sunday = 0x40;
    private static final int Everyday = 0x7f;

    public static String getWkString(Context mContext, int wk) {
        String WkString = "";
        int i = 0x01;
        while (i < 0x80) {
            int s = wk & i;
            switch (s) {
                case Monday:
                    WkString = mContext.getString(R.string.Monday);
                    break;
                case Tuesday:
                    WkString = WkString + " " + mContext.getString(R.string.Tuesday);
                    break;
                case Wednesday:
                    WkString = WkString + " " + mContext.getString(R.string.Wednesday);
                    break;
                case Thursday:
                    WkString = WkString + " " + mContext.getString(R.string.Thursday);
                    break;
                case Friday:
                    WkString = WkString + " " + mContext.getString(R.string.Friday);
                    break;
                case Saturday:
                    WkString = WkString + " " + mContext.getString(R.string.Saturday);
                    break;
                case Sunday:
                    WkString = WkString + " " + mContext.getString(R.string.Sunday);
                    break;
            }
            i = i << 1;
        }
        if (WkString.equals(mContext.getString(R.string.Monday)
                + " " + mContext.getString(R.string.Tuesday)
                + " " + mContext.getString(R.string.Wednesday)
                + " " + mContext.getString(R.string.Thursday)
                + " " + mContext.getString(R.string.Friday)
                + " " + mContext.getString(R.string.Saturday)
                + " " + mContext.getString(R.string.Sunday))) {
            WkString = mContext.getString(R.string.everyday);
        } else if (WkString.equals(mContext.getString(R.string.Monday)
                + " " + mContext.getString(R.string.Tuesday)
                + " " + mContext.getString(R.string.Wednesday)
                + " " + mContext.getString(R.string.Thursday)
                + " " + mContext.getString(R.string.Friday))) {
            WkString = mContext.getString(R.string.Daily);
        } else if (WkString.equals(" " + mContext.getString(R.string.Saturday)
                + " " + mContext.getString(R.string.Sunday))) {
            WkString = mContext.getString(R.string.Weekdays);
        }
        return WkString;
    }

    public static int getImageDiary(Context mContext, String deviceType, String Alarm) {
        MyApplication.getLogger().i("输出字符:" + Alarm + "Alarm:" + mContext.getString(R.string.HOME_ARM_0));
        int image = 0;
        switch (deviceType) {
            case "0":
                if (Alarm.equals(mContext.getString(R.string.ARM_0))) {
                    image = R.drawable.gw_away_pressed;
                    MyApplication.getLogger().i("输出字符:外出布防");
                } else if (Alarm.equals(mContext.getString(R.string.DISARM_0))) {
                    image = R.drawable.gw_disarm_pressed;
                    MyApplication.getLogger().i("输出字符:撤防");
                } else if (Alarm.equals(mContext.getString(R.string.HOME_ARM_0))) {
                    image = R.drawable.gw_home_pressed;
                    MyApplication.getLogger().i("输出字符:在家布防");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB + "":
                if (Alarm.equals(mContext.getString(R.string.LIGHT_OFF_1))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:27");
                } else if (Alarm.equals(mContext.getString(R.string.LIGHT_ON_1))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:28");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS + "":
                if (Alarm.equals(mContext.getString(R.string.ALRM_RESUME_17))) {
                    image = R.drawable.device_state_close;
                    MyApplication.getLogger().i("输出字符:关");
                } else if (Alarm.equals(mContext.getString(R.string.ALRMING_17))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:开");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_ALARM_17))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:31");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_DEVALARM_17))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:33");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_DEVRESUME_17))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:35");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_RESUME_17))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:37");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER + "":
                if (Alarm.equals(mContext.getString(R.string.ALRM_RESUME_18))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:1");
                } else if (Alarm.equals(mContext.getString(R.string.ALRMING_18))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:2");
                }else if (Alarm.equals(mContext.getString(R.string.TAMPER_DEVALARM_18))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:88");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_DEVRESUME_18))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:89");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_ALARM_18))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:90");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_RESUME_18))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:91");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR + "":
                if (Alarm.equals(mContext.getString(R.string.ALRM_RESUME_19))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:3");
                } else if (Alarm.equals(mContext.getString(R.string.ALRMING_19))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:4");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_DEVALARM_19))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:34");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_DEVRESUME_19))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:36");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_ALARM_19))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:32");
                } else if (Alarm.equals(mContext.getString(R.string.TAMPER_RESUME_19))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:38");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE + "":
                if (Alarm.equals(mContext.getString(R.string.ALRM_RESUME_20))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:5");
                } else if (Alarm.equals(mContext.getString(R.string.ALRMING_20))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:6");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP + "":
                if (Alarm.equals(mContext.getString(R.string.HUM_ALARM_21))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:18");
                } else if (Alarm.equals(mContext.getString(R.string.TEMP_ALARM_21))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:19");
                } else if (Alarm.equals(mContext.getString(R.string.TEMP_HUM_ALARM_21))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:20");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS + "":
                if (Alarm.equals(mContext.getString(R.string.ALRM_RESUME_22))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:7");
                } else if (Alarm.equals(mContext.getString(R.string.ALRMING_22))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:8");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO + "":
                if (Alarm.equals(mContext.getString(R.string.ALRM_RESUME_24))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:9");
                } else if (Alarm.equals(mContext.getString(R.string.ALRMING_24))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:10");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS + "":
                if (Alarm.equals(mContext.getString(R.string.ALRMING_49))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:13");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW + "":
                if (Alarm.equals(mContext.getString(R.string.ALRMING_50))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:14");
                } else if (Alarm.equals(mContext.getString(R.string.ARM_50))) {
                    image = R.drawable.gw_away_pressed;
                    MyApplication.getLogger().i("输出字符:15");
                } else if (Alarm.equals(mContext.getString(R.string.DISARM_50))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:16");
                } else if (Alarm.equals(mContext.getString(R.string.HOMEARM_50))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:17");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN + "":
                if (Alarm.equals(mContext.getString(R.string.ALRM_RESUME_67))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:11");
                } else if (Alarm.equals(mContext.getString(R.string.ALRMING_67))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:12");
                } else if (Alarm.equals(mContext.getString(R.string.RELAY_OFF_67))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:29");
                } else if (Alarm.equals(mContext.getString(R.string.RELAY_ON_67))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:30");
                } else if (Alarm.equals(mContext.getString(R.string.USB_OFF_67))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:39");
                } else if (Alarm.equals(mContext.getString(R.string.USB_ON_67))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:40");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN + "":
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY + "":
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN + "":
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN + "":
                if (Alarm.equals(mContext.getString(R.string.Power_1042))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:21");
                } else if (Alarm.equals(mContext.getString(R.string.Power_Current_1042))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:22");
                } else if (Alarm.equals(mContext.getString(R.string.Current_1042))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:23");
                } else if (Alarm.equals(mContext.getString(R.string.Power_Currentvoltage_1042))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:24");
                } else if (Alarm.equals(mContext.getString(R.string.Power_Voltage_1042))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:25");
                } else if (Alarm.equals(mContext.getString(R.string.Current_Voltage_1042))) {
                    image = R.drawable.device_state_open;
                    MyApplication.getLogger().i("输出字符:26");
                }
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_AIR + "":
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_IPC + "":
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_RC + "":
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_GAS + "":
                break;
        }
        return image;
    }

    public static int getDeviceToImage(int deviceType) {
        int image = 0;
        switch (deviceType) {
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:
                image = R.drawable.device_light;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                image = R.drawable.device_door;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                image = R.drawable.device_water;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                image = R.drawable.device_pir;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                image = R.drawable.device_smoke;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                image = R.drawable.device_thp;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                image = R.drawable.device_gas;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                image = R.drawable.device_co;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                image = R.drawable.device_sos;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                image = R.drawable.device_sw;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                image = R.drawable.device_plug;
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                image = R.drawable.device_plug;
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_RC:
                image = R.drawable.home_security;
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY:
                image = R.drawable.device_gw;
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN:
                image = R.drawable.device_plug;
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN:
                image = R.drawable.home_electric;
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_AIR:
                image = R.drawable.home_light;
                break;
            default:
                image = R.drawable.home_device;
                break;
        }
        return image;
    }

    /**
     * 根据URL获取图片 Bitmap
     *
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        Bitmap bitmap = null;
        URL myUrl;
        try {
            myUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //返回圆形bitmap
        return bitmap;
    }

    public static String clientNameToEmail(String ClientName) {
        if (Utils.isEmial(ClientName)) {
            String[] b = ClientName.split("@");
            return b[0] + Constants.BEIMAEmail;
        } else {
            return ClientName + Constants.BEIMAEmail;
        }
    }

    public static void sleepThread(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void showNoDialog(Context context) {
        new NormalAlertDialog.Builder(context).setHeight(0.23f)  //屏幕高度*0.23
                .setWidth(0.65f)  //屏幕宽度*0.65
                .setTitleVisible(true).setTitleText(context.getResources().getString(R.string.Tips))
                .setTitleTextColor(R.color.text_title)
                .setContentText(context.getString(R.string.NoGongNeng))
                .setContentTextColor(R.color.text_color)
                .setSingleMode(true).setSingleButtonText(context.getResources().getString(R.string.Close))
                .setSingleButtonTextColor(R.color.colorRed)
                .setCanceledOnTouchOutside(true)
                .setSingleListener(new DialogInterface.OnSingleClickListener<NormalAlertDialog>() {
                    @Override
                    public void clickSingleButton(NormalAlertDialog dialog, View view) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    /**
     * 添加IPC列表
     */
    public void addIPC() {
//        List<Contact> contact = FList.getInstance().list();
//        for (int i = 0; i < contact.size(); i++) {
//            JSONObject obj = new JSONObject();
//            try {
//                obj.put("protocol", 1);
//                JSONObject deviceJson = new JSONObject();
//                String macc = "ACCF2" + contact.get(i).contactId;
//                deviceJson.put("macAddress", macc);
//                deviceJson.put("deviceName", contact.get(i).contactName);
//                deviceJson.put("deviceID", contact.get(i).contactId);
//                deviceJson.put("version", 99);
//                deviceJson.put("mcuHardVersion", 0);
//                deviceJson.put("mucSoftVersion", 0);
//                deviceJson.put("productID", "9fc2c50412264ce29dae547ff08b941e");
//                deviceJson.put("accesskey", contact.get(i).contactPassword);
//                deviceJson.put("deviceIP", contact.get(i).ipadressAddress);
//                obj.put("device", deviceJson);
//                MyApplication.getLogger().json(obj.toString());
////                XDevice xdevice = XlinkAgent.JsonToDevice(obj);
//                XlinkDevice xlinkDevice = new XlinkDevice();
//                xlinkDevice.setxDevice(obj.toString());
//                xlinkDevice.setAccessKey(contact.get(i).contactPassword);
//                xlinkDevice.setDeviceType(Constants.DEVICE_TYPE.DEVICE_WIFI_IPC + contact.get(i).contactType);
//                xlinkDevice.setDeviceName(contact.get(i).contactName);
//                xlinkDevice.setDeviceState(contact.get(i).onLineState);
//                DeviceManage.getInstance().addDevice(xlinkDevice);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
