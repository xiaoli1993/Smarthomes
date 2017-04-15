package com.nuowei.smarthome.view.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nuowei.smarthome.R;

import cn.iwgang.familiarrecyclerview.IFamiliarLoadMore;

/**
 * 用于List中加载更多显示的View
 */
public class CustomLoadMoreView extends FrameLayout implements IFamiliarLoadMore {
    private ProgressBar mPbLoad;
    private TextView mTvLoadText;

    private boolean isLoading = false; // 是否加载中


    public CustomLoadMoreView(Context context) {
        this(context, null);
    }

    public CustomLoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_load_more, this);
        mPbLoad = (ProgressBar) findViewById(R.id.pb_load);
        mTvLoadText = (TextView) findViewById(R.id.tv_loadText);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void showNormal() {
        isLoading = false;
        mPbLoad.setVisibility(GONE);
        mTvLoadText.setText(getResources().getString(R.string.frv_def_load_more_view_status_normal));
    }

    @Override
    public void showLoading() {
        isLoading = true;
        mPbLoad.setVisibility(VISIBLE);
        mTvLoadText.setText(getResources().getString(R.string.frv_def_load_more_view_status_loading));
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

}
