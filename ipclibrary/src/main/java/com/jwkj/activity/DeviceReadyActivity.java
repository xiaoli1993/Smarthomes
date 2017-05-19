package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.global.Constants;
import com.jwkj.widget.PromptDialog;
import com.nuowei.ipclibrary.R;

/**
 * Created by Administrator on 2016/10/13.
 */

public class DeviceReadyActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private TextView tv_no_listen;
    private Button bt_listen;
    private ImageView iv_device;
    private ImageView back_btn;
    String ssid;
    String wifiPwd;
    private byte mAuthMode;
    int mLocalIp;
    boolean isRegFilter=false;
    private PromptDialog promptDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_ready);
        context=this;
        ssid = getIntent().getStringExtra("ssidname");
        wifiPwd = getIntent().getStringExtra("wifiPwd");
        mAuthMode = getIntent().getByteExtra("type", (byte) -1);
        mLocalIp = getIntent().getIntExtra("LocalIp", -1);
        initUI();
        regFilter();
    }
    public void initUI(){
        tv_no_listen=(TextView)findViewById(R.id.tv_no_listen);
        bt_listen=(Button)findViewById(R.id.bt_listen);
        iv_device=(ImageView)findViewById(R.id.iv_device);
        back_btn=(ImageView)findViewById(R.id.back_btn);
        tv_no_listen.setOnClickListener(this);
        bt_listen.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        tv_no_listen.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        AnimationDrawable animationDrawable=(AnimationDrawable) iv_device.getBackground();
        animationDrawable.start();
    }
    public void regFilter(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constants.Action.ADD_CONTACT_SUCCESS);
        filter.addAction(Constants.Action.RADAR_SET_WIFI_SUCCESS);
        filter.addAction(Constants.Action.RADAR_SET_WIFI_FAILED);
        filter.addAction(Constants.Action.EXIT_ADD_DEVICE);
        registerReceiver(br,filter);
        isRegFilter=true;
    }
    BroadcastReceiver br=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.Action.ADD_CONTACT_SUCCESS)){
                finish();
            }else if(intent.getAction().equals(Constants.Action.RADAR_SET_WIFI_SUCCESS)){
                finish();
            }else if(intent.getAction().equals(Constants.Action.RADAR_SET_WIFI_FAILED)){
                finish();
            }else if(intent.getAction().equals(Constants.Action.EXIT_ADD_DEVICE)){
                finish();
            }
        }
    };
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_no_listen) {
            if (promptDialog != null && promptDialog.isShowing()) {
                return;
            }
            promptDialog = new PromptDialog(context);
            View v = LayoutInflater.from(context).inflate(R.layout.dialog_no_listen, null);
            promptDialog.setTitle(getResources().getString(R.string.not_listen_connect_voice));
            promptDialog.addView(v);
            promptDialog.show();

        } else if (i == R.id.bt_listen) {
            Intent send_sound_wave = new Intent(this, SendSoundWaveGuideActivity.class);
            send_sound_wave.putExtra("ssidname", ssid);
            send_sound_wave.putExtra("wifiPwd", wifiPwd);
            send_sound_wave.putExtra("type", mAuthMode);
            send_sound_wave.putExtra("LocalIp", mLocalIp);
            startActivity(send_sound_wave);

        } else if (i == R.id.back_btn) {
            finish();

        } else {
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isRegFilter){
            unregisterReceiver(br);
            isRegFilter=false;
        }
    }

    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_DEVICEREADYACTIVITY;
    }

}
