package com.jwkj.activity;

import java.util.ArrayList;
import java.util.List;

import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.data.Contact;
import com.jwkj.entity.DefenceWorkGroup;
import com.jwkj.global.Constants;
import com.jwkj.selectdialog.SelectItem;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.wheel.widget.OnWheelChangedListener;
import com.jwkj.wheel.widget.WheelView;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.scedueView;
import com.p2p.core.P2PHandler;
import com.nuowei.smarthome.R;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class DefenceSelectTimeActivity extends BaseActivity implements
		OnClickListener {
	private boolean isRegFilter = false;
	private Context mContext;
	private Contact mContact;
	private ImageView dstBackBtn;
	private Button dstSaveBtn;
	private WheelView hourFrom;
	private WheelView minuteFrom;
	private WheelView hourTo;
	private WheelView minuteTo;
	private scedueView dstSvMode;
	private DefenceWorkGroup defenceWorkGroup;
	private long[] beginArray;
	private long[] endArray;
	private NormalDialog dialog_loading;
	private List<DefenceWorkGroup> Groups;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_defence_select_time);
		mContext = this;
		mContact = (Contact) getIntent().getSerializableExtra("mContact");
		defenceWorkGroup = (DefenceWorkGroup) getIntent().getParcelableExtra(
				"DefenceWorkGroup");
		Groups = getIntent().getParcelableArrayListExtra("Groups");
		beginArray = getIntent().getLongArrayExtra("beginArray");
		endArray = getIntent().getLongArrayExtra("endArray");
		initComponent();
		regFilter();
	}

	public void initComponent() {
		dstBackBtn = (ImageView) findViewById(R.id.dst_back_btn);
		dstSaveBtn = (Button) findViewById(R.id.dst_save_btn);
		hourFrom = (WheelView) findViewById(R.id.dst_hour_from);
		minuteFrom = (WheelView) findViewById(R.id.dst_minute_from);
		hourTo = (WheelView) findViewById(R.id.dst_hour_to);
		minuteTo = (WheelView) findViewById(R.id.dst_minute_to);
		dstSvMode = (scedueView) findViewById(R.id.dst_sv_mode);
		hourFrom.setViewAdapter(new DateNumericAdapter(mContext, 0, 23));
		hourFrom.setCyclic(true);
		minuteFrom.setViewAdapter(new DateNumericAdapter(mContext, 0, 59));
		minuteFrom.setCyclic(true);
		hourTo.setViewAdapter(new DateNumericAdapter(mContext, 0, 23));
		hourTo.setCyclic(true);
		minuteTo.setViewAdapter(new DateNumericAdapter(mContext, 0, 59));
		minuteTo.setCyclic(true);
		dstBackBtn.setOnClickListener(this);
		dstSaveBtn.setOnClickListener(this);
		hourFrom.addChangingListener(wheelChangedListener);
		minuteFrom.addChangingListener(wheelChangedListener);
		hourTo.addChangingListener(wheelChangedListener);
		minuteTo.addChangingListener(wheelChangedListener);
		dstSvMode.setWorkGroup(defenceWorkGroup);
		dstSvMode.setViewState(1);
		dstSvMode.setScedueViewListener(scedueViewListener);
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_SET_DEFENCE_WORK_GROUP);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String iSrcID = intent.getStringExtra("iSrcID");
			byte boption = intent.getByteExtra("boption", (byte) -1);
			byte[] data = intent.getByteArrayExtra("data");
			if (intent.getAction().equals(
					Constants.P2P.RET_SET_DEFENCE_WORK_GROUP)) {
				if (null != dialog_loading) {
					dialog_loading.dismiss();
				}
				if (boption == 0) {
					Intent i = new Intent();
					i.putExtra("DefenceWorkGroup", defenceWorkGroup);
					DefenceSelectTimeActivity.this.setResult(RESULT_OK, i);
					DefenceSelectTimeActivity.this.finish();
				}
			}
		}

	};
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.dst_back_btn:
			this.finish();
			break;
		case R.id.dst_save_btn:
			toSave();
			break;
		default:
			break;
		}
	}

	public void toSave() {
		if (defenceWorkGroup.getBeginTime() == defenceWorkGroup.getEndTime()) {
			T.showShort(mContext, R.string.time_repeat);
			return;
		}
		if (defenceWorkGroup.getBeginTime() > defenceWorkGroup.getEndTime()) {
			T.showShort(mContext, R.string.time_beyond);
			return;
		}
		if (isRepeat()) {
			T.showShort(mContext, R.string.time_group_exsite);
			return;
		}
		showLoadingDialog(mContext.getResources().getString(R.string.inserting));
		List<DefenceWorkGroup> list = new ArrayList<DefenceWorkGroup>();
		list.addAll(Groups);
		list.add(defenceWorkGroup.getIndex(), defenceWorkGroup);
		byte[] data = getDataArray(list);
		if (mContact != null) {
			P2PHandler.getInstance().setDefenceWorkGroup(mContact.contactId,
					mContact.contactPassword, (short) list.size(), data);
		}
	}

	// 判断是否有重复的时间点
	public boolean isRepeat() {
		if (null == Groups || Groups.size() == 0) {
			return false;
		} else {
			for (int i = 0; i < Groups.size(); i++) {
				if (defenceWorkGroup.getBeginTime() == Groups.get(i)
						.getBeginTime()
						&& defenceWorkGroup.getEndTime() == Groups.get(i)
								.getEndTime()) {
					return true;
				}
			}
		}
		return false;
	}

	OnWheelChangedListener wheelChangedListener = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// TODO Auto-generated method stub
			switch (wheel.getId()) {
			case R.id.dst_hour_from:
				defenceWorkGroup.setbBeginHour((byte) newValue);
				break;
			case R.id.dst_minute_from:
				defenceWorkGroup.setbBeginMin((byte) newValue);
				break;
			case R.id.dst_hour_to:
				defenceWorkGroup.setbEndHour((byte) newValue);
				break;
			case R.id.dst_minute_to:
				defenceWorkGroup.setbEndMin((byte) newValue);
				break;
			default:
				break;
			}
		}
	};

	scedueView.ScedueViewListener scedueViewListener = new scedueView.ScedueViewListener() {

		@Override
		public void changeWorkGroupState(int state, int position) {
			// TODO Auto-generated method stub
			if (position == 6) {
				position = 0;
			} else {
				position++;
			}
			if (state == 1) {
				defenceWorkGroup.setbWeekDay(Utils.ChangeBitTrue(
						defenceWorkGroup.getbWeekDay(), position));
			} else {
				defenceWorkGroup.setbWeekDay(Utils.ChangeByteFalse(
						defenceWorkGroup.getbWeekDay(), position));
			}
			dstSvMode.setWorkGroup(defenceWorkGroup);
		}
	};

	// 展示等待弹框的方法
	public void showLoadingDialog(String title) {
		if (dialog_loading == null) {
			dialog_loading = new NormalDialog(mContext, title, "", "", "");
			dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
		} else {
			dialog_loading.setTitle(title);
		}
		dialog_loading.showDialog();
	}

	// 得到由时间组集合变为的byte数组
	public byte[] getDataArray(List<DefenceWorkGroup> list) {
		byte[] data = new byte[60];
		for (int i = 0; i < list.size(); i++) {
			byte[] info = list.get(i).getAllInfo();
			System.arraycopy(info, 0, data, (0 + i) * info.length, info.length);
		}
		return data;
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_DEFENCE_SELECT_TIME;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
}
