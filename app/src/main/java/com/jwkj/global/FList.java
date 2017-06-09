package com.jwkj.global;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.Prepoint;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.LocalDevice;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.utils.TcpClient;
import com.jwkj.utils.Utils;
import com.jwkj.utils.WifiUtils;
import com.nuowei.smarthome.MyApplication;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.shake.ShakeManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FList {
	private static FList manager = null;
	private static List<Contact> lists = null;
	private static HashMap<String, Contact> maps = new HashMap<String, Contact>();
	private static List<LocalDevice> localdevices = new ArrayList<LocalDevice>();
	private static List<LocalDevice> tempLocalDevices = new ArrayList<LocalDevice>();
	private static List<LocalDevice> foundLocalDevices = new ArrayList<LocalDevice>();
	private static List<LocalDevice> apdevices = new ArrayList<LocalDevice>();
	static List<LocalDevice> temAplist = new ArrayList<LocalDevice>();
	// 局域网内搜索到的全部的设备
	private static List<LocalDevice> allLocalDevices = new ArrayList<LocalDevice>();

	public FList() {
		if (null != lists) {
			lists.clear();
		}

		if (null != localdevices) {
			localdevices.clear();
		}
		if (null != foundLocalDevices) {
			foundLocalDevices.clear();
		}
		if (null != allLocalDevices) {
			allLocalDevices.clear();
		}
		manager = this;
		lists = DataManager.findContactByActiveUser(MyApplication.app,
				NpcCommon.mThreeNum);
		maps.clear();
		for (Contact contact : lists) {
			maps.put(contact.contactId, contact);
		}
	}

	public static FList getInstance() {
		if (manager == null) {
			manager = new FList();
		}
		return manager;
	}

	public List<Contact> list() {
		return this.lists;
	}

	public HashMap<String, Contact> map() {
		return this.maps;
	}

	public Contact get(int position) {
		if (position >= lists.size()) {
			return null;
		} else {
			return lists.get(position);
		}
	}

	public int getType(String threeNum) {
		Contact contact = maps.get(threeNum);
		if (null == contact) {
			return P2PValue.DeviceType.UNKNOWN;
		} else {
			return contact.contactType;
		}

	}

	public void setType(String threeNum, int type) {
		Contact contact = maps.get(threeNum);
		if (null != contact&&contact.contactType==0) {
			contact.contactType = type;
			DataManager.updateContact(MyApplication.app, contact);
		}
	}

	/**
	 * 更新设备的子类型
	 * @param threeNum 设备ID或者IP
	 * @param subType
     */
	public void setSubType(String threeNum, int subType) {
		Contact contact = maps.get(threeNum);
		if(contact==null){
			String contactId=getIdfromIp(threeNum);
			if(!TextUtils.isEmpty(contactId)){
				contact=maps.get(contactId);
			}
		}
		if(contact!=null){
			contact.subType = subType;
			update(contact);
		}
	}
	//设置设备视屏宽高
	public void setVideowh(String threeNum, int videow,int videoh,int pos) {
		Contact contact = maps.get(threeNum);
		if (null != contact) {
			contact.videow = videow;
			contact.videoh=videoh;
			contact.fishPos=pos;
			update(contact);
		}
	}

	public int getState(String threeNum) {
		Contact contact = maps.get(threeNum);
		if (null == contact) {
			return Constants.DeviceState.OFFLINE;
		} else {
			return contact.onLineState;
		}
	}

	public void setState(String threeNum, int state) {
		Contact contact = maps.get(threeNum);
		if (null != contact) {
			contact.onLineState = state;
		}
	}

	public void setDefenceState(String threeNum, int state) {
		if (threeNum.equals("1")) {
			String wifiName = WifiUtils.getInstance().getConnectWifiName();
			int length = AppConfig.Relese.APTAG.length();
			if (wifiName.length() > 0 && wifiName.charAt(0) == '"') {
				wifiName = wifiName.substring(1, wifiName.length() - 1);
			}
			if (wifiName.startsWith(AppConfig.Relese.APTAG)) {
				String contactId = wifiName
						.substring(length, wifiName.length());
				for (int i = 0; i < apdevices.size(); i++) {
					if (apdevices.get(i).contactId.equals(contactId)) {
						apdevices.get(i).defenceState = state;
						apdevices.get(i).apModeState = Constants.APmodeState.LINK;
						Intent refresh = new Intent();
						refresh.setAction(Constants.Action.REFRESH_CONTANTS);
						MyApplication.app.sendBroadcast(refresh);
						break;
					}
				}
			}
		} else {
			Contact contact = maps.get(threeNum);
			if (null != contact) {
				contact.defenceState = state;
				if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_NET
						|| state == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD
						|| state == Constants.DefenceState.DEFENCE_NO_PERMISSION
						|| state == Constants.DefenceState.DEFENCE_STATE_LOADING) {

				}
			}
		}
	}

	public void setUpdate(String contactId, int state, String cur_version,
			String up_version) {
		Contact contact = maps.get(contactId);
		if (null != contact) {
			contact.Update = state;
			contact.cur_version = cur_version;
			contact.up_version = up_version;
		}
	}

	public void setIsClickGetDefenceState(String threeNum, boolean bool) {
		Contact contact = maps.get(threeNum);
		if (null != contact) {
			contact.isClickGetDefenceState = bool;
		}
	}

	public int size() {
		return lists.size();
	}

	public int apListsize() {
		return apdevices.size();
	}

	public void sort() {
		Collections.sort(lists);
	}

	public String getContactId(String ip) {
		for (int i = 0; i < allLocalDevices.size(); i++) {
			String mack = allLocalDevices.get(i).address.getHostName();
			String ipaddress = mack.substring(mack.lastIndexOf(".") + 1,
					mack.length());
			if (ipaddress.equals(ip)) {
				return allLocalDevices.get(i).contactId;
			}
		}
		return "";
	}

	public void delete(Contact contact, int position, Handler handler) {
		maps.remove(contact.contactId);
		lists.remove(position);
		DataManager.deleteContactByActiveUserAndContactId(MyApplication.app,
				NpcCommon.mThreeNum, contact.contactId);
		DataManager.deletePrepoint(MyApplication.app, contact.contactId);
		if (contact.contactType == P2PValue.DeviceType.DOORBELL) {// 如果删除门铃则绑定标记为否，下次默认绑定报警ID
			SharedPreferencesManager.getInstance().putIsDoorbellBind(
					contact.contactId, false, MyApplication.app);
			SharedPreferencesManager.getInstance().putIsDoorBellToast(
					contact.contactId, false, MyApplication.app);
		} else if (contact.contactType == P2PValue.DeviceType.NVR) {
			DataManager.deleteJAContactByActiveUserAndContactId(MyApplication.app,
					NpcCommon.mThreeNum, contact.contactId);
		}
		handler.sendEmptyMessage(0);

		Intent refreshNearlyTell = new Intent();
		refreshNearlyTell
				.setAction(Constants.Action.ACTION_REFRESH_NEARLY_TELL);
		MyApplication.app.sendBroadcast(refreshNearlyTell);
	}

	public void insert(Contact contact) {
		DataManager.insertContact(MyApplication.app, contact);
		lists.add(contact);
		// 初始化预置位
		if (!maps.containsKey(contact.contactId)) {
			DataManager.insertPrepoint(MyApplication.app, contact.contactId,new Prepoint());// 初始或预置位名称
		}
		maps.put(contact.contactId, contact);
		String[] contactIds = new String[] { contact.contactId };
		P2PHandler.getInstance().getFriendStatus(contactIds);
	}

	public void update(Contact contact) {
		int i = 0;
		for (Contact u : lists) {
			if (u.contactId.equals(contact.contactId)) {
				lists.set(i, contact);
				break;
			}
			i++;
		}

		maps.put(contact.contactId, contact);
		DataManager.updateContact(MyApplication.app, contact);
	}

	public  Contact isContact(String contactId) {
		return maps.get(contactId);
	}

	public synchronized void updateOnlineState() {
		// 获取好友在线状态

		FList flist = FList.getInstance();
		if (flist.size() <= 0) {
			Intent friends = new Intent();
			friends.setAction(Constants.Action.GET_FRIENDS_STATE);
			MyApplication.app.sendBroadcast(friends);
			return;
		}

		String[] contactIds = new String[flist.size()];
		List<Contact> lists = flist.list();
		int i = 0;
		for (Contact contact : lists) {
			contactIds[i] = contact.contactId;
			i++;
		}
		P2PHandler.getInstance().getFriendStatus(contactIds);
	}

	public void getDefenceState() {
		new Thread() {
			public void run() {
				for (int i = 0; i < manager.lists.size(); i++) {
					Contact contact = manager.lists.get(i);
					if ((contact.contactType == P2PValue.DeviceType.DOORBELL
							|| contact.contactType == P2PValue.DeviceType.IPC || contact.contactType == P2PValue.DeviceType.NPC)) {
						P2PHandler.getInstance().getDefenceStates(
								contact.contactId, contact.contactPassword);
					}
					if (contact.contactType == P2PValue.DeviceType.NVR) {
						// 获取JA info(逻辑改为点击时获取)
						// P2PHandler.getInstance().getNVRInfo(contact.contactId,
						// contact.contactPassword);
					}
				}
			}
		}.start();

	}
	//不应该被调用
	public void getCheckUpdate() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < manager.list().size(); i++) {
					Contact contact = manager.list().get(i);
					if ((contact.contactType == P2PValue.DeviceType.DOORBELL
							|| contact.contactType == P2PValue.DeviceType.IPC || contact.contactType == P2PValue.DeviceType.NPC)) {
						if (contact.Update != Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_VERSION
								&& contact.Update != Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_IN_SD) {
							P2PHandler.getInstance().checkDeviceUpdate(
									contact.contactId, contact.contactPassword);
						}
					}
				}
			}

		}.start();
	}

	public synchronized void searchLocalDevice() {
		try {
			ShakeManager.getInstance().setSearchTime(5000);
			ShakeManager.getInstance().setInetAddress(
					Utils.getIntentAddress(MyApplication.app));
			ShakeManager.getInstance().setHandler(mHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (ShakeManager.getInstance().shaking()) {
			tempLocalDevices.clear();
		}
		WifiUtils.getInstance().setHandler(mHandler);
		WifiUtils.getInstance().getAPmodeDevice();
		getApModeDefenceState();
	}

	public void getApModeDefenceState() {
		P2PHandler.getInstance().getDefenceStates("1", "0");
	}

	public static void updateLocalDeviceWithLocalFriends() {
		List<LocalDevice> removeList = new ArrayList<LocalDevice>();
		for (LocalDevice localDevice : localdevices) {
			if (null != manager.isContact(localDevice.getContactId())
					&& localDevice.flag != Constants.DeviceFlag.AP_MODE) {
				removeList.add(localDevice);
			}
		}
		for (LocalDevice localDevice : removeList) {
			localdevices.remove(localDevice);
		}
	}

	public void updateApModelist() {
		List<LocalDevice> removeList = new ArrayList<LocalDevice>();
		for (LocalDevice localDevice : apdevices) {
			if (null != manager.isContact(localDevice.getContactId())) {
				removeList.add(localDevice);
			}
		}
		for (LocalDevice localDevice : removeList) {
			apdevices.remove(localDevice);
		}
	}

	public List<LocalDevice> getLocalDevices() {
		return this.localdevices;
	}

	// 获取局域网收索的设备 排除AP设备
	public List<LocalDevice> getLocalDevicesNoAP() {
		List<LocalDevice> localdevi = new ArrayList<LocalDevice>();
		for (LocalDevice device : localdevices) {
			if (device.flag != Constants.DeviceFlag.AP_MODE) {
				localdevi.add(device);
			}
		}
		return localdevi;
	}

	public List<LocalDevice> getUnsetPasswordLocalDevices() {
		List<LocalDevice> datas = new ArrayList<LocalDevice>();

		for (LocalDevice device : this.localdevices) {
			int flag = device.flag;
			if (flag == Constants.DeviceFlag.UNSET_PASSWORD
					&& null == this.isContact(device.contactId)) {
				datas.add(device);
			}
		}
		return datas;
	}

	public String getCompleteIPAddress(String contactid) {
		String ip = "";
		for (LocalDevice ss : tempLocalDevices) {
			if (ss.contactId.equals(contactid)) {
				ip = ss.address.getHostAddress();
			}
		}
		return ip;
	}

	public List<LocalDevice> getSetPasswordLocalDevices() {
		List<LocalDevice> datas = new ArrayList<LocalDevice>();
		for (LocalDevice device : this.localdevices) {
			int flag = device.flag;
			if (flag == Constants.DeviceFlag.ALREADY_SET_PASSWORD
					&& null == this.isContact(device.contactId)) {
				datas.add(device);
			}
		}
		return datas;
	}

	/**
	 * 获得AP模式设备
	 * 
	 * @return
	 */
	public static List<LocalDevice> getAPModeLocalDevices() {
		for (int j = 0; j < temAplist.size(); j++) {
			LocalDevice device = temAplist.get(j);
			for (int i = 0; i < apdevices.size(); i++) {
				if (device.contactId.equals(apdevices.get(i).contactId)) {
					device.apModeState = apdevices.get(i).apModeState;
					device.defenceState = apdevices.get(i).defenceState;
					break;
				}
			}
		}
		apdevices.clear();
		for (LocalDevice device : temAplist) {
			apdevices.add(device);
		}
		Intent i = new Intent();
		i.setAction(Constants.Action.REFRESH_CONTANTS);
		MyApplication.app.sendBroadcast(i);
		return apdevices;
	}

	public LocalDevice getAPDdeviceByPosition(int position) {
		Log.e("position", "ap_position=" + position);
		if (position >= apdevices.size()) {
			return null;
		} else {
			return apdevices.get(position);
		}
	}

	public LocalDevice isContactUnSetPassword(String contactId) {
		if (null == this.isContact(contactId)) {
			return null;
		}

		for (LocalDevice device : this.foundLocalDevices) {
			if (device.contactId.equals(contactId)) {
				if (device.flag == Constants.DeviceFlag.UNSET_PASSWORD) {
					return device;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	public void updateLocalDeviceFlag(String contactId, int flag) {
		for (LocalDevice device : this.foundLocalDevices) {
			if (device.contactId.equals(contactId)) {
				device.flag = flag;
				return;
			}
		}
	}

	public InetAddress getLocalDeviceIp(String id) {
		for (int i = 0; i < allLocalDevices.size(); i++) {
			if (allLocalDevices.get(i).contactId.equals(id)) {
				// String mark=allLocalDevices.get(i).address.getHostAddress();
				// return mark.substring(mark.lastIndexOf(".")+1,mark.length());
				return allLocalDevices.get(i).address;
			}
		}
		return null;

	}

	// public String getLocalDeviceIp(String id) {
	// for (int i = 0; i < allLocalDevices.size(); i++) {
	// if (allLocalDevices.get(i).contactId.equals(id)) {
	// String mark = allLocalDevices.get(i).address.getHostAddress();
	// return mark.substring(mark.lastIndexOf(".") + 1, mark.length());
	// }
	// }
	// return "";
	//
	// }

	private static Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case ShakeManager.HANDLE_ID_SEARCH_END:
				localdevices.clear();
				allLocalDevices.clear();
				foundLocalDevices.clear();
				for (LocalDevice localDevice : tempLocalDevices) {
					localdevices.add(localDevice);
					foundLocalDevices.add(localDevice);
					allLocalDevices.add(localDevice);
				}
				for (int i = 0; i < lists.size(); i++) {
					if (!searchLocalContact(lists.get(i).contactId)) {
						lists.get(i).ipadressAddress = null;
					}
				}
				updateLocalDeviceWithLocalFriends();
				Intent i = new Intent();
				i.setAction(Constants.Action.LOCAL_DEVICE_SEARCH_END);
				MyApplication.app.sendBroadcast(i);
				break;
			case ShakeManager.HANDLE_ID_RECEIVE_DEVICE_INFO:
				Bundle bundle = msg.getData();
				String id = bundle.getString("id");
				String name = bundle.getString("name");
				int flag = bundle.getInt("flag",
						Constants.DeviceFlag.ALREADY_SET_PASSWORD);
				int type = bundle.getInt("type", P2PValue.DeviceType.UNKNOWN);

                int rflag=bundle.getInt("rtspflag", 0);
                int rtspflag=(rflag>>2)&1;
                int subType=bundle.getInt("subType");
                int curVersion=(rflag>>4)&0x1;
				InetAddress address = (InetAddress) bundle
						.getSerializable("address");
				LocalDevice localDevice = new LocalDevice();
				localDevice.setContactId(id);
				localDevice.setFlag(flag);
				localDevice.setType(type);
				localDevice.setAddress(address);
				localDevice.setName(name);
				localDevice.setRtspFrag(rtspflag);
				if(curVersion==1){
				    localDevice.setSubType(subType);
				}
				
				if (!tempLocalDevices.contains(localDevice)
						&& localDevice.type != P2PValue.DeviceType.NVR) {
					tempLocalDevices.add(localDevice);
				}
				String mark = address.getHostAddress();
				String ip = mark.substring(mark.lastIndexOf(".") + 1,
						mark.length());
				Contact c = FList.getInstance().isContact(id);
				if(c!=null){
					 c.ipadressAddress=address;
					 c.rtspflag=rtspflag;
				    if(curVersion==1){
					  c.subType=subType;
					  FList.getInstance().update(c);
				    }
					 
				}
				break;
			case ShakeManager.HANDLE_ID_ONEAPDEVICE:
				Bundle bundle1 = msg.getData();
				String id1 = bundle1.getString("id");
				String name1 = bundle1.getString("name");
				int flag1 = bundle1.getInt("flag",
						Constants.DeviceFlag.ALREADY_SET_PASSWORD);
				int type1 = bundle1.getInt("type", P2PValue.DeviceType.UNKNOWN);
				int rflag1 = bundle1.getInt("rtspflag", 0);
				int rtspflag1 = (rflag1 >> 2) & 1;
				InetAddress address1 = (InetAddress) bundle1
						.getSerializable("address");
				LocalDevice localDevice1 = new LocalDevice();
				localDevice1.setContactId(id1);
				localDevice1.setFlag(flag1);
				localDevice1.setType(type1);
				localDevice1.setAddress(address1);
				localDevice1.setName(name1);
				localDevice1.setRtspFrag(rtspflag1);
				temAplist.add(localDevice1);
				break;
			case ShakeManager.HANDLE_ID_APDEVICE_END:
				getAPModeLocalDevices();
				temAplist.clear();
				WifiUtils.getInstance().wifiUnlock();
				break;

			}
			return false;
		}
	});

	public void gainDeviceMode(String id) {
		for (int i = 0; i < apdevices.size(); i++) {
			if (apdevices.get(i).contactId.equals(id)) {
				TcpClient tcpClient = new TcpClient(Utils.gainWifiMode());
				try {
					tcpClient.setIpdreess(InetAddress.getByName(Utils.getAPDeviceIp(MyApplication.app)));
					tcpClient.setCallBack(myHandler);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tcpClient.createClient();
				break;
			}
		}
	}

	public boolean isApMode(String id) {
		for (int i = 0; i < apdevices.size(); i++) {
			if (apdevices.get(i).contactId.equals(id)
					&& apdevices.get(i).flag == Constants.DeviceFlag.AP_MODE) {
				return true;
			}
		}
		return false;
	}

	public static Handler myHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case TcpClient.SEARCH_AP_DEVICE:
				Bundle bundle = msg.getData();
				String id = bundle.getString("contactId");
				String wifiName = WifiUtils.getInstance().getConnectWifiName();
				if (wifiName.length() > 0 && wifiName.charAt(0) == '"') {
					wifiName = wifiName.substring(1, wifiName.length() - 1);
				}
				int length = AppConfig.Relese.APTAG.length();
				if (wifiName.startsWith(AppConfig.Relese.APTAG)) {
					String contactId = wifiName.substring(length,
							wifiName.length());
					if (id.equals(contactId)) {
						FList.getInstance().setIsConnectApWifi(contactId, true);
					}
				}
				for (int i = 0; i < apdevices.size(); i++) {
					if (apdevices.get(i).contactId.equals(id)) {
						apdevices.get(i).flag = Constants.DeviceFlag.AP_MODE;
						apdevices.get(i).apModeState = Constants.APmodeState.LINK;
						P2PHandler.getInstance().getDefenceStates("1", "0");
					} else {
						apdevices.get(i).apModeState = Constants.APmodeState.UNLINK;
					}
				}
				break;

			default:
				break;
			}
			return false;
		}
	});

	public void setIsConnectApWifi(String id, boolean isApWifi) {
		for (int i = 0; i < lists.size(); i++) {
			Contact contact = lists.get(i);
			contact.isConnectApWifi = isApWifi;
		}
		for (int i = 0; i < apdevices.size(); i++) {
			if (apdevices.get(i).contactId.equals(id)) {
				apdevices.get(i).flag = Constants.DeviceFlag.AP_MODE;
				apdevices.get(i).apModeState = Constants.APmodeState.LINK;
				P2PHandler.getInstance().getDefenceStates("1", "0");
			} else {
				apdevices.get(i).apModeState = Constants.APmodeState.UNLINK;
			}
		}
		Intent it = new Intent();
		it.setAction(Constants.Action.REFRESH_CONTANTS);
		MyApplication.app.sendBroadcast(it);
	}

	public void setAllApUnLink() {
		for (int i = 0; i < apdevices.size(); i++) {
			apdevices.get(i).apModeState = Constants.APmodeState.UNLINK;
		}
		Intent it = new Intent();
		it.setAction(Constants.Action.REFRESH_CONTANTS);
		MyApplication.app.sendBroadcast(it);
	}

	public void setIsConnectApWifi(boolean isApWifi) {
		for (int i = 0; i < lists.size(); i++) {
			Contact contact = lists.get(i);
			contact.isConnectApWifi = isApWifi;
		}
		Intent it = new Intent();
		it.setAction(Constants.Action.REFRESH_CONTANTS);
		MyApplication.app.sendBroadcast(it);
	}

	// 搜索到的列表中是否有联系人
	public static boolean searchLocalContact(String contactId) {
		boolean isHas = false;
		for (int i = 0; i < allLocalDevices.size(); i++) {
			LocalDevice d = allLocalDevices.get(i);
			if (d.contactId.equals(contactId)) {
				isHas = true;
				return isHas;
			}
		}
		return isHas;
	}

	public void gainIsApMode(String contactId) {
		TcpClient tcpClient = new TcpClient(Utils.gainWifiMode());
		try {
			tcpClient.setIpdreess(InetAddress.getByName(Utils.getAPDeviceIp(MyApplication.app)));
			tcpClient.setCallBack(myHandler);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tcpClient.createClient();
	}

	/**
	 * 获取设备工作模式
	 */
	public void getModeState() {
		new Thread() {
			@Override
			public void run() {
				int appID = Integer.parseInt(NpcCommon.mThreeNum);
				for (int i = 0; i < FList.lists.size(); i++) {
					Contact contact = FList.lists.get(i);
					if (contact.isSmartHomeContatct()) {
						FisheyeSetHandler.getInstance().sGetCurrentWorkMode(
								contact.contactId, contact.contactPassword);
					}
				}
			}
		}.start();
	}

	/**
	 * 更新设备工作模式并返回列表中的位置
	 * 
	 * @param id
	 *            设备号
	 * @param state
	 *            工作模式
	 * @return 列表中的位置
	 */
	public int updateDeviceModeState(String id, int state) {
		Contact contact = maps.get(id);
		if (contact != null) {
			contact.FishMode = state;
		}
		Intent i = new Intent();
		i.setAction(Constants.Action.REFRESH_CONTANTS);
		MyApplication.app.sendBroadcast(i);
		return 0;
	}

	/**
	 * 获取设备在列表中的位置
	 * 
	 * @param contact
	 *            设备对象
	 * @return 列表中的位置
	 */
	public int getContactPosition(Contact contact) {
		return lists.indexOf(contact);
	}
	//空间换时间?
	private static Set<String> OffContactTemp=new HashSet<>();
	private static Set<String> OnLineContactTemp =new HashSet<>();
	private static Set<String> ContactChangeTemp=new HashSet<>();
	public void frushContact(int count, String[] contactIDs,int[] status, int[] types,boolean hasFilish){
		//每次将在线的设备更新到UI,第二次不在理睬数据
        Log.e("dxsTest","count:"+count+" ids:"+ Arrays.toString(contactIDs)+" status:"+Arrays.toString(status));
		ContactChangeTemp.clear();
		for(int i=0,leng=status.length;i<leng;i++){
			if(!OnLineContactTemp.contains(contactIDs[i])){
				if(status[i]==Constants.DeviceState.OFFLINE){
					OffContactTemp.add(contactIDs[i]);
				}else{
					ContactChangeTemp.add(contactIDs[i]);
					//仅能更新在线状态的设备大类型
					setType(contactIDs[i],types[i]);
				}
			}
		}
        //结束时将无type的设备的type同步到数据库中
        //维护一个离线设备ID列表，结束时更新到UI上
        OffContactTemp.removeAll(ContactChangeTemp);
        FrushUI(ContactChangeTemp,Constants.DeviceState.ONLINE);
		if(hasFilish){
			FrushUI(OffContactTemp,Constants.DeviceState.OFFLINE);
			OffContactTemp.clear();
			OnLineContactTemp.clear();
		}
        Intent friends = new Intent();
        friends.setAction(Constants.Action.GET_FRIENDS_STATE);
        MyApplication.app.sendBroadcast(friends);
		OnLineContactTemp.addAll(ContactChangeTemp);
	}

	private void FrushUI(Set<String> ids,int type){
       for(String id:ids){
		   setState(id,type);
	   }
	}

	/**
	 * 通过IP地址找ID号
	 * @param ip
	 * @return
     */
	public String getIdfromIp(String ip){
		if(TextUtils.isEmpty(ip)){
			return "";
		}
		for(Contact contact:lists){
			if(ip.equals(contact.getIpMark())){
				return contact.contactId;
			}
		}
		return "";
	}

}
