package com.jwkj.listener;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jwkj.utils.Utils;
import com.nuowei.smarthome.MyApplication;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dxs on 2015/9/11.
 */
public class DrawTextImageTask extends AsyncTask<String, Void, Integer> {
	private Handler handler;
	Message msg = new Message();
	private Bitmap bitmap;
	public static final int DrawTextHandlerWhat=1000;

	public DrawTextImageTask(Handler handler, Bitmap bitmap) {
		this.handler = handler;
		this.bitmap = bitmap;
	}

	@Override
	protected Integer doInBackground(String... params) {
		int ret=-1;
		try {
			Bitmap src = BitmapFactory.decodeFile(params[0]);
			Bitmap newScreenShot = createWaterMaskImage(src, bitmap);
			saveMyBitmap(newScreenShot, params[0]);
			ret=1;
		} catch (Exception e) {
			ret=-1;
		}
		Log.e("dxsTest", "ret:"+ret);
		return ret;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		msg.what = DrawTextHandlerWhat;
		msg.arg1=integer;
		handler.sendMessage(msg);
	}

	@Override
	protected void onCancelled() {
		msg.what = DrawTextHandlerWhat;
		msg.arg1=-1;
		handler.sendMessage(msg);
	}

	private Bitmap createWaterMaskImage(Bitmap src, Bitmap watermark) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.RGB_565);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(watermark, Utils.dip2px(MyApplication.app, 10), Utils.dip2px(MyApplication.app, 11), null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	/**
	 * 保存文件到指定的路径下面
	 * 
	 * @param bitmap
	 * @param bitName
	 *            文件路径
	 */
	public void saveMyBitmap(Bitmap bitmap, String bitName) throws Exception {
		if(bitmap==null){throw new Exception("Create Bitmap error");}
		File f = new File(bitName);
		FileOutputStream fOut = null;
		f.createNewFile();
		fOut = new FileOutputStream(f);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		fOut.flush();
		fOut.close();
	}
}
