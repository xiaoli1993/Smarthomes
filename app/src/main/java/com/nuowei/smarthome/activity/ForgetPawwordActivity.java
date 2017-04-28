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
import android.widget.RelativeLayout;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.common.util.ToastUtils;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.smarthomesdk.utils.Utils;
import com.nuowei.smarthome.view.textview.AvenirTextView;

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
public class ForgetPawwordActivity extends AppCompatActivity {

    @BindView(R.id.tip)
    AvenirTextView tip;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.btn_sign_in)
    AvenirTextView btnSignIn;
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

    @OnClick(R.id.btn_sign_in)
    void onSignin() {
        //TODO implement
        if (TextUtils.isEmpty(etEmail.getText())) {
            // 设置晃动
//            etEmail.setShakeAnimation();
            // 设置提示
//            showToast(getResources().getString(R.string.phone_ese));
            return;
        }
        String Email = etEmail.getText().toString();
        if (Utils.isEmial(Email)) {
            onForgetpwd(Email);
        } else {
            MyApplication.getLogger().e("邮箱不正确！");
        }

    }

    /**
     * 找回密码手机或邮箱获取验证码
     *
     * @param uid 手机或邮箱
     */
    private void onForgetpwd(String uid) {
        HttpManage.getInstance().forgetbyEmailPasswd(this, uid, new HttpManage.ResultCallback<Map<String, String>>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                MyApplication.getLogger().e("失败！：" + error.getCode());
                ToastUtils.showShortToast(MyApplication.getMyApplication(), "Error.");
            }

            @Override
            public void onSuccess(int i, Map<String, String> stringStringMap) {
                MyApplication.getLogger().i("找回密码成功！");
                ToastUtils.showShortToast(MyApplication.getMyApplication(),"Success.");
                finish();
//                            NetManager.getInstance(this).
            }
        });

    }

    @OnClick(R.id.tv_bottom_sign)
    void onBottonSign() {
        //TODO implement
        startActivity(new Intent(ForgetPawwordActivity.this, RegisterActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        initEven();

    }

    private void initEven() {
        titleName.setText(R.string.ForgetPassword);
        btnBack.setVisibility(View.VISIBLE);
    }

}
