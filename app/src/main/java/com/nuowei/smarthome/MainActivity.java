package com.nuowei.smarthome;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nuowei.smarthome.adapter.LeftMainAdapter;
import com.nuowei.smarthome.fragment.MainListFragment;
import com.nuowei.smarthome.modle.IpMac;
import com.nuowei.smarthome.modle.LeftMain;
import com.nuowei.smarthome.view.gridview.DragGridView;
import com.nuowei.smarthome.yahoo.WeatherInfo;
import com.nuowei.smarthome.yahoo.YahooWeather;
import com.nuowei.smarthome.yahoo.YahooWeatherExceptionListener;
import com.nuowei.smarthome.yahoo.YahooWeatherInfoListener;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import qiu.niorgai.StatusBarCompat;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/1/9 13:33
 * @Description :主程序
 */
public class MainActivity extends AppCompatActivity implements YahooWeatherExceptionListener, YahooWeatherInfoListener {

    @BindView(R.id.left_listview)
    ListView listView;

    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);
    public static final int PAGE_COMMON = 0;
    public static final int PAGE_TRANSLUCENT = 1;
    public static final int PAGE_COORDINATOR = 2;
    public static final int PAGE_COLLAPSING_TOOLBAR = 3;
    private int fragmentContentId = R.id.fragment_layout;
    private int currentTab;
    private List<LeftMain> list;

    private HashMap<Integer, Fragment> fragments = new HashMap<>();
    private LeftMainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SDK >= 21时, 取消状态栏的阴影
        StatusBarCompat.translucentStatusBar(MainActivity.this, false);
        ButterKnife.bind(this);
        initWeather();
        initFragment();
        initData();
        initEven();

    }

    private void initEven() {
        adapter = new LeftMainAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void initData() {
        list=new ArrayList<LeftMain>();

        list.add(new LeftMain(R.drawable.home_device, getResources().getString(R.string.Device)));
        list.add(new LeftMain(R.drawable.home_light, getResources().getString(R.string.Share_Device)));
        list.add(new LeftMain(R.drawable.home_security, getResources().getString(R.string.Feedback)));
        list.add(new LeftMain(R.drawable.home_electric, getResources().getString(R.string.About)));
        list.add(new LeftMain(R.drawable.home_setting, getResources().getString(R.string.Setting)));
    }

    private void initFragment() {
        fragments.put(PAGE_COMMON, new MainListFragment());
        FragmentTransaction ft = MainActivity.this.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(PAGE_COMMON));
        currentTab = PAGE_COMMON;
        ft.commit();
    }

    private void initWeather() {
        LoginResult loginResult = NetManager.getInstance(this).createLoginResult(NetManager.getInstance(this).login("554674787@qq.com", "8888"));
        MyApplication.getLogger().i(loginResult.contactId + "\n" + loginResult.error_code);
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

    private void changeTab(int page) {
        if (currentTab == page) {
            return;
        }
        Fragment fragment = fragments.get(page);
        FragmentTransaction ft = MainActivity.this.getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            ft.add(fragmentContentId, fragment);
        }
        ft.hide(fragments.get(currentTab));
        ft.show(fragments.get(page));
//        changeButtonStatus(currentTab, false);
        currentTab = page;
//        changeButtonStatus(currentTab, true);
        if (!this.isFinishing()) {
            ft.commitAllowingStateLoss();
        }
    }

    private void searchByGPS() {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.GPS);
        mYahooWeather.queryYahooWeatherByGPS(getApplicationContext(), this);
    }

    private void searchByPlaceName(String location) {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.PLACE_NAME);
        mYahooWeather.queryYahooWeatherByPlaceName(getApplicationContext(), location, MainActivity.this);
    }

    @Override
    public void onFailConnection(final Exception e) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "Fail Connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailParsing(final Exception e) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "Fail Parsing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailFindLocation(final Exception e) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "Fail Find Location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        if (weatherInfo != null) {
            if (mYahooWeather.getSearchMode() == YahooWeather.SEARCH_MODE.GPS) {
                if (weatherInfo.getAddress() != null) {
//                    mEtAreaOfCity.setText(YahooWeather.addressToPlaceName(weatherInfo.getAddress()));
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
//            mTvWeather0.setText("====== CURRENT ======" + "\n" +
//                    "date: " + weatherInfo.getCurrentConditionDate() + "\n" +
//                    "weather: " + weatherInfo.getCurrentText() + "\n" +
//                    "temperature in ºC: " + weatherInfo.getCurrentTemp() + "\n" +
//                    "wind chill: " + weatherInfo.getWindChill() + "\n" +
//                    "wind direction: " + weatherInfo.getWindDirection() + "\n" +
//                    "wind speed: " + weatherInfo.getWindSpeed() + "\n" +
//                    "Humidity: " + weatherInfo.getAtmosphereHumidity() + "\n" +
//                    "Pressure: " + weatherInfo.getAtmospherePressure() + "\n" +
//                    "Visibility: " + weatherInfo.getAtmosphereVisibility()
//            );
            if (weatherInfo.getCurrentConditionIcon() != null) {
//                mIvWeather0.setImageBitmap(weatherInfo.getCurrentConditionIcon());
            }
            for (int i = 0; i < YahooWeather.FORECAST_INFO_MAX_SIZE; i++) {
//                final LinearLayout forecastInfoLayout = (LinearLayout)
//                        getLayoutInflater().inflate(R.layout.forecastinfo, null);
//                final TextView tvWeather = (TextView) forecastInfoLayout.findViewById(R.id.textview_forecast_info);
                final WeatherInfo.ForecastInfo forecastInfo = weatherInfo.getForecastInfoList().get(i);
//                tvWeather.setText("====== FORECAST " + (i + 1) + " ======" + "\n" +
//                        "date: " + forecastInfo.getForecastDate() + "\n" +
//                        "weather: " + forecastInfo.getForecastText() + "\n" +
//                        "low  temperature in ºC: " + forecastInfo.getForecastTempLow() + "\n" +
//                        "high temperature in ºC: " + forecastInfo.getForecastTempHigh() + "\n"
//                );
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
}