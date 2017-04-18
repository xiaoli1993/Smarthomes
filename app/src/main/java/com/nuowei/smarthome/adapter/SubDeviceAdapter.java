package com.nuowei.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author : 肖力
 * @Time :  2017/4/8 10:54
 * @Description :
 * @Modify record :
 */

public class SubDeviceAdapter extends BaseAdapter {

    private List<SubDevice> subList;

    private Context context;
    private LayoutInflater layoutInflater;

    public SubDeviceAdapter(Context context, List<SubDevice> subList) {
        this.context = context;
        this.subList = subList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return subList.size();
    }

    @Override
    public SubDevice getItem(int position) {
        return subList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_lists, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((SubDevice) getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(SubDevice subDevice, ViewHolder holder) {
        //TODO implement
        holder.deviceName.setText(subDevice.getDeviceName());
        switch (subDevice.getDeviceType()) {
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:
                holder.deviceIcon.setImageResource(R.drawable.device_light);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                holder.deviceIcon.setImageResource(R.drawable.device_door);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                holder.deviceIcon.setImageResource(R.drawable.device_water);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                holder.deviceIcon.setImageResource(R.drawable.device_pir);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                holder.deviceIcon.setImageResource(R.drawable.device_smoke);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                holder.deviceIcon.setImageResource(R.drawable.device_thp);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                holder.deviceIcon.setImageResource(R.drawable.device_gas);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                holder.deviceIcon.setImageResource(R.drawable.device_co);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                holder.deviceIcon.setImageResource(R.drawable.device_sos);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                holder.deviceIcon.setImageResource(R.drawable.device_sw);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                holder.deviceIcon.setImageResource(R.drawable.device_plug);
                break;
            case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                holder.deviceIcon.setImageResource(R.drawable.device_plug);
                break;
            default:
                holder.deviceIcon.setImageResource(R.drawable.home_device);
                break;
        }
    }

    static class ViewHolder {
        @BindView(R.id.device_icon)
        ImageView deviceIcon;
        @BindView(R.id.device_name)
        DinProTextView deviceName;
        @BindView(R.id.image_right)
        ImageView imageRight;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

