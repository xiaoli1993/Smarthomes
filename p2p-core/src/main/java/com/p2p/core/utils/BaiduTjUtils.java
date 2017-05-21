package com.p2p.core.utils;

import android.content.Context;

import com.baidu.mobstat.StatService;

public class BaiduTjUtils {
    public static final String TJ_TIME_CONTROL="time_control";
    public static final String TJ_VIDEO_CONTROL="video_control";
    public static final String TJ_SECURITY_CONTROL="security_control";
    public static final String TJ_ALARM_CONTROL="alarm_control";
    public static final String TJ_RECORD_CONTROL="record_control";
    public static final String TJ_KEYBOARD="keyboard";
    public static final String TJ_SETVOICESTATE="setVoiceState";
    public static final String TJ_SCREENSHOT="screenShot";
    public static final String TJ_INTERCOM="intercom";

    /**
     * 百度统计自定义事件
     * @param context 上下文
     * @param event_id 事件id与网页上创建的事件id对应
     * @param label 自定义的标签
     */
    public static void onEvent(Context context, String event_id, String label) {
        StatService.onEvent(context, event_id, label, 1);
    }

    /**
     * 百度统计自定义时长开始
     * @param context
     * @param event_id
     * @param label
     */
    public static void onEventStart(Context context, String event_id, String label){
        StatService.onEventStart(context,event_id,label);
    }

    /**
     * 百度统计自定义时长结束
     * @param context
     * @param event_id
     * @param label
     */
    public static void onEventEnd(Context context, String event_id, String label){
        StatService.onEventEnd(context,event_id,label);
    }
}
