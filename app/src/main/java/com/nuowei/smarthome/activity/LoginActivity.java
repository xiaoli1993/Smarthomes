package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.UserInfo;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.view.imageview.CircleImageView;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.orhanobut.hawk.Hawk;

import org.apache.http.Header;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/17 14:33
 * @Description :
 */
public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_sign_in)
    Button tvSignIn;
    @BindView(R.id.tv_forget_password)
    AvenirTextView tvForgetPassword;
    @BindView(R.id.tv_tip)
    AvenirTextView tvTip;
    @BindView(R.id.tv_bottom_sign)
    AvenirTextView tvBottomSign;

    @BindView(R.id.table_linear)
    RelativeLayout tableLinear;
    @BindView(R.id.title_name)
    AvenirTextView titleName;
    public static Context mContext;
    private String ClientName;
    private String Password;

    @OnClick(R.id.tv_sign_in)
    void onSignin(View view) {
        //TODO implement
        if (TextUtils.isEmpty(etName.getText())) {
            // 设置晃动
//            etName.setShakeAnimation();
            // 设置提示
//            showToast(getResources().getString( R.string.Account_can_not_be_empty));
            return;
        }

        if (TextUtils.isEmpty(etPassword.getText())) {
//            etPassword.setShakeAnimation();
//            showToast(getResources().getString( R.string.Password_can_not_be_empty));
            return;
        }

        // 收缩按钮
//        CircularAnim.hide(tvSignIn).go();

        onLogin();
    }


    private void onLogin() {
        ClientName = etName.getText().toString().trim();// .toUpperCase();
        Password = etPassword.getText().toString().trim();

        HttpManage.getInstance().doLogin(MyApplication.getMyApplication(), ClientName, Password, new HttpManage.ResultCallback<Map<String, String>>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                MyApplication.getLogger().e("Code:" + error.getCode());
            }

            @Override
            public void onSuccess(int i, Map<String, String> stringStringMap) {
                String authKey = stringStringMap.get("authorize");
                String accessToken = stringStringMap.get("access_token");
                int appid = Integer.parseInt(stringStringMap.get("user_id"));
                String refresh_token = stringStringMap.get("refresh_token");

                MyApplication.getMyApplication().setAccessToken(accessToken);
                MyApplication.getMyApplication().setAppid(appid);
                MyApplication.getMyApplication().setAuthKey(authKey);
                MyApplication.getMyApplication().setRefresh_token(refresh_token);

                MyApplication.getLogger().i("Auth", "accessToken:" + accessToken + "appid:" + appid + "authKey:" + authKey);
                Hawk.put(Constants.SAVE_appId, appid);
                Hawk.put(Constants.SAVE_authKey, authKey);
                Hawk.put("MY_ACCOUNT", ClientName);
                Hawk.put("MY_PASSWORD", Password);

                mHandler.sendEmptyMessage(1);

            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
//                    LoginResult loginResult = NetManager.getInstance(LoginActivity.this).createLoginResult(NetManager.getInstance(LoginActivity.this).login(ClientName, Constants.BEIMAPASS));
//                    MyApplication.getLogger().i(loginResult.contactId + "\n" + loginResult.error_code);
//                    new LoginTask(MyUtil.clientNameToEmail(ClientName), Constants.BEIMAPASS).execute();
                    HttpManage.getInstance().getUserInfo(MyApplication.getMyApplication(), new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {

                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            Gson gson = new Gson();
                            MyApplication.getMyApplication().setUserInfo(gson.fromJson(response, UserInfo.class));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                    break;
            }
        }
    };

//    class LoginTask extends AsyncTask {
//        String username;
//        String password;
//
//        public LoginTask(String username, String password) {
//            this.username = username;
//            this.password = password;
//
//        }
//
//        @Override
//        protected Object doInBackground(Object... params) {
//            // TODO Auto-generated method stub
//            // Utils.sleepThread(100);
//            return NetManager.getInstance(mContext).login(username, password);
//        }
//
//        @Override
//        protected void onPostExecute(Object object) {
//            // TODO Auto-generated method stub
//            LoginResult result = NetManager
//                    .createLoginResult((JSONObject) object);
//            switch (Integer.parseInt(result.error_code)) {
//                case NetManager.SESSION_ID_ERROR:
//                    Intent i = new Intent();
//                    i.setAction(Constants.Action.SESSION_ID_ERROR);
//                    MyApplication.getMyApplication().sendBroadcast(i);
//                    break;
//                case NetManager.CONNECT_CHANGE:
//                    new LoginTask(username, password).execute();
//                    return;
//                case NetManager.LOGIN_SUCCESS:
//                    MyApplication.getLogger().i("登录成功");
////                    SharedPreferencesManager.getInstance().putData(mContext,
////                            SharedPreferencesManager.SP_FILE_GWELL,
////                            SharedPreferencesManager.KEY_RECENTNAME_EMAIL,
////                            ClientName);
////                    SharedPreferencesManager.getInstance().putData(mContext,
////                            SharedPreferencesManager.SP_FILE_GWELL,
////                            SharedPreferencesManager.KEY_RECENTPASS_EMAIL,
////                            Password);
////                    SharedPreferencesManager.getInstance().putRecentLoginType(mContext, Constants.LoginType.EMAIL);
////                    String codeStr1 = String.valueOf(Long.parseLong(result.rCode1));
////                    String codeStr2 = String.valueOf(Long.parseLong(result.rCode2));
////                    Account account = AccountPersist.getInstance().getActiveAccountInfo(mContext);
////                    if (null == account) {
////                        account = new Account();
////                    }
////                    account.three_number = result.contactId;
////                    account.phone = result.phone;
////                    account.email = result.email;
////                    account.sessionId = result.sessionId;
////                    account.rCode1 = codeStr1;
////                    account.rCode2 = codeStr2;
////                    account.countryCode = result.countryCode;
////                    AccountPersist.getInstance().setActiveAccount(mContext, account);
////                    NpcCommon.mThreeNum = AccountPersist.getInstance().getActiveAccountInfo(mContext).three_number;
//                    break;
//                case NetManager.LOGIN_USER_UNEXIST:
////                    T.showShort(mContext, R.string.account_no_exist);
//                    new RegisterTask("1", username, "", "", password, password, "", "1").execute();
//                    MyApplication.getLogger().e("正在注册" + username + "pass:" + password);
//                    break;
//                case NetManager.LOGIN_PWD_ERROR:
//                    new RegisterTask("1", username, "", "", password, password, "", "1").execute();
//                    MyApplication.getLogger().e("正在注册" + username + "pass:" + password);
//                    break;
//                default:
////                    T.showShort(mContext, R.string.loginfail);
//                    break;
//            }
//        }
//    }
//
//    class RegisterTask extends AsyncTask {
//        String VersionFlag;
//        String Email;
//        String CountryCode;
//        String PhoneNO;
//        String Pwd;
//        String RePwd;
//        String VerifyCode;
//        String IgnoreSafeWarning;
//
//        public RegisterTask(String VersionFlag, String Email,
//                            String CountryCode, String PhoneNO, String Pwd, String RePwd,
//                            String VerifyCode, String IgnoreSafeWarning) {
//            this.VersionFlag = VersionFlag;
//            this.Email = Email;
//            this.CountryCode = CountryCode;
//            this.PhoneNO = PhoneNO;
//            this.Pwd = Pwd;
//            this.RePwd = RePwd;
//            this.VerifyCode = VerifyCode;
//            this.IgnoreSafeWarning = IgnoreSafeWarning;
//        }
//
//        @Override
//        protected Object doInBackground(Object... params) {
//            // TODO Auto-generated method stub
//            // Utils.sleepThread(100);
//            return NetManager.getInstance(mContext).register(VersionFlag,
//                    Email, CountryCode, PhoneNO, Pwd, RePwd, VerifyCode,
//                    IgnoreSafeWarning);
//        }
//
//        @Override
//        protected void onPostExecute(Object object) {
//            // TODO Auto-generated method stub
//            RegisterResult result = NetManager
//                    .createRegisterResult((JSONObject) object);
//            switch (Integer.parseInt(result.error_code)) {
//                case NetManager.SESSION_ID_ERROR:
//                    Intent relogin = new Intent();
//                    relogin.setAction(Constants.Action.SESSION_ID_ERROR);
//                    MyApplication.getMyApplication().sendBroadcast(relogin);
//                    new LoginTask(Email, Pwd).execute();
//                    break;
//                case NetManager.CONNECT_CHANGE:
//                    new RegisterTask(VersionFlag, Email, CountryCode, PhoneNO, Pwd,
//                            RePwd, VerifyCode, IgnoreSafeWarning).execute();
//                    return;
//                case NetManager.REGISTER_SUCCESS:
//                    Intent i = new Intent();
//                    i.setAction(Constants.Action.REPLACE_EMAIL_LOGIN);
//                    i.putExtra("username", Email);
//                    i.putExtra("password", Pwd);
//                    mContext.sendBroadcast(i);
//                    new LoginTask(Email, Pwd).execute();
//                    finish();
//                    break;
//                case NetManager.REGISTER_EMAIL_USED:
////                    T.showShort(mContext, R.string.email_used);
//                    new LoginTask(Email, Pwd).execute();
//                    break;
//                case NetManager.REGISTER_EMAIL_FORMAT_ERROR:
////                    T.showShort(mContext, R.string.email_format_error);
//                    break;
//                case NetManager.REGISTER_PASSWORD_NO_MATCH:
//
//                    break;
//
//                default:
////                    T.showShort(mContext, R.string.operator_error);
//                    break;
//            }
//        }
//    }

    @OnClick(R.id.tv_forget_password)
    void onForgetPassword() {
        //TODO implement
        startActivity(new Intent(LoginActivity.this, ForgetPawwordActivity.class));
    }

    @OnClick(R.id.tv_bottom_sign)
    void onBottonSign(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mContext = this;
        initEven();
    }

    private void initEven() {
        AssetManager assetmanager = this.getAssets();
        Typeface fonc = Typeface.createFromAsset(assetmanager, "Avenir.ttf");
        etName.setTypeface(fonc);
        etPassword.setTypeface(fonc);
        titleName.setText(R.string.Login);
        tvSignIn.setTypeface(fonc);
        boolean contains = Hawk.contains("MY_ACCOUNT");
        boolean ispass = Hawk.contains("MY_PASSWORD");
        if (contains) {
            String MY_ACCOUNT = Hawk.get("MY_ACCOUNT");
            if (ispass) {
                String MY_PASSWORD = Hawk.get("MY_PASSWORD");
                etPassword.setText(MY_PASSWORD);
            }
            etName.setText(MY_ACCOUNT);

        }
    }
}
