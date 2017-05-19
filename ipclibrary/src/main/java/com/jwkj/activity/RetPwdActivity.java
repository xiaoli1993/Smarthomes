package com.jwkj.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.global.Constants;
import com.jwkj.global.MyApp;
import com.jwkj.utils.LanguageUtils;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.ConfirmDialog;
import com.jwkj.widget.ConfirmOrCancelDialog;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.network.GetAccountByPhoneNOResult;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.RegisterResult;
import com.nuowei.ipclibrary.R;

import org.json.JSONObject;


public class RetPwdActivity extends BaseActivity implements View.OnClickListener {
    EditText et_account;
    Button btn_next;
    String account;
    TextView tv_login;
    Context mContext;
    NormalDialog normalDialog;
    boolean isDialogCanel;
    GetAccountByPhoneNOResult accountByPhoneNOResult;
    String accountID;
    String vkey;
    String accountPhone;
    ImageView iv_back;
    boolean isCN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_retpwd);
        mContext = this;
        initCompenet();
    }

    private void initCompenet() {
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setEnabled(false);
        btn_next.setBackgroundResource(R.drawable.bg_button_style_disable);
        et_account = (EditText) findViewById(R.id.et_account);
        et_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btn_next.setEnabled(true);
                    btn_next.setBackgroundResource(R.drawable.bg_button_style);
                }
                if (s.length() == 0) {
                    btn_next.setEnabled(false);
                    btn_next.setBackgroundResource(R.drawable.bg_button_style_disable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_next.setOnClickListener(this);
        if (!LanguageUtils.isLanguage("CN", this)) {
            et_account.setHint(R.string.input_email);
        } else {
            isCN = true;
        }
    }

    @Override
    public int getActivityInfo() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_login) {
            finish();

        } else if (i == R.id.btn_next) {
            retPwd(v);

        } else if (i == R.id.iv_back) {
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
                    finish();
                }
            });
            confirmOrCancelDialog.show();

        }
    }

    private void retPwd(View view) {
        Utils.hindKeyBoard(view);
        account = et_account.getText().toString();
        // new GetAccountExistTask(account).execute();


        if (isCN) {
            if ("".equals(account) || null == account) {
                T.showShort(mContext, R.string.emailorphone_null);
                return;
            } else {
                if (!Utils.isEmail(account) && !Utils.isMobileNO(account)) {
                    T.showShort(mContext, R.string.phone_email_error);
                    return;
                }
                if (Utils.isEmail(account)) {
                    checkEmail();
                } else {
//                    Intent intent = new Intent();
//                    intent.putExtra("phone", account);
//                    intent.setClass(mContext, RetpwdByPhoneActivity.class);
//                    startActivity(intent);
                    normalDialog = new NormalDialog(mContext, "", "", "", "");
                    normalDialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
                    normalDialog
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    isDialogCanel = true;
                                }
                            });
                    isDialogCanel = false;
                    normalDialog.showDialog();
                    new GetAccountExistTask(account).execute();
                }
            }
        } else {
            if ("".equals(account) || null == account) {
                T.showShort(mContext, R.string.email_null);
                return;
            } else {
                if (!Utils.isEmail(account)) {
                    T.showShort(mContext, R.string.email_format_error);
                    return;
                }
                if (Utils.isEmail(account)) {
                    checkEmail();
                }
            }
        }
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
            switch (result) {
                case NetManager.REGISTER_PHONE_USED:
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("phone", account);
                    intent.setClass(mContext, RetpwdByPhoneActivity.class);
                    startActivity(intent);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new GetAccountExistTask(account).execute();
                    return;
                case NetManager.UNKNOWN_ERROR:
                    T.showShort(mContext, R.string.timeout);
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }
                    return;
                case NetManager.REGISTER_EMAIL_USED:
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }


                case NetManager.GET_DEVICE_LIST_EMPTY:
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }
                    if (!isDialogCanel) {
//                        final ConfirmDialog confirmDialog = new ConfirmDialog(mContext);
//                        confirmDialog.setTitle(R.string.account_no_exist);
//                        confirmDialog.show();
//                        confirmDialog.setOnComfirmClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                confirmDialog.dismiss();
//                            }
//                        });
                        T.showShort(mContext,R.string.account_no_exist);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void checkEmail() {
        account = et_account.getText().toString();
        normalDialog = new NormalDialog(mContext, mContext.getResources()
                .getString(R.string.send_email), "", "", "");
        normalDialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
        normalDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        isDialogCanel = true;
                    }
                });
        normalDialog.setOnNormalDialogTimeOutListner(new NormalDialog.OnNormalDialogTimeOutListner() {
            @Override
            public void onTimeOut() {
                T.showShort(mContext,R.string.time_out);
            }
        });
        normalDialog.setTimeOut(20000);
        isDialogCanel = false;
        normalDialog.showDialog();
        new EmailBackTask(account).execute();
    }

    class EmailBackTask extends AsyncTask {
        String email;

        public EmailBackTask(String email) {
            this.email = email;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Utils.sleepThread(1000);
            return NetManager.getInstance(MyApp.app).sendEmail(email);
        }

        @Override
        protected void onPostExecute(Object object) {
            RegisterResult result = NetManager
                    .createRegisterResult((JSONObject) object);
            switch (Integer.parseInt(result.error_code)) {
                case NetManager.SESSION_ID_ERROR:
                    Intent relogin = new Intent();
                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApp.app.sendBroadcast(relogin);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new EmailBackTask(email).execute();
                    return;
                case NetManager.REGISTER_SUCCESS:
                    if (normalDialog != null) {

                        normalDialog.dismiss();
                        normalDialog = null;
                        ConfirmDialog emailConfirmDialog = new ConfirmDialog(mContext);
                        emailConfirmDialog.setOnComfirmClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(RetPwdActivity.this, LoginActivity.class));
                                Intent i = new Intent();
                                i.setAction(Constants.Action.REPLACE_EMAIL_LOGIN);
                                i.putExtra("username", email);
                                mContext.sendBroadcast(i);
                            }
                        });
                        emailConfirmDialog.show();
                    }

                    break;
                case NetManager.REGISTER_EMAIL_USED:
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext,R.string.email_used);
                    }
                    break;
                case NetManager.REGISTER_EMAIL_FORMAT_ERROR:
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext,R.string.email_format_error);
                    }
                    break;
                case NetManager.REGISTER_PASSWORD_NO_MATCH:
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }

                    break;
                case NetManager.LOGIN_USER_UNEXIST:
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.email_not_found);
                    }
                    break;
                case NetManager.UNKNOWN_ERROR:
                    T.showShort(mContext,R.string.time_out);
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    break ;
                default:
                    if (normalDialog != null) {
                        normalDialog.dismiss();
                        normalDialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.dor_operator_error);
                    }
                    break;
            }
        }

    }
}
