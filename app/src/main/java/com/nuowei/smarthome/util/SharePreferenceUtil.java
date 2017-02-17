package com.nuowei.smarthome.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 首选项管理
 *
 * @author smile
 * @ClassName: SharePreferenceUtil
 * @Description: TODO
 * @date 2014-6-10 下午4:20:14
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
    private SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    private String SHARED_KEY_NOTIFY = "shared_key_notify";
    private String SHARED_KEY_VOICE = "shared_key_sound";
    private String SHARED_KEY_VIBRATE = "shared_key_vibrate";
    private String SHARED_KEY_CROSS = "shared_key_cross";
    private String SHARED_KEY_Automatic = "shared_key_automatic";

    // 是否允许推送通知
    public boolean isAllowPushNotify() {
        return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
    }

    public void setPushNotifyEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
        editor.commit();
    }

    // 允许声音
    public boolean isAllowVoice() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
    }

    public void setAllowVoiceEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_VOICE, isChecked);
        editor.commit();
    }

    // 允许震动
    public boolean isAllowVibrate() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
    }

    public void setAllowVibrateEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
        editor.commit();
    }

    // 允许横屏
    public boolean isSHARED_KEY_CROSS() {
        return mSharedPreferences.getBoolean(SHARED_KEY_CROSS, false);
    }

    public void setSHARED_KEY_CROSS(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_CROSS, isChecked);
        editor.commit();
    }

    public boolean isSHARED_KEY_Automatic() {
        return mSharedPreferences.getBoolean(SHARED_KEY_Automatic, true);
    }

    public void setSHARED_KEY_Automatic(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_Automatic, isChecked);
        editor.commit();
    }

    public static String getUserName(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        String UserName = sharedPreferences.getString("USER_NAME", "");
        return UserName;
    }
}
