package com.nuowei.smarthome.fragment;/**
 * Created by xiaoli on 2017/3/24.
 */

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nuowei.smarthome.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/3/24 11:38
 * @Description :
 */
public class MainListFragment extends Fragment {
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.toolbar_layout, R.id.app_bar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_layout:
                break;
            case R.id.app_bar:
                break;
        }
    }
}
