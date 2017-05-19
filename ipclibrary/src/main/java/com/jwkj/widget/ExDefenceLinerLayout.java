package com.jwkj.widget;

import com.jwkj.entity.DefenceWorkGroup;
import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ExDefenceLinerLayout  extends RelativeLayout implements View.OnClickListener{
	private final static int CLOSE = 0;
    private final static int EXPANDE = 1;
    private int ExpandeState = CLOSE;
    private float EditorimageW = 15.5f;
    private float DeleteimageW = 15.5f;
    private final static float ImageHeight = 35.5f;
    private final static int padding = 11;
    private int w, h;
    private ImageView ivEditer, ivDelete;
    private Context context;
    private int RealPadding;
    private int RealImageHeigh;
    private boolean isAnimal = false;
    private scedueView mScedueView;
    private ExpandCollapseAnimation anim;
    private float progress;
    private int HighSpace;

    public ExDefenceLinerLayout(Context context) {
        super(context);
        this.context = context;
    }

    public ExDefenceLinerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        RealPadding = Utils.dip2px(context, padding);
        RealImageHeigh = Utils.dip2px(context, ImageHeight);
        EditorimageW = Utils.dip2px(context, EditorimageW);
        DeleteimageW = Utils.dip2px(context, DeleteimageW);
        ivEditer = new ImageView(context);
        ivEditer.setScaleType(ImageView.ScaleType.CENTER);
        ivDelete = new ImageView(context);
        ivDelete.setScaleType(ImageView.ScaleType.CENTER);
        ivEditer.setTag(0);
        ivDelete.setTag(1);
        ivEditer.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        HighSpace = Utils.dip2px(context, 71f) - Utils.dip2px(context, 53.5f);
        addView(ivEditer);
        addView(ivDelete);
    }

    /**
     * 开始展开与闭合的动画
     *
     * @param startH   开始高度
     * @param endHeigh 结束高度
     */
    public void startExpandCollAnima(int startH, int endHeigh) {
        this.anim = new ExpandCollapseAnimation(this, startH, endHeigh);
        this.startAnimation(anim);
        anim.setAnimationListener(animalListner);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, hMode);
        int childW = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
        int childH = MeasureSpec.makeMeasureSpec(h - RealImageHeigh, MeasureSpec.EXACTLY);
        if (getChildCount() > 0) {
            View v = getChildAt(0);
            v.measure(childW, childH);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (ivEditer != null) {
            ivEditer.setImageResource(R.drawable.bg_mode_editor_down);
        }
        if (ivDelete != null) {
            ivDelete.setImageResource(R.drawable.bg_mode_delete);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        w = getMeasuredWidth();
        h = getMeasuredHeight();
        ivEditer = (ImageView) getChildAt(0);
        ivDelete = (ImageView) getChildAt(1);
        ivEditer.layout(RealPadding, h - RealImageHeigh, (int) (EditorimageW + RealPadding), h);
        ivDelete.layout((int) (w - DeleteimageW - RealPadding), h - RealImageHeigh, w - RealPadding, h);
        mScedueView = (scedueView) getChildAt(2);
        if (isAnimal) {
            mScedueView.layout(0, 0, w, RealImageHeigh);
        } else {
            mScedueView.layout(0, 0, w, h - RealImageHeigh);
        }
        mScedueView.setTag(2);
        mScedueView.setOnClickListener(this);
        updateUI(ExpandeState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isAnimal;
    }

    @Override
    public void onClick(View v) {
        switch (Integer.parseInt(v.getTag().toString())) {
            case 0://编辑
                if (listner != null) {
                    listner.onEditorClick(ExpandeState);
                }
                Log.e("dxsTestclick","v.name-->"+v.getClass().getName()+"ExpandeState-->"+ExpandeState);
                if (ExpandeState == EXPANDE) {
                    startExpandCollAnima(Utils.dip2px(context, 71f), Utils.dip2px(context, 53.5f));
                } else {
                    startExpandCollAnima(Utils.dip2px(context, 53.5f), Utils.dip2px(context, 71f));
                }
                break;
            case 1://删除
                if (listner != null && mScedueView != null) {
                    listner.onDeleteClick((DefenceWorkGroup)mScedueView.getWorkGroup());
                }
                break;
            case 2://修改时间组
                Log.e("dxsTest", "isAnimal-->" + isAnimal + "ExpandeState-->" + ExpandeState);
                if (!isAnimal && (ExpandeState == EXPANDE) && listner != null) {
                    Log.e("dxsTest", "isAnimal-->" + isAnimal + "ExpandeState-->" + ExpandeState);
                    listner.onTimeClick((DefenceWorkGroup)mScedueView.getWorkGroup());
                }
                break;
        }
    }

    private void updateUI(int state) {
        mScedueView.setViewState(state);
        if (state == EXPANDE) {
            if (ivDelete != null && ivEditer != null) {
                ivDelete.setVisibility(View.VISIBLE);
                ivEditer.setImageResource(R.drawable.bg_mode_editor_up);
            }
        } else if (state == CLOSE) {
            if (ivDelete != null && ivEditer != null) {
                ivDelete.setVisibility(View.INVISIBLE);
                ivEditer.setImageResource(R.drawable.bg_mode_editor_down);
            }
        }
    }

    //更新日期控件显示高度
    private void UpdateLayoutHight(int state) {
        if (state == EXPANDE) {

        } else if (state == CLOSE) {

        }
    }

    //解析图片的宽
    private int getImageWith(int R) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R, options);
        float dp = Utils.px2dp(context, options.outWidth);
        return Utils.dip2px(context, dp);
    }

    class ExpandCollapseAnimation extends Animation {
        long mAnimationDuration = 250;
        private final View mTargetView;
        private final int mEndHeight;
        private final int mStartHeight;

        public ExpandCollapseAnimation(View view, int startHeight, int endHeight) {
            mTargetView = view;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(mAnimationDuration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            final int newHeight = (int) (mStartHeight - (mStartHeight - mEndHeight) * interpolatedTime);
            mTargetView.getLayoutParams().height = newHeight;
            mTargetView.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public interface ExDefenceLinearLayoutListner {
        void onEditorClick(int LayoutState);

        void onTimeClick(DefenceWorkGroup group);

        void onDeleteClick(DefenceWorkGroup group);
    }

    private ExDefenceLinearLayoutListner listner;

    public void setOnExDefenceLinearLayoutListner(ExDefenceLinearLayoutListner listner) {
        this.listner = listner;
    }

    private Animation.AnimationListener animalListner = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isAnimal = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isAnimal = false;
            if (ExpandeState == CLOSE) {
                ExpandeState = EXPANDE;
            } else {
                ExpandeState = CLOSE;
            }
            updateUI(ExpandeState);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
