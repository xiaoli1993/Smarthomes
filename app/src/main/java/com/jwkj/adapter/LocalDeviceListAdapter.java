package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.activity.AddApDeviceActivity;
import com.jwkj.activity.AddContactNextActivity;
import com.jwkj.data.APContact;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.Utils;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.PromptDialog;
import com.p2p.core.P2PValue;
import com.nuowei.smarthome.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class LocalDeviceListAdapter extends BaseAdapter {
	List<LocalDevice> datas;
	Context mContext;
	private int addDeviceMethod=Constants.AddDeviceMethod.LAN_ADD;

	public LocalDeviceListAdapter(Context context) {
		datas = FList.getInstance().getLocalDevicesNoAP();
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return datas.size()+1;
	}

	@Override
	public LocalDevice getItem(int arg0) {
		// TODO Auto-generated method stub
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public int getItemViewType(int position) {
		if(position<datas.size()){
			return 0;
		}else{
			return 1;
		}
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
    public  void setAddDeviceMethod(int addDeviceMethod){
		this.addDeviceMethod=addDeviceMethod;
	}
	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		int type=getItemViewType(position);
		if(type==0){
			View view = arg1;
			if (null == view) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.list_item_local_device, null);
			}
			TextView name = (TextView) view.findViewById(R.id.tx_device_id);
			ImageView typeImg = (ImageView) view.findViewById(R.id.img_type);
			TextView text_ip_address = (TextView) view
					.findViewById(R.id.tv_device_ip);

			final LocalDevice localDevice = datas.get(position);
			if (localDevice.flag == Constants.DeviceFlag.AP_MODE) {
				name.setText(localDevice.getName());
			} else {
				name.setText(localDevice.getContactId());

			}
			text_ip_address.setText(localDevice.address.getHostAddress());
			switch (localDevice.getType()) {
				case P2PValue.DeviceType.NPC:
					typeImg.setImageResource(R.drawable.ic_device_type_npc);
					break;
				case P2PValue.DeviceType.IPC:
					typeImg.setImageResource(R.drawable.ic_device_type_ipc);
					break;
				case P2PValue.DeviceType.DOORBELL:
					typeImg.setImageResource(R.drawable.ic_device_type_door_bell);
					break;
				case P2PValue.DeviceType.UNKNOWN:
			typeImg.setImageResource(R.drawable.ic_device_type_unknown);
					break;
				case P2PValue.DeviceType.NVR:
					typeImg.setImageResource(R.drawable.ic_device_type_nvr);
					break;
				default:
			typeImg.setImageResource(R.drawable.ic_device_type_unknown);
					break;
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Contact saveContact = new Contact();
					saveContact.contactId = localDevice.contactId;
					saveContact.contactName = localDevice.name;
					saveContact.contactType = localDevice.type;
					saveContact.contactFlag = localDevice.flag;
					saveContact.messageCount = 0;
					saveContact.activeUser = NpcCommon.mThreeNum;
					saveContact.rtspflag=localDevice.rtspFrag;
					saveContact.subType=localDevice.subType;
					String mark = localDevice.address.getHostAddress();
					saveContact.ipadressAddress=localDevice.address;
					Intent modify = new Intent();
					if (localDevice.getFlag() == Constants.DeviceFlag.ALREADY_SET_PASSWORD) {
						modify.setClass(mContext, AddContactNextActivity.class);
						modify.putExtra("isCreatePassword", false);
					} else if (localDevice.getFlag() == Constants.DeviceFlag.UNSET_PASSWORD) {
						modify.setClass(mContext, AddContactNextActivity.class);
						modify.putExtra("isCreatePassword", true);
					} else {
						try {
							saveContact.ipadressAddress = InetAddress
									.getByName(Utils.getAPDeviceIp(mContext));
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						modify.setClass(mContext, AddApDeviceActivity.class);
						modify.putExtra("isCreatePassword", false);
						Log.e("dxswifi", "saveContact.contactId-->"
								+ saveContact.contactId
								+ "---saveContact.contactName"
								+ saveContact.contactName);
						if (WifiUtils.getInstance().isConnectWifi(
								saveContact.contactName)) {
							APContact cona = DataManager
									.findAPContactByActiveUserAndContactId(
											mContext, NpcCommon.mThreeNum,
											saveContact.contactId);
							if (cona != null && cona.Pwd != null
									&& cona.Pwd.length() > 0) {
								saveContact.contactPassword = cona.Pwd;
								modify.putExtra("isAPModeConnect", 1);
							} else {
								modify.putExtra("isAPModeConnect", 0);
							}
						} else {
							modify.putExtra("isAPModeConnect", 0);
						}
					}
					modify.putExtra("contact", saveContact);
					modify.putExtra("ipFlag", mark.substring(
							mark.lastIndexOf(".") + 1, mark.length()));
					modify.putExtra("ip", mark);
					modify.putExtra("addDeviceMethod",addDeviceMethod);
					mContext.startActivity(modify);
				}

			});
			return view;
		}else{
			View view=arg1;
			final MainAdapter.ViewHolder3 holder3;
			if(view==null) {
				view = LayoutInflater.from(mContext).inflate(R.layout.list_item_local_no_device, null);
			}
				TextView tv_no_device = (TextView) view.findViewById(R.id.tv_no_device);
				tv_no_device.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			    tv_no_device.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						PromptDialog promptDialog=new PromptDialog(mContext);
						View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_device_not_in_list,null);
						promptDialog.setTitle(mContext.getResources().getString(R.string.device_not_in_list));
						promptDialog.addView(v);
						promptDialog.show();
					}
				});
			return view;
		}
	}

	public void updateData(List<LocalDevice> datas) {
		this.datas =datas;
		this.notifyDataSetChanged();
	}
}
