package com.jwkj.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.CallActivity;
import com.jwkj.P2PConnect;
import com.jwkj.SettingListener;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.Prepoint;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.fragment.MonitorOneFragment;
import com.jwkj.fragment.MonitorThreeFrag;
import com.jwkj.fragment.MonitorTwoFragment;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.listener.CopyImgaShootTask;
import com.jwkj.listener.DrawTextImageTask;
import com.jwkj.listener.MainFragmentAdapter;
import com.jwkj.utils.T;
import com.jwkj.utils.TextViewUtils;
import com.jwkj.utils.Utils;
import com.jwkj.widget.AlarmCloseVoice;
import com.jwkj.widget.AlarmCloseVoice.closeClickListener;
import com.jwkj.widget.ConfirmOrCancelDialog;
import com.jwkj.widget.GuideRelayout;
import com.jwkj.widget.GuideRelayout.GuideListner;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.InputPasswordDialog;
import com.jwkj.widget.MoniterTimeTextview;
import com.jwkj.widget.MyInputPassDialog;
import com.jwkj.widget.MyInputPassDialog.OnCustomDialogListener;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnAlarmClickListner;
import com.jwkj.widget.PreSetLocationLayout;
import com.jwkj.widget.PreSetLocationLayout.PreSetLocationListener;
import com.jwkj.widget.VerticalSeekBar;
import com.jwkj.widget.VerticalSeekBar.OnVerticalSeekBarChangeListener;
import com.jwkj.widget.VideoStatusView;
import com.jwkj.widget.control.MonitorNumberTextView;
import com.jwkj.widget.control.MonitorPanImConView;
import com.jwkj.widget.control.MonitorPanView;
import com.jwkj.widget.control.MonitorPanView.onMonitorPanClickListner;
import com.jwkj.widget.control.MonitorTitleViewGroup;
import com.jwkj.widget.control.MonitorTitleViewGroup.onTitleClickListner;
import com.kandaovr.tracking.representation.Quaternion;
import com.kandaovr.tracking.tracker.SensorTracker;
import com.kandaovr.tracking.tracker.SensorTrackerListener;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.BaseMonitorActivity;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PView;
import com.p2p.core.utils.BaiduTjUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApMonitorActivity extends BaseMonitorActivity implements
        OnClickListener, OnPageChangeListener {
    RelativeLayout layout_title;
    ImageView iv_full_screen, iv_voice, open_door, iv_opendor;
    LinearLayout l_control;
    RelativeLayout rl_control, control_bottom;
    LinearLayout control_top;
    Button choose_video_format;
    View line;
    ImageView back_btn;
    TextView video_mode_hd, video_mode_sd, video_mode_ld;
    ImageView close_voice, send_voice, iv_half_screen, hungup, screenshot,
            defence_state;
    LinearLayout layout_voice_state;
    TextView tv_name;
    RelativeLayout r_p2pview;
    ImageView voice_state;
    RelativeLayout l_zoom;
    LinearLayout l_f;
    public static Contact mContact;
    int callType = 3;
    private Context mContext;
    boolean isReject = false;
    boolean isRegFilter = false;
    private int ScrrenOrientation;
    int window_width, window_height;
    int connectType;
    private int defenceState = -1;
    boolean mIsCloseVoice = false;
    int mCurrentVolume, mMaxVolume;
    AudioManager mAudioManager;
    public static boolean isSurpportOpenDoor = false;
    boolean isShowVideo = false;
    public static boolean isSpeak = false;
    int current_video_mode;
    int screenWidth;
    int screenHeigh;
    private String NewMessageDeviceID = "";
    // 刷新监控部分
    private RelativeLayout rlPrgTxError;
    private TextView txError, tx_wait_for_connect;
    private Button btnRefrash;
    private ProgressBar progressBar;
    private HeaderView ivHeader;
    private String alarm_id = "";

    private boolean isReceveHeader = false;
    boolean isPermission = true;
    private View vLineHD;
    private boolean connectSenconde = false;
    int pushAlarmType;
    boolean isCustomCmdAlarm = false;
    private Handler mhandler = new Handler();
    private boolean mIsLand = false; // 是否是横屏
    OrientationEventListener mOrientationEventListener;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    MonitorOneFragment mOneFrag;
    MonitorTwoFragment mTwoFrag;
    MonitorThreeFrag mThreeFrag;
    private FragmentManager fragmentManager;
    ViewPager viewPager;
    ImageView iv_point_one, iv_point_two, iv_point_three;
    int deviceType;
    public static int subType, mainType;
    private VerticalSeekBar seebar_zoom;
    boolean isFoucusZoom = false;
    int currentPosition = 0;
    NormalDialog dialog_visit;
    // 灯光控制
    RelativeLayout rlLampControl;
    ProgressBar progressBarLamp;
    ImageView iv_light;
    int lamp_switch;
    int cur_modify_lamp_state;
    public static byte PrePointInfo = -1;
    public static int PreFunctionMode = -2;
    public static boolean userCanClick = false;
    // 监控当前设备推送报警信息，显示关闭本次报警按钮
    AlarmCloseVoice alarmClose;
    boolean isShowAlarmClose = false;
    AlarmCloseVoice acl;
    // 区分静音正在监控的设备还是其它设备
    public final int KEEP_SELF_CLIENT = 0;
    public final int KEEP_OTHER_CLIENT = 1;
    // 设备静音按钮状态
    public final int NORMAL = 0;
    public final int LOADING = 1;
    public final int CLOSE = 2;
    private PreSetLocationLayout preSetLocationLayout;
    private ArrayList<Integer> preSetLocations = new ArrayList<Integer>();
    private NormalDialog loading,checkpwdLoading;
    boolean isCallAnother = false;
    //变焦变倍加减
    ImageView zoomAdd, zoomReduce;
    // 全景增加
    SensorTracker mSensorTracker = null;
    private boolean bSensorTracker = false;// 是否是陀螺仪模式
    private MonitorNumberTextView MOnitorNumber;
    public static boolean isSupportPrepoint = false;
    int Heigh = 0;
    ImageView iv_set;
    private VideoStatusView vStatusView;
    InputPasswordDialog inputPwdDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apmonitor);
        P2PConnect.setPlaying(true);
        isSpeak = false;
        PrePointInfo = -1;
        PreFunctionMode = -2;
        userCanClick = false;
        isSupportPrepoint = false;
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mContext = this;
        mContact = (Contact) getIntent().getSerializableExtra("contact");
        deviceType = mContact.contactType;
        subType = mContact.subType;
        mainType = mContact.contactType;
        if (mContact.contactType == P2PValue.DeviceType.IPC) {
            setIsLand(false);
        } else {
            setIsLand(true);
        }
        setHalfScreen(true);
        connectType = getIntent().getIntExtra("connectType", Constants.ConnectType.P2PCONNECT);
        isSurpportOpenDoor = getIntent().getBooleanExtra("isSurpportOpenDoor", false);
        isCustomCmdAlarm = getIntent().getBooleanExtra("isCustomCmdAlarm", false);
        P2PHandler.getInstance().getNpcSettings(mContact.getIpContactId(), mContact.getPassword());
        P2PHandler.getInstance().getFocusZoom(mContact.getIpContactId(), mContact.getPassword());
        P2PHandler.getInstance().checkPassword(mContact.getIpContactId(), mContact.getPassword());
        P2PConnect.setMonitorId(mContact.contactId);// 设置在监控的ID
        SettingListener.setMonitorID(mContact.contactId);// 设置在监控的ID
        P2PConnect.setCurrent_state(P2PConnect.P2P_STATE_CALLING);
        P2PConnect.setCurrent_call_id(mContact.contactId);
        getScreenWithHeigh();
        regFilter();
        callDevice();
        initcComponent();
        // readyCallDevice();
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        mCurrentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        ScrrenOrientation = Configuration.ORIENTATION_PORTRAIT;
    }

    public void initcComponent() {
        pView = (P2PView) findViewById(R.id.p2pview);
        P2PView.type = 0;
        pView.setHandler(myHandler);
        layout_title = (RelativeLayout) findViewById(R.id.layout_title);
        iv_full_screen = (ImageView) findViewById(R.id.iv_full_screen);
        l_control = (LinearLayout) findViewById(R.id.l_control);
        line = (View) findViewById(R.id.line);
        rl_control = (RelativeLayout) findViewById(R.id.rl_control);
        iv_voice = (ImageView) findViewById(R.id.iv_voice);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        control_bottom = (RelativeLayout) findViewById(R.id.control_bottom);
        control_top = (LinearLayout) findViewById(R.id.control_top);
        video_mode_hd = (TextView) findViewById(R.id.video_mode_hd);
        video_mode_sd = (TextView) findViewById(R.id.video_mode_sd);
        video_mode_ld = (TextView) findViewById(R.id.video_mode_ld);

        vLineHD = findViewById(R.id.v_line_hd);
        choose_video_format = (Button) findViewById(R.id.choose_video_format);
        close_voice = (ImageView) findViewById(R.id.close_voice);
        send_voice = (ImageView) findViewById(R.id.send_voice);
        layout_voice_state = (LinearLayout) findViewById(R.id.layout_voice_state);
        iv_half_screen = (ImageView) findViewById(R.id.iv_half_screen);
        hungup = (ImageView) findViewById(R.id.hungup);
        screenshot = (ImageView) findViewById(R.id.screenshot);
        defence_state = (ImageView) findViewById(R.id.defence_state);
        open_door = (ImageView) findViewById(R.id.open_door);
        tv_name = (TextView) findViewById(R.id.tv_name);
        r_p2pview = (RelativeLayout) findViewById(R.id.r_p2pview);
        preSetLocationLayout = new PreSetLocationLayout(mContext);
        r_p2pview.addView(preSetLocationLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) preSetLocationLayout
                .getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(0, Utils.dip2px(mContext, 15), 0, 0);
        preSetLocationLayout.setLayoutParams(params);
        preSetLocationLayout.setVisibility(View.GONE);
        preSetLocationLayout.setPreSetLocationListener(preSetLocationListener);
        voice_state = (ImageView) findViewById(R.id.voice_state);

        viewPager = (ViewPager) findViewById(R.id.monitor_control_viewPager);
        iv_point_one = (ImageView) findViewById(R.id.iv_point_one);
        iv_point_two = (ImageView) findViewById(R.id.iv_point_two);
        iv_point_three = (ImageView) findViewById(R.id.iv_point_three);
        l_zoom = (RelativeLayout) findViewById(R.id.l_zoom);

        rlLampControl = (RelativeLayout) findViewById(R.id.rl_lamp);
        rlLampControl.setVisibility(View.GONE);
        iv_light = (ImageView) findViewById(R.id.iv_light);
        progressBarLamp = (ProgressBar) findViewById(R.id.progressBar_lamp);
        l_zoom.setVisibility(View.GONE);
        l_f = (LinearLayout) findViewById(R.id.l_f);
        iv_opendor = (ImageView) findViewById(R.id.iv_opendor);
        if (mContact.isSmartHomeContatct()) {
            defence_state.setVisibility(View.GONE);
        } else {
            defence_state.setVisibility(View.VISIBLE);
        }
        viewPager.setOnPageChangeListener(this);
        setControlButtomHeight(0);
        frushLayout(mContact.contactType);
        // 刷新监控
        rlPrgTxError = (RelativeLayout) findViewById(R.id.rl_prgError);
        txError = (TextView) findViewById(R.id.tx_monitor_error);
        btnRefrash = (Button) findViewById(R.id.btn_refrash);
        progressBar = (ProgressBar) findViewById(R.id.prg_monitor);
        tx_wait_for_connect = (TextView) findViewById(R.id.tx_wait_for_connect);
        ivHeader = (HeaderView) findViewById(R.id.hv_header);

        zoomAdd = (ImageView) findViewById(R.id.zoom_add);
        zoomReduce = (ImageView) findViewById(R.id.zoom_reduce);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        rlPrgTxError.setOnClickListener(this);
        btnRefrash.setOnClickListener(this);
        // 更新头像
        setHeaderImage();
        //因控件无法正常获取横竖屏切换状态而提前，挪后会造成UI布局间距有问题
        ShowVideoStateView(VideoStatusView.NO_VIDEO);

        iv_full_screen.setOnClickListener(this);
        iv_voice.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        choose_video_format.setOnClickListener(this);
        close_voice.setOnClickListener(this);
        send_voice.setOnClickListener(this);
        iv_half_screen.setOnClickListener(this);
        hungup.setOnClickListener(this);
        screenshot.setOnClickListener(this);
        video_mode_hd.setOnClickListener(this);
        video_mode_sd.setOnClickListener(this);
        video_mode_ld.setOnClickListener(this);
        defence_state.setOnClickListener(this);
        open_door.setOnClickListener(this);
        rlLampControl.setOnClickListener(this);
        iv_opendor.setOnClickListener(this);
        zoomAdd.setOnClickListener(this);
        zoomReduce.setOnClickListener(this);
        iv_set.setOnClickListener(this);
        tv_name.setText(mContact.getContactName());
        final AnimationDrawable anim = (AnimationDrawable) voice_state
                .getDrawable();
        OnPreDrawListener opdl = new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                anim.start();
                return true;
            }

        };
        voice_state.getViewTreeObserver().addOnPreDrawListener(opdl);
        if (mContact.contactType == P2PValue.DeviceType.NPC) {
            current_video_mode = P2PValue.VideoMode.VIDEO_MODE_LD;
        } else {
            current_video_mode = P2PConnect.getMode();
        }
        if (isSurpportOpenDoor == true
                || deviceType == P2PValue.DeviceType.DOORBELL) {
            open_door.setVisibility(ImageView.VISIBLE);
            iv_opendor.setVisibility(ImageView.VISIBLE);
        } else {
            open_door.setVisibility(ImageView.GONE);
            iv_opendor.setVisibility(ImageView.INVISIBLE);
        }
        updateVideoModeText(current_video_mode);
        if (mContact.contactType != P2PValue.DeviceType.DOORBELL
                && !isSurpportOpenDoor) {
            send_voice.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent event) {
                    // TODO Auto-generated method stub
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            BaiduTjUtils.onEventStart(mContext, BaiduTjUtils.TJ_INTERCOM, "Intercom horizontal screen");
                            hideVideoFormat();
                            layout_voice_state
                                    .setVisibility(RelativeLayout.VISIBLE);

                            send_voice
                                    .setBackgroundResource(R.drawable.ic_send_audio_p);
                            setMute(false);
                            isSpeak = true;
                            return true;
                        case MotionEvent.ACTION_UP:
                            BaiduTjUtils.onEventEnd(mContext, BaiduTjUtils.TJ_INTERCOM, "Intercom horizontal screen");
                            layout_voice_state.setVisibility(RelativeLayout.GONE);
                            send_voice
                                    .setBackgroundResource(R.drawable.ic_send_audio);
                            setMute(true);
                            isSpeak = false;
                            return true;
                    }
                    return false;
                }
            });
        } else if (mContact.contactType == P2PValue.DeviceType.DOORBELL
                && !isSurpportOpenDoor) {
            // iv_speak.setOnComfirmClickListener(this);
            send_voice.setOnClickListener(this);
        } else if (isSurpportOpenDoor) {
            // iv_speak.setOnComfirmClickListener(this);
            // 开始监控时没有声音，暂时这样
            send_voice.setOnClickListener(this);
            // iv_speak.performClick();
            // iv_speak.performClick();
            // speak();
            // speak();
            // send_voice.performClick();
            // speak();
        }
        mOrientationEventListener = new OrientationEventListener(mContext) {

            @Override
            public void onOrientationChanged(int rotation) {
                // TODO Auto-generated method stub
                // 设置竖屏(全景陀螺仪打开时不能自动旋转)
                if (isSpeak || bSensorTracker) {
                    return;
                }
                // 设置横屏
                if (((rotation >= 0) && (rotation <= 30)) || (rotation >= 330)) {
                    if (mIsLand) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        mIsLand = false;
                        setHalfScreen(true);
                    }
                } else if (((rotation >= 230) && (rotation <= 310))) {
                    if (!mIsLand) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        mIsLand = true;
                        setHalfScreen(false);
                    }
                }
            }
        };
        if (mContact.contactType == P2PValue.DeviceType.NPC) {
            Heigh = screenWidth * 3 / 4;
            setIsLand(true);
            setPanorama(false);
        } else if (mContact.isPanorama()) {
            P2PView.type = 1;
            P2PView.scale = 2;
            Heigh = screenWidth;
            setIsLand(false);
            setPanorama(true);
        } else {
            Heigh = screenWidth * 9 / 16;
            setIsLand(false);
            setPanorama(false);
        }
        LayoutParams parames = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        parames.height = Heigh;
        r_p2pview.setLayoutParams(parames);
        mOneFrag = new MonitorOneFragment();
        mTwoFrag = new MonitorTwoFragment();
        mThreeFrag = new MonitorThreeFrag();
        Bundle bundle = new Bundle();
        bundle.putString("contactid", mContact.contactId);
        bundle.putString("contactpwd", mContact.getPassword());
        mThreeFrag.setArguments(bundle);
        fragments.add(mOneFrag);
        // fragments.add(mTwoFrag);
        fragments.add(mThreeFrag);
        fragmentManager = getSupportFragmentManager();
        MainFragmentAdapter adpter = new MainFragmentAdapter(fragmentManager,
                fragments);
        viewPager.setAdapter(adpter);
        seebar_zoom = (VerticalSeekBar) findViewById(R.id.seebar_zoom);
        seebar_zoom.setProgress(10);
        seebar_zoom.setSeeckListen(new OnVerticalSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {
                // TODO Auto-generated method stub
                int progress = seekBar.getProgress();
                long last_time = SharedPreferencesManager.getInstance()
                        .getFocusZoomTime(mContext);
                long current_time = System.currentTimeMillis();
                if (current_time - last_time > 4000) {
                    P2PHandler.getInstance().setFocusZoom(mContact.getIpContactId(), mContact.getPassword(), progress);
                    currentPosition = progress;
                    seekBar.setProgress(progress);
                    SharedPreferencesManager.getInstance().putFocusZoomTime(
                            mContext, current_time);
                } else {
                    seekBar.setProgress(currentPosition);
                }
            }
        });
        mSensorTracker = new SensorTracker(this);
        mSensorTracker.registerListener(new SensorTrackerListener() {

            @Override
            public void onOrientationChange(Quaternion orientation) {
                MediaPlayer.setTrackerQuat(orientation.getX(), orientation.getY(), orientation.getZ(), orientation.getW());
            }
        });
        // AP模式不支持观看人数与清晰度切换
        if (connectType == CallActivity.P2PCONECT) {
            MOnitorNumber = new MonitorNumberTextView(mContext);
            MOnitorNumber.setVisibility(View.INVISIBLE);
            r_p2pview.addView(MOnitorNumber);
        }
        initDisplay();
        setZoomMargin(false);
    }

    public void initSpeark(int deviceType, boolean isOpenDor) {
        if (mContact.isSmartHomeContatct()) {
            defence_state.setVisibility(View.GONE);
        } else {
            defence_state.setVisibility(View.VISIBLE);
        }
        if (isOpenDor|| deviceType == P2PValue.DeviceType.DOORBELL) {
            open_door.setVisibility(View.VISIBLE);
            iv_opendor.setVisibility(ImageView.VISIBLE);
        } else {
            open_door.setVisibility(View.GONE);
            iv_opendor.setVisibility(ImageView.INVISIBLE);
        }
        if (deviceType != P2PValue.DeviceType.DOORBELL && !isOpenDor) {
            send_voice.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent event) {
                    // TODO Auto-generated method stub
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            hideVideoFormat();
                            layout_voice_state
                                    .setVisibility(RelativeLayout.VISIBLE);

                            send_voice
                                    .setBackgroundResource(R.drawable.ic_send_audio_p);
                            setMute(false);
                            return true;
                        case MotionEvent.ACTION_UP:
                            layout_voice_state.setVisibility(RelativeLayout.GONE);
                            send_voice
                                    .setBackgroundResource(R.drawable.ic_send_audio);
                            setMute(true);
                            return true;
                    }
                    return false;
                }
            });
            layout_voice_state.setVisibility(RelativeLayout.GONE);
            send_voice
                    .setBackgroundResource(R.drawable.ic_send_audio);
            setMute(true);
        } else if (deviceType == P2PValue.DeviceType.DOORBELL && !isOpenDor) {
            send_voice.setOnTouchListener(null);
            send_voice.setOnClickListener(this);
        } else if (isOpenDor) {
            send_voice.setOnTouchListener(null);
            changeControl(true);
            // 开始监控时没有声音，暂时这样
            send_voice.setOnClickListener(this);
            control_bottom.setVisibility(View.VISIBLE);
        }
        isSurpportOpenDoor=isOpenDor;
        Intent new_monitor = new Intent();
        new_monitor.setAction(Constants.Action.NEW_MONITOR);
        new_monitor.putExtra("deviceType", deviceType);
        new_monitor.putExtra("isOpenDor", isOpenDor);
        new_monitor.putExtra("subType", subType);
        sendBroadcast(new_monitor);
    }

    private void setHeaderImage() {
        ivHeader.updateImage(mContact.contactId, true, 1);
    }

    /**
     * 刷新IPC和NPC布局异同
     */
    private void frushLayout(int contactType) {
        if (contactType == P2PValue.DeviceType.IPC) {
            video_mode_hd.setVisibility(View.VISIBLE);
            vLineHD.setVisibility(View.VISIBLE);
        } else if (contactType == P2PValue.DeviceType.NPC) {
            video_mode_hd.setVisibility(View.GONE);
            vLineHD.setVisibility(View.GONE);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.P2P_ACCEPT);
        filter.addAction(Constants.P2P.P2P_READY);
        filter.addAction(Constants.P2P.P2P_REJECT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Constants.P2P.ACK_RET_CHECK_PASSWORD);
        filter.addAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
        filter.addAction(Constants.P2P.RET_SET_REMOTE_DEFENCE);
        filter.addAction(Constants.P2P.P2P_RESOLUTION_CHANGE);
        filter.addAction(Constants.P2P.DELETE_BINDALARM_ID);
        filter.addAction(Constants.Action.MONITOR_NEWDEVICEALARMING);
        filter.addAction(Constants.P2P.RET_P2PDISPLAY);
        filter.addAction(Constants.P2P.ACK_GET_REMOTE_DEFENCE);
        filter.addAction(Constants.P2P.RET_GET_FOCUS_ZOOM);
        filter.addAction(Constants.P2P.RET_GET_FOCUS_ZOOM_POSITION);
        filter.addAction(Constants.P2P.RET_SET_FOCUS_ZOOM_POSITION);
        filter.addAction(Constants.Action.MONITOR_ALARM_DOOR_PUSH);
        filter.addAction(Constants.P2P.ACK_OPEN_DOOR);
        // 灯
        filter.addAction(Constants.P2P.SET_LAMP_STATUS);
        filter.addAction(Constants.P2P.GET_LAMP_STATUS);
        filter.addAction(Constants.P2P.ACK_SET_LAMP_STATUS);
        filter.addAction(Constants.P2P.RET_GET_PRESETMOTOROS);
        filter.addAction(Constants.P2P.RET_KEEP_CLIENT);
        filter.addAction(Constants.P2P.RET_SET_PRESETMOTOROS);
        filter.addAction(Constants.P2P.P2P_MONITOR_NUMBER_CHANGE);
        filter.addAction(Constants.P2P.GET_PREPOINT_SURPPORTE);
        filter.addAction(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT);
        filter.addAction(Constants.P2P.RET_TF_VIDEO_STATE);
        filter.addAction(Constants.P2P.RET_GET_SD_CARD_FORMAT);
        filter.addAction(Constants.P2P.ACK_GET_SD_CARD_FORMAT);
//        filter.addAction(Constants.P2P.RET_GET_DEVICE_TYPE);
        mContext.registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.P2P.P2P_READY)) {
                userCanClick = true;
                if (isSupportPrepoint) {
                    Utils.setPrePoints(mContact.getIpContactId(), mContact.getPassword(), 2, 0);
                }
                P2PHandler.getInstance().getDefenceStates(mContact.getIpContactId(), mContact.getPassword());
                P2PHandler.getInstance().getFocusZoom(mContact.getIpContactId(), mContact.getPassword());
                isReceveHeader = false;
                P2PConnect.setMonitorId(mContact.contactId);
                SettingListener.setMonitorID(mContact.contactId);
                mOrientationEventListener.enable();
                if (isSpeak && ScrrenOrientation == Configuration.ORIENTATION_LANDSCAPE) {// 对讲过程中不可消失
                    // 设置control_bottom的高度
                    int height = (int) getResources().getDimension(R.dimen.p2p_monitor_bar_height);
                    setControlButtomHeight(height);
                    control_bottom.setVisibility(View.VISIBLE);
                    control_bottom.bringToFront();
                    return;
                }
                pView.sendStartBrod();
                if(isSurpportOpenDoor){
                    isSpeak = false;
                    speak();
                }
            } else if (intent.getAction().equals(Constants.P2P.P2P_ACCEPT)) {
                int[] type = intent.getIntArrayExtra("type");
                if (mContact.isPanorama()) {
                    P2PView.type = 1;
                    P2PView.scale = 2;
                    Heigh = screenWidth;
                    setIsLand(false);
                    setPanorama(true);
                } else {
                    P2PView.type = type[0];
                    P2PView.scale = type[1];
                    if (P2PView.type == 1) {
                        if (P2PView.scale == 0) {
                            Heigh = screenWidth * 3 / 4;
                            setIsLand(true);
                        } else {
                            Heigh = screenWidth * 9 / 16;
                            setIsLand(false);
                        }
                    } else {
                        if (mContact.contactType == P2PValue.DeviceType.NPC) {
                            Heigh = screenWidth * 3 / 4;
                            setIsLand(true);
                        } else {
                            Heigh = screenWidth * 9 / 16;
                            setIsLand(false);
                        }
                    }
                    setPanorama(false);
                }
                if (ScrrenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    LayoutParams parames = new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    parames.height = Heigh;
                    r_p2pview.setLayoutParams(parames);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_CHECK_PASSWORD)) {
                int result = intent.getIntExtra("result", -1);
                String deviceId=intent.getStringExtra("deviceId");
                if(!deviceId.equals(mContact.contactId)&&!deviceId.equals(mContact.getIpMark())){
                    return;
                }
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
                    if(checkpwdLoading!=null&&checkpwdLoading.isShowing()){
                        checkpwdLoading.dismiss();
                        Intent control = new Intent();
                        control.setClass(mContext, MainControlActivity.class);
                        control.putExtra("contact", mContact);
                        control.putExtra("type", mContact.contactType);
                        control.putExtra("connectType",connectType);
                        control.putExtra("isMonitor", true);
                        mContext.startActivity(control);
                        reject();
                    }
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    if(checkpwdLoading!=null&&checkpwdLoading.isShowing()){
                        checkpwdLoading.dismiss();
                    }
                    if (inputPwdDialog != null && inputPwdDialog.isShowing()) {
                        return;
                    }
                    inputPwdDialog = new InputPasswordDialog(mContext);
                    inputPwdDialog.setInputPasswordClickListener(inputPwdClickListener);
                    inputPwdDialog.setContactId(mContact.contactId);
                    inputPwdDialog.show();
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().checkPassword(mContact.getIpContactId(), mContact.getPassword());
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
                     if(checkpwdLoading!=null&&checkpwdLoading.isShowing()){
                         checkpwdLoading.dismiss();
                         T.showShort(mContext,R.string.insufficient_permissions);
                     }
                }
            } else if (intent.getAction().equals(Constants.P2P.P2P_REJECT)) {
                String error = intent.getStringExtra("error");
                int code = intent.getIntExtra("code", 9);
                showError(error, code);
                if (isCallAnother) {
                    isCallAnother = false;
                } else if (code == 9) {
                    reject();
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_REMOTE_DEFENCE)) {
                String ids = intent.getStringExtra("contactId");
                if (!ids.equals("") && ids.equals(mContact.getIpContactId())) {
                    defenceState = intent.getIntExtra("state", -1);
                    changeDefence(defenceState);
                }
                if (!mContact.isSmartHomeContatct()) {
                    defence_state.setVisibility(ImageView.VISIBLE);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_SET_REMOTE_DEFENCE)) {
                int result = intent.getIntExtra("state", -1);
                if (result == 0) {
                    if (defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
                        defenceState = Constants.DefenceState.DEFENCE_STATE_OFF;
                    } else {
                        defenceState = Constants.DefenceState.DEFENCE_STATE_ON;
                    }
                    changeDefence(defenceState);
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                String error = intent.getStringExtra("error");
                showError(error, -1);
            } else if (intent.getAction().equals(
                    Constants.Action.MONITOR_NEWDEVICEALARMING)) {
                // 弹窗
                int MessageType = intent.getIntExtra("messagetype", 2);
                int type = intent.getIntExtra("alarm_type", 0);
                pushAlarmType = type;
                disconnectDooranerfa();
                isCustomCmdAlarm = intent.getBooleanExtra("isCustomCmdAlarm",
                        false);
                int group = intent.getIntExtra("group", -1);
                int item = intent.getIntExtra("item", -1);
                boolean isSupport = intent.getBooleanExtra("isSupport", false);
                boolean isSupportDelete = intent.getBooleanExtra(
                        "isSupportDelete", false);
                subType = intent.getIntExtra("subType", -1);
                mainType = intent.getIntExtra("mainType", -1);
                String customMsg = intent.getStringExtra("customMsg");
                if (MessageType == 1) {
                    // 报警推送
                    NewMessageDeviceID = intent.getStringExtra("alarm_id");
                    if (NewMessageDeviceID.equals(mContact.contactId)) {
                        if (type != P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
                            showAlarmCloseVoice();
                        }
                        return;
                    }
                    if (alarm_id.equals(NewMessageDeviceID)
                            && passworddialog != null
                            && passworddialog.isShowing()) {
                        return;
                    } else {
                        alarm_id = NewMessageDeviceID;
                    }
                } else {
                    // 透传推送
                    NewMessageDeviceID = intent.getStringExtra("contactId");
                    Log.i("dxsmoniter_alarm", "透传推送" + NewMessageDeviceID);
                }
                String alarmtype = Utils.getAlarmType(NewMessageDeviceID, type,
                        isSupport, group, item, customMsg);
                StringBuffer NewMassage = new StringBuffer(
                        Utils.getStringByResouceID(R.string.tab_device))
                        .append("：").append(
                                Utils.getDeviceName(NewMessageDeviceID));
                NewMassage.append("\n").append(Utils.getStringByResouceID(R.string.allarm_type)).
                        append(alarmtype);
                int alarmstate = NORMAL;
                if (type == P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
                    alarmstate = CLOSE;
                }
                acl = new AlarmCloseVoice(mContext, NewMessageDeviceID);
                acl.settype(KEEP_OTHER_CLIENT);
                acl.setState(alarmstate);
                acl.setcloseClickListener(clistener);
                NewMessageDialog(NewMassage.toString(), NewMessageDeviceID,
                        isSupportDelete, acl);
            } else if (intent.getAction().equals(Constants.P2P.RET_P2PDISPLAY)) {
                Log.e("monitor", "RET_P2PDISPLAY");
                connectSenconde = true;
                if (!isReceveHeader) {
                    hindRlProTxError();
                    initTimeTextView();
                    initDisplay();
                    pView.updateScreenOrientation();
                    // iv_full_screen.setVisibility(View.VISIBLE);
                    isReceveHeader = true;
                    showDisplay();
                }
                showGuideLayout(0);
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
            } else if (intent.getAction().equals(Constants.P2P.ACK_GET_REMOTE_DEFENCE)) {
                String contactId = intent.getStringExtra("contactId");
                int result = intent.getIntExtra("result", -1);
                if (contactId.equals(mContact.getIpContactId())) {
                    if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
                        isPermission = false;
                    } else {
                        isPermission = true;
                    }
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_GET_FOCUS_ZOOM)) {
//              弃用
//				int result=intent.getIntExtra("result", -1);
//				String deviceId=intent.getStringExtra("deviceId");
//				if(deviceId!=null&&deviceId.equals(callId)){
//					if(result==Constants.CHANGE_FOCUS_ZOOM.CHANGE_FOCUS_ZOOM){
//						l_zoom.setVisibility(RelativeLayout.VISIBLE);
//					}else{
//						l_zoom.setVisibility(RelativeLayout.GONE);
//					}
//				}
            } else if (intent.getAction().equals(Constants.P2P.RET_GET_FOCUS_ZOOM_POSITION)) {
                String deviceId = intent.getStringExtra("deviceId");
                int result = intent.getIntExtra("result", -1);
                int value = intent.getIntExtra("value", -1);
                if (deviceId.equals(mContact.getIpContactId())) {
                    if (result == 0) {
                        isFoucusZoom = true;
                        P2PView.SUPPORT_ZOOM_FOCUS = true;
                        if (value >= 0 && value <= 10) {
                            currentPosition = value;
                            seebar_zoom.setProgress(value);
                            if (ScrrenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                                l_zoom.setVisibility(View.VISIBLE);
                            } else {
                                if (control_bottom.getVisibility() == View.VISIBLE) {
                                    l_zoom.setVisibility(View.VISIBLE);
                                } else {
                                    l_zoom.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_SET_FOCUS_ZOOM_POSITION)) {
                String deviceId = intent.getStringExtra("deviceId");
                int result = intent.getIntExtra("result", -1);
                int value = intent.getIntExtra("value", -1);
                if (deviceId.equals(mContact.getIpContactId())) {
                    if (result == 0) {
                        currentPosition = value;
                        seebar_zoom.setProgress(value);
                    }
                }
            } else if (intent.getAction().equals(
                    Constants.Action.MONITOR_ALARM_DOOR_PUSH)) {
                open_door.setVisibility(View.VISIBLE);
                if (dialog_visit != null && dialog_visit.isShowing()) {
                    return;
                }
                if (dialog_visit == null) {
                    dialog_visit = new NormalDialog(mContext);
                }
                dialog_visit.showVisit();
            } else if (intent.getAction().equals(Constants.P2P.ACK_OPEN_DOOR)) {
                int state = intent.getIntExtra("state", -1);
                if (state == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {

                } else if (state == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    T.showShort(mContext, R.string.password_error);
                } else if (state == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    T.showShort(mContext, R.string.net_error);
                } else if (state == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
                    T.showShort(mContext, R.string.insufficient_permissions);
                }
            } else if (intent.getAction().equals(Constants.P2P.SET_LAMP_STATUS)) {
                int result = intent.getIntExtra("result", -1);
                String deviceId = intent.getStringExtra("deviceId");
                if (!deviceId.equals(mContact.getIpContactId())) {
                    return;
                }
                Log.i("dxslamp", "result1-->" + result);
                if (result == 0) {
                    if (cur_modify_lamp_state == 0) {
                        lamp_switch = 0;
                        // txLamp.setText("开灯");
                        iv_light.setBackgroundResource(R.drawable.lighton);
                    } else {
                        lamp_switch = 1;
                        // txLamp.setText("关灯");
                        iv_light.setBackgroundResource(R.drawable.lightoff);
                    }
                    showLampState();
                    T.showShort(mContext, R.string.modify_success);
                } else {
                    showLampState();
                    T.showShort(mContext, R.string.operator_error);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_SET_LAMP_STATUS)) {
                int result = intent.getIntExtra("result", -1);
                Log.i("dxslamp", "result2-->" + result);
            } else if (intent.getAction().equals(Constants.P2P.GET_LAMP_STATUS)) {
                int result = intent.getIntExtra("result", -1);
                String deviceId = intent.getStringExtra("deviceId");
                if (!deviceId.equals(mContact.getIpContactId())) {
                    return;
                }
                if (result != -1) {
                    rlLampControl.setVisibility(View.VISIBLE);
                } else {
                    rlLampControl.setVisibility(View.GONE);
                }
                Log.i("dxslamp", "result3-->" + result);
                if (result == 1) {
                    lamp_switch = 1;
                    // txLamp.setText("关灯");
                    iv_light.setBackgroundResource(R.drawable.lightoff);
                } else {
                    lamp_switch = 0;
                    // txLamp.setText("开灯");
                    iv_light.setBackgroundResource(R.drawable.lighton);
                }
                showLampState();
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_PRESETMOTOROS)) {
                byte[] result = intent.getByteArrayExtra("result");
                if (result[1] == 0) {// 设置成功,不会出现
                    PreFunctionMode = 0;
                    PrePointInfo = result[3];
                } else if (result[1] == 1) {// 获取预置位成功
                    PreFunctionMode = 1;
                    PrePointInfo = result[3];
                    setLocation(Utils.getByteBinnery(PrePointInfo, true));
                    if (isSupportPrepoint && ScrrenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        preSetLocationLayout.setVisibility(View.VISIBLE);
                    }
                    // LbtnToSeePrepoint.setVisibility(View.VISIBLE);
                } else if (result[1] == 84) {// 无此选项
                    PreFunctionMode = 84;
                } else if (result[1] == 254) {// 参数错误
                    PreFunctionMode = 254;
                } else {// 不支持
                    PreFunctionMode = -1;
                }
                Log.e("RememberPoins", "PrePointInfo=" + PrePointInfo
                        + "PreFunctionMode=" + PreFunctionMode);
            } else if (intent.getAction().equals(Constants.P2P.RET_KEEP_CLIENT)) {
                int iSrcID = intent.getIntExtra("iSrcID", -1);
                byte boption = intent.getByteExtra("boption", (byte) -1);
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    if (mContact.getIpContactId().equals(String.valueOf(iSrcID))) {
                        // if(alarmClose!=null){
                        // alarmClose.setState(2);
                        // alarmClose.startLoading();
                        // }
                        hideAlarmCloseVoice();
                    } else {
                        if (acl != null) {
                            acl.setState(2);
                            acl.startLoading();
                        }

                    }
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_SET_PRESETMOTOROS)) {
                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
                }
                byte[] result = intent.getByteArrayExtra("result");
                if (result[1] == 0) {
                    // 执行截图
                    if (mContext instanceof BaseMonitorActivity) {
                        ScreenShot(result[3]);
                    }
                }
                Log.e("dxsprepoint", "SET-->" + Arrays.toString(result));
            } else if (intent.getAction().equals(
                    Constants.P2P.P2P_MONITOR_NUMBER_CHANGE)) {
                //当前观看人数
                int number = intent.getIntExtra("number", -1);
                //TF卡和录像状态信息
                int isVideo = intent.getIntExtra("isVideo", -1);//是否在录像  0 否 1 是
                int isHaveTF = intent.getIntExtra("isHaveTF", -1);//是否有TF卡 0 没有 1 有
                int videoType = intent.getIntExtra("videoType", -1);//录像状态 0 手动录像 1报警录像 2定时录像
                int isVideoSuccess = intent.getIntExtra("isVideoSuccess", -1);//录像是否成功 0 成功 1失败
                if (number != -1) {
                    if (MOnitorNumber != null) {
                        MOnitorNumber.showNumbers(mContext.getResources().getString(
                                R.string.monitor_number)
                                + " " + P2PConnect.getNumber());
                    }
                }
                //                    WXY 注释此部分  13版暂不发布TF卡异常提示功能
//                if (isVideo != -1 && isHaveTF != -1 && videoType != -1 && isVideoSuccess != -1) {
//                    if (isHaveTF == 1) {
//                        if (isVideoSuccess == 1) {
//                            ShowVideoStateView(VideoStatusView.NO_TF);
//                        }else{
//                            ShowVideoStateView(VideoStatusView.NO_VIDEO);
//                        }
//                    }else {
//                        ShowVideoStateView(VideoStatusView.NO_VIDEO);
//                    }
//                }
            } else if (intent.getAction().equals(Constants.P2P.GET_PREPOINT_SURPPORTE)) {
                String deviceID = intent.getStringExtra("deviceId");
                int result = intent.getIntExtra("result", 0);
                if (deviceID.equals(mContact.getIpContactId())) {
                    if (result == Constants.SurportPrepoint.NO) {
                        PreFunctionMode = -1;
                        isSupportPrepoint = false;
                    } else {
                        PreFunctionMode = -2;
                        isSupportPrepoint = true;
                        Utils.setPrePoints(mContact.getIpContactId(), mContact.getPassword(), 2, 0);
                    }
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_CUSTOM_CMD_DISCONNECT)) {
                String contactId = intent.getStringExtra("contactId");
                if (NewMessageDeviceID.equals(contactId) && pushAlarmType == P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_TF_VIDEO_STATE)) {
                //TF卡和录像状态信息
                int isVideo = intent.getIntExtra("isVideo", -1);//是否在录像  0 否 1 是
                int isHaveTF = intent.getIntExtra("isHaveTF", -1);//是否有TF卡 0 没有 1 有
                int videoType = intent.getIntExtra("videoType", -1);//录像状态 0 手动录像 1报警录像 2定时录像
                int isVideoSuccess = intent.getIntExtra("isVideoSuccess", -1);//录像是否成功 0 成功 1失败
                //                    WXY 注释此部分  13版暂不发布TF卡异常提示功能
//                if (isVideo != -1 && isHaveTF != -1 && videoType != -1 && isVideoSuccess != -1) {
//                    if (isHaveTF == 1) {
//                        if (isVideoSuccess == 1) {
//                            ShowVideoStateView(VideoStatusView.NO_TF);
//                        } else {
//                            ShowVideoStateView(VideoStatusView.NO_VIDEO);
//                        }
//                    } else {
//                        ShowVideoStateView(VideoStatusView.NO_VIDEO);
//                    }
//                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_SD_CARD_FORMAT)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.SD_FORMAT.SD_CARD_SUCCESS) {
                    ShowVideoStateView(VideoStatusView.NO_VIDEO);
                    T.showShort(mContext, R.string.sd_format_success);
                } else if (result == Constants.P2P_SET.SD_FORMAT.SD_CARD_FAIL) {
                    T.showShort(mContext, R.string.sd_format_fail);
                } else if (result == Constants.P2P_SET.SD_FORMAT.SD_NO_EXIST) {
                    T.showShort(mContext, R.string.sd_no_exist);
                } else if (result == Constants.P2P_SET.SD_FORMAT.BEING_VIDEO) {
                    T.showShort(mContext, R.string.being_video);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_GET_SD_CARD_FORMAT)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    T.showShort(mContext, R.string.password_error);
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().setSdFormat(mContact.getIpContactId(),
                            mContact.getPassword(), 16);
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS){
                    T.showShort(mContext, R.string.insufficient_permissions);
                }
            }else if(intent.getAction().equals(Constants.P2P.RET_GET_DEVICE_TYPE)){
                String deviceId=intent.getStringExtra("deviceId");
                int mainType=intent.getIntExtra("mainType",-1);
                int subType=intent.getIntExtra("subType",-1);
                if(deviceId.equals(mContact.contactId)||mContact.equals(mContact.getIpMark())){
                    if(mainType==P2PValue.DeviceType.DOORBELL){
                        open_door.setVisibility(ImageView.VISIBLE);
                        iv_opendor.setVisibility(ImageView.VISIBLE);
                    }else {
                        open_door.setVisibility(ImageView.GONE);
                        iv_opendor.setVisibility(ImageView.GONE);
                    }
                }

            }
        }
    };

    // 灯光控制
    public void showLampState() {
        progressBarLamp.setVisibility(RelativeLayout.GONE);
        // txLamp.setVisibility(RelativeLayout.VISIBLE);
        iv_light.setVisibility(ImageView.VISIBLE);
        rlLampControl.setEnabled(true);
    }

    // 灯光控制
    public void showProgress_lamp() {
        progressBarLamp.setVisibility(RelativeLayout.VISIBLE);
        // txLamp.setVisibility(RelativeLayout.GONE);
        iv_light.setVisibility(ImageView.INVISIBLE);
        rlLampControl.setEnabled(false);
    }

    public void changeDefence(int defencestate) {
        if (defencestate == Constants.DefenceState.DEFENCE_STATE_ON) {
            defence_state.setImageResource(R.drawable.deployment);
            if (PanFunction != null) {
                PanFunction.setItemImage(0, new int[]{
                        R.drawable.monitor_l_pan_defenon,
                        R.drawable.monitor_l_pan_defenon_p});
            }
        } else {
            defence_state.setImageResource(R.drawable.disarm);
            if (PanFunction != null) {
                PanFunction.setItemImage(0, new int[]{
                        R.drawable.monitor_l_pan_defenoff,
                        R.drawable.monitor_l_pan_defenoff_p});
            }
        }
        Intent i = new Intent();
        i.setAction(Constants.Action.CHANGE_REMOTE_DEFENCE);
        i.putExtra("defencestate", defencestate);
        sendBroadcast(i);
    }

    /**
     * 隐藏过度页
     */
    private void hindRlProTxError() {
        // ObjectAnimator anima = ObjectAnimator.ofFloat(rlPrgTxError, "alpha",
        // 1.0f, 0f);
        rlPrgTxError.setVisibility(View.GONE);
        // anima.setDuration(500).start();
        // anima.addListener(new AnimatorListener() {
        //
        // @Override
        // public void onAnimationStart(Animator animation) {
        // }
        //
        // @Override
        // public void onAnimationRepeat(Animator animation) {
        // // TODO Auto-generated method stub
        // }
        //
        // @Override
        // public void onAnimationEnd(Animator animation) {
        // rlPrgTxError.setVisibility(View.GONE);
        // }
        //
        // @Override
        // public void onAnimationCancel(Animator animation) {
        // }
        // });
    }

    private void showRlProTxError() {
        rlPrgTxError.setVisibility(View.VISIBLE);
    }

    // 设置布防
    public void setDefence() {
        if (!isPermission) {
            T.showShort(mContext, R.string.insufficient_permissions);
            return;
        }
        if (defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
            P2PHandler.getInstance().setRemoteDefence(mContact.getIpContactId(), mContact.getPassword(),
                    Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
        } else if (defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
            P2PHandler.getInstance().setRemoteDefence(mContact.getIpContactId(), mContact.getPassword(),
                    Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
        }
    }

    public void callDevice() {
        P2PConnect.setCurrent_state(P2PConnect.P2P_STATE_CALLING);
        P2PConnect.setCurrent_call_id(mContact.contactId);
        String push_mesg = NpcCommon.mThreeNum
                + ":"
                + mContext.getResources()
                .getString(R.string.p2p_call_push_mesg);
        if (connectType == Constants.ConnectType.RTSPCONNECT) {
            callType = 3;
            String ipAddress = "";
            String ipFlag = "";
            if (mContact.ipadressAddress != null) {
                ipAddress = mContact.ipadressAddress.getHostAddress();
                ipFlag = ipAddress.substring(ipAddress.lastIndexOf(".") + 1,
                        ipAddress.length());
            } else {

            }
            P2PHandler.getInstance().call(NpcCommon.mThreeNum, "0", true,
                    Constants.P2P_TYPE.P2P_TYPE_MONITOR, "1", "1", push_mesg,
                    AppConfig.VideoMode, mContact.contactId,
                    mContact.subType, mContact.videow, mContact.videoh,
                    mContact.fishPos);
            // P2PHandler.getInstance().RTSPConnect(NpcCommon.mThreeNum,
            // mContact.contactPassword, true, callType, mContact.contactId,
            // ipFlag, push_mesg, ipAddress,AppConfig.VideoMode,rtspHandler);
        } else if (connectType == Constants.ConnectType.P2PCONNECT) {
            callType = 1;
            String ipAdress = FList.getInstance().getCompleteIPAddress(
                    mContact.contactId);
            P2PHandler.getInstance().call(NpcCommon.mThreeNum, mContact.getPassword(), true,
                    Constants.P2P_TYPE.P2P_TYPE_MONITOR, mContact.getIpContactId(), ipAdress,
                    push_mesg, AppConfig.VideoMode, mContact.contactId,
                    mContact.subType, mContact.videow, mContact.videoh,
                    mContact.fishPos);
            Log.e("dxsTest", "subType:" + mContact.subType + "videow" + mContact.videow + "fishPos=" + mContact.fishPos);
        }
    }

    Handler rtspHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("dxswifi", "rtsp失败");
                    showError("connect error", 0);
                    P2PHandler.getInstance().reject();
                    break;
                case 1:
                    Log.e("dxswifi", "rtsp成功");
                    rlPrgTxError.setVisibility(View.GONE);
                    P2PConnect.setCurrent_state(2);
                    playReady();
                    mContact.apModeState = Constants.APmodeState.LINK;
                    break;
            }
        }
    };

    private void playReady() {
        // P2PHandler.getInstance().openAudioAndStartPlaying(callType);
        Intent ready = new Intent();
        ready.setAction(Constants.P2P.P2P_READY);
        this.sendBroadcast(ready);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setHalfScreen(false);
            if (isSupportPrepoint && PreFunctionMode != -1 && PreFunctionMode != 84) {
                Utils.setPrePoints(mContact.getIpContactId(), mContact.getPassword(), 2, 0);
            }
            ScrrenOrientation = Configuration.ORIENTATION_LANDSCAPE;
            layout_title.setVisibility(View.GONE);
            l_control.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            rl_control.setVisibility(View.GONE);
            if (!pView.isPanorama()) {
                if (isSupportPrepoint && PreFunctionMode != -1 && PreFunctionMode != 84) {
                    preSetLocationLayout.setVisibility(View.VISIBLE);
                } else {
                    preSetLocationLayout.setVisibility(View.GONE);
                }
            } else {
                preSetLocationLayout.setVisibility(View.GONE);
            }
            setPositionName();
            setZoomMargin(true);
            changeControl(true);
            if (P2PView.type == 1) {
                if (P2PView.scale == 0) {
                    isFullScreen = false;
                    pView.halfScreen();
                } else {
                    isFullScreen = true;
                    pView.fullScreen();
                }
            } else {
                if (mContact.contactType == P2PValue.DeviceType.NPC) {
                    isFullScreen = false;
                    pView.halfScreen();
                } else {
                    isFullScreen = true;
                    pView.fullScreen();
                }
            }
            LayoutParams parames = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            r_p2pview.setLayoutParams(parames);
        } else {
            setHalfScreen(true);
            ScrrenOrientation = Configuration.ORIENTATION_PORTRAIT;
            layout_title.setVisibility(View.VISIBLE);
            l_control.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            rl_control.setVisibility(View.VISIBLE);
            preSetLocationLayout.setVisibility(View.GONE);
            control_top.setVisibility(View.GONE);
            setZoomMargin(false);
            changeControl(false);
            if (isFullScreen) {
                isFullScreen = false;
                pView.halfScreen();
            }
            if (P2PView.type == 1) {
                if (P2PView.scale == 0) {
                    int Heigh = screenWidth * 3 / 4;
                    LayoutParams parames = new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    parames.height = Heigh;
                    r_p2pview.setLayoutParams(parames);
                } else if (P2PView.scale == 1) {
                    int Heigh = screenWidth * 9 / 16;
                    LayoutParams parames = new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    parames.height = Heigh;
                    r_p2pview.setLayoutParams(parames);
                } else {
                    int Heigh = screenWidth;
                    LayoutParams parames = new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    parames.height = Heigh;
                    r_p2pview.setLayoutParams(parames);
                }
            } else {
                if (mContact.contactType == P2PValue.DeviceType.NPC) {
                    int Heigh = screenWidth * 3 / 4;
                    LayoutParams parames = new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    parames.height = Heigh;
                    r_p2pview.setLayoutParams(parames);
                } else {
                    int Heigh = screenWidth * 9 / 16;
                    LayoutParams parames = new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    parames.height = Heigh;
                    r_p2pview.setLayoutParams(parames);
                }
            }
        }
    }

    public void updateVideoModeText(int mode) {
        if (mode == P2PValue.VideoMode.VIDEO_MODE_HD) {
            video_mode_hd.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_blue));
            video_mode_sd.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_white));
            video_mode_ld.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_white));
            choose_video_format.setText(R.string.video_mode_hd);
        } else if (mode == P2PValue.VideoMode.VIDEO_MODE_SD) {
            video_mode_hd.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_white));
            video_mode_sd.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_blue));
            video_mode_ld.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_white));
            choose_video_format.setText(R.string.video_mode_sd);
        } else if (mode == P2PValue.VideoMode.VIDEO_MODE_LD) {
            video_mode_hd.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_white));
            video_mode_sd.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_white));
            video_mode_ld.setTextColor(mContext.getResources().getColor(
                    R.color.text_color_blue));
            choose_video_format.setText(R.string.video_mode_ld);
        }
    }

    @Override
    protected void onP2PViewSingleTap() {
        if (pView.isPanorama()) {
            //全景控制条
            if (ScrrenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                pView.changeVisibly();
            }
            if (PanDisplayControl != null) {
                PanDisplayControl.hindControl();
            }
        } else {
            changeControl();
        }
    }

    private Handler CopyImageHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == DrawTextImageTask.DrawTextHandlerWhat) {
                // 保存全景截图(不判断结果)
                T.showShort(mContext, R.string.capture_success);
                if (TimeTextView != null) {
                    TimeTextView.setDrawingCacheEnabled(false);
                }
            } else {
                if (mThreeFrag != null) {
                    mThreeFrag.addPrepoint(msg.arg1, msg.what);
                }
            }
            return false;
        }
    });

    @Override
    protected void onCaptureScreenResult(boolean isSuccess, int prePoint) {
        // TODO Auto-generated method stub
        if (isSuccess) {
            // Capture success
            if (prePoint == -1) {
                if (pView.isPanorama()) {// 全景贴时间
                    new DrawTextImageTask(CopyImageHandler, TextViewCatch)
                            .execute(Utils.getScreenShotImagePath(
                                    mContact.contactId, 1).get(0));
                } else {
                    T.showShort(mContext, R.string.capture_success);
                }
                List<String> pictrues = Utils.getScreenShotImagePath(
                        mContact.contactId, 1);
                if (pictrues.size() <= 0) {
                    return;
                }
                Utils.saveImgToGallery(pictrues.get(0));
                // 全景截图贴时间
            } else {
                try {
                    new CopyImgaShootTask(CopyImageHandler, prePoint).execute(
                            Utils.getScreenShotImagePath(mContact.contactId, 1)
                                    .get(0), Utils.getPrepointPath(
                                    mContact.contactId, prePoint));
                } catch (Exception e) {
                    if (mThreeFrag != null) {
                        mThreeFrag.addPrepoint(prePoint, 0);
                    }
                }
            }
        } else {
            T.showShort(mContext, R.string.capture_failed);
        }
    }

    @Override
    public int getActivityInfo() {
        // TODO Auto-generated method stub
        return Constants.ActivityInfo.ACTIVITY_APMONITORACTIVITY;
    }

    @Override
    protected void onGoBack() {
//         TODO Auto-generated method stub
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

    @Override
    public void onBackPressed() {
        reject();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    mCurrentVolume, 0);
        }
        if (isRegFilter) {
            mContext.unregisterReceiver(mReceiver);
            isRegFilter = false;
        }
        if (MOnitorNumber != null) {
            MOnitorNumber.releaseTextView();
        }
        P2PView.SUPPORT_ZOOM_FOCUS = false;
        P2PConnect.setPlaying(false);
        P2PConnect.setMonitorId("");// 设置在监控的ID为空
        SettingListener.setMonitorID("");
        if (!activity_stack
                .containsKey(Constants.ActivityInfo.ACTIVITY_MAINACTIVITY)) {
            Intent i = new Intent(this, MainActivity.class);
            this.startActivity(i);
        }
        Intent refreshContans = new Intent();
        refreshContans.setAction(Constants.Action.REFRESH_CONTANTS);
        mContext.sendBroadcast(refreshContans);
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            mCurrentVolume++;
            if (mCurrentVolume > mMaxVolume) {
                mCurrentVolume = mMaxVolume;
            }

            if (mCurrentVolume != 0) {
                mIsCloseVoice = false;
                iv_voice.setImageResource(R.drawable.selector_half_screen_voice_open);
                close_voice.setBackgroundResource(R.drawable.m_voice_on);
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
                iv_voice.setImageResource(R.drawable.selector_half_screen_voice_close);
                close_voice.setBackgroundResource(R.drawable.m_voice_off);
            }

            return false;
        }

        return super.dispatchKeyEvent(event);
    }

    boolean ispa = true;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_full_screen:
                ScrrenOrientation = Configuration.ORIENTATION_LANDSCAPE;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case R.id.defence_state:
                setDefence();
                break;
            case R.id.close_voice:
            case R.id.iv_voice:
                BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_SETVOICESTATE, "Sets mute state horizontal screen");
                setVoiceState();
                break;
            case R.id.screenshot:
                BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_SCREENSHOT, "Screenshots horizontal screen");
                ScreenShot(-1);
                break;
            case R.id.hungup:
            case R.id.back_btn:
                reject();
                break;
            case R.id.choose_video_format:
                changevideoformat();
                break;
            case R.id.iv_half_screen:
