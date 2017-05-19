package com.jwkj.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.P2PConnect;
import com.jwkj.data.AlarmRecord;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.DefenceArea;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.ImageUtils;
import com.jwkj.utils.MusicManger;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.MyInputPassDialog;
import com.jwkj.widget.MyInputPassDialog.OnCustomDialogListener;
import com.jwkj.widget.NormalDialog;
import com.lib.imagesee.ImageTools;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.nuowei.ipclibrary.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class AlarmWithPictrueActivity extends BaseActivity implements OnClickListener {
	TextView tv_deviceid,tv_alarm_type;
	ImageView iv_alarm_pictrue,iv_alarm_unbund,iv_alarm_check,alarming,img_close_voice;
	RelativeLayout iv_close;
	RelativeLayout r_alarm_pictrue;
	TextView tv_load_progress;
	TextView tv_name;
	int alarm_id, alarm_type, group, item;
	boolean isSupport;
	private boolean isSupportDelete = false;
	private boolean hasPictrue=false;
	private int imageCounts;
	private String ImagePath="";
	private String time="";
	int contactType;
	private String Image = "";
	private int currentImage = 0;
	String callId,Password;
	private String[] paths = null;
	private String[] LocalPaths = null;
	private boolean isRegFilter = false;
	private ImageLoader imageLoader;
	private Context mContext;
	NormalDialog dialog;
	private boolean isGetProgress = false;
	boolean isAlarm;
	String alarmTime="";
	String alarmPictruePath="";
	private int TIME_OUT = 20 * 1000;
	String sensorName="";
	int mainType,subType;
	int pictrue_h,close_voice_h;
	LinearLayout l_alarm_animation;
	TranslateAnimation transformation;
	RelativeLayout rl_anim_doorbell;
	ImageView alarm_bell, alarm_left_right;
	int p_w;
	int p_h;
	Bitmap bitmap;
	NormalDialog dialog_loading;
	String pwd;
	boolean isload=false;
	public Handler myhandler = new Handler();
	NormalDialog dialog_input;
	Timer timer = new Timer();
	int timeCount=0;
	int lastAlarmId;
	int animation=0;
	public static final int ALARM_ANIMATION=1;
	public static final int DOORBELL_ANIMATION=2;
	boolean isAnimation=false;
	String customMsg="";
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.activity_alarm_with_pictrue);
		mContext=this;
		initAlarmData(getIntent());
		regFilter();
		excuteTimeOutTimer();
		timer.schedule(task,3000, 3000);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		initAlarmData(intent);
	}
	public void initComponent(){
		getImagePath();
	    tv_deviceid=(TextView)findViewById(R.id.tv_deviceid);
	    iv_close=(RelativeLayout)findViewById(R.id.iv_close);
	    iv_alarm_pictrue=(ImageView)findViewById(R.id.iv_alarm_pictrue);
	    tv_alarm_type=(TextView)findViewById(R.id.tv_alarm_type);
	    iv_alarm_unbund=(ImageView)findViewById(R.id.iv_alarm_unbund);
	    iv_alarm_check=(ImageView)findViewById(R.id.iv_alarm_check);
	    r_alarm_pictrue=(RelativeLayout)findViewById(R.id.r_alarm_pictrue);
	    tv_load_progress=(TextView)findViewById(R.id.tv_load_progress);
	    alarming=(ImageView)findViewById(R.id.alarming);
	    tv_name=(TextView)findViewById(R.id.tv_name);
	    l_alarm_animation=(LinearLayout)findViewById(R.id.l_alarm_animation);
	    img_close_voice=(ImageView)findViewById(R.id.img_close_voice);
	    rl_anim_doorbell=(RelativeLayout)findViewById(R.id.rl_anim_doorbell);
	    alarm_bell = (ImageView) findViewById(R.id.iv_alarm_bell);
		alarm_left_right = (ImageView) findViewById(R.id.iv_doorbell_left_right);
	    iv_close.setOnClickListener(this);
	    iv_alarm_check.setOnClickListener(this);
	    iv_alarm_unbund.setOnClickListener(this);
	    
	    img_close_voice.setOnClickListener(this);
	    imageLoader = ImageTools.getImageLoaderInstance(this);
	    tv_deviceid.setText(String.valueOf(alarm_id));
	    tv_name.setText(getResources().getString(R.string.name)+sensorName);
	    if(isSupportDelete){
	    	iv_alarm_unbund.setVisibility(View.VISIBLE);
	    }else{
	    	iv_alarm_unbund.setVisibility(View.GONE);
	    }
		if(transformation!=null){
			transformation.cancel();
			if(animation==ALARM_ANIMATION){
				l_alarm_animation.clearAnimation();
			}else if(animation==DOORBELL_ANIMATION){
				rl_anim_doorbell.clearAnimation();
			}
		}
	    if(lastAlarmId!=alarm_id){
	    	if(alarm_type!=P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
	    		close_voice_h=(int) getResources().getDimension(R.dimen.img_alarm_close_voice_width);
	    		LayoutParams parames_close=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    		parames_close.width=4*MyApp.SCREENWIGHT/5;
	    		parames_close.height=close_voice_h;
	    		parames_close.gravity=Gravity.CENTER_HORIZONTAL;
	    		r_alarm_pictrue.setLayoutParams(parames_close);
	    		l_alarm_animation.setVisibility(View.VISIBLE);
	    		rl_anim_doorbell.setVisibility(View.GONE);
	    		img_close_voice.setVisibility(View.VISIBLE);
	    	}else{
	    		LayoutParams parames_close=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    		parames_close.width=4*MyApp.SCREENWIGHT/5;
	    		parames_close.height=0;
	    		parames_close.gravity=Gravity.CENTER_HORIZONTAL;
	    		r_alarm_pictrue.setLayoutParams(parames_close);
	    		l_alarm_animation.setVisibility(View.GONE);
	    		rl_anim_doorbell.setVisibility(View.VISIBLE);
	    		img_close_voice.setVisibility(View.GONE);
	    	}
	    	iv_alarm_pictrue.setVisibility(View.GONE);
	    }else{
	    	if(alarm_type!=P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
	    		l_alarm_animation.setVisibility(View.VISIBLE);
	    		rl_anim_doorbell.setVisibility(View.GONE);
	    		img_close_voice.setVisibility(View.VISIBLE);
	    	}else{
	    		l_alarm_animation.setVisibility(View.GONE);
	    		rl_anim_doorbell.setVisibility(View.VISIBLE);
	    		img_close_voice.setVisibility(View.GONE);
	    	}
	    }
		switch (alarm_type) {
		case P2PValue.AlarmType.EXTERNAL_ALARM:
			tv_alarm_type.setText(R.string.allarm_type1);
			if (isSupport) {                        
				tv_name.setVisibility(View.VISIBLE);
				String type;
				if (group<1) {
					type=getResources().getString(R.string.remote);
				}else if(group==8){
					type=getResources().getString(R.string.special_sensors);
				}else {
					type=getResources().getString(R.string.sensor);
				}
				DefenceArea dArea=new DefenceArea();
				dArea.setGroup(group);
				dArea.setItem(item);
				DefenceArea defenceArea=DataManager.findDefenceAreaByDeviceID(mContext, callId, dArea);
				if (defenceArea==null) {
					type=type+":"+mContext.getResources().getString(R.string.area)+((group-1)*8+(item+1));
				}else {
					type=type+":"+defenceArea.getName();
				}
				tv_name.setText(type);
			}
			break;
		case P2PValue.AlarmType.MOTION_DECT_ALARM:
			tv_alarm_type.setText(R.string.allarm_type2);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.EMERGENCY_ALARM:
			tv_alarm_type.setText(R.string.allarm_type3);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.LOW_VOL_ALARM:
			tv_alarm_type.setText(R.string.low_voltage_alarm);
			if (isSupport) {
				tv_name.setVisibility(View.VISIBLE);
				String type;
				if (group<1) {
					type=getResources().getString(R.string.remote);
				}else if(group==8){
					type=getResources().getString(R.string.special_sensors);
				}else {
					type=getResources().getString(R.string.sensor);
				}
				DefenceArea dArea=new DefenceArea();
				dArea.setGroup(group);
				dArea.setItem(item);
				DefenceArea defenceArea=DataManager.findDefenceAreaByDeviceID(mContext, callId, dArea);
				if (defenceArea==null) {
					type=type+":"+mContext.getResources().getString(R.string.area)+((group-1)*8+(item+1));
				}else {
					type=type+":"+defenceArea.getName();
				}
				tv_name.setText(type);
			}
			break;
		case P2PValue.AlarmType.PIR_ALARM:
		case P2PValue.AlarmType.ALARM_TYPE_PIR_ALARM:
			tv_alarm_type.setText(R.string.allarm_type4);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.EXT_LINE_ALARM:
			tv_alarm_type.setText(R.string.allarm_type5);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.DEFENCE:
			tv_alarm_type.setText(R.string.defence);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.NO_DEFENCE:
			tv_alarm_type.setText(R.string.no_defence);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.BATTERY_LOW_ALARM:
			tv_alarm_type.setText(R.string.battery_low_alarm);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH:
			tv_alarm_type.setText(R.string.door_bell);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.RECORD_FAILED_ALARM:
			tv_alarm_type.setText(R.string.record_failed);
			tv_name.setVisibility(View.GONE);
			break;
		case P2PValue.AlarmType.ALARM_TYPE_DOOR_MAGNET:
			tv_alarm_type.setText(R.string.door_alarm);
			tv_name.setVisibility(View.VISIBLE);
			break;
		case P2PValue.AlarmType.ALARM_TYPE_SMOKE_ALARM:
			tv_alarm_type.setText(R.string.alarm_type40);
			tv_name.setVisibility(View.VISIBLE);
			break;
		case P2PValue.AlarmType.ALARM_TYPE_GAS_ALARM:
			tv_alarm_type.setText(R.string.alarm_type41);
			tv_name.setVisibility(View.GONE);
		    break;
		case P2PValue.AlarmType.ALARM_TYPE_TEMPTATURE:
			tv_alarm_type.setText(R.string.alarm_type43);
			tv_name.setVisibility(View.GONE);
		    break;
		case P2PValue.AlarmType.ALARM_TYPE_HUMIDITY:
			tv_alarm_type.setText(R.string.alarm_type44);
			tv_name.setVisibility(View.GONE);
		    break;
		case 999:
			tv_alarm_type.setText(String.valueOf(alarm_type));
			tv_name.setVisibility(View.VISIBLE);
			tv_name.setText(customMsg);
			break;
		default:
			tv_alarm_type.setText(String.valueOf(alarm_type));
			tv_name.setVisibility(View.GONE);
			break;
		}
		showAlarmAnimation();
		if(hasPictrue==true&&imageCounts>0){
			startdown();
		}
	}
	TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
 
            runOnUiThread(new Runnable() {      // UI thread  
                @Override  
                public void run() { 
                     if(!isload&&timeCount<3){
                    	 if(hasPictrue==true&&imageCounts>0){
                 			startdown();
                 			timeCount++;
                 		 }
                     }
                }  
            });  
        }  
    };  
	private void initAlarmData(Intent intent) {
		    isAnimation=false;
			P2PHandler.getInstance().CancelGetAllarmImage();
		    lastAlarmId=alarm_id;
			alarm_id = intent.getIntExtra("alarm_id", 0);
			alarm_type = intent.getIntExtra("alarm_type", 0);
			isSupport = intent.getBooleanExtra("isSupport", false);
			group = intent.getIntExtra("group", 0);
			item = intent.getIntExtra("item", 0);
			isSupportDelete = intent.getBooleanExtra("isSupportDelete", false);
			imageCounts=intent.getIntExtra("imageCounts", 0);
			ImagePath=intent.getStringExtra("picture");
			time=intent.getStringExtra("time");
			hasPictrue=intent.getBooleanExtra("hasPictrue",false);
			alarmTime=intent.getStringExtra("alarmTime");
			sensorName=intent.getStringExtra("sensorName");
			mainType=intent.getIntExtra("mainType", 0);
			subType=intent.getIntExtra("subType",-1);
			customMsg=intent.getStringExtra("customMsg");
			paths = new String[imageCounts];
			LocalPaths = new String[imageCounts];
			pwd=null;
			creatFile();
			Contact c=FList.getInstance().isContact(String.valueOf(alarm_id));
			callId=String.valueOf(alarm_id);
			if(c!=null){
				contactType=c.contactType;
				Password=c.contactPassword;
			}else{
				P2PHandler.getInstance().getFriendStatus(new String[]{String.valueOf(alarm_id)});
			}
			initComponent();
			isload=false;
			if(closepassworddialog!=null){
				closepassworddialog.dismiss();
			}
			if(dialog_loading!=null){
				dialog_loading.dismiss();
			}
			if(dialog!=null){
				dialog.dismiss();
			}
			timeCount=0;

	}
	private void creatFile() {
		Image = Environment.getExternalStorageDirectory().getPath()
				+ "/allarmimage/" + String.valueOf(alarm_id) + "/";
		File destDir = new File(Image);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
	}

	public void startdown() {
		currentImage = 0;
		getImage();
		isGetProgress=true;
//		tv_load_progress.setVisibility(View.VISIBLE);
		new MyThread().start();
	}

	private void getImage() {
		if (currentImage < imageCounts) {
			alarmPictruePath=LocalPaths[currentImage];
			if(Password==null||Password.equals("")){
				return;
			}
			Log.e("leleTest", "paths[currentImage]="+paths[currentImage]+"--"+"LocalPaths[currentImage]="+LocalPaths[currentImage]);
			P2PHandler.getInstance().GetAllarmImage(String.valueOf(alarm_id), Password,
					paths[currentImage], LocalPaths[currentImage]);
		}
	}
	private void getImagePath() {
		for (int i = 0; i <imageCounts ; i++) {
			paths[i] = ImagePath + "0" + (i + 1) + ".jpg";
			LocalPaths[i] = Image + time + (i + 1) + ".jpg";
		}
	}
	public void regFilter() {
		isRegFilter = true;
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_GET_ALLARMIMAGE);
		filter.addAction(Constants.P2P.RET_GET_ALLARMIMAGE_PROGRESS);
		filter.addAction(Constants.P2P.DELETE_BINDALARM_ID);
		filter.addAction(Constants.P2P.RET_KEEP_CLIENT);
		filter.addAction(Constants.P2P.ACK_RET_KEEP_CLIENT);
		filter.addAction(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT);
		registerReceiver(br, filter);
	}

	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.P2P.RET_GET_ALLARMIMAGE)) {
				int errorcode = intent.getIntExtra("errorCode", 8);
				final String filename = intent.getStringExtra("filename");
				int deviceId=intent.getIntExtra("deviceId", 0);
				tv_load_progress.setVisibility(View.GONE);
				if (errorcode == 0) {
					isload=true;
					isGetProgress=false;
					if(deviceId==alarm_id){
						 File file = new File(filename);
						 FileInputStream fis;
						try {
							fis = new FileInputStream(file);
							try {
								int fileLen = fis.available();
								int size=fileLen/1024;
								if(size<5){
									startdown();
								}else{
									AlarmRecord alarmRecord=DataManager.findAlarmRecordByDeviceIdAndTime(mContext, NpcCommon.mThreeNum, String.valueOf(deviceId), alarmTime);
								    if(alarmRecord!=null){
								    	alarmRecord.alarmPictruePath=alarmPictruePath;
								    	DataManager.updateAlarmRecord(mContext, alarmRecord);
								    	Intent refresh_alarm_record=new Intent();
								    	refresh_alarm_record.setAction(Constants.Action.REFRESH_ALARM_MESSAGE);
								    	sendBroadcast(refresh_alarm_record);
								    }
									showAlarmPictrue(filename);
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else {
					if(hasPictrue==true&&imageCounts>0){
              			startdown();
              		}
					isGetProgress=false;
				}
//				switch (errorcode) {
//				case 1:
//					T.showLong(mContext, "密码错误");
//					break;
//				case 2:
//					T.showLong(mContext, "被冻结");
//					break;
//				case 3:
//					T.showLong(mContext, "没有需要的文件");
//					break;
//				case 4:
//					T.showLong(mContext, "不允许");
//					break;
//				case 5:
//					T.showLong(mContext, "读文件出错");
//					break;
//				case 6:
//					T.showLong(mContext, "忙线中");
//					break;
//				case 7:
//					T.showLong(mContext, "内存不足");
//					break;
//				case 8:
//					T.showLong(mContext, "连接超时");
//					break;
//				default:
//					break;
//				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_ALLARMIMAGE_PROGRESS)) {

			} else if (intent.getAction().equals(
					Constants.P2P.DELETE_BINDALARM_ID)) {
				int result = intent.getIntExtra("deleteResult", 1);
				int resultType=intent.getIntExtra("resultType", -1);
				String deviceId=intent.getStringExtra("deviceId");
				if (DeleteDialog != null && DeleteDialog.isShowing()) {
					DeleteDialog.dismiss();
				}
				if(deviceId.equals(String.valueOf(alarm_id))){
					// 删除成功
					if (result == 0) {
						//门铃主动挂断
						if(resultType==3){
							if(alarm_type==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
								finish();
							}
						}else if(resultType==1){
                        //删除成功
							T.showShort(mContext, R.string.modify_success);
							finish();
						}
					} else if (result == -1) {
						// 不支持
						T.showShort(mContext, R.string.device_not_support);
					} else {
						// 失败
					}
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_KEEP_CLIENT)){
            	int iSrcID=intent.getIntExtra("iSrcID", -1);
            	byte boption=intent.getByteExtra("boption", (byte) -1);
            	if(boption==Constants.FishBoption.MESG_SET_OK&&alarm_id==iSrcID){
            		if(dialog_loading!=null){
            			dialog_loading.dismiss();
            			img_close_voice.setVisibility(View.GONE);
            			T.showShort(mContext, R.string.close_alarm_voice_success);
            		}
            	}
            }else if(intent.getAction().equals(Constants.P2P.ACK_RET_KEEP_CLIENT)){
            	String deviceId=intent.getStringExtra("deviceId");
            	int state=intent.getIntExtra("state", -1);
            	if (state == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					T.showShort(mContext, R.string.password_error);
				} else if (state == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					if(deviceId.equals(String.valueOf(alarm_id))&&pwd!=null&&!pwd.equals("")){
						FisheyeSetHandler.getInstance().sKeepClientCmd(deviceId, pwd);
					}
					T.showShort(mContext, R.string.net_error);
				}
            }else if(intent.getAction().equals(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT)){
            	String contactId=intent.getStringExtra("contactId");
            	if(contactId.equals(String.valueOf(alarm_id))&&alarm_type==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
            		finish();
            	}
            }
		}
	};
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_ALRAM_WITH_PICTRUE;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.iv_close) {
			viewed = true;
			if (alarm_type == P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
				P2PHandler.getInstance().setReceiveDoorBell(String.valueOf(alarm_id));
			}
			long currentTime = System.currentTimeMillis();
			SharedPreferencesManager.getInstance().putAlarmTimeInterval(mContext, 10);
			SharedPreferencesManager.getInstance().putIgnoreAlarmTime(mContext, currentTime);
			int timeInterval = SharedPreferencesManager.getInstance()
					.getAlarmTimeInterval(mContext);
			T.showShort(
					mContext,
					mContext.getResources().getString(
							R.string.ignore_alarm_prompt_start)
							+ " "
							+ timeInterval
							+ " "
							+ mContext.getResources().getString(
							R.string.ignore_alarm_prompt_end));
			finish();

		} else if (i == R.id.iv_alarm_check) {
			viewed = true;
			Contact contact = FList.getInstance().isContact(callId);
			if (contact != null) {
//				contact.subType=subType;
				Intent monitor = new Intent(mContext, ApMonitorActivity.class);
				monitor.putExtra("contact", contact);
				monitor.putExtra("connectType", Constants.ConnectType.P2PCONNECT);
				if (alarm_type == P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
					monitor.putExtra("isSurpportOpenDoor", true);
				}
				startActivity(monitor);
				finish();
			} else {
				createPassDialog(String.valueOf(alarm_id));
			}

		} else if (i == R.id.iv_alarm_unbund) {
			viewed = true;
			DeleteDevice();

		} else if (i == R.id.img_close_voice) {
			closeAlarmVoice(String.valueOf(alarm_id));

		} else {
		}
	}
	//关闭本次报警声音
		public void closeAlarmVoice(String alarmID){
			final Contact contact = FList.getInstance().isContact(alarmID);
			if (null != contact) {
				        dialog = new NormalDialog(mContext, mContext
						.getResources().getString(R.string.mute_the_alarm), mContext
						.getResources().getString(R.string.comfirm_mute_the_alarm),
						mContext.getResources().getString(R.string.confirm),
						mContext.getResources().getString(R.string.cancel));
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

					@Override
					public void onClick() {
						  pwd=contact.contactPassword;
						  FisheyeSetHandler.getInstance().sKeepClientCmd(contact.contactId, contact.contactPassword);
						  dialog_loading=new NormalDialog(mContext);
						  dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
						  dialog_loading.showDialog();
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
				pwd=P2PHandler.getInstance().EntryPassword(password);
				FisheyeSetHandler.getInstance().sKeepClientCmd(id, pwd);
				dialog_loading=new NormalDialog(mContext);
				dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				dialog_loading.showDialog();
				closepassworddialog.dismiss();
			}
		};
	private Dialog passworddialog;
	void createPassDialog(String id) {
		passworddialog = new MyInputPassDialog(mContext,
				Utils.getStringByResouceID(R.string.check), id, getResources().getString(R.string.inputpassword),listener);
		passworddialog.show();
	}
	private OnCustomDialogListener listener = new OnCustomDialogListener() {

		@Override
		public void check(final String password, final String id) {
			if (password.trim().equals("")) {
				T.showShort(mContext, R.string.input_monitor_pwd);
				return;
			}
			if (password.length() > 30) {
				T.showShort(mContext,R.string.device_password_invalid);
				return;
			}

			P2PConnect.vReject("");
			new Thread() {
				@Override
				public void run() {
					while (true) {
						if (P2PConnect.getCurrent_state() == P2PConnect.P2P_STATE_NONE) {
							Message msg = new Message();
							String pwd=P2PHandler.getInstance().EntryPassword(password);
							String[] data = new String[] {id, pwd,
									String.valueOf(P2PValue.DeviceType.IPC) };
							msg.obj = data;
							handler.sendMessage(msg);
							break;
						}
						Utils.sleepThread(500);
					}
				}
			}.start();

		}
	};
	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			String[] data = (String[]) msg.obj;
			Contact contact=new Contact();
			contact.contactId=data[0];
			contact.contactName=data[0];
			contact.contactPassword=data[1];
			contact.subType=subType;
			contact.contactType=mainType;
			Intent monitor=new Intent(mContext,ApMonitorActivity.class);
			monitor.putExtra("contact", contact);
			monitor.putExtra("connectType", Constants.ConnectType.P2PCONNECT);
			if(alarm_type==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
				monitor.putExtra("isSurpportOpenDoor", true);
			}
			startActivity(monitor);
			finish();
			return false;
		}
	});
	private void DeleteDevice() {
		dialog = new NormalDialog(mContext, mContext.getResources().getString(
				R.string.clear_bundealarmid), mContext.getResources()
				.getString(R.string.clear_bundealarmid_tips), mContext
				.getResources().getString(R.string.ensure), mContext
				.getResources().getString(R.string.cancel));
		dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

			@Override
			public void onClick() {
				P2PHandler.getInstance().DeleteDeviceAlarmId(
						String.valueOf(alarm_id));
				dialog.dismiss();
				ShowLoading();
			}
		});
		dialog.showDialog();
	}
	private NormalDialog DeleteDialog;
	private void ShowLoading() {
		DeleteDialog = new NormalDialog(mContext);
		DeleteDialog.showLoadingDialog();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadMusicAndVibrate();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MusicManger.getInstance().stop();
		P2PHandler.getInstance().CancelGetAllarmImage();
	}
	public void loadMusicAndVibrate() {
		isAlarm = true;
		int a_muteState = SharedPreferencesManager.getInstance().getAMuteState(
				MyApp.app);
		if (a_muteState == 1) {
			MusicManger.getInstance().playAlarmMusic(alarm_type);
		}

		int a_vibrateState = SharedPreferencesManager.getInstance()
				.getAVibrateState(MyApp.app);
		if (a_vibrateState == 1) {
			new Thread() {
				public void run() {
					while (isAlarm) {
						MusicManger.getInstance().Vibrate();
						Utils.sleepThread(100);
					}
					MusicManger.getInstance().stopVibrate();

				}
			}.start();
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(task!=null){
			task.cancel();
		}
		if(isRegFilter==true){
			unregisterReceiver(br);
			isRegFilter=false;
		}
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		SharedPreferencesManager.getInstance().putIgnoreAlarmTime(mContext,
				System.currentTimeMillis());
		isAlarm = false;
		P2PConnect.vEndAllarm();
	}
	class MyThread extends Thread {
		int progress = 0;
		public void run() {
			isGetProgress = true;
			while (isGetProgress) {
				progress = P2PHandler.getInstance().GetAllarmImageProgress();
				myHandler.sendEmptyMessage(progress);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private Handler myHandler=new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int  progress=msg.what;
			tv_load_progress.setText(String.valueOf(progress)+"%");
			return false;
		}
	});
	// 超时计时器
	boolean viewed = false;
	Timer timeOutTimer;
	public void excuteTimeOutTimer() {

			timeOutTimer = new Timer();
			TimerTask mTask = new TimerTask() {
				@Override
				public void run() {
					// 弹出一个对话框
					if (!viewed) {
						Message message = new Message();
						message.what = USER_HASNOTVIEWED;
						mHandler.sendMessage(message);
					}
				}
			};
			timeOutTimer.schedule(mTask, TIME_OUT);

		}
	public static final int USER_HASNOTVIEWED = 3;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case USER_HASNOTVIEWED:
				long currentTime=System.currentTimeMillis();
				SharedPreferencesManager.getInstance().putIgnoreAlarmTime(mContext, currentTime);
				SharedPreferencesManager.getInstance().putAlarmTimeInterval(
						mContext, 1);
				finish();
				break;
			default:
				break;
			}
			return false;

		}
	});
	int start_y;
	int end_y;
	public void showAlarmPictrue(final String filename){
		int[] location = new int[2];
		final int self_height;
		final int location_y;
		if(alarm_type!=P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
			l_alarm_animation.getLocationOnScreen(location);  
			location_y = location[1]; 
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			l_alarm_animation.measure(w, h);
			self_height=l_alarm_animation.getMeasuredHeight();
		}else{
			rl_anim_doorbell.getLocationOnScreen(location);  
			location_y = location[1]; 
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			rl_anim_doorbell.measure(w, h);
			self_height=rl_anim_doorbell.getMeasuredHeight();
		}
		start_y=location_y-self_height/2;
//		 Bitmap bm = BitmapFactory.decodeFile(filename);
//	     Log.e("lelealarm","width="+bm.getWidth()+"height="+bm.getHeight());
		imageLoader.loadImage("file://"+filename,  new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				int w=arg2.getWidth();
				int h=arg2.getHeight();
				double p=(double)h/(double)w;
			    if(MyApp.SCREENWIGHT<MyApp.SCREENHIGHT){
					p_w=4*MyApp.SCREENWIGHT/5;
					p_h=(int) (p_w*p);
				}else{
					p_w=4/5*MyApp.SCREENHIGHT;
					p_h=(int) (p_w*p);
				}
			    pictrue_h=p_h;
			    bitmap=arg2;
			    
			    if(alarm_type!=P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
					end_y=location_y-self_height+pictrue_h-close_voice_h;
				}else{
					end_y=location_y-self_height+pictrue_h;
				}
		        showAlarmPictrue();
//		        transformation=new TranslateAnimation(0, 0, start_y, end_y);
//		        transformation.setDuration(500); 
//		        transformation.setAnimationListener(new AnimationListener() {
//					
//					@Override
//					public void onAnimationStart(Animation arg0) {
//						// TODO Auto-generated method stub
//					}
//					
//					@Override
//					public void onAnimationRepeat(Animation arg0) {
//						// TODO Auto-generated method stub
//						
//					}
//					@Override
//					public void onAnimationEnd(Animation arg0) {
//						// TODO Auto-generated method stub
//						if(isAnimation=true){
//							dHandler.sendEmptyMessageDelayed(0, 10);
//							Log.e("lelealarm","onAnimationEnd---------");
//						}
//					}
//				});
//		        transformation.setFillAfter(false);
//		        Log.e("lelealarm", "show pictrue--"+"lastAlarmId="+lastAlarmId+"--"+"alarm_id="+alarm_id+"visible="+iv_alarm_pictrue.getVisibility());
//		        if(iv_alarm_pictrue.getVisibility()==View.VISIBLE){
//		        	Log.e("lelealarm", "show pictrue--");
//		        	showAlarmPictrue();
//		        }else{
//		        	if(alarm_type!=P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
//		        		Log.e("lelealarm", "show pictrue--ALARM_ANIMATION");
//		        		animation=ALARM_ANIMATION;
//		        		l_alarm_animation.startAnimation(transformation);
//		        	}else{
//		        		Log.e("lelealarm", "show pictrue--DOORBELL_ANIMATION");
//		        		animation=DOORBELL_ANIMATION;
//		        		rl_anim_doorbell.startAnimation(transformation);
//		        	}
//		        	isAnimation=true;
//		        }
			
			}
			
		});
	}
	 Handler dHandler=new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				showAlarmPictrue();
				return false;
			}
		});
	public void showAlarmPictrue(){
		final LayoutParams parames=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        parames.gravity=Gravity.CENTER_HORIZONTAL;
        parames.width=p_w;
        parames.height=p_h;
		r_alarm_pictrue.setLayoutParams(parames);
		bitmap=ImageUtils.roundCorners(bitmap, ImageUtils.getScaleRounded(bitmap.getWidth()/2));
		iv_alarm_pictrue.setImageBitmap(bitmap);
		iv_alarm_pictrue.setVisibility(View.VISIBLE);
	}
	
	private void showDoorbellAlarm() {
		final AnimationDrawable anim = (AnimationDrawable) alarm_bell
				.getDrawable();
		final AnimationDrawable anim1 = (AnimationDrawable) alarm_left_right
				.getDrawable();
		OnPreDrawListener opdl = new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				anim.start();
				anim1.start();
				return true;
			}

		};
		alarm_bell.getViewTreeObserver().addOnPreDrawListener(opdl);
		alarm_left_right.getViewTreeObserver().addOnPreDrawListener(opdl);
	}
	public void showAlarmAnimation(){
		if(alarm_type==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
			showDoorbellAlarm();
		}else{
			AnimationDrawable animationDrawable=(AnimationDrawable) alarming.getBackground();
			animationDrawable.start(); 	
		}
	}

}
