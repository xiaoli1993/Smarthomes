package com.nuowei.smarthome;/**
 * Created by xiaoli on 2017/1/9.
 */

import android.app.Application;
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

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import im.fir.sdk.FIR;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/1/9 13:33
 * @Description :application
 */
public class MyApplication extends LitePalApplication {

    private static MyApplication myApplication;
    private static Logger sLogger;
    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        initSDK();
    }

    /**
     * 初始化sdk
     */
    private void initSDK() {
        //log打印
        List<String> logLevels = new ArrayList<>();
        logLevels.add(LogLevel.ERROR);
        logLevels.add(LogLevel.WTF);
        sLogger = Logger.Builder.newBuilder(getApplicationContext(), "jlog")
                /* 下面的属性都是默认值，你可以根据需求决定是否修改它们. */
                .setDebug(true)
                .setWriteToFile(false)
                .setLogDir("jlog")
                .setLogPrefix("")
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

}
