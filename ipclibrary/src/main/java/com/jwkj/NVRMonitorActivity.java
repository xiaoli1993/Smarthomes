package com.jwkj;

import java.io.File;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout;

import com.juan.Interface.JiWeiFlingListener;
import com.juan.video.ClickTitaniumListener;
import com.juan.video.EseeVideoContainer;
import com.juan.video.OnVideoChange;
import com.juan.video.videoconnect;
import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.activity.BaseActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.JAContact;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.AlarmCloseVoice;
import com.jwkj.widget.MyInputPassDialog;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.AlarmCloseVoice.closeClickListener;
import com.jwkj.widget.MyInputPassDialog.OnCustomDialogListener;
import com.jwkj.widget.NormalDialog.OnAlarmClickListner;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.utils.MyUtils;
import com.nuowei.ipclibrary.R;

public class NVRMonitorActivity extends BaseActivity implements OnClickListener {
	private static final int JA_HD = 0;
	private static final int JA_SD = 1;
	private static final int JA_PTZ_UP = 0;
	private static final int JA_PTZ_DOWN = 1;
	private static final int JA_PTZ_LEFT = 2;
	private static final int JA_PTZ_RIGHT = 3;
	private final int MINX = 50;
	private final int MINY = 25;
	private Context mContext;
	private EseeVideoContainer esee;
	private boolean isHD = false;
	private String CaptureImagePath = AppConfig.Relese.SCREENSHORT + "/1.png";
	private boolean isInit = false;
	private int ScrrenOrientation;

