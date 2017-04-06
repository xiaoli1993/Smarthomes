package com.nuowei.smarthome.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.kyleduo.switchbutton.SwitchButton;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.util.Time;
import com.nuowei.smarthome.view.pickerview.TimePickerView;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/3/31 11:37
 * @Description :
 */
public class SceneAddActivity extends BaseActivity {
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.rl_add_execution)
    RelativeLayout rlAddExecution;
    @BindView(R.id.image_Add)
    ImageView imageAdd;
    @BindView(R.id.list_execution)
    ListView listExecution;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.sw_timer)
    SwitchButton swTimer;
    @BindView(R.id.rl_repeat)
    RelativeLayout rlRepeat;
    @BindView(R.id.image_more)
    ImageView imageMore;
    @BindView(R.id.rl_coutdown)
    RelativeLayout rlCoutdown;
    @BindView(R.id.image_more1)
    ImageView imageMore1;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;

    private TimePickerView pvTime;
    private int coutdown = 0, hours = 0, mins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_add);
        initEven();
    }

    private void initEven() {
        tvTitle.setText(getResources().getString(R.string.add_scene));
        swTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rlRepeat.setVisibility(View.VISIBLE);
                } else {
                    rlRepeat.setVisibility(View.GONE);
                }
            }
        });
        Calendar selectedDate = Calendar.getInstance();
//        selectedDate.setTime();
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                hours = Time.getHour(date);
                mins = Time.getMinute(date);
            }
        }).setType(TimePickerView.Type.HOURS_MINS)
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(getResources().getColor(R.color.divider))
                .setContentSize(20)
                .setDate(selectedDate)
                .build();
    }

    @OnClick(R.id.rl_add_execution)
    public void onAddExecution(View v) {

    }

    @OnClick(R.id.rl_repeat)
    public void onRepeat(View v) {
        startActivity(new Intent(SceneAddActivity.this, RepeatActivity.class));
    }


    @OnClick(R.id.rl_coutdown)
    public void onCoutdown(View v) {
        pvTime.show();
    }

    @OnClick(R.id.tv_right)
    void onRight() {
//        startActivity(new Intent(SceneAddActivity.this, DiaryActivity.class));
    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }

}
