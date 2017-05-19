package com.jwkj.widget;

import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.TextView;

public class ProgressTextView extends TextView {
    public static final int STATE_TEXT = 1;
    public static final int STATE_PROGRESS = 0;
    public static final int STATE_ERROR = 2;
    public static final int PROGRESS_CENTER = 0;
    public static final int PROGRESS_LEFT = 1;
    public static final int PROGRESS_RIGHT = 2;
    private int State = 0;
    private int progressPosition = PROGRESS_CENTER;
    private Context mContext;
    private ArcAnimal animal;
    private float degree = 0;
    private Bitmap progressBitmap;
    private Paint mPaint;
    private String text = "";

    public ProgressTextView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        text = getResources().getString(R.string.search);
        animal = new ArcAnimal();
        animal.setRepeatCount(-1);
        animal.setRepeatMode(Animation.RESTART);
        animal.setInterpolator(new LinearInterpolator());
        animal.setDuration(800);
        progressBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.progress_white_small);
        progressBitmap = zoomImage(progressBitmap, Utils.dip2px(mContext, 15),
                Utils.dip2px(mContext, 15));
        mPaint = getPaint();
    }

    /**
     * text不再起作用
     *
     * @param modeStatde
     * @param text
     */
    public void setModeStatde(int modeStatde, String text) {
        State = modeStatde;
        startLoading(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int Mode = MeasureSpec.getMode(widthMeasureSpec);
        int textW = (int) mPaint.measureText(text);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(w, textW), Mode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        startLoading("");
        super.onAttachedToWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (State != STATE_PROGRESS) {
            DrawText(canvas);
        } else {
            canvas.save();
            DraProgress(canvas);
            canvas.restore();
        }
    }

    private void DrawText(Canvas canvas) {
        Rect targetRect = new Rect(0, 0, getWidth(), getHeight());
        if (text.length() > 0) {
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top
                    - fontMetrics.ascent + fontMetrics.descent)
                    / 2 - fontMetrics.bottom;
            if (progressPosition == PROGRESS_LEFT) {
                mPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(text, 0, baseline, mPaint);
            } else if (progressPosition == PROGRESS_RIGHT) {
                mPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(text, getWidth(), baseline, mPaint);
            } else {
                mPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(text, targetRect.centerX(), baseline, mPaint);
            }
        }
    }

    private void DraProgress(Canvas canvas) {
        float left, top;
        if (progressBitmap != null && State == 0) {
            if (progressPosition == PROGRESS_LEFT) {
                left = 0;
                top = (getHeight() - progressBitmap.getHeight()) / 2;
                canvas.rotate(degree * 360, progressBitmap.getWidth() / 2, getHeight() / 2);
            } else if (progressPosition == PROGRESS_RIGHT) {
                left = getWidth() - progressBitmap.getWidth();
                top = (getHeight() - progressBitmap.getHeight()) / 2;
                canvas.rotate(degree * 360, getWidth() - progressBitmap.getWidth() / 2, getHeight() / 2);
            } else {
                left = (getWidth() - progressBitmap.getWidth()) / 2;
                top = (getHeight() - progressBitmap.getHeight()) / 2;
                canvas.rotate(degree * 360, getWidth() / 2, getHeight() / 2);
            }

            canvas.drawBitmap(progressBitmap, left, top, mPaint);
        }
    }

    public class ArcAnimal extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            degree = interpolatedTime;
            postInvalidate();
        }
    }

    private void startLoading(String Text) {
        switch (State) {
            case STATE_PROGRESS:
                this.setClickable(false);
                if (animal == null) {
                    init();
                }
                this.startAnimation(animal);
                break;
            case STATE_ERROR:
            case STATE_TEXT:
                this.clearAnimation();
                this.text = Text;
                this.setClickable(true);
                requestLayout();
                break;
        }
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    public int getProgressPosition() {
        return progressPosition;
    }

    public void setProgressPosition(int progressPosition) {
        this.progressPosition = progressPosition;
        mPaint.setColor(Color.parseColor("#8d8d8d"));
        invalidate();
    }
}
