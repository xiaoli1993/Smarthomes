package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.common.util.ToastUtils;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.smarthomesdk.utils.XlinkUtils;
import com.nuowei.smarthome.view.button.StateButton;
import com.nuowei.smarthome.view.scan.RadarScanView;
import com.nuowei.smarthome.view.scan.RandomTextView;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;

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
        isAddSub(true);
        randomTextView.setOnRippleViewClickListener(
                new RandomTextView.OnRippleViewClickListener() {
                    @Override
                    public void onRippleViewClicked(View view) {
//                        MainActivity.this.startActivity(
//                                new Intent(MainActivity.this, RefreshProgressActivity.class));
                    }
                });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                randomTextView.addKeyWord("彭丽媛");
//                randomTextView.addKeyWord("习近平");
//                randomTextView.show();
//            }
//        }, 2 * 1000);
    }

    private void isAddSub(boolean isEnable) {
        final String addSubDevice = ZigbeeGW.SetAllowAddSubDevice(MyApplication.getMyApplication().getUserInfo().getNickname(), isEnable);
        if (MainActivity.getChoiceGwDevice().getDeviceState() == 0) {
            XlinkAgent.getInstance().connectDevice(MainActivity.getChoiceGwDevice().getxDevice(), MainActivity.getChoiceGwDevice().getAccessKey(), new ConnectDeviceListener() {
                @Override
                public void onConnectDevice(XDevice xDevice, int ret) {
                    if (ret < 0) {
                        MyApplication.getLogger().i("连接失败:" + ret);
                        XlinkUtils.shortTips(getString(R.string.deviceoffline));
                    } else {
                        MainActivity.getChoiceGwDevice().setDeviceState(1);
                        int i = XlinkAgent.getInstance().sendPipeData(xDevice, addSubDevice.getBytes(), new SendPipeListener() {
                            @Override
                            public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                            }
                        });
                        if (i > 0) {
                            finish();
                        } else {
                            ToastUtils.showShortToast(MyApplication.getMyApplication(), getString(R.string.Adderror));
                        }
                    }
                }
            });
        } else {
            int i = XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), addSubDevice.getBytes(), new SendPipeListener() {
                @Override
                public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                }
            });
            if (i > 0) {
                finish();
            } else {
                ToastUtils.showShortToast(MyApplication.getMyApplication(), getString(R.string.Adderror));
            }
        }
    }

    @OnClick({R.id.image_btn_backs, R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_btn_backs:
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                isAddSub(false);
                this.finish();
                break;
            case R.id.btn_share:
                isAddSub(false);
                break;
        }
    }
}
