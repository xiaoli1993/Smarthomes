package com.jwkj.activity;


import com.jwkj.global.MyApp;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.BaseCoreActivity;

public abstract class BaseActivity extends BaseCoreActivity {
	public NormalDialog dialog;
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
		MyApp.app.showNotification();
	}

	@Override
	protected void onGoFront() {
		// TODO Auto-generated method stub
		MyApp.app.hideNotification();
	}

	@Override
	protected void onExit() {
		// TODO Auto-generated method stub
		MyApp.app.hideNotification();
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
