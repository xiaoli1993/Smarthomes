package com.jwkj.activity;

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

import com.jwkj.adapter.BindingLocationRecyAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.DefenceArea;
import com.jwkj.data.Prepoint;
import com.jwkj.global.Constants;
import com.jwkj.recycleview.ItemDecor.LineItemDecoration;
import com.jwkj.recycleview.ItemDecor.RecycleViewLinearLayoutManager;
import com.jwkj.utils.T;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BindingLocationActivity extends BaseActivity implements OnClickListener{
	boolean isRegFilter = false;
	private Context mContext;
	private Contact mContact;
	private DefenceArea defenceArea;
	private ImageView blBackBtn;
	private RecyclerView blRlLocation;
	private BindingLocationRecyAdapter mAdapter;
	private List<Integer> location=new ArrayList<Integer>();
	private int listIndex=-1;
	private NormalDialog dialog_loading;
	private int setIndex=-1;
	private Prepoint prepoint;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_binding_location);
		mContext = this;
		mContact = (Contact) getIntent().getSerializableExtra("mContact");
		defenceArea = (DefenceArea) getIntent().getSerializableExtra("defenceArea");
		location=(ArrayList<Integer>)getIntent().getIntegerArrayListExtra("allLocation");
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
		if (defenceArea.getLocation()==-1) {
			mAdapter.setSelect(-1);
		}else {
			mAdapter.setSelect(defenceArea.getLocation());
		}
		mAdapter.updateAll();
		blBackBtn.setOnClickListener(this);
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.P2P_SET_ALARM_PRESET_MOTOR_POS);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Constants.P2P.P2P_SET_ALARM_PRESET_MOTOR_POS)) {
				byte[] result=intent.getByteArrayExtra("result");
				if (result[1]==0) {
					if (null!=dialog_loading) {
						dialog_loading.dismiss();
					}
					if (setIndex==(location.size()-1)) {
						T.showLong(mContext, R.string.clear_success);
					}else {
						T.showLong(mContext, R.string.bound_success);
					}
					mAdapter.setSelect(setIndex);
					mAdapter.updateAll();
					listIndex=setIndex;
				}else if (result[1]==84) {
					if (null!=dialog_loading) {
						dialog_loading.dismiss();
					}
				}
			}
		}
		
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bl_back_btn:
			this.finish();
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
		return Constants.ActivityInfo.ACTIVITY_BINDING_LOCATION;
	}
	
	
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
					dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
					if (value==-1) {
						dialog_loading.setTitle(mContext.getResources().getString(R.string.clearing));	
					}
			}
			dialog_loading.showDialog();
			byte[] data;
			if (value==-1) {
				data=new byte[]{90,0,1,0,(byte)(defenceArea.getGroup()-1),(byte)defenceArea.getItem(),(byte)255};
			}else {
				data=new byte[]{90,0,1,0,(byte)(defenceArea.getGroup()-1),(byte)defenceArea.getItem(),(byte)value};
			}
			setIndex=position;
			P2PHandler.getInstance().sMesgSetAlarmPresetMotorPos(mContact.getContactId(), mContact.getPassword(), data);
		}
	};
}
