package com.nuowei.smarthome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.feedback.FeedbackAgent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.SmartHomeServer;
import com.nuowei.smarthome.adapter.MainLeftAdapter;
import com.nuowei.smarthome.fragment.MainGridFragment;
import com.nuowei.smarthome.fragment.MainListTFragment;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.modle.LeftMain;
import com.nuowei.smarthome.modle.Messages;
import com.nuowei.smarthome.modle.MessagesContent;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.util.GlideCircleTransform;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.util.Time;
import com.orhanobut.hawk.Hawk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;
import qiu.niorgai.StatusBarCompat;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 08:29
 * @Description :
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.left_listview)
    ListView listView;
    @BindView(R.id.iv_Avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_nikeName)
    TextView tvNikeName;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.left)
    RelativeLayout left;
    @BindView(R.id.image_list)
    ImageView imageList;
    @BindView(R.id.image_grid)
    ImageView imageGrid;
    @BindView(R.id.tv_list)
    TextView tvList;
    @BindView(R.id.tv_grid)
    TextView tvGrid;

    private List<LeftMain> list;

    private HashMap<Integer, Fragment> fragments = new HashMap<>();
    private MainLeftAdapter adapter;
    private boolean isList;
    public String userName;
    private static final int GET_MESSAGE_TIMER = 5;
    private static final int GET_MESSAGE_HAND = 6;

    private final Timer timer = new Timer();
    private TimerTask task;
    private boolean isRunTask = true;
    private Fragment mTab01;
    private Fragment mTab02;
    private FeedbackAgent feedbackeAgent;

    public static XlinkDevice choiceGwDevice;
    public static MainActivity instance = null;

    public static int defence = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        P2PHandler.getInstance().p2pInit(this, new P2PListener(),new SettingListener());
        setContentView(R.layout.activity_main);
        instance = this;
        initXlink();
        initData();
        initEven();
        initDevice();
        isRegisterBroadcast = true;
        registerReceiver(mBroadcastReceiver, MyUtil.regFilter());
        startCustomService();
        MyApplication.getMyApplication().setCurrentActivity(this);
        initFeedBack();
        initIPC();
