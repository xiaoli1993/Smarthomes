package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.CallActivity;
import com.jwkj.activity.DefenceAreaControlActivity;
import com.jwkj.activity.DeviceUpdateActivity;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.data.Contact;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.utils.T;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnButtonOkListener;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.global.Constants.ACK_RET_TYPE;
import com.p2p.core.utils.BaiduTjUtils;
import com.nuowei.ipclibrary.R;

import java.lang.reflect.Field;

public class MainControlFrag extends BaseFragment implements OnClickListener {
    RelativeLayout time_contrl, remote_control, alarm_control, video_control,
            record_control, security_control, net_control, defenceArea_control,
            chekc_device_update, sd_card_control, language_control, modify_wifipwd_control,
            ap_statechange, scene_mode_control, smart_device_control, defence_control, ftp_control, ehome_control, pirlight_control,
            device_info;
    private Context mContext;
    private Contact mContact;
    private int connectType = 0;
    boolean isRegFilter = false;
    NormalDialog dialog;
    private TextView tvAPmodeChange;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mContext = getActivity();
        mContact = (Contact) getArguments().getSerializable("contact");
        connectType = getArguments().getInt("connectType");
        View view = inflater.inflate(R.layout.fragment_control_main, container,
                false);
        initComponent(view);
        regFilter();
        initData();
        return view;
    }

    public void initComponent(View view) {
        time_contrl = (RelativeLayout) view.findViewById(R.id.time_control);
        remote_control = (RelativeLayout) view
                .findViewById(R.id.remote_control);
        alarm_control = (RelativeLayout) view.findViewById(R.id.alarm_control);
        video_control = (RelativeLayout) view.findViewById(R.id.video_control);
        record_control = (RelativeLayout) view
                .findViewById(R.id.record_control);
        security_control = (RelativeLayout) view
                .findViewById(R.id.security_control);
        net_control = (RelativeLayout) view.findViewById(R.id.net_control);
        defenceArea_control = (RelativeLayout) view
                .findViewById(R.id.defenceArea_control);
        chekc_device_update = (RelativeLayout) view
                .findViewById(R.id.check_device_update);
        sd_card_control = (RelativeLayout) view
                .findViewById(R.id.sd_card_control);
        language_control = (RelativeLayout) view
                .findViewById(R.id.language_control);
        modify_wifipwd_control = (RelativeLayout) view
                .findViewById(R.id.modify_wifipwd_control);
        ap_statechange = (RelativeLayout) view
                .findViewById(R.id.ap_statechange);
        tvAPmodeChange = (TextView) view.findViewById(R.id.tx_apmodecange);
        scene_mode_control = (RelativeLayout) view
                .findViewById(R.id.scene_mode_control);
        smart_device_control = (RelativeLayout) view
                .findViewById(R.id.smart_device_control);
        ehome_control = (RelativeLayout) view.findViewById(R.id.ehome_control);
        defence_control = (RelativeLayout) view
                .findViewById(R.id.defence_control);
        ftp_control = (RelativeLayout) view
                .findViewById(R.id.ftp_control);
        pirlight_control = (RelativeLayout) view.findViewById(R.id.pirlight_control);
        device_info = (RelativeLayout) view.findViewById(R.id.device_info);

        defenceArea_control.setOnClickListener(this);
        net_control.setOnClickListener(this);
        security_control.setOnClickListener(this);
        record_control.setOnClickListener(this);
        video_control.setOnClickListener(this);
        time_contrl.setOnClickListener(this);
        remote_control.setOnClickListener(this);
        alarm_control.setOnClickListener(this);
        chekc_device_update.setOnClickListener(this);
        sd_card_control.setOnClickListener(this);
        language_control.setOnClickListener(this);
        modify_wifipwd_control.setOnClickListener(this);
        ap_statechange.setOnClickListener(this);
        scene_mode_control.setOnClickListener(this);
        smart_device_control.setOnClickListener(this);
        defence_control.setOnClickListener(this);
        ftp_control.setOnClickListener(this);
        ehome_control.setOnClickListener(this);
        pirlight_control.setOnClickListener(this);
        device_info.setOnClickListener(this);
        modifyFeatures(view);
        // AP模式部分功能隐藏
        if (connectType == CallActivity.P2PCONECT) {
            security_control.setVisibility(View.VISIBLE);
            net_control.setVisibility(View.VISIBLE);
            modify_wifipwd_control.setVisibility(View.GONE);
            modifyFeatures(view);
        } else {
            security_control.setVisibility(View.GONE);
            net_control.setVisibility(View.GONE);
            sd_card_control.setBackgroundResource(R.drawable.tiao_bg_bottom);
            chekc_device_update.setVisibility(View.GONE);
            modify_wifipwd_control.setVisibility(View.VISIBLE);
        }
        Log.e("contactType", "contactType=" + mContact.contactType);
        if (mContact.isSmartHomeContatct()) {
            scene_mode_control.setVisibility(View.VISIBLE);
            smart_device_control.setVisibility(View.VISIBLE);
            defenceArea_control.setVisibility(View.GONE);
        } else {
            scene_mode_control.setVisibility(View.GONE);
            smart_device_control.setVisibility(View.GONE);
            defenceArea_control.setVisibility(View.VISIBLE);
        }

        if (AppConfig.Relese.isShowEhome) {
            ehome_control.setVisibility(View.VISIBLE);
        } else {
            ehome_control.setVisibility(View.GONE);
        }
    }

    private void initData() {
        if (mContact != null) {
            P2PHandler.getInstance().getNpcSettings(mContact.getIpContactId(),
                    mContact.contactPassword);
            P2PHandler.getInstance().getFTPConfigInfo(mContact.contactId,
                    mContact.contactPassword);
            P2PHandler.getInstance().getDefenceWorkGroup(
                    mContact.getIpContactId(), mContact.contactPassword);
        }
    }

    private void modifyFeatures(View view) {
        if (mContact.contactType == P2PValue.DeviceType.PHONE) {
            view.findViewById(R.id.control_main_frame).setVisibility(
                    RelativeLayout.GONE);
        } else if (mContact.contactType == P2PValue.DeviceType.NPC) {
            chekc_device_update.setVisibility(RelativeLayout.GONE);
            defenceArea_control
                    .setBackgroundResource(R.drawable.tiao_bg_bottom);
        } else {
            chekc_device_update.setVisibility(RelativeLayout.VISIBLE);
            defenceArea_control
                    .setBackgroundResource(R.drawable.tiao_bg_center);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.RET_GET_LANGUEGE);
        filter.addAction(Constants.P2P.RET_DEVICE_NOT_SUPPORT);
        filter.addAction(Constants.P2P.RET_SET_AP_MODE);
        filter.addAction(Constants.P2P.RET_AP_MODESURPPORT);
        filter.addAction(Constants.P2P.RET_GET_FTP_CONFIG_INFO);
        filter.addAction(Constants.P2P.ACK_GET_FTP_INFO);
        filter.addAction(Constants.P2P.RET_SETPIRLIGHT);
        filter.addAction(Constants.P2P.RET_GET_DEFENCE_WORK_GROUP);
        filter.addAction(Constants.P2P.ACK_GET_DEFENCE_WORK_GROUP);
        mContext.registerReceiver(br, filter);
        isRegFilter = true;
    }

    BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.P2P.RET_GET_LANGUEGE)) {
                int languegecount = intent.getIntExtra("languegecount", -1);
                int curlanguege = intent.getIntExtra("curlanguege", -1);
                int[] langueges = intent.getIntArrayExtra("langueges");
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    Intent go_language_control = new Intent();
                    go_language_control
                            .setAction(Constants.Action.REPLACE_LANGUAGE_CONTROL);
                    go_language_control.putExtra("isEnforce", true);
                    go_language_control
                            .putExtra("languegecount", languegecount);
                    go_language_control.putExtra("curlanguege", curlanguege);
                    go_language_control.putExtra("langueges", langueges);
                    mContext.sendBroadcast(go_language_control);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_DEVICE_NOT_SUPPORT)) {
                if (dialog != null) {
                    dialog.dismiss();
                    T.showShort(mContext, R.string.not_support);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_AP_MODESURPPORT)) {
                int result = intent.getIntExtra("result", 0);
                String id = intent.getStringExtra("id");
                if (id == null || mContact == null) {
                    return;
                }
                if (id.equals(mContact.getIpContactId())) {
                    if (result == 0) {// 不支持AP模式
                        ap_statechange.setVisibility(View.GONE);
                    } else if (result == 1) {// 支持AP模式,不处于AP模式
                        ap_statechange.setVisibility(View.VISIBLE);
                        tvAPmodeChange.setText(R.string.ap_modecahnge_line);
                    } else if (result == 2) {// 支持AP模式，处于AP模式
                        ap_statechange.setVisibility(View.VISIBLE);
                        tvAPmodeChange.setText(R.string.ap_modecahnge_ap);
                    }
                    ap_statechange.setTag(result);
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_SET_AP_MODE)) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                ((MainControlActivity) mContext).finish();
            } else if (intent.getAction()
                    .equals(Constants.P2P.RET_GET_FTP_CONFIG_INFO)) {
                String iSrcID = intent.getStringExtra("iSrcID");
                byte[] data = intent.getByteArrayExtra("data");
                if (data[1] == 1) {
//                    ftp_control.setVisibility(View.VISIBLE);
                }
            } else if (intent.getAction()
                    .equals(Constants.P2P.ACK_GET_FTP_INFO)) {
                int state = intent.getIntExtra("state", -1);
                if (state == ACK_RET_TYPE.ACK_NET_ERROR) {
                    P2PHandler.getInstance().getFTPConfigInfo(mContact.contactId,
                            mContact.contactPassword);
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_SETPIRLIGHT)) {
                //公版去掉龙的定制
                //int result=intent.getIntExtra("result", 0);
                //if(result==1){
                //	pirlight_control.setVisibility(View.VISIBLE);
                //}else{
                //	pirlight_control.setVisibility(View.GONE);
                //}
            } else if (intent.getAction()
                    .equals(Constants.P2P.RET_GET_DEFENCE_WORK_GROUP)) {
                String iSrcID = intent.getStringExtra("iSrcID");
                byte boption = intent.getByteExtra("boption", (byte) -1);
                byte[] data = intent.getByteArrayExtra("data");
                if (boption == 0 && !mContact.isSmartHomeContatct()) {
                    defence_control.setVisibility(View.VISIBLE);
                }
            } else if (intent.getAction()
                    .equals(Constants.P2P.ACK_GET_DEFENCE_WORK_GROUP)) {
                int state = intent.getIntExtra("state", -1);
                if (state == ACK_RET_TYPE.ACK_NET_ERROR) {
                    P2PHandler.getInstance().getDefenceWorkGroup(
                            mContact.getIpContactId(), mContact.contactPassword);
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        int i = view.getId();
        if (i == R.id.remote_control) {
            Intent go_remote_control = new Intent();
            go_remote_control
                    .setAction(Constants.Action.REPLACE_REMOTE_CONTROL);
            go_remote_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_remote_control);

        } else if (i == R.id.time_control) {
            BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_TIME_CONTROL, "Time setting");
            Intent go_time_control = new Intent();
            go_time_control.setAction(Constants.Action.REPLACE_SETTING_TIME);
            go_time_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_time_control);

        } else if (i == R.id.alarm_control) {
            BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_ALARM_CONTROL, "Alarm settings");
            Intent go_alarm_control = new Intent();
            go_alarm_control.setAction(Constants.Action.REPLACE_ALARM_CONTROL);
            go_alarm_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_alarm_control);

        } else if (i == R.id.record_control) {
            BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_RECORD_CONTROL, "Recording settings");
            Intent go_record_control = new Intent();
            go_record_control
                    .setAction(Constants.Action.REPLACE_RECORD_CONTROL);
            go_record_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_record_control);

        } else if (i == R.id.video_control) {
            BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_VIDEO_CONTROL, "Media settings");
            Intent go_video_control = new Intent();
            go_video_control.setAction(Constants.Action.REPLACE_VIDEO_CONTROL);
            go_video_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_video_control);

        } else if (i == R.id.security_control) {
            BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_SECURITY_CONTROL, "Security settings");
            Intent go_security_control = new Intent();
            go_security_control
                    .setAction(Constants.Action.REPLACE_SECURITY_CONTROL);
            go_security_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_security_control);

        } else if (i == R.id.net_control) {
            Intent go_net_control = new Intent();
            go_net_control.setAction(Constants.Action.REPLACE_NET_CONTROL);
            go_net_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_net_control);

        } else if (i == R.id.defenceArea_control) {//			Intent go_da_control = new Intent();
//			go_da_control
//					.setAction(Constants.Action.REPLACE_DEFENCE_AREA_CONTROL);
//			go_da_control.putExtra("isEnforce", true);
//			mContext.sendBroadcast(go_da_control);
            Intent defence_area_control = new Intent();
            defence_area_control.putExtra("mContact", mContact);
            defence_area_control.setClass(mContext, DefenceAreaControlActivity.class);
            getActivity().startActivity(defence_area_control);

        } else if (i == R.id.check_device_update) {
            Intent check_update = new Intent(mContext,
                    DeviceUpdateActivity.class);
            check_update.putExtra("contact", mContact);
            mContext.startActivity(check_update);

        } else if (i == R.id.sd_card_control) {
            Intent go_sd_card_control = new Intent();
            go_sd_card_control
                    .setAction(Constants.Action.REPLACE_SD_CARD_CONTROL);
            go_sd_card_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_sd_card_control);

        } else if (i == R.id.language_control) {
            P2PHandler.getInstance().getDeviceLanguage(mContact.contactId,
                    mContact.contactPassword);
            dialog = new NormalDialog(mContext);
            dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
            dialog.showDialog();

        } else if (i == R.id.modify_wifipwd_control) {
            Intent go_modify_wifipwd = new Intent();
            go_modify_wifipwd
                    .setAction(Constants.Action.REPLACE_MODIFY_WIFI_PWD_CONTROL);
            go_modify_wifipwd.putExtra("isEnforce", true);
            mContext.sendBroadcast(go_modify_wifipwd);

        } else if (i == R.id.ap_statechange) {
            int resuly = (Integer) view.getTag();
            dialog = new NormalDialog(mContext);
            if (resuly == 1) {// 支持AP模式,不处于AP模式
                dialog.setTitle(R.string.ap_modecahnge_line);
            } else if (resuly == 2) {// 支持AP模式，处于AP模式
                dialog.setTitle(R.string.ap_modecahnge_ap);
            }
            dialog.setContentStr(R.string.ap_modecahnge);
            dialog.setbtnStr2(R.string.cancel);
            dialog.setbtnStr1(R.string.ensure);
            dialog.setStyle(NormalDialog.DIALOG_STYLE_NORMAL);
            dialog.showDialog();
            dialog.setOnButtonOkListener(new OnButtonOkListener() {

                @Override
                public void onClick() {
                    P2PHandler.getInstance().setAPModeChange(
                            mContact.getIpContactId(), mContact.contactPassword,
                            1);
                    dialog.showLoadingDialog2();
                }
            });
            // 查看返回结果，再跳转

        } else if (i == R.id.scene_mode_control) {// 模式设置
            Intent mode_control = new Intent();
            mode_control.setAction(Constants.Action.REPLACE_MODE_CONTROL);
            mode_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(mode_control);

        } else if (i == R.id.smart_device_control) {// 智能设备
            Intent device_control = new Intent();
            device_control
                    .setAction(Constants.Action.REPLACE_SMART_DEVICE_CONTROL);
            device_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(device_control);

        } else if (i == R.id.defence_control) {//			Intent defence_control=new Intent();
//			defence_control.putExtra("mContact", mContact);
//			defence_control.setClass(mContext, DefenceControlActivity.class);
//			getActivity().startActivity(defence_control);
            Intent defence_control = new Intent();
            defence_control
                    .setAction(Constants.Action.REPLACE_DEFENCE_CONTROL);
            defence_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(defence_control);

        } else if (i == R.id.ftp_control) {
            Intent ftp_control = new Intent();
            ftp_control.setAction(Constants.Action.REPLACE_FTP_CONTROL);
            ftp_control.putExtra("isEnforce", true);
            mContext.sendBroadcast(ftp_control);

        } else if (i == R.id.ehome_control) {// ehome
            Intent ehome_control = new Intent();
            ehome_control
                    .setAction(Constants.Action.EHOME_FRAG);
            mContext.sendBroadcast(ehome_control);

        } else if (i == R.id.pirlight_control) {// ehome
            Intent pirlight_control = new Intent();
            pirlight_control
                    .setAction(Constants.Action.PIRLIGHT_FRAG);
            mContext.sendBroadcast(pirlight_control);

        } else if (i == R.id.device_info) {
            Intent device_info = new Intent();
            device_info.setAction(Constants.Action.REPLACE_DEVICE_INFO);
            device_info.putExtra("isEnforce", true);
            mContext.sendBroadcast(device_info);

        }
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        if (isRegFilter) {
            mContext.unregisterReceiver(br);
            isRegFilter = false;
        }
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
