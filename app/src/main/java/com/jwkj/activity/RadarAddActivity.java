package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.ConfirmDialog;
import com.jwkj.widget.ConfirmOrCancelDialog;
import com.jwkj.widget.PromptDialog;
import com.jwkj.widget.PwdTextView;
import com.lsemtmf.genersdk.tools.SettingManager;
import com.lsemtmf.genersdk.tools.commen.PreventViolence;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.List;

public class RadarAddActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	String ssid;
	int type;
	int mLocalIp;
	Button bt_next;
	PwdTextView edit_pwd;
	ImageView back_btn;
	boolean bool1, bool2, bool3, bool4;
	private byte mAuthMode;
	private byte AuthModeAutoSwitch = 2;
	private byte AuthModeOpen = 0;
	private byte AuthModeShared = 1;
	private byte AuthModeWPA = 3;
	private byte AuthModeWPA1PSKWPA2PSK = 9;
	private byte AuthModeWPA1WPA2 = 8;
	private byte AuthModeWPA2 = 6;
	private byte AuthModeWPA2PSK = 7;
	private byte AuthModeWPANone = 5;
	private byte AuthModeWPAPSK = 4;
	boolean isRegFilter = false;
	private RelativeLayout rlPwd;
	private boolean isWifiOpen = false;
	SettingManager settingManager;
	private TextView tx_wifi_require;
	private TextView tx_wifi_name;
    private PromptDialog promptDialog;
	private ConfirmOrCancelDialog confirmDialog;
	private TextView tx_connect_wifi;
	String wifiPwd;
	ConfirmDialog dialog;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		mContext = this;
		setContentView(R.layout.activity_radar_add);
		initComponent();
		regFilter();
		currentWifi();
	}

	public void initComponent() {
		edit_pwd = (PwdTextView) findViewById(R.id.edit_pwdeditext);
		back_btn = (ImageView) findViewById(R.id.back_btn);
		bt_next = (Button) findViewById(R.id.next);
		rlPwd = (RelativeLayout) findViewById(R.id.layout_pwd);
		tx_wifi_require=(TextView)findViewById(R.id.tx_wifi_require);
		tx_wifi_name=(TextView)findViewById(R.id.tx_wifi_name);
		tx_connect_wifi=(TextView)findViewById(R.id.tx_connect_wifi);
		bt_next.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		tx_wifi_require.setOnClickListener(this);
		tx_wifi_require.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		//把Wi-Fi文字改为蓝色
//		String wholeStr = getResources().getString(R.string.camera_need_network);
//        StringFormatUtil spanStr = new StringFormatUtil(this, wholeStr,
//                  "Wi-Fi", R.color.radar_blue).fillColor();
//        if(spanStr!=null&&spanStr.getResult()!=null){
//			tx_connect_wifi.setText(spanStr.getResult());
//        }
//		edit_pwd.addTextChangedListener(textWatcher);
	}
