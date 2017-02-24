package com.nuowei.smarthome.adapter;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.view.draggridview.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 08:31
 * @Description :
 */
public class MainAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MainDatas> mainDatases;
    private LayoutInflater inflater;

    public MainAdapter(Activity Activity, ArrayList<MainDatas> mainDatases) {

        this.mContext = Activity;
        inflater = LayoutInflater.from(mContext);
        this.mainDatases = mainDatases;
    }

    @Override
    public int getCount() {
        return mainDatases.size();
    }

    @Override
    public Object getItem(int position) {
        return mainDatases.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindView(int position, ViewHolder viewHolder) {
        ((MyVH) viewHolder).tv.setText("" + mainDatases.get(position).getMainString());
        switch (mainDatases.get(position).getMainType()) {
            case 0:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_security);
                break;
            case 1:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_air);
                break;
            case 2:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_water);
                break;
            case 3:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_electric);
                break;
            case 4:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_light);
                break;
            case 5:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_warmfloor);
                break;
            case 6:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_service);
                break;
            case 7:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_device);
                break;
            case 8:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_setting);
                break;
            default:
                ((MyVH) viewHolder).image.setImageResource(R.drawable.home_device);
                break;
        }
    }

    @Override
    public ViewHolder onCreateView(int index, ViewGroup parent) {
        MainDatas mainDatas = mainDatases.get(index);
        MyVH vh = new MyVH(inflater.inflate(R.layout.item_main, null), mainDatas);
        return vh;
    }

    @Override
    public void onExchange(int from, int to, boolean isEnd) {
        MyApplication.getLogger().i(from + "\n" + to + isEnd);
        if (!isEnd) {
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(mainDatases, i, i + 1);
                }
            } else if (from > to) {
                for (int i = from; i > to; i--) {
                    Collections.swap(mainDatases, i, i - 1);
                }
            }
        }
        for (int i = 0; i < mainDatases.size(); i++) {
            MyApplication.getLogger().i(String.valueOf(mainDatases.get(i).getMainString()));
        }
    }

    class MyVH extends BaseAdapter.ViewHolder {

        TextView tv;
        ImageView image;

        public MyVH(View view, final MainDatas mainDatas) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.getLogger().i(tv.getText().toString() + mainDatas.getMainString() + mainDatas.getMainType() + "\n" + mainDatas.getMainsort());

                }
            });
            tv = (TextView) view.findViewById(R.id.tv_txt);
            image = (ImageView) view.findViewById(R.id.image_icon);

        }
    }
}

