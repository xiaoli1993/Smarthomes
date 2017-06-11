package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.data.Contact;
import com.jwkj.global.Constants;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.T;
import com.jwkj.utils.UDPHelper;
import com.jwkj.widget.ConfirmOrCancelDialog;
import com.lsemtmf.genersdk.tools.emtmf.EMTMFSDK;
import com.lsemtmf.genersdk.tools.emtmf.EMTMFSDKListener;
import com.mediatek.elian.ElianNative;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.nuowei.smarthome.R.string.give_up_add_device;

public class AddWaitActicity extends BaseActivity implements OnClickListener {
	private ImageView ivBacke;
	private Context mContext;
	private DinProTextView tv_title,tv_time;
	ImageView iv_sound_wave,iv_sound_wave_out;
	public Handler myhandler = new Handler();
	boolean isReceive = false;
	String ssid, pwd;

	Thread mThread = null;
	boolean mDone = true;
	public UDPHelper mHelper;
	byte type;
	int mLocalIp;
	ElianNative elain;
	private boolean isSendWifiStop = true;
	private boolean isTimerCancel = true;
	private boolean isNeedSendWifi = true;// 二维码页面返回时不需要发包
	private long TimeOut;
	private int timeCountdown;
	Timer timer = new Timer();
	boolean isExit=false;
	WifiManager.MulticastLock lock;
	TranslateAnimation manimation;
	static {
		System.loadLibrary("elianjni");
	}
	boolean isNeedStopSendWave=false;
	Animation animation,animation2;
	boolean isAnimation=true;
	private ConfirmOrCancelDialog backDialg;
    private int timer_unit=1;
//    private MyTimerTask timerTask;
//    Timer timer;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.activity_add_waite);
		mContext = this;
		WifiManager manager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		lock = manager.createMulticastLock("localWifi");
		ssid = getIntent().getStringExtra("ssidname");
		pwd = getIntent().getStringExtra("wifiPwd");
		type = getIntent().getByteExtra("type", (byte) -1);
		mLocalIp = getIntent().getIntExtra("LocalIp", -1);
		isNeedSendWifi = getIntent().getBooleanExtra("isNeedSendWifi", true);
		initUI();
		timeCountdown=110;
		if (isNeedSendWifi) {
			TimeOut = 110 * 1000;
			excuteTimer();
		} else {
			TimeOut = 60 * 1000;
			tv_title.setText(getResources().getString(
					R.string.qr_code_add_device));
		}
		lock.acquire();
		mHelper = new UDPHelper(9988);
		listen();
		myhandler.postDelayed(mrunnable, TimeOut);
		mHelper.StartListen();
		timer.schedule(task, 1000, 1000);
//        startTimer();
	}
//	TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//
//            runOnUiThread(new Runnable() {      // UI thread
//                @Override
//                public void run() {
//                	Log.e("leleTimerTask","timeCountdown="+timeCountdown);
//                	timeCountdown--;
//                	tv_time.setText(timeCountdown+"s");
//                	Log.e("leleTimerTask","timeCountdown-->"+timeCountdown);
//                    if(timeCountdown < 0){
//                        timer.cancel();
//                        Log.e("leleTimerTask","timer.cancel");
//                    }
//                }
//            });
//        }
//    };
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			timeCountdown--;
			timeHandler.sendEmptyMessage(timeCountdown);

		}
	};
//	 class MyTimerTask extends TimerTask{
//         @Override
//         public void run() {
//             timeCountdown--;
//             Log.e("timeCountdown","timeCountdown="+timeCountdown);
//             timeHandler.sendEmptyMessage(timeCountdown);
//         }
//     }

