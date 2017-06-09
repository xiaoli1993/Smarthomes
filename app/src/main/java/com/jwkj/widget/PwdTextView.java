package com.jwkj.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.nuowei.smarthome.R;

/**
 * Created by wzy on 2016/6/6.
 */
public class PwdTextView extends EditText {
    private Context mContext;
    private Drawable imgInable;
    private Drawable imgAble;
    private boolean isTextVisiable;
    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    public PwdTextView(Context context) {
        super(context);
        mContext = context ;
        init();
    }
 
    
    public PwdTextView(Context context, AttributeSet attrs) {
 
        super(context, attrs);
        mContext = context ;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void init(){
        imgInable = mContext.getResources().getDrawable(R.drawable.pwd_eye_gray);
        imgAble = mContext.getResources().getDrawable(R.drawable.pwd_eye_blue);

        changeVisiable();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
//        if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
//            int eventX = (int) event.getRawX();
//            int eventY = (int) event.getRawY();
//            Rect rect = drawableRight.getBounds();
////            Rect rect = new Rect();
////            getGlobalVisibleRect(rect);
////            rect.left = rect.right - 50;
//            if(rect.contains(eventX, eventY)){
//                isTextVisiable = !isTextVisiable;
//                changeVisiable();
//            }
//        }
//        return super.onTouchEvent(event);
//    }

    private interface DrawableRightListener{
        public  void onDrawableRightClick();
    }

    DrawableRightListener mRightListener = new DrawableRightListener() {
        @Override
        public void onDrawableRightClick() {
            isTextVisiable = !isTextVisiable;
            changeVisiable();
        }
    };
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mRightListener != null) {
                    Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT] ;

                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {

                        mRightListener.onDrawableRightClick() ;
                        return false ; //返回false避免点击时Edtittext会获取焦点的问题
                    }
                }
                break;
        }

        return super.onTouchEvent(event);
    }


    //设置查看图片
    private void changeVisiable() {

        if(isTextVisiable){
            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble, null);
        } else {
            setTransformationMethod(PasswordTransformationMethod.getInstance());
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgInable, null);
        }
    }

}
