package com.jwkj.widget.control;

import java.lang.ref.WeakReference;

import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MonitorNumberTextView extends TextView {
	private String Text;
	private Rect targetRect = null;
	private long AnimalTime = 6 * 1000;
	private int ViewH_L_DP = 23;
	private int ViewH_P_DP = 20;
	private Context mContext;
	private Paint mPaint;
	private Handler mAnimalHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (TextView != null && TextView.getVisibility() == View.VISIBLE) {
				TextView.startAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.slide_out_to_top));
				TextView.setVisibility(View.GONE);
			}
		};
	};
	private Runnable hindeRunable = new Runnable() {

		@Override
		public void run() {
			mAnimalHandler.sendEmptyMessage(1);
		}
	};
	private MonitorNumberTextView TextView;
	private GestureDetector mGestureDetector;

	public MonitorNumberTextView(Context context) {
		super(context);
		this.mContext = context;
		ViewH_P_DP = Utils.dip2px(mContext, ViewH_P_DP);
		init(context);
		TextView = this;
	}

	@SuppressLint("NewApi")
	private void init(Context context) {
		if (Utils.isSpecification(Build.VERSION_CODES.JELLY_BEAN)) {
			this.setBackground(getTextDrawable());
		} else {
			this.setBackgroundDrawable(getTextDrawable());
		}
		changeLayoutParames(Configuration.ORIENTATION_PORTRAIT);
		mGestureDetector = new GestureDetector(context,
				new LearnGestureListener());
		setTextColor(Utils.getColorByResouce(R.color.white));
		mPaint = getPaint();
		mPaint.setColor(Utils.getColorByResouce(R.color.white));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		targetRect = new Rect(left, top, right, bottom);
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getText() != null) {
			Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
			int baseline = (targetRect.bottom + targetRect.top
					- fontMetrics.ascent + fontMetrics.descent)
					/ 2 - fontMetrics.bottom;
			mPaint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(getText().toString(), targetRect.centerX(),
					baseline, mPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	private Drawable getTextDrawable() {
		RoundDrawable draNormal = new RoundDrawable(R.color.monitor_number_bac,R.color.monitor_number_bac,0,0);
		// RoundDrawable draPress=new
		// RoundDrawable(getContext().getResources().getColor(R.color.halhal_eight),
		// 0);
		// StateListDrawable drawable = new StateListDrawable();
		// //如果要设置莫项为false，在前面加负号
		// ，比如android.R.attr.state_focesed标志true，-android.R.attr.state_focesed就标志false
		// drawable.addState(new int[]{android.R.attr.state_focused}, draPress);
		// drawable.addState(new int[]{android.R.attr.state_pressed}, draPress);
		// drawable.addState(new int[]{android.R.attr.state_selected},
		// draPress);
		// drawable.addState(new int[]{}, draNormal);//默认
		return draNormal;
	}

	public String getText() {
		return Text;
	}

	public void setNUmberText(String text) {
		Text = text;
		postInvalidate();
	}

	public void showNumbers(String text) {
		setNUmberText(text);
		if (this.getVisibility() != View.VISIBLE) {
			// 执行动画
			this.setVisibility(View.VISIBLE);
			this.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.slide_in_top));
			mAnimalHandler.postDelayed(hindeRunable, AnimalTime);
		}
		this.bringToFront();
	}

	public void releaseTextView() {
		if (mAnimalHandler != null) {
			mAnimalHandler.removeCallbacks(hindeRunable);
		}
		this.setVisibility(View.GONE);
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		changeLayoutParames(newConfig.orientation);
		super.onConfigurationChanged(newConfig);
	}

	public void changeLayoutParames(int orientation) {
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, Utils.dip2px(getContext(),
							ViewH_L_DP));
			p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			setTextSize(10);
			setLayoutParams(p);
		} else {
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, ViewH_P_DP);
			p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			setTextSize(10);
			setLayoutParams(p);
		}
		if(this.getVisibility()!=View.VISIBLE){
			releaseTextView();
			showNumbers(Text);
		}
		this.bringToFront();
	}

	private class LearnGestureListener extends GestureDetector.SimpleOnGestureListener {
		
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			mAnimalHandler.sendEmptyMessage(1);
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getY() - e2.getY() > (ViewH_P_DP / 2)
					&& Math.abs(velocityX) > (ViewH_P_DP / 4)) {
				mAnimalHandler.sendEmptyMessage(1);
			}
			return true;
		}
	}
}
