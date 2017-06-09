package com.jwkj;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.data.Contact;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.PhoneWatcher;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.AlarmCloseVoice;
import com.jwkj.widget.AlarmCloseVoice.closeClickListener;
import com.jwkj.widget.MoniterTimeTextview;
import com.jwkj.widget.MyInputPassDialog;
import com.jwkj.widget.MyInputPassDialog.OnCustomDialogListener;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnAlarmClickListner;
import com.jwkj.widget.PlayBackFastLayout;
import com.jwkj.widget.control.MonitorRangeSeekBar;
import com.jwkj.widget.control.MonitorRangeSeekBar.OnMonitorRangeSeekBarChangeListener;
import com.jwkj.widget.control.MonitorTitleViewGroup;
import com.jwkj.widget.control.MonitorTitleViewGroup.onTitleClickListner;
import com.kandaovr.tracking.representation.Quaternion;
import com.kandaovr.tracking.tracker.SensorTracker;
import com.kandaovr.tracking.tracker.SensorTrackerListener;
import com.nuowei.smarthome.R;
import com.p2p.core.BasePlayBackActivity;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PView;

import java.io.IOException;
import java.util.ArrayList;

import static com.jwkj.activity.ApMonitorActivity.mainType;
import static com.jwkj.activity.ApMonitorActivity.subType;

