package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.SearchWather;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.MyPassLinearLayout;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.nuowei.smarthome.R;

import org.json.JSONObject;

import java.util.List;

import it.sauronsoftware.base64.Base64;

public class AddContactActivity extends BaseActivity implements OnClickListener {
	private static final String DEVICEID="DevId";
	private static final String DEVICETYPE="DevType";
	private static final String DEVICEPWD="Password";
	private TextView mNext;
	private ImageView mBack;
	Context mContext;
	EditText contactId;
	Contact mContact;
	Button ensure;
	EditText input_device_id, input_device_name, input_device_password;
	Contact saveContact = new Contact();
	private MyPassLinearLayout llPass;
	private String result="";
	private int DeviceType;
    private  boolean isRegFilter=false;
	private int TIME_OUT = 10* 1000;
	private int count=0;
	NormalDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		mContact = (Contact) getIntent().getSerializableExtra("contact");
		result=getIntent().getStringExtra("result");
		mContext = this;
		initCompent();
		regFilter();
	}
	public void initCompent() {
		// contactId = (EditText) findViewById(R.id.contactId);
		mBack = (ImageView) findViewById(R.id.back_btn);
		mNext = (TextView) findViewById(R.id.next);
		ensure = (Button) findViewById(R.id.bt_ensure);
		input_device_id = (EditText) findViewById(R.id.input_device_id);
		input_device_name = (EditText) findViewById(R.id.input_contact_name);
		input_device_password = (EditText) findViewById(R.id.input_contact_pwd);
		// if(null!=mContact){
		// contactId.setText(mContact.contactId);
		// }

		input_device_password
				.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
		mBack.setOnClickListener(this);
		mNext.setOnClickListener(this);
		ensure.setOnClickListener(this);
		llPass = (MyPassLinearLayout) findViewById(R.id.ll_p);
		llPass.setEditextListener(input_device_password);
		input_device_password.addTextChangedListener(new SearchWather(input_device_password));
		initMuAdd();
	}
	public void regFilter(){
		IntentFilter filter=new IntentFilter();
		filter.addAction(Constants.Action.GET_FRIENDS_STATE);
		filter.addAction(Constants.P2P.ACK_RET_CHECK_PASSWORD);
		registerReceiver(br,filter);
		isRegFilter=true;
	}
	BroadcastReceiver br=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Constants.Action.GET_FRIENDS_STATE)){
				String [] contactIDs=intent.getStringArrayExtra("contactIDs");
				int [] status=intent.getIntArrayExtra("status");
				if(contactIDs==null){
					return;
				}
				for(int i=0;i<contactIDs.length;i++){
					if(contactIDs[i].equals(saveContact.contactId)){
						if(status[i]==Constants.DeviceState.ONLINE){
							if(dialog!=null&&dialog.isShowing()){
								P2PHandler.getInstance().checkPassword(saveContact.contactId,saveContact.contactPassword);
							}
						}else{
							if(dialog!=null&&dialog.isShowing()){
								dialog.dismiss();
								T.showShort(mContext,R.string.ensure_device_online);
							}
						}
                        break;
					}
				}

			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_CHECK_PASSWORD)){
				int result = intent.getIntExtra("result", -1);
				String deviceId=intent.getStringExtra("deviceId");
				if(deviceId.equals(saveContact.contactId)){
					if(result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS||result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS){
						if(dialog!=null&&dialog.isShowing()){
							dialog.dismiss();
							addContact();
							T.showShort(mContext, R.string.add_success);
							finish();
						}
					}else if(result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
						if(dialog!=null&&dialog.isShowing()){
							dialog.dismiss();
							T.showShort(mContext,R.string.password_error);
						}
					}else if(result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
						P2PHandler.getInstance().checkPassword(saveContact.contactId,saveContact.contactPassword);

					}
				}

			}

		}
	};

	private void initMuAdd() {
		if(!TextUtils.isEmpty(result)){
			try {
				String deleteFirst=result.substring(result.indexOf("#")+1);
				String json=Base64.decode(deleteFirst);
				paserJson(json);
			} catch (Exception e) {
				T.showLong(mContext, R.string.error_qcode);
			}
		}
	}
	
	private void paserJson(String json) throws Exception{
		JSONObject obj=new JSONObject(json);
		if(!json.contains(DEVICEID)){
			throw new Exception("json not contains deviceid");
		}
		DeviceType=obj.getInt(DEVICETYPE);
		String id=obj.getString(DEVICEID);
		String pwd="";
        if(json.contains(DEVICEPWD)){
        	pwd=obj.getString(DEVICEPWD);
		}
		
		input_device_id.setText(id);
		if(DeviceType==P2PValue.DeviceType.NVR){
			input_device_name.setText("NVR"+id);
		}else{
			input_device_name.setText("Cam"+id);
		}
		input_device_password.setText(pwd);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			this.finish();
			break;
		case R.id.next:
			next();
			break;
		case R.id.bt_ensure:
			next();
			break;
		default:
			break;
		}
	}

	public void next() {
		String input_id = input_device_id.getText().toString().trim();
		String input_name = input_device_name.getText().toString().trim();
		String input_pwd = input_device_password.getText().toString().trim();
		if (TextUtils.isEmpty(input_id)) {
			T.showShort(mContext, R.string.input_contact_id);
			return;
		}
		if (input_id.charAt(0) == '0' || input_id.length() > 9
				|| !Utils.isNumeric(input_id)) {
			T.show(mContext, R.string.device_id_invalid, Toast.LENGTH_SHORT);
			return;
		}
		if (null != FList.getInstance().isContact(input_id)) {
			T.showShort(mContext, R.string.contact_already_exist);
			return;
		}

		int type;
		if (input_id.charAt(0) == '0') {
			type = P2PValue.DeviceType.PHONE;
		} else {
			type = P2PValue.DeviceType.UNKNOWN;
		}
		if (TextUtils.isEmpty(input_name)) {
			T.showShort(mContext, R.string.input_contact_name);
			return;
		}
		saveContact.contactId = input_id;
		saveContact.contactType = type;
		saveContact.activeUser = NpcCommon.mThreeNum;
		saveContact.messageCount = 0;
		List<Contact> lists = DataManager.findContactByActiveUser(mContext,
				NpcCommon.mThreeNum);
		for (Contact c : lists) {
			if (c.contactName.equals(input_name)) {
				T.showShort(mContext, R.string.device_name_exist);
				return;
			}
		}
		if (TextUtils.isEmpty(input_pwd)) {
			 T.showShort(this, R.string.input_password);
			 return;
		}
		if (saveContact.contactType != P2PValue.DeviceType.PHONE) {
		 if (!TextUtils.isEmpty(input_pwd)) {
		  if (input_pwd.length() > 30) {
		      T.showShort(mContext, R.string.device_password_invalid);
		      return;
		   }
		 }
	    }

		List<Contact> contactlist = DataManager.findContactByActiveUser(
				mContext, NpcCommon.mThreeNum);
		for (Contact contact : contactlist) {
			if (contact.contactId.equals(saveContact.contactId)) {
				T.showShort(mContext, R.string.contact_already_exist);
				return;
			}
		}
		saveContact.contactName = input_name;
		saveContact.userPassword=input_pwd;
		String pwd=P2PHandler.getInstance().EntryPassword(input_pwd);
		saveContact.contactPassword = pwd;
        int deviceId=Integer.parseInt(saveContact.contactId);
        if(deviceId<256){
            //ip添加不验证在线状态
            P2PHandler.getInstance().checkPassword(saveContact.contactId,saveContact.contactPassword);
        }else{
            //验证是否在线
            String[] contactIds={saveContact.contactId};
            P2PHandler.getInstance().getFriendStatus(contactIds);
        }
		dialog = new NormalDialog(mContext);
		dialog.showLoadingDialog();
		dialog.setCanceledOnTouchOutside(false);
		count++;
		timeoutHandler.sendEmptyMessageDelayed(0,TIME_OUT);
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_ADDCONTACTACTIVITY;
	}

	public void sendSuccessBroadcast() {
		Intent refreshContans = new Intent();
		refreshContans.setAction(Constants.Action.REFRESH_CONTANTS);
		refreshContans.putExtra("contact", saveContact);
		mContext.sendBroadcast(refreshContans);

		Intent add_success = new Intent();
		add_success.setAction(Constants.Action.ADD_CONTACT_SUCCESS);
		add_success.putExtra("contact", saveContact);
		mContext.sendBroadcast(add_success);

		Intent refreshNearlyTell = new Intent();
		refreshNearlyTell
				.setAction(Constants.Action.ACTION_REFRESH_NEARLY_TELL);
		mContext.sendBroadcast(refreshNearlyTell);
	}
	Handler timeoutHandler=new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			count--;
			if(count==0){
				if(dialog!=null&&dialog.isShowing()){
					addContact();
					finish();
				}
			}
			return false;
		}
	});
	private  void addContact(){
		Contact contact=FList.getInstance().isContact(saveContact.contactId);
		if(contact!=null){
			FList.getInstance().update(saveContact);
		}else{
			FList.getInstance().insert(saveContact);
			FList.getInstance().updateLocalDeviceWithLocalFriends();
			sendSuccessBroadcast();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(isRegFilter){
            this.unregisterReceiver(br);
			isRegFilter=false;
		}
		if(timeoutHandler!=null){
			timeoutHandler.removeCallbacksAndMessages(null);
		}
	}
}
