package com.jwkj;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.juan.Interface.JiWeiFrameComeListener;
import com.juan.video.ClickTitaniumListener;
import com.juan.video.RealTimeListener;
import com.juan.video.RecordPlayControl;
import com.juan.video.RecordPlayControl.loginRecordListener;
import com.juan.video.RecordPlayControl.searchRecordListener;
import com.juan.video.RecordPlayer;
import com.juan.video.ThreadPoolService;
import com.juan.video.videoconnect;
import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.activity.BaseActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.JAContact;
import com.jwkj.entity.NVRRecodeTime;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.fragment.NVRPlayBackFrag;
import com.jwkj.fragment.NVRPlayBackFrag.NVRFragmentListner;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.AlarmCloseVoice;
import com.jwkj.widget.AlarmCloseVoice.closeClickListener;
import com.jwkj.widget.MyInputPassDialog;
import com.jwkj.widget.MyInputPassDialog.OnCustomDialogListener;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnAlarmClickListner;
import com.jwkj.widget.ProgressTextView;
import com.jwkj.widget.control.MonitorRangeSeekBar;
import com.jwkj.widget.control.MonitorRangeSeekBar.OnMonitorRangeSeekBarChangeListener;
import com.jwkj.widget.control.MonitorTitleViewGroup;
import com.jwkj.widget.control.MonitorTitleViewGroup.onTitleClickListner;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.nuowei.smarthome.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class NVRPlayBackActivity extends BaseActivity implements
		OnClickListener, OnMonitorRangeSeekBarChangeListener {
	private Context mContext;
	private String CaptureImagePath = AppConfig.Relese.SCREENSHORT + "/1.png";
	private RecordPlayer esee;
	private ImageView btnPause, btnPrevies, btnNext, close_voice, screen_shot;
	private RecordPlayControl PlayControl;
	private NVRPlayBackFrag nvrSearchFrag;
	private List<NVRRecodeTime> recoderList = new ArrayList<NVRRecodeTime>();
	private FrameLayout fraSearch;
	long starttime = 0;
	private boolean isPlaying = false;
	private boolean isAudioPlaying = true;
	private int curentPosition;
	private long realTime = 0;
	private MonitorRangeSeekBar seekbar;
	private NVRRecodeTime Curenttime;
	private LinearLayout rlControlBottom;
	private boolean isControlShow = true;
	private volatile boolean isConnectSuccess = false;
	private long startTime, endTime;
	private Contact nvrContact;
	private JAContact jaContatct;
	private volatile boolean canCapture = false;
	private int chinnal = 0;
	private boolean isFirstPlay = true;
	private boolean isLoading = true;
	private MonitorTitleViewGroup PanBack;
	boolean isRegFilter=false;
	//报警推送
	private String NewMessageDeviceID="";
	private int pushAlarmType;
	private boolean isCustomCmdAlarm=false;
	private String alarm_id="";
	AlarmCloseVoice acl;
	int mainType,subType;
	static {
		videoconnect.getInstance().initEseeSDK();
	}

	// 单击查询时间:1458777600时间：2016-03-24 08:00:00结束时间:1458863999-----去掉时区
	// 单击查询时间:1458748800时间：2016-03-24 00:00:00结束时间:1458835199-----加上时区
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_nvrplay);
		mContext = this;
		nvrContact = (Contact) getIntent().getSerializableExtra("contact");
		jaContatct = (JAContact) getIntent().getSerializableExtra("jacontact");
		nvrSearchFrag = getSearchFragment();
		initSearchTime();
		initUI();
		CaptureImagePath = AppConfig.Relese.SCREENSHORT + File.separator
				+ Utils.getCapturePath(nvrContact.contactId);
		setDefaultFragment();
		regFilter();
	}
    public void regFilter(){
    	IntentFilter filter=new IntentFilter();
    	// 正在监控时新设备弹框
		filter.addAction(Constants.Action.MONITOR_NEWDEVICEALARMING);
		filter.addAction(Constants.P2P.RET_KEEP_CLIENT);
		filter.addAction(Constants.P2P.DELETE_BINDALARM_ID);
		filter.addAction(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT);
		registerReceiver(br, filter);
		isRegFilter = true;
    }
    BroadcastReceiver br=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			   if(intent.getAction().equals(Constants.Action.MONITOR_NEWDEVICEALARMING)){
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
					type = 13;
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
	private void setDefaultFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.fl_searh, nvrSearchFrag);
		transaction.commit();
	}

	private NVRPlayBackFrag getSearchFragment() {
		if (nvrSearchFrag == null) {
			nvrSearchFrag = new NVRPlayBackFrag();
			Bundle bund = new Bundle();
			bund.putSerializable("contact", nvrContact);
			bund.putSerializable("jacontatct", jaContatct);
			nvrSearchFrag.setArguments(bund);
		}
		nvrSearchFrag.setOnNVRFragmentListner(listner);
		return nvrSearchFrag;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				// 更新列表
				getSearchFragment().setRecoderList(recoderList);
				nvrSearchFrag.setSearhText(ProgressTextView.STATE_TEXT,
						initSearchText(startTime, endTime, chinnal));
			} else if (msg.what == 1) {
				T.showLong(mContext, R.string.no_match_file);
				nvrSearchFrag.setSearhText(ProgressTextView.STATE_ERROR,
						R.string.no_match_file);
			} else if (msg.what == 2) {
				long time = msg.getData().getLong("time");
				showProgress(time);
			} else if (msg.what == 3) {// 播放器自动播放了下一个
				playNext();
			} else if (msg.what == 4) {// 验证成功
				isConnectSuccess = true;
				esee.SearchRecFile(startTime, endTime, chinnal);
			} else if (msg.what == 6) {// 验证失败
				isConnectSuccess = false;
				T.showLong(mContext, R.string.conn_fail);
				nvrSearchFrag.setSearhText(ProgressTextView.STATE_ERROR,
						R.string.conn_fail);
			}

		};
	};

	private void initUI() {
		esee = (RecordPlayer) findViewById(R.id.nvr_rp);
		btnPause = (ImageView) findViewById(R.id.pause);
		fraSearch = (FrameLayout) findViewById(R.id.fl_searh);
		btnPrevies = (ImageView) findViewById(R.id.previous);
		btnNext = (ImageView) findViewById(R.id.next);
		seekbar = (MonitorRangeSeekBar) findViewById(R.id.seek_bar);
		close_voice = (ImageView) findViewById(R.id.close_voice);
		rlControlBottom = (LinearLayout) findViewById(R.id.control_bottom);
		screen_shot = (ImageView) findViewById(R.id.screen_shot);
		PanBack=new MonitorTitleViewGroup(mContext);
		PanBack.setOnTitleClickListner(TitleClickListner);
		ViewGroup view=(ViewGroup) esee.getParent();
		view.addView(PanBack);
		PanBack.setVisibility(View.GONE);
		
		btnPause.setOnClickListener(this);
		btnPrevies.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		seekbar.setOnRangeSeekBarChangeListener(this);
		close_voice.setOnClickListener(this);
		screen_shot.setOnClickListener(this);

		esee.setOnSearchRecirdListener(new searchRecordListener() {

			@Override
			public void onSearchRecordListener(boolean isSearchSuccess,
					long startRecordTime, long endRecordTime,
					int SearchP2pHandler, List<Map<String, String>> list) {
				Looper.prepare();
				if (isSearchSuccess) {
					printList(list);
					handler.sendEmptyMessage(0);
				} else {
					handler.sendEmptyMessage(1);
				}
				Looper.loop();
			}
		});

		esee.setClickTitaniumListener(new ClickTitaniumListener() {

			@Override
			public void clickListener() {
				if (isControlShow) {
					if(fraSearch.getVisibility()==View.GONE){
						Animation anim2 = AnimationUtils.loadAnimation(mContext,
								R.anim.slide_out_top);
						Animation anim3 = AnimationUtils.loadAnimation(mContext,
								R.anim.slide_out_bottom);
						anim2.setDuration(300);
						anim3.setDuration(300);
						PanBack.startAnimation(anim3);
						rlControlBottom.startAnimation(anim2);
						PanBack.setVisibility(View.GONE);
						rlControlBottom.setVisibility(View.GONE);
						isControlShow = !isControlShow;
					}
				} else {
					if(fraSearch.getVisibility()==View.GONE){
						PanBack.setVisibility(View.VISIBLE);
						rlControlBottom.setVisibility(View.VISIBLE);
						Animation anim2 = AnimationUtils.loadAnimation(mContext,
								R.anim.slide_in_bottom);
						Animation anim3 = AnimationUtils.loadAnimation(mContext,
								R.anim.slide_in_top);
						anim2.setDuration(300);
						anim3.setDuration(300);
						PanBack.startAnimation(anim3);
						rlControlBottom.startAnimation(anim2);
						isControlShow = !isControlShow;
					}
				}
			}
		});
		esee.setOnLoginListener(new loginRecordListener() {

			@Override
			public void onLogin(int p2pHandler, int errorCode) {
				Looper.prepare();
				if (errorCode == 0) {
					handler.sendEmptyMessage(4);
				} else {
					handler.sendEmptyMessage(6);

				}
				Looper.loop();
			}
		});

		esee.setPlayBackFrameComeListener(new JiWeiFrameComeListener() {

			@Override
			public void onFrameComeListener() {
				esee.play();
				esee.setIsLoading(false);
			}
		});

		esee.setRealTimeListener(new RealTimeListener() {

			@Override
			public void realTime(long paramLong) {
				canCapture = true;
				if (paramLong != realTime) {
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putLong("time", paramLong);
					msg.what = 2;
					msg.setData(data);
					handler.sendMessage(msg);
				} else {

				}
				realTime = paramLong;
			}
		});

		// esee.setPlayBackFrameComeListener(new JiWeiFrameComeListener() {
		//
		// @Override
		// public void onFrameComeListener() {
		// }
		// });

		if (jaContatct != null) {
			esee.setConnectDeviceInfo(jaContatct.getJaid(),
					jaContatct.getUser(), jaContatct.getPwd(), chinnal, 0);
//			esee.setConnectDeviceInfo("632865069",
//					"admin", "", chinnal, 0);
			connectVideo();
			esee.playAudio();
		} else {
			T.showLong(mContext, R.string.no_use);
		}
	}

	private void initSearchTime() {
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		Calendar Zro = new GregorianCalendar(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0,
				0, 0);
		startTime = Zro.getTimeInMillis() / 1000;
		endTime = calendar.getTimeInMillis() / 1000;
	}

	/**
	 * 连接视屏
	 */
	private void connectVideo() {
		esee.initMediaBuffer();
		esee.Connect();
	}

	void printList(List<Map<String, String>> List) {
		recoderList.clear();
		int i = 0;
		long endtime = 0;
		long startTime = 0;
		for (Map<String, String> map : List) {
			Set<String> keys = map.keySet();
			for (String string : keys) {
				if (i % 2 == 0) {
					endtime = Long.valueOf(map.get(string));
				} else {
					startTime = Long.valueOf(map.get(string));
					NVRRecodeTime time = new NVRRecodeTime(startTime, endtime);
					recoderList.add(time);
				}
				i++;
			}
		}
	}

	private void showProgress(long time) {
		long PlayedTime = time - Curenttime.getStartTime();
		seekbar.setSelectedMaxValue((int)PlayedTime);
	}

	private NVRFragmentListner listner = new NVRFragmentListner() {

		@Override
		public void onSearchClick(long start, long end, int channl) {
			startTime = start;
			endTime = end;
			chinnal = channl;
			if (!isConnectSuccess) {
				connectVideo(); 
			} else {
				esee.SearchRecFile(startTime, endTime, chinnal);
			}
			nvrSearchFrag.setSearhText(ProgressTextView.STATE_PROGRESS,
					initSearchText(startTime, endTime, chinnal));
		}

		@Override
		public void onBackClick() {
			finish();
		}

		@Override
		public void onPlayBack(NVRRecodeTime time, int position) {
			if (esee != null) {
				isPlaying = true;
				curentPosition = position;
				isControlShow=false;
				PanBack.setVisibility(View.GONE);
				rlControlBottom.setVisibility(View.GONE);
				if (isFirstPlay) {// 修改为都采用play-stop-play
					playItem(position);
					isFirstPlay = false;
				} else {
					playItem(position);
				}
				fraSearch.setVisibility(View.GONE);
				P2PConnect.setPlaying(true);
				setlande();
			}
		}
	};

	// 首次播放
	private void playItem(int position) {
		esee.setIsLoading(true);
		curentPosition = position;
		Curenttime = recoderList.get(position);
//		playTimeNoThread();
		playTimeByThread();
		seekbar.setRangeValues(0,(int) Curenttime.getMillis());
		seekbar.setSelectedMaxValue(0);
	}

	// 跳转(play弃用)
	private void seekItem(int position) {
		curentPosition = position;
		Curenttime = recoderList.get(position);
		seekTime();
		seekbar.setRangeValues(0,(int) Curenttime.getMillis());
	}

	private void playTimeNoThread() {
		esee.startPlay(Curenttime.getStartTime(), Curenttime.getEndTime());
	}
	
	private void playTimeByThread(){
		ThreadPoolService.instance().sumbit(new Runnable(){

			@Override
			public void run() {
				esee.startPlay(Curenttime.getStartTime(), Curenttime.getEndTime());
			}});
	}

	// 跳转时间
	private void seekTime() {
		esee.seek(Curenttime.getStartTime(), Curenttime.getEndTime());
	}

	private boolean playVideo(int position, int type) {

		if (type == 1) {
			position--;
		} else if (type == 2) {
			position++;
		}
		if (position < 0) {
			T.showLong(mContext, R.string.no_previous_file);
			return false;
		} else if (position > recoderList.size() - 1) {
			T.showLong(mContext, R.string.no_next_file);
			return false;
		} else {
			setIsLoading(true);
			playItem(position);
			return true;
		}
	}

	/**
	 * 播放器自动播放到下一个时间段视频，刷新UI
	 */
	private void playNext() {
		curentPosition++;
		if (curentPosition <= recoderList.size() - 1) {
			Curenttime = recoderList.get(curentPosition);
			seekbar.setRangeValues(0,(int) Curenttime.getMillis());
		}
	}

	/**
	 * 设置横屏
	 */
	private void setlande() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * 设置竖屏
	 */
	private void setPrivous() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("lifestyle", "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("lifestyle", "onPause");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("lifestyle", "onDestroy");
		esee.stopAudio();
		esee.DisConnect();
		handler.removeCallbacksAndMessages(null);
		if(isRegFilter){
			mContext.unregisterReceiver(br);
			isRegFilter=false;
		}
	}

	private String paserTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date((time * 1000L)));
	}

	private static String HalfPaser(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date((time * 1000L)));
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_NVR_PLAYBACK;
	}

	void changePlayState() {
		if (isPlaying) {
			esee.resume();
			esee.playAudio();
			btnPause.setImageResource(R.drawable.playing_pause);
		} else {
			// 暂停
			esee.pause();
			esee.stopAudio();
			btnPause.setImageResource(R.drawable.playing_start);

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pause:
			isPlaying = !isPlaying;
			changePlayState();
			break;
		case R.id.previous:
			stopPlay();
			playVideo(curentPosition, 1);
			break;
		case R.id.next:
			stopPlay();
			playVideo(curentPosition, 2);
			break;
		case R.id.close_voice:
			if (isAudioPlaying) {
				close_voice.setImageResource(R.drawable.btn_playback_voice_s);
				esee.stopAudio();
			} else {
				close_voice.setImageResource(R.drawable.btn_playback_voice);
				esee.playAudio();
			}
			isAudioPlaying = !isAudioPlaying;
			break;
		case R.id.screen_shot:
			if (canCapture) {
				esee.captureImg(CaptureImagePath);
				T.showLong(mContext, R.string.capture_success);
			} else {
				T.showLong(mContext, R.string.video_disconnect);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 返回到收索页面
	 */
	private void backToSearch() {
		if (fraSearch != null) {
			esee.stopAudio();
			setIsLoading(true);
			esee.stop();
			setPrivous();
			fraSearch.setVisibility(View.VISIBLE);
			P2PConnect.setPlaying(false);
		}
		if(PanBack!=null){
			PanBack.setVisibility(View.GONE);
		}
	}

	private void stopPlay() {
		esee.stop();
		esee.setIsLoading(true);
	}

	/**
	 * 根据收索时间及通道显示文本
	 * 
	 * @param start
	 * @param end
	 * @param channl
	 * @return
	 */
	public static String initSearchText(long start, long end, int channl) {
		StringBuilder sb = new StringBuilder();
		sb.append(HalfPaser(start));
		sb.append("  ");
		sb.append(channl + 1);
		sb.append("-CH");
		return sb.toString();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (fraSearch.getVisibility() != View.VISIBLE) {
				backToSearch();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
		esee.setIsLoading(isLoading);
	}

	@Override
	public void onRangeSeekBarValuesChanged(MonitorRangeSeekBar bar,
			Object minValue, Object maxValue) {
		if (bar.getAbsoluteMaxValue() == (Integer)maxValue) {
			playVideo(curentPosition, 2);
		}
	}

	@Override
	public void onRangeSeekBarValuesSelected(MonitorRangeSeekBar bar,
			Object minValue, Object maxValue, int type) {
		stopPlay();
		esee.startPlay(Curenttime.getStartTime() + (Integer)maxValue,
				Curenttime.getEndTime());
		
	}
	
    private onTitleClickListner TitleClickListner=new onTitleClickListner() {
		
		@Override
		public void onBackClick(View view) {
			backToSearch();
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
				final Contact contact=new Contact();
				contact.contactId=id;
				contact.activeUser=NpcCommon.mThreeNum;
				contact.contactName=id;
				contact.contactPassword=pwd;
				contact.contactType=mainType;
				contact.subType=subType;
				if (fraSearch.getVisibility() != View.VISIBLE) {
					backToSearch();
				}
				callDevice(contact);
			}
		};
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
	private void seeMonitor(String contactId) {
		final Contact contact = FList.getInstance().isContact(contactId);
		if (null != contact) {
			if (fraSearch.getVisibility() != View.VISIBLE) {
				backToSearch();
			}
			callDevice(contact);
		} else {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			createPassDialog(contactId);
		}
	}
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
		private void callDevice(Contact contact){
				Intent monitor = new Intent();
				monitor.setClass(mContext, ApMonitorActivity.class);
				monitor.putExtra("contact", contact);
				monitor.putExtra("connectType",Constants.ConnectType.P2PCONNECT);
			    if(pushAlarmType==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
				    monitor.putExtra("isSurpportOpenDoor", true);
			    }
				monitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(monitor);
		}
}
