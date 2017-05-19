package com.lib.scaleimage;



import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wzy on 2016/5/19.
 */
public class PhotoViewPagerImpl extends ViewPager {
	private Context mContext;
	
    public PhotoViewPagerImpl(Context context) {
        super(context);
        mContext = context ;
    }

    public PhotoViewPagerImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context ;
    }
    private boolean mIsDisallowIntercept = false;
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // keep the info about if the innerViews do
        // requestDisallowInterceptTouchEvent
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // the incorrect array size will only happen in the multi-touch
        // scenario.
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
    
    public void setPhotoViewLisetner(PhotoViewPagerIistner listner){
    	
    }
}