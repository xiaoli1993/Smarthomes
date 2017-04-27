package com.nuowei.smarthome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.SceneListAdapter;
import com.nuowei.smarthome.modle.Scene;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.smarthomesdk.utils.XlinkUtils;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.wevey.selector.dialog.DialogInterface;
import com.wevey.selector.dialog.NormalAlertDialog;
import com.wooplr.spotlight.SpotlightView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
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
    //    private SceneAdapter mAdapter;
    private SceneListAdapter mAdapters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        initEven();
        initData();

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
                removeScen(macs, data);
            } else if (action.equals(Constants.BROADCAST_RECVPIPE_SYNC)) {
                String data = intent.getStringExtra(Constants.DATA);
                addScene(macs, data);
                removeScen(macs, data);
            } else if (action.equals(Constants.BROADCAST_DEVICE_CHANGED)) {

            } else if (action.equals(Constants.BROADCAST_CONNENCT_SUCCESS)) {

            } else if (action.equals(Constants.BROADCAST_CONNENCT_FAIL)) {

            } else if (action.equals(Constants.BROADCAST_SEND_OVERTIME)) {

            } else if (action.equals(Constants.BROADCAST_SEND_SUCCESS)) {

            }
        }
    };

    private void addScene(String macs, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject PLjson = jsonObject.getJSONObject("PL");
            JSONArray add = PLjson.getJSONArray("2.1.1.3.3.250");
            Gson gson = new Gson();
            Scene scene = gson.fromJson(add.get(0).toString(), Scene.class);
            scene.setGwMac(macs);
            addScene(scene);
            listDev = getScene();
//            mAdapter.notifyDataSetChanged();
            mAdapters.notifyDataSetChanged();
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        upDateList();
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
                String Include = ZigbeeGW.GetScene(MyApplication.getMyApplication().getUserInfo().getNickname());
                MyApplication.getLogger().json(Include);
                XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), Include.getBytes(), new SendPipeListener() {
                    @Override
                    public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                        MyApplication.getLogger().i("发送:" + i + "\n" + xDevice.getMacAddress());
                    }
                });
            }
        }).start();
    }

    private void initEven() {
        new SpotlightView.Builder(SceneActivity.this)
                .introAnimationDuration(400)
//                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                //.setTypeface(FontUtil.get(this, "RemachineScript_Personal_Use"))
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText(getString(R.string.add_scene))
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText(getString(R.string.ClickheretoaddScene))
                .maskColor(Color.parseColor("#dc000000"))
                .target(btnRight)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
//                .dismissOnBackPress(true)
//                .enableDismissAfterShown(true)
                .usageId("") //UNIQUE ID
                .show();
        tvTitle.setText(getResources().getString(R.string.Scene));
        tvRight.setVisibility(View.GONE);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setBackgroundResource(R.drawable.r_add);

        listDev = getScene();
//        mAdapter = new SceneAdapter(SceneActivity.this,listDev);
        mAdapters = new SceneListAdapter(R.layout.item_scene, listDev);

        refreshListRecyclerView.setLoadMoreEnabled(false);
        mFamiliarRecyclerView = refreshListRecyclerView.getFamiliarRecyclerView();
        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        refreshListRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Scene scene = listDev.get(position);
                Bundle b = new Bundle();
                b.putSerializable(Constants.DEVICE_MAC, scene.getGwMac());
                b.putSerializable(Constants.DEVICE_NAME, scene.getName());
                b.putSerializable(Constants.Scene_sceneID, scene.getSceneID());
//                b.putSerializable(Constants.Scene_Listtext, scene.getLoad());
                b.putSerializable(Constants.Scene_Load, scene.getLoad().toString());
                b.putSerializable(Constants.Scene_Hour, scene.getTime().getHour());
                b.putSerializable(Constants.Scene_Min, scene.getTime().getMin());
                b.putSerializable(Constants.Scene_Wk, scene.getTime().getWkflag());
                b.putSerializable(Constants.Scene_TimeEnable, scene.getTimeEnable());
                b.putSerializable(Constants.Scene_countTime, scene.getCountTime());
                Intent intent = new Intent(SceneActivity.this, SceneAddActivity.class);
                intent.putExtras(b);
                intent.setClass(SceneActivity.this, SceneAddActivity.class);
                startActivity(intent);
            }
        });
        refreshListRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Scene scene = listDev.get(position);
                showDialogs(scene.getSceneID());
                return true;
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
        refreshListRecyclerView.setAdapter(mAdapters);
    }

    private void showDialogs(final int scenID) {
        new NormalAlertDialog.Builder(SceneActivity.this).setTitleVisible(false)
                .setTitleText(getString(R.string.Tips))
                .setTitleTextColor(R.color.black_light)
                .setContentText(getString(R.string.Deletethisscene))
                .setContentTextColor(R.color.black_light)
                .setLeftButtonText(getString(R.string.dialog_cancel))
                .setLeftButtonTextColor(R.color.gray)
                .setRightButtonText(getString(R.string.dialog_ok))
                .setRightButtonTextColor(R.color.black_light)
                .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<NormalAlertDialog>() {
                    @Override
                    public void clickLeftButton(NormalAlertDialog dialog, View view) {

                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(final NormalAlertDialog dialog, View view) {
                        final KProgressHUD hud = KProgressHUD.create(SceneActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel(getResources().getString(R.string.Send))
                                .setCancellable(true);
                        hud.show();
                        dialog.dismiss();
                        final String removeScene = ZigbeeGW.RemoveScene(MyApplication.getMyApplication().getUserInfo().getNickname(), scenID, true);
                        if (MainActivity.getChoiceGwDevice().getDeviceState() == 0) {
                            XlinkAgent.getInstance().connectDevice(MainActivity.getChoiceGwDevice().getxDevice(), MainActivity.getChoiceGwDevice().getAccessKey(), new ConnectDeviceListener() {
                                @Override
                                public void onConnectDevice(XDevice xDevice, int ret) {
                                    if (ret < 0) {
                                        MyApplication.getLogger().i("连接失败:" + ret);
                                        XlinkUtils.shortTips(getString(R.string.deviceoffline));
                                        hud.dismiss();
                                    } else {
                                        MainActivity.getChoiceGwDevice().setDeviceState(1);
                                        XlinkAgent.getInstance().sendPipeData(xDevice, removeScene.getBytes(), new SendPipeListener() {
                                            @Override
                                            public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                                hud.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), removeScene.getBytes(), new SendPipeListener() {
                                @Override
                                public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                    hud.dismiss();
                                }
                            });
                        }

                    }
                })
                .build()
                .show();
    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }

    @OnClick(R.id.btn_right)
    public void onViewClicked() {
        startActivity(new Intent(SceneActivity.this, SceneAddActivity.class));
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
//                int type1 = lhs.getSceneID();
//                int type2 = rhs.getSceneID();
//                if (!lhs.getGW_name().equals(rhs.getGW_name())) {
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
            String sd = device.getGwMac() + device.getSceneID();
            if (sd.equals(scenst)) {
                dev = device;
                break;
            }
        }
        return dev;
    }

    public static void addScene(Scene dev) {
        Scene device = frdMap.get(dev.getGwMac() + dev.getSceneID());
        if (device != null) { // 如果已经保存过设备，就不add
            frdMap.put(dev.getGwMac() + dev.getSceneID(), dev);
            return;
        }
        frdMap.put(dev.getGwMac() + dev.getSceneID(), dev);
    }

    public static void removeScene(String Mac, int SceneId) {
        frdMap.remove(Mac + SceneId + "");
    }

    private void removeScen(String mac, String res) {
        try {
            JSONObject json = new JSONObject(res);
            JSONObject PLjson = json.getJSONObject("PL");
            try {
                int isremove = PLjson.getInt("2.1.1.3.3.250." + 1 + ".255");
                MyApplication.getLogger().i("删除" + 1);
                if (isremove == 1) {
                    removeScene(mac, 1);
                    listDev = getScene();
                    mAdapters.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }
            try {
                int isremove = PLjson.getInt("2.1.1.3.3.250." + 2 + ".255");
                MyApplication.getLogger().i("删除" + 2);
                if (isremove == 1) {
                    removeScene(mac, 2);
                    listDev = getScene();
                    mAdapters.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }

            try {
                int isremove = PLjson.getInt("2.1.1.3.3.250." + 3 + ".255");
                MyApplication.getLogger().i("删除" + 3);
                if (isremove == 1) {
                    removeScene(mac, 3);
                    listDev = getScene();
                    mAdapters.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }
            try {
                int isremove = PLjson.getInt("2.1.1.3.3.250." + 4 + ".255");
                MyApplication.getLogger().i("删除" + 4);
                if (isremove == 1) {
                    removeScene(mac, 4);
                    listDev = getScene();
                    mAdapters.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }
            try {
                int isremove = PLjson.getInt("2.1.1.3.3.250." + 5 + ".255");
                MyApplication.getLogger().i("删除" + 5);
                if (isremove == 1) {
                    removeScene(mac, 5);
                    listDev = getScene();
                    mAdapters.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }
            try {
                int isremove = PLjson.getInt("2.1.1.3.3.250." + 6 + ".255");
                MyApplication.getLogger().i("删除" + 6);
                if (isremove == 1) {
                    removeScene(mac, 6);
                    listDev = getScene();
                    mAdapters.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }

        } catch (Exception e) {

        }
    }

}
