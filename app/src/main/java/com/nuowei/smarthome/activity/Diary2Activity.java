package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.DiaryAdapter;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.view.calendar.OnCalendarClickListener;
import com.nuowei.smarthome.view.calendar.month.MonthCalendarView;
import com.nuowei.smarthome.view.calendar.schedule.ScheduleLayout;
import com.nuowei.smarthome.view.calendar.schedule.ScheduleRecyclerView;
import com.nuowei.smarthome.view.calendar.week.WeekCalendarView;
import com.nuowei.smarthome.view.calendars.CaledarAdapter;
import com.nuowei.smarthome.view.calendars.CalendarBean;
import com.nuowei.smarthome.view.calendars.CalendarDateView;
import com.nuowei.smarthome.view.calendars.CalendarUtil;
import com.nuowei.smarthome.view.calendars.CalendarView;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import qiu.niorgai.StatusBarCompat;

import static com.nuowei.smarthome.util.MyUtil.px;

/**
 * @Author : 肖力
 * @Time :  2017/4/14 18:29
 * @Description :
 * @Modify record :
 */

public class Diary2Activity extends BaseActivity implements OnCalendarClickListener {

    @BindView(R.id.slSchedule)
    ScheduleLayout slSchedule;
    @BindView(R.id.rlMonthCalendar)
    RelativeLayout rlMonthCalendar;
    @BindView(R.id.mcvCalendar)
    MonthCalendarView mcvCalendar;
    @BindView(R.id.wcvCalendar)
    WeekCalendarView wcvCalendar;
    @BindView(R.id.rlScheduleList)
    RelativeLayout rlScheduleList;
    @BindView(R.id.rvScheduleList)
    ScheduleRecyclerView rvScheduleList;
    @BindView(R.id.rlNoTask)
    RelativeLayout rlNoTask;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;

    private DiaryAdapter mDiaryAdapter;
    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        initData();
        initEven();
    }

    List<DataDevice> dataDeviceList = null;

    private void initData() {
        dataDeviceList = new ArrayList<DataDevice>();
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        String gwMac = bundle.getString(Constants.GATEWAY_MAC);
        String zigbeeMac = bundle.getString(Constants.ZIGBEE_MAC);
        boolean isgw = bundle.getBoolean("isGw");
        if (isgw) {
            SubDevice subDevice = SubDeviceManage.getInstance().getDevice(gwMac, zigbeeMac);
            dataDeviceList = DataSupport.where("deviceMac = ? and subMac = ?", gwMac, zigbeeMac).find(DataDevice.class);
            for (int i = 0; i < dataDeviceList.size(); i++) {
                MyApplication.getLogger().i("这日记内容:" + dataDeviceList.get(i).getBodyLocKey() + dataDeviceList.get(i).getActionName());
            }
        } else {
            XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(gwMac);
//            List<DataDevice> Xldevice = DataSupport.where("deviceMac = ? and zigbeeMac = ?", gwMac,subMac).find(DataDevice.class);
        }
        initScheduleList();
    }

    private void initScheduleList() {
        rvScheduleList = slSchedule.getSchedulerRecyclerView();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvScheduleList.setLayoutManager(manager);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        rvScheduleList.setItemAnimator(itemAnimator);
        mDiaryAdapter = new DiaryAdapter(R.layout.item_list, dataDeviceList);
        rvScheduleList.setAdapter(mDiaryAdapter);
    }

    //
    private void initEven() {
        tvTitle.setText(R.string.Diary);
        tvRight.setVisibility(View.GONE);
        slSchedule.setOnCalendarClickListener(this);
        StatusBarCompat.translucentStatusBar(this, false);
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
//        setCurrentSelectDate(year, month, day);
//        resetScheduleList();
    }

    @OnClick(R.id.tv_right)
    void onRight() {

    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }
}
