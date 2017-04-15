package com.nuowei.smarthome.fragment;/**
 * Created by xiaoli on 2017/3/24.
 */

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainListAdapter;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.helper.OnRecyclerItemClickListener;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.modle.ListMain;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.util.ACache;
import com.nuowei.smarthome.util.SharePreferenceUtil;
import com.nuowei.smarthome.util.VibratorUtil;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/3/24 11:38
 * @Description :
 */
public class MainListFragment extends Fragment implements MyItemTouchCallback.OnDragListener {
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.shimmer_recycler_view)
    ShimmerRecyclerView shimmerRecyclerView;
    private List<ListMain> dataSourceList = new ArrayList<ListMain>();
    private MainListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initData();
        initEven();
    }

    private void initData() {

        List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
        for (int x = 0; x < xlinkDeviceList.size(); x++) {

            MyApplication.getLogger().w("List列表:" + xlinkDeviceList.get(x).getDeviceMac());
            dataSourceList.add(new ListMain("", xlinkDeviceList.get(x).getDeviceMac(), false, xlinkDeviceList.get(x).getDeviceType()));
        }
        List<SubDevice> subDeviceList = SubDeviceManage.getInstance().getDevices();
        for (int i = 0; i < subDeviceList.size(); i++) {

            MyApplication.getLogger().w("List列表:" + subDeviceList.get(i).getZigbeeMac() + "\t" + subDeviceList.get(i).getDeviceMac());
            dataSourceList.add(new ListMain(subDeviceList.get(i).getZigbeeMac(), subDeviceList.get(i).getDeviceMac(), true, subDeviceList.get(i).getDeviceType()));
        }
    }

    private void initEven() {
        mAdapter = new MainListAdapter(R.layout.item_list, dataSourceList);
        shimmerRecyclerView.setHasFixedSize(true);
        shimmerRecyclerView.setAdapter(mAdapter);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                    VibratorUtil.Vibrate(getActivity(), 70);   //震动70ms
                }
            }
        });
    }

    private void loadCards() {
        shimmerRecyclerView.hideShimmerAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onFinishDrag() {
        ACache.get(getActivity()).put("main_device_list", (ArrayList<ListMain>) dataSourceList);
    }
}
