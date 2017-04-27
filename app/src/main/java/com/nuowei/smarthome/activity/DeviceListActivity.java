package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.orhanobut.hawk.Hawk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
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

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 08:31
 * @Description :
 */
public class DeviceListActivity extends BaseActivity {
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

    @OnClick(R.id.image_btn_backs)
    public void onViewClicked() {
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
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

                        initDevice();
//                        mAdapter.notifyDataSetChanged();
                        mListRecyclerView.pullRefreshComplete();
                    }
                }, 2000);
            }
        });


        mListRecyclerView.setAdapter(mAdapter);
    }

    private void initDevice() {

        HttpManage.getInstance().getSubscribe(MyApplication.getMyApplication(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                MyApplication.getLogger().e("获取订阅设备出错:" + error.getMsg() + "\t" + error.getCode());
            }

            @Override
            public void onSuccess(int code, String response) {
                MyApplication.getLogger().json(response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray list = object.getJSONArray("list");
                    int iSize = list.length();
                    for (int i = 0; i < iSize; i++) {
                        JSONObject jsonObj = list.getJSONObject(i);
                        MyUtil.subscribeToDevice(jsonObj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initData();
                mAdapter.notifyDataSetChanged();
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    final List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
                    List<XlinkDevice> gwXlink = new ArrayList<XlinkDevice>();
                    for (int i = 0; i < xlinkDeviceList.size(); i++) {
                        if (xlinkDeviceList.get(i).getDeviceType() == Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY) {
                            gwXlink.add(xlinkDeviceList.get(i));
                            final String getSub = ZigbeeGW.GetSubDevice(MyApplication.getMyApplication().getUserInfo().getNickname());
                            if (xlinkDeviceList.get(i).getDeviceState() == 0) {
                                XlinkAgent.getInstance().connectDevice(xlinkDeviceList.get(i).getxDevice(), xlinkDeviceList.get(i).getAccessKey(), new ConnectDeviceListener() {
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
                                int sendRet = XlinkAgent.getInstance().sendPipeData(xlinkDeviceList.get(i).getxDevice(), getSub.getBytes(), null);
                                if (sendRet < 0) {
                                    MyApplication.getLogger().i("发送失败:" + sendRet + "\n" + getSub);
                                } else {
                                    MyApplication.getLogger().i("发送成功！");
                                }
                            }
                        }
                    }
                    boolean isChoice = Hawk.contains(Constants.DEVICE_GW);
                    if (isChoice) {
                        XlinkDevice xlinkDevices = Hawk.get(Constants.DEVICE_GW);
                        MainActivity.setChoiceGwDevice(xlinkDevices);
                    } else {
                        try {
                            MainActivity.setChoiceGwDevice(gwXlink.get(0));
                        } catch (Exception e) {
                        }
                    }
                    break;
            }
        }
    };

    private void initData() {
        dataSourceList.clear();
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

