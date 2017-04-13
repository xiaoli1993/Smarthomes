package com.nuowei.smarthome.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.activity.MainActivity;
import com.nuowei.smarthome.activity.SceneAddActivity;
import com.nuowei.smarthome.activity.ScrollingActivity;
import com.nuowei.smarthome.activity.SecurityActivity;
import com.nuowei.smarthome.adapter.MainGridAdapter;
import com.nuowei.smarthome.common.DividerGridItemDecoration;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.helper.OnRecyclerItemClickListener;
import com.nuowei.smarthome.modle.CollapsingToolbarLayoutState;
import com.nuowei.smarthome.modle.IpMac;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.util.SharePreferenceUtil;
import com.nuowei.smarthome.util.VibratorUtil;
import com.nuowei.smarthome.view.scrollview.MyLinearLayoutManager;
import com.nuowei.smarthome.yahoo.WeatherInfo;
import com.nuowei.smarthome.yahoo.YahooWeather;
import com.nuowei.smarthome.yahoo.YahooWeatherExceptionListener;
import com.nuowei.smarthome.yahoo.YahooWeatherInfoListener;
import com.orhanobut.hawk.Hawk;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Copyright ©深圳市海曼科技有限公司
 */

public class MainGridFragment extends Fragment implements MyItemTouchCallback.OnDragListener, YahooWeatherExceptionListener, YahooWeatherInfoListener {
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.shimmer_recycler_view)
    ShimmerRecyclerView shimmerRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolBar;
    @BindView(R.id.rl_temp)
    RelativeLayout rlTemp;

    @BindView(R.id.tv_new_temp)
    TextView tvNewTemp;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.tv_pm25)
    TextView tvPm25;
    @BindView(R.id.tv_tds)
    TextView tvTds;
    @BindView(R.id.tv_temp)
    TextView tvTemp;

    private List<HashMap<String, MainDatas>> dataSourceList = new ArrayList<HashMap<String, MainDatas>>();
    private CollapsingToolbarLayoutState state;
    private MainGridAdapter mAdapter;

    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_grid, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initWeather();
        initToobar();
        initData();
        initEven();
    }

    @OnClick(R.id.image_menu)
    void openDrawers() {
        ((MainActivity) getActivity()).openDrawers();
    }

    private void initToobar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolBar);
        setHasOptionsMenu(true);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                        toolBar.setTitle("");//设置title为EXPANDED
                        rlTemp.setVisibility(View.VISIBLE);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        toolBar.setTitle("");//设置title不显示
                        rlTemp.setVisibility(View.GONE);//隐藏播放按钮
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            rlTemp.setVisibility(View.VISIBLE);//由折叠变为中间状态时隐藏播放按钮
                        }
                        toolBar.setTitle("");//设置title为INTERNEDIATE
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });
    }


    private void initEven() {
        shimmerRecyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        mAdapter = new MainGridAdapter(R.layout.item_main, dataSourceList);
        shimmerRecyclerView.setHasFixedSize(true);
        shimmerRecyclerView.setAdapter(mAdapter);
        shimmerRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        shimmerRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        shimmerRecyclerView.showShimmerAdapter();

        shimmerRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCards();
            }
        }, 2000);

        shimmerRecyclerView.showShimmerAdapter();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(mAdapter).setOnDragListener(this));
        itemTouchHelper.attachToRecyclerView(shimmerRecyclerView);
        shimmerRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(shimmerRecyclerView) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                if (vh.getLayoutPosition() != dataSourceList.size() - 1) {
                    itemTouchHelper.startDrag(vh);
                    VibratorUtil.Vibrate(getActivity(), 70);   //震动70ms
                }
            }

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                HashMap<String, MainDatas> item = dataSourceList.get(vh.getLayoutPosition());
                switch (item.get("Main").getMainType()) {
                    case 0:
                        startActivity(new Intent(getActivity(), SecurityActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), ScrollingActivity.class));
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    default:
                        break;
                }

                Toast.makeText(getActivity(), item.get("Main").getMainsort() + " " + item.get("Main").getMainString(), Toast.LENGTH_SHORT).show();
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

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        MyApplication.getLogger().e("onCreateOptionsMenu()");
//        menu.clear();
//        inflater.inflate(R.menu.menu_parent_fragment, menu);
//    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WeatherInfo weatherInfo = Hawk.get("weater", new WeatherInfo());
                    if (weatherInfo != null) {
                        tvNewTemp.setText(weatherInfo.getCurrentTemp() + "°");
                        tvWeather.setText(weatherInfo.getCurrentText() + "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        } catch (Exception e) {
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

    @Override
    public void onFinishDrag() {
        Hawk.put("Main", SharePreferenceUtil.listToJson(dataSourceList));
    }

    /**
     * 天气获取
     */
    private void initWeather() {
        String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                MyApplication.getLogger().e("\t" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                MyApplication.getLogger().i("response:" + response);
                Gson gson = new Gson();
                IpMac ipmac = gson.fromJson(response, IpMac.class);
                MyApplication.getLogger().i("City:" + ipmac.getCity());
                searchByPlaceName(ipmac.getCity());
            }

        });
    }


    private void searchByGPS() {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.GPS);
        mYahooWeather.queryYahooWeatherByGPS(MyApplication.getMyApplication(), this);
    }

    private void searchByPlaceName(String location) {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.PLACE_NAME);
        mYahooWeather.queryYahooWeatherByPlaceName(MyApplication.getMyApplication(), location, this);
    }

    @Override
    public void onFailConnection(final Exception e) {
        // TODO Auto-generated method stub
        Toast.makeText(MyApplication.getMyApplication(), "Fail Connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailParsing(final Exception e) {
        // TODO Auto-generated method stub
        Toast.makeText(MyApplication.getMyApplication(), "Fail Parsing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailFindLocation(final Exception e) {
        // TODO Auto-generated method stub
        Toast.makeText(MyApplication.getMyApplication(), "Fail Find Location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        if (weatherInfo != null) {
            if (mYahooWeather.getSearchMode() == YahooWeather.SEARCH_MODE.GPS) {
                if (weatherInfo.getAddress() != null) {
                    MyApplication.getLogger().v("====== Address ======" + YahooWeather.addressToPlaceName(weatherInfo.getAddress()));
                }
            }
            MyApplication.getLogger().v("====== CURRENT ======" + "\n" +
                    "date: " + weatherInfo.getCurrentConditionDate() + "\n" +
                    "weather: " + weatherInfo.getCurrentText() + "\n" +
                    "temperature in ºC: " + weatherInfo.getCurrentTemp() + "\n" +
                    "wind chill: " + weatherInfo.getWindChill() + "\n" +
                    "wind direction: " + weatherInfo.getWindDirection() + "\n" +
                    "wind speed: " + weatherInfo.getWindSpeed() + "\n" +
                    "Humidity: " + weatherInfo.getAtmosphereHumidity() + "\n" +
                    "Pressure: " + weatherInfo.getAtmospherePressure() + "\n" +
                    "Visibility: " + weatherInfo.getAtmosphereVisibility());
            tvNewTemp.setText(weatherInfo.getCurrentTemp() + "°");
            tvWeather.setText(weatherInfo.getCurrentText() + "");
            Hawk.put("weater", weatherInfo);
            if (weatherInfo.getCurrentConditionIcon() != null) {
                Drawable imageIcon = new BitmapDrawable(weatherInfo.getCurrentConditionIcon());
                imageIcon.setBounds(0, 0, imageIcon.getMinimumWidth(), imageIcon.getMinimumHeight());
                tvWeather.setCompoundDrawables(imageIcon, null, null, null); //设置左图标
//                mIvWeather0.setImageBitmap(weatherInfo.getCurrentConditionIcon());
            }
            for (int i = 0; i < YahooWeather.FORECAST_INFO_MAX_SIZE; i++) {
                final WeatherInfo.ForecastInfo forecastInfo = weatherInfo.getForecastInfoList().get(i);
//                final ImageView ivForecast = (ImageView) forecastInfoLayout.findViewById(R.id.imageview_forecast_info);
//                if (forecastInfo.getForecastConditionIcon() != null) {
//                    ivForecast.setImageBitmap(forecastInfo.getForecastConditionIcon());
//                }
//                mWeatherInfosLayout.addView(forecastInfoLayout);
                MyApplication.getLogger().d("====== FORECAST " + (i + 1) + " ======" + "\n" +
                        "date: " + forecastInfo.getForecastDate() + "\n" +
                        "weather: " + forecastInfo.getForecastText() + "\n" +
                        "low  temperature in ºC: " + forecastInfo.getForecastTempLow() + "\n" +
                        "high temperature in ºC: " + forecastInfo.getForecastTempHigh() + "\n");
            }
        } else {
//            setNoResultLayout();
        }

    }

    /**
     * 在家布防、外出布防、撤防
     */
    @OnClick(R.id.btn_home)
    void onBtnHomeClick() {
        //TODO implement
    }

    @OnClick(R.id.btn_away)
    void onBtnAwayClick() {
        //TODO implement
    }


    @OnClick(R.id.btn_disarm)
    void onBtnDisarmClick() {
        //TODO implement
    }

}