public class PlayBackActivity extends BasePlayBackActivity implements
        OnClickListener, OnTouchListener, OnMonitorRangeSeekBarChangeListener {
    private int mCurrentVolume, mMaxVolume;
    private AudioManager mAudioManager = null;
    LinearLayout control_bottom;
    private boolean isControlShow = true;
    private boolean mIsCloseVoice = false;
    ImageView stopVoice, previous, pause, next;
    Context mContext;
    private MonitorRangeSeekBar seekbar;
    boolean isPause = false;
    boolean isRegFilter = false;

    boolean isScroll = false;
    boolean isReject = false;
    PhoneWatcher mPhoneWatcher;
    ArrayList<String> list = new ArrayList<String>();
    private int currentFile = 0;
    private int currentFileTemp = currentFile;
    private MonitorTitleViewGroup PanBack;
    private Contact mContact;
    private MoniterTimeTextview TimeTextView;
    //全景增加
    SensorTracker mSensorTracker = null;
    private boolean bSensorTracker = false;
    //报警推送
    private String NewMessageDeviceID = "";
    private int pushAlarmType;
    private boolean isCustomCmdAlarm = false;
    private String alarm_id = "";
    AlarmCloseVoice acl;
    int mainTypesubType;
    Contact alarm_contact;
    private RelativeLayout playBackLayout;

    private ImageView fast;
    private PlayBackFastLayout fastLayout;
    private boolean isStartFast = false;
    private NormalDialog normalDialog;
    private boolean isShowDialog=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        P2PConnect.setPlaying(true);
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.p2p_playback);
        mContext = this;
        mContact = (Contact) getIntent().getSerializableExtra("contact");
        if (savedInstanceState == null) {
            list = getIntent().getStringArrayListExtra("playbacklist");
            currentFile = getIntent().getIntExtra("currentFile", 0);
        } else {
            list = savedInstanceState.getStringArrayList("playbacklist");
            currentFile = savedInstanceState.getInt("currentFile", 0);
        }
        currentFileTemp = currentFile;
        initComponent();
        regFilter();
        startWatcher();
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        mCurrentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSensorTracker = new SensorTracker(this);
        mSensorTracker.registerListener(new SensorTrackerListener() {

            @Override
            public void onOrientationChange(Quaternion orientation) {
                MediaPlayer.setTrackerQuat(orientation.getX(), orientation.getY(), orientation.getZ(), orientation.getW());
            }
        });
    }

    @Override
    public void onHomePressed() {
        // TODO Auto-generated method stub
        super.onHomePressed();
        reject();
    }

    private void upDataCurrentFile() {
        currentFile = currentFileTemp;
    }

    private void startWatcher() {
        mPhoneWatcher = new PhoneWatcher(mContext);
        mPhoneWatcher
                .setOnCommingCallListener(new PhoneWatcher.OnCommingCallListener() {

                    @Override
                    public void onCommingCall() {
                        // TODO Auto-generated method stub
                        reject();
                    }

                });
        mPhoneWatcher.startWatcher();
    }

    private void initComponent() {
        pView = (P2PView) findViewById(R.id.pView);
        this.initP2PView(P2PConnect.getCurrentDeviceType(),P2PView.LAYOUTTYPE_SEPARATION);
        pView.fullScreen();
        setPanorama(mContact.isPanorama());
        control_bottom = (LinearLayout) findViewById(R.id.control_bottom);
        stopVoice = (ImageView) findViewById(R.id.close_voice);
        previous = (ImageView) findViewById(R.id.previous);
        pause = (ImageView) findViewById(R.id.pause);
        next = (ImageView) findViewById(R.id.next);
        seekbar = (MonitorRangeSeekBar) findViewById(R.id.seek_bar);
        playBackLayout = (RelativeLayout) findViewById(R.id.playback_layout);
        fast = (ImageView) findViewById(R.id.fast);
        //快进框添加
        fastLayout = new PlayBackFastLayout(mContext);
        playBackLayout.addView(fastLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fastLayout.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        fastLayout.setLayoutParams(params);
        fastLayout.setVisibility(View.GONE);

        stopVoice.setOnClickListener(this);
        control_bottom.setOnTouchListener(this);
        previous.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        seekbar.setOnRangeSeekBarChangeListener(this);
        seekbar.setRangeValues(0,0);
        fast.setOnClickListener(this);
        fastLayout.setOnClickListener(fastLayoutListener);

        if (mContact != null && mContact.isPanorama()) {
            setPanorama(true);
            initTimeTextView();
        } else {
            setPanorama(false);
        }
        PanBack = new MonitorTitleViewGroup(mContext);
        PanBack.setOnTitleClickListner(TitleClickListner);
        ViewGroup view = (ViewGroup) pView.getParent();
        view.addView(PanBack);
        normalDialog=new NormalDialog(mContext);
        normalDialog.setOnCancelListener(cancelListener);
        normalDialog.setOnNormalDialogTimeOutListner(timeOutListner);
    }

    private void initTimeTextView() {
        TimeTextView = new MoniterTimeTextview(mContext);
        ViewGroup r_p2pview = (ViewGroup) pView.getParent();
        r_p2pview.addView(TimeTextView);
    }

    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.P2P_REJECT);
        filter.addAction(Constants.P2P.PLAYBACK_CHANGE_SEEK);
        filter.addAction(Constants.P2P.PLAYBACK_CHANGE_STATE);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 正在监控时新设备弹框
        filter.addAction(Constants.Action.MONITOR_NEWDEVICEALARMING);
        filter.addAction(Constants.P2P.RET_KEEP_CLIENT);
        filter.addAction(Constants.P2P.DELETE_BINDALARM_ID);
        filter.addAction(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT);
        registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.P2P.P2P_REJECT)) {
                reject();
            } else if (intent.getAction().equals(
                    Constants.P2P.PLAYBACK_CHANGE_SEEK)) {
                if (!isScroll) {
                    int max = intent.getIntExtra("max", 0);
                    int current= intent.getIntExtra("current", 0);
                    seekbar.setRangeValues(0, max);
                    seekbar.setSelectedMaxValue(current);
                    if (isStartFast){
                        fastLayout.setFastTime(current);
                    }
                    if (current==max){
                        if (isStartFast){
                            dismissFastLayout();
                        }
                    }
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.PLAYBACK_CHANGE_STATE)) {
                int state = intent.getIntExtra("state", 0);
                switch (state) {
                    case 0:
                        isPause = true;
                        pause.setImageResource(R.drawable.selector_playback_play);
                        break;
                    case 1:
                        isPause = true;
                        pause.setImageResource(R.drawable.selector_playback_play);
                        break;
                    case 2:
                        isPause = false;
                        pause.setImageResource(R.drawable.selector_playback_pause);
                        break;
                    case 16:
                        if (isShowDialog){
                            normalDialog.dismiss();
                            isShowDialog=false;
                        }
                        if (!isStartFast){
                            showFastLayout();
                            if (isPause){
                                isPause = false;
                                pause.setImageResource(R.drawable.selector_playback_pause);
                            }
                        }
                        break;
                    case 18:
                        if (isShowDialog){
                            normalDialog.dismiss();
                            isShowDialog=false;
                        }
                        if (isStartFast){
                            dismissFastLayout();
                            if (isPause){
                                isPause = false;
                                pause.setImageResource(R.drawable.selector_playback_pause);
                            }
                        }
                        break;
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                reject();
            } else if (intent.getAction().equals(Constants.Action.MONITOR_NEWDEVICEALARMING)) {
                // 弹窗
                int MessageType = intent.getIntExtra("messagetype", 2);
                int type = intent.getIntExtra("alarm_type", 0);
                pushAlarmType = type;
                isCustomCmdAlarm = intent.getBooleanExtra("isCustomCmdAlarm", false);
                int group = intent.getIntExtra("group", -1);
                int item = intent.getIntExtra("item", -1);
                boolean isSupport = intent.getBooleanExtra("isSupport", false);
                boolean isSupportDelete = intent.getBooleanExtra("isSupportDelete", false);
                subType = intent.getIntExtra("subType", -1);
                mainType = intent.getIntExtra("mainType", -1);
                String customMsg = intent.getStringExtra("customMsg");
                if (MessageType == 1) {
                    // 报警推送
                    NewMessageDeviceID = intent.getStringExtra("alarm_id");
                    if (alarm_id.equals(NewMessageDeviceID) && passworddialog != null && passworddialog.isShowing()) {
                        return;
                    } else {
                        alarm_id = NewMessageDeviceID;
                    }
                } else {
                    // 透传推送
                    NewMessageDeviceID = intent.getStringExtra("contactId");
                    Log.i("dxsmoniter_alarm", "透传推送" + NewMessageDeviceID);
                }
                String alarmtype = Utils.getAlarmType(NewMessageDeviceID, type, isSupport, group,
                        item, customMsg);
                StringBuffer NewMassage = new StringBuffer(
                        Utils.getStringByResouceID(R.string.tab_device))
                        .append("：").append(
                                Utils.getDeviceName(NewMessageDeviceID));
                NewMassage.append("\n").append(Utils.getStringByResouceID(R.string.allarm_type)).
                        append(alarmtype);
                int alarmstate = Constants.DeviceMuteState.NORMAL;
                if (type == P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
                    alarmstate = Constants.DeviceMuteState.CLOSE;
                }
                acl = new AlarmCloseVoice(mContext, NewMessageDeviceID);
                acl.setState(alarmstate);
                acl.setcloseClickListener(clistener);
                NewMessageDialog(NewMassage.toString(), NewMessageDeviceID, isSupportDelete, acl);
            } else if (intent.getAction().equals(Constants.P2P.RET_KEEP_CLIENT)) {
                int iSrcID = intent.getIntExtra("iSrcID", -1);
                byte boption = intent.getByteExtra("boption", (byte) -1);
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    if (acl != null) {
                        acl.setState(2);
                        acl.startLoading();
                    }
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.DELETE_BINDALARM_ID)) {
                int result = intent.getIntExtra("deleteResult", 1);
                int resultType = intent.getIntExtra("resultType", -1);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (result == 0) {
                    if (resultType == 1) {
                        // 删除成功
                        T.showShort(mContext, R.string.modify_success);
                    }
                } else if (result == -1) {
                    // 不支持
                    T.showShort(mContext, R.string.device_not_support);
                } else {
                    // 失败
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT)) {
                String contactId = intent.getStringExtra("contactId");
                if (NewMessageDeviceID.equals(contactId) && pushAlarmType == P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }

        }
    };
    /**
     * 新报警信息
     */
    private NormalDialog dialog;
    private String contactidTemp = "";

    private void NewMessageDialog(String Meassage, final String contacid,
                                  boolean isSurportdelete, AlarmCloseVoice alarmCloseVoice) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog = new NormalDialog(mContext);
        dialog.setContentStr(Meassage);
        dialog.setbtnStr1(R.string.check);
        dialog.setbtnStr2(R.string.cancel);
        dialog.setbtnStr3(R.string.clear_bundealarmid);
        dialog.showAlarmDialog(isSurportdelete, contacid, alarmCloseVoice);
        dialog.setOnAlarmClickListner(AlarmClickListner);
        contactidTemp = contacid;
    }

    closeClickListener clistener = new closeClickListener() {

        @Override
        public void onclose(String deviceId, int type) {
            // TODO Auto-generated method stub
            acl.setState(1);
            acl.startLoading();
            closeAlarmVoice(deviceId, type);
        }
    };

    //关闭本次报警声音
    public void closeAlarmVoice(String alarmID, int type) {
        final Contact contact = FList.getInstance().isContact(alarmID);
        if (null != contact) {
            NormalDialog dialog = new NormalDialog(mContext, mContext
                    .getResources().getString(R.string.mute_the_alarm), mContext
                    .getResources().getString(R.string.comfirm_mute_the_alarm),
                    mContext.getResources().getString(R.string.exit),
                    mContext.getResources().getString(R.string.cancel));
            dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

                @Override
                public void onClick() {
                    if (acl != null) {
                        acl.setState(1);
                        acl.startLoading();
                    }
                    FisheyeSetHandler.getInstance().sKeepClientCmd(contact.getIpContactId(), contact.contactPassword);
                }
            });
            dialog.showNormalDialog();
        } else {
            closecreatePassDialog(alarmID);
        }

    }

    //关闭本次报警声音
    public void closeAlarmVoice(String alarmID) {
        final Contact contact = FList.getInstance().isContact(alarmID);
        if (null != contact) {
            NormalDialog dialog = new NormalDialog(mContext, mContext
                    .getResources().getString(R.string.mute_the_alarm), mContext
                    .getResources().getString(R.string.comfirm_mute_the_alarm),
                    mContext.getResources().getString(R.string.confirm),
                    mContext.getResources().getString(R.string.cancel));
            dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

                @Override
                public void onClick() {

                }
            });
            dialog.showNormalDialog();
        } else {
            closecreatePassDialog(alarmID);
        }

    }

    private Dialog closepassworddialog;

    void closecreatePassDialog(String id) {
        closepassworddialog = new MyInputPassDialog(mContext, Utils.getStringByResouceID(R.string.mute_the_alarm), id, getResources().getString(R.string.inputpassword), closelistener);
        closepassworddialog.show();
    }

    private OnCustomDialogListener closelistener = new OnCustomDialogListener() {

        @Override
        public void check(final String password, final String id) {
            if (password.trim().equals("")) {
                T.showShort(mContext, R.string.create_pwd);
                return;
            }

            if (password.length() > 30 || password.charAt(0) == '0') {
                T.showShort(mContext, R.string.device_password_invalid);
                return;
            }
            if (acl != null) {
                acl.setState(1);
                acl.startLoading();
            }
            String pwd = P2PHandler.getInstance().EntryPassword(password);
            FisheyeSetHandler.getInstance().sKeepClientCmd(id, pwd);
            closepassworddialog.dismiss();
        }
    };
    private Dialog passworddialog;

    void createPassDialog(String id) {
        passworddialog = new MyInputPassDialog(mContext, Utils.getStringByResouceID(R.string.check), id, getResources().getString(R.string.inputpassword), listener);
        passworddialog.show();
    }

    private OnCustomDialogListener listener = new OnCustomDialogListener() {

        @Override
        public void check(final String password, final String id) {
            if (password.trim().equals("")) {
                T.showShort(mContext, R.string.input_monitor_pwd);
                return;
            }

            if (password.length() > 30 || password.charAt(0) == '0') {
                T.showShort(mContext, R.string.device_password_invalid);
                return;
            }
            String pwd=P2PHandler.getInstance().EntryPassword(password);
            final Contact contact = new Contact();
            contact.contactId = id;
            contact.activeUser = NpcCommon.mThreeNum;
            contact.contactName = id;
            contact.contactPassword = pwd;
            contact.contactType = mainType;
            contact.subType = subType;
            alarm_contact = contact;
//				callDevice(contact);
            P2PConnect.vReject(0, "");
            Message msg = new Message();
            msg.obj = contact;
            handler.sendMessageDelayed(msg, 500);
        }
    };
    /**
     * 监控对话框单击回调
     */
    private OnAlarmClickListner AlarmClickListner = new OnAlarmClickListner() {

        @Override
        public void onOkClick(String alarmID, boolean isSurportDelete,
                              Dialog dialog) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            // 查看新监控--挂断当前监控，再次呼叫另一个监控
            seeMonitor(alarmID);
        }

        @Override
        public void onDeleteClick(String alarmID, boolean isSurportDelete,
                                  Dialog dialog) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            DeleteDevice(alarmID);
        }

        @Override
        public void onCancelClick(String alarmID, boolean isSurportDelete,
                                  Dialog dialog) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    };

    private void seeMonitor(String contactId) {
        final Contact contact = FList.getInstance().isContact(contactId);
        if (null != contact) {
            reject();
            P2PConnect.vReject(0, "");
            Message msg = new Message();
            msg.obj = contact;
            handler.sendMessageDelayed(msg, 500);
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            createPassDialog(contactId);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Contact contact = (Contact) msg.obj;
            Intent monitor = new Intent();
            monitor.setClass(mContext, ApMonitorActivity.class);
            monitor.putExtra("contact", contact);
            monitor.putExtra("connectType", Constants.ConnectType.P2PCONNECT);
            if(pushAlarmType==P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH){
                monitor.putExtra("isSurpportOpenDoor", true);
            }
            monitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(monitor);
            return false;
        }
    });

    // 解绑确认弹框
    private void DeleteDevice(final String alarmId) {
        dialog = new NormalDialog(mContext, mContext.getResources().getString(
                R.string.clear_bundealarmid), mContext.getResources()
                .getString(R.string.clear_bundealarmid_tips), mContext
                .getResources().getString(R.string.ensure), mContext
                .getResources().getString(R.string.cancel));
        dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

            @Override
            public void onClick() {
                P2PHandler.getInstance().DeleteDeviceAlarmId(
                        String.valueOf(alarmId));
                dialog.dismiss();
                ShowLoading();
            }
        });
        dialog.showDialog();
    }

    private void ShowLoading() {
        dialog = new NormalDialog(mContext);
        dialog.showLoadingDialog();
    }

    public void reject() {
        if (!isReject) {
            isReject = true;
            P2PHandler.getInstance().reject();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        reject();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            mCurrentVolume++;
            if (mCurrentVolume > mMaxVolume) {
                mCurrentVolume = mMaxVolume;
            }

            if (mCurrentVolume != 0) {
                mIsCloseVoice = false;
                stopVoice.setImageResource(R.drawable.selector_playback_voice);
            }
            return false;
        } else if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mCurrentVolume--;
            if (mCurrentVolume < 0) {
                mCurrentVolume = 0;
            }

            if (mCurrentVolume == 0) {
                mIsCloseVoice = true;
                stopVoice
                        .setImageResource(R.drawable.selector_playback_voiceno);
            }

            return false;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDestroy() {
        Log.e("myyy", "onDestroy");
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    mCurrentVolume, 0);
        }
        if (isRegFilter) {
            mContext.unregisterReceiver(mReceiver);
            isRegFilter = false;
        }
        if (null != mPhoneWatcher) {
            mPhoneWatcher.stopWatcher();
        }
        P2PConnect.setPlaying(false);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.close_voice:
                if (mIsCloseVoice) {
                    mIsCloseVoice = false;
                    stopVoice.setImageResource(R.drawable.selector_playback_voice);
                    if (mCurrentVolume == 0) {
                        mCurrentVolume = 1;
                    }
                    if (mAudioManager != null) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                mCurrentVolume, 0);
                    }
                } else {
                    mIsCloseVoice = true;
                    stopVoice
                            .setImageResource(R.drawable.selector_playback_voiceno);
                    if (mAudioManager != null) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
                                0);
                    }
                }
                break;
            case R.id.pause:
                if (isPause) {
                    this.startPlayBack();
                } else {
                    this.pausePlayBack();
                }
                break;
            case R.id.next:
                Next();
                break;
            case R.id.previous:
                currentFileTemp--;
                if (currentFileTemp <= -1) {
                    T.showShort(mContext, R.string.no_previous_file);
                    currentFileTemp++;
                } else {
                    if (currentFileTemp < list.size()
                            && this.previous(list.get(currentFileTemp))) {
                        if (isStartFast){
                            dismissFastLayout();
                        }
                        upDataCurrentFile();
                    } else {
                        currentFileTemp++;
                    }
                }
                break;
            case R.id.fast:
                if (isShowDialog){
                    normalDialog.dismiss();
                    isShowDialog=false;
                }
                normalDialog.showLoadingDialog();
                normalDialog.setCanceledOnTouchOutside(true);
                normalDialog.setTimeOut(8000);
                isShowDialog=true;
                if (!isStartFast) {
                    fastPlay((Integer) seekbar.getSelectedMaxValue());
                } else {
                    fastPlayCancel((Integer) seekbar.getSelectedMaxValue());
                }
                break;
        }

    }

    private void Next() {
        currentFileTemp++;
        if (currentFileTemp >= list.size()) {
            T.showShort(mContext, R.string.no_next_file);
            currentFileTemp--;
        } else {
            if (this.next(list.get(currentFileTemp))) {
                if (isStartFast){
                    dismissFastLayout();
                }
                upDataCurrentFile();
            } else {
                // 播放错误
                currentFileTemp--;
            }
        }
    }

    public void changeControl() {
        if (isControlShow) {
            isControlShow = false;
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_top);
            Animation anim3 = AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_bottom);
            anim2.setDuration(300);
            anim3.setDuration(300);
            control_bottom.startAnimation(anim2);
            PanBack.startAnimation(anim3);
            PanBack.setVisibility(View.GONE);
            control_bottom.setVisibility(View.GONE);

        } else {
            isControlShow = true;
            control_bottom.setVisibility(View.VISIBLE);
            PanBack.setVisibility(View.VISIBLE);
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_bottom);
            Animation anim3 = AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_top);
            anim2.setDuration(300);
            anim3.setDuration(300);
            control_bottom.startAnimation(anim2);
            PanBack.startAnimation(anim3);
        }
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.control_bottom:
                return true;
        }
        return false;
    }

    @Override
    public int getActivityInfo() {
        // TODO Auto-generated method stub
        return Constants.ActivityInfo.ACTIVITY_PLAYBACKACTIVITY;
    }

    @Override
    protected void onP2PViewSingleTap() {
        // TODO Auto-generated method stub
        changeControl();
    }

    @Override
    protected void onGoBack() {
        // TODO Auto-generated method stub
//        MyApplication.app.showNotification();
    }

    @Override
    protected void onGoFront() {
        // TODO Auto-generated method stub
//        MyApplication.app.hideNotification();
    }

    @Override
    protected void onExit() {
        // TODO Auto-generated method stub
//        MyApplication.app.hideNotification();
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                // Toast.makeText(getApplicationContext(),R.string.Press_again_exit,
                // Toast.LENGTH_SHORT).show();
                T.showShort(mContext, R.string.Press_again_exit);
                exitTime = System.currentTimeMillis();
            } else {
                reject();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRangeSeekBarValuesChanged(MonitorRangeSeekBar bar,
                                            Object minValue, Object maxValue) {
        bar.setSelectedMaxValue((Number) maxValue);
        if ((Integer) maxValue == bar.getAbsoluteMaxValue()) {
            Next();
        }
    }

    @Override
    public void onRangeSeekBarValuesSelected(MonitorRangeSeekBar bar,
                                             Object minValue, Object maxValue, int type) {
        if (isStartFast){
            dismissFastLayout();
        }
        this.jump((Integer) maxValue);
    }

    private onTitleClickListner TitleClickListner = new onTitleClickListner() {

        @Override
        public void onBackClick(View view) {
            reject();
        }
    };

    @Override
    protected void onVideoPTS(final long videoPTS) {
        if (TimeTextView != null) {
            ((PlayBackActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TimeTextView.setTime(videoPTS);
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorTracker.stopTracking();
    }

    @Override
    public void DoubleTap() {
//		Log.d(TAG, "DoubleTap >> bSensorTracker " + bSensorTracker);
        if (!bSensorTracker) {
            mSensorTracker.startTracking();
            bSensorTracker = true;
            try {
                MediaPlayer.getInstance()._OnGesture(5, 1, 0, 0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            mSensorTracker.stopTracking();
            bSensorTracker = false;
            try {
                MediaPlayer.getInstance()._OnGesture(5, 0, 0, 0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void showFastLayout() {
        if (fastLayout != null) {
            fastLayout.setVisibility(View.VISIBLE);
        }
        isStartFast = true;
    }

    private void dismissFastLayout() {
        if (fastLayout != null) {
            fastLayout.setVisibility(View.GONE);
        }
        isStartFast = false;
    }

    private OnClickListener fastLayoutListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isStartFast){
                fastPlayCancel((Integer) seekbar.getSelectedMaxValue());
            }
        }
    };

    private DialogInterface.OnCancelListener cancelListener=new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            SetNoneState();
        }
    };

    private NormalDialog.OnNormalDialogTimeOutListner timeOutListner=
            new NormalDialog.OnNormalDialogTimeOutListner() {
        @Override
        public void onTimeOut() {
            SetNoneState();
        }
    };

}
