package com.jwkj.adapter;

import java.util.List;

import com.jwkj.data.DataManager;
import com.jwkj.data.DefenceArea;
import com.jwkj.data.Prepoint;
import com.jwkj.widget.SwitchView;
import com.nuowei.ipclibrary.R;

import android.R.integer;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChannelAreaSetRecyAdapter extends
		RecyclerView.Adapter<ChannelAreaSetRecyAdapter.ChannelAreaHolder> {
	private Context mContext;
	private List<DefenceArea> channel;
	private Prepoint prepoint;
	private boolean isShowPosition=false;

	public ChannelAreaSetRecyAdapter(Context mContext, List<DefenceArea> channel) {
		super();
		this.mContext = mContext;
		this.channel = channel;
	}

	public ChannelAreaSetRecyAdapter(Context mContext,
			List<DefenceArea> channel, Prepoint prepoint) {
		super();
		this.mContext = mContext;
		this.channel = channel;
		this.prepoint = prepoint;
	}

	public class ChannelAreaHolder extends RecyclerView.ViewHolder {
		private View rootView;
		private LinearLayout layout;
		private TextView txName;
		private ImageView icPosition;
		private SwitchView svBtn;
		private TextView txPositionName;

		public ChannelAreaHolder(View itemView) {
			super(itemView);
			rootView = itemView;
			layout = (LinearLayout) itemView.findViewById(R.id.df_ae_llayout);
			txName = (TextView) itemView.findViewById(R.id.df_ae_tx_name);
			icPosition = (ImageView) itemView
					.findViewById(R.id.df_ae_ic_position);
			svBtn = (SwitchView) itemView.findViewById(R.id.df_ae_sv_btn);
			txPositionName=(TextView)itemView.findViewById(R.id.df_ae_tx_positionName);
		}

	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return channel.size();
	}

	@Override
	public ChannelAreaHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		View view = View.inflate(mContext, R.layout.items_defence_area, null);
		ChannelAreaHolder holder = new ChannelAreaHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(final ChannelAreaHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.svBtn.setVisibility(View.VISIBLE);
		holder.txName.setText(channel.get(position).getName());
		switch (channel.get(position).getState()) {
		case -1:
			holder.svBtn.setModeStatde(0);
			break;
		case 0:
			holder.svBtn.setModeStatde(2);
			break;
		case 1:
			holder.svBtn.setModeStatde(1);
			break;

		default:
			break;
		}
		holder.svBtn.getModeStatde();
		if (channel.get(position).getGroup()==1||channel.get(position).getGroup()==8) {
			if (holder.svBtn.getModeStatde()!=0) {
				if (isShowPosition) {
					holder.icPosition.setVisibility(View.VISIBLE);
					holder.txPositionName.setVisibility(View.VISIBLE);
					if (channel.get(position).getLocation()==-1) {
						holder.txPositionName.setText("");
					}else {
						holder.txPositionName.setText(prepoint.getName(channel.get(position).getLocation()));
					}
				}
			}else {
				holder.icPosition.setVisibility(View.GONE);
				holder.txPositionName.setVisibility(View.GONE);
			}
		}
		holder.layout.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				channelAreaListener.deleteChannelArea(channel.get(position),
						position);
				return true;
			}
		});
		holder.layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				channelAreaListener.changeChannelAreaName(position,channel.get(position).getType());
			}
		});
		holder.icPosition.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				channelAreaListener.selectLocation(channel.get(position),position);
			}
		});
		holder.svBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				holder.svBtn.setModeStatde(0);
				channelAreaListener.channelAreaSwitch(channel.get(position), position);
			}
		});
	}

	/**
	 * 更新全部
	 */
	public void updateAll() {
		notifyDataSetChanged();
	}

	/**
	 * 更新指定的UI
	 * 
	 * @param position
	 */
	public void updateByPosition(int position) {
		notifyItemChanged(position);
	}

	private ChannelAreaListener channelAreaListener;

	public void setChannelAreaListener(ChannelAreaListener channelAreaListener) {
		this.channelAreaListener = channelAreaListener;
	}

	public interface ChannelAreaListener {
		void deleteChannelArea(DefenceArea defenceArea, int position);
		void channelAreaSwitch(DefenceArea defenceArea, int position);
		void selectLocation(DefenceArea defenceArea, int position);
		void changeChannelAreaName(int position, int type);
	}
	
	/**
	 * 是否显示预置位
	 */
	public void setIsShowPosition(boolean isShowPosition){
		this.isShowPosition=isShowPosition;
	}

}
