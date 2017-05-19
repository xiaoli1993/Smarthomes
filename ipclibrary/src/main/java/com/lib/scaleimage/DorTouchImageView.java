/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.lib.scaleimage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.lib.imagesee.ImageTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DorTouchImageView extends RelativeLayout {
//	protected PinchImageView mImageView;
	protected PhotoView photoView ;
	protected Context mContext;
	private ImageLoader mImageLoader;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheOnDisk(false)
			.cacheInMemory(false)
			.build();// 构建完成
	public DorTouchImageView(Context ctx) {
		super(ctx);
		mContext = ctx;
		init();
	}

	public DorTouchImageView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mContext = ctx;
		init();
	}

	public PhotoView getImageView() {
		return photoView;
	}

	protected void init() {
		mImageLoader = ImageTools.getImageLoaderInstance(mContext);
//		mImageView = new TouchImageView(mContext);
//		LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
//				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
//		mImageView.setLayoutParams(params);
		photoView = new PhotoView(mContext);
		LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
		android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		photoView.setLayoutParams(params);
		this.addView(photoView);
	}

	public void setScaleType(ScaleType scaleType) {
		photoView.setScaleType(scaleType);
	}

	public void setUrlFromFile(String imagePath) {
		mImageLoader.displayImage("file://" + imagePath, photoView, options);
	}
	public void setUrlFromNet(String url) {
		mImageLoader.displayImage(url, photoView, options);
	}

	public void setOnImageClickListener(final onImageClikListiner clikListiner) {
		photoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				clikListiner.onPhotoClicked();

			}
		});
	}

	public void setFilePath(String path) {
		//mImageLoader.displayImage(url, mImageView, options);
//		Bitmap bitmap = BitmapFactory.decodeFile(path);
//		photoView.setImageBitmap(bitmap);
		photoView.setUrlFromFile(path);

	}



}
