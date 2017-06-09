package com.jwkj.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.jwkj.global.Constants;
import com.jwkj.utils.T;
import com.nuowei.smarthome.R;

public class SendSoundWaveGuideActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	ImageView back_btn;
	Button send_wave;
	String ssid;
	String wifiPwd;
	private byte mAuthMode;
	int mLocalIp;
	boolean isRegFilter=false;
	AudioManager mAudioManager;
	int mCurrentVolume, mMaxVolume;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_send_sound_wave_guide);
		mContext=this;
		ssid = getIntent().getStringExtra("ssidname");
		wifiPwd = getIntent().getStringExtra("wifiPwd");
		mAuthMode = getIntent().getByteExtra("type", (byte) -1);
		mLocalIp = getIntent().getIntExtra("LocalIp", -1);
		initComponent();
		regFilter();
	}
    public void initComponent(){
    	back_btn=(ImageView)findViewById(R.id.back_btn);
    	send_wave=(Button)findViewById(R.id.send_wave);
    	back_btn.setOnClickListener(this);
    	send_wave.setOnClickListener(this);
//    	String wholeStr = getResources().getString(R.string.ajust_voice2);
//        StringFormatUtil spanStr = new StringFormatUtil(this, wholeStr,
//                  getResources().getString(R.string.close_device), R.color.blue).fillColor();
//        if(spanStr!=null&&spanStr.getResult()!=null){
//        	tv_ajust_voice2.setText(spanStr.getResult());
//        }
    	
    }
    public void regFilter(){
    	IntentFilter filter=new IntentFilter();
    	filter.addAction(Constants.Action.RADAR_SET_WIFI_SUCCESS);
    	filter.addAction(Constants.Action.RADAR_SET_WIFI_FAILED);
		filter.addAction(Constants.Action.EXIT_ADD_DEVICE);
    	registerReceiver(br, filter);
    	isRegFilter=true;
    }
    BroadcastReceiver br=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Constants.Action.RADAR_SET_WIFI_SUCCESS)){
				finish();
			}else if(intent.getAction().equals(Constants.Action.RADAR_SET_WIFI_FAILED)){
				finish();
			}else if(intent.getAction().equals(Constants.Action.EXIT_ADD_DEVICE)){
				finish();
			}
		}
	};
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_SENDSOUNDWAVEGUIDEACTIVITY;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.send_wave:
			if (mAudioManager == null) {
				mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			}
			mCurrentVolume = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			mMaxVolume = mAudioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			if(mCurrentVolume<10){
				T.showShort(mContext, R.string.tone_voice);
			}
			Intent device_network = new Intent(mContext, AddWaitActicity.class);
			device_network.putExtra("ssidname", ssid);
			device_network.putExtra("wifiPwd", wifiPwd);
			device_network.putExtra("type", mAuthMode);
			device_network.putExtra("LocalIp", mLocalIp);
			device_network.putExtra("isNeedSendWifi", true);
			startActivity(device_network);
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(isRegFilter){
			unregisterReceiver(br);
			isRegFilter=false;
		}
	}

}
