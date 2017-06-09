package com.jwkj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
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
import com.p2p.core.network.CheckPhoneVKeyResult;
import com.p2p.core.network.GetAccountByPhoneNOResult;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.RegisterResult;

import org.json.JSONObject;


public class RetpwdByPhoneActivity extends BaseActivity implements View.OnClickListener {
   private  ImageView iv_back;
   private  TextView et_account;
   private  EditText et_conmsg;
   private  String account;
   private  TextView backtime;
   private  LinearLayout rl_verCode;
   private  boolean hasGetver;
   private  int time = 120;
   private  TextView iv_refresh;
   private  boolean isDialogCanel;
   private  Context mContext;
   private  Handler handler = new Handler();
   private  EditText et_pwd;
   private  EditText et_conpwd;
   private  String pwd;
   private  String conmsg;
   private  Button btn_regist;
   private  String conpwd;
   private  String code;
   private  TextView tv_login;
   private  RelativeLayout rl_backtime;
   private  ImageView iv_refresh1;
   private  String verCode;
   private  int loginFlag = 0;
   private  boolean needVer;
   private  EditText et_vercode;
   private  boolean isAgree = true;
   private  GetAccountByPhoneNOResult accountByPhoneNOResult;
   private  CheckPhoneVKeyResult checkPhoneVKeyResult;
   private  String accountID;
   private  String vkey;
   private  String accountPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_retpwd_by_phone);
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
        time = 120;
        iv_refresh.setVisibility(View.GONE);
        backtime.setText("120s");
        backtime.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public void initCompenet() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        rl_verCode = (LinearLayout) findViewById(R.id.rl_ver);
        rl_verCode.setVisibility(View.GONE);
        iv_refresh1 = (ImageView) findViewById(R.id.iv_ver);
        iv_refresh1.setImageBitmap(Code.getInstance().getBitmap());
        iv_refresh1.setOnClickListener(this);
        iv_refresh1.setVisibility(View.VISIBLE);
        account = getIntent().getExtras().getString("phone");
        rl_backtime = (RelativeLayout) findViewById(R.id.rl_backtime);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_conpwd = (EditText) findViewById(R.id.et_conpwd);
        et_conmsg = (EditText) findViewById(R.id.et_conmsg);
        et_conmsg.requestFocus();
        et_conmsg.setOnClickListener(this);
        et_conmsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //tv_conmsg_invalid.setVisibility(View.GONE);
                } else {

                }
            }
        });
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
                confirmOrCancelDialog.setTitle(R.string.give_up_retpwd);
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
                setRefreshEnAble();
                new GetAccountByPhoneNOTask("86", account).execute();
                break;
            case R.id.btn_login:
                resetPwd(v);
                break;
            case R.id.tv_login:
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
                break;
            case R.id.iv_ver:
                iv_refresh1.setImageBitmap(Code.getInstance().getBitmap());
                break;
            default:
                break;

        }
    }


    class GetAccountByPhoneNOTask extends AsyncTask {
        String CountryCode;
        String PhoneNO;

        public GetAccountByPhoneNOTask(String CountryCode, String PhoneNO) {
            this.CountryCode = CountryCode;
            this.PhoneNO = PhoneNO;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Utils.sleepThread(1000);
            return NetManager.getInstance(MyApplication.app).getAccountByPhoneNO(
                    CountryCode, PhoneNO);
        }

        @Override
        protected void onPostExecute(Object object) {
            accountByPhoneNOResult = NetManager
                    .creatAccountByPhoneNOResult((JSONObject) object);
            int error_code = Integer.parseInt(accountByPhoneNOResult
                    .getError_code());
            switch (error_code) {
                case NetManager.LOGIN_USER_UNEXIST:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null ;
                    }
                    T.showShort(mContext,
                            R.string.account_no_exist);
                    break;
                case NetManager.SESSION_ID_ERROR:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null ;
                    }
                    Intent relogin = new Intent();
                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(relogin);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new GetAccountByPhoneNOTask(CountryCode, PhoneNO).execute();
                    break;
                case NetManager.GET_PHONE_CODE_SUCCESS:
                    T.showShort(mContext,
                            R.string.vercode_hassend);
                    hasGetver = true;
                    accountID = accountByPhoneNOResult.getID();
                    vkey = accountByPhoneNOResult.getVKey();
                    accountPhone = accountByPhoneNOResult.getPhoneNO();
                    break;
                case NetManager.GET_PHONE_CODE_TOO_TIMES:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null ;
                    }
                    T.showShort(mContext, R.string.get_phone_code_too_times);
                    break;
                case NetManager.SEND_MSG_FAILED:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null ;
                    }
                    T.showShort(mContext, R.string.getver_fail);
                    break;
                case NetManager.GET_PHONE_CODE_PHONE_FORMAT_ERROR:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null ;
                    }
                    T.showShort(mContext, R.string.phone_format_error);
                    break;
                case NetManager.UNKNOWN_ERROR:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null ;
                    }
                    T.showShort(mContext, R.string.timeout);
                    break;
                default:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null ;
                    }
                    T.showShort(mContext, R.string.fail);
                    // Utils.showPromptDialog(mContext, R.string.prompt, R.string.fail);
                    break;
            }
        }
    }

    public void checkCode() {
        String code = et_conmsg.getText().toString();
        new CheckPhoneVKeyTask(accountID, vkey, "86", account, code).execute();
    }

    class CheckPhoneVKeyTask extends AsyncTask {
        private String ID;
        private String VKey;
        private String CountryCode;
        private String PhoneNO;
        private String PhoneVerifyCode;

        public CheckPhoneVKeyTask(String ID, String VKey, String countryCode,
                                  String phoneNO, String phoneVerifyCode) {
            this.ID = ID;
            this.VKey = VKey;
            this.CountryCode = countryCode;
            this.PhoneNO = phoneNO;
            this.PhoneVerifyCode = phoneVerifyCode;
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            Utils.sleepThread(1000);
            return NetManager.getInstance(MyApplication.app).getCheckPhoneVKey(ID,
                    VKey, CountryCode, PhoneNO, PhoneVerifyCode);
        }

        @Override
        protected void onPostExecute(Object object) {
            // TODO Auto-generated method stub
            checkPhoneVKeyResult = NetManager
                    .setCheckPhoneVKeyResult((JSONObject) object);
            switch (Integer.parseInt(checkPhoneVKeyResult.getError_code())) {
                case NetManager.SESSION_ID_ERROR:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null;
                    }
                    Intent relogin = new Intent();
                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(relogin);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new CheckPhoneVKeyTask(ID, VKey, CountryCode, PhoneNO,
                            PhoneVerifyCode).execute();
                    return;
                case NetManager.VERIFY_CODE_SUCCESS:
                    new ResetPasswordTask(accountID, vkey, pwd, conpwd).execute();
                    break;
                case NetManager.VERIFY_CODE_ERROR:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext, R.string.dor_vfcode_error);
                    break;
                case NetManager.VERIFY_CODE_TIME_OUT:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext, R.string.dor_vfcode_timeout);
                    break;
                case NetManager.UNKNOWN_ERROR:
                    T.showShort(mContext, R.string.timeout);
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    break;
                default:
                    if(dialog!=null){
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext, R.string.dor_operator_error);
                    break;
            }
        }
    }