//			changeControl(false);
                ScrrenOrientation = Configuration.ORIENTATION_PORTRAIT;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.video_mode_hd:
                if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_HD) {
                    current_video_mode = P2PValue.VideoMode.VIDEO_MODE_HD;
                    P2PHandler.getInstance().setVideoMode(
                            P2PValue.VideoMode.VIDEO_MODE_HD);
                    updateVideoModeText(current_video_mode);
                }
                hideVideoFormat();
                break;
            case R.id.video_mode_sd:
                if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_SD) {
                    current_video_mode = P2PValue.VideoMode.VIDEO_MODE_SD;
                    P2PHandler.getInstance().setVideoMode(
                            P2PValue.VideoMode.VIDEO_MODE_SD);
                    updateVideoModeText(current_video_mode);
                }
                hideVideoFormat();
                break;
            case R.id.video_mode_ld:
                if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_LD) {
                    current_video_mode = P2PValue.VideoMode.VIDEO_MODE_LD;
                    P2PHandler.getInstance().setVideoMode(
                            P2PValue.VideoMode.VIDEO_MODE_LD);
                    updateVideoModeText(current_video_mode);
                }
                hideVideoFormat();
                break;
            case R.id.rl_prgError:
            case R.id.btn_refrash:
                if (btnRefrash.getVisibility() == View.VISIBLE) {
                    hideError();
                    callDevice();
                }
                break;
            case R.id.iv_opendor:
            case R.id.open_door:
                openDor();
                break;
            case R.id.send_voice:
                if (!isSpeak) {
                    speak();
                } else {
                    noSpeak();
                }
                break;
            case R.id.rl_lamp:
                showProgress_lamp();
                if (lamp_switch != 0) {
                    cur_modify_lamp_state = 0;
                    P2PHandler.getInstance().vsetLampStatus(mContact.getIpContactId(), mContact.getPassword(),
                            cur_modify_lamp_state);
                } else {
                    cur_modify_lamp_state = 1;
                    P2PHandler.getInstance().vsetLampStatus(mContact.getIpContactId(), mContact.getPassword(),
                            cur_modify_lamp_state);
                }
                break;
            case R.id.zoom_add:
                myHandler.sendEmptyMessage(1);
                break;
            case R.id.zoom_reduce:
                myHandler.sendEmptyMessage(-1);
                break;
            case R.id.iv_set:
                P2PHandler.getInstance().checkPassword(mContact.getIpContactId(), mContact.getPassword());
                if (checkpwdLoading == null) {
                    checkpwdLoading = new NormalDialog(mContext);
                }
                checkpwdLoading.setTitle(R.string.set);
                checkpwdLoading.showLoadingDialog();
                checkpwdLoading.setTimeOut(10000);
                checkpwdLoading.setCanceledOnTouchOutside(false);
                checkpwdLoading.setOnNormalDialogTimeOutListner(new NormalDialog.OnNormalDialogTimeOutListner() {
                    @Override
                    public void onTimeOut() {
                          T.showShort(MyApplication.app, R.string.time_out);
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 设置静音状态
     */
    public void setVoiceState() {
        if (mIsCloseVoice) {
            mIsCloseVoice = false;
            iv_voice.setImageResource(R.drawable.selector_half_screen_voice_open);
            close_voice.setBackgroundResource(R.drawable.m_voice_on);
            if (PanFunction != null) {
                PanFunction.setItemImage(1, new int[]{
                        R.drawable.monitor_l_pan_voice,
                        R.drawable.monitor_l_pan_voice_p});
            }
            if (mCurrentVolume == 0) {
                mCurrentVolume = 1;
            }
            if (mAudioManager != null) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        mCurrentVolume, 0);
            }
        } else {
            mIsCloseVoice = true;
            iv_voice.setImageResource(R.drawable.selector_half_screen_voice_close);
            close_voice.setBackgroundResource(R.drawable.m_voice_off);
            if (PanFunction != null) {
                PanFunction.setItemImage(1, new int[]{
                        R.drawable.monitor_l_pan_voiceoff,
                        R.drawable.monitor_l_pan_voiceoff_p});
            }
            if (mAudioManager != null) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            }
        }
    }

    // 设置成对话状态
    public void speak() {
        hideVideoFormat();
        layout_voice_state.setVisibility(RelativeLayout.VISIBLE);
        send_voice.setBackgroundResource(R.drawable.ic_send_audio_p);
        setMute(false);
        isSpeak = true;
        layout_voice_state.bringToFront();
    }

    public void noSpeak() {
        send_voice.setBackgroundResource(R.drawable.ic_send_audio);
        layout_voice_state.setVisibility(RelativeLayout.GONE);
        setMute(true);
        isSpeak = false;
    }

    public void stopSpeak() {
        send_voice.setBackgroundResource(R.drawable.ic_send_audio);
        layout_voice_state.setVisibility(RelativeLayout.GONE);
        setMute(true);
        isSpeak = false;
    }

    /**
     * 开门
     */
    private Dialog dialog_open_dor;

    private void openDor() {
        // NormalDialog dialog = new NormalDialog(mContext, mContext
        // .getResources().getString(R.string.open_door), mContexts
        // .getResources().getString(R.string.confirm_open_door), mContext
        // .getResources().getString(R.string.yes), mContext
        // .getResources().getString(R.string.no));
        // dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
        //
        // @Override
        // public void onClick() {
        // if(isCustomCmdAlarm==true){
        // String cmd = "IPC1anerfa:unlock";
        // P2PHandler.getInstance().sendCustomCmd(callId, password, cmd);
        // }else{
        // P2PHandler.getInstance().setGPIO1_0(callId, password);
        // }
        // }
        // });
        // dialog.showDialog();
//  WXY 注释  方便恢复UI  勿删除
//        dialog_open_dor = new MyInputPassDialog(mContext,
//                Utils.getStringByResouceID(R.string.confirm_open_door), "",
//                getResources().getString(R.string.input_admin_password),
//                opendorListener);
//        dialog_open_dor.show();

        final ConfirmOrCancelDialog dialog = new ConfirmOrCancelDialog(mContext);
        dialog.setTitle(R.string.confirm_open_door);
        dialog.setTextYes(mContext.getResources().getString(R.string.confirm));
        dialog.setTextNo(mContext.getResources().getString(R.string.cancel));
        dialog.setOnNoClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnYesClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCustomCmdAlarm == true) {
                    String cmd = "IPC1anerfa:unlock";
                    P2PHandler.getInstance().sendCustomCmd(mContact.getIpContactId(),
                            mContact.getPassword(), cmd);
                } else {
                    P2PHandler.getInstance().setGPIO1_0(mContact.getIpContactId(),
                            mContact.getPassword());
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
//  WXY 注释  方便恢复UI  勿删除
//	private OnCustomDialogListener opendorListener = new OnCustomDialogListener() {
//
//		@Override
//		public void check(String password, String id) {
//			// TODO Auto-generated method stub
//			if (password.trim().equals("")) {
//				T.showShort(mContext, R.string.input_monitor_pwd);
//				return;
//			}
//
//			if (password.length() > 30 || password.charAt(0) == '0') {
//				T.showShort(mContext, R.string.device_password_invalid);
//				return;
//			}
//			String pwd=P2PHandler.getInstance().EntryPassword(password);
//		    if(isCustomCmdAlarm==true){
//			   String cmd = "IPC1anerfa:unlock";
//			   P2PHandler.getInstance().sendCustomCmd(mContact.getIpContactId(), pwd, cmd);
//		    }else{
//			   P2PHandler.getInstance().setGPIO1_0(mContact.getIpContactId(), pwd);
//		    }
//		    dialog_open_dor.dismiss();
//		}
//	};

    Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            switchConnect();
        }

        ;
    };

    /**
     * 展示连接错误
     *
     * @param error
     */
    public void showError(String error, int code) {
        if (!connectSenconde && code != 9) {
            callDevice();
            connectSenconde = true;
            return;
        }
        progressBar.setVisibility(View.GONE);
        tx_wait_for_connect.setVisibility(View.GONE);
        txError.setVisibility(View.VISIBLE);
        btnRefrash.setVisibility(View.VISIBLE);
        txError.setText(error);
        //code为0时代表密码错误
        if (code == 0) {
            if (inputPwdDialog != null && inputPwdDialog.isShowing()) {
                return;
            }
            inputPwdDialog = new InputPasswordDialog(mContext);
            inputPwdDialog.setInputPasswordClickListener(inputPwdClickListener);
            inputPwdDialog.setContactId(mContact.contactId);
            inputPwdDialog.show();
        }
    }

    /**
     * 隐藏连接错误
     */
    private void hideError() {
        progressBar.setVisibility(View.VISIBLE);
        tx_wait_for_connect.setText(getResources().getString(
                R.string.waite_for_linke));
        tx_wait_for_connect.setVisibility(View.VISIBLE);
        txError.setVisibility(View.GONE);
        btnRefrash.setVisibility(View.GONE);
    }

    /**
     * 切换连接
     */
    private void switchConnect() {
        progressBar.setVisibility(View.VISIBLE);
        tx_wait_for_connect.setText(getResources().getString(
                R.string.switch_connect));
        tx_wait_for_connect.setVisibility(View.VISIBLE);
        txError.setVisibility(View.GONE);
        btnRefrash.setVisibility(View.GONE);
        // iv_full_screen.setVisibility(View.INVISIBLE);
        showRlProTxError();
        Log.e("switchConnect", "switchConnect");
    }

    public void reject() {
        if (!isReject) {
            isReject = true;
            P2PHandler.getInstance().reject();
            disconnectDooranerfa();
            finish();
        }
    }

    public void readyCallDevice() {
        if (connectType == Constants.ConnectType.P2PCONNECT) {
            P2PHandler.getInstance().openAudioAndStartPlaying(1);
            P2PHandler.getInstance().getDefenceStates(mContact.getIpContactId(), mContact.getPassword());
        } else {
            P2PHandler.getInstance().openAudioAndStartPlaying(1);
            String callId = "1";
            String password = "0";
            P2PHandler.getInstance().getDefenceStates(callId, password);
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                T.showShort(mContext, R.string.press_again_monitor);
                exitTime = System.currentTimeMillis();
            } else {
                reject();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void changevideoformat() {
        if (control_top.getVisibility() == RelativeLayout.VISIBLE) {
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_out);
            anim2.setDuration(100);
            control_top.startAnimation(anim2);
            control_top.setVisibility(RelativeLayout.GONE);
            isShowVideo = false;
        } else {
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_in);
            anim2.setDuration(100);
            control_top.setVisibility(RelativeLayout.VISIBLE);
            control_top.startAnimation(anim2);
            isShowVideo = true;
        }
    }

    public void hideVideoFormat() {
        if (control_top.getVisibility() == RelativeLayout.VISIBLE) {
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_out);
            anim2.setDuration(100);
            control_top.startAnimation(anim2);
            control_top.setVisibility(RelativeLayout.GONE);
            isShowVideo = false;
        }
    }

    private MonitorPanView PanFunction;
    private MonitorTitleViewGroup PanBack;
    //private RoundSelectorTextView PanControl;
    private MonitorPanImConView PanDisplayControl;

    public void changeControl(boolean isShow) {
        if (mContact != null && mContact.isPanorama()) {
            control_bottom.setVisibility(View.INVISIBLE);
            if (PanFunction == null) {
                PanBack = new MonitorTitleViewGroup(mContext);
                PanFunction = new MonitorPanView(mContext);

                PanBack.setTag("PanBack");
                PanFunction.setTag("PanFunction");
                PanFunction.setOnMonitorPanClickListner(ItemPanClick);
                PanBack.setOnTitleClickListner(TitleClickListner);

                View[] linkViews = new View[]{PanBack, PanFunction};
                pView.setLinkedView(linkViews);
                r_p2pview.addView(PanBack);
                r_p2pview.addView(PanFunction);

                PanBack.bringToFront();
            }
            if (isShow) {
                // 显示全景工具条
                if (pView != null) {
                    pView.showVisible(P2PView.SHOWTYPE_MUST_SHOW);
                }
            } else {
                // 隐藏全景工具条
                if (pView != null) {
                    pView.showVisible(P2PView.SHOWTYPE_MUST_HIDEN);
                }
            }
        } else {
            // 隐藏全景工具条
            if (pView != null) {
                pView.showVisible(P2PView.SHOWTYPE_MUST_HIDEN);
            }
            if (PanDisplayControl != null) {
                PanDisplayControl.setVisibility(View.GONE);
            }
            changeControl();
        }
    }

    public void changeControl() {
        if (isSpeak && ScrrenOrientation == Configuration.ORIENTATION_LANDSCAPE) {// 对讲过程中不可消失
            // 设置control_bottom的高度
            int height = (int) getResources().getDimension(R.dimen.p2p_monitor_bar_height);
            setControlButtomHeight(height);
            control_bottom.setVisibility(View.VISIBLE);
            vStatusView.setVisibility(View.VISIBLE);
            control_bottom.bringToFront();
            if (isFoucusZoom) {
                l_zoom.setVisibility(View.VISIBLE);
            }
            return;
        } else if (isSpeak && ScrrenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setControlButtomHeight(0);
            control_bottom.setVisibility(View.INVISIBLE);
            vStatusView.setVisibility(View.VISIBLE);
            if (isFoucusZoom) {
                l_zoom.setVisibility(View.VISIBLE);
                l_zoom.bringToFront();
            }
            return;
        }
        if (ScrrenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setControlButtomHeight(0);
            control_bottom.setVisibility(View.INVISIBLE);
            vStatusView.setVisibility(View.VISIBLE);
            if (isFoucusZoom) {
                l_zoom.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (pView.isPanorama()) {// 全景不再响应
            return;
        }
        if (control_bottom.getVisibility() == RelativeLayout.VISIBLE) {
            // 设置control_bottom的高度等于0
            l_zoom.setVisibility(View.GONE);
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_out);
            anim2.setDuration(100);
            control_bottom.startAnimation(anim2);
            anim2.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation arg0) {
                    // TODO Auto-generated method stub
                    hideVideoFormat();
                    choose_video_format.setClickable(false);
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    // TODO Auto-generated method stub
                    hideVideoFormat();
                    setControlButtomHeight(0);
                    control_bottom.setVisibility(View.INVISIBLE);
                    choose_video_format
                            .setBackgroundResource(R.drawable.sd_backgroud);
                    choose_video_format.setClickable(true);
//					l_zoom.setVisibility(View.GONE);
                    vStatusView.setVisibility(View.GONE);
                }
            });
        } else {
            // 设置control_bottom的高度
            int height = (int) getResources().getDimension(
                    R.dimen.p2p_monitor_bar_height);
            setControlButtomHeight(height);
            control_bottom.setVisibility(View.VISIBLE);
            vStatusView.setVisibility(View.VISIBLE);
            control_bottom.bringToFront();
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_in);
            anim2.setDuration(100);
            control_bottom.startAnimation(anim2);
            anim2.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation arg0) {
                    // TODO Auto-generated method stub
                    hideVideoFormat();
                    choose_video_format.setClickable(false);
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    // TODO Auto-generated method stub
                    hideVideoFormat();
                    choose_video_format.setClickable(true);
                    if (isFoucusZoom) {
                        l_zoom.setVisibility(View.VISIBLE);
                    }

                }
            });
        }
    }

    /**
     * 新报警信息
     */
    NormalDialog dialog;
    String contactidTemp = "";

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

    // 关闭本次报警声音
    public void closeAlarmVoice(String alarmID, int type) {
        if (type == KEEP_SELF_CLIENT) {
            NormalDialog dialog = new NormalDialog(mContext, mContext
                    .getResources().getString(R.string.mute_the_alarm),
                    mContext.getResources().getString(
                            R.string.comfirm_mute_the_alarm), mContext
                    .getResources().getString(R.string.confirm),
                    mContext.getResources().getString(R.string.cancel));
            dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

                @Override
                public void onClick() {
                    if (alarmClose != null) {
                        alarmClose.setState(2);
                        alarmClose.startLoading();
                    }
                    FisheyeSetHandler.getInstance().sKeepClientCmd(mContact.getIpContactId(), mContact.getPassword());
                }
            });
            dialog.showNormalDialog();
        } else {
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
                        if (acl != null) {
                            acl.setState(2);
                            acl.startLoading();
                        }
                        FisheyeSetHandler.getInstance().sKeepClientCmd(contact.getIpContactId(), contact.getPassword());
                    }
                });
                dialog.showNormalDialog();
            } else {
                closecreatePassDialog(alarmID);
            }
        }
    }

    //解绑确认弹框
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

    private void seeMonitor(String contactId) {
        final Contact contact = FList.getInstance().isContact(contactId);
        if (null != contact) {
            mContact = contact;
            callAnotherInit();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Log.i("dxsmonitor", contactId);
            createPassDialog(contactId);
        }
    }

    private Dialog closepassworddialog;

    void closecreatePassDialog(String id) {
        closepassworddialog = new MyInputPassDialog(mContext,
                Utils.getStringByResouceID(R.string.mute_the_alarm), id,
                getResources().getString(R.string.inputpassword), closelistener);
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
        passworddialog = new MyInputPassDialog(mContext,
                Utils.getStringByResouceID(R.string.check), id, getResources()
                .getString(R.string.inputpassword), listener);
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

//			P2PConnect.vReject(9, "");
            new Thread() {
                @Override
                public void run() {
//					while (true) {
//						if (P2PConnect.getCurrent_state() == P2PConnect.P2P_STATE_NONE) {
                    Message msg = new Message();
                    String pwd = P2PHandler.getInstance()
                            .EntryPassword(password);
                    String[] data = new String[]{id, pwd,
                            String.valueOf(pushAlarmType)};
                    msg.what = 1;
                    msg.obj = data;
                    handler.sendMessage(msg);
//							break;
//						}
//						Utils.sleepThread(500);
//					}
                }
            }.start();

        }
    };
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (msg.what == 0) {
                Contact contact = (Contact) msg.obj;
                Intent monitor = new Intent(mContext, ApMonitorActivity.class);
                monitor.putExtra("contact", contact);
                monitor.putExtra("connectType",
                        Constants.ConnectType.P2PCONNECT);
                startActivity(monitor);

                // Intent monitor = new Intent();
                // monitor.setClass(mContext, CallActivity.class);
                // monitor.putExtra("callId", contact.contactId);
                // monitor.putExtra("password", contact.contactPassword);
                // monitor.putExtra("isOutCall", true);
                // monitor.putExtra("contactType", P2PValue.DeviceType.NPC);
                // monitor.putExtra("type",
                // Constants.P2P_TYPE.P2P_TYPE_MONITOR);

                // if (Integer.parseInt(data[2]) ==
                // P2PValue.DeviceType.DOORBELL) {
                // monitor.putExtra("isSurpportOpenDoor", true);
                // }
                // startActivity(monitor);
                // finish();
            } else if (msg.what == 1) {
                if (passworddialog != null && passworddialog.isShowing()) {
                    passworddialog.dismiss();
                }
                String[] data = (String[]) msg.obj;
                Contact contact = new Contact();
                contact.contactId = data[0];
                contact.contactPassword = data[1];
                contact.subType = subType;
                contact.contactType = mainType;
                mContact = contact;
                callAnotherInit();
            }
            // Intent monitor = new Intent();
            // monitor.setClass(mContext, CallActivity.class);
            // monitor.putExtra("callId", data[0]);
            // monitor.putExtra("password", data[1]);
            // monitor.putExtra("isOutCall", true);
            // monitor.putExtra("contactType", Integer.parseInt(data[2]));
            // monitor.putExtra("type", Constants.P2P_TYPE.P2P_TYPE_MONITOR);
            // if (Integer.parseInt(data[2]) == P2PValue.DeviceType.DOORBELL) {
            // monitor.putExtra("isSurpportOpenDoor", true);
            // }
            return false;
        }
    });

    //监控另一台设备时，还原一些状态
    public void callAnotherInit() {
        P2PHandler.getInstance().reject();
        isCallAnother = true;
        switchConnect();
        deviceType = mContact.contactType;
        l_zoom.setVisibility(View.GONE);
        rlLampControl.setVisibility(View.GONE);
        isFoucusZoom = false;
        P2PView.SUPPORT_ZOOM_FOCUS = false;
        if (mThreeFrag.prepointPop != null) {
            mThreeFrag.prepointPop.dismiss();
        }
        if (pView != null && mContact != null) {
            pView.setPanorama(mContact.isPanorama());
        }
        PrePointInfo = -1;
        PreFunctionMode = -2;
        isSupportPrepoint = false;
        isReceveHeader = false;
        preSetLocationLayout.setVisibility(View.GONE);
        P2PHandler.getInstance().getFocusZoom(mContact.getIpContactId(), mContact.getPassword());
        subType = mContact.subType;
        tv_name.setText(mContact.contactId);
        if (isSpeak) {
            stopSpeak();
        }
        setHeaderImage();
        if (pushAlarmType == P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH) {
            initSpeark(mContact.contactType, true);
        } else {
            initSpeark(mContact.contactType, false);
        }
        connectDooranerfa();
        callDevice();
        P2PHandler.getInstance().getNpcSettings(mContact.getIpContactId(), mContact.getPassword());
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        frushLayout(P2PValue.DeviceType.IPC);
        hideAlarmCloseVoice();
        hideTimeTextView();
        //隐藏全屏监控下面工具条
        control_bottom.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onHomePressed() {
        reject();
        super.onHomePressed();
    }

    public void getScreenWithHeigh() {
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        MyApplication.SCREENWIGHT = dm.widthPixels;
        MyApplication.SCREENHIGHT = dm.heightPixels;
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        readyCallDevice();
        initp2pView();
    }

    /*
     * 初始化P2pview
	 */
    public void initp2pView() {
        this.initP2PView(mContact.contactType,P2PView.LAYOUTTYPE_TOGGEDER);
        WindowManager manager = getWindowManager();
        window_width = manager.getDefaultDisplay().getWidth();
        window_height = manager.getDefaultDisplay().getHeight();
        this.initScaleView(this, window_width, window_height);
        setMute(true);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void connectDooranerfa() {
        if (isCustomCmdAlarm == true) {
            String cmd_connect = "IPC1anerfa:connect";
            P2PHandler.getInstance().sendCustomCmd(mContact.getIpContactId(), mContact.getPassword(),
                    cmd_connect);
        }
    }

    public void disconnectDooranerfa() {
        if (isCustomCmdAlarm == true) {
            String cmd_disconnect = "IPC1anerfa:disconnect";
            P2PHandler.getInstance().sendCustomCmd(mContact.getIpContactId(), mContact.getPassword(),
                    cmd_disconnect);
        }
    }

    public void setControlButtomHeight(int height) {
        LayoutParams control_bottom_parames = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        control_bottom_parames.height = height;
        control_bottom.setLayoutParams(control_bottom_parames);
    }

    public void setZoomMargin(boolean isFullScreen) {
        LayoutParams zoom_params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int control_bottom_h = (int) getResources().getDimension(R.dimen.p2p_monitor_bar_height);
        int preset_h = (int) getResources().getDimension(R.dimen.tx_preset_location_height);
        int preset_left = (int) getResources().getDimension(R.dimen.tx_preset_location_maginright);
        int preset_w = (int) getResources().getDimension(R.dimen.tx_preset_location_width);
        int mH = (int) getResources().getDimension(R.dimen.monitor_zoom_margin_top);
        int zoom_add_w = zoomAdd.getMeasuredWidth();
        int margin_right = (int) getResources().getDimension(R.dimen.monitor_zoom_focus_margin_right);
        if (isFullScreen) {
            zoom_params.bottomMargin = mH + control_bottom_h;
            if (preSetLocationLayout.getVisibility() == View.VISIBLE) {
                //横屏时变焦变倍位于预置位添加按钮下面
                zoom_params.topMargin = mH + preset_h;
                zoom_params.rightMargin = preset_w / 2 + preset_left - zoom_add_w / 2;
            } else {
                zoom_params.topMargin = mH;
                zoom_params.rightMargin = margin_right;
            }
        } else {
            zoom_params.topMargin = mH;
            zoom_params.bottomMargin = mH;
            zoom_params.rightMargin = margin_right;
        }
        l_zoom.setLayoutParams(zoom_params);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        Log.e("onPageSelected", "arg0=" + arg0);
        changePoint(arg0);
    }

    private void changePoint(int arg0) {
        switch (arg0) {
            case 0:
                showOne();
                break;
            case 1:
                showTwo();
                break;
            case 2:
                break;
            default:
                break;
        }

        viewPager.setCurrentItem(arg0);

    }

    public void showOne() {
        iv_point_one.setImageResource(R.drawable.monitor_point_black);
        iv_point_two.setImageResource(R.drawable.monitor_point_gary);

    }

    public void showTwo() {
        iv_point_one.setImageResource(R.drawable.monitor_point_gary);
        iv_point_two.setImageResource(R.drawable.monitor_point_black);

    }

    public void ScreenShot(int prePoint) {
        captureScreen(prePoint);

    }

    Handler myHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int multiple = msg.what;
            long last_time = SharedPreferencesManager.getInstance()
                    .getFocusZoomTime(mContext);
            long current_time = System.currentTimeMillis();
            if (current_time - last_time > 4000) {
                currentPosition = currentPosition + multiple;
                if (currentPosition > 10) {
                    currentPosition = 10;
                }
                if (currentPosition < 0) {
                    currentPosition = 0;
                }
                P2PHandler.getInstance().setFocusZoom(mContact.getIpContactId(), mContact.getPassword(),
                        currentPosition);
                SharedPreferencesManager.getInstance().putFocusZoomTime(
                        mContext, current_time);
            }
            return false;
        }
    });

    // 监控时，本台设备推送报警过来，在页面上显示关闭本次报警图标
    public void showAlarmCloseVoice() {
        if (!isShowAlarmClose) {
            alarmClose = new AlarmCloseVoice(mContext, mContact.contactId);
            alarmClose.settype(KEEP_SELF_CLIENT);
            alarmClose.setcloseClickListener(clistener);
            alarmClose.setState(NORMAL);
            r_p2pview.addView(alarmClose);
            isShowAlarmClose = true;
        }
    }

    public void hideAlarmCloseVoice() {
        if (alarmClose != null && isShowAlarmClose == true) {
            r_p2pview.removeView(alarmClose);
            isShowAlarmClose = false;
        }
    }

    closeClickListener clistener = new closeClickListener() {

        @Override
        public void onclose(String deviceId, int type) {
            // TODO Auto-generated method stub
            closeAlarmVoice(deviceId, type);
        }
    };

    public void setLocation(int[] data) {
        preSetLocations.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 1) {
                preSetLocations.add(i);
            }
        }
        preSetLocationLayout.changeVisibility(preSetLocations, PreFunctionMode);
    }

    PreSetLocationListener preSetLocationListener = new PreSetLocationListener() {

        @Override
        public void addPresetLocation() {
            // TODO Auto-generated method stub
            int index = 0;
            int[] itemArray;
            if (null == preSetLocations || preSetLocations.size() == 0) {
                itemArray = new int[]{};
            } else {
                itemArray = new int[preSetLocations.size() + 1];
                for (int i = 0; i < preSetLocations.size(); i++) {
                    itemArray[i] = preSetLocations.get(i);
                }
                itemArray[preSetLocations.size()] = MonitorThreeFrag.PREPOINTCOUNTS;
            }
            index = Utils.getNextItem(itemArray);
            if (!userCanClick || PreFunctionMode == -2) {
                T.showLong(mContext, R.string.prepoint_cannottosee);
                return;
            }
            if (PreFunctionMode == 84 || PreFunctionMode == -1) {
                T.showShort(mContext, R.string.not_support);
                return;
            }
            Utils.setPrePoints(mContact.getIpContactId(),
                    mContact.getPassword(), 1, index);
            if (loading == null) {
                loading = new NormalDialog(mContext);
            }
            loading.setTitle(R.string.set);
            loading.showLoadingDialog();
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        public void lookPresetLocation(int point) {
            // TODO Auto-generated method stub
            Utils.setPrePoints(mContact.getIpContactId(), mContact.getPassword(), 0, point);
        }
    };

    public void setPositionName() {
        Prepoint prepoint = DataManager.findPrepointByDevice(mContext,
                mContact.contactId);
        preSetLocationLayout.changeName(prepoint, preSetLocations,
                PreFunctionMode);
    }

    private ViewGroup parent;
    private final static String VIEWTAG = "guide";

    private void showGuideLayout(int type) {
        if (hasShowGuide())
            return;
        parent = (ViewGroup) ApMonitorActivity.this
                .findViewById(android.R.id.content);
        GuideRelayout guide = (GuideRelayout) parent.findViewWithTag(VIEWTAG);
        if (guide == null) {
            guide = (GuideRelayout) LayoutInflater.from(ApMonitorActivity.this)
                    .inflate(R.layout.layout_guide_monitor, null);
        }
        guide.setGuideListner(guideListner);
        if (type == 0) {
            if (!guide.isShown()) {
                TextView txTips = (TextView) guide.findViewById(R.id.tx_tips);
                txTips.setTypeface(TextViewUtils.setTypeFont(txTips,
                        "monitor.ttf"));
                if (pView.isPanorama()) {
                    txTips.setText(R.string.guide_monitor_pan);
                } else {
                    txTips.setText(R.string.guide_monitor);
                }
                ImageView raw = (ImageView) guide.findViewById(R.id.iv_raw);
                LinearLayout llGuide = (LinearLayout) guide
                        .findViewById(R.id.ll_guide);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                p.addRule(Gravity.CENTER_HORIZONTAL);
                if (isFullScreen) {
                    if (pView.isPanorama()) {
                        raw.setImageResource(R.drawable.guide_moniter_pan_land);
                        // p.topMargin =
                        // getGuideTopMargin(R.drawable.guide_moniter_pan_land);
                    } else {
                        raw.setImageResource(R.drawable.guide_monitor_land);
                        // p.topMargin =
                        // getGuideTopMargin(R.drawable.guide_monitor_land);
                    }
                    p.addRule(RelativeLayout.CENTER_IN_PARENT);
                } else {
                    if (pView.isPanorama()) {
                        raw.setImageResource(R.drawable.guide_moniter_pan);
                        p.topMargin = getGuideTopMargin(R.drawable.guide_moniter_pan);
                    } else {
                        raw.setImageResource(R.drawable.guide_monitor);
                        p.topMargin = getGuideTopMargin(R.drawable.guide_monitor);
                    }
                }
                llGuide.setLayoutParams(p);
                guide.setTag(VIEWTAG);
                parent.addView(guide);
                setShowGuide();
                guide.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        parent.removeView(v);
                    }
                });
            }
        }
    }

    private int getGuideTopMargin(int draR) {
        int title = (int) mContext.getResources().getDimension(
                R.dimen.title_height);
        int margin = (r_p2pview.getMeasuredHeight()
                - Utils.getDrawableWAndrH(draR)[1] - Utils.dip2px(mContext, 23)
                - Utils.sp2px(mContext, 18) - 50) / 2;// 50尝试的结果
        return title + margin;
    }

    private GuideListner<GuideRelayout> guideListner = new GuideListner<GuideRelayout>() {

        @Override
        public void onGuideShow(GuideRelayout view) {
            changeControl(false);
        }

        @Override
        public void onGuideDismiss(GuideRelayout view) {
            if (ScrrenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                changeControl(true);
            } else {
                changeControl(false);
            }
        }

        @Override
        public void onLayoutState(GuideRelayout view, int state) {
            changeControl(false);
            LinearLayout llGuide = (LinearLayout) view
                    .findViewById(R.id.ll_guide);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ImageView raw = (ImageView) view.findViewById(R.id.iv_raw);
            if (state == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
                if (pView.isPanorama()) {
                    raw.setImageResource(R.drawable.guide_moniter_pan_land);
                } else {
                    raw.setImageResource(R.drawable.guide_monitor_land);
                }
                p.addRule(RelativeLayout.CENTER_IN_PARENT);
            } else {
                if (pView.isPanorama()) {
                    raw.setImageResource(R.drawable.guide_moniter_pan);
                    p.topMargin = getGuideTopMargin(R.drawable.guide_moniter_pan);
                } else {
                    raw.setImageResource(R.drawable.guide_monitor);
                    p.topMargin = (int) mContext.getResources().getDimension(
                            R.dimen.title_height);
                }
                p.addRule(Gravity.CENTER_HORIZONTAL);
            }
            llGuide.setLayoutParams(p);
            view.requestLayout();
        }
    };

    private boolean hasShowGuide() {
        String vaules = pView.isPanorama() ? SharedPreferencesManager.SHOW_MONITER_PAN_GUIDE
                : SharedPreferencesManager.SHOW_MONITER_NORMA_GUIDE;
        return SharedPreferencesManager.getInstance().getShowGuide(mContext,
                vaules);
    }

    private void setShowGuide() {
        String vaules = pView.isPanorama() ? SharedPreferencesManager.SHOW_MONITER_PAN_GUIDE
                : SharedPreferencesManager.SHOW_MONITER_NORMA_GUIDE;
        SharedPreferencesManager.getInstance().putShowGuide(mContext, vaules,
                true);
    }

    private MoniterTimeTextview TimeTextView;

    private void initTimeTextView() {
        if (mContact != null && mContact.isPanorama()) {
            TimeTextView = new MoniterTimeTextview(mContext);
            r_p2pview.addView(TimeTextView);
        }
    }

    private void hideTimeTextView() {
        if (TimeTextView != null) {
            r_p2pview.removeView(TimeTextView);
        }
        DismissDisplay();
        changeControl(false);
    }

    @Override
    protected void onVideoPTS(final long videoPTS) {
        if (TimeTextView != null) {
            ((ApMonitorActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TimeTextView.setTime(videoPTS);
                }
            });
        }
    }

    private onMonitorPanClickListner ItemPanClick = new onMonitorPanClickListner() {

        @Override
        public void onItemClick(View view, int position) {
            switch (position) {
                case 0:
                    setDefence();
                    break;
                case 1:
                    BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_SETVOICESTATE, "Sets mute state vertical screen");
                    setVoiceState();
                    break;
                case 2:
                    ApMonitorActivity.this.captureScreen(-1);
                    break;
                case 4:
                    changeGyro();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onItemPress(View view, int position, boolean isPress) {
            if (isPress) {
                speak();
            } else {
                noSpeak();
            }
        }

        @Override
        public void onControlClick(ViewGroup group, View viewControl) {
            //empty
        }
    };

    private onMonitorPanClickListner ItemPanDisplay = new onMonitorPanClickListner() {

        @Override
        public void onItemClick(View view, int position) {
            Utils.setSelectMode(mContact, position);
            pView.setShapeType(Utils.getShapeType(mContact, position));
        }

        @Override
        public void onItemPress(View view, int position, boolean isPress) {
            //empty
        }

        @Override
        public void onControlClick(ViewGroup group, View viewControl) {
            //单击控制
            if (group instanceof MonitorPanImConView) {
                ((MonitorPanImConView) group).changeControl();
            }
        }
    };
    private onTitleClickListner TitleClickListner = new onTitleClickListner() {

        @Override
        public void onBackClick(View view) {
            if (bSensorTracker) {
                changeGyro();
            }
            changeControl(false);
            ScrrenOrientation = Configuration.ORIENTATION_PORTRAIT;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mSensorTracker.stopTracking();
    }

    private void changeGyro() {
        if (!bSensorTracker) {
            mSensorTracker.startTracking();
            bSensorTracker = true;
            try {
                MediaPlayer.getInstance()._OnGesture(5, 1, 0, 0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (PanFunction != null) {
                PanFunction.setItemImage(4, new int[]{
                        R.drawable.monitor_l_pantuo_on,
                        R.drawable.monitor_l_pantuo_on_p});
            }
        } else {
            mSensorTracker.stopTracking();
            bSensorTracker = false;
            try {
                MediaPlayer.getInstance()._OnGesture(5, 0, 0, 0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (PanFunction != null) {
                PanFunction.setItemImage(4, new int[]{
                        R.drawable.monitor_l_pantuo,
                        R.drawable.monitor_l_pantuo_p});
            }
        }
    }

    //全景设备初始化渲染方式的选择控件
    private void initDisplay() {
        if (mContact != null && mContact.isPanorama()) {
            View view = r_p2pview.findViewWithTag("PanDisplayControl");
            if (view != null) {
                r_p2pview.removeView(view);
            }
            PanDisplayControl = new MonitorPanImConView(mContext, mContact);
            PanDisplayControl.setTag("PanDisplayControl");
            PanDisplayControl.setOnMonitorPanClickListner(ItemPanDisplay);
            r_p2pview.addView(PanDisplayControl);
            PanDisplayControl.dissMiss();
        }
    }

    //全景设备显示渲染方式的选择控件
    private void showDisplay() {
        if (mContact != null && mContact.isPanorama() && PanDisplayControl != null) {
            PanDisplayControl.show();
        }
    }

    //全景设备隐藏渲染方式的选择控件
    private void DismissDisplay() {
        if (mContact != null && mContact.isPanorama() && PanDisplayControl != null && r_p2pview != null) {
            PanDisplayControl.dissMiss();
            r_p2pview.removeView(r_p2pview);
        }
    }

    private Bitmap TextViewCatch;

    public void onPreCapture(int mark, int prepoint) {
        if (TimeTextView != null) {
            TimeTextView.setDrawingCacheEnabled(true);
            TextViewCatch = TimeTextView.getDrawingCache();
        }
    }

    //显示设备录像状态
    private void ShowVideoStateView(int state) {
        if (vStatusView == null) {
            vStatusView = new VideoStatusView(mContext, ScrrenOrientation, state);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            r_p2pview.addView(vStatusView, params);
            vStatusView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ConfirmOrCancelDialog dialog = new ConfirmOrCancelDialog(mContext,
                            ConfirmOrCancelDialog.SELECTOR_BLUE_TEXT, ConfirmOrCancelDialog.SELECTOR_GARY_TEXT);
                    dialog.setTitle(R.string.request_to_format_tf_card);
                    dialog.setTextNo(mContext.getResources().getString(R.string.format));
                    dialog.setTextYes(mContext.getResources().getString(R.string.cancel));
                    dialog.setOnYesClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setOnNoClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            P2PHandler.getInstance().setSdFormat(mContact.getIpContactId(),
                                    mContact.getPassword(), 16);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            if (state == VideoStatusView.NO_TF) {
                vStatusView.setClickable(true);
            } else {
                vStatusView.setClickable(false);
            }
        } else {
            if (state == VideoStatusView.NO_TF) {
                vStatusView.setClickable(true);
            } else {
                vStatusView.setClickable(false);
            }
            vStatusView.setState(state);
            vStatusView.startLoading();
        }

    }

    //密码错误输入框监听
    InputPasswordDialog.InputPasswordClickListener inputPwdClickListener = new InputPasswordDialog.InputPasswordClickListener() {
        @Override
        public void onCancelClick() {
            if (inputPwdDialog != null) {
                inputPwdDialog.dismiss();
                inputPwdDialog = null;
            }
        }

        @Override
        public void onOkClick(String contactId, String pwd) {
            if (pwd == null || pwd.equals("")) {
                T.showShort(mContext, R.string.input_password);
                return;
            }
            if (pwd.length() > 30 || pwd.charAt(0) == '0') {
                T.showShort(mContext, R.string.device_password_invalid);
                return;
            }
            String password = P2PHandler.getInstance().EntryPassword(pwd);
            mContact.userPassword = pwd;
            mContact.contactPassword = password;
            mContact.defenceState = Constants.DefenceState.DEFENCE_STATE_LOADING;
            Contact c=FList.getInstance().isContact(mContact.contactId);
            if(c!=null){
                FList.getInstance().update(mContact);
            }
//			T.showShort(mContext, R.string.modify_success);
            //重新监控
            P2PHandler.getInstance().getNpcSettings(mContact.getIpContactId(), mContact.getPassword());
            P2PHandler.getInstance().getFocusZoom(mContact.getIpContactId(), mContact.getPassword());
            P2PHandler.getInstance().checkPassword(mContact.getIpContactId(), mContact.getPassword());
            callDevice();
            hideError();
            if (inputPwdDialog != null) {
                inputPwdDialog.dismiss();
                inputPwdDialog = null;
            }

        }
    };
}
