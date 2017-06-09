package com.nuowei.smarthome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.jwkj.activity.AddContactActivity;
import com.jwkj.activity.NetworkModeActivity;
import com.jwkj.data.Contact;
import com.jwkj.widget.NormalDialog;
import com.libzxing.activity.CaptureActivity;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.ChoiceAddDeviceAdapter;
import com.nuowei.smarthome.adapter.ChoiceAddDeviceGidwAdapter;
import com.nuowei.smarthome.modle.ChoiceAddDevice;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.orhanobut.hawk.Hawk;
import com.wevey.selector.dialog.DialogInterface;
import com.wevey.selector.dialog.MDSelectionDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private SimpleAdapter sim_adapter;
    private List<Map<String, Object>> data_list;
    private Context mContext;
    private static final int QRcodeRequestCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        mContext = this;
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
            mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ChoiceAddDevice choiceAddDevice = chioseAddDevices.get(position);
                    if (choiceAddDevice.iswifi()) {
                        if (choiceAddDevice.getDeviceid() == Constants.DEVICE_TYPE.DEVICE_WIFI_IPC) {
//                            NormalDialog dialog = new NormalDialog(mContext);
//                            dialog.setOnDialogItemClick(onChooseAddDialogItemClick);
//                            dialog.setmContact(null);
//                            dialog.showChooseAddDialog(1);

                            showMDSelectionDialog();
                        } else {
                            Intent intent = new Intent(AddDeviceActivity.this, SmartLinkActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(Constants.DEVICE_TYPES, choiceAddDevice.getDeviceid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(AddDeviceActivity.this, AddSubDeviceActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.DEVICE_TYPES, choiceAddDevice.getDeviceid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } else

        {
//            data_list = new ArrayList<Map<String, Object>>();
//            String[] from = {"image", "text", "iswifi"};
//            int[] to = {R.id.image_icon, R.id.tv_txt};
//            getZigbeeData();
//            data_list = getData();
//            sim_adapter = new SimpleAdapter(this, data_list, R.layout.item_main, from, to);

            mGidwAdapter = new ChoiceAddDeviceGidwAdapter(AddDeviceActivity.this, getZigbeeData());
            mListview.setVisibility(View.GONE);
            mGridview.setVisibility(View.VISIBLE);
            mGridview.setAdapter(mGidwAdapter);

            mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ChoiceAddDevice choiceAddDevice = chioseAddDevices.get(position);
                    if (choiceAddDevice.iswifi()) {
                        Intent intent = new Intent(AddDeviceActivity.this, SmartLinkActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.DEVICE_TYPES, choiceAddDevice.getDeviceid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(AddDeviceActivity.this, AddSubDeviceActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.DEVICE_TYPES, choiceAddDevice.getDeviceid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
        }


    }

    private void showMDSelectionDialog() {
        List<String> datas = new ArrayList<>();
        datas.add(getResources().getString(R.string.intelligent_online));
        datas.add(getResources().getString(R.string.manually_add));
        datas.add(getResources().getString(R.string.moni_shuju3));

        new MDSelectionDialog.Builder(AddDeviceActivity.this)
                .setCanceledOnTouchOutside(true)
                .setItemTextColor(R.color.black_light)
                .setItemHeight(50)
                .setItemWidth(0.8f)  //屏幕宽度*0.8
                .setItemTextSize(15)
                .setCanceledOnTouchOutside(true)
                .setOnItemListener(new DialogInterface.OnItemClickListener<MDSelectionDialog>() {
                    @Override
                    public void onItemClick(MDSelectionDialog dialog, View button, int position) {
                        if (position == 0) {
                            Intent radar_add = new Intent(mContext, NetworkModeActivity.class);
                            mContext.startActivity(radar_add);
                        } else if (position == 1) {
                            Intent add_contact = new Intent(mContext, AddContactActivity.class);
                            mContext.startActivity(add_contact);
                        } else {
                            Intent scan_qr_code = new Intent(mContext, CaptureActivity.class);
                            scan_qr_code.putExtra("type", 1);
                            startActivityForResult(scan_qr_code, QRcodeRequestCode);
                        }
                        finish();
                        dialog.dismiss();
                    }
                })
                .build()
                .setDatas(datas)
                .show();

    }

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < chioseAddDevices.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", MyUtil.getIconImage(chioseAddDevices.get(i).getDeviceid()));
            map.put("text", chioseAddDevices.get(i).getDevicename());
            map.put("iswifi", chioseAddDevices.get(i).iswifi());
            data_list.add(map);
        }

        return data_list;
    }

    //添加设备方式选择框
    private NormalDialog.onDialogItemClick onChooseAddDialogItemClick = new NormalDialog.onDialogItemClick() {

        @Override
        public void onItemClick(View view, Contact mContact, int position, int contactPosition) {
            if (position == 1) {
                Intent radar_add = new Intent(mContext, NetworkModeActivity.class);
                mContext.startActivity(radar_add);
            } else if (position == 2) {
                Intent add_contact = new Intent(mContext, AddContactActivity.class);
                mContext.startActivity(add_contact);
            } else {
                Intent scan_qr_code = new Intent(mContext, CaptureActivity.class);
                scan_qr_code.putExtra("type", 1);
                startActivityForResult(scan_qr_code, QRcodeRequestCode);
            }
        }
    };

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
        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_IPC, getResources().getString(R.string.device_ipc), true));
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
//        chioseAddDevices.add(new ChoiceAddDevice(Constants.DEVICE_TYPE.DEVICE_WIFI_AIR, getResources().getString(R.string.Air), true));
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

    /**
     * 通过包名跳转
     *
     * @param activityName
     */
    public void startActivityForName(String activityName, Bundle paramBundle) {
        try {
            Class clazz = Class.forName(activityName);
            Intent intent = new Intent(this, clazz);
            if (paramBundle != null)
                intent.putExtras(paramBundle);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void startActivityForName(String activityName) {
        try {
            Class clazz = Class.forName(activityName);
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
