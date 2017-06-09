package com.jwkj.widget.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MonitorPanNormalImageView extends ImageView{
	private int[] imageSrc=new int[2];
	public MonitorPanNormalImageView(Context context,int[] imageSrc) {
		super(context);
		this.imageSrc=imageSrc;
		init(context);
	}
	
	private void init(Context context){
		setScaleType(ScaleType.CENTER_INSIDE);
		setImageDrawable(getTextDrawable());
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
	
	public void setCustomDrawable(int[] imageSrc){
		setScaleType(ScaleType.CENTER_INSIDE);
		this.imageSrc=imageSrc;
		setImageDrawable(getTextDrawable());
	}

}
