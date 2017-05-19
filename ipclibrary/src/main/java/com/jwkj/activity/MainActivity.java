package com.jwkj.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwkj.P2PListener;
import com.jwkj.SettingListener;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemMsg;
import com.jwkj.entity.Account;
import com.jwkj.fragment.APContactFrag;
import com.jwkj.fragment.APContactFrag.ChangeMode;
import com.jwkj.fragment.ContactFrag;
import com.jwkj.fragment.KeyboardFrag;
import com.jwkj.fragment.SettingFrag;
import com.jwkj.fragment.UtilsFrag;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.global.NpcCommon.NETWORK_TYPE;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.NormalDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.p2p.core.P2PHandler;
import com.p2p.core.global.Config;
import com.p2p.core.network.GetAccountInfoResult;
import com.p2p.core.network.GetStartInfoResult;
import com.p2p.core.network.MallUrlResult;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.SetStoreIdResult;
import com.p2p.core.network.SystemMessageResult;
import com.p2p.core.network.SystemMessageResult.SystemMessage;
import com.p2p.core.update.UpdateManager;
import com.p2p.core.utils.BaiduTjUtils;
import com.p2p.core.utils.MyUtils;
import com.nuowei.ipclibrary.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.base64.Base64;


public class MainActivity extends BaseActivity implements OnClickListener {
	public Context mContext;

	private ImageView dials_img, contact_img, recent_img, settings_img,
			discover_img;
	private RelativeLayout contact, dials, recent, settings, discover;
	boolean isRegFilter = false;

	private int currFrag = 0;
	private AlertDialog dialog_update;
	private AlertDialog dialog_downapk;
	private ProgressBar downApkBar;
	private TextView tv_contact, tv_message, tv_image, tv_more;

