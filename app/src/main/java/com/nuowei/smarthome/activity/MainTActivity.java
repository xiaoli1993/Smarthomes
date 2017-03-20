package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainTAdapter;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.util.SharePreferenceUtil;
import com.nuowei.smarthome.view.gridview.DragGridView;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/24 08:28
 * @Description :
 */
public class MainTActivity extends AppCompatActivity {
    @BindView(R.id.dragGridView)
    DragGridView dragGridView;

    private List<HashMap<String, MainDatas>> dataSourceList = new ArrayList<HashMap<String, MainDatas>>();
    private MainTAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_main);
        ButterKnife.bind(this);
        initData();
        initEven();
    }

    private void initEven() {
//        final SimpleAdapter mainAdapter = new SimpleAdapter(MainTActivity.this, dataSourceList
//                , R.layout.item_main, new String[]{"Main", "Image"},
//                new int[]{R.id.tv_txt, R.id.image_icon});
        mainAdapter = new MainTAdapter(MainTActivity.this, dataSourceList);

        dragGridView.setAdapter(mainAdapter);

        dragGridView.setOnChangeListener(new DragGridView.OnChanageListener() {

            @Override
            public void onChange(int from, int to) {
                HashMap<String, MainDatas> temp = dataSourceList.get(from);
                //直接交互item
//				dataSourceList.set(from, dataSourceList.get(to));
//				dataSourceList.set(to, temp);


                //这里的处理需要注意下
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(dataSourceList, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(dataSourceList, i, i - 1);
                    }
                }

                dataSourceList.set(to, temp);

                mainAdapter.notifyDataSetChanged();
                Hawk.put("Main", SharePreferenceUtil.listToJson(dataSourceList)); // save list
            }
        });
    }

    private void initData() {
        String JsonMain = Hawk.get("Main");

        try {
            if (JsonMain.length() != 0 || JsonMain != null) {
                try {
                    final JSONArray list = new JSONArray(JsonMain);
                    final int iSize = list.length();
                    for (int i = 0; i < iSize; i++) {
                        HashMap<String, MainDatas> itemHashMap = new HashMap<String, MainDatas>();
                        JSONObject jsonObj = list.getJSONObject(i);
                        JSONObject main = jsonObj.getJSONObject("Main");
                        Gson gson = new Gson();
                        MainDatas mainDatas = gson.fromJson(main.toString(), MainDatas.class);
                        itemHashMap.put("Main", mainDatas);
                        dataSourceList.add(itemHashMap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            HashMap<String, MainDatas> itemHashMap = new HashMap<String, MainDatas>();
            itemHashMap.put("Main", new MainDatas(getResources().getString(R.string.Security), 0, 0));
            dataSourceList.add(itemHashMap);
            HashMap<String, MainDatas> itemHashMap1 = new HashMap<String, MainDatas>();
            itemHashMap1.put("Main", new MainDatas(getResources().getString(R.string.Air), 1, 1));
            dataSourceList.add(itemHashMap1);
            HashMap<String, MainDatas> itemHashMap2 = new HashMap<String, MainDatas>();
            itemHashMap2.put("Main", new MainDatas(getResources().getString(R.string.Water), 2, 2));
            dataSourceList.add(itemHashMap2);
            HashMap<String, MainDatas> itemHashMap3 = new HashMap<String, MainDatas>();
            itemHashMap3.put("Main", new MainDatas(getResources().getString(R.string.Electricity), 3, 4));
            dataSourceList.add(itemHashMap3);
            HashMap<String, MainDatas> itemHashMap4 = new HashMap<String, MainDatas>();
            itemHashMap4.put("Main", new MainDatas(getResources().getString(R.string.Lights), 4, 4));
            dataSourceList.add(itemHashMap4);
            HashMap<String, MainDatas> itemHashMap5 = new HashMap<String, MainDatas>();
            itemHashMap5.put("Main", new MainDatas(getResources().getString(R.string.Floor_heating), 5, 5));
            dataSourceList.add(itemHashMap5);
            HashMap<String, MainDatas> itemHashMap6 = new HashMap<String, MainDatas>();
            itemHashMap6.put("Main", new MainDatas(getResources().getString(R.string.Service), 6, 6));
            dataSourceList.add(itemHashMap6);
            HashMap<String, MainDatas> itemHashMap7 = new HashMap<String, MainDatas>();
            itemHashMap7.put("Main", new MainDatas(getResources().getString(R.string.Equipment), 7, 7));
            dataSourceList.add(itemHashMap7);
            HashMap<String, MainDatas> itemHashMap8 = new HashMap<String, MainDatas>();
            itemHashMap8.put("Main", new MainDatas(getResources().getString(R.string.Setting), 8, 8));
            dataSourceList.add(itemHashMap8);
            Hawk.put("Main", SharePreferenceUtil.listToJson(dataSourceList));
        }


        MyApplication.getLogger().json(SharePreferenceUtil.listToJson(dataSourceList));

    }

}
