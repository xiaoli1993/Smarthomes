package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.activity.AccountInfoActivity;
import com.jwkj.activity.MallActivity;
import com.jwkj.activity.RecommendInformationActivity;
import com.jwkj.activity.SettingSystemActivity;
import com.jwkj.activity.SysMsgActivity;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SysMessage;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.NpcCommon;
import com.jwkj.thread.UpdateCheckVersionThread;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.global.Config;
import com.nuowei.ipclibrary.R;

import java.util.List;
import java.util.Locale;

public class SettingFrag extends BaseFragment implements OnClickListener {
	private Context mContext;
	private RelativeLayout mCheckUpdateTextView = null;
	private RelativeLayout mLogOut, mExit, center_about, account_set, sys_set,
			sys_msg,r_mall,r_recommend_product,r_help;
	private TextView mName;
	boolean isRegFilter = false;
	// update add
	private MyHandler handler;
	private boolean isCancelCheck = true;
	private NormalDialog dialog;
	ImageView sysMsg_notify_img, network_type;
	ImageView bt_system_message;
	boolean isLogin=true;
	public static boolean isCheckMsg=false;
	TextView tx_update;
	boolean isUpdate=false;
	boolean isNeedUpdate=false;
    ImageView iv_new_version,iv_new_version_right_arrow;
	// end
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		mContext = getActivity();
		initComponent(view);
		regFilter();
		updateSysMsg();
		return view;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public void initComponent(View view) {
		mCheckUpdateTextView = (RelativeLayout) view
				.findViewById(R.id.center_t);
		mName = (TextView) view.findViewById(R.id.mailAdr);
		mLogOut = (RelativeLayout) view.findViewById(R.id.logout_layout);
		account_set = (RelativeLayout) view.findViewById(R.id.account_set);
		sys_set = (RelativeLayout) view.findViewById(R.id.system_set);
		mExit = (RelativeLayout) view.findViewById(R.id.exit_layout);
		center_about = (RelativeLayout) view.findViewById(R.id.center_about);
		sys_msg = (RelativeLayout) view.findViewById(R.id.system_message);
		sysMsg_notify_img = (ImageView) view
				.findViewById(R.id.sysMsg_notify_img);

		network_type = (ImageView) view.findViewById(R.id.network_type);
		r_mall=(RelativeLayout)view.findViewById(R.id.r_mall);
		r_recommend_product=(RelativeLayout)view.findViewById(R.id.r_recommend_product);
		bt_system_message=(ImageView)view.findViewById(R.id.bt_system_message);
		r_help=(RelativeLayout)view.findViewById(R.id.r_help);
		tx_update=(TextView)view.findViewById(R.id.tx_update);
		iv_new_version=(ImageView)view.findViewById(R.id.iv_new_version);
		iv_new_version_right_arrow=(ImageView)view.findViewById(R.id.iv_new_version_right_arrow);
		showNoReadCount();
		if (NpcCommon.mThreeNum.equals("0517401")) {
			mName.setText(R.string.no_login);
			isLogin=false;
		} else {
			mName.setText(String.valueOf(NpcCommon.mThreeNum));
		}
		if (NpcCommon.mNetWorkType == NpcCommon.NETWORK_TYPE.NETWORK_WIFI) {
			network_type.setImageResource(R.drawable.wifi);
		} else {
			network_type.setImageResource(R.drawable.net_3g);
		}
		String sellerId=Config.AppConfig.store_id;
		if(sellerId!=null&&!sellerId.equals("")&&!sellerId.equals("0")){
			r_mall.setVisibility(View.VISIBLE);
			r_recommend_product.setVisibility(View.VISIBLE);
		}else{
			r_mall.setVisibility(View.GONE);
			r_recommend_product.setVisibility(View.GONE);
		}
		String url=SharedPreferencesManager.getInstance().getHelpUrl(mContext);
		if(url!=null&&!url.equals("")){
			r_help.setVisibility(View.VISIBLE);
		}else{
			r_help.setVisibility(View.GONE);
		}
		mLogOut.setOnClickListener(this);
		account_set.setOnClickListener(this);
		sys_msg.setOnClickListener(this);
		mExit.setOnClickListener(this);
		sys_set.setOnClickListener(this);
		center_about.setOnClickListener(this);
		mCheckUpdateTextView.setOnClickListener(this);
		mName.setOnClickListener(this);
		r_mall.setOnClickListener(this);
		r_recommend_product.setOnClickListener(this);
		r_help.setOnClickListener(this);
		handler = new MyHandler();
		if (NpcCommon.mThreeNum.equals("0517401")) {
			account_set.setVisibility(RelativeLayout.GONE);
		}
		isConnectAp();
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.RECEIVE_SYS_MSG);
		filter.addAction(Constants.Action.NET_WORK_TYPE_CHANGE);
		filter.addAction(Constants.Action.SYSTEM_MESSAGE_COUNT);
		filter.addAction(Constants.Action.ACTION_NETWORK_CHANGE);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Action.RECEIVE_SYS_MSG)) {
				updateSysMsg();
			} else if (intent.getAction().equals(
					Constants.Action.NET_WORK_TYPE_CHANGE)) {
				if (NpcCommon.mNetWorkType == NpcCommon.NETWORK_TYPE.NETWORK_WIFI) {
					network_type.setImageResource(R.drawable.wifi);
				} else {
					network_type.setImageResource(R.drawable.net_3g);
				}
			}else if(intent.getAction().equals(Constants.Action.SYSTEM_MESSAGE_COUNT)){
				if(!isCheckMsg){
					showNoReadCount();
				}
			}else if(intent.getAction().equals(Constants.Action.ACTION_NETWORK_CHANGE)){
				isConnectAp();
			}
		}
	};
	public void isConnectAp(){
		String wifiName=WifiUtils.getInstance().getConnectWifiName();
		if(wifiName.length()<=0){
			return;
		}
		if(wifiName.charAt(0)=='"'){
			wifiName=wifiName.substring(1, wifiName.length()-1);
		}
		if(wifiName.startsWith(AppConfig.Relese.APTAG)){
			mLogOut.setVisibility(View.GONE);
			mExit.setVisibility(View.GONE);
		}else{
			mLogOut.setVisibility(View.VISIBLE);
			mExit.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(!isUpdate){
			new UpdateCheckVersionThread(handler).start();
			isUpdate=true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(handler!=null){
			handler.cancel();
			handler.removeCallbacksAndMessages(null);
		}
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.logout_layout) {
			Intent canel = new Intent();
			canel.setAction(Constants.Action.ACTION_SWITCH_USER);
			mContext.sendBroadcast(canel);

		} else if (i == R.id.exit_layout) {
			Intent exit = new Intent();
			exit.setAction(Constants.Action.ACTION_EXIT);
			exit.putExtra("isLogin", isLogin);
			mContext.sendBroadcast(exit);

		} else if (i == R.id.center_t) {
			if (isNeedUpdate) {
				if (null != dialog && dialog.isShowing()) {
					return;
				}
				dialog = new NormalDialog(mContext);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						// TODO Auto-generated method stub
						isCancelCheck = true;
					}

				});
				dialog.setTitle(mContext.getResources().getString(
						R.string.check_update));
				dialog.showLoadingDialog();
				dialog.setCanceledOnTouchOutside(false);
				isCancelCheck = false;
				new UpdateCheckVersionThread(handler).start();
			}

		} else if (i == R.id.center_about) {// Intent i = new Intent(mContext,AboutActivity.class);
			// mContext.startActivity(i);

			NormalDialog about = new NormalDialog(mContext);
			about.showAboutDialog();

		} else if (i == R.id.account_set) {
			Intent goAccount_set = new Intent();
			goAccount_set.setClass(mContext, AccountInfoActivity.class);
			startActivity(goAccount_set);

		} else if (i == R.id.system_set) {
			Intent goSys_set = new Intent();
			goSys_set.setClass(mContext, SettingSystemActivity.class);
			startActivity(goSys_set);

		} else if (i == R.id.system_message) {
			Intent goSysMsg = new Intent(mContext, SysMsgActivity.class);
			startActivity(goSysMsg);

//		case R.id.qr_code_layout:
//			Intent go_qr = new Intent(mContext, QRcodeActivity.class);
//			mContext.startActivity(go_qr);
//			break;
//		case R.id.alarm_set_btn:
//			Intent go_alarm_set = new Intent(mContext, AlarmSetActivity.class);
//			startActivity(go_alarm_set);
//			break;
//		case R.id.mailAdr:
//			Intent login = new Intent();
//			login.setAction(Constants.Action.ACTION_SWITCH_USER);
//			mContext.sendBroadcast(login);
//			break;
		} else if (i == R.id.r_mall) {
			Intent go_mall = new Intent(mContext, MallActivity.class);
			startActivity(go_mall);

		} else if (i == R.id.r_recommend_product) {
			SharedPreferencesManager.getInstance().putNoReadCount(mContext, 0);
			bt_system_message.setVisibility(View.GONE);
			Intent go_recommend_information = new Intent(mContext, RecommendInformationActivity.class);
			startActivity(go_recommend_information);

		} else if (i == R.id.r_help) {
			Locale locale = getResources().getConfiguration().locale;
			String language = locale.getLanguage();
			String url = SharedPreferencesManager.getInstance().getHelpUrl(mContext);
			int index = SharedPreferencesManager.getInstance().getHelpIndexUrl(mContext);
			if (url == null || url.equals("")) {
				return;
			}
			//有些网页加后面那些参数会显示不出来
			if (index != 0) {
				if (hasQuestionMark(url)) {
					url = url + "&gwsys=android&gwlan=" + language;
				} else {
					url = url + "?gwsys=android&gwlan=" + language;
				}
			}
//			Uri help_url=Uri.parse(url);
//			Intent intent = new Intent(Intent.ACTION_VIEW, help_url);
//          startActivity(intent);
			Intent help = new Intent(mContext, MallActivity.class);
			help.putExtra("isHelpUrl", true);
			help.putExtra("url", url);
			startActivity(help);

		} else {
		}
	}
    public boolean hasQuestionMark(String url){
    	return url.contains("?");
    }
	class MyHandler extends Handler {
		private boolean hasCancel=false;//add by dxs
		@Override
		public void handleMessage(Message msg) {
			isUpdate=false;
			if(hasCancel)return;
			switch (msg.what) {
			case Constants.Update.CHECK_UPDATE_HANDLE_FALSE:
				isNeedUpdate=false;
//				if (null != dialog) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (isCancelCheck) {
//					return;
//				}
//
//				dialog = new NormalDialog(mContext, mContext.getResources()
//						.getString(R.string.update_prompt_title),
//						Utils.getFormatString(R.string.update_check_false,
//								MyUtils.getVersion()), "", "");
//				dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
//				dialog.showDialog();
				iv_new_version.setVisibility(View.GONE);
				iv_new_version_right_arrow.setVisibility(View.GONE);
				tx_update.setText(getResources().getString(R.string.update_check_false));
				tx_update.setVisibility(View.VISIBLE);
				break;
			case Constants.Update.CHECK_UPDATE_HANDLE_TRUE:
				isNeedUpdate=true;
				if (null != dialog) {
					dialog.dismiss();
					dialog = null;
				}
				iv_new_version.setVisibility(View.VISIBLE);
				iv_new_version_right_arrow.setVisibility(View.VISIBLE);
				tx_update.setVisibility(View.GONE);
				if (isCancelCheck) {
					return;
				}
				isCancelCheck=true;
				Intent i = new Intent(Constants.Action.ACTION_UPDATE);
				i.putExtra("updateDescription",msg.obj.toString());
				mContext.sendBroadcast(i);
				break;
			}
		}

		public void cancel(){
			hasCancel=true;
		}
	}

	public void updateSysMsg() {
		List<SysMessage> sysmessages = DataManager.findSysMessageByActiveUser(
				mContext, NpcCommon.mThreeNum);
		boolean isNewSysMsg = false;
		for (SysMessage msg : sysmessages) {
			if (msg.msgState == SysMessage.MESSAGE_STATE_NO_READ) {
				isNewSysMsg = true;
			}
		}

		if (isNewSysMsg) {
			sysMsg_notify_img.setVisibility(RelativeLayout.VISIBLE);
		} else {
			sysMsg_notify_img.setVisibility(RelativeLayout.GONE);
		}
	}
	//精品推荐里面没有读取信息的条数
    public void showNoReadCount(){
    	int Count=SharedPreferencesManager.getInstance().getNoReadCount(mContext);
//		bt_system_message.setText(String.valueOf(Count));
		if(Count>0){
			bt_system_message.setVisibility(View.VISIBLE);
		}else{
			bt_system_message.setVisibility(View.GONE);
		}
    }
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isRegFilter) {
			isRegFilter = false;
			mContext.unregisterReceiver(mReceiver);
		}
	}
}
