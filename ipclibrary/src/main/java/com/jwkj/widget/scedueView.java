package com.jwkj.widget;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jwkj.entity.DefenceWorkGroup;
import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.global.MyApp;
import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

/**
 * Created by dansesshou on 16/2/20.
 */
public class scedueView extends View {
    private final static int VIEW_STATE_TEXT = 0;
    private final static int VIEW_STATE_IMAGE = 1;
    private static final int counts = 7;
    private byte week = 6;
    private int[] weeks = new int[]{0, 0, 0, 0, 0, 0, 0};
    private final static int padding = 11;
    private int oneDayW = 0;
    private int oneDayH = 0;
    private Paint mPaint;
    private int TextColor = android.R.color.black;
    private int Gray = android.R.color.darker_gray;
    private int Light = android.R.color.holo_blue_light;
    private String[] days = new String[]{MyApp.app.getString(R.string.time_mon), MyApp.app.getString(R.string.time_tue)
    		, MyApp.app.getString(R.string.time_wen), MyApp.app.getString(R.string.time_thur), MyApp.app.getString(R.string.time_fri)
    		, MyApp.app.getString(R.string.time_sat), MyApp.app.getString(R.string.time_sun)};
    private int[] enable=new int[]{1,1,1,1,1,1,1};
    private int Realpadding;
    private Object group;
    private int ViewState = 0;
    
    private int ObjectType=0;

    public scedueView(Context context) {
        super(context);
        init(context);
    }

    public scedueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Realpadding = Utils.dip2px(context, padding);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public void setWorkGroup(Object group) {
    	this.group = group;
    	if(group instanceof WorkScheduleGroup){
    		ObjectType=0;
    		System.arraycopy(((WorkScheduleGroup) group).getDayInWeek(),0,weeks,0,weeks.length);
    	}else if(group instanceof DefenceWorkGroup) {
    		ObjectType=1;
    		System.arraycopy(((DefenceWorkGroup) group).getDayInWeek(),0,weeks,0,weeks.length);
		}
        invalidate();
    }
    public Object getWorkGroup(){
        return group;
    }
    public void setViewState(int state) {
        ViewState = state;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        oneDayW = getWidth() / counts;
        oneDayH = getHeight();
        if (ViewState == VIEW_STATE_TEXT) {
            if (group != null) {
            	if(ObjectType==0){
            		DrawGroupText(canvas, ((WorkScheduleGroup) group).getTimeText());
            	}else if(ObjectType==1){
            		DrawGroupText(canvas, ((DefenceWorkGroup) group).getTimeText());
            	}
            }
        } else if (ViewState == VIEW_STATE_IMAGE) {
            for (int i = 0; i < weeks.length; i++) {
                DrawOneDay(canvas, i, (byte) weeks[i]);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w=MeasureSpec.getSize(widthMeasureSpec);
        int h=MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 绘制文字
     *
     * @param canvas
     * @param time
     */
    private void DrawGroupText(Canvas canvas, String time) {
        Rect targetRect = new Rect(0, 0, getWidth(), oneDayH);
        mPaint.setColor(getResources().getColor(TextColor));
        mPaint.setTextSize(30);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        mPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(time, Realpadding, -fontMetrics.top, mPaint);
    }

    private void DrawOneDay(Canvas canvas, int position, byte dayState) {
        DrawOndayText(canvas, position);
        DrawOnedayRect(canvas, position, dayState);
    }

    private void DrawOndayText(Canvas canvas, int position) {
        Rect targetRect = new Rect(oneDayW * position, 0, oneDayW * (position + 1), oneDayH / 2);
        mPaint.setColor(getResources().getColor(TextColor));
        mPaint.setTextSize(30);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(days[position], targetRect.centerX(), baseline, mPaint);
    }

    private void DrawOnedayRect(Canvas canvas, int position, byte dayState) {
        Rect targetRect = new Rect(oneDayW * position + Realpadding, oneDayH / 3 * 2, oneDayW * (position + 1) - Realpadding, oneDayH / 6 * 5);
        if (dayState == 0) {
            mPaint.setColor(getResources().getColor(Gray));
        } else {
            mPaint.setColor(getResources().getColor(Light));
        }
        canvas.drawRect(targetRect, mPaint);
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	if (scedueViewListener==null) {
			return super.onTouchEvent(event);
		}
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x=(int)event.getX();
			isRunListener(x);
			break;
		default:
			break;
		}
    	return true;
    }
    
    public void isRunListener(int x){
    	for (int i = 0; i < weeks.length; i++) {
			if (x>oneDayW*i+Realpadding&&x<oneDayW*(i+1)-Realpadding) {
				if (weeks[i]==0) {
					scedueViewListener.changeWorkGroupState(1, i);
				}else {
					scedueViewListener.changeWorkGroupState(0, i);
				}
			}
		}
    }
    
    private ScedueViewListener scedueViewListener;
    public void setScedueViewListener(ScedueViewListener scedueViewListener){
    	this.scedueViewListener=scedueViewListener;
    }
    public interface ScedueViewListener{
    	void changeWorkGroupState(int state, int position);
    }
    
    
}
