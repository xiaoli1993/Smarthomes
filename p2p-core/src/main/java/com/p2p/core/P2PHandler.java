package com.p2p.core;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.p2p.core.P2PInterface.IP2P;
import com.p2p.core.P2PInterface.ISetting;
import com.p2p.core.global.Config;
import com.p2p.core.global.Constants;
import com.p2p.core.utils.DES;
import com.p2p.core.utils.MyUtils;
import com.p2p.core.utils.RtspThread;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * P2P交互帮助类 在此类中包装需要与设备通信的数据，通过JNI传给设备
 */
public class P2PHandler {
	String TAG = "SDK";

	private static int MSG_ID_SETTING_DEVICE_TIME = Constants.MsgSection.MSG_ID_SETTING_DEVICE_TIME;
	private static int MSG_ID_GETTING_DEVICE_TIME = Constants.MsgSection.MSG_ID_GETTING_DEVICE_TIME;

	private static int MSG_ID_GETTING_NPC_SETTINGS = Constants.MsgSection.MSG_ID_GETTING_NPC_SETTINGS;
	private static int MSG_ID_SET_REMOTE_DEFENCE = Constants.MsgSection.MSG_ID_SET_REMOTE_DEFENCE;
	private static int MSG_ID_SET_REMOTE_RECORD = Constants.MsgSection.MSG_ID_SET_REMOTE_RECORD;
	private static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT;
	private static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME;
	private static int MSG_ID_SETTING_NPC_SETTINGS_BUZZER = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER;
	private static int MSG_ID_SETTING_NPC_SETTINGS_MOTION = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION;
	private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE;
	private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME;
	private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME;
	private static int MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE;

	private static int MSG_ID_SETTING_ALARM_EMAIL = (int) (Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL + Math
			.random() * 999);
	private static int MSG_ID_GETTING_ALARM_EMAIL = (int) (Constants.MsgSection.MSG_ID_GETTING_ALARM_EMAIL + Math
			.random() * 999);

	private static int MSG_ID_SETTING_ALARM_BIND_ID = Constants.MsgSection.MSG_ID_SETTING_ALARM_BIND_ID;
	private static int MSG_ID_GETTING_ALARM_BIND_ID = Constants.MsgSection.MSG_ID_GETTING_ALARM_BIND_ID;

	private static int MSG_ID_SETTING_INIT_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_INIT_PASSWORD;
	private static int MSG_ID_SETTING_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD;
	private static int MSG_ID_CHECK_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD;

	private static int MSG_ID_SETTING_DEFENCEAREA = Constants.MsgSection.MSG_ID_SETTING_DEFENCEAREA;
	private static int MSG_ID_GETTING_DEFENCEAREA = Constants.MsgSection.MSG_ID_GETTING_DEFENCEAREA;

	private static int MSG_ID_SETTING_WIFI = Constants.MsgSection.MSG_ID_SETTING_WIFI;
	private static int MSG_ID_GETTING_WIFI_LIST = Constants.MsgSection.MSG_ID_GETTING_WIFI_LIST;

	private static int MSG_ID_GETTING_RECORD_FILE_LIST = Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST;
	private static int MSG_ID_SEND_MESSAGE = Constants.MsgSection.MSG_ID_SEND_MESSAGE;
	private static int MSG_ID_SEND_CUSTOM_CMD = Constants.MsgSection.MSG_ID_SEND_CUSTOM_CMD;
	private static int MSG_ID_CHECK_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_CHECK_DEVICE_UPDATE;
	private static int MSG_ID_CANCEL_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE;
	private static int MSG_ID_DO_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_DO_DEVICE_UPDATE;
	private static int MSG_ID_GET_DEFENCE_STATE = Constants.MsgSection.MSG_ID_GET_DEFENCE_STATE;
	private static int MSG_ID_GET_DEVICE_VERSION = Constants.MsgSection.MSG_ID_GET_DEVICE_VERSION;
	private static int MSG_ID_CLEAR_DEFENCE_GROUP = Constants.MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP;
	private static int MESG_ID_STTING_PIC_REVERSE = Constants.MsgSection.MESG_ID_STTING_PIC_REVERSE;
	private static int MESG_ID_STTING_IR_ALARM_EN = Constants.MsgSection.MESG_ID_STTING_IR_ALARM_EN;
	private static int MESG_STTING_ID_EXTLINE_ALARM_IN_EN = Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN;
	private static int MESG_STTING_ID_EXTLINE_ALARM_OUT_EN = Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN;
	private static int MESG_STTING_ID_SECUPGDEV = Constants.MsgSection.MESG_STTING_ID_SECUPGDEV;
	private static int MESG_STTING_ID_GUEST_PASSWD = Constants.MsgSection.MESG_STTING_ID_GUEST_PASSWD;
	private static int MESG_STTING_ID_TIMEZONE = Constants.MsgSection.MESG_STTING_ID_TIMEZONE;
	private static int MESG_GET_SD_CARD_CAPACITY = Constants.MsgSection.MESG_GET_SD_CARD_CAPACITY;
	private static int MESG_SD_CARD_FORMAT = Constants.MsgSection.MESG_SD_CARD_FORMAT;
	private static int MESG_SET_GPIO = Constants.MsgSection.MESG_SET_GPIO;
	private static int MESG_SET_GPI1_0 = Constants.MsgSection.MESG_SET_GPI1_0;
	private static int MESG_SET_PRE_RECORD = Constants.MsgSection.MESG_SET_PRE_RECORD;
	private static int MESG_GET_DEFENCE_AREA_SWITCH = Constants.MsgSection.MESG_GET_DEFENCE_AREA_SWITCH;
	private static int MESG_SET_DEFENCE_AREA_SWITCH = Constants.MsgSection.MESG_SET_DEFENCE_AREA_SWITCH;
	private static int MESG_SET_LAMP = Constants.MsgSection.MESG_SET_LAMP;
	private static int MESG_GET_LAMP = Constants.MsgSection.MESG_GET_LAMP;
	private static int SET_USER_DEFINE_MESG = Constants.MsgSection.SET_USER_DEFINE_MESG;
	private static int MESG_PRESET_MOTOR_POS = Constants.MsgSection.MESG_PRESET_MOTOR_POS;
	private static int MESG_GET_PRESET_MOTOR_POS = Constants.MsgSection.MESG_GET_PRESET_MOTOR_POS;
	private static int MESG_SET_PRESET_MOTOR_POS = Constants.MsgSection.MESG_SET_PRESET_MOTOR_POS;
	private static int MESG_GET_ALARM_CENTER_PARAMETER = Constants.MsgSection.MESG_GET_ALARM_CENTER_PARAMETER;
	private static int MESG_SET_ALARM_CENTER_PARAMETER = Constants.MsgSection.MESG_SET_ALARM_CENTER_PARAMETER;
	private static int MESG_DELETE_ALARMID = Constants.MsgSection.MESG_DELETE_DEVICEALARMID;
	private static int MESG_SET_SYSTEM_MESSAGE_INDEX = Constants.MsgSection.MESG_SET_SYSTEM_MESSAGE_INDEX;
	private static int CONTROL_CAMERA = Constants.MsgSection.CONTROL_CAMERA;
	private static int MESG_SET_RECEIVE_DOOBELL = Constants.MsgSection.MESG_SET_RECEIVE_DOOBELL;
	private static int MESG_GET_LANGUEGE = Constants.MsgSection.MESG_GET_LANGUEGE;
	private static int MESG_SET_LANGUEGE = Constants.MsgSection.MESG_SET_LANGUEGE;
	private static int MESG_TYPE_GET_LAN_IPC_LIST = Constants.MsgSection.MESG_TYPE_GET_LAN_IPC_LIST;
	private static int MESG_TYPE_SET_AP_MODE_CHANGE = Constants.MsgSection.MESG_TYPE_SET_AP_MODE_CHANGE;
	private static int MESG_TYPE_GET_NVRINFO = Constants.MsgSection.MESG_TYPE_GET_NVRINFO;
	private static int MESG_TYPE_SET_ZOOM = Constants.MsgSection.MESG_TYPE_SET_ZOOM;
	private static int MESG_TYPE_GET_FOCUS_ZOOM = Constants.MsgSection.MESG_TYPE_GET_FOCUS_ZOOM;
	private static int MESG_TYPE_SET_FOCUS_ZOOM = Constants.MsgSection.MESG_TYPE_SET_FOCUS_ZOOM;
	private static P2PHandler manager = null;

	private static int IP_CONFIG = Constants.MsgSection.IP_CONFIG;

	private static int MESG_SET_DEFENCE_SWITCH = Constants.MsgSection.MESG_SET_DEFENCE_SWITCH;

	public static int MESG_GET_DEFENCE_AREA_NAME = Constants.MsgSection.MESG_GET_DEFENCE_AREA_NAME;
	public static int MESG_SET_DEFENCE_AREA_NAME = Constants.MsgSection.MESG_SET_DEFENCE_AREA_NAME;

	private static int MESG_GET_GPIO = Constants.MsgSection.MESG_GET_GPIO;

	private static int MESG_GET_DEFENCE_WORK_GROUP = Constants.MsgSection.MESG_GET_DEFENCE_WORK_GROUP;
	private static int MESG_SET_DEFENCE_WORK_GROUP = Constants.MsgSection.MESG_SET_DEFENCE_WORK_GROUP;

	private static int MESG_GET_FTP_CONFIG_INFO = Constants.MsgSection.MESG_GET_FTP_CONFIG_INFO;
	private static int MESG_SET_FTP_CONFIG_INFO = Constants.MsgSection.MESG_SET_FTP_CONFIG_INFO;

	private static int GPIO_STATUS = Constants.MsgSection.GPIO_STATUS;
	private static int GET_GPIO_STATUS = Constants.MsgSection.GET_GPIO_STATUS;

	private static int GET_PIRLIGHT = Constants.MsgSection.GET_PIRLIGHT;
	private static int SET_PIRLIGHT = Constants.MsgSection.SET_PIRLIGHT;
	//鱼眼全景
	private static int GET_FISHEYE_INFO = Constants.MsgSection.GET_FISHEYE_INFO;
	private static int SET_FISHEYE_INFO = Constants.MsgSection.SET_FISHEYE_INFO;

    private static int MESG_ID_AUTO_SNAPSHOT_SWITCH=Constants.MsgSection.MESG_ID_AUTO_SNAPSHOT_SWITCH;
    //true 配置后自动间隔拍照，全屏监控黑白图像
    private static int MESG_GET_VIDEO_COLOR=Constants.MsgSection.MESG_GET_VIDEO_COLOR;
    private static int MESG_SET_VIDEO_COLOR=Constants.MsgSection.MESG_SET_VIDEO_COLOR;

	//移动侦测灵敏度
	private static int MSG_ID_SETTING_NPC_SETTINGS_MOTION_SENS = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION_SENS;
    
	private P2PHandler() {
	}

	;

	public synchronized static P2PHandler getInstance() {
		if (null == manager) {
			synchronized (P2PHandler.class) {
				manager = new P2PHandler();
			}
		}
		return manager;
	}

    /**
     * 初始化
     * @param context
     * @param p2pListener 监听类
     * @param settingListener 监听类
     */
	public void p2pInit(Context context, IP2P p2pListener,
			ISetting settingListener) {
		new MediaPlayer(context);
		MediaPlayer.getInstance().setP2PInterface(p2pListener);
		MediaPlayer.getInstance().setSettingInterface(settingListener);
	}

