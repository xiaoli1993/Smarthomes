package com.jwkj.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwkj.data.DefenceArea;
import com.jwkj.widget.SwitchView;
import com.nuowei.ipclibrary.R;

public class RemoteAreaSetRecyAdapter extends
		RecyclerView.Adapter<RemoteAreaSetRecyAdapter.RemoteAreaHolder> {
	private Context mContext;
	private List<DefenceArea> remote;

	public RemoteAreaSetRecyAdapter(Context mContext, List<DefenceArea> remote) {
		super();
		this.mContext = mContext;
		this.remote = remote;
	}

	public class RemoteAreaHolder extends RecyclerView.ViewHolder {
		private View rootView;
		private LinearLayout layout;
		private TextView txName;
		private ImageView icPosition;
		private SwitchView svBtn;
		private TextView txPositionName;

		public RemoteAreaHolder(View itemView) {
			super(itemView);
			rootView = itemView;
			layout = (LinearLayout) itemView.findViewById(R.id.df_ae_llayout);
			txName = (TextView) itemView.findViewById(R.id.df_ae_tx_name);
			icPosition = (ImageView) itemView
					.findViewById(R.id.df_ae_ic_position);
			svBtn = (SwitchView) itemView.findViewById(R.id.df_ae_sv_btn);
			txPositionName = (TextView) itemView
					.findViewById(R.id.df_ae_tx_positionName);
		}

	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return remote.size();
	}

	@Override
	public RemoteAreaHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		View view = View.inflate(mContext, R.layout.items_defence_area, null);
		RemoteAreaHolder holder = new RemoteAreaHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(RemoteAreaHolder holder, final int position) {
		// TODO Auto-generated method stub
		holder.txName.setText(remote.get(position).getName());
		holder.layout.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				remoteAreaListener.deleteRemoteArea(remote.get(position),
						position);
				return true;
			}
		});
		holder.layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				remoteAreaListener.changeRemoteAreaName(position,DefenceArea.REMOTETYPE);
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

	private RemoteAreaListener remoteAreaListener;

	public void setRemoteAreaListener(RemoteAreaListener remoteAreaListener) {
		this.remoteAreaListener = remoteAreaListener;
	}

	public interface RemoteAreaListener {
		void deleteRemoteArea(DefenceArea defenceArea, int position);
		void changeRemoteAreaName(int position, int type);
	}

}
