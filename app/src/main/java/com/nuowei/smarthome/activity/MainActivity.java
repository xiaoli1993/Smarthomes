package com.nuowei.smarthome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
import com.jwkj.P2PListener;
import com.jwkj.SettingListener;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemMsg;
import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
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
import com.p2p.core.P2PHandler;
import com.p2p.core.global.Config;
import com.p2p.core.network.GetAccountInfoResult;
import com.p2p.core.network.MallUrlResult;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.SystemMessageResult;
import com.p2p.core.update.UpdateManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import it.sauronsoftware.base64.Base64;
import qiu.niorgai.StatusBarCompat;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 08:29
 * @Description :
 */
public class MainActivity extends BaseActivity {

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
    public Context mContext;
    boolean isRegFilter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        P2PHandler.getInstance().p2pInit(this, new P2PListener(), new SettingListener());
        setContentView(R.layout.activity_main);
        instance = this;
        mContext = this;
        if (!verifyLogin()) {
            Intent login = new Intent(mContext, LoginActivity.class);
            startActivity(login);
            finish();
        } else {
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
        }
//        FList.getInstance().searchLocalDevice();
    }

    private boolean verifyLogin() {
        Account activeUser = AccountPersist.getInstance().getActiveAccountInfo(
                mContext);

        if (activeUser != null && !activeUser.three_number.equals("0517401")) {
            NpcCommon.mThreeNum = activeUser.three_number;
            return true;
        }
        return false;
    }

    private void initIPC() {
        new FList();
        NpcCommon.verifyNetwork(mContext);
        regFilter();
        connect();
    }

    /**
     * 启动IPC服务
     */
    private void connect() {
        Intent service = new Intent(MyApplication.MAIN_SERVICE_START);
        service.setPackage(getPackageName());
        startService(service);
        if (!NpcCommon.mThreeNum.equals("0517401")) {
            new GetAccountInfoTask().execute();
        }
        getMallMsg();
        getHelpUrl();
    }

    public void getMallMsg() {
        String store_id = Config.AppConfig.store_id;
        if (store_id != null && !store_id.equals("") && !store_id.equals("0")) {
//			  new StoreIdTask(store_id).execute();
            new MallUrlTask(store_id).execute();
        }
//        判断当前账号是否登录过
        boolean islogin = DataManager.fingIsLoginByActiveUser(mContext, NpcCommon.mThreeNum);
        if (islogin == false) {
            if (store_id != null && !store_id.equals("") && !store_id.equals("0")) {
                new SystemTask(store_id, 2).execute();
                DataManager.insertIsLogin(mContext, NpcCommon.mThreeNum);
            }
        }
//		if(store_id!=null&&!store_id.equals("")){
//			new StartInfoTask(store_id).execute();
//		}
    }

    /**
     * 注册IPC所需广播
     */
    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(com.jwkj.global.Constants.Action.ACTION_NETWORK_CHANGE);
        filter.addAction(com.jwkj.global.Constants.Action.ACTION_SWITCH_USER);
        filter.addAction(com.jwkj.global.Constants.Action.ACTION_EXIT);
        filter.addAction(com.jwkj.global.Constants.Action.RECEIVE_MSG);
        filter.addAction(com.jwkj.global.Constants.Action.RECEIVE_SYS_MSG);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(com.jwkj.global.Constants.Action.SESSION_ID_ERROR);
        filter.addAction(com.jwkj.global.Constants.Action.EXITE_AP_MODE);
        filter.addAction(com.jwkj.global.Constants.P2P.RET_NEW_SYSTEM_MESSAGE);
        filter.addAction(com.jwkj.global.Constants.P2P.RET_GETFISHINFO);
        filter.addAction("DISCONNECT");
        // filter.addAction(Constants.Action.SETTING_WIFI_SUCCESS);
        this.registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
