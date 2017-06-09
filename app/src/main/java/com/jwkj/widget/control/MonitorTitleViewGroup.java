package com.jwkj.widget.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

public class MonitorTitleViewGroup extends ViewGroup{
	private int[] backIDs=new int[]{R.drawable.back_white,R.drawable.back_white_p};
	private int left=17;
	public MonitorTitleViewGroup(Context context) {
		super(context);
		init(context);
	}

	@SuppressLint("NewApi")
	private void init(Context context) {
		if(Utils.isSpecification(Build.VERSION_CODES.JELLY_BEAN)){
			this.setBackground(getTextDrawable());
		}else{
			this.setBackgroundDrawable(getTextDrawable());
		}
		left=Utils.dip2px(getContext(), left);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(getContext(), 40));
		p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		setLayoutParams(p);
		MonitorPanControlView view=new MonitorPanControlView(getContext(),backIDs,MonitorPanControlView.ControlType_Rect);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listner!=null){
					listner.onBackClick(v);
				}
			}
		});
		addView(view);
	}

	private Drawable getTextDrawable() {
		return new RoundDrawable(R.color.black_night, R.color.alpha, 0,0);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		View v=getChildAt(0);
		v.layout(left, t, left+80, b);
	}
	
	public interface onTitleClickListner{
		void onBackClick(View view);
	}
	private onTitleClickListner listner;
	public void setOnTitleClickListner(onTitleClickListner listner){
		this.listner=listner;
	}
}
