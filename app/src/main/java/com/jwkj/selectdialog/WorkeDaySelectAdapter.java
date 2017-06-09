package com.jwkj.selectdialog;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.entity.DefenceWorkGroup;
import com.jwkj.entity.Sensor;
import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.R;

public class WorkeDaySelectAdapter extends RecyclerView.Adapter<WorkeDaySelectAdapter.WorkeDayViewHolder>{
	private Context mContext;
    private List<SelectItem> items;
    private int type;//单选还是多选
    private DefenceWorkGroup grop;
    public WorkeDaySelectAdapter(Context context, DefenceWorkGroup grop,int type) {
        mContext = context;
        this.grop=grop;
        this.type=type;
        items=getSelectItems();
    }
	
	public static class WorkeDayViewHolder extends RecyclerView.ViewHolder {
        public TextView txName;
        public ImageView ivSelect;
        public View RootView;

        public WorkeDayViewHolder(View itemView) {
            super(itemView);
            RootView=itemView;
            txName = (TextView) itemView.findViewById(R.id.text_selet_name);
            ivSelect = (ImageView) itemView.findViewById(R.id.iv_select);
        }
    }

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public void onBindViewHolder(WorkeDayViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		final SelectItem item=items.get(position);
        holder.txName.setText(item.getName());
        if(type==NormalDialog.SelectType_List){
            if(item.isSelected()){
                holder.ivSelect.setImageResource(R.drawable.checkbox_selected);
            }else{
                holder.ivSelect.setImageResource(R.drawable.checkbox_up);
            }
        }else{
            if(item.isSelected()){
                holder.ivSelect.setImageResource(R.drawable.check1_video_mode);
            }else{
                holder.ivSelect.setImageResource(R.drawable.check2_video_mode);
            }
        }

        holder.RootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==NormalDialog.SelectType_List){
                    changeUI(item, position);
                }else{
                    changeSingleUI(position);
                    grop.setbWeekDay(changeMusic(grop.getbWeekDay(), (byte) position));
                }
                listener.onItemClick(v,item,position);
            }
        });
	}

	@Override
	public WorkeDayViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// TODO Auto-generated method stub
		View view = View.inflate(parent.getContext(), R.layout.items_dialog_select, null);
        // 创建一个ViewHolder
		WorkeDayViewHolder holder = new WorkeDayViewHolder(view);
        return holder;
	}
	
	private void changeUI(SelectItem item,int position){
        if(item.isSelected()){
            item.setIsSelected(false);
        }else{
            item.setIsSelected(true);
        }
        notifyItemChanged(position);
    }
	
	private void changeSingleUI(int position){
        for (int i = 0; i < items.size(); i++) {
            if(i==position){
                items.get(i).setIsSelected(true);
            }else{
                items.get(i).setIsSelected(false);
            }
        }
        notifyDataSetChanged();
    }
	
	private List<SelectItem> getSelectItems(){
        List<SelectItem> items=new ArrayList<SelectItem>();
        if(type== NormalDialog.SelectType_List){
            int[] test= grop.getDayInWeek();
            for (int i = 0; i < WorkScheduleGroup.modes.length; i++) {
                SelectItem item=new SelectItem(Utils.getStringForId(WorkScheduleGroup.modes[i]),test[i]==1);
                items.add(item);
            }
        }else if(type== NormalDialog.SelectType_Sigle){
            byte position= (byte) ((grop.getbWeekDay()>>4)&0x7);
            for (int i = 0; i < Sensor.modes.length; i++) {
                if(i==position){
                    SelectItem item=new SelectItem(Utils.getStringForId(Sensor.modes[i]),true);
                    items.add(item);
                }else{
                    SelectItem item=new SelectItem(Utils.getStringForId(Sensor.modes[i]),false);
                    items.add(item);
                }
            }
        }

        return items;
    }
	
	private byte changeMusic(byte src,byte position){
        int[] temp=Utils.getByteBinnery(position,true);
        for (int i = 0; i <3 ; i++) {
            if(temp[i]==1){
                src=Utils.ChangeBitTrue(src,i+4);
            }else{
                src=Utils.ChangeByteFalse(src,i+4);
            }
        }
        return src;
    }
	private WorkeDaySetting listener;
	public void setWorkeDaySetting(WorkeDaySetting listener){
		this.listener=listener;
	}
	public interface WorkeDaySetting {
        void onItemClick(View v, SelectItem grop, int position);
    }
}
