package com.nuowei.smarthome.adapter;
/**
 * Copyright ©深圳市海曼科技有限公司
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.ListMain;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.XlinkDevice;

import java.util.Collections;
import java.util.List;

/**
 * @Author : 肖力
 * @Time :  2017/3/27 11:53
 * @Description :
 * @Modify record :
 */
public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MyViewHolder> implements MyItemTouchCallback.ItemTouchAdapter {

    private Context context;
    private int src;
    private List<ListMain> results;

    public MainListAdapter(int src, List<ListMain> results) {
        this.results = results;
        this.src = src;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(src, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
//        holder.imageView.setImageResource(results.get(position).getImg());
//        holder.textView.setText(results.get(position).getName());

        if (results.get(position).isSub()) {
            SubDevice subDevice = SubDeviceManage.getInstance().getDevice(results.get(position).getDeviceMac(), results.get(position).getSubMac());
            holder.textView.setText(subDevice.getDeviceName());

            switch (subDevice.getDeviceType()) {
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:
                    holder.imageView.setImageResource(R.drawable.device_light);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                    holder.imageView.setImageResource(R.drawable.device_door);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                    holder.imageView.setImageResource(R.drawable.device_water);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                    holder.imageView.setImageResource(R.drawable.device_pir);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                    holder.imageView.setImageResource(R.drawable.device_smoke);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                    holder.imageView.setImageResource(R.drawable.device_thp);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                    holder.imageView.setImageResource(R.drawable.device_gas);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                    holder.imageView.setImageResource(R.drawable.device_co);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                    holder.imageView.setImageResource(R.drawable.device_sos);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                    holder.imageView.setImageResource(R.drawable.device_sw);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                    holder.imageView.setImageResource(R.drawable.device_plug);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                    holder.imageView.setImageResource(R.drawable.device_plug);
                    break;
                default:
                    holder.imageView.setImageResource(R.drawable.home_device);
                    break;
            }
        } else {
            XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(results.get(position).getDeviceMac());
            holder.textView.setText(xlinkDevice.getDeviceName());
            switch (xlinkDevice.getDeviceType()) {
                case Constants.DEVICE_TYPE.DEVICE_WIFI_RC:
                    holder.imageView.setImageResource(R.drawable.home_security);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY:
                    holder.imageView.setImageResource(R.drawable.device_gw);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN:
                    holder.imageView.setImageResource(R.drawable.device_plug);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN:
                    holder.imageView.setImageResource(R.drawable.home_electric);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_WIFI_AIR:
                    holder.imageView.setImageResource(R.drawable.home_light);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition == results.size() - 1 || toPosition == results.size() - 1) {
            return;
        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(results, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(results, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(int position) {
        results.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = width / 4;
            itemView.setLayoutParams(layoutParams);
            textView = (TextView) itemView.findViewById(R.id.tv_txt);
            imageView = (ImageView) itemView.findViewById(R.id.image_icon);
        }
    }

}

