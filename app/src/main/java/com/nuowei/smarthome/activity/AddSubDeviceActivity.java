package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.button.StateButton;
import com.nuowei.smarthome.view.scan.RadarScanView;
import com.nuowei.smarthome.view.scan.RandomTextView;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/4/27 17:25
 * @Description :
 * @Modify record :
 */

public class AddSubDeviceActivity extends BaseActivity {
    @BindView(R.id.image_btn_backs)
    ImageButton imageBtnBacks;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.btn_share)
    StateButton btnShare;
    @BindView(R.id.radarScanView)
    RadarScanView radarScanView;
    @BindView(R.id.randomTextView)
    RandomTextView randomTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_device);
        initEven();

    }

    private void initEven() {
        randomTextView.setOnRippleViewClickListener(
                new RandomTextView.OnRippleViewClickListener() {
                    @Override
                    public void onRippleViewClicked(View view) {
//                        MainActivity.this.startActivity(
//                                new Intent(MainActivity.this, RefreshProgressActivity.class));
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                randomTextView.addKeyWord("彭丽媛");
                randomTextView.addKeyWord("习近平");
                randomTextView.show();
            }
        }, 2 * 1000);
    }

    @OnClick({R.id.image_btn_backs, R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_btn_backs:
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                this.finish();
                break;
            case R.id.btn_share:
                break;
        }
    }
}
