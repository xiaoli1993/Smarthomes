package com.nuowei.smarthome.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainListAdapter;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.ListMain;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.cbdialog.CBDialogBuilder;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;

public class SecurityActivity extends BaseActivity {//implements MyItemTouchCallback.OnDragListener {
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    //    @BindView(R.id.shimmer_recycler_view)
//    ShimmerRecyclerView shimmerRecyclerView;
    @BindView(R.id.refreshListRecyclerView)
    FamiliarRefreshRecyclerView refreshListRecyclerView;

    @BindView(R.id.activity_security)
    LinearLayout activitySecurity;
    @BindView(R.id.image_home)
    ImageButton imageHome;
    @BindView(R.id.image_away)
    ImageButton imageAway;
    @BindView(R.id.image_disarm)
    ImageButton imageDisarm;
    @BindView(R.id.tv_home)
    DinProTextView tvHome;
    @BindView(R.id.tv_away)
    DinProTextView tvAway;
    @BindView(R.id.tv_disarm)
    DinProTextView tvDisarm;
    @BindView(R.id.btn_right)
    ImageButton btn_right;

    private List<ListMain> dataSourceList = new ArrayList<ListMain>();
    private MainListAdapter mAdapter;

    private FamiliarRecyclerView mFamiliarRecyclerView;
    private boolean isChiose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        initData();
        initEven();
    }

    private void initData() {
        try {
            MyUtil.isEmptyString(MainActivity.getChoiceGwDevice().getDeviceMac());
            isChiose = true;
        } catch (Exception e) {
            isChiose = false;
        }
//        List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
//        for (int x = 0; x < xlinkDeviceList.size(); x++) {
//
//            MyApplication.getLogger().w("List列表:" + xlinkDeviceList.get(x).getDeviceMac());
//            dataSourceList.add(new ListMain("", xlinkDeviceList.get(x).getDeviceMac(), false));
//        }
        List<SubDevice> subDeviceList = SubDeviceManage.getInstance().getDevices();
        for (int i = 0; i < subDeviceList.size(); i++) {
            MyApplication.getLogger().w("List列表:" + subDeviceList.get(i).getZigbeeMac() + "\t" + subDeviceList.get(i).getDeviceMac());
            if (isChiose) {
                if (MainActivity.getChoiceGwDevice().getDeviceMac().equals(subDeviceList.get(i).getDeviceMac())) {
                    if (subDeviceList.get(i).getDeviceType() != Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP) {
                        dataSourceList.add(new ListMain(subDeviceList.get(i).getZigbeeMac(), subDeviceList.get(i).getDeviceMac(), true, subDeviceList.get(i).getDeviceType()));
                    }
                }
            }
        }
        switch (MainActivity.getDefence()) {
            case 2:
                imageAway.setImageResource(R.drawable.gw_away_pressed);
                tvAway.setTextColor(getResources().getColor(R.color.text_title));
                break;
            case 1:
                imageHome.setImageResource(R.drawable.gw_home_pressed);
                tvHome.setTextColor(getResources().getColor(R.color.text_title));
                break;
            case 0:
                imageDisarm.setImageResource(R.drawable.gw_disarm_pressed);
                tvDisarm.setTextColor(getResources().getColor(R.color.text_title));
                break;
        }

    }

    private void initEven() {
        tvTitle.setText(getResources().getString(R.string.Security));
        tvRight.setVisibility(View.GONE);
        btn_right.setVisibility(View.VISIBLE);
        mAdapter = new MainListAdapter(R.layout.item_lists, dataSourceList);


//        shimmerRecyclerView.setHasFixedSize(true);
//        shimmerRecyclerView.setAdapter(mAdapter);
//        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(SecurityActivity.this));
//        shimmerRecyclerView.showShimmerAdapter();
//
//        shimmerRecyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadCards();
//            }
//        }, 2000);
//
//        shimmerRecyclerView.showShimmerAdapter();
//
//        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(mAdapter));
//        itemTouchHelper.attachToRecyclerView(shimmerRecyclerView);
//
//        shimmerRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(shimmerRecyclerView) {
//            @Override
//            public void onLongClick(RecyclerView.ViewHolder vh) {
//                if (vh.getLayoutPosition() != dataSourceList.size() - 1) {
//                    itemTouchHelper.startDrag(vh);
//                    VibratorUtil.Vibrate(SecurityActivity.this, 70);   //震动70ms
//                }
//            }
//
//            @Override
//            public void onItemClick(RecyclerView.ViewHolder vh) {
//                ListMain item = dataSourceList.get(vh.getLayoutPosition());
//                if (item.isSub()) {
//                    switch (item.getDeviceType()) {
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:
//
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
//                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
//                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
//                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
//                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
//                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
//                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
//                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
//                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
//                            break;
//                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
//                            break;
//                        default:
//                            break;
//                    }
//                } else {
//
//                }
//
//            }
//        });

        refreshListRecyclerView.setLoadMoreEnabled(false);

        mFamiliarRecyclerView = refreshListRecyclerView.getFamiliarRecyclerView();
        // ItemAnimator
        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Item Click and Item Long Click
        refreshListRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                ListMain item = dataSourceList.get(position);
                if (item.isSub()) {
                    switch (item.getDeviceType()) {
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:

                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                            openDiary(item.getDeviceMac(), item.getSubMac(), item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                            openDiary(item.getDeviceMac(), item.getSubMac(), item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                            openDiary(item.getDeviceMac(), item.getSubMac(), item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                            openDiary(item.getDeviceMac(), item.getSubMac(), item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                            openDiary(item.getDeviceMac(), item.getSubMac(), item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                            openDiary(item.getDeviceMac(), item.getSubMac(), item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                            openDiary(item.getDeviceMac(), item.getSubMac(), item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                            openDiary(item.getDeviceMac(), item.getSubMac(), item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PLUGIN:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_METRTING_PLUGIN:
                            break;
                        default:
                            break;
                    }
                } else {

                }
            }
        });
//        refreshListRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
//                Log.i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
//                return true;
//            }
//        });
        refreshListRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        String getSub = ZigbeeGW.GetSubDevice(userName);
//                        XlinkAgent.getInstance().sendPipeData(xDevice, getSub.getBytes(), new SendPipeListener() {
//                            @Override
//                            public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
//                                JSONObject object = XlinkAgent.deviceToJson(xDevice);
//                                MyApplication.getLogger().i("发送:" + i + "\n" + getSub);
//                            }
//                        });
                        try {
                            MyUtil.isEmptyString(MainActivity.getChoiceGwDevice().getDeviceMac());
                            isChiose = true;
                        } catch (Exception e) {
                            isChiose = false;
                        }
                        if (isChiose) {
                            final String getSub = ZigbeeGW.GetSubDevice(MyApplication.getMyApplication().getUserInfo().getNickname());
                            if (MainActivity.getChoiceGwDevice().getDeviceState() == 0) {
                                XlinkAgent.getInstance().connectDevice(MainActivity.getChoiceGwDevice().getxDevice(), MainActivity.getChoiceGwDevice().getAccessKey(), new ConnectDeviceListener() {
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
                                XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), getSub.getBytes(), new SendPipeListener() {
                                    @Override
                                    public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                        JSONObject object = XlinkAgent.deviceToJson(xDevice);
                                        MyApplication.getLogger().i("发送:" + i + "\n" + getSub);
                                    }
                                });
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        refreshListRecyclerView.pullRefreshComplete();
                    }
                }, 1000);
            }
        });


        refreshListRecyclerView.setAdapter(mAdapter);

    }

    private void openDiary(String gwMac, String zigbeeMac, boolean isgw) {
        int isGw2 = 0;
        Intent intent = new Intent(SecurityActivity.this, DiaryActivity.class);
        //用Bundle携带数据
        Bundle bundle = new Bundle();
        //传递name参数为tinyphp
        bundle.putString(Constants.GATEWAY_MAC, gwMac);
        bundle.putString(Constants.ZIGBEE_MAC, zigbeeMac);
        if (isgw) {
            isGw2 = 1;
        }
        bundle.putInt("isGw", isGw2);
        intent.putExtras(bundle);
        startActivity(intent);
    }

