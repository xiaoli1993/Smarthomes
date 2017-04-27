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
import android.widget.RelativeLayout;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.activity.MainActivity;
import com.nuowei.smarthome.modle.Scene;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.smarthomesdk.utils.XlinkUtils;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.button.StateButton;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.List;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;

/**
 * @Author : 肖力
 * @Time :  2017/3/27 11:53
 * @Description :
 * @Modify record :
 */
public class SceneListAdapter extends RecyclerView.Adapter<SceneListAdapter.MyViewHolder> {

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
        final Scene scene = results.get(position);
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
        holder.btnExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final KProgressHUD hud = KProgressHUD.create(context)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel(context.getResources().getString(R.string.Send))
                        .setCancellable(true);
                hud.show();
                final String enableScene = ZigbeeGW.EnableScene(MyApplication.getMyApplication().getUserInfo().getNickname(), scene.getSceneID(), 2);
                if (MainActivity.getChoiceGwDevice().getDeviceState() == 0) {
                    XlinkAgent.getInstance().connectDevice(MainActivity.getChoiceGwDevice().getxDevice(), MainActivity.getChoiceGwDevice().getAccessKey(), new ConnectDeviceListener() {
                        @Override
                        public void onConnectDevice(XDevice xDevice, int ret) {
                            if (ret < 0) {
                                MyApplication.getLogger().i("连接失败:" + ret);
                                XlinkUtils.shortTips(context.getString(R.string.deviceoffline));
                                hud.dismiss();
                            } else {
                                MainActivity.getChoiceGwDevice().setDeviceState(1);
                                XlinkAgent.getInstance().sendPipeData(xDevice, enableScene.getBytes(), new SendPipeListener() {
                                    @Override
                                    public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                        hud.dismiss();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), enableScene.getBytes(), new SendPipeListener() {
                        @Override
                        public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                            hud.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout openTimeSet;
        private DinProTextView tvSceneName;
        private DinProTextView tvScene;
        private StateButton btnExecute;
        private ImageView imageMore;


        public MyViewHolder(View itemView) {
            super(itemView);
            openTimeSet = (RelativeLayout) itemView.findViewById(R.id.open_time_set);
            tvSceneName = (DinProTextView) itemView.findViewById(R.id.tv_SceneName);
            tvScene = (DinProTextView) itemView.findViewById(R.id.tv_Scene);
            btnExecute = (StateButton) itemView.findViewById(R.id.btn_execute);
            imageMore = (ImageView) itemView.findViewById(R.id.image_more);
        }
    }

}