//    public void checkCode() {
//        String account = et_account.getText().toString();
//        String code = et_conmsg.getText().toString();
//        if (code == null || code.equals("")) {
//            T.showShort(mContext, R.string.input_vf_code);
//            //tv_conmsg_invalid.setVisibility(View.VISIBLE);
//            return;
//        }
//        new VerifyCodeTask("86", account, code).execute();
//    }

//    class VerifyCodeTask extends AsyncTask {
//        String countryCode;
//        String phoneNO;
//        String code;
//
//        public VerifyCodeTask(String countryCode, String phoneNO, String code) {
//            this.countryCode = countryCode;
//            this.phoneNO = phoneNO;
//            this.code = code;
//        }
//
//        @Override
//        protected Object doInBackground(Object... params) {
//            // TODO Auto-generated method stub
//            Utils.sleepThread(1000);
//            return NetManager.getInstance(mContext).verifyPhoneCode(
//                    countryCode, phoneNO, code);
//        }
//
//        @Override
//        protected void onPostExecute(Object object) {
//            // TODO Auto-generated method stub
//            int result = (Integer) object;
//            if (Utils.isTostCmd(result)) {
//                if (null != dialog && dialog.isShowing()) {
//                    dialog.dismiss();
//                    dialog = null;
//                }
//                if (!isDialogCanel) {
//                    T.showLong(mContext, Utils.GetToastCMDString(result));
//                }
//                return;
//            }
//            switch (result) {
//
//                case NetManager.SESSION_ID_ERROR:
//                    Intent relogin = new Intent();
//                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
//                    MyApp.app.sendBroadcast(relogin);
//                    break;
//                case NetManager.CONNECT_CHANGE:
//                    new VerifyCodeTask(countryCode, phoneNO, code).execute();
//                    return;
//                case NetManager.VERIFY_CODE_SUCCESS:
//                    if (dialog != null) {
//                        dialog.dismiss();
//                        dialog = null;
//                    }
//                    if (!isDialogCanel) {
////                        Intent i = new Intent(mContext, RegisterActivity2.class);
////                        i.putExtra("phone", phone);
////                        i.putExtra("count", count);
////                        i.putExtra("code", code);
////                        startActivity(i);
////                        finish();
//                        //tv_conmsg_invalid.setVisibility(View.GONE);
//                    }
//                    break;
//                case NetManager.VERIFY_CODE_ERROR:
//                    if (dialog != null) {
//                        dialog.dismiss();
//                        dialog = null;
//                    }
//                    //tv_conmsg_invalid.setVisibility(View.VISIBLE);
//                    T.showShort(mContext, R.string.vercode_error);
//                    break;
//                case NetManager.VERIFY_CODE_TIME_OUT:
//                    if (dialog != null) {
//                        dialog.dismiss();
//                        dialog = null;
//                    }
//                    if (!isDialogCanel) {
//                        T.showShort(mContext, R.string.vfcode_timeout);
//                    }
//                    break;
//                case NetManager.UNKNOWN_ERROR:
//                    T.showShort(mContext, R.string.timeout);
//                    if (dialog != null) {
//                        dialog.dismiss();
//                        dialog = null;
//                    }
//                    break;
//            }
//        }
//
//    }

    public void resetPwd(View view) {
        Utils.hindKeyBoard(view);
//        account = et_account.getText().toString();
        pwd = et_pwd.getText().toString();
        conpwd = et_conpwd.getText().toString();
        conmsg = et_conmsg.getText().toString();
        if (!hasGetver) {
            T.showShort(mContext, R.string.please_getver);
            return;
        }
        if (TextUtils.isEmpty(conmsg)) {
            T.showShort(mContext, R.string.input_vercode);
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            T.showShort(mContext, R.string.password_null);
            return;
        }
        if (TextUtils.isEmpty(conpwd)) {
            T.showShort(mContext, R.string.passwordagain_null);
            return;
        }
        if (pwd.length() > 18 || pwd.length() < 6 || Utils.isWeakPassword(pwd)) {
//            tv_pwd_invalid.setVisibility(View.VISIBLE);
            T.showShort(mContext, R.string.pwd_format_error);
            return;
        }
        if (!pwd.equals(conpwd)) {
            T.showShort(mContext, R.string.differentpassword);
//            tv_conpwd_invalid.setVisibility(View.VISIBLE);
            return;
        }
        dialog = new NormalDialog(mContext, this.getResources().getString(
                R.string.getvering), "", "", "");
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


    class ResetPasswordTask extends AsyncTask {
        String ID;
        String VKey;
        String NewPwd;
        String ReNewPwd;

        public ResetPasswordTask(String ID, String VKey, String newPwd,
                                 String reNewPwd) {
            this.ID = ID;
            this.VKey = VKey;
            NewPwd = newPwd;
            ReNewPwd = reNewPwd;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Utils.sleepThread(1000);
            return NetManager.getInstance(MyApplication.app).resetPwd(ID, VKey, NewPwd,
                    ReNewPwd);
        }

        @Override
        protected void onPostExecute(Object object) {
            // TODO Auto-generated method stub
            RegisterResult result = NetManager
                    .createRegisterResult((JSONObject) object);
            switch (Integer.parseInt(result.error_code)) {
                case NetManager.SESSION_ID_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    Intent relogin = new Intent();
                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(relogin);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new ResetPasswordTask(ID, VKey, NewPwd, ReNewPwd).execute();
                    return;
                case NetManager.REGISTER_SUCCESS:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext, R.string.reset_pwd_success);
                    Intent i = new Intent();
                    i.setClass(RetpwdByPhoneActivity.this, LoginActivity.class);
                    i.setAction(Constants.Action.REPLACE_PHONE_LOGIN);
                    i.putExtra("username", account);
                    i.putExtra("password", NewPwd);
                    startActivity(i);
                    mContext.sendBroadcast(i);
                    break;
                case NetManager.LOGIN_USER_UNEXIST:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext, R.string.account_no_exist);
                    break;
                case NetManager.REGISTER_PASSWORD_NO_MATCH:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext, R.string.password_no_match);
                    break;
                case NetManager.UNKNOWN_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext, R.string.timeout);
                    break;
                default:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.dor_operator_error);
                    }
                    break;
            }
        }
    }
}
