package com.jwkj.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwkj.entity.Sensor;
import com.jwkj.global.MyApp;
import com.jwkj.widget.SwitchView;
import com.nuowei.ipclibrary.R;

import java.util.List;

/**
 * Created by dxs on 2016/1/6.
 */
public class MonitorSwitchAdapter extends RecyclerView.Adapter<MonitorSwitchAdapter.ViewHolder> {
    private List<Sensor> sensors;
    private static float w= MyApp.SCREENWIGHT/4;
    private int h;
    public MonitorSwitchAdapter(List<Sensor> sensors,int h) {
        this.sensors = sensors;
        this.h=h;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.items_recy_switch, null);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams((int)w,h);
        view.setLayoutParams(params);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Sensor sensor=sensors.get(position);
        holder.ivImage.setModeStatde(sensor.getLampState());
        holder.txSensorName.setText(sensor.getName());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onItemClick(v,sensor,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public SwitchView ivImage;
        public TextView txSensorName;
        public View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView=itemView;
            ivImage = (SwitchView) itemView.findViewById(R.id.iv_switch);
            txSensorName= (TextView) itemView.findViewById(R.id.tx_switchname);
        }

    }
    private OnSensorSwitchClickListner listner;
    public interface OnSensorSwitchClickListner{
        void onItemClick(View view, Sensor sensor, int position);
    }
    public void setOnSensorSwitchClickListner(OnSensorSwitchClickListner listner){
        this.listner=listner;
    }
}
