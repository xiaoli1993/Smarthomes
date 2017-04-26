package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.badoualy.stepperindicator.StepperIndicator;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/4/26 15:34
 * @Description :
 * @Modify record :
 */

public class GuideWifiDeviceActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.stepper_indicator)
    StepperIndicator stepperIndicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_device);
    }

    @OnClick(R.id.image_btn_backs)
    public void onViewClicked() {
    }
}
