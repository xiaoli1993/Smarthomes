package com.nuowei.smarthome.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.button.LoadingsButton;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/4/26 16:04
 * @Description :
 * @Modify record :
 */

public class SmartLinkActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.tv_wifiSSID)
    DinProTextView tvWifiSSID;
    @BindView(R.id.first)
    LoadingsButton first;
    @BindView(R.id.image_btn_backs)
    ImageButton imageBtnBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlink);
        ButterKnife.bind(this);
        LoadingsButton.ViewSwitcherFactory factory = new LoadingsButton.ViewSwitcherFactory(this,
                getResources().getColor(android.R.color.white),
                44F,
                Typeface.DEFAULT);
        first.setTextFactory(factory);

        first.setText("Press");
        first.setLoadingText("wait...");
        first.setBackgroundColor(Color.RED);
        first.setAnimationInDirection(LoadingsButton.IN_FROM_LEFT);
    }


    @OnClick({R.id.image_btn_backs, R.id.first})
    public void onViewClicked(final View view) {
        switch (view.getId()) {
            case R.id.image_btn_backs:
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((LoadingsButton) view).showButtonText();
                    }
                }, 2000);
                break;
            case R.id.first:
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((LoadingsButton) view).showButtonText();
                    }
                }, 2000);
                break;
        }
    }
}
