package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.view.circularanim.CircularAnim;
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
        final String ClientName = etName.getText().toString().trim();// .toUpperCase();
        final String Password = etPassword.getText().toString().trim();

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
                Hawk.put(Constants.SAVE_appId,appid);
                Hawk.put(Constants.SAVE_authKey,authKey);
                Hawk.put("MY_ACCOUNT", ClientName);
                Hawk.put("MY_PASSWORD", Password);
                startActivity(new Intent(LoginActivity.this, MainTActivity.class));
                finish();
            }
        });
    }

    @OnClick(R.id.tv_forget_password)
    void onForgetPassword() {
        //TODO implement
        startActivity(new Intent(LoginActivity.this, ForgetPawwordActivity.class));
    }

    @OnClick(R.id.tv_bottom_sign)
    void onBottonSign(View view) {
        //TODO implement
//        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

        CircularAnim.fullActivity(LoginActivity.this, view)
                .colorOrImageRes(R.color.text_green)
                .go(new CircularAnim.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
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
