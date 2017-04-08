package com.nuowei.smarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.loopview.LoopView;

import android.view.View;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.view.loopview.OnItemSelectedListener;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import android.widget.ImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/3/31 15:50
 * @Description :
 * @Modify record :
 */


public class RepeatActivity extends BaseActivity {
    @BindView(R.id.loop_hour)
    LoopView loopHour;
    @BindView(R.id.loop_min)
    LoopView loopMin;
    @BindView(R.id.rl_Sunday)
    RelativeLayout rlSunday;
    @BindView(R.id.tv_Sunday)
    AvenirTextView tvSunday;
    @BindView(R.id.image_Sunday)
    ImageView imageSunday;
    @BindView(R.id.rl_Monday)
    RelativeLayout rlMonday;
    @BindView(R.id.tv_Monday)
    AvenirTextView tvMonday;
    @BindView(R.id.image_Monday)
    ImageView imageMonday;
    @BindView(R.id.rl_Tuesday)
    RelativeLayout rlTuesday;
    @BindView(R.id.tv_Tuesday)
    AvenirTextView tvTuesday;
    @BindView(R.id.image_Tuesday)
    ImageView imageTuesday;
    @BindView(R.id.rl_Wednesday)
    RelativeLayout rlWednesday;
    @BindView(R.id.tv_Wednesday)
    AvenirTextView tvWednesday;
    @BindView(R.id.image_Wednesday)
    ImageView imageWednesday;
    @BindView(R.id.rl_Thursday)
    RelativeLayout rlThursday;
    @BindView(R.id.tv_Thursday)
    AvenirTextView tvThursday;
    @BindView(R.id.image_Thursday)
    ImageView imageThursday;
    @BindView(R.id.rl_Friday)
    RelativeLayout rlFriday;
    @BindView(R.id.tv_Friday)
    AvenirTextView tvFriday;
    @BindView(R.id.image_Friday)
    ImageView imageFriday;
    @BindView(R.id.rl_Saturday)
    RelativeLayout rlSaturday;
    @BindView(R.id.tv_Saturday)
    AvenirTextView tvSaturday;
    @BindView(R.id.image_Saturday)
    ImageView imageSaturday;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;

    private int wkFlag = 0x80;
    private int intMonday = 0;
    private int intTuesday = 0;
    private int intWednesday = 0;
    private int intThursday = 0;
    private int intFriday = 0;

    private int intSaturday = 0;
    private int intSunday = 0;
    private int intEveryday = 0;

    private boolean isSunday, isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday;
    private int hour = 0, min = 0;
    public final static int RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);
        initEven();
        initData();
    }

    private void initData() {
        Calendar c = Calendar.getInstance();
        ArrayList<String> hourlist = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            DecimalFormat df = new DecimalFormat("#00");
            hourlist.add(df.format(i));
        }
        ArrayList<String> minlist = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            DecimalFormat df = new DecimalFormat("#00");
            minlist.add(df.format(i));
        }
        //设置原始数据
        loopHour.setItems(hourlist);
        loopMin.setItems(minlist);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        loopHour.setInitPosition(hour);
        loopMin.setInitPosition(min);
    }

    private void initEven() {
        tvTitle.setText(getResources().getString(R.string.repeat));
        loopHour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                hour = index;
            }
        });
        loopMin.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                min = index;
            }
        });
    }

    @OnClick(R.id.tv_right)
    void onRight() {
        int wk = wkFlag + intMonday + intTuesday + intWednesday + intThursday + intFriday + intSaturday + intSunday + intEveryday;
//        MyApplication.getLogger().i("周期WK：" + wk + "时间hour:" + hour + "min:" + min);
        Intent intent = new Intent();
        intent.putExtra("wk", wk);
        intent.putExtra("hour", hour);
        intent.putExtra("min", min);
        setResult(RESULT_CODE, intent);
        finish();

    }


    @OnClick({R.id.rl_Sunday, R.id.rl_Monday, R.id.rl_Tuesday
            , R.id.rl_Wednesday, R.id.rl_Thursday, R.id.rl_Friday, R.id.rl_Saturday})
    void onWeek(View v) {
        switch (v.getId()) {
            case R.id.rl_Sunday:
                if (!isSunday) {
                    intSunday = 0x40;
                    imageSunday.setVisibility(View.VISIBLE);
                } else {
                    intSunday = 0;
                    imageSunday.setVisibility(View.GONE);
                }
                isSunday = !isSunday;
                break;
            case R.id.rl_Monday:
                if (!isMonday) {
                    intMonday = 0x01;
                    imageMonday.setVisibility(View.VISIBLE);
                } else {
                    intMonday = 0;
                    imageMonday.setVisibility(View.GONE);
                }
                isMonday = !isMonday;
                break;
            case R.id.rl_Tuesday:
                if (!isTuesday) {
                    intTuesday = 0x02;
                    imageTuesday.setVisibility(View.VISIBLE);
                } else {
                    intTuesday = 0;
                    imageTuesday.setVisibility(View.GONE);
                }
                isTuesday = !isTuesday;
                break;
            case R.id.rl_Wednesday:
                if (!isWednesday) {
                    intWednesday = 0x04;
                    imageWednesday.setVisibility(View.VISIBLE);
                } else {
                    intWednesday = 0;
                    imageWednesday.setVisibility(View.GONE);
                }
                isWednesday = !isWednesday;
                break;
            case R.id.rl_Thursday:
                if (!isThursday) {
                    intThursday = 0x08;
                    imageThursday.setVisibility(View.VISIBLE);
                } else {
                    intThursday = 0;
                    imageThursday.setVisibility(View.GONE);
                }
                isThursday = !isThursday;
                break;
            case R.id.rl_Friday:
                if (!isFriday) {
                    intFriday = 0x10;
                    imageFriday.setVisibility(View.VISIBLE);
                } else {
                    intFriday = 0;
                    imageFriday.setVisibility(View.GONE);
                }
                isFriday = !isFriday;
                break;
            case R.id.rl_Saturday:
                if (!isSaturday) {
                    intSaturday = 0x20;
                    imageSaturday.setVisibility(View.VISIBLE);
                } else {
                    intSaturday = 0;
                    imageSaturday.setVisibility(View.GONE);
                }
                isSaturday = !isSaturday;
                break;

        }
    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }
}

