package com.nuowei.smarthome;/**
 * Created by xiaoli on 2017/1/9.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.avos.avoscloud.AVOSCloud;
import com.jiongbull.jlog.Logger;
import com.jiongbull.jlog.constant.LogLevel;
import com.jiongbull.jlog.constant.LogSegment;
import com.jiongbull.jlog.util.TimeUtils;
import com.nuowei.smarthome.util.Cockroach;
import com.nuowei.smarthome.util.SharePreferenceUtil;
import com.orhanobut.hawk.Hawk;
import com.squareup.leakcanary.LeakCanary;
import com.taobao.hotfix.HotFixManager;
import com.taobao.hotfix.PatchLoadStatusListener;
import com.taobao.hotfix.util.PatchStatusCode;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import im.fir.sdk.FIR;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.bean.EventNotify;
import io.xlink.wifi.sdk.listener.XlinkNetListener;
import okhttp3.OkHttpClient;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/1/9 13:33
 * @Description :application
 */
public class MyApplication extends LitePalApplication implements XlinkNetListener {

    private static MyApplication myApplication;
    private static Logger sLogger;
    private SQLiteDatabase db;
    private static Handler mainHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initSDK();
        initXlinkSDK();
    }

    private void initXlinkSDK() {
        // 初始化sdk
        XlinkAgent.init(this);
        XlinkAgent.setCMServer("io.heiman.cn", 23778);//正式平台地址
        XlinkAgent.getInstance().addXlinkListener(this);
        //优先内网连接(谨慎使用,如果优先内网,则外网会在内网连接成功或者失败,或者超时后再进行连接,可能会比较慢)
        XlinkAgent.getInstance().setPreInnerServiceMode(true);
        initHandler();
    }

    public static void initHandler() {
        mainHandler = new Handler();
    }

    /**
     * 执行在主线程任务
     *
     * @param runnable
     */
    public static void postToMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    /**
     * 初始化sdk
     */
    private void initSDK() {

        //OKHTTP初始化
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    //.addInterceptor(new LoggerInterceptor("TAG"))
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .addInterceptor(new LoggerInterceptor("Smart home"))
                    //其他配置
                    .build();
            OkHttpUtils.initClient(okHttpClient);
        } catch (Exception e) {
            e.printStackTrace();
        }



        //log打印
        List<String> logLevels = new ArrayList<>();
        logLevels.add(LogLevel.ERROR);
        logLevels.add(LogLevel.WTF);
        sLogger = Logger.Builder.newBuilder(getApplicationContext(), "nuoweiLog")
                /* 下面的属性都是默认值，你可以根据需求决定是否修改它们. */
                .setDebug(true)
                .setWriteToFile(false)
                .setLogDir(getString(R.string.app_name))
                .setLogPrefix(getString(R.string.app_name)+ File.separator+"sd")
                .setLogSegment(LogSegment.TWELVE_HOURS)
                .setLogLevelsForFile(logLevels)
                .setZoneOffset(TimeUtils.ZoneOffset.P0800)
                .setTimeFormat("yyyy-MM-dd HH:mm:ss")
                .setPackagedLevel(0)
                .setStorage(null)
                .build();
        //异常处理类
        Cockroach.install(new Cockroach.ExceptionHandler() {

            // handlerException内部建议手动try{  你的异常处理逻辑  }catch(Throwable e){ } ，以防handlerException内部再次抛出异常，导致循环调用handlerException

            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getLogger().d(thread + "\n" + throwable.toString());
                            throwable.printStackTrace();
//                        throw new RuntimeException("..."+(i++));
                        } catch (Throwable e) {

                        }
                    }
                });
            }
        });
        myApplication = this;

        //以下是内存泄露检查代码
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        LeakCanary.install(this);
        // Normal app init code..

        //bug 收集器
        FIR.init(this);
        //数据库初始化
        LitePal.initialize(this);
        //友盟sdk初始化
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,
                "587308b9677baa1b9c00095f", "Umeng", MobclickAgent.EScenarioType.E_UM_NORMAL, true));
        //sdk热补丁
        HotFixManager.getInstance().setContext(this)
                .setAppVersion("1.0")
                .setAppId("85748-1")
                .setAesKey(null)
                .setSupportHotpatch(true)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onload(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatusCode.CODE_SUCCESS_LOAD) {
                            // TODO: 10/24/16 表明补丁加载成功
                        } else if (code == PatchStatusCode.CODE_ERROR_NEEDRESTART) {
                            // TODO: 10/24/16 表明新补丁生效需要重启. 业务方可自行实现逻辑, 提示用户或者强制重启, 建议: 用户可以监听进入后台事件, 然后应用自杀
                        } else if (code == PatchStatusCode.CODE_ERROR_INNERENGINEFAIL) {
                            // 内部引擎加载异常, 推荐此时清空本地补丁, 但是不清空本地版本号, 防止失败补丁重复加载
                            //HotFixManager.getInstance().cleanPatches(false);
                        } else {
                            // TODO: 10/25/16 其它错误信息, 查看PatchStatusCode类说明
                        }
                    }
                }).initialize();


        //意见反馈模组
        AVOSCloud.initialize(this, "PUfaNAwuo1oW0qx7MRbNPVh8-gzGzoHsz", "1zVJl0bgigUQIUl2wVPleRDI");
        AVOSCloud.setDebugLogEnabled(true);

        //数据库加密保存
        Hawk.init(this).build();
        //数据库初始化
        db = Connector.getDatabase();
    }

    public static Logger getLogger() {
        return sLogger;
    }

    public static MyApplication getApp() {
        return myApplication;
    }

    // 单例模式，才能及时返回数据
    SharePreferenceUtil mSpUtil;
    public static final String PREFERENCE_NAME = "_sharedinfo";

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null) {
            String currentId = "";//UserName
            String sharedName = currentId + PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this, sharedName);
        }
        return mSpUtil;
    }


    // 当前的activity
    private Activity currentActivity;

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    // xlink 回调的onStart
    @Override
    public void onStart(int code) {
        // TODO Auto-generated method stub
        getLogger().e( "onStart code" + code);
        sendBroad(Constants.BROADCAST_ON_START, code);
    }

    // 回调登录xlink状态
    @Override
    public void onLogin(int code) {
        // TODO Auto-generated method stub
        getLogger().e( "login code" + code);
        sendBroad(Constants.BROADCAST_ON_LOGIN, code);
//        if (code == XlinkCode.SUCCEED) {
////            XlinkUtils.shortTips("云端网络已可用");
//        } else if (code == XlinkCode.CLOUD_CONNECT_NO_NETWORK
//                || XlinkUtils.isConnected()) {
//            // XlinkUtils.shortTips("网络不可用，请检查网络连接");
//
//        } else {
//            XlinkUtils.shortTips("连接到服务器失败，请检查网络连接");
//        }
    }

    @Override
    public void onLocalDisconnect(int code) {
        if (code == XlinkCode.LOCAL_SERVICE_KILL) {
            // 这里是xlink服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止应用/服务）
            // 永不结束的service
            // 除非调用 XlinkAgent.getInstance().stop（）;
            XlinkAgent.getInstance().start();
        }
//        XlinkUtils.shortTips("本地网络已经断开");
    }


    @Override
    public void onDisconnect(int code) {
        if (code == XlinkCode.CLOUD_SERVICE_KILL) {
            // 这里是服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止服务）
//            if (appid != 0 && !TextUtils.isEmpty(authKey)) {
//                XlinkAgent.getInstance().login(appid, authKey);
//            }
        }
//        XlinkUtils.shortTips("正在修复云端连接");
    }

    /**
     * 收到 局域网设备推送的pipe数据
     */
    @Override
    public void onRecvPipeData(short messageId,XDevice xdevice,  byte[] data) {
        // TODO Auto-generated method stub
        getLogger().e( "onRecvPipeData::device:" + xdevice.toString() + "data:"
                + data);
//        Device device = DeviceManage.getInstance().getDevice(
//                xdevice.getMacAddress());
//        if (device != null) {
//             //发送广播，那个activity需要该数据可以监听广播，并获取数据，然后进行响应的处理
//            //sendPipeBroad(Constant.BROADCAST_RECVPIPE, device, data);
//             TimerManage.getInstance().parseByte(device,data);
//        }
    }

    /**
     * 收到设备通过云端服务器推送的pipe数据
     */
    @Override
    public void onRecvPipeSyncData(short messageId,XDevice xdevice,  byte[] data) {
        // TODO Auto-generated method stub
        getLogger().e( "onRecvPipeSyncData::device:" + xdevice.toString() + "data:"
                + data);
//        Device device = DeviceManage.getInstance().getDevice(
//                xdevice.getMacAddress());
//        if (device != null) {
//            // 发送广播，那个activity需要该数据可以监听广播，并获取数据，然后进行响应的处理
//            // TimerManage.getInstance().parseByte(device,data);
//            sendPipeBroad(Constant.BROADCAST_RECVPIPE_SYNC, device, data);
//        }
    }

    /**
     */
    public void sendBroad(String action, int code) {
        Intent intent = new Intent(action);
        intent.putExtra(Constants.STATUS, code);
        MyApplication.this.sendBroadcast(intent);
    }

    /**
     */
