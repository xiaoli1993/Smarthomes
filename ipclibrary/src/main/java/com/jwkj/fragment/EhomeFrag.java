package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jwkj.data.Contact;
import com.jwkj.global.Constants;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;
import com.p2p.core.P2PHandler;
import com.p2p.core.utils.MyUtils;

import java.util.Arrays;

public class EhomeFrag extends BaseFragment implements OnClickListener {
	private Context mContext;
	private Button btnGet, btnSet, btnUser;
	private TextView txResult;
	private EditText etTime, etGPIO, etLorH;
	private Contact mContact;
	private boolean isRegFilter = false;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ehome, container, false);
		mContext = getActivity();
		mContact = (Contact) getArguments().getSerializable("contact");
		initUI(view);
		return view;
	}
	
	@Override
	public void onResume() {
		regFilter();
		super.onResume();
	}

	private void initUI(View view) {
		btnGet = (Button) view.findViewById(R.id.btn_get);
		btnSet = (Button) view.findViewById(R.id.btn_vsetgpiostatus);
		btnUser = (Button) view.findViewById(R.id.btn_usermessage);

		txResult = (TextView) view.findViewById(R.id.tx_result);

		etTime = (EditText) view.findViewById(R.id.et_time);
		etGPIO = (EditText) view.findViewById(R.id.et_gpio);
		etLorH = (EditText) view.findViewById(R.id.et_lorh);

		btnGet.setOnClickListener(this);
		btnSet.setOnClickListener(this);
		btnUser.setOnClickListener(this);
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.GET_GPIO);
		filter.addAction(Constants.P2P.SET_GPIO_STATUS);
		filter.addAction(Constants.P2P.USER_DEFINDE_MESSAGE);

		mContext.registerReceiver(br, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Constants.P2P.SET_GPIO_STATUS)) {
				Log.i("dxsgpio", "SET_GPIO_DATA");

				byte[] data = intent.getByteArrayExtra("data");
				Log.i("dxsgpio", "data-->" + Arrays.toString(data));
				int datalen = data.length;
				int mark;
				if (data[0] < 0) {
					mark = data[0] + 256;
				} else {
					mark = data[0];
				}
				byte bOption = data[1];
				int len = MyUtils.byte2ToShort(data, 2);
				int u32TimeInterval = Utils.bytesToInt(data, 4);
				byte u8HighOrLow = data[8];
				byte u8PinNo = data[9];
				String textData = "len:" + len + "\n" + "TimeInterval:"
						+ u32TimeInterval + "\n" + "HighOrLow:" + u8HighOrLow
						+ "\n" + "PinNo:" + u8PinNo;
				T.show(mContext, "HighOrLow:" + u8HighOrLow + "PinNo:"
						+ u8PinNo, 2000);
				showData(textData);
			} else if (intent.getAction().equals(Constants.P2P.GET_GPIO)) {
				Log.i("dxsgpio", "GET_GPIO");
				boolean[] Level = intent.getBooleanArrayExtra("Level");
				String textData = "gpio1:" + Level[7] + "\n" + "gpio2:"
						+ Level[6] + "\n" + "gpio3:" + Level[5] + "\n"
						+ "gpio4:" + Level[4] + "\n" + "gpio5:" + Level[3]
						+ "\n" + "gpio6:" + Level[2];
				showData(textData);
			} else if (intent.getAction().equals(
					Constants.P2P.USER_DEFINDE_MESSAGE)) {
				Log.i("dxsgpio", "USER_DEFINDE_MESSAGE");
				T.show(mContext, "USER_DEFINDE_MESSAGE", 2000);
				int fromDeviceId = intent.getIntExtra("fromwhichdevice", -1);
				byte[] data = intent.getByteArrayExtra("Data");
				String datas = Arrays.toString(data);
				String message = "fromDeviceId:" + fromDeviceId + "\n"
						+ "datas:" + datas;
				showData(message);
			} else if (intent.getAction().equals(Constants.P2P.SET_LAMP_STATUS)) {
				byte[] data = intent.getByteArrayExtra("data");
				int Lampstatus = Utils.bytesToInt(data, 8);
				byte bOption = data[1];
				String message = "bOption-->" + bOption + "LampStatus-->"
						+ Lampstatus;
				showData(message);
			}
		}
	};

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.btn_get) {
			P2PHandler.getInstance().vGetGPIOStatus(mContact.contactId,
					mContact.contactPassword);

		} else if (i == R.id.btn_vsetgpiostatus) {
			try {
				setGPIO();
			} catch (Exception e) {
				Log.e("dxsTest", "set:Exception");
			}

		} else if (i == R.id.btn_usermessage) {
			senUserMessage();

		} else {
		}

	}

	private void showData(String data) {
		txResult.setText(data);
	}

	private void setGPIO() {
		String time = etTime.getText().toString().trim();
		String GPIO = etGPIO.getText().toString().trim();
		String LORH = etLorH.getText().toString().trim();
		if (TextUtils.isEmpty(time) || TextUtils.isEmpty(GPIO)
				|| TextUtils.isEmpty(LORH)) {
			// Empty
			Log.e("dxsTest", "set:empty");
		} else {
			P2PHandler.getInstance().vSetGPIOStatus(mContact.contactId,
					mContact.contactPassword, Integer.parseInt(GPIO),
					Integer.parseInt(LORH) != 1, Integer.parseInt(time));
		}
	}

	private void senUserMessage() {
		String gwell = "Gwell";
		byte[] byteBuffer = gwell.getBytes();
		boolean ipc = (int) (Math.random() * 10) % 2 == 0;
		P2PHandler.getInstance().vSendDataToURAT(mContact.contactId,
				mContact.contactPassword, byteBuffer, byteBuffer.length, ipc);
		T.showLong(mContext, "isIPC1:" + ipc);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (isRegFilter) {
			isRegFilter = false;
			mContext.unregisterReceiver(br);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it = new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}
}
