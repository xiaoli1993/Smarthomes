package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.jwkj.data.Contact;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.thread.DelayThread;
import com.jwkj.utils.T;
import com.p2p.core.P2PHandler;
import com.nuowei.ipclibrary.R;

public class VideoControlFrag extends BaseFragment implements OnClickListener {
	private Context mContext;
	private Contact contact;
	private boolean isRegFilter = false;
	RelativeLayout change_video_format, change_volume, video_voleme_seek,
			layout_reverse;
	LinearLayout video_format_radio;
	ProgressBar progressBar_video_format, progressBar_volume;
	RadioButton radio_one, radio_two;
	SeekBar seek_volume;
	int cur_modify_video_format;
	int cur_modify_video_volume;
	RelativeLayout change_image_reverse;
	ImageView img_image_reverse;
	ProgressBar progressbar_image_reverse;
	//指示灯
	RelativeLayout r_light_control;
	ImageView light_control_img;
	ProgressBar progressBar_light_control;
	
	boolean isOpenReverse = true;
	boolean isOpenModify;
	String idOrIp;
	boolean islightOn=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = getActivity();
		contact = (Contact) getArguments().getSerializable("contact");
		idOrIp=contact.getContactId();
		View view = inflater.inflate(R.layout.fragment_video_control,
				container, false);
		initComponent(view);
		regFilter();
		showProgress_video_format();
		showProgress_volume();
		showProgress_image_reverse();
		P2PHandler.getInstance()
				.getNpcSettings(idOrIp, contact.contactPassword);
		FisheyeSetHandler.getInstance().sGetLedState(idOrIp, contact.contactPassword);
		return view;
	}

	public void initComponent(View view) {
		change_video_format = (RelativeLayout) view
				.findViewById(R.id.change_video_format);
		video_format_radio = (LinearLayout) view
				.findViewById(R.id.video_format_radio);
		progressBar_video_format = (ProgressBar) view
				.findViewById(R.id.progressBar_video_format);

		change_volume = (RelativeLayout) view.findViewById(R.id.change_volume);
		video_voleme_seek = (RelativeLayout) view
				.findViewById(R.id.video_voleme_seek);
		progressBar_volume = (ProgressBar) view
				.findViewById(R.id.progressBar_volume);

		seek_volume = (SeekBar) view.findViewById(R.id.seek_volume);
		radio_one = (RadioButton) view.findViewById(R.id.radio_one);
		radio_two = (RadioButton) view.findViewById(R.id.radio_two);

		change_image_reverse = (RelativeLayout) view
				.findViewById(R.id.change_image_reverse);
		img_image_reverse = (ImageView) view
				.findViewById(R.id.image_reverse_img);
		progressbar_image_reverse = (ProgressBar) view
				.findViewById(R.id.progressBar_image_reverse);
		layout_reverse = (RelativeLayout) view
				.findViewById(R.id.change_image_reverse);
		r_light_control=(RelativeLayout)view
				.findViewById(R.id.r_light_control);
		light_control_img=(ImageView)view.findViewById(R.id.light_control_img);
		progressBar_light_control=(ProgressBar)view.findViewById(R.id.progressBar_light_control);
		r_light_control.setVisibility(View.GONE);
		radio_one.setOnClickListener(this);
		radio_two.setOnClickListener(this);
		change_image_reverse.setOnClickListener(this);
		r_light_control.setOnClickListener(this);
		seek_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				final int value = arg0.getProgress();
				progressBar_volume.setVisibility(RelativeLayout.VISIBLE);
				seek_volume.setEnabled(false);

				cur_modify_video_volume = value;
				switchVideoVolume(value);
			}

		});
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_GET_NPC_SETTINGS);

		filter.addAction(Constants.P2P.ACK_RET_SET_VIDEO_FORMAT);
		filter.addAction(Constants.P2P.RET_SET_VIDEO_FORMAT);
		filter.addAction(Constants.P2P.RET_GET_VIDEO_FORMAT);

		filter.addAction(Constants.P2P.ACK_RET_SET_VIDEO_VOLUME);
		filter.addAction(Constants.P2P.RET_SET_VIDEO_VOLUME);
		filter.addAction(Constants.P2P.RET_GET_VIDEO_VOLUME);

		filter.addAction(Constants.P2P.RET_GET_IMAGE_REVERSE);
		filter.addAction(Constants.P2P.ACK_VRET_SET_IMAGEREVERSE);
		filter.addAction(Constants.P2P.RET_LED_LIGHT);
		filter.addAction(Constants.P2P.SET_LED_LIGHT);
		filter.addAction(Constants.P2P.ACK_RET_GET_LIGHT);
		filter.addAction(Constants.P2P.ACK_RET_SET_LIGHT);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.P2P.RET_GET_VIDEO_FORMAT)) {
				int type = intent.getIntExtra("type", -1);
				if (type == Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL) {
					radio_one.setChecked(true);
				} else if (type == Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC) {
					radio_two.setChecked(true);
				}
				showVideoFormat();
				radio_one.setEnabled(true);
				radio_two.setEnabled(true);
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_VIDEO_FORMAT)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.VIDEO_FORMAT_SET.SETTING_SUCCESS) {
					if (cur_modify_video_format == Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL) {
						radio_one.setChecked(true);
					} else if (cur_modify_video_format == Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC) {
						radio_two.setChecked(true);
					}
					showVideoFormat();
					radio_one.setEnabled(true);
					radio_two.setEnabled(true);
					T.showShort(mContext, R.string.modify_success);
				} else {
					showVideoFormat();
					radio_one.setEnabled(true);
					radio_two.setEnabled(true);
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_VIDEO_VOLUME)) {
				int value = intent.getIntExtra("value", 0);
				seek_volume.setProgress(value);
				seek_volume.setEnabled(true);
				showVideoVolume();
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_VIDEO_VOLUME)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.VIDEO_VOLUME_SET.SETTING_SUCCESS) {
					seek_volume.setProgress(cur_modify_video_volume);
					seek_volume.setEnabled(true);
					showVideoVolume();
					T.showShort(mContext, R.string.modify_success);
				} else {
					seek_volume.setEnabled(true);
					showVideoVolume();
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_NPC_SETTINGS)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:get npc settings");
					P2PHandler.getInstance().getNpcSettings(idOrIp,
							contact.contactPassword);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_VIDEO_FORMAT)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my",
							"net error resend:set npc settings video format");
					switchVideoFormat(cur_modify_video_format);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_VIDEO_VOLUME)) {

				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my",
							"net error resend:set npc settings video volume");
					switchVideoVolume(cur_modify_video_volume);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_IMAGE_REVERSE)) {
				int type = intent.getIntExtra("type", -1);
				if (type == 0) {
					layout_reverse.setVisibility(RelativeLayout.VISIBLE);
					showImageview_image_reverse();
					img_image_reverse
							.setBackgroundResource(R.drawable.ic_checkbox_off);
					isOpenReverse = true;
				} else if (type == 1) {
					layout_reverse.setVisibility(RelativeLayout.VISIBLE);
					showImageview_image_reverse();
					img_image_reverse
							.setBackgroundResource(R.drawable.ic_checkbox_on);
					isOpenReverse = false;
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_VRET_SET_IMAGEREVERSE)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					if (isOpenReverse = true) {
						P2PHandler.getInstance().setImageReverse(idOrIp,
								contact.contactPassword, 1);
					} else {
						P2PHandler.getInstance().setImageReverse(idOrIp,
								contact.contactPassword, 0);
					}

				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
					if (isOpenReverse == true) {
						showImageview_image_reverse();
						img_image_reverse
								.setBackgroundResource(R.drawable.ic_checkbox_on);
						isOpenReverse = false;
					} else {
						showImageview_image_reverse();
						img_image_reverse
								.setBackgroundResource(R.drawable.ic_checkbox_off);
						isOpenReverse = true;
					}
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_LED_LIGHT)){
				byte [] data=intent.getByteArrayExtra("data");
				String iSrcID=String.valueOf(intent.getIntExtra("iSrcID", -1));
				byte boption=intent.getByteExtra("boption", (byte) -1);
				if(iSrcID.equals(idOrIp)){
					if(boption==Constants.FishBoption.MESG_GET_OK){
						for(int i=0;i<4;i++){
							if(data[i+3]==0){
								islightOn=true;
								break;
							}
						}
						if(islightOn){
							light_control_img.setBackgroundResource(R.drawable.ic_checkbox_on);
						}else{
							light_control_img.setBackgroundResource(R.drawable.ic_checkbox_off);
						}
						r_light_control.setVisibility(View.VISIBLE);
						showLightImg();
					}
				}
			}else if(intent.getAction().equals(Constants.P2P.SET_LED_LIGHT)){
				byte [] data=intent.getByteArrayExtra("data");
				String iSrcID=String.valueOf(intent.getIntExtra("iSrcID", -1));
				byte boption=intent.getByteExtra("boption", (byte) -1);
				if(iSrcID.equals(idOrIp)){
					if(boption==Constants.FishBoption.MESG_SET_OK){
					   if(islightOn){
						   light_control_img.setBackgroundResource(R.drawable.ic_checkbox_off); 
						   islightOn=false;
					   }else{
						   light_control_img.setBackgroundResource(R.drawable.ic_checkbox_on);
						   islightOn=true;
					   }
					   showLightImg();
					}
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_GET_LIGHT)){
				String contactId=intent.getStringExtra("contactId");
				int state=intent.getIntExtra("state", -1);
				if(state==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					FisheyeSetHandler.getInstance().sGetLedState(idOrIp, contact.contactPassword);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_LIGHT)){
				String contactId=intent.getStringExtra("contactId");
				int state=intent.getIntExtra("state", -1);
				if(state==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					if(islightOn){
						FisheyeSetHandler.getInstance().sSetLedState(idOrIp, contact.contactPassword, Constants.LightState.OFF);
					}else{
						FisheyeSetHandler.getInstance().sSetLedState(idOrIp, contact.contactPassword, Constants.LightState.ON);
					}
				}
			}
		}
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int i = view.getId();
		if (i == R.id.radio_one) {
			progressBar_video_format.setVisibility(RelativeLayout.VISIBLE);
			radio_one.setEnabled(false);
			radio_two.setEnabled(false);

			cur_modify_video_format = Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL;
			switchVideoFormat(Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL);

		} else if (i == R.id.radio_two) {
			progressBar_video_format.setVisibility(RelativeLayout.VISIBLE);
			radio_one.setEnabled(false);
			radio_two.setEnabled(false);

			cur_modify_video_format = Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC;
			switchVideoFormat(Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC);

		} else if (i == R.id.change_image_reverse) {
			showProgress_image_reverse();
			if (isOpenReverse == true) {
				P2PHandler.getInstance().setImageReverse(idOrIp,
						contact.contactPassword, 1);
			} else {
				P2PHandler.getInstance().setImageReverse(idOrIp,
						contact.contactPassword, 0);
			}

		} else if (i == R.id.r_light_control) {
			if (islightOn) {
				FisheyeSetHandler.getInstance().sSetLedState(idOrIp, contact.contactPassword, Constants.LightState.OFF);
			} else {
				FisheyeSetHandler.getInstance().sSetLedState(idOrIp, contact.contactPassword, Constants.LightState.ON);
			}
			showLightProgress();

		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}

	public void switchVideoVolume(final int toggle) {

		new DelayThread(Constants.SettingConfig.SETTING_CLICK_TIME_DELAY,
				new DelayThread.OnRunListener() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						P2PHandler.getInstance().setVideoVolume(idOrIp,
								contact.contactPassword, toggle);

					}
				}).start();
	}

	public void switchVideoFormat(final int toggle) {
		new DelayThread(Constants.SettingConfig.SETTING_CLICK_TIME_DELAY,
				new DelayThread.OnRunListener() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						P2PHandler.getInstance().setVideoFormat(idOrIp,
								contact.contactPassword, toggle);
					}
				}).start();
	}

	public void showProgress_image_reverse() {
		progressbar_image_reverse.setVisibility(ProgressBar.VISIBLE);
		img_image_reverse.setVisibility(ImageView.GONE);
	}

	public void showImageview_image_reverse() {
		progressbar_image_reverse.setVisibility(ImageView.GONE);
		img_image_reverse.setVisibility(ImageView.VISIBLE);
	}

	public void showVideoFormat() {
		change_video_format.setBackgroundResource(R.drawable.tiao_bg_up);
		progressBar_video_format.setVisibility(RelativeLayout.GONE);
		video_format_radio.setVisibility(RelativeLayout.VISIBLE);
	}

	public void showProgress_video_format() {
		change_video_format.setBackgroundResource(R.drawable.tiao_bg_single);
		progressBar_video_format.setVisibility(RelativeLayout.VISIBLE);
		video_format_radio.setVisibility(RelativeLayout.GONE);
	}

	public void showVideoVolume() {
		change_volume.setBackgroundResource(R.drawable.tiao_bg_up);
		video_voleme_seek.setVisibility(RelativeLayout.VISIBLE);
		progressBar_volume.setVisibility(RelativeLayout.GONE);
		seek_volume.setEnabled(true);
	}

	public void showProgress_volume() {
		progressBar_volume.setVisibility(RelativeLayout.VISIBLE);
		seek_volume.setEnabled(false);
	}
	public void showLightImg(){
		light_control_img.setVisibility(View.VISIBLE);
		progressBar_light_control.setVisibility(View.GONE);
		
	}
    public void showLightProgress(){
    	light_control_img.setVisibility(View.GONE);
		progressBar_light_control.setVisibility(View.VISIBLE);
    }
	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it = new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}
}