    /**
     * p2p连接
     * @param activeUser 登录账户
     * @param codeStr1 服务器连接的验证码
     * @param codeStr2 服务器连接的验证码
     * @return
     */
	public boolean p2pConnect(String activeUser, int codeStr1, int codeStr2) {
		// final String cHostName="|6sci.com|6sci.com.cn";
		// final String cHostName =
		// "|cloudlinks.cn|2cu.co|gwelltimes.com|cloud-links.net";

//        final String cHostName="|192.168.1.122";
		final String cHostName = "|p2p1.cloudlinks.cn|p2p3.cloud-links.net|p2p2.cloudlinks.cn|p2p4.cloud-links.net|p2p5.cloudlinks.cn|p2p6.cloudlinks.cn|p2p7.cloudlinks.cn|p2p8.cloudlinks.cn|p2p9.cloudlinks.cn|p2p10.cloudlinks.cn";
//		final String cHostName = "|P2P01.trueiot4gcctv.com|P2P02.trueiot4gcctv.com|P2P03.trueiot4gcctv.com|P2P04.trueiot4gcctv.com|P2P05.trueiot4gcctv.com|P2P06.trueiot4gcctv.com";


		// final String cHostName = "|104.250.135.115";
		// final String cHostName =
		// "|p2p1.cloudlinks.cn|p2p3.cloud-links.net|p2p2.cloudlinks.cn|p2p4.cloud-links.net";
		// final String cHostName = "|videoipcamera.cn|videoipcamera.com";
		// if (MediaPlayer.getInstance().native_p2p_connect(
		// Integer.parseInt(activeUser)|0x80000000, 886976412, codeStr1,
		// codeStr2,cHostName.getBytes() ) == 1) {
		// return true;
		// } else {
		// return false;
		// }
		int connect;
		int[] customers = Config.AppConfig.CustomerIDs;
		int[] customer_ids = new int[10];
		if (customers.length < 10) {
			for (int i = 0; i < customers.length; i++) {
				customer_ids[i] = customers[i];
			}
			for (int j = customers.length; j < 10; j++) {
				customer_ids[j] = 0;
			}
		} else {
			customer_ids = customers;
		}
		if (activeUser.equals("517401")) {
			connect = MediaPlayer.getInstance().native_p2p_connect(
					Integer.parseInt(activeUser), 886976412, codeStr1,
					codeStr2, cHostName.getBytes(), customer_ids);
		} else {
			connect = MediaPlayer.getInstance().native_p2p_connect(
					Integer.parseInt(activeUser) | 0x80000000, 886976412,
					codeStr1, codeStr2, cHostName.getBytes(), customer_ids);
		}
		if (connect == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * p2p断开
	 */
	public void p2pDisconnect() {
		MediaPlayer.getInstance().native_p2p_disconnect();
	}

    /**
     * 获取WIFI列表
     * @param contactId
     * @param password
     */
	public void getWifiList(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getWifiList");
		if (this.MSG_ID_GETTING_WIFI_LIST >= (Constants.MsgSection.MSG_ID_GETTING_WIFI_LIST)) {
			this.MSG_ID_GETTING_WIFI_LIST = Constants.MsgSection.MSG_ID_GETTING_WIFI_LIST - 1000;
		}

		MediaPlayer.iGetNPCWifiList(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_GETTING_WIFI_LIST);
		this.MSG_ID_GETTING_WIFI_LIST++;
	}

    /**
     * 设置WIFI
     * @param contactId
     * @param password
     * @param type  WIFI类型
     * @param name WIFI名
     * @param wifiPassword  WIFI密码
     */
	public void setWifi(String contactId, String password, int type,
			String name, String wifiPassword) {
		Log.e(TAG, "P2PHANDLER:setWifi");
		String s = null;
		byte[] bt;
		try {
			bt = name.getBytes("UTF-8");
			for (int i = 0; i < bt.length; i++) {
				s = s + "  " + bt[i];
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (this.MSG_ID_SETTING_WIFI >= (Constants.MsgSection.MSG_ID_SETTING_WIFI)) {
			this.MSG_ID_SETTING_WIFI = Constants.MsgSection.MSG_ID_SETTING_WIFI - 1000;
		}

		MediaPlayer.iSetNPCWifi(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_SETTING_WIFI, type,
				name.getBytes(), name.length(), wifiPassword.getBytes(),
				wifiPassword.length());
		this.MSG_ID_SETTING_WIFI++;
	}

	/**
	 * 获取NPC各种设置
	 */
	public void getNpcSettings(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getNpcSettings");
		int iPassword = Integer.MAX_VALUE;
		try {
			iPassword = Integer.parseInt(password);
		} catch (Exception e) {
			iPassword = Integer.MAX_VALUE;
		}

		if (this.MSG_ID_GETTING_NPC_SETTINGS >= (Constants.MsgSection.MSG_ID_GETTING_NPC_SETTINGS)) {
			this.MSG_ID_GETTING_NPC_SETTINGS = Constants.MsgSection.MSG_ID_GETTING_NPC_SETTINGS - 1000;
		}

		MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId), iPassword,
				this.MSG_ID_GETTING_NPC_SETTINGS);
		this.MSG_ID_GETTING_NPC_SETTINGS++;

	}

	/**
	 * 获取布放状态
	 */
	public void getDefenceStates(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getDefenceStates");
		int iPassword = Integer.MAX_VALUE;
		try {
			iPassword = Integer.parseInt(password);
		} catch (Exception e) {
			iPassword = Integer.MAX_VALUE;
		}
		if (this.MSG_ID_GET_DEFENCE_STATE >= (Constants.MsgSection.MSG_ID_GET_DEFENCE_STATE)) {
			this.MSG_ID_GET_DEFENCE_STATE = Constants.MsgSection.MSG_ID_GET_DEFENCE_STATE - 1000;
		}

		MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId), iPassword,
				this.MSG_ID_GET_DEFENCE_STATE);
		this.MSG_ID_GET_DEFENCE_STATE++;
	}

