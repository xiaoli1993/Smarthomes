package com.nuowei.smarthome.activity;

import android.annotation.SuppressLint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.Json.AirDetector;
import com.nuowei.smarthome.smarthomesdk.Json.Currency;
import com.nuowei.smarthome.smarthomesdk.Json.Remote;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.smarthomesdk.ModuleInfo;
import com.nuowei.smarthome.smarthomesdk.SmartLinkManipulator;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.smarthomesdk.utils.Utils;
import com.nuowei.smarthome.view.button.ButtonProgressBar;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;
import com.orhanobut.hawk.Hawk;

import org.apache.http.Header;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;

/**
 * @Author : 肖力
 * @Time :  2017/4/26 16:04
 * @Description :
 * @Modify record :
 */

public class SmartLinkActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.tv_wifiSSID)
    DinProTextView tvWifiSSID;
    @BindView(R.id.image_btn_backs)
    ImageButton imageBtnBacks;
    @BindView(R.id.bpb_main)
    ButtonProgressBar bpbMain;
    @BindView(R.id.et_password)
    EditText etPassword;
    private SmartLinkManipulator sm;
    private boolean isconncting = false;
    private int deviceType;
    private Timer timethread = new Timer();
    private String deviceMac;
    private boolean isAdd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlink);
        initEven();
        initData();
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        deviceType = bundle.getInt(Constants.DEVICE_TYPES);

    }

    private void initEven() {
        tvTitle.setText(R.string.Configurewifi);
        tvRight.setVisibility(View.GONE);
        tvWifiSSID.setText(getString(R.string.WifiSSID) + getSSid());
    }


    TimerTask task = new TimerTask() {
        public void run() {
            if (!isAdd) {
                scanDevice(deviceType, deviceMac);
            }
        }
    };

    private String getSSid() {
        @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo wi = wm.getConnectionInfo();
            if (wi != null) {
                String s = wi.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                } else {
                    return s;
                }
            }
        }
        return "";
    }

    SmartLinkManipulator.ConnectCallBack callback = new SmartLinkManipulator.ConnectCallBack() {

        @Override
        public void onConnectTimeOut() {
            // TODO Auto-generated method stub
            hand.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(SmartLinkActivity.this, "配置超时", Toast.LENGTH_SHORT).show();
//                    bpbMain.setText("开始链接");
                    isconncting = false;
                }
            });
        }

        @Override
        public void onConnect(final ModuleInfo mi) {
            // TODO Auto-generated method stub
            hand.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(SmartLinkActivity.this, "发现设备  " + mi.getMid() + "mac" + mi.getMac() + "IP" + mi.getModuleIP(), Toast.LENGTH_SHORT).show();
                    deviceMac = mi.getMac();
                    timethread.schedule(task, 0, 500); // 启动timer
                }
            });
        }

        @Override
        public void onConnectOk() {
            // TODO Auto-generated method stub
            hand.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(SmartLinkActivity.this, "配置完成", Toast.LENGTH_SHORT).show();
//                    bpbMain.setText("开始链接");
                    hand.sendEmptyMessage(2);
                    isconncting = false;
                }
            });
        }
    };

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
//                    bpbMain.setText("停止链接");
                    bpbMain.startLoader();
                    break;
                case 2:
