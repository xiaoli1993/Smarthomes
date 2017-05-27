package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.repo.BubbleSeekBar;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/5/26 13:58
 * @Description :
 * @Modify record :
 */

public class GwSetingsActivity extends BaseActivity {

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
    @BindView(R.id.prss_voice)
    BubbleSeekBar prssVoice;
    @BindView(R.id.prss_zigbeegw)
    BubbleSeekBar prssZigbeegw;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.Volume_setting)
    RelativeLayout VolumeSetting;
    @BindView(R.id.Alert_setting)
    RelativeLayout AlertSetting;
    @BindView(R.id.Induction_setting)
    RelativeLayout InductionSetting;
    @BindView(R.id.Diary_setting)
    RelativeLayout DiarySetting;
    @BindView(R.id.img_new)
    ImageView imgNew;
    @BindView(R.id.Checkupdate)
    RelativeLayout Checkupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gw_setings);
//        initEven();
    }

    @OnClick({R.id.image_btn_backs, R.id.btn_right, R.id.tv_right, R.id.Volume_setting, R.id.Alert_setting, R.id.Induction_setting, R.id.Diary_setting, R.id.Checkupdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_btn_backs:
                break;
            case R.id.btn_right:
                break;
            case R.id.tv_right:
                break;
            case R.id.Volume_setting:
                break;
            case R.id.Alert_setting:
                break;
            case R.id.Induction_setting:
                break;
            case R.id.Diary_setting:
                break;
            case R.id.Checkupdate:
                break;
        }
    }
}