	/**
	 * 检查密码
	 */
	public void checkPassword(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:checkPassword");
		if(contactId==null||contactId.equals("")||password==null||password.equals("")){
			return;
		}
		if (this.MSG_ID_CHECK_DEVICE_PASSWORD >= (Constants.MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD)) {
			this.MSG_ID_CHECK_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD - 1000;
		}

		MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_CHECK_DEVICE_PASSWORD);
		this.MSG_ID_CHECK_DEVICE_PASSWORD++;
	}

	/**
	 * 获取防区设置
	 */
	public void getDefenceArea(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getDefenceArea");
		if (this.MSG_ID_GETTING_DEFENCEAREA >= (Constants.MsgSection.MSG_ID_GETTING_DEFENCEAREA)) {
			this.MSG_ID_GETTING_DEFENCEAREA = Constants.MsgSection.MSG_ID_GETTING_DEFENCEAREA - 1000;
		}

		MediaPlayer.iGetAlarmCodeStatus(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_GETTING_DEFENCEAREA);
		this.MSG_ID_GETTING_DEFENCEAREA++;
	}

    /**
     * 设置远程布撤防
     * @param contactId
     * @param password
     * @param value  0 OFF  1 ON
     */
	public void setRemoteDefence(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setRemoteDefence");
		if (this.MSG_ID_SET_REMOTE_DEFENCE >= (Constants.MsgSection.MSG_ID_SET_REMOTE_DEFENCE)) {
			this.MSG_ID_SET_REMOTE_DEFENCE = Constants.MsgSection.MSG_ID_SET_REMOTE_DEFENCE - 1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_SET_REMOTE_DEFENCE,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_DEFENCE,
				value);
		this.MSG_ID_SET_REMOTE_DEFENCE++;
	}

	// public void SetSystemMessageIndex(int iSystemMessageType,
	// int iSystemMessageIndex) {
	// MediaPlayer.SetSystemMessageIndex(iSystemMessageType,
	// iSystemMessageIndex);
	// }

	public void vSendWiFiCmd(int iType, byte[] SSID, int iSSIDLen,
			byte[] Password, int iPasswordLen) {
		MediaPlayer.vSendWiFiCmd(iType, SSID, iSSIDLen, Password, iPasswordLen);
	}

    /**
     * 设置远程录像
     * @param contactId
     * @param password
     * @param value 0 OFF  1 ON
     */
	public void setRemoteRecord(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setRemoteRecord");
		if (this.MSG_ID_SET_REMOTE_RECORD >= (Constants.MsgSection.MSG_ID_SET_REMOTE_RECORD)) {
			this.MSG_ID_SET_REMOTE_RECORD = Constants.MsgSection.MSG_ID_SET_REMOTE_RECORD - 1000;
		}
		MediaPlayer
				.iSetNPCSettings(
						Integer.parseInt(contactId),
						Integer.parseInt(password),
						this.MSG_ID_SET_REMOTE_RECORD,
						Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_RECORD,
						value);
		this.MSG_ID_SET_REMOTE_RECORD++;
	}

    /**
     * 设置设备时间
     * @param contactId
     * @param password
     * @param time 时间  格式"0000-00-00 :00:00"
     */
	public void setDeviceTime(String contactId, String password, String time) {
		Log.e(TAG, "P2PHANDLER:setDeviceTime");
		if (this.MSG_ID_SETTING_DEVICE_TIME >= (Constants.MsgSection.MSG_ID_SETTING_DEVICE_TIME)) {
			this.MSG_ID_SETTING_DEVICE_TIME = Constants.MsgSection.MSG_ID_SETTING_DEVICE_TIME - 1000;
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date date = null;
		try {
			date = df.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int parseTime = 0;

		if (null != date) {

			if (time.substring(11, 13).equals("12")) {
				parseTime = ((calendar.get(Calendar.YEAR) - 2000) << 24)
						| (((calendar.get(Calendar.MONTH) + 1) << 18))
						| (calendar.get(Calendar.DAY_OF_MONTH) << 12)
						| (12 << 6) | (calendar.get(Calendar.MINUTE) << 0);
			} else {
				parseTime = ((calendar.get(Calendar.YEAR) - 2000) << 24)
						| (((calendar.get(Calendar.MONTH) + 1) << 18))
						| (calendar.get(Calendar.DAY_OF_MONTH) << 12)
						| (calendar.get(Calendar.HOUR_OF_DAY) << 6)
						| (calendar.get(Calendar.MINUTE) << 0);
			}

		}
		MediaPlayer.iSetNPCDateTime(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_SETTING_DEVICE_TIME,
				parseTime);
		this.MSG_ID_SETTING_DEVICE_TIME++;
	}

	/**
	 * 获取设备时间
	 */
	public void getDeviceTime(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getDeviceTime");
		if (this.MSG_ID_GETTING_DEVICE_TIME >= (Constants.MsgSection.MSG_ID_GETTING_DEVICE_TIME)) {
			this.MSG_ID_GETTING_DEVICE_TIME = Constants.MsgSection.MSG_ID_GETTING_DEVICE_TIME - 1000;
		}

		MediaPlayer.iGetNPCDateTime(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_GETTING_DEVICE_TIME);
		this.MSG_ID_GETTING_DEVICE_TIME++;
	}

    /**
     * 设置设备音量
     * @param contactId
     * @param password
     * @param value 音量值 0-9
     */
	public void setVideoVolume(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setVideoVolume");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_VOLUME, value);
		this.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME++;
	}

    /**
     * 设置视频格式
     * @param contactId
     * @param password
     * @param value 视频制式  0 NTSC  1 PAL
     */
	public void setVideoFormat(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setVideoFormat");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_VIDEO_FORMAT, value);
		this.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT++;
	}

    /**
     * 设置录像类型
     * @param contactId
     * @param password
     * @param type 0 手动录像  1 报警录像  2 定时录像
     */
	public void setRecordType(String contactId, String password, int type) {
		Log.e(TAG, "P2PHANDLER:setRecordType");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TYPE, type);
		this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE++;
	}

    /**
     * 设置报警录像时间
     * @param contactId
     * @param password
     * @param time  0 1分钟  1 2分钟  2 分钟
     */
	public void setRecordTime(String contactId, String password, int time) {
		Log.e(TAG, "P2PHANDLER:setRecordTime");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TIME, time);
		this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME++;
	}

    /**
     * 设置定时录像时间
     * @param contactId
     * @param password
     * @param time 时间值 "00:00-00:00"
     */
	public void setRecordPlanTime(String contactId, String password, String time) {
		Log.e(TAG, "P2PHANDLER:setRecordPlanTime");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME - 1000;
		}

		int iTime = MyUtils.convertPlanTime(time);
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_PLAN_TIME,
				iTime);
		this.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME++;
	}

    /**
     * 设置防区状态
     * @param contactId
     * @param password
     * @param group 防区通道
     * @param item 防区
     * @param type 0 学习防区  1 清除防区  2 清除通道
     */
	public void setDefenceAreaState(String contactId, String password,
			int group, int item, int type) {
		Log.e(TAG, "P2PHANDLER:setDefenceAreaState");
		if (this.MSG_ID_SETTING_DEFENCEAREA >= (Constants.MsgSection.MSG_ID_SETTING_DEFENCEAREA)) {
			this.MSG_ID_SETTING_DEFENCEAREA = Constants.MsgSection.MSG_ID_SETTING_DEFENCEAREA - 1000;
		}

		MediaPlayer.iSetAlarmCodeStatus(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_SETTING_DEFENCEAREA, 1,
				type, new int[] { group }, new int[] { item });
		this.MSG_ID_SETTING_DEFENCEAREA++;
	}

    /**
     * 清空防区状态
     * @param contactId
     * @param password
     * @param group 防区通道
     */
	public void clearDefenceAreaState(String contactId, String password,
			int group) {
		Log.e(TAG, "P2PHANDLER:setDefenceAreaState");
		if (this.MSG_ID_CLEAR_DEFENCE_GROUP >= (Constants.MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP)) {
			this.MSG_ID_CLEAR_DEFENCE_GROUP = Constants.MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP - 1000;
		}

		MediaPlayer.iClearAlarmCodeGroup(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_CLEAR_DEFENCE_GROUP,
				group);

		this.MSG_ID_CLEAR_DEFENCE_GROUP++;
	}

    /**
     * 设置网络类型
     * @param contactId
     * @param password
     * @param type  0 有线  1 WIFI
     */
	public void setNetType(String contactId, String password, int type) {
		Log.e(TAG, "P2PHANDLER:setNetType");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_NET_TYPE, type);
		this.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE++;
	}

    /**
     * 设置绑定报警ID
     * @param contactId
     * @param password
     * @param count  ID数量
     * @param datas ID的string数组
     */
	public void setBindAlarmId(String contactId, String password, int count,
			String[] datas) {
		Log.e(TAG, "P2PHANDLER:setBindAlarmId");
		if (this.MSG_ID_SETTING_ALARM_BIND_ID >= (Constants.MsgSection.MSG_ID_SETTING_ALARM_BIND_ID)) {
			this.MSG_ID_SETTING_ALARM_BIND_ID = Constants.MsgSection.MSG_ID_SETTING_ALARM_BIND_ID - 1000;
		}
		int[] iData = new int[datas.length];
		try {

			for (int i = 0; i < datas.length; i++) {
				iData[i] = Integer.parseInt(datas[i]);
			}
		} catch (Exception e) {
			iData = new int[] { 0 };
			count = 1;
		}
		MediaPlayer.iSetBindAlarmId(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_SETTING_ALARM_BIND_ID,
				count, iData);
		this.MSG_ID_SETTING_ALARM_BIND_ID++;
	}

	/**
	 * 获取绑定报警ID
	 */
	public void getBindAlarmId(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getBindAlarmId");
		if (this.MSG_ID_GETTING_ALARM_BIND_ID >= (Constants.MsgSection.MSG_ID_GETTING_ALARM_BIND_ID)) {
			this.MSG_ID_GETTING_ALARM_BIND_ID = Constants.MsgSection.MSG_ID_GETTING_ALARM_BIND_ID - 1000;
		}

		MediaPlayer.iGetBindAlarmId(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_GETTING_ALARM_BIND_ID);
		this.MSG_ID_GETTING_ALARM_BIND_ID++;
	}

    /**
     * 设置报警邮箱
     * @param contactId
     * @param password
     * @param email 邮箱
     */
	public void setAlarmEmail(String contactId, String password, String email) {
		Log.e(TAG, "P2PHANDLER:setAlarmEmail");
		if (this.MSG_ID_SETTING_ALARM_EMAIL >= (Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL)) {
			this.MSG_ID_SETTING_ALARM_EMAIL = Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL - 1000;
		}

		MediaPlayer.iSetNPCEmail(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_SETTING_ALARM_EMAIL,
				email.getBytes(), email.length());
		this.MSG_ID_SETTING_ALARM_EMAIL++;
	}

	/**
	 * 设置报警邮箱带SMTP参数（添加##结束标记并用0补齐8的整数位）
	 * 
	 * @param contactId
	 * @param password
	 * @param boption
	 *            操作标记
	 * @param emailaddress
	 *            收件人邮箱地址
	 * @param port
	 *            发件人的邮局SMTP端口参数
	 * @param server
	 *            发件人的邮局SMTP服务参数
	 * @param user
	 *            发件人邮箱
	 * @param pwd
	 *            发件人邮箱密码
	 * @param subject
	 *            发件主题
	 * @param content
	 *            发件邮件内容
	 */
	public void setAlarmEmailWithSMTP(String contactId, String password,
			byte boption, String emailaddress, int port, String server,
			String user, String pwd, String subject, String content,
			byte Entry, byte reserve1, int reserve2) {
		Log.e(TAG, "P2PHANDLER:setAlarmEmail");
		if (this.MSG_ID_SETTING_ALARM_EMAIL >= (Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL)) {
			this.MSG_ID_SETTING_ALARM_EMAIL = Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL - 1000;
		}
		String pwds = pwd + "##";
		int m = pwds.length();
		int k = 8 - m % 8;
		for (int i = 0; i < k; i++) {
			pwds = pwds + "0";
		}
		byte[] ppp = null;
		try {
			ppp = DES.des(pwds.getBytes(), 0);
		} catch (Exception e) {
			ppp = new byte[] { 0 };
			e.printStackTrace();
		}
		Log.e("alarm_email", "contactId=" + contactId + "--" + "password="
				+ Integer.parseInt(password) + "--" + "boption=" + boption
				+ "--" + "emailaddress=" + emailaddress + "--" + "port=" + port
				+ "--" + "server=" + server + "--" + "user=" + user + "--"
				+ "ppp=" + ppp + "--" + "subject=" + subject + "--"
				+ "content=" + content + "--" + "Entry=" + Entry + "--"
				+ "reserve1=" + reserve1 + "--" + "reserve2=" + reserve1);
		MediaPlayer.SetRobortEmailNew(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_SETTING_ALARM_EMAIL,
				boption, emailaddress, port, server, user, ppp, subject,
				content, Entry, (byte) reserve1, reserve2, ppp.length);
		this.MSG_ID_SETTING_ALARM_EMAIL++;
	}

	/**
	 * 获取报警邮箱
	 */
	public void getAlarmEmail(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getAlarmEmail");
		if (this.MSG_ID_GETTING_ALARM_EMAIL >= (Constants.MsgSection.MSG_ID_GETTING_ALARM_EMAIL)) {
			this.MSG_ID_GETTING_ALARM_EMAIL = Constants.MsgSection.MSG_ID_GETTING_ALARM_EMAIL - 1000;
		}

		MediaPlayer.iGetNPCEmail(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MSG_ID_GETTING_ALARM_EMAIL);
		this.MSG_ID_GETTING_ALARM_EMAIL++;
	}

    /**
     * 设置蜂鸣器
     * @param contactId
     * @param password
     * @param value  0 OFF  开启蜂鸣： 1 一分钟  2 两分钟  3 三分钟
     */
	public void setBuzzer(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setBuzzer");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_BUZZER >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_BUZZER = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_BUZZER,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_BUZZER, value);
		this.MSG_ID_SETTING_NPC_SETTINGS_BUZZER++;
	}

    /**
     * 设置移动侦测
     * @param contactId
     * @param password
     * @param value 0 OFF  1 ON
     */
	public void setMotion(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setMotion");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_MOTION >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_MOTION = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_MOTION,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_MOTION_DECT, value);
		this.MSG_ID_SETTING_NPC_SETTINGS_MOTION++;
	}

    /**
     * 设置初始密码
     * @param contactId ip地址
     * @param password  初始密码
     * @param RTSPPwd   用户密码
     * @param userPassword  用户密码
     */
	public void setInitPassword(String contactId, String password,
			String RTSPPwd, String userPassword) {
		Log.e(TAG, "P2PHANDLER:setInitPassword");
		if (this.MSG_ID_SETTING_INIT_PASSWORD >= (Constants.MsgSection.MSG_ID_SETTING_INIT_PASSWORD)) {
			this.MSG_ID_SETTING_INIT_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_INIT_PASSWORD - 1000;
		}
		byte[] EntryPwd = getPwdBytes(userPassword);
		int pwdLen = EntryPwd.length;
		String rtsp = "admin:HIipCamera:" + RTSPPwd;
		String result = MediaPlayer.RTSPEntry(rtsp);
		byte[] rtspPwd = "errror".equals(result) ? new byte[32] : result
				.getBytes();
		MediaPlayer.iSetInitPassword(Integer.parseInt(contactId), 0,
				this.MSG_ID_SETTING_INIT_PASSWORD, Integer.parseInt(password),
				rtspPwd, Integer.parseInt(contactId), pwdLen, EntryPwd);
		this.MSG_ID_SETTING_INIT_PASSWORD++;
	}

    /**
     * 设置设备密码
     * @param contactId
     * @param oldPassword  旧的密码
     * @param newPassword  新的密码
     */
	public void setDevicePassword(String contactId, String oldPassword,
			String newPassword) {
		Log.e(TAG, "P2PHANDLER:setDevicePassword");
		if (this.MSG_ID_SETTING_DEVICE_PASSWORD >= (Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD)) {
			this.MSG_ID_SETTING_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(oldPassword),
				this.MSG_ID_SETTING_DEVICE_PASSWORD,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_DEVICE_PWD,
				Integer.parseInt(newPassword));
		this.MSG_ID_SETTING_DEVICE_PASSWORD++;
	}

    /**
     * 设置设备密码，NVR专用
     * @param contactId
     * @param oldPassword 旧的数字密码
     * @param newPassword 新的数字密码
     * @param RTSPPwd  新的密码
     * @param userPwd  新的密码
     */
	public void setDevicePassword(String contactId, String oldPassword,
			String newPassword, String RTSPPwd, String userPwd) {
		Log.e(TAG, "P2PHANDLER:setDevicePassword");
		if (this.MSG_ID_SETTING_DEVICE_PASSWORD >= (Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD)) {
			this.MSG_ID_SETTING_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD - 1000;
		}
		byte[] tt = new byte[32];
		int len = userPwd.length();
		byte[] ss = userPwd.getBytes();
		System.arraycopy(ss, 0, tt, 0, ss.length);
		for (int i = 0; i < tt.length / 8; i++) {
			byte[] de = new byte[8];
			System.arraycopy(tt, i * 8, de, 0, de.length);
			byte[] entry = P2PEntryPassword(de);
			System.arraycopy(entry, 0, tt, i * 8, entry.length);
		}
		String rtsp = "admin:HIipCamera:" + RTSPPwd;
		String result = MediaPlayer.RTSPEntry(rtsp);
		byte[] rtspPwd = "errror".equals(result) ? new byte[32] : result
				.getBytes();
		MediaPlayer.iSetDevicePwd(Integer.parseInt(contactId),
				Integer.parseInt(oldPassword),
				this.MSG_ID_SETTING_DEVICE_PASSWORD,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_DEVICE_PWD,
				Integer.parseInt(newPassword), rtspPwd, len, tt);
		this.MSG_ID_SETTING_DEVICE_PASSWORD++;
	}

    /**
     * 设置访客密码
     * @param contactId
     * @param oldPassword 管理员密码
     * @param visitorPassword  访客密码
     */
	public void setDeviceVisitorPassword(String contactId, String oldPassword,
			String visitorPassword) {
		Log.e(TAG, "P2PHANDLER:setDevicePassword");
		if (this.MESG_STTING_ID_GUEST_PASSWD >= (Constants.MsgSection.MESG_STTING_ID_GUEST_PASSWD)) {
			this.MESG_STTING_ID_GUEST_PASSWD = Constants.MsgSection.MESG_STTING_ID_GUEST_PASSWD - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(oldPassword),
				this.MESG_STTING_ID_GUEST_PASSWD,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_GUEST_PASSWD,
				Integer.parseInt(visitorPassword));
		// MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
		// Integer.parseInt(oldPassword),
		// this.MESG_STTING_ID_GUEST_PASSWD,
		// Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_VISITORPWD,
		// Integer.parseInt(visitorPassword));
		this.MESG_STTING_ID_GUEST_PASSWD++;
	}

    /**
     * 获取好友状态
     * @param contactIds 设备ID组
     */
	public void getFriendStatus(String[] contactIds) {
		Log.e(TAG, "P2PHANDLER:getFriendStatus");
		int[] friends = new int[contactIds.length];
		for (int i = 0; i < contactIds.length; i++) {
			if (contactIds[i].substring(0, 1).equals("0")) {
				friends[i] = Integer.parseInt(contactIds[i]) | 0x80000000;
			} else {
				friends[i] = Integer.parseInt(contactIds[i]);
			}
		}
		MediaPlayer.iGetFriendsStatus(friends, friends.length);
	}

    /**
     * 获取录像列表
     * @param contactId
     * @param password
     * @param timeInterval
     */
    @Deprecated
	public void getRecordFiles(String contactId, String password,
			int timeInterval) {
		Log.e(TAG, "P2PHANDLER:getRecordFiles");
		if (this.MSG_ID_GETTING_RECORD_FILE_LIST >= (Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST)) {
			this.MSG_ID_GETTING_RECORD_FILE_LIST = Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST - 1000;
		}
		Timestamp now = new Timestamp(System.currentTimeMillis());
		int i_start;
		if (now.getDate() < timeInterval) {
			i_start = ((now.getYear() - 70) << 24)
					| (((now.getMonth()) << 18))
					| ((timeInterval - now.getDate()) << 12)
					| ((now.getHours()) << 6) | ((now.getMinutes()) << 0);
		} else {
			i_start = ((now.getYear() - 70) << 24)
					| (((now.getMonth() + 1) << 18))
					| ((now.getDate() - timeInterval) << 12)
					| ((now.getHours()) << 6) | ((now.getMinutes()) << 0);
		}
		int i_end = ((now.getYear() - 70) << 24)
				| (((now.getMonth() + 1) << 18)) | ((now.getDate()) << 12)
				| ((now.getHours()) << 6) | ((now.getMinutes()) << 0);
		Log.e("timestamp", "year" + now.getYear() + "month" + now.getMonth()
				+ "hour" + now.getHours());
		Log.e("timestamp", "i_start=" + i_start + "i_end=" + i_end);
		MediaPlayer.iGetRecFiles(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_GETTING_RECORD_FILE_LIST, i_start, i_end);
		this.MSG_ID_GETTING_RECORD_FILE_LIST++;
	}

    /**
     * 获取录像列表
     * @param contactId
     * @param password
     * @param start 查询的开始的时间
     * @param end  查询的结束的时间
     */
	public void getRecordFiles(String contactId, String password, Date start,
			Date end) {
		Log.e(TAG, "P2PHANDLER:getRecordFiles");
		if (this.MSG_ID_GETTING_RECORD_FILE_LIST >= (Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST)) {
			this.MSG_ID_GETTING_RECORD_FILE_LIST = Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST - 1000;
		}

		int i_start = ((start.getYear() - 70) << 24)
				| (((start.getMonth() + 1) << 18)) | ((start.getDate()) << 12)
				| ((start.getHours()) << 6) | ((start.getMinutes()) << 0);
		int i_end = ((end.getYear() - 70) << 24)
				| (((end.getMonth() + 1) << 18)) | ((end.getDate()) << 12)
				| ((end.getHours()) << 6) | ((end.getMinutes()) << 0);
		Log.e("timestamp", "i_start=" + i_start + "i_end=" + i_end);
		MediaPlayer.iGetRecFiles(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_GETTING_RECORD_FILE_LIST, i_start, i_end);
		this.MSG_ID_GETTING_RECORD_FILE_LIST++;
	}

    /**
     * 发送端消息
     * @param contactId
     * @param msg  消息内容
     * @return
     */
	public String sendMessage(String contactId, String msg) {
		Log.e(TAG, "P2PHANDLER:sendMessage");
		if (this.MSG_ID_SEND_MESSAGE >= (Constants.MsgSection.MSG_ID_SEND_MESSAGE)) {
			this.MSG_ID_SEND_MESSAGE = Constants.MsgSection.MSG_ID_SEND_MESSAGE - 1000;
		}
		int iId = Integer.parseInt(contactId) | 0x80000000;
		MediaPlayer.iSendMesgToFriend(iId, this.MSG_ID_SEND_MESSAGE,
				msg.getBytes(), msg.getBytes().length);
		this.MSG_ID_SEND_MESSAGE++;
		return String.valueOf(this.MSG_ID_SEND_MESSAGE - 1);
	}

    /**
     * 发送自定义命令
     * @param contactId
     * @param password
     * @param msg  命令
     * @return
     */
	public String sendCustomCmd(String contactId, String password, String msg) {
		Log.e(TAG, "P2PHANDLER:sendCustomCmd");
		if (this.MSG_ID_SEND_CUSTOM_CMD >= (Constants.MsgSection.MSG_ID_SEND_CUSTOM_CMD)) {
			this.MSG_ID_SEND_CUSTOM_CMD = Constants.MsgSection.MSG_ID_SEND_CUSTOM_CMD - 1000;
		}
		int iId = Integer.parseInt(contactId);
		MediaPlayer.iSendCmdToFriend(iId, Integer.parseInt(password),
				this.MSG_ID_SEND_CUSTOM_CMD, msg.getBytes(),
				msg.getBytes().length);
		this.MSG_ID_SEND_CUSTOM_CMD++;
		return String.valueOf(this.MSG_ID_SEND_CUSTOM_CMD - 1);
	}

	/**
	 * 打开音频设备并准备播放
	 * @param callType 监控类型
	 */
	public void openAudioAndStartPlaying(int callType) {
		try {
			MediaPlayer.getInstance().openAudioTrack();
			MediaPlayer.getInstance()._StartPlaying(
					Constants.P2P_WINDOW.P2P_SURFACE_START_PLAYING_WIDTH,
					Constants.P2P_WINDOW.P2P_SURFACE_START_PLAYING_HEIGHT,
					callType);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 挂断p2p
	 */
	public synchronized void reject() {
		synchronized (P2PHandler.this) {
			MediaPlayer.getInstance().native_p2p_hungup();
		}

	}

	/**
	 * 接听
	 */
	public void accept() {
		MediaPlayer.getInstance().native_p2p_accpet();
	}

	/**
	 * RTSP
	 */
	public void RTSPConnect(final String contactId, final String password,
			final boolean isOutCall, final int callType, final String callId,
			final String ipFlag, final String pushMsg, String ipAddress,
			int VideoMode, Handler rtspHandler, String headerID) {
		new RtspThread(ipAddress, rtspHandler, contactId, password, isOutCall,
				callType, callId, ipFlag, pushMsg, VideoMode, headerID).start();

	}


    /**
     * 呼叫设备
     * @param contactId APPID
     * @param password
     * @param isOutCall  是否对外呼出
     * @param callType   呼叫类型
     * @param callId     设备ID
     * @param ipFlag     IP地址
     * @param pushMsg    附带消息
     * @param VideoMode  视频清晰度
     * @param headerID  截图时头像命名ID
     * @param subtype   设备子类型
     * @param videow    视频宽度
     * @param videoh    视频高度
     * @param fishpos   全景模式安装方式
     * @return
     */
	public boolean call(final String contactId, final String password,
			final boolean isOutCall, final int callType, final String callId,
			final String ipFlag, final String pushMsg, int VideoMode,
			String headerID,int subtype,int videow,int videoh,int fishpos) {
		boolean result = false;
		long headerId = Long.parseLong(headerID);
		byte[] byt = new byte[8];
		try {
			if (isOutCall) {
				String parseNum = callId;
				if (parseNum.contains("+")) {
					boolean isPhone = false;
					for (int i = 0; i < regionCode.length; i++) {
						int cLength = String.valueOf(regionCode[i]).length();
						parseNum = parseNum.replace("+", "");
						String hight = parseNum.substring(0, cLength);
						String low = parseNum.substring(cLength,
								parseNum.length());
						if (Integer.parseInt(hight) == regionCode[i]) {
							long num = (Long.parseLong(hight) << 48 | Long
									.parseLong(low));
							parseNum = String.valueOf(num);
							isPhone = true;
							break;
						}
					}
					if (!isPhone) {
						return result;
					}
				}

				long id = Long.parseLong(parseNum);
				if (parseNum.charAt(0) == '0') {
					id = 0 - id;
				}

				int pwd = 0;

				int isMonitor = 0;
				if (callType == Constants.P2P_TYPE.P2P_TYPE_MONITOR) {
					isMonitor = 1;
					pwd = Integer.parseInt(password);
				}

				int x = 0;
				if (null != ipFlag && !ipFlag.equals("")
						&& MyUtils.isNumeric(ipFlag)) {
					x = MediaPlayer.getInstance().native_p2p_call(
							Integer.parseInt(ipFlag), isMonitor, pwd, -1,
							VideoMode, byt, pushMsg.getBytes("utf-8"), "gwell",
							headerId,subtype,videow,videoh,fishpos);
				} else {
					x = MediaPlayer.getInstance().native_p2p_call(id,
							isMonitor, pwd, -1, VideoMode, byt,
							pushMsg.getBytes("utf-8"), "gwell", headerId,subtype,videow,videoh,fishpos);
				}

				if (x == 1) {
					result = true;
					Log.i("tag", "p2p call success");
				} else {
					Log.e("tag", "p2p call fail");
				}

			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;

		// new DelayThread(1000, new DelayThread.OnRunListener() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// try {
		// synchronized (P2PHandler.this) {
		// call_alter(contactId, password, isOutCall, callType,
		// callId, ipFlag, pushMsg);
		// }
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// }).start();
	}
	
	public boolean call(final String contactId, final String password,
			final boolean isOutCall, final int callType, final String callId,
			final String ipFlag, final String pushMsg, int VideoMode,
			String headerID){
		return call(contactId,password,isOutCall,callType,callId,ipFlag,pushMsg,VideoMode,headerID,0,0,0,0);
	}

	private static int[] regionCode = { 1264, 1268, 1242, 1246, 1441, 1284,
			1345, 1767, 1809, 1473, 1876, 1664, 1787, 1869, 1758, 1784, 1868,
			1649, 1340, 1671, 1670, 210, 211, 212, 213, 214, 215, 216, 217,
			218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230,
			231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243,
			244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256,
			257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269,
			290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 350, 351, 352,
			353, 354, 355, 356, 357, 358, 359, 370, 371, 372, 373, 374, 375,
			376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388,
			389, 420, 421, 422, 423, 424, 425, 426, 427, 428, 429, 500, 501,
			502, 503, 504, 505, 506, 507, 508, 509, 590, 591, 592, 593, 594,
			595, 596, 597, 598, 599, 670, 671, 672, 673, 674, 675, 676, 677,
			678, 679, 680, 681, 682, 683, 684, 685, 686, 687, 688, 689, 690,
			691, 692, 693, 694, 695, 696, 697, 698, 699, 800, 801, 802, 803,
			804, 805, 806, 807, 808, 809, 850, 851, 852, 853, 854, 855, 856,
			857, 858, 859, 870, 871, 872, 873, 874, 875, 876, 877, 878, 879,
			880, 881, 882, 883, 884, 885, 886, 887, 888, 889, 960, 961, 962,
			963, 964, 965, 966, 967, 968, 969, 970, 971, 972, 973, 974, 975,
			976, 977, 978, 979, 990, 991, 992, 993, 994, 995, 996, 997, 998,
			999, 20, 27, 28, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40, 41, 42,
			43, 44, 45, 46, 47, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 60, 61,
			62, 63, 64, 65, 66, 81, 82, 83, 84, 86, 90, 91, 92, 93, 94, 95, 98,
			1, 7 };

	private void call_alter(String threeNumber, String password,
			boolean isOutCall, int callType, String callId, String ipFlag,
			String pushMsg, int VideoMode, String headerID,int subtype,int videow,int videoh,int fishPos)
			throws UnsupportedEncodingException {
		byte[] byt = new byte[8];
		long headerId = Long.parseLong(headerID);
		if (isOutCall) {
			String parseNum = callId;
			if (parseNum.contains("+")) {
				boolean isPhone = false;
				try {
					for (int i = 0; i < regionCode.length; i++) {
						int cLength = String.valueOf(regionCode[i]).length();
						parseNum = parseNum.replace("+", "");
						String hight = parseNum.substring(0, cLength);
						String low = parseNum.substring(cLength,
								parseNum.length());
						if (Integer.parseInt(hight) == regionCode[i]) {
							long num = (Long.parseLong(hight) << 48 | Long
									.parseLong(low));
							parseNum = String.valueOf(num);
							isPhone = true;
							break;
						}
					}
				} catch (Exception e) {
					// onCallResult(Constants.P2P_CALL.CALL_RESULT.CALL_PHONE_FORMAT_ERROR);
				}

				if (!isPhone) {
					// onCallResult(Constants.P2P_CALL.CALL_RESULT.CALL_PHONE_FORMAT_ERROR);
					return;
				}
			}

			long id = Long.parseLong(parseNum);
			if (parseNum.charAt(0) == '0') {
				id = 0 - id;
			}

			int pwd = 0;

			int isMonitor = 0;
			if (callType == Constants.P2P_TYPE.P2P_TYPE_MONITOR) {
				isMonitor = 1;
				if (MyUtils.isNumeric(password)) {
					pwd = Integer.parseInt(password);
				}
			}

			int result = 0;
			if (null != ipFlag && !ipFlag.equals("")
					&& MyUtils.isNumeric(ipFlag)) {
				result = MediaPlayer.getInstance().native_p2p_call(
						Integer.parseInt(ipFlag), isMonitor, pwd, -1,
						VideoMode, byt, pushMsg.getBytes("utf-8"), "gwell",
						headerId,subtype,videow,videoh,fishPos);
			} else {
				result = MediaPlayer.getInstance().native_p2p_call(id,
						isMonitor, pwd, -1, VideoMode, byt,
						pushMsg.getBytes("utf-8"), "gwell", headerId,subtype,videow,videoh,fishPos);
			}

			if (result == 1) {
				Log.i("tag", "p2p call success");
			} else {
				Log.e("tag", "p2p call fail");
			}
		}
	}

    /**
     * 录像回放连接
     * @param contactId
     * @param password
     * @param filename  文件名
     * @param recordFilePosition  文件在文件组的位置
     * @param VideoMode 录像清晰度
     * @param subtype  设备子类型
     * @param videow  视频宽度
     * @param videoh  视频高度
     * @param fishPos 全景设备安装方式
     * 回放清晰度没有作用  录像时清晰度是什么格式的回放时就是什么格式的   填0
     * 后四个参数全景设备专用  非全景设备全部填0
     */
	public void playbackConnect(String contactId, String password,
			String filename, int recordFilePosition, int VideoMode,int subtype,int videow,int videoh,int fishPos) {
		byte[] byt = filename.getBytes();
		MediaPlayer.getInstance().native_p2p_call(Integer.parseInt(contactId),
				Constants.P2P_TYPE.P2P_TYPE_PLAYBACK,
				Integer.parseInt(password), recordFilePosition, VideoMode, byt,
				"".getBytes(), "gwell", Integer.parseInt(contactId),subtype,videow,videoh,fishPos);
	}

    /**
     * 设置视频模式
     * @param type  5:SD  6:LD  7:HD
     * @return
     */
	public int setVideoMode(int type) {
		return MediaPlayer.getInstance().iSetVideoMode(type);
	}

	public void checkDeviceUpdate(String contactId, String password) {

		Log.e(TAG, "P2PHANDLER:checkDeviceUpdate");
		if (this.MSG_ID_CHECK_DEVICE_UPDATE >= (Constants.MsgSection.MSG_ID_CHECK_DEVICE_UPDATE)) {
			this.MSG_ID_CHECK_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_CHECK_DEVICE_UPDATE - 1000;
		}

		MediaPlayer.getInstance().checkDeviceUpdate(
				Integer.parseInt(contactId), Integer.parseInt(password),
				this.MSG_ID_CHECK_DEVICE_UPDATE);
		this.MSG_ID_CHECK_DEVICE_UPDATE++;
	}

	/**
	 * 设备固件更新
	 * @param contactId
	 * @param password
	 */
	public void doDeviceUpdate(String contactId, String password) {

		Log.e(TAG, "P2PHANDLER:doDeviceUpdate");
		if (this.MSG_ID_DO_DEVICE_UPDATE >= (Constants.MsgSection.MSG_ID_DO_DEVICE_UPDATE)) {
			this.MSG_ID_DO_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_DO_DEVICE_UPDATE - 1000;
		}

		MediaPlayer.getInstance().doDeviceUpdate(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MSG_ID_DO_DEVICE_UPDATE);
		this.MSG_ID_DO_DEVICE_UPDATE++;

	}

	/**
	 * 取消设备固件更新
	 * @param contactId
	 * @param password
	 */
	public void cancelDeviceUpdate(String contactId, String password) {

		Log.e(TAG, "P2PHANDLER:cancelDeviceUpdate");
		if (this.MSG_ID_CANCEL_DEVICE_UPDATE >= (Constants.MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE)) {
			this.MSG_ID_CANCEL_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE - 1000;
		}

		MediaPlayer.getInstance().cancelDeviceUpdate(
                Integer.parseInt(contactId), Integer.parseInt(password),
                this.MSG_ID_CANCEL_DEVICE_UPDATE);
		this.MSG_ID_CANCEL_DEVICE_UPDATE++;

	}

	/**
	 * 获得固件版本
	 * @param contactId
	 * @param password
	 */
	public void getDeviceVersion(String contactId, String password) {

		Log.e(TAG, "P2PHANDLER:getDeviceVersion");
		if (this.MSG_ID_GET_DEVICE_VERSION >= (Constants.MsgSection.MSG_ID_GET_DEVICE_VERSION)) {
			this.MSG_ID_GET_DEVICE_VERSION = Constants.MsgSection.MSG_ID_GET_DEVICE_VERSION - 1000;
		}

		MediaPlayer.getInstance().getDeviceVersion(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MSG_ID_GET_DEVICE_VERSION);
		this.MSG_ID_GET_DEVICE_VERSION++;
	}

	public boolean sendCtlCmd(int cmd, int option) {
		if (MediaPlayer.iSendCtlCmd(cmd, option) == 1) {
			return true;
		} else {
			return false;
		}
	}

	public void setBindFlag(int flag) {
		MediaPlayer.setBindFlag(flag);
	}

	/**
	 * 设置是否接收音视频数据在高端2CU中单独设置无效
	 * @param fgEn
	 */
	public void setRecvAVDataEnable(boolean fgEn) {
		MediaPlayer.getInstance()._SetRecvAVDataEnable(fgEn);
	}

	public void setImageReverse(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setImageReverse");
		if ((this.MESG_ID_STTING_PIC_REVERSE) >= (Constants.MsgSection.MESG_ID_STTING_PIC_REVERSE)) {
			this.MESG_ID_STTING_PIC_REVERSE = Constants.MsgSection.MESG_ID_STTING_PIC_REVERSE - 1000;
		}
		MediaPlayer
				.iSetNPCSettings(
						Integer.parseInt(contactId),
						Integer.parseInt(password),
						this.MESG_ID_STTING_PIC_REVERSE,
						Constants.P2P_SETTING.SETTING_TYPE.SETTING_IMAGE_REVERSE,
						value);
		this.MESG_ID_STTING_PIC_REVERSE++;
	}

	public void setInfraredSwitch(String contactId, String password, int value) {
		if ((this.MESG_ID_STTING_IR_ALARM_EN) >= (Constants.MsgSection.MESG_ID_STTING_IR_ALARM_EN)) {
			this.MESG_ID_STTING_IR_ALARM_EN = Constants.MsgSection.MESG_ID_STTING_IR_ALARM_EN - 1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_ID_STTING_IR_ALARM_EN,
                Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_IR_ALARM_EN,
                value);
		this.MESG_ID_STTING_IR_ALARM_EN++;
	}

	public void setWiredAlarmInput(String contactId, String password, int value) {
		if ((this.MESG_STTING_ID_EXTLINE_ALARM_IN_EN) >= (Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN)) {
			this.MESG_STTING_ID_EXTLINE_ALARM_IN_EN = Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN - 1000;
		}
		MediaPlayer
				.iSetNPCSettings(
                        Integer.parseInt(contactId),
                        Integer.parseInt(password),
                        this.MESG_STTING_ID_EXTLINE_ALARM_IN_EN,
                        Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_IN_EN,
                        value);
		this.MESG_STTING_ID_EXTLINE_ALARM_IN_EN++;
	}

	public void setWiredAlarmOut(String contactId, String password, int value) {
		if ((this.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN) >= (Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN)) {
			this.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN = Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN - 1000;
		}
		MediaPlayer
				.iSetNPCSettings(
						Integer.parseInt(contactId),
						Integer.parseInt(password),
						this.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN,
						Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_OUT_EN,
						value);
		this.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN++;
	}

	public void setAutomaticUpgrade(String contactId, String password, int value) {
		if ((this.MESG_STTING_ID_SECUPGDEV) >= (Constants.MsgSection.MESG_STTING_ID_SECUPGDEV)) {
			this.MESG_STTING_ID_SECUPGDEV = Constants.MsgSection.MESG_STTING_ID_SECUPGDEV - 1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_STTING_ID_SECUPGDEV,
                Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_SECUPGDEV, value);
		this.MESG_STTING_ID_SECUPGDEV++;
	}

	/**
	 * 设置时区
	 * @param contactId
	 * @param password
	 * @param value 时区值  '-11'-'+12'
	 */
	public void setTimeZone(String contactId, String password, int value) {
		if ((this.MESG_STTING_ID_TIMEZONE) >= (Constants.MsgSection.MESG_STTING_ID_TIMEZONE)) {
			this.MESG_STTING_ID_TIMEZONE = Constants.MsgSection.MESG_STTING_ID_TIMEZONE - 1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_STTING_ID_TIMEZONE,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_TIMEZONE, value);
		this.MESG_STTING_ID_TIMEZONE++;
	}

	public void getSdCardCapacity(String contactId, String password, String data) {
		if ((this.MESG_GET_SD_CARD_CAPACITY) >= (Constants.MsgSection.MESG_GET_SD_CARD_CAPACITY)) {
			this.MESG_GET_SD_CARD_CAPACITY = Constants.MsgSection.MESG_GET_SD_CARD_CAPACITY - 1000;
		}
		byte[] datas = new byte[16];
		datas[0] = 80;
		datas[1] = 0;
		datas[2] = 0;
		datas[3] = 0;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_GET_SD_CARD_CAPACITY,
                datas, 4);
		this.MESG_GET_SD_CARD_CAPACITY++;
	}

	/**
	 * SD卡格式化
	 * @param contactId
	 * @param password
	 * @param SDcardID 没有双SD卡时  默认填16
	 */
	public void setSdFormat(String contactId, String password, int SDcardID) {
		if ((this.MESG_SD_CARD_FORMAT) >= (Constants.MsgSection.MESG_SD_CARD_FORMAT)) {
			this.MESG_SD_CARD_FORMAT = Constants.MsgSection.MESG_SD_CARD_FORMAT - 1000;
		}
		byte[] datas = new byte[16];
		datas[0] = 81;
		datas[1] = 0;
		datas[2] = 0;
		datas[3] = 0;
		datas[4] = (byte) SDcardID;
		Log.e("id", "id:" + datas[4]);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SD_CARD_FORMAT, datas, 5);
		this.MESG_SD_CARD_FORMAT++;
	}

	public void setGPIO(String contactId, String password, int group, int pin) {
		if ((this.MESG_SET_GPIO) >= (Constants.MsgSection.MESG_SET_GPIO)) {
			this.MESG_SET_GPIO = Constants.MsgSection.MESG_SET_GPIO - 1000;
		}
		byte[] datas = new byte[37];
		datas[0] = 95;
		datas[1] = 0;
		datas[2] = (byte) group;
		datas[3] = (byte) pin;
		datas[4] = 5;
		datas[5] = (byte) (-15 & 0xFF);
		datas[6] = (byte) (-15 >> 8 & 0xFF);
		datas[7] = (byte) (-15 >> 16 & 0xFF);
		datas[8] = (byte) (-15 >> 24 & 0xFF);
		datas[9] = (byte) (1000 & 0xFF);
		datas[10] = (byte) (1000 >> 8 & 0xFF);
		datas[11] = (byte) (1000 >> 16 & 0xFF);
		datas[12] = (byte) (1000 >> 24 & 0xFF);
		datas[13] = (byte) (-1000 & 0xFF);
		datas[14] = (byte) (-1000 >> 8 & 0xFF);
		datas[15] = (byte) (-1000 >> 16 & 0xFF);
		datas[16] = (byte) (-1000 >> 24 & 0xFF);
		datas[17] = (byte) (1000 & 0xFF);
		datas[18] = (byte) (1000 >> 8 & 0xFF);
		datas[19] = (byte) (1000 >> 16 & 0xFF);
		datas[20] = (byte) (1000 >> 24 & 0xFF);
		datas[21] = (byte) (-1000 & 0xFF);
		datas[22] = (byte) (-1000 >> 8 & 0xFF);
		datas[23] = (byte) (-1000 >> 16 & 0xFF);
		datas[24] = (byte) (-1000 >> 24 & 0xFF);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_GPIO, datas,
				datas.length);
		this.MESG_SET_GPIO++;
	}

	public void setGPIO1_0(String contactId, String password) {
		if ((this.MESG_SET_GPI1_0) >= (Constants.MsgSection.MESG_SET_GPI1_0)) {
			this.MESG_SET_GPI1_0 = Constants.MsgSection.MESG_SET_GPI1_0 - 1000;
		}
		byte[] datas = new byte[37];
		datas[0] = 95;
		datas[1] = 0;
		datas[2] = 1;
		datas[3] = 0;
		datas[4] = 3;
		datas[5] = (byte) (-15 & 0xFF);
		datas[6] = (byte) (-15 >> 8 & 0xFF);
		datas[7] = (byte) (-15 >> 16 & 0xFF);
		datas[8] = (byte) (-15 >> 24 & 0xFF);
		datas[9] = (byte) (6000 & 0xFF);
		datas[10] = (byte) (6000 >> 8 & 0xFF);
		datas[11] = (byte) (6000 >> 16 & 0xFF);
		datas[12] = (byte) (6000 >> 24 & 0xFF);
		datas[13] = (byte) (-6000 & 0xFF);
		datas[14] = (byte) (-6000 >> 8 & 0xFF);
		datas[15] = (byte) (-6000 >> 16 & 0xFF);
		datas[16] = (byte) (-6000 >> 24 & 0xFF);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_GPI1_0, datas,
				datas.length);
		this.MESG_SET_GPI1_0++;
	}

	/**
	 * 设置预录像
	 * @param contactId
	 * @param password
	 * @param value  0 OFF 1 ON
	 */
	public void setPreRecord(String contactId, String password, int value) {
		if ((this.MESG_SET_PRE_RECORD) >= (Constants.MsgSection.MESG_SET_PRE_RECORD)) {
			this.MESG_SET_PRE_RECORD = Constants.MsgSection.MESG_SET_PRE_RECORD - 1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_PRE_RECORD,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_PRERECORD, value);
		this.MESG_SET_PRE_RECORD++;
	}

	/**
	 * 获得防区报警开关状态，需要设备配置
	 * @param contactId
	 * @param password
	 */
	public void getDefenceAreaAlarmSwitch(String contactId, String password) {
		if ((this.MESG_GET_DEFENCE_AREA_SWITCH) >= (Constants.MsgSection.MESG_GET_DEFENCE_AREA_SWITCH)) {
			this.MESG_GET_DEFENCE_AREA_SWITCH = Constants.MsgSection.MESG_GET_DEFENCE_AREA_SWITCH - 1000;
		}
		byte[] datas = new byte[12];
		datas[0] = 82;
		datas[1] = 0;
		datas[2] = 8;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_GET_DEFENCE_AREA_SWITCH,
				datas, datas.length);
		this.MESG_GET_DEFENCE_AREA_SWITCH++;
	}

	/**
	 * 设置防区报警开关
	 * @param contactId
	 * @param password
	 * @param state 开关状态
	 * @param group 防区
	 * @param item 通道
	 */
	public void setDefenceAreaAlarmSwitch(String contactId, String password,
			int state, int group, int item) {
		if ((this.MESG_SET_DEFENCE_AREA_SWITCH) >= (Constants.MsgSection.MESG_SET_DEFENCE_AREA_SWITCH)) {
			this.MESG_SET_DEFENCE_AREA_SWITCH = Constants.MsgSection.MESG_SET_DEFENCE_AREA_SWITCH - 1000;
		}
		byte[] datas = new byte[12];
		datas[0] = 83;
		datas[1] = 0;
		datas[2] = (byte) state;
		datas[3] = 1;
		datas[4] = (byte) (group & 0xFF);
		datas[5] = (byte) (group >> 8 & 0xFF);
		datas[6] = (byte) (group >> 16 & 0xFF);
		datas[7] = (byte) (group >> 24 & 0xFF);
		datas[8] = (byte) (item & 0xFF);
		datas[9] = (byte) (item >> 8 & 0xFF);
		datas[10] = (byte) (item >> 16 & 0xFF);
		datas[11] = (byte) (item >> 24 & 0xFF);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_DEFENCE_AREA_SWITCH,
				datas, datas.length);
		this.MESG_SET_DEFENCE_AREA_SWITCH++;
	}

	/**
	 * 获得灯的状态
	 * @param contactId
	 * @param password
	 */
	public void vgetLampStatus(String contactId, String password) {
		if (this.MESG_GET_LAMP >= (Constants.MsgSection.MESG_GET_LAMP)) {
			this.MESG_GET_LAMP = Constants.MsgSection.MESG_GET_LAMP - 1000;
		}
		MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_GET_LAMP);
		this.MESG_GET_LAMP++;
	}

    /**
     * 设置灯的状态
     * @param contactId
     * @param password
     * @param LampStatus 0 OFF 1 ON
     */
	public void vsetLampStatus(String contactId, String password, int LampStatus) {
		if (this.MESG_SET_LAMP >= (Constants.MsgSection.MESG_SET_LAMP)) {
			this.MESG_SET_LAMP = Constants.MsgSection.MESG_SET_LAMP - 1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_SET_LAMP, 34, LampStatus);
		this.MESG_SET_LAMP++;
	}

	/**
	 * 自定义串口消息
	 * @param contactId
	 * @param password
	 * @param dataBuffer 用户输入的串口消息
	 * @param len 消息长度
	 * @param appID  消息带不带ID
	 */
	public void vSendDataToURAT(String contactId, String password,
			byte[] dataBuffer, int len, boolean appID) {
		if (this.SET_USER_DEFINE_MESG >= (Constants.MsgSection.SET_USER_DEFINE_MESG)) {
			this.SET_USER_DEFINE_MESG = Constants.MsgSection.SET_USER_DEFINE_MESG - 1000;
		}
		int iId = Integer.parseInt(contactId);
		String fgMesg = "";
		if (appID) {
			fgMesg = "IPC1";
		} else {
			fgMesg = "IPC2";
		}
		byte[] fgme = null;
		fgme = fgMesg.getBytes();
		int lens = fgme.length;
		byte[] datas = new byte[fgme.length + dataBuffer.length];
		System.arraycopy(fgme, 0, datas, 0, fgme.length);
		System.arraycopy(dataBuffer, 0, datas, fgme.length, dataBuffer.length);
		MediaPlayer.iSendCmdToFriend(iId, Integer.parseInt(password),
                this.SET_USER_DEFINE_MESG, datas, datas.length);
		this.SET_USER_DEFINE_MESG++;
	}

	/**
	 * 获取报警类型或防区通道对应的摄像头预置位置
	 * @param contactId
	 * @param password
	 * @param data
	 */
	public void sMesgGetAlarmPresetMotorPos(String contactId, String password,
			byte[] data) {
		if ((this.MESG_GET_PRESET_MOTOR_POS) >= (Constants.MsgSection.MESG_GET_PRESET_MOTOR_POS)) {
			this.MESG_GET_PRESET_MOTOR_POS = Constants.MsgSection.MESG_GET_PRESET_MOTOR_POS - 1000;
		}

		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_GET_PRESET_MOTOR_POS,
                data, 7);

		this.MESG_GET_PRESET_MOTOR_POS++;
	}

	/**
	 * 设置报警类型或防区通道对应的摄像头预置位置
	 * @param contactId
	 * @param password
	 * @param data  命令值
	 */
	public void sMesgSetAlarmPresetMotorPos(String contactId, String password,
			byte[] data) {
		if ((this.MESG_SET_PRESET_MOTOR_POS) >= (Constants.MsgSection.MESG_SET_PRESET_MOTOR_POS)) {
			this.MESG_SET_PRESET_MOTOR_POS = Constants.MsgSection.MESG_SET_PRESET_MOTOR_POS - 1000;
		}

		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_PRESET_MOTOR_POS,
				data, 7);

		this.MESG_SET_PRESET_MOTOR_POS++;
	}

	/**
	 * 设置/查看摄像头预置位置
	 * @param contactId
	 * @param password
	 * @param data  命令值
	 */
	public void sMesgPresetMotorPos(String contactId, String password,
			byte[] data) {
		if ((this.MESG_PRESET_MOTOR_POS) >= (Constants.MsgSection.MESG_PRESET_MOTOR_POS)) {
			this.MESG_PRESET_MOTOR_POS = Constants.MsgSection.MESG_PRESET_MOTOR_POS - 1000;
		}
		MediaPlayer
				.iExtendedCmd(Integer.parseInt(contactId),
                        Integer.parseInt(password), this.MESG_PRESET_MOTOR_POS,
                        data, 7);
		this.MESG_PRESET_MOTOR_POS++;
	}

	/**
	 * 获取设备IP相关信息
	 * @param contactId
	 * @param password
	 * @param data
	 */
	public void sIpConfig(String contactId, String password, byte[] data) {
		if ((this.IP_CONFIG) >= (Constants.MsgSection.IP_CONFIG)) {
			this.IP_CONFIG = Constants.MsgSection.IP_CONFIG - 1000;
		}
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
                Integer.parseInt(password), this.IP_CONFIG, data, 8);
		this.IP_CONFIG++;
	}

	public void getAlarmCenterParameters(String contactId, String password) {
		if ((this.MESG_GET_ALARM_CENTER_PARAMETER) >= (Constants.MsgSection.MESG_GET_ALARM_CENTER_PARAMETER)) {
			this.MESG_GET_ALARM_CENTER_PARAMETER = Constants.MsgSection.MESG_GET_ALARM_CENTER_PARAMETER - 1000;
		}
		byte[] datas = new byte[20];
		datas[0] = 100;
		datas[1] = 0;
		datas[2] = 0;
		datas[3] = 20;
		Log.e("alarmcenter", "MESG_GET_ALARM_CENTER_PARAMETER="
				+ this.MESG_GET_ALARM_CENTER_PARAMETER + "  " + "length="
				+ datas.length);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MESG_GET_ALARM_CENTER_PARAMETER, datas, datas.length);
		this.MESG_GET_ALARM_CENTER_PARAMETER++;
	}

	/**
	 * 设置报警中心参数
	 * @param contactId
	 * @param password
	 * @param port  端口
	 * @param ipdress ip地址
	 * @param userId 用户ID
	 * @param state  状态
	 */
	public void setAlarmCenterParameters(String contactId, String password,
			int port, String ipdress, String userId, int state) {
		if ((this.MESG_SET_ALARM_CENTER_PARAMETER) >= (Constants.MsgSection.MESG_SET_ALARM_CENTER_PARAMETER)) {
			this.MESG_SET_ALARM_CENTER_PARAMETER = Constants.MsgSection.MESG_SET_ALARM_CENTER_PARAMETER - 1000;
		}
		byte[] datas = new byte[20];
		datas[0] = 101;
		datas[1] = 0;
		datas[2] = 0;
		datas[3] = 20;
		datas[4] = (byte) state;
		datas[5] = 0;
		datas[6] = 0;
		datas[7] = 0;
		Log.e("ipdesss_length", ipdress);
		String[] ipdesss = ipdress.split("\\.");
		Log.e("ipdesss_length", "lenght=" + ipdesss.length);
		int[] ips = { Integer.parseInt(ipdesss[0]),
				Integer.parseInt(ipdesss[1]), Integer.parseInt(ipdesss[2]),
				Integer.parseInt(ipdesss[3]) };
		int user_id2 = Integer.parseInt(userId, 16);
		datas[8] = (byte) (ips[3]);
		datas[9] = (byte) (ips[2]);
		datas[10] = (byte) (ips[1]);
		datas[11] = (byte) ips[0];
		datas[12] = (byte) (port & 0xFF);
		datas[13] = (byte) (port >> 8 & 0xFF);
		datas[14] = (byte) (port >> 16 & 0xFF);
		datas[15] = (byte) (port >> 24 & 0xFF);
		datas[16] = (byte) (user_id2 & 0xFF);
		datas[17] = (byte) (user_id2 >> 8 & 0xFF);
		datas[18] = (byte) (user_id2 >> 16 & 0xFF);
		datas[19] = (byte) (user_id2 >> 24 & 0xFF);
		// datas[20]=(byte)(user_id2>>32&0xFF);
		// datas[21]=(byte)(user_id2>>40&0xFF);
		// datas[22]=(byte)(user_id2>>48&0xFF);
		// datas[23]=(byte)(user_id2>>56&0xFF);

		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MESG_SET_ALARM_CENTER_PARAMETER, datas, datas.length);
		this.MESG_SET_ALARM_CENTER_PARAMETER++;
	}

	public void DeleteDeviceAlarmId(String contactId) {
		if ((this.MESG_DELETE_ALARMID) >= (Constants.MsgSection.MESG_DELETE_DEVICEALARMID)) {
			this.MESG_DELETE_ALARMID = Constants.MsgSection.MESG_DELETE_DEVICEALARMID - 1000;
		}
		byte[] datas = new byte[16];
		datas[0] = 127;
		datas[3] = 16;
		datas[8] = 1;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId), 0,
				this.MESG_DELETE_ALARMID, datas, datas.length);
		this.MESG_DELETE_ALARMID++;
	}

	public void setSystemMessageIndex(int iSystemMessageType,
			int iSystemMessageIndex) {
		if ((this.MESG_SET_SYSTEM_MESSAGE_INDEX) >= (Constants.MsgSection.MESG_SET_SYSTEM_MESSAGE_INDEX)) {
			this.MESG_SET_SYSTEM_MESSAGE_INDEX = Constants.MsgSection.MESG_SET_SYSTEM_MESSAGE_INDEX - 1000;
		}

		MediaPlayer.SetSystemMessageIndex(iSystemMessageType,
                iSystemMessageIndex);
		this.MESG_SET_SYSTEM_MESSAGE_INDEX++;
	}

	/**
	 * 自定义截图路径
	 * @param path 路径（必须省略sdcard路径）~/11/22/33/10234.jpg时 path=/11/22/33
	 * @param name name=1023.jpg
	 * @return 创建文件夹是否成功 0成功 !0失败
	 */
	public int setScreenShotpath(String path, String name) {
		return MediaPlayer.getInstance().SetScreenShotPath(path, name);
	}

	public void controlCamera(String contactId, String password, byte[] data) {
		if (this.CONTROL_CAMERA >= (Constants.MsgSection.CONTROL_CAMERA)) {
			this.CONTROL_CAMERA = Constants.MsgSection.CONTROL_CAMERA - 1000;
		}
		byte[] sendData = new byte[68];
		for (int i = 0; i < data.length; i++) {
			sendData[i] = data[i];
		}
		if (data.length < 68) {
			for (int j = data.length; j < sendData.length; j++) {
				sendData[j] = 0;
			}
		}
		Log.e("control_camera", "contactId=" + contactId + "--" + "password="
				+ password + "--" + Arrays.toString(sendData));
		int iId = Integer.parseInt(contactId);
		MediaPlayer.iExtendedCmd(iId, Integer.parseInt(password),
				this.CONTROL_CAMERA, sendData, sendData.length);
		this.CONTROL_CAMERA++;

	}

	/**
	 * 收到门铃
	 */
	public void setReceiveDoorBell(String contactId) {
		Log.e(TAG, "P2PHANDLER:setRemoteDefence");
		if (this.MESG_SET_RECEIVE_DOOBELL >= (Constants.MsgSection.MESG_SET_RECEIVE_DOOBELL)) {
			this.MESG_SET_RECEIVE_DOOBELL = Constants.MsgSection.MESG_SET_RECEIVE_DOOBELL - 1000;
		}
		byte[] datas = new byte[16];
		datas[0] = 127;
		datas[3] = 16;
		datas[8] = 3;
		datas[12] = 1;

		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId), 0,
				this.MESG_SET_RECEIVE_DOOBELL, datas, datas.length);
		Log.e("vRetExtenedCmd", "datas-->" + Arrays.toString(datas));
		this.MESG_SET_RECEIVE_DOOBELL++;
	}

	public void getDeviceLanguage(String contactId, String password) {
		if ((this.MESG_GET_LANGUEGE) >= (Constants.MsgSection.MESG_GET_LANGUEGE)) {
			this.MESG_GET_LANGUEGE = Constants.MsgSection.MESG_GET_LANGUEGE - 1000;
		}
		byte[] datas = new byte[7];
		datas[0] = (byte) 211;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_GET_LANGUEGE, datas,
                datas.length);
		this.MESG_GET_LANGUEGE++;
	}

	public void setDevieceLanguege(String contactId, String password,
			int curLanguege) {
		if ((this.MESG_SET_LANGUEGE) >= (Constants.MsgSection.MESG_SET_LANGUEGE)) {
			this.MESG_SET_LANGUEGE = Constants.MsgSection.MESG_SET_LANGUEGE - 1000;
		}
		byte[] datas = new byte[7];
		datas[0] = (byte) 212;
		datas[5] = (byte) curLanguege;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_LANGUEGE, datas,
				datas.length);
		this.MESG_SET_LANGUEGE++;
	}

	public void setFocus(byte state) {
		byte[] data = new byte[5];
		data[0] = (byte) state;
		MediaPlayer.SendUserData(9, 1, data, data.length);
		Log.e("ptz", "focus--" + Arrays.toString(data));
	}

	public void setZoom(byte state) {
		byte[] data = new byte[5];
		data[0] = (byte) state;
		MediaPlayer.SendUserData(9, 2, data, data.length);
		Log.e("ptz", "zoom--" + Arrays.toString(data));
	}

    /**
     * 字符串密码转数字密码
     * @param password
     * @return 纯数字不做处理原样返回
     */
	public String EntryPassword(String password) {
		if (password.length() == 0
				|| (MyUtils.isNumeric(password) && password.length() < 10 && (password
						.charAt(0) != '0'))) {
			return password;
		} else {
			return String.valueOf(MediaPlayer.EntryPwd(password));
		}
	}

    /**
     * 获取报警图像
     * @param id
     * @param password
     * @param filename  图片文件名完整路径
     * @param localName 存放文件名完整路径
     * @return
     */
	public int GetAllarmImage(String id, String password, String filename,
			String localName) {
		return MediaPlayer.GetAllarmImage(Integer.parseInt(id),
				Integer.parseInt(password), filename, localName);
	}

	/**
	 * 获取图片的进度
	 * @return
	 */
	public int GetAllarmImageProgress() {
		return MediaPlayer.GetFileProgress();
	}

	/**
	 * 取消获取图片
	 */
	public void CancelGetAllarmImage() {
		MediaPlayer.CancelGetRemoteFile();
	}

	public static byte[] intToByte2(int i) {
		byte[] targets = new byte[2];
		targets[0] = (byte) (i & 0xFF);
		targets[1] = (byte) (i >> 8 & 0xFF);
		return targets;
	}

	public static byte[] intToByte4(int i) {
		byte[] targets = new byte[4];
		targets[0] = (byte) (i & 0xFF);
		targets[1] = (byte) (i >> 8 & 0xFF);
		targets[2] = (byte) (i >> 16 & 0xFF);
		targets[3] = (byte) (i >> 24 & 0xFF);
		return targets;
	}

	public void setFishEye(int contactId, int password, int msgId, byte[] datas) {
		MediaPlayer.iExtendedCmd(contactId, password, msgId, datas,
				datas.length);
	}

	public void getNvrIpcList(String nvrId, String password) {
		if (this.MESG_TYPE_GET_LAN_IPC_LIST >= (Constants.MsgSection.MESG_TYPE_GET_LAN_IPC_LIST)) {
			this.MESG_TYPE_GET_LAN_IPC_LIST = Constants.MsgSection.MESG_TYPE_GET_LAN_IPC_LIST - 1000;
		}
		byte[] data = new byte[20];
		data[0] = (byte) 129;
		MediaPlayer.iExtendedCmd(Integer.parseInt(nvrId),
				Integer.parseInt(password), this.MESG_TYPE_GET_LAN_IPC_LIST,
				data, data.length);
		Log.e("nvr_list", "nvrId=" + nvrId + "--" + "password=" + password
				+ "data=" + Arrays.toString(data));
		this.MESG_TYPE_GET_LAN_IPC_LIST++;

	}

	/**
	 * 兼容RTSP密码加密
	 * @param password
	 * @return
	 */
	public String RTSPPwd(String password) {
		if (password != null && password.length() > 0) {
			return MediaPlayer.RTSPEntry(password);
		} else {
			return "error";
		}
	}

    /**
     *
     * @param userID 用户ID
     * @param password 密文
     * @param backLen 返回的字符串缓冲区长度，建议比预估的值稍大
     * @return 返回的error可能是错误 也可能是缓冲区长度不足
     */
	public String HTTPDecrypt(String userID, String password, int backLen) {
		if (password != null && password.length() > 0) {
			return MediaPlayer.HTTPDecrypt(userID, password, backLen);
		} else {
			return "error";
		}
	}

    /**
     *
     * @param userID 登陆账号关联的ID
     * @param password 密文
     * @param backLen 返回的字符串缓冲区长度，建议比预估的值稍大
     * @return 返回的error可能是错误 也可能是缓冲区长度不足
     */
	public String HTTPEncrypt(String userID, String password, int backLen) {
		if (password != null && password.length() > 0) {
			return MediaPlayer.HTTPEncrypt(userID, password, backLen);
		} else {
			return "error";
		}
	}

	/**
	 * 密码加密，用于P2P
	 * @param pwd
	 * @return
	 */
	public byte[] P2PEntryPassword(byte[] pwd) {
		return MediaPlayer.P2PEntryPassword(pwd);
	}

    /**
     * AP模式切换
     * @param contactId
     * @param password
     * @param value
     */
	public void setAPModeChange(String contactId, String password, int value) {
		if (this.MESG_TYPE_SET_AP_MODE_CHANGE >= (Constants.MsgSection.MESG_TYPE_SET_AP_MODE_CHANGE)) {
			this.MESG_TYPE_SET_AP_MODE_CHANGE = Constants.MsgSection.MESG_TYPE_SET_AP_MODE_CHANGE - 1000;
		}
		MediaPlayer
				.iSetNPCSettings(
						Integer.parseInt(contactId),
						Integer.parseInt(password),
						this.MESG_TYPE_SET_AP_MODE_CHANGE,
						Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_SET_WIFI_WORK_MODE,
						value);
		this.MESG_TYPE_SET_AP_MODE_CHANGE++;
	}
	
	/**
	 * 获取NVR设备信息
	 */
	public void getNVRInfo(String contactId, String password) {
		if (this.MESG_TYPE_GET_NVRINFO >= (Constants.MsgSection.MESG_TYPE_GET_NVRINFO)) {
			this.MESG_TYPE_GET_NVRINFO = Constants.MsgSection.MESG_TYPE_GET_NVRINFO - 1000;
		}
		byte[] data = new byte[94];
		data[0] = (byte) 131;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_TYPE_GET_NVRINFO, data,
                data.length);
		this.MESG_TYPE_GET_NVRINFO++;
	}

	public void getFocusZoom(String contactId, String password) {
		if (this.MESG_TYPE_GET_FOCUS_ZOOM >= (Constants.MsgSection.MESG_TYPE_GET_FOCUS_ZOOM)) {
			this.MESG_TYPE_GET_FOCUS_ZOOM = Constants.MsgSection.MESG_TYPE_GET_FOCUS_ZOOM - 1000;
		}
		byte[] data = new byte[4];
		data[0] = (byte) 224;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_TYPE_GET_FOCUS_ZOOM,
				data, data.length);
		this.MESG_TYPE_GET_FOCUS_ZOOM++;
	}

	public void setFocusZoom(String contactId, String password, int value) {
		Log.e("setFocusZoom", "contactId=" + contactId + "--" + "value="
				+ value);
		if (this.MESG_TYPE_SET_FOCUS_ZOOM >= (Constants.MsgSection.MESG_TYPE_SET_FOCUS_ZOOM)) {
			this.MESG_TYPE_SET_FOCUS_ZOOM = Constants.MsgSection.MESG_TYPE_SET_FOCUS_ZOOM - 1000;
		}
		byte[] data = new byte[4];
		data[0] = (byte) 224;
		data[1] = (byte) 1;
		data[3] = (byte) value;
		Log.e("focus_zoom", "focus_zoom=" + value);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_TYPE_SET_FOCUS_ZOOM,
				data, data.length);
		this.MESG_TYPE_SET_FOCUS_ZOOM++;
	}

	public byte[] getPwdBytes(String pwd) {
		byte[] ppp = null;
		try {
			int m=pwd.getBytes().length;
			int byteLen=(m/8+1)*8;
			byte[] pwds=new byte[Math.min(byteLen, 32)];
			System.arraycopy(pwd.getBytes(), 0, pwds, 0, Math.min(m, 32));
			ppp = pwds;
		} catch (Exception e) {
			ppp = new byte[] { 0 };
			e.printStackTrace();
		}
		return ppp;
	}

	public void setGPIO(String contactId, String password, int group, int pin,
			int[] data) {
		if ((this.MESG_SET_GPIO) >= (Constants.MsgSection.MESG_SET_GPIO)) {
			this.MESG_SET_GPIO = Constants.MsgSection.MESG_SET_GPIO - 1000;
		}
		byte[] datas = new byte[37];
		datas[0] = 95;
		datas[1] = 0;
		datas[2] = (byte) group;
		datas[3] = (byte) pin;
		datas[4] = (byte) Math.min(8, data.length);
		for (int i = 0; i < datas[4]; i++) {
			byte[] io = intToByte4(data[i]);
			System.arraycopy(io, 0, datas, 5 + i * io.length, io.length);
		}
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_GPIO, datas,
				datas.length);
		this.MESG_SET_GPIO++;
	}

	public void getGPIO(String contactId, String password, int group, int pin,
			int[] data) {
		if ((this.MESG_GET_GPIO) >= (Constants.MsgSection.MESG_GET_GPIO)) {
			this.MESG_GET_GPIO = Constants.MsgSection.MESG_GET_GPIO - 1000;
		}
		byte[] datas = new byte[37];
		datas[0] = 95;
		datas[1] = 0;
		datas[2] = (byte) group;
		datas[3] = (byte) pin;
		datas[4] = (byte) 0xFF;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_GET_GPIO, datas,
				datas.length);
		this.MESG_GET_GPIO++;
	}

	/**
	 * 获取设备时间组
	 * @param contactId
	 * @param password
	 */
	public void getDefenceWorkGroup(String contactId, String password) {
		if ((this.MESG_GET_DEFENCE_WORK_GROUP) >= (Constants.MsgSection.MESG_GET_DEFENCE_WORK_GROUP)) {
			this.MESG_GET_DEFENCE_WORK_GROUP = Constants.MsgSection.MESG_GET_DEFENCE_WORK_GROUP - 1000;
		}
		byte[] datas = new byte[64];
		datas[0] = (byte) 214;
		datas[1] = 0;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_GET_DEFENCE_WORK_GROUP,
				datas, datas.length);
		this.MESG_GET_DEFENCE_WORK_GROUP++;
	}

	/**
	 * 设置设备时间组
	 * @param contactId
	 * @param password
	 * @param groupCount 时间组数量 最大5
	 * @param data 时间组byte值
	 */
	public void setDefenceWorkGroup(String contactId, String password,
			short groupCount, byte[] data) {
		if ((this.MESG_SET_DEFENCE_WORK_GROUP) >= (Constants.MsgSection.MESG_SET_DEFENCE_WORK_GROUP)) {
			this.MESG_SET_DEFENCE_WORK_GROUP = Constants.MsgSection.MESG_SET_DEFENCE_WORK_GROUP - 1000;
		}
		if (groupCount > 10 || data.length > 60)
			return;
		byte[] datas = new byte[64];
		datas[0] = (byte) 215;
		datas[1] = 0;
		byte[] count = MyUtils.shortToByte2(groupCount);
		System.arraycopy(count, 0, datas, 2, count.length);
		System.arraycopy(data, 0, datas, 4, data.length);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_DEFENCE_WORK_GROUP,
				datas, datas.length);
		this.MESG_SET_DEFENCE_WORK_GROUP++;
	}


    /**
     * 获取FTP信息
     * @param contactId
     * @param password
     */
	public void getFTPConfigInfo(String contactId, String password) {

		if ((this.MESG_GET_FTP_CONFIG_INFO) >= (Constants.MsgSection.MESG_GET_FTP_CONFIG_INFO)) {
			this.MESG_GET_FTP_CONFIG_INFO = Constants.MsgSection.MESG_GET_FTP_CONFIG_INFO - 1000;
		}
		byte[] data = new byte[102];
		data[0] = (byte) 225;
		data[1] = 0;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_GET_FTP_CONFIG_INFO,
				data, data.length);
		this.MESG_GET_FTP_CONFIG_INFO++;

	}

    /**
     * 设置FTP信息
     * @param contactId
     * @param password
     * @param hostname IP地址或域名
     * @param username 用户名
     * @param pass  密码
     * @param svrport  端口号
     * @param usrflag  开关 0 OFF 1 ON
     * 当发送的数据到设备  设备验证不通过时就会回复原命令option为106
     */
	public void setFTPConfigInfo(String contactId, String password,
			String hostname, String username, String pass, short svrport,
			short usrflag) {

		if ((this.MESG_SET_FTP_CONFIG_INFO) >= (Constants.MsgSection.MESG_SET_FTP_CONFIG_INFO)) {
			this.MESG_SET_FTP_CONFIG_INFO = Constants.MsgSection.MESG_SET_FTP_CONFIG_INFO - 1000;
		}
		byte[] data = new byte[102];
		data[0] = (byte) 226;
		data[1] = 0;
		byte[] hostnames = hostname.getBytes();
		byte[] usernames = username.getBytes();
		byte[] passwords = pass.getBytes();
		byte[] svrports = MyUtils.shortToByte2(svrport);
		byte[] usrflags = MyUtils.shortToByte2(usrflag);
		System.arraycopy(hostnames, 0, data, 2, hostnames.length);
		System.arraycopy(usernames, 0, data, 34, usernames.length);
		System.arraycopy(passwords, 0, data, 66, passwords.length);
		System.arraycopy(svrports, 0, data, 98, svrports.length);
		System.arraycopy(usrflags, 0, data, 100, usrflags.length);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_GET_FTP_CONFIG_INFO,
				data, data.length);
		this.MESG_GET_FTP_CONFIG_INFO++;
	}

	/********************* Ehome专用 ********************************/
	public void vSetGPIOStatus(String contactId, String password, int ID,
			boolean OutputLevel, int KeepTime) {
		if ((this.GPIO_STATUS) >= (Constants.MsgSection.GPIO_STATUS)) {
			this.GPIO_STATUS = Constants.MsgSection.GPIO_STATUS - 1000;
		}
		byte[] datas = new byte[10];
		datas[0] = (byte) 180;
		datas[1] = 0;
		byte[] ss = MyUtils.shortToByte2((short) datas.length);
		datas[2] = ss[0];
		datas[3] = ss[1];
		byte[] time = intToByte4(KeepTime);
		System.arraycopy(time, 0, datas, 4, time.length);
		datas[8] = (byte) (OutputLevel ? 2 : 1);
		datas[9] = (byte) ID;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.GPIO_STATUS, datas,
				datas.length);
		this.GPIO_STATUS++;
	}

    /**
     * 获取GPIO状态
     * @param contactId
     * @param password
     */
	public void vGetGPIOStatus(String contactId, String password) {
		if ((this.GET_GPIO_STATUS) >= (Constants.MsgSection.GET_GPIO_STATUS)) {
			this.GET_GPIO_STATUS = Constants.MsgSection.GET_GPIO_STATUS - 1000;
		}
		byte[] datas = new byte[10];
		datas[0] = (byte) 185;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.GET_GPIO_STATUS, datas,
				datas.length);
		this.GET_GPIO_STATUS++;
	}

	/********************* Ehome专用 ********************************/

    /**
     *  设置带名防区开关
     * @param contactId
     * @param password
     * @param value  0 OFF 1 ON
     */
	public void setDefenceSwitch(String contactId, String password, int value) {
		if (this.MESG_SET_DEFENCE_SWITCH >= (Constants.MsgSection.MESG_SET_DEFENCE_SWITCH)) {
			this.MESG_SET_DEFENCE_SWITCH = Constants.MsgSection.MESG_SET_DEFENCE_SWITCH - 1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_SET_DEFENCE_SWITCH,
                Constants.P2P_SETTING.SETTING_TYPE.SETTING_DEFENCE_SWITCH,
                value);
		this.MESG_SET_DEFENCE_SWITCH++;
	}

    /**
     * 获取防区通道名字
     * @param contactId
     * @param password
     * @param option 默认为0
     */
	public void getDefenceAreaName(String contactId, String password, int option) {
		if (this.MESG_GET_DEFENCE_AREA_NAME >= (Constants.MsgSection.MESG_GET_DEFENCE_AREA_NAME)) {
			this.MESG_GET_DEFENCE_AREA_NAME = Constants.MsgSection.MESG_GET_DEFENCE_AREA_NAME - 1000;
		}
		byte[] data = new byte[20];
		data[0] = (byte) 223;
		data[1] = (byte) 2;
		data[2] = (byte) option;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_GET_DEFENCE_AREA_NAME,
				data, data.length);
		Log.e("wxy", "mediaplayer  run:" + Arrays.toString(data));
		this.MESG_GET_DEFENCE_AREA_NAME++;
	}

    /**
     * 设置防区通道名字
     * @param contactId
     * @param password
     * @param bStructVersion  版本号
     * @param group  防区通道
     * @param item   防区
     * @param bSensorName 要改为的名字
     */
	public void setDefenceAreaName(String contactId, String password,
			int bStructVersion, int group, int item, String bSensorName) {
		if (this.MESG_SET_DEFENCE_AREA_NAME >= (Constants.MsgSection.MESG_SET_DEFENCE_AREA_NAME)) {
			this.MESG_SET_DEFENCE_AREA_NAME = Constants.MsgSection.MESG_SET_DEFENCE_AREA_NAME - 1000;
		}
		if (bSensorName.equals("")) {
			return;
		}
		byte[] name = bSensorName.getBytes();
		if (name.length > 16) {
			return;
		}
		byte[] data = new byte[23];
		data[0] = (byte) 223;
		data[1] = (byte) 4;
		data[2] = 0;
		data[3] = (byte) bStructVersion;
		data[4] = (byte) group;
		data[5] = (byte) item;
		data[6] = (byte) 1;
		System.arraycopy(name, 0, data, 7, name.length);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.MESG_SET_DEFENCE_AREA_NAME,
				data, data.length);
		this.MESG_SET_DEFENCE_AREA_NAME++;
	}

	/**
	 * 获取PIR灯光控制参数
	 * @param contactId
	 * @param password
	 * @param option 暂时无需设置
	 */
	public void getPIRLight(String contactId, String password, int option) {
		if (this.GET_PIRLIGHT >= (Constants.MsgSection.GET_PIRLIGHT)) {
			this.GET_PIRLIGHT = Constants.MsgSection.GET_PIRLIGHT - 1000;
		}
		byte[] data = new byte[13];
		data[0] = (byte) 223;
		data[1] = (byte) 8;
		data[2] = (byte) option;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.GET_PIRLIGHT, data,
				data.length);
		this.GET_PIRLIGHT++;
	}

	/**
	 * 设置PIR灯光控制参数
	 * @param contactId
	 * @param password
	 */
	public void setPIRLight(String contactId, String password,byte[] info) {
		if (this.SET_PIRLIGHT >= (Constants.MsgSection.SET_PIRLIGHT)) {
			this.SET_PIRLIGHT = Constants.MsgSection.SET_PIRLIGHT - 1000;
		}
		byte[] data = new byte[13];
		data[0] = (byte) 223;
		data[1] = (byte) 6;
		System.arraycopy(info, 0, data, 4, 9);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.SET_PIRLIGHT, data,
				data.length);
		this.SET_PIRLIGHT++;
	}

    /**
     * 设置间隔一定时间拍照
     * @param contactId
     * @param password
     * @param value 间隔时间
     */
    public void setAutoSnapshotSwitch(String contactId, String password, int value) {
        if ((this.MESG_ID_AUTO_SNAPSHOT_SWITCH) >= (Constants.MsgSection.MESG_ID_AUTO_SNAPSHOT_SWITCH)) {
            this.MESG_ID_AUTO_SNAPSHOT_SWITCH = Constants.MsgSection.MESG_ID_AUTO_SNAPSHOT_SWITCH - 1000;
        }
        MediaPlayer
                .iSetNPCSettings(
                        Integer.parseInt(contactId),
                        Integer.parseInt(password),
                        this.MESG_ID_AUTO_SNAPSHOT_SWITCH,
                        Constants.P2P_SETTING.SETTING_TYPE.SETTING_AUTO_SNAPSHOT,
                        value);
        this.MESG_ID_AUTO_SNAPSHOT_SWITCH++;
    }
	
	/**
	 * 获取鱼眼全景信息
	 * @param contactId
	 * @param password
	 */
	public void getFishEyeInfo(String contactId, String password) {
		if (this.GET_FISHEYE_INFO >= (Constants.MsgSection.GET_FISHEYE_INFO)) {
			this.GET_FISHEYE_INFO = Constants.MsgSection.GET_FISHEYE_INFO - 1000;
		}
		byte[] data = new byte[68];
		data[0] = (byte) 228;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.GET_FISHEYE_INFO, data,
				data.length);
		this.GET_FISHEYE_INFO++;
	}

    /**
     * 设置鱼眼信息
     * @param contactId
     * @param password
     * @param previewPixels 预览分辨率
     * @param recodePixels 录像分辨率
     * @param fishPos 安装方式
     */
	public void setFishEyeInfo(String contactId, String password,String previewPixels,String recodePixels,short fishPos) {
		if (this.SET_FISHEYE_INFO >= (Constants.MsgSection.SET_FISHEYE_INFO)) {
			this.SET_FISHEYE_INFO = Constants.MsgSection.SET_FISHEYE_INFO - 1000;
		}
		byte[] data = new byte[68];
		data[0] = (byte) 229;
		byte[] pre=previewPixels.getBytes();
		byte[] recode=recodePixels.getBytes();
		System.arraycopy(pre, 0, data, 2, pre.length);
		System.arraycopy(recode, 0, data, 34, Math.min(32, recode.length));
		byte[] pos=MyUtils.shortToByte2(fishPos);
		System.arraycopy(pos, 0, data, 66, 2);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), this.SET_FISHEYE_INFO, data,
				data.length);
		this.SET_FISHEYE_INFO++;
	}

	/***
	 * 获取黑白视频状态
	 * @param contactId
	 * @param password
	 */
    public void getVideoColorSwitch(String contactId, String password){
        if ((this.MESG_GET_VIDEO_COLOR) >= (Constants.MsgSection.MESG_GET_VIDEO_COLOR)) {
            this.MESG_GET_VIDEO_COLOR = Constants.MsgSection.MESG_GET_VIDEO_COLOR - 1000;
        }
        byte[] data=new byte[5];
        data[0]=(byte)223;
        data[1]=(byte)15;
        MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_GET_VIDEO_COLOR, data, data.length);
        this.MESG_GET_VIDEO_COLOR++;
    }
    
    /***
     * 设置黑白视频状态
     * @param contactId
     * @param password
     * @param switchValue
     */
    public void setVideoColorSwitch(String contactId, String password,int switchValue){
        if ((this.MESG_SET_VIDEO_COLOR) >= (Constants.MsgSection.MESG_SET_VIDEO_COLOR)) {
            this.MESG_SET_VIDEO_COLOR = Constants.MsgSection.MESG_SET_VIDEO_COLOR - 1000;
        }
        byte[] data=new byte[5];
        data[0]=(byte)223;
        data[1]=(byte)14;
        data[4]=(byte)switchValue;
        MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
                Integer.parseInt(password), this.MESG_SET_VIDEO_COLOR, data, data.length);
        this.MESG_SET_VIDEO_COLOR++;
    }

    /**
     * 设置移动侦测灵敏度
     * @param contactId
     * @param password
     * @param value  值的范围根据设备不一样请参照设备网络消息接口文档
     */
	public void setMotionSens(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setMotionSens");
		if (this.MSG_ID_SETTING_NPC_SETTINGS_MOTION_SENS >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION_SENS)) {
			this.MSG_ID_SETTING_NPC_SETTINGS_MOTION_SENS = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION_SENS - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				this.MSG_ID_SETTING_NPC_SETTINGS_MOTION_SENS,
				Constants.P2P_SETTING.SETTING_TYPE.STTING_ID_MOTION_SENS, value);
		this.MSG_ID_SETTING_NPC_SETTINGS_MOTION_SENS++;
	}

}