	private ImageView close_voice, iv_full_screen, iv_voice, iv_speak,
			iv_defence, iv_half_screen, iv_screenshot, iv_back, iv_next,
			screenshot, iv_last, hungup;
	private RelativeLayout rlTitle, rlControl, control_bottom;
	private LinearLayout lControl;
	private Button choose_video_format;
	private LinearLayout control_top;
	private boolean isShowVideo = false;
	private TextView video_mode_hd, video_mode_sd;
	private boolean mIsCloseVoice = false;
	private boolean isSingleSurface = false;
	private GestureDetector mGesDetect;
	private TextView txPage;
	private Contact nvrContact;
	private JAContact jaContatct;
	private TextView txTitle;
	private boolean isRegFilter = false;
	private int curentPage=0;
	private long time;
    AlarmCloseVoice acl;
    int mainType,subType;
	static {
		videoconnect.getInstance().initEseeSDK();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_nvrmonitor);
		time=System.currentTimeMillis();
		mContext = this;
		P2PConnect.setPlaying(true);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		nvrContact = (Contact) getIntent().getSerializableExtra("contact");
		ScrrenOrientation = Configuration.ORIENTATION_PORTRAIT;
		mGesDetect = new GestureDetector(mContext, new MyOnGestureListener());
		CaptureImagePath = AppConfig.Relese.SCREENSHORT + File.separator
				+ Utils.getCapturePath(nvrContact.contactId);
		jaContatct = (JAContact) getIntent().getSerializableExtra("jacontact");
		P2PConnect.setMonitorId(nvrContact.contactId);// 设置在监控的ID
		SettingListener.setMonitorID(nvrContact.contactId);// 设置在监控的ID
		Utils.lastClickTime=System.currentTimeMillis();
		initUI();
		regFilter();
	}

	private void initUI() {
		esee = (EseeVideoContainer) findViewById(R.id.nvr_evc);
		if (jaContatct != null) {
			esee.setConnectDeiceInfo(jaContatct.getJaid(), nvrContact.userPassword,
					jaContatct.getUser(), 0, jaContatct.getChannls());
		} else {
			T.showLong(mContext, R.string.no_use);
		}
		// btnConnect=(Button) findViewById(R.id.btn_connect);
		iv_full_screen = (ImageView) findViewById(R.id.iv_full_screen);
		iv_screenshot = (ImageView) findViewById(R.id.iv_screenshot);
		rlTitle = (RelativeLayout) findViewById(R.id.rl_monitor_title);
		iv_back = (ImageView) findViewById(R.id.iv_monitor_back);
		lControl = (LinearLayout) findViewById(R.id.l_control);
		rlControl = (RelativeLayout) findViewById(R.id.rl_control);
		control_bottom = (RelativeLayout) findViewById(R.id.control_bottom);
		iv_half_screen = (ImageView) findViewById(R.id.iv_half_screen);
		choose_video_format = (Button) findViewById(R.id.choose_video_format);
		control_top = (LinearLayout) findViewById(R.id.control_top);
		video_mode_hd = (TextView) findViewById(R.id.video_mode_hd);
		video_mode_sd = (TextView) findViewById(R.id.video_mode_sd);
		iv_speak = (ImageView) findViewById(R.id.iv_speak);
		iv_voice = (ImageView) findViewById(R.id.iv_voice);
		close_voice = (ImageView) findViewById(R.id.close_voice);
		txPage = (TextView) findViewById(R.id.tx_page);
		iv_next = (ImageView) findViewById(R.id.iv_next);
		iv_last = (ImageView) findViewById(R.id.iv_last);
		hungup = (ImageView) findViewById(R.id.hungup);
		screenshot = (ImageView) findViewById(R.id.screenshot);
		txTitle = (TextView) findViewById(R.id.tv_name);
		txTitle.setText(nvrContact.contactName);
		iv_defence = (ImageView) findViewById(R.id.iv_defence);

		iv_full_screen.setOnClickListener(this);
		iv_screenshot.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_half_screen.setOnClickListener(this);
		choose_video_format.setOnClickListener(this);
		video_mode_hd.setOnClickListener(this);
		video_mode_sd.setOnClickListener(this);
		iv_voice.setOnClickListener(this);
		close_voice.setOnClickListener(this);
		iv_next.setOnClickListener(this);
		iv_last.setOnClickListener(this);
		hungup.setOnClickListener(this);
		screenshot.setOnClickListener(this);
		iv_defence.setOnClickListener(this);
		esee.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getPointerCount() == 1) {
					mGesDetect.onTouchEvent(event);
				}
				if (event.getAction() == MotionEvent.ACTION_POINTER_UP) {
					
				}
				return esee.onTouchEvent(event);
			}
		});
		esee.setClickTitaniumListener(new ClickTitaniumListener() {

			@Override
			public void clickListener() {
				changeControl();
			}
		});
		esee.setVideoChangeCallBack(new OnVideoChange() {

			@Override
			public void onVideoPage(int arg0, int arg1, int arg2) {
				setTextPage();
				if (!isInit) {
					isShake=false;
					isInit = true;
				}
			}

			@Override
			public void onPageMove(int arg0, int arg1, int arg2, int arg3) {
				setTextPage(arg2, arg1);
			}
		});
		esee.mJiWeiFlingListener = new JiWeiFlingListener() {

			@Override
			public void onJiWeiFlingListener(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				int id = -1;
				float distance = 0;
				boolean ishorizontal = false;
				if ((Math.abs(e2.getX() - e1.getX())) > (Math.abs(e2.getY()
						- e1.getY()))) {
					ishorizontal = true;
				}
				if (ishorizontal) {
					distance = e2.getX() - e1.getX();
					if (Math.abs(distance) > MyUtils.dip2px(
							NVRMonitorActivity.this, MINX)) {
						if (distance > 0) {
							id = JA_PTZ_RIGHT;
						} else {
							id = JA_PTZ_LEFT;
						}
					}
				} else {
					distance = e2.getY() - e1.getY();
					if (Math.abs(distance) > MyUtils.dip2px(
							NVRMonitorActivity.this, MINY)) {
						if (distance > 0) {
							id = JA_PTZ_UP;
						} else {
							id = JA_PTZ_DOWN;
						}
					}
				}

				if (id != -1) {
					P2PControl(esee, id);
				} else {

				}
			}

		};
		initVoiceUI();
	}
