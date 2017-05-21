package com.p2p.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.RelativeLayout;

import com.p2p.core.GestureDetector.OnZoomInListener;
import com.p2p.core.global.Constants;

import java.io.IOException;

public class P2PView extends BaseP2PView {
	static final String TAG = "P2PView";
	public static final int SHAPE_CIRCLE = 0;
	public static final int SHAPE_180_HALF_SPHERE = 1;
	public static final int SHAPE_CYLIDER = 2;
	public static final int SHAPE_BOWLE = 3;
	public static final int SHAPE_QUAD = 4;
	public static final int LAYOUTTYPE_SEPARATION=0;//分离使用
	public static final int LAYOUTTYPE_TOGGEDER=1;//一起
	Context mContext;
	MediaPlayer mPlayer;
	private int mWidth, mHeight;
	boolean isInitScreen = false;
	protected GestureDetector mGestureDetector;
	int deviceType;
	int mWindowWidth, mWindowHeight;
	public int fgFullScreen = 0;
	boolean isInitScale;					
	public static boolean SUPPORT_ZOOM =true;
	public static boolean SUPPORT_ZOOM_FOCUS=false;
	public static int type=0;
	public static int scale=0;
//	public static String contactId;
//	public static String password;
	private boolean isPanorama=false;
//	private RenderThread renderThread;
	private boolean isFourFace=false;
	private int ShapeType=0;
	private int layoutType=0;
	public P2PView(Context context) {
		super(context);
		this.mContext = context;
		mPlayer = MediaPlayer.getInstance();
		
	}