//	private TextWatcher textWatcher =new TextWatcher() {
//		@Override
//		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//		}
//
//		@Override
//		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//		}
//
//		@Override
//		public void afterTextChanged(Editable editable) {
//			String pwd=edit_pwd.getText().toString();
//			if(pwd==null||pwd.equals("")){
//				bt_next.setEnabled(false);
//				bt_next.setBackgroundResource(R.drawable.bg_button_not_press);
//			}else{
//				bt_next.setEnabled(true);
//				bt_next.setBackgroundResource(R.drawable.bg_button_style);
//			}
//		}
//	};

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.RADAR_SET_WIFI_FAILED);
		filter.addAction(Constants.Action.RADAR_SET_WIFI_SUCCESS);
		filter.addAction(Constants.Action.EXIT_ADD_DEVICE);
		filter.addAction(Constants.Action.ACTION_NETWORK_CHANGE);
		registerReceiver(br, filter);
		isRegFilter = true;

	}

	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(
					Constants.Action.RADAR_SET_WIFI_FAILED)) {
//				NormalDialog dialog = new NormalDialog(mContext);
//				dialog.setOnButtonCancelListener(new NormalDialog.OnButtonCancelListener() {
//					@Override
//					public void onClick() {
//						// TODO Auto-generated method stub
//						finish();
//					}
//				});
//				dialog.showConnectFail();
				finish();
			} else if (intent.getAction().equals(
					Constants.Action.RADAR_SET_WIFI_SUCCESS)) {
				finish();
			}else if(intent.getAction().equals(Constants.Action.EXIT_ADD_DEVICE)){
				finish();
			}else if(intent.getAction().equals(
					Constants.Action.ACTION_NETWORK_CHANGE)){
				currentWifi();
			}
		}
	};

	public void currentWifi() {
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!manager.isWifiEnabled())
			return;
		WifiInfo info = manager.getConnectionInfo();
		ssid = info.getSSID();
		mLocalIp = info.getIpAddress();
		List<ScanResult> datas = new ArrayList<ScanResult>();
		if (!manager.isWifiEnabled())
			return;
		manager.startScan();
		datas = manager.getScanResults();
		if (ssid == null) {
			return;
		}
		if (ssid.equals("")) {
			return;
		}
		int a = ssid.charAt(0);
		if (a == 34) {
			ssid = ssid.substring(1, ssid.length() - 1);
		}
		if (!ssid.equals("<unknown ssid>") && !ssid.equals("0x")) {
			tx_wifi_name.setText(ssid);
		}
		if(datas==null){
			return;
		}
		for (int i = 0; i < datas.size(); i++) {
			ScanResult result = datas.get(i);
			if (!result.SSID.equals(ssid)) {
				continue;
			}
			if (Utils.isWifiOpen(result)) {
				type = 0;
				isWifiOpen = true;
				rlPwd.setVisibility(View.GONE);
			} else {
				type = 1;
				isWifiOpen = false;
				rlPwd.setVisibility(View.VISIBLE);
			}
			bool1 = result.capabilities.contains("WPA-PSK");
			bool2 = result.capabilities.contains("WPA2-PSK");
			bool3 = result.capabilities.contains("WPA-EAP");
			bool4 = result.capabilities.contains("WPA2-EAP");
			if (result.capabilities.contains("WEP")) {
				this.mAuthMode = this.AuthModeOpen;
			}
			if ((bool1) && (bool2)) {
				mAuthMode = AuthModeWPA1PSKWPA2PSK;
			} else if (bool2) {
				this.mAuthMode = this.AuthModeWPA2PSK;
			} else if (bool1) {
				this.mAuthMode = this.AuthModeWPAPSK;
			} else if ((bool3) && (bool4)) {
				this.mAuthMode = this.AuthModeWPA1WPA2;
			} else if (bool4) {
				this.mAuthMode = this.AuthModeWPA2;
			} else {
				if (!bool3)
					break;
				this.mAuthMode = this.AuthModeWPA;
			}

		}
		String wifipwd=SharedPreferencesManager.getInstance().getWifiPwd(mContext, ssid);
		edit_pwd.setText(wifipwd);

	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_RARDARADDACTIVITY;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next:
			if(!WifiUtils.getInstance().isWifiConnected(mContext)){
				dialog=new ConfirmDialog(mContext);
				dialog.setTitle(getResources().getString(R.string.please_connect_wifi));
				dialog.setGravity(Gravity.TOP);
				dialog.setTxButton(getResources().getString(R.string.i_get_it));
				dialog.show();
				return;
			}
			PreventViolence.preventClick(mContext,v, PreventViolence.LONG_TIME);
			Utils.hindKeyBoard(v);
			wifiPwd = edit_pwd.getText().toString();
			if (ssid == null || ssid.equals("")) {
				T.showShort(mContext, R.string.please_choose_wireless);
				return;
			}
			if (ssid.equals("<unknown ssid>")) {
				T.showShort(mContext, R.string.please_choose_wireless);
				return;
			}
			if (!isWifiOpen) {
				if (null == wifiPwd || wifiPwd.length() <= 0
						&& (type == 1 || type == 2)) {
					T.showShort(mContext, R.string.please_input_wifi_password);
					return;
				}
			}
			if(wifiPwd.equals("")){
				if(confirmDialog!=null&&confirmDialog.isShowing()){
				    return;
			    }
				confirmDialog=new ConfirmOrCancelDialog(mContext,ConfirmOrCancelDialog.SELECTOR_BLUE_TEXT,ConfirmOrCancelDialog.SELECTOR_GARY_TEXT);
				confirmDialog.setTitle(getResources().getString(R.string.wifi_no_pwd));
				confirmDialog.setTextYes(getResources().getString(R.string.confirm));
				confirmDialog.setTextNo(getResources().getString(R.string.exit));
				confirmDialog.setOnYesClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					SharedPreferencesManager.getInstance().putWifiPwd(mContext,ssid, wifiPwd);
					Intent deviceReady=new Intent(mContext,DeviceReadyActivity.class);
					deviceReady.putExtra("ssidname", ssid);
					deviceReady.putExtra("wifiPwd", wifiPwd);
					deviceReady.putExtra("type", mAuthMode);
					deviceReady.putExtra("LocalIp", mLocalIp);
					startActivity(deviceReady);
				}
			});
				confirmDialog.setOnNoClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					confirmDialog.dismiss();
				}
			});
				confirmDialog.show();
				return;
			}
			boolean canSend=true;
			String alarm="为了保证手机能正常发声,";
