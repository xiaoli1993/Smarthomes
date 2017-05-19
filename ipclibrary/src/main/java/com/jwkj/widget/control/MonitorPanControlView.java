package com.jwkj.widget.control;

import com.nuowei.ipclibrary.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.widget.ImageView;

public class MonitorPanControlView extends ImageView{
	public final static int ControlType_Normal=0;
	public final static int ControlType_Rect=1;
	public final static int ControlType_RECT_Normal=2;
	public final static int ControlType_RECT_Inis=3;//透明背景
	public final static int ControlType_Special=4;//透明背景
	private int[] imageSrc=new int[2];
	private int Type=0;
	public MonitorPanControlView(Context context,int[] imageSrc) {
		this(context,imageSrc,0);
	}
	
	public MonitorPanControlView(Context context,int[] imageSrc,int type) {
		super(context);
		this.imageSrc=imageSrc;
		Type=type;
		init(context);
	}
	private int mark;
	public MonitorPanControlView(Context context,int[] imageSrc,int type,int mark) {
		super(context);
		this.imageSrc=imageSrc;
		Type=type;
		init(context);
		this.mark=mark;
	}
	
	
	private void init(Context context){
		setScaleType(ScaleType.CENTER_INSIDE);
		if(Type==ControlType_Normal){
			setImageDrawable(getTextDrawable(imageSrc));
		}else if(Type==ControlType_RECT_Normal){
			setImageDrawable(getTextDrawable(1));
		}else if(Type==ControlType_RECT_Inis){
			setImageDrawable(getTextDrawable(2));
		}else{
			setImageDrawable(getTextDrawable());
		}
	}
	
	private Drawable getTextDrawable(int[] imageSrc){
		Bitmap bitmapNormal=BitmapFactory.decodeResource(getResources(), imageSrc[0]);
		Bitmap bitmapPress=BitmapFactory.decodeResource(getResources(), imageSrc[1]);
		RoundDrawable draNormal=new RoundDrawable(getContext().getResources().getColor(R.color.half_alpha), bitmapNormal);
		RoundDrawable draPress=new RoundDrawable(getContext().getResources().getColor(R.color.halhal_eight), bitmapPress);
		StateListDrawable drawable = new StateListDrawable();
		//如果要设置莫项为false，在前面加负号 ，比如android.R.attr.state_focesed标志true，-android.R.attr.state_focesed就标志false  
	    drawable.addState(new int[]{android.R.attr.state_focused}, draPress);  
	    drawable.addState(new int[]{android.R.attr.state_pressed}, draPress);  
	    drawable.addState(new int[]{android.R.attr.state_selected}, draPress);  
	    drawable.addState(new int[]{}, draNormal);//默认
		return drawable;
	}
	
	private Drawable getTextDrawable(){
		Bitmap bitmapNormal=BitmapFactory.decodeResource(getResources(), imageSrc[0]);
		Bitmap bitmapPress=BitmapFactory.decodeResource(getResources(), imageSrc[1]);
		RoundDrawable draNormal=new RoundDrawable( bitmapNormal);
		RoundDrawable draPress=new RoundDrawable( bitmapPress);
		StateListDrawable drawable = new StateListDrawable();
		//如果要设置莫项为false，在前面加负号 ，比如android.R.attr.state_focesed标志true，-android.R.attr.state_focesed就标志false  
	    drawable.addState(new int[]{android.R.attr.state_focused}, draPress);  
	    drawable.addState(new int[]{android.R.attr.state_pressed}, draPress);  
	    drawable.addState(new int[]{android.R.attr.state_selected}, draPress);  
	    drawable.addState(new int[]{}, draNormal);//默认
		return drawable;
	}
	//背景变色，图标不变色，背景方的
	private Drawable getTextDrawable(int type){
		Bitmap bitmapNormal=BitmapFactory.decodeResource(getResources(), imageSrc[0]);
		Bitmap bitmapPress=BitmapFactory.decodeResource(getResources(), imageSrc[1]);
		RoundDrawable draNormal;
		RoundDrawable draPress;
		if(type==1){
			draNormal=new RoundDrawable(getContext().getResources().getColor(R.color.half_alpha), bitmapNormal,0);
			draPress=new RoundDrawable(getContext().getResources().getColor(R.color.halhal_eight), bitmapPress,0);
		}else{
			draNormal=new RoundDrawable(Color.TRANSPARENT,bitmapNormal,0);
			draPress=new RoundDrawable( Color.TRANSPARENT,bitmapPress,0);
		}
		StateListDrawable drawable = new StateListDrawable();
		//如果要设置莫项为false，在前面加负号 ，比如android.R.attr.state_focesed标志true，-android.R.attr.state_focesed就标志false  
	    drawable.addState(new int[]{android.R.attr.state_focused}, draPress);  
	    drawable.addState(new int[]{android.R.attr.state_pressed}, draPress);  
	    drawable.addState(new int[]{android.R.attr.state_selected}, draPress);  
	    drawable.addState(new int[]{}, draNormal);//默认
		return drawable;
	}
	
	
	public void setCustomDrawable(int[] imageSrc){
		setScaleType(ScaleType.CENTER_INSIDE);
		setImageDrawable(getTextDrawable(imageSrc));
	}
	
	public void setCustomType(Context context,int type){
		this.Type=type;
		init(context);
	}
}
