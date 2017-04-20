package com.nuowei.smarthome.adapter;

/**
 * @Author : 肖力
 * @Time :  2017/4/19 10:57
 * @Description :
 * @Modify record :
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.Scene;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.DinProTextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SceneAdapter extends BaseAdapter {

    private ArrayList<Scene> scene;

    private Context context;
    private LayoutInflater layoutInflater;

    public SceneAdapter(Context context, ArrayList<Scene> scene) {
        this.context = context;
        this.scene = scene;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return scene.size();
    }

    @Override
    public Scene getItem(int position) {
        return scene.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_scene, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((Scene) getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(Scene scene, ViewHolder holder) {
        //TODO implement
        MyApplication.getLogger().i("场景：" + scene.getName());
        holder.tvSceneName.setText(scene.getName());
        if (scene.getTimeEnable() == 0 && scene.getCountTime() == 0) {
            holder.tvScene.setText(context.getResources().getString(R.string.time_onoff));
        } else if (scene.getTime().getMonth() == 255 && scene.getTime().getDay() == 255 && scene.getTime().getHour() == 255 && scene.getTime().getMin() == 255 && scene.getTime().getType() == 255) {
            holder.tvScene.setText(context.getResources().getString(R.string.no_set_time));
        } else {
            if (scene.getCountTime() == 0) {
                holder.tvScene.setText(context.getResources().getString(R.string.action_time) + scene.getTime().getHour() + ":" + scene.getTime().getMin() + "    " + context.getResources().getString(R.string.open_period) + ":    " + MyUtil.getWkString(context, scene.getTime().getWkflag()));
            } else {
                holder.tvScene.setText(context.getResources().getString(R.string.action_time) + scene.getTime().getHour() + ":" + scene.getTime().getMin() + "    " + context.getResources().getString(R.string.open_period) + ":    " + MyUtil.getWkString(context, scene.getTime().getWkflag()));
            }
        }
    }

    static class ViewHolder {
        @BindView(R.id.open_time_set)
        RelativeLayout openTimeSet;
        @BindView(R.id.tv_SceneName)
        DinProTextView tvSceneName;
        @BindView(R.id.tv_Scene)
        DinProTextView tvScene;
        @BindView(R.id.btn_execute)
        Button btnExecute;
        @BindView(R.id.image_more)
        ImageView imageMore;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
