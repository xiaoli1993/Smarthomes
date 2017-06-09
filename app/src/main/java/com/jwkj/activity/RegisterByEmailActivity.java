package com.jwkj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwkj.global.Constants;
import com.jwkj.utils.Code;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.ConfirmOrCancelDialog;
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.RegisterResult;

import org.json.JSONObject;


public class RegisterByEmailActivity extends BaseActivity implements View.OnClickListener {
    Button btn_regist;
    TextView tv_login ;
    ImageView iv_back;
    EditText et_pwd;
    EditText et_conpwd;
    String pwd;
    String conpwd;
//    TextView tv_conpwd_invalid;
//    TextView tv_pwd_invalid;
    boolean isDialogCanel;
    EditText tv_account;
    String email_str;
    Context mContext;
    int verTime = 0;
    boolean needVer;
    LinearLayout rl_verCode;
    EditText et_vercode;
    String verCode;
    ImageView iv_ver;
    ImageView iv_agree;
    boolean isAgree = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_register_by_email);
        initCompenet();
    }

    private void initCompenet() {
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        iv_ver = (ImageView) findViewById(R.id.iv_ver);
        iv_ver.setImageBitmap(Code.getInstance().getBitmap());
        iv_ver.setVisibility(View.VISIBLE);
        iv_ver.setOnClickListener(this);
        rl_verCode = (LinearLayout) findViewById(R.id.rl_ver);
        et_vercode = (EditText) findViewById(R.id.et_vercode);
        tv_account = (EditText) findViewById(R.id.et_account);
        if (getIntent() != null) {
            if (null != getIntent().getExtras() && !"".equals(getIntent().getExtras().getString("account"))) {
                String account = getIntent().getExtras().getString("account");
                tv_account.setText(account);
                tv_account.setEnabled(false);
                tv_account.setFocusable(false);
            }
        }
        btn_regist = (Button) findViewById(R.id.btn_login);
        btn_regist.setOnClickListener(this);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_conpwd = (EditText) findViewById(R.id.et_conpwd);
//        tv_conpwd_invalid = (TextView) findViewById(R.id.tv_conpwd_invalid);
//        tv_pwd_invalid = (TextView) findViewById(R.id.tv_pwd_invalid);
        iv_agree = (ImageView) findViewById(R.id.iv_agree);
        iv_agree.setOnClickListener(this);
    }


    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_REGISTERBYEMAIL;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                final ConfirmOrCancelDialog confirmOrCancelDialog = new ConfirmOrCancelDialog(mContext);
                confirmOrCancelDialog.setOnNoClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmOrCancelDialog.dismiss();
                        return;
                    }
                });
                confirmOrCancelDialog.setOnYesClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, LoginActivity.class);
                        startActivity(i);
                        return;
                    }
                });
                confirmOrCancelDialog.show();
                break;
            case R.id.tv_login:
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
                break;
            case R.id.btn_login:
                regist(v);
                break;
            case R.id.iv_ver:
                iv_ver.setImageBitmap(Code.getInstance().getBitmap());
                break;
            case R.id.iv_agree:
                if (isAgree) {
                    isAgree = !isAgree;
                    iv_agree.setImageResource(R.drawable.not_checked);
                    btn_regist.setEnabled(false);
                    btn_regist.setBackgroundResource(R.drawable.bg_button_style_disable);
                    // btn_regist.setBackgroundResource(R.color.bg_btn_enable);
                } else {
                    isAgree = !isAgree;
                    btn_regist.setEnabled(true);
                    btn_regist.setBackgroundResource(R.drawable.bg_button_style);
                    iv_agree.setImageResource(R.drawable.checked);
                }
                break;
        }
    }

    private void regist(View view) {
        Utils.hindKeyBoard(view);
        verTime++;
        if (verTime == 3) {
            needVer = true;
            rl_verCode.setVisibility(View.VISIBLE);
            iv_ver.setVisibility(View.VISIBLE);
            return;
        }
        if (verTime > 3) {
            rl_verCode.setVisibility(View.VISIBLE);
            iv_ver.setVisibility(View.VISIBLE);
            verCode = et_vercode.getText().toString();
            if (!verCode.equalsIgnoreCase(Code.getInstance().getCode())) {
                T.showShort(mContext, R.string.vercode_error);
                return;
            }
        }
        pwd = et_pwd.getText().toString();
        conpwd = et_conpwd.getText().toString();
        email_str = tv_account.getText().toString();
        if(TextUtils.isEmpty(pwd)){
            T.showShort(mContext,R.string.input_password);
            return;
        }
        if(TextUtils.isEmpty(conpwd)){
            T.showShort(mContext,R.string.passwordagain_null);
            return;
        }
        if (pwd.length() > 30 || pwd.length() < 6 || Utils.isWeakPassword(pwd)) {

//            tv_pwd_invalid.setVisibility(View.VISIBLE);
            T.showShort(mContext, R.string.pwd_format_error);
            return;
        }
        if (!pwd.equals(conpwd)) {
//            tv_conpwd_invalid.setVisibility(View.VISIBLE);
            T.showShort(mContext, R.string.differentpassword);
            return;
        }
        dialog = new NormalDialog(this, this.getResources().getString(
                R.string.registering), "", "", "");
        dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                isDialogCanel = true;
            }

        });
        dialog.setOnNormalDialogTimeOutListner(new NormalDialog.OnNormalDialogTimeOutListner() {
            @Override
            public void onTimeOut() {
                T.showShort(mContext,R.string.time_out);
            }
        });
        dialog.setTimeOut(20000);
        isDialogCanel = false;
        dialog.showDialog();
        new RegisterTask("1", email_str, "", "", pwd,
                conpwd, "", "1").execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        verTime = 0;
    }

    class RegisterTask extends AsyncTask {
        String VersionFlag;
        String Email;
        String CountryCode;
        String PhoneNO;
        String Pwd;
        String RePwd;
        String VerifyCode;
        String IgnoreSafeWarning;

        public RegisterTask(String VersionFlag, String Email,
                            String CountryCode, String PhoneNO, String Pwd, String RePwd,
                            String VerifyCode, String IgnoreSafeWarning) {
            this.VersionFlag = VersionFlag;
            this.Email = Email;
            this.CountryCode = CountryCode;
            this.PhoneNO = PhoneNO;
            this.Pwd = Pwd;
            this.RePwd = RePwd;
            this.VerifyCode = VerifyCode;
            this.IgnoreSafeWarning = IgnoreSafeWarning;
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            Utils.sleepThread(1000);
            return NetManager.getInstance(mContext).register(VersionFlag, Email,
                    CountryCode, PhoneNO, Pwd, RePwd, VerifyCode,
                    IgnoreSafeWarning);
        }

        @Override
        protected void onPostExecute(Object object) {
            // TODO Auto-generated method stub
            RegisterResult result = NetManager
                    .createRegisterResult((JSONObject) object);
            if (Utils.isTostCmd(Integer.parseInt(result.error_code))) {
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (!isDialogCanel) {
                    T.showLong(mContext, Utils.GetToastCMDString(Integer.parseInt(result.error_code)));
                }
                return;
            }
            switch (Integer.parseInt(result.error_code)) {
                case NetManager.SESSION_ID_ERROR:
                    Intent relogin = new Intent();
                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(relogin);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new RegisterTask(VersionFlag, Email, CountryCode, PhoneNO, Pwd,
                            RePwd, VerifyCode, IgnoreSafeWarning).execute();
                    return;
                case NetManager.REGISTER_SUCCESS:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext,R.string.regist_success);
                        startActivity(new Intent(RegisterByEmailActivity.this, LoginActivity.class));
                        Intent i = new Intent();
                        i.setAction(Constants.Action.REPLACE_EMAIL_LOGIN);
                        i.putExtra("username", Email);
                        i.putExtra("password",Pwd);
                        mContext.sendBroadcast(i);
                    }

                    break;
                case NetManager.REGISTER_EMAIL_USED:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                      T.showShort(mContext,R.string.email_used);
                    }
                    break;
                case NetManager.REGISTER_EMAIL_FORMAT_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext,R.string.email_format_error);
                    }
                    break;
                case NetManager.UNKNOWN_ERROR:
                    T.showShort(mContext,R.string.timeout);
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    break ;
                default:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, getResources().getString(R.string.operator_error)+result.error_code);
                    }
                    break;
            }
        }

    }
}
