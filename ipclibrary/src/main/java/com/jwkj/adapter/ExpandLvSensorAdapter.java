package com.jwkj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.jwkj.entity.Sensor;
import com.jwkj.utils.Utils;
import com.jwkj.widget.SwitchView;
import com.nuowei.ipclibrary.R;

/**
 * Created by dxs on 2016/1/18.
 */
public class ExpandLvSensorAdapter extends BaseExpandableListAdapter {
    private int[] gropNames;
    private int[] ItemNames;
    private Sensor sensor;
    private Context mContext;

    public ExpandLvSensorAdapter(Context mContext, int[] gropNames, int[] itemNames, Sensor sensor) {
        this.mContext = mContext;
        this.gropNames = gropNames;
        ItemNames = itemNames;
        this.sensor = sensor;
    }

    @Override
    public int getGroupCount() {
        return gropNames.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ItemNames.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return gropNames[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ItemNames[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewholder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_group_sensor, null);
            viewholder = new GroupViewHolder();
            viewholder.GroupName = (TextView) convertView.findViewById(R.id.tx_sensor_name);
            viewholder.ModeSwitch = (SwitchView) convertView.findViewById(R.id.iv_sensor_mode_switch);
            convertView.setTag(viewholder);
        } else {
            viewholder = (GroupViewHolder) convertView.getTag();
        }
        viewholder.GroupName.setText(Utils.getStringForId(gropNames[groupPosition]));
        if (sensor.getSensorStateAtMode(groupPosition,7)) {
            viewholder.ModeSwitch.setModeStatde(SwitchView.State_on);
        } else {
            viewholder.ModeSwitch.setModeStatde(SwitchView.State_off);
        }
        viewholder.ModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sensor.getSensorStateAtMode(groupPosition, 7)) {
                    sensor.setSensorStateAtMode(groupPosition, 7, false);
                } else {
                    sensor.setSensorStateAtMode(groupPosition, 7, true);
                }
                listner.onGroupClick(groupPosition, sensor);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemViewHolder viewholder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_items_sensor, null);
            viewholder = new ItemViewHolder();
            viewholder.itemsName = (TextView) convertView.findViewById(R.id.tx_sensor_item);
            viewholder.checkBox = (ImageView) convertView.findViewById(R.id.iv_checkbox);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ItemViewHolder) convertView.getTag();
        }
        viewholder.itemsName.setText(Utils.getStringForId(ItemNames[childPosition]));
        if (childPosition == 0) {//提示音
            viewholder.checkBox.setImageResource(R.drawable.right_arrow);
        } else if (childPosition <= 3) {
            if (sensor.getSensorStateAtMode(groupPosition, childPosition - 1)) {
                viewholder.checkBox.setImageResource(R.drawable.checkbox_selected);
            } else {
                viewholder.checkBox.setImageResource(R.drawable.checkbox_up);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childPosition != 0) {//开关类
                    if (sensor.getSensorStateAtMode(groupPosition, childPosition - 1)) {
                        sensor.setSensorStateAtMode(groupPosition, childPosition - 1, false);
                    } else {
                        sensor.setSensorStateAtMode(groupPosition, childPosition - 1, true);
                    }
                }
                listner.onChildClick(groupPosition, childPosition, sensor);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupViewHolder {
        public SwitchView ModeSwitch;
        public TextView GroupName;
    }

    static class ItemViewHolder {
        public TextView itemsName;
        public ImageView checkBox;
    }

    private onExPanChildClickListner listner;

    public void setOnExPanChildClickListner(onExPanChildClickListner listner) {
        this.listner = listner;
    }

    public interface onExPanChildClickListner {
        void onGroupClick(int groupPosition, Sensor sensor);

        void onChildClick(int groupPosition, int childPosition, Sensor sensor);
    }

    public Sensor getSensor() {
        return sensor;
    }
}
