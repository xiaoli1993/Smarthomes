package com.jwkj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.global.Constants;
import com.jwkj.widget.ExpandeLinearLayout;
import com.jwkj.widget.scedueView;
import com.nuowei.smarthome.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by dxs on 2016/1/13.
 */
public class ModeSetRecyAdapter extends RecyclerView.Adapter<ModeSetRecyAdapter.ViewHolder> {
    private Context mContext;
    private List<WorkScheduleGroup> Groups;
    public ModeSetRecyAdapter(Context context,List<WorkScheduleGroup> list) {
        mContext = context;
        Groups=list;
        Collections.sort(Groups);
    }



    @Override
    public ModeSetRecyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.items_modeset, null);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ModeSetRecyAdapter.ViewHolder holder, final int position) {
        final WorkScheduleGroup grop=Groups.get(position);
        //模拟的头部距离
        if(position==0){
            holder.vLineHeader.setVisibility(View.VISIBLE);
        }else{
            holder.vLineHeader.setVisibility(View.GONE);
        }
        holder.txTime.setText(grop.getTime());
        holder.txPlan.setText(grop.getModeText());
        holder.progress.setVisibility(View.GONE);
        holder.Switch.setVisibility(View.VISIBLE);
        holder.sView.setWorkGroup(grop);
        if(grop.getbWorkMode()== Constants.FishMode.MODE_HOME){
            holder.ivMode.setImageResource(R.drawable.mode_home_small);
        }else if(grop.getbWorkMode()== Constants.FishMode.MODE_OUT){
            holder.ivMode.setImageResource(R.drawable.mode_out_small);
        }else{
            holder.ivMode.setImageResource(R.drawable.mode_sleep_small);
        }
        if(grop.isEnable()){
            holder.Switch.setImageResource(R.drawable.on);
        }else{
            holder.Switch.setImageResource(R.drawable.off);
        }
//        holder.RootView.setOnComfirmClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listner.onItemClick(v,grop,position);
//            }
//        });
        holder.txTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onTimeAndModeClick(v,grop,position);
            }
        });
        holder.txPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onTimeAndModeClick(v,grop,position);
            }
        });
        holder.Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.Switch.setVisibility(View.GONE);
                holder.progress.setVisibility(View.VISIBLE);
                listner.onSwitchClick(v, grop, position);
            }
        });
        if(eXlistner!=null){
            holder.exRelative.setOnExpandeLinearLayoutListner(eXlistner);
        }
    }

    @Override
    public int getItemCount() {
        return Groups.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txTime;
        public TextView txPlan;
        public ImageView Switch;
        public ImageView ivMode;
        public View RootView;
        public RelativeLayout vLineHeader;
        public ProgressBar progress;
        public scedueView sView;
        public ExpandeLinearLayout exRelative;

        public ViewHolder(View itemView) {
            super(itemView);
            RootView=itemView;
            txTime = (TextView) itemView.findViewById(R.id.tx_time);
            txPlan = (TextView) itemView.findViewById(R.id.tx_plan);
            Switch = (ImageView) itemView.findViewById(R.id.iv_timeswitch);
            vLineHeader= (RelativeLayout) itemView.findViewById(R.id.rl_headerlne);
            progress= (ProgressBar) itemView.findViewById(R.id.progressBar_gropswitch);
            sView= (scedueView) itemView.findViewById(R.id.sv_mode);
            ivMode= (ImageView) itemView.findViewById(R.id.iv_mode);
            exRelative= (ExpandeLinearLayout) itemView.findViewById(R.id.ell_mode);
        }
    }

    /**
     * 更新全部数据
     */
    public void UpdataAll(){
        Collections.sort(Groups);
        notifyDataSetChanged();
    }

    public void addGroup(WorkScheduleGroup group){
        Groups.add(group);
        Collections.sort(Groups);
        notifyDataSetChanged();//全部更新
        //notifyItemInserted(Groups.indexOf(group));
    }

    /**
     * 更新
     * @param group
     */
    public void UpdataGroup(WorkScheduleGroup group){
        int oldPosition=-1;
        for (int i = 0; i < Groups.size(); i++) {
            if(Groups.get(i).getGroupIndex()==group.getGroupIndex()){
                oldPosition=i;
                Groups.set(i, group);
                notifyItemChanged(i);
            }
        }
        //更新排序如有必要
        Collections.sort(Groups);
        int mewPosition=Groups.indexOf(group);
        if((oldPosition!=-1)&&(oldPosition!=mewPosition)){
            notifyItemMoved(oldPosition,mewPosition);
            if(oldPosition==0){
                notifyItemChanged(0);//类似代码为了防止头部空余空间消失
            }
        }
    }

    /**
     * 删除
     * @param GroupIndex
     */
    public void DeleteGroup(byte GroupIndex){
        for (int i = 0; i < Groups.size(); i++) {
            if(Groups.get(i).getGroupIndex()==GroupIndex){
                Groups.remove(i);
                notifyItemRemoved(i);
                if(i==0&&Groups.size()>0){
                    notifyItemChanged(0);
                }
            }
        }
    }

    private onModeSetting listner;
    private ExpandeLinearLayout.ExpandeLinearLayoutListner eXlistner;
    public void setOnModeSetting(onModeSetting listner) {
        this.listner = listner;
    }
    public void setOnExpandeLinearLayoutListner(ExpandeLinearLayout.ExpandeLinearLayoutListner eXlistner) {
        this.eXlistner = eXlistner;
    }
    public interface onModeSetting {
        void onSwitchClick(View v,WorkScheduleGroup grop,int position);
        void onItemClick(View v,WorkScheduleGroup grop,int position);
        void onTimeAndModeClick(View v,WorkScheduleGroup grop,int position);
    }
}
