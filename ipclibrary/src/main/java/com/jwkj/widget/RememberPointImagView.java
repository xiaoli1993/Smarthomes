package com.jwkj.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.jwkj.entity.OnePrepoint;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nuowei.ipclibrary.R;


/**
 * Created by dxs on 2015/12/3.
 */
public class RememberPointImagView extends CircleImageView {

    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.remember_defalt) // 设置图片在下载期间显示的图片
            .showImageForEmptyUri(R.drawable.remember_defalt)// 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.remember_defalt) // 设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                    // .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    // .delayBeforeLoading(int delayInMillis)//int
                    // delayInMillis为你设置的下载前的延迟时间
                    // 设置图片加入缓存前，对bitmap进行设置
                    // .preProcessor(BitmapProcessor preProcessor)
            .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                    //.displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                    // .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
            .build();// 构建完成
    private OnePrepoint points;
    public RememberPointImagView(Context context) {
        super(context);
    }
    public RememberPointImagView(Context context,OnePrepoint points) {
        super(context);
        this.points=points;
        setBitmap(points.imagePath);
    }

    public RememberPointImagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBitmap(String activeUser,String deviceId,int prepoint){
    }
    
    public void setBitmap(String path){
        imageLoader.displayImage("file://" + path, this, options);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int w=MeasureSpec.getSize(widthMeasureSpec);
    	int h=MeasureSpec.getSize(heightMeasureSpec);
    	int wMode=MeasureSpec.getMode(widthMeasureSpec);
    	int hMode=MeasureSpec.getMode(heightMeasureSpec);
    	
    	int s=Math.min(w, h);
    	widthMeasureSpec=MeasureSpec.makeMeasureSpec(s, wMode);
    	heightMeasureSpec=MeasureSpec.makeMeasureSpec(s, hMode);
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
