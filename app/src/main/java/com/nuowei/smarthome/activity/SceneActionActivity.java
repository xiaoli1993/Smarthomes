package com.nuowei.smarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.ActionAdapter;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.Action;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/4/8 11:41
 * @Description :场景动作选择
 * @Modify record :
 */

public class SceneActionActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.mList)
    ListView mList;
    @BindView(R.id.device_icon)
    ImageView deviceIcon;
    @BindView(R.id.device_name)
    DinProTextView deviceName;
    @BindView(R.id.rl_device)
    LinearLayout rlDevice;

    private ActionAdapter actionAdapter;
    private List<Action> list;
    public final static int SUB_DEVICE_CODE = 2;
    private boolean isGw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initData();
        initEven();
    }

    private void initData() {
        list = new ArrayList<Action>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isGw = bundle.getBoolean("isGw");
        MyApplication.getLogger().i("isGw" + isGw);
        if (isGw) {
            String mac = bundle.getString(Constants.GATEWAY_MAC);
//            XlinkDevice device = DeviceManage.getInstance().getDevice(mac);
            list.add(new Action(getResources().getString(R.string.AtHome), mac, mac, Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY, true, false));
            list.add(new Action(getResources().getString(R.string.OutAlert), mac, mac, Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY, true, false));
            list.add(new Action(getResources().getString(R.string.Disarm), mac, mac, Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY, true, false));
        } else {
            String zigbeemac = bundle.getString(Constants.ZIGBEE_MAC);
            String mac = bundle.getString(Constants.GATEWAY_MAC);
            SubDevice subDevice = SubDeviceManage.getInstance().getDevice(mac, zigbeemac);
            switch (subDevice.getDeviceType()) {
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                    list.add(new Action(getResources().getString(R.string.Arming), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.Disarm), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                    list.add(new Action(getResources().getString(R.string.Arming), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.Disarm), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                    list.add(new Action(getResources().getString(R.string.poweroff), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.poweron), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.usboff), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.usbon), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.PNUN), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.PNUF), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.PFUN), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.PFUF), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                    list.add(new Action(getResources().getString(R.string.poweroff), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.poweron), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:
                    list.add(new Action(getResources().getString(R.string.turned_on), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.turned_off), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    list.add(new Action(getResources().getString(R.string.Brightness), mac, zigbeemac, subDevice.getDeviceType(), false, false));
                    break;
            }
        }
    }

    private void initEven() {
        rlDevice.setVisibility(View.GONE);
        tvTitle.setText(getResources().getString(R.string.Perform_tasks));
        tvRight.setVisibility(View.GONE);
        actionAdapter = new ActionAdapter(this, list);
        mList.setAdapter(actionAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Constants.GATEWAY_MAC, list.get(position).getGwMac());
                intent.putExtra(Constants.ZIGBEE_MAC, list.get(position).getSubMac());
                intent.putExtra("action", list.get(position).getAction());
                intent.putExtra("isGw", isGw);
                MyApplication.getLogger().i("isGw" + isGw);
                intent.putExtra(Constants.DEVICE_TYPES, list.get(position).getDeviceType());
                setResult(SUB_DEVICE_CODE, intent);
                finish();
            }
        });
    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }


}