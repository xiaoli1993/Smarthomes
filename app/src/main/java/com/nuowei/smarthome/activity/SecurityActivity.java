package com.nuowei.smarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainListAdapter;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.helper.OnRecyclerItemClickListener;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.ListMain;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.util.ACache;
import com.nuowei.smarthome.util.VibratorUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecurityActivity extends BaseActivity implements MyItemTouchCallback.OnDragListener {
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.shimmer_recycler_view)
    ShimmerRecyclerView shimmerRecyclerView;
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
    private List<ListMain> dataSourceList = new ArrayList<ListMain>();
    private MainListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        ButterKnife.bind(this);
        initData();
        initEven();
    }

    private void initData() {

//        List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
//        for (int x = 0; x < xlinkDeviceList.size(); x++) {
//
//            MyApplication.getLogger().w("List列表:" + xlinkDeviceList.get(x).getDeviceMac());
//            dataSourceList.add(new ListMain("", xlinkDeviceList.get(x).getDeviceMac(), false));
//        }
        List<SubDevice> subDeviceList = SubDeviceManage.getInstance().getDevices();
        for (int i = 0; i < subDeviceList.size(); i++) {

            MyApplication.getLogger().w("List列表:" + subDeviceList.get(i).getZigbeeMac() + "\t" + subDeviceList.get(i).getDeviceMac());
            dataSourceList.add(new ListMain(subDeviceList.get(i).getZigbeeMac(), subDeviceList.get(i).getDeviceMac(), true, subDeviceList.get(i).getDeviceType()));
        }
    }

    private void initEven() {
        tvTitle.setText(getResources().getString(R.string.add_scene));
        mAdapter = new MainListAdapter(R.layout.item_list, dataSourceList);
        shimmerRecyclerView.setHasFixedSize(true);
        shimmerRecyclerView.setAdapter(mAdapter);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(SecurityActivity.this));
        shimmerRecyclerView.showShimmerAdapter();

        shimmerRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCards();
            }
        }, 2000);

        shimmerRecyclerView.showShimmerAdapter();

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(shimmerRecyclerView);

        shimmerRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(shimmerRecyclerView) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                if (vh.getLayoutPosition() != dataSourceList.size() - 1) {
                    itemTouchHelper.startDrag(vh);
                    VibratorUtil.Vibrate(SecurityActivity.this, 70);   //震动70ms
                }
            }

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                ListMain item = dataSourceList.get(vh.getLayoutPosition());
                if (item.isSub()) {
                    switch (item.getDeviceType()) {
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_RGB:

                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_DOORS:
                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_WATER:
                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_PIR:
                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SMOKE:
                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_THP:
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_GAS:
                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_CO:
                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SOS:
                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
                            break;
                        case Constants.DEVICE_TYPE.DEVICE_ZIGBEE_SW:
                            openDiary(item.getDeviceMac(), item.getSubMac(),item.isSub());
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
    }

    private void openDiary(String gwMac, String zigbeeMac, boolean isgw) {
        Intent intent = new Intent(SecurityActivity.this, DiaryActivity.class);
        //用Bundle携带数据
        Bundle bundle = new Bundle();
        //传递name参数为tinyphp
        bundle.putString(Constants.GATEWAY_MAC, gwMac);
        bundle.putString(Constants.ZIGBEE_MAC, zigbeeMac);
        bundle.putBoolean("isGw", isgw);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void loadCards() {
        shimmerRecyclerView.hideShimmerAdapter();
    }


    @Override
    public void onFinishDrag() {
        ACache.get(SecurityActivity.this).put("main_device_list", (ArrayList<ListMain>) dataSourceList);
    }

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
                setDefence(0);
                break;
            case R.id.btn_disarm:
                setDefence(2);
                break;
            case R.id.image_home:
                setDefence(1);
                break;
            case R.id.image_away:
                setDefence(0);
                break;
            case R.id.image_disarm:
                setDefence(2);
                break;
        }
    }

    private void setDefence(int defence) {
        initImage();
        switch (defence) {
            case 0:
                imageAway.setImageResource(R.drawable.gw_away_pressed);
                tvAway.setTextColor(getResources().getColor(R.color.text_title));
                break;
            case 1:
                imageHome.setImageResource(R.drawable.gw_home_pressed);
                tvHome.setTextColor(getResources().getColor(R.color.text_title));
                break;
            case 2:
                imageDisarm.setImageResource(R.drawable.gw_disarm_pressed);
                tvDisarm.setTextColor(getResources().getColor(R.color.text_title));
                break;
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
