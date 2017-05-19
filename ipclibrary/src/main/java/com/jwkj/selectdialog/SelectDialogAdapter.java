package com.jwkj.selectdialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import com.jwkj.entity.Sensor;
import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.nuowei.ipclibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dxs on 2016/1/13.
 */
public class SelectDialogAdapter extends RecyclerView.Adapter<SelectDialogAdapter.ViewHolder> {
    private Context mContext;
    private List<SelectItem> items;
    private int type;//单选还是多选
    private WorkScheduleGroup grop;
    public SelectDialogAdapter(Context context, WorkScheduleGroup grop,int type) {
        mContext = context;
        this.grop=grop;
        this.type=type;
        items=getSelectItems();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.items_dialog_select, null);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
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
                if (item==null) {
					Log.e("wxy", "item is null");
				}
                listner.onItemClick(v,item,position);
            }
        });
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

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txName;
        public ImageView ivSelect;
        public View RootView;

        public ViewHolder(View itemView) {
            super(itemView);
            RootView=itemView;
            txName = (TextView) itemView.findViewById(R.id.text_selet_name);
            ivSelect = (ImageView) itemView.findViewById(R.id.iv_select);
        }
    }

    private onModeSetting listner;

    public void setOnModeSetting(onModeSetting listner) {
        this.listner = listner;
    }

    public interface onModeSetting {
        void onItemClick(View v, SelectItem grop, int position);
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
}