//			if(RingerModelTools.getCurRingModel(MyApp.app.getApplicationContext()!=AudioManager.RINGER_MODE_NORMAL)){
//				canSend=false;
//				alarm=alarm+"请将手机的情景模式设置为户外模式或标准模式;";
//			}
//			if(RingerModelTools.isWiredHeadsetOn(mContext)){
//				canSend=false;
//				alarm=alarm+"请将耳机拔出！";
//			}
//			if(canSend){
//				if (ssid.equals("") ) {
//					AlertUtils.SimpleAlert(mContext, "输入有误！",
//							"SSID输入为空，请输入后再提交！");
//				}
//				else {
					
//					if(WifiSetRemindTools.guideType==ProductInfoEntity.GuideType_Doubleclick){
//						ActivitySetting.startUnfinishedActivity(a, WifiSetRemindActivity.class,null);
//					}else if(WifiSetRemindTools.guideType==ProductInfoEntity.GuideType_Voice){
//						ActivitySetting.startUnfinishedActivity(a, WifiSetNokeyRemindActivity.class,null);
//					}
//					settingManager.setSPStringValue(ssid, wifiPwd);
			        SharedPreferencesManager.getInstance().putWifiPwd(mContext,ssid, wifiPwd);
			        Intent deviceReady=new Intent(this,DeviceReadyActivity.class);
			        deviceReady.putExtra("ssidname", ssid);
					deviceReady.putExtra("wifiPwd", wifiPwd);
					deviceReady.putExtra("type", mAuthMode);
					deviceReady.putExtra("LocalIp", mLocalIp);
			        startActivity(deviceReady);

//				}
//			}else{
//				AlertUtils.SimpleAlert(mContext, "操作有误！",alarm);
//			}
			break;
		case R.id.back_btn:
//			if(backDialg!=null&&backDialg.isShowing()){
//				return;
//			}
//			backDialg=new ConfirmOrCancelDialog(mContext,ConfirmOrCancelDialog.SELECTOR_BLUE_TEXT,ConfirmOrCancelDialog.SELECTOR_GARY_TEXT);
//			backDialg.setTitle(getResources().getString(R.string.exit_add_device));
//			backDialg.setTextYes(getResources().getString(R.string.exit));
//			backDialg.setTextNo(getResources().getString(R.string.continue_add));
//			backDialg.setOnYesClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					Intent exit=new Intent();
//					exit.setAction(Constants.Action.EXIT_ADD_DEVICE);
//					sendBroadcast(exit);
//					finish();
//				}
//			});
//			backDialg.setOnNoClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					backDialg.dismiss();
//				}
//			});
//			backDialg.show();
			finish();
			break;
		case R.id.tx_wifi_require:
			if(promptDialog!=null&&promptDialog.isShowing()){
				return;
			}
			promptDialog=new PromptDialog(mContext);
			promptDialog.setTitle(getResources().getString(R.string.camera_wifi_require));
			View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_wifi_require,null);
			promptDialog.addView(view);
			promptDialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isRegFilter == true) {
			unregisterReceiver(br);
			isRegFilter = false;
		}
	}
}
