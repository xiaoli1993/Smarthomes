package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.imageview.CircleImageView;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/17 14:33
 * @Description :
 */
public class LoginActivity extends Activity {
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.areaCodeTV)
    TextView areaCodeTV;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_sign_in)
    AvenirTextView tvSignIn;
    @BindView(R.id.tv_forget_password)
    AvenirTextView tvForgetPassword;
    @BindView(R.id.tv_tip)
    AvenirTextView tvTip;
    @BindView(R.id.tv_bottom_sign)
    AvenirTextView tvBottomSign;

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
    }
}
