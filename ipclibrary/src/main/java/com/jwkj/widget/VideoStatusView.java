package com.jwkj.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuowei.ipclibrary.R;

/**
 * Created by Administrator on 2016/9/28.
 */

public class VideoStatusView extends LinearLayout {
    public final static int NO_TF = 0;
    public final static int NO_VIDEO = 1;
    public final static int BEING_VIDEO = 2;
    int state = 1;
    private Context context;
    private ImageView iv_state;
    private TextView tx_rec;
    AnimationDrawable animationDrawable;
    AlphaAnimation alphaAnimation;

    public VideoStatusView(Context context, int ScrrenOrientation, int state) {
        super(context);
        this.context = context;
        this.state = state;
        initUI(context, ScrrenOrientation);
    }

    public VideoStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initUI(Context context, int ScrrenOrientation) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.video_status, this);
        iv_state = (ImageView) findViewById(R.id.iv_state);
        tx_rec = (TextView) findViewById(R.id.tx_rec);
        setSelfPadding(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoading();
    }

    public void startLoading() {
        switch (state) {
            case NO_TF:
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
                if (alphaAnimation != null) {
                    alphaAnimation.cancel();
                }
                iv_state.clearAnimation();
                iv_state.setBackgroundResource(R.drawable.no_tf);
                tx_rec.setVisibility(View.GONE);
                iv_state.setVisibility(View.VISIBLE);
                alphaAnimation = new AlphaAnimation(1, 0.1f);
                alphaAnimation.setDuration(750);
                alphaAnimation.setRepeatCount(-1);
                //设置动画的重复模式，默认是Restart,Reverse是反方向执行
                alphaAnimation.setRepeatMode(Animation.REVERSE);
                //通过控件启动动画
                iv_state.startAnimation(alphaAnimation);
                invalidate();
                break;
            case BEING_VIDEO:
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
                if (alphaAnimation != null) {
                    alphaAnimation.cancel();
                }
                iv_state.clearAnimation();
                iv_state.setBackgroundResource(R.drawable.rec_anim);
                iv_state.setVisibility(View.VISIBLE);
                tx_rec.setVisibility(View.VISIBLE);
                animationDrawable = (AnimationDrawable) iv_state.getBackground();
                animationDrawable.start();
                invalidate();
                break;
            case NO_VIDEO:
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
                if (alphaAnimation != null) {
                    alphaAnimation.cancel();
                }
                iv_state.setVisibility(View.GONE);
                tx_rec.setVisibility(View.GONE);
                invalidate();
                break;
            default:
                break;
        }
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSelfPadding(newConfig.orientation);
    }

    private void setSelfPadding(int ScrrenOrientation) {
        int padding_height, padding;
        if (ScrrenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (state == NO_TF) {
                padding_height = (int) getResources().getDimension(R.dimen.tf_margin_bottom);
                padding = 20;
            } else {
                padding_height = (int) getResources().getDimension(R.dimen.rec_margin_bottom);
                padding = 10;
            }
        } else {
            if (state == NO_TF) {
                padding_height = (int) getResources().getDimension(R.dimen.tf_margin_bottom) + (int) getResources().getDimension(R.dimen.p2p_monitor_bar_height);
                padding = 20;
            } else {
                padding_height = (int) getResources().getDimension(R.dimen.rec_margin_bottom) + (int) getResources().getDimension(R.dimen.p2p_monitor_bar_height);
                padding = 10;
            }
        }
        this.setPadding(padding, padding, padding, padding_height);
    }
}
