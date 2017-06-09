package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.LanguageUtils;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;

import org.json.JSONObject;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_regist;
    private TextView tv_forgetpwd;
    private Context mContext;
    private Button btn_regist;
    private EditText mAccountName;
    private EditText mAccountPwd;
    private String mInputName;
    private String mInputPwd;
    private String recentCode = "";
    private boolean isDialogCanel;
    private String recentName = "";
    private boolean isCN;
    private boolean isRegFilter;
    private String recentPwd;
    private ImageView iv_wechat;
    private boolean isLoginTimeout;
    private LinearLayout ll_countrycode;
    private TextView tv_countrycode;
    private TextView tv_country;
    boolean isMobileLogin = false;
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tv_country.setTextColor(getResources().getColor(R.color.bg_btn_blue_p));
                    tv_countrycode.setTextColor(getResources().getColor(R.color.bg_btn_blue_p));
                    break;
                case MotionEvent.ACTION_UP:
                    tv_country.setTextColor(getResources().getColor(R.color.bg_btn_blue));
                    tv_countrycode.setTextColor(getResources().getColor(R.color.bg_btn_blue));
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_login_ipc);
        initComponent();
        initRememberPass();
        regFilter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getExtras() != null && intent.getExtras().getString("username") != null) {
            String username = intent.getExtras().getString("username");

            mAccountName.setText(username);
        }
        if (intent != null && intent.getExtras() != null && intent.getExtras().getString("password") != null) {
            mAccountPwd.setText(intent.getExtras().getString("password"));
        } else {
            mAccountPwd.setText("");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void initComponent() {
        tv_countrycode = (TextView) findViewById(R.id.tv_countrycode);
        tv_country = (TextView) findViewById(R.id.tv_country);
        ll_countrycode = (LinearLayout) findViewById(R.id.ll_countrycode);
        iv_wechat = (ImageView) findViewById(R.id.iv_wechat);
        iv_wechat.setOnClickListener(this);
        tv_forgetpwd = (TextView) findViewById(R.id.tv_forgetpwd);
        tv_forgetpwd.setOnClickListener(this);
        mAccountName = (EditText) findViewById(R.id.et_account);
        mAccountPwd = (EditText) findViewById(R.id.et_pwd);
        tv_regist = (TextView) findViewById(R.id.tv_regist);
        btn_regist = (Button) findViewById(R.id.btn_login);
        ll_countrycode.setOnTouchListener(onTouchListener);
        ll_countrycode.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
        tv_regist.setOnClickListener(this);
        if (!LanguageUtils.isLanguage("CN", this)) {
            isCN = false;
        } else {
            isCN = true;
        }
    }

    public void initRememberPass() {

        recentName = SharedPreferencesManager.getInstance().getData(
                mContext, SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME_EMAILORPHONE);
        if (null == recentName || "".equals(recentName)) {
            recentName = SharedPreferencesManager.getInstance().getData(
                    mContext, SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.KEY_RECENTNAME);
        }
        if (null == recentName || "".equals(recentName)) {
            recentName = SharedPreferencesManager.getInstance().getData(
                    mContext, SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.KEY_RECENTNAME_EMAIL);
        }

        recentPwd = SharedPreferencesManager.getInstance().getData(
                mContext, SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTPASS_EMAILORPHONE);

        if (null == recentPwd || "".equals(recentPwd)) {
            recentPwd = SharedPreferencesManager.getInstance().getData(
                    mContext, SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.KEY_RECENTPASS_EMAIL);
        }
        if (null == recentPwd || "".equals(recentPwd)) {
            recentPwd = SharedPreferencesManager.getInstance().getData(
                    mContext, SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.KEY_RECENTPASS);
        }

        recentCode = SharedPreferencesManager.getInstance().getData(
                mContext, SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTCODE);
        mAccountName.setText(recentName);
        mAccountPwd.setText(recentPwd);
        if (!recentCode.equals("")) {
            tv_countrycode.setText("+" + recentCode);
            String name = SearchListActivity.getNameByCode(mContext,
                    Integer.parseInt(recentCode));
            tv_country.setText(name);
        } else {
            if (getResources().getConfiguration().locale.getCountry()
                    .equals("TW")) {
                tv_countrycode.setText("+886");
                String name = SearchListActivity.getNameByCode(mContext,
                        886);
                tv_country.setText(name);
            } else if (getResources().getConfiguration().locale
                    .getCountry().equals("CN")) {
                tv_countrycode.setText("+86");
                String name = SearchListActivity
                        .getNameByCode(mContext, 86);
                tv_country.setText(name);
            } else if (getResources().getConfiguration().locale
                    .getCountry().equals("HK")) {
                tv_countrycode.setText("+852");
                String name = SearchListActivity
                        .getNameByCode(mContext, 852);
                tv_country.setText(name);
            } else {
                tv_countrycode.setText("+1");
                String name = SearchListActivity.getNameByCode(mContext, 1);
                tv_country.setText(name);
            }
        }
    }

    //}
    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_LOGINACTIVITY;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_regist:
                if (!isCN) {
                    startActivity(new Intent(LoginActivity.this, RegisterByEmailActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
                break;
            case R.id.btn_login:
                Utils.hindKeyBoard(v);
                String wifiName = WifiUtils.getInstance().getConnectWifiName();
                if (WifiUtils.getInstance().isConnectAP()) {
                    // T.showLong(mContext, R.string.check_wifi);
                    WifiUtils.getInstance().disConnectWifi(wifiName);
                    login();
                } else {
                    login();
                }
                break;
            case R.id.tv_forgetpwd:
                if (WifiUtils.getInstance().isConnectAP()) {
                    T.showLong(mContext, R.string.check_wifi);
                } else {
                    startActivity(new Intent(mContext, RetPwdActivity.class));
                }
                break;
            case R.id.iv_wechat:
                T.showShort(mContext, R.string.please_expect);
                break;
            case R.id.ll_countrycode:
                Intent i = new Intent(mContext, SearchListActivity.class);
                //这里改变textview的颜色
                startActivity(i);
                break;
        }
    }

    private void login() {
        isLoginTimeout = false;
        mInputName = mAccountName.getText().toString().trim();
        mInputPwd = mAccountPwd.getText().toString().trim();
        if ((mInputName != null && !mInputName.equals(""))
                && (mInputPwd != null && !mInputPwd.equals(""))) {

            if (!Utils.isNumeric(mInputName) && !Utils.isEmail(mInputName)) {
                T.showShort(mContext, R.string.phone_email_error);
                return;
            }

            if (Utils.isNumeric(mInputName)) {
                if (mInputName.charAt(0) != '0') {
                    if (!Utils.isMobileNO(mInputName)) {
                        T.showShort(mContext, R.string.phone_email_error);
                        return;
                    } else {
                        isMobileLogin = true;
                        String code = tv_countrycode.getText().toString();
                        String name = code + "-"
                                + mInputName;
                        new LoginTask(name, mInputPwd).execute();
                        showDialog();
                        return;
                    }
                } else {
                    if (mInputName.length() > 10) {
                        T.showShort(mContext, R.string.account_no_exist);
                        return;
                    }
                    new LoginTask(mInputName, mInputPwd).execute();
                    showDialog();
                }
            } else {
                new LoginTask(mInputName, mInputPwd).execute();
                showDialog();
            }

        } else {
            if ((mInputName == null || mInputName.equals(""))
                    && (mInputPwd != null && !mInputPwd.equals(""))) {
                T.showShort(mContext, R.string.input_account);
            } else if ((mInputName != null && !mInputName.equals(""))
                    && (mInputPwd == null || mInputPwd.equals(""))) {
                T.showShort(mContext, R.string.input_password);
            } else {
                T.showShort(mContext, R.string.input_tip);
            }
        }
    }

    public void showDialog() {
        dialog = new NormalDialog(mContext);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                // TODO Auto-generated method stub
                isDialogCanel = true;
            }

        });
        dialog.setTitle(mContext.getResources().getString(
                R.string.login_ing));
        dialog.showLoadingDialog();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnNormalDialogTimeOutListner(new NormalDialog.OnNormalDialogTimeOutListner() {
            @Override
            public void onTimeOut() {
                if (!isLoginTimeout) {
                    T.showShort(mContext, R.string.time_out);
                }
                isLoginTimeout = true;
                isDialogCanel = true;
            }
        });
        dialog.setTimeOut(20000);
        isDialogCanel = false;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(Constants.Action.REPLACE_EMAIL_LOGIN)) {
                mAccountName.setText(intent.getStringExtra("username"));
                mAccountPwd.setText(intent.getStringExtra("password"));
                //login();
            } else if (intent.getAction().equals(
                    Constants.Action.REPLACE_PHONE_LOGIN)) {
                mAccountName.setText(intent.getStringExtra("username"));
                mAccountPwd.setText(intent.getStringExtra("password"));
                //login();
            } else if (intent.getAction().equals(
                    Constants.Action.ACTION_COUNTRY_CHOOSE)) {
                String[] info = intent.getStringArrayExtra("info");
                tv_country.setText(info[0]);
                tv_countrycode.setText("+" + info[1]);
            }
        }
    };


    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Action.REPLACE_EMAIL_LOGIN);
        filter.addAction(Constants.Action.REPLACE_PHONE_LOGIN);
        filter.addAction(Constants.Action.ACTION_COUNTRY_CHOOSE);
        mContext.registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegFilter) {
            unregisterReceiver(mReceiver);
            isRegFilter = false;
        }
    }

    class LoginTask extends AsyncTask {
        String username;
        String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            Utils.sleepThread(1000);
            return NetManager.getInstance(mContext).login(username, password);
        }

        @Override
        protected void onPostExecute(Object object) {
            // TODO Auto-generated method stub

            LoginResult result = NetManager
                    .createLoginResult((JSONObject) object);


            if (Utils.isTostCmd(Integer.parseInt(result.error_code))) {
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (!isDialogCanel) {
                    T.showLong(mContext, Utils.GetToastCMDString(Integer
                            .parseInt(result.error_code)));
                }
                return;
            }
            switch (Integer.parseInt(result.error_code)) {
                case NetManager.SESSION_ID_ERROR:
                    Intent i = new Intent();
                    i.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(i);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new LoginTask(username, password).execute();
                    break;
                case NetManager.LOGIN_SUCCESS:
                    if (isDialogCanel) {
                        return;
                    }
                    if (null != dialog) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    //如果是手机登录，才保存国别码
                    if (isMobileLogin) {
                        String code = tv_countrycode.getText().toString();
                        code = code.substring(1, code.length());
                        SharedPreferencesManager.getInstance().putData(mContext,
                                SharedPreferencesManager.SP_FILE_GWELL,
                                SharedPreferencesManager.KEY_RECENTCODE,
                                code);
                    }
                    SharedPreferencesManager.getInstance().putData(mContext,
                            SharedPreferencesManager.SP_FILE_GWELL,
                            SharedPreferencesManager.KEY_RECENTNAME_EMAILORPHONE,
                            mInputName);
                    SharedPreferencesManager.getInstance().putData(mContext,
                            SharedPreferencesManager.SP_FILE_GWELL,
                            SharedPreferencesManager.KEY_RECENTPASS_EMAILORPHONE,
                            password);
                    String codeStr1 = String.valueOf(Long.parseLong(result.rCode1));
                    String codeStr2 = String.valueOf(Long.parseLong(result.rCode2));
                    Account account = AccountPersist.getInstance()
                            .getActiveAccountInfo(mContext);
                    if (null == account) {
                        account = new Account();
                    }
                    account.three_number = result.contactId;
                    account.phone = result.phone;
                    account.email = result.email;
                    account.sessionId = result.sessionId;
                    account.rCode1 = codeStr1;
                    account.rCode2 = codeStr2;
                    account.countryCode = result.countryCode;
                    AccountPersist.getInstance()
                            .setActiveAccount(mContext, account);
                    NpcCommon.mThreeNum = AccountPersist.getInstance()
                            .getActiveAccountInfo(mContext).three_number;
                    Intent login = new Intent(mContext, MainActivity.class);
                    startActivity(login);
                    finish();
                    break;
                case NetManager.LOGIN_USER_UNEXIST:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.account_no_exist);
                    }
                    break;
                case NetManager.LOGIN_PWD_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.password_error);
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
                case NetManager.REGISTER_PHONE_FORMAT_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.phone_format_error);
                    }
                    break;

                case NetManager.UNKNOWN_ERROR:
                    if (!isLoginTimeout) {
                        T.showShort(mContext, R.string.time_out);
                        isLoginTimeout = true;
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    break;
                case NetManager.LOGIN_ACCOUNT_NEED_CONTRYCODE:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, R.string.need_contrycode);
                    }
                    break;
                default:
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    if (!isDialogCanel) {
                        T.showShort(mContext, getResources().getString(R.string.loginfail) + result.error_code);
                    }
                    break;
            }
        }

    }
}
