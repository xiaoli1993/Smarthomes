package com.jwkj.activity;

import java.util.ArrayList;
import java.util.List;

import com.jwkj.adapter.BindingLocationRecyAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.Prepoint;
import com.jwkj.entity.Sensor;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.recycleview.ItemDecor.LineItemDecoration;
import com.jwkj.recycleview.ItemDecor.RecycleViewLinearLayoutManager;
import com.jwkj.utils.T;
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BindingLocationActivity2 extends BaseActivity implements OnClickListener{
	boolean isRegFilter = false;
	private Context mContext;
	private Contact mContact;
	private Sensor sensor;
	private ImageView blBackBtn;
	private RecyclerView blRlLocation;
	private BindingLocationRecyAdapter mAdapter;
	private List<Integer> location=new ArrayList<Integer>();
	private int listIndex=-1;
	private NormalDialog dialog_loading;
	private int setIndex=-1;
	private Prepoint prepoint;
	private int index;
	private int prepointValue=-1;
	private int locationValue=-1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_binding_location);
		mContext = this;
		mContact = (Contact) getIntent().getSerializableExtra("mContact");
		sensor=(Sensor)getIntent().getSerializableExtra("sensor");
		location=(ArrayList<Integer>)getIntent().getIntegerArrayListExtra("allLocation");
		index=getIntent().getIntExtra("index", -1);
		initComponent();
		regFilter();
	}
	
	public void initComponent() {
		blBackBtn=(ImageView)findViewById(R.id.bl_back_btn);
		blRlLocation=(RecyclerView)findViewById(R.id.bl_rl_location);
		RecycleViewLinearLayoutManager recycleManager = new RecycleViewLinearLayoutManager(mContext);
		blRlLocation.addItemDecoration(new LineItemDecoration(mContext, LinearLayoutManager.VERTICAL));
		blRlLocation.setLayoutManager(recycleManager);
		prepoint=DataManager.findPrepointByDevice(mContext, mContact.contactId);
		mAdapter=new BindingLocationRecyAdapter(mContext, location,prepoint);
		mAdapter.setBDLocationListener(bdlLocationListener);
		blRlLocation.setAdapter(mAdapter);
		if ((sensor.getPrepoint()-1)==-1) {
			mAdapter.setSelect(-1);
		}else {
			mAdapter.setSelect(sensor.getPrepoint()-1);
		}
		prepointValue=sensor.getPrepoint();
		mAdapter.updateAll();
		blBackBtn.setOnClickListener(this);
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_SET_SENSOR_PREPOINT);
		filter.addAction(Constants.P2P.RET_SET_ONE_SPECIAL_ALARM);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int iSrcID=intent.getIntExtra("iSrcID", -1);
			byte boption=intent.getByteExtra("boption", (byte)-1);
			byte[] data = intent.getByteArrayExtra("data");
			if (intent.getAction().equals(Constants.P2P.RET_SET_SENSOR_PREPOINT)) {
				if (null!=dialog_loading) {
					dialog_loading.dismiss();
				}
				if (boption== Constants.FishBoption.MESG_SET_OK) {
					if (setIndex==(location.size()-1)) {
						T.showLong(mContext, R.string.clear_success);
					}else {
						T.showLong(mContext, R.string.bound_success);
					}
					prepointValue=locationValue;
					mAdapter.setSelect(setIndex);
					mAdapter.updateAll();
					listIndex=setIndex;
				}else if (boption==29) {
					if (null!=dialog_loading) {
						dialog_loading.dismiss();
					}
					T.showShort(mContext, R.string.sensor_not_exist);
				}
			}else if (intent.getAction().equals(Constants.P2P.RET_SET_ONE_SPECIAL_ALARM)) {
				if (null!=dialog_loading) {
					dialog_loading.dismiss();
				}
				if (boption== Constants.FishBoption.MESG_SET_OK) {
					if (setIndex==(location.size()-1)) {
						T.showLong(mContext, R.string.clear_success);
					}else {
						T.showLong(mContext, R.string.bound_success);
					}
					prepointValue=locationValue;
					mAdapter.setSelect(setIndex);
					mAdapter.updateAll();
					listIndex=setIndex;
				}else if (boption==28) {
					if (null!=dialog_loading) {
						dialog_loading.dismiss();
					}
					T.showShort(mContext, R.string.no_sensor_type);
				}
			}
		}
		
	};
	
	
	
	BindingLocationRecyAdapter.BDLocationListener bdlLocationListener=new BindingLocationRecyAdapter.BDLocationListener() {

		@Override
		public void bindingClick(int position, int value) {
			// TODO Auto-generated method stub
//			mAdapter.setSelect(position);
//			mAdapter.updateAll();
			if (position==listIndex) {
				return;
			}
			if (dialog_loading==null) {
				dialog_loading = new NormalDialog(mContext,
						mContext.getResources().getString(
								R.string.biding_waite), "", "", "");
				dialog_loading
						.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				if (value==-1) {
					dialog_loading.setTitle(mContext.getResources().getString(R.string.clearing));	
				}
		}
		dialog_loading.showDialog();
		setIndex=position;
		locationValue=value+1;
		if (sensor.getSensorType()==Sensor.SENSORCATEGORY_NORMAL) {
			FisheyeSetHandler.getInstance().sSpecialAlarmData(mContact.contactId,mContact.contactPassword,sensor.getSensorData4Byte(),locationValue);
		}else {
			FisheyeSetHandler.getInstance().sSetSenSorPrePoint(mContact.contactId,mContact.contactPassword,sensor.getSensorData4Byte(),locationValue,(byte)0);	
		}
		
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bl_back_btn:
			Intent i=new Intent();
			i.putExtra("index", index);
			i.putExtra("SensorPrepoint", prepointValue);
			BindingLocationActivity2.this.setResult(RESULT_OK, i);
			BindingLocationActivity2.this.finish();
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
			isRegFilter = false;
			this.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_BINDING_LOCATION2;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i=new Intent();
		i.putExtra("index", index);
		i.putExtra("SensorPrepoint", prepointValue);
		BindingLocationActivity2.this.setResult(RESULT_OK, i);
		BindingLocationActivity2.this.finish();
	}

}
