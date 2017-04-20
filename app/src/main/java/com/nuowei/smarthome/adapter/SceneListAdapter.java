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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.modle.Scene;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Author : 肖力
 * @Time :  2017/3/27 11:53
 * @Description :
 * @Modify record :
 */
public class SceneListAdapter extends RecyclerView.Adapter<SceneListAdapter.MyViewHolder>  {

    private Context context;
    private int src;
    private List<Scene> results;

    public SceneListAdapter(int src, List<Scene> results) {
        this.results = results;
        this.src = src;
    }

    @Override
    public SceneListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(src, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SceneListAdapter.MyViewHolder holder, int position) {
        Scene scene = results.get(position);
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

    @Override
    public int getItemCount() {
        return results.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout openTimeSet;
        private DinProTextView tvSceneName;
        private DinProTextView tvScene;
        private Button btnExecute;
        private ImageView imageMore;


        public MyViewHolder(View itemView) {
            super(itemView);
            openTimeSet = (RelativeLayout) itemView.findViewById(R.id.open_time_set);
            tvSceneName = (DinProTextView) itemView.findViewById(R.id.tv_SceneName);
            tvScene = (DinProTextView) itemView.findViewById(R.id.tv_Scene);
            btnExecute = (Button) itemView.findViewById(R.id.btn_execute);
            imageMore = (ImageView) itemView.findViewById(R.id.image_more);
        }
    }

}