//                    bpbMain.setText("开始链接");
                    if (timethread != null) {// 停止timer
                        timethread.cancel();
                        timethread = null;
                    }
                    sm.StopConnection();
                    bpbMain.stopLoader();
                    break;
                default:
                    break;
            }
        }
    };

    @OnClick({R.id.image_btn_backs, R.id.bpb_main})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_btn_backs:
                overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                this.finish();
                break;
            case R.id.bpb_main:

                if (!isconncting) {
                    isconncting = true;
                    sm = SmartLinkManipulator.getInstence();
                    //不管在不在发送 先停止发送  释放所有缓存
//					sm.StopConnection();

                    //再次获取实例 加载需要的信息
//					sm = SmartLinkManipulator.getInstence(MainActivity.this);

                    String ss = getSSid();
                    String ps = etPassword.getText().toString().trim();
                    hand.sendEmptyMessage(1);

                    //设置要配置的ssid 和pswd
                    try {
                        sm.setConnection(ss, ps, SmartLinkActivity.this);
                    } catch (SocketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //开始 smartLink
                    sm.Startconnection(callback);
                } else {
                    sm.StopConnection();
                    hand.sendEmptyMessage(2);
                    isconncting = false;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timethread != null) {// 停止timer
            timethread.cancel();
            timethread = null;
        }
    }

    private void connectDevice(XDevice xDevice) {
        XlinkAgent.getInstance().connectDevice(xDevice, xDevice.getAccessKey(), new ConnectDeviceListener() {
            @Override
            public void onConnectDevice(XDevice xDevice, int i) {
                String tips;
                String s = "";
                if (deviceType == Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN
                        || deviceType == Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN) {
                    s = Currency.Getbasiclnfo(MyApplication.getMyApplication().getUserInfo().getNickname());// String变量
                } else if (deviceType == Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY) {
                    s = ZigbeeGW.GetZigbeeGW(MyApplication.getMyApplication().getUserInfo().getNickname());// String变量
                    boolean isChoice = Hawk.contains(Constants.DEVICE_GW);
                    if (!isChoice) {
                        XlinkDevice xlinkDevices = Hawk.get(Constants.DEVICE_GW);
                        MainActivity.setChoiceGwDevice(xlinkDevices);
                    } else {
                        XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(xDevice.getMacAddress());
                        try {
                            MainActivity.setChoiceGwDevice(xlinkDevice);
                        } catch (Exception e) {
                        }
                    }

                } else if (deviceType == Constants.DEVICE_TYPE.DEVICE_WIFI_AIR) {
                    s = AirDetector.GetDeviceMessage(MyApplication.getMyApplication().getUserInfo().getNickname());// String变量
                } else if (deviceType == Constants.DEVICE_TYPE.DEVICE_WIFI_RC) {
                    s = Remote.GetDeviceMessage(MyApplication.getMyApplication().getUserInfo().getNickname());
                }
                byte b[] = s.getBytes();// String转换为byte[]
                switch (i) {
                    // 连接设备成功 设备处于内网
                    case XlinkCode.DEVICE_STATE_LOCAL_LINK:
                        // 连接设备成功，成功后
                        XlinkAgent.getInstance().sendProbe(xDevice);
                        sendData(b, xDevice);
                        break;
                    // 连接设备成功 设备处于云端
                    case XlinkCode.DEVICE_STATE_OUTER_LINK:
                        sendData(b, xDevice);
                        break;
                    // 设备授权码错误
                    case XlinkCode.CONNECT_DEVICE_INVALID_KEY:
                        openDevicePassword(xDevice);
                        break;
                }
            }
        });
    }

    public void sendData(final byte[] bs, XDevice xdevice) {
        XlinkAgent.getInstance().sendPipeData(xdevice, bs,
                new SendPipeListener() {
                    @Override
                    public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {

                    }
                });
    }

    private void scanDevice(final int type, final String deviceMac) {
        XlinkAgent.getInstance().scanDeviceByProductId(
                Utils.getPID(type), new ScanDeviceListener() {
                    @Override
                    public void onGotDeviceByScan(XDevice xDevice) {
                        if (xDevice.getMacAddress().equals(deviceMac)) {
                            HttpManage.getInstance().registerDevice(MyApplication.getMyApplication(), Utils.getPID(deviceType), xDevice.getMacAddress(), xDevice.getMacAddress(), new HttpManage.ResultCallback<String>() {
                                @Override
                                public void onError(Header[] headers, HttpManage.Error error) {

                                }

                                @Override
                                public void onSuccess(int code, String response) {

                                }
                            });
                            XlinkAgent.getInstance().initDevice(xDevice);
                            XlinkDevice xlinkDevice = new XlinkDevice();
                            xlinkDevice.setxDevice(XlinkAgent.deviceToJson(xDevice).toString());
                            xlinkDevice.setDeviceType(type);
                            xlinkDevice.setDeviceMac(xDevice.getMacAddress());
                            xlinkDevice.setDeviceName(xDevice.getMacAddress());
                            xlinkDevice.setDeviceId(xDevice.getDeviceId());
                            xlinkDevice.setDate(new Date());
                            xlinkDevice.setProductId(xDevice.getProductId());
                            xlinkDevice.setAccessKey(xDevice.getAccessKey() + "");
                            xlinkDevice.setDeviceState(1);
                            if (xDevice.getVersion() == 1) {
                                connectDevice(xDevice);
                                DeviceManage.getInstance().addDevice(xlinkDevice);
                                isAdd = true;
                            } else if (xDevice.getVersion() == 2) {
                                if (xDevice.getAccessKey() > 0) {
                                    connectDevice(xDevice);
                                    DeviceManage.getInstance().addDevice(xlinkDevice);
                                    isAdd = true;
                                } else {
                                    openDevicePassword(xDevice);
                                }
                            }
                            XlinkAgent.getInstance().subscribeDevice(xDevice, xDevice.getAccessKey(), null);
                        }
                        hand.sendEmptyMessage(2);
                    }
                });
    }

    private void openDevicePassword(XDevice xDevice) {
        int ret = 0;
        if (deviceType == Constants.DEVICE_TYPE.DEVICE_WIFI_PLUGIN) {
            ret = setDevicePassword(8888, xDevice);
        } else {
            ret = setDevicePassword(Constants.passwrods, xDevice);
        }
    }

    private int setDevicePassword(final int password, XDevice xDevice) {
        int ret = XlinkAgent.getInstance().setDeviceAccessKey(
                xDevice, password,
                new SetDeviceAccessKeyListener() {
                    @Override
                    public void onSetLocalDeviceAccessKey(XDevice xdevice,
                                                          int code, int msgId) {
                        switch (code) {
                            case XlinkCode.SUCCEED:
                                SUCCEED(xdevice, password);
                                break;
                            default:
                                break;
                        }
                    }
                });

        return ret;
    }

    private void SUCCEED(XDevice xd, int pwd) {
        XlinkDevice xlinkDevice = new XlinkDevice();
        xlinkDevice.setxDevice(XlinkAgent.deviceToJson(xd).toString());
        xlinkDevice.setAccessKey(pwd + "");
        xlinkDevice.setDeviceType(deviceType);
        xlinkDevice.setDeviceName(xd.getMacAddress());
        xlinkDevice.setDeviceMac(xd.getMacAddress());
        xlinkDevice.setDeviceId(xd.getDeviceId());
        xlinkDevice.setDate(new Date());
        xlinkDevice.setProductId(xd.getProductId());
        xlinkDevice.setDeviceState(1);
        DeviceManage.getInstance().addDevice(xlinkDevice);
        connectDevice(xd);
        XlinkAgent.getInstance().subscribeDevice(xd, pwd, null);
        isAdd = true;
    }

}
