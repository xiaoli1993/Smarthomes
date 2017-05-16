package com.nuowei.smarthome.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.activity.AddDeviceActivity;
import com.nuowei.smarthome.activity.AirActivity;
import com.nuowei.smarthome.activity.DeviceListActivity;
import com.nuowei.smarthome.activity.DiaryActivity;
import com.nuowei.smarthome.activity.ElectricityActivity;
import com.nuowei.smarthome.activity.MainActivity;
import com.nuowei.smarthome.activity.SceneActivity;
import com.nuowei.smarthome.activity.SecurityActivity;
import com.nuowei.smarthome.activity.SettingActivity;
import com.nuowei.smarthome.adapter.MainListTAdapter;
import com.nuowei.smarthome.helper.MyItemTouchCallback;
import com.nuowei.smarthome.helper.OnRecyclerItemClickListener;
import com.nuowei.smarthome.modle.CollapsingToolbarLayoutState;
import com.nuowei.smarthome.modle.IpMac;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.util.SharePreferenceUtil;
import com.nuowei.smarthome.util.VibratorUtil;
import com.nuowei.smarthome.view.cbdialog.CBDialogBuilder;
import com.nuowei.smarthome.view.textview.DinProTextView;
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
import butterknife.Unbinder;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;
import okhttp3.Call;

/**
 * @Author : 肖力
 * @Time :  2017/4/13 15:20
 * @Description :
 * @Modify record :
 */

public class MainListTFragment extends Fragment implements MyItemTouchCallback.OnDragListener, YahooWeatherExceptionListener, YahooWeatherInfoListener {
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
    @BindView(R.id.image_weather)
    ImageView imageWeather;
    Unbinder unbinder;
    @BindView(R.id.image_add)
    ImageView imageAdd;
    private List<HashMap<String, MainDatas>> dataSourceList = new ArrayList<HashMap<String, MainDatas>>();
    private CollapsingToolbarLayoutState state;

    private MainListTAdapter mAdapter;

    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);
    private boolean isChiose = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initWeather();
        initToobar();
        initData();
        initEven();
    }

    @OnClick(R.id.image_news)
    void openNews() {
        Intent intent = new Intent(getActivity(), DiaryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("isGw", 2);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.image_add)
    void addDevice() {
        startActivity(new Intent(getActivity(), AddDeviceActivity.class));
    }

    @OnClick(R.id.image_menu)
    void openDrawers() {
        ((MainActivity) getActivity()).openDrawers();
    }

    private void initToobar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolBar);
        setHasOptionsMenu(true);
        toolBar.setBackgroundColor(getResources().getColor(R.color.main_table));
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
            itemHashMap6.put("Main", new MainDatas(getResources().getString(R.string.Scene), 6, 6));
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

    private void initEven() {
//        new SpotlightView.Builder(getActivity())
//                .introAnimationDuration(400)
////                .enableRevealAnimation(true)
//                .performClick(true)
//                .fadeinTextDuration(400)
//                //.setTypeface(FontUtil.get(this, "RemachineScript_Personal_Use"))
//                .headingTvColor(Color.parseColor("#eb273f"))
//                .headingTvSize(32)
//                .headingTvText(getString(R.string.adddevice))
//                .subHeadingTvColor(Color.parseColor("#ffffff"))
//                .subHeadingTvSize(16)
//                .subHeadingTvText(getString(R.string.Clickheretoadddevice))
//                .maskColor(Color.parseColor("#dc000000"))
//                .target(imageAdd)
//                .lineAnimDuration(400)
//                .lineAndArcColor(Color.parseColor("#eb273f"))
//                .dismissOnTouch(true)
////                .dismissOnBackPress(true)
////                .enableDismissAfterShown(true)
//                .usageId("") //UNIQUE ID
//                .show();
//        shimmerRecyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));

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

        mAdapter = new MainListTAdapter(R.layout.item_list, dataSourceList);
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

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                HashMap<String, MainDatas> item = dataSourceList.get(vh.getLayoutPosition());
                switch (item.get("Main").getMainType()) {
                    case 0:
                        startActivity(new Intent(getActivity(), SecurityActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), AirActivity.class));
                        break;
                    case 2:
                        MyUtil.showNoDialog(getActivity());
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), ElectricityActivity.class));
                        break;
                    case 4:
                        MyUtil.showNoDialog(getActivity());
                        break;
                    case 5:
                        MyUtil.showNoDialog(getActivity());
                        break;
                    case 6:
                        try {
                            MyUtil.isEmptyString(MainActivity.getChoiceGwDevice().getDeviceMac());
                            isChiose = true;
                        } catch (Exception e) {
                            isChiose = false;
                        }
                        if (isChiose) {
                            startActivity(new Intent(getActivity(), SceneActivity.class));
                        } else {
                            new CBDialogBuilder(getActivity())
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
                        break;
                    case 7:
                        startActivity(new Intent(getActivity(), DeviceListActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(getActivity(), SettingActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void loadCards() {
        shimmerRecyclerView.hideShimmerAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getLogger().i("----------LonResume---------");
        initImage();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
            tvPm25.setText(weatherInfo.getAtmosphereHumidity() + "");
            tvTds.setText(weatherInfo.getAtmospherePressure() + "");
            tvTemp.setText(weatherInfo.getAtmosphereVisibility() + "");
            Hawk.put("weater", weatherInfo);
            if (weatherInfo.getCurrentConditionIcon() != null) {
//                Drawable imageIcon = new BitmapDrawable(weatherInfo.getCurrentConditionIcon());
//                imageIcon.setBounds(0, 0, imageIcon.getMinimumWidth(), imageIcon.getMinimumHeight());
//                tvWeather.setCompoundDrawables(imageIcon, null, null, null); //设置左图标
                imageWeather.setImageBitmap(weatherInfo.getCurrentConditionIcon());
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
            new CBDialogBuilder(getActivity())
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
