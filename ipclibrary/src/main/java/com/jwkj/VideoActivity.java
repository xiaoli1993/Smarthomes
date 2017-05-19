package com.jwkj;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.activity.MainActivity;
import com.jwkj.data.Contact;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.PhoneWatcher;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.AlarmCloseVoice;
import com.jwkj.widget.AlarmCloseVoice.closeClickListener;
import com.jwkj.widget.MyInputPassDialog;
import com.jwkj.widget.MyInputPassDialog.OnCustomDialogListener;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnAlarmClickListner;
import com.p2p.core.BaseVideoActivity;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PView;
import com.nuowei.ipclibrary.R;

import java.util.List;

public class VideoActivity extends BaseVideoActivity implements OnClickListener {
	private final int DEFAULT_FRAME_RATE = 15;
	public static final int P2P_SURFACE_START_PLAYING_WIDTH = 320;
	public static final int P2P_SURFACE_START_PLAYING_HEIGHT = 240;
	Context mContext;
	boolean isRegFilter = false;
	boolean mIsCloseMike = false;
	boolean isYV12 = false;
	int type;
	ImageView switch_camera, hungup, close_mike;
	PhoneWatcher mPhoneWatcher;

	Camera mCamera;
	SurfaceHolder mHolder;
	SurfaceView local_surface_camera;
	ImageView mask_p2p_view, mask_camera;
	private boolean cameraIsShow = true;
	private boolean mPreviewRunning = false;
	private H264Encoder mEncoder;

	RelativeLayout control_bottom;
	boolean isControlShow = true;

	private int mWindowWidth, mWindowHeight;
	boolean isReject = false;
	//报警推送
	private String NewMessageDeviceID="";
	private int pushAlarmType;
	private boolean isCustomCmdAlarm=false;
	private String alarm_id="";
	AlarmCloseVoice acl;
	int mainType,subType;
	Contact alarm_contact;
	String callId="";
	// 监控当前设备推送报警信息，显示关闭本次报警按钮
	AlarmCloseVoice alarmClose;
	boolean isShowAlarmClose = false;
	// 区分静音正在监控的设备还是其它设备
	public final int KEEP_SELF_CLIENT = 0;
	public final int KEEP_OTHER_CLIENT = 1;
	// 设备静音按钮状态
	public final int NORMAL = 0;
	public final int LOADING = 1;
	public final int CLOSE = 2;
	RelativeLayout r_pview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		P2PConnect.setPlaying(true);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.p2p_video);
		type = this.getIntent().getIntExtra("type", -1);
		callId=this.getIntent().getStringExtra("callId");
		mContext = this;
		initComponent();
		regFilter();
		startWatcher();
		openCamera();
	}

	public void initComponent() {
		pView = (P2PView) findViewById(R.id.pView);
		this.initP2PView(P2PConnect.getCurrentDeviceType(),P2PView.LAYOUTTYPE_SEPARATION);

		switch_camera = (ImageView) findViewById(R.id.switch_camera);
		hungup = (ImageView) findViewById(R.id.hungup);
		close_mike = (ImageView) findViewById(R.id.close_mike);
		local_surface_camera = (SurfaceView) findViewById(R.id.local_surface_camera);
		mask_camera = (ImageView) findViewById(R.id.mask_camera);

		mask_p2p_view = (ImageView) findViewById(R.id.mask_p2p_view);
		control_bottom = (RelativeLayout) findViewById(R.id.control_bottom);
		r_pview=(RelativeLayout)findViewById(R.id.r_pview);
		

		mask_camera.setOnTouchListener(onTouch);
		local_surface_camera.setOnTouchListener(onTouch);

		switch_camera.setOnClickListener(this);
		hungup.setOnClickListener(this);
		close_mike.setOnClickListener(this);

		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();

		mWindowWidth = dm.widthPixels;
		mWindowHeight = dm.heightPixels;
		AbsoluteLayout.LayoutParams params1 = (AbsoluteLayout.LayoutParams) local_surface_camera
				.getLayoutParams();
		AbsoluteLayout.LayoutParams params2 = (AbsoluteLayout.LayoutParams) mask_camera
				.getLayoutParams();
		params1.x = mWindowWidth - params1.width;
		local_surface_camera.setLayoutParams(params1);

		params2.x = mWindowWidth - params2.width;
		mask_camera.setLayoutParams(params2);

	}

	@Override
	public void onHomePressed() {
		// TODO Auto-generated method stub
		super.onHomePressed();
		this.reject();
	}

	private void startWatcher() {
		mPhoneWatcher = new PhoneWatcher(mContext);
		mPhoneWatcher
				.setOnCommingCallListener(new PhoneWatcher.OnCommingCallListener() {

					@Override
					public void onCommingCall() {
						// TODO Auto-generated method stub
						reject();
					}

				});
		mPhoneWatcher.startWatcher();
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.P2P_REJECT);
		filter.addAction(Constants.P2P.P2P_CHANGE_IMAGE_TRANSFER);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
