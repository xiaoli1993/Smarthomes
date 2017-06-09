package com.jwkj.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

/**
 * Created by Administrator on 2016/9/27.
 */

public class InputPasswordDialog extends Dialog {
    private EditText et_pwd;
    private TextView tx_cancel,tx_ok;
    private String contactId="";
    private TextView dialogtitle;
    private TextView tx_device_id;
    public static final int PASSWORD_DIALOG=0;
    public static final int EDITOR_DIALOG=1;
    public int type=PASSWORD_DIALOG;
    public InputPasswordDialog(Context context) {
        super(context, R.style.CustomnewInputDialog);
    }

    public InputPasswordDialog(Context context, int theme) {
        super(context, theme);
    }

    protected InputPasswordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_password);
        initUI();
    }

    /**
     *
     * @param type 框类型
     *   PASSWORD_DIALOG：错误密码框 EDITOR_DIALOG：编辑设备名框 默认为PASSWORD_DIALOG
     */
    public void setType(int type){
        this.type=type;
    }
    public void initUI(){
        et_pwd=(EditText)findViewById(R.id.et_pwd);
        tx_cancel=(TextView)findViewById(R.id.tx_cancel);
        tx_ok=(TextView)findViewById(R.id.tx_ok);
        dialogtitle=(TextView)findViewById(R.id.et_title);
        tx_device_id=(TextView)findViewById(R.id.tx_device_id);
        tx_device_id.setText("ID: "+contactId);
        if(type==EDITOR_DIALOG){
            tx_device_id.setVisibility(View.VISIBLE);
        }else{
            tx_device_id.setVisibility(View.GONE);
        }
        tx_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hindKeyBoard(et_pwd);
                if (inputPasswordClickListener!=null){
                    inputPasswordClickListener.onCancelClick();
                }
            }
        });
        tx_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hindKeyBoard(et_pwd);
                if (inputPasswordClickListener!=null){
                    String password=et_pwd.getText().toString().trim();
                    inputPasswordClickListener.onOkClick(contactId,password);
                }
            }
        });

    }

    /**
     *
     * @param title 标题textview 文本
     * @param textContent 输入框 文本
     * @param textHint 输入框 在未输入时的文本
     * @param textlimit 输入框 限制的字符数量
     * @param oktext 确定按钮 文本
     * @param canceltext 取消按钮 文本
     */
    public void settingDialog(String title,String textContent, String textHint, int textlimit,
                              String oktext,String canceltext,int inputType){
        if (inputType!=0){
            et_pwd.setInputType(inputType);
        }
        if (title!=null){
            dialogtitle.setText(title);
        }
        if (textlimit>0){
            et_pwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(textlimit)});
        }
        if (textContent!=null){
            textContent=textContent.substring(0,Math.min(textlimit,textContent.length()));
            et_pwd.setText(textContent);
            et_pwd.setSelection(Math.min(textContent.length(),textlimit));
        }
        if (textHint!=null){
            et_pwd.setHint(textHint);
        }
        if (oktext!=null){
            tx_ok.setText(oktext);
        }
        if (canceltext!=null){
            tx_cancel.setText(canceltext);
        }

    }

    public void setContactId(String contactId){
        this.contactId=contactId;
    }
    private InputPasswordClickListener inputPasswordClickListener;
    public void setInputPasswordClickListener(InputPasswordClickListener inputPasswordClickListener){
       this.inputPasswordClickListener=inputPasswordClickListener;
    }
    public interface InputPasswordClickListener{
        void onCancelClick();
        //改造后 这个方法在不是修改密码功能时cantactid会是空字符 pwd为文本框的内容
        void onOkClick(String contactId,String pwd);
    }
}
