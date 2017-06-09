package com.nuowei.smarthome.activity;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.baidu.mobstat.StatService;
import com.nuowei.smarthome.R;
import com.p2p.core.global.Constants;
import com.p2p.core.utils.HomeWatcher;
import com.p2p.core.utils.OnHomePressedListener;

import java.util.HashMap;
import java.util.Set;

/**
 * @Author : 肖力
 * @Time :  2017/6/9 11:06
 * @Description :
 * @Modify record :
 */

public abstract class BaseCoreActivity extends AppCompatActivity implements
        OnHomePressedListener {
    private boolean isGoExit = false;
    protected static HashMap<Integer, Integer> activity_stack = new HashMap<Integer, Integer>();
    public HomeWatcher mHomeWatcher;

    private void onStackChange() {
        Set<Integer> keys = activity_stack.keySet();
        int start = 0;
        int stop = 0;
        for (Integer key : keys) {
            int status = activity_stack.get(key);
            if (status == Constants.ActivityStatus.STATUS_START) {
                start++;
            } else if (status == Constants.ActivityStatus.STATUS_STOP) {
                stop++;
            }
        }

        if (activity_stack.size() > 0 && start == 0) {
            if (!isGoExit) {
                onGoBack();
            }
        } else if (activity_stack.size() > 0 && start > 0) {
            onGoFront();
        }
        Log.e("my", "stack size:" + activity_stack.size() + "    start:"
                + start + "  stop:" + stop);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mHomeWatcher != null) {
            mHomeWatcher.stopWatch();
            mHomeWatcher = null;
        }
        activity_stack.put(getActivityInfo(),
                Constants.ActivityStatus.STATUS_STOP);
        onStackChange();
    }

    // 应用程序是否在前台运行
    protected boolean isOnFront() {
        Set<Integer> keys = activity_stack.keySet();
        int start = 0;
        int stop = 0;
        for (Integer key : keys) {
            int status = activity_stack.get(key);
            if (status == Constants.ActivityStatus.STATUS_START) {
                start++;
            } else if (status == Constants.ActivityStatus.STATUS_STOP) {
                stop++;
            }
        }

        if (activity_stack.size() > 0 && start == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
        activity_stack.put(getActivityInfo(),
                Constants.ActivityStatus.STATUS_START);
        onStackChange();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        activity_stack.remove(getActivityInfo());
        onStackChange();
    }

    // 每个继承的activity必须返回不同的值
    public abstract int getActivityInfo();

    protected void isGoExit(boolean isGoExit) {
        this.isGoExit = isGoExit;
    }

    // Home键按下时回调
    @Override
    public void onHomePressed() {
        // TODO Auto-generated method stub

    }

    // Home键长按下时回调
    @Override
    public void onHomeLongPressed() {
        // TODO Auto-generated method stub

    }

    // 进入后台
    protected abstract void onGoBack();

    // 进入前台
    protected abstract void onGoFront();

    // 退出应用
    protected abstract void onExit();


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
}
