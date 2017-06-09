package com.jwkj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import static com.nuowei.smarthome.R.drawable.bg_button_style;
import static com.nuowei.smarthome.R.id.iv_ver;

/**
 * create by wzy on 2016/10/11
 */

public class RegisterByPhoneActivity extends BaseActivity implements View.OnClickListener {
    ImageView iv_back;
    TextView et_account;
    EditText et_conmsg;
    String account;
    TextView backtime;
    LinearLayout rl_verCode;
    //    TextView tv_conmsg_invalid;
    int time = 60;
    //    TextView tv_conpwd_invalid;
//    TextView tv_pwd_invalid;
    TextView iv_refresh;
    boolean isDialogCanel;
    Context mContext;
    Handler handler = new Handler();
    EditText et_pwd;
    EditText et_conpwd;
    String pwd;
    String conmsg;
    Button btn_regist;
    String conpwd;
    String code;
    TextView tv_login;
    RelativeLayout rl_backtime;
    ImageView iv_refresh1;
    String verCode;
    int verTime = 0;
    boolean needVer;
    EditText et_vercode;
    ImageView iv_agree;
    boolean isAgree = true;
    private boolean hasGetver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_register_by_phone);
        initCompenet();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (time > 0) {
                    time--;
                    if (time == 0) {
                        setRefreshAble();
                    }
                    backtime.setText(time + "s");
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void setRefreshAble() {
        iv_refresh.setVisibility(View.VISIBLE);
        backtime.setVisibility(View.GONE);
    }

    private void setRefreshEnAble() {
        time = 60;
        iv_refresh.setVisibility(View.GONE);
        backtime.setText("60s");
        backtime.setVisibility(View.VISIBLE);

    }

    public void initCompenet() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        iv_agree = (ImageView) findViewById(R.id.iv_agree);
        iv_agree.setOnClickListener(this);
        rl_verCode = (LinearLayout) findViewById(R.id.rl_ver);
        rl_verCode.setVisibility(View.GONE);
        iv_refresh1 = (ImageView) findViewById(iv_ver);
        iv_refresh1.setImageBitmap(Code.getInstance().getBitmap());
        iv_refresh1.setOnClickListener(this);
        iv_refresh1.setVisibility(View.VISIBLE);
        account = getIntent().getExtras().getString("phone");
        rl_backtime = (RelativeLayout) findViewById(R.id.rl_backtime);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
//        tv_pwd_invalid = (TextView) findViewById(R.id.tv_pwd_invalid);
//        tv_conpwd_invalid = (TextView) findViewById(R.id.tv_conpwd_invalid);
//        et_pwd.addTextChangedListener(
//                new DismissTextWatcher<TextView>(tv_pwd_invalid)
//            );
        et_conpwd = (EditText) findViewById(R.id.et_conpwd);
//        et_conpwd.addTextChangedListener(
//                new DismissTextWatcher<TextView>(tv_conpwd_invalid));
        et_conmsg = (EditText) findViewById(R.id.et_conmsg);
        et_conmsg.requestFocus();
        et_conmsg.setOnClickListener(this);
//        et_conmsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    tv_conmsg_invalid.setVisibility(View.GONE);
//                } else {
//                    checkCode();
//                }
//            }
//        });
        btn_regist = (Button) findViewById(R.id.btn_login);
        btn_regist.setOnClickListener(this);
        et_conpwd.setOnClickListener(this);
        et_account = (TextView) findViewById(R.id.et_account);
        backtime = (TextView) findViewById(R.id.backtime);
        iv_refresh = (TextView) findViewById(R.id.iv_refresh);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        backtime.setOnClickListener(this);
        //tv_conmsg_invalid = (TextView) findViewById(R.id.tv_conmsg_invalid);
        if(Utils.isZh(mContext)){
            et_account.setText("+86-"+account);
        }else{
            et_account.setText(account);
        }
        et_vercode = (EditText) findViewById(R.id.et_vercode);
    }


    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_REGISTERBYPHONE;
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
            case R.id.iv_refresh:
                getPhoneCode();
                break;
            case R.id.et_conpwd:
                break;
            case R.id.btn_login:
                regist(v);
                break;
            case R.id.tv_login:
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
                break;
            case iv_ver:
                iv_refresh1.setImageBitmap(Code.getInstance().getBitmap());
                break;
            case R.id.iv_agree:
                if (isAgree) {
                    isAgree = !isAgree;
                    iv_agree.setImageResource(R.drawable.not_checked);
                    btn_regist.setEnabled(false);
                    btn_regist.setBackgroundResource(R.drawable.bg_button_style_disable);
                } else {
                    isAgree = !isAgree;
                    btn_regist.setEnabled(true);
                    btn_regist.setBackgroundResource(bg_button_style);
                    iv_agree.setImageResource(R.drawable.checked);
                }
                break;

        }
    }

    private void regist(View view) {
        Utils.hindKeyBoard(view);
        code = et_conmsg.getText().toString();
//        account = et_account.getText().toString();
        pwd = et_pwd.getText().toString();
        conpwd = et_conpwd.getText().toString();
        conmsg = et_conmsg.getText().toString();

        if (!hasGetver) {
            T.showShort(mContext, R.string.please_getver);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            T.showShort(mContext, R.string.input_vercode);
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            T.showShort(mContext, R.string.input_password);
            return;
        }
        if (TextUtils.isEmpty(conpwd)) {
            T.showShort(mContext, R.string.passwordagain_null);
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
                T.showShort(mContext, R.string.time_out);
            }
        });
        dialog.setTimeOut(20000);
        isDialogCanel = false;
        dialog.showDialog();
        checkCode();

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
                    break;
                case NetManager.REGISTER_SUCCESS:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext, R.string.regist_success);
                    Intent i = new Intent();
                    i.setClass(RegisterByPhoneActivity.this, LoginActivity.class);
                    i.setAction(Constants.Action.REPLACE_PHONE_LOGIN);
                    i.putExtra("username", PhoneNO);
                    i.putExtra("password",Pwd);
                    mContext.sendBroadcast(i);
                    startActivity(i);
                    break;
                case NetManager.REGISTER_EMAIL_USED:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.email_used);
                    }
                    break;
                case NetManager.REGISTER_EMAIL_FORMAT_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.email_format_error);
                    }
                    break;
                case NetManager.REGISTER_PASSWORD_NO_MATCH:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }

                    break;
                case NetManager.UNKNOWN_ERROR:
                    T.showShort(mContext, R.string.timeout);
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    break;
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

    private void getPhoneCode() {
        verTime++;
        if (verTime == 3) {
            needVer = true;
            rl_verCode.setVisibility(View.VISIBLE);
            return;
        }
        if (verTime > 3) {
            rl_verCode.setVisibility(View.VISIBLE);
            verCode = et_vercode.getText().toString();
            if (!verCode.equalsIgnoreCase(Code.getInstance().getCode())) {
                T.showShort(mContext, R.string.vercode_error);
                return;
            }
        }
//        final String phone = et_account.getText().toString();

        dialog = new NormalDialog(this, this.getResources().getString(
                R.string.waiting_verify_code), "", "", "");
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
                T.showShort(mContext, R.string.time_out);
            }
        });
        dialog.setTimeOut(20000);
        isDialogCanel = false;
        dialog.showDialog();

        new GetPhoneCodeTask("86", account)
                .execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        verTime = 0;
    }

    public void checkCode() {

//        String account = et_account.getText().toString();
        String code = et_conmsg.getText().toString();
        if (code == null || code.equals("")) {
            T.showShort(mContext, R.string.input_vf_code);
            //tv_conmsg_invalid.setVisibility(View.VISIBLE);

            return;
        }
//        dialog = new NormalDialog(this, this.getResources().getString(
//                R.string.verifing), "", "", "");
//        dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                // TODO Auto-generated method stub
//                isDialogCanel = true;
//            }
//
//        });
//        isDialogCanel = false;
//        dialog.showDialog();
        new VerifyCodeTask("86", account, code).execute();
    }

    class VerifyCodeTask extends AsyncTask {
        String countryCode;
        String phoneNO;
        String code;

        public VerifyCodeTask(String countryCode, String phoneNO, String code) {
            this.countryCode = countryCode;
            this.phoneNO = phoneNO;
            this.code = code;
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            Utils.sleepThread(1000);
            return NetManager.getInstance(mContext).verifyPhoneCode(
                    countryCode, phoneNO, code);
        }

        @Override
        protected void onPostExecute(Object object) {
            // TODO Auto-generated method stub
            int result = (Integer) object;
            if (Utils.isTostCmd(result)) {
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (!isDialogCanel) {
                    T.showLong(mContext, Utils.GetToastCMDString(result));
                }
                return;
            }
            switch (result) {

                case NetManager.SESSION_ID_ERROR:
                    Intent relogin = new Intent();
                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(relogin);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new VerifyCodeTask(countryCode, phoneNO, code).execute();
                    return;
                case NetManager.VERIFY_CODE_SUCCESS:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        new RegisterTask("1", "", "86", account, pwd,
                                conpwd, code, "1").execute();
                    }
                    break;
                case NetManager.VERIFY_CODE_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    //tv_conmsg_invalid.setVisibility(View.VISIBLE);
                    T.showShort(mContext, R.string.vercode_error);
                    break;
                case NetManager.VERIFY_CODE_TIME_OUT:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.vfcode_timeout);
                    }
                    break;
            }
        }

    }


    class GetPhoneCodeTask extends AsyncTask {
        String CountryCode;
        String PhoneNO;

        public GetPhoneCodeTask(String CountryCode, String PhoneNO) {
            this.CountryCode = CountryCode;
            this.PhoneNO = PhoneNO;
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            Utils.sleepThread(1000);
            return NetManager.getInstance(mContext).getPhoneCode(CountryCode,
                    PhoneNO);
        }

        @Override
        protected void onPostExecute(Object object) {
            // TODO Auto-generated method stub
            int result = (Integer) object;
            if (null != dialog) {
                dialog.dismiss();
                dialog = null;
            }
            if (Utils.isTostCmd(result)) {
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (!isDialogCanel) {
                    T.showLong(mContext, Utils.GetToastCMDString(result));
                }
                return;
            }
            switch (result) {

                case NetManager.SESSION_ID_ERROR:
                    Intent relogin = new Intent();
                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(relogin);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new GetPhoneCodeTask(CountryCode, PhoneNO).execute();
                    return;
                case NetManager.UNKNOWN_ERROR:
                    T.showShort(mContext, R.string.timeout);
                    return;
                case NetManager.SEND_SHORTMSG_FAILED:
                    T.showShort(mContext, R.string.send_msg_failed);
                    return;
                case NetManager.GET_PHONE_CODE_SUCCESS:
                    hasGetver = true;
                    if (isDialogCanel) {
                        return;
                    }
                    if (null != dialog) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.vercode_hassend);
                        setRefreshEnAble();
                    }
                    break;
                case NetManager.GET_PHONE_CODE_TOO_TIMES:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.get_phone_code_too_times);
                    }
                    break;
                case NetManager.GET_PHONE_CODE_PHONE_FORMAT_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.phone_format_error);
                    }
                    break;
                case NetManager.GET_PHONE_CODE_PHONE_USED:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.phone_number_used);
                    }
                    break;
                default:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.registerfail);
                    }
                    break;
            }
        }
    }
}
