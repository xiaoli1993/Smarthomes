package com.jwkj.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.adapter.ImageBrowserAdapter;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nuowei.ipclibrary.R;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * create by wzy 2016/9/22
 */
public class UtilsFrag extends BaseFragment implements OnClickListener {
    private Context mContext;
    File[] files;
    GridView list;
    ImageBrowserAdapter adapter;
    RelativeLayout rl_screenshot_alldelete;
    LinearLayout l_no_pictrue;
    ImageView iv_exitselmode;
    ImageView btn_del;
    TextView tv_selall;
    boolean isSelectMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_utils, container, false);
        mContext = getActivity();
        if (null == files) {
            files = new File[0];
        }
        initComponent(view);
        return view;

    }

    public void initComponent(View view) {
        rl_screenshot_alldelete = (RelativeLayout) view.findViewById(R.id.rl_screenshot_alldelete);
        rl_screenshot_alldelete.setOnTouchListener(onTouchListener);
        rl_screenshot_alldelete.setOnClickListener(this);
        list = (GridView) view.findViewById(R.id.list_grid);
        l_no_pictrue = (LinearLayout) view.findViewById(R.id.l_no_pictrue);
        iv_exitselmode = (ImageView) view.findViewById(R.id.iv_exitselmode);
        tv_selall = (TextView) view.findViewById(R.id.chat_selall);
        adapter = new ImageBrowserAdapter(mContext, this);
        list.setAdapter(adapter);
        //list.setOnScrollListener(adapter);
        btn_del = (ImageView) view.findViewById(R.id.iv_delete_one);
        iv_exitselmode.setOnClickListener(this);
        tv_selall.setOnClickListener(this);
        hideSelModeView();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    /**
     * 图片加载监听事件
     **/
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
                    displayedImages.add(imageUri); // 将图片uri添加到集合中
                }
            }
        }
    }

    public void showHideNoPictrue(File[] data) {
        if (data.length <= 0) {
            l_no_pictrue.setVisibility(View.VISIBLE);
        } else {
            l_no_pictrue.setVisibility(View.GONE);
        }
    }

    public void showNoPicture() {
        l_no_pictrue.setVisibility(View.VISIBLE);
    }

    public void hideNoPicture() {
        l_no_pictrue.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rl_screenshot_alldelete) {
            adapter.showDelDialog();

        } else if (i == R.id.iv_exitselmode) {
            adapter.existSelectMode();

        } else if (i == R.id.chat_selall) {
            selectAll();

        }
    }

    private void selectAll() {
        if (!isSelectMode) {
            isSelectMode = !isSelectMode;
            adapter.setSelMode();
            tv_selall.setText(R.string.select_all);
            showSelModeView();
        } else {
            String select_all = tv_selall.getText().toString();
            String res_selall = getString(R.string.select_all);
            String res_cancelall = getString(R.string.cancle_all);
            if (select_all.equals(res_selall)) {
                tv_selall.setText(R.string.cancle_all);
                adapter.selecteAll();
            } else if (select_all.equals(res_cancelall)) {
                tv_selall.setText(R.string.select_all);
                adapter.cancelSelectAll();
            }
        }
    }

    public void setSelBtn(int id) {
        tv_selall.setText(id);
    }

    public void showSelModeView() {
        isSelectMode = true;
        tv_selall.setText(R.string.select_all);
        rl_screenshot_alldelete.setVisibility(View.VISIBLE);
        iv_exitselmode.setVisibility(View.VISIBLE);
    }

    public void hideSelModeView() {
        rl_screenshot_alldelete.setVisibility(View.GONE);
        iv_exitselmode.setVisibility(View.GONE);
        tv_selall.setText(R.string.select);
        isSelectMode = false;
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if (parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

    RelativeLayout.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    btn_del.setBackgroundResource(R.drawable.screenshot_delete_press);
                    break;
                case MotionEvent.ACTION_UP:
                    btn_del.setBackgroundResource(R.drawable.screenshot_delete);
                    break;
            }
            return false;
        }
    };

    public void hideSelBtn() {
        tv_selall.setVisibility(View.GONE);
    }

    public void hideCancelBtn() {
        iv_exitselmode.setVisibility(View.GONE);
    }

    public void hideDelRel() {
        rl_screenshot_alldelete.setVisibility(View.GONE);
    }
}
