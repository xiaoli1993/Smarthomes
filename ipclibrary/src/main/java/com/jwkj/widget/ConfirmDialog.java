package com.jwkj.widget;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.nuowei.ipclibrary.R;

/**
 * Created by wzy on 2016/10/11.
 */

public class ConfirmDialog extends BaseDialog {
    TextView tv_confirm ;
    String title;
    TextView tv_title ;
    View.OnClickListener onComfirmClickListener;

    public View.OnClickListener getOnComfirmClickListener() {
        return onComfirmClickListener;
    }

    public void setOnComfirmClickListener(View.OnClickListener onComfirmClickListener) {
        this.onComfirmClickListener = onComfirmClickListener;
    }


    public ConfirmDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_confirm);
        initUI();
    }

    public void setTitle(String title) {
        this.title = title;
        tv_title.setText(title);
    }

    public void setTitle(int titleRes) {
        tv_title.setText(titleRes);
    }
    public ConfirmDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_confirm);
        initUI();
    }
    public void setTxButton(String str){
        tv_confirm.setText(str);
    }

    public void initUI(){
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        tv_title = (TextView) findViewById(R.id.title);
        tv_confirm  = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onComfirmClickListener!=null){
                    onComfirmClickListener.onClick(v);
                }
            }
        });

    };

    public void setGravity(int gravity){
        tv_title.setGravity(gravity);
    }

}
