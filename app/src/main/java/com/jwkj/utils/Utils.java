package com.jwkj.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.DefenceArea;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SysMessage;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.NpcCommon;
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.network.NetManager;
import com.p2p.core.update.UpdateManager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Utils {
	public static long lastClickTime = 0;

	public static String[] getMsgInfo(SysMessage msg, Context context) {

		if (msg.msgType == SysMessage.MESSAGE_TYPE_ADMIN) {

			String title = context.getResources().getString(
					R.string.system_administrator);
			String content = msg.msg;
			return new String[] { title, content };
		} else {
			return null;
		}

	}

	public static boolean hasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())
			flag = true;
		return flag;
	}

    // 自定义类型long转换为时间 注意此方法内会清空时区
    public static String ConvertTimeByString(long time,String format) {
        if (format==null||format.equals("")){
            return "";
        }
        SimpleDateFormat sdf;
        Date date;
        TimeZone timeZone;
        try {
            sdf = new SimpleDateFormat(format);
            timeZone = new SimpleTimeZone(0, "UTC");
            date = new Date(0);
            sdf.setTimeZone(timeZone);
            date.setTime(time);
            return sdf.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

	public static String ConvertTimeByLong(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return sdf.format(date);
	}

	public static String getDefenceAreaByGroup(Context context, int group) {
		switch (group) {
		case 0:
			return context.getResources().getString(R.string.remote);
		case 1:
			return context.getResources().getString(R.string.hall);
		case 2:
			return context.getResources().getString(R.string.window);
		case 3:
			return context.getResources().getString(R.string.balcony);
		case 4:
			return context.getResources().getString(R.string.bedroom);
		case 5:
			return context.getResources().getString(R.string.kitchen);
		case 6:
			return context.getResources().getString(R.string.courtyard);
		case 7:
			return context.getResources().getString(R.string.door_lock);
		case 8:
			return context.getResources().getString(R.string.other);
		default:
			return "";
		}
	}

	public static Bitmap montageBitmap(Bitmap frame, Bitmap src, int x, int y) {
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap sizeFrame = Bitmap.createScaledBitmap(frame, w, h, true);

		Bitmap newBM = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBM);
		canvas.drawBitmap(src, 0, 0, null);
		canvas.drawBitmap(sizeFrame, 0, 0, null);
		return newBM;
	}

	public static boolean isZh(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("zh"))
			return true;
		else
			return false;
	}

	public static void upDate(final Context context) {
		new Thread() {
			@Override
			public void run() {

				boolean isUpdate;
				boolean isOk = false;
				try {
					Timestamp time = new Timestamp(System.currentTimeMillis());
					String recent_checkTime = SharedPreferencesManager
							.getInstance()
							.getData(
									context,
									SharedPreferencesManager.SP_FILE_GWELL,
									SharedPreferencesManager.KEY_UPDATE_CHECKTIME);
					if (recent_checkTime.equals("")) {
						isOk = true;
					} else {
						Timestamp recentTime = Timestamp
								.valueOf(recent_checkTime);
						long now = time.getTime();
						long last = recentTime.getTime();
						if ((now - last) > (1000 * 60 * 60 * 24)) {
							isOk = true;
						}
					}
					if (!isOk) {
						return;
					}
					isUpdate = UpdateManager.getInstance().checkUpdate(
							NpcCommon.mThreeNum);
					if (isUpdate && isOk) {
						SharedPreferencesManager.getInstance().putData(context,
								SharedPreferencesManager.SP_FILE_GWELL,
								SharedPreferencesManager.KEY_UPDATE_CHECKTIME,
								time.toString());
						Intent i = new Intent(Constants.Action.ACTION_UPDATE);
						String data = "";
						if (Utils.isZh(MyApplication.app)) {
							data = UpdateManager.getInstance()
									.getUpdateDescription();
						} else {
							data = UpdateManager.getInstance()
									.getUpdateDescription_en();
						}
						i.putExtra("updateDescription", data);
						context.sendBroadcast(i);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void showPromptDialog(Context context, int title, int content) {
		NormalDialog dialog = new NormalDialog(context, context.getResources()
				.getString(title), context.getResources().getString(content),
				"", "");
		dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
		dialog.showDialog();
	}

	public static String intToIp(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xff) + "."
				+ ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);
	}

	public static HashMap getHash(String string) {
		try {
			HashMap map = new HashMap<String, String>();
			String[] item = string.split(",");
			for (int i = 0, len = item.length; i < len; i++) {
				String[] info = item[i].split(":");
				map.put("" + info[0], info[1]);
			}
			return map;
		} catch (Exception e) {
			return null;
		}

	}

	public static String getFormatTellDate(Context context, String time) {

		String year = context.getString(R.string.year_format);
		String month = context.getString(R.string.month_format);
		String day = context.getString(R.string.day_format);

		SimpleDateFormat sd = new SimpleDateFormat("yyyy" + year + "MM" + month
				+ "dd" + day + " HH:mm");
		Date dt = null;
		try {
			dt = new Date(Long.parseLong(time));
		} catch (Exception e) {
		}

		String s = "";
		if (dt != null) {
			s = sd.format(dt);
		}

		return s;
	}

	public static void sleepThread(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String[] getDeleteAlarmIdArray(String[] data, int position) {
		if (data.length == 1) {
			return new String[] { "0" };
		}

		String[] array = new String[data.length - 1];
		int count = 0;
		for (int i = 0; i < data.length; i++) {
			if (position != i) {
				array[count] = data[i];
				count++;
			}
		}

		return array;
	}

	public static String convertDeviceTime(int iYear, int iMonth, int iDay,
			int iHour, int iMinute) {
		int year = (2000 + iYear);
		int month = iMonth;
		int day = iDay;
		int hour = iHour;
		int minute = iMinute;

		StringBuilder sb = new StringBuilder();
		sb.append(year + "-");

		if (month < 10) {
			sb.append("0" + month + "-");
		} else {
			sb.append(month + "-");
		}

		if (day < 10) {
			sb.append("0" + day + " ");
		} else {
			sb.append(day + " ");
		}

		if (hour < 10) {
			sb.append("0" + hour + ":");
		} else {
			sb.append(hour + ":");
		}

		if (minute < 10) {
			sb.append("0" + minute);
		} else {
			sb.append("" + minute);
		}
		return sb.toString();
	}

	public static String convertPlanTime(int hour_from, int minute_from,
			int hour_to, int minute_to) {

		StringBuilder sb = new StringBuilder();

		if (hour_from < 10) {
			sb.append("0" + hour_from + ":");
		} else {
			sb.append(hour_from + ":");
		}

		if (minute_from < 10) {
			sb.append("0" + minute_from + "-");
		} else {
			sb.append(minute_from + "-");
		}

		if (hour_to < 10) {
			sb.append("0" + hour_to + ":");
		} else {
			sb.append(hour_to + ":");
		}

		if (minute_to < 10) {
			sb.append("0" + minute_to);
		} else {
			sb.append("" + minute_to);
		}

		return sb.toString();
	}

	public static boolean isNumeric(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
			file.delete();

		}
	}

	public static int dip2px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
				| ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
		return value;
	}

	public static int getColorByResouse(int resouse) {
		return MyApplication.app.getResources().getColor(resouse);
	}

	public static String getDeviceName(String deviceID) {
		Contact contact = DataManager.findContactByActiveUserAndContactId(
				MyApplication.app, NpcCommon.mThreeNum, deviceID);
		if (contact != null) {
			return contact.contactName;
		}
		return deviceID;
	}

	public static String getStringByResouceID(int R) {
		return MyApplication.app.getString(R);
	}

	public static String getAlarmType(String deviceId,int type, boolean isSupport, int group,
			int item ,String customMsg) {
		switch (type) {
		case P2PValue.AlarmType.EXTERNAL_ALARM:
			StringBuffer buffer = new StringBuffer();
			if (isSupport) {
				String defenceName;
				if (group<1) {
					defenceName=MyApplication.app.getResources().getString(R.string.remote);
				}else{
					defenceName=MyApplication.app.getResources().getString(R.string.sensor);
				}
				DefenceArea dArea=new DefenceArea();
				dArea.setGroup(group);
				dArea.setItem(item);
				DefenceArea defenceArea=DataManager.findDefenceAreaByDeviceID(MyApplication.app, deviceId, dArea);
				if (defenceArea==null) {
					defenceName=defenceName+":"+MyApplication.app.getResources().getString(R.string.area)+((group-1)*8+(item+1));
				}else {
					defenceName=defenceName+":"+defenceArea.getName();
				}
				buffer.append(getStringByResouceID(R.string.allarm_type1))
						.append("   ");
				buffer.append("\n");
//				buffer.append(getStringByResouceID(R.string.area)).append("：")
//						.append(getDefenceAreaByGroup(MyApp.app, group))
//						.append("  ")
//						.append(getStringByResouceID(R.string.channel))
//						.append("：").append(item + 1);
				buffer.append(defenceName);
			}
			return buffer.toString();
		case P2PValue.AlarmType.MOTION_DECT_ALARM:

			return getStringByResouceID(R.string.allarm_type2);
		case P2PValue.AlarmType.EMERGENCY_ALARM:

			return getStringByResouceID(R.string.allarm_type3);

		case P2PValue.AlarmType.LOW_VOL_ALARM:
			StringBuffer buffer2 = new StringBuffer();
			if (isSupport) {
				buffer2.append(getStringByResouceID(R.string.low_voltage_alarm))
						.append("   ");
				buffer2.append("\n");
				String defenceName;
				if (group<1) {
					defenceName=MyApplication.app.getResources().getString(R.string.remote);
				}else{
					defenceName=MyApplication.app.getResources().getString(R.string.sensor);
				}
				DefenceArea dArea=new DefenceArea();
				dArea.setGroup(group);
				dArea.setItem(item);
				DefenceArea defenceArea=DataManager.findDefenceAreaByDeviceID(MyApplication.app, deviceId, dArea);
				if (defenceArea==null) {
					defenceName=defenceName+":"+MyApplication.app.getResources().getString(R.string.area)+((group-1)*8+(item+1));
				}else {
					defenceName=defenceName+":"+defenceArea.getName();
				}
				buffer2.append(defenceName);
//				buffer2.append(getStringByResouceID(R.string.area)).append("：")
//						.append(getDefenceAreaByGroup(MyApp.app, group))
//						.append("  ")
//						.append(getStringByResouceID(R.string.channel))
//						.append("：").append(item + 1);
			}
			return buffer2.toString();
		case P2PValue.AlarmType.PIR_ALARM:

			return getStringByResouceID(R.string.allarm_type4);
		case P2PValue.AlarmType.EXT_LINE_ALARM:

			return getStringByResouceID(R.string.allarm_type5);
		case P2PValue.AlarmType.DEFENCE:

			return getStringByResouceID(R.string.defence);
		case P2PValue.AlarmType.NO_DEFENCE:

			return getStringByResouceID(R.string.no_defence);
		case P2PValue.AlarmType.BATTERY_LOW_ALARM:

			return getStringByResouceID(R.string.battery_low_alarm);
		case P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH:

			return getStringByResouceID(R.string.guest_coming);
		case P2PValue.AlarmType.RECORD_FAILED_ALARM:

			return getStringByResouceID(R.string.record_failed);
		case P2PValue.AlarmType.ALARM_TYPE_DOOR_MAGNET:

			return getStringByResouceID(R.string.door_alarm);
		case 999:
			return customMsg;
		      
		default:

			return getStringByResouceID(R.string.not_know);
		}
	}

	public static boolean isWeakPassword(String passWord) {
		if (getPassWordStatus(passWord) < 2) {
			return true;
		}
		return false;
	}

	/**
	 * 获取密码强弱标记
	 * 
	 * @param password
	 * @return
	 */
	private static String WEAK = "^[1-9]\\d*$|^[A-Z]+$|^[a-z]+$|^(.)\\1+$";
	private static String MID = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S+$";
	private static String STRONG = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,100}$";

	public static int getPassWordStatus(String password) {
		if (password.length() == 0) {
			return 0;
		} else if (password.length() < 6 || isRuo(password)) {
			return 1;
		}
		if (password.matches(MID)) {
			if (password.matches(STRONG)) {
				return 3;
			}
			return 2;
		}
		return -1;
	}

	/**
	 * 是否弱密码
	 * 
	 * @param password
	 * @return
	 */
	private static boolean isRuo(String password) {
		if (password.matches(WEAK)) {
			return true;
		}
		return false;
	}

	public static InetAddress getIntentAddress(Context mContext)
			throws IOException {
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifiManager.getDhcpInfo();
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	public static String getIntentAddress(Context mContext, String deviceID)
			throws IOException {
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int address = wifiInfo.getIpAddress();
		String ip = intToIp(address);
		String ipAddress = ip.substring(0, ip.lastIndexOf(".") + 1) + deviceID;
		return ipAddress;
	}

	/**
	 * 隐藏软键盘()
	 */
	public static void hindKeyBoard(View btnKey) {
		InputMethodManager imm = (InputMethodManager) MyApplication.app
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		if(imm!=null){
			imm.hideSoftInputFromWindow(btnKey.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * byte[]以16进制字符串输出
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder.append("[");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
			if (i != src.length - 1) {
				stringBuilder.append(", ");
			}
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	/**
	 * 检测邮箱格式
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmial(String email) {
		 String str=
		 "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
//		String str =
//		 "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
//	    "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
//		"^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})";
		Pattern pattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		// Matcher matcher = Pattern.compile(regEx).matcher(email);
		return matcher.matches();
	}

	/**
	 * WiFi是否加密
	 * 
	 * @param result
	 * @return
	 */
	public static boolean isWifiOpen(ScanResult result) {
		if (result.capabilities.toLowerCase().indexOf("wep") != -1
				|| result.capabilities.toLowerCase().indexOf("wpa") != -1) {
			return false;
		}
		return true;
	}

	/**
	 * 创建录像根目录 ~/videorecode
	 */
	public static File createRecodeFile() {
		if (!isSD()) {
			return null;
		}
		File sd = Environment.getExternalStorageDirectory();
		String path = sd.getPath() + "/videorecode";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		if (file == null) {
			Log.e("Utils", "create Recoding file failed");
		}
		return file;
	}

	/**
	 * 判断手机SD卡是否插入
	 * 
	 * @return
	 */
	public static boolean isSD() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置录像文件名（~/videorecoder/callId/callId_yyyy_MM_dd_HH_mm_ss.MP4）
	 * 
	 * @param callId
	 * @return
	 */
	public static String getVideoRecodeName(String callId) {
		if (!isSD()) {
			return "noSD";
		}
		long time = System.currentTimeMillis();
		File sd = Environment.getExternalStorageDirectory();
		String path = sd.getPath() + "/videorecode/" + callId;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");// 制定日期的显示格式
		String name = callId + "_" + sdf.format(new Date(time));
		String filename = file.getPath() + "/" + name + ".MP4";
		return filename;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
	 */
	public static int[] intToBytes2(int value) {
		int[] src = new int[4];
		src[3] = value & 0xFF;
		src[2] = (value >> 8) & 0xFF;
		src[1] = (value >> 16) & 0xFF;
		src[0] = (value >> 24) & 0xFF;

		return src;
	}

	/**
	 * DES加密
	 * 
	 * @param datasource
	 * @param password
	 * @return
	 */
	public static byte[] desCrypto(byte[] datasource, String password) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * DES解密
	 * 
	 * @param src
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(byte[] src, String password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return new String(cipher.doFinal(src));
	}

	public static byte[] gainWifiMode() {
		byte[] data = new byte[20];
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
		return data;
	}

	public static byte[] setDeviceApWifiPwd(String pwd) {
		byte[] data = new byte[272];
		for (int i = 0; i < data.length; i++) {
			data[0] = 0;
		}
		data[0] = 2;
		data[8] = 1;
		byte[] password = pwd.getBytes();
		for (int j = 0; j < password.length; j++) {
			data[j + 144] = password[j];
		}
		return data;

	}

	/**
	 * 截图存更新到相册
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean saveImgToGallery(String fileName) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (!sdCardExist)
			return false;
		if (fileName == null || fileName.length() <= 0)
			return false;
		try {
			ContentValues values = new ContentValues();
			values.put("datetaken", new Date().toString());
			values.put("mime_type", "image/png");
			values.put("_data", fileName);
			ContentResolver cr = MyApplication.app.getContentResolver();
			cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MediaScannerConnection.scanFile(MyApplication.app,
				new String[] { AppConfig.Relese.SCREENSHORT }, null, null);
		return true;

	}

	/**
	 * callId查询的Id 当其为“”时查询所有截图,type 1为时间降序 2为时间升序
	 */
	public static List<String> getScreenShotImagePath(final String callId,
			int type) {
		File[] data = new File[0];

		List<String> tempPathList = new ArrayList<String>();
		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();// 将需要的子文件信息存入到FileInfo里面
		File file = new File(AppConfig.Relese.SCREENSHORT);
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (null == callId || "".equals(callId)) {
					if (pathname.getName().endsWith(".jpg")) {
						return true;
					} else {
						return false;
					}
				} else {
					// final String
					// ipflag=FList.getInstance().getIpFlag(callId);
					if (pathname.getName().startsWith(callId)) {
						return true;
					} else {
						return false;
					}
				}
			}
		};
		data = file.listFiles(filter);
		if (null != data) {
			for (int i = 0, count = data.length; i < count; i++) {
				FileInfo info = new FileInfo();
				File f = data[i];
				info.path = f.getPath();
				info.LastModified = f.lastModified();
				fileList.add(info);
			}
			if (type == 1) {
				Collections.sort(fileList, new FileComparatorUp());// 通过重写Comparator的实现类
			} else {
				Collections.sort(fileList, new FileComparatorDown());// 通过重写Comparator的实现类
			}
			for (FileInfo info : fileList) {
				tempPathList.add(info.path);
			}

		}
		return tempPathList;
	}

	public static class FileInfo {
		long LastModified;
		String path;
	}

	public static class FileComparatorDown implements Comparator<FileInfo> {
		@Override
		public int compare(FileInfo file1, FileInfo file2) {
			if (file1.LastModified < file2.LastModified) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	public static class FileComparatorUp implements Comparator<FileInfo> {
		@Override
		public int compare(FileInfo file1, FileInfo file2) {
			if (file1.LastModified < file2.LastModified) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	public static int getColorByResouce(int R) {
		return MyApplication.app.getResources().getColor(R);
	}

	public static boolean checkPassword(String password) {
		if (password.length() < 10 && Utils.isNumeric(password)
				&& password.charAt(0) == '0') {
			return false;
		} else {
			return true;
		}
	}

	public static String getWifiName(String wifiname) {
		if (wifiname != null && wifiname.length() > 2) {
			int first = wifiname.charAt(0);
			if (first == 34) {
				wifiname = wifiname.substring(1, wifiname.length() - 1);
			}
		}
		return wifiname;
	}

	/**
	 * byte按位输出
	 * 
	 * @param info
	 * @param isOpposite
	 *            是否反向读取
	 * @return
	 */
	public static int[] getByteBinnery(byte info, boolean isOpposite) {
		int i;
		int j = 0;
		int[] gpiostatus = new int[8];
		if (isOpposite) {
			for (i = 0; i <= 7; ++i) {
				gpiostatus[j] = (byte) ((info >> i) & 0x01);
				j++;
			}
		} else {
			for (i = 7; i >= 0; --i) {
				gpiostatus[j] = (byte) ((info >> i) & 0x01);
				j++;
			}
		}
		return gpiostatus;
	}

	/**
	 * 指定某位二进制为1
	 * 
	 * @param src
	 * @param position
	 * @return
	 */
	public static byte ChangeBitTrue(byte src, int position) {
		return (byte) (src | (1 << position));
	}

	/**
	 * 指定某位二进制0，其他位保持不变
	 * 
	 * @param src
	 * @param position
	 * @return
	 */
	public static byte ChangeByteFalse(byte src, int position) {
		int t = Integer.MAX_VALUE ^ (1 << position);
		return (byte) (src & t);
	}

	/**
	 * 鱼眼可用
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToBytes(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) (value & 0xFF);
		src[1] = (byte) ((value >> 8) & 0xFF);
		src[2] = (byte) ((value >> 16) & 0xFF);
		src[3] = (byte) ((value >> 24) & 0xFF);

		return src;
	}

	public static String getStringForId(int StringId) {
		return MyApplication.app.getResources().getString(StringId);

	}

	public static String getPhoneIpdress() {
		WifiManager wifiManager = (WifiManager) MyApplication.app
				.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;

	}

	/**
	 * 判断Android版本是否正确
	 * 
	 * @param androidVersion
	 * @return
	 */
	public static boolean isSpecification(int androidVersion) {
		if (Build.VERSION.SDK_INT >= androidVersion) {
			return true;
		}
		return false;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] HexString2Bytes(String src) {
		byte[] tmp = src.getBytes();
		byte[] ret = new byte[tmp.length / 2];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 合成截图路径
	 * 
	 * @param id
	 * @return
	 */
	public static String getCapturePath(String id) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		Date date = calendar.getTime();
		return id + "_" + sdf.format(date) + ".jpg";
	}

	/**
	 * 文件名排序
	 * 
	 * @author Administrator
	 * 
	 */
	public static class FileUp implements Comparator<File> {
		@Override
		public int compare(File file1, File file2) {
			return file1.getName().compareToIgnoreCase(file2.getName());
		}
	}

	/**
	 * 获取jA头像文件列表
	 * 
	 * @param id
	 * @return
	 */
	public static File[] getHeaderImage(final String id) {
		File file = MyApplication.app.getExternalFilesDir(null);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().startsWith(id)) {
					return true;
				} else {
					return false;
				}
			}
		};
		File[] fs = file.listFiles(filter);
		Arrays.sort(fs, new FileUp());
		return fs;
	}

	/**
	 * 是否是只需要弹窗的错误码
	 * 
	 * @param errorCode
	 *            webAPI专用
	 * @return
	 */
	public static boolean isTostCmd(int errorCode) {
		if (errorCode == NetManager.SYSTEM_DOWN) {
			return true;
		}
		return false;
	}

	public static String GetToastCMDString(int errorCode) {
		switch (errorCode) {
		case NetManager.SYSTEM_DOWN:
			return getStringByResouceID(R.string.system_down);
		default:
			return String.valueOf(errorCode);
		}
	}

	/**
	 * 短暂震动
	 * 
	 * @param context
	 */
	public static void PhoneVibrator(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	/**
	 * 
	 * 判断是否支持智能家居
	 * 
	 * @param contactType设备类型
	 * @param subType设备子类型
	 * @return是否支持智能家居
	 */
	public static boolean isSmartHomeContatct(int contactType, int subType) {
		if (contactType == P2PValue.DeviceType.IPC
				&& (subType == P2PValue.subType.IPC_868_SCENE_MODE
						|| subType == P2PValue.subType.IPC_8
						|| subType == P2PValue.subType.IPC_18 || subType == P2PValue.subType.IPC_28)) {
			return true;
		}
		return false;
	}

    public static boolean is868Device(int contactType,int subType){
    	if(contactType==P2PValue.DeviceType.IPC&&(subType==P2PValue.subType.IPC_868||subType==P2PValue.subType.IPC_868_SCENE_MODE||subType==P2PValue.subType.IPC_868_SCENE_MODE_SHARE
				||subType==P2PValue.subType.IPC_8||subType==P2PValue.subType.IPC_18||subType==P2PValue.subType.IPC_28)){
			return true;
		}
		return false;
    }
  
    public static boolean isFastDoubleClick(){
		long time=System.currentTimeMillis();
		long timeD=time-lastClickTime;
		lastClickTime=time;
		if(timeD<1000){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 给Bitmap添加文字
	 */
	private static int stoken = 2;// 文字描边大小

	public static Bitmap changeImageView(String text, Bitmap photo) {
		int width = photo.getWidth(), hight = photo.getHeight();
		Bitmap icon = Bitmap
				.createBitmap(width, hight, Config.ARGB_8888); // 建立一个空的BItMap
		Canvas canvas = new Canvas(icon);// 初始化画布绘制的图像到icon上

		Paint photoPaint = new Paint(); // 建立画笔
		photoPaint.setDither(true); // 获取跟清晰的图像采样
		photoPaint.setFilterBitmap(true);// 过滤一些

		Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());// 创建一个指定的新矩形的坐标
		Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
		canvas.drawBitmap(photo, src, dst, photoPaint);// 将photo 缩放或则扩大到
														// dst使用的填充区photoPaint

		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
		textPaint.setTextSize(90);// 字体大小
		// textPaint.setTypeface(Typeface.SANS_SERIF);//采用默认的宽度
		textPaint.setColor(Color.WHITE);// 文字描边采用的颜色
		Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
		// 转载请注明出处：http://blog.csdn.net/hursing
		int baseline = dst.top
				+ (dst.bottom - dst.top - fontMetrics.bottom + fontMetrics.top)
				/ 2 - fontMetrics.top;
		// 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setShadowLayer(30, 0, 0, Color.BLACK);// 影音的设置
		canvas.drawText(text, dst.centerX() - stoken, baseline, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.drawText(text, dst.centerX() + stoken, baseline, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.drawText(text, dst.centerX(), baseline - stoken, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.drawText(text, dst.centerX(), baseline + stoken, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.drawText(text, dst.centerX() + stoken, baseline + stoken,
				textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.drawText(text, dst.centerX() - stoken, baseline - stoken,
				textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.drawText(text, dst.centerX() + stoken, baseline - stoken,
				textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.drawText(text, dst.centerX() - stoken, baseline + stoken,
				textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		textPaint.setColor(Color.RED);// 文字本体采用的颜色
		canvas.drawText(text, dst.centerX(), baseline, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return icon;
	}

	/**
	 * +-------------+-----------------+------------+ 默认比较数组(自然数)|源数组(自然数) |返回值
	 * | +-------------+-----------------+------------+ [0,1,2,3,...] |[2,3,67]
	 * |0 | +-------------+-----------------+------------+ [0,1,2,3,...]
	 * |[0,1,2,5,3] |4 | +-------------+-----------------+------------+
	 * 
	 * @param itemArray
	 *            源数组
	 * @return 源数组中不包含的首个自然数
	 */
	public static int getNextItem(int itemArray[]) {
		if (itemArray.length == 0)
			return 0;
		Arrays.sort(itemArray);
		for (int i = 0; i < itemArray.length; i++) {
			if (Arrays.binarySearch(itemArray, i) < 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 预置位命令相关，相关命令共用一个临时请求标记，所以没有监听ACK回调， 同时在设置与删除预置位时，附带发送一个获取已经设置预置位命令(按需设置)
	 * 
	 * @param contactID
	 * @param contactPwd
	 * @param bOpretion
	 *            操作
	 * @param presetNum
	 *            预置位
	 */
	public static void setPrePoints(String contactID, String contactPwd,
			int bOpretion, int presetNum) {
		if (contactPwd.length() > 0) {
			byte[] data = { 87, 0, (byte) bOpretion, (byte) presetNum };
			P2PHandler.getInstance().sMesgPresetMotorPos(contactID, contactPwd,
					data);
			if (bOpretion == 1 || bOpretion == 3) {
				P2PHandler.getInstance().sMesgPresetMotorPos(contactID,
						contactPwd, new byte[] { 87, 0, 2, 0 });
			}
		}
	}

	/**
	 * 拼接预置位图片路径
	 * 
	 * @param deviceId
	 * @param prepoint
	 * @return
	 */
	public static String getPrepointPath(String deviceId, int prepoint) {
		return AppConfig.Relese.PREPOINTPATH + File.separator + deviceId + "_"
				+ prepoint + ".jpg";
	}

	/**
	 * *文件的复制操作方法
	 * 
	 * @param fromFile
	 *            被复制的文件
	 * @param toFile
	 *            复制的目录文件
	 * @param rewrite
	 *            是否重新创建文件
	 * @param isDelteFromFile
	 *            是否删除源文件
	 * @return 操作成功与否
	 */
	public static int copyfile(File fromFile, File toFile, boolean rewrite,
			boolean isDelteFromFile) {
		int copyReslut = 0;
		if (!fromFile.exists()) {
			return 0;
		}
		if (!fromFile.isFile()) {
			return 0;
		}
		if (!fromFile.canRead()) {
			return 0;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}
		try {
			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);

			byte[] bt = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			// 关闭输入、输出流
			fosfrom.close();
			fosto.close();
			copyReslut = 1;
		} catch (IOException e) {
			copyReslut = 0;
			e.printStackTrace();
		}
		if (isDelteFromFile && fromFile.exists()) {
			fromFile.delete();
		}
		return copyReslut;
	}

	static String emailRegex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

	/*
	 * 验证邮箱
	 */
	public static boolean isEmail(String str) {
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static String getStringByByte(byte[] data,int offset,int len){
    	if(offset+len>data.length){return "";}
    	byte[] result=new byte[len];
    	System.arraycopy(data, offset, result, 0, len);
		return new String(result).trim();
    }
	/**
	 * 是否是全景鱼眼设备
	 * @param contactType
	 * @param subType
	 * @return
	 */
	public static boolean isFishPosContatct(int contactType, int subType) {
		if (contactType == P2PValue.DeviceType.IPC
				&& (subType == P2PValue.subType.IPC_868)) {
			return true;
		}
		return false;
	}
	/**
	 * 获取资源图片的宽高
	 * @param R 资源ID
	 * @return 宽高数组
	 */
	public static int[] getDrawableWAndrH(int R){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.app.getResources(), R, options);
		int[] wAndH=new int[2];
		wAndH[0]=options.outWidth;
		wAndH[1]=options.outHeight;
		Log.e("dxsTest", "w---h"+Arrays.toString(wAndH));
		return wAndH;
	}
	
	public static void setSelectMode(Contact mContact,int shapeType){
		shapeType=getShapeType(mContact,shapeType);
		MediaPlayer.getInstance().selectPanormaMode(mContact.subType,mContact.fishPos,shapeType);
	}
	
	public static String arryToString(byte[] data){
        int index=data.length;
        for (int i=0;i<data.length;i++){
            if (data[i]==0){
                index=i;
            }
            if (i>index){
                data[i]=0;
            }
        }
        return new String(data).trim();
    }
	
	public static int getShapeType(Contact mContact,int shapeType){
		if(mContact.is360Panorama()){
			if(shapeType==0){
				shapeType=4;
			}else if(shapeType==1){
				shapeType=3;
			}else if(shapeType==2){
				shapeType=2;
			}else if(shapeType==3){
				shapeType=0;
			}
		}else{
			if(shapeType==0){
				shapeType=1;
			}else if(shapeType==1){
				shapeType=2;
			}
		}
		return shapeType;
	}


//	public static boolean isMobileNO(String mobiles) {
//		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0,5-9])|(17[0-9]))\\d{8}$");
//		Matcher m = p.matcher(mobiles);
//		return m.matches();
//	}

	/**
	 * 简版国际手机号判断
	 * @param mobiles
	 * @return
     */
	public static boolean isMobileNO(String mobiles) {
		if(isNumeric(mobiles)){
			if(mobiles.length()>4 && mobiles.length()<16){
				return  true;
			}
		}
		return  false;
	}

	/**
	 * 获取AP模式设备的IP地址
	 * @param context
	 * @return 默认返回192.168.1.1
     */
	public static String getAPDeviceIp(Context context){
		String IP="192.168.1.1";
		try {
			WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcp = wifiManager.getDhcpInfo();
			//IP=Formatter.formatIpAddress(dhcp.serverAddress);
            IP=intToInetAddress(dhcp.serverAddress).getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return IP;
	}

    /**
     * int转IP(仅支持IPv4)
     * @param hostAddress
     * @return
     */
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    /**
     * 从预置位图片路径中解析出预置位
     * @param fileName
     * @return
     */
    public static int getPointFromPointPath(String fileName){
        if(fileName!=null&&fileName.length()>=5){
            try {
                return Integer.parseInt(String.valueOf(fileName.charAt(fileName.lastIndexOf(".")-1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * AP设备wifi名截取'_' 获得ID号
     * @param wifiName
     * @return
     */
    public static String wifiNameSubToContactID(String wifiName){
        if(!TextUtils.isEmpty(wifiName)){
            int lastIndex=wifiName.lastIndexOf("_");
            if (lastIndex!=-1){
                return wifiName.substring(lastIndex+1);
            }
        }
        return wifiName;
    }

	/**
	 * 判断包名是否是com.yoosee
	 */
	public static boolean isYooseePackge(){
		String packageName = "aom.yoosee".replace("a", "c") ;
		return packageName.equals(MyApplication.app.getPackageName());
	}
}
