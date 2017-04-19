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


import com.nuowei.smarthome.R;

import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 09:00
 * @Description :
 */
public class MainGridAdapter extends RecyclerView.Adapter<MainGridAdapter.MyViewHolder> implements MyItemTouchCallback.ItemTouchAdapter {

    private Context context;
    private int src;
    private List<HashMap<String, MainDatas>> results;

    public MainGridAdapter(int src, List<HashMap<String, MainDatas>> results) {
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

        holder.textView.setText(results.get(position).get("Main").getMainString());
        switch (results.get(position).get("Main").getMainType()) {
            case 0:
                holder.imageView.setImageResource(R.drawable.home_security);
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.home_air);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.home_water);
                break;
            case 3:
                holder.imageView.setImageResource(R.drawable.home_electric);
                break;
            case 4:
                holder.imageView.setImageResource(R.drawable.home_light);
                break;
            case 5:
                holder.imageView.setImageResource(R.drawable.home_warmfloor);
                break;
            case 6:
                holder.imageView.setImageResource(R.drawable.home_service);
                break;
            case 7:
                holder.imageView.setImageResource(R.drawable.home_device);
                break;
            case 8:
                holder.imageView.setImageResource(R.drawable.home_setting);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.home_device);
                break;
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

        public DinProTextView textView;
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = width / 3;
            itemView.setLayoutParams(layoutParams);
            textView = (DinProTextView) itemView.findViewById(R.id.tv_txt);
            imageView = (ImageView) itemView.findViewById(R.id.image_icon);
        }
    }

}
