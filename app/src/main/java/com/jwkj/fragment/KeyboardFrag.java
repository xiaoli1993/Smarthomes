package com.jwkj.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jwkj.activity.MainActivity;
import com.jwkj.adapter.AlarmRecordAdapter;
import com.jwkj.data.AlarmRecord;
import com.jwkj.data.DataManager;
import com.jwkj.data.DefenceArea;
import com.jwkj.global.Constants;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.lib.pullToRefresh.PullToRefreshListView;
import com.nuowei.smarthome.R;

public class KeyboardFrag extends BaseFragment implements OnClickListener,OnScrollListener{
	private boolean isRegFilter = false;
	private Context mContext;
	private List<AlarmRecord> list=new ArrayList<AlarmRecord>();
	private AlarmRecordAdapter adapter;
	private ImageView clear;
	LinearLayout l_no_alarm_record;
	
	ListView list_record;
	PullToRefreshListView mpull_refresh_list;
	private int visibleLastIndex = 0; // 最后的可视项索引
	private int visibleItemCount; // 当前窗口可见项总数

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_keyboard, container,
				false);
		mContext = getActivity();
		initComponent(view);
		regFilter();
		return view;
	}

	public void initComponent(View view) {
		clear = (ImageView) view.findViewById(R.id.clear);
		l_no_alarm_record=(LinearLayout)view.findViewById(R.id.l_no_alarm_record);
		list_record = (ListView) view.findViewById(R.id.list_allarm);
		mpull_refresh_list = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		clear.setOnClickListener(this);
		if (RecoderChange()> 0) {
			if(list.size()>0){
				l_no_alarm_record.setVisibility(View.GONE);
			}
		}
//		getRecoder();
		adapter = new AlarmRecordAdapter(mContext, list);
		list_record.setAdapter(adapter);
		list_record.setOnScrollListener(this);
		if(list.size()<=0){
			l_no_alarm_record.setVisibility(View.VISIBLE);
		}else{
			l_no_alarm_record.setVisibility(View.GONE);
		}
	}
	private int pages=0;
	private int pageNumbers=50;
	int getRecoder(){
		int[] num = new int[2];
		num[0] = pages * pageNumbers;
		num[1] = pageNumbers;
		list.addAll(DataManager.findAlarmRecordByActiveUser(mContext, NpcCommon.mThreeNum,num));
		setAlarmRecordName();
		return DataManager.findAlarmRecordByActiveUser(mContext, NpcCommon.mThreeNum,num).size();
	}
	int RecoderChange(){
		pages=0;
		list.clear();
		return getRecoder();
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.REFRESH_ALARM_RECORD);
		filter.addAction(Constants.Action.REFRESH_ALARM_MESSAGE);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals(Constants.Action.REFRESH_ALARM_RECORD)) {
				if(RecoderChange()>0){
					if(list.size()<=0){
						l_no_alarm_record.setVisibility(View.VISIBLE);
					}else{
						l_no_alarm_record.setVisibility(View.GONE);
					}
					adapter.updateData();
				}
			}else if(intent.getAction().equals(Constants.Action.REFRESH_ALARM_MESSAGE)){
				if(RecoderChange()>0){
					if(list.size()<=0){
						l_no_alarm_record.setVisibility(View.VISIBLE);
					}else{
						l_no_alarm_record.setVisibility(View.GONE);
					}
					adapter.updateData();
				}
			}
		}
	};

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		int itemsLastIndex = adapter.getCount() - 1; // 数据集最后一项的索引
		int lastIndex = itemsLastIndex; // 加上底部的loadMoreView项
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLastIndex == lastIndex) {
			pages++;
			view.setSelection(view.getLastVisiblePosition());
			if (getRecoder() > 0) {
				adapter.notifyDataSetChanged();
			} else {
				// 到底
				T.show(mContext, R.string.no_alarm_record, 1000);
			}

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.visibleItemCount = visibleItemCount;
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.clear:
			if (list.size() <= 0) {
				T.show(mContext, Utils.getStringForId(R.string.no_alarm_record), 2000);
				return;
			}
			NormalDialog dialog = new NormalDialog(mContext, mContext.getResources().getString(
					R.string.delete_alarm_records), mContext.getResources().getString(R.string.confirm_clear), mContext
					.getResources().getString(R.string.clear), mContext.getResources().getString(R.string.cancel));
			dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

				@Override
				public void onClick() {
					// TODO Auto-generated method stub
					DataManager.clearAlarmRecord(mContext, NpcCommon.mThreeNum);
					if(RecoderChange()>0){
						l_no_alarm_record.setVisibility(View.GONE);
					}else {
						l_no_alarm_record.setVisibility(View.VISIBLE);
					}
					adapter.updateData();
				}
			});
			dialog.showDialog();
			break;
		}
	}
	
	public void setAlarmRecordName(){
		if (list==null) {
			return;
		}
		if (list.size()==0) {
			return;
		}
		DefenceArea dArea=new DefenceArea();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).alarmType==1) {
				dArea.setGroup(list.get(i).group);
				dArea.setItem(list.get(i).item);
				DefenceArea defenceArea=DataManager.findDefenceAreaByDeviceID(mContext, list.get(i).deviceId, dArea);
				if (defenceArea!=null) {
					list.get(i).name=defenceArea.getName();
				}else {
					list.get(i).name=mContext.getResources().getString(R.string.area)+((list.get(i).group-1)*8+(list.get(i).item+1));
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isRegFilter) {
			isRegFilter = false;
			mContext.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
