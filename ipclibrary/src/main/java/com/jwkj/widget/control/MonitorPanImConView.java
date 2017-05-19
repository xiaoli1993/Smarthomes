package com.jwkj.widget.control;

import java.util.ArrayList;
import java.util.List;

import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.data.Contact;
import com.jwkj.global.MyApp;
import com.jwkj.utils.Utils;
import com.jwkj.widget.control.MonitorPanView.onMonitorPanClickListner;
import com.nuowei.ipclibrary.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class MonitorPanImConView extends ViewGroup {
	private int[][] controlIds_360 = new int[][] {
			{ R.drawable.moniter_pan_four, R.drawable.moniter_pan_four },
			{ R.drawable.moniter_pan_wan, R.drawable.moniter_pan_wan },
			{ R.drawable.moniter_pan_circle, R.drawable.moniter_pan_circle },
			{ R.drawable.moniter_pan_normal, R.drawable.moniter_pan_normal } };
	
	private int[][] controlIds_se_360 = new int[][] {
			{ R.drawable.moniter_pan_four_s, R.drawable.moniter_pan_four_s },
			{ R.drawable.moniter_pan_wan_s, R.drawable.moniter_pan_wan_s},
			{ R.drawable.moniter_pan_circle_s, R.drawable.moniter_pan_circle_s},
			{ R.drawable.moniter_pan_normal_s, R.drawable.moniter_pan_normal_s} };
	
	private int[][] controlIds_180 = new int[][] {
			{ R.drawable.moniter_pan_wan, R.drawable.moniter_pan_wan },
			{ R.drawable.moniter_pan_circle, R.drawable.moniter_pan_circle } };
	
	private int[][] controlIds_se_180 = new int[][] {
			{ R.drawable.moniter_pan_wan_s, R.drawable.moniter_pan_wan_s},
			{ R.drawable.moniter_pan_circle_s, R.drawable.moniter_pan_circle_s} };
	
	private int[] FirstViews = new int[] { R.drawable.monitor_pan_position,
			R.drawable.monitor_pan_position_p };
	private List<MonitorPanNormalImageView> views = new ArrayList<MonitorPanNormalImageView>();
	private Context mContext;
	private int ItemLeftPadding = 0;
	private float ItemH = 44f;
	private float ItemW = 46f;
	private float FirstItemH=46f;
	private boolean isShowControl=true;
	private int selectPosition=3;
    private MonitorPanControlView viewFirst;
    public static final int PANORMTYPE_360=0;//360全景相机
    public static final int PANORMTYPE_180=1;//180全景相机
    private int DisplayType=-1;
    private Contact mContact;
    private int[][] DisplayIds;
    private int[][] DisplayIds_se;
	public MonitorPanImConView(Context context,Contact mContact) {
		super(context);
		this.mContact=mContact;
		setContact(mContact);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		ItemLeftPadding = Utils.dip2px(mContext, ItemLeftPadding);
		SetCircleBackGroud(0);
		addControlView(this);
		addFirstView(this);
		changeLayoutParames(Configuration.ORIENTATION_PORTRAIT);
		setSelect(selectPosition);
	}
	
	
	public void setContact(Contact mContact){
		this.mContact=mContact;
		if(mContact.is360Panorama()){
			DisplayType=PANORMTYPE_360;
			DisplayIds=controlIds_360;
			DisplayIds_se=controlIds_se_360;
			this.setVisibility(View.VISIBLE);
		}else if(mContact.is180Panorama()){
			DisplayType=PANORMTYPE_180;
			DisplayIds=controlIds_180;
			DisplayIds_se=controlIds_se_180;
			this.setVisibility(View.GONE);
		}else{
			DisplayType=-1;
			DisplayIds=controlIds_360;
			DisplayIds_se=controlIds_se_360;
			this.setVisibility(View.VISIBLE);
		}
	}
	
	@SuppressLint("NewApi")
	private void SetCircleBackGroud(int type){
		if (Utils.isSpecification(Build.VERSION_CODES.JELLY_BEAN)) {
			this.setBackground(getRoundDrawable(type));
		} else {
			this.setBackgroundDrawable(getRoundDrawable(type));
		}
	}

	// 主控制
	private void addFirstView(MonitorPanImConView monitorPanImConView) {
		viewFirst = new MonitorPanControlView(getContext(),
				FirstViews, MonitorPanControlView.ControlType_RECT_Inis);
		viewFirst.setTag("fist");
		viewFirst.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeControl();
			}
		});
		addView(viewFirst,0);
	}
	
	public void setSelect(int position){
		this.selectPosition=position;
		changeSelect(position);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		int k = getChildCount();
		int hi = 0;
		for (int i = 1; i < k; i++) {
			View v = getChildAt(i);
			v.layout(ItemLeftPadding, (int)(Utils.dip2px(mContext, ItemH) * (i-1)), w - ItemLeftPadding, (int)Utils.dip2px(mContext, ItemH)* (i));
			hi = (int) (Utils.dip2px(mContext, ItemH) * (i));
		}
		View v = getChildAt(0);
		if(v!=null){
			v.layout(0, hi, w, h);
		}
	}

	// 功能控制
	private void addControlView(ViewGroup group) {
		for (int i = 0; i < DisplayIds.length; i++) {
			MonitorPanNormalImageView view = new MonitorPanNormalImageView(getContext(), DisplayIds[i]);
			view.setTag(i);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ItemClick != null) {
						ItemClick.onItemClick(v, (Integer) v.getTag());
					}
					changeSelect((Integer) v.getTag());
				}
			});
			group.addView(view);
			views.add(view);
		}
	}
	/**
	 * 切换选中图片（单选）
	 */
	public void changeSelect(int position){
		this.selectPosition=position;
		for(MonitorPanNormalImageView view:views){
			if((Integer) view.getTag()==position){
				view.setCustomDrawable(DisplayIds_se[position]);
			}else{
				view.setCustomDrawable(DisplayIds[(Integer) view.getTag()]);
			}
		}
		changeControl();
	}
	
	public void setItemImage(int position,int[] drawable){
		MonitorPanControlView view=(MonitorPanControlView) findViewWithTag(position);
		if(view!=null){
			view.setCustomDrawable(drawable);
		}
	}
	public void hindControl(){
        for(View view:views){
			view.setVisibility(View.GONE);
		}
        if(viewFirst!=null){
        	viewFirst.setCustomType(mContext, MonitorPanControlView.ControlType_Normal);
        }
        SetCircleBackGroud(1);
        isShowControl=false;
	}
	
	public void ShowControl(){
        for(View view:views){
			view.setVisibility(View.VISIBLE);
		}
        if(viewFirst!=null){
        	viewFirst.setCustomType(mContext, MonitorPanControlView.ControlType_RECT_Inis);
        }
        SetCircleBackGroud(0);
        isShowControl=true;
	}
	/**
	 * 改变显示状态
	 */
	public void changeControl(){
		if(isShowControl){
			hindControl();
		}else{
			ShowControl();
		}
	}
	
	private onMonitorPanClickListner ItemClick;

	public void setOnMonitorPanClickListner(onMonitorPanClickListner ItemClick) {
		this.ItemClick = ItemClick;
	}
	
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		changeLayoutParames(newConfig.orientation);
		super.onConfigurationChanged(newConfig);
	}
	
	public void changeLayoutParames(int orientation) {
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			int height = Utils.dip2px(mContext, ItemH*DisplayIds.length+FirstItemH);
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(Utils.dip2px(mContext, ItemW), height);
			p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			int h=Math.min(MyApp.SCREENHIGHT, MyApp.SCREENWIGHT)/10;
			p.bottomMargin=h/2+Math.max(0,(h*9/5-Utils.dip2px(getContext(), MonitorPanView.ItemW_DP))/2);
			p.leftMargin = Utils.dip2px(mContext, 24.5f);
			setLayoutParams(p);
			int s=getLayoutParams().height;
		} else {
			int height = Utils.dip2px(mContext, ItemH*DisplayIds.length+FirstItemH);
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(Utils.dip2px(mContext, ItemW), height);
			p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			int h=Math.min(MyApp.SCREENHIGHT, MyApp.SCREENWIGHT)/10;
			p.bottomMargin=Utils.dip2px(getContext(), 14f);
			p.rightMargin = Utils.dip2px(mContext, 14f);
			setLayoutParams(p);
			int d=getLayoutParams().height;
		}
	}
	//背景  0 半透明  1 透明
	private Drawable getRoundDrawable(int type){
		if(type==0){
			return new RoundDrawable(Utils.getColorByResouce(R.color.half_alpha), Utils.dip2px(mContext, FirstItemH/2));
		}else{
			return new RoundDrawable(Utils.getColorByResouce(R.color.alpha), Utils.dip2px(mContext, FirstItemH/2));
		}
	}
	
	public void show(){
		if(mContact!=null&&mContact.is360Panorama()){
			this.setVisibility(View.VISIBLE);
			changeLayoutParames(getContext().getResources().getConfiguration().orientation);
		}
	}
	
	public void dissMiss(){
		this.setVisibility(View.GONE);
	}
}
