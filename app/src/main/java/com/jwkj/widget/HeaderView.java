package com.jwkj.widget;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nuowei.smarthome.R;
import com.baidu.mobstat.t;
import com.juan.video.RecordPlayControl.loginRecordListener;
import com.jwkj.data.DataManager;
import com.jwkj.data.JAContact;
import com.jwkj.global.Constants;
import com.jwkj.global.Constants.P2P;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.HeaderImageUtils;
import com.jwkj.utils.ImageUtils;
import com.jwkj.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.p2p.core.P2PValue;

public class HeaderView extends ImageView {
	Bitmap tempBitmap;
	private Context mContext;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
//	.showImageOnLoading(R.drawable.header_icon)
    .showImageForEmptyUri(R.drawable.header_icon) // image连接地址为空时  
    .showImageOnFail(R.drawable.header_icon) // image加载失败 
    .cacheInMemory(false) // 加载图片时会在内存中加载缓存  
    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//    .bitmapConfig(Bitmap.Config.RGB_565)
    .displayer(new SimpleBitmapDisplayer())
//    .resetViewBeforeLoading(true) 
    .build();  
	public HeaderView(Context context) {
		super(context);
		this.mContext=context;
	}

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		// TypedArray array = context.obtainStyledAttributes(attrs,
		// R.styleable.);
		this.mContext = context;
	}

	public void updateImage(String threeNum, boolean isGray) {
		updateImage(threeNum, isGray, -1);
	}

	public void updateImage(String threeNum, boolean isGray, int oritation) {
//		if(NpcCommon.mThreeNum==null||NpcCommon.mThreeNum.length()<=0){
//			NpcCommon.mThreeNum="0517401";
//		}
		try {

			if (oritation == P2PValue.DeviceType.NVR) {
				new NVRHeaderTask(mContext, this).execute(threeNum);
			} else {
				tempBitmap = ImageUtils.getBitmap(new File(
						"/sdcard/screenshot/tempHead/" + NpcCommon.mThreeNum
								+ "/" + threeNum + ".jpg"), 200, 200);
//				tempBitmap = ImageUtils.roundCorners(tempBitmap,
//						ImageUtils.getScaleRounded(tempBitmap.getWidth()));
				this.setImageBitmap(tempBitmap);
			}
		} catch (Exception e) {
			tempBitmap = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.header_icon);
            //OOM
//			if (oritation != -1) {
//				tempBitmap = ImageUtils.roundHalfCorners(mContext, tempBitmap,
//						5, oritation);
//			} else {
//				tempBitmap = ImageUtils.roundCorners(tempBitmap,
//						ImageUtils.getScaleRounded(tempBitmap.getWidth()));
//			}
			this.setImageBitmap(tempBitmap);
		}
	}
//	public void updateImage(String threeNum, boolean isGray, int oritation) {
////		if(NpcCommon.mThreeNum==null||NpcCommon.mThreeNum.length()<=0){
////			NpcCommon.mThreeNum="0517401";
////		}
//		try {
//			if (oritation == P2PValue.DeviceType.NVR) {
//				new NVRHeaderTask(mContext, this).execute(threeNum);
//			} else {
//				ImageLoader.getInstance().displayImage("file://"+Environment.getExternalStorageDirectory().getPath()+"/screenshot/tempHead/" + NpcCommon.mThreeNum
//						+ "/" + threeNum + ".jpg", this,options);
//			}
//		} catch (Exception e) {
//			tempBitmap = BitmapFactory.decodeResource(this.getResources(),
//					R.drawable.header_icon);
//            //OOM
////			if (oritation != -1) {
////				tempBitmap = ImageUtils.roundHalfCorners(mContext, tempBitmap,
////						5, oritation);
////			} else {
////				tempBitmap = ImageUtils.roundCorners(tempBitmap,
////						ImageUtils.getScaleRounded(tempBitmap.getWidth()));
////			}
//			this.setImageBitmap(tempBitmap);
//		}
//	}
	public void updateImage(String threeNum, boolean isGray, int oritation,final HeaderView head,final String tag) {
		try {
			if (oritation == P2PValue.DeviceType.NVR) {
				head.setTag(tag);
				new NVRHeaderTask(mContext, this).execute(threeNum);
			} else {
				ImageLoader.getInstance().displayImage("file://"+Environment.getExternalStorageDirectory().getPath()+"/screenshot/tempHead/" + NpcCommon.mThreeNum
						+ "/" + threeNum + ".jpg", this,options,new ImageLoadingListener() {
							
							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								
							}
							
							@Override
							public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

							}
							
							@Override
							public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
								// TODO Auto-generated method stub
								head.setTag(tag);
							}
							
							@Override
							public void onLoadingCancelled(String arg0, View arg1) {
								// TODO Auto-generated method stub
								
							}
						});
			}
		} catch (Exception e) {
			tempBitmap = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.header_icon);
			this.setImageBitmap(tempBitmap);
		}
	}
	public Bitmap getBitmap(){
		return tempBitmap;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	class NVRHeaderTask extends AsyncTask<String, Void, Bitmap> {
		private static final int scale = 2;
		private Context mContext;
		private ImageView ivHeader;
		RectF r1 = new RectF(0, 0, scale * 100, scale * 100);
		RectF r2 = new RectF(scale * 100, 0, scale * 200, scale * 100);
		RectF r3 = new RectF(0, scale * 100, scale * 100, scale * 200);
		RectF r4 = new RectF(scale * 100, scale * 100, scale * 200, scale * 200);
		RectF[] fs = new RectF[] { r1, r2, r3, r4 };

		public NVRHeaderTask(Context mContext, ImageView ivHeader) {
			super();
			this.mContext = mContext;
			this.ivHeader = ivHeader;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap;
			try {
				JAContact conta = DataManager
						.findJAContactByActiveUserAndContactId(mContext,
								NpcCommon.mThreeNum, params[0]);
				File[] file = null;
				if (conta != null) {
					file = Utils.getHeaderImage(conta.getJaid());
				}
				
				if (file == null || file.length <= 0) {
					bitmap = BitmapFactory.decodeResource(mContext.getResources(),
							R.drawable.header_icon);
				} else {
					bitmap = Bitmap.createBitmap(scale * 200, scale * 200,
							Config.RGB_565);
					Canvas canvas = new Canvas(bitmap);
					Bitmap map;
					for (int i = 0; i < 4; i++) {
						File f=HasImage(file, i);
						if (f!=null) {
							map = compressImageFromFile(f.getPath());
						} else {
							map = BitmapFactory.decodeResource(
									mContext.getResources(), R.drawable.no_video_header);
						}
						canvas.drawBitmap(map, null, fs[i], null);
					}
				}
			} catch (Exception e) {
				bitmap = BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.header_icon);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			ivHeader.setScaleType(ScaleType.FIT_XY);
			ivHeader.setImageBitmap(result);
		}

		private File HasImage(File[] file, int i) {
			for (File file2 : file) {
				int k = getFileNumber(file2.getName());
				if (k==i) {
					return file2;
				}
			}
			return null;
		}

		private int getFileNumber(String fileName) {
			int s = fileName.lastIndexOf("_");
			String number = fileName.substring(s + 1, s + 2);
			return Integer.parseInt(number);
		}
	}

	
	private Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 200f;//
		float ww = 200f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

}
