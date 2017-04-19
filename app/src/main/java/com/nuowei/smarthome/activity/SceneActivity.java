package com.nuowei.smarthome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.SceneAdapter;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.modle.Scene;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.SendPipeListener;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/3/31 11:37
 * @Description :
 */
public class SceneActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.refreshListRecyclerView)
    FamiliarRefreshRecyclerView refreshListRecyclerView;

    private FamiliarRecyclerView mFamiliarRecyclerView;
    private static ConcurrentHashMap<String, Scene> frdMap = new ConcurrentHashMap<String, Scene>();
    private boolean isRegisterBroadcast = false;
    private SceneAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        initData();
        initEven();
        isRegisterBroadcast = true;
        registerReceiver(mBroadcastReceiver, MyUtil.regFilter());
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
                addScene(macs, data);
            } else if (action.equals(Constants.BROADCAST_RECVPIPE_SYNC)) {
                String data = intent.getStringExtra(Constants.DATA);
                addScene(macs, data);
            } else if (action.equals(Constants.BROADCAST_DEVICE_CHANGED)) {

            } else if (action.equals(Constants.BROADCAST_CONNENCT_SUCCESS)) {

            } else if (action.equals(Constants.BROADCAST_CONNENCT_FAIL)) {

            } else if (action.equals(Constants.BROADCAST_SEND_OVERTIME)) {

            } else if (action.equals(Constants.BROADCAST_SEND_SUCCESS)) {

            }
        }
    };

    private void addScene(String macs, String data) {
        Gson gson = new Gson();
        Scene scene = gson.fromJson(data, Scene.class);
        scene.setGwMac(macs);
        MyApplication.getLogger().json(data);
//        addScene(scene);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterBroadcast) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    private void initData() {
        upDateList();
    }

    private void upDateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<XlinkDevice> arraylist = DeviceManage.getInstance().getDevices();
                for (int i = 0; i < arraylist.size(); i++) {
                    if (arraylist.get(i).getDeviceType() == Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY) {
                        String Include = ZigbeeGW.GetScene(MyApplication.getMyApplication().getUserInfo().getNickname());
                        MyApplication.getLogger().json(Include);
                        XlinkAgent.getInstance().sendPipeData(arraylist.get(i).getxDevice(), Include.getBytes(), new SendPipeListener() {
                            @Override
                            public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                MyApplication.getLogger().i("发送:" + i + "\n" + xDevice.getMacAddress());
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void initEven() {
        tvTitle.setText(getResources().getString(R.string.Scene));
        tvRight.setVisibility(View.GONE);
        btnRight.setVisibility(View.VISIBLE);

        mAdapter = new SceneAdapter(getScene());


        refreshListRecyclerView.setLoadMoreEnabled(false);
        mFamiliarRecyclerView = refreshListRecyclerView.getFamiliarRecyclerView();
        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        refreshListRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {

//                Scene.PLBean.SceneBean scenes = item.getPL().getScene().get(0);
//                Bundle b = new Bundle();
//                b.putSerializable(Constants.DEVICE_MAC, item.getGwMac());
//                b.putSerializable(Constants.Scene_sceneID, scenes.getSceneID());
//                Intent intent = new Intent(SceneActivity.this, SceneAddActivity.class);
//                intent.putExtras(b);
//                intent.setClass(SceneActivity.this, SceneAddActivity.class);
//                startActivity(intent);
            }
        });
        refreshListRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        upDateList();
                        refreshListRecyclerView.pullRefreshComplete();
                    }
                }, 2000);
            }
        });
//        refreshListRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }

    @OnClick(R.id.btn_right)
    public void onViewClicked() {

    }

    public static ArrayList<Scene> listDev = new ArrayList<Scene>();

    public static synchronized ArrayList<Scene> getScene() {
        listDev.clear();
        Iterator<Map.Entry<String, Scene>> iter = frdMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Scene> entry = iter.next();
            listDev.add(entry.getValue());
        }
//        Collections.sort(listDev, new Comparator<Scene>() {
//
//            @Override
//            public int compare(Scene lhs, Scene rhs) {
//                int type1 = lhs.getPL().getScene().get(0).getSceneID();
//                int type2 = rhs.getPL().getScene().get(0).getSceneID();
//                if (!lhs.getPL().getScene().get(0).getGW_name().equals(rhs.getGW_name())) {
//                    return lhs.getGW_name().compareTo(rhs.getGW_name());
//                } else if (type1 != type2) {
//                    return type1 - type2;
//                } else {
//                    return lhs.getName().compareTo(rhs.getName());
//                }
//            }
//        });
        return listDev;
    }

    public static Scene getScene(String scenst) {
        Scene dev = null;
        for (Scene device : getScene()) {
            String sd = device.getGwMac() + device.getPL().getScene().get(0).getSceneID();
            if (sd.equals(scenst)) {
                dev = device;
                break;
            }
        }
        return dev;
    }

    public static void addScene(Scene dev) {
        Scene device = frdMap.get(dev.getGwMac() + dev.getPL().getScene().get(0).getSceneID());
        if (device != null) { // 如果已经保存过设备，就不add
            frdMap.put(device.getGwMac() + device.getPL().getScene().get(0).getSceneID(), dev);
            return;
        }
        frdMap.put(device.getGwMac() + device.getPL().getScene().get(0).getSceneID(), dev);
    }

    public static void removeScene(String Mac, int SceneId) {
        frdMap.remove(Mac + SceneId + "");
    }
}
