package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainListAdapter;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.helper.OnRecyclerItemClickListener;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.ListMain;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.util.ACache;
import com.nuowei.smarthome.util.VibratorUtil;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

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
    private List<ListMain> dataSourceList = new ArrayList<ListMain>();
    private MainListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
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
            dataSourceList.add(new ListMain(subDeviceList.get(i).getZigbeeMac(), subDeviceList.get(i).getDeviceMac(), true));
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
        });
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


    @OnClick({R.id.btn_home, R.id.btn_away, R.id.btn_disarm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_home:
                break;
            case R.id.btn_away:
                break;
            case R.id.btn_disarm:
                break;
        }
    }
}