//            if (intent.getAction().equals(
//                    com.jwkj.global.Constants.Action.ACTION_NETWORK_CHANGE)) {
//                boolean isNetConnect = false;
//                ConnectivityManager connectivityManager = (ConnectivityManager) mContext
//                        .getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo activeNetInfo = connectivityManager
//                        .getActiveNetworkInfo();
//                Log.e("connectwifi", "--------");
//                if (activeNetInfo != null) {
//                    Log.e("connectwifi", "connect++++++");
//                    if (activeNetInfo.isConnected()) {
//                        Log.e("connectwifi", "connect++++++ok");
//                        isNetConnect = true;
//                        T.showShort(mContext,
//                                getString(R.string.message_net_connect)
//                                        + activeNetInfo.getTypeName());
//                        WifiManager wifimanager = (WifiManager) mContext
//                                .getSystemService(mContext.WIFI_SERVICE);
//                        if (wifimanager == null) {
//                            return;
//                        }
//                        WifiInfo wifiinfo = wifimanager.getConnectionInfo();
//                        if (wifiinfo == null) {
//                            return;
//                        }
//                        if (wifiinfo.getSSID() == null) {
//                            return;
//                        }
//                        if (wifiinfo.getSSID().length() > 0) {
//                            String wifiName = Utils.getWifiName(wifiinfo.getSSID());
//                            if (wifiName.startsWith(AppConfig.Relese.APTAG)) {
//                                String id = wifiName
//                                        .substring(AppConfig.Relese.APTAG.length());
////								APList.getInstance().gainDeviceMode(id);
////								FList.getInstance().gainDeviceMode(id);
//                                FList.getInstance().setIsConnectApWifi(id, true);
//                            } else {
//                                FList.getInstance().setAllApUnLink();
//                            }
//                        }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        Intent intentNew = new Intent();
//                        intentNew
//                                .setAction(com.jwkj.global.Constants.Action.NET_WORK_TYPE_CHANGE);
//                        mContext.sendBroadcast(intentNew);
//                        WifiUtils.getInstance().isApDevice();
//                    } else {
//                        T.showShort(mContext, getString(R.string.network_error)
//                                + " " + activeNetInfo.getTypeName());
//                    }
//
//                    if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                        NpcCommon.mNetWorkType = NpcCommon.NETWORK_TYPE.NETWORK_WIFI;
//                    } else {
//                        NpcCommon.mNetWorkType = NpcCommon.NETWORK_TYPE.NETWORK_2GOR3G;
//                    }
//                } else {
//                    Toast.makeText(mContext, getString(R.string.network_error),
//                            Toast.LENGTH_SHORT).show();
//                }
//
//                NpcCommon.setNetWorkState(isNetConnect);
//
//                // Intent intentNew = new Intent();
//                // intentNew.setAction(Constants.Action.NET_WORK_TYPE_CHANGE);
//                // mContext.sendBroadcast(intentNew);
//            } else if (intent.getAction().equals(
//                    com.jwkj.global.Constants.Action.ACTION_SWITCH_USER)) {
//                Account account = AccountPersist.getInstance()
//                        .getActiveAccountInfo(mContext);
//                if (!account.three_number.equals("0517401")) {
//                    new MainActivity.ExitTask(account).execute();
//                }
//                AccountPersist.getInstance().setActiveAccount(mContext,
//                        new Account());
//                NpcCommon.mThreeNum = "";
//                Intent i = new Intent(MyApplication.MAIN_SERVICE_START);
//                i.setPackage(getPackageName());
//                stopService(i);
//                dialog = new NormalDialog(mContext);
//                dialog.showLoadingDialog2();
//            } else if (intent.getAction().equals(
//                    com.jwkj.global.Constants.Action.SESSION_ID_ERROR)) {
//                Account account = AccountPersist.getInstance()
//                        .getActiveAccountInfo(mContext);
//                new MainActivity.ExitTask(account).execute();
//                AccountPersist.getInstance().setActiveAccount(mContext,
//                        new Account());
//                Intent i = new Intent(MyApplication.MAIN_SERVICE_START);
//                i.setPackage(getPackageName());
//                stopService(i);
//                Intent login = new Intent(mContext, LoginActivity.class);
//                startActivity(login);
//                finish();
//            } else if (intent.getAction().equals(com.jwkj.global.Constants.Action.ACTION_EXIT)) {
//                NormalDialog dialog = new NormalDialog(mContext, mContext
//                        .getResources().getString(R.string.exit), mContext
//                        .getResources().getString(R.string.confirm_exit),
//                        mContext.getResources().getString(R.string.exit),
//                        mContext.getResources().getString(R.string.cancel));
//                dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
//
//                    @Override
//                    public void onClick() {
//                        Intent i = new Intent(MyApplication.MAIN_SERVICE_START);
//                        i.setPackage(getPackageName());
//                        stopService(i);
//                        isGoExit(true);
//                        finish();
//                    }
//                });
//                dialog.showNormalDialog();
//            } else if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
//
//            } else if (intent.getAction().equals(com.jwkj.global.Constants.Action.RECEIVE_MSG)) {
//                int result = intent.getIntExtra("result", -1);
//                String msgFlag = intent.getStringExtra("msgFlag");
//
//                if (result == com.jwkj.global.Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
//                    DataManager.updateMessageStateByFlag(mContext, msgFlag,
//                            com.jwkj.global.Constants.MessageType.SEND_SUCCESS);
//                } else {
//                    DataManager.updateMessageStateByFlag(mContext, msgFlag,
//                            com.jwkj.global.Constants.MessageType.SEND_FAULT);
//                }
//
//            }else if (intent.getAction().equals(com.jwkj.global.Constants.P2P.RET_NEW_SYSTEM_MESSAGE)) {
//                Log.e("new_message", "new message comming----");
//                String iSystemMessageIndex = intent.getStringExtra("iSystemMessageIndex");
//                String sellerId = Config.AppConfig.store_id;
//                if (sellerId != null && !sellerId.equals("") && !sellerId.equals("0")) {
//                    String msgId = DataManager.findLastMsgIdSystemMessage(mContext, NpcCommon.mThreeNum);
//                    if (msgId == null || msgId.equals("")) {
//                        //数据库中没有的话，取最新的
//                        new MainActivity.SystemTask(sellerId, 2).execute();
//                        Log.e("new_message", "first new message----");
//                    } else {
//                        //数据库中有，Option 取值：0向后（下[比MsgIndex新的数据]）
//                        new MainActivity.GetNewSystemTaskByMsgID(sellerId, msgId, 0).execute();
//                        Log.e("new_message", "later new message----");
//                    }
//                }
//            } else if (intent.getAction().equals(com.jwkj.global.Constants.P2P.RET_GETFISHINFO)) {
//                String id = intent.getStringExtra("iSrcID");
//                byte[] data = intent.getByteArrayExtra("data");
//                try {
//                    String pre = Utils.getStringByByte(data, 2, 32);
//                    String recode = Utils.getStringByByte(data, 34, 32);
//                    short pos = MyUtils.byte2ToShort(data, 66);
//                    if (pre.contains("X")) {
//                        int w = Integer.parseInt(pre.split("X")[0]);
//                        int h = Integer.parseInt(pre.split("X")[1]);
//                        FList.getInstance().setVideowh(id, w, h, pos);
//                    }
//                } catch (Exception e) {
//
//                }
//
//            }
        }

    };

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
        if (isRegFilter) {
            isRegFilter = false;
            this.unregisterReceiver(mReceiver);
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


    class GetAccountInfoTask extends AsyncTask {

        public GetAccountInfoTask() {

        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            Utils.sleepThread(1000);
            Account account = AccountPersist.getInstance()
                    .getActiveAccountInfo(mContext);
            if (NpcCommon.mThreeNum == null || account == null) {
                return null;
            }
            return NetManager.getInstance(mContext).getAccountInfo(
                    NpcCommon.mThreeNum, account.sessionId);
        }

        @Override
        protected void onPostExecute(Object object) {
            if (object == null) {
                return;
            }
            GetAccountInfoResult result = NetManager
                    .createGetAccountInfoResult((JSONObject) object);
            if (Utils.isTostCmd(Integer.parseInt(result.error_code))) {
                T.showLong(mContext, Utils.GetToastCMDString(Integer.parseInt(result.error_code)));
                return;
            }
            switch (Integer.parseInt(result.error_code)) {
                case NetManager.SESSION_ID_ERROR:
                    Intent i = new Intent();
                    i.setAction(com.jwkj.global.Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(i);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new GetAccountInfoTask().execute();
                    return;
                case NetManager.GET_ACCOUNT_SUCCESS:
                    try {
                        String email = result.email;
                        String phone = result.phone;
                        Account account = AccountPersist.getInstance()
                                .getActiveAccountInfo(mContext);
                        if (null == account) {
                            account = new Account();
                        }
                        account.email = email;
                        account.phone = phone;
                        AccountPersist.getInstance().setActiveAccount(mContext,
                                account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    }

    class MallUrlTask extends AsyncTask {
        String sellerId;

        public MallUrlTask(String sellerId) {
            this.sellerId = sellerId;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            // TODO Auto-generated method stub
            Account account = AccountPersist.getInstance().getActiveAccountInfo(mContext);
            return NetManager.getInstance(mContext).getMallUrl(account.three_number, account.sessionId, sellerId);
        }

        @Override
        protected void onPostExecute(Object jObject) {
            // TODO Auto-generated method stub
            MallUrlResult result = NetManager.getInstance(mContext).getMallUrlResult((JSONObject) jObject);
            String error_code = result.error_code;
            Log.e("error_code", "error_code=" + error_code + "MallUrl");
            switch (Integer.parseInt(error_code)) {
                case NetManager.SESSION_ID_ERROR:
                    Intent i = new Intent();
                    i.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(i);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new MallUrlTask(sellerId).execute();
                    break;
                case NetManager.GET_START_LOGO_INFO:
                    String mall_url = Base64.decode(result.store_link);
                    Log.e("mall_url", "--" + mall_url + "--");
                    if (mall_url != null && !mall_url.equals("")) {
                        SharedPreferencesManager.getInstance().putMallUrl(mContext, mall_url);
                    }
                    break;
            }
        }
    }

    class SystemTask extends AsyncTask {
        String sellerId;
        int Option;

        public SystemTask(String sellerId, int Option) {
            this.sellerId = sellerId;
            this.Option = Option;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            // TODO Auto-generated method stub
            Account account;
            account = AccountPersist.getInstance().getActiveAccountInfo(mContext);
            Log.e("account", "account.three_number=" + account.three_number + "account.sessionId=" + account.sessionId);
            return NetManager.getInstance(mContext).getSystemMessage(account.three_number, account.sessionId, sellerId, 20, Option);
        }

        @Override
        protected void onPostExecute(Object object) {
            // TODO Auto-generated method stub
            SystemMessageResult result = NetManager.getInstance(mContext).GetSystemMessageResult((JSONObject) object);
            String error_code = result.error_code;
            String RecordCount = result.RecordCount;
            String Surplus = result.Surplus;
            String RecommendFlag = result.RecommendFlag;
            Log.e("system_mesg", "error_code=" + error_code + "-------------");
            Log.e("system_mesg", "RecordCount=" + RecordCount + "-------------");
            Log.e("system_mesg", "Surplus=" + Surplus + "-------------");
            Log.e("system_mesg", "RecommendFlag=" + RecommendFlag + "-------------");
            switch (Integer.parseInt(error_code)) {
                case NetManager.SESSION_ID_ERROR:
                    Intent i = new Intent();
                    i.setAction(Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(i);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new SystemTask(sellerId, Option).execute();
                    break;
                case NetManager.GET_SYSTEM_MESSAGE_SUCCESS:
                    Log.e("new_message", "first new message+++++++");
                    P2PHandler.getInstance().setSystemMessageIndex(com.jwkj.global.Constants.SystemMessgeType.MALL_NEW, Integer.parseInt(RecommendFlag));
                    final List<SystemMsg> msgs = new ArrayList<SystemMsg>();
                    List<SystemMessageResult.SystemMessage> lists = result.systemMessages;
                    String app_name = getResources().getString(R.string.app_name);
                    final String path = Environment.getExternalStorageDirectory().getPath() + "/" + Constants.CACHE_FOLDER_NAME;
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    for (int j = lists.size() - 1; j >= 0; j--) {
                        try {
                            SystemMsg msg = new SystemMsg();
                            msg.msgId = lists.get(j).msgId;
                            msg.title = Base64.decode(lists.get(j).title, "UTF-8");
                            msg.content = Base64.decode(lists.get(j).content, "UTF-8");
                            msg.time = Utils.ConvertTimeByLong(lists.get(j).time);
                            msg.pictrue_url = lists.get(j).picture_url;
                            msg.url = Base64.decode(lists.get(j).picture_in_url, "UTF-8");
                            msg.active_user = NpcCommon.mThreeNum;
                            msg.isRead = 0;
                            Log.e("system_message", "msg.msgId=" + msg.msgId + " " +
                                    "msg.title=" + msg.title + " " +
                                    "msg.content=" + msg.content + " " +
                                    "msg.time=" + msg.time + " " +
                                    "msg.pictrue=" + msg.pictrue + " " +
                                    "msg.url=" + msg.url + " " +
                                    "msg.active_user=" + msg.active_user + " ");
                            msgs.add(msg);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            for (final SystemMsg m : msgs) {
                                boolean contains = false;
                                List<SystemMsg> list = DataManager.findSystemMessgeByActiveUser(mContext, NpcCommon.mThreeNum);
                                for (SystemMsg s : list) {
                                    if (m.title.equals(s.title) && m.content.equals(s.content) && m.time.equals(s.time)) {
                                        contains = true;
                                        break;
                                    }
                                }
                                if (contains == false) {
                                    DataManager.insertSystemMessage(mContext, m);
                                    ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener() {
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            saveSystemMessagePictrue(loadedImage, path, m.msgId);
                                        }

                                        ;
                                    });
                                }
                            }
                            Intent it = new Intent();
                            it.setAction(com.jwkj.global.Constants.Action.REFRESH_SYSTEM_MESSAGE);
                            sendBroadcast(it);
                        }
                    }).start();
                    if (Integer.parseInt(Surplus) > 0) {
                        String messageId = msgs.get(0).msgId;
                        new GetOldSystemTaskByMsgID(sellerId, messageId, 1);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    class GetOldSystemTaskByMsgID extends AsyncTask {
        String sellerId;
        String msgId;
        int Option;

        public GetOldSystemTaskByMsgID(String sellerId, String msgId, int Option) {
            this.sellerId = sellerId;
            this.msgId = msgId;
            this.Option = Option;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            // TODO Auto-generated method stub
            Account account;
            account = AccountPersist.getInstance().getActiveAccountInfo(mContext);
            Log.e("account", "account.three_number=" + account.three_number + "account.sessionId=" + account.sessionId);
            return NetManager.getInstance(mContext).getSystemMessageByMsgId(account.three_number, account.sessionId, sellerId, msgId, 100, Option);
        }

        @Override
        protected void onPostExecute(Object object) {
            // TODO Auto-generated method stub
            SystemMessageResult result = NetManager.getInstance(mContext).GetSystemMessageResult((JSONObject) object);
            String error_code = result.error_code;
            String RecordCount = result.RecordCount;
            String Surplus = result.Surplus;
            String RecommendFlag = result.RecommendFlag;
            Log.e("system_mesg", "error_code=" + error_code + "-------------");
            Log.e("system_mesg", "RecordCount=" + RecordCount + "-------------");
            Log.e("system_mesg", "Surplus=" + Surplus + "-------------");
            Log.e("system_mesg", "RecommendFlag=" + RecommendFlag + "-------------");
            switch (Integer.parseInt(error_code)) {
                case NetManager.SESSION_ID_ERROR:
                    Intent i = new Intent();
                    i.setAction(com.jwkj.global.Constants.Action.SESSION_ID_ERROR);
                    MyApplication.app.sendBroadcast(i);
                    break;
                case NetManager.CONNECT_CHANGE:
                    new GetOldSystemTaskByMsgID(sellerId, msgId, Option);
                    break;
                case NetManager.GET_SYSTEM_MESSAGE_SUCCESS:
                    final List<SystemMsg> msgs = new ArrayList<SystemMsg>();
                    List<SystemMessageResult.SystemMessage> lists = result.systemMessages;
                    String app_name = getResources().getString(R.string.app_name);
                    final String path = Environment.getExternalStorageDirectory().getPath() + "/" + com.jwkj.global.Constants.CACHE_FOLDER_NAME;
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    for (int j = lists.size() - 1; j >= 0; j--) {
                        try {
                            SystemMsg msg = new SystemMsg();
                            msg.msgId = lists.get(j).msgId;
                            msg.title = Base64.decode(lists.get(j).title, "UTF-8");
                            msg.content = Base64.decode(lists.get(j).content, "UTF-8");
                            msg.time = Utils.ConvertTimeByLong(lists.get(j).time);
                            msg.pictrue_url = lists.get(j).picture_url;
                            msg.url = Base64.decode(lists.get(j).picture_in_url, "UTF-8");
                            msg.active_user = NpcCommon.mThreeNum;
                            msg.isRead = 0;
                            msgs.add(msg);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            for (final SystemMsg m : msgs) {
                                boolean contains = false;
                                List<SystemMsg> list = DataManager.findSystemMessgeByActiveUser(mContext, NpcCommon.mThreeNum);
                                for (SystemMsg s : list) {
                                    if (m.title.equals(s.title) && m.content.equals(s.content) && m.time.equals(s.time)) {
                                        contains = true;
                                        break;
                                    }
                                }
                                if (contains == false) {
                                    DataManager.insertSystemMessage(mContext, m);
                                    ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener() {
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            saveSystemMessagePictrue(loadedImage, path, m.msgId);
                                        }

                                        ;
                                    });
                                }
                            }
                            Intent it = new Intent();
                            it.setAction(com.jwkj.global.Constants.Action.REFRESH_SYSTEM_MESSAGE);
                            sendBroadcast(it);
                        }
                    }).start();
                    if (Integer.parseInt(Surplus) > 0) {
                        String messageId = msgs.get(0).msgId;
                        new GetOldSystemTaskByMsgID(sellerId, messageId, 1);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    public void saveSystemMessagePictrue(Bitmap bitamap, String path, String fileName) {
        Bitmap bitmap = bitamap;
        String picture_path = path + "/" + fileName + ".jpg";  // 这个就是你存放的路径了。
        File bitmapFile = new File(picture_path);
        FileOutputStream fos = null;
        if (!bitmapFile.exists()) {
            try {
                bitmapFile.createNewFile();
                fos = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                fos = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getHelpUrl() {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    String[] url = UpdateManager.getInstance().getHelpUrl();
                    if (url[0] != null && !url[0].equals("")) {
                        SharedPreferencesManager.getInstance().putHelpUrl(mContext, url[0]);
                        if (url[1] != null && !url[1].equals("")) {
                            SharedPreferencesManager.getInstance().putHelpIndexUrl(mContext, Integer.parseInt(url[1]));
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }.start();
    }
}