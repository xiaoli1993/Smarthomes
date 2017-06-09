package com.jwkj.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

/**
 * Created by dxs on 2016/2/23.
 */
public class lineView extends View {
    private static final int LineMarginLeft=64;
    private int left;
    private int h;
    private int w;
    private Paint mPaint=new Paint();
    public lineView(Context context,int w,int h) {
        super(context);
        this.w=w;
        this.h=h;
        init(context);
    }

    public lineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        left= Utils.dip2px(context,LineMarginLeft);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.main_titlebar));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DrawableLine(canvas);
    }

    private void DrawableLine(Canvas canvas){
        canvas.drawLine(left,0,left,getHeight(),mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec=MeasureSpec.makeMeasureSpec(w,MeasureSpec.EXACTLY);
        heightMeasureSpec=MeasureSpec.makeMeasureSpec(h,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
