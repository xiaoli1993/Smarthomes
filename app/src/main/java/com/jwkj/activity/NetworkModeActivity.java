package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jwkj.global.Constants;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.ConfirmDialog;
import com.jwkj.widget.PromptDialog;
import com.larksmart.emtmf.jni.EMTMFOptions;
import com.lib.emtmfinfo.EMTMFInit;
import com.lsemtmf.genersdk.tools.commen.AlertUtils;
import com.lsemtmf.genersdk.tools.commen.PreventViolence;
import com.lsemtmf.genersdk.tools.emtmf.EMTMFSDK;
import com.nuowei.smarthome.R;

/**
 * Created by Administrator on 2016/10/13.
 */

public class NetworkModeActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private ImageView back_btn;
    private Button bt_wifi,bt_wire;
    ConfirmDialog dialog;
    boolean isRegFilter=false;
    private PromptDialog promptDialog;
    public boolean isInitEMTMFSDK=false;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_mode);
        context=this;
        initUI();
        regFilter();
    }
    public void initUI(){
        back_btn=(ImageView)findViewById(R.id.back_btn);
        bt_wifi=(Button)findViewById(R.id.bt_wifi);
        bt_wire=(Button)findViewById(R.id.bt_wire);
        bt_wifi.setOnClickListener(this);
        bt_wire.setOnClickListener(this);
        back_btn.setOnClickListener(this);
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
                isInitEMTMFSDK=false;
                finish();
            }else if(intent.getAction().equals(Constants.Action.RADAR_SET_WIFI_FAILED)){
                if(promptDialog!=null&&promptDialog.isShowing()){
                    return;
                }
                promptDialog=new PromptDialog(context);
                promptDialog.setTitle(getResources().getString(R.string.conn_fail)+"?");
                View view = LayoutInflater.from(context).inflate(R.layout.dialg_connect_fail,null);
                promptDialog.addView(view);
                promptDialog.setconnectFailListener(listener);
                promptDialog.show();
            }else if(intent.getAction().equals(Constants.Action.EXIT_ADD_DEVICE)){
                finish();
            }
        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.bt_wifi:
                PreventViolence.preventClick(context, view, PreventViolence.LONG_TIME);
                int errcode = EMTMFSDK.getInstance(context).initSDK(context, EMTMFInit.manufacturer,
                        EMTMFInit.client, EMTMFInit.productModel,
                        EMTMFInit.license);
                isInitEMTMFSDK=true;
//			ToastTools.short_Toast(context, "错误码为" + errcode);
                if (errcode == EMTMFOptions.INITSDK_ERRCOE_WIFIDISABLE) {
//				AlertUtils.SimpleAlert(context, "WIFI未连接",
//						"为了保证您能正常使用EMTMF配置，请先将手机连接上WIFI网络~");
//                    NormalDialog dialog = new NormalDialog(context, context.getResources()
//                            .getString(R.string.no_connect_wifi),
//                            context.getResources().getString(R.string.no_connect_wifi_prompt), "", "");
//                    dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
//                    dialog.showDialog();
                    if(dialog!=null&&dialog.isShowing()){
                        return;
                    }
                    if(!WifiUtils.getInstance().isWifiConnected(context)){
                        dialog=new ConfirmDialog(context);
                        dialog.setTitle(getResources().getString(R.string.please_connect_wifi));
                        dialog.setGravity(Gravity.TOP);
                        dialog.setTxButton(getResources().getString(R.string.i_get_it));
                        dialog.show();
                        return;
                    }
                } else if (errcode == EMTMFOptions.INITSDK_INVAILDDATA) {
                    AlertUtils.SimpleAlert(context, "SDK初始化的参数非法",
                            "请检查SDK初始化时传入的参数是否正确~");
                } else {
                    // 网络可用才进入配置
                    // 配置前需要初始化SDK
                    Intent it = new Intent();
                    it.setClass(context, RadarAddActivity.class);
                    startActivity(it);
                }
//                Intent wifi=new Intent(this,PrepareCameraActivity.class);
//                wifi.putExtra("connect_mode", Constants.NetworkMode.WIFI);
//                startActivity(wifi);
                break;
            case R.id.bt_wire:
                Intent wire=new Intent(this,PrepareCameraActivity.class);
                wire.putExtra("connect_mode",Constants.NetworkMode.WIRE);
                startActivity(wire);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isInitEMTMFSDK){
            EMTMFSDK.getInstance(context).exitEMTFSDK(context);
            isInitEMTMFSDK=false;
        }
        if(isRegFilter){
            unregisterReceiver(br);
            isRegFilter=false;
        }
    }
    private PromptDialog.connectFailListener listener=new PromptDialog.connectFailListener() {
        @Override
        public void onTryAgain() {

        }

        @Override
        public void onTryWird() {

        }
    };
    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_NETWORKMODEACTIVITY;
    }
}