//    public void sendPipeBroad(String action, Device device, byte[] data) {
//        Intent intent = new Intent(action);
//        intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
//        if (data != null) {
//            intent.putExtra(Constant.DATA, data);
//        }
//        MyApp.this.sendBroadcast(intent);
//    }

    /**
     * 设备状态改变：掉线/重连/在线
     */
    @Override
    public void onDeviceStateChanged(XDevice xdevice, int state) {
        // TODO Auto-generated method stub

        getLogger().e( "onDeviceStateChanged:" + xdevice.getMacAddress()
                + " state:" + state);
//        Device device = DeviceManage.getInstance().getDevice(
//                xdevice.getMacAddress());
//        if (device != null) {
//            device.setxDevice(xdevice);
//            Intent intent = new Intent(Constant.BROADCAST_DEVICE_CHANGED);
//            intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
//            intent.putExtra(Constant.STATUS, state);
//            MyApp.getApp().sendBroadcast(intent);
//        }

    }

    @Override
    public void onDataPointUpdate(XDevice xDevice, List<DataPoint> dataPionts, int channel) {
        getLogger().e("onDataPointUpdate:"+dataPionts.toString());

//        Device device = DeviceManage.getInstance().getDevice(xDevice.getMacAddress());
//        if (device != null) {
//            Intent intent = new Intent(Constant.BROADCAST_DATAPOINT_RECV);
//            intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
//            if (dataPionts != null) {
//                intent.putExtra(Constant.DATA, (Serializable) dataPionts);
//            }
//            MyApp.this.sendBroadcast(intent);
//        }
    }

    @Override
    public void onEventNotify(EventNotify eventNotify) {
        String str = "EventNotify{" +
                "notyfyFlags=" + eventNotify.notyfyFlags +
                ", formId=" + eventNotify.formId +
                ", messageId=" + eventNotify.messageId +
                ", messageType=" + eventNotify.messageType +
                ", notifyData=" +new String(eventNotify.notifyData) +
                '}';

//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyApp.getApp());
//
//        mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
//                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
//                .setDefaults(Notification.DEFAULT_SOUND)
//                .setOngoing(false)//不是正在进行的   true为正在进行  效果和.flag一样
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentText(str);
//        Notification notify = mBuilder.build();
//        Intent intent=new Intent(this, NotifyEventInfoActivity.class);
//        intent.putExtra(NotifyEventInfoActivity.NOTIFY_BUNDLE,eventNotify);
//        PendingIntent pendingIntent=PendingIntent.getActivities(getApplicationContext(),0,new Intent[]{intent},0);
//        notify.contentIntent=pendingIntent;
//
//        notify.flags = Notification.FLAG_AUTO_CANCEL;
//        NotificationManager mNotificationManager = (NotificationManager) MyApp.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(new Random().nextInt(), notify);
//        XlinkUtils.longTips(str);
    }
}
