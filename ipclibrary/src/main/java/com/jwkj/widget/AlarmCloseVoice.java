package com.jwkj.widget;

import com.nuowei.ipclibrary.R;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class AlarmCloseVoice extends RelativeLayout {
	public final static int state_nurmal=0;
	public final static int state_loading=1;
	public final static int state_closed=2;
	ImageView alarming,img_close;
	AnimationDrawable animationDrawable;
	ProgressBar progress_close;
	public int state=0;
	public String deviceId,password;
	public int type=0;
	public AlarmCloseVoice(Context context,String deviceId) {
		super(context);
		this.deviceId=deviceId;
		initViews(context);
		// TODO Auto-generated constructor stub
	}
	public AlarmCloseVoice(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public AlarmCloseVoice(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.alarm_voice_close, this);
//		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		alarming = (ImageView) findViewById(R.id.alarming);
		img_close=(ImageView)findViewById(R.id.img_close);
		progress_close=(ProgressBar)findViewById(R.id.progress_close);
		animationDrawable=(AnimationDrawable) alarming.getBackground();
		animationDrawable.start();
		alarming.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(listener!=null){
					listener.onclose(deviceId,type);
				}
			}
		});

	}
	public void setState(int alarmstate){
		state=alarmstate;
	}
	public void settype(int closeType){
		type=closeType;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		startLoading();
	}
    public void startLoading(){
    	switch (state) {
		case state_nurmal:
			progress_close.setVisibility(View.GONE);
			alarming.setVisibility(View.VISIBLE);
			img_close.setVisibility(View.VISIBLE);
			 invalidate();
			break;
		case state_loading:
			progress_close.setVisibility(View.VISIBLE);
			alarming.setVisibility(View.GONE);
			img_close.setVisibility(View.GONE);
			invalidate();
			break;
		case state_closed:
			progress_close.setVisibility(View.GONE);
			alarming.setVisibility(View.GONE);
			img_close.setVisibility(View.GONE);
			invalidate();
			break;
		default:
			break;
		}
    }
	public void setcloseClickListener(closeClickListener listener){
		this.listener=listener;
	}
	private closeClickListener listener;
	public interface closeClickListener{
		void onclose(String deviceId, int type);
	}
}
