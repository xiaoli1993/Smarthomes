package com.jwkj.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jwkj.adapter.MonitorSwitchAdapter;
import com.jwkj.entity.Sensor;
import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

import java.util.List;

/**
 * Created by dxs on 2015/9/8.
 * 展示预置位的popwindow
 */
public class SwitchPopwindow extends BasePopWindow{
    private final static String TAG = "SwitchPopwindow";
    private View conentView;
    private RecyclerView prePointGrild;
    private Context context;
    private MonitorSwitchAdapter gridAdapter;
    private LayoutInflater inflater;
    private List<Sensor> sensors;
    private TextView txEmpty;
    private ProgressBar progress;
    private int h;

    public SwitchPopwindow(final Context context, int h,List<Sensor> sensors) {
        super(context,h);
        this.h=h;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        conentView = inflater.inflate(R.layout.popwin_switch, null);
        this.sensors=sensors;
        this.setAnimationStyle(R.style.AnimationFade);
        setContentView(conentView);
        initUI(conentView);
    }


    private void initUI(View conentView) {
        prePointGrild = (RecyclerView) conentView.findViewById(R.id.recycle_monitor_switch);
        txEmpty= (TextView) conentView.findViewById(R.id.tx_emptyview);
        progress= (ProgressBar) conentView.findViewById(R.id.prg_monitor);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        prePointGrild.setLayoutManager(manager);
        gridAdapter = new MonitorSwitchAdapter(sensors, h-Utils.dip2px(context,32));
        gridAdapter.setOnSensorSwitchClickListner(listner);
        prePointGrild.setAdapter(gridAdapter);
    }

    /**
     * 更新某个开关的状态
     * @param position
     */
    public void updataswitch(int position){
        gridAdapter.notifyItemChanged(position);
    }

    /**
     * 获取开关的结果
     * state 0在加载  1成功
     */
    public void getSensored(int state){
        if(state==1){
            if(sensors.size()>0){
                prePointGrild.setVisibility(View.VISIBLE);
                txEmpty.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                gridAdapter.notifyDataSetChanged();
            }else{
                prePointGrild.setVisibility(View.INVISIBLE);
                txEmpty.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }else{
            prePointGrild.setVisibility(View.INVISIBLE);
            txEmpty.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        }
    }

    private MonitorSwitchAdapter.OnSensorSwitchClickListner listner=new MonitorSwitchAdapter.OnSensorSwitchClickListner() {
        @Override
        public void onItemClick(View view, Sensor sensor, int position) {
            popwindowListner.onSwitchClick(position,sensor);
        }
    };

    private OnSwitchListner popwindowListner;
    public void setOnSwitchListner(OnSwitchListner popwindowListner){
        this.popwindowListner=popwindowListner;
    }
    public interface OnSwitchListner{
        void onSwitchClick(int position,Sensor sensor);
    }
}
