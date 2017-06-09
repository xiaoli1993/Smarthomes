package com.jwkj.adapter;

import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wzy on 2016/10/13.
 * 滚动延迟加载适配
 */

public abstract  class ImageBaseAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    ImageLoader imageLoader = ImageLoader.getInstance();

    AbsListView listView ;

    boolean isOnscroll;

    public ListView.OnScrollListener onScrollListener;

    public ListView.OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public void setOnScrollListener(ListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public boolean isOnscroll() {
        return isOnscroll;
    }

    public void setOnscroll(boolean onscroll) {
        isOnscroll = onscroll;
    }

    public AbsListView getListView() {
        return listView;
    }

    public void setListView(AbsListView listView) {
        this.listView = listView;

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            isOnscroll = false;
            imageLoader.resume();
        }else{
            isOnscroll = true;
            imageLoader.pause();
        }
    }
}
