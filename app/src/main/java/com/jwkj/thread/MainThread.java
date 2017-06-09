package com.jwkj.thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.Utils;
import com.nuowei.smarthome.MyApplication;
import com.p2p.core.update.UpdateManager;

public class MainThread {
	static MainThread manager;
	boolean isRun;
	private String version;
	private int serVersion;
	private static final long SYSTEM_MSG_INTERVAL = 60 * 60 * 1000;
	long lastSysmsgTime;
	private Main main;
	//屏蔽了检查设备更新
//	private SearchUpdate update;
	Context context;
	private static boolean isOpenThread;

	public MainThread(Context context) {
		manager = this;
		this.context = context;
	}

	public static MainThread getInstance(Context context) {
		if(manager==null){
			manager=new MainThread(context);
		}
		return manager;

	}

	class Main extends Thread {
		@Override
		public void run() {
			isRun = true;
			Utils.sleepThread(3000);
			while (isRun) {
				if (isOpenThread == true) {
					checkUpdate();
					try {
						FList.getInstance().updateOnlineState();
						FList.getInstance().searchLocalDevice();
						FList.getInstance().getModeState();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Utils.sleepThread(20 * 1000);
				} else {
					Utils.sleepThread(10 * 1000);
				}

			}
		}
	};
	
	class SearchUpdate extends Thread {
		@Override
		public void run() {
			Utils.sleepThread(3000);
			while (isRun) {
				if (isOpenThread == true) {
					try {
						FList.getInstance().getCheckUpdate();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Utils.sleepThread(4*60*60 * 1000);
				} else {
					Utils.sleepThread(10 * 1000);
				}
			}
		}
	};

	public void go() {

		if (null == main || !main.isAlive()) {
			main = new Main();
			main.start();
		}
		//屏蔽了检查设备更新的线程
//		if (null == update || !update.isAlive()) {
//			update = new SearchUpdate();
//			update.start();
//		}
	}

	public void kill() {
		isRun = false;
		main = null;
		//屏蔽了检查设备更新的相关
//		update=null;
	}

	public static void setOpenThread(boolean isOpenThread) {
		MainThread.isOpenThread = isOpenThread;
	}

	public void checkUpdate() {
		try {
			long last_check_update_time = SharedPreferencesManager
					.getInstance().getLastAutoCheckUpdateTime(MyApplication.app);
			long now_time = System.currentTimeMillis();

			if ((now_time - last_check_update_time) > 1000 * 60 * 60 * 24) {
				SharedPreferencesManager.getInstance()
						.putLastAutoCheckUpdateTime(now_time, MyApplication.app);
				Log.e("my", "后台检查更新");
				if (UpdateManager.getInstance().checkUpdate(NpcCommon.mThreeNum)) {
					String data = "";
					if (Utils.isZh(MyApplication.app)) {
						data = UpdateManager.getInstance()
								.getUpdateDescription();
					} else {
						data = UpdateManager.getInstance()
								.getUpdateDescription_en();
					}
					Intent i = new Intent(Constants.Action.ACTION_UPDATE);
					i.putExtra("updateDescription", data);
					MyApplication.app.sendBroadcast(i);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("my", "后台检查更新失败");
		}
	}

}
