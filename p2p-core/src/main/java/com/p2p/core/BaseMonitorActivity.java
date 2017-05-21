package com.p2p.core;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.p2p.core.MediaPlayer.IVideoPTS;
import com.p2p.core.global.Constants;
import com.p2p.core.utils.MyUtils;

import java.io.IOException;

public abstract class BaseMonitorActivity extends BaseCoreActivity implements IVideoPTS{
	private final int MSG_SHOW_CAPTURERESULT = 0x00002;
	public static int mVideoFrameRate = 15;
	private final int MINX = 50;
	private final int MINY = 25;
	private final int USR_CMD_OPTION_PTZ_TURN_LEFT = 0;
	private final int USR_CMD_OPTION_PTZ_TURN_RIGHT = 1;
	private final int USR_CMD_OPTION_PTZ_TURN_UP = 2;
	private final int USR_CMD_OPTION_PTZ_TURN_DOWN = 3;
	public P2PView pView;
	boolean isBaseRegFilter = false;
	public boolean isFullScreen = false;
	public boolean isLand = true;// 是否全屏
	public boolean isHalfScreen=true;
	private int PrePoint=-1;
	public boolean bFlagPanorama = false;
	
	private String TAG = "BaseMonitorActivity" ;
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_SHOW_CAPTURERESULT:
					if (msg.arg1 == 1) {
						onCaptureScreenResult(true,PrePoint);
					} else {
						onCaptureScreenResult(false,PrePoint);
					}
					break;
				default:
					break;
			}
			return false;
		}
	});

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		baseRegFilter();
		MediaPlayer.getInstance().setCaptureListener(
				new CaptureListener(mHandler));
		MediaPlayer.getInstance().setVideoPTSListener(this);
		String mac = MyUtils.getLocalMacAddress(this);
		String imei = MyUtils.getIMEI(this);
		//MediaPlayer.native_init_hardMessage(mac, imei);
	}
    @Deprecated
	public void initP2PView(int type) {
		pView.setCallBack();
		pView.setGestureDetector(new GestureDetector(this,
				new GestureListener(), null, true));
		pView.setDeviceType(type);
	}

	/**
	 * 初始化P2PView
	 * @param type 宽高类型
	 * @param layoutType  布局类型默认分离使用（call与P2Pview分开）
     */
	public void initP2PView(int type,int layoutType){
		if(pView!=null){pView.setLayoutType(layoutType);}
		initP2PView(type);
	}

	public void initScaleView(Activity activity, int windowWidth,
			int windowHeight) {
		pView.setmActivity(activity);
		pView.setScreen_W(windowHeight);
		pView.setScreen_H(windowWidth);
		pView.initScaleView();
	}

	private void baseRegFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
		this.registerReceiver(baseReceiver, filter);
		isBaseRegFilter = true;
	}

	public void setMute(boolean bool) {
		try {
			MediaPlayer.getInstance()._SetMute(bool);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//可供子类使用
	public void DoubleTap(MotionEvent e)
	{   
		//四画面才响应
		if(pView!=null&&pView.isPanorama()&&pView.getShapeType()==P2PView.SHAPE_QUAD){
			if(pView.getIsFourFace()){
				pView.ZoomOutPanom(e.getX(), e.getY());
			}else{
				pView.ZoomINPanom(e.getX(), e.getY());
			}
		}	
	}

	public void setIsLand(boolean isLan) {
		this.isLand = isLan;
	}
    public void setHalfScreen(boolean isHalfScreen){
    	this.isHalfScreen=isHalfScreen;
    }
	private BroadcastReceiver baseReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(
					Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START)) {
				final MediaPlayer mPlayer = MediaPlayer.getInstance();
				new Thread(new Runnable() {
					@Override
					public void run() {
						MediaPlayer.nativeInit(mPlayer);
						try {
							mPlayer.setDisplay(pView);
						} catch (IOException e) {
							e.printStackTrace();
						}
						mPlayer.start(mVideoFrameRate);
						setMute(true);
					}
				}).start();
			}
		}
	};

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
		private String TAG = "GestureListener";


		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			onP2PViewSingleTap();
			try {
				MediaPlayer.getInstance()._OnGesture(0, 1, 0, 0);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (isLand&&!isHalfScreen) {
				if (!isFullScreen) {
					isFullScreen = true;
					pView.fullScreen();
				} else {
					isFullScreen = false;
					pView.halfScreen();
				}
			}
			DoubleTap(e);
			return super.onDoubleTap(e);
		}
		
		@Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,  float distanceX, float distanceY) {
//			Log.d(TAG, "onScroll event1 = "+ e1 + "  event2 = "+ e2+  " distanceX = "+ distanceX + " distanceY = "+ distanceY+ "\n");
			
            return false;
        }
		
		@Override
        public void onLongPress(MotionEvent e) {
			
			Log.d(TAG, "onLongPress >>>" );
			
			try {
				MediaPlayer.getInstance()._OnGesture(3, 1, e.getX(), e.getY());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if(pView!=null&&pView.isPanorama()&&pView.getShapeType()==P2PView.SHAPE_QUAD){
				pView.setIsFourFace(false);
			}
        }
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int id = -1;
			float distance = 0;
			boolean ishorizontal = false;
			if ((Math.abs(e2.getX() - e1.getX())) > (Math.abs(e2.getY()
					- e1.getY()))) {
				ishorizontal = true;
			}

			if (ishorizontal) {
				distance = e2.getX() - e1.getX();
				if (Math.abs(distance) > MyUtils.dip2px(
						BaseMonitorActivity.this, MINX)) {
					if (distance > 0) {
						id = USR_CMD_OPTION_PTZ_TURN_RIGHT;
					} else {
						id = USR_CMD_OPTION_PTZ_TURN_LEFT;
					}
				}
			} else {
				distance = e2.getY() - e1.getY();
				if (Math.abs(distance) > MyUtils.dip2px(
						BaseMonitorActivity.this, MINY)) {
					if (distance > 0) {
						id = USR_CMD_OPTION_PTZ_TURN_UP;
					} else {
						id = USR_CMD_OPTION_PTZ_TURN_DOWN;
					}
				}
			}

			if (id != -1) {
				MediaPlayer.getInstance().native_p2p_control(id);
			} else {
			}
			
			try {
				MediaPlayer.getInstance()._OnGesture(2, 1, velocityX, velocityY);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return true;
		}
	}

	private class CaptureListener implements MediaPlayer.ICapture {
		Handler mSubHandler;

		public CaptureListener(Handler handler) {
			mSubHandler = handler;
		}

		@Override
		public void vCaptureResult(int result) {
			Message msg = new Message();
			msg.what = MSG_SHOW_CAPTURERESULT;
			msg.arg1 = result;
			mSubHandler.sendMessage(msg);
		}
	}

	/**
	 * -1是普通截图，0~4是预置位截图
	 * @param prePoint
	 */
	public void captureScreen(int prePoint) {
		this.PrePoint=prePoint;
		onPreCapture(1,prePoint);
		try {
			MediaPlayer.getInstance()._CaptureScreen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPanorama(boolean  bFlag) {
		bFlagPanorama=bFlag;
		pView.setPanorama(bFlag);
		try {
			MediaPlayer.getInstance()._setPanorama(bFlag);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isBaseRegFilter) {
			this.unregisterReceiver(baseReceiver);
			isBaseRegFilter = false;
		}
	}
	
	@Override
	public void vVideoPTS(long videoPTS) {
		if(bFlagPanorama){
			onVideoPTS(videoPTS);
		}
	}
	
	@Override
	public void vSendRendNotify(int MsgType, int MsgAction) {
		if(bFlagPanorama&&pView!=null){
			pView.FilpAction(MsgType, MsgAction);
		}
		
	}
 
	protected abstract void onP2PViewSingleTap();
	protected abstract void onCaptureScreenResult(boolean isSuccess,int prePoint);
	protected abstract void onVideoPTS(long videoPTS);
	public void onPreCapture(int mark,int prepoint){
		
	}
	
}
