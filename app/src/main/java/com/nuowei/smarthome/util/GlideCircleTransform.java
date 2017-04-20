package com.nuowei.smarthome.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;

/**
 * @Author : 肖力
 * @Time :  2017/4/19 10:01
 * @Description :
 * @Modify record :
 */

public class GlideCircleTransform extends BitmapTransformation {
    public GlideCircleTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
//        paint.setShadowLayer(12.0f, 3.0f, 6f, MyApplication.getMyApplication().getResources().getColor(R.color.text_ash));
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return result;
    }
    /**

     * add shadow to bitmap
     *
     * @param originalBitmap
     * @return
     */
    private static Bitmap drawImageDropShadow(Bitmap originalBitmap) {

        BlurMaskFilter blurFilter = new BlurMaskFilter(1,
                BlurMaskFilter.Blur.NORMAL);
        Paint shadowPaint = new Paint();
        shadowPaint.setAlpha(50);
        shadowPaint.setColor( MyApplication.getMyApplication().getResources()
                .getColor(R.color.text_ash));
        shadowPaint.setMaskFilter(blurFilter);

        int[] offsetXY = new int[2];
        Bitmap shadowBitmap = originalBitmap
                .extractAlpha(shadowPaint, offsetXY);

        Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(originalBitmap, offsetXY[0], offsetXY[1], null);

        return shadowImage32;
    }

    /**
     * 获取白色边框
     *
     * @param path
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundeBitmapWithWhite(String path, Bitmap bitmap) {
        int size = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth()
                : bitmap.getHeight();
        int num = 14;
        int sizebig = size + num;
        Bitmap output = Bitmap.createBitmap(sizebig, sizebig, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = Color.parseColor("#FFFFFF");
        final Paint paint = new Paint();
        final float roundPx = sizebig / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawBitmap(bitmap, num / 2, num / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        RadialGradient gradient = new RadialGradient(roundPx, roundPx, roundPx,
                new int[]{Color.WHITE, Color.WHITE,
                        Color.parseColor("#AAAAAAAA")}, new float[]{0.f,
                0.97f, 1.0f}, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        return output;
    }

    /**
     * 添加阴影
     *
     * @param originalBitmap
     */
    public static Bitmap drawImageDropShadow(Bitmap originalBitmap, Context context) {

        BlurMaskFilter blurFilter = new BlurMaskFilter(1,
                BlurMaskFilter.Blur.NORMAL);
        Paint shadowPaint = new Paint();
        shadowPaint.setAlpha(50);


        shadowPaint.setColor(context.getResources().getColor(R.color.text_ash));
        shadowPaint.setMaskFilter(blurFilter);

        int[] offsetXY = new int[2];
        Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint, offsetXY);

        Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(originalBitmap, offsetXY[0], offsetXY[1], null);

        return shadowImage32;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
