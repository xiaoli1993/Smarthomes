package com.jwkj;

import android.content.Intent;
import android.util.Log;

import com.jwkj.activity.AlarmWithPictrueActivity;
import com.jwkj.activity.SysMsgActivity;
import com.jwkj.data.AlarmRecord;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.Message;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SysMessage;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.MusicManger;
import com.jwkj.utils.Utils;
import com.nuowei.smarthome.MyApplication;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PInterface.ISetting;
import com.p2p.core.P2PValue;
import com.p2p.core.utils.MyUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingListener implements ISetting {
    String TAG = "SDK";
    private static boolean isAlarming = false;
    private static String MonitorDeviceID = "";

    public static void setAlarm(boolean isAlarm) {
        SettingListener.isAlarming = isAlarm;
    }

    public static void setMonitorID(String id) {
        SettingListener.MonitorDeviceID = id;
    }

    /* ***************************************************************
     * 检查密码 开始
     */
    @Override
    public void ACK_vRetCheckDevicePassword(int msgId, int result,
                                            String deviceId) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetCheckDevicePassword:" + result);
        // if(result==Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS){
        // FList.getInstance().setDefenceState(threeNum, state)
        // }
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_CHECK_PASSWORD);
        i.putExtra("result", result);
        i.putExtra("deviceId", deviceId);
        MyApplication.app.sendBroadcast(i);
    }

	/*
     * 检查密码 结束 ****************************************************************
	 */

    /* ***************************************************************
     * 获取设备各种设置回调 开始
     */
    @Override
    public void ACK_vRetGetNpcSettings(String contactId, int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetGetNpcSettings:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_GET_NPC_SETTINGS);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetDefenceStates(String contactId, int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetGetDefenceStates:" + result);
        Log.e("defence", "contactId=" + contactId + "result=" + result);
        if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
            FList.getInstance().setDefenceState(contactId,
                    Constants.DefenceState.DEFENCE_STATE_WARNING_NET);
            Intent i = new Intent();
            i.putExtra("state",
                    Constants.DefenceState.DEFENCE_STATE_WARNING_NET);
            i.putExtra("contactId", contactId);
            i.setAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
            MyApplication.app.sendBroadcast(i);
        } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
            FList.getInstance().setDefenceState(contactId,
                    Constants.DefenceState.DEFENCE_STATE_WARNING_PWD);
            Intent i = new Intent();
            i.putExtra("state",
                    Constants.DefenceState.DEFENCE_STATE_WARNING_PWD);
            i.putExtra("contactId", contactId);
            i.setAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
            MyApplication.app.sendBroadcast(i);
        } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
            FList.getInstance().setDefenceState(contactId,
                    Constants.DefenceState.DEFENCE_NO_PERMISSION);
            Intent i = new Intent();
            i.putExtra("state",
                    Constants.DefenceState.DEFENCE_NO_PERMISSION);
            i.putExtra("contactId", contactId);
            i.setAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
            MyApplication.app.sendBroadcast(i);
        }
        Intent ack_Defence = new Intent();
        ack_Defence.setAction(Constants.P2P.ACK_GET_REMOTE_DEFENCE);
        ack_Defence.putExtra("contactId", contactId);
        ack_Defence.putExtra("result", result);
        MyApplication.app.sendBroadcast(ack_Defence);
    }

	/*
	 * 获取设备各种设置回调 结束
	 * ****************************************************************
	 */

	/* ***************************************************************
	 * 时间设置相关回调 开始
	 */

    @Override
    public void vRetGetDeviceTimeResult(String time) {
        // 获取设备时间回调
        Log.e(TAG, "vRetGetDeviceTimeResult:" + time);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_TIME);
        i.putExtra("time", time);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetDeviceTimeResult(int result) {
        // 设置设备时间回调
        Log.e(TAG, "vRetSetDeviceTimeResult:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.RET_SET_TIME);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetSetDeviceTime(int msgId, int result) {
        // 设置设备时间ACK回调
        Log.e(TAG, "ACK_vRetSetDeviceTime:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_TIME);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetDeviceTime(int msgId, int result) {
        // 获取设备时间ACK回调
        Log.e(TAG, "ACK_vRetGetDeviceTime:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_GET_TIME);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 时间设置相关回调 结束
	 * ****************************************************************
	 */

    /* ***************************************************************
     * 设置视频格式相关回调 开始
     */
    @Override
    public void ACK_vRetSetNpcSettingsVideoFormat(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetNpcSettingsVideoFormat:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_VIDEO_FORMAT);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetVideoFormatResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetVideoFormatResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_VIDEO_FORMAT);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetVideoFormatResult(int type) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetVideoFormatResult:" + type);
        Intent format_type = new Intent();
        format_type.setAction(Constants.P2P.RET_GET_VIDEO_FORMAT);
        format_type.putExtra("type", type);
        MyApplication.app.sendBroadcast(format_type);
    }

	/*
	 * 设置视频格式相关回调 结束
	 * ****************************************************************
	 */

    /* ***************************************************************
     * 设置设备音量大小相关回调 开始
     */
    @Override
    public void ACK_vRetSetNpcSettingsVideoVolume(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetNpcSettingsVideoVolume:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_VIDEO_VOLUME);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetVolumeResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetVolumeResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_VIDEO_VOLUME);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetVideoVolumeResult(int value) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetVideoVolumeResult:" + value);
        Intent volume = new Intent();
        volume.setAction(Constants.P2P.RET_GET_VIDEO_VOLUME);
        volume.putExtra("value", value);
        MyApplication.app.sendBroadcast(volume);
    }

	/*
	 * 设置设备音量大小相关回调 结束
	 * ****************************************************************
	 */

    /* ***************************************************************
     * 修改设备密码相关回调 开始
     */
    @Override
    public void ACK_vRetSetDevicePassword(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetDevicePassword:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_DEVICE_PASSWORD);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetDevicePasswordResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetDevicePasswordResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_DEVICE_PASSWORD);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 修改设备密码相关回调 结束
	 * ****************************************************************
	 */

    /*
     * 设置网络类型 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetNpcSettingsNetType(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetNpcSettingsNetType:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_NET_TYPE);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetNetTypeResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetNetTypeResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_NET_TYPE);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetNetTypeResult(int type) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetNetTypeResult:" + type);
        Intent net_type = new Intent();
        net_type.setAction(Constants.P2P.RET_GET_NET_TYPE);
        net_type.putExtra("type", type);
        MyApplication.app.sendBroadcast(net_type);
    }

	/*
	 * 设置网络类型 结束
	 * ****************************************************************
	 */

    /*
     * 设置WIFI 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetWifi(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetWifi:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_WIFI);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetWifiList(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetGetWifiList:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_GET_WIFI);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetWifiResult(int result, int currentId, int count,
                               int[] types, int[] strengths, String[] names) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetWifiResult:" + result + ":" + currentId);
        if (result == 1) {
            Intent i = new Intent();
            i.setAction(Constants.P2P.RET_GET_WIFI);
            i.putExtra("iCurrentId", currentId);
            i.putExtra("iCount", count);
            i.putExtra("iType", types);
            i.putExtra("iStrength", strengths);
            i.putExtra("names", names);
            MyApplication.app.sendBroadcast(i);
        } else {
            Intent i = new Intent();
            i.putExtra("result", result);
            i.setAction(Constants.P2P.RET_SET_WIFI);
            MyApplication.app.sendBroadcast(i);
        }
    }

	/*
	 * 设置WIFI 结束
	 * ****************************************************************
	 */

	/*
	 * 设置绑定报警ID 开始
	 * ****************************************************************
	 */

    @Override
    public void ACK_vRetSetAlarmBindId(int srcID, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetAlarmBindId:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra("srcID", String.valueOf(srcID));
        i.setAction(Constants.P2P.ACK_RET_SET_BIND_ALARM_ID);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetAlarmBindId(int srcID, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetGetAlarmBindId:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_GET_BIND_ALARM_ID);
        i.putExtra("srcID", String.valueOf(srcID));
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetBindAlarmIdResult(int srcID, int result, int maxCount,
                                      String[] data) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetBindAlarmIdResult:" + result);
        if (result == 1) {
            Intent alarmId = new Intent();
            alarmId.setAction(Constants.P2P.RET_GET_BIND_ALARM_ID);
            alarmId.putExtra("data", data);
            alarmId.putExtra("max_count", maxCount);
            alarmId.putExtra("srcID", String.valueOf(srcID));
            MyApplication.app.sendBroadcast(alarmId);
        } else {
            Intent i = new Intent();
            i.putExtra("result", result);
            i.setAction(Constants.P2P.RET_SET_BIND_ALARM_ID);
            i.putExtra("max_count", maxCount);
            i.putExtra("srcID", String.valueOf(srcID));
            MyApplication.app.sendBroadcast(i);
        }
    }

	/*
	 * 设置绑定报警ID 结束
	 * ****************************************************************
	 */

    /*
     * 设置报警邮箱 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetAlarmEmail(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetAlarmEmail:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_ALARM_EMAIL);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetAlarmEmail(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetGetAlarmEmail:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_GET_ALARM_EMAIL);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetAlarmEmailResult(int result, String email) {
        // TODO Auto-generated method stub
        Log.e("option", "vRetAlarmEmailResult:" + result + ":" + email);
        byte option = (byte) (result & (1 << 0));
        byte ooo = (byte) ((result << 1) & (0x1));
        Log.e("option", "option:" + option + "ooo-->" + ooo);
        if (option == 1) {
            Intent i = new Intent();
            i.setAction(Constants.P2P.RET_GET_ALARM_EMAIL);
            i.putExtra("email", email);
            i.putExtra("result", result);
            MyApplication.app.sendBroadcast(i);
        } else {
            // Intent i = new Intent();
            // i.putExtra("result", result);
            // i.setAction(Constants.P2P.RET_SET_ALARM_EMAIL);
            // MyApp.app.sendBroadcast(i);
        }
    }

	/*
	 * 设置报警邮箱 结束
	 * ****************************************************************
	 */

	/*
	 * 设置移动侦测相关 开始
	 * ****************************************************************
	 */

    @Override
    public void ACK_vRetSetNpcSettingsMotion(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetNpcSettingsMotion:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_MOTION);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetMotionResult(int state) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetMotionResult:" + state);
        Intent motion = new Intent();
        motion.setAction(Constants.P2P.RET_GET_MOTION);
        motion.putExtra("motionState", state);
        MyApplication.app.sendBroadcast(motion);
    }

    @Override
    public void vRetSetMotionResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetMotionResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_MOTION);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 设置移动侦测相关 结束
	 * ****************************************************************
	 */

    /*
     * 设置蜂鸣器相关 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetNpcSettingsBuzzer(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetNpcSettingsBuzzer:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_BUZZER);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetBuzzerResult(int state) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetBuzzerResult:" + state);
        Intent buzzer = new Intent();
        buzzer.setAction(Constants.P2P.RET_GET_BUZZER);
        buzzer.putExtra("buzzerState", state);
        MyApplication.app.sendBroadcast(buzzer);
    }

    @Override
    public void vRetSetBuzzerResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetBuzzerResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_BUZZER);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 设置蜂鸣器相关 结束
	 * ****************************************************************
	 */

    /*
     * 设置录像模式相关 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetNpcSettingsRecordType(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetNpcSettingsRecordType:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_RECORD_TYPE);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetRecordTypeResult(int type) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetRecordTypeResult:" + type);
        Intent record_type = new Intent();
        record_type.setAction(Constants.P2P.RET_GET_RECORD_TYPE);
        record_type.putExtra("type", type);
        MyApplication.app.sendBroadcast(record_type);
    }

    @Override
    public void vRetSetRecordTypeResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetRecordTypeResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_RECORD_TYPE);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 设置录像模式相关 结束
	 * ****************************************************************
	 */

	/*
	 * 设置录像时长相关 开始
	 * ****************************************************************
	 */

    @Override
    public void ACK_vRetSetNpcSettingsRecordTime(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetNpcSettingsRecordTime:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_RECORD_TIME);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetRecordTimeResult(int time) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetRecordTimeResult:" + time);
        Intent record_time = new Intent();
        record_time.setAction(Constants.P2P.RET_GET_RECORD_TIME);
        record_time.putExtra("time", time);
        MyApplication.app.sendBroadcast(record_time);
    }

    @Override
    public void vRetSetRecordTimeResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetRecordTimeResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_RECORD_TIME);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 设置录像时长相关 结束
	 * ****************************************************************
	 */

    /*
     * 设置录像计划时间 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetNpcSettingsRecordPlanTime(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetNpcSettingsRecordPlanTime:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_RECORD_PLAN_TIME);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetRecordPlanTimeResult(String time) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetRecordPlanTimeResult:" + time);
        Intent plan_time = new Intent();
        plan_time.setAction(Constants.P2P.RET_GET_RECORD_PLAN_TIME);
        plan_time.putExtra("time", time);
        MyApplication.app.sendBroadcast(plan_time);
    }

    @Override
    public void vRetSetRecordPlanTimeResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetRecordPlanTimeResult:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_RECORD_PLAN_TIME);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 设置录像计划时间 结束
	 * ****************************************************************
	 */

    /*
     * 防区设置相关 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetDefenceArea(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetDefenceArea:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_SET_DEFENCE_AREA);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetClearDefenceAreaState(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetClearDefenceAreaState:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_CLEAR_DEFENCE_AREA);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetClearDefenceAreaState(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetClearDefenceAreaState:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.RET_CLEAR_DEFENCE_AREA);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetDefenceArea(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetGetDefenceArea:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_GET_DEFENCE_AREA);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetDefenceAreaResult(int result, ArrayList<int[]> data,
                                      int group, int item) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetDefenceAreaResult:" + result);
        if (result == 1) {
            Intent i = new Intent();
            i.putExtra("result", result);
            i.setAction(Constants.P2P.RET_GET_DEFENCE_AREA);
            i.putExtra("data", data);
            MyApplication.app.sendBroadcast(i);
        } else {
            Intent i = new Intent();
            i.putExtra("result", result);
            i.setAction(Constants.P2P.RET_SET_DEFENCE_AREA);
            i.putExtra("group", group);
            i.putExtra("item", item);
            MyApplication.app.sendBroadcast(i);
        }
    }

	/*
	 * 防区设置相关 结束
	 * ****************************************************************
	 */

    /*
     * 远程设置相关 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetRemoteDefence(String contactId, int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetRemoteDefence:" + result);
        Log.e("remote_defence", "ACK_vRetSetRemoteDefence--" + "contactId="
                + contactId + "---" + "result=" + result);
        if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
            Contact contact = FList.getInstance().isContact(contactId);
            if (null != contact) {
                P2PHandler.getInstance().getNpcSettings(contact.contactId,
                        contact.contactPassword);
            }

        } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
            FList.getInstance().setDefenceState(contactId,
                    Constants.DefenceState.DEFENCE_STATE_WARNING_NET);
            Intent i = new Intent();
            i.putExtra("state",
                    Constants.DefenceState.DEFENCE_STATE_WARNING_NET);
            i.putExtra("contactId", contactId);
            i.setAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
            MyApplication.app.sendBroadcast(i);

        } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
            FList.getInstance().setDefenceState(contactId,
                    Constants.DefenceState.DEFENCE_STATE_WARNING_PWD);
            Intent i = new Intent();
            i.putExtra("state",
                    Constants.DefenceState.DEFENCE_STATE_WARNING_PWD);
            i.putExtra("contactId", contactId);
            i.setAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
            MyApplication.app.sendBroadcast(i);
        } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
            FList.getInstance().setDefenceState(contactId,
                    Constants.DefenceState.DEFENCE_NO_PERMISSION);
        }

    }

    @Override
    public void ACK_vRetSetRemoteRecord(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetRemoteRecord:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.RET_SET_REMOTE_RECORD);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetRemoteDefenceResult(String contactId, int state) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetRemoteDefenceResult:" + state);
        if (state == Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON) {
            FList.getInstance().setDefenceState(contactId,
                    Constants.DefenceState.DEFENCE_STATE_ON);
        } else {
            FList.getInstance().setDefenceState(contactId,
                    Constants.DefenceState.DEFENCE_STATE_OFF);
        }

        Intent defence = new Intent();
        defence.setAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
        defence.putExtra("state", state);
        defence.putExtra("contactId", contactId);
        MyApplication.app.sendBroadcast(defence);
    }

    @Override
    public void vRetGetRemoteRecordResult(int state) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetRemoteRecordResult:" + state);
        Intent record = new Intent();
        record.setAction(Constants.P2P.RET_GET_REMOTE_RECORD);
        record.putExtra("state", state);
        MyApplication.app.sendBroadcast(record);
    }

    @Override
    public void vRetSetRemoteDefenceResult(String contactId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetRemoteDefenceResult:" + result);
        Intent defence = new Intent();
        defence.setAction(Constants.P2P.RET_SET_REMOTE_DEFENCE);
        defence.putExtra("state", result);
        defence.putExtra("contactId", contactId);
        MyApplication.app.sendBroadcast(defence);
    }

    @Override
    public void vRetSetRemoteRecordResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetRemoteRecordResult:" + result);
        Intent record = new Intent();
        record.setAction(Constants.P2P.RET_SET_REMOTE_RECORD);
        record.putExtra("state", result);
        MyApplication.app.sendBroadcast(record);
    }

	/*
	 * 远程设置相关 结束
	 * ****************************************************************
	 */

    /*
     * 设置设备初始密码（仅当设备出厂化未设置密码时可用） 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetSetInitPassword(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetSetInitPassword:" + result);
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_INIT_PASSWORD);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetInitPasswordResult(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetSetInitPasswordResult******:" + result);

        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_INIT_PASSWORD);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 设置设备初始密码（仅当设备出厂化未设置密码时可用） 结束
	 * ****************************************************************
	 */

    /*
     * 设备检查更新 开始
     * ****************************************************************
     */
    @Override
    public void ACK_vRetGetDeviceVersion(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetGetDeviceVersion:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_GET_DEVICE_INFO);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetDeviceVersion(int result, String cur_version,
                                     int iUbootVersion, int iKernelVersion, int iRootfsVersion) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetGetDeviceVersion:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra("cur_version", cur_version);
        i.putExtra("iUbootVersion", iUbootVersion);
        i.putExtra("iKernelVersion", iKernelVersion);
        i.putExtra("iRootfsVersion", iRootfsVersion);
        i.setAction(Constants.P2P.RET_GET_DEVICE_INFO);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetCheckDeviceUpdate(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetCheckDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_CHECK_DEVICE_UPDATE);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetCheckDeviceUpdate(String contactId, int result,
                                      String cur_version, String upg_version) {
        Log.e("vRetCheckDeviceUpdate", "vRetCheckDeviceUpdate:" + result
                + "cur_version-->" + cur_version + "upg_version-->"
                + upg_version);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra("cur_version", cur_version);
        i.putExtra("upg_version", upg_version);
        i.putExtra("contactId", contactId);
        i.setAction(Constants.P2P.RET_CHECK_DEVICE_UPDATE);
        MyApplication.app.sendBroadcast(i);
        FList.getInstance().setUpdate(contactId, result, cur_version,
                upg_version);
    }

    @Override
    public void ACK_vRetDoDeviceUpdate(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetDoDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_DO_DEVICE_UPDATE);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetDoDeviceUpdate(String contactId, int result, int value) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetDoDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra("value", value);
        i.putExtra("contactId", contactId);
        i.setAction(Constants.P2P.RET_DO_DEVICE_UPDATE);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetCancelDeviceUpdate(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetCancelDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_CANCEL_DEVICE_UPDATE);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetCancelDeviceUpdate(int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetCancelDeviceUpdate:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.RET_CHECK_DEVICE_UPDATE);
        MyApplication.app.sendBroadcast(i);
    }

	/*
	 * 设备检查更新 结束
	 * ****************************************************************
	 */

    @Override
    public void ACK_vRetGetRecordFileList(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACK_vRetGetRecordFileList:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.ACK_RET_GET_PLAYBACK_FILES);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetRecordFiles(String[] names, byte option0, byte option1) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_PLAYBACK_FILES);
        i.putExtra("recordList", names);
        i.putExtra("option1", option1);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetFriendStatus(int count, String[] contactIDs,
                                    int[] status, int[] types, boolean isFinish) {
        Log.e(TAG, "vRetGetFriendStatus:" + count);
        FList flist = FList.getInstance();
        for (int i = 0; i < count; i++) {
            flist.setState(contactIDs[i], status[i]);
            if (contactIDs[i].length() > 0 && contactIDs[i].charAt(0) == '0') {
                flist.setType(contactIDs[i], P2PValue.DeviceType.PHONE);
            } else {
                if (status[i] == Constants.DeviceState.ONLINE) {
                    flist.setType(contactIDs[i], types[i]);
                }
            }
        }
        // TODO Auto-generated method stub
        FList.getInstance().getDefenceState();
        FList.getInstance().sort();
        Intent friends = new Intent();
        friends.setAction(Constants.Action.GET_FRIENDS_STATE);
        friends.putExtra("contactIDs", contactIDs);
        friends.putExtra("status", status);
        MyApplication.app.sendBroadcast(friends);

    }

    @Override
    public void ACK_vRetMessage(int msgId, int result) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.Action.RECEIVE_MSG);
        i.putExtra("msgFlag", msgId + "");
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetMessage(String contactId, String msgStr) {
        // TODO Auto-generated method stub
        Contact contact = FList.getInstance().isContact(contactId);
        if (contact == null) {
            // 没加好友返回
            return;
        }
        Message msg = new Message();
        msg.activeUser = NpcCommon.mThreeNum;
        msg.fromId = contactId;
        msg.toId = NpcCommon.mThreeNum;
        msg.msg = msgStr;
        msg.msgTime = String.valueOf(System.currentTimeMillis());
        msg.msgFlag = String.valueOf(-1);
        msg.msgState = String.valueOf(Constants.MessageType.READED);
        contact.messageCount += 1;

        // FList.getInstance().update(contact);

        DataManager.insertMessage(MyApplication.app, msg);
        Intent k = new Intent();
        k.setAction(Constants.Action.REFRESH_CONTANTS);
        MyApplication.app.sendBroadcast(k);
        MusicManger.getInstance().playMsgMusic();
    }

    @Override
    public void vRetSysMessage(String msg) {
        // TODO Auto-generated method stub
        SysMessage sysMessage = new SysMessage();
        sysMessage.activeUser = NpcCommon.mThreeNum;
        sysMessage.msg = msg;
        sysMessage.msg_time = String.valueOf(System.currentTimeMillis());
        sysMessage.msgState = SysMessage.MESSAGE_STATE_NO_READ;
        sysMessage.msgType = SysMessage.MESSAGE_TYPE_ADMIN;
        DataManager.insertSysMessage(MyApplication.app, sysMessage);
        Intent i = new Intent();
        i.setAction(SysMsgActivity.REFRESH);
        MyApplication.app.sendBroadcast(i);
        Intent k = new Intent();
        k.setAction(Constants.Action.RECEIVE_SYS_MSG);
        MyApplication.app.sendBroadcast(k);
    }

    @Override
    public void vRetCustomCmd(int contactId, int len, byte[] cmd) {

        // TODO Auto-generated method stub
        String cmdString = new String(cmd);
        try {
            String id = String.valueOf(contactId);
            String v_call = cmdString.substring(0, 11);
            //门铃主动挂断
            if (cmdString.equals("anerfa:disconnect")) {
                Intent i = new Intent();
                i.setAction(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT);
                i.putExtra("contactId", id);
                MyApplication.app.sendBroadcast(i);
                return;
            }
            //门铃推送
            if (v_call.equals("anerfa:call")) {
                AlarmRecord alarmRecord = new AlarmRecord();
                alarmRecord.alarmTime = String.valueOf(System.currentTimeMillis());
                alarmRecord.deviceId = id;
                alarmRecord.alarmType = 13;
                alarmRecord.activeUser = NpcCommon.mThreeNum;
                alarmRecord.group = -1;
                alarmRecord.item = -1;
                DataManager.insertAlarmRecord(P2PConnect.mContext, alarmRecord);
                Intent i = new Intent();
                i.setAction(Constants.Action.REFRESH_ALARM_RECORD);
                P2PConnect.mContext.sendBroadcast(i);
                long time = SharedPreferencesManager.getInstance().getIgnoreAlarmTime(
                        MyApplication.app);
                int time_interval = SharedPreferencesManager.getInstance()
                        .getAlarmTimeInterval(MyApplication.app);
                if ((System.currentTimeMillis() - time) < (1000 * time_interval)) {
                    return;
                }
                if (MonitorDeviceID.equals("")) {// 没在监控
                    Intent it = new Intent();
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.setClass(P2PConnect.mContext, AlarmWithPictrueActivity.class);
                    it.putExtra("alarm_id", contactId);
                    it.putExtra("alarm_type", P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH);
                    it.putExtra("isSupport", false);
                    it.putExtra("group", 0);
                    it.putExtra("item", 0);
                    it.putExtra("isSupportDelete", false);
                    it.putExtra("time", String.valueOf(System.currentTimeMillis()));
                    it.putExtra("imageCounts", 0);
                    it.putExtra("picture", "");
                    it.putExtra("hasPictrue", false);
                    it.putExtra("alarmTime", String.valueOf(System.currentTimeMillis()));
                    it.putExtra("sensorName", "");
                    it.putExtra("mainType", P2PValue.DeviceType.DOORBELL);
                    it.putExtra("subType", 0);
                    P2PConnect.mContext.startActivity(it);
                } else if (MonitorDeviceID.equals(id)) {// 正在监控此设备
                    Log.i("dxsalarmmessage", "正在监控此设备" + id + "MonitorDeviceID-->"
                            + MonitorDeviceID);
                    // 不推送
                } else {// 正在监控但不是此设备
                    // 监控页面弹窗
                    Log.i("dxsalarmmessage", "正在监控但不是此设备" + id
                            + "MonitorDeviceID-->" + MonitorDeviceID);
                    Intent k = new Intent();
                    k.setAction(Constants.Action.MONITOR_NEWDEVICEALARMING);
                    k.putExtra("messagetype", 2);// 1是报警，2是透传门铃
                    k.putExtra("contactId", id);
                    k.putExtra("CustomCmdDoorAlarm", true);
                    k.putExtra("alarm_type", P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH);
                    MyApplication.app.sendBroadcast(k);
                }
            } else {
//				toAlarm(contactId, cmdString);

            }

        } catch (Exception e) {
            // TODO: handle exception
//			toAlarm(contactId, cmdString);
        }
        //ehome
        Intent ehome = new Intent();
        ehome.setAction(Constants.P2P.USER_DEFINDE_MESSAGE);
        ehome.putExtra("fromwhichdevice", contactId);
        ehome.putExtra("Data", cmd);
        MyApplication.app.sendBroadcast(ehome);
        //ehome
    }

    public void toAlarm(int contactId, String cmdString) {
        if (MonitorDeviceID.equals("")) {
            Intent it = new Intent();
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.setClass(P2PConnect.mContext, AlarmWithPictrueActivity.class);
            it.putExtra("alarm_id", contactId);
            it.putExtra("alarm_type", 999);
            it.putExtra("isSupport", false);
            it.putExtra("group", 0);
            it.putExtra("item", 0);
            it.putExtra("isSupportDelete", false);
            it.putExtra("time", String.valueOf(System.currentTimeMillis()));
            it.putExtra("imageCounts", 0);
            it.putExtra("picture", "");
            it.putExtra("hasPictrue", false);
            it.putExtra("alarmTime", String.valueOf(System.currentTimeMillis()));
            it.putExtra("sensorName", "");
            it.putExtra("mainType", P2PValue.DeviceType.IPC);
            it.putExtra("subType", 0);
            it.putExtra("customMsg", cmdString);
            MyApplication.app.startActivity(it);
        } else {
            Intent k = new Intent();
            k.setAction(Constants.Action.MONITOR_NEWDEVICEALARMING);
            k.putExtra("messagetype", 2);// 1是报警，2是透传门铃
            k.putExtra("contactId", String.valueOf(contactId));
            k.putExtra("CustomCmdDoorAlarm", true);
            k.putExtra("customMsg", cmdString);
            k.putExtra("alarm_type", 999);
            MyApplication.app.sendBroadcast(k);
        }
    }

    @Override
    public void ACK_vRetCustomCmd(int msgId, int result) {
        // TODO Auto-generated method stub
        Log.e("my", result + "");
    }

    @Override
    public void vRetDeviceNotSupport() {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_DEVICE_NOT_SUPPORT);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetSetImageReverse(int msgId, int result) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_VRET_SET_IMAGEREVERSE);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetSetImageReverse(int result) {
    }

    @Override
    public void vRetGetImageReverseResult(int type) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_IMAGE_REVERSE);
        i.putExtra("type", type);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetSetInfraredSwitch(int msgId, int result) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_INFRARED_SWITCH);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetGetInfraredSwitch(int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_INFRARED_SWITCH);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetInfraredSwitch(int result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ACK_vRetSetWiredAlarmInput(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_WIRED_ALARM_INPUT);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void ACK_vRetSetWiredAlarmOut(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_WIRED_ALARM_OUT);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetSetAutomaticUpgrade(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_AUTOMATIC_UPGRADE);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetGetWiredAlarmInput(int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_WIRED_ALARM_INPUT);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetGetWiredAlarmOut(int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_WIRED_ALARM_OUT);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetGetAutomaticUpgrade(int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_AUTOMATIC_UPGRAD);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetSetWiredAlarmInput(int state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetSetWiredAlarmOut(int state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetSetAutomaticUpgrade(int state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ACK_VRetSetVisitorDevicePassword(int msgId, int state) {
        Log.i("dxssetting", "state-->" + state);
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_VISITOR_DEVICE_PASSWORD);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetSetVisitorDevicePassword(int result) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_VISITOR_DEVICE_PASSWORD);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void ACK_vRetSetTimeZone(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_TIME_ZONE);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetGetTimeZone(int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_TIME_ZONE);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetSetTimeZone(int result) {

    }

    @Override
    public void vRetGetSdCard(int result1, int result2, int SDcardID, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_SD_CARD_CAPACITY);
        i.putExtra("total_capacity", result1);
        i.putExtra("remain_capacity", result2);
        i.putExtra("SDcardID", SDcardID);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
        Log.e("sdid", SDcardID + "");
    }

    @Override
    public void ACK_vRetGetSDCard(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_GET_SD_CARD_CAPACITY);
        i.putExtra("result", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetSdFormat(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_GET_SD_CARD_FORMAT);
        i.putExtra("result", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSdFormat(int result) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_SD_CARD_FORMAT);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void VRetGetUsb(int result1, int result2, int SDcardID, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_USB_CAPACITY);
        i.putExtra("total_capacity", result1);
        i.putExtra("remain_capacity", result2);
        i.putExtra("SDcardID", SDcardID);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetSetGPIO(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_GPIO);
        i.putExtra("result", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void ACK_vRetSetGPIO1_0(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_GPIO1_0);
        i.putExtra("result", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetSetGPIO(int result) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_GPIO);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetAudioDeviceType(int type) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_AUDIO_DEVICE_TYPE);
        i.putExtra("type", type);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetSetPreRecord(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_PRE_RECORD);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetPreRecord(int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_PRE_RECORD);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetPreRecord(int result) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_PRE_RECORD);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetSensorSwitchs(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_GET_SENSOR_SWITCH);
        i.putExtra("result", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetSetSensorSwitchs(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_SENSOR_SWITCH);
        i.putExtra("result", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetSensorSwitchs(int result, ArrayList<int[]> data) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_SENSOR_SWITCH);
        i.putExtra("result", result);
        i.putExtra("data", data);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetSensorSwitchs(int result) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_SET_SENSOR_SWITCH);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRecvSetLAMPStatus(String deviceId, int result) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.SET_LAMP_STATUS);
        i.putExtra("result", result);
        i.putExtra("deviceId", deviceId);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vACK_RecvSetLAMPStatus(int result, int value) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_SET_LAMP_STATUS);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRecvGetLAMPStatus(String deviceId, int result) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.GET_LAMP_STATUS);
        i.putExtra("result", result);
        i.putExtra("deviceId", deviceId);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetAlarmEmailResultWithSMTP(int result, String email,
                                             int smtpport, byte Entry, String[] SmptMessage, byte reserve1) {
        if ((result & (1 << 0)) == 1) {
            Intent i = new Intent();
            i.setAction(Constants.P2P.RET_GET_ALARM_EMAIL_WITHSMTP);
            i.putExtra("contectid", SmptMessage[5]);
            i.putExtra("result", result);
            i.putExtra("email", email);
            i.putExtra("smtpport", smtpport);
            i.putExtra("SmptMessage", SmptMessage);
            i.putExtra("encrypt", (int) Entry);
            i.putExtra("isSupport", (int) reserve1);
            MyApplication.app.sendBroadcast(i);
        } else {
            Intent i = new Intent();
            i.putExtra("result", result);
            i.setAction(Constants.P2P.RET_SET_ALARM_EMAIL);
            MyApplication.app.sendBroadcast(i);
        }

    }

    @Override
    public void vRetPresetMotorPos(byte[] result) {
        Log.e("dxsprepoint", Arrays.toString(result));
        Intent i = new Intent();
        if (result[2] == 0) {
            i.setAction(Constants.P2P.RET_TOSEE_PRESETMOTOROS);
        } else if (result[2] == 1) {// 设置预置位返回
            i.setAction(Constants.P2P.RET_SET_PRESETMOTOROS);
        } else if (result[2] == 2) {// 查询所有预置位返回
            i.setAction(Constants.P2P.RET_GET_PRESETMOTOROS);
        } else if (result[2] == 3) {// 删除预置位返回
            i.setAction(Constants.P2P.RET_DELETE_PRESETMOTOROS);
        } else if (result[2] == 4) {// 是否在预置位返回
            i.setAction(Constants.P2P.RET_GET_IS_PRESETMOTOROS);
        }
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetDefenceSwitchStatus(int result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetDefenceSwitchStatusResult(byte[] result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetAlarmPresetMotorPos(byte[] result) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.P2P_SET_ALARM_PRESET_MOTOR_POS);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetIpConfig(byte[] result) {
        // TODO Auto-generated method stub

    }

    /**
     * 获取访客密码
     */
    @Override
    public void vRetNPCVistorPwd(int pwd) {
        Intent i = new Intent();
        i.putExtra("visitorpwd", pwd);
        i.setAction(Constants.P2P.RET_GET_VISTOR_PASSWORD);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void ACK_vRetGetAlarmCenter(int msgId, int state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ACK_vRetSetAlarmCenter(int msgId, int state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetGetAlarmCenter(int result, int state, String ipdress,
                                   int port, String userId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetSetAlarmCenter(int result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetDeviceNotSupportAlarmCenter() {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetDeleteDeviceAlarmID(String deviceId, int result, int resultType) {
        Intent i = new Intent();
        i.putExtra("deviceId", deviceId);
        i.putExtra("deleteResult", result);
        i.putExtra("resultType", resultType);
        i.setAction(Constants.P2P.DELETE_BINDALARM_ID);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetDeviceLanguege(int result, int languegecount,
                                   int curlanguege, int[] langueges) {
        // TODO Auto-generated method stub
        if (result == 1) {
            Intent i = new Intent();
            i.putExtra("languegecount", languegecount);
            i.putExtra("curlanguege", curlanguege);
            i.putExtra("langueges", langueges);
            i.setAction(Constants.P2P.RET_GET_LANGUEGE);
            MyApplication.app.sendBroadcast(i);
        } else {
            Intent i = new Intent();
            i.putExtra("result", result);
            i.setAction(Constants.P2P.RET_SET_LANGUEGE);
            MyApplication.app.sendBroadcast(i);
        }
    }

    @Override
    public void vRetFocusZoom(String deviceId, int result) {
        // TODO Auto-generated method stub
        Log.e(TAG, "vRetFocusZoom:" + result);
        Log.e("vRetFocusZoom", "vRetFocusZoom:" + result);
        Intent i = new Intent();
        i.putExtra("result", result);
        i.putExtra("deviceId", deviceId);
        i.setAction(Constants.P2P.RET_GET_FOCUS_ZOOM);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetAllarmImage(int id, String filename, int errorCode) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("deviceId", id);
        i.putExtra("filename", filename);
        i.putExtra("errorCode", errorCode);
        i.setAction(Constants.P2P.RET_GET_ALLARMIMAGE);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetFishEyeData(int iSrcID, byte[] data, int datasize) {
        // TODO Auto-generated method stub
        // 鱼眼设备回调
        Intent i = new Intent();
        i.putExtra("iSrcID", iSrcID);
        i.putExtra("boption", data[2]);
        i.putExtra("data", data);
        Log.e("vRetFishEyeData",
                "iSrcID-->" + iSrcID + "--data-->" + Arrays.toString(data));
        switch (data[1]) {
            case 2:
                i.setAction(Constants.P2P.RET_SET_IPC_WORKMODE);
                break;
            case 4:
                i.setAction(Constants.P2P.RET_SET_SENSER_WORKMODE);
                break;
            case 6:
                i.setAction(Constants.P2P.RET_SET_SCHEDULE_WORKMODE);
                break;
            case 8:
                i.setAction(Constants.P2P.RET_DELETE_SCHEDULE);
                break;
            case 10:
                i.setAction(Constants.P2P.RET_GET_CURRENT_WORKMODE);
                break;
            case 12:
                i.setAction(Constants.P2P.RET_GET_SENSOR_WORKMODE);
                break;
            case 14:
                i.setAction(Constants.P2P.RET_GET_SCHEDULE_WORKMODE);
                break;
            case 16:
                i.setAction(Constants.P2P.RET_SET_ALLSENSER_SWITCH);
                break;
            case 18:
                i.setAction(Constants.P2P.RET_GET_ALLSENSER_SWITCH);
                break;
            case 20:
                i.setAction(Constants.P2P.RET_SET_LOWVOL_TIMEINTERVAL);// 暂时不处理
                break;
            case 22:
                i.setAction(Constants.P2P.RET_GET_LOWVOL_TIMEINTERVAL);// 暂时不处理
                break;
            case 24:
                i.setAction(Constants.P2P.RET_DELETE_ONE_CONTROLER);
                break;
            case 26:
                i.setAction(Constants.P2P.RET_DELETE_ONE_SENSOR);
                break;
            case 28:
                // 修改遥控器名字返回,但不带修改后的名字
                i.setAction(Constants.P2P.RET_CHANGE_CONTROLER_NAME);
                break;
            case 30:
                // 修改传感器名字返回,但不带修改后的名字
                i.setAction(Constants.P2P.RET_CHANGE_SENSOR_NAME);
                break;
            case 32:
                i.setAction(Constants.P2P.RET_INTO_LEARN_STATE);
                break;
            case 34:
                i.setAction(Constants.P2P.RET_TURN_SENSOR);
                break;
            case 36:
                // 分享时管理员返回
                i.setAction(Constants.P2P.RET_SHARE_TO_MEMBER);
                break;
            case 37:
                // 分享时用户收到的信息
                i.setAction(Constants.P2P.RET_GOT_SHARE);
                break;
            case 39:
                i.setAction(Constants.P2P.RET_DEV_RECV_MEMBER_FEEDBACK);
                break;
            case 41:
                i.setAction(Constants.P2P.RET_ADMIN_DELETE_ONE_MEMBER);
                break;
            case 43:
                i.setAction(Constants.P2P.RET_DELETE_DEV);
                break;
            case 45:
                i.setAction(Constants.P2P.RET_GET_MEMBER_LIST);
                break;
            case 47:
                i.setAction(Constants.P2P.RET_SET_ONE_SPECIAL_ALARM);
                break;
            case 49:
                i.setAction(Constants.P2P.RET_GET_ALL_SPECIAL_ALARM);
                break;
            case 51:
                i.setAction(Constants.P2P.RET_GET_LAMPSTATE);
                break;
            case 53:
                i.setAction(Constants.P2P.RET_KEEP_CLIENT);
                break;
            case 63:
                i.setAction(Constants.P2P.RET_LED_LIGHT);
                break;
            case 61:
                i.setAction(Constants.P2P.SET_LED_LIGHT);
                break;
            case 65:
                i.setAction(Constants.P2P.RET_SET_SENSOR_PREPOINT);
                break;
        }
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void ACK_VRetGetNvrIpcList(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_GET_NVR_IPC_LIST);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
        Log.e("ACK_VRetGetNvrIpcList", "state=" + state);
    }

    @Override
    public void vRetGetNvrIpcList(String contactId, String[] data, int number) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GET_NVR_IPC_LIST);
        i.putExtra("contactId", contactId);
        i.putExtra("data", data);
        i.putExtra("number", number);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetSetWifiMode(String id, int result) {
        Intent i = new Intent();
        i.putExtra("id", id);
        i.putExtra("result", result);
        i.setAction(Constants.P2P.RET_SET_AP_MODE);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetAPModeSurpport(String id, int result) {
        Intent i = new Intent();
        i.putExtra("id", id);
        i.putExtra("result", result);
        i.setAction(Constants.P2P.RET_AP_MODESURPPORT);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetDeviceType(String id, int mainType, int subType) {
//        Intent i = new Intent();
//        i.setAction(Constants.P2P.RET_GET_DEVICE_TYPE);
//        i.putExtra("mainType", mainType);
//        i.putExtra("subType", subType);
//        i.putExtra("deviceId",id);
//        MyApp.app.sendBroadcast(i);
        FList.getInstance().setSubType(id, subType);
    }

    @Override
    public void ACK_VRetGetNvrInfo(String deviceId, int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_GET_NVRINFO);
        i.putExtra("state", state);
        i.putExtra("deviceId", deviceId);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetNVRInfo(int iSrcID, byte[] data, int datasize) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("iSrcID", iSrcID);
        i.putExtra("boption", data[1]);
        i.putExtra("data", data);
        i.setAction(Constants.P2P.RET_GET_NVRINFO);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetFocusZoom(String deviceId, int result, int value) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("deviceId", deviceId);
        i.putExtra("result", result);
        i.putExtra("value", value);
        i.setAction(Constants.P2P.RET_GET_FOCUS_ZOOM_POSITION);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetFocusZoom(String deviceId, int result, int value) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("deviceId", deviceId);
        i.putExtra("result", result);
        i.putExtra("value", value);
        i.setAction(Constants.P2P.RET_SET_FOCUS_ZOOM_POSITION);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetGPIO(String contactid, int result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetGetGPIO(String contactid, int result, int bValueNs) {
        // TODO Auto-generated method stub

    }

    @Override
    public void vRetGetDefenceWorkGroup(String contactid, byte[] data) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("iSrcID", contactid);
        i.putExtra("data", data);
        i.putExtra("boption", data[1]);
        i.setAction(Constants.P2P.RET_GET_DEFENCE_WORK_GROUP);
        MyApplication.app.sendBroadcast(i);
    }

	@Override
	public void vRetSetDefenceWorkGroup(String contactid, byte[] data) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.putExtra("iSrcID", contactid);
		i.putExtra("data", data);
		i.putExtra("boption", data[1]);
		i.setAction(Constants.P2P.RET_SET_DEFENCE_WORK_GROUP);
		MyApplication.app.sendBroadcast(i);
	}

    @Override
    public void vRetFTPConfigInfo(String contactid, byte[] data) {
        // TODO Auto-generated method stub
        if (data[1] == 1 || data[1] == 107) {
            Intent i = new Intent();
            i.putExtra("iSrcID", contactid);
            i.putExtra("data", data);
            i.setAction(Constants.P2P.RET_GET_FTP_CONFIG_INFO);
            MyApplication.app.sendBroadcast(i);
        } else {
            Intent i = new Intent();
            i.putExtra("iSrcID", contactid);
            i.putExtra("data", data);
            i.setAction(Constants.P2P.RET_SET_FTP_CONFIG_INFO);
            MyApplication.app.sendBroadcast(i);
        }

    }

    @Override
    public void vRetGetDefenceSwitch(int value) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("value", value);
        i.setAction(Constants.P2P.RET_GET_DEFENCE_SWITCH);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetDefenceSwitch(int result) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.RET_SET_DEFENCE_SWITCH);
        MyApplication.app.sendBroadcast(i);
    }

	@Override
	public void vRetDefenceAreaName(String contactid, byte[] data) {
		// TODO Auto-generated method stub
		Log.e("wxy", "DefenceAreaName-data:" + Arrays.toString(data));
		Intent i = new Intent();
		i.putExtra("iSrcID", contactid);
		i.putExtra("data", data);
		switch (data[1]) {
		case 3:
			i.setAction(Constants.P2P.RET_GET_DEFENCE_AREA_NAME);
			break;
		case 5:
			i.setAction(Constants.P2P.RET_SET_DEFENCE_AREA_NAME);
			break;
		case 7:
			i.setAction(Constants.P2P.RET_SETTING_LIGHT_CONTROL);
			break;
		case 9:
			i.setAction(Constants.P2P.RET_GET_LIGHT_CONTROL_SETTING);
			break;
		case 16://全屏监控黑白彩色切换
            if (data[2]==0){
                i.setAction(Constants.P2P.RET_SET_VIDEO_COLOR_SWITCH);
            }else {
                i.setAction(Constants.P2P.RET_GET_VIDEO_COLOR_SWITCH);
            }
            break;
        case 17:
            i.putExtra("isNew",1);
            i.setAction(Constants.P2P.RET_GET_IS_PRESETMOTOROS);
            break;
		default:
			break;
		}
		MyApplication.app.sendBroadcast(i);
	}


    @Override
    public void ACK_OpenDoor(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_OPEN_DOOR);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }


    @Override
    public void vRetGPIOStatus(String contactid, byte[] data) {
        boolean[] bo = new boolean[8];
        byte bOption = data[1];
        int len = MyUtils.byte2ToShort(data, 2);
        int u32TimeInterval = MyUtils.bytes2ToInt(data, 4);
        byte u8HighOrLow = data[8];
        byte u8PinNo = data[9];
        int[] gpio = Utils.getByteBinnery(u8HighOrLow, true);
        Log.i("dxsgpio", Arrays.toString(gpio));
        for (int i = 0; i < gpio.length; i++) {
            if (gpio[i] == 0) {
                bo[i] = false;
            } else {
                bo[i] = true;
            }
        }
        Intent i = new Intent();
        i.setAction(Constants.P2P.GET_GPIO);
        i.putExtra("Level", bo);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRecvSetGPIOStatus(String contactid, byte[] data) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.SET_GPIO_STATUS);
        i.putExtra("data", data);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetFTPInfo(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_GET_FTP_INFO);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetPIRLightControl(int value) {
        Intent i = new Intent();
        i.putExtra("result", value);
        i.setAction(Constants.P2P.RET_SETPIRLIGHT);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void ACK_vRetGetPIRLight(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_GET_PIRLight);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void ACK_vRetSetPIRLight(int msgId, int state) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_SET_PIRLight);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);

    }

    @Override
    public void vRetFishInfo(String contactid, byte[] data) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.RET_GETFISHINFO);
        i.putExtra("iSrcID", contactid);
        i.putExtra("data", data);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_vRetGetDefenceWorkGroup(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_GET_DEFENCE_WORK_GROUP);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_VRetGetPresetPos(int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_PRESET_POS);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_VRetSetKeepClient(String contactId, int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_KEEP_CLIENT);
        i.putExtra("state", state);
        i.putExtra("deviceId", contactId);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetAutoSnapshotSwitch(int value) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("value", value);
        i.setAction(Constants.P2P.RET_GET_AUTO_SNAPSHOT_SWITCH);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetSetAutoSnapshotSwitch(int result) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.putExtra("result", result);
        i.setAction(Constants.P2P.RET_SET_AUTO_SNAPSHOT_SWITCH);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_VRetGetLed(String contactId, int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_GET_LIGHT);
        i.putExtra("contactId", contactId);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void ACK_VRetSetLed(String contactId, int msgId, int state) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_RET_SET_LIGHT);
        i.putExtra("contactId", contactId);
        i.putExtra("state", state);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRecvGetPrepointSurpporte(String deviceId, int result) {
        Intent i = new Intent();
        i.setAction(Constants.P2P.GET_PREPOINT_SURPPORTE);
        i.putExtra("result", result);
        i.putExtra("deviceId", deviceId);
        MyApplication.app.sendBroadcast(i);
    }


    @Override
    public void ACK_vRetSetNpcSettingsMotionSens(int msgId, int result) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setAction(Constants.P2P.ACK_vRetSetNpcSettingsMotionSens);
        i.putExtra("result", result);
        MyApplication.app.sendBroadcast(i);
    }

    @Override
    public void vRetGetMotionSensResult(int value) {
        // TODO Auto-generated method stub
        Intent motionSens = new Intent();
//		if((value>>16&0xff) == 0xff){
//			motionSens.putExtra("value", value&0x00ffff);
//			motionSens.putExtra("deviceType", Constants.DeviceType.DT_ZY);
//		}else{
//			motionSens.putExtra("value", value);
//			motionSens.putExtra("deviceType", Constants.DeviceType.DT_HS);
//		}
        //modify by wzy ZY，HS两种设备从返回256和6两种等级，现改为只返回6
        if ((value >> 16 & 0xff) == 0xff) {
            motionSens.putExtra("value", value & 0x00ffff);
            motionSens.putExtra("deviceType", Constants.DeviceType.DT_HS);
        } else {
            motionSens.putExtra("value", value);
            motionSens.putExtra("deviceType", Constants.DeviceType.DT_HS);
        }
        motionSens.setAction(Constants.P2P.RET_GET_MOTION_SENS);
        MyApplication.app.sendBroadcast(motionSens);
    }

    @Override
    public void vRetSetMotionSensResult(int iResult) {
        Intent i = new Intent();
        i.putExtra("result", iResult);
        i.setAction(Constants.P2P.RET_SET_MOTION_SENS);
        MyApplication.app.sendBroadcast(i);
    }

}
