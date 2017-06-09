package com.jwkj.activity;

import android.os.Bundle;

import com.jwkj.global.Constants;
import com.lib.scaleimage.PhotoViewAdapter;
import com.lib.scaleimage.PhotoViewListener;
import com.lib.scaleimage.PhotoViewPagerImpl;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmPictrueActivity extends BaseActivity implements PhotoViewListener {
	String alarmPictruePath;
	List<String> imagePath;
	private PhotoViewPagerImpl mViewPager;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_alarm_pictrue);
		alarmPictruePath=getIntent().getStringExtra("alarmPictruePath");
		imagePath = new ArrayList<String>();
		imagePath.add(alarmPictruePath);
		
		PhotoViewAdapter pagerAdapter = new PhotoViewAdapter(this,imagePath);
		mViewPager = (PhotoViewPagerImpl) findViewById(R.id.viewer);
		pagerAdapter.setPhotoClicked(this);
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(0);
//		Bitmap bitmap = ImageUtils.getBitmap(alarmPictruePath);
//		iv_alarm_pictrue.setImageBitmap(bitmap);
//		iv_alarm_pictrue.setPictrue(alarmPictruePath);
	}
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_ALRAM_PICTRUE;
	}
	@Override
	public void onPhotoClicked() {
		// TODO Auto-generated method stub
		finish();
	}

}
