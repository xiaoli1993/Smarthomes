package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

import com.jwkj.adapter.LocalDeviceListAdapter;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.nuowei.smarthome.R;

import java.util.List;

public class LocalDeviceListActivity extends BaseActivity implements
		OnClickListener {
	private ImageView mBack;
	private ListView mList;
	private LocalDeviceListAdapter mAdapter;
	private Context mContext;
	boolean isRegFilter;
	private  View line;
	private int addDeviceMethod=Constants.AddDeviceMethod.LAN_ADD;
    private ImageView iv_refresh;
	Animation animation;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_local_device_list);
		addDeviceMethod=getIntent().getIntExtra("addDeviceMethod",Constants.AddDeviceMethod.LAN_ADD);
		initCompent();
		regFilter();
	}

	public void initCompent() {
		mBack = (ImageView) findViewById(R.id.back_btn);
		mList = (ListView) findViewById(R.id.list_local_device);
		line=(View)findViewById(R.id.line) ;
		iv_refresh=(ImageView) findViewById(R.id.iv_refresh);
		List<LocalDevice> datas = FList.getInstance().getLocalDevicesNoAP();
		showHideLine(datas);
		mAdapter = new LocalDeviceListAdapter(mContext);
		mAdapter.setAddDeviceMethod(addDeviceMethod);
		mList.setAdapter(mAdapter);
		mBack.setOnClickListener(this);
		iv_refresh.setOnClickListener(this);
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.ADD_CONTACT_SUCCESS);
		filter.addAction(Constants.Action.LOCAL_DEVICE_SEARCH_END);
		filter.addAction(Constants.Action.EXIT_ADD_DEVICE);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.Action.ADD_CONTACT_SUCCESS)) {
				FList.getInstance().updateLocalDeviceWithLocalFriends();
				List<LocalDevice> datas = FList.getInstance().getLocalDevicesNoAP();
				showHideLine(datas);
				mAdapter.updateData(datas);
				finish();
			} else if (intent.getAction().equals(
					Constants.Action.LOCAL_DEVICE_SEARCH_END)) {
				FList.getInstance().updateLocalDeviceWithLocalFriends();
				List<LocalDevice> datas = FList.getInstance().getLocalDevicesNoAP();
				if(animation!=null){
					animation.cancel();
					iv_refresh.clearAnimation();
				}
				iv_refresh.setClickable(true);
				showHideLine(datas);
				mAdapter.updateData(datas);
			}else if(intent.getAction().equals(Constants.Action.EXIT_ADD_DEVICE)){
				finish();
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.iv_refresh:
			animation= AnimationUtils.loadAnimation(mContext,R.anim.progress_refresh);
			animation.setInterpolator(new LinearInterpolator());
			iv_refresh.startAnimation(animation);
			FList.getInstance().searchLocalDevice();
			iv_refresh.setClickable(false);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_LOCAL_DEVICE_LIST;
	}
	private void showHideLine(List<LocalDevice> datas){
		if(datas.size()<=0){
			line.setVisibility(View.GONE);
			iv_refresh.setVisibility(View.VISIBLE);
		}else{
			line.setVisibility(View.VISIBLE);
			iv_refresh.setVisibility(View.GONE);
		}
	}

}