//        FList.getInstance().searchLocalDevice();
    }


    private void initIPC() {
//        P2PHandler.getInstance().p2pInit(this, new P2PListener(), new SettingListener());
    }

    @OnClick(R.id.iv_Avatar)
    void openNews() {
        startActivity(new Intent(MainActivity.this, PersonalActivity.class));
    }

    private void initFeedBack() {
        feedbackeAgent = new FeedbackAgent(MainActivity.this);
        feedbackeAgent.sync();
    }


    public void openDrawers() {
        drawerLayout.openDrawer(left);
    }

    // 启动服务
    private void startCustomService() {
        Intent intent = new Intent(this, SmartHomeServer.class);
        startService(intent);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            String macs = intent.getStringExtra(Constants.DEVICE_MAC);
            // 收到pipe包
            if (action.equals(Constants.BROADCAST_DEVICE_SYNC)) {
                String data = intent.getStringExtra(Constants.DATA);
            } else if (action.equals(Constants.BROADCAST_RECVPIPE_SYNC)) {
                String data = intent.getStringExtra(Constants.DATA);
            } else if (action.equals(Constants.BROADCAST_DEVICE_CHANGED)) {

            } else if (action.equals(Constants.BROADCAST_CONNENCT_SUCCESS)) {

            } else if (action.equals(Constants.BROADCAST_CONNENCT_FAIL)) {

            } else if (action.equals(Constants.BROADCAST_SEND_OVERTIME)) {

            } else if (action.equals(Constants.BROADCAST_SEND_SUCCESS)) {

            } else if (action.equals(Constants.CHANGE_URL)) {
                Glide.with(MainActivity.this).load(MyApplication.getMyApplication().getUserInfo().getAvatar())
                        .centerCrop()
                        .dontAnimate()
                        .priority(Priority.NORMAL)
                        .placeholder(R.drawable.log_icon)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivAvatar);
            }
        }
    };

    private void initXlink() {
        //SDK >= 21时, 取消状态栏的阴影
        StatusBarCompat.translucentStatusBar(MainActivity.this, false);
        ButterKnife.bind(this);
        userName = Hawk.get("MY_ACCOUNT");
        boolean isLists = Hawk.contains(Constants.ISLIST);
        if (isLists) {
            isList = Hawk.get(Constants.ISLIST, false);
            initFragments(isList);
//            setSelect(0);
        } else {
            initFragments(false);
//            setSelect(1);
        }
        if (!XlinkAgent.getInstance().isConnectedLocal()) {
            MyApplication.getLogger().i("启动Xlink");
            XlinkAgent.getInstance().start();
        }
        if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
            MyApplication.getLogger().i("登录Xlink");
            XlinkAgent.getInstance().login(MyApplication.getMyApplication().getAppid(),
                    MyApplication.getMyApplication().getAuthKey());
        }
        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (isRunTask) {
                    mHandler.sendEmptyMessage(GET_MESSAGE_TIMER);
                }
            }
        };
    }

    @OnClick({R.id.ll_list, R.id.ll_Grid})
    void changeList(View view) {
        switch (view.getId()) {
            case R.id.ll_list:
                isList = true;
                Hawk.put(Constants.ISLIST, isList);
                setIndexSelected(0);
//                initFragment(isList);
                drawerLayout.closeDrawer(left);
                imageList.setImageResource(R.drawable.main_right_list_pressed);
                imageGrid.setImageResource(R.drawable.main_right_lattice_normal);
                tvList.setTextColor(getResources().getColor(R.color.text_o));
                tvGrid.setTextColor(getResources().getColor(R.color.text_title));
//                setSelect(0);
                break;
            case R.id.ll_Grid:
                isList = false;
                Hawk.put(Constants.ISLIST, isList);
                setIndexSelected(1);
//                initFragment(isList);
                drawerLayout.closeDrawer(left);
                imageList.setImageResource(R.drawable.main_right_list_normal);
                imageGrid.setImageResource(R.drawable.main_right_lattice_pressed);
                tvGrid.setTextColor(getResources().getColor(R.color.text_o));
                tvList.setTextColor(getResources().getColor(R.color.text_title));
//                setSelect(1);
                break;
        }
    }

    private void initEven() {

        adapter = new MainLeftAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, DeviceListActivity.class));
                        break;
                    case 1:
                        Intent intent = new Intent(MainActivity.this, ShareDeviceActivity.class);
                        //用Bundle携带数据
                        Bundle bundle = new Bundle();
                        //传递name参数为tinyphp
                        bundle.putString(Constants.GATEWAY_MAC, getChoiceGwDevice().getDeviceMac());
                        intent.putExtras(bundle);
                        startActivity(intent);
//                        startActivity(new Intent(MainActivity.this, ShareDeviceActivity.class));
                        break;
                    case 2:
