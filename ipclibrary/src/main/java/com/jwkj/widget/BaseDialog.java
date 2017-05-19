package com.jwkj.widget;

import android.app.Dialog;
import android.content.Context;

import com.nuowei.ipclibrary.R;

/**
 * Created by wzy on 2016/10/11.
 */

public class BaseDialog extends Dialog {
    public BaseDialog(Context context) {
        super(context,R.style.add_dialog);
    }
    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }
}
