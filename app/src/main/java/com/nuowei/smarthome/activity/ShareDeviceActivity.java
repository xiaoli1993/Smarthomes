package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.common.util.ToastUtils;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.view.button.StateButton;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import org.apache.http.Header;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/4/25 16:33
 * @Description :
 * @Modify record :
 */

public class ShareDeviceActivity extends BaseActivity {

    @BindView(R.id.image_btn_backs)
    ImageButton imageBtnBacks;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.image_share)
    ImageView imageShare;
    @BindView(R.id.theme_table)
    RelativeLayout themeTable;
    @BindView(R.id.et_share)
    EditText etShare;
    @BindView(R.id.btn_share)
    StateButton btnShare;
    XlinkDevice xlinkDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initData();
        initEven();
    }

    private void initEven() {
        tvTitle.setText(getString(R.string.Share_Device));
        tvRight.setVisibility(View.GONE);
    }

    private void initData() {
        Bundle b = this.getIntent().getExtras();
        String mac = b.getString(Constants.GATEWAY_MAC);
        xlinkDevice = DeviceManage.getInstance().getDevice(mac);
    }

    private void setsharedevice(final String User, int deviceID) {
        HttpManage.getInstance().shareDevice(MyApplication.getMyApplication(), User, deviceID, "app", new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                MyApplication.getLogger().e(error.getMsg() + "\t" + error.getCode());
            }

            @Override
            public void onSuccess(int code, String response) {
//                Toasty.success(ShareDeviceActivity.this, getString(R.string.Sharesuccess));
                ToastUtils.showShortToast(MyApplication.getMyApplication(), getString(R.string.Sharesuccess));
//                Toasty.success(MyApplication.getMyApplication(), "Success.", Toast.LENGTH_SHORT, true).show();
                finish();
            }
        });
    }

    @OnClick({R.id.image_btn_backs, R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_btn_backs:
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                this.finish();
                break;
            case R.id.btn_share:
                setsharedevice(etShare.getText().toString(), xlinkDevice.getxDevice().getDeviceId());
                break;
        }
    }
}
