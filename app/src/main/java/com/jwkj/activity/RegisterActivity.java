package com.jwkj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.network.NetManager;

import static com.jwkj.utils.Utils.isMobileNO;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    Button btn_register;
    ImageView iv_back;
    EditText et_account;
    TextView tv_login;
    String account;
//    TextView tv_warm;
    boolean isCN;
    boolean isDialogCanel;
    NormalDialog dialog;
    Context mContext;
    LinearLayout rl_ver;
    boolean needVer;
    int verTime = 0;
    EditText et_vercode;
    String ver_code;
    ImageView iv_ver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_register);
        initCompenet();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        rl_ver.setVisibility(View.GONE);
        iv_ver.setVisibility(View.GONE);
    }

    public void initCompenet() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        iv_ver = (ImageView) findViewById(R.id.iv_ver);
        iv_ver.setImageBitmap(Code.getInstance().getBitmap());
        iv_ver.setOnClickListener(this);
        et_vercode = (EditText) findViewById(R.id.et_vercode);
        rl_ver = (LinearLayout) findViewById(R.id.rl_ver);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        btn_register = (Button) findViewById(R.id.register);
        btn_register.setEnabled(false);
        btn_register.setBackgroundResource(R.drawable.bg_button_style_disable);
        et_account = (EditText) findViewById(R.id.et_account);
        et_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                tv_warm.setVisibility(View.GONE);
                if (s.length() > 0) {
                    btn_register.setEnabled(true);
                    btn_register.setBackgroundResource(R.drawable.bg_button_style);
                }
                if (s.length() == 0) {
                    btn_register.setEnabled(false);
                    btn_register.setBackgroundResource(R.drawable.bg_button_style_disable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        tv_warm = (TextView) findViewById(R.id.tv_warm);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                regist(v);
                break;
            case R.id.tv_login:
                finish();
                break;
            case R.id.iv_ver:
                iv_ver.setImageBitmap(Code.getInstance().getBitmap());
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }


    private void regist(View view) {
        Utils.hindKeyBoard(view);
        verTime++;
        if (verTime == 3) {
            rl_ver.setVisibility(View.VISIBLE);
            iv_ver.setVisibility(View.VISIBLE);
            return;
        }
        if (verTime>3) {
            rl_ver.setVisibility(View.VISIBLE);
            iv_ver.setVisibility(View.VISIBLE);
            ver_code = et_vercode.getText().toString();
            if (!ver_code.equalsIgnoreCase(Code.getInstance().getCode())) {

                T.showShort(mContext, R.string.input_correct_vercode);
                return;
            }
        }
        account = et_account.getText().toString();
            if (null == account || "".equals(account)) {
//                tv_warm.setVisibility(View.VISIBLE);
//                tv_warm.setText(R.string.emailorphone_null);
                T.showShort(mContext,R.string.emailorphone_null);
                return;
            }
            if (!Utils.isEmail(account) &&
                    !isMobileNO(account)) {
//                tv_warm.setVisibility(View.VISIBLE);
//                tv_warm.setText(R.string.input_emailorphone);
                T.showShort(mContext,R.string.phone_email_error);
                return;
            } else {
                getAccountExist();
            }
    }

    private void getAccountExist() {
        final String phone = et_account.getText().toString();

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
                T.showShort(mContext,R.string.time_out);
            }
        });
        dialog.setTimeOut(20000);
        isDialogCanel = false;
        dialog.showDialog();

//        new GetPhoneCodeTask("86", phone)
//                .execute();
        new GetAccountExistTask(phone).execute();
    }


    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_MAINREGISTER;
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
                case NetManager.GET_PHONE_CODE_SUCCESS:

                    if (isDialogCanel) {
                        return;
                    }
                    if (null != dialog) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.vercode_hassend);
                        Intent i = new Intent(mContext, RegisterByPhoneActivity.class);
                        i.putExtra("phone", PhoneNO);
                        startActivity(i);
                        finish();
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
                case NetManager.UNKNOWN_ERROR:
                    T.showShort(mContext,R.string.timeout);
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    return ;
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

    @Override
    protected void onPause() {
        super.onPause();
        verTime = 0 ;
    }

    private class GetAccountExistTask extends AsyncTask {
        String account;

        public GetAccountExistTask(String account) {
            this.account = account;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Utils.sleepThread(1000);
            return NetManager.getInstance(mContext).getAccountExist(account);
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            int result = (Integer) object;
            Log.e("wzytest","result"+result);
            switch (result) {
                case NetManager.REGISTER_PHONE_USED:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext,R.string.phone_number_used);
                    break;
                case NetManager.CONNECT_CHANGE:
                     new GetAccountExistTask(account).execute();
                    return ;
                case NetManager.UNKNOWN_ERROR:
                    T.showShort(mContext,R.string.timeout);
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    return ;
                case NetManager.REGISTER_EMAIL_USED:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    T.showShort(mContext,R.string.email_used);

                    break;
                case NetManager.GET_DEVICE_LIST_EMPTY:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (Utils.isEmail(account)) {
                        Intent intent = new Intent(RegisterActivity.this, RegisterByEmailActivity.class);
                        intent.putExtra("account", account);
                        startActivity(intent);
                    }
                    if (Utils.isMobileNO(account)) {
                        Intent intent = new Intent(RegisterActivity.this, RegisterByPhoneActivity.class);
                        intent.putExtra("phone", account);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
