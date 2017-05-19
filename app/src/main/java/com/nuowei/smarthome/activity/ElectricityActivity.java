package com.nuowei.smarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainListAdapter;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.ListMain;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;

public class ElectricityActivity extends BaseActivity {

    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.refreshListRecyclerView)
    FamiliarRefreshRecyclerView refreshListRecyclerView;

    @BindView(R.id.activity_security)
    LinearLayout activitySecurity;
    @BindView(R.id.image_home)
    ImageButton imageHome;
    @BindView(R.id.image_away)
    ImageButton imageAway;
    @BindView(R.id.image_disarm)
    ImageButton imageDisarm;
    @BindView(R.id.tv_home)
    DinProTextView tvHome;
    @BindView(R.id.tv_away)
    DinProTextView tvAway;
    @BindView(R.id.tv_disarm)
    DinProTextView tvDisarm;
    @BindView(R.id.btn_right)
    ImageButton btn_right;
    @BindView(R.id.ll_gw)
    LinearLayout llGw;
    private List<ListMain> dataSourceList = new ArrayList<ListMain>();
    private MainListAdapter mAdapter;

    private FamiliarRecyclerView mFamiliarRecyclerView;
    private boolean isChiose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        initData();
        initEven();
    }

    private void initData() {
        try {
            MyUtil.isEmptyString(MainActivity.getChoiceGwDevice().getDeviceMac());
            isChiose = true;
        } catch (Exception e) {
            isChiose = false;
        }

        List<SubDevice> subDeviceList = SubDeviceManage.getInstance().getDevices();
        for (int i = 0; i < subDeviceList.size(); i++) {
            MyApplication.getLogger().w("List列表:" + subDeviceList.get(i).getZigbeeMac() + "\t" + subDeviceList.get(i).getDeviceMac());
            if (isChiose) {
                if (MainActivity.getChoiceGwDevice().getDeviceMac().equals(subDeviceList.get(i).getDeviceMac())) {
                    if (subDeviceList.get(i).getDeviceType() == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN || subDeviceList.get(i).getDeviceType() == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN) {
                        dataSourceList.add(new ListMain(subDeviceList.get(i).getZigbeeMac(), subDeviceList.get(i).getDeviceMac(), true, subDeviceList.get(i).getDeviceType()));
                    }
                }
            }
        }

        List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
        for (int x = 0; x < xlinkDeviceList.size(); x++) {
            if (xlinkDeviceList.get(x).getDeviceType() == Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN || xlinkDeviceList.get(x).getDeviceType() == Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN) {
                MyApplication.getLogger().w("List列表:" + xlinkDeviceList.get(x).getDeviceMac() + "\t" + xlinkDeviceList.get(x).getDeviceName());
                dataSourceList.add(new ListMain("", xlinkDeviceList.get(x).getDeviceMac(), false, xlinkDeviceList.get(x).getDeviceType()));
            }
        }
    }

    private void initEven() {
        llGw.setVisibility(View.GONE);
        tvTitle.setText(getResources().getString(R.string.Electricity));
        tvRight.setVisibility(View.GONE);
        btn_right.setVisibility(View.GONE);
        mAdapter = new MainListAdapter(R.layout.item_lists, dataSourceList);

        refreshListRecyclerView.setLoadMoreEnabled(false);

        mFamiliarRecyclerView = refreshListRecyclerView.getFamiliarRecyclerView();
        // ItemAnimator
        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Item Click and Item Long Click
        refreshListRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                ListMain item = dataSourceList.get(position);
                if (item.isSub()) {
                    switch (item.getDeviceType()) {
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                            openDiary(item.getDeviceMac(), item.getSubMac());
                            break;
                    }
                }
            }
        });
        refreshListRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MyUtil.isEmptyString(MainActivity.getChoiceGwDevice().getDeviceMac());
                            isChiose = true;
                        } catch (Exception e) {
                            isChiose = false;
                        }
                        if (isChiose) {
                            final String getSub = ZigbeeGW.GetSubDevice(MyApplication.getMyApplication().getUserInfo().getNickname());
                            if (MainActivity.getChoiceGwDevice().getDeviceState() == 0) {
                                XlinkAgent.getInstance().connectDevice(MainActivity.getChoiceGwDevice().getxDevice(), MainActivity.getChoiceGwDevice().getAccessKey(), new ConnectDeviceListener() {
                                    @Override
                                    public void onConnectDevice(XDevice xDevice, int ret) {
                                        if (ret < 0) {
                                            MyApplication.getLogger().i("连接失败:" + ret);
                                        } else {
                                            MyApplication.getLogger().i("连接成功:" + ret);
                                            XlinkAgent.getInstance().sendPipeData(xDevice, getSub.getBytes(), new SendPipeListener() {
                                                @Override
                                                public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                                    JSONObject object = XlinkAgent.deviceToJson(xDevice);
                                                    MyApplication.getLogger().i("发送:" + i + "\n" + getSub);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), getSub.getBytes(), new SendPipeListener() {
                                    @Override
                                    public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                        JSONObject object = XlinkAgent.deviceToJson(xDevice);
                                        MyApplication.getLogger().i("发送:" + i + "\n" + getSub);
                                    }
                                });
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        refreshListRecyclerView.pullRefreshComplete();
                    }
                }, 1000);
            }
        });
        refreshListRecyclerView.setAdapter(mAdapter);

    }

    private void openDiary(String gwMac, String zigbeeMac) {
        SubDevice subDevice = SubDeviceManage.getInstance().getDevice(gwMac, zigbeeMac);
        Intent intent = new Intent(ElectricityActivity.this, THPActivity.class);
        //用Bundle携带数据
        Bundle bundle = new Bundle();
        //传递name参数为tinyphp
        bundle.putString(Constants.GATEWAY_MAC, gwMac);
        bundle.putString(Constants.ZIGBEE_MAC, zigbeeMac);
        bundle.putString(Constants.DEVICE_TEMP, subDevice.getTemp());
        bundle.putString(Constants.DEVICE_HUM, subDevice.getHumidity());

//        bundle.putInt("isGw", isGw2);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }
}
