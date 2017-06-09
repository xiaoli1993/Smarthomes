package com.jwkj.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import com.jwkj.utils.Utils;
import com.jwkj.widget.control.RoundDrawable;
import com.nuowei.smarthome.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class MoniterTimeTextview extends TextView{
	private final static int TextSize=12;
	private long time;
	private String TimeStr="";
	private SimpleDateFormat sdf = null;
	private TimeZone timeZone=null;
	private Rect textRect=new Rect();
	private Date d;
	private Paint mPaint;
	private float Texthight=18f;
	public MoniterTimeTextview(Context context) {
		super(context);
		init(context);
	}

	public MoniterTimeTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	@SuppressLint("NewApi")
	private void init(Context context) {
		if(Utils.isSpecification(Build.VERSION_CODES.JELLY_BEAN)){
			this.setBackground(getTextDrawable());
		}else{
			this.setBackgroundDrawable(getTextDrawable());
		}
		Texthight=Utils.dip2px(getContext(), Texthight);
		sdf = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
		timeZone=new SimpleTimeZone(0, "UTC");
		sdf.setTimeZone(timeZone);
		d = new Date(time);
		setTextColor(Color.WHITE);
		setTextSize(Utils.dip2px(getContext(), TextSize));
		mPaint=getPaint();
		mPaint.setColor(context.getResources().getColor(R.color.white));
		mPaint.setTextSize(Utils.dip2px(getContext(), TextSize));
		setPadding(Utils.dip2px(getContext(), 8), 3, Utils.dip2px(getContext(), 8), 3);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, (int) Texthight);
		p.leftMargin=Utils.dip2px(context, 5);
		p.topMargin=Utils.dip2px(context, 11);
		setLayoutParams(p);
	}
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		d.setTime((long)(time)/1000);
		TimeStr=sdf.format(d);
		setText(TimeStr);
	}
	
	private void drawTime(Canvas canvas){
		if(TimeStr!=null&&TimeStr.length()>0){
			mPaint.getTextBounds(TimeStr, 0, TimeStr.length(), textRect);
		    FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();  
		    int baseline = (getHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		    canvas.drawText(TimeStr, textRect.centerX(), baseline, mPaint);
		}
	}
	
	private Drawable getTextDrawable() {
		return new RoundDrawable(R.color.black_40, R.color.halhal_eight, 0,1);
	}
	
}