//		filter.addAction(Constants.Action.MONITOR_NEWDEVICEALARMING);
//		filter.addAction(Constants.P2P.RET_KEEP_CLIENT);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.P2P.P2P_REJECT)) {
				reject();
			} else if (intent.getAction().equals(
					Constants.P2P.P2P_CHANGE_IMAGE_TRANSFER)) {
				int state = intent.getIntExtra("state", -1);
				if (state == 0) {
					mask_p2p_view.setVisibility(RelativeLayout.GONE);
				} else if (state == 1) {
					mask_p2p_view.setVisibility(RelativeLayout.VISIBLE);
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					reject();
				}
			} else if (intent.getAction().equals(
					Constants.Action.MONITOR_NEWDEVICEALARMING)) {
				// 弹窗
				int MessageType = intent.getIntExtra("messagetype", 2);
				int type = intent.getIntExtra("alarm_type", 0);
				pushAlarmType = type;
				isCustomCmdAlarm = intent.getBooleanExtra("isCustomCmdAlarm",
						false);
				int group = intent.getIntExtra("group", -1);
				int item = intent.getIntExtra("item", -1);
				boolean isSupport = intent.getBooleanExtra("isSupport", false);
				boolean isSupportDelete = intent.getBooleanExtra(
						"isSupportDelete", false);
				subType = intent.getIntExtra("subType", -1);
				mainType = intent.getIntExtra("mainType", -1);
				String customMsg=intent.getStringExtra("customMsg");
				if (MessageType == 1) {
					// 报警推送
					NewMessageDeviceID = intent.getStringExtra("alarm_id");
					if (NewMessageDeviceID.equals(callId)) {
						if (type != P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
							showAlarmCloseVoice();
						}
						return;
					}
					if (alarm_id.equals(NewMessageDeviceID)
							&& passworddialog != null
							&& passworddialog.isShowing()) {
						return;
					} else {
						alarm_id = NewMessageDeviceID;
					}
				} else {
					// 透传推送
					NewMessageDeviceID = intent.getStringExtra("contactId");
					Log.i("dxsmoniter_alarm", "透传推送" + NewMessageDeviceID);
				}
				String alarmtype = Utils.getAlarmType(NewMessageDeviceID, type,
						isSupport, group, item,customMsg);
				StringBuffer NewMassage = new StringBuffer(
						Utils.getStringByResouceID(R.string.tab_device))
						.append("：").append(
								Utils.getDeviceName(NewMessageDeviceID));
				NewMassage.append("\n").append(Utils.getStringByResouceID(R.string.allarm_type)).
				append(alarmtype);
				int alarmstate=NORMAL;
				if(type==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
					alarmstate=CLOSE;
				}
				acl = new AlarmCloseVoice(mContext, NewMessageDeviceID);
				acl.settype(KEEP_OTHER_CLIENT);
				acl.setState(alarmstate);
				acl.setcloseClickListener(clistener);
				NewMessageDialog(NewMassage.toString(), NewMessageDeviceID,
						isSupportDelete, acl);
			}else if (intent.getAction().equals(Constants.P2P.RET_KEEP_CLIENT)) {
				int iSrcID = intent.getIntExtra("iSrcID", -1);
				byte boption = intent.getByteExtra("boption", (byte) -1);
				if (boption == Constants.FishBoption.MESG_SET_OK) {
            		if(callId.equals(String.valueOf(iSrcID))){
						// if(alarmClose!=null){
						// alarmClose.setState(2);
						// alarmClose.startLoading();
						// }
						hideAlarmCloseVoice();
					} else {
						if (acl != null) {
							acl.setState(2);
							acl.startLoading();
						}

					}
				}
			}

		}
	};

	OnTouchListener onTouch = new OnTouchListener() {
		boolean isActive = false;
		long downTime;
		int mWidth, mHeight;
		float downWidth;
		float downHeight;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// TODO Auto-generated method stub
			int y = (int) event.getY();
			AbsoluteLayout.LayoutParams params1 = (AbsoluteLayout.LayoutParams) local_surface_camera
					.getLayoutParams();
			AbsoluteLayout.LayoutParams params2 = (AbsoluteLayout.LayoutParams) mask_camera
					.getLayoutParams();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downTime = System.currentTimeMillis();
				mWidth = params1.width;
				mHeight = params1.height;
				downWidth = event.getRawX() - params1.x;
				downHeight = event.getRawY() - params1.y;
				isActive = true;
				break;
			case MotionEvent.ACTION_UP:
				isActive = false;
				if ((System.currentTimeMillis() - downTime) < 100) {
					if (cameraIsShow) {
						if (VideoActivity.this.closeLocalCamera()) {
							cameraIsShow = false;
							mask_camera.setVisibility(RelativeLayout.VISIBLE);
							local_surface_camera
									.setVisibility(RelativeLayout.GONE);
						}
					} else {
						if (VideoActivity.this.openLocalCamera()) {
							cameraIsShow = true;
							mask_camera.setVisibility(RelativeLayout.GONE);
							local_surface_camera
									.setVisibility(RelativeLayout.VISIBLE);
						}
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				Log.e("my", "rawxy:" + event.getRawX() + ":" + event.getRawY());

				int changeX = (int) (event.getRawX() - downWidth);
				if (changeX < 0) {
					changeX = 0;
				} else if (changeX > (mWindowWidth - mWidth)) {
					changeX = mWindowWidth - mWidth;
				}

				int changeY = (int) (event.getRawY() - downHeight);
				if (changeY < 0) {
					changeY = 0;
				} else if (changeY > (mWindowHeight - mHeight)) {
					changeY = mWindowHeight - mHeight;
				}
				params1.x = changeX;
				params1.y = changeY;
				local_surface_camera.setLayoutParams(params1);

				params2.x = changeX;
				params2.y = changeY;
				mask_camera.setLayoutParams(params2);
				break;
			}
			return true;
		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.switch_camera) {
			swtichCamera();

		} else if (i == R.id.hungup) {
			this.reject();

		} else if (i == R.id.close_mike) {
			if (!mIsCloseMike) {
				close_mike.setImageResource(R.drawable.btn_no_sound_s);
				this.setMute(true);
				mIsCloseMike = true;
			} else {
				close_mike.setImageResource(R.drawable.btn_no_sound);
				this.setMute(false);
				mIsCloseMike = false;
			}

		} else if (i == R.id.local_surface_camera) {
			if (cameraIsShow) {
				if (this.closeLocalCamera()) {
					Log.e("my", "close camera");
					cameraIsShow = false;
					mask_camera.setVisibility(RelativeLayout.VISIBLE);
					local_surface_camera.setVisibility(RelativeLayout.GONE);
				}
			}

		} else if (i == R.id.mask_camera) {
			if (!cameraIsShow) {
				if (this.openLocalCamera()) {
					Log.e("my", "open camera");
					cameraIsShow = true;
					mask_camera.setVisibility(RelativeLayout.GONE);
					local_surface_camera.setVisibility(RelativeLayout.VISIBLE);
				}
			}
		}
	}

	public void changeControl() {
		if (isControlShow) {
			isControlShow = false;
			// Animation anim2 = AnimationUtils.loadAnimation(this,
			// R.anim.slide_out_top);
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out);
			anim2.setDuration(200);
			control_bottom.startAnimation(anim2);
			control_bottom.setVisibility(RelativeLayout.GONE);

		} else {
			isControlShow = true;
			control_bottom.setVisibility(RelativeLayout.VISIBLE);
			// Animation anim2 = AnimationUtils.loadAnimation(this,
			// R.anim.slide_in_bottom);
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in);
			anim2.setDuration(200);
			control_bottom.startAnimation(anim2);

		}
	}

	private void swtichCamera() {
		try {
			if (Camera.getNumberOfCameras() < 2) {
				return;
			}

			releaseCamera();
			mCamera = XCamera.switchCamera();
			if (null != mCamera) {
				mCamera.setPreviewDisplay(mHolder);
				Parameters parameters = mCamera.getParameters();

				List<Integer> LRates = parameters
						.getSupportedPreviewFrameRates();
				int iFrameRateTmp = 1;
				int iDiff = DEFAULT_FRAME_RATE;
				int iNewRate = 0;
				for (int i = 0; i < LRates.size(); i++) {
					iNewRate = LRates.get(i);
					if (iFrameRateTmp > DEFAULT_FRAME_RATE)
						iDiff = iFrameRateTmp - DEFAULT_FRAME_RATE;
					else
						iDiff = DEFAULT_FRAME_RATE - iFrameRateTmp;
					if (iDiff == 0)
						break;

					if (iNewRate <= DEFAULT_FRAME_RATE
							&& (DEFAULT_FRAME_RATE - iNewRate) < iDiff) {
						iFrameRateTmp = iNewRate;
					} else if (iNewRate > DEFAULT_FRAME_RATE
							&& (iNewRate - DEFAULT_FRAME_RATE) < iDiff) {
						iFrameRateTmp = iNewRate;
					}
				}
				if (iFrameRateTmp > DEFAULT_FRAME_RATE / 2
						|| (iFrameRateTmp < DEFAULT_FRAME_RATE * 3 / 2)) {
					mVideoFrameRate = iFrameRateTmp;
					parameters.setPreviewFrameRate(mVideoFrameRate);
				}

				parameters.setPreviewSize(P2P_SURFACE_START_PLAYING_WIDTH,
						P2P_SURFACE_START_PLAYING_HEIGHT);
				parameters.set("orientation", "landscape");
				setFormat(parameters);
				mCamera.setDisplayOrientation(0);
				mCamera.setParameters(parameters);
				mEncoder = new H264Encoder(mCamera.getParameters()
						.getPreviewSize().width, mCamera.getParameters()
						.getPreviewSize().height);
				mCamera.setPreviewCallback(mEncoder);
				mCamera.startPreview();
				mPreviewRunning = true;
			}
		} catch (Exception e) {
			releaseCamera();
		}

	}

	private void setFormat(Parameters p) {
		List<Integer> supportList = null;
		supportList = p.getSupportedPreviewFormats();
		if (supportList == null || supportList.size() == 0) {
			return;
		}
		int format = -1;
		for (int i = 0; i < supportList.size(); i++) {
			int test = supportList.get(i);
			Log.e("my", test + "");

		}

		for (int i = 0; i < supportList.size(); i++) {
			format = supportList.get(i);
			if (ImageFormat.NV21 == format) {
				p.setPreviewFormat(ImageFormat.NV21);
				isYV12 = false;
				break;
			} else if (ImageFormat.YV12 == format) {
				p.setPreviewFormat(ImageFormat.YV12);
				isYV12 = true;
				break;
			}

		}

	}

	@Override
	public void onBackPressed() {
		reject();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
		if (null != mPhoneWatcher) {
			mPhoneWatcher.stopWatcher();
		}
		P2PConnect.setPlaying(false);

		if (!activity_stack
				.containsKey(Constants.ActivityInfo.ACTIVITY_MAINACTIVITY)) {
			Intent i = new Intent(this, MainActivity.class);
			this.startActivity(i);
		}

		Intent refreshContans = new Intent();
		refreshContans.setAction(Constants.Action.REFRESH_CONTANTS);
		mContext.sendBroadcast(refreshContans);
	}

	public void openCamera() {
		mHolder = local_surface_camera.getHolder();
		mHolder.addCallback(new LocalCameraCallBack());
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		local_surface_camera.setZOrderOnTop(true);
	}

	public synchronized void releaseCamera() {
		if (mCamera != null) {
			Log.e("p2p", "releaseCamera");
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	class H264Encoder implements Camera.PreviewCallback {

		private boolean mIsWritingData = false;

		public H264Encoder(int width, int height) {
		};

		@Override
		public void onPreviewFrame(final byte[] data, Camera camera) {
			if (data != null && !mIsWritingData) {
				mIsWritingData = true;
				Parameters p = camera.getParameters();

				Log.e("debug",
						p.getPreviewSize().width + ":::::"
								+ p.getPreviewSize().height);
				if (cameraIsShow) {
					if (isYV12) {
						VideoActivity.this.fillCameraData(data, data.length,
								p.getPreviewSize().width,
								p.getPreviewSize().height, 1);
					} else {
						VideoActivity.this.fillCameraData(data, data.length,
								p.getPreviewSize().width,
								p.getPreviewSize().height, 0);
					}

				}
				mIsWritingData = false;
			}
		}
	}

	class LocalCameraCallBack implements SurfaceHolder.Callback,
			Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			if (mPreviewRunning) {
				mCamera.stopPreview();
			}
			try {
				if (null != mCamera) {
					Parameters parameters = mCamera.getParameters();

					List<Integer> LRates = parameters
							.getSupportedPreviewFrameRates();
					int iFrameRateTmp = 1;
					int iDiff = DEFAULT_FRAME_RATE;
					int iNewRate = 0;
					for (int i = 0; i < LRates.size(); i++) {
						iNewRate = LRates.get(i);
						if (iFrameRateTmp > DEFAULT_FRAME_RATE)
							iDiff = iFrameRateTmp - DEFAULT_FRAME_RATE;
						else
							iDiff = DEFAULT_FRAME_RATE - iFrameRateTmp;
						if (iDiff == 0)
							break;

						if (iNewRate <= DEFAULT_FRAME_RATE
								&& (DEFAULT_FRAME_RATE - iNewRate) < iDiff) {
							iFrameRateTmp = iNewRate;
						} else if (iNewRate > DEFAULT_FRAME_RATE
								&& (iNewRate - DEFAULT_FRAME_RATE) < iDiff) {
							iFrameRateTmp = iNewRate;
						}
					}
					if (iFrameRateTmp > DEFAULT_FRAME_RATE / 2
							|| (iFrameRateTmp < DEFAULT_FRAME_RATE * 3 / 2)) {
						mVideoFrameRate = iFrameRateTmp;
						parameters.setPreviewFrameRate(mVideoFrameRate);
					}

					List<Size> supportedPreviewSizes = parameters
							.getSupportedPreviewSizes();

					for (Size size : supportedPreviewSizes) {
						Log.e("debug", size.width + ":" + size.height);
					}
					parameters.setPreviewSize(P2P_SURFACE_START_PLAYING_WIDTH,
							P2P_SURFACE_START_PLAYING_HEIGHT);
					parameters.set("orientation", "landscape"); //

					setFormat(parameters);
					mCamera.setDisplayOrientation(0);
					mCamera.setParameters(parameters); // 将Camera.Parameters设定予Camera
					mEncoder = new H264Encoder(mCamera.getParameters()
							.getPreviewSize().width, mCamera.getParameters()
							.getPreviewSize().height);
					mCamera.setPreviewCallback(mEncoder);
					mCamera.startPreview(); // 打开预览画面
					mPreviewRunning = true;
				}

			} catch (Exception e) {
				T.showShort(mContext, R.string.camera_error);
				releaseCamera();
			}

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			try {
				mCamera = XCamera.open();
				mCamera.setPreviewDisplay(mHolder);
			} catch (Exception e) {
				if (null != mCamera) {
					releaseCamera();
				}
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			releaseCamera();
		}

	}

	public void reject() {
		if (!isReject) {
			isReject = true;
			P2PHandler.getInstance().reject();
			releaseCamera();
			finish();
		}
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_VIDEOACTIVITY;
	}

	@Override
	protected void onP2PViewSingleTap() {
		// TODO Auto-generated method stub
		changeControl();
	}

	@Override
	protected void onGoBack() {
		// TODO Auto-generated method stub
		MyApp.app.showNotification();
	}

	@Override
	protected void onGoFront() {
		// TODO Auto-generated method stub
		MyApp.app.hideNotification();
	}

	@Override
	protected void onExit() {
		// TODO Auto-generated method stub
		MyApp.app.hideNotification();
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), R.string.Press_again,
						Toast.LENGTH_SHORT).show();
				T.showShort(mContext, R.string.Press_again);
				exitTime = System.currentTimeMillis();
			} else {
				reject();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
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
	closeClickListener clistener = new closeClickListener() {

		@Override
		public void onclose(String deviceId, int type) {
			// TODO Auto-generated method stub
			closeAlarmVoice(deviceId, type);
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
			reject();
			P2PConnect.vReject(0,"");
			Message msg = new Message();
			msg.obj = contact;
			handler.sendMessageDelayed(msg, 500);
		} else {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			createPassDialog(contactId);
		}
	}
	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			Contact contact = (Contact) msg.obj;
			Intent monitor = new Intent();
			monitor.setClass(mContext, ApMonitorActivity.class);
			monitor.putExtra("contact", contact);
			monitor.putExtra("connectType",Constants.ConnectType.P2PCONNECT);
			monitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(monitor);
			return false;
		}
	});
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
				final Contact contact=new Contact();
				contact.contactId=id;
				contact.activeUser=NpcCommon.mThreeNum;
				contact.contactName=id;
				contact.contactPassword=password;
				contact.contactType=mainType;
				contact.subType=subType;
				alarm_contact=contact;
