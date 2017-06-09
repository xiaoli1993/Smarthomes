package com.jwkj.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jwkj.adapter.NVRListAdapter.ViewHolder;
import com.jwkj.entity.NVRRecodeTime;
import com.nuowei.smarthome.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NVRRecoderAdapter extends BaseAdapter{
	private List<NVRRecodeTime> list;
	
	

	public NVRRecoderAdapter(List<NVRRecodeTime> list) {
		super();
		this.list = list;
		 Collections.sort(list);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.items_nvrplayback, null);
			holder = new ViewHolder();
			holder.name=(TextView) convertView.findViewById(R.id.tx_nvrid);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NVRRecodeTime time=list.get(position);
		holder.name.setText(time.getStarTime()+"-"+time.getEndTimes());
		return convertView;
	}
	
	class ViewHolder {
		private TextView name;
	}
	
	public void Notify(List<NVRRecodeTime> recoderList){
		this.list=recoderList;
		Collections.sort(list);
		this.notifyDataSetChanged();
	}

}
