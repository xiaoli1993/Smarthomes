package com.nuowei.smarthome.adapter;
/**
 * Copyright ©深圳市海曼科技有限公司
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.util.Time;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.Collections;
import java.util.List;

/**
 * @Author : 肖力
 * @Time :  2017/4/27 11:53
 * @Description :
 * @Modify record :
 */
public class THPAdapter extends RecyclerView.Adapter<THPAdapter.MyViewHolder> implements MyItemTouchCallback.ItemTouchAdapter {

    private Context context;
    private int src;
    private List<DataDevice> results;

    public THPAdapter(int src, List<DataDevice> results) {
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

        holder.textView.setText(context.getString(R.string.Temp) + results.get(position).getTemp() + context.getString(R.string.tempp) + "\t" + context.getString(R.string.Hum) + results.get(position).getHumidity() + context.getString(R.string.hump));
        holder.textState.setText(Time.dateToString(results.get(position).getDate(), "yyyy-MM-dd HH:mm:ss"));
        holder.imageView.setImageResource(R.drawable.device_thp);
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
            textView = (DinProTextView) itemView.findViewById(R.id.device_name);
            textState = (DinProTextView) itemView.findViewById(R.id.tv_state);
            imageView = (ImageView) itemView.findViewById(R.id.device_icon);
        }
    }

}

