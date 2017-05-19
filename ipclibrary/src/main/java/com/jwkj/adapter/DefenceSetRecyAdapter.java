package com.jwkj.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.entity.DefenceWorkGroup;
import com.jwkj.widget.ExDefenceLinerLayout;
import com.jwkj.widget.SwitchView;
import com.jwkj.widget.scedueView;
import com.nuowei.ipclibrary.R;

public class DefenceSetRecyAdapter extends
		RecyclerView.Adapter<DefenceSetRecyAdapter.DefenceHolder> {
	private Context mContext;
	private List<DefenceWorkGroup> Groups;

	public DefenceSetRecyAdapter(Context context, List<DefenceWorkGroup> list) {
		super();
		mContext = context;
		Groups = list;
	}

	@Override
	public DefenceHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		View view = View.inflate(mContext, R.layout.items_defence, null);
		DefenceHolder holder = new DefenceHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(final DefenceHolder holder, final int position) {
		// TODO Auto-generated method stub
		final DefenceWorkGroup grop = Groups.get(position);
		holder.txBeginTime.setText(grop.getBeginTimeString());
		holder.txEndTime.setText(grop.getEndTimeString());
		holder.sView.setWorkGroup(grop);
		switch (grop.getBswitch()) {
		case 0:
			holder.swBtn.setModeStatde(2);
			break;
		case 1:
			holder.swBtn.setModeStatde(1);
			break;
		case -1:
			holder.swBtn.setModeStatde(0);
			break;

		default:
			break;
		}
		holder.ll_item.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				listner.onDelete(v, grop, position);
				return true;
			}
		});
		holder.timeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listner.onBeginAndEndTimeClick(v, grop, position);
			}
		});
		holder.swBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listner.onSwitchClick(v, grop, position);
			}
		});
		if (eXlistner != null) {
			holder.exRelative.setOnExDefenceLinearLayoutListner(eXlistner);
		}
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return Groups.size();
	}

	public static class DefenceHolder extends RecyclerView.ViewHolder {
		public View RootView;
		public LinearLayout ll_item;
		public RelativeLayout timeLayout;
		public TextView txBeginTime;
		public TextView txEndTime;
		public SwitchView swBtn;
		public scedueView sView;
		public ExDefenceLinerLayout exRelative;

		public DefenceHolder(View itemView) {
			super(itemView);
			RootView = itemView;
			ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
			timeLayout = (RelativeLayout) itemView
					.findViewById(R.id.rl_timelayout);
			txBeginTime = (TextView) itemView.findViewById(R.id.tx_begintime);
			txEndTime = (TextView) itemView.findViewById(R.id.tx_endtime);
			swBtn = (SwitchView) itemView.findViewById(R.id.df_sw_btn);
			exRelative = (ExDefenceLinerLayout) itemView
					.findViewById(R.id.df_ell_mode);
			sView = (scedueView) itemView.findViewById(R.id.df_sv_mode);
		}

	}

	/**
	 * 更新全部数据
	 */
	public void UpdataAll() {
		Collections.sort(Groups);
		notifyDataSetChanged();
	}

	public void addGroup(DefenceWorkGroup group) {
		Groups.add(group);
		Collections.sort(Groups);
		notifyDataSetChanged();// 全部更新
		// notifyItemInserted(Groups.indexOf(group));
	}

	/**
	 * 更新
	 * 
	 * @param group
	 */
	public void UpdataGroup(DefenceWorkGroup group) {
		int oldPosition = -1;
		for (int i = 0; i < Groups.size(); i++) {
			if (Groups.get(i).getGroupIndex() == group.getGroupIndex()) {
				oldPosition = i;
				Groups.set(i, group);
				notifyItemChanged(i);
			}
		}
		// 更新排序如有必要
		Collections.sort(Groups);
		int mewPosition = Groups.indexOf(group);
		if ((oldPosition != -1) && (oldPosition != mewPosition)) {
			notifyItemMoved(oldPosition, mewPosition);
			if (oldPosition == 0) {
				notifyItemChanged(0);// 类似代码为了防止头部空余空间消失
			}
		}
	}

	/**
	 * 删除
	 * 
	 * @param GroupIndex
	 */
	public void DeleteGroup(byte GroupIndex) {
		for (int i = 0; i < Groups.size(); i++) {
			if (Groups.get(i).getGroupIndex() == GroupIndex) {
				Groups.remove(i);
				notifyItemRemoved(i);
				if (i == 0 && Groups.size() > 0) {
					notifyItemChanged(0);
				}
			}
		}
	}

	private onDefenceSetting listner;
	private ExDefenceLinerLayout.ExDefenceLinearLayoutListner eXlistner;

	public void setOnDefenceSetting(onDefenceSetting listner) {
		this.listner = listner;
	}

	public void setOnExDefenceLinearLayoutListner(
			ExDefenceLinerLayout.ExDefenceLinearLayoutListner eXlistner) {
		this.eXlistner = eXlistner;
	}

	public interface onDefenceSetting {
		void onSwitchClick(View v, DefenceWorkGroup grop, int position);

		void onBeginAndEndTimeClick(View v, DefenceWorkGroup grop, int position);

		void onDelete(View v, DefenceWorkGroup grop, int position);
	}
	
	/**
	 * 更新指定的UI
	 * 
	 * @param position
	 */
	public void updateByPosition(int position) {
		notifyItemChanged(position);
	}

}