	private String[] fragTags = new String[] { "contactFrag", "keyboardFrag",
			"nearlyTellFrag", "utilsFrag", "settingFrag", "apcontactFrag" };
	private SettingFrag settingFrag;
	private KeyboardFrag keyboardFrag;
	private ContactFrag contactFrag;
	private UtilsFrag utilsFrag;
	private APContactFrag apcontactFrag;
	boolean isApEnter = false;
	public static boolean isConnectApWifi=false;
	private NormalDialog dialog;
	int insertCount=0;
	int count=2;
	boolean isInitEMTMFSDK=false;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		P2PHandler.getInstance().p2pInit(this, new P2PListener(),new SettingListener());
		super.onCreate(arg0);
		 ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
         // 添加你的配置需求
         .build();
        ImageLoader.getInstance().init(configuration);
		mContext = this;
		isConnectApWifi=getIntent().getBooleanExtra("isConnectApWifi", false);
		isConnectWifi(isConnectApWifi);
		getHightAndWight();
//	    DataManager.findAlarmMaskByActiveUser(mContext, "");
//		NpcCommon.verifyNetwork(mContext);
//		regFilter();
	}
	//获取屏幕宽高，存储在Application中,后面的获取可舍弃
	void getHightAndWight() {
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		MyApp.SCREENWIGHT = dm.widthPixels;
		MyApp.SCREENHIGHT = dm.heightPixels;
	}
	
	private void isConnectWifi(boolean is){
		if(is){
			initComponent();
			new FList();
			regFilter();
			currFrag = 0;
			connect();
			if (null == apcontactFrag) {
				apcontactFrag = new APContactFrag();
			}
			apcontactFrag.setOnChangeMode(Change);
			replaceFragment(R.id.fragContainer, apcontactFrag, fragTags[5]);
			changeIconShow();
			getHelpUrl();
		}else{
		    if (!verifyLogin()) {
				Intent login = new Intent(mContext, LoginActivity.class);
				startActivity(login);
				finish();
			} else {
				initComponent();
				new FList();
				NpcCommon.verifyNetwork(mContext);
				regFilter();
				connect();
				currFrag = 0;
					if (null == contactFrag) {
						contactFrag = new ContactFrag();
					}
					replaceFragment(R.id.fragContainer, contactFrag, fragTags[0]);
					changeIconShow();
				if (!NpcCommon.mThreeNum.equals("0517401")) {
			          new GetAccountInfoTask().execute();
				}
				getMallMsg();
				getHelpUrl();
//	           WifiUtils.getInstance().isApDevice();
			}
		}
	}
	private ChangeMode Change=new ChangeMode() {
		
		@Override
		public void OnChangeMode() {
			String wifiName=WifiUtils.getInstance().getConnectWifiName();
			WifiUtils.getInstance().DisConnectWifi(wifiName);
//			WifiUtils.getInstance().SetWiFiEnAble(false);
			Intent canel = new Intent();
			canel.setAction(Constants.Action.ACTION_SWITCH_USER);
			mContext.sendBroadcast(canel);
		}
	};

	public void initComponent() {
		setContentView(R.layout.activity_main);
		dials = (RelativeLayout) findViewById(R.id.icon_keyboard);
		dials_img = (ImageView) findViewById(R.id.icon_keyboard_img);
		contact = (RelativeLayout) findViewById(R.id.icon_contact);
		contact_img = (ImageView) findViewById(R.id.icon_contact_img);
		recent = (RelativeLayout) findViewById(R.id.icon_nearlytell);
		recent_img = (ImageView) findViewById(R.id.icon_nearlytell_img);
		settings = (RelativeLayout) findViewById(R.id.icon_setting);
		settings_img = (ImageView) findViewById(R.id.icon_setting_img);
		discover = (RelativeLayout) findViewById(R.id.icon_discover);
		discover_img = (ImageView) findViewById(R.id.icon_discover_img);
		tv_contact = (TextView) findViewById(R.id.tv_contact);
		tv_message = (TextView) findViewById(R.id.tv_message);
		tv_image = (TextView) findViewById(R.id.tv_image);
		tv_more = (TextView) findViewById(R.id.tv_more);

		dials.setOnClickListener(this);
		contact.setOnClickListener(this);
		recent.setOnClickListener(this);
		settings.setOnClickListener(this);
		discover.setOnClickListener(this);
//		if (isConnectApWifi) {
//			dials.setVisibility(RelativeLayout.GONE);
//			settings.setVisibility(RelativeLayout.GONE);
//		} else {
//			dials.setVisibility(RelativeLayout.VISIBLE);
//			settings.setVisibility(RelativeLayout.VISIBLE);
//		}
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.ACTION_NETWORK_CHANGE);
		filter.addAction(Constants.Action.ACTION_SWITCH_USER);
		filter.addAction(Constants.Action.ACTION_EXIT);
		filter.addAction(Constants.Action.RECEIVE_MSG);
		filter.addAction(Constants.Action.RECEIVE_SYS_MSG);
		filter.addAction(Intent.ACTION_LOCALE_CHANGED);
		filter.addAction(Constants.Action.ACTION_UPDATE);
		filter.addAction(Constants.Action.SESSION_ID_ERROR);
		filter.addAction(Constants.Action.EXITE_AP_MODE);
		filter.addAction(Constants.P2P.RET_NEW_SYSTEM_MESSAGE);
		filter.addAction(Constants.P2P.RET_GETFISHINFO);
		filter.addAction("DISCONNECT");
		// filter.addAction(Constants.Action.SETTING_WIFI_SUCCESS);
		this.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	Handler handler = new Handler() {
		long last_time;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int value = msg.arg1;
			switch (msg.what) {
			case UpdateManager.HANDLE_MSG_DOWNING:
				if ((System.currentTimeMillis() - last_time) > 1000) {
					MyApp.app.showDownNotification(
							UpdateManager.HANDLE_MSG_DOWNING, value);
					last_time = System.currentTimeMillis();
				}
				break;
			case UpdateManager.HANDLE_MSG_DOWN_SUCCESS:
				// MyApp.app.showDownNotification(UpdateManager.HANDLE_MSG_DOWN_SUCCESS,0);
				MyApp.app.hideDownNotification();
				// T.showShort(mContext, R.string.down_success);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/" + Constants.Update.SAVE_PATH + "/"
						+ Constants.Update.FILE_NAME);
				if (!file.exists()) {
					return;
				}
				intent.setDataAndType(Uri.fromFile(file),
						Constants.Update.INSTALL_APK);
				mContext.startActivity(intent);
				break;
			case UpdateManager.HANDLE_MSG_DOWN_FAULT:

				MyApp.app.showDownNotification(
						UpdateManager.HANDLE_MSG_DOWN_FAULT, value);
				T.showShort(mContext, R.string.down_fault);
				break;
			}
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(
					Constants.Action.ACTION_NETWORK_CHANGE)) {
				boolean isNetConnect = false;
				ConnectivityManager connectivityManager = (ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				Log.e("connectwifi", "--------");
				if (activeNetInfo != null) {
					 Log.e("connectwifi", "connect++++++");
					if (activeNetInfo.isConnected()) {
						 Log.e("connectwifi", "connect++++++ok");
						isNetConnect = true;
						T.showShort(mContext,
								getString(R.string.message_net_connect)
										+ activeNetInfo.getTypeName());
						WifiManager wifimanager = (WifiManager) mContext
								.getSystemService(mContext.WIFI_SERVICE);
						if(wifimanager==null){
						    return;	
						}
						WifiInfo wifiinfo = wifimanager.getConnectionInfo();
						if(wifiinfo==null){
							return;
						}
						if(wifiinfo.getSSID()==null){
							return;
						}
						if(wifiinfo.getSSID().length()>0){
							String wifiName = Utils.getWifiName(wifiinfo.getSSID());
							if (wifiName.startsWith(AppConfig.Relese.APTAG)) {
								String id = wifiName
										.substring(AppConfig.Relese.APTAG.length());
//								APList.getInstance().gainDeviceMode(id);
//								FList.getInstance().gainDeviceMode(id);
								FList.getInstance().setIsConnectApWifi(id,true);
							}else{
								FList.getInstance().setAllApUnLink();
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intentNew = new Intent();
						intentNew
								.setAction(Constants.Action.NET_WORK_TYPE_CHANGE);
						mContext.sendBroadcast(intentNew);
						WifiUtils.getInstance().isApDevice();
					} else {
						T.showShort(mContext, getString(R.string.network_error)
								+ " " + activeNetInfo.getTypeName());
					}

					if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						NpcCommon.mNetWorkType = NETWORK_TYPE.NETWORK_WIFI;
					} else {
						NpcCommon.mNetWorkType = NETWORK_TYPE.NETWORK_2GOR3G;
					}
				} else {
					Toast.makeText(mContext, getString(R.string.network_error),
							Toast.LENGTH_SHORT).show();
				}

				NpcCommon.setNetWorkState(isNetConnect);

				// Intent intentNew = new Intent();
				// intentNew.setAction(Constants.Action.NET_WORK_TYPE_CHANGE);
				// mContext.sendBroadcast(intentNew);
			} else if (intent.getAction().equals(
					Constants.Action.ACTION_SWITCH_USER)) {
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mContext);
				if(!account.three_number.equals("0517401")){
					new ExitTask(account).execute();
				}
				AccountPersist.getInstance().setActiveAccount(mContext,
						new Account());
				NpcCommon.mThreeNum = "";
				Intent i = new Intent(MyApp.MAIN_SERVICE_START);
				i .setPackage(getPackageName());
				stopService(i);
				dialog=new NormalDialog(mContext);
				dialog.showLoadingDialog2();
			} else if (intent.getAction().equals(
					Constants.Action.SESSION_ID_ERROR)) {
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mContext);
				new ExitTask(account).execute();
				AccountPersist.getInstance().setActiveAccount(mContext,
						new Account());
				Intent i = new Intent(MyApp.MAIN_SERVICE_START);
				i .setPackage(getPackageName());
				stopService(i);
				Intent login = new Intent(mContext, LoginActivity.class);
				startActivity(login);
				finish();
			} else if (intent.getAction().equals(Constants.Action.ACTION_EXIT)) {
				NormalDialog dialog = new NormalDialog(mContext, mContext
						.getResources().getString(R.string.exit), mContext
						.getResources().getString(R.string.confirm_exit),
						mContext.getResources().getString(R.string.exit),
						mContext.getResources().getString(R.string.cancel));
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

					@Override
					public void onClick() {
						Intent i = new Intent(MyApp.MAIN_SERVICE_START);
						i .setPackage(getPackageName());
						stopService(i);
						isGoExit(true);
						finish();
					}
				});
				dialog.showNormalDialog();
			} else if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {

			} else if (intent.getAction().equals(Constants.Action.RECEIVE_MSG)) {
				int result = intent.getIntExtra("result", -1);
				String msgFlag = intent.getStringExtra("msgFlag");

				if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
					DataManager.updateMessageStateByFlag(mContext, msgFlag,
							Constants.MessageType.SEND_SUCCESS);
				} else {
					DataManager.updateMessageStateByFlag(mContext, msgFlag,
							Constants.MessageType.SEND_FAULT);
				}

			} else if (intent.getAction().equals(
					Constants.Action.RECEIVE_SYS_MSG)) {

			} else if (intent.getAction()
					.equals(Constants.Action.ACTION_UPDATE)) {
				if (null != dialog_update && dialog_update.isShowing()) {
					Log.e("my", "isShowing");
					return;
				}

				View view = LayoutInflater.from(mContext).inflate(
						R.layout.dialog_update, null);
				TextView title = (TextView) view.findViewById(R.id.title_text);
				WebView content = (WebView) view
						.findViewById(R.id.content_text);
				TextView button1 = (TextView) view
						.findViewById(R.id.button1_text);
				TextView button2 = (TextView) view
						.findViewById(R.id.button2_text);

				title.setText(R.string.update);
				content.setBackgroundColor(0); // 设置背景色
				content.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
				String data = intent.getStringExtra("updateDescription");
				content.loadDataWithBaseURL(null, data, "text/html", "utf-8",
						null);
				button1.setText(R.string.update_now);
				button2.setText(R.string.next_time);
				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != dialog_update) {
							dialog_update.dismiss();
							dialog_update = null;
						}
						if (UpdateManager.getInstance().getIsDowning()) {
							return;
						}
						MyApp.app.showDownNotification(
								UpdateManager.HANDLE_MSG_DOWNING, 0);
						T.showShort(mContext, R.string.start_down);
						new Thread() {
							public void run() {
								UpdateManager.getInstance().downloadApk(
										handler, Constants.Update.SAVE_PATH,
										Constants.Update.FILE_NAME);
							}
						}.start();
					}
				});
				button2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != dialog_update) {
							dialog_update.cancel();
						}
					}
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				dialog_update = builder.create();
				dialog_update.show();
				dialog_update.setContentView(view);
				LayoutParams layout = (LayoutParams) view
						.getLayoutParams();
				layout.width = (int) mContext.getResources().getDimension(
						R.dimen.update_dialog_width);
				view.setLayoutParams(layout);
				dialog_update.setCanceledOnTouchOutside(false);
				Window window = dialog_update.getWindow();
				window.setWindowAnimations(R.style.dialog_normal);
			} else if (intent.getAction().equals(
					Constants.Action.SETTING_WIFI_SUCCESS)) {
				currFrag = 0;
				if (null == contactFrag) {
					contactFrag = new ContactFrag();
				}
				replaceFragment(R.id.fragContainer, contactFrag, fragTags[0]);
				changeIconShow();
			} else if (intent.getAction()
					.equals(Constants.Action.EXITE_AP_MODE)) {
				finish();
			}else if(intent.getAction().equals("DISCONNECT")){
				if(dialog!=null&&dialog.isShowing()){
					dialog.dismiss();
				}
				isConnectApWifi=false;
				isConnectWifi(isConnectApWifi);
			}else if(intent.getAction().equals(Constants.P2P.RET_NEW_SYSTEM_MESSAGE)){
				Log.e("new_message","new message comming----");
				String iSystemMessageIndex=intent.getStringExtra("iSystemMessageIndex");
				String sellerId=Config.AppConfig.store_id;
				if(sellerId!=null&&!sellerId.equals("")&&!sellerId.equals("0")){
				     String msgId=DataManager.findLastMsgIdSystemMessage(mContext, NpcCommon.mThreeNum);
				     if(msgId==null||msgId.equals("")){
				    	 //数据库中没有的话，取最新的
				    	 new SystemTask(sellerId,2).execute();	
				    	 Log.e("new_message","first new message----");
				     }else{
				    	//数据库中有，Option 取值：0向后（下[比MsgIndex新的数据]）
				    	 new GetNewSystemTaskByMsgID(sellerId, msgId,0).execute();	
				    	 Log.e("new_message","later new message----");
				     }
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_GETFISHINFO)){
            	String id=intent.getStringExtra("iSrcID");
            	byte[] data=intent.getByteArrayExtra("data");
            	try {
            		String pre=Utils.getStringByByte(data, 2, 32);
                	String recode=Utils.getStringByByte(data, 34, 32);
                	short pos=MyUtils.byte2ToShort(data, 66);
                	if(pre.contains("X")){
                		int w=Integer.parseInt(pre.split("X")[0]);
                		int h=Integer.parseInt(pre.split("X")[1]);
                		FList.getInstance().setVideowh(id,w,h,pos);
                	}
				} catch (Exception e) {
					
				}
            	
            }
		}

	};

	private void connect() {
		Intent service = new Intent(MyApp.MAIN_SERVICE_START);
		service.setPackage(getPackageName());
		startService(service);
		if (AppConfig.DeBug.isWrightAllLog) {
			Intent log = new Intent(MyApp.LOGCAT);
			log.setPackage(getPackageName());
			startService(log);
		}
	}

	private boolean verifyLogin() {
		Account activeUser = AccountPersist.getInstance().getActiveAccountInfo(
				mContext);

		if (activeUser != null&&!activeUser.three_number.equals("0517401")) {
			NpcCommon.mThreeNum = activeUser.three_number;
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int i = view.getId();
		if (i == R.id.icon_contact) {
			currFrag = 0;
			if (isConnectApWifi) {
				if (null == apcontactFrag) {
					apcontactFrag = new APContactFrag();
				}
				replaceFragment(R.id.fragContainer, apcontactFrag, fragTags[5]);
				changeIconShow();
			} else {
				if (null == contactFrag) {
					contactFrag = new ContactFrag();
				}
				replaceFragment(R.id.fragContainer, contactFrag, fragTags[0]);
				changeIconShow();
			}

		} else if (i == R.id.icon_keyboard) {
			BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_KEYBOARD, "Alarm messages");
			ContactFrag.isHideAdd = true;
			currFrag = 1;
			if (null == keyboardFrag) {
				keyboardFrag = new KeyboardFrag();
			}
			replaceFragment(R.id.fragContainer, keyboardFrag, fragTags[1]);
			changeIconShow();

		} else if (i == R.id.icon_nearlytell) {
		} else if (i == R.id.icon_setting) {
			ContactFrag.isHideAdd = true;
			currFrag = 3;
			if (null == settingFrag) {
				settingFrag = new SettingFrag();
			}
			replaceFragment(R.id.fragContainer, settingFrag, fragTags[4]);
			changeIconShow();

		} else if (i == R.id.icon_discover) {
			ContactFrag.isHideAdd = true;
			currFrag = 4;
			if (null == utilsFrag) {
				utilsFrag = new UtilsFrag();
			}
			replaceFragment(R.id.fragContainer, utilsFrag, fragTags[3]);
			changeIconShow();

		}

	}

	public void changeIconShow() {
		switch (currFrag) {
		case 0:
			contact_img.setImageResource(R.drawable.contact_p);
			dials_img.setImageResource(R.drawable.keyboard);
			recent_img.setImageResource(R.drawable.recent);
			settings_img.setImageResource(R.drawable.setting);
			discover_img.setImageResource(R.drawable.toolbox);
			tv_contact.setTextColor(getResources().getColor(
					R.color.radar_blue));
			tv_message.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_image.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_more.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			contact.setSelected(true);
			dials.setSelected(false);
			recent.setSelected(false);
			settings.setSelected(false);
			discover.setSelected(false);
			break;
		case 1:
			contact_img.setImageResource(R.drawable.contact);
			dials_img.setImageResource(R.drawable.keyboard_p);
			recent_img.setImageResource(R.drawable.recent);
			settings_img.setImageResource(R.drawable.setting);
			discover_img.setImageResource(R.drawable.toolbox);
			tv_contact.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_message.setTextColor(getResources().getColor(
					R.color.radar_blue));
			tv_image.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_more.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			contact.setSelected(false);
			dials.setSelected(true);
			recent.setSelected(false);
			settings.setSelected(false);
			discover.setSelected(false);
			break;
		case 2:
			contact_img.setImageResource(R.drawable.contact);
			dials_img.setImageResource(R.drawable.keyboard);
			recent_img.setImageResource(R.drawable.recent_p);
			settings_img.setImageResource(R.drawable.setting);
			discover_img.setImageResource(R.drawable.toolbox);
			contact.setSelected(false);
			dials.setSelected(false);
			recent.setSelected(true);
			settings.setSelected(false);
			discover.setSelected(false);
			break;
		case 3:
			contact_img.setImageResource(R.drawable.contact);
			dials_img.setImageResource(R.drawable.keyboard);
			recent_img.setImageResource(R.drawable.recent);
			settings_img.setImageResource(R.drawable.setting_p);
			discover_img.setImageResource(R.drawable.toolbox);
			tv_contact.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_message.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_image.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_more.setTextColor(getResources().getColor(
					R.color.radar_blue));
			contact.setSelected(false);
			dials.setSelected(false);
			recent.setSelected(false);
			settings.setSelected(true);
			discover.setSelected(false);
			break;
		case 4:
			contact_img.setImageResource(R.drawable.contact);
			dials_img.setImageResource(R.drawable.keyboard);
			recent_img.setImageResource(R.drawable.recent);
			settings_img.setImageResource(R.drawable.setting);
			discover_img.setImageResource(R.drawable.toolbox_p);
			tv_contact.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_message.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			tv_image.setTextColor(getResources().getColor(
					R.color.radar_blue));
			tv_more.setTextColor(getResources().getColor(
					R.color.tab_text_gray));
			contact.setSelected(false);
			dials.setSelected(false);
			recent.setSelected(false);
			settings.setSelected(false);
			discover.setSelected(true);
			break;
		}
	}

	public void replaceFragment(int container, Fragment fragment, String tag) {
		try {
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			// transaction.setCustomAnimations(android.R.anim.fade_in,
			// android.R.anim.fade_out);
			transaction.replace(R.id.fragContainer, fragment, tag);
			transaction.commit();
			manager.executePendingTransactions();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("my", "replaceFrag error--main");
		}
	}

	@Override
	public void onPause() {
		Log.e("life", "MainActivity->>onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.e("life", "MainActivity->>onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.e("life", "MainActivity->>onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.e("life", "MainActivity->>onDestroy");
		super.onDestroy();
		if (isRegFilter) {
			isRegFilter = false;
			this.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onBackPressed() {
		Log.e("my", "onBackPressed");
		if (null != keyboardFrag && currFrag == 1) {
			// if(keyboardFrag.IsInputDialogShowing()){
			// Intent close_input_dialog = new Intent();
			// close_input_dialog.setAction(Constants.Action.CLOSE_INPUT_DIALOG);
			// mContext.sendBroadcast(close_input_dialog);
			// return;
			// }
		}
		Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		mContext.startActivity(mHomeIntent);
	}

	class GetAccountInfoTask extends AsyncTask {

		public GetAccountInfoTask() {

		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Utils.sleepThread(1000);
			Account account = AccountPersist.getInstance()
					.getActiveAccountInfo(mContext);
			if(NpcCommon.mThreeNum==null||account==null){
				return null;
			}
			return NetManager.getInstance(mContext).getAccountInfo(
					NpcCommon.mThreeNum, account.sessionId);
		}

		@Override
		protected void onPostExecute(Object object) {
			if(object==null){
				return;
			}
			GetAccountInfoResult result = NetManager
					.createGetAccountInfoResult((JSONObject) object);
			if(Utils.isTostCmd(Integer.parseInt(result.error_code))){
				T.showLong(mContext, Utils.GetToastCMDString(Integer.parseInt(result.error_code)));
				return;
			}
			switch (Integer.parseInt(result.error_code)) {
			case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
				i.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(i);
				break;
			case NetManager.CONNECT_CHANGE:
				new GetAccountInfoTask().execute();
				return;
			case NetManager.GET_ACCOUNT_SUCCESS:
				try {
					String email = result.email;
					String phone = result.phone;
					Account account = AccountPersist.getInstance()
							.getActiveAccountInfo(mContext);
					if (null == account) {
						account = new Account();
					}
					account.email = email;
					account.phone = phone;
					AccountPersist.getInstance().setActiveAccount(mContext,
							account);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}

	}

	class ExitTask extends AsyncTask {
		Account account;

		public ExitTask(Account account) {
			this.account = account;
		}

		@Override
		protected Object doInBackground(Object... params) {
			int result=998;
			try {
				result= NetManager.getInstance(mContext).exit_application(
						account.three_number, account.sessionId);
			} catch (Exception e) {
				result=998;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			int result = (Integer) object;
			switch (result) {
			case NetManager.CONNECT_CHANGE:
				new ExitTask(account).execute();
				return;
			default:

				break;
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_MAINACTIVITY;
	}
    public void getMallMsg(){
    	String store_id=Config.AppConfig.store_id;
		if(store_id!=null&&!store_id.equals("")&&!store_id.equals("0")){
//			  new StoreIdTask(store_id).execute();
			  new MallUrlTask(store_id).execute();
		}
//        判断当前账号是否登录过
		boolean islogin=DataManager.fingIsLoginByActiveUser(mContext, NpcCommon.mThreeNum);
		if(islogin==false){
			if(store_id!=null&&!store_id.equals("")&&!store_id.equals("0")){
				new SystemTask(store_id,2).execute();
			    DataManager.insertIsLogin(mContext, NpcCommon.mThreeNum);
			}
		}
//		if(store_id!=null&&!store_id.equals("")){
//			new StartInfoTask(store_id).execute();
//		}
    }
	class StoreIdTask extends AsyncTask{
        public String sellerId;
        public StoreIdTask(String sellerId){
        	this.sellerId=sellerId;
        }
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			Account account;
			account=AccountPersist.getInstance().getActiveAccountInfo(mContext);
			sellerId=Config.AppConfig.store_id;
			return NetManager.getInstance(mContext).setStoreId(account.three_number,account.sessionId, sellerId);
		}
    	@Override
    	protected void onPostExecute(Object object) {
    		// TODO Auto-generated method stub
    		SetStoreIdResult result=NetManager.getInstance(mContext).setStoreIdResult((JSONObject) object);
    		String error_code=result.error_code;
    	    switch (Integer.parseInt(error_code)) {
    	    case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
				i.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(i);
				break;
			default:
				break;
			}
    	}
    }
    class MallUrlTask extends AsyncTask{
   	 String sellerId;
        public MallUrlTask(String sellerId){
       	 this.sellerId=sellerId;
        }
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			Account account=AccountPersist.getInstance().getActiveAccountInfo(mContext);
			return NetManager.getInstance(mContext).getMallUrl(account.three_number,account.sessionId,sellerId);
		}
   	@Override
   	protected void onPostExecute(Object jObject) {
   		// TODO Auto-generated method stub
   		MallUrlResult result=NetManager.getInstance(mContext).getMallUrlResult((JSONObject) jObject);
   		String error_code=result.error_code;
			Log.e("error_code", "error_code="+error_code+"MallUrl");
			switch (Integer.parseInt(error_code)) {
			case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
				i.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(i);
				break;
			case NetManager.CONNECT_CHANGE:
				new MallUrlTask(sellerId).execute();
				break;
			case NetManager.GET_START_LOGO_INFO:
				String mall_url=Base64.decode(result.store_link);
				Log.e("mall_url", "--"+mall_url+"--");
				if(mall_url!=null&&!mall_url.equals("")){
					SharedPreferencesManager.getInstance().putMallUrl(mContext, mall_url);
				}
				break;
			}
   	}
   }
    class SystemTask extends AsyncTask{
    	String sellerId;
    	int Option;
        public SystemTask(String sellerId,int Option){
        	this.sellerId=sellerId;
        	this.Option=Option;
        }
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			Account account;
			account=AccountPersist.getInstance().getActiveAccountInfo(mContext);
			Log.e("account","account.three_number="+account.three_number+"account.sessionId="+account.sessionId);
			return NetManager.getInstance(mContext).getSystemMessage(account.three_number, account.sessionId,sellerId,20,Option);
		}
		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			SystemMessageResult result=NetManager.getInstance(mContext).GetSystemMessageResult((JSONObject) object);
		    String error_code=result.error_code;
		    String RecordCount=result.RecordCount;
		    String Surplus=result.Surplus;
		    String RecommendFlag=result.RecommendFlag;
		    Log.e("system_mesg", "error_code="+error_code+"-------------");
		    Log.e("system_mesg", "RecordCount="+RecordCount+"-------------");
		    Log.e("system_mesg", "Surplus="+Surplus+"-------------");
		    Log.e("system_mesg", "RecommendFlag="+RecommendFlag+"-------------");
			switch (Integer.parseInt(error_code)) {
			case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
				i.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(i);
				break;
			case NetManager.CONNECT_CHANGE:
				new SystemTask(sellerId,Option).execute();
				break;
			case NetManager.GET_SYSTEM_MESSAGE_SUCCESS:
				 Log.e("new_message","first new message+++++++");
				 P2PHandler.getInstance().setSystemMessageIndex(Constants.SystemMessgeType.MALL_NEW,Integer.parseInt(RecommendFlag));
				 final List<SystemMsg> msgs=new ArrayList<SystemMsg>();
				 List<SystemMessage> lists = result.systemMessages;
				 String app_name=getResources().getString(R.string.app_name);
				 final String path=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME;
					File file=new File(path);
					if(!file.exists()){
						file.mkdirs();
					}
				 for(int j=lists.size()-1;j>=0;j--){
				     try {
				    	 SystemMsg msg=new SystemMsg();
				    	 msg.msgId=lists.get(j).msgId;
				    	 msg.title=Base64.decode(lists.get(j).title, "UTF-8");
				    	 msg.content=Base64.decode(lists.get(j).content,"UTF-8");
				    	 msg.time=Utils.ConvertTimeByLong(lists.get(j).time);
					     msg.pictrue_url=lists.get(j).picture_url;
					     msg.url=Base64.decode(lists.get(j).picture_in_url, "UTF-8");
					     msg.active_user=NpcCommon.mThreeNum;
					     msg.isRead=0;
					     Log.e("system_message", "msg.msgId="+ msg.msgId+" "+
					    		 "msg.title="+ msg.title+" "+
					    		 "msg.content="+ msg.content+" "+
					    		 "msg.time="+msg.time+" "+
					    		 "msg.pictrue="+ msg.pictrue+" "+
					    		 "msg.url="+  msg.url+" "+
					    		 "msg.active_user="+msg.active_user+" ");
					     msgs.add(msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				     
				 }
				 new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						for(final SystemMsg m:msgs){
							boolean contains=false;
							List<SystemMsg> list=DataManager.findSystemMessgeByActiveUser(mContext,NpcCommon.mThreeNum);
							for(SystemMsg s:list){
								 if(m.title.equals(s.title)&&m.content.equals(s.content)&&m.time.equals(s.time)){
									 contains=true;
									 break;
								 }
							 }
							if(contains==false){
								 DataManager.insertSystemMessage(mContext,m);
								 ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener(){
									 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
										 saveSystemMessagePictrue(loadedImage, path,m.msgId);
									 };
								 });	 	 
							 }
						}
						Intent it=new Intent();
						it.setAction(Constants.Action.REFRESH_SYSTEM_MESSAGE);
						sendBroadcast(it);
					}
				}).start();
				 if(Integer.parseInt(Surplus)>0){
					String messageId=msgs.get(0).msgId;
				    new GetOldSystemTaskByMsgID(sellerId,messageId,1);
				 }
				break;
			default:
				break;
			}
		}
    	
    }
    class GetOldSystemTaskByMsgID extends AsyncTask{
    	String sellerId;
    	String msgId;
    	int Option;
    	
        public GetOldSystemTaskByMsgID(String sellerId,String msgId,int Option){
        	this.sellerId=sellerId;
        	this.msgId=msgId;
        	this.Option=Option;
        }
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			Account account;
			account=AccountPersist.getInstance().getActiveAccountInfo(mContext);
			Log.e("account","account.three_number="+account.three_number+"account.sessionId="+account.sessionId);
			return NetManager.getInstance(mContext).getSystemMessageByMsgId(account.three_number, account.sessionId,sellerId,msgId,100,Option);
		}
		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			SystemMessageResult result=NetManager.getInstance(mContext).GetSystemMessageResult((JSONObject) object);
		    String error_code=result.error_code;
		    String RecordCount=result.RecordCount;
		    String Surplus=result.Surplus;
		    String RecommendFlag=result.RecommendFlag;
		    Log.e("system_mesg", "error_code="+error_code+"-------------");
		    Log.e("system_mesg", "RecordCount="+RecordCount+"-------------");
		    Log.e("system_mesg", "Surplus="+Surplus+"-------------");
		    Log.e("system_mesg", "RecommendFlag="+RecommendFlag+"-------------");
			switch (Integer.parseInt(error_code)) {
			case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
				i.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(i);
				break;
			case NetManager.CONNECT_CHANGE:
				new GetOldSystemTaskByMsgID(sellerId, msgId, Option);
				break;
			case NetManager.GET_SYSTEM_MESSAGE_SUCCESS:
				 final List<SystemMsg> msgs=new ArrayList<SystemMsg>();
				 List<SystemMessage> lists = result.systemMessages;
				 String app_name=getResources().getString(R.string.app_name);
				 final String path=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME;
					File file=new File(path);
					if(!file.exists()){
						file.mkdirs();
					}
				 for(int j=lists.size()-1;j>=0;j--){
				     try {
				    	 SystemMsg msg=new SystemMsg();
					     msg.msgId=lists.get(j).msgId;
				    	 msg.title=Base64.decode(lists.get(j).title,"UTF-8");
				    	 msg.content=Base64.decode(lists.get(j).content,"UTF-8");
				    	 msg.time=Utils.ConvertTimeByLong(lists.get(j).time);
					     msg.pictrue_url=lists.get(j).picture_url;
					     msg.url=Base64.decode(lists.get(j).picture_in_url,"UTF-8");
					     msg.active_user=NpcCommon.mThreeNum;
					     msg.isRead=0;
					     msgs.add(msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				     
				 }
				 new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							for(final SystemMsg m:msgs){
								boolean contains=false;
								List<SystemMsg> list=DataManager.findSystemMessgeByActiveUser(mContext,NpcCommon.mThreeNum);
								for(SystemMsg s:list){
									 if(m.title.equals(s.title)&&m.content.equals(s.content)&&m.time.equals(s.time)){
										 contains=true;
										 break;
									 }
								 }
								 if(contains==false){
									 DataManager.insertSystemMessage(mContext,m);
									 ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener(){
										 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
											 saveSystemMessagePictrue(loadedImage, path,m.msgId);
										 };
									 });	 	 
								 }
							}
							Intent it=new Intent();
							it.setAction(Constants.Action.REFRESH_SYSTEM_MESSAGE);
							sendBroadcast(it);
						}
					}).start();
				 if(Integer.parseInt(Surplus)>0){
					String messageId=msgs.get(0).msgId;
					new GetOldSystemTaskByMsgID(sellerId,messageId,1);
				 }
				break;
			default:
				break;
			}
		}
    	
    }
    class StartInfoTask extends AsyncTask{
        String sellerId;
        public StartInfoTask(String sellerId){
       	 this.sellerId=sellerId;
        }
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			Account account=AccountPersist.getInstance().getActiveAccountInfo(mContext);
			return NetManager.getInstance(mContext).getLogoStartInfo(account.three_number,account.sessionId,sellerId);
		}
		@Override
		protected void onPostExecute(Object jObject) {
			// TODO Auto-generated method stub
			GetStartInfoResult result=NetManager.getInstance(mContext).createGetStartInfoResult((JSONObject) jObject);
			String error_code=result.error_code;
			Log.e("error_code", "error_code="+error_code+"getstartinfo");
			switch (Integer.parseInt(error_code)) {
			case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
				i.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(i);
				break;
			case NetManager.CONNECT_CHANGE:
				new StartInfoTask(sellerId).execute();
				break;
			case NetManager.GET_START_LOGO_INFO:
				final String imageUrl=result.ImageSrc;
				String pictrue_Url=Base64.decode(result.pictrue_Link,"UTF-8");
				Log.e("pictrue_url",pictrue_Url);
//				Startlogo startlogo=new Startlogo();
//				try {
//					startlogo.sellerId=sellerId;
//					startlogo.pictrue=img(loadImageFromUrl(imageUrl));
//					startlogo.logo_url=pictrue_Url;
//					startlogo.active_user=NpcCommon.mThreeNum;
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if(DataManager.findStartLogo(mContext,sellerId)==null){
//					DataManager.insertStartLogo(mContext, startlogo);
//				}else{
//					DataManager.UpdateStartLogo(mContext, startlogo);
//				}
				SharedPreferencesManager.getInstance().putLogoUrl(mContext, pictrue_Url);
				String app_name=getResources().getString(R.string.app_name);
				String path=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME;
				File file=new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				final String path2=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME+"/startlogo";
				File file2=new File(path2);
				if(!file2.exists()){
					file2.mkdirs();
				}		
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
							ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener(){
								public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
									saveSystemMessagePictrue(loadedImage, path2,sellerId);
								};
							});
					
					}
				}).start();
