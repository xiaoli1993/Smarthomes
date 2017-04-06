package com.nuowei.smarthome.activity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.util.CloseActivityClass;
import com.nuowei.smarthome.util.SharePreferenceUtil;
import com.nuowei.smarthome.view.swipeback.SwipeBackLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/17 11:37
 * @Description :
 */
public class SwipeBackActivity extends FragmentActivity {
    @BindView(R.id.image_btn_back)
    ImageButton imageBtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private SwipeBackLayout mSwipeBackLayout;

    private boolean mOverrideExitAniamtion = true;

    private boolean mIsFinishing;
    SharePreferenceUtil mSharedUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloseActivityClass.activityList.add(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = new SwipeBackLayout(this);
        mSharedUtil = MyApplication.getMyApplication().getSpUtil();
        if (mSharedUtil.isSHARED_KEY_CROSS()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// landscape是横向
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// portrait是纵向
        }
        setupViews();   //加载 activity_title 布局 ，并获取标题及两侧按钮
    }

    private void setupViews() {
        super.setContentView(R.layout.activity_title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackLayout.attachToActivity(this);
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null)
            return v;
        return mSwipeBackLayout.findViewById(id);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setEnableGesture(enable);
    }

    /**
     * slide from left
     */
    public void setEdgeFromLeft() {
        final int edgeFlag = SwipeBackLayout.EDGE_LEFT;
        mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
    }

    /**
     * Override Exit Animation
     *
     * @param override
     */
    public void setOverrideExitAniamtion(boolean override) {
        mOverrideExitAniamtion = override;
    }

    /**
     * Scroll out contentView and finish the activity
     */
    public void scrollToFinishActivity() {
        mSwipeBackLayout.scrollToFinishActivity();
    }

    @Override
    public void finish() {
        if (mOverrideExitAniamtion && !mIsFinishing) {
            scrollToFinishActivity();
            mIsFinishing = true;
            return;
        }
        mIsFinishing = false;
        super.finish();
    }

    protected String[] mMonths = new String[]{"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"};

    protected static String[] mParties = new String[]{"Party A", "Party B",
            "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N",
            "Party O", "Party P", "Party Q", "Party R", "Party S", "Party T",
            "Party U", "Party V", "Party W", "Party X", "Party Y", "Party Z"};

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    /**
     * 提供是否显示提交按钮
     *
     * @param forwardResId 文字
     * @param show         true则显示
     */
    protected void showForwardView(int forwardResId, boolean show) {
        if (tvRight != null) {
            if (show) {
                tvRight.setVisibility(View.VISIBLE);
                tvRight.setText(forwardResId);
            } else {
                tvRight.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    /**
     * 是否显示返回按钮
     *
     * @param show true则显示
     */
    protected void showBackwardView(boolean show) {
        if (imageBtnBack != null) {
            if (show) {
                imageBtnBack.setVisibility(View.VISIBLE);
            } else {
                imageBtnBack.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    /**
     * 返回按钮点击后触发
     *
     * @param backwardView
     */
    protected void onBackward(View backwardView) {
        finish();
    }

    /**
     * 提交按钮点击后触发
     *
     * @param forwardView
     */
    protected void onForward(View forwardView) {
//        Toast.makeText(this, 点击提交, Toast.LENGTH_LONG).show();
    }

    //设置标题内容
    @Override
    public void setTitle(int titleId) {
        tvTitle.setText(titleId);
    }

    //设置标题内容
    @Override
    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    //设置标题文字颜色
    @Override
    public void setTitleColor(int textColor) {
        tvTitle.setTextColor(textColor);
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     * 按钮点击调用的方法
     */
    @OnClick(R.id.tv_right)
    public void onRight(View v) {
        onForward(v);
    }

    /* (non-Javadoc)
      * @see android.view.View.OnClickListener#onClick(android.view.View)
      * 按钮点击调用的方法
     */
    @OnClick(R.id.image_btn_back)
    public void onBack(View v) {
        onBackward(v);
    }
}
