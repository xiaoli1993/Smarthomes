package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.ImageUtils;
import com.jwkj.utils.SearchWather;
import com.jwkj.utils.T;
import com.jwkj.utils.UDPClient;
import com.jwkj.utils.Utils;
import com.jwkj.widget.ConfirmOrCancelDialog;
import com.jwkj.widget.MyPassLinearLayout;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.utils.MyUtils;
import com.nuowei.smarthome.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.nuowei.smarthome.R.string.add_device_back;

public class AddContactNextActivity extends BaseActivity implements
		OnClickListener {
	private static final int RESULT_GETIMG_FROM_CAMERA = 0x11;
	private static final int RESULT_GETIMG_FROM_GALLERY = 0x12;
	private static final int RESULT_CUT_IMAGE = 0x13;
	private TextView mSave;
	private ImageView mBack;
	Context mContext;
	EditText contactPwd;
	NormalDialog dialog;
	Contact mSaveContact;
	EditText createPwd1, createPwd2;
	private Bitmap tempHead;
	boolean isSave = false;
	boolean isCreatePassword = false;
	String input_pwd, input_create_pwd1, input_create_pwd2,input_name;
	boolean isRegFilter;
	String ipFlag;
	boolean isfactory;
	private MyPassLinearLayout llPass;
	private MyPassLinearLayout llPass2;
	String userPassword;
	String ip;
	private int UDP_TIME_OUT = 6 * 1000;
	boolean isSendUdp=false;
    boolean isFirstAckSuccess=true;
    boolean isInitPassword=false;
	private TextView tv_device_id;
	private RelativeLayout r_create_pwd,r_create_re_pwd,r_device_pwd;
	private RelativeLayout r_init_pwd_prompt,r_create_prompt;
	ConfirmOrCancelDialog backDialg;
	private int TIME_OUT = 10* 1000;
	int count=0;
	private int addDeviceMethod;
	private EditText contactName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact_next);
		mSaveContact = (Contact) getIntent().getSerializableExtra("contact");
		isCreatePassword = getIntent().getBooleanExtra("isCreatePassword",
				false);
		isfactory = getIntent().getBooleanExtra("isfactory", false);
		ipFlag = getIntent().getStringExtra("ipFlag");
		ip=getIntent().getStringExtra("ip");
		addDeviceMethod=getIntent().getIntExtra("addDeviceMethod",Constants.AddDeviceMethod.LAN_ADD);
		mContext = this;
		initCompent();
		regFilter();
		UDPClient.getInstance().startListner(8899);
	}

	public void initCompent() {
		tv_device_id=(TextView)findViewById(R.id.tv_device_id);
		contactPwd = (EditText) findViewById(R.id.contactPwd);
		contactPwd.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		r_create_pwd=(RelativeLayout)findViewById(R.id.r_create_pwd);
		r_create_re_pwd=(RelativeLayout)findViewById(R.id.r_create_re_pwd);
		r_device_pwd=(RelativeLayout)findViewById(R.id.r_device_pwd);
		r_init_pwd_prompt=(RelativeLayout)findViewById(R.id.r_init_pwd_prompt);
		r_create_prompt=(RelativeLayout)findViewById(R.id.r_create_prompt);
		createPwd1 = (EditText) findViewById(R.id.et_pwd);
		createPwd2 = (EditText) findViewById(R.id.et_repwd);
		createPwd1.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		createPwd2.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		contactName=(EditText)findViewById(R.id.contactName);
		mBack = (ImageView) findViewById(R.id.back_btn);
		mSave = (TextView) findViewById(R.id.save);
		llPass = (MyPassLinearLayout) findViewById(R.id.ll_p);
		llPass.setEditextListener(contactPwd);
		llPass2 = (MyPassLinearLayout) findViewById(R.id.ll_p2);
		llPass2.setEditextListener(createPwd1);
		showHideUI();
		Contact contact = FList.getInstance().isContact(mSaveContact.contactId);
		if(contact!=null){
			contactName.setText(contact.contactName);
		}
		tv_device_id.setText("ID "+mSaveContact.contactId);
		contactPwd.addTextChangedListener(new SearchWather(contactPwd));
		createPwd2.addTextChangedListener(new SearchWather(createPwd2));
		mBack.setOnClickListener(this);
		mSave.setOnClickListener(this);
		contactName.requestFocus();
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_SET_INIT_PASSWORD);
		filter.addAction(Constants.P2P.RET_SET_INIT_PASSWORD);
		filter.addAction(Constants.P2P.ACK_RET_CHECK_PASSWORD);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.P2P.RET_SET_INIT_PASSWORD)) {
				int result = intent.getIntExtra("result", -1);
				isSendUdp=false;
				isInitPassword=false;
				if (result == Constants.P2P_SET.INIT_PASSWORD_SET.SETTING_SUCCESS) {
					addDevice();
					FList.getInstance().updateLocalDeviceFlag(
							mSaveContact.contactId,
							Constants.DeviceFlag.ALREADY_SET_PASSWORD);
					sendSuccessBroadcast();
					T.showShort(mContext, R.string.add_success);
					if(addDeviceMethod==Constants.AddDeviceMethod.WIRE_METHOD){
						sendWireAddSuccess();
					}
					finish();
				} else if (result == Constants.P2P_SET.INIT_PASSWORD_SET.ALREADY_EXIST_PASSWORD) {
					P2PHandler.getInstance().checkPassword(ipFlag, input_create_pwd1);
//					Intent createPwdSuccess = new Intent();
//					createPwdSuccess
//							.setAction(Constants.Action.UPDATE_DEVICE_FALG);
//					createPwdSuccess.putExtra("threeNum",
//							mSaveContact.contactId);
//					mContext.sendBroadcast(createPwdSuccess);
//					T.showShort(mContext, R.string.already_init_passwd);
//					finish();
				} else {
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_INIT_PASSWORD)) {
				int result = intent.getIntExtra("result", -1);
				String deviceId=intent.getStringExtra("deviceId");
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					T.showShort(mContext, R.string.password_error);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					T.showShort(mContext, R.string.net_error_operator_fault);
					UdpSetInitPwd();
					excuteTimeOutTimer();
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS){
					repeatInitPwd();
					isFirstAckSuccess=false;
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_CHECK_PASSWORD)){
				int result = intent.getIntExtra("result", -1);
				String deviceId=intent.getStringExtra("deviceId");
				if(deviceId.equals(mSaveContact.contactId)||deviceId.equals(String.valueOf(ipFlag))){
					if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS||result==Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
						addDevice();
						FList.getInstance().updateLocalDeviceFlag(
								mSaveContact.contactId,
								Constants.DeviceFlag.ALREADY_SET_PASSWORD);
						sendSuccessBroadcast();
						T.showShort(mContext,R.string.add_success);
						if(addDeviceMethod==Constants.AddDeviceMethod.WIRE_METHOD){
							sendWireAddSuccess();
						}
						finish();
					} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
						if (null != dialog && dialog.isShowing()) {
							dialog.dismiss();
							dialog = null;
						}
						if(isCreatePassword){
							T.showShort(mContext, R.string.already_init_passwd);
						}else{
							T.showShort(mContext,R.string.password_error);
						}
					} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
						if(isCreatePassword){
							P2PHandler.getInstance().checkPassword(deviceId, input_create_pwd1);
						}else{
							P2PHandler.getInstance().checkPassword(deviceId,mSaveContact.contactPassword);
						}
					}
				}
				
			}
		}
	};
	public void UdpSetInitPwd(){
		final byte[] messageudp = getSendMessage(NpcCommon.mThreeNum,
				input_create_pwd1);
		isSendUdp=true;
		new Thread() {
			public void run() {
				while(isSendUdp){
					try {
						UDPClient.getInstance().send(messageudp, 8899, ip);
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			};
		}.start();
	}
	public void repeatInitPwd(){
		if(isFirstAckSuccess){
			isInitPassword=true;
			new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int i=1; 
					while(isInitPassword){
                       try {
                    	P2PHandler.getInstance().setInitPassword(ipFlag,input_create_pwd1,userPassword,userPassword);
						Thread.sleep(5000);
						i++;
					   } catch (InterruptedException e) {
						e.printStackTrace();
					   }
					}
				}
			}.start();
		}
	}
	private byte[] getSendMessage(String AppID, String pwd) {
		byte[] realPwd = P2PHandler.getInstance().intToByte4(Integer.parseInt(pwd));
		byte[] ppp = P2PHandler.getInstance().P2PEntryPassword(realPwd);
		int App = Integer.parseInt(AppID);
		byte[] aaa = P2PHandler.getInstance().intToByte4(App);
		byte[] message = new byte[48];
		message[0] = 48;
		System.arraycopy(ppp, 0, message, 4, ppp.length);
		System.arraycopy(aaa, 0, message, 44, aaa.length);
		Log.e("dxs", "mssage-->" + Arrays.toString(message) + "pwd--->" + pwd
				+ "app-->" + AppID);
		return message;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == RESULT_GETIMG_FROM_CAMERA) {
			try {
				Bundle extras = data.getExtras();
				tempHead = (Bitmap) extras.get("data");
				Log.e("my", tempHead.getWidth() + ":" + tempHead.getHeight());
				ImageUtils.saveImg(tempHead, Constants.Image.USER_HEADER_PATH,
						Constants.Image.USER_HEADER_TEMP_FILE_NAME);

				Intent cutImage = new Intent(mContext, CutImageActivity.class);
				cutImage.putExtra("contact", mSaveContact);
				startActivityForResult(cutImage, RESULT_CUT_IMAGE);
			} catch (NullPointerException e) {
				Log.e("my", "用户终止..");
			}
		} else if (requestCode == RESULT_GETIMG_FROM_GALLERY) {

			try {
				Uri uri = data.getData();
				tempHead = ImageUtils.getBitmap(
						ImageUtils.getAbsPath(mContext, uri),
						Constants.USER_HEADER_WIDTH_HEIGHT,
						Constants.USER_HEADER_WIDTH_HEIGHT);
				ImageUtils.saveImg(tempHead, Constants.Image.USER_HEADER_PATH,
						Constants.Image.USER_HEADER_TEMP_FILE_NAME);

				Intent cutImage = new Intent(mContext, CutImageActivity.class);
				cutImage.putExtra("contact", mSaveContact);
				startActivityForResult(cutImage, RESULT_CUT_IMAGE);

			} catch (NullPointerException e) {
				Log.e("my", "用户终止..");
			}
		} else if (requestCode == RESULT_CUT_IMAGE) {
			Log.e("my", resultCode + "");
			try {
				if (resultCode == 1) {
					Intent refreshContans = new Intent();
					refreshContans.setAction(Constants.Action.REFRESH_CONTANTS);
					refreshContans.putExtra("contact", mSaveContact);
					mContext.sendBroadcast(refreshContans);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			if(backDialg!=null&&backDialg.isShowing()){
				return;
			}
			backDialg=new ConfirmOrCancelDialog(mContext);
			backDialg.setTitle(getResources().getString(add_device_back));
			backDialg.setTextYes(getResources().getString(R.string.prepoint_abandon));
			backDialg.setTextNo(getResources().getString(R.string.cancel));
			backDialg.setOnYesClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(addDeviceMethod==Constants.AddDeviceMethod.WIFI_METHOD||addDeviceMethod==Constants.AddDeviceMethod.WIRE_METHOD){
						Intent exit=new Intent();
						exit.setAction(Constants.Action.EXIT_ADD_DEVICE);
						sendBroadcast(exit);
					}
					finish();
				}
			});
			backDialg.setOnNoClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					backDialg.dismiss();
				}
			});
			backDialg.show();
			break;
		case R.id.save:
			save();
			break;
		default:
			break;
		}
	}

	public void destroyTempHead() {
		if (tempHead != null && !tempHead.isRecycled()) {
			tempHead.recycle();
			tempHead = null;
		}
	}

	void save() {
		input_pwd = contactPwd.getText().toString();
		input_create_pwd1 = createPwd1.getText().toString();
		input_create_pwd2 = createPwd2.getText().toString();
		input_name=contactName.getText().toString();
		if (input_name != null && input_name.trim().equals("")) {
			T.showShort(mContext, R.string.input_contact_name);
			return;
		}

		if (isCreatePassword) {
			if (null == input_create_pwd1 || "".equals(input_create_pwd1)) {
				T.showShort(this, R.string.inputpassword);
				return;
			}
			if(input_create_pwd1.length()>30){
				T.showShort(this,R.string.device_password_invalid);
			}

			if (null == input_create_pwd2 || "".equals(input_create_pwd2)) {
				T.showShort(this, R.string.reinputpassword);
				return;
			}
			if (!input_create_pwd1.equals(input_create_pwd2)) {
				T.showShort(this, R.string.differentpassword);
				return;
			}
			if (llPass2.isWeakpassword()) {
				T.showShort(mContext, R.string.simple_password);
				return;
			}
			if (null == dialog) {
				dialog = new NormalDialog(this, this.getResources().getString(
						R.string.verification), "", "", "");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
			}
			String tips = "---------------------------";
			if (null != ipFlag && !ipFlag.equals("")
					&& MyUtils.isNumeric(ipFlag)) {
//				tips = "--IP-->" + ipFlag + "-ID-->" + mSaveContact.contactId;
//				dialog.setTitle(tips);
				userPassword=input_create_pwd1;
				input_create_pwd1 = P2PHandler.getInstance().EntryPassword(
						input_create_pwd1);
				count++;
				P2PHandler.getInstance().setInitPassword(ipFlag,
						input_create_pwd1,userPassword,userPassword);
				timeoutHandler.sendEmptyMessageDelayed(0,TIME_OUT);
//				UdpSetInitPwd();
//				excuteTimeOutTimer();
			} else {
				// P2PHandler.getInstance().setInitPassword(
				// mSaveContact.contactId, input_create_pwd1);
				// Log.e("sendwifi", "contactId=" + mSaveContact.contactId +
				// "--"
				// + "device_pwd=" + input_create_pwd1);
				T.showShort(mContext, "IP没有找到");
			}
			Log.e("dxsinitPassWord", "tips-->" + tips);
			dialog.showDialog();

		} else {
			if (input_pwd == null || input_pwd.trim().equals("")) {
				T.showShort(this, R.string.input_password);
				return;
			}
			if (mSaveContact.contactType != P2PValue.DeviceType.PHONE) {
				if (input_pwd != null && !input_pwd.trim().equals("")) {
					if (input_pwd.length() >30|| input_pwd.charAt(0) == '0') {
						T.showShort(mContext, R.string.device_password_invalid);
						return;
					}
				}
			}
			dialog = new NormalDialog(mContext);
			dialog.showLoadingDialog();
			dialog.setCanceledOnTouchOutside(false);
			count++;
			timeoutHandler.sendEmptyMessageDelayed(0,TIME_OUT);
			if (isfactory == false) {
				List<Contact> lists = DataManager.findContactByActiveUser(
						mContext, NpcCommon.mThreeNum);
				for (Contact c : lists) {
					if (c.contactName.equals(input_name)) {
						T.showShort(mContext, R.string.device_name_exist);
						return;
					}
					if (c.contactId.equals(mSaveContact.contactId)) {
						T.showShort(mContext, R.string.contact_already_exist);
						return;
					}
				}
				mSaveContact.contactName = input_name;
				mSaveContact.userPassword=input_pwd;
				input_pwd=P2PHandler.getInstance().EntryPassword(input_pwd);
				mSaveContact.contactPassword =input_pwd;
				if(null != ipFlag && !ipFlag.equals("")
						&& MyUtils.isNumeric(ipFlag)){
					P2PHandler.getInstance().checkPassword(ipFlag,mSaveContact.contactPassword);
				}else{
					P2PHandler.getInstance().checkPassword(mSaveContact.contactId,mSaveContact.contactPassword);
				}
			} else {
				Contact contact = FList.getInstance().isContact(
						mSaveContact.contactId);
				if (null != contact) {
					contact.contactName = input_name;
					contact.userPassword=input_pwd;
					input_pwd=P2PHandler.getInstance().EntryPassword(input_pwd);
					contact.contactPassword = input_pwd;
					mSaveContact=contact;
				} else {
					mSaveContact.contactName = input_name;
					mSaveContact.userPassword=input_pwd;
					input_pwd=P2PHandler.getInstance().EntryPassword(input_pwd);
					mSaveContact.contactPassword = input_pwd;
				}
				if(null != ipFlag && !ipFlag.equals("")
						&& MyUtils.isNumeric(ipFlag)){
					P2PHandler.getInstance().checkPassword(ipFlag,mSaveContact.contactPassword);
				}else{
					P2PHandler.getInstance().checkPassword(mSaveContact.contactId,mSaveContact.contactPassword);
				}

			}
		}
	}

	public void sendSuccessBroadcast() {
		Intent refreshContans = new Intent();
		refreshContans.setAction(Constants.Action.REFRESH_CONTANTS);
		refreshContans.putExtra("contact", mSaveContact);
		mContext.sendBroadcast(refreshContans);

		Intent add_success = new Intent();
		add_success.setAction(Constants.Action.ADD_CONTACT_SUCCESS);
		add_success.putExtra("contact", mSaveContact);
		mContext.sendBroadcast(add_success);

		Intent refreshNearlyTell = new Intent();
		refreshNearlyTell
				.setAction(Constants.Action.ACTION_REFRESH_NEARLY_TELL);
		mContext.sendBroadcast(refreshNearlyTell);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		destroyTempHead();
		isInitPassword=false;
		isSendUdp=false;
		if (isCreatePassword) {
			Contact contact = FList.getInstance().isContact(
					mSaveContact.contactId);
			if (!isSave && null == contact) {
				File file = new File(Constants.Image.USER_HEADER_PATH
						+ NpcCommon.mThreeNum + "/" + mSaveContact.contactId);
				Utils.deleteFile(file);
			}
		} else {
			if (!isSave) {
				File file = new File(Constants.Image.USER_HEADER_PATH
						+ NpcCommon.mThreeNum + "/" + mSaveContact.contactId);
				Utils.deleteFile(file);
			}
		}

		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
		UDPClient.getInstance().StopListen();
		if(timeOutTimer!=null){
			try {
				timeOutTimer.cancel();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	// 超时计时器
	    Timer timeOutTimer;
		public void excuteTimeOutTimer() {

			timeOutTimer = new Timer();
			TimerTask mTask = new TimerTask() {
				@Override
				public void run() {
					if(mContext!=null){
						((AddContactNextActivity)mContext).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// 弹出一个对话框
								if(dialog!=null&&dialog.isShowing()){
									dialog.dismiss();
								}
								addDevice();
//								T.showShort(mContext, R.string.time_out);
								sendSuccessBroadcast();
								finish();
								isSendUdp=false;
							}
						});
					}
				}
			};
			timeOutTimer.schedule(mTask, UDP_TIME_OUT);
		}
    public void showHideUI(){
		if (isCreatePassword) {
			createPwd1.requestFocus();
			r_create_pwd.setVisibility(View.VISIBLE);
			r_create_re_pwd.setVisibility(View.VISIBLE);
			r_create_prompt.setVisibility(View.VISIBLE);
			r_device_pwd.setVisibility(View.GONE);
			r_init_pwd_prompt.setVisibility(View.GONE);
		} else {
			contactPwd.requestFocus();
			r_create_pwd.setVisibility(View.GONE);
			r_create_re_pwd.setVisibility(View.GONE);
			r_create_prompt.setVisibility(View.GONE);
			if (mSaveContact.contactType != P2PValue.DeviceType.PHONE) {
				r_device_pwd.setVisibility(View.VISIBLE);
				r_init_pwd_prompt.setVisibility(View.VISIBLE);
			} else {
				r_device_pwd.setVisibility(View.GONE);
			}
		}
	}
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_ADDCONTACTNEXTACTIVITY;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if(backDialg!=null&&backDialg.isShowing()){
				return true;
			}
			backDialg=new ConfirmOrCancelDialog(mContext);
			backDialg.setTitle(getResources().getString(add_device_back));
			backDialg.setTextYes(getResources().getString(R.string.prepoint_abandon));
			backDialg.setTextNo(getResources().getString(R.string.cancel));
			backDialg.setOnYesClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(addDeviceMethod==Constants.AddDeviceMethod.WIFI_METHOD||addDeviceMethod==Constants.AddDeviceMethod.WIRE_METHOD){
						Intent exit=new Intent();
						exit.setAction(Constants.Action.EXIT_ADD_DEVICE);
						sendBroadcast(exit);
					}
					finish();
				}
			});
			backDialg.setOnNoClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					backDialg.dismiss();
				}
			});
			backDialg.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private  void addDevice(){
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		if(isCreatePassword){
			Contact contact = FList.getInstance().isContact(
					mSaveContact.contactId);
			if (null != contact) {
				contact.contactName = input_name;
				contact.contactPassword = input_create_pwd1;
				contact.userPassword=userPassword;
				FList.getInstance().update(contact);
			} else {
				mSaveContact.contactName = input_name;
				mSaveContact.contactPassword = input_create_pwd1;
				mSaveContact.userPassword=userPassword;
				FList.getInstance().insert(mSaveContact);
			}
			isSave = true;
			FList.getInstance().updateLocalDeviceWithLocalFriends();
		}else{
			Contact contact=FList.getInstance().isContact(mSaveContact.contactId);
			if(contact!=null){
				FList.getInstance().update(mSaveContact);
			}else{
				FList.getInstance().insert(mSaveContact);
				FList.getInstance().updateLocalDeviceWithLocalFriends();
				isSave = true;
			}
		}
	}
	Handler timeoutHandler=new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			count--;
			if(count==0){
				if(dialog!=null&&dialog.isShowing()){
					addDevice();
					sendSuccessBroadcast();
					finish();
				}
			}
			return false;
		}
	});
	private void sendWireAddSuccess(){
		Intent i=new Intent();
		i.setAction(Constants.Action.WIRE_ADD_SUCCESS);
		sendBroadcast(i);
	}
}
