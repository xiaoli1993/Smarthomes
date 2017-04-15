package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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
import com.nuowei.smarthome.view.calendars.CaledarAdapter;
import com.nuowei.smarthome.view.calendars.CalendarBean;
import com.nuowei.smarthome.view.calendars.CalendarDateView;
import com.nuowei.smarthome.view.calendars.CalendarUtil;
import com.nuowei.smarthome.view.calendars.CalendarView;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private DiaryAdapter mDiaryAdapter;
    private Diary2Adapter mDiary2Adapter;
    private String gwMac;
    private String zigbeeMac;

    //    private FamiliarRecyclerView mFamiliarRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary2);
        ButterKnife.bind(this);
        initData();
        initList();
        initEven();
    }

    List<DataDevice> dataDeviceList = null;

    private void initData() {
        int[] data = CalendarUtil.getYMD(new Date());
        tvTitle.setText(data[0] + "/" + data[1] + "/" + data[2]);
        dataDeviceList = new ArrayList<DataDevice>();
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        gwMac = bundle.getString(Constants.GATEWAY_MAC);
        zigbeeMac = bundle.getString(Constants.ZIGBEE_MAC);
        boolean isgw = bundle.getBoolean("isGw");

        if (isgw) {
            SubDevice subDevice = SubDeviceManage.getInstance().getDevice(gwMac, zigbeeMac);
            dataDeviceList = DataSupport.where("deviceMac = ? and subMac = ? and year = ? and month = ? and day = ?", gwMac, zigbeeMac, data[0] + "", new DecimalFormat("00").format(data[1]) + "", new DecimalFormat("00").format(data[2] - 1) + "").find(DataDevice.class);
            for (int i = 0; i < dataDeviceList.size(); i++) {
                MyApplication.getLogger().i("这日记内容:" + dataDeviceList.get(i).getBodyLocKey() + dataDeviceList.get(i).getActionName() + "\n" + dataDeviceList.get(i).getDate());
            }
        } else {
            XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(gwMac);
//            List<DataDevice> Xldevice = DataSupport.where("deviceMac = ? and zigbeeMac = ?", gwMac,subMac).find(DataDevice.class);
        }

    }

    private void initList() {
//        mCvRefreshListRecyclerView.setLoadMoreView(new CustomLoadMoreView(this));
//        mCvRefreshListRecyclerView.setColorSchemeColors(0xFFFF5000, Color.RED, Color.YELLOW, Color.GREEN);
//        mCvRefreshListRecyclerView.setLoadMoreEnabled(true);
//
//        mFamiliarRecyclerView = mCvRefreshListRecyclerView.getFamiliarRecyclerView();
//        // ItemAnimator
//        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        // head view
////        mFamiliarRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, true, 0xFFFF5000, "Head View 1"));
//
//
//        // Item Click and Item Long Click
//        mCvRefreshListRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
//            @Override
//            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
//                MyApplication.getLogger().i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
//            }
//        });
//        mCvRefreshListRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
//                MyApplication.getLogger().i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
//                return true;
//            }
//        });
//        mCvRefreshListRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
//            @Override
//            public void onPullRefresh() {
//                new android.os.Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        mDatas.clear();
////                        mDatas.addAll(getDatas());
////                        mDiaryAdapter.notifyDataSetChanged();
//                        mCvRefreshListRecyclerView.pullRefreshComplete();
//                        MyApplication.getLogger().i("wg", "加载完成啦...");
//                    }
//                }, 1000);
//            }
//        });
//
//        mCvRefreshListRecyclerView.setOnLoadMoreListener(new FamiliarRefreshRecyclerView.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                new android.os.Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        int startPos = dataDeviceList.size();
////                        List<String> newDatas = getDatas();
////                        mDatas.addAll(newDatas);
////                        mDiaryAdapter.notifyItemInserted(startPos);
//                        mCvRefreshListRecyclerView.loadMoreComplete();
//                    }
//                }, 1000);
//            }
//        });
        mDiaryAdapter = new DiaryAdapter(R.layout.item_list, dataDeviceList);
//        mCvRefreshListRecyclerView.setAdapter(mDiaryAdapter);
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
                tvTitle.setText(bean.year + "/" + getDisPlayNumber(bean.moth) + "/" + getDisPlayNumber(bean.day));
                MyApplication.getLogger().i("时间是：" + bean.year + "/" + getDisPlayNumber(bean.moth) + "/" + getDisPlayNumber(bean.day));
                dataDeviceList.clear();
                dataDeviceList = DataSupport.where("deviceMac = ? and subMac = ? and year = ? and month = ? and day = ?", gwMac, zigbeeMac, bean.year + "", getDisPlayNumber(bean.moth), getDisPlayNumber(bean.day)).find(DataDevice.class);
                for (int i = 0; i < dataDeviceList.size(); i++) {
                    MyApplication.getLogger().i("这日记内容:" + dataDeviceList.get(i).getBodyLocKey() + dataDeviceList.get(i).getActionName() + "\n" + dataDeviceList.get(i).getDate());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }).start();
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
}
