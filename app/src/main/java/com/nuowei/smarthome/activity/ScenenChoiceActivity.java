package com.nuowei.smarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.SubDeviceAdapter;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/4/8 10:47
 * @Description :场景选择设备
 * @Modify record :
 */

public class ScenenChoiceActivity extends BaseActivity {
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


    private SubDeviceAdapter subDeviceAdapter;
    private List<SubDevice> list;
    public final static int SUB_DEVICE_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initData();
        initEven();
    }

    private void initData() {
        list = new ArrayList<SubDevice>();
        List<SubDevice> sublist = SubDeviceManage.getInstance().getDevices();
        for (int i = 0; i < sublist.size(); i++) {
            int deviceType = sublist.get(i).getDeviceType();
            if (deviceType == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB
                    || deviceType == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR
                    || deviceType == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN
                    || deviceType == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN
                    || deviceType == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS) {
                if (MainActivity.getChoiceGwDevice().getDeviceMac().equals(sublist.get(i).getDeviceMac())) {
                    list.add(sublist.get(i));
                }
            }
        }
    }

    private void initEven() {
        deviceIcon.setImageResource(R.drawable.device_gw);
        deviceName.setText(MainActivity.getChoiceGwDevice().getDeviceName());
        tvTitle.setText(getResources().getString(R.string.Perform_tasks));
        tvRight.setVisibility(View.GONE);
        subDeviceAdapter = new SubDeviceAdapter(this, list);
        mList.setAdapter(subDeviceAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.getLogger().i("position:" + list.get(position).getDeviceMac() + list.get(position).getDeviceType());
                Intent intent = new Intent();
                intent.setClass(ScenenChoiceActivity.this, SceneActionActivity.class);
                intent.putExtra(Constants.GATEWAY_MAC, list.get(position).getDeviceMac());
                intent.putExtra(Constants.ZIGBEE_MAC, list.get(position).getZigbeeMac());
                intent.putExtra("isGw", false);
                startActivityForResult(intent, SUB_DEVICE_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SUB_DEVICE_CODE:
                if (resultCode == SceneActionActivity.SUB_DEVICE_CODE) {
                    Bundle bundle = data.getExtras();
                    Intent intent = new Intent();
                    intent.putExtra(Constants.GATEWAY_MAC, bundle.getString(Constants.GATEWAY_MAC));
                    intent.putExtra(Constants.ZIGBEE_MAC, bundle.getString(Constants.ZIGBEE_MAC));
                    intent.putExtra("action", bundle.getString("action"));
                    intent.putExtra("isGw", bundle.getBoolean("isGw"));
                    intent.putExtra(Constants.DEVICE_TYPES, bundle.getString(Constants.DEVICE_TYPES));
                    setResult(SUB_DEVICE_CODE, intent);
                    finish();
                }
                break;
        }
    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }

    @OnClick(R.id.rl_device)
    void onGwDeviceClick() {
        //TODO implement
        Intent intent = new Intent();
        intent.setClass(ScenenChoiceActivity.this, SceneActionActivity.class);
        intent.putExtra(Constants.GATEWAY_MAC, MainActivity.getChoiceGwDevice().getDeviceMac());
//        intent.putExtra(Constants.ZIGBEE_MAC, "mac");
        intent.putExtra("isGw", true);
        startActivityForResult(intent, SUB_DEVICE_CODE);
    }

}
