package com.jwkj.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.jwkj.adapter.ChannelAreaSetRecyAdapter;
import com.jwkj.adapter.RemoteAreaSetRecyAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.DefenceArea;
import com.jwkj.data.Prepoint;
import com.jwkj.global.Constants;
import com.jwkj.recycleview.ItemDecor.RecycleViewLinearLayoutManager;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.ImputDialog;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefenceAreaControlActivity extends BaseActivity implements
        OnClickListener {
    private boolean isRegFilter = false;
    private Context mContext;
    private Contact mContact;
    private ImageView back_btn;
    private ImageView ic_channelAdd;
    private ImageView ic_remoteAdd;
    private ImageView ic_specialAdd;
    private RecyclerView rl_channel;
    private RecyclerView rl_remote_control;
    private RecyclerView rl_special;
    private ChannelAreaSetRecyAdapter channelAdapter;
    private RemoteAreaSetRecyAdapter remoteAdapter;
    private ChannelAreaSetRecyAdapter specialAdapter;
    private NormalDialog dialog_loading;
    private List<DefenceArea> channel = new ArrayList<DefenceArea>();
    private List<DefenceArea> remote = new ArrayList<DefenceArea>();
    private List<DefenceArea> special = new ArrayList<DefenceArea>();
    private int current_group;
    private int current_item;
    private int current_type;
    private int current_defenceType;
    private int deleteIndex;
    private int current_Switch;
    private int switchIndex;
    private List<DefenceArea> dbAreaList;
    private boolean isInsert = false;
    private Prepoint prepoint;
    private ArrayList<Integer> defenceLocation = new ArrayList<Integer>();
    private boolean isFlush = false;
    private ImputDialog inputDialog;
    private int changeIndex;
    private int changeType;
    private int structVersion = 0;

    private int defenceSwitch = -1;
    private static final int DEFENCE_NAME_INFO_LENGTH = 19;
    private static final int DEFENCE_NAME_LENGTH = 16;
    private String changeName;
    private boolean isGetName = false;
    private int switchType;
    private boolean isResume = false;
    private int toBindIndex = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_defence_area_control);
        mContext = this;
        mContact = (Contact) getIntent().getSerializableExtra("mContact");
        prepoint = DataManager.findPrepointByDevice(mContext,
                mContact.contactId);
        getAreaListDB();
        setAreaInfoByDB();
        initComponent();
        regFilter();
        P2PHandler.getInstance().getDefenceArea(mContact.getContactId(),
                mContact.getPassword());
    }

    public void initComponent() {
        back_btn = (ImageView) findViewById(R.id.df_ae_back_btn);
        ic_channelAdd = (ImageView) findViewById(R.id.df_ae_ic_channeladd);
        ic_remoteAdd = (ImageView) findViewById(R.id.df_ae_ic_remoteadd);
        ic_specialAdd = (ImageView) findViewById(R.id.df_ae_ic_specialadd);
        rl_channel = (RecyclerView) findViewById(R.id.df_ae_rl_channel);
        rl_remote_control = (RecyclerView) findViewById(R.id.df_ae_rl_remote);
        rl_special = (RecyclerView) findViewById(R.id.df_ae_rl_special);
        RecycleViewLinearLayoutManager channelManager = new RecycleViewLinearLayoutManager(
                mContext);
        RecycleViewLinearLayoutManager remoteManager = new RecycleViewLinearLayoutManager(
                mContext);
        RecycleViewLinearLayoutManager specialManager = new RecycleViewLinearLayoutManager(
                mContext);
        rl_channel.setLayoutManager(channelManager);
        channelAdapter = new ChannelAreaSetRecyAdapter(mContext, channel,
                prepoint);
        channelAdapter.setChannelAreaListener(channelAreaListener);
        rl_channel.setAdapter(channelAdapter);
        rl_remote_control.setLayoutManager(remoteManager);
        remoteAdapter = new RemoteAreaSetRecyAdapter(mContext, remote);
        remoteAdapter.setRemoteAreaListener(remoteAreaListener);
        rl_remote_control.setAdapter(remoteAdapter);
        rl_special.setLayoutManager(specialManager);
        specialAdapter = new ChannelAreaSetRecyAdapter(mContext, special,
                prepoint);
        specialAdapter.setChannelAreaListener(channelAreaListener);
        rl_special.setAdapter(specialAdapter);
        back_btn.setOnClickListener(this);
        ic_channelAdd.setOnClickListener(this);
        ic_remoteAdd.setOnClickListener(this);
        ic_specialAdd.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.ACK_RET_SET_DEFENCE_AREA);
        filter.addAction(Constants.P2P.ACK_RET_GET_DEFENCE_AREA);
        filter.addAction(Constants.P2P.ACK_RET_CLEAR_DEFENCE_AREA);
        filter.addAction(Constants.P2P.RET_CLEAR_DEFENCE_AREA);
        filter.addAction(Constants.P2P.RET_SET_DEFENCE_AREA);
        filter.addAction(Constants.P2P.RET_GET_DEFENCE_AREA);
        filter.addAction(Constants.P2P.RET_DEVICE_NOT_SUPPORT);
        filter.addAction(Constants.P2P.ACK_RET_GET_SENSOR_SWITCH);
        filter.addAction(Constants.P2P.ACK_RET_SET_SENSOR_SWITCH);
        filter.addAction(Constants.P2P.RET_GET_SENSOR_SWITCH);
        filter.addAction(Constants.P2P.RET_SET_SENSOR_SWITCH);
        filter.addAction(Constants.P2P.P2P_SET_ALARM_PRESET_MOTOR_POS);
        filter.addAction(Constants.P2P.RET_GET_PRESETMOTOROS);
        filter.addAction(Constants.P2P.RET_GET_DEFENCE_SWITCH);
        filter.addAction(Constants.P2P.RET_SET_DEFENCE_SWITCH);
        filter.addAction(Constants.P2P.RET_GET_DEFENCE_AREA_NAME);
        filter.addAction(Constants.P2P.RET_SET_DEFENCE_AREA_NAME);
        filter.addAction(Constants.P2P.ACK_RET_PRESET_POS);
        filter.addAction(Constants.P2P.GET_PREPOINT_SURPPORTE);
        filter.addAction(Constants.P2P.ACK_RET_GET_NPC_SETTINGS);
        mContext.registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.P2P.RET_GET_DEFENCE_AREA)) {
                int result = intent.getIntExtra("result", -1);
                ArrayList<int[]> data = (ArrayList<int[]>) intent
                        .getSerializableExtra("data");
                if (result == 1) {
                    setAreList(data);
                    channelAdapter.updateAll();
                    remoteAdapter.updateAll();
                    specialAdapter.updateAll();
                    insertInfoToDB();
                    contrastInfo(dbAreaList, getInfoList());
                    setAllDefenceName();
                    if (channel.size() > 0 || special.size() > 0) {
                        P2PHandler.getInstance().getDefenceAreaAlarmSwitch(
                                mContact.getContactId(), mContact.getPassword());
                        P2PHandler.getInstance()
                                .getDefenceAreaName(mContact.getContactId(),
                                        mContact.getPassword(), 0);
                    }
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_GET_DEFENCE_AREA)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    Intent i = new Intent();
                    i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
                    mContext.sendBroadcast(i);
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().getDefenceArea(mContact.getContactId(),
                            mContact.getPassword());
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_SENSOR_SWITCH)) {
                int result = intent.getIntExtra("result", -1);
                ArrayList<int[]> sensors = (ArrayList<int[]>) intent
                        .getSerializableExtra("data");
                if (result == 1) {
                    setAreListDefenceState(sensors);
                    channelAdapter.updateAll();
                    specialAdapter.updateAll();
                    P2PHandler.getInstance().getNpcSettings(mContact.getContactId(),
                            mContact.getPassword());
                } else if (result == 41) {
                    T.showShort(mContext,
                            R.string.device_unsupport_defence_area);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_GET_SENSOR_SWITCH)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    Intent i = new Intent();
                    i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
                    mContext.sendBroadcast(i);
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().getDefenceAreaAlarmSwitch(
                            mContact.getContactId(), mContact.getPassword());
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_SET_DEFENCE_AREA)) {
                if (null != dialog_loading) {
                    dialog_loading.dismiss();
                    dialog_loading = null;
                }
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.DEFENCE_AREA_SET.SETTING_SUCCESS) {
                    if (current_type == Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR) {
                        if (current_defenceType == DefenceArea.REMOTETYPE) {
                            DataManager.deleteDefenceAreaByGroupAndItem(
                                    mContext, mContact.contactId,
                                    remote.get(deleteIndex));
                            remote.remove(deleteIndex);
                            remoteAdapter.updateAll();
                        } else if (current_defenceType == DefenceArea.CHANNELTYPE) {
                            DataManager.deleteDefenceAreaByGroupAndItem(
                                    mContext, mContact.contactId,
                                    channel.get(deleteIndex));
                            channel.remove(deleteIndex);
                            channelAdapter.updateAll();
                        } else if (current_defenceType == DefenceArea.SPECIAL_CHANNELTYPE) {
                            DataManager.deleteDefenceAreaByGroupAndItem(
                                    mContext, mContact.contactId,
                                    special.get(deleteIndex));
                            special.remove(deleteIndex);
                            specialAdapter.updateAll();
                        }
                        T.showShort(mContext, R.string.clear_success);
                    } else {
                        if (current_defenceType == DefenceArea.REMOTETYPE) {
                            DefenceArea defenceArea = new DefenceArea(
                                    current_group, current_item, 1,
                                    current_defenceType);
                            DataManager.insertDefenceArea(mContext,
                                    mContact.contactId, defenceArea);
                            remote.add(defenceArea);
                            remoteAdapter.updateAll();
                        } else if (current_defenceType == DefenceArea.CHANNELTYPE) {
                            DefenceArea defenceArea = new DefenceArea(
                                    current_group, current_item, 1,
                                    current_defenceType);
                            DataManager.insertDefenceArea(mContext,
                                    mContact.contactId, defenceArea);
                            channel.add(defenceArea);
                            channelAdapter.updateAll();
                            if (isGetName) {
                                sendLocationByGroupAndID(defenceArea);
                            }
                        } else if (current_defenceType == DefenceArea.SPECIAL_CHANNELTYPE) {
                            DefenceArea defenceArea = new DefenceArea(
                                    current_group, current_item, 1,
                                    current_defenceType);
                            DataManager.insertDefenceArea(mContext,
                                    mContact.contactId, defenceArea);
                            special.add(defenceArea);
                            specialAdapter.updateAll();
                            if (isGetName) {
                                sendLocationByGroupAndID(defenceArea);
                            }
                        }
                        T.showShort(mContext, R.string.add_success);
                    }
                } else if (result == 30) {
                    T.showShort(mContext, R.string.clear_success);
                } else if (result == 32) {
                    int group = intent.getIntExtra("group", -1);
                    int item = intent.getIntExtra("item", -1);
                    DefenceArea da = findExistedSensor(group, item);
                    if (da != null) {
                        T.showShort(mContext, da.getName() +
                                mContext.getResources().getString(R.string.has_been_learning));
                    }
                } else if (result == 41) {
                    Intent back = new Intent();
                    back.setAction(Constants.Action.REPLACE_MAIN_CONTROL);
                    mContext.sendBroadcast(back);
                    T.showShort(mContext,
                            R.string.device_unsupport_defence_area);
                    DefenceAreaControlActivity.this.finish();
                } else {
                    T.showShort(mContext, R.string.operator_error);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_SET_DEFENCE_AREA)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    Intent i = new Intent();
                    i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
                    mContext.sendBroadcast(i);
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().setDefenceAreaState(
                            mContact.getContactId(), mContact.getPassword(),
                            current_group, current_item, current_type);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_SET_SENSOR_SWITCH)) {
                int result = intent.getIntExtra("result", -1);
                if (result == 0) {
                    if (switchType == DefenceArea.CHANNELTYPE) {
                        channel.get(switchIndex).setState(current_Switch);
                        channelAdapter.updateByPosition(switchIndex);
                    } else {
                        special.get(switchIndex).setState(current_Switch);
                        specialAdapter.updateByPosition(switchIndex);
                    }
                } else if (result == 41) {
                    T.showShort(mContext,
                            R.string.device_unsupport_defence_area);
                } else {

                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_SET_SENSOR_SWITCH)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    Intent i = new Intent();
                    i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
                    mContext.sendBroadcast(i);
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().setDefenceAreaAlarmSwitch(
                            mContact.getContactId(), mContact.getPassword(),
                            current_Switch, current_group, current_item);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.P2P_SET_ALARM_PRESET_MOTOR_POS)) {
                byte[] result = intent.getByteArrayExtra("result");
                if (result[1] == 1) {
                    getChannelLocation(result);
                    channelAdapter.setIsShowPosition(true);
                    channelAdapter.updateAll();
                    specialAdapter.setIsShowPosition(true);
                    specialAdapter.updateAll();
                } else if (result[1] == 84) {
                    T.showShort(mContext, R.string.device_not_support);
                } else if (result[1] == -1) {
                    T.showShort(mContext, R.string.device_not_location);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_PRESETMOTOROS)) {
                byte[] result = intent.getByteArrayExtra("result");
                if (result[1] == 1) {
                    setLocation(Utils.getByteBinnery(result[3], true));
                    sendChannelLocation();
                    isFlush = true;
                } else if (result[1] == 84) {
                    T.showShort(mContext, R.string.device_not_support);
                } else if (result[1] == -1) {
                    T.showShort(mContext, R.string.device_not_location);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_DEFENCE_AREA_NAME)) {
                String iSrcID = intent.getStringExtra("iSrcID");
                byte[] data = intent.getByteArrayExtra("data");
                if (data != null) {
                    if (data[2] == 1) {
                        isGetName = true;
                        ChooseParsingData(data);
                    }
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_SET_DEFENCE_AREA_NAME)) {
                if (null != dialog_loading) {
                    dialog_loading.dismiss();
                    dialog_loading = null;
                }
                String iSrcID = intent.getStringExtra("iSrcID");
                byte[] data = intent.getByteArrayExtra("data");
                if (data != null) {
                    if (data[2] == 0) {
                        if (changeType == DefenceArea.REMOTETYPE) {
                            DataManager
                                    .updateDefenceAreaNameAndEflagByGroupAndItem(
                                            mContext, mContact.contactId,
                                            changeName, 1,
                                            remote.get(changeIndex).getGroup(),
                                            remote.get(changeIndex).getItem());
                            remote.get(changeIndex).setName(changeName);
                            remote.get(changeIndex).setEflag(1);
                            remoteAdapter.updateByPosition(changeIndex);
                        } else if (changeType == DefenceArea.CHANNELTYPE) {
                            DataManager
                                    .updateDefenceAreaNameAndEflagByGroupAndItem(
                                            mContext,
                                            mContact.contactId,
                                            changeName,
                                            1,
                                            channel.get(changeIndex).getGroup(),
                                            channel.get(changeIndex).getItem());
                            channel.get(changeIndex).setName(changeName);
                            channel.get(changeIndex).setEflag(1);
                            channelAdapter.updateByPosition(changeIndex);
                        } else if (changeType == DefenceArea.SPECIAL_CHANNELTYPE) {
                            DataManager
                                    .updateDefenceAreaNameAndEflagByGroupAndItem(
                                            mContext,
                                            mContact.contactId,
                                            changeName,
                                            1,
                                            special.get(changeIndex).getGroup(),
                                            special.get(changeIndex).getItem());
                            special.get(changeIndex).setName(changeName);
                            special.get(changeIndex).setEflag(1);
                            specialAdapter.updateByPosition(changeIndex);
                        }
                        T.showShort(mContext, R.string.modify_success);
                    }
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_PRESET_POS)) {
                int state = intent.getIntExtra("state", -1);
                if (state == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    Intent i = new Intent();
                    i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
                    mContext.sendBroadcast(i);
                } else if (state == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    Utils.setPrePoints(mContact.getContactId(),
                            mContact.getPassword(), 2, 0);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.GET_PREPOINT_SURPPORTE)) {
                String deviceID = intent.getStringExtra("deviceId");
                int result = intent.getIntExtra("result", 0);
                if (result == Constants.SurportPrepoint.YES) {// 支持记忆点
                    Utils.setPrePoints(mContact.getContactId(),
                            mContact.getPassword(), 2, 0);
                }
            } else if (intent.getAction().equals(Constants.P2P.ACK_RET_GET_NPC_SETTINGS)) {
                int result = intent.getIntExtra("result", 0);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    Intent i = new Intent();
                    i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
                    mContext.sendBroadcast(i);
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().getNpcSettings(mContact.getContactId(),
                            mContact.getPassword());
                }
            }
        }

    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.df_ae_back_btn:
                this.finish();
                break;
            case R.id.df_ae_ic_channeladd:
                showAddDialog(DefenceArea.CHANNELTYPE);
                break;
            case R.id.df_ae_ic_remoteadd:
                showAddDialog(DefenceArea.REMOTETYPE);
                break;
            case R.id.df_ae_ic_specialadd:
                showAddDialog(DefenceArea.SPECIAL_CHANNELTYPE);
                break;
            default:
                break;
        }
    }

    private void setAreList(ArrayList<int[]> data) {
        channel.clear();
        remote.clear();
        special.clear();
        DefenceArea defenceArea;
        for (int i = 0; i < data.size(); i++) {
            int[] array = data.get(i);
            for (int j = 0; j < array.length; j++) {
                if (array[j] == 0) {
                    if (i == 0) {
                        defenceArea = new DefenceArea(i, j, -1,
                                DefenceArea.REMOTETYPE);
                        remote.add(defenceArea);
                    } else if (i == 8) {
                        defenceArea = new DefenceArea(i, j, -1,
                                DefenceArea.SPECIAL_CHANNELTYPE);
                        special.add(defenceArea);
                    } else {
                        defenceArea = new DefenceArea(i, j, -1,
                                DefenceArea.CHANNELTYPE);
                        channel.add(defenceArea);
                    }
                }
            }
        }
    }

    private void setAreListDefenceState(ArrayList<int[]> data) {
        for (int i = 0; i < channel.size(); i++) {
            int index = channel.get(i).getItemIndex() - 8;
            int[] state = data.get(index / 8);
            channel.get(i).setState(state[(7 - index % 8)]);
        }
        for (int j = 0; j < special.size(); j++) {
            int specialIndex = special.get(j).getItemIndex() - 8;
            int[] state = data.get(specialIndex / 8);
            special.get(j).setState(state[7 - specialIndex % 8]);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isFlush) {
            if (isResume) {
                sendChannelLocation();
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        isResume = true;
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
        return Constants.ActivityInfo.ACTIVITY_DEFENCE_AREA_CONTROL;
    }

    public void showAddDialog(final int type) {
        dialog_loading = null;
        dialog_loading = new NormalDialog(mContext, mContext.getResources()
                .getString(R.string.learing_code), mContext.getResources()
                .getString(R.string.learing_code_prompt), mContext
                .getResources().getString(R.string.ensure), mContext
                .getResources().getString(R.string.cancel));
        dialog_loading
                .setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

                    @Override
                    public void onClick() {
                        // TODO Auto-generated method stub
                        if (null != dialog_loading) {
                            dialog_loading.dismiss();
                            dialog_loading = null;
                        }
                        int defenceIndex = addDefenceArea(type);
                        if (type == DefenceArea.CHANNELTYPE) {
                            if (defenceIndex == -1 || (defenceIndex + 8) > 63) {
                                T.showLong(mContext, R.string.add_ceiling);
                                return;
                            }
                            settingsDialogLoading(mContext.getResources()
                                    .getString(R.string.studying));
                            current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN;
                            current_group = (defenceIndex + 8) / 8;
                            current_item = (defenceIndex + 8) % 8;
                            current_defenceType = DefenceArea.CHANNELTYPE;
                            P2PHandler
                                    .getInstance()
                                    .setDefenceAreaState(
                                            mContact.getContactId(),
                                            mContact.getPassword(),
                                            current_group,
                                            current_item,
                                            Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN);
                        } else if (type == DefenceArea.REMOTETYPE) {
                            if (defenceIndex == -1 || defenceIndex > 8) {
                                T.showLong(mContext, R.string.add_ceiling);
                                return;
                            }
                            dialog_loading = new NormalDialog(mContext,
                                    mContext.getResources().getString(
                                            R.string.studying), "", "", "");
                            dialog_loading
                                    .setStyle(NormalDialog.DIALOG_STYLE_LOADING);
                            dialog_loading.showDialog();
                            current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN;
                            current_group = defenceIndex / 8;
                            current_item = defenceIndex % 8;
                            current_defenceType = DefenceArea.REMOTETYPE;
                            P2PHandler
                                    .getInstance()
                                    .setDefenceAreaState(
                                            mContact.getContactId(),
                                            mContact.getPassword(),
                                            current_group,
                                            current_item,
                                            Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN);
                        } else if (type == DefenceArea.SPECIAL_CHANNELTYPE) {
                            if (defenceIndex == -1 || (defenceIndex + 64) > 71) {
                                T.showLong(mContext, R.string.add_ceiling);
                                return;
                            }
                            settingsDialogLoading(mContext.getResources()
                                    .getString(R.string.studying));
                            current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN;
                            current_group = (defenceIndex + 64) / 8;
                            current_item = (defenceIndex + 64) % 8;
                            current_defenceType = DefenceArea.SPECIAL_CHANNELTYPE;
                            P2PHandler
                                    .getInstance()
                                    .setDefenceAreaState(
                                            mContact.getContactId(),
                                            mContact.getPassword(),
                                            current_group,
                                            current_item,
                                            Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN);
                        }

                    }

                });
        dialog_loading.showNormalDialog();
    }

    private int addDefenceArea(int type) {
        int[] itemArray;
        switch (type) {
            case DefenceArea.CHANNELTYPE:
                if (channel.size() == 0) {
                    itemArray = new int[]{};
                } else {
                    itemArray = new int[channel.size() + 1];
                    for (int i = 0; i < channel.size(); i++) {
                        itemArray[i] = channel.get(i).getItemIndex() - 8;
                    }
                    itemArray[channel.size()] = 56;
                }
                return Utils.getNextItem(itemArray);
            case DefenceArea.REMOTETYPE:
                if (remote.size() == 0) {
                    itemArray = new int[]{};
                } else {
                    itemArray = new int[remote.size() + 1];
                    for (int i = 0; i < remote.size(); i++) {
                        itemArray[i] = remote.get(i).getItemIndex();
                    }
                    itemArray[remote.size()] = 8;
                }
                return Utils.getNextItem(itemArray);
            case DefenceArea.SPECIAL_CHANNELTYPE:
                if (special.size() == 0) {
                    itemArray = new int[]{};
                } else {
                    itemArray = new int[special.size() + 1];
                    for (int i = 0; i < special.size(); i++) {
                        itemArray[i] = special.get(i).getItemIndex() - 64;
                    }
                    itemArray[special.size()] = 8;
                }
                return Utils.getNextItem(itemArray);
            default:
                return -1;
        }
    }

    private RemoteAreaSetRecyAdapter.RemoteAreaListener remoteAreaListener = new RemoteAreaSetRecyAdapter.RemoteAreaListener() {

        @Override
        public void deleteRemoteArea(final DefenceArea defenceArea,
                                     final int position) {
            // TODO Auto-generated method stub
            dialog_loading = null;
            dialog_loading = new NormalDialog(mContext, mContext.getResources()
                    .getString(R.string.clear_code), mContext.getResources()
                    .getString(R.string.clear_code_prompt), mContext
                    .getResources().getString(R.string.ensure), mContext
                    .getResources().getString(R.string.cancel));

            dialog_loading
                    .setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

                        @Override
                        public void onClick() {
                            // TODO Auto-generated method stub
                            settingsDialogLoading(mContext.getResources()
                                    .getString(R.string.clearing));
                            current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR;
                            deleteIndex = position;
                            current_defenceType = DefenceArea.REMOTETYPE;
                            P2PHandler
                                    .getInstance()
                                    .setDefenceAreaState(
                                            mContact.getContactId(),
                                            mContact.getPassword(),
                                            defenceArea.getGroup(),
                                            defenceArea.getItem(),
                                            Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR);
                        }
                    });
            dialog_loading.showNormalDialog();
        }

        @Override
        public void changeRemoteAreaName(int position, int type) {
            // TODO Auto-generated method stub
            if (null == inputDialog) {
                inputDialog = new ImputDialog(mContext);
                inputDialog.setOnMyinputClickListner(inputClickListner);
                inputDialog.SetText(
                        "",
                        mContext.getResources().getString(
                                R.string.input_change_name), "", mContext
                                .getResources()
                                .getString(R.string.bt_determine), mContext
                                .getResources().getString(R.string.cancel));
            }
            inputDialog.setInputTitle(mContext.getResources().getString(
                    R.string.change_remote_name));
            inputDialog.setEdtextText(remote.get(position).getName());
            changeIndex = position;
            changeType = type;
            inputDialog.inputDialogShow();
        }
    };

    private ChannelAreaSetRecyAdapter.ChannelAreaListener channelAreaListener = new ChannelAreaSetRecyAdapter.ChannelAreaListener() {

        @Override
        public void deleteChannelArea(final DefenceArea defenceArea,
                                      final int position) {
            // TODO Auto-generated method stub
            dialog_loading = null;
            dialog_loading = new NormalDialog(mContext, mContext.getResources()
                    .getString(R.string.clear_code), mContext.getResources()
                    .getString(R.string.clear_code_prompt), mContext
                    .getResources().getString(R.string.ensure), mContext
                    .getResources().getString(R.string.cancel));
            dialog_loading
                    .setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

                        @Override
                        public void onClick() {
                            // TODO Auto-generated method stub
                            settingsDialogLoading(mContext.getResources()
                                    .getString(R.string.clearing));
                            current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR;
                            deleteIndex = position;
                            current_defenceType = defenceArea.getType();
                            P2PHandler
                                    .getInstance()
                                    .setDefenceAreaState(
                                            mContact.getContactId(),
                                            mContact.getPassword(),
                                            defenceArea.getGroup(),
                                            defenceArea.getItem(),
                                            Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR);
                        }
                    });
            dialog_loading.showNormalDialog();
        }

        @Override
        public void channelAreaSwitch(DefenceArea defenceArea, int position) {
            // TODO Auto-generated method stub
            switchIndex = position;
            switchType = defenceArea.getType();
            current_group = defenceArea.getGroup() - 1;
            current_item = defenceArea.getItem();
            if (defenceArea.getState() == 0) {
                current_Switch = 1;
            } else if (defenceArea.getState() == 1) {
                current_Switch = 0;
            }
            P2PHandler.getInstance().setDefenceAreaAlarmSwitch(
                    mContact.getContactId(), mContact.getPassword(),
                    current_Switch, defenceArea.getGroup() - 1,
                    defenceArea.getItem());

        }

        @Override
        public void selectLocation(DefenceArea defenceArea, int position) {
            // TODO Auto-generated method stub
            toBindIndex = position;
            Intent intent = new Intent();
            intent.putExtra("mContact", mContact);
            intent.putExtra("defenceArea", defenceArea);
            intent.putIntegerArrayListExtra("allLocation", defenceLocation);
            intent.setClass(mContext, BindingLocationActivity.class);
            mContext.startActivity(intent);
        }

        @Override
        public void changeChannelAreaName(int position, int type) {
            // TODO Auto-generated method stub
            if (null == inputDialog) {
                inputDialog = new ImputDialog(mContext);
                inputDialog.setOnMyinputClickListner(inputClickListner);
                inputDialog.SetText(
                        "",
                        mContext.getResources().getString(
                                R.string.input_change_name), "", mContext
                                .getResources()
                                .getString(R.string.bt_determine), mContext
                                .getResources().getString(R.string.cancel));
            }
            inputDialog.setInputTitle(mContext.getResources().getString(
                    R.string.change_channel_name));
            if (type == DefenceArea.CHANNELTYPE) {
                inputDialog.setEdtextText(channel.get(position).getName());
            } else {
                inputDialog.setEdtextText(special.get(position).getName());
            }
            changeIndex = position;
            changeType = type;
            inputDialog.inputDialogShow();
        }
    };

    // 查询所有group为1的防区的预设位
    public void sendChannelLocation() {
        for (int i = 0; i < channel.size(); i++) {
            if (channel.get(i).getGroup() == 1) {
                byte[] data = new byte[]{89, 0, 1, 0,
                        (byte) (channel.get(i).getGroup() - 1),
                        (byte) channel.get(i).getItem(), 0};
                P2PHandler.getInstance().sMesgPresetMotorPos(
                        mContact.getContactId(), mContact.getPassword(), data);
            }
        }
        for (int i = 0; i < special.size(); i++) {
            if (special.get(i).getGroup() == 8) {
                byte[] data = new byte[]{89, 0, 1, 0,
                        (byte) (special.get(i).getGroup() - 1),
                        (byte) special.get(i).getItem(), 0};
                P2PHandler.getInstance().sMesgPresetMotorPos(
                        mContact.getContactId(), mContact.getPassword(), data);
            }
        }
    }

    public void sendLocationByGroupAndID(DefenceArea defenceArea) {
        if (defenceArea.getGroup() == 1 || defenceArea.getGroup() == 8) {
            byte[] data = new byte[]{89, 0, 1, 0,
                    (byte) (defenceArea.getGroup() - 1),
                    (byte) defenceArea.getItem(), 0};
            P2PHandler.getInstance().sMesgPresetMotorPos(mContact.getContactId(),
                    mContact.getPassword(), data);
        }
    }

    public void getChannelLocation(byte[] data) {
        for (int i = 0; i < channel.size(); i++) {
            if ((channel.get(i).getGroup() - 1) == data[4]
                    && channel.get(i).getItem() == data[5]) {
                if (data[6] == 7) {
                    channel.get(i).setLocation(-1);
                } else {
                    channel.get(i).setLocation(data[6]);
                }
            }
        }
        for (int i = 0; i < special.size(); i++) {
            if ((special.get(i).getGroup() - 1) == data[4]
                    && special.get(i).getItem() == data[5]) {
                if (data[6] == 7) {
                    special.get(i).setLocation(-1);
                } else {
                    special.get(i).setLocation(data[6]);
                }
            }
        }

    }

    public List<DefenceArea> getInfoList() {
        dbAreaList = DataManager.findAllDefenceAreaByDeviceID(mContext,
                mContact.contactId);
        List<DefenceArea> list = new ArrayList<DefenceArea>();
        for (DefenceArea re : remote) {
            list.add(re);
        }
        for (DefenceArea ch : channel) {
            list.add(ch);
        }
        for (DefenceArea sp : special) {
            list.add(sp);
        }
        return list;
    }

    public void insertInfoToDB() {
        if (!isInsert) {
            return;
        }
        if (channel.size() == 0 && remote.size() == 0) {
            return;
        }
        if (remote.size() > 0) {
            for (int i = 0; i < remote.size(); i++) {
                DataManager.insertDefenceArea(mContext, mContact.contactId,
                        remote.get(i));
            }
        }
        if (channel.size() > 0) {
            for (int i = 0; i < channel.size(); i++) {
                DataManager.insertDefenceArea(mContext, mContact.contactId,
                        channel.get(i));
            }
        }
        isInsert = false;
    }

    public void getAreaListDB() {
        dbAreaList = DataManager.findAllDefenceAreaByDeviceID(mContext,
                mContact.contactId);
        if (dbAreaList == null) {
            isInsert = true;
            return;
        }
        Collections.sort(dbAreaList);
    }

    public void setAreaInfoByDB() {
        if (isInsert) {
            return;
        }
        for (int i = 0; i < dbAreaList.size(); i++) {
            if (dbAreaList.get(i).getGroup() == 0) {
                remote.add(dbAreaList.get(i));
            } else if (dbAreaList.get(i).getGroup() == 8) {
                special.add(dbAreaList.get(i));
            } else {
                channel.add(dbAreaList.get(i));
            }
        }
    }

    public void contrastInfo(List<DefenceArea> dbList, List<DefenceArea> srcList) {
        if (isInsert) {
            return;
        }
        if (dbList == null) {
            return;
        }
        List<DefenceArea> listdb = new ArrayList<DefenceArea>();
        List<DefenceArea> listsrc = new ArrayList<DefenceArea>();

        listdb.addAll(dbList);
        listsrc.addAll(srcList);

        listdb.removeAll(srcList);
        if (listdb.size() > 0) {
            for (DefenceArea defenceArea : listsrc) {
                DataManager.deleteDefenceAreaByGroupAndItem(mContext,
                        mContact.contactId, defenceArea);
            }
        }

        listsrc.removeAll(dbList);
        if (listsrc.size() > 0) {
            for (DefenceArea defenceArea : listsrc) {
                DataManager.insertDefenceArea(mContext, mContact.contactId,
                        defenceArea);
            }
        }

    }

    public void setAllDefenceName() {
        if (remote.size() > 0) {
            for (int i = 0; i < remote.size(); i++) {
                DefenceArea defenceArea = DataManager
                        .findDefenceAreaByDeviceID(mContext,
                                mContact.contactId, remote.get(i));
                if (defenceArea != null) {
                    remote.get(i).setName(defenceArea.getName());
                    remote.get(i).setEflag(defenceArea.getEflag());
                }
            }
        }

        if (channel.size() > 0) {
            for (int i = 0; i < channel.size(); i++) {
                DefenceArea defenceArea = DataManager
                        .findDefenceAreaByDeviceID(mContext,
                                mContact.contactId, channel.get(i));
                if (defenceArea != null) {
                    channel.get(i).setName(defenceArea.getName());
                    channel.get(i).setEflag(defenceArea.getEflag());
                }
            }
        }
        channelAdapter.updateAll();
        remoteAdapter.updateAll();
    }

    public void setLocation(int[] data) {
        defenceLocation.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 1) {
                defenceLocation.add(i);
            }
        }
        defenceLocation.add(-1);
    }

    ImputDialog.MyInputClickListner inputClickListner = new ImputDialog.MyInputClickListner() {

        @Override
        public void onYesClick(Dialog dialog, View v, String input) {
            // TODO Auto-generated method stub
            if (input.equals("")) {
                T.showShort(mContext, R.string.name_not_null);
                return;
            }
            if (input.getBytes().length > DEFENCE_NAME_LENGTH) {
                T.showShort(mContext, R.string.name_length_beyond);
                return;
            }
            if (isSameName(input)) {
                T.showShort(mContext, R.string.name_is_same);
                return;
            }
            if (changeType == DefenceArea.REMOTETYPE) {
                if (remote.get(changeIndex).getName().equals(input)) {
                    T.showShort(mContext, R.string.update_name_same);
                    return;
                }
                if (isGetName) {
                    changeName = input;
                    P2PHandler.getInstance().setDefenceAreaName(
                            mContact.getContactId(), mContact.getPassword(),
                            structVersion, remote.get(changeIndex).getGroup(),
                            remote.get(changeIndex).getItem(), input);
                    inputDialog.inputDialogDismiss();
                    settingsDialogLoading(mContext.getResources().getString(
                            R.string.update_loading));
                } else {
                    DataManager.updateDefenceAreaNameAndEflagByGroupAndItem(
                            mContext, mContact.contactId, input, 1,
                            remote.get(changeIndex).getGroup(),
                            remote.get(changeIndex).getItem());
                    remote.get(changeIndex).setName(input);
                    remote.get(changeIndex).setEflag(1);
                    remoteAdapter.updateByPosition(changeIndex);
                    inputDialog.inputDialogDismiss();
                    T.showShort(mContext, R.string.modify_success);
                }
            } else if (changeType == DefenceArea.CHANNELTYPE) {
                if (channel.get(changeIndex).getName().equals(input)) {
                    T.showShort(mContext, R.string.update_name_same);
                    return;
                }
                if (isGetName) {
                    changeName = input;
                    P2PHandler.getInstance().setDefenceAreaName(
                            mContact.getContactId(), mContact.getPassword(),
                            structVersion, channel.get(changeIndex).getGroup(),
                            channel.get(changeIndex).getItem(), input);
                    inputDialog.inputDialogDismiss();
                    settingsDialogLoading(mContext.getResources().getString(
                            R.string.update_loading));
                } else {
                    DataManager.updateDefenceAreaNameAndEflagByGroupAndItem(
                            mContext, mContact.contactId, input, 1, channel
                                    .get(changeIndex).getGroup(),
                            channel.get(changeIndex).getItem());
                    channel.get(changeIndex).setName(input);
                    channel.get(changeIndex).setEflag(1);
                    channelAdapter.updateByPosition(changeIndex);
                    inputDialog.inputDialogDismiss();
                    T.showShort(mContext, R.string.modify_success);
                }
            } else if (changeType == DefenceArea.SPECIAL_CHANNELTYPE) {
                if (special.get(changeIndex).getName().equals(input)) {
                    T.showShort(mContext, R.string.update_name_same);
                    return;
                }
                if (isGetName) {
                    changeName = input;
                    P2PHandler.getInstance().setDefenceAreaName(
                            mContact.getContactId(), mContact.getPassword(),
                            structVersion, special.get(changeIndex).getGroup(),
                            special.get(changeIndex).getItem(), input);
                    inputDialog.inputDialogDismiss();
                    settingsDialogLoading(mContext.getResources().getString(
                            R.string.update_loading));
                } else {
                    DataManager.updateDefenceAreaNameAndEflagByGroupAndItem(
                            mContext, mContact.contactId, input, 1, special
                                    .get(changeIndex).getGroup(),
                            special.get(changeIndex).getItem());
                    special.get(changeIndex).setName(input);
                    special.get(changeIndex).setEflag(1);
                    specialAdapter.updateByPosition(changeIndex);
                    inputDialog.inputDialogDismiss();
                    T.showShort(mContext, R.string.modify_success);
                }
            }
            Utils.hindKeyBoard(v);
        }

        @Override
        public void onNoClick(View v) {
            // TODO Auto-generated method stub

        }
    };

    private void ChooseParsingData(byte[] data) {
        if (data.length < 24) {
            return;
        }
        structVersion = data[13];
        switch (structVersion) {
            case 0:
                int remotedataLength = Utils.bytesToInt(data, 16);
                int channeldataLength = Utils.bytesToInt(data, 20);
                int allLength = remotedataLength + channeldataLength;
                if (allLength == 0) {
                    return;
                }
                byte[] defenceData = new byte[allLength];
                System.arraycopy(data, 24, defenceData, 0, allLength);
                getDefenceName(defenceData);
                break;
            default:
                break;
        }
    }

    private void getDefenceName(byte[] data) {
        for (int i = 0; i < data.length / DEFENCE_NAME_INFO_LENGTH; i++) {
            byte[] defenceNameData = new byte[DEFENCE_NAME_INFO_LENGTH];
            System.arraycopy(data, i * DEFENCE_NAME_INFO_LENGTH,
                    defenceNameData, 0, DEFENCE_NAME_INFO_LENGTH);
            byte[] defenceName = new byte[DEFENCE_NAME_LENGTH];
            System.arraycopy(defenceNameData, 3, defenceName, 0,
                    DEFENCE_NAME_LENGTH);
            compareDefenceName(defenceNameData[0], defenceNameData[1],
                    defenceNameData[2], new String(defenceName).trim());
        }
    }

    private void compareDefenceName(int group, int item, int editFlag,
                                    String name) {
        if (editFlag == 0) {
            return;
        }
        if (group < 1) {
            for (int i = 0; i < remote.size(); i++) {
                if (remote.get(i).getGroup() == group
                        && remote.get(i).getItem() == item) {
                    if (!remote.get(i).getName().equals(name)) {
                        DataManager
                                .updateDefenceAreaNameAndEflagByGroupAndItem(
                                        mContext, mContact.contactId, name,
                                        editFlag, group, item);
                        remote.get(i).setEflag(editFlag);
                        remote.get(i).setName(name);
                    }
                }
            }
            remoteAdapter.updateAll();
        } else if (group == 8) {
            for (int i = 0; i < special.size(); i++) {
                if (special.get(i).getGroup() == group
                        && special.get(i).getItem() == item) {
                    if (!special.get(i).getName().equals(name)) {
                        DataManager
                                .updateDefenceAreaNameAndEflagByGroupAndItem(
                                        mContext, mContact.contactId, name,
                                        editFlag, group, item);
                        special.get(i).setEflag(editFlag);
                        special.get(i).setName(name);
                    }
                }
            }
            specialAdapter.updateAll();
        } else {
            for (int i = 0; i < channel.size(); i++) {
                if (channel.get(i).getGroup() == group
                        && channel.get(i).getItem() == item) {
                    if (!channel.get(i).getName().equals(name)) {
                        DataManager
                                .updateDefenceAreaNameAndEflagByGroupAndItem(
                                        mContext, mContact.contactId, name,
                                        editFlag, group, item);
                        channel.get(i).setEflag(editFlag);
                        channel.get(i).setName(name);
                    }
                }
            }
            channelAdapter.updateAll();
        }
    }

    public void settingsDialogLoading(String title) {
        if (null != dialog_loading) {
            dialog_loading.dismiss();
            dialog_loading = null;
        }
        dialog_loading = new NormalDialog(mContext, title, "", "", "");
        dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
        dialog_loading.showDialog();
    }

    public boolean isSameName(String name) {
        for (int i = 0; i < remote.size(); i++) {
            if (remote.get(i).getName().equals(name)) {
                return true;
            }
        }
        for (int i = 0; i < channel.size(); i++) {
            if (channel.get(i).getName().equals(name)) {
                return true;
            }
        }
        for (int i = 0; i < special.size(); i++) {
            if (special.get(i).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private DefenceArea findExistedSensor(int group, int item) {
        if (remote.size() > 0) {
            for (DefenceArea da : remote) {
                if (da.getGroup() == group && da.getItem() == item) {
                    return da;
                }
            }
        }
        if (special.size() > 0) {
            for (DefenceArea da : special) {
                if (da.getGroup() == group && da.getItem() == item) {
                    return da;
                }
            }
        }
        if (channel.size() > 0) {
            for (DefenceArea da : channel) {
                if (da.getGroup() == group && da.getItem() == item) {
                    return da;
                }
            }
        }
        return null;
    }
}
