package com.nuowei.smarthome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nuowei.smarthome.modle.IpMac;
import com.nuowei.smarthome.yahoo.WeatherInfo;
import com.nuowei.smarthome.yahoo.YahooWeather;
import com.nuowei.smarthome.yahoo.YahooWeatherExceptionListener;
import com.nuowei.smarthome.yahoo.YahooWeatherInfoListener;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/1/9 13:33
 * @Description :主程序
 */
public class MainActivity extends AppCompatActivity implements YahooWeatherExceptionListener, YahooWeatherInfoListener {

    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginResult loginResult = NetManager.getInstance(this).createLoginResult(NetManager.getInstance(this).login("554674787@qq.com", "8888"));
        MyApplication.getLogger().i(loginResult.contactId + "\n" + loginResult.error_code);
        String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                MyApplication.getLogger().e("\t"+e.getMessage());
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