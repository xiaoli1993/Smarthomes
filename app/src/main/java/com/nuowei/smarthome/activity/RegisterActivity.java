package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.common.util.ToastUtils;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.smarthomesdk.utils.Utils;
import com.nuowei.smarthome.util.MyUtil;
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
 * @Time :  2017/2/24 08:30
 * @Description :
 */
public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_again_password)
    EditText etAgainPassword;
    @BindView(R.id.tv_continue)
    AvenirTextView tvContinue;
    @BindView(R.id.tv_tip)
    AvenirTextView tvTip;
    @BindView(R.id.tv_bottom_sign)
    AvenirTextView tvBottomSign;
    @BindView(R.id.table_linear)
    RelativeLayout tableLinear;
    @BindView(R.id.title_name)
    AvenirTextView titleName;
    @BindView(R.id.image_btn_back)
    ImageButton btnBack;

    @OnClick(R.id.image_btn_back)
    void onImageBtnBackClick() {
        //TODO implement
        finish();
    }

    @OnClick(R.id.tv_continue)
    void onContinue() {
        //TODO implement
        if (TextUtils.isEmpty(etName.getText())) {
//            etPassword.setShakeAnimation();
//            showToast(getResources().getString( R.string.Password_can_not_be_empty));
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText())) {
//            etPassword.setShakeAnimation();
//            showToast(getResources().getString( R.string.Password_can_not_be_empty));
            return;
        }
        if (TextUtils.isEmpty(etAgainPassword.getText())) {
//            etPassword.setShakeAnimation();
//            showToast(getResources().getString( R.string.Password_can_not_be_empty));
            return;
        }
        onRegister();

    }

    private void onRegister() {
        final String user = etName.getText().toString();
        String pw = etPassword.getText().toString();
        String pwag = etAgainPassword.getText().toString();

        if (Utils.isEmial(user)) {
            if ((pw.equals(pwag)) && (pw != "") && (pw.length() != 0)) {
                if (pw.length() > 5) {
                    String language = "";
                    if (MyUtil.isZh(this)) {
                        language = "zh-cn";
                    } else {
                        language = "en-us";
                    }
                    HttpManage.getInstance().registerUser(this, user, user, pw, "", language, new HttpManage.ResultCallback<Map<String, String>>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            MyApplication.getLogger().e("失败！：" + error.getCode());
                            ToastUtils.showShortToast(MyApplication.getMyApplication(), "Register error.");
                        }

                        @Override
                        public void onSuccess(int i, Map<String, String> stringStringMap) {
                            Hawk.put("MY_ACCOUNT", user);
                            MyApplication.getLogger().i("注册成功！");
                            ToastUtils.showShortToast(MyApplication.getMyApplication(), "Register success.");
                            finish();
//                            NetManager.getInstance(this).
                        }
                    });
                } else {
                    MyApplication.getLogger().e("密码长度小于6！");
                }
            } else {
                MyApplication.getLogger().e("两次密码不一致！");
            }
        } else {
            MyApplication.getLogger().e("请输入正确邮箱！");
        }
    }


    @OnClick(R.id.tv_bottom_sign)
    void onBottonSign() {
        //TODO implement
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initEven();
    }

    private void initEven() {
        titleName.setText(R.string.Register);
        btnBack.setVisibility(View.VISIBLE);
    }

}