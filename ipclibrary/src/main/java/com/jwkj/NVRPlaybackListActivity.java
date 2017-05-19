package com.jwkj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.juan.video.videoconnect;
import com.jwkj.activity.BaseActivity;
import com.jwkj.adapter.NVRListAdapter;
import com.jwkj.global.Constants;
import com.nuowei.ipclibrary.R;

public class NVRPlaybackListActivity extends BaseActivity{
	private ListView lvNVR;
	private String[] lists=new String[]{"0","1","2","3"};
	private NVRListAdapter adapter;
	private Context mContext;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		mContext=this;
		setContentView(R.layout.activity_nvrplayback);
		initUI();
	}

	private void initUI() {
		lvNVR=(ListView) findViewById(R.id.lv_nvrlist);
		adapter=new NVRListAdapter(lists);
		lvNVR.setAdapter(adapter);
		lvNVR.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent to=new Intent();
				to.setClass(mContext, NVRPlayBackActivity.class);
				to.putExtra("chinnal", position);
				startActivity(to);
			}
		});
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_NVRPLAYBACKLISTACTIVITY;
	}

}
