package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.ChoiceAddDeviceAdapter;
import com.nuowei.smarthome.adapter.ChoiceAddDeviceGidwAdapter;
import com.nuowei.smarthome.modle.ChoiceAddDevice;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/4/19 11:59
 * @Description :
 * @Modify record :
 */

public class AddDeviceActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.mListview)
    ListView mListview;
    @BindView(R.id.mGridview)
    GridView mGridview;

    private ChoiceAddDeviceAdapter mAdater;
    private ChoiceAddDeviceGidwAdapter mGidwAdapter;
    private ArrayList<ChoiceAddDevice> chioseAddDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        initEven();
    }

    private void initEven() {
        tvTitle.setText(getResources().getString(R.string.choice_add_device));
        btnRight.setVisibility(View.GONE);
        tvRight.setVisibility(View.GONE);
        boolean isLists = Hawk.contains(Constants.ISLIST);
        if (isLists) {
            isLists = Hawk.get(Constants.ISLIST);
        } else {
            isLists = false;
//            setSelect(1);
        }
        if (isLists) {
            mAdater = new ChoiceAddDeviceAdapter(AddDeviceActivity.this, getZigbeeData());
            mListview.setVisibility(View.VISIBLE);
            mGridview.setVisibility(View.GONE);
            mListview.setAdapter(mAdater);
        } else {
            mGidwAdapter = new ChoiceAddDeviceGidwAdapter(AddDeviceActivity.this, getZigbeeData());
            mListview.setVisibility(View.GONE);
            mGridview.setVisibility(View.VISIBLE);
            mGridview.setAdapter(mGidwAdapter);
        }
    }

    /**
     * 获取ZIGBEE设备列表
     *
     * @return
     */
    private ArrayList<ChoiceAddDevice> getZigbeeData() {
        chioseAddDevices = new ArrayList<ChoiceAddDevice>();
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY, getResources().getString(R.string.device_gateway), true));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN, getResources().getString(R.string.device_smart_plug), true));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN, getResources().getString(R.string.device_metering_Plug), true));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB, getResources().getString(R.string.device_rgb), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS, getResources().getString(R.string.device_door_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER, getResources().getString(R.string.device_water_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR, getResources().getString(R.string.device_pir_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE, getResources().getString(R.string.device_smoke_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP, getResources().getString(R.string.device_thp_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS, getResources().getString(R.string.device_gas_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO, getResources().getString(R.string.device_co_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS, getResources().getString(R.string.device_sos_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW, getResources().getString(R.string.device_sw_sensor), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN, getResources().getString(R.string.device_smart_plug), false));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN, getResources().getString(R.string.device_metering_Plug), false));
        return chioseAddDevices;
    }

    /**
     * 获取WIFI设备列表
     *
     * @return
     */
    private ArrayList<ChoiceAddDevice> getWifiData() {
        chioseAddDevices = new ArrayList<ChoiceAddDevice>();
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY, getResources().getString(R.string.device_gateway), true));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN, getResources().getString(R.string.device_smart_plug), true));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN, getResources().getString(R.string.device_metering_Plug), true));
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_AIR, getResources().getString(R.string.Air), true));
//        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_RC, getResources().getString(R.string.device_remote), true));
//        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_GAS, getResources().getString(R.string.device_wifi_gas), true));
        return chioseAddDevices;
    }

    @OnClick({R.id.image_btn_backs, R.id.btn_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_btn_backs:
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                this.finish();
                break;
            case R.id.btn_right:

                break;
        }
    }
}
