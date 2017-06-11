package com.jwkj.activity;



import android.os.Bundle;
import android.view.KeyEvent;

import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.util.CloseActivityClass;
import com.nuowei.smarthome.view.swipbackhelper.SwipeBackHelper;
import com.p2p.core.BaseCoreActivity;

import qiu.niorgai.StatusBarCompat;

public abstract class BaseActivity extends BaseCoreActivity {
	public NormalDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CloseActivityClass.activityList.add(this);
//		SwipeBackHelper.onCreate(this);
//		SwipeBackHelper.getCurrentPage(this)
//				.setSwipeBackEnable(true)
//				.setSwipeSensitivity(0.5f)
//				.setSwipeRelateEnable(true)
//				.setSwipeRelateOffset(300);
//		//ViewServer.get(this).addWindow(this);
//		StatusBarCompat.translucentStatusBar(this, false);
//        ButterKnife.bind(this);
	}
//	 /**
//     * 处理后退键的情况
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            this.finish(); // finish当前activity
//            overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
//            return true;
//        }
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
//    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onGoBack() {
		// TODO Auto-generated method stub
//		MyApplication.app.showNotification();
	}

	@Override
	protected void onGoFront() {
		// TODO Auto-generated method stub
//		MyApplication.app.hideNotification();
	}

	@Override
	protected void onExit() {
		// TODO Auto-generated method stub
//		MyApplication.app.hideNotification();
	}

	public abstract int getActivityInfo();
	/**
	 * 弹出等待框
	 * @param listener 消失监听
	 * @param mark 等待框标记
	 */
	public void showLoadingDialog(NormalDialog.OnCustomCancelListner listener,int mark){
		dialog=new NormalDialog(this);
		dialog.setOnCustomCancelListner(listener);
		dialog.setLoadingMark(mark);
		dialog.showLoadingDialog();
	}

}
