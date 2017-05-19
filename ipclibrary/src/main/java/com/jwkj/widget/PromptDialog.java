package com.jwkj.widget;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nuowei.ipclibrary.R;

/**
 * Created by Administrator on 2016/10/17.
 */
public class PromptDialog extends BaseDialog {
    private ImageView iv_close;
    private TextView tx_title;
    private ScrollView l_content;
    private Context context;
    String title = "";

    public PromptDialog(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_prompt_large);
        initUI();
    }

    public PromptDialog(Context context, int theme) {
        super(context, theme);
    }

    public void initUI() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        l_content = (ScrollView) findViewById(R.id.l_content);
        tx_title = (TextView) findViewById(R.id.tx_title);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setListener() {
    }

    public void setTitle(String title) {
        this.title = title;
        tx_title.setText(title);
    }

    private connectFailListener cListener;

    public void setconnectFailListener(connectFailListener cListener) {
        this.cListener = cListener;
    }

    public void addView(View view) {
        l_content.addView(view);
        TextView tx_know = (TextView) view.findViewById(R.id.tx_know);
        if (tx_know != null) {
            tx_know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        TextView tx_try_again = (TextView) view.findViewById(R.id.tx_try_again);
        if (tx_try_again != null) {
            tx_try_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cListener.onTryAgain();
                    dismiss();
                }
            });
        }
        TextView tx_try_wire = (TextView) view.findViewById(R.id.tx_try_wire);
        if (tx_try_wire != null) {
            tx_try_wire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cListener.onTryWird();
                    dismiss();
                }
            });

        }

    }

    public interface connectFailListener {
        void onTryAgain();

        void onTryWird();
    }
}
