package com.jwkj.recycleview.ItemDecor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

/**
 * Created by dxs on 2015/12/30.
 */
public class lineViewItemDecoration extends RecyclerView.ItemDecoration{
    private static final float LineMarginLeft=65f;
    private int space;
    private int left;
    private Paint mPaint=new Paint();

    public lineViewItemDecoration(Context context,int space) {
        this.space = space;
        left= Utils.dip2px(context, LineMarginLeft);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(Utils.dip2px(context, 0.5f));
        mPaint.setAlpha((int) (0.8*255));
        mPaint.setColor(context.getResources().getColor(R.color.main_titlebar));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //super.onDraw(c, parent, state);
        drawVertical(c, parent, state);
    }

    private void drawVertical(Canvas c, RecyclerView parent,RecyclerView.State state) {
        c.drawLine(left, 0, left, c.getClipBounds().bottom,mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, space);
    }
}
