package com.jwkj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwkj.data.Prepoint;
import com.nuowei.ipclibrary.R;

import java.util.List;

public class BindingLocationRecyAdapter extends RecyclerView.Adapter<BindingLocationRecyAdapter.LocationHolder>{
	private Context mContext;
	private List<Integer> location;
	private int[] select;
	private Prepoint prepoint;

	public BindingLocationRecyAdapter(Context mContext, List<Integer> location,
									  Prepoint prepoint) {
		super();
		this.mContext = mContext;
		this.location = location;
		this.prepoint = prepoint;
		select=new int[location.size()];
	}


	public class LocationHolder extends RecyclerView.ViewHolder{
		private View view;
		private LinearLayout bllLauyout;
		private TextView blTxName;
		private ImageView blIcLocation;

		public LocationHolder(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			view=itemView;
			bllLauyout=(LinearLayout)itemView.findViewById(R.id.bl_llayout);
			blTxName=(TextView)itemView.findViewById(R.id.bl_tx_name);
			blIcLocation=(ImageView)itemView.findViewById(R.id.bl_ic_location);
		}

	}

	/**
	 * 更新全部
	 */
	public void updateAll() {
		notifyDataSetChanged();
	}

	public void updateSelect(){
		select=new int[location.size()];
	}

	/**
	 * 更新指定的UI
	 *
	 * @param position
	 */
	public void updateByPosition(int position) {
		notifyItemChanged(position);
	}
	/**
	 * 跟新选中状态的数组
	 *
	 */
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return location.size();
	}

	@Override
	public LocationHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(R.layout.items_location, arg0, false);
		LocationHolder holder = new LocationHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(LocationHolder holder, final int position) {
		// TODO Auto-generated method stub
		if (location.get(position)!=-1) {
			holder.blTxName.setText(prepoint.getName(location.get(position)));
		}else {
			holder.blTxName.setText(R.string.not_binding_location);
		}
		if (select[position]==0) {
			holder.blIcLocation.setImageResource(R.drawable.img_bl_location_p);
		}else {
			holder.blIcLocation.setImageResource(R.drawable.img_bl_location);
		}
		holder.bllLauyout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bdLocationListener.bindingClick(position, location.get(position));
			}
		});
	}


	public void setSelect(int position){
//		for (int i = 0; i < select.length; i++) {
//			if (position==i) {
//				if (location.get(i)==-1||location.get(i)==position) {
//					select[i]=1;	
//				}
//			}else {
//				select[i]=0;
//			}
//		}
		if (position==-1||(location!=null&&position==location.size()-1)) {
			restoreSelect();
			if(location.size()-1>=0){
				select[location.size()-1]=1;
			}
		}else {
			restoreSelect();
			for (int i = 0; i < location.size()-1; i++) {
				if (position==location.get(i)) {
					select[i]=1;
				}
			}
		}
	}

	private BDLocationListener bdLocationListener;
	public void setBDLocationListener(BDLocationListener bdLocationListener){
		this.bdLocationListener=bdLocationListener;
	}
	public interface BDLocationListener{
		void bindingClick(int position, int value);
	}

	public void restoreSelect(){
		for (int i = 0; i < select.length; i++) {
			select[i]=0;
		}
	}


}
