package com.nuowei.smarthome.activity;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

import com.google.gson.Gson;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainTAdapter;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.modle.MainDatas;
import com.nuowei.smarthome.modle.Messages;
import com.nuowei.smarthome.modle.MessagesContent;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.util.SharePreferenceUtil;
import com.nuowei.smarthome.util.Time;
import com.nuowei.smarthome.view.gridview.DragGridView;
import com.orhanobut.hawk.Hawk;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.ParseException;
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
    public String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_main);
        userName = Hawk.get("MY_ACCOUNT");
        ButterKnife.bind(this);
        initData();
        initEven();
        initDevice();

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


    private void initDevice() {

        HttpManage.getInstance().getSubscribe(MyApplication.getMyApplication(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                MyApplication.getLogger().e("获取订阅设备出错:" + error.getMsg() + "\t" + error.getCode());
            }

            @Override
            public void onSuccess(int code, String response) {
                MyApplication.getLogger().json(response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray list = object.getJSONArray("list");
                    int iSize = list.length();
                    for (int i = 0; i < iSize; i++) {
                        JSONObject jsonObj = list.getJSONObject(i);
                        MyUtil.subscribeToDevice(jsonObj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getMessage("2014-10-09T08:15:40.843Z");
            }
        });
    }

    int MAX_LIMIT = 0;
    int MAX_Refresh = 100;
    public boolean isRefreshMessage = true;

    private void getMessage(final String CreateDate) {
        try {
            HttpManage.getInstance().GetMessagesID(MyApplication.getMyApplication(), MAX_LIMIT + "", MAX_Refresh + "", MyUtil.getdeviceid(), CreateDate, new HttpManage.ResultCallback<String>() {
                @Override
                public void onError(Header[] headers, HttpManage.Error error) {
                    MyApplication.getLogger().e("获取消息失败:" + error.getMsg() + "\t" + error.getCode());
                }

                @Override
                public void onSuccess(int code, String response) {
                    MyApplication.getLogger().json(response);
                    Gson gaon = new Gson();
                    Messages messages = gaon.fromJson(response, Messages.class);
                    List<DataDevice> datalist = new ArrayList<DataDevice>();
                    List<Messages.ListBean> listsize = messages.getList();
                    for (int i = 0; i < listsize.size(); i++) {
                        DataDevice datadevice = new DataDevice();
                        datadevice.setMessageID(listsize.get(i).getId());
                        datadevice.setMessageType(listsize.get(i).getType());
                        datadevice.setNotifyType(listsize.get(i).getNotify_type());
                        datadevice.setPush(listsize.get(i).isIs_push());
                        datadevice.setRead(listsize.get(i).isIs_read());
                        datadevice.setAlertName(listsize.get(i).getAlert_name());
                        datadevice.setAlertValue(listsize.get(i).getAlert_value());
                        datadevice.setCreateDate(listsize.get(i).getCreate_date());
                        datadevice.setUserid(MyApplication.getMyApplication().getAppid() + "");
                        datadevice.setUserName(userName);
                        try {


                            MessagesContent messagesconte = gaon.fromJson(listsize.get(i).getContent(), MessagesContent.class);

                            XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(listsize.get(i).getFrom());

                            datadevice.setXlinkDevice(xlinkDevice);
                            datadevice.setDeviceId(listsize.get(i).getFrom() + "");
                            datadevice.setDeviceMac(xlinkDevice.getDeviceMac());

                            if (MyUtil.isEmptyString(messagesconte.getPlugMac())) {
//                                SubDevice subDevice = SubDeviceManage.getInstance().getDevice(xlinkDevice.getDeviceMac(), messagesconte.getZigbeeMac());


//                                datadevice.setSubID(subDevice.getIndex() + "");

//                                datadevice.setSubDevice(subDevice);

                                String timer = messagesconte.getNotification().getBody_loc_args().get(0);
                                datadevice.setActionName(messagesconte.getNotification().getTitle());
                                MyApplication.getLogger().i(timer);
                                try {
                                    Date date = Time.stringToDate(timer, "yyyy-MM-dd HH:mm:ss");
                                    datadevice.setDate(date);
                                    datadevice.setYear(Time.dateToString(date, "yyyy"));
                                    datadevice.setMonth(Time.dateToString(date, "MM"));
                                    datadevice.setDay(Time.dateToString(date, "dd"));
                                    datadevice.setHH(Time.dateToString(date, "HH"));
                                    datadevice.setMm(Time.dateToString(date, "mm"));
                                    datadevice.setSs(Time.dateToString(date, "ss"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                String loc_key = messagesconte.getNotification().getBody_loc_key();
                                datadevice.setBodyLocKey(loc_key);
                                datadevice.setSubType(MyUtil.Gettype(loc_key) + "");
                                if (!MyUtil.isEmptyString(messagesconte.getZigbeeMac())) {
                                    datadevice.setSubMac(messagesconte.getZigbeeMac());
                                }
                                if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_Temperature) {
                                    try {
                                        datadevice.setHumidity(messagesconte.getNotification().getBody_loc_args().get(1));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(2));
                                    } catch (Exception e) {
                                    }
                                } else if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_ZIGBEE_Metering_Plugin) {
                                    try {
                                        datadevice.setHumidity(messagesconte.getNotification().getBody_loc_args().get(1));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        if (messagesconte.getNotification().getBody_loc_args().get(2).equals("NA")) {
                                            datadevice.setHumidity(messagesconte.getNotification().getBody_loc_args().get(1) + "NA");
                                        }
                                    } catch (Exception e) {
                                    }
                                    try {
                                        datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(3));
                                    } catch (Exception e) {
                                    }
                                } else if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_WIFI_AIR) {
                                    try {
                                        datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(1).replaceAll(" ", ""));
                                    } catch (Exception e) {
                                    }
                                } else if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_WIFI_GAS) {
                                    try {
                                        datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(1));
                                    } catch (Exception e) {
                                    }
                                } else if (MyUtil.Gettype(loc_key) == Constants.DEVICE_TYPE.DEVICE_WIFI_RC) {
                                    datadevice.setTemp(messagesconte.getNotification().getBody_loc_args().get(1));

                                }

                            } else {
                                if (!MyUtil.isEmptyString(messagesconte.getMessage().getElectricity())) {
                                    datadevice.setElectricity(messagesconte.getMessage().getElectricity());
                                    datadevice.setActionName("electricity_1042");
                                } else {
                                    datadevice.setActionName("onoff__1042");

                                    if (!MyUtil.isEmptyString(messagesconte.getMessage().getSID())) {
                                        if (messagesconte.getMessage().isOnoff()) {
                                            datadevice.setBodyLocKey(messagesconte.getMessage().getSID() + "_true");
                                        } else {
                                            datadevice.setBodyLocKey(messagesconte.getMessage().getSID() + "_false");
                                        }
                                    } else {
                                        if (messagesconte.getMessage().isOnoff()) {
                                            datadevice.setBodyLocKey("true");
                                        } else {
                                            datadevice.setBodyLocKey("false");
                                        }
                                    }
                                }
                                String timer = messagesconte.getMessage().getTime();
                                MyApplication.getLogger().i(timer);
                                try {
                                    Date date = Time.stringToDate(timer, "yyyy-MM-dd HH:mm:ss");
                                    datadevice.setDate(date);
                                    datadevice.setYear(Time.dateToString(date, "yyyy"));
                                    datadevice.setMonth(Time.dateToString(date, "MM"));
                                    datadevice.setDay(Time.dateToString(date, "dd"));
                                    datadevice.setHH(Time.dateToString(date, "HH"));
                                    datadevice.setMm(Time.dateToString(date, "mm"));
                                    datadevice.setSs(Time.dateToString(date, "ss"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        } catch (Exception e) {
                        }
                        datalist.add(datadevice);
                    }
                    DataSupport.saveAll(datalist);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
