package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainAdapter;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.view.draggridview.DragGridView;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 08:29
 * @Description :
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.drag_grid_view)
    DragGridView dragGridView;
    private ArrayList<MainDatas> mainDatas;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();
        LoginResult loginResult = NetManager.getInstance(this).createLoginResult(NetManager.getInstance(this).login("554674787@qq.com", "8888"));
        MyApplication.getLogger().i(loginResult.contactId + "\n" + loginResult.error_code);
    }

    private void initView() {
        mainAdapter = new MainAdapter(MainActivity.this, mainDatas);
        dragGridView.setAdapter(mainAdapter);
    }

    private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();

    private void initData() {
        List<MainDatas> allNews = DataSupport.findAll(MainDatas.class);
        if (allNews != null && !allNews.isEmpty()) {
            mainDatas = new ArrayList<MainDatas>();
            for (int i = 0; i < allNews.size(); i++) {
                mainDatas.add(allNews.get(i));
            }
        } else {
            mainDatas = new ArrayList<MainDatas>();
            mainDatas.add(new MainDatas(getResources().getString(R.string.Security), 0, 0));
            mainDatas.add(new MainDatas(getResources().getString(R.string.Air), 1, 1));
            mainDatas.add(new MainDatas(getResources().getString(R.string.Water), 2, 2));
            mainDatas.add(new MainDatas(getResources().getString(R.string.Electricity), 3, 3));
            mainDatas.add(new MainDatas(getResources().getString(R.string.Lights), 4, 4));
            mainDatas.add(new MainDatas(getResources().getString(R.string.Floor_heating), 5, 5));
            mainDatas.add(new MainDatas(getResources().getString(R.string.Service), 6, 6));
            mainDatas.add(new MainDatas(getResources().getString(R.string.Equipment), 7, 7));
            mainDatas.add(new MainDatas(getResources().getString(R.string.Setting), 8, 8));
            DataSupport.saveAll(mainDatas);
        }
        Collections.sort(mainDatas, new Comparator<MainDatas>() {

            @Override
            public int compare(MainDatas lhs, MainDatas rhs) {
                int type1 = lhs.getMainsort();
                int type2 = rhs.getMainsort();
                return type1 - type2;
            }
        });
    }
}