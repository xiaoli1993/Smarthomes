package com.nuowei.smarthome.adapter;/**
 * Created by hp on 2016/8/17.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.ChoiceAddDevice;

import java.util.ArrayList;


/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/8/17 14:35
 */
public class ChoiceAddDeviceAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ChoiceAddDevice> devices;
    private LayoutInflater inflater;


    public ChoiceAddDeviceAdapter(Activity activity, ArrayList<ChoiceAddDevice> device) {
        this.mContext = activity;
        this.devices = device;
        inflater = LayoutInflater.from(mContext);
    }

    public void setDevices(ArrayList<ChoiceAddDevice> devices) {
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index = position;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_chiose_device,
                    parent, false);
        }
        TextView device_name = (TextView) convertView.findViewById(R.id.device_name);
        ImageView device_icon = (ImageView) convertView.findViewById(R.id.device_icon);

        final ChoiceAddDevice device = devices.get(index);
        device_name.setText(device.getDevicename());
        switch (device.getDeviceid()) {
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:
                device_icon.setImageResource(R.drawable.device_light);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                device_icon.setImageResource(R.drawable.device_door);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                device_icon.setImageResource(R.drawable.device_water);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                device_icon.setImageResource(R.drawable.device_pir);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                device_icon.setImageResource(R.drawable.device_smoke);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                device_icon.setImageResource(R.drawable.device_thp);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                device_icon.setImageResource(R.drawable.device_gas);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                device_icon.setImageResource(R.drawable.device_co);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                device_icon.setImageResource(R.drawable.device_sos);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                device_icon.setImageResource(R.drawable.device_sw);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                device_icon.setImageResource(R.drawable.device_plug);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                device_icon.setImageResource(R.drawable.device_plug);
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY:
                device_icon.setImageResource(R.drawable.device_gw);
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN:
                device_icon.setImageResource(R.drawable.device_plug);
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN:
                device_icon.setImageResource(R.drawable.device_plug);
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_AIR:
//                device_icon.setImageResource(R.drawable.);
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_IPC:
                device_icon.setImageResource(R.drawable.device_ipc);
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_RC:
//                device_icon.setImageResource(R.drawable.rc1);
                break;
            case Constants.DEVICE_TYPE.DEVICE_WIFI_GAS:
//                device_icon.setImageResource(R.drawable.ws2cg);
                break;

        }
//        if (devices.get(position) != null) {
//            convertView.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Bundle b = new Bundle();
//                    b.putSerializable(Constants.DEVICE_TYPES, device.getDeviceid());
//                    if (device.iswifi()) {
////                        Intent i = new Intent();
////                        i.putExtras(b);
////                        i.setClass(mContext, Nextactivity.class);
////                        mContext.startActivity(i);
////                        ChoiceAddDeviceActivity activity = (ChoiceAddDeviceActivity) mContext;
////                        activity.finish();
//                    } else {
////                        Intent i = new Intent();
////                        i.putExtras(b);
////                        i.setClass(mContext, Zigbee_Add.class);
////                        mContext.startActivity(i);
////                        ChoiceAddDeviceActivity activity = (ChoiceAddDeviceActivity) mContext;
////                        activity.finish();
//                    }
//
//                }
//            });
//        }
        return convertView;
    }
}
