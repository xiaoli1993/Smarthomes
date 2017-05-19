package com.jwkj.activity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.nuowei.ipclibrary.R;
import com.jwkj.MonitorActivity;
import com.jwkj.activity.BaseActivity;
import com.jwkj.global.Constants;
import com.lib.imagesee.FilePagerAdapter;
import com.lib.imagesee.GalleryViewPager;
import com.lib.scaleimage.PhotoViewAdapter;
import com.lib.scaleimage.PhotoViewListener;
import com.lib.scaleimage.PhotoViewPagerImpl;
import com.lib.scaleimage.PhotoViewPagerIistner;
import com.lib.scaleimage.onImageClikListiner;

public class ImageSeeActivity extends BaseActivity implements PhotoViewListener{
	File[] files;
	List<String> imagePath;
	private PhotoViewPagerImpl mViewPager;
	private Context mContext;
	private int currentItem;
	String callId;
	Intent mIntent;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_imagegallay);
		mContext = this;
		mIntent = getIntent();
		currentItem = mIntent.getIntExtra("currentImage", 0);
		callId = getIntent().getStringExtra("callId");
		initUI(currentItem);
	}

	public void initUI(int position) {
		imagePath = new ArrayList<String>();
		String screenshotPath = Environment.getExternalStorageDirectory()
				.getPath() + "/screenshot";
		File file = new File(screenshotPath);
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (null == callId || "".equals(callId)) {
					if (pathname.getName().endsWith(".jpg")) {
						return true;
					} else {
						return false;
					}
				} else {
					if (pathname.getName().startsWith(callId)) {
						return true;
					} else {
						return false;
					}
				}

			}
		};
		files = file.listFiles(filter);
		//按时间倒序
		Arrays.sort(files,new Comparator< File>(){

			@Override
			public int compare(File arg0, File arg1) {
				// TODO Auto-generated method stub
				long diff = arg0.lastModified() - arg1.lastModified();
				if (diff > 0)
					 return -1;
				else if (diff == 0)
					 return 0;
				else
					 return 1;
			}
		    @Override
		    public boolean equals(Object o) {
		    	// TODO Auto-generated method stub
		    	return true;
		    }
				
		 });
		for (int i = 0, count = files.length; i < count; i++) {
			imagePath.add((files[i]).getPath());
		}
//		FilePagerAdapter pagerAdapter = new FilePagerAdapter(mContext,
//				imagePath);
		PhotoViewAdapter pagerAdapter = new PhotoViewAdapter(mContext,imagePath);
		pagerAdapter.setPhotoClicked(this);
		// pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
		// @Override
		// public void onItemChange(int currentPosition) {
		// // Toast.makeText(mContext, "Current item is " + currentPosition,
		// // Toast.LENGTH_SHORT).show();
		// }
		// });

		mViewPager = (PhotoViewPagerImpl) findViewById(R.id.viewer);
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(position);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	@Override
	public int getActivityInfo() {
		return Constants.ActivityInfo.ACTIVITY_IMAGESEE;
	}
	
	public void finishActivity(){
		finish();
	}


	@Override
	public void onPhotoClicked() {
		// TODO Auto-generated method stub
		finishActivity();
	}

}
