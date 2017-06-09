package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.entity.Sensor;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.utils.Utils;
import com.jwkj.widget.SwitchPopwindow;
import com.p2p.core.BaseMonitorActivity;
import com.p2p.core.P2PValue;
import com.p2p.core.utils.BaiduTjUtils;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//将此类的setBackgroundResource都改为setImageResource
public class MonitorOneFragment extends Fragment implements OnClickListener {
    private Context mContext;
    ImageView iv_defence, iv_speak, iv_screenshot;

    private List<Sensor> sensors = new ArrayList<Sensor>();
    boolean isRegFilter = false;
    private int SwitchPopState = 0;// 开关弹出窗状态 0还在获取，1获取结束
    private View view;
    private final static int SwitchH = 117;
    boolean isSupport = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater
                .inflate(R.layout.fragment_monitor_one, container, false);
        mContext = getActivity();
        initUI(view);
        regFilter();
        return view;
    }

    public void initUI(View view) {
        iv_defence = (ImageView) view.findViewById(R.id.iv_defence);
        iv_speak = (ImageView) view.findViewById(R.id.iv_speak);
        iv_screenshot = (ImageView) view.findViewById(R.id.iv_screenshot);
        if (Utils.isSmartHomeContatct(P2PValue.DeviceType.IPC,
                ApMonitorActivity.subType)) {
            iv_defence.setImageResource(R.drawable.bg_monitor_control);
        } else {
            iv_defence.setImageResource(R.drawable.selector_portrait_disarm);
        }
        iv_defence.setOnClickListener(this);
        iv_screenshot.setOnClickListener(this);
        if (ApMonitorActivity.mContact.contactType != P2PValue.DeviceType.DOORBELL
                && !ApMonitorActivity.isSurpportOpenDoor) {
            iv_speak.setOnTouchListener(speakListener);
        } else if (ApMonitorActivity.mContact.contactType == P2PValue.DeviceType.DOORBELL
                && !ApMonitorActivity.isSurpportOpenDoor) {
            iv_speak.setOnClickListener(this);
        } else if (ApMonitorActivity.isSurpportOpenDoor) {
            iv_speak.setOnClickListener(this);
            // 开始监控时没有声音，暂时这样
            setSendVoidSpeak();
        }
        SwitchPopState = 0;
    }

    OnTouchListener speakListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mContext instanceof BaseMonitorActivity) {
                        ((ApMonitorActivity) mContext).speak();
                    }
                    iv_speak.setImageResource(R.drawable.portrait_speak_p);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (mContext instanceof BaseMonitorActivity) {
                        ((ApMonitorActivity) mContext).noSpeak();
                    }
                    iv_speak.setImageResource(R.drawable.portrait_speak);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    if (mContext instanceof BaseMonitorActivity) {
                        ((ApMonitorActivity) mContext).noSpeak();
                    }
                    iv_speak.setImageResource(R.drawable.portrait_speak);
                    return true;
            }
            return false;
        }
    };

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        // filter.addAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
        // filter.addAction(Constants.P2P.RET_SET_REMOTE_DEFENCE);
        filter.addAction(Constants.Action.CHANGE_REMOTE_DEFENCE);
        filter.addAction(Constants.Action.NEW_MONITOR);
        // 控制类传感器
        filter.addAction(Constants.P2P.RET_GET_SENSOR_WORKMODE);
        filter.addAction(Constants.P2P.RET_GET_LAMPSTATE);
        filter.addAction(Constants.P2P.RET_DEVICE_NOT_SUPPORT);
        mContext.registerReceiver(br, filter);
        isRegFilter = true;
    }

    BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.P2P.RET_GET_REMOTE_DEFENCE)) {
                String ids = intent.getStringExtra("contactId");
                if (!Utils.isSmartHomeContatct(P2PValue.DeviceType.IPC,
                        ApMonitorActivity.subType)) {
                    if (!ids.equals("")
                            && ids.equals(ApMonitorActivity.mContact.contactId)) {
                        int defenceState = intent.getIntExtra("state", -1);
                        if (defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
                            iv_defence
                                    .setImageResource(R.drawable.selector_portrait_arm);
                        } else {
                            iv_defence
                                    .setImageResource(R.drawable.selector_portrait_disarm);
                        }
                    }
                }
            } else if (intent.getAction().equals(
                    Constants.Action.CHANGE_REMOTE_DEFENCE)) {
                int defencestate = intent.getIntExtra("defencestate", -1);
                if (!Utils.isSmartHomeContatct(P2PValue.DeviceType.IPC,
                        ApMonitorActivity.subType)) {
                    if (defencestate == Constants.DefenceState.DEFENCE_STATE_ON) {
                        iv_defence
                                .setImageResource(R.drawable.selector_portrait_arm);
                    } else {
                        iv_defence
                                .setImageResource(R.drawable.selector_portrait_disarm);
                    }
                }
            } else if (intent.getAction().equals(Constants.Action.NEW_MONITOR)) {
                int deviceType = intent.getIntExtra("deviceType", -1);
                boolean isOpenDor = intent.getBooleanExtra("isOpenDor", false);
                setSendVoidNoSpeak();
                initSpeark(deviceType, isOpenDor);
                isSupport = true;
                sensors.clear();
                getAllSensorData();
                if (switchPop != null && switchPop.isShowing()) {
                    switchPop.dismiss();
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_SENSOR_WORKMODE)) {
                // 获取传感器防护计划返回
                int iSrcID = intent.getIntExtra("iSrcID", 0);
                byte boption = intent.getByteExtra("boption", (byte) -1);
                byte[] data = intent.getByteArrayExtra("data");
                if (boption == Constants.FishBoption.MESG_GET_OK) {
                    if (data.length < 14) {
                        return;
                    }
                    int len1 = Utils.bytesToInt(data, 5);
                    int len2 = Utils.bytesToInt(data, 10);
                    paserSensorData(data, data[4], len1, data[9], len2);
                    getLampState((byte) 1);
                } else {
                    // T.showLong(mContext, "获取错误");
                }
                SwitchPopState = 1;
                if (switchPop != null) {
                    // 展示获取的结果
                    switchPop.getSensored(SwitchPopState);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_GET_LAMPSTATE)) {
                int iSrcID = intent.getIntExtra("iSrcID", 0);
                byte boption = intent.getByteExtra("boption", (byte) -1);
                byte[] data = intent.getByteArrayExtra("data");
                if (boption == Constants.FishBoption.MESG_GET_OK) {
                    Sensor sensor = getSensorByData(data, 4);
                    if (sensor != null) {
                        sensor.setLampState(data[3]);
                        if (switchPop != null) {
                            switchPop.getSensored(SwitchPopState);
                            switchPop.updataswitch(getSensorPosition(sensor));
                        } else {
                            Log.e("dxsTest", "switchPop为空");
                        }
                    } else {
                        Log.e("dxsTest", "sensor为空");
                    }
                } else if (boption == 10) {
                    Sensor sensor = getSensorByData(data, 4);
                    if (sensor != null) {
                        getLampState((byte) 1, sensor);
                    }
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_DEVICE_NOT_SUPPORT)) {
                isSupport = false;
            }
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_defence:
                if (!Utils.isSmartHomeContatct(P2PValue.DeviceType.IPC,
                        ApMonitorActivity.subType)) {
                    if (mContext instanceof BaseMonitorActivity) {
                        ((ApMonitorActivity) mContext).setDefence();
                    }
                } else {
                    showSwitchPop(sensors);
                }
                break;
            case R.id.iv_speak:
                if (mContext instanceof BaseMonitorActivity) {
                    if (!ApMonitorActivity.isSpeak) {
                        ((ApMonitorActivity) mContext).speak();
                        setSendVoidSpeak();
                    } else {
                        ((ApMonitorActivity) mContext).noSpeak();
                        setSendVoidNoSpeak();
                    }
                }
                break;
            case R.id.iv_screenshot:
                if (mContext instanceof BaseMonitorActivity) {
                    BaiduTjUtils.onEvent(mContext, BaiduTjUtils.TJ_SCREENSHOT, "Screenshots vertical screen");
                    ((ApMonitorActivity) mContext).ScreenShot(-1);
                }
                break;
            default:
                break;
        }
    }

    public void initSpeark(int deviceType, boolean isOpenDor) {
        if (deviceType != P2PValue.DeviceType.DOORBELL && !isOpenDor) {
            iv_speak.setOnTouchListener(speakListener);
        } else if (deviceType == P2PValue.DeviceType.DOORBELL && !isOpenDor) {
            iv_speak.setOnTouchListener(null);
            iv_speak.setOnClickListener(this);
        } else if (isOpenDor) {
            iv_speak.setOnTouchListener(null);
            iv_speak.setOnClickListener(this);
            setSendVoidSpeak();
        }
        if (Utils.isSmartHomeContatct(P2PValue.DeviceType.IPC,
                ApMonitorActivity.subType)) {
            iv_defence.setImageResource(R.drawable.bg_monitor_control);
        } else {
            iv_defence.setImageResource(R.drawable.selector_portrait_disarm);
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getAllSensorData();
        changeUI();
    }

    private void changeUI() {
        if (iv_speak != null && iv_defence != null && iv_screenshot != null) {
            int w = iv_speak.getMeasuredWidth();
            int smallW = (int) (w * 0.564);

        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (isRegFilter) {
            mContext.unregisterReceiver(br);
            isRegFilter = false;
        }
    }

    /**
     * 设置
     *
     * @param sensors
     */
    SwitchPopwindow switchPop;

    private void showSwitchPop(List<Sensor> sensors) {
        switchPop = new SwitchPopwindow(mContext, Utils.dip2px(mContext,
                SwitchH), sensors);
        switchPop.setOnSwitchListner(popwindowListner);
        switchPop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        switchPop.getSensored(SwitchPopState);
    }

    private SwitchPopwindow.OnSwitchListner popwindowListner = new SwitchPopwindow.OnSwitchListner() {
        @Override
        public void onSwitchClick(int position, Sensor sensor) {
            // 带开关的传感器的开关点击
            if (sensor.isControlSensor()) {
                if (sensor.getLampState() == 1 || sensor.getLampState() == 3) {
                    getLampState((byte) 3, sensor);
                } else if (sensor.getLampState() == 2
                        || sensor.getLampState() == 4) {
                    getLampState((byte) 2, sensor);
                }
                sensor.setLampState((byte) 0);
                switchPop.updataswitch(position);
            }
        }
    };

    /**
     * 获取所有的一般传感器
     */
    private void getAllSensorData() {
        if (ApMonitorActivity.mContact != null) {
            FisheyeSetHandler.getInstance().sGetSenSorWorkMode(
                    ApMonitorActivity.mContact.contactId,
                    ApMonitorActivity.mContact.contactPassword);
        }
    }

    /**
     * 解析指定传感器类型数据
     *
     * @param data
     * @param Controcons
     * @param controlen
     * @param Sensorcons
     * @param Sensorlen
     */
    private void paserSensorData(byte[] data, byte Controcons, int controlen,
                                 byte Sensorcons, int Sensorlen) {
        byte[] contro = new byte[21];
        if ((14 + Controcons * 21) > data.length) {
            return;
        }
        if ((14 + controlen + Sensorcons * 24) > data.length) {
            return;
        }
        for (int i = 0; i < Controcons; i++) {
            System.arraycopy(data, 14 + i * contro.length, contro, 0,
                    contro.length);
            Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_NORMAL, contro,
                    contro[0]);
            // sensors.add(sensor);
        }
        if (data[3] == 0) {// 没有防护计划
            byte[] sens = new byte[24];
            for (int i = 0; i < Sensorcons; i++) {
                System.arraycopy(data, 14 + controlen + i * sens.length, sens,
                        0, sens.length);
                Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_NORMAL, sens,
                        sens[0]);
                Sensor s = getSensorByData(sensor.getSensorData(), 0);
                if (sensor.isControlSensor() && s == null) {
                    sensors.add(sensor);
                }
            }
        } else if (data[3] == 1) {// 有24位的防护计划
            byte[] sens = new byte[49];
            byte[] RealSens = new byte[24];
            for (int i = 0; i < Sensorcons; i++) {
                System.arraycopy(data, 14 + controlen + i * sens.length, sens,
                        0, sens.length);
                System.arraycopy(sens, 0, RealSens, 0, RealSens.length);
                Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_NORMAL,
                        RealSens, RealSens[0]);
                Sensor s = getSensorByData(sensor.getSensorData(), 0);
                if (sensor.isControlSensor() && s == null) {
                    sensors.add(sensor);
                }
            }
        }
    }

    /**
     * 获取所有插座状态
     *
     * @param lamstate 1.查询 2.开 3.关闭
     */
    private void getLampState(byte lamstate) {
        for (Sensor sensor : sensors) {
            if (sensor.isControlSensor()) {
                getLampState(lamstate, sensor);
            }
        }
    }

    /**
     * 获取或者设置插座状态
     *
     * @param lamstate 0.查询 2.开 3.关闭
     * @param sensor
     */
    private void getLampState(byte lamstate, Sensor sensor) {
        if (sensor.isControlSensor()) {
            FisheyeSetHandler.getInstance().sGetLampStatu(
                    ApMonitorActivity.mContact.contactId,
                    ApMonitorActivity.mContact.contactPassword, lamstate,
                    sensor.getSensorData());
        }
    }

    /**
     * 通过特征码获取列表的传感器
     *
     * @param data   原始数据
     * @param offset 数据偏移值
     * @return 不存在返回null
     */
    private Sensor getSensorByData(byte[] data, int offset) {
        byte[] dataTemp = new byte[4];
        byte[] sensorTemp = new byte[4];
        System.arraycopy(data, offset, dataTemp, 0, dataTemp.length);
        for (Sensor sensor : sensors) {
            byte[] sensorInfo = sensor.getSensorData();
            System.arraycopy(sensorInfo, 0, sensorTemp, 0, sensorTemp.length);
            if (Arrays.equals(sensorTemp, dataTemp)) {
                return sensor;
            }
        }
        return null;
    }

    /**
     * 获取sensor在列表的位子
     *
     * @param sensor
     * @return
     */
    private int getSensorPosition(Sensor sensor) {
        return sensors.indexOf(sensor);
    }

    public void setSendVoidSpeak() {
        iv_speak.setImageResource(R.drawable.portrait_speak_p);
    }

    public void setSendVoidNoSpeak() {
        iv_speak.setImageResource(R.drawable.portrait_speak);
    }

}
