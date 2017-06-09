package com.jwkj.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.ImputDialog;
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;

public class BaseFragment extends Fragment {
	private boolean isRun = false;
	public NormalDialog dialog;
	public ImputDialog inputDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    }
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isRun = false;
		hindLoadingDialog();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isRun = true;
	}

	public boolean getIsRun() {
		return isRun;
	}
	/**
	 * 弹出等待框
	 * @param listener 消失监听
	 * @param mark 等待框标记
	 */
	public void showLoadingDialog(NormalDialog.OnCustomCancelListner listener,int mark){
		dialog=new NormalDialog(getActivity());
		dialog.setOnCustomCancelListner(listener);
		dialog.setLoadingMark(mark);
		dialog.showLoadingDialog();
	}

	public void showLoadingDialog(int mark, NormalDialog.OnNormalDialogTimeOutListner timeOutListner,NormalDialog.OnCustomCancelListner listener,long time){
		dialog=new NormalDialog(getActivity());
		dialog.setOnNormalDialogTimeOutListner(timeOutListner);
		dialog.setTimeOut(time);
		dialog.setOnCustomCancelListner(listener);
		dialog.setLoadingMark(mark);
		dialog.showLoadingDialog();
	}

	/**
	 * 展示输入框
	 * @param inputClickListner
	 * @param title
	 * @param hintstring
	 * @param etx
	 * @param Yes
	 * @param No
	 */
	public void showInputDialog(ImputDialog.MyInputClickListner inputClickListner,int title,int hintstring,String etx,int Yes,int No){
		inputDialog=new ImputDialog(getActivity());
		inputDialog.setEditextHint(Utils.getStringForId(hintstring));
		inputDialog.setInputTitle(Utils.getStringForId(title));
		inputDialog.setEdtextText(etx);
		inputDialog.setYes(Utils.getStringForId(Yes));
		inputDialog.setNo(Utils.getStringForId(No));
		inputDialog.setOnMyinputClickListner(inputClickListner);
		inputDialog.show();
	}
	
	public void hindLoadingDialog(){
		if(dialog!=null&&dialog.isShowing()){
        	dialog.dismiss();
        }
	}

	/**
	 * 设置默认加载框超时时间
	 * @param time
     */
	public void setLoadingTimeOut(long time){
		if(dialog!=null){
			dialog.setOnNormalDialogTimeOutListner(new NormalDialog.OnNormalDialogTimeOutListner() {
				@Override
				public void onTimeOut() {
					T.showShort(MyApplication.app, R.string.time_out);
				}
			});
			dialog.setTimeOut(time);
		}
	}

}
