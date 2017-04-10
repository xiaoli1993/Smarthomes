package com.nuowei.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.Action;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author : 肖力
 * @Time :  2017/4/8 11:43
 * @Description :
 * @Modify record :
 */

public class ActionAdapter extends BaseAdapter {

    private List<Action> subList;

    private Context context;
    private LayoutInflater layoutInflater;

    public ActionAdapter(Context context, List<Action> subList) {
        this.context = context;
        this.subList = subList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return subList.size();
    }

    @Override
    public Action getItem(int position) {
        return subList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_text, null);
            convertView.setTag(new ActionAdapter.ViewHolder(convertView));
        }
        initializeViews((Action) getItem(position), (ActionAdapter.ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(Action action, ActionAdapter.ViewHolder holder) {
        //TODO implement
//        holder.deviceIcon.setVisibility(View.GONE);
//        holder.imageRight.setVisibility(View.GONE);

        if (action.isChoise()) {
            holder.deviceName.setTextColor(context.getResources().getColor(R.color.color_start));
        }
        holder.deviceName.setText(action.getAction());

    }

    static class ViewHolder {
//        @BindView(R.id.device_icon)
//        ImageView deviceIcon;
        @BindView(R.id.device_name)
        AvenirTextView deviceName;
//        @BindView(R.id.image_right)
//        ImageView imageRight;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}