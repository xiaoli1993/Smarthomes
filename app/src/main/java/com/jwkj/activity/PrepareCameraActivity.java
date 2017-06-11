package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.global.Constants;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.ConfirmDialog;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.textview.DinProTextView;


/**
 * Created by Administrator on 2016/10/13.
 */

public class PrepareCameraActivity extends BaseActivity implements View.OnClickListener{
    private Context context;
    private ImageView back_btn;
    private ImageView iv_connect_power;
    private DinProTextView tv_connect_power;
    private  Button bt_next;
    int connect_mode;
    public boolean isInitEMTMFSDK=false;
    boolean isRegFilter=false;
    ConfirmDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_camera);
        context=this;
        connect_mode=getIntent().getIntExtra("connect_mode",-1);
        initUI();
        regFilter();
    }
    public void initUI(){
        back_btn=(ImageView)findViewById(R.id.back_btn);
        iv_connect_power=(ImageView)findViewById(R.id.iv_connect_power);
        tv_connect_power=(DinProTextView)findViewById(R.id.tv_connect_power);
        bt_next=(Button)findViewById(R.id.bt_next);
        back_btn.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        if(connect_mode==Constants.NetworkMode.WIFI){
            iv_connect_power.setImageResource(R.drawable.wifi_electricity_on);
            tv_connect_power.setText(R.string.connect_the_power);
        }else{
            iv_connect_power.setImageResource(R.drawable.wire_electricity_on);
            tv_connect_power.setText(R.string.wire_prepare_camera);
        }
    }
    public void regFilter(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constants.Action.RADAR_SET_WIFI_SUCCESS);
        filter.addAction(Constants.Action.ADD_CONTACT_SUCCESS);
        filter.addAction(Constants.Action.RADAR_SET_WIFI_FAILED);
        filter.addAction(Constants.Action.EXIT_ADD_DEVICE);
        registerReceiver(br, filter);
        isRegFilter=true;
    }
    BroadcastReceiver br=new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals(Constants.Action.RADAR_SET_WIFI_SUCCESS)){
                isInitEMTMFSDK=false;
                finish();
            }else if(intent.getAction().equals(Constants.Action.ADD_CONTACT_SUCCESS)){
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
       switch (view.getId()){
           case R.id.back_btn:
               finish();
               break;
           case R.id.bt_next:
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
//               if(connect_mode==Constants.NetworkMode.WIFI){
//                   PreventViolence.preventClick(context, view, PreventViolence.LONG_TIME);
//                   int errcode = EMTMFSDK.getInstance(context).initSDK(context, EMTMFInit.manufacturer,
//                           EMTMFInit.client, EMTMFInit.productModel,
//                           EMTMFInit.license);
//                   isInitEMTMFSDK=true;
////			ToastTools.short_Toast(context, "错误码为" + errcode);
//                   if (errcode == EMTMFOptions.INITSDK_ERRCOE_WIFIDISABLE) {
////				AlertUtils.SimpleAlert(context, "WIFI未连接",
////						"为了保证您能正常使用EMTMF配置，请先将手机连接上WIFI网络~");
//                       NormalDialog dialog = new NormalDialog(context, context.getResources()
//                               .getString(R.string.no_connect_wifi),
//                               context.getResources().getString(R.string.no_connect_wifi_prompt), "", "");
//                       dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
//                       dialog.showDialog();
//                   } else if (errcode == EMTMFOptions.INITSDK_INVAILDDATA) {
//                       AlertUtils.SimpleAlert(context, "SDK初始化的参数非法",
//                               "请检查SDK初始化时传入的参数是否正确~");
//                   } else {
//                       // 网络可用才进入配置
//                       // 配置前需要初始化SDK
//                       Intent it = new Intent();
//                       it.setClass(context, RadarAddActivity.class);
//                       startActivity(it);
//                   }
//               }else{
                   Intent i = new Intent(context, LocalDeviceListActivity.class);
                   i.putExtra("addDeviceMethod",Constants.AddDeviceMethod.WIRE_METHOD);
                   startActivity(i);
//               }
               break;
           default:
               break;
       }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(isInitEMTMFSDK){
//            EMTMFSDK.getInstance(context).exitEMTFSDK(context);
//            isInitEMTMFSDK=false;
//        }
        if(isRegFilter){
            unregisterReceiver(br);
            isRegFilter=false;
        }
    }

    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_PREPARECAMERAACTIVITY;
    }

}
