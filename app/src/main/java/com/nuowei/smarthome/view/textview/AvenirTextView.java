package com.nuowei.smarthome.view.textview;/**
 * Created by hp on 2016/7/18.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 作者：肖力
 * 邮箱：554674787@qq.com
 * 时间：2016/7/18 10:53
 */
public class AvenirTextView extends TextView {
    public AvenirTextView(Context context) {
        super(context);
        init(context);
    }

    public AvenirTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AvenirTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AvenirTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        AssetManager assetmanager = context.getAssets();
        Typeface fonc = Typeface.createFromAsset(assetmanager, "Avenir.ttf");
        setTypeface(fonc);
    }
}