	public P2PView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mPlayer = MediaPlayer.getInstance();
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		mWindowWidth = dm.widthPixels;
		mWindowHeight = dm.heightPixels;
//		renderThread=new RenderThread();
	}

	public void initScaleView() {
		isInitScale = true;
	}

	private void vSetWindow() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		mWindowWidth = dm.widthPixels;
		mWindowHeight = dm.heightPixels;

		Log.e("my", "xWidth:" + mWindowWidth + " xHeight:" + mWindowHeight);
		mWidth = mWindowWidth;
		mHeight = mWindowHeight;
		//如果type==1，type等于其它值时，通过npc和ipc来判断
        //scale：0 代表4：3     1 代表16：9
		if (fgFullScreen == 0) {
			if(type==1){
				if (scale==0) {
					int Rate, Rate2;
					Rate = mWidth * 1024 / mHeight;
					Rate2 = 4 * 1024 / 3;
					if (Rate > Rate2) {
						mWidth = mHeight * 4 / 3;
					} else {
						mHeight = mWidth * 3 / 4;
					}
					// fullScreen();//为何要全屏
				} else if(scale==1){
					int Rate, Rate2;
					Rate = mWidth * 1024 / mHeight;
					Rate2 = 16 * 1024 / 9;
					if (Rate > Rate2) {
						mWidth = mHeight * 16 / 9;
					} else {
						mHeight = mWidth * 9 / 16;
					}
				}else{
					int Rate, Rate2;
					Rate = mWidth * 1024 / mHeight;
					Rate2 =  1024 ;
					if (Rate > Rate2) {
						mWidth = mHeight;
					} else {
						mHeight = mWidth;
					}
				}
			}else{
				if (deviceType == P2PValue.DeviceType.NPC) {
					int Rate, Rate2;
					Rate = mWidth * 1024 / mHeight;
					Rate2 = 4 * 1024 / 3;
					if (Rate > Rate2) {
						mWidth = mHeight * 4 / 3;
					} else {
						mHeight = mWidth * 3 / 4;
					}

					// fullScreen();//为何要全屏
				} else {
					int Rate, Rate2;
					Rate = mWidth * 1024 / mHeight;
					Rate2 = 16 * 1024 / 9;
					if (Rate > Rate2) {
						mWidth = mHeight * 16 / 9;
					} else {
						mHeight = mWidth * 9 / 16;
					}
				}
			}
		}

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
		layoutParams.width = mWidth;
		layoutParams.height = mHeight;
		// layoutParams.leftMargin = (mWindowWidth - mWidth) / 2;
		// layoutParams.topMargin = (mWindowHeight - mHeight) / 2;
		setLayoutParams(layoutParams);
		mPlayer.ChangeScreenSize(mWidth, mHeight, fgFullScreen);
		Log.e("dxslayout","mWidth---"+mWidth+"mHeight---"+mHeight);
	}

	public void updateScreenOrientation() {
		vSetWindow();
	}

	public void setCallBack() {
		MediaPlayer.setEglView(this);
		getHolder().addCallback(mSHCallback);
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		mGestureDetector = gestureDetector;
	}

	public void setDeviceType(int type) {
		this.deviceType = type;
	}

	
	float startPointslength ;
	float endPointslength ;
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!(super.onTouchEvent(event))) {
			if (mGestureDetector != null) {
				mGestureDetector.setOnZoomInListener(zoomInListener);
				mGestureDetector.onTouchEvent(event);
			}
		}
		
		int n_points = event.getPointerCount();
		
		if(n_points == 1)
		{
			if(mPlayer != null)
			{
				try {
					mPlayer._OnGesture(1, event.getAction(), event.getX(0), event.getY(0));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public OnZoomInListener zoomInListener = new OnZoomInListener() {

		@Override
		public void onZoom(MotionEvent event) {
			// TODO Auto-generated method stub
//			if (SUPPORT_ZOOM) {
				mode = MODE.ZOOM;
				touchSuper(event);
//			}
		}

	};

	public void touchSuper(MotionEvent event) {
		super.onTouchEvent(event);
	}

	public void fullScreen() {

		fgFullScreen = 1;
		vSetWindow();
	}

	public void halfScreen() {
		fgFullScreen = 0;
		vSetWindow();
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// startVideo();
			Log.v(TAG, "surfaceChanged()");
			int sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565 by default
			switch (format) {
			case PixelFormat.A_8:
				Log.v(TAG, "pixel format A_8");
				break;
			case PixelFormat.LA_88:
				Log.v(TAG, "pixel format LA_88");
				break;
			case PixelFormat.L_8:
				Log.v(TAG, "pixel format L_8");
				break;
			case PixelFormat.RGBA_4444:
				Log.v(TAG, "pixel format RGBA_4444");
				sdlFormat = 0x85421002; // SDL_PIXELFORMAT_RGBA4444
				break;
			case PixelFormat.RGBA_5551:
				Log.v(TAG, "pixel format RGBA_5551");
				sdlFormat = 0x85441002; // SDL_PIXELFORMAT_RGBA5551
				break;
			case PixelFormat.RGBA_8888:
				Log.v(TAG, "pixel format RGBA_8888");
				sdlFormat = 0x86462004; // SDL_PIXELFORMAT_RGBA8888
				break;
			case PixelFormat.RGBX_8888:
				Log.v(TAG, "pixel format RGBX_8888");
				sdlFormat = 0x86262004; // SDL_PIXELFORMAT_RGBX8888
				break;
			case PixelFormat.RGB_332:
				Log.v(TAG, "pixel format RGB_332");
				sdlFormat = 0x84110801; // SDL_PIXELFORMAT_RGB332
				break;
			case PixelFormat.RGB_565:
				Log.v(TAG, "pixel format RGB_565");
				sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565
				break;
			case PixelFormat.RGB_888:
				Log.v(TAG, "pixel format RGB_888");
				// Not sure this is right, maybe SDL_PIXELFORMAT_RGB24 instead?
				sdlFormat = 0x86161804; // SDL_PIXELFORMAT_RGB888
				break;
			default:
				Log.v(TAG, "pixel format unknown " + format);
				break;
			}
			sdlFormat = 0x86161804;
			mPlayer.onNativeResize(w, h, sdlFormat);
			Log.e("surface", w + ":" + h);
			mWidth = w;


			mHeight = h;

			vSetWindow();
			if(layoutType==LAYOUTTYPE_SEPARATION){
				sendStartBrod();//del by dxs to fix audio BUG
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			Log.v(TAG, "surfaceCreated()");
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			holder.setKeepScreenOn(true);
//			renderThread.start();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.e("leleTest","surfaceDestroyed");
			isPanorama=false;
			isFourFace=false;
			ShapeType=SHAPE_CIRCLE;
			type=1;
			scale=1;
			//fix some new Android phone release Bug -add by dxs
			new Handler().postDelayed(new Runnable(){    
				public void run() {    
				    release();
				}    
			}, 300); 
		}
	};
	public void sendStartBrod(){
		Log.e("leleTest","isInitScreen="+isInitScreen);
		if(!isInitScreen){
			isInitScreen=true;
			MediaPlayer.getInstance().init(mWidth, mHeight, mWindowWidth);
			Intent start = new Intent();
			start.setAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
			mContext.sendBroadcast(start);
		}

	}
	public synchronized void release() {
		
		if (mPlayer != null) {
			// mPlayer.native_p2p_hungup();
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		Log.e("leleTest","release");
		isInitScreen=false;
		MediaPlayer.ReleaseOpenGL();
	}

	@Override
	protected int getCurrentWidth() {
		// TODO Auto-generated method stub
		return mWindowWidth;
	}

	public int getmHeight(){
		return mHeight;
	}

	@Override
	protected int getCurrentHeight() {
		// TODO Auto-generated method stub
		return mWindowHeight;
	}

	@Override
	protected void setVideoScale(int x, int y, float scale) {
		// TODO Auto-generated method stub

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();

		y = mHeight - (layoutParams.topMargin + y);

		Log.e("Gview","zoom" + x + ":" + y+"       "+scale);

		mPlayer.ZoomView(x, y, scale);
	}

	@Override
	protected boolean MovePicture(int left, int top) {
		// TODO Auto-generated method stub

		if (mPlayer.MoveView(left, 0 - top) == 0) {
			return false;
		}
		return true;

	}

	@Override
	protected void StopMoving() {
		// TODO Auto-generated method stub

		mPlayer.MoveView(0, 0);

	}

	@Override
	public void changeNormalSize() {
		/*
		 * LayoutParams layoutParams = (LayoutParams) getLayoutParams();
		 * layoutParams.width = mFixWidth; layoutParams.height = mFixHeight;
		 * layoutParams.leftMargin = (mWindowWidth - mFixWidth) / 2;
		 * layoutParams.topMargin = (mWindowHeight - mFixHeight) / 2;
		 * setLayoutParams(layoutParams); mPlayer.ChangeScreenSize(mFixWidth,
		 * mFixHeight, 0);
		 */
	}
	public void setHandler(Handler handler){
		myHandler=handler;
	}

	public int getmWidth() {
		return mWidth;
	}

	public void setmWidth(int mWidth) {
		this.mWidth = mWidth;
	}

	public void setmHeight(int mHeight) {
		this.mHeight = mHeight;
	}

	public boolean isPanorama() {
		return isPanorama;
	}

	public void setPanorama(boolean isPanorama) {
		this.isPanorama = isPanorama;
	}
	//全景控制添加，暂时放在这个位置
	private View[] LinkedViews;
	private boolean isShowControl;
	public final static int SHOWTYPE_MUST_NORMAL=0;
	public final static int SHOWTYPE_MUST_SHOW=1;
	public final static int SHOWTYPE_MUST_HIDEN=2;
    public View[] getLinkedView() {
		return LinkedViews;
	}

	public void setLinkedView(View[] linkedView) {
		LinkedViews = linkedView;
	}
	private void HindenControl(){
		isShowControl=false;
		if(LinkedViews==null)return;
		for(int i=0;i<LinkedViews.length;i++){
			LinkedViews[i].setVisibility(View.INVISIBLE);
		}
	}
	private void ShowControl(){
		isShowControl=true;
		if(LinkedViews==null)return;
		for(int i=0;i<LinkedViews.length;i++){
			LinkedViews[i].setVisibility(View.VISIBLE);
			LinkedViews[i].bringToFront();
		}
	}
	public void changeVisibly(){
		if(isShowControl){
			HindenControl();
		}else{
			ShowControl();
		}
	}
	public void showVisible(int ShowType){
		if(ShowType==SHOWTYPE_MUST_SHOW){
			ShowControl();
		}else if(ShowType==SHOWTYPE_MUST_HIDEN){
			HindenControl();
		}else{
			if(isShowControl){
				ShowControl();
			}else{
				HindenControl();
			}
		}
	}
	public void FilpAction(int MsgType, int MsgAction){
		
		float x = 0,y = 0;
		if(MsgAction==2){
			x=-2000;y=0f;
		}else if(MsgAction==3){
			x=3000;y=0f;
		}
		if(mPlayer != null){
			try {
				mPlayer._OnGesture(1, 0, 0, 0);
				for(int i=0;i<5;i++){
					mPlayer._OnGesture(1, 2, 0, i);
				}
				mPlayer._OnGesture(2, 1, x, y);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean getIsFourFace(){
		return isFourFace;
	}
	
	public void setIsFourFace(boolean isFor){
		this.isFourFace=isFor;
	}
	
	public void setShapeType(int shapeType){
		this.ShapeType=shapeType;
	}
	
	public int getShapeType(){
		return this.ShapeType;
	}
	
	public void ZoomINPanom(float x,float y){
		if(mPlayer != null&&isPanorama){
			try {
				mPlayer._OnGesture(3, 1, x, y);
			} catch (IOException e) {
				isFourFace=false;
				e.printStackTrace();
			}
			isFourFace=true;
		}
	}
	public void ZoomOutPanom(float x,float y){
		if(mPlayer != null&&isPanorama){
			try {
				mPlayer._OnGesture(4, 1, x, y);
			} catch (IOException e) {
				isFourFace=true;
				e.printStackTrace();
			}
			isFourFace=false;
		}
	}

	public int getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(int layoutType) {
		this.layoutType = layoutType;
	}
}
