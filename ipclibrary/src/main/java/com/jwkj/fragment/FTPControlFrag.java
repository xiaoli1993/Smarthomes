package com.jwkj.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jwkj.activity.FTPControlActivity;
import com.jwkj.data.Contact;
import com.jwkj.global.Constants;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.SwitchView;
import com.p2p.core.P2PHandler;
import com.p2p.core.global.Constants.ACK_RET_TYPE;
import com.p2p.core.utils.MyUtils;
import com.nuowei.ipclibrary.R;

public class FTPControlFrag extends BaseFragment implements
		View.OnClickListener {
	private Context mContext;
	private Contact mContact;
	private NormalDialog dialog;
	private boolean isRegFilter = false;
	private TextView txName;
	private SwitchView svBtn;
	private Button operationBtn;
	private String hostname = "";
	private String usrname = "";
	private String passwd = "";
	private short svrport = 0;
	private short usrflag = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = getActivity();
		mContact = (Contact) getArguments().getSerializable("contact");
		View view = inflater.inflate(R.layout.fragment_ftp_control, container,
				false);
		initComponent(view);
		if (mContact != null) {
			P2PHandler.getInstance().getFTPConfigInfo(mContact.getContactId(),
					mContact.getPassword());
		}
		return view;
	}

	public void initComponent(View view) {
		txName = (TextView) view.findViewById(R.id.ftp_tx_name);
		svBtn = (SwitchView) view.findViewById(R.id.ftp_sv_btn);
		operationBtn = (Button) view.findViewById(R.id.ftp_operation_btn);
		svBtn.setOnClickListener(this);
		operationBtn.setOnClickListener(this);
		changeSwitch(usrflag);
	}

	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_GET_FTP_CONFIG_INFO);
		filter.addAction(Constants.P2P.RET_SET_FTP_CONFIG_INFO);
		filter.addAction(Constants.P2P.ACK_GET_FTP_INFO);
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
					.equals(Constants.P2P.RET_GET_FTP_CONFIG_INFO)) {
				if (data[1] == 1) {
					setInfo(data);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_FTP_CONFIG_INFO)) {
				if (data[1] == 0) {
					setInfo(data);
				}
			}else if (intent.getAction()
					.equals(Constants.P2P.ACK_GET_FTP_INFO)) {
				int state=intent.getIntExtra("state", -1);
				if (state==ACK_RET_TYPE.ACK_NET_ERROR) {
					P2PHandler.getInstance().getFTPConfigInfo(mContact.getContactId(),
							mContact.getPassword());
				}
			}
		}

	};

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
		changeSwitch(usrflag);
	}

	public void changeSwitch(short state) {
		switch (state) {
		case -1:
			svBtn.setModeStatde(0);
			break;
		case 0:
			svBtn.setModeStatde(2);
			break;
		case 1:
			svBtn.setModeStatde(1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.ftp_sv_btn) {
			changeFTPState();

		} else if (i == R.id.ftp_operation_btn) {
			Intent intent = new Intent();
			intent.putExtra("mContact", mContact);
			intent.putExtra("hostname", hostname);
			intent.putExtra("usrname", usrname);
			intent.putExtra("passwd", passwd);
			intent.putExtra("svrport", svrport);
			intent.putExtra("usrflag", usrflag);
			intent.setClass(mContext, FTPControlActivity.class);
			startActivityForResult(intent, 1);

		} else {
		}
	}

	public void changeFTPState() {
		short state = 0;
		if (usrflag==-1) {
			return;
		}else if (usrflag==1) {
			state=0;
		}else if (usrflag==0) {
			state=1;
		}
		changeSwitch((short)-1);
		P2PHandler.getInstance().setFTPConfigInfo(mContact.getContactId(),
				mContact.getPassword(), hostname, usrname, passwd, svrport,state);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==1) {
			if (resultCode==Activity.RESULT_OK) {
				hostname=data.getStringExtra("hostname");
				usrname=data.getStringExtra("usrname");
				passwd=data.getStringExtra("passwd");
				svrport=data.getShortExtra("svrport", (short)0);
				usrflag=data.getShortExtra("usrflag", (short)0);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		regFilter();
	}

	@Override
	public void onPause() {
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
		super.onPause();
	}

}
