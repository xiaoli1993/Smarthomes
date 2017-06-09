package com.jwkj.recycleview.ItemDecor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jwkj.utils.Utils;

/**
 * Created by dxs on 2015/12/30.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int space;
    private int color;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildPosition(view) != 0)
            outRect.top = Utils.dip2px(parent.getContext(),space);
    }
}
