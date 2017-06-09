package com.jwkj.adapter;

import com.jwkj.adapter.APContactAdapter.ViewHolder;
import com.jwkj.widget.HeaderView;
import com.nuowei.smarthome.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NVRListAdapter extends BaseAdapter{
	private String[] nvrLists; 
	public NVRListAdapter(String[] nvrLists) {
		super();
		this.nvrLists = nvrLists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return nvrLists.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return nvrLists[position];
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
		holder.name.setText(nvrLists[position]);
		return convertView;
	}
	
	class ViewHolder {
		private TextView name;
	}

}
