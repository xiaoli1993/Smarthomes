package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.global.Constants;
import com.jwkj.widget.NormalDialog;
import com.larksmart.emtmf.jni.EMTMFOptions;
import com.lib.emtmfinfo.EMTMFInit;
import com.lsemtmf.genersdk.tools.commen.AlertUtils;
import com.lsemtmf.genersdk.tools.commen.PreventViolence;
import com.lsemtmf.genersdk.tools.emtmf.EMTMFSDK;
import com.nuowei.smarthome.R;

public class RadarAddFirstActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	Button next;
	ImageView back;
    public boolean isInitEMTMFSDK=false;
    boolean isRegFilter=false;
    TextView tv_no_listen;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_radar_add_first);
		mContext = this;
		next = (Button) findViewById(R.id.next);
		back = (ImageView) findViewById(R.id.back_btn);
		tv_no_listen=(TextView)findViewById(R.id.tv_no_listen);
		tv_no_listen.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		next.setOnClickListener(this);
		back.setOnClickListener(this);
		tv_no_listen.setOnClickListener(this);
		regFilter();

	}
    public void regFilter(){
    	IntentFilter filter=new IntentFilter();
    	filter.addAction(Constants.Action.RADAR_SET_WIFI_SUCCESS);
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
			}
		}
	};
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_RADARADDFIRSTACTIVITY;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(isInitEMTMFSDK){
			EMTMFSDK.getInstance(mContext).exitEMTFSDK(mContext);
			isInitEMTMFSDK=false;
		}
		if(isRegFilter){
			unregisterReceiver(br);
			isRegFilter=false;
		}

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next:
			PreventViolence.preventClick(mContext, v, PreventViolence.LONG_TIME);
			int errcode = EMTMFSDK.getInstance(mContext).initSDK(mContext, EMTMFInit.manufacturer,
					EMTMFInit.client, EMTMFInit.productModel,
					EMTMFInit.license);
			isInitEMTMFSDK=true;
//			ToastTools.short_Toast(mContext, "错误码为" + errcode);
			if (errcode == EMTMFOptions.INITSDK_ERRCOE_WIFIDISABLE) {
//				AlertUtils.SimpleAlert(mContext, "WIFI未连接",
//						"为了保证您能正常使用EMTMF配置，请先将手机连接上WIFI网络~");
				NormalDialog dialog = new NormalDialog(mContext, mContext.getResources()
						.getString(R.string.no_connect_wifi),
						mContext.getResources().getString(R.string.no_connect_wifi_prompt), "", "");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
				dialog.showDialog();
			} else if (errcode == EMTMFOptions.INITSDK_INVAILDDATA) {
				AlertUtils.SimpleAlert(mContext, "SDK初始化的参数非法",
						"请检查SDK初始化时传入的参数是否正确~");
			} else {
				// 网络可用才进入配置
				// 配置前需要初始化SDK
				Intent it = new Intent();
				it.setClass(mContext, RadarAddActivity.class);
				startActivity(it);
			}
			break;
		case R.id.back_btn:
			finish();
			break;
		case R.id.tv_no_listen:
			NormalDialog dialog=new NormalDialog(mContext);
			dialog.showNoListenVoice();
			break;
		default:
			break;
		}
	}

}
