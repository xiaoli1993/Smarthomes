package com.nuowei.smarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.util.CloseActivityClass;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qiu.niorgai.StatusBarCompat;

/**
 * @Author : 肖力
 * @Time :  2017/3/31 17:21
 * @Description :
 * @Modify record :
 */

public class DiaryActivity extends AppCompatActivity {}
//        implements OnCalendarClickListener {

//    @BindView(R.id.slSchedule)
//    ScheduleLayout slSchedule;
//    @BindView(R.id.rlMonthCalendar)
//    RelativeLayout rlMonthCalendar;
//    @BindView(R.id.mcvCalendar)
//    MonthCalendarView mcvCalendar;
//    @BindView(R.id.wcvCalendar)
//    WeekCalendarView wcvCalendar;
//    @BindView(R.id.rlScheduleList)
//    RelativeLayout rlScheduleList;
//    @BindView(R.id.rvScheduleList)
//    ScheduleRecyclerView rvScheduleList;
//    @BindView(R.id.rlNoTask)
//    RelativeLayout rlNoTask;
//    @BindView(R.id.tv_title)
//    AvenirTextView tvTitle;
//    @BindView(R.id.tv_right)
//    AvenirTextView tvRight;
//
//    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_diary);
//        initEven();
//    }
//
//    private void initEven() {
//        CloseActivityClass.activityList.add(this);
//        ButterKnife.bind(this);
//        tvTitle.setText("日记");
//        tvRight.setVisibility(View.GONE);
//        slSchedule.setOnCalendarClickListener(this);
//        StatusBarCompat.translucentStatusBar(this, false);
//    }
//
//    @Override
//    public void onClickDate(int year, int month, int day) {
//        mCurrentSelectYear = year;
//        mCurrentSelectMonth = month;
//        mCurrentSelectDay = day;
////        setCurrentSelectDate(year, month, day);
////        resetScheduleList();
//    }
//
//    @OnClick(R.id.tv_right)
//    void onRight() {
//    }
//
//    @OnClick(R.id.image_btn_backs)
//    void onImageBtnBacksClick() {
//        //TODO implement
//        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
//        this.finish();
//    }
//}
