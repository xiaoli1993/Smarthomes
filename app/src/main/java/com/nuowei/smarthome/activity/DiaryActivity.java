package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.Diary2Adapter;
import com.nuowei.smarthome.adapter.DiaryAdapter;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.util.CloseActivityClass;
import com.nuowei.smarthome.view.calendars.CaledarAdapter;
import com.nuowei.smarthome.view.calendars.CalendarBean;
import com.nuowei.smarthome.view.calendars.CalendarDateView;
import com.nuowei.smarthome.view.calendars.CalendarUtil;
import com.nuowei.smarthome.view.calendars.CalendarView;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qiu.niorgai.StatusBarCompat;

import static com.nuowei.smarthome.util.MyUtil.px;

/**
 * @Author : 肖力
 * @Time :  2017/3/31 17:21
 * @Description :
 * @Modify record :
 */

public class DiaryActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.calendarDateView)
    CalendarDateView calendarDateView;
    //    @BindView(R.id.list)
//    FamiliarRefreshRecyclerView mCvRefreshListRecyclerView;
    @BindView(R.id.list)
    ListView mlist;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    @BindView(R.id.tv_date)
    DinProTextView tvDate;


    private DiaryAdapter mDiaryAdapter;
    private Diary2Adapter mDiary2Adapter;
    private String gwMac;
    private String zigbeeMac;
    public int isgw;
    public static DiaryActivity diaryActivity;

    //    private FamiliarRecyclerView mFamiliarRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary2);
        CloseActivityClass.activityList.add(this);
        StatusBarCompat.translucentStatusBar(this, false);
        ButterKnife.bind(this);
        diaryActivity = this;
        initData();
        initList();
        initEven();
    }

    List<DataDevice> dataDeviceList = null;

    private void initData() {
        int[] data = CalendarUtil.getYMD(new Date());
        tvDate.setText(data[0] + "/" + data[1] + "/" + data[2]);
        dataDeviceList = new ArrayList<DataDevice>();
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        gwMac = bundle.getString(Constants.GATEWAY_MAC);
        zigbeeMac = bundle.getString(Constants.ZIGBEE_MAC);
        isgw = bundle.getInt("isGw");

        if (isgw == 1) {
            SubDevice subDevice = SubDeviceManage.getInstance().getDevice(gwMac, zigbeeMac);
            dataDeviceList = DataSupport.where("deviceMac = ? and subMac = ? and year = ? and month = ? and day = ?", gwMac, zigbeeMac, data[0] + "", new DecimalFormat("00").format(data[1]) + "", new DecimalFormat("00").format(data[2]) + "").find(DataDevice.class);
            tvTitle.setText(subDevice.getDeviceName());
        } else if (isgw == 2) {
            dataDeviceList = DataSupport.where("alertName = ? and year = ? and month = ? and day = ?", "gcm notification", data[0] + "", new DecimalFormat("00").format(data[1]) + "", new DecimalFormat("00").format(data[2]) + "").find(DataDevice.class);
            tvTitle.setText(getResources().getString(R.string.message));
        } else {
            XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(gwMac);
            tvTitle.setText(xlinkDevice.getDeviceName());
        }

    }

    private void initList() {
        mDiary2Adapter = new Diary2Adapter(DiaryActivity.this, dataDeviceList);
        mlist.setAdapter(mDiary2Adapter);

    }

    private void initEven() {
        tvRight.setVisibility(View.GONE);
        calendarDateView.setAdapter(new CaledarAdapter() {
            @Override
            public View getView(View convertView, ViewGroup parentView, CalendarBean bean) {
                TextView view;
                if (convertView == null) {
                    convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_calendar, null);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(px(48), px(48));
                    convertView.setLayoutParams(params);
                }
                view = (TextView) convertView.findViewById(R.id.text);
                view.setText("" + bean.day);
                if (bean.mothFlag != 0) {
                    view.setTextColor(getResources().getColor(R.color.calendar_tv_coclor_f));
                }
                return convertView;
            }
        });

        calendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, CalendarBean bean) {
                MyApplication.getLogger().i("时间是：" + bean.year + "/" + getDisPlayNumber(bean.moth) + "/" + getDisPlayNumber(bean.day));
                tvDate.setText(bean.year + "/" + getDisPlayNumber(bean.moth) + "/" + getDisPlayNumber(bean.day));
                dataDeviceList.clear();
                if (isgw == 1) {
                    dataDeviceList = DataSupport.where("deviceMac = ? and subMac = ? and year = ? and month = ? and day = ?", gwMac, zigbeeMac, bean.year + "", getDisPlayNumber(bean.moth), getDisPlayNumber(bean.day)).find(DataDevice.class);
                } else if (isgw == 2) {
                    dataDeviceList = DataSupport.where("alertName = ? and year = ? and month = ? and day = ?", "gcm notification", bean.year + "", getDisPlayNumber(bean.moth), getDisPlayNumber(bean.day)).find(DataDevice.class);
                }
                mDiary2Adapter = new Diary2Adapter(DiaryActivity.this, dataDeviceList);
                mlist.setAdapter(mDiary2Adapter);
            }
        });
    }

    private String getDisPlayNumber(int num) {
        return num < 10 ? "0" + num : "" + num;
    }


    @OnClick(R.id.image_btn_backs)
    public void onViewClicked() {
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mDiary2Adapter.notifyDataSetChanged();
                    mDiaryAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    /**
     * 处理后退键的情况
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish(); // finish当前activity
            overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
            return true;
        }
        return true;
    }
}
