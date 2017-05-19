package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jwkj.data.Contact;
import com.jwkj.global.Constants;
import com.jwkj.utils.T;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.utils.MyUtils;
import com.nuowei.ipclibrary.R;

public class FTPControlActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private Contact mContact;
	private boolean isRegFilter = false;
	private ImageView back;
	private EditText edhostname;
	private EditText edusrname;
	private EditText edpasswd;
	private EditText edsvrport;
	private Button saveBtn;

	private String hostname = "";
	private String usrname = "";
	private String passwd = "";
	private short svrport;
	private short usrflag;
	private NormalDialog dialog_loading;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_ftp_control);
		mContext = this;
		mContact = (Contact) getIntent().getSerializableExtra("mContact");
		getFTPInfo();
		initComponent();
		setEditTextInfo();
		regFilter();
	}

	private void initComponent() {
		back = (ImageView) findViewById(R.id.ftp_back_btn);
		edhostname = (EditText) findViewById(R.id.hostname);
		edusrname = (EditText) findViewById(R.id.usrname);
		edpasswd = (EditText) findViewById(R.id.passwd);
		edsvrport = (EditText) findViewById(R.id.svrport);
		saveBtn = (Button) findViewById(R.id.ftp_save_btn);
		back.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
	}

	public void getFTPInfo() {
		hostname = getIntent().getStringExtra("hostname");
		usrname = getIntent().getStringExtra("usrname");
		passwd = getIntent().getStringExtra("passwd");
		svrport = getIntent().getShortExtra("svrport", (short) 0);
		usrflag = getIntent().getShortExtra("usrflag", (short) 0);
	}

	public void setEditTextInfo() {
		if (hostname.equals("null")) {
			edhostname.setText("");
		} else {
			edhostname.setText(hostname);
		}
		if (usrname.equals("null")) {
			edusrname.setText("");
		} else {
			edusrname.setText(usrname);
		}
		if (passwd.equals("null")) {
			edpasswd.setText("");
		} else {
			edpasswd.setText(passwd);
		}
		if (svrport != 0) {
			int svp;
			if (svrport<0) {
				svp=((int)svrport)+65536;
			}else {
				svp=(int)svrport;
			}
			edsvrport.setText(String.valueOf(svp));
		}
	}

	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_GET_FTP_CONFIG_INFO);
		filter.addAction(Constants.P2P.RET_SET_FTP_CONFIG_INFO);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String iSrcID = intent.getStringExtra("iSrcID");
			byte[] data = intent.getByteArrayExtra("data");
			if (intent.getAction()
					.equals(Constants.P2P.RET_SET_FTP_CONFIG_INFO)) {
                if (dialog_loading != null) {
                    dialog_loading.dismiss();
                }
				if (data[1] == 0) {
					setInfo(data);
					Intent i = new Intent();
					i.putExtra("mContact", mContact);
					i.putExtra("hostname", hostname);
					i.putExtra("usrname", usrname);
					i.putExtra("passwd", passwd);
					i.putExtra("svrport", svrport);
					i.putExtra("usrflag", usrflag);
					FTPControlActivity.this.setResult(RESULT_OK, i);
					FTPControlActivity.this.finish();
				}else if(data[1] == 106){
					T.showLong(mContext,R.string.ftp_set_error);
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.ftp_back_btn) {
			this.finish();

		} else if (i == R.id.ftp_save_btn) {
			changeFTPInfo();

		} else {
		}
	}

	public void changeFTPInfo() {

		String hName = edhostname.getText().toString().trim();
		String uName = edusrname.getText().toString().trim();
		String pass = edpasswd.getText().toString().trim();
		short sPort = 0;
		if (hName.equals("")) {
			T.showShort(mContext, R.string.hostname_not_empty);
			return;
		}
		if (hName.getBytes().length>31) {
			T.showShort(mContext, R.string.hostname_length_big);
			return;
		}
		if (uName.equals("")) {
			T.showShort(mContext, R.string.usrname_not_empty);
			return;
		}
		if (uName.getBytes().length>26) {
			T.showShort(mContext, R.string.user_name_no_exceed26);
			return;
		}
		if (pass.equals("")) {
			T.showShort(mContext, R.string.passwd_not_empty);
			return;
		}
		if (pass.getBytes().length>20) {
			T.showShort(mContext, R.string.password_length_error);
			return;
		}
		if (edsvrport.getText().toString().trim().equals("")) {
			T.showShort(mContext, R.string.svrport_not_empty);
			return;
		}
		int port=Integer.parseInt(edsvrport.getText().toString().trim());
		if (port < 1 || port > 65535) {
			T.showShort(mContext, R.string.port_between);
			return;
		} else {
			sPort = (short)(Integer.parseInt(edsvrport.getText().toString().trim()));
		}
		if (hName.equals(hostname) && uName.equals(usrname)
				&& pass.equals(passwd) && sPort == svrport) {
			T.showShort(mContext, R.string.no_modification);
			return;
		}
		dialog_loading = new NormalDialog(mContext, mContext.getResources()
				.getString(R.string.setting_loading), "", "", "");
		dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
		dialog_loading.showDialog();
		P2PHandler.getInstance().setFTPConfigInfo(mContact.getContactId(),
				mContact.getPassword(), hName, uName, pass, sPort, usrflag);
		Log.e("dxsTest","usrflag:"+usrflag);
	}

	public void setInfo(byte[] data) {
		if (data.length < 102) {
			return;
		}
		byte[] hostnames = new byte[32];
		System.arraycopy(data, 2, hostnames, 0, 32);
		byte[] usrnames = new byte[32];
		System.arraycopy(data, 34, usrnames, 0, 32);
		byte[] passwds = new byte[32];
		System.arraycopy(data, 66, passwds, 0, 32);
		byte[] svrports = new byte[2];
		System.arraycopy(data, 98, svrports, 0, 2);
		byte[] usrflags = new byte[2];
		System.arraycopy(data, 100, usrflags, 0, 2);

		hostname = new String(hostnames).trim();
		usrname = new String(usrnames).trim();
		passwd = new String(passwds).trim();
		svrport = MyUtils.byte2ToShort(svrports, 0);
		usrflag = MyUtils.byte2ToShort(usrflags, 0);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// if (mContact != null) {
		// P2PHandler.getInstance().getFTPConfigInfo(mContact.contactId,
		// mContact.contactPassword);
		// }
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isRegFilter) {
			isRegFilter = false;
			this.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_FTP_CONTROL;
	}
}