//				try {
//					saveLogo(loadImageFromUrl(imageUrl),path);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Log.e("path", "path="+path);
				break;
			}
		}    	
   }
    class GetNewSystemTaskByMsgID extends AsyncTask{
    	String sellerId;
    	String msgId;
    	int Option;
    	
        public GetNewSystemTaskByMsgID(String sellerId,String msgId,int Option){
        	this.sellerId=sellerId;
        	this.msgId=msgId;
        	this.Option=Option;
        }
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			Account account;
			account=AccountPersist.getInstance().getActiveAccountInfo(mContext);
			return NetManager.getInstance(mContext).getSystemMessageByMsgId(account.three_number, account.sessionId,sellerId,msgId,100,Option);
		}
		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			SystemMessageResult result=NetManager.getInstance(mContext).GetSystemMessageResult((JSONObject) object);
		    String error_code=result.error_code;
		    String RecordCount=result.RecordCount;
		    String Surplus=result.Surplus;
		    String RecommendFlag=result.RecommendFlag;
		    Log.e("system_mesg", "error_code="+error_code+"-------------");
		    Log.e("system_mesg", "RecordCount="+RecordCount+"-------------");
		    Log.e("system_mesg", "Surplus="+Surplus+"-------------");
		    Log.e("system_mesg", "RecommendFlag="+RecommendFlag+"-------------");
			switch (Integer.parseInt(error_code)) {
			case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
				i.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(i);
				break;
			case NetManager.CONNECT_CHANGE:
				new GetNewSystemTaskByMsgID(sellerId, msgId, Option);
				break;
			case NetManager.GET_SYSTEM_MESSAGE_SUCCESS:
				Log.e("new_message","later new message+++++++");
				 P2PHandler.getInstance().setSystemMessageIndex(Constants.SystemMessgeType.MALL_NEW,Integer.parseInt(RecommendFlag));
				 final List<SystemMsg> msgs=new ArrayList<SystemMsg>();
				 List<SystemMessage> lists = result.systemMessages;
				 String app_name=getResources().getString(R.string.app_name);
				 final String path=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME;
					File file=new File(path);
					if(!file.exists()){
						file.mkdirs();
					}
				 for(int j=lists.size()-1;j>=0;j--){
				     try {
				    	 SystemMsg msg=new SystemMsg();
					     msg.msgId=lists.get(j).msgId;
				    	 msg.title=Base64.decode(lists.get(j).title,"UTF-8");
				    	 msg.content=Base64.decode(lists.get(j).content,"UTF-8");
				    	 msg.time=Utils.ConvertTimeByLong(lists.get(j).time);
					     msg.pictrue_url=lists.get(j).picture_url;
					     msg.url=Base64.decode(lists.get(j).picture_in_url,"UTF-8");
					     msg.active_user=NpcCommon.mThreeNum;
					     msg.isRead=0;
					     msgs.add(msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				     
				 }
				 new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							for(final SystemMsg m:msgs){
								List<SystemMsg> list=DataManager.findSystemMessgeByActiveUser(mContext,NpcCommon.mThreeNum);
						            boolean contains=false;
									 for(SystemMsg s:list){
										 if(m.title.equals(s.title)&&m.content.equals(s.content)&&m.time.equals(s.time)){
											 contains=true;
											 break;
										 }
									 }
									 if(contains==false){
										 DataManager.insertSystemMessage(mContext,m);
										 MyApp.app.showSystemNotification(mContext, m.title,m.content, count,m.url);
										 count++;
										 insertCount=insertCount+1;
										 Log.e("new_message", "insertCount="+insertCount);
										 ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener(){
											 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
												 saveSystemMessagePictrue(loadedImage, path,m.msgId);
											 };
										 });	 	 
									 }
							    
							}
						   if(msgs.size()>0){ 
//								if(currFrag!=1){
							       int total_count=SharedPreferencesManager.getInstance().getNoReadCount(mContext)+insertCount;
							       SharedPreferencesManager.getInstance().putNoReadCount(mContext, total_count);
							        insertCount=0;
									Intent it=new Intent();
									it.setAction(Constants.Action.SYSTEM_MESSAGE_COUNT);
									mContext.sendBroadcast(it);
//								}
							}
							Intent it=new Intent();
							it.setAction(Constants.Action.REFRESH_SYSTEM_MESSAGE);
							sendBroadcast(it);
						}
					}).start();