//	private void  startTimer(){
//		timer = new Timer();
//		timerTask = new MyTimerTask();
//		timer.schedule(timerTask, 1000, 1000);
//	}
//    private void stopTimer(){
//        if(timer!=null){
//            timer.cancel();
//        }
//    }
	Handler timeHandler=new Handler(){
		public void handleMessage(Message msg) {
			int what=msg.what;
			tv_time.setText(what+"s");
		};
	};
	private Timer mTimer;
	private int time;

	private void excuteTimer() {
		mTimer = new Timer();
		TimerTask mTask = new TimerTask() {
			@Override
			public void run() {
				if (time < 3) {
					sendWifiHandler.sendEmptyMessage(0);
				} else {
					sendWifiHandler.sendEmptyMessage(1);
				}
			}
		};
		mTimer.schedule(mTask, 500, 30 * 1000);
		isTimerCancel = false;
	}

	private void cancleTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			isTimerCancel = true;
		}
	}

	private Handler sendWifiHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message arg0) {
			switch (arg0.what) {
			case 0:
				time++;
				sendWifi();
				Log.i("dxsnewTimer", "第" + time + "次发包时间:" + getTime());
				break;
			case 1:
				cancleTimer();
				Log.i("dxsnewTimer", "第" + time + "次停止计时器时间:" + getTime());
				break;
			case 2:
				stopSendWifi();
				Log.i("dxsnewTimer", "第" + time + "次停止发包时间:" + getTime());
				break;

			default:
				break;
			}
			return false;
		}
	});

	/**
	 * 发包 20秒后停止
	 */ 
	private void sendWifi() {
		if (elain == null) {
			elain = new ElianNative();
		}
		if (null != ssid && !"".equals(ssid)) {
			elain.InitSmartConnection(null, 1, 1);
			elain.StartSmartConnection(ssid, pwd, "", type);
			Log.e("wifi_mesg", "ssidname=" + ssid + "--" + "wifipwd=" + pwd
					+ "--" + "type=" + type);
			isSendWifiStop = false;
		}
		sendWifiHandler.postDelayed(stopRunnable, 20 * 1000);
	}

	public Runnable stopRunnable = new Runnable() {
		@Override
		public void run() {
			sendWifiHandler.sendEmptyMessage(2);
		}
	};

	/**
	 * 停止发包
	 */
	private void stopSendWifi() {
		if (elain != null) {
			elain.StopSmartConnection();
			isSendWifiStop = true;
		}

	}

	private void initUI() {
		ivBacke = (ImageView) findViewById(R.id.img_back);
		tv_title = (DinProTextView) findViewById(R.id.title);
		tv_time=(DinProTextView)findViewById(R.id.tv_time);
		iv_sound_wave=(ImageView)findViewById(R.id.iv_sound_wave);
		iv_sound_wave_out=(ImageView)findViewById(R.id.iv_sound_wave_out);
	    animation=AnimationUtils.loadAnimation(mContext, R.anim.sound_wave_translate);
	    animation2=AnimationUtils.loadAnimation(mContext, R.anim.sound_wave_out_translate);
	    iv_sound_wave.setAnimation(animation2);
	    iv_sound_wave_out.setAnimation(animation);
	    animation.start();
	    animation2.start();
		ivBacke.setOnClickListener(this);
		
	}
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	EMTMFSDK.getInstance(mContext).setListener(listener);
    	EMTMFSDK.getInstance(mContext).sendWifiSet(mContext, ssid, pwd);
		isNeedStopSendWave=true;
    }
	void listen() {
		mHelper.setCallBack(new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case UDPHelper.HANDLER_MESSAGE_BIND_ERROR:
					Log.e("my", "HANDLER_MESSAGE_BIND_ERROR");
					T.showShort(mContext, R.string.port_is_occupied);
					break;
				case UDPHelper.HANDLER_MESSAGE_RECEIVE_MSG:
					isExit=true;
					isAnimation=false;
					animation.cancel();
					animation2.cancel();
					iv_sound_wave.clearAnimation();
					iv_sound_wave_out.clearAnimation();
					EMTMFSDK.getInstance(mContext).stopSend();
					T.showShort(mContext, R.string.set_wifi_success);
					mHelper.StopListen();
					Bundle bundle = msg.getData();
					Intent it = new Intent();
					it.setAction(Constants.Action.RADAR_SET_WIFI_SUCCESS);
					sendBroadcast(it);
					
					String contactId = bundle.getString("contactId");
					String frag = bundle.getString("frag");
					String ipFlag = bundle.getString("ipFlag");
					String ip=bundle.getString("ip");
					Contact saveContact = new Contact();
					saveContact.contactId = contactId;
					saveContact.contactName=contactId;
					saveContact.activeUser = NpcCommon.mThreeNum;
					isReceive = true;
				    EMTMFSDK.getInstance(mContext).exitEMTFSDK(mContext);
					Intent add_device = new Intent(mContext,
							AddContactNextActivity.class);
					add_device.putExtra("contact", saveContact);
					if (Integer.parseInt(frag) == Constants.DeviceFlag.UNSET_PASSWORD) {
						add_device.putExtra("isCreatePassword", true);
					} else {
						add_device.putExtra("isCreatePassword", false);
					}
					add_device.putExtra("isfactory", true);
					add_device.putExtra("ipFlag", ipFlag);
					add_device.putExtra("ip", ip);
                    add_device.putExtra("addDeviceMethod",Constants.AddDeviceMethod.WIFI_METHOD);
					startActivity(add_device);
					finish();
					break;
				}
				cancleTimer();
			}

		});
	}

	public Runnable mrunnable = new Runnable() {

		@Override
		public void run() {
			if (!isReceive) {
				if (isNeedSendWifi) {
					T.showShort(mContext, R.string.set_wifi_failed);
					Intent it = new Intent();
					it.setAction(Constants.Action.RADAR_SET_WIFI_FAILED);
					sendBroadcast(it);
					// 跳转
					finish();
				} else {
					T.showShort(mContext, R.string.set_wifi_failed);
					finish();
				}

			}
		}
	};
	public final int EMTMFSDK_SET_SUCCESS=0;
	public final int EMTMFSDK_END=1;
    EMTMFSDKListener listener=new EMTMFSDKListener(){
    	public void didSetWifiResult(int errcode, String content) {
    		if(errcode==EMTMFSDK.SET_SUCCESS){
    			EMTMFSDKHandler.sendEmptyMessage(EMTMFSDK_SET_SUCCESS);
    		}else{
    			
    		}
    	};
    	public void didEndOfPlay() {
    		EMTMFSDKHandler.sendEmptyMessage(EMTMFSDK_END);
//    		EMTMFSDKHandler.sendEmptyMessageDelayed(EMTMFSDK_END, 2000);
    		
    	};
    	public void didSDKErrcode(){
    		
    	}
    };
    Handler EMTMFSDKHandler=new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case EMTMFSDK_SET_SUCCESS:
