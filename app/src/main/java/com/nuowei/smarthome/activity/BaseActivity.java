package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jwkj.global.Constants;
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.util.CloseActivityClass;
import com.nuowei.smarthome.view.swipbackhelper.SwipeBackHelper;

import butterknife.ButterKnife;
import qiu.niorgai.StatusBarCompat;

/**
 * @Author : 肖力
 * @Time :  2017/3/31 13:40
 * @Description :
 * @Modify record :
 */

public class BaseActivity extends BaseCoreActivity {
    public NormalDialog dialog;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloseActivityClass.activityList.add(this);
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(0.5f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(300);
        //ViewServer.get(this).addWindow(this);
        StatusBarCompat.translucentStatusBar(this, false);
//        ButterKnife.bind(this);
    }

    @Override
    public int getActivityInfo() {
        // TODO Auto-generated method stub
        return Constants.ActivityInfo.ACTIVITY_MAINACTIVITY;
    }

    @Override
    protected void onGoBack() {

    }

    @Override
    protected void onGoFront() {

    }

    @Override
    protected void onExit() {

    }

    /**
     * 处理后退键的情况
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish(); // finish当前activity
            overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }


    /**
     * 弹出等待框
     *
     * @param listener 消失监听
     * @param mark     等待框标记
     */
    public void showLoadingDialog(NormalDialog.OnCustomCancelListner listener, int mark) {
        dialog = new NormalDialog(this);
        dialog.setOnCustomCancelListner(listener);
        dialog.setLoadingMark(mark);
        dialog.showLoadingDialog();
    }

}

