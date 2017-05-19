package com.jwkj.adapter;

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
import com.jwkj.interfac.dialogShowItem;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.nuowei.ipclibrary.R;

public class DialogSelectStringAdapter<K extends dialogShowItem> extends RecyclerView.Adapter<DialogSelectStringAdapter.WorkeDayViewHolder>{
	private Context mContext;
    private List<K> items;
    private int select;//单选还是多选
    public DialogSelectStringAdapter(Context context, List<K> names,int select) {
        mContext = context;
        this.items=names;
        this.select=select;
    }
	
	public static class WorkeDayViewHolder extends RecyclerView.ViewHolder {
        public TextView txName;
        public View RootView;

        public WorkeDayViewHolder(View itemView) {
            super(itemView);
            RootView=itemView;
            txName = (TextView) itemView.findViewById(R.id.text_selet_name);
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
		final K item=items.get(position);
        holder.txName.setText(item.getName());
        holder.RootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	ItemClick.onItemClick(item, position);
            }
        });
	}

	@Override
	public WorkeDayViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		// TODO Auto-generated method stub
		View view = View.inflate(parent.getContext(), R.layout.items_dialog_string, null);
        // 创建一个ViewHolder
		WorkeDayViewHolder holder = new WorkeDayViewHolder(view);
        return holder;
	}
	
	private OnItemClick<K> ItemClick;
	public interface OnItemClick<K>{
		void onItemClick(K k, int select);
	}
	public void setOnItemClick(OnItemClick<K> click){
		this.ItemClick=click;
	}
	
}
