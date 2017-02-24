package com.nuowei.smarthome.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.modle.MainDatas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * 将Map转化为Json
     *
     * @param map
     * @return String
     */
    public static <T> String listToJson(List<HashMap<String, T>> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }

    public static <T> List<HashMap<String, T>> jsonToList(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<HashMap<String, T>>>() {}.getType();
        List<HashMap<String, T>> beanOnes = gson.fromJson(jsonString, type);
        return beanOnes;
    }

    /**
     * 将JSONObjec对象转换成Map-List集合
     *
     * @param json
     * @return
     */
    public static Map<String, Object> toMap(JsonObject json) {
        Map<String, Object> map = new HashMap<String, Object>();
        Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
        for (Iterator<Map.Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ) {
            Map.Entry<String, JsonElement> entry = iter.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof JsonArray)
                map.put((String) key, toList((JsonArray) value));
            else if (value instanceof JsonObject)
                map.put((String) key, toMap((JsonObject) value));
            else
                map.put((String) key, value);
        }
        return map;
    }

    /**
     * 将JSONArray对象转换成List集合
     *
     * @param json
     * @return
     */
    public static List<Object> toList(JsonArray json) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < json.size(); i++) {
            Object value = json.get(i);
            if (value instanceof JsonArray) {
                list.add(toList((JsonArray) value));
            } else if (value instanceof JsonObject) {
                list.add(toMap((JsonObject) value));
            } else {
                list.add(value);
            }
        }
        return list;
    }

}
