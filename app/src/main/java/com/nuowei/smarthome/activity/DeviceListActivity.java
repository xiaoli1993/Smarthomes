package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.InputType;
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
import com.nuowei.smarthome.smarthomesdk.Json.AirDetector;
import com.nuowei.smarthome.smarthomesdk.Json.Remote;
import com.nuowei.smarthome.smarthomesdk.Json.SmartPlug;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.smarthomesdk.utils.XlinkUtils;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.orhanobut.hawk.Hawk;
import com.wevey.selector.dialog.DialogInterface;
import com.wevey.selector.dialog.MDEditDialog;
import com.wevey.selector.dialog.MDSelectionDialog;
import com.wevey.selector.dialog.NormalAlertDialog;

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
import io.xlink.wifi.sdk.XlinkCode;
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

        mListRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                ListMain item = dataSourceList.get(position);
                showOnLongClikDialog(DeviceListActivity.this, item);
                return true;
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

    /**
     * 显示长按弹出框
     *
     * @param context
     * @param item
     */
    private void showOnLongClikDialog(final Context context, final ListMain item) {
        final List<String> datas = new ArrayList<>();
        datas.add(getString(R.string.Change_Name));
        datas.add(getString(R.string.Delete_Device));
        datas.add(getString(R.string.Device_TOP));
        if (!item.isSub()) {
            datas.add(getString(R.string.Share_Device));
        }
        new MDSelectionDialog.Builder(context)
                .setCanceledOnTouchOutside(true)
                .setItemTextColor(R.color.black_light)
                .setItemHeight(50)
                .setItemWidth(0.8f)  //屏幕宽度*0.8
                .setItemTextSize(15)
                .setCanceledOnTouchOutside(true)
                .setOnItemListener(new DialogInterface.OnItemClickListener<MDSelectionDialog>() {
                    @Override
                    public void onItemClick(MDSelectionDialog dialog, View button, int position) {
                        switch (position) {
                            case 0:
                                showChangeNameDialog(context, item);
                                break;
                            case 1:
                                showDeleteDeviceDialog(context, item);
                                break;
                            case 2:

                                break;
                            case 3:
                                XlinkDevice subDevice = DeviceManage.getInstance().getDevice(item.getDeviceMac());
                                Intent intent = new Intent(DeviceListActivity.this, ShareDeviceActivity.class);
                                //用Bundle携带数据
                                Bundle bundle = new Bundle();
                                //传递name参数为tinyphp
                                bundle.putString(Constants.GATEWAY_MAC, subDevice.getDeviceMac());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .build()
                .setDatas(datas)
                .show();
    }

    /**
     * 显示删除弹出框
     *
     * @param context
     * @param item
     */
    private void showDeleteDeviceDialog(Context context, final ListMain item) {

        new NormalAlertDialog.Builder(context).setTitleVisible(false)
                .setTitleText(getResources().getString(R.string.Tips))
                .setTitleTextColor(R.color.black_light)
                .setContentText(getString(R.string.is_delete_device))
                .setContentTextColor(R.color.black_light)
                .setLeftButtonText(getResources().getString(R.string.cancel))
                .setLeftButtonTextColor(R.color.error_stroke_color)
                .setRightButtonText(getResources().getString(R.string.dialog_ok))
                .setRightButtonTextColor(R.color.color_schedule_start)
                .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<NormalAlertDialog>() {
                    @Override
                    public void clickLeftButton(NormalAlertDialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(NormalAlertDialog dialog, View view) {
                        dialog.dismiss();
                        if (item.isSub()) {
                            SubDevice subDevice = SubDeviceManage.getInstance().getDevice(item.getDeviceMac(), item.getSubMac());
                            deleteSubDevice(item.getDeviceMac(), subDevice);
                        } else {
                            XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(item.getDeviceMac());
                            deleteWifiDevice(xlinkDevice);
                        }
                    }
                })
                .build()
                .show();
    }

    /**
     * 删除子设备
     *
     * @param subDevice
     */
    private void deleteSubDevice(String deviceMac, SubDevice subDevice) {
        String rmoveDevice;
        if (subDevice.getDeviceType() == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB || subDevice.getDeviceType() == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN || subDevice.getDeviceType() == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN) {
            rmoveDevice = ZigbeeGW.RemoveRGBorPlug(MyApplication.getMyApplication().getUserInfo().getNickname(), subDevice.getIndex());
        } else {
            rmoveDevice = ZigbeeGW.RemoveSensor(MyApplication.getMyApplication().getUserInfo().getNickname(), subDevice.getIndex());
        }
        sendDate(rmoveDevice);
        SubDeviceManage.getInstance().removeDevice(deviceMac, subDevice.getDeviceMac());
        Message message = new Message();
        message.what = 2;
        mHandler.sendMessage(message);
    }

    /**
     * 删除WIFI设备
     *
     * @param xlinkDevice
     */
    private void deleteWifiDevice(final XlinkDevice xlinkDevice) {
        HttpManage.getInstance().unsubscribe(DeviceListActivity.this, xlinkDevice.getxDevice().getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                MyApplication.getLogger().e("取消订阅失败" + error.getCode());
            }

            @Override
            public void onSuccess(int code, String response) {
                MyApplication.getLogger().i("取消订阅成功" + response);
                DeviceManage.getInstance().removeDevice(xlinkDevice.getDeviceMac());
                Message message = new Message();
                message.what = 2;
                mHandler.sendMessage(message);
            }
        });
    }

    /**
     * 显示修改名字弹出框
     *
     * @param context
     * @param item
     */
    private void showChangeNameDialog(final Context context, final ListMain item) {
        String deviceName = "";
        if (item.isSub()) {
            SubDevice subDevice = SubDeviceManage.getInstance().getDevice(item.getDeviceMac(), item.getDeviceMac());
            deviceName = subDevice.getDeviceName();
        } else {
            XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(item.getDeviceMac());
            deviceName = xlinkDevice.getDeviceName();
        }

        new MDEditDialog.Builder(context).setTitleVisible(true)
                .setTitleText(getString(R.string.Change_Name))
                .setTitleTextSize(20)
                .setTitleTextColor(R.color.black_light)
                .setContentText(deviceName)
                .setContentTextSize(18)
                .setMaxLength(20)
                .setHintText(getString(R.string.no_device_name))
                .setMaxLines(1)
                .setContentTextColor(R.color.colorPrimary)
                .setButtonTextSize(14)
                .setLeftButtonTextColor(R.color.colorPrimary)
                .setLeftButtonText(getString(R.string.cancel))
                .setRightButtonTextColor(R.color.colorPrimary)
                .setRightButtonText(getString(R.string.dialog_ok))
                .setLineColor(R.color.colorPrimary)
                .setInputTpye(InputType.TYPE_CLASS_TEXT)
                .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<MDEditDialog>() {

                    @Override
                    public void clickLeftButton(MDEditDialog dialog, View view) {

                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(MDEditDialog dialog, View view) {
                        final String rename = dialog.getEditTextContent();
                        if (item.isSub()) {
                            SubDevice subDevice = SubDeviceManage.getInstance().getDevice(item.getDeviceMac(), item.getDeviceMac());
                            String changeName = ZigbeeGW.ChangeSubDeviceName(MyApplication.getMyApplication().getUserInfo().getNickname(), subDevice.getIndex(), rename);
                            sendDate(changeName);
                            subDevice.setDeviceName(changeName);
                            SubDeviceManage.getInstance().addDevice(subDevice);
                        } else {
                            final XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(item.getDeviceMac());
                            String changeName = "";
                            switch (xlinkDevice.getDeviceType()) {
                                case Constants.DEVICE_TYPE.DEVICE_WIFI_RC:
                                    changeName = Remote.SetRenoteName(MyApplication.getMyApplication().getUserInfo().getNickname(), rename);
                                    break;
                                case Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY:
                                    changeName = ZigbeeGW.ChangeDeviceName(MyApplication.getMyApplication().getUserInfo().getNickname(), rename);
                                    break;
                                case Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN:
                                    changeName = SmartPlug.ChangeDeviceName(MyApplication.getMyApplication().getUserInfo().getNickname(), rename);
                                    break;
                                case Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN:
                                    changeName = SmartPlug.ChangeDeviceName(MyApplication.getMyApplication().getUserInfo().getNickname(), rename);
                                    break;
                                case Constants.DEVICE_TYPE.DEVICE_WIFI_AIR:
                                    changeName = AirDetector.SetAirName(MyApplication.getMyApplication().getUserInfo().getNickname(), rename);
                                    break;
                            }
                            XlinkAgent.getInstance().sendPipeData(xlinkDevice.getxDevice(), changeName.getBytes(), new SendPipeListener() {
                                @Override
                                public void onSendLocalPipeData(XDevice device, int code, int messageId) {
                                    switch (code) {
                                        case XlinkCode.SUCCEED:
                                            XlinkUtils.shortTips(context.getResources().getString(R.string.modify_success));
                                            xlinkDevice.setDeviceName(rename);
                                            DeviceManage.getInstance().addDevice(xlinkDevice);
                                            initData();
                                            mAdapter.notifyDataSetChanged();
                                            break;
                                        case XlinkCode.TIMEOUT:
                                            // 重新调用connect
                                            XlinkUtils.shortTips(context.getResources().getString(R.string.time_out));
                                            break;
                                        case XlinkCode.SERVER_CODE_UNAUTHORIZED:
                                            MyApplication.getLogger().e("控制设备失败,当前帐号未订阅此设备，请重新订阅");
                                            break;
                                        case XlinkCode.SERVER_DEVICE_OFFLIEN:
                                            MyApplication.getLogger().e("设备不在线");
                                            XlinkUtils.shortTips(context.getResources().getString(R.string.offline));
                                            break;
                                        default:
                                            MyApplication.getLogger().e("控制设备其他错误码:" + code);
                                            break;
                                    }

                                }
                            });
                        }
                        dialog.dismiss();
                    }
                }).setMinHeight(0.3f)
                .setWidth(0.8f)
                .build()
                .show();
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
                case 2:
                    initData();
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initData() {
        dataSourceList.clear();
        List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
        for (int x = 0; x < xlinkDeviceList.size(); x++) {

            MyApplication.getLogger().w("List列表:" + xlinkDeviceList.get(x).getDeviceMac() + "\t" + xlinkDeviceList.get(x).getDeviceName());
            dataSourceList.add(new ListMain("", xlinkDeviceList.get(x).getDeviceMac(), false, xlinkDeviceList.get(x).getDeviceType()));
        }
        List<SubDevice> subDeviceList = SubDeviceManage.getInstance().getDevices();
        for (int i = 0; i < subDeviceList.size(); i++) {

            MyApplication.getLogger().w("List列表:" + subDeviceList.get(i).getZigbeeMac() + "\t" + subDeviceList.get(i).getDeviceMac());
            dataSourceList.add(new ListMain(subDeviceList.get(i).getZigbeeMac(), subDeviceList.get(i).getDeviceMac(), true, subDeviceList.get(i).getDeviceType()));
        }
//        List<Contact> contact = FList.getInstance().list();
//        for (int i = 0; i < contact.size(); i++) {
//            dataSourceList.add(new ListMain(contact.get(i).contactId, contact.get(i).contactId, false, Constants.DEVICE_TYPE.DEVICE_WIFI_IPC));
//        }
    }


    private void sendDate(final String json) {
        if (MainActivity.getChoiceGwDevice().getDeviceState() == 0) {
            XlinkAgent.getInstance().connectDevice(MainActivity.getChoiceGwDevice().getxDevice(), MainActivity.getChoiceGwDevice().getxDevice().getAccessKey(), new ConnectDeviceListener() {
                @Override
                public void onConnectDevice(XDevice xDevice, int i) {
                    XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), json.getBytes(),
                            new SendPipeListener() {
                                @Override
                                public void onSendLocalPipeData(XDevice device, int code, int messageId) {
                                    switch (code) {
                                        case XlinkCode.SUCCEED:
                                            XlinkUtils.shortTips(getResources().getString(R.string.modify_success));
                                            break;
                                        case XlinkCode.TIMEOUT:
                                            // 重新调用connect
                                            XlinkUtils.shortTips(getResources().getString(R.string.time_out));
                                            break;
                                        case XlinkCode.SERVER_CODE_UNAUTHORIZED:
                                            MyApplication.getLogger().e("控制设备失败,当前帐号未订阅此设备，请重新订阅");
                                            break;
                                        case XlinkCode.SERVER_DEVICE_OFFLIEN:
                                            MyApplication.getLogger().e("设备不在线");
                                            XlinkUtils.shortTips(getResources().getString(R.string.offline));
                                            break;
                                        default:
                                            MyApplication.getLogger().e("控制设备其他错误码:" + code);
                                            break;
                                    }
                                }
                            });
                }
            });
        } else {
            XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), json.getBytes(),
                    new SendPipeListener() {
                        @Override
                        public void onSendLocalPipeData(XDevice device, int code, int messageId) {
                            switch (code) {
                                case XlinkCode.SUCCEED:
                                    XlinkUtils.shortTips(getResources().getString(R.string.modify_success));
                                    break;
                                case XlinkCode.TIMEOUT:
                                    // 重新调用connect
                                    XlinkUtils.shortTips(getResources().getString(R.string.time_out));
                                    break;
                                case XlinkCode.SERVER_CODE_UNAUTHORIZED:
                                    MyApplication.getLogger().e("控制设备失败,当前帐号未订阅此设备，请重新订阅");
                                    break;
                                case XlinkCode.SERVER_DEVICE_OFFLIEN:
                                    MyApplication.getLogger().e("设备不在线");
                                    XlinkUtils.shortTips(getResources().getString(R.string.offline));
                                    break;
                                default:
                                    MyApplication.getLogger().e("控制设备其他错误码:" + code);
                                    break;
                            }
                        }
                    });
        }
    }

}

