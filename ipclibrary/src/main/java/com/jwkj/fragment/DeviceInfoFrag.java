package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.utils.T;
import com.jwkj.widget.InputPasswordDialog;
import com.jwkj.widget.ProgressTextView;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.nuowei.ipclibrary.R;

import static com.jwkj.PlayBackListActivity.mContext;


/**
 * Created by WXY on 2016/10/12.
 */

public class DeviceInfoFrag extends BaseFragment implements View.OnClickListener {
    private Context context;
    private Contact contact;
    private boolean isRegFilter = false;
    private RelativeLayout deviceNameLayout;
    private ImageView deviceNameArrow;
    private TextView deviceName;
    private ProgressTextView currentVersion;
    private ProgressTextView uBootVersion;
    private ProgressTextView kernelVersion;
    private ProgressTextView systemVersion;
    private ProgressTextView ipAddress;
    private InputPasswordDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        contact = (Contact) getArguments().getSerializable("contact");
        View view = inflater.inflate(R.layout.fragment_device_info, container, false);
        initComponent(view);
        if (contact != null) {
            P2PHandler.getInstance().getDeviceVersion(contact.contactId,
                    contact.contactPassword);
        }
        return view;
    }

    private void initComponent(View view) {
        deviceNameLayout = (RelativeLayout) view.findViewById(R.id.rl_device_name);
        deviceNameArrow = (ImageView) view.findViewById(R.id.device_name_arrow);
        deviceName = (TextView) view.findViewById(R.id.pt_device_name);
        currentVersion = (ProgressTextView) view.findViewById(R.id.pt_current_version);
        uBootVersion = (ProgressTextView) view.findViewById(R.id.pt_uboot_version);
        kernelVersion = (ProgressTextView) view.findViewById(R.id.pt_kernel_version);
        systemVersion = (ProgressTextView) view.findViewById(R.id.pt_system_version);
        ipAddress = (ProgressTextView) view.findViewById(R.id.pt_ip_address);
        if (contact != null && contact.mode != P2PValue.DeviceMode.AP_MODE) {
            deviceNameArrow.setVisibility(View.VISIBLE);
            deviceNameLayout.setOnClickListener(this);
            deviceName.setOnClickListener(this);
        }
        settingProgressTextView();

    }

    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.ACK_RET_GET_DEVICE_INFO);
        filter.addAction(Constants.P2P.RET_GET_DEVICE_INFO);
        context.registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_GET_DEVICE_INFO)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    goneProgressTextView();
                    T.showShort(context, R.string.password_error);
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().getDeviceVersion(contact.contactId,
                            contact.contactPassword);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_DEVICE_INFO)) {
                int result = intent.getIntExtra("result", -1);
                String cur_version = intent.getStringExtra("cur_version");
                int iUbootVersion = intent.getIntExtra("iUbootVersion", 0);
                int iKernelVersion = intent.getIntExtra("iKernelVersion", 0);
                int iRootfsVersion = intent.getIntExtra("iRootfsVersion", 0);
                setProgressTextViewData(cur_version, iUbootVersion, iKernelVersion, iRootfsVersion);
            }
        }
    };

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.pt_device_name || i == R.id.rl_device_name) {
            dialog = new InputPasswordDialog(context);
            dialog.setInputPasswordClickListener(inputNameClickListener);
            dialog.setContactId(contact.contactId);
            dialog.setType(InputPasswordDialog.EDITOR_DIALOG);
            dialog.show();
            dialog.settingDialog(context.getString(R.string.edit), contact.contactName,
                    context.getString(R.string.please_input_device_name), 64,
                    context.getString(R.string.confirm),
                    context.getString(R.string.cancel), InputType.TYPE_CLASS_TEXT);

        }
    }

    private InputPasswordDialog.InputPasswordClickListener inputNameClickListener =
            new InputPasswordDialog.InputPasswordClickListener() {
                @Override
                public void onCancelClick() {
                    dialog.dismiss();
                }

                @Override
                public void onOkClick(String contactId, String name) {
                    if (name == null || name.equals("")){
                        T.showShort(mContext, R.string.input_device_name);
                        return;
                    }
                    if (name.length() > 64) {
                        return;
                    }
                    if (name.equals(contact.getContactName())){
                        dialog.dismiss();
                        return;
                    }
                    contact.contactName = name;
                    try {
                        Contact c=FList.getInstance().isContact(contactId);
                        if(c!=null){
                            c.contactName=name;
                            DataManager.updateContact(context, c);
                        }
//                        deviceName.setModeStatde(ProgressTextView.STATE_TEXT, name);
                        deviceName.setText(name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dialog.dismiss();
                    }
                }
            };

    private void settingProgressTextView() {
//        deviceName.setProgressPosition(ProgressTextView.PROGRESS_RIGHT);
        currentVersion.setProgressPosition(ProgressTextView.PROGRESS_RIGHT);
        uBootVersion.setProgressPosition(ProgressTextView.PROGRESS_RIGHT);
        kernelVersion.setProgressPosition(ProgressTextView.PROGRESS_RIGHT);
        systemVersion.setProgressPosition(ProgressTextView.PROGRESS_RIGHT);
        ipAddress.setProgressPosition(ProgressTextView.PROGRESS_RIGHT);

    }

    private void goneProgressTextView() {
        deviceName.setVisibility(View.GONE);
        currentVersion.setVisibility(View.GONE);
        uBootVersion.setVisibility(View.GONE);
        kernelVersion.setVisibility(View.GONE);
        systemVersion.setVisibility(View.GONE);
        ipAddress.setVisibility(View.GONE);
    }

    private void setProgressTextViewData(String cur_version, int iUbootVersion, int iKernelVersion,
                                         int iRootfsVersion) {
        currentVersion.setModeStatde(ProgressTextView.STATE_TEXT, cur_version);
        uBootVersion.setModeStatde(ProgressTextView.STATE_TEXT, String.valueOf(iUbootVersion));
        kernelVersion.setModeStatde(ProgressTextView.STATE_TEXT, String.valueOf(iKernelVersion));
        systemVersion.setModeStatde(ProgressTextView.STATE_TEXT, String.valueOf(iRootfsVersion));
    }

    @Override
    public void onResume() {
        super.onResume();
//        deviceName.setModeStatde(ProgressTextView.STATE_TEXT, contact.contactName);
        deviceName.setText(contact.contactName);
        regFilter();
    }

    @Override
    public void onPause() {
        if (isRegFilter) {
            context.unregisterReceiver(mReceiver);
            isRegFilter = false;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent it = new Intent();
        it.setAction(Constants.Action.CONTROL_BACK);
        context.sendBroadcast(it);
    }
}
