package com.jwkj.widget.control;

import com.jwkj.global.MyApp;
import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RoundSelectorTextView extends TextView implements View.OnClickListener{
	private int round=20;
	private int[] color=new int[]{0xffffffff, 0x88ffffff, 0x88ffffff, 0x88ffffff};
	Rect targetRect =null;
	private Paint mPaint;
	private boolean isShowControl=true;
	public final static int SHOWTYPE_MUST_NORMAL=0;
	public final static int SHOWTYPE_MUST_SHOW=1;
	public final static int SHOWTYPE_MUST_HIDEN=2;
	public RoundSelectorTextView(Context context) {
		super(context);
		init(context);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void init(Context context) {
		setTextSize(12);
		setText(R.string.hide);
		if(Utils.isSpecification(Build.VERSION_CODES.JELLY_BEAN)){
			this.setBackground(getTextDrawable());
		}else{
			this.setBackgroundDrawable(getTextDrawable());
		}
		this.setTextColor(createColorStateList(color[0], color[1], color[2], color[3]));
		mPaint=getPaint();
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setColor(color[0]);
		setOnClickListener(this);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(Utils.dip2px(getContext(), 53), Utils.dip2px(getContext(), 26));
		p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		p.leftMargin=Utils.dip2px(getContext(), 21);
		int h=Math.min(MyApp.SCREENHIGHT, MyApp.SCREENWIGHT)/10;
		p.bottomMargin=h/2+Math.max(0,(h*9/5-Utils.dip2px(getContext(), MonitorPanView.ItemW_DP))/2);
		setLayoutParams(p);
	}
	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
		init(getContext());
	}
	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		targetRect = new Rect(0, 0, getWidth(), getHeight());
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if(getText()!=null){
			Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
	        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.ascent + fontMetrics.descent) / 2- fontMetrics.bottom;
	        canvas.drawText(getText().toString(), targetRect.centerX(), baseline,mPaint);
		}
	}
	
	private void setSelectColor(int normal, int pressed, int focused, int unable){
		color[0]=normal;
		color[1]=pressed;
		color[2]=focused;
		color[3]=unable;
		init(getContext());
	}

	private Drawable getTextDrawable(){
		RoundDrawable draNormal=new RoundDrawable(getContext().getResources().getColor(R.color.half_alpha), round);
		RoundDrawable draPress=new RoundDrawable(getContext().getResources().getColor(R.color.halhal_eight), round);
		StateListDrawable drawable = new StateListDrawable();
		//如果要设置莫项为false，在前面加负号 ，比如android.R.attr.state_focesed标志true，-android.R.attr.state_focesed就标志false  
	    drawable.addState(new int[]{android.R.attr.state_focused}, draPress);  
	    drawable.addState(new int[]{android.R.attr.state_pressed}, draPress);  
	    drawable.addState(new int[]{android.R.attr.state_selected}, draPress);  
	    drawable.addState(new int[]{}, draNormal);//默认
		return drawable;
	}
	
	 /** 对TextView设置不同状态时其文字颜色。 */  
    private ColorStateList createColorStateList(int normal, int pressed, int focused, int unable) {  
        int[] colors = new int[] { pressed, focused, normal, focused, unable, normal };  
        int[][] states = new int[6][];
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };  
        states[1] = new int[] { android.R.attr.state_enabled, android.R.attr.state_focused };  
        states[2] = new int[] { android.R.attr.state_enabled };  
        states[3] = new int[] { android.R.attr.state_focused };  
        states[4] = new int[] { android.R.attr.state_window_focused };  
        states[5] = new int[] {};  
        ColorStateList colorList = new ColorStateList(states, colors);  
        return colorList;  
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPaint.setColor(color[1]);
			postInvalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mPaint.setColor(color[0]);
			postInvalidate();
			break;
		default:
			break;
		}
    	return super.onTouchEvent(event);
    }
    private View[] LinkedViews;
    public View[] getLinkedView() {
		return LinkedViews;
	}

	public void setLinkedView(View[] linkedView) {
		LinkedViews = linkedView;
	}

	@Override
	public void onClick(View v) {
		changeVisibly();
	}
	
	private void changeVisibly(){
		if(isShowControl){
			HindenControl();
		}else{
			ShowControl();
		}
	}
	
	private void HindenControl(){
		for(int i=0;i<LinkedViews.length;i++){
			LinkedViews[i].setVisibility(View.INVISIBLE);
		}
		isShowControl=false;
		setText(R.string.display);
		bringToFront();
	}
	private void ShowControl(){
		for(int i=0;i<LinkedViews.length;i++){
			LinkedViews[i].setVisibility(View.VISIBLE);
			LinkedViews[i].bringToFront();
		}
		isShowControl=true;
		setText(R.string.hide);
		bringToFront();
	}
	
	public void showVisible(int ShowType){
		if(ShowType==SHOWTYPE_MUST_SHOW){
			ShowControl();
			this.setVisibility(View.VISIBLE);
		}else if(ShowType==SHOWTYPE_MUST_HIDEN){
			HindenControl();
			this.setVisibility(View.INVISIBLE);
		}else{
			if(isShowControl){
				ShowControl();
			}else{
				HindenControl();
			}
		}
	}

}