//    private void loadCards() {
//        shimmerRecyclerView.hideShimmerAdapter();
//    }


//    @Override
//    public void onFinishDrag() {
//        ACache.get(SecurityActivity.this).put("main_device_list", (ArrayList<ListMain>) dataSourceList);
//    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }

    @OnClick(R.id.btn_right)
    void onBtnRightClick() {
        //TODO implement
    }


    @OnClick({R.id.btn_home, R.id.btn_away, R.id.btn_disarm, R.id.image_home, R.id.image_away, R.id.image_disarm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_home:
                setDefence(1);
                break;
            case R.id.btn_away:
                setDefence(2);
                break;
            case R.id.btn_disarm:
                setDefence(0);
                break;
            case R.id.image_home:
                setDefence(1);
                break;
            case R.id.image_away:
                setDefence(2);
                break;
            case R.id.image_disarm:
                setDefence(0);
                break;
        }
    }

    private void setDefence(int defence) {
        try {
            MyUtil.isEmptyString(MainActivity.getChoiceGwDevice().getDeviceMac());
            isChiose = true;
        } catch (Exception e) {
            isChiose = false;
        }
        if (isChiose) {
            initImage();
            final String json = ZigbeeGW.Setdefence(MyApplication.getMyApplication().getUserInfo().getNickname(), defence);
            if (MainActivity.getChoiceGwDevice().getDeviceState() == 0) {
                XlinkAgent.getInstance().connectDevice(MainActivity.getChoiceGwDevice().getxDevice(), MainActivity.getChoiceGwDevice().getxDevice().getAccessKey(), new ConnectDeviceListener() {
                    @Override
                    public void onConnectDevice(XDevice xDevice, int i) {
                        XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), json.getBytes(),
                                new SendPipeListener() {
                                    @Override
                                    public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {

                                    }
                                });
                    }
                });
            } else {
                XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), json.getBytes(),
                        new SendPipeListener() {
                            @Override
                            public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {

                            }
                        });
            }
            MainActivity.setDefence(defence);
            switch (defence) {
                case 2:
                    imageAway.setImageResource(R.drawable.gw_away_pressed);
                    tvAway.setTextColor(getResources().getColor(R.color.text_title));
                    break;
                case 1:
                    imageHome.setImageResource(R.drawable.gw_home_pressed);
                    tvHome.setTextColor(getResources().getColor(R.color.text_title));
                    break;
                case 0:
                    imageDisarm.setImageResource(R.drawable.gw_disarm_pressed);
                    tvDisarm.setTextColor(getResources().getColor(R.color.text_title));
                    break;
            }
        } else {
            new CBDialogBuilder(SecurityActivity.this)
                    .setTouchOutSideCancelable(true)
                    .showCancelButton(true)
                    .setTitle(getString(R.string.not_gateway))
                    .setMessage("")
                    .setCustomIcon(R.drawable.alerter_ic_notifications)
                    .setConfirmButtonText(getString(R.string.Adddevice))
                    .setCancelButtonText(getResources().getString(R.string.dialog_cancel))
                    .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                    .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                        @Override
                        public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                            switch (whichBtn) {
                                case BUTTON_CONFIRM:
//                                                    Toast.makeText(context, "点击了确认按钮", Toast.LENGTH_SHORT).show();
                                    break;
                                case BUTTON_CANCEL:
//                                                    Toast.makeText(context, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }).create().show();
        }
    }

    private void initImage() {
        imageAway.setImageResource(R.drawable.gw_away_normal);
        imageHome.setImageResource(R.drawable.gw_home_normal);
        imageDisarm.setImageResource(R.drawable.gw_disarm_normal);
        tvAway.setTextColor(getResources().getColor(R.color.text_title_g));
        tvHome.setTextColor(getResources().getColor(R.color.text_title_g));
        tvDisarm.setTextColor(getResources().getColor(R.color.text_title_g));
    }

}