//				T.showShort(mContext, "配置成功");
				break;
			case EMTMFSDK_END:
//				T.showShort(mContext, "发送声波结束");
//				if(isNeedStopSendWave){
//					EMTMFSDK.getInstance(mContext).stopSend(); 
//					isNeedStopSendWave=false;
//				}
				if(!isExit){
//					T.showShort(mContext, "开始发送声波");
					EMTMFSDK.getInstance(mContext).sendWifiSet(mContext, ssid, pwd);
					isNeedStopSendWave=true;
				}
				break;
			default:
				break;
			}
			return false;
		}
	});
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.img_back:
			if(backDialg!=null&&backDialg.isShowing()){
				return;
			}
			backDialg=new ConfirmOrCancelDialog(mContext,ConfirmOrCancelDialog.SELECTOR_BLUE_TEXT,ConfirmOrCancelDialog.SELECTOR_GARY_TEXT);
			backDialg.setTitle(getResources().getString(give_up_add_device));
			backDialg.setTextYes(getResources().getString(R.string.exit));
			backDialg.setTextNo(getResources().getString(R.string.continue_to_wait));
			backDialg.setOnYesClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
                    Intent exit=new Intent();
                    exit.setAction(Constants.Action.EXIT_ADD_DEVICE);
                    sendBroadcast(exit);
					EMTMFSDK.getInstance(mContext).stopSend();
					finish();
				}
			});
			backDialg.setOnNoClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					backDialg.dismiss();
				}
			});
			backDialg.show();
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		myhandler.removeCallbacks(mrunnable);
		sendWifiHandler.removeCallbacks(stopRunnable);
		mHelper.StopListen();
		EMTMFSDK.getInstance(mContext).stopSend();
		isExit=true;
		if (!isTimerCancel) {
			cancleTimer();
		}
		if(task!=null){
			task.cancel();
		}
		if(isAnimation){
			animation.cancel();
			animation2.cancel();
			isAnimation=false;
		}
		super.onDestroy();
		

	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_ADDWAITACTIVITY;
	}

	private String getTime() {
		String time = new SimpleDateFormat("HH-mm-ss").format(new Date());
		return time;
	}

	@Override
	protected void onPause() {
		super.onPause();
		EMTMFSDK.getInstance(mContext).stopSend();
		if (!isSendWifiStop) {
			stopSendWifi();
		}
		try {
			lock.release();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if(backDialg!=null&&backDialg.isShowing()){
				return true;
			}
			backDialg=new ConfirmOrCancelDialog(mContext);
			backDialg.setTitle(getResources().getString(give_up_add_device));
			backDialg.setTextYes(getResources().getString(R.string.exit));
			backDialg.setTextNo(getResources().getString(R.string.continue_to_wait));
			backDialg.setOnYesClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent exit=new Intent();
					exit.setAction(Constants.Action.EXIT_ADD_DEVICE);
					sendBroadcast(exit);
					EMTMFSDK.getInstance(mContext).stopSend();
					finish();
				}
			});
			backDialg.setOnNoClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					backDialg.dismiss();
				}
			});
			backDialg.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