//                        feedbackeAgent.startDefaultThreadActivity();
                        startActivity(new Intent(MainActivity.this, ThreadActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        break;
                }
            }
        });
        if (!MyUtil.isEmptyString(MyApplication.getMyApplication().getUserInfo().getAvatar())) {
            Glide.with(this).load(MyApplication.getMyApplication().getUserInfo().getAvatar())
                    .centerCrop()
                    .dontAnimate()
                    .priority(Priority.NORMAL)
                    .placeholder(R.drawable.log_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideCircleTransform(this))
                    .into(ivAvatar);
        }

        if (!MyUtil.isEmptyString(MyApplication.getMyApplication().getUserInfo().getNickname())) {
            tvNikeName.setText(MyApplication.getMyApplication().getUserInfo().getNickname());
        }

    }

    private void initData() {
        list = new ArrayList<LeftMain>();

        list.add(new LeftMain(R.drawable.main_right_device, getResources().getString(R.string.Device), 0));
        list.add(new LeftMain(R.drawable.main_right_share, getResources().getString(R.string.Share_Device), 0));
        list.add(new LeftMain(R.drawable.main_right_feeback, getResources().getString(R.string.Feedback), 0));
        list.add(new LeftMain(R.drawable.main_right_about, getResources().getString(R.string.About), 0));
        list.add(new LeftMain(R.drawable.main_right_setting, getResources().getString(R.string.Setting), 0));

    }

    private Fragment[] mFragments;

    private int mIndex = 2;

    private void initFragments(boolean isList) {
        MainListTFragment homeFragment = new MainListTFragment();
        MainListTFragment homeFragment1 = new MainListTFragment();
        MainGridFragment shopcartFragment = new MainGridFragment();
        //添加到数组
        mFragments = new Fragment[]{homeFragment, shopcartFragment, homeFragment1};
        //开启事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_layout, homeFragment1).commit();
//        ft.add(R.id.fragment_layout, shopcartFragment).commit();
        setIndexSelected(2);
        if (isList) {
            //默认设置为第0个
            setIndexSelected(0);
            imageList.setImageResource(R.drawable.main_right_list_pressed);
            imageGrid.setImageResource(R.drawable.main_right_lattice_normal);
            tvList.setTextColor(getResources().getColor(R.color.text_o));
            tvGrid.setTextColor(getResources().getColor(R.color.text_title));
        } else {
//            ft.add(R.id.fragment_layout, shopcartFragment).commit();
            //默认设置为第0个
            setIndexSelected(1);
            imageList.setImageResource(R.drawable.main_right_list_normal);
            imageGrid.setImageResource(R.drawable.main_right_lattice_pressed);
            tvGrid.setTextColor(getResources().getColor(R.color.text_o));
            tvList.setTextColor(getResources().getColor(R.color.text_title));
        }

    }


    private void setIndexSelected(int index) {

        if (mIndex == index) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //隐藏
        ft.hide(mFragments[mIndex]);
        //判断是否添加
        if (!mFragments[index].isAdded()) {
            ft.add(R.id.fragment_layout, mFragments[index]).show(mFragments[index]);
        } else {
            ft.show(mFragments[index]);
        }
        ft.commit();
        //再次赋值
        mIndex = index;

    }


