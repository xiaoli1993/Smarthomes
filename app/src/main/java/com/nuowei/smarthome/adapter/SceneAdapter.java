package com.nuowei.smarthome.adapter;

/**
 * @Author : 肖力
 * @Time :  2017/4/19 10:57
 * @Description :
 * @Modify record :
 */

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.Scene;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.DinProTextView;


public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.MyViewHolder> {

    private ArrayList<Scene> scene;
    private Context context;

    public SceneAdapter(ArrayList<Scene> scene) {
        this.scene = scene;
    }


    private void initializeViews(Scene scene, MyViewHolder holder) {
        //TODO implement
        Scene.PLBean.SceneBean scenes = scene.getPL().getScene().get(0);
        holder.tvSceneName.setText(scenes.getName());
        if (scenes.getTimeEnable() == 0 && scenes.getCountTime() == 0) {
            holder.tvScene.setText(context.getResources().getString(R.string.time_onoff));
        } else if (scenes.getTime().getMonth() == 255 && scenes.getTime().getDay() == 255 && scenes.getTime().getHour() == 255 && scenes.getTime().getMin() == 255 && scenes.getTime().getType() == 255) {
            holder.tvScene.setText(context.getResources().getString(R.string.no_set_time));
        } else {
            if (scenes.getCountTime() == 0) {
                holder.tvScene.setText(context.getResources().getString(R.string.action_time) + scenes.getTime().getHour() + ":" + scenes.getTime().getMin() + "    " + context.getResources().getString(R.string.open_period) + ":    " + MyUtil.getWkString(context, scenes.getTime().getWkflag()));
            } else {
                holder.tvScene.setText(context.getResources().getString(R.string.action_time) + scenes.getTime().getHour() + ":" + scenes.getTime().getMin() + "    " + context.getResources().getString(R.string.open_period) + ":    " + MyUtil.getWkString(context, scenes.getTime().getWkflag()));
            }
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scene, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Scene scenes = scene.get(position);
        initializeViews(scenes, holder);
//        initEvenViews(scenes, holder);
    }

    @Override
    public int getItemCount() {
        return scene.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public DinProTextView tvSceneName;
        public DinProTextView tvScene;
        public Button btnExecute;
        public ImageView imageMore;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvSceneName = (DinProTextView) itemView.findViewById(R.id.tv_SceneName);
            tvScene = (DinProTextView) itemView.findViewById(R.id.tv_Scene);
            imageMore = (ImageView) itemView.findViewById(R.id.image_more);
            btnExecute = (Button) itemView.findViewById(R.id.btn_execute);
        }
    }
}
