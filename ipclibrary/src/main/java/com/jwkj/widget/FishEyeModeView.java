package com.jwkj.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

import java.util.Locale;

/**
 * Created by dxs on 2015/12/30.
 */
public class FishEyeModeView extends TextView {
    private final static String TAG="FishEyeModeView";
    private final static int FISHMODE_SLEEP=3;
    private final static int FISHMODE_OUT=2;
    private final static int FISHMODE_HOME=1;
    private final float Contontpadding = Utils.dip2px(getContext(), 0);
    private final float ModeToTextInnerPadding = Utils.dip2px(getContext(), 4);
    private final float ModeToTextOutPadding = Utils.dip2px(getContext(), 0);
    private int ModeTextSize = Utils.dip2px(getContext(), 12);
    private int DeviceNameTextSize = Utils.dip2px(getContext(), 10);
    private float Ox = 0.0f;
    private float Oy = 0.0f;
    private float r = 0.0f;
    private Paint mPaint;
    private Context mContext;
    private int draw_up_id;
    private Bitmap modeBitmap,progressBitmap;
    private String TxMode = "";
    private String TxName = "";
    private float ImageTop=0.0f;
    private ArcAnimal animal;
    private float degree=0;
    private int ModeStatde=-1;

    public FishEyeModeView(Context context) {
        super(context);
        mContext = context;
    }

    public FishEyeModeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FishEyeModeView);
        draw_up_id = a.getResourceId(R.styleable.FishEyeModeView_draw_up_id, R.mipmap.ic_launcher);
        TxMode = a.getString(R.styleable.FishEyeModeView_text_mode_id);
        TxName=a.getString(R.styleable.FishEyeModeView_text_name_id);
        a.recycle();
    }

    private void init() {
        animal=new ArcAnimal();
        animal.setRepeatCount(-1);
        animal.setRepeatMode(Animation.RESTART);
        animal.setInterpolator(new LinearInterpolator());
        animal.setDuration(800);
        progressBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.mode_loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoading();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = getPaint();
        DrawCircle(canvas);
        DrawModeImage(canvas);
        DrawModeText(canvas);
//        DrawDeviceNameText(canvas);
        canvas.save();
        DraProgress(canvas);
        canvas.restore();
        super.onDraw(canvas);
    }
    private void DraProgress(Canvas canvas) {
        if (progressBitmap != null&&ModeStatde==-1) {
            float left = (getWidth() - progressBitmap.getWidth()) / 2;
            float top=(2*r-progressBitmap.getHeight())/2;
            canvas.rotate(degree*360,r,r);
            canvas.drawBitmap(progressBitmap, left, top, mPaint);
        }
    }

    private void DrawCircle(Canvas canvas) {
        Ox = getWidth() / 2;
        r = Ox - Contontpadding;
        Oy = r;
        mPaint.setColor(getResources().getColor(R.color.main_titlebar));
        canvas.drawCircle(Ox, Oy, r, mPaint);
    }

    private void DrawModeImage(Canvas canvas) {
        if (modeBitmap != null) {
            float left = (getWidth() - modeBitmap.getWidth()) / 2;
            ImageTop = (2 * r - modeBitmap.getHeight() - ModeToTextInnerPadding - ModeTextSize) / 2;
            canvas.drawBitmap(modeBitmap, left, ImageTop, mPaint);
        }

    }

    private void DrawModeText(Canvas canvas) {
        Rect targetRect = new Rect(0, (int) ((2*r)-ImageTop-ModeTextSize), getWidth(), (int)((2*r)-ImageTop));
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.endsWith("zh")||language.endsWith("zh_TW")||language.endsWith("zh_HK")){
        	 ModeTextSize = Utils.dip2px(getContext(), 12);
        }else{
        	 ModeTextSize = Utils.dip2px(getContext(), 8);
        }
        if (TxMode.length() > 0) {
            mPaint.setColor(Color.RED);
            mPaint.setTextSize(ModeTextSize);
            mPaint.setColor(Color.WHITE);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.ascent + fontMetrics.descent) / 2- fontMetrics.bottom;
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(TxMode, targetRect.centerX(), baseline, mPaint);
        }
    }
    private void DrawDeviceNameText(Canvas canvas) {
        Rect targetRect = new Rect(0, (int) (2*r), getWidth(), getHeight());
        if (TxName.length() > 0) {
            mPaint.setTextSize(DeviceNameTextSize);
            mPaint.setColor(getResources().getColor(R.color.device_name_txcolor));
            mPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.ascent + fontMetrics.descent) / 2- fontMetrics.bottom;
            canvas.drawText(TxName, targetRect.centerX(), baseline, mPaint);
        }

    }

    public void setTextDeviceName(String text){
        TxName=text;
        invalidate();
    }

    public void setTextDeviceName(int R){
        setTextDeviceName(mContext.getResources().getString(R));
    }

    public int getModeStatde() {
        return ModeStatde;
    }

    public void setModeStatde(int modeStatde) {
        ModeStatde = modeStatde;
        startLoading();
    }

    private void startLoading(){
            switch (ModeStatde){
                case -1:
                    if(animal==null){
                        init();
                    }
                    modeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mode_home);
                    TxMode="...";
                    this.startAnimation(animal);
                    break;
                case FISHMODE_HOME:
                    modeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mode_home);
                    TxMode=getResources().getString(R.string.mode_home);
                    invalidate();
                    break;
                case FISHMODE_OUT:
                    modeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mode_out);
                    TxMode=getResources().getString(R.string.mode_out);
                    invalidate();
                    break;
                case FISHMODE_SLEEP:
                    modeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mode_sleep);
                    TxMode=getResources().getString(R.string.mode_sleep);
                    invalidate();
                    break;
            }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public class ArcAnimal extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            degree=interpolatedTime;
            postInvalidate();
        }

    }
    
}
