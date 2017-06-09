package com.jwkj.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.activity.DefenceSelectTimeActivity;
import com.jwkj.adapter.DefenceSetRecyAdapter;
import com.jwkj.data.Contact;
import com.jwkj.entity.DefenceWorkGroup;
import com.jwkj.global.Constants;
import com.jwkj.recycleview.ItemDecor.SpaceItemDecoration;
import com.jwkj.selectdialog.SelectItem;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.ExDefenceLinerLayout;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.onTimeSelectListner;
import com.p2p.core.P2PHandler;
import com.p2p.core.utils.MyUtils;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.List;

public class DefenceControlFrag extends BaseFragment implements View.OnClickListener{
	private Context mContext;
	private Contact mContact;
	private boolean isRegFilter = false;
	private DefenceSetRecyAdapter mAdapter;
	private List<DefenceWorkGroup> Groups = new ArrayList<DefenceWorkGroup>();
	private byte weekDayTemp = 0;
	private ImageView dfIvAddTime;
	private RecyclerView dfRlDefence;
	private int biggestLimit = 0;
	private NormalDialog dialog;
	private NormalDialog dialog_loading;
	private NormalDialog delete_Dialog;
	private long beginTime;
	private long endTime;
	private DefenceWorkGroup setDefenceWorkGroup;
	private int setType;
	private int changePosition;
	private int isEnable = -1;