//    Fragment fragment = null;
//
//    private void initFragment(boolean isList) {
//        if (isList) {
//            fragment = new MainListTFragment();
//            imageList.setImageResource(R.drawable.main_right_list_pressed);
//            imageGrid.setImageResource(R.drawable.main_right_lattice_normal);
//            tvList.setTextColor(getResources().getColor(R.color.text_o));
//            tvGrid.setTextColor(getResources().getColor(R.color.text_title));
//        } else {
//            fragment = new MainGridFragment();
//            imageList.setImageResource(R.drawable.main_right_list_normal);
//            imageGrid.setImageResource(R.drawable.main_right_lattice_pressed);
//            tvGrid.setTextColor(getResources().getColor(R.color.text_o));
//            tvList.setTextColor(getResources().getColor(R.color.text_title));
//        }
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_layout, fragment)
//                .addToBackStack(null)
//                .commit();
//    }


    private boolean isRegisterBroadcast = false;

    @Override
    protected void onDestroy() {
        if (isRegisterBroadcast) {
            unregisterReceiver(mBroadcastReceiver);
        }
        timer.cancel();
        super.onDestroy();
    }


    private String createDate;
    private String queryCreate = "$gte";
    private boolean isStart = true;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    final List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
                    List<XlinkDevice> gwXlink = new ArrayList<XlinkDevice>();
                    for (int i = 0; i < xlinkDeviceList.size(); i++) {
                        if (xlinkDeviceList.get(i).getDeviceType() == Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY) {
                            gwXlink.add(xlinkDeviceList.get(i));
                            final String getSub = ZigbeeGW.GetSubDevice(userName);
                            if (xlinkDeviceList.get(i).getDeviceState() == 0) {
                                XlinkAgent.getInstance().connectDevice(xlinkDeviceList.get(i).getxDevice(), xlinkDeviceList.get(i).getAccessKey(), new ConnectDeviceListener() {
                                    @Override
                                    public void onConnectDevice(XDevice xDevice, int ret) {
                                        if (ret < 0) {
                                            MyApplication.getLogger().i("连接失败:" + ret);
                                        } else {
                                            MyApplication.getLogger().i("连接成功:" + ret);
                                            XlinkAgent.getInstance().sendPipeData(xDevice, getSub.getBytes(), new SendPipeListener() {
                                                @Override
                                                public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                                    JSONObject object = XlinkAgent.deviceToJson(xDevice);
                                                    MyApplication.getLogger().i("发送:" + i + "\n" + getSub);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                int sendRet = XlinkAgent.getInstance().sendPipeData(xlinkDeviceList.get(i).getxDevice(), getSub.getBytes(), null);
                                if (sendRet < 0) {
                                    MyApplication.getLogger().i("发送失败:" + sendRet + "\n" + getSub);
                                } else {
                                    MyApplication.getLogger().i("发送成功！");
                                }
                            }
                        }
                    }
                    timer.schedule(task, 500, 1000 * 10);
                    boolean isChoice = Hawk.contains(Constants.DEVICE_GW);
                    if (isChoice) {
                        XlinkDevice xlinkDevices = Hawk.get(Constants.DEVICE_GW);
                        setChoiceGwDevice(xlinkDevices);
                        setDefence(xlinkDevices.getDefence());
                    } else {
                        try {
                            setChoiceGwDevice(gwXlink.get(0));
                            setDefence(gwXlink.get(0).getDefence());
                        } catch (Exception e) {
                        }
                    }

                    break;
                case GET_MESSAGE_TIMER:
                    boolean isFirstAdd = Hawk.contains(userName + "_isFirstAdd");
                    if (isRefreshMessage) {
                        if (!isFirstAdd) {
                            MAX_Refresh = 3000;
                            MAX_LIMIT = 0;
                            isStart = false;
                            createDate = DataSupport.select("createDate").where("userName = ?", userName).max(DataDevice.class, "createDate", String.class);
                            if (MyUtil.isEmptyString(createDate)) {
                                queryCreate = "$gte";
                            } else {
                                createDate = DataSupport.select("createDate").where("userName = ?", userName).min(DataDevice.class, "createDate", String.class);
                                queryCreate = "$lt";
                            }
                        } else {
                            MAX_Refresh = 100;
                            MAX_LIMIT = 0;
                            queryCreate = "$gte";
                            isStart = true;
                            createDate = DataSupport.select("createDate").where("userName = ?", userName).max(DataDevice.class, "createDate", String.class);
                        }
                    }
                    getMessage(createDate, isStart, queryCreate);
                    break;
            }
        }
    };

    private void initDevice() {

        HttpManage.getInstance().getSubscribe(MyApplication.getMyApplication(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                MyApplication.getLogger().e("获取订阅设备出错:" + error.getMsg() + "\t" + error.getCode());
            }

            @Override
            public void onSuccess(int code, String response) {
                MyApplication.getLogger().json(response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray list = object.getJSONArray("list");
                    int iSize = list.length();
                    for (int i = 0; i < iSize; i++) {
                        JSONObject jsonObj = list.getJSONObject(i);
                        MyUtil.subscribeToDevice(jsonObj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    int MAX_LIMIT = 0;
    int MAX_Refresh = 100;
    public boolean isRefreshMessage = true;

    private List<DataDevice> datalist = new ArrayList<DataDevice>();

    private void getMessage(final String CreateDate, final boolean isStart, String queryCreate) {
        MyApplication.getLogger().i("进入MAX_LIMIT:" + MAX_LIMIT + "\t" + "MAX_Refresh:" + MAX_Refresh + "\t" + "CreateDate:" + CreateDate + "\tisStart:" + isStart);
        try {
            JSONArray deviceid = MyUtil.getdeviceid();

            if (deviceid.length() > 0) {
                HttpManage.getInstance().GetMessagesID(MyApplication.getMyApplication(), MAX_LIMIT + "", MAX_Refresh + "", deviceid, queryCreate, CreateDate, new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {
                        MyApplication.getLogger().e("获取消息失败:" + error.getMsg() + "\t" + error.getCode());
                        try {
                            DataSupport.saveAll(datalist);
                        } catch (Exception e) {

                        }

                        isRefreshMessage = true;
                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        final Gson gaon = new Gson();
                        Messages messages = gaon.fromJson(response, Messages.class);
                        if (isStart) {
                            MAX_LIMIT += MAX_Refresh;
                            MAX_Refresh = 30;
                        } else {
                            MAX_LIMIT += MAX_Refresh;
                            if (messages.getCount() > 10000) {
                                MAX_Refresh = 10000;
                            } else {
                                MAX_Refresh = messages.getCount();
                            }
                        }
                        final List<Messages.ListBean> listsize = messages.getList();
                        final int iSize = listsize.size();
                        if (iSize == 0) {
                            isRefreshMessage = true;
                            Hawk.put(userName + "_isFirstAdd", true);
                            return;
                        }
                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < iSize; i++) {
                                    DataDevice datadevice = new DataDevice();
                                    datadevice.setMessageID(listsize.get(i).getId());
                                    datadevice.setMessageType(listsize.get(i).getType());
                                    datadevice.setNotifyType(listsize.get(i).getNotify_type());
                                    datadevice.setPush(listsize.get(i).isIs_push());
                                    datadevice.setRead(listsize.get(i).isIs_read());
                                    datadevice.setAlertName(listsize.get(i).getAlert_name());
                                    datadevice.setAlertValue(listsize.get(i).getAlert_value());
                                    datadevice.setCreateDate(listsize.get(i).getCreate_date());
                                    datadevice.setUserid(MyApplication.getMyApplication().getAppid() + "");
                                    datadevice.setUserName(userName);
                                    try {
                                        MessagesContent messagesconte = gaon.fromJson(listsize.get(i).getContent(), MessagesContent.class);
                                        XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(listsize.get(i).getFrom());
                                        datadevice.setXlinkDevice(xlinkDevice);
                                        datadevice.setDeviceId(listsize.get(i).getFrom() + "");
                                        datadevice.setDeviceMac(xlinkDevice.getDeviceMac());
                                        if (MyUtil.isEmptyString(messagesconte.getPlugMac())) {
//                                SubDevice subDevice = SubDeviceManage.getInstance().getDevice(xlinkDevice.getDeviceMac(), messagesconte.getZigbeeMac());
//                                datadevice.setSubID(subDevice.getIndex() + "");
//                                datadevice.setSubDevice(subDevice);
                                            String timer = messagesconte.getNotification().getBody_loc_args().get(0);
                                            datadevice.setActionName(messagesconte.getNotification().getTitle());

                                            try {
                                                Date date = Time.stringToDate(timer, "yyyy-MM-dd HH:mm:ss");
                                                datadevice.setDate(date);
                                                datadevice.setYear(Time.dateToString(date, "yyyy"));
                                                datadevice.setMonth(Time.dateToString(date, "MM"));
                                                datadevice.setDay(Time.dateToString(date, "dd"));
                                                datadevice.setHH(Time.dateToString(date, "HH"));
                                                datadevice.setMm(Time.dateToString(date, "mm"));
                                                datadevice.setSs(Time.dateToString(date, "ss"));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String loc_key = messagesconte.getNotification().getBody_loc_key();
                                            datadevice.setBodyLocKey(loc_key);
                                            datadevice.setSubType(MyUtil.Gettype(loc_key) + "");
                                            if (!MyUtil.isEmptyString(messagesconte.getZigbeeMac())) {
                                                datadevice.setSubMac(messagesconte.getZigbeeMac());
                                            }
                                            if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP) {
                                                try {
                                                    datadevice.setHumidity(messagesconte.getNotification().getBody_loc_args().get(1));
                                                } catch (Exception e) {
                                                }
                                                try {
                                                    datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(2));
                                                } catch (Exception e) {
                                                }
                                            } else if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_WIFI_METRTING_PLUGIN) {
                                                try {
                                                    datadevice.setHumidity(messagesconte.getNotification().getBody_loc_args().get(1));
                                                } catch (Exception e) {
                                                }
                                                try {
                                                    if (messagesconte.getNotification().getBody_loc_args().get(2).equals("NA")) {
                                                        datadevice.setHumidity(messagesconte.getNotification().getBody_loc_args().get(1) + "NA");
                                                    }
                                                } catch (Exception e) {
                                                }
                                                try {
                                                    datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(3));
                                                } catch (Exception e) {
                                                }
                                            } else if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_WIFI_AIR) {
                                                try {
                                                    datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(1).replaceAll(" ", ""));
                                                } catch (Exception e) {
                                                }
                                            } else if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_WIFI_GAS) {
                                                try {
                                                    datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(1));
                                                } catch (Exception e) {
                                                }
                                            } else if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_WIFI_RC) {
                                                datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(1));

                                            }

                                        } else {
                                            if (!MyUtil.isEmptyString(messagesconte.getMessage().getElectricity())) {
                                                datadevice.setElectricity(messagesconte.getMessage().getElectricity());
                                                datadevice.setActionName("electricity_1042");
                                            } else {
                                                datadevice.setActionName("onoff__1042");

                                                if (!MyUtil.isEmptyString(messagesconte.getMessage().getSID())) {

                                                    if (messagesconte.getMessage().isOnoff()) {
                                                        datadevice.setBodyLocKey(messagesconte.getMessage().getSID() + "_true");
                                                    } else {
                                                        datadevice.setBodyLocKey(messagesconte.getMessage().getSID() + "_false");
                                                    }
                                                } else {
                                                    if (messagesconte.getMessage().isOnoff()) {
                                                        datadevice.setBodyLocKey("true");
                                                    } else {
                                                        datadevice.setBodyLocKey("false");
                                                    }
                                                }
                                            }
                                            String timer = messagesconte.getMessage().getTime();
//                                        MyApplication.getLogger().i(timer);
                                            try {
                                                Date date = Time.stringToDate(timer, "yyyy-MM-dd HH:mm:ss");
                                                datadevice.setDate(date);
                                                datadevice.setYear(Time.dateToString(date, "yyyy"));
                                                datadevice.setMonth(Time.dateToString(date, "MM"));
                                                datadevice.setDay(Time.dateToString(date, "dd"));
                                                datadevice.setHH(Time.dateToString(date, "HH"));
                                                datadevice.setMm(Time.dateToString(date, "mm"));
                                                datadevice.setSs(Time.dateToString(date, "ss"));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    } catch (Exception e) {
                                    }
                                    boolean isFirst = Hawk.contains(userName + "_isFirstAdd");
                                    if (isFirst) {
                                        if (!MyUtil.isEmptyString(CreateDate)) {
                                            if (CreateDate.equals(listsize.get(i).getCreate_date())) {
                                                isRefreshMessage = true;
                                                if (!MyUtil.isEmptyList(datalist)) {
                                                    DataSupport.saveAllAsync(datalist);
                                                }
                                                return;
                                            } else {
                                                isRefreshMessage = false;
                                                datalist.add(datadevice);
                                            }
                                        }
                                    } else {
                                        isRefreshMessage = false;
                                        datalist.add(datadevice);
                                    }
                                }
                                if (!MyUtil.isEmptyList(datalist)) {
                                    DataSupport.saveAllAsync(datalist);
                                }
                            }
                        });
                        thread.start();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static XlinkDevice getChoiceGwDevice() {
        return choiceGwDevice;
    }

    public static void setChoiceGwDevice(XlinkDevice choiceGwDevice) {
        MainActivity.choiceGwDevice = choiceGwDevice;
    }

    public static int getDefence() {
        return defence;
    }

    public static void setDefence(int defence) {
        MainActivity.defence = defence;
        XlinkDevice xlinkDevice = getChoiceGwDevice();
        xlinkDevice.setDefence(defence);
        DeviceManage.getInstance().addDevice(xlinkDevice);
    }
}