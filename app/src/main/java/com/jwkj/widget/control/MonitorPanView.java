package com.jwkj.widget.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jwkj.utils.Utils;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.List;
/**
 * 横屏监控全景工具条
 * @author dxs
 *
 */
public class MonitorPanView extends ViewGroup{
	private int w, h;
	private int ItemPading;
	private List<MonitorPanControlView> views=new ArrayList<MonitorPanControlView>();
	private int ItemH=0;
	private final int TextID=111;
	public final static int ItemW_DP=46;//宽,不应该写在这里
	
	private int[][] controlIds=new int[][]{
			{R.drawable.monitor_l_pan_defenon,R.drawable.monitor_l_pan_defenon_p},
			{R.drawable.monitor_l_pan_voice,R.drawable.monitor_l_pan_voice_p},
			{R.drawable.monitor_l_pan_scree,R.drawable.monitor_l_pan_scree_p},
			{R.drawable.monitor_l_pan_talk,R.drawable.monitor_l_pan_talk_p},
			{R.drawable.monitor_l_pantuo,R.drawable.monitor_l_pantuo_p}};
	
	private final int TouchView=3;
	private Context mContext;
	public MonitorPanView(Context context) {
		super(context);
		init(context);
	}

	public MonitorPanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		mContext=context;
		ItemPading=Utils.dip2px(context, 7.5f);
		addControlView(this);
		int height=Math.min(MyApplication.SCREENHIGHT, MyApplication.SCREENWIGHT)*9/10;
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(Utils.dip2px(mContext, ItemW_DP), height);
		p.addRule(RelativeLayout.CENTER_VERTICAL);
		p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		//p.bottomMargin=Math.min(MyApplication.SCREENHIGHT, MyApplication.SCREENWIGHT)/10;
		p.rightMargin=Utils.dip2px(mContext, 10.5f);
		setLayoutParams(p);
		ItemH=height/controlIds.length;
	}
	
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		w = getMeasuredWidth();
		int count=getChildCount();
		for (int i = 0; i < count; i++) {
			View v = getChildAt(i);
			v.layout(0,ItemH*i,w,ItemH*(i+1));
		}
	}
	private void addControlView(ViewGroup group){
		for(int i=0;i<controlIds.length;i++){
			final MonitorPanControlView view=new MonitorPanControlView(getContext(),controlIds[i]);
			view.setTag(i);
			views.add(view);
			if(i==TouchView){
				view.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							ItemClick.onItemPress(v, (Integer) v.getTag(), true);
							view.setCustomDrawable(new int[]{R.drawable.monitor_l_pan_talk_p,R.drawable.monitor_l_pan_talk_p});
							return true;
						case MotionEvent.ACTION_UP:
							ItemClick.onItemPress(v, (Integer) v.getTag(), false);
							view.setCustomDrawable(controlIds[TouchView]);
							return true;
						case MotionEvent.ACTION_MOVE:
							return true;
						case MotionEvent.ACTION_CANCEL:
							ItemClick.onItemPress(v, (Integer) v.getTag(), false);
							view.setCustomDrawable(controlIds[TouchView]);
							return true;
						}
						return false;
					}
				});
			}else{
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(ItemClick!=null){
							ItemClick.onItemClick(v, (Integer) v.getTag());
						}
					}
				});
			}
			group.addView(view);
		}
	}
	

	
	public void setItemImage(int position,int[] drawable){
		MonitorPanControlView view=(MonitorPanControlView) findViewWithTag(position);
		if(view!=null){
			view.setCustomDrawable(drawable);
		}
	}
	
	
	public interface onMonitorPanClickListner{
		void onItemClick(View view,int position);
		void onItemPress(View view,int position,boolean isPress);
		void onControlClick(ViewGroup group,View viewControl);
	}
	private onMonitorPanClickListner ItemClick;
	public void setOnMonitorPanClickListner(onMonitorPanClickListner ItemClick){
		this.ItemClick=ItemClick;
	}
	
}