	private static final int DATA_DELETE = -1;
	public static final int DATA_ADD = 0;
	private static final int DATA_CHANGE_SWITCH = 1;
	private static final int DATA_CHANGE_TIME = 2;
	private static final int DATA_CHANGE_WEEK = 3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = getActivity();
		mContact = (Contact) getArguments().getSerializable("contact");
		View view=inflater.inflate(R.layout.fragment_defence_control,container,false);
		initComponent(view);
		initData();
		return view;
	}
	public void initData() {
		if (mContact != null) {
			P2PHandler.getInstance().getDefenceWorkGroup(
					mContact.getIpContactId(), mContact.contactPassword);
		}
	}
	public void initComponent(View view) {
		dfRlDefence = (RecyclerView) view.findViewById(R.id.df_rl_defence);
		dfIvAddTime = (ImageView) view.findViewById(R.id.df_iv_add_timegroup);
		dfRlDefence.setLayoutManager(new LinearLayoutManager(mContext));
		dfRlDefence.addItemDecoration(new SpaceItemDecoration(16));
		mAdapter = new DefenceSetRecyAdapter(mContext, Groups);
		mAdapter.setOnDefenceSetting(listner);
		mAdapter.setOnExDefenceLinearLayoutListner(eXlistner);
		dfRlDefence.setAdapter(mAdapter);
		dfIvAddTime.setOnClickListener(this);
	}
	
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_GET_DEFENCE_WORK_GROUP);
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
					Constants.P2P.RET_GET_DEFENCE_WORK_GROUP)) {
				if (boption == 0) {
					biggestLimit = MyUtils.byte2ToShort(data, 2);
					getDeviceWiteList(data, biggestLimit);
					mAdapter.UpdataAll();
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_DEFENCE_WORK_GROUP)) {
				if (null != dialog_loading) {
					dialog_loading.dismiss();
				}
				if (boption == 0) {
					processingDataInfo(setType);
				}
			}
		}
	};
	
	/**
	 * 解析时间组数据
	 * 
	 * @param data
	 * @param len
	 */
	private void getDeviceWiteList(byte[] data, int len) {
		Groups.clear();
		byte[] device = new byte[6];
		for (int i = 0; i < len; i++) {
			System.arraycopy(data, 4 + i * 6, device, 0, device.length);
			DefenceWorkGroup user = new DefenceWorkGroup(device);
			if (user.getbFlag() != -1) {
				Groups.add(user);
			}
		}
	}
	int p;
	private DefenceSetRecyAdapter.onDefenceSetting listner = new DefenceSetRecyAdapter.onDefenceSetting() {

		@Override
		public void onSwitchClick(View v, DefenceWorkGroup grop, int position) {
			// TODO Auto-generated method stub
			settingsDataInfo(DATA_CHANGE_SWITCH,position,grop);
		}

		@Override
		public void onBeginAndEndTimeClick(View v, DefenceWorkGroup grop,
				int position) {
			// TODO Auto-generated method stub
			if (null == dialog) {
				dialog = new NormalDialog(mContext);
				dialog.setOnTimeSelectListner(timeSelectListner);
			}
			DefenceWorkGroup dwg = grop.clone();
			beginTime = grop.getBeginTime();
			endTime = grop.getEndTime();
			dialog.showSelectTime(DATA_CHANGE_TIME, dwg, position);
		}

		@Override
		public void onDelete(View v, final DefenceWorkGroup grop,
				int position) {
			// TODO Auto-generated method stub
			//用全局变量避免position变化了
			p=position;
			if (null == delete_Dialog) {
				delete_Dialog = new NormalDialog(mContext, mContext
						.getResources().getString(R.string.delete_time),
						mContext.getResources().getString(
								R.string.confirm_delete), mContext
								.getResources().getString(R.string.ensure),
						mContext.getResources().getString(R.string.cancel));
				delete_Dialog
						.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

							@Override
							public void onClick() {
								// TODO Auto-generated method stub
								delete_Dialog.dismiss();
								settingsDataInfo(DATA_DELETE,p,grop);
							}

						});
			}
			delete_Dialog.showNormalDialog();
		}
	};
	
	private ExDefenceLinerLayout.ExDefenceLinearLayoutListner eXlistner = new ExDefenceLinerLayout.ExDefenceLinearLayoutListner() {

		@Override
		public void onTimeClick(DefenceWorkGroup group) {
			// TODO Auto-generated method stub
			showSelectDialog(group);
		}

		@Override
		public void onEditorClick(int LayoutState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDeleteClick(final DefenceWorkGroup group) {
			// TODO Auto-generated method stub
			if (null == delete_Dialog) {
				delete_Dialog = new NormalDialog(mContext, mContext
						.getResources().getString(R.string.delete_time),
						mContext.getResources().getString(
								R.string.confirm_delete), mContext
								.getResources().getString(R.string.ensure),
						mContext.getResources().getString(R.string.cancel));
				delete_Dialog
						.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

							@Override
							public void onClick() {
								// TODO Auto-generated method stub
								delete_Dialog.dismiss();
								int index=Groups.indexOf(group);
								settingsDataInfo(DATA_DELETE,index,group);
							}

						});
			}
			delete_Dialog.showNormalDialog();
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.df_iv_add_timegroup:
			int dex = getGroupIndex();
			if (dex < 0) {
				T.showShort(mContext, R.string.add_ceiling);
				return;
			}
			if (null == dialog) {
				dialog = new NormalDialog(mContext);
				dialog.setOnTimeSelectListner(timeSelectListner);
			}
			DefenceWorkGroup group = new DefenceWorkGroup();
			group.setbFlag((byte) dex);
			group.setEnable(true);
			group.setBswitch(1);
			group.setWeekDay(0, true);
			group.setWeekDay(1, true);
			group.setWeekDay(2, true);
			group.setWeekDay(3, true);
			group.setWeekDay(4, true);
			group.setWeekDay(5, true);
			group.setWeekDay(6, true);
//			dialog.showSelectTime(DATA_ADD, group, -1);
			Intent intent=new Intent();
			intent.putExtra("mContact", mContact);
			intent.putExtra("DefenceWorkGroup", group);
			intent.putParcelableArrayListExtra("Groups", (ArrayList<DefenceWorkGroup>)Groups);
			intent.setClass(mContext, DefenceSelectTimeActivity.class);
			startActivityForResult(intent, 2);
			break;
		}
	}
	
	onTimeSelectListner timeSelectListner = new onTimeSelectListner() {

		@Override
		public void onTimeChange(TextView time, int value, int position,
				DefenceWorkGroup group) {
			switch (position) {
			case 0:
				group.setbBeginHour((byte) value);
				break;
			case 1:
				group.setbBeginMin((byte) value);
				break;
			case 2:
				group.setbEndHour((byte) value);
				break;
			case 3:
				group.setbEndMin((byte) value);
				break;

			default:
				break;
			}
			time.setText(group.getBeginTimeString() + "-"
					+ group.getEndTimeString());
		}

		@Override
		public void onTimeSet(AlertDialog alertDialog, DefenceWorkGroup group,
				int type, int position) {
			if (group.getBeginTime() == group.getEndTime()) {
				T.showShort(mContext, R.string.time_repeat);
				return;
			}
			if (isRepeat(group)) {
				T.showShort(mContext, R.string.time_group_exsite);
				return;
			}
			if (beginTime==group.getBeginTime()&&endTime==group.getEndTime()) {
				T.showShort(mContext, R.string.time_group_exsite);
				return;
			}
			if (group.getBeginTime() > group.getEndTime()) {
				T.showShort(mContext, R.string.time_beyond);
				return;
			}
			alertDialog.dismiss();
			settingsDataInfo(type, position, group);
		}
	};
	
	
	/**
	 * 设置命令方法
	 * 
	 * @param groupCount
	 * @param data
	 */
	private void setDefenceWorkGroup(short groupCount, byte[] data) {
		if (mContact != null) {
			P2PHandler.getInstance().setDefenceWorkGroup(mContact.contactId,
					mContact.contactPassword, groupCount, data);
		}
	}
	
	//展示选择周天的弹框
		private void showSelectDialog(DefenceWorkGroup grop) {
			NormalDialog dialog = new NormalDialog(mContext);
			dialog.setTitle(R.string.fish_repete);
			dialog.setBtn1_str(Utils.getStringForId(R.string.yes));
			dialog.showDefenceSelectListDialog(grop, NormalDialog.SelectType_List);
			dialog.setOnWorkDayDialogSelectListner(workDaySelect);
			weekDayTemp = grop.getbWeekDay();
		}
		
		
		// 多选(设备中是从星期天开始计算)
		/**
		 * weekDay:0 1 2 3 4 5 6 7 周 :日 1 2 3 4 5 6
		 */
		private NormalDialog.onWorkDayDialogSelectListner workDaySelect = new NormalDialog.onWorkDayDialogSelectListner() {
			@Override
			public void onItemClick(View v, SelectItem item, int position) {
				if (position == 6) {
					position = 0;
				} else {
					position++;
				}
				if (item.isSelected()) {
					weekDayTemp = Utils.ChangeBitTrue(weekDayTemp, position);
				} else {
					weekDayTemp = Utils.ChangeByteFalse(weekDayTemp, position);
				}
			}

			@Override
			public void onOkClick(AlertDialog dialog, DefenceWorkGroup grop) {
				dialog.dismiss();
				int index = Groups.indexOf(grop);
				DefenceWorkGroup dwg = grop.clone();
				settingsDataInfo(DATA_CHANGE_WEEK, index, dwg);
			}
		};

		//展示等待弹框的方法
		public void showLoadingDialog(String title) {
			if (dialog_loading == null) {
				dialog_loading = new NormalDialog(mContext, title, "", "", "");
				dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
			} else {
				dialog_loading.setTitle(title);
			}
			dialog_loading.showDialog();
		}
		
		//获取下一个可存放的位置
		public int getGroupIndex() {
			int[] itemArray;
			if (Groups.size() == 0) {
				itemArray = new int[] {};
			} else {
				itemArray = new int[Groups.size() + 1];
				for (int i = 0; i < Groups.size(); i++) {
					itemArray[i] = Groups.get(i).getIndex();
				}
				itemArray[Groups.size()] = biggestLimit;
			}
			return Utils.getNextItem(itemArray);
		}
		
		//判断当前集合中是否有与给的对象一样的时间点
		public boolean isRepeat(DefenceWorkGroup dwg) {
			if (Groups.size() < 1) {
				return false;
			}
			for (DefenceWorkGroup dwgs : Groups) {
				if (dwgs.getBeginTime() == dwg.getBeginTime()
						&& dwgs.getEndTime() == dwg.getEndTime()) {
					return true;
				}
			}
			return false;
		}
		
		
		//得到由时间组集合变为的byte数组
		public byte[] getDataArray(List<DefenceWorkGroup> list) {
			byte[] data = new byte[60];
			for (int i = 0; i < list.size(); i++) {
				byte[] info = list.get(i).getAllInfo();
				System.arraycopy(info, 0, data, (0 + i) * info.length, info.length);
			}
			return data;
		}

		
		//对于广播接收的数据做分类处理
		public void processingDataInfo(int type) {
			switch (type) {
			case DATA_DELETE:
				Groups.remove(changePosition);
				mAdapter.UpdataAll();
				T.showShort(mContext, R.string.clear_success);
				break;
			case DATA_ADD:
				Groups.add(setDefenceWorkGroup.getIndex(),setDefenceWorkGroup);
				mAdapter.UpdataAll();
				T.showShort(mContext, R.string.add_success);
				break;
			case DATA_CHANGE_SWITCH:
				if (isEnable == 1) {
					Groups.get(changePosition).setEnable(true);
				} else if (isEnable == 0) {
					Groups.get(changePosition).setEnable(false);
				}
				Groups.get(changePosition).setBswitch(isEnable);
				mAdapter.updateByPosition(changePosition);
				break;
			case DATA_CHANGE_TIME:
				Groups.remove(changePosition);
				Groups.add(changePosition, setDefenceWorkGroup);
				mAdapter.UpdataAll();
				T.showShort(mContext, R.string.modify_success);
				break;
			case DATA_CHANGE_WEEK:
				Groups.remove(changePosition);
				Groups.add(changePosition, setDefenceWorkGroup);
				mAdapter.UpdataAll();
				T.showShort(mContext, R.string.modify_success);
				break;
			default:
				break;
			}
		}
		
		//对于所有的增删改命令做集中处理
		public void settingsDataInfo(int type,int position,DefenceWorkGroup group){
			byte[] data = null;
			List<DefenceWorkGroup> list = new ArrayList<DefenceWorkGroup>();
			list.addAll(Groups);
			switch (type) {
			case DATA_DELETE:
				showLoadingDialog(mContext.getResources().getString(
						R.string.clearing));
				setType = DATA_DELETE;
				changePosition = position;
				if(position<0||position>=list.size()){
					return;
				}
				list.remove(position);
				data = getDataArray(list);
				break;
			case DATA_ADD:
				showLoadingDialog(mContext.getResources().getString(
						R.string.inserting));
				setType = DATA_ADD;
				setDefenceWorkGroup = group;
				list.add(group.getIndex(),group);
				data=getDataArray(list);
				break;
			case DATA_CHANGE_SWITCH:
				showLoadingDialog(mContext.getResources().getString(
						R.string.update_loading));
				setType = DATA_CHANGE_SWITCH;
				changePosition = position;
				if (group.getBswitch() == 0) {
					isEnable = 1;
					list.get(position).setEnable(true);
				} else if (group.getBswitch() == 1) {
					isEnable = 0;
					list.get(position).setEnable(false);
				} else {
					return;
				}
				Groups.get(position).setBswitch(-1);
				mAdapter.updateByPosition(position);
				data = getDataArray(list);
				setDefenceWorkGroup((short) list.size(), data);
				break;
			case DATA_CHANGE_TIME:
				showLoadingDialog(mContext.getResources().getString(
						R.string.update_loading));
				setType = DATA_CHANGE_TIME;
				changePosition = position;
				setDefenceWorkGroup = group;
				list.remove(position);
				list.add(position, group);
				data = getDataArray(list);
				break;
			case DATA_CHANGE_WEEK:
				showLoadingDialog(mContext.getResources().getString(
						R.string.update_loading));
				setType = DATA_CHANGE_WEEK;
				changePosition = position;
				group.setbWeekDay(weekDayTemp);
				list.remove(position);
				list.add(position, group);
				setDefenceWorkGroup = group;
				data = getDataArray(list);
				setDefenceWorkGroup((short) list.size(), data);
				break;
			default:
				break;
			}
			if (data!=null) {
				setDefenceWorkGroup((short) list.size(), data);
			}
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode==2) {
				if (resultCode==Activity.RESULT_OK) {
					if (data!=null) {
						setDefenceWorkGroup=data.getParcelableExtra("DefenceWorkGroup");
						processingDataInfo(DATA_ADD);
					}
				}
			}
		}
		
		public long[] getBeginArray(){
			long[] beginData=new long[Groups.size()];
			for (int i = 0; i < Groups.size(); i++) {
				beginData[i]=Groups.get(i).getBeginTime();
			}
			return beginData;
		}
		
		public long[] getEndArray(){
			long[] endData=new long[Groups.size()];
			for (int i = 0; i < Groups.size(); i++) {
				endData[i]=Groups.get(i).getEndTime();
			}
			return endData;
		}
		
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		regFilter();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (isRegFilter) {
			isRegFilter = false;
			mContext.unregisterReceiver(mReceiver);
		}
		super.onPause();
	}

}
