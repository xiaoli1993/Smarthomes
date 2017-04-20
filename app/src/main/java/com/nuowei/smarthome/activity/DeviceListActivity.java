package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import com.nuowei.smarthome.view.textview.AvenirTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 08:31
 * @Description :
 */
public class DeviceListActivity extends SwipeBackActivity {
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.btn_right)
    ImageButton btn_right;
    @BindView(R.id.mListRecyclerView)
    FamiliarRefreshRecyclerView mListRecyclerView;
    private List<ListMain> dataSourceList = new ArrayList<ListMain>();
    private MainListAdapter mAdapter;
    private FamiliarRecyclerView mFamiliarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        initData();
        initEven();
    }

    private void initEven() {
        tvTitle.setText(getResources().getString(R.string.Device));
        tvRight.setVisibility(View.GONE);
        btn_right.setVisibility(View.GONE);
        mAdapter = new MainListAdapter(R.layout.item_lists, dataSourceList);
        mListRecyclerView.setLoadMoreEnabled(false);

        mFamiliarRecyclerView = mListRecyclerView.getFamiliarRecyclerView();
        // ItemAnimator
        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Item Click and Item Long Click
        mListRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                ListMain item = dataSourceList.get(position);
                if (item.isSub()) {
                    switch (item.getDeviceType()) {
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:

                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                            break;
                        default:
                            break;
                    }
                } else {

                }
            }
        });
        mListRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        String getSub = ZigbeeGW.GetSubDevice(userName);
//                        XlinkAgent.getInstance().sendPipeData(xDevice, getSub.getBytes(), new SendPipeListener() {
//                            @Override
//                            public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
//                                JSONObject object = XlinkAgent.deviceToJson(xDevice);
//                                MyApplication.getLogger().i("发送:" + i + "\n" + getSub);
//                            }
//                        });
                        mAdapter.notifyDataSetChanged();
                        mListRecyclerView.pullRefreshComplete();
                    }
                }, 1000);
            }
        });


        mListRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {

        List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
        for (int x = 0; x < xlinkDeviceList.size(); x++) {

            MyApplication.getLogger().w("List列表:" + xlinkDeviceList.get(x).getDeviceMac());
            dataSourceList.add(new ListMain("", xlinkDeviceList.get(x).getDeviceMac(), false, xlinkDeviceList.get(x).getDeviceType()));
        }
        List<SubDevice> subDeviceList = SubDeviceManage.getInstance().getDevices();
        for (int i = 0; i < subDeviceList.size(); i++) {

            MyApplication.getLogger().w("List列表:" + subDeviceList.get(i).getZigbeeMac() + "\t" + subDeviceList.get(i).getDeviceMac());
            dataSourceList.add(new ListMain(subDeviceList.get(i).getZigbeeMac(), subDeviceList.get(i).getDeviceMac(), true, subDeviceList.get(i).getDeviceType()));
        }
    }

}

