package com.jwkj.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jwkj.data.DataManager;
import com.jwkj.data.Prepoint;
import com.jwkj.entity.OnePrepoint;
import com.jwkj.fragment.MonitorThreeFrag;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.RememberPontPopWindow.ondismissListener;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by dxs on 2015/12/7.
 */
public class PrePointLayout extends ViewGroup implements
		RememberImageRl.OnRememberImageRlClickListner,
		RememberImageRl.OnRememberImageRlLongClickListner {
	int w, h, itemTw, itemh, itemBw;
	private ImageButton viewAdd;
	private boolean isSelectedMode = false;
	private Context mContext;
	private List<OnePrepoint> RememberPoins = new ArrayList<OnePrepoint>();
	private List<String> deletePaths = new ArrayList<String>();
	private int selectePoints = 0;
	private String deviceId = "";
	private int MarginTop = 5;// 20dp,在初始化时

	public PrePointLayout(Context context) {
		super(context);
		initUI(context);
	}

	public PrePointLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI(context);
	}
	private ondismissListener listener;
	public void setOnPopwindowListner(ondismissListener listener){
		this.listener=listener;
	}
	
	

	private void initUI(final Context context) {
		// params=new
		// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		viewAdd = new ImageButton(context);
		this.mContext = context;
		MarginTop = Utils.dip2px(mContext, MarginTop);
		viewAdd.setImageResource(R.drawable.bg_button_remember_point);
		viewAdd.setPadding(20, 20, 20, 20);
		viewAdd.setBackgroundColor(Utils.getColorByResouce(R.color.alpha));
		viewAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (listner != null) {
					if (!isSelectedMode) {
						listner.addMyImageViewListner(getNextPrepoint());
					} else {
						T.showLong(context, R.string.prepoint_cannottoadd);
					}
				}
			}
		});
		viewAdd.setTag("add");
		addView(viewAdd);
		
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(listener!=null){
					listener.onshow();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 400);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(listener!=null){
			listener.onDismiss();
		}
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		w = getMeasuredWidth();
		h = getMeasuredHeight();
		itemTw = (int) (w / 3);
		itemBw = (int) (w / 2);
		itemh = h / 2;
		int count=getChildCount();
		for (int i = 0; i < count; i++) {
			if (i <= 2) {
				View v = getChildAt(i);
				v.layout(itemTw * i, 0 + MarginTop, itemTw * (i + 1), itemh
						+ MarginTop);
			} else if (i == 3) {
				View v = getChildAt(i);
				v.layout(itemBw * (i - 3) + itemTw / 2, itemh, itemTw * (i - 2)
						+ itemTw / 2, itemh * 2);
			} else if (i == 4) {
				View v = getChildAt(i);
				v.layout(itemBw * (i - 3), itemh, itemBw + itemTw, itemh * 2);
			} else if (i == 5) {
				View v = getChildAt(i);
				v.layout(itemBw * (i - 3), itemh, itemBw + itemTw, itemh * 2);
			}
		}
		View v=findViewWithTag("add");
		if(count>5){
			v.setVisibility(View.GONE);
		}else{
			v.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int widthSizeT = MeasureSpec.getSize(widthMeasureSpec) / 3;
		int widthSizeB = MeasureSpec.getSize(widthMeasureSpec) / 3;
		int heightSize = MeasureSpec.getSize(heightMeasureSpec) / 2;

		int _widthMeasureSpecT = MeasureSpec.makeMeasureSpec(widthSizeT,
				widthMode);
		int _widthMeasureSpecB = MeasureSpec.makeMeasureSpec(widthSizeB,
				widthMode);
		int _heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
				heightMode);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if (i <= 2) {
				getChildAt(i).measure(_widthMeasureSpecT, _heightMeasureSpec);
			} else if (i <= 5) {
				getChildAt(i).measure(_widthMeasureSpecB, _heightMeasureSpec);
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void addInnerView(List<RememberImageRl> views) {
		for (int i = 0; i < views.size(); i++) {
			addInnerView(views.get(i), i);
		}
	}

	public void addInnerView(RememberImageRl view, int position) {
		view.setOnRememberImageRlClickListner(this);
		view.setOnRememberImageRlLongClickListner(this);
		view.setTag(view.getPrepoint().prepoint);
		addView(view, position);
		if (!RememberPoins.contains(view.getPrepoint())) {
			RememberPoins.add(view.getPrepoint());
		}
		requestLayout();
		invalidate();
		setBackgroundColor(getResources().getColor(R.color.fish_back));
	}

	public void removeInnerView(int[] position) {
		List<OnePrepoint> pointsTemp = new ArrayList<OnePrepoint>();
		for(int i=0;i<position.length;i++){
			if(position[i]!=-2){
				removeView(findViewWithTag(position[i]));
				for (OnePrepoint onePrepoint : RememberPoins) {
					if(onePrepoint.prepoint==position[i]){
						pointsTemp.add(onePrepoint);
					}
				}
			}
		}
		RememberPoins.removeAll(pointsTemp);
		requestLayout();
		invalidate();
		setBackgroundColor(getResources().getColor(R.color.fish_back));
	}

	/**
	 * 更新子View的界面
	 */
	public void updataInnerView() {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof RememberImageRl) {
				((RememberImageRl) v).updateIsSelected();
			}
		}
	}

	/**
	 * 更新子View的界面
	 */
	public void updataInnerView(int position) {
		if (position >= 0 && position < getChildCount()) {
			View v = getChildAt(position);
			if (v instanceof RememberImageRl) {
				((RememberImageRl) v).updateIsSelected();
			}
		}
	}

	private OnAddMyImageViewListner listner;

	@Override
	public void onClick(OnePrepoint point, int position) {
		if (isSelectedMode) {
			// 选择模式
			if (point.isSelected) {
				point.isSelected = false;
				// rememberListner.cancelPrepoint(point,position);
			} else {
				point.isSelected = true;
				// rememberListner.selectedPrepoint(point,position);
			}
			rememberListner.selectedPrepoints(getSeletedPrepoint(),
					deletePaths, selectePoints);
			updataInnerView();
		} else {
			// 查看預置位
			rememberListner.toSeePrepoint(point);
		}
	}

	@Override
	public void onLongClick(OnePrepoint point, int position) {
		if (!isSelectedMode) {
			isSelectedMode = true;
			point.isSelected = true;
			Utils.PhoneVibrator(mContext);
			rememberListner.selectedPrepoints(getSeletedPrepoint(),
					deletePaths, selectePoints);
			updataInnerView(position);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean isOutOfView=false;
		for (int i = 0; i < getChildCount(); i++) {
			if(!inRangeOfView(getChildAt(i),event)){
				isOutOfView=true;
				break;
			}
		}
		if(isOutOfView&&isSelectedMode)rememberListner.onViewCancel();
		return super.onTouchEvent(event);
	}
	
	private boolean inRangeOfView(View view, MotionEvent ev) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y
				|| ev.getY() > (y + view.getHeight())) {
			return false;
		}
		return true;
	}

	public interface OnAddMyImageViewListner {
		void addMyImageViewListner(int prepointNum);
	}

	public void setOnAddMyImageViewListner(OnAddMyImageViewListner listner) {
		this.listner = listner;
	}

	public int getNextPosition() {
		return getChildCount() - 1;
	}

	private onRememberlistner rememberListner;

	public void setOnRememberlistner(onRememberlistner listner) {
		this.rememberListner = listner;
	}

	public interface onRememberlistner {
		void selectedPrepoint(OnePrepoint point, int position);

		void cancelPrepoint(OnePrepoint point, int position);

		void toSeePrepoint(OnePrepoint point);

		void selectedPrepoints(List<OnePrepoint> Selected,
				List<String> prepointPath, int SelectedPoins);

		void onViewCancel();
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelectedMode = isSelected;
	}

	public boolean getIsSelected() {
		return isSelectedMode;
	}

	/**
	 * 获取下一个可设置的预置位
	 * 
	 * @return 下一个可设置预置位编号
	 */
	private int getNextPrepoint() {
		int[] prepointTemp = new int[] { 0, 1, 2, 3, 4 };
		for (OnePrepoint points : RememberPoins) {
			prepointTemp[points.prepoint] = -1;
		}
		for (int i = 0; i < prepointTemp.length; i++) {
			if (prepointTemp[i] != -1) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 指定某位二进制为1，其他位为0，即设置为预置位
	 * 
	 * @param src
	 *            源
	 * @param position
	 *            改变的位置
	 * @return 改变后的值
	 */
	private int select(int src, int position) {
		return src | (1 << position);
	}

	/**
	 * 获取选中的记忆点
	 * 
	 * @return 选中的记忆点
	 */
	public List<OnePrepoint> getSeletedPrepoint() {
		List<OnePrepoint> SeletedPrePoint = new ArrayList<OnePrepoint>();
		deletePaths.clear();
		selectePoints = 0;
		for (OnePrepoint po : RememberPoins) {
			if (po.isSelected) {
				SeletedPrePoint.add(po);
				deletePaths.add(po.imagePath);
				selectePoints = select(selectePoints, po.prepoint);
			}
		}
		return SeletedPrePoint;
	}

	/**
	 * 清除预置位所有选中状态
	 */
	public void ClearPoints() {
		isSelectedMode = false;
		for (OnePrepoint points : RememberPoins) {
			points.isSelected = false;
		}
		updataInnerView();
	}

	/**
	 * 添加一个预置位,如果已经存在则更新数据
	 * 
	 * @param position
	 *            待添加的预置位
	 */
	public void addPrepoint(int position) {
		if (isLocalPrepoint(position)) {
			updataInnerView(getPrepointBuypoint(position));
			return;
		}
		Prepoint points = DataManager.findPrepointByDevice(mContext, deviceId);
		OnePrepoint point = new OnePrepoint();
		point.prepoint = position;
		point.imagePath = Utils.getPrepointPath(deviceId, position);
		point.name = points.getName(position);
		RememberImageRl rl = new RememberImageRl(mContext);
		rl.setPrePoint(point, position);
		addInnerView(rl, position);
		T.showShort(mContext, R.string.set_wifi_success);
	}

	/**
	 * 删除几个预置位
	 * 
	 * @param info
	 */
	public void deletePrepoint(byte info) {
		int[] temp = ParsePrepointInfo(info);
		int[] removeTemp=new int[MonitorThreeFrag.PREPOINTCOUNTS];
		Arrays.fill(removeTemp, -2);
		int j=0;
		for (int i = 0; i < MonitorThreeFrag.PREPOINTCOUNTS; i++) {
			if (temp[i] == 1) {
				DataManager.upDataPrepointByCount(mContext, deviceId, i);
//				int s = getPrepointBuypoint(i);
//				if (s != -1) {
					removeTemp[j++]=i;
//				}
			}
		}
		removeInnerView(removeTemp);
		ClearPoints();
	}

	/**
	 * 是否已设置的预置位
	 * 
	 * @param prepoint
	 *            被检测的预置位
	 * @return
	 */
	private boolean isLocalPrepoint(int prepoint) {
		for (OnePrepoint poin : RememberPoins) {
			if (poin.prepoint == prepoint) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据预置位标记获得在布局中的位置
	 * 
	 * @param i
	 * @return
	 */
	private int getPrepointBuypoint(int i) {
		int temp = 0;
		Collections.sort(RememberPoins);
		for (OnePrepoint p : RememberPoins) {
			if (p.prepoint == i) {
				return temp;
			}
			temp++;
		}
		return -1;
	}

	/**
	 * 解析预置位信息
	 * 
	 * @param prepointInfo
	 * @return
	 */
	public int[] ParsePrepointInfo(byte prepointInfo) {
		return Utils.getByteBinnery(prepointInfo, true);
	}
}
