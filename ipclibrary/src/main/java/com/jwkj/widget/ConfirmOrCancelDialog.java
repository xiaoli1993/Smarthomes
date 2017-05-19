package com.jwkj.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.nuowei.ipclibrary.R;

/**
 * Created by wzy on 2016/10/11.
 */

public class ConfirmOrCancelDialog extends BaseDialog {
    private TextView tv_yes;
    private TextView tv_no;
    private String title;
    private TextView tv_title;
    private View.OnClickListener onYesClickListener;
    private View.OnClickListener onNoClickListener;
    public static final int SELECTOR_BLUE_TEXT = R.color.selector_blue_text_button;
    public static final int SELECTOR_GARY_TEXT = R.color.selector_gray_text_button;

    public ConfirmOrCancelDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_confirmorcancel);
        initUI();
        tv_yes.setTextColor(context.getResources().getColorStateList(SELECTOR_BLUE_TEXT));
        tv_no.setTextColor(context.getResources().getColorStateList(SELECTOR_GARY_TEXT));
    }

    /**
     * WXY
     * 用于设定按钮自定义的文本颜色
     * 颜色值可以传color文件内的颜色值
     * 也可以是 SELECTOR_BLUE_TEXT SELECTOR_GARY_TEXT 设定好的selector颜色值 详情点击参数进入查看
     * 也可以模仿 SELECTOR_BLUE_TEXT SELECTOR_GARY_TEXT 参数值文件自定义selector颜色值
     * @param context
     * @param leftColor 左按钮文本颜色int值
     * @param rightColor 左按钮文本颜色int值
     */
    public ConfirmOrCancelDialog(Context context,int leftColor,int rightColor){
        super(context);
        setContentView(R.layout.dialog_confirmorcancel);
        initUI();
        tv_yes.setTextColor(context.getResources().getColorStateList(rightColor));
        tv_no.setTextColor(context.getResources().getColorStateList(leftColor));
    }

    public ConfirmOrCancelDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_confirmorcancel);
        initUI();
        tv_yes.setTextColor(context.getResources().getColorStateList(SELECTOR_BLUE_TEXT));
        tv_no.setTextColor(context.getResources().getColorStateList(SELECTOR_GARY_TEXT));
    }


    public void setOnNoClickListener(View.OnClickListener onNoClickListener) {
        this.onNoClickListener = onNoClickListener;
    }

    public void setOnYesClickListener(View.OnClickListener onYesClickListener) {
        this.onYesClickListener = onYesClickListener;
    }

    public void setTitle(String title) {
        this.title = title;
        tv_title.setText(title);
    }

    public void setTitle(int titleRes) {

        tv_title.setText(titleRes);
    }

    /**
     * yes 在右
     *
     * @param str1
     */
    public void setTextYes(String str1) {
        tv_yes.setText(str1);
    }

    /**
     * no 在左
     *
     * @param str2
     */
    public void setTextNo(String str2) {
        tv_no.setText(str2);
    }

    public void initUI() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        tv_yes = (TextView) findViewById(R.id.tv_yes);
        tv_no = (TextView) findViewById(R.id.tv_no);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onYesClickListener != null) {
                    onYesClickListener.onClick(v);
                }
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNoClickListener != null) {
                    onNoClickListener.onClick(v);
                }
            }
        });

        tv_title = (TextView) findViewById(R.id.title);

    }

}
