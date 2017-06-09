package com.jwkj.utils;

import android.app.Service;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.util.Log;

import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemDataManager;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.P2PValue;

import java.util.HashMap;

public class MusicManger {
	private static MusicManger manager = null;
	private static MediaPlayer player;
	private Vibrator vibrator;

	private MusicManger() {
	}

	private boolean isVibrate = false;

	public synchronized static MusicManger getInstance() {
		if (null == manager) {
			synchronized (MusicManger.class) {
				if (null == manager) {
					manager = new MusicManger();
				}
			}
		}
		return manager;
	}

	public void playCommingMusic() {
		if (null != player) {
			return;
		}
		try {
			player = new MediaPlayer();

			int bellType = SharedPreferencesManager.getInstance().getCBellType(
					MyApplication.app);
			HashMap<String, String> data;
			if (bellType == SharedPreferencesManager.TYPE_BELL_SYS) {
				int bellId = SharedPreferencesManager.getInstance()
						.getCSystemBellId(MyApplication.app);
				data = SystemDataManager.getInstance().findSystemBellById(
						MyApplication.app, bellId);
			} else {
				int bellId = SharedPreferencesManager.getInstance()
						.getCSdBellId(MyApplication.app);
				data = SystemDataManager.getInstance().findSdBellById(
						MyApplication.app, bellId);
			}

			String path = data.get("path");
			if (null == path || "".equals(path)) {

			} else {
				player.reset();
				player.setDataSource(path);
				player.setLooping(true);
				player.prepare();
				player.start();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (null != player) {
				player.stop();
				player.release();
				player = null;
			}
		}
	}

	public void playAlarmMusic(int alarm_type) {
		if (null != player) {
			return;
		}
		try {
			player = new MediaPlayer();
			if (alarm_type != P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
				HashMap<String, String> data;
				int bellType = SharedPreferencesManager.getInstance()
						.getABellType(MyApplication.app);

				if (bellType == SharedPreferencesManager.TYPE_BELL_SYS) {

					int bellId = SharedPreferencesManager.getInstance()
							.getASystemBellId(MyApplication.app);
					if (bellId != 0&&bellId != -1) {

						data = SystemDataManager.getInstance()
								.findSystemBellById(MyApplication.app, bellId);
						if (data == null) {
							return;
						}
						String path = data.get("path");
						if (null == path || "".equals(path)) {

						} else {
							player.reset();
							player.setDataSource(path);
							player.setLooping(true);
							player.prepare();
							player.start();
						}
					} else {

						player.reset();
						AssetManager am = MyApplication.app.getAssets();// 获得该应用的AssetManager
						AssetFileDescriptor afd = am.openFd("default.mp3");
						player.setDataSource(afd.getFileDescriptor(),
								afd.getStartOffset(), afd.getLength());
						player.setLooping(true);
						player.prepare(); // 准备
						player.start();
					}

				} else {

					int bellId = SharedPreferencesManager.getInstance()
							.getASdBellId(MyApplication.app);
					data = SystemDataManager.getInstance().findSdBellById(
							MyApplication.app, bellId);
					if (data == null) {
						return;
					}
					String path = data.get("path");
					if (null == path || "".equals(path)) {

					} else {
						player.reset();
						player.setDataSource(path);
						player.setLooping(true);
						player.prepare();
						player.start();
					}
				}
			} else {
				player.reset();
				AssetManager am = MyApplication.app.getAssets();// 获得该应用的AssetManager
				AssetFileDescriptor afd = am.openFd("default2.mp3");
				player.setDataSource(afd.getFileDescriptor(),
						afd.getStartOffset(), afd.getLength());
				player.setLooping(true);
				player.prepare(); // 准备
				player.start();

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (null != player) {
				player.stop();
				player.release();
				player = null;
			}
		}
	}

	// public void playMusic(int bellId) {
	//
	// try {
	// player.reset();
	// if (bellId != 0) {
	// HashMap<String, String> data;
	// data = SystemDataManager.getInstance().findSystemBellById(
	// context, bellId);
	// String path = data.get("path");
	// if (null == path || "".equals(path)) {
	//
	// } else {
	// player.setDataSource(path);
	// player.prepare();
	// player.start();
	// }
	// } else {
	// AssetManager am = cgetAssets();// 获得该应用的AssetManager
	// AssetFileDescriptor afd = am.openFd("jingubang.mp3");
	// player.setDataSource(afd.getFileDescriptor());
	// player.prepare(); // 准备
	// player.start();
	// }
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalStateException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	public void playMsgMusic() {
		try {
			final MediaPlayer msgPlayer = MediaPlayer.create(MyApplication.app,
					R.raw.message);
			// msgPlayer.prepare();
			msgPlayer.start();
			msgPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					msgPlayer.release();
				}
			});
		} catch (Exception e) {
			Log.e("my", "msg music error!");
		}
	}

	public void stop() {
		if (null != player) {
			player.stop();
			player.release();
			player = null;
		}
	}

	public void Vibrate() {
		if (isVibrate) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				isVibrate = true;
				while (isVibrate) {
					if (null == vibrator) {
						vibrator = (Vibrator) MyApplication.app
								.getSystemService(Service.VIBRATOR_SERVICE);
					}
					vibrator.vibrate(400);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void stopVibrate() {
		isVibrate = false;
	}
}