//				callDevice(contact);
				P2PConnect.vReject(0,"");
				Message msg = new Message();
				msg.obj = contact;
				handler.sendMessageDelayed(msg, 500);
			}
		};
		// 关闭本次报警声音
		public void closeAlarmVoice(String alarmID, int type) {
			if (type == KEEP_SELF_CLIENT) {
				NormalDialog dialog = new NormalDialog(mContext, mContext
						.getResources().getString(R.string.mute_the_alarm),
						mContext.getResources().getString(
								R.string.comfirm_mute_the_alarm), mContext
								.getResources().getString(R.string.confirm),
						mContext.getResources().getString(R.string.cancel));
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

						@Override
						public void onClick() {
							  if(alarmClose!=null){
								  alarmClose.setState(1);
								  alarmClose.startLoading();
							  }
							  Contact contact = FList.getInstance().isContact(callId);
							  if(contact!=null){
								  FisheyeSetHandler.getInstance().sKeepClientCmd(contact.contactId, contact.contactPassword);
							  }
						}
					});
					dialog.showNormalDialog();
				}else{
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
								 if(acl!=null){
									 acl.setState(1);
									 acl.startLoading();
								  }
								FisheyeSetHandler.getInstance().sKeepClientCmd(contact.getIpContactId(), contact.contactPassword);
							}
						});
						dialog.showNormalDialog();
					} else {
						closecreatePassDialog(alarmID);
					}
				}
			}
			// 监控时，本台设备推送报警过来，在页面上显示关闭本次报警图标
			public void showAlarmCloseVoice() {
				if (!isShowAlarmClose) {
					alarmClose = new AlarmCloseVoice(mContext, callId);
					alarmClose.settype(KEEP_SELF_CLIENT);
					alarmClose.setcloseClickListener(clistener);
					alarmClose.setState(NORMAL);
					r_pview.addView(alarmClose);
					isShowAlarmClose = true;
				}
			}
			public void hideAlarmCloseVoice() {
				if (alarmClose != null && isShowAlarmClose == true) {
					r_pview.removeView(alarmClose);
					isShowAlarmClose = false;
				}
			}
}