//  摇手机切换ipc
	SensorManager sensorManager;
	Sensor sensor;
	SensorEventListener sensorListener;
	boolean isShake=true;
	private long lastUpdateTime;
	private float lastX;
	private float lastY;
	private float lastZ;
	private static final int UPTATE_INTERVAL_TIME = 70;
	private static final int SPEED_SHRESHOLD = 2000;
	private void Sensor(){
		sensorManager=(SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
		if(sensorManager!=null){
			sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorListener=new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub
					if(!isShake){
						// 现在检测时间
						long currentUpdateTime = System.currentTimeMillis();
						// 两次检测的时间间隔
						long timeInterval = currentUpdateTime - lastUpdateTime;
						if (timeInterval < UPTATE_INTERVAL_TIME)
							return;
						// 现在的时间变成last时间
						lastUpdateTime = currentUpdateTime;
						
						// 获得x,y,z坐标
						float x = event.values[0];
						float y = event.values[1];
						float z = event.values[2];

						// 获得x,y,z的变化值
						float deltaX = x - lastX;
						float deltaY = y - lastY;
						float deltaZ = z - lastZ;

						// 将现在的坐标变成last坐标
						lastX = x;
						lastY = y;
						lastZ = z;
						double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
								* deltaZ)
								/ timeInterval * 10000;
						// 达到速度阀值，发出提示
						if (speed >= SPEED_SHRESHOLD) {
							isShake = true;
							Utils.PhoneVibrator(mContext);
							if(esee!=null){
								esee.pageTurnLeft();
							}
							isShake=false;
						}
					}
				}

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
					
				}
				
			};
		}
		sensorManager.registerListener(sensorListener , sensor,SensorManager.SENSOR_DELAY_GAME);
	}
	
	

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_CHECK_PASSWORD);
		// 正在监控时新设备弹框
		filter.addAction(Constants.Action.MONITOR_NEWDEVICEALARMING);
		filter.addAction(Constants.P2P.RET_KEEP_CLIENT);
		filter.addAction(Constants.P2P.DELETE_BINDALARM_ID);
		filter.addAction(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private String NewMessageDeviceID="";
	private int pushAlarmType;
	private boolean isCustomCmdAlarm=false;
	private String alarm_id="";
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					Constants.Action.MONITOR_NEWDEVICEALARMING)) {
				// 弹窗
				int MessageType = intent.getIntExtra("messagetype", 2);
				int type = intent.getIntExtra("alarm_type", 0);
				pushAlarmType=type;
				isCustomCmdAlarm=intent.getBooleanExtra("isCustomCmdAlarm", false);
				int group = intent.getIntExtra("group", -1);
				int item = intent.getIntExtra("item", -1);
				boolean isSupport = intent.getBooleanExtra("isSupport", false);
				boolean isSupportDelete=intent.getBooleanExtra("isSupportDelete", false);
				subType=intent.getIntExtra("subType", -1);
				mainType=intent.getIntExtra("mainType", -1);
				String customMsg=intent.getStringExtra("customMsg");
				if (MessageType == 1) {
					// 报警推送
					NewMessageDeviceID = intent.getStringExtra("alarm_id");
					if(alarm_id.equals(NewMessageDeviceID)&&passworddialog!=null&&passworddialog.isShowing()){
						return;
					}else{
						alarm_id=NewMessageDeviceID;
					}
				} else {
					// 透传推送
					NewMessageDeviceID = intent.getStringExtra("contactId");
					Log.i("dxsmoniter_alarm", "透传推送" + NewMessageDeviceID);
				}
				String alarmtype = Utils.getAlarmType(NewMessageDeviceID,type, isSupport, group,
						item,customMsg);
				StringBuffer NewMassage = new StringBuffer(
						Utils.getStringByResouceID(R.string.tab_device))
						.append("：").append(
								Utils.getDeviceName(NewMessageDeviceID));
				NewMassage.append("\n").append(Utils.getStringByResouceID(R.string.allarm_type)).
				append(alarmtype);
				int alarmstate=Constants.DeviceMuteState.NORMAL;
				if(type==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
					alarmstate=Constants.DeviceMuteState.CLOSE;
				}
				acl=new AlarmCloseVoice(mContext,NewMessageDeviceID);
				acl.setState(alarmstate);
				acl.setcloseClickListener(clistener);
				NewMessageDialog(NewMassage.toString(), NewMessageDeviceID,isSupportDelete,acl);
			}else if(intent.getAction().equals(Constants.P2P.RET_KEEP_CLIENT)){
            	int iSrcID=intent.getIntExtra("iSrcID", -1);
            	byte boption=intent.getByteExtra("boption", (byte) -1);
            	if(boption==Constants.FishBoption.MESG_SET_OK){
            		if(acl!=null){
            			acl.setState(2);
            			acl.startLoading();
            		}
            	}
            }else if (intent.getAction().equals(
					Constants.P2P.DELETE_BINDALARM_ID)) {
				int result = intent.getIntExtra("deleteResult", 1);
				int resultType = intent.getIntExtra("resultType", -1);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				if (result == 0) {
					if (resultType == 1) {
						// 删除成功
						T.showShort(mContext, R.string.modify_success);
					}
				} else if (result == -1) {
					// 不支持
					T.showShort(mContext, R.string.device_not_support);
				} else {
					// 失败
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT)){
            	String contactId=intent.getStringExtra("contactId");
            	if(NewMessageDeviceID.equals(contactId)&&pushAlarmType==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
            		if(dialog!=null){
            			dialog.dismiss();
            		}
            	}
            }

		}

	};
	
	/**
	 * 新报警信息
	 */
	private NormalDialog dialog;
	private String contactidTemp = "";
	private void NewMessageDialog(String Meassage, final String contacid,
			boolean isSurportdelete,AlarmCloseVoice  alarmCloseVoice) {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		dialog = new NormalDialog(mContext);
		dialog.setContentStr(Meassage);
		dialog.setbtnStr1(R.string.check);
		dialog.setbtnStr2(R.string.cancel);
		dialog.setbtnStr3(R.string.clear_bundealarmid);
		dialog.showAlarmDialog(isSurportdelete, contacid,alarmCloseVoice);
		dialog.setOnAlarmClickListner(AlarmClickListner);
		contactidTemp = contacid;
	}

	/**
	 * 监控对话框单击回调
	 */
	private OnAlarmClickListner AlarmClickListner = new OnAlarmClickListner() {

		@Override
		public void onOkClick(String alarmID, boolean isSurportDelete,
				Dialog dialog) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			// 查看新监控--挂断当前监控，再次呼叫另一个监控
			seeMonitor(alarmID);
		}

		@Override
		public void onDeleteClick(String alarmID, boolean isSurportDelete,
				Dialog dialog) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			DeleteDevice(alarmID);
		}

		@Override
		public void onCancelClick(String alarmID, boolean isSurportDelete,
				Dialog dialog) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	};
	//关闭本次报警声音
		public void closeAlarmVoice(String alarmID){
			final Contact contact = FList.getInstance().isContact(alarmID);
			if (null != contact) {
				NormalDialog dialog = new NormalDialog(mContext, mContext
						.getResources().getString(R.string.mute_the_alarm), mContext
						.getResources().getString(R.string.comfirm_mute_the_alarm),
						mContext.getResources().getString(R.string.confirm),
						mContext.getResources().getString(R.string.cancel));
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

					@Override
					public void onClick() {
					
					}
				});
				dialog.showNormalDialog();
			} else {
				closecreatePassDialog(alarmID);
			}
			
		}
		private Dialog closepassworddialog;
		void closecreatePassDialog(String id) {
			closepassworddialog = new MyInputPassDialog(mContext,Utils.getStringByResouceID(R.string.mute_the_alarm), id,getResources().getString(R.string.inputpassword), closelistener);
			closepassworddialog.show();
		}
		private OnCustomDialogListener closelistener = new OnCustomDialogListener() {

			@Override
			public void check(final String password, final String id) {
				if (password.trim().equals("")) {
					T.showShort(mContext, R.string.create_pwd);
					return;
				}

				if (password.length() > 30||password.charAt(0)=='0') {
					T.showShort(mContext,R.string.device_password_invalid);
					return;
				}
				if(acl!=null){
					 acl.setState(1);
					 acl.startLoading();
				 }
				String pwd=P2PHandler.getInstance().EntryPassword(password);
				FisheyeSetHandler.getInstance().sKeepClientCmd(id, pwd);
				closepassworddialog.dismiss();
			}
		};
	// 解绑确认弹框
	private void DeleteDevice(final String alarmId) {
		dialog = new NormalDialog(mContext, mContext.getResources().getString(
				R.string.clear_bundealarmid), mContext.getResources()
				.getString(R.string.clear_bundealarmid_tips), mContext
				.getResources().getString(R.string.ensure), mContext
				.getResources().getString(R.string.cancel));
		dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

			@Override
			public void onClick() {
				P2PHandler.getInstance().DeleteDeviceAlarmId(
						String.valueOf(alarmId));
				dialog.dismiss();
				ShowLoading();
			}
		});
		dialog.showDialog();
	}

	private void ShowLoading() {
		dialog = new NormalDialog(mContext);
		dialog.showLoadingDialog();
	}
	
	private void seeMonitor(String contactId) {
		final Contact contact = FList.getInstance().isContact(contactId);
		if (null != contact) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			callDevice(contact.contactId,contact.contactPassword);
		} else {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			Log.i("dxsmonitor", contactId);
			createPassDialog(contactId);
		}
	}
	
	private Dialog passworddialog;
	void createPassDialog(String id) {
		passworddialog = new MyInputPassDialog(mContext,Utils.getStringByResouceID(R.string.check), id, getResources().getString(R.string.inputpassword),listener);
		passworddialog.show();
	}
	private OnCustomDialogListener listener = new OnCustomDialogListener() {

		@Override
		public void check(final String password, final String id) {
			if (password.trim().equals("")) {
				T.showShort(mContext, R.string.input_monitor_pwd);
				return;
			}

			if (password.length() > 30||password.charAt(0)=='0') {
				T.showShort(mContext,R.string.device_password_invalid);
				return;
			}
			String pwd=P2PHandler.getInstance().EntryPassword(password);
			callDevice(id,pwd);
		}
	};
	private void callDevice(String id,String pwd){
		Contact contact=FList.getInstance().isContact(id);
		if(contact==null){
			contact=new Contact();
			contact.contactId=id;
			contact.activeUser=NpcCommon.mThreeNum;
			contact.contactName=id;
			contact.contactPassword=pwd;
			contact.contactType=mainType;
			contact.subType=subType;
		}
		Intent monitor = new Intent();
		monitor.setClass(mContext, ApMonitorActivity.class);
		monitor.putExtra("contact", contact);
		monitor.putExtra("connectType",Constants.ConnectType.P2PCONNECT);
		if(pushAlarmType==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
			monitor.putExtra("isSurpportOpenDoor", true);
		}
		monitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(monitor);
		finish();
	}

	/**
	 * 初始化声音部分控件，功能暂不支持
	 */
	private void initVoiceUI() {
		iv_speak.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					esee.PanVideo(100, 100);
					iv_speak.setBackgroundResource(R.drawable.portrait_speak_p);
					T.showShort(mContext, R.string.not_surrport);
					return true;
				case MotionEvent.ACTION_UP:
					iv_speak.setBackgroundResource(R.drawable.portrait_speak);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			ScrrenOrientation = Configuration.ORIENTATION_LANDSCAPE;
			rlTitle.setVisibility(View.GONE);
			lControl.setVisibility(View.GONE);
			rlControl.setVisibility(View.GONE);
			if (isSingleSurface) {
				control_bottom.setVisibility(View.VISIBLE);
			} else {
				control_bottom.setVisibility(View.GONE);
			}
		} else {
			ScrrenOrientation = Configuration.ORIENTATION_PORTRAIT;
			rlTitle.setVisibility(View.VISIBLE);
			lControl.setVisibility(View.VISIBLE);
			rlControl.setVisibility(View.VISIBLE);
			control_bottom.setVisibility(View.INVISIBLE);
			control_top.setVisibility(View.GONE);
		}
		super.onConfigurationChanged(newConfig);
		esee.calcCenter();
	}

	private void changeControl() {
		if (ScrrenOrientation == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		if (esee.getVideoState()) {
			updateVideoModeText(esee.getBitrate());
		}
		if(curentPage!=esee.getVideoIndex()){
			curentPage=esee.getVideoIndex();
			return;
		}

		if (control_bottom.getVisibility() == RelativeLayout.VISIBLE) {
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out);
			anim2.setDuration(100);
			control_bottom.startAnimation(anim2);
			anim2.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub
					hideVideoFormat();
					choose_video_format.setClickable(false);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					hideVideoFormat();
					control_bottom.setVisibility(RelativeLayout.INVISIBLE);
					choose_video_format
							.setBackgroundResource(R.drawable.sd_backgroud);
					choose_video_format.setClickable(true);
				}
			});

		} else {
			control_bottom.setVisibility(RelativeLayout.VISIBLE);
			control_bottom.bringToFront();
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in);
			anim2.setDuration(100);
			control_bottom.startAnimation(anim2);
			anim2.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub
					hideVideoFormat();
					choose_video_format.setClickable(false);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					hideVideoFormat();
					choose_video_format.setClickable(true);
				}
			});
		}
	}

	/**
	 * 设置分页文字
	 * 
	 * @param currentPage
	 * @param pages
	 */
	private void setTextPage() {
		int currentPage = esee.getVideoCurrPager();
		int pages = esee.getVideoPagerCount();
		setTextPage(currentPage, pages);
	}

	private void setTextPage(int currenPage, int pages) {
		String page = String.format("(%d/%d)", currenPage + 1, pages);
		if (txPage != null) {
			txPage.setText(page);
		}
	}

	public void hideVideoFormat() {
		if (control_top.getVisibility() == RelativeLayout.VISIBLE) {
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out);
			anim2.setDuration(100);
			control_top.startAnimation(anim2);
			control_top.setVisibility(RelativeLayout.GONE);
			isShowVideo = false;
		}
	}

	public void changevideoformat() {
		updateVideoModeText(esee.getBitrate());
		if (control_top.getVisibility() == RelativeLayout.VISIBLE) {
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out);
			anim2.setDuration(100);
			control_top.startAnimation(anim2);
			control_top.setVisibility(RelativeLayout.GONE);
			isShowVideo = false;
		} else {
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in);
			anim2.setDuration(100);
			control_top.setVisibility(RelativeLayout.VISIBLE);
			control_top.startAnimation(anim2);
			isShowVideo = true;
		}
	}

	public void updateVideoModeText(int mode) {
		if (mode == JA_HD) {
			video_mode_hd.setTextColor(mContext.getResources().getColor(
					R.color.text_color_blue));
			video_mode_sd.setTextColor(mContext.getResources().getColor(
					R.color.text_color_white));
			// choose_video_format.setText(R.string.video_mode_hd);
		} else if (mode == JA_SD) {
			video_mode_hd.setTextColor(mContext.getResources().getColor(
					R.color.text_color_white));
			video_mode_sd.setTextColor(mContext.getResources().getColor(
					R.color.text_color_blue));
			// choose_video_format.setText(R.string.video_mode_sd);
		} else if (mode == P2PValue.VideoMode.VIDEO_MODE_LD) {// 不使用
			video_mode_hd.setTextColor(mContext.getResources().getColor(
					R.color.text_color_white));
			video_mode_sd.setTextColor(mContext.getResources().getColor(
					R.color.text_color_white));
			// choose_video_format.setText(R.string.video_mode_ld);
		}
	}
   private boolean dispatchTouch=true;
	@Override
	protected void onResume() {
		super.onResume();
		dispatchTouch=false;
		handlers.sendEmptyMessageDelayed(1, 600);
		Sensor();
	}
	private Handler handlers = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			dispatchTouch=true;
		}
	};
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(!dispatchTouch){
			return false;
		}
		return super.dispatchTouchEvent(ev);
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		isShake=true;
		if (sensorListener != null) {
			sensorManager.unregisterListener(sensorListener);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		esee.setOnTouchListener(null);
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
		Reject();
	}

	@Override
	public int getActivityInfo() {
		return Constants.ActivityInfo.ACTIVITY_NVR_MONITOR;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 1) {
			// esee.resetError();
			// esee.ConnectVideo();
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	private void Reject() {
		esee.DisConnectVideo();
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.iv_full_screen) {
			ScrrenOrientation = Configuration.ORIENTATION_LANDSCAPE;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		} else if (i == R.id.screenshot || i == R.id.iv_screenshot) {
			if (esee.getVideoState()) {
				esee.CaptureImage(CaptureImagePath);
				T.showLong(mContext, R.string.capture_success);
			} else {
				T.showLong(mContext, R.string.video_disconnect);
			}

		} else if (i == R.id.iv_monitor_back) {
			finish();

		} else if (i == R.id.iv_half_screen) {
			control_bottom.setVisibility(View.INVISIBLE);
			ScrrenOrientation = Configuration.ORIENTATION_PORTRAIT;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		} else if (i == R.id.choose_video_format) {
			changevideoformat();

		} else if (i == R.id.video_mode_hd) {
			changeBitrate(JA_HD);
			hideVideoFormat();

		} else if (i == R.id.video_mode_sd) {
			changeBitrate(JA_SD);
			hideVideoFormat();

		} else if (i == R.id.close_voice || i == R.id.iv_voice) {
			if (mIsCloseVoice) {
				mIsCloseVoice = false;
				iv_voice.setImageResource(R.drawable.selector_half_screen_voice_open);
				close_voice.setBackgroundResource(R.drawable.m_voice_on);
				esee.playAudio();
			} else {
				mIsCloseVoice = true;
				iv_voice.setImageResource(R.drawable.selector_half_screen_voice_close);
				close_voice.setBackgroundResource(R.drawable.m_voice_off);
				esee.stopAudio();
			}

		} else if (i == R.id.iv_last) {
			esee.pageTurnRight();

		} else if (i == R.id.iv_next) {
			esee.pageTurnLeft();

		} else if (i == R.id.hungup) {
			finish();

		} else if (i == R.id.iv_defence) {
			T.showLong(mContext, R.string.not_surrport);

		} else {
		}

	}

	private void changeBitrate(int Bitrate) {
		if (esee != null && esee.getVideoState()) {
			esee.SwitchBitrate(Bitrate);
			updateVideoModeText(Bitrate);
		} else {
			// 视屏未连接
		}
	}

	class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
		public boolean onSingleTapUp(MotionEvent e) {
			
			return true;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			return true;
		}

		public boolean onDoubleTap(MotionEvent e) {
			isSingleSurface = !isSingleSurface;
			curentPage=esee.getVideoIndex();
//			changeControl();
			return true;
		}

		public void onLongPress(MotionEvent e) {
		}
	}

	/**
	 * 云台控制 up = 0, down = 1, letf = 2, right = 3, auto = 8 iris_open = 9
	 * iris_close = 10 zoom_in = 11, zoom_out = 12, focus_far = 13, focus_near =
	 * 14
	 * 
	 * @param id
	 */
	private void P2PControl(final EseeVideoContainer esees, int id) {
		esees.ptz(id);
		stopPTZ(esees, 1000);
	}

	private void stopPTZ(final EseeVideoContainer esees, long time) {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				esees.ptz(-1);
			}
		}, time);
	}
	closeClickListener clistener=new closeClickListener() {
		   
		   @Override
		   public void onclose(String deviceId,int type) {
			   // TODO Auto-generated method stub
			   acl.setState(1);
			   acl.startLoading();
			   closeAlarmVoice(deviceId,type);
		   }
	   };
	 //关闭本次报警声音
		public void closeAlarmVoice(String alarmID,int type){
			final Contact contact = FList.getInstance().isContact(alarmID);
			if (null != contact) {
					NormalDialog dialog = new NormalDialog(mContext, mContext
							.getResources().getString(R.string.mute_the_alarm), mContext
							.getResources().getString(R.string.comfirm_mute_the_alarm),
							mContext.getResources().getString(R.string.exit),
							mContext.getResources().getString(R.string.cancel));
					dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
						
						@Override
						public void onClick() {
							 if(acl!=null){
								 acl.setState(1);
								 acl.startLoading();
							  }
							FisheyeSetHandler.getInstance().sKeepClientCmd(contact.contactId, contact.contactPassword);
						}
					});
					dialog.showNormalDialog();
				} else {
					closecreatePassDialog(alarmID);
				}
			
			
		}
}
