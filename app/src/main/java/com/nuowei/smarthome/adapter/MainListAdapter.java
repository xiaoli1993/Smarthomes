package com.nuowei.smarthome.adapter;
/**
 * Copyright ©深圳市海曼科技有限公司
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.ListMain;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.Collections;
import java.util.List;

/**
 * @Author : 肖力
 * @Time :  2017/4/27 11:53
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
                    if (subDevice.getRgbOnoff() == 1) {
                        holder.textState.setText(context.getResources().getString(R.string.Open));
                    } else {
                        holder.textState.setText(context.getResources().getString(R.string.Close));
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                    holder.imageView.setImageResource(R.drawable.device_door);
                    switch (subDevice.getDeviceOnoff()) {
                        case 5:
                            holder.textState.setText(context.getResources().getString(R.string.Open));
                            break;
                        case 4:
                            holder.textState.setText(context.getResources().getString(R.string.Close));
                            break;
                        case 1:
                            holder.textState.setText(context.getResources().getString(R.string.Open));
                            break;
                        case 0:
                            holder.textState.setText(context.getResources().getString(R.string.Close));
                            break;
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                    holder.imageView.setImageResource(R.drawable.device_water);
                    switch (subDevice.getDeviceOnoff()) {
                        case 5:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 4:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                        case 1:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 0:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                    holder.imageView.setImageResource(R.drawable.device_pir);
                    YoYo.with(Techniques.Swing).duration(1200).repeat(5)
                            .playOn(holder.imageView);
                    switch (subDevice.getDeviceOnoff()) {
                        case 5:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 4:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                        case 1:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 0:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                    holder.imageView.setImageResource(R.drawable.device_smoke);
                    switch (subDevice.getDeviceOnoff()) {
                        case 5:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 4:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                        case 1:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 0:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                    holder.imageView.setImageResource(R.drawable.device_thp);
                    YoYo.with(Techniques.FlipOutY).duration(1200)
                            .playOn(holder.imageView);
                    holder.imageView.setImageResource(R.drawable.device_temp);
                    YoYo.with(Techniques.FlipInY).duration(1200)
                            .playOn(holder.imageView);
                    holder.textState.setText(subDevice.getTemp() + "° " + subDevice.getHumidity() + "%");
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                    holder.imageView.setImageResource(R.drawable.device_gas);


                    switch (subDevice.getDeviceOnoff()) {
                        case 5:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 4:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                        case 1:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 0:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                    holder.imageView.setImageResource(R.drawable.device_co);
                    switch (subDevice.getDeviceOnoff()) {
                        case 5:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 4:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                        case 1:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 0:
                            holder.textState.setText(context.getResources().getString(R.string.Clear));
                            break;
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                    holder.imageView.setImageResource(R.drawable.device_sos);
                    switch (subDevice.getDeviceOnoff()) {
                        case 1:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                    holder.imageView.setImageResource(R.drawable.device_sw);
                    Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
                    holder.imageView.setAnimation(mAnimation);
                    switch (subDevice.getDeviceOnoff()) {
                        case 3:
                            holder.textState.setText(context.getResources().getString(R.string.Alarm));
                            break;
                        case 2:
                            holder.textState.setText(context.getResources().getString(R.string.home));
                            break;
                        case 1:
                            holder.textState.setText(context.getResources().getString(R.string.away));
                            break;
                        case 0:
                            holder.textState.setText(context.getResources().getString(R.string.disarm));
                            break;
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                    holder.imageView.setImageResource(R.drawable.device_plug);
                    if (subDevice.getRelayOnoff() == 1) {
                        if (subDevice.getUsbOnoff() == 1) {
                            holder.textState.setText(context.getResources().getString(R.string.PNUN));

                        } else {
                            holder.textState.setText(context.getResources().getString(R.string.PNUF));

                        }
                    } else {
                        if (subDevice.getUsbOnoff() == 1) {
                            holder.textState.setText(context.getResources().getString(R.string.PFUN));

                        } else {
                            holder.textState.setText(context.getResources().getString(R.string.PFUF));

                        }
                    }
                    break;
                case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                    holder.imageView.setImageResource(R.drawable.device_plug);
                    if (subDevice.getRelayOnoff() == 1) {
                        holder.textState.setText(context.getResources().getString(R.string.Open));

                    } else {
                        holder.textState.setText(context.getResources().getString(R.string.Close));

                    }
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
                    holder.textState.setText("");
                    holder.imageView.setImageResource(R.drawable.home_security);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY:
                    holder.textState.setText("");
                    holder.imageView.setImageResource(R.drawable.device_gw);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN:
                    holder.textState.setText("");
                    holder.imageView.setImageResource(R.drawable.device_plug);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN:
                    holder.textState.setText("");
                    holder.imageView.setImageResource(R.drawable.home_electric);
                    break;
                case Constants.DEVICE_TYPE.DEVICE_WIFI_AIR:
                    holder.textState.setText("");
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
        public TextView textState;

        public MyViewHolder(View itemView) {
            super(itemView);
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            int width = wm.getDefaultDisplay().getWidth();
//            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
//            layoutParams.height = width / 5;
//            itemView.setLayoutParams(layoutParams);
            textView = (DinProTextView) itemView.findViewById(R.id.device_name);
            textState = (DinProTextView) itemView.findViewById(R.id.tv_state);
            imageView = (ImageView) itemView.findViewById(R.id.device_icon);
        }
    }

}

