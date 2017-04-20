package com.nuowei.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.Personal;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author : 肖力
 * @Time :  2017/4/20 15:57
 * @Description :
 * @Modify record :
 */

public class PersonalAdapter extends BaseAdapter {

    private List<Personal> personalList;

    private Context context;
    private LayoutInflater layoutInflater;

    public PersonalAdapter(Context context, List<Personal> personalList) {
        this.context = context;
        this.personalList = personalList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return personalList.size();
    }

    @Override
    public Personal getItem(int position) {
        return personalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_personal, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((Personal) getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(Personal personal, ViewHolder holder) {
        //TODO implement
        if (personal.getName().equals(context.getResources().getString(R.string.Phone)) || personal.getName().equals(context.getResources().getString(R.string.Email))) {
            holder.imageRight.setVisibility(View.GONE);
        }
        holder.deviceIcon.setColorFilter(context.getResources().getColor(R.color.text_title));
        holder.deviceIcon.setImageResource(personal.getImage());
        holder.tvState.setText(personal.getState());
        holder.deviceName.setText(personal.getName());
    }

    static class ViewHolder {
        @BindView(R.id.rl_device)
        LinearLayout rlDevice;
        @BindView(R.id.device_icon)
        ImageView deviceIcon;
        @BindView(R.id.device_name)
        DinProTextView deviceName;
        @BindView(R.id.tv_state)
        DinProTextView tvState;
        @BindView(R.id.image_right)
        ImageView imageRight;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}