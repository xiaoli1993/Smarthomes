package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.THPAdapter;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.CollapsingToolbarLayoutState;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.view.calendars.CalendarUtil;
import com.nuowei.smarthome.view.recyclerview.IRecyclerView;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class THPActivity extends BaseActivity {
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.image_btn_backs)
    ImageButton imageBtnBacks;

    @BindView(R.id.image_icon)
    ImageView imageIcon;
    @BindView(R.id.tv_temp)
    DinProTextView tvTemp;
    @BindView(R.id.tv_hum)
    DinProTextView tvHum;
    //    @BindView(R.id.refreshListRecyclerView)
//    FamiliarRefreshRecyclerView refreshListRecyclerView;
    @BindView(R.id.cardView)
    CardView cardView;

    @BindView(R.id.recyclerview)
    IRecyclerView iRecyclerView;

    private String gwMac;
    private String zigbeeMac;
    private int limit = 10;
    private int offset = 0;
    //    private FamiliarRecyclerView mFamiliarRecyclerView;
    private THPAdapter mAdapter;
    private List<DataDevice> dataDeviceList;
    private String nowTemp;
    private String nowHum;
    private CollapsingToolbarLayoutState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thp);
        initData();
        initEven();
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initEven() {
        tvTemp.setText(getString(R.string.now_temp) + nowTemp + getString(R.string.tempp));//℃/℉
        tvHum.setText(getString(R.string.new_hum) + nowHum + getString(R.string.hump));
        tvRight.setVisibility(View.GONE);
        mAdapter = new THPAdapter(R.layout.item_lists, dataDeviceList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

//        iRecyclerView = (IRecyclerView) this.findViewById(R.id.recyclerview);
        iRecyclerView.setLayoutManager(layoutManager);
        iRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        iRecyclerView.setLoadingListener(new IRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter.notifyDataSetChanged();
                        iRecyclerView.refreshComplete();
                    }
                }, 1000);
            }


            @Override
            public void onLoadMore() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        offset += 10;
                        DataSupport.where("deviceMac = ? and subMac = ? ", gwMac, zigbeeMac)
                                .order("date desc")
                                .limit(limit).offset(offset)
                                .findAsync(DataDevice.class).listen(new FindMultiCallback() {
                            @Override
                            public <T> void onFinish(List<T> t) {
                                List<DataDevice> listDev = (List<DataDevice>) t;
                                for (int i = 0; i < listDev.size(); i++) {
                                    dataDeviceList.add(listDev.get(i));
                                }
                                mAdapter.notifyDataSetChanged();
                                iRecyclerView.loadMoreComplete();
                            }
                        });
                    }
                }, 1000);

            }
        });
        iRecyclerView.setAdapter(mAdapter);

//        refreshListRecyclerView.setLoadMoreEnabled(true);
//
//
//        mFamiliarRecyclerView = refreshListRecyclerView.getFamiliarRecyclerView();
//        // ItemAnimator
//        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mFamiliarRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, true, 0xFFFF5000, "Head View 1"));
//
////        refreshListRecyclerView.setLoadMoreView(new CustomLoadMoreView(this));
//
//        refreshListRecyclerView.setOnLoadMoreListener(new FamiliarRefreshRecyclerView.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
////                new android.os.Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////
////                        mAdapter.notifyDataSetChanged();
////                        refreshListRecyclerView.pullRefreshComplete();
////                    }
////                }, 1000);
//                MyApplication.getLogger().i("上拉刷新！！！！");
//                offset += 10;
//                DataSupport.where("deviceMac = ? and subMac = ? ", gwMac, zigbeeMac)
//                        .order("date desc")
//                        .limit(limit).offset(offset)
//                        .findAsync(DataDevice.class).listen(new FindMultiCallback() {
//                    @Override
//                    public <T> void onFinish(List<T> t) {
//                        List<DataDevice> listDev = (List<DataDevice>) t;
//                        for (int i = 0; i < listDev.size(); i++) {
//                            dataDeviceList.add(listDev.get(i));
//                        }
//                        mAdapter.notifyDataSetChanged();
//                        refreshListRecyclerView.loadMoreComplete();
//                    }
//                });
//            }
//        });
//        refreshListRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
//            @Override
//            public void onPullRefresh() {
//
//                new android.os.Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        mAdapter.notifyDataSetChanged();
//                        refreshListRecyclerView.pullRefreshComplete();
//                    }
//                }, 1000);
//            }
//        });
//        refreshListRecyclerView.setAdapter(mAdapter);

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                        imageBtnBacks.setBackgroundResource(R.drawable.avoscloud_feedback_thread_actionbar_back);
                        tvTitle.setTextColor(getResources().getColor(R.color.white));
                        cardView.setVisibility(View.VISIBLE);
                    }

                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                        imageBtnBacks.setBackgroundResource(R.drawable.back);
                        tvTitle.setTextColor(getResources().getColor(R.color.text_title));
                        cardView.setVisibility(View.GONE);
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            imageBtnBacks.setBackgroundResource(R.drawable.back);
                            tvTitle.setTextColor(getResources().getColor(R.color.text_title));
                            cardView.setVisibility(View.GONE);
                        }
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });
    }

    private void initData() {
        int[] data = CalendarUtil.getYMD(new Date());
//        tvTitle.setText(data[0] + "/" + data[1] + "/" + data[2]);
        dataDeviceList = new ArrayList<DataDevice>();
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        gwMac = bundle.getString(Constants.GATEWAY_MAC);
        zigbeeMac = bundle.getString(Constants.ZIGBEE_MAC);
        nowTemp = bundle.getString(Constants.DEVICE_TEMP);
        nowHum = bundle.getString(Constants.DEVICE_HUM);

        SubDevice subDevice = SubDeviceManage.getInstance().getDevice(gwMac, zigbeeMac);

        dataDeviceList = DataSupport.where("deviceMac = ? and subMac = ? ", gwMac, zigbeeMac).order("date desc").limit(limit).offset(offset).find(DataDevice.class);

        for (int i = 0; i < dataDeviceList.size(); i++) {
            MyApplication.getLogger().i(dataDeviceList.get(i).getMessageID() + "\t时间：" + dataDeviceList.get(i).getDate() + "\t温度:" + dataDeviceList.get(i).getTemp() + "\t湿度：" + dataDeviceList.get(i).getHumidity() + "");
        }
        tvTitle.setText(subDevice.getDeviceName());

    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        finish();
    }

    @OnClick(R.id.btn_right)
    void onBtnRightClick() {
        //TODO implement
    }

    @OnLongClick(R.id.btn_right)
    boolean onBtnRightLongClick() {
        //TODO implement
        return true;
    }
}
