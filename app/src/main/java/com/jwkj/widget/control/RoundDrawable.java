package com.jwkj.widget.control;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

import com.jwkj.utils.Utils;
import com.nuowei.smarthome.MyApplication;

public class RoundDrawable extends Drawable{
	private static final int DrawableTYPE_back=0;
	private static final int DrawableTYPE_bitmap_circle=1;
	private static final int DrawableTYPE_back_shader=2;
	private static final int DrawableTYPE_bitmap_normal=3;
	private static final int DrawableTYPE_bitmap_rect=4;
	private int round,color;
	private RectF rectF;
	private Paint mPaint;
	private Paint mBackPaint;
	
	private Bitmap mBitmap ;
	private int mWidth;
	
	private int DrawableType=0;
	private int DrawableCircleR=23;
	
	private int startColor;
	private int endColor;
	private LinearGradient shader;
	private int oritation;
	public RoundDrawable(int color,int round)  
    {  
        this.color=color;
        this.round=round;
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        DrawableType=DrawableTYPE_back;
    }
	
	public RoundDrawable(int color,Bitmap bitmap){
		this.color=color;
		this.mBitmap=bitmap;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBackPaint= new Paint();
        mBackPaint.setAntiAlias(true);
        mBackPaint.setColor(color);
        mWidth = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
//      DrawableCircleR=Math.max(mWidth/2, Utils.dip2px(MyApp.app, DrawableCircleR));
        DrawableCircleR=Utils.dip2px(MyApplication.app, DrawableCircleR);
        DrawableType=DrawableTYPE_bitmap_circle;
	}
	public RoundDrawable(int color,Bitmap bitmap,int round){
		this.color=color;
		this.mBitmap=bitmap;
		this.round=round;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBackPaint= new Paint();
        mBackPaint.setAntiAlias(true);
        mBackPaint.setColor(color);
        mWidth = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
//      DrawableCircleR=Math.max(mWidth/2, Utils.dip2px(MyApp.app, DrawableCircleR));
        DrawableType=DrawableTYPE_bitmap_rect;
	}
	
	public RoundDrawable(Bitmap bitmap){
		this.mBitmap=bitmap;
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
		DrawableType=DrawableTYPE_bitmap_normal;
	}
	
	public RoundDrawable(int startColor,int endColor,int round,int oritation){
		this.startColor=Utils.getColorByResouce(startColor);
		this.endColor=Utils.getColorByResouce(endColor);
        this.round=round;
        this.oritation=oritation;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(shader);
        DrawableType=DrawableTYPE_back_shader;
	}
	
	
	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		rectF=new RectF(left, top, right, bottom);
		if(oritation==0){
			shader = new LinearGradient(0, 0, rectF.top, rectF.bottom, new int[]{startColor,endColor},null, TileMode.MIRROR);
		}else{
			shader = new LinearGradient(0, 0, rectF.right, rectF.top, new int[]{startColor,endColor},null, TileMode.MIRROR);
		}
		
	}

	@Override
	public void draw(Canvas canvas) {
		if(DrawableType==DrawableTYPE_back){
			canvas.drawRoundRect(rectF, round, round, mPaint);
		}else if(DrawableType==DrawableTYPE_bitmap_circle){
			canvas.drawCircle(rectF.centerX(), rectF.centerY(), DrawableCircleR, mBackPaint);
			canvas.drawBitmap(mBitmap, rectF.centerX()-mBitmap.getWidth()/2, rectF.centerY()-mBitmap.getHeight()/2, mPaint);
		}else if(DrawableType==DrawableTYPE_back_shader){
			mPaint.setShader(shader);
			canvas.drawRoundRect(rectF, round, round, mPaint);
		}else if(DrawableType==DrawableTYPE_bitmap_rect){
			canvas.drawRoundRect(rectF, round, round, mBackPaint);
			canvas.drawBitmap(mBitmap, rectF.centerX()-mBitmap.getWidth()/2, rectF.centerY()-mBitmap.getHeight()/2, mPaint);
		}else if(DrawableType==DrawableTYPE_bitmap_normal){
			canvas.drawBitmap(mBitmap, rectF.centerX()-mBitmap.getWidth()/2, rectF.centerY()-mBitmap.getHeight()/2, mPaint);
		}
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);  
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
		
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

}
