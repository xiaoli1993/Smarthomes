package com.jwkj.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class GuideRelayout extends RelativeLayout {
	public GuideRelayout(Context context) {
		super(context);
	}

	public GuideRelayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAttachedToWindow() {
		if (listner != null) {
			listner.onGuideShow(this);
		}
		super.onAttachedToWindow();
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (listner != null) {
			listner.onLayoutState(this, newConfig.orientation);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (listner != null) {
			listner.onGuideDismiss(this);
		}
		super.onDetachedFromWindow();
	}

	public interface  GuideListner<T extends GuideRelayout> {
		void onGuideShow(T view);

		void onGuideDismiss(T view);

		void onLayoutState(T view, int state);
	}

	private GuideListner<GuideRelayout> listner;

	public void setGuideListner(GuideListner<GuideRelayout> listner) {
		this.listner = listner;
	}

}
