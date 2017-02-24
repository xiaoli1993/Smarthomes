package com.nuowei.smarthome.adapter;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.MainDatas;

import java.util.HashMap;
import java.util.List;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 09:00
 * @Description :
 */
public class MainTAdapter extends BaseAdapter {
    private Context mContext;
    private List<HashMap<String, MainDatas>> mainDatases;
    private LayoutInflater inflater;

    public MainTAdapter(Activity Activity, List<HashMap<String, MainDatas>> mainDatases) {

        this.mContext = Activity;
        inflater = LayoutInflater.from(mContext);
        this.mainDatases = mainDatases;
    }

    @Override
    public int getCount() {
        return mainDatases.size();
    }

    @Override
    public Object getItem(int i) {
        return mainDatases.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index = position;
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_main, parent, false);
            holder = new ViewHolder();
            holder.tv = ((TextView) convertView.findViewById(R.id.tv_txt));
            holder.image = ((ImageView) convertView.findViewById(R.id.image_icon));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText("" + mainDatases.get(position).get("Main").getMainString());
        switch (mainDatases.get(position).get("Main").getMainType()) {
            case 0:
                holder.image.setImageResource(R.drawable.home_security);
                break;
            case 1:
                holder.image.setImageResource(R.drawable.home_air);
                break;
            case 2:
                holder.image.setImageResource(R.drawable.home_water);
                break;
            case 3:
                holder.image.setImageResource(R.drawable.home_electric);
                break;
            case 4:
                holder.image.setImageResource(R.drawable.home_light);
                break;
            case 5:
                holder.image.setImageResource(R.drawable.home_warmfloor);
                break;
            case 6:
                holder.image.setImageResource(R.drawable.home_service);
                break;
            case 7:
                holder.image.setImageResource(R.drawable.home_device);
                break;
            case 8:
                holder.image.setImageResource(R.drawable.home_setting);
                break;
            default:
                holder.image.setImageResource(R.drawable.home_device);
                break;
        }

        return convertView;
    }

    class ViewHolder {
        TextView tv;
        ImageView image;

        public TextView getTv() {
            return tv;
        }

        public void setTv(TextView tv) {
            this.tv = tv;
        }

        public ImageView getImage() {
            return image;
        }

        public void setImage(ImageView image) {
            this.image = image;
        }
    }
}