//				 for(final SystemMsg m:msgs){
//					 DataManager.insertSystemMessage(mContext,m);
//						saveSystemMessagePictrue(loadImageFromUrl(m.pictrue_url), path, m.msgId);
//						ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener(){
//						   public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//							   saveSystemMessagePictrue(loadedImage, path,m.msgId);
//						   };
//						});
//				 }
				 if(Integer.parseInt(Surplus)>0){
					 String messagId=msgs.get(0).msgId;
					 new GetNewSystemTaskByMsgID(sellerId, messagId,0);
				 }
				break;
			default:
				break;
			}
		}
    	
    }
    public void saveSystemMessagePictrue(Bitmap bitamap,String path,String fileName){
        Bitmap bitmap = bitamap;
        String picture_path =path+"/"+fileName+".jpg";  // 这个就是你存放的路径了。
        File bitmapFile = new File(picture_path);
        FileOutputStream fos = null;
        if (!bitmapFile.exists()) {
         try{
          bitmapFile.createNewFile();
          fos = new FileOutputStream(bitmapFile);
          bitmap.compress(Bitmap.CompressFormat.JPEG,100, fos);
         }catch (IOException e) {
          e.printStackTrace();
         }finally {
          try {
           if (fos != null) {
            fos.close();
           }
          } catch (IOException e) {
           e.printStackTrace();
          }
         }
        }else{
        	 try {
				fos = new FileOutputStream(bitmapFile);
				 bitmap.compress(Bitmap.CompressFormat.JPEG,100, fos);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
		           try {
		              if (fos != null) {
		               fos.close();
		              }
		             } catch (IOException e) {
		              e.printStackTrace();
		             }
		            }
        }
 }
    public void getHelpUrl(){
    	new Thread(){
    		@Override
    		public void run() {
    			// TODO Auto-generated method stub
    			try {
    				String[] url=UpdateManager.getInstance().getHelpUrl();
    				if(url[0]!=null&&!url[0].equals("")){
    					SharedPreferencesManager.getInstance().putHelpUrl(mContext, url[0]);
    					if(url[1]!=null&&!url[1].equals("")){
    						SharedPreferencesManager.getInstance().putHelpIndexUrl(mContext, Integer.parseInt(url[1]));
    					}
    				}
				} catch (Exception e) {
					// TODO: handle exception
				}
    		}
    	}.start();
    }
	
}
