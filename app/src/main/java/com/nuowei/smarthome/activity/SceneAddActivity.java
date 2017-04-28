package com.nuowei.smarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.kyleduo.switchbutton.SwitchButton;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.MainLeftAdapter;
import com.nuowei.smarthome.common.util.ToastUtils;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.manage.SubDeviceManage;
import com.nuowei.smarthome.modle.LeftMain;
import com.nuowei.smarthome.modle.Scene;
import com.nuowei.smarthome.modle.SubDevice;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.Json.ZigbeeGW;
import com.nuowei.smarthome.smarthomesdk.bean.SceneBean;
import com.nuowei.smarthome.smarthomesdk.utils.XlinkUtils;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.util.Stringtotype;
import com.nuowei.smarthome.util.Time;
import com.nuowei.smarthome.view.pickerview.TimePickerView;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.wevey.selector.dialog.DialogInterface;
import com.wevey.selector.dialog.MDEditDialog;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/3/31 11:37
 * @Description : 场景添加设备
 */
public class SceneAddActivity extends BaseActivity {
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.rl_add_execution)
    RelativeLayout rlAddExecution;
    @BindView(R.id.image_Add)
    ImageView imageAdd;
    @BindView(R.id.list_execution)
    ListView listExecution;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.sw_timer)
    SwitchButton swTimer;
    @BindView(R.id.rl_repeat)
    RelativeLayout rlRepeat;
    @BindView(R.id.image_more)
    ImageView imageMore;
    @BindView(R.id.rl_coutdown)
    RelativeLayout rlCoutdown;
    @BindView(R.id.image_more1)
    ImageView imageMore1;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tv_timer)
    AvenirTextView tvTimer;
    @BindView(R.id.tv_contdown)
    AvenirTextView tvContdown;


    private MainLeftAdapter adapter;

    private TimePickerView pvTime;

    private List<LeftMain> list;
    private final static int REQUEST_CODE = 1;
    private final static int SUB_DEVICE_CODE = 2;
    private int wk = 0;
    private int hour = 0;
    private int min = 0;
    private String gwMac;
    private String scenName;
    private String scenLoad;
    private int seconds = 0;
    private int deviceType = 0;
    private int scenId = 0;
    private int timerEnabler = 0;
    private int delayTimer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_add);
        initData();
        initEven();
    }

    private void initData() {
        list = new ArrayList<LeftMain>();
        adapter = new MainLeftAdapter(this, list);
        listExecution.setAdapter(adapter);
        Intent intent = getIntent();
//        Constants.Scene_Load, scene.getLoad().toString()
        scenId = intent.getIntExtra(Constants.Scene_sceneID, 0);
        scenName = getResources().getString(R.string.SceneName);
        if (scenId != 0) {
            gwMac = intent.getStringExtra(Constants.DEVICE_MAC);
            scenName = intent.getStringExtra(Constants.DEVICE_NAME);
            timerEnabler = intent.getIntExtra(Constants.Scene_TimeEnable, 0);
            hour = intent.getIntExtra(Constants.Scene_Hour, 0);
            min = intent.getIntExtra(Constants.Scene_Min, 0);
            wk = intent.getIntExtra(Constants.Scene_Wk, 0);
            delayTimer = intent.getIntExtra(Constants.Scene_countTime, 0);
            intent.getIntExtra(Constants.DEVICE_TYPES, 0);
            scenLoad = intent.getStringExtra(Constants.Scene_Load);
            try {
                JSONArray jsonArray = new JSONArray(scenLoad);
                MyApplication.getLogger().i("123");
                int iSize = jsonArray.length();
                MyApplication.getLogger().i("123");
                for (int i = 0; i < iSize; i++) {
                    MyApplication.getLogger().i("123");
                    String lodtext = jsonArray.getString(i);
                    String indexstring = lodtext.substring(0, lodtext.indexOf("."));
                    String activionstring = lodtext.substring(lodtext.indexOf(".") + 1, lodtext.length());
                    MyApplication.getLogger().i("123");
                    int index = Integer.parseInt(indexstring);
                    int activion = Integer.parseInt(activionstring);
                    MyApplication.getLogger().i("index:" + indexstring + "activion:" + activionstring);
                    if (index == 1) {
                        LeftMain leftMain = new LeftMain(MyUtil.getDeviceToImage(Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY), MainActivity.getChoiceGwDevice().getDeviceName() + "\t" + MyUtil.getSceneactivon(MyApplication.getMyApplication(), Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY, activion), Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY);
                        leftMain.setActivion(activion);
                        leftMain.setIndex(index);
                        list.add(leftMain);
                    } else {
                        SubDevice subDevice = SubDeviceManage.getInstance().getDevice(MainActivity.getChoiceGwDevice().getDeviceMac(), index);
                        LeftMain leftMain = new LeftMain(MyUtil.getDeviceToImage(subDevice.getDeviceType()), subDevice.getDeviceName() + "\t" + MyUtil.getSceneactivon(MyApplication.getMyApplication(), subDevice.getDeviceType(), activion), subDevice.getDeviceType());
                        leftMain.setActivion(activion);
                        leftMain.setIndex(index);
                        list.add(leftMain);
                    }
                }
                listExecution.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }

        }
    }

    private void initEven() {
        tvTitle.setText(getResources().getString(R.string.add_scene));

        if (timerEnabler == 0) {
            swTimer.setChecked(false);
            rlRepeat.setVisibility(View.GONE);
        } else {
            swTimer.setChecked(true);
            rlRepeat.setVisibility(View.VISIBLE);
        }

        tvContdown.setText(String.format("%02d", Time.hous(delayTimer)) + ":" + String.format("%02d", Time.cal(delayTimer)) + ":" + String.format("%02d", Time.calsss(delayTimer)));
        if (wk == 255 && hour == 255 && min == 255) {
            tvTimer.setText(MyUtil.getWkString(MyApplication.getMyApplication(), wk) + " " + "00" + ":" + "00");
        } else {
            tvTimer.setText(MyUtil.getWkString(MyApplication.getMyApplication(), wk) + " " + String.format("%02d", hour) + ":" + String.format("%02d", min));

        }

        swTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rlRepeat.setVisibility(View.VISIBLE);
                    timerEnabler = 1;
                } else {
                    rlRepeat.setVisibility(View.GONE);
                    timerEnabler = 0;
                }
            }
        });
        Calendar selectedDate = Calendar.getInstance();
//        selectedDate.setTime();
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                tvContdown.setText(String.format("%02d", Time.getHour(date)) + ":" + String.format("%02d", Time.getMinute(date)) + ":" + String.format("%02d", Time.getSecond(date)));
                delayTimer = Time.getHour(date) * 3600 + Time.getMinute(date) * 60 + Time.getSecond(date);

            }
        }).setType(TimePickerView.Type.HOURS_MINS_SECOND)
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(getResources().getColor(R.color.divider))
                .setContentSize(20)
                .setDate(selectedDate)
                .build();

    }

    @OnClick(R.id.rl_add_execution)
    public void onAddExecution(View v) {
        startActivityForResult(new Intent(SceneAddActivity.this, ScenenChoiceActivity.class), SUB_DEVICE_CODE);
    }

    @OnClick(R.id.rl_repeat)
    public void onRepeat(View v) {
        startActivityForResult(new Intent(SceneAddActivity.this, RepeatActivity.class), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RepeatActivity.RESULT_CODE) {
                    Bundle bundle = data.getExtras();
                    wk = bundle.getInt("wk");
                    hour = bundle.getInt("hour");
                    min = bundle.getInt("min");
                    tvTimer.setText(MyUtil.getWkString(MyApplication.getMyApplication(), wk) + " " + String.format("%02d", hour) + ":" + String.format("%02d", min));
                }
                break;
            case SUB_DEVICE_CODE:
                if (resultCode == ScenenChoiceActivity.SUB_DEVICE_CODE) {
                    Bundle bundle = data.getExtras();
                    gwMac = bundle.getString(Constants.GATEWAY_MAC);
                    String subMac = bundle.getString(Constants.ZIGBEE_MAC);
                    String action = bundle.getString("action");
                    deviceType = bundle.getInt(Constants.DEVICE_TYPES);
                    boolean isGw = bundle.getBoolean("isGw");
                    MyApplication.getLogger().i("gwMac:" + gwMac + "subMac:" + subMac + "action:" + action + "isGW" + isGw);
                    if (!isGw) {
                        SubDevice subDevice = SubDeviceManage.getInstance().getDevice(gwMac, subMac);
                        LeftMain leftMain = new LeftMain(MyUtil.getDeviceToImage(subDevice.getDeviceType()), subDevice.getDeviceName() + "\t" + action, 1);
                        leftMain.setActivion(MyUtil.Sceneactivon(MyApplication.getMyApplication(), action));
                        leftMain.setIndex(subDevice.getIndex());
                        list.add(leftMain);
                    } else {
                        XlinkDevice xlinkDevice = DeviceManage.getInstance().getDevice(gwMac);
                        LeftMain leftMain = new LeftMain(MyUtil.getDeviceToImage(xlinkDevice.getDeviceType()), xlinkDevice.getDeviceName() + "\t" + action, deviceType);
                        leftMain.setActivion(MyUtil.Sceneactivon(MyApplication.getMyApplication(), action));
                        leftMain.setIndex(1);
                        list.add(leftMain);
                    }
                    adapter.notifyDataSetChanged();
                    listExecution.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @OnClick(R.id.rl_coutdown)
    public void onCoutdown(View v) {
        pvTime.show();
    }

    @OnClick(R.id.tv_right)
    void onRight() {
//        startActivity(new Intent(SceneAddActivity.this, DiaryActivity.class));
        showDialog();
    }

    private void showDialog() {
        new MDEditDialog.Builder(SceneAddActivity.this).setTitleVisible(true)
                .setTitleText(getResources().getString(R.string.add_scene))
                .setTitleTextSize(20)
                .setTitleTextColor(R.color.black_light)
                .setContentText(scenName)
                .setContentTextSize(18)
                .setMaxLength(12)
                .setHintText(getResources().getString(R.string.SceneName))
                .setMaxLines(1)
                .setContentTextColor(R.color.colorPrimary)
                .setButtonTextSize(14)
                .setLeftButtonTextColor(R.color.colorPrimary)
                .setLeftButtonText(getResources().getString(R.string.cancel))
                .setRightButtonTextColor(R.color.colorPrimary)
                .setRightButtonText(getResources().getString(R.string.dialog_ok))
                .setLineColor(R.color.colorPrimary)
                .setInputTpye(InputType.TYPE_CLASS_TEXT)
                .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<MDEditDialog>() {

                    @Override
                    public void clickLeftButton(MDEditDialog dialog, View view) {

//                        dialog.getEditTextContent();
                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(final MDEditDialog dialog, View view) {
                        final KProgressHUD hud = KProgressHUD.create(SceneAddActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel(getResources().getString(R.string.Send))
                                .setCancellable(true);
                        hud.show();
                        dialog.dismiss();
                        if (MainActivity.getChoiceGwDevice().getDeviceState() == 0) {
                            XlinkAgent.getInstance().connectDevice(MainActivity.getChoiceGwDevice().getxDevice(), MainActivity.getChoiceGwDevice().getAccessKey(), new ConnectDeviceListener() {
                                @Override
                                public void onConnectDevice(XDevice xDevice, int ret) {
                                    if (ret < 0) {
                                        MyApplication.getLogger().i("连接失败:" + ret);
                                        XlinkUtils.shortTips(getString(R.string.deviceoffline));
                                        hud.dismiss();
                                    } else {
                                        MainActivity.getChoiceGwDevice().setDeviceState(1);
                                        int i = XlinkAgent.getInstance().sendPipeData(xDevice, setScene(dialog.getEditTextContent()).getBytes(), new SendPipeListener() {
                                            @Override
                                            public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                                hud.dismiss();
                                            }
                                        });
                                        if (i > 0) {
                                            finish();
                                        } else {
                                            ToastUtils.showShortToast(MyApplication.getMyApplication(), getString(R.string.Adderror));
                                        }
                                    }
                                }
                            });
                        } else {
                            int i = XlinkAgent.getInstance().sendPipeData(MainActivity.getChoiceGwDevice().getxDevice(), setScene(dialog.getEditTextContent()).getBytes(), new SendPipeListener() {
                                @Override
                                public void onSendLocalPipeData(XDevice xDevice, int i, int i1) {
                                    hud.dismiss();
                                }
                            });
                            if (i > 0) {
                                finish();
                            } else {
                                ToastUtils.showShortToast(MyApplication.getMyApplication(), getString(R.string.Adderror));
                            }
                        }
                    }
                })
                .setMinHeight(0.3f)
                .setWidth(0.8f)
                .build()
                .show();
    }

    private String setScene(String scenName) {
        SceneBean sceneBean = new SceneBean();
        List<String> stringList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            stringList.add(list.get(i).getIndex() + "." + list.get(i).getActivion());
            MyApplication.getLogger().i("action:" + list.get(i).getText() + "deviceType:" + list.get(0).getDeviceType());
        }
        sceneBean.setLoad(stringList);
        getSeneID();
        sceneBean.setCountTime(delayTimer);
        sceneBean.setName(scenName);
        sceneBean.setSceneID(scenId);
        sceneBean.setTimeEnable(timerEnabler);
        SimpleDateFormat mm = new SimpleDateFormat("MM");
        String month = mm.format(new java.util.Date());
        SimpleDateFormat ddd = new SimpleDateFormat("dd");
        String day = ddd.format(new java.util.Date());
        int timeType = 0;
        MyApplication.getLogger().i("wk:" + wk + "hour:" + hour + "min:" + min);
        if (wk == 0x80) {
            timeType = 1;
        } else {
            timeType = 2;
        }
        SceneBean.TimeBean timeBean = new SceneBean.TimeBean();
        timeBean.setDay(Stringtotype.stringToInt(day));
        timeBean.setMonth(Stringtotype.stringToInt(month));
        timeBean.setHour(hour);
        timeBean.setMin(min);
        timeBean.setWkflag(wk);
        timeBean.setType(timeType);
        sceneBean.setTime(timeBean);
        String Scene = ZigbeeGW.SetScene(MyApplication.getMyApplication().getUserInfo().getNickname(), sceneBean);
        MyApplication.getLogger().json(Scene);
        return Scene;
    }

    private void getSeneID() {
        boolean a = false, b = false, c = false, d = false, e = false, f = false;
        int aa = 0, bb = 0, cc = 0, dd = 0, ee = 0, ff = 0, ss = 0;
        ArrayList<Scene> listDev = SceneActivity.getScene();
        for (int i = 0; i < listDev.size(); i++) {
            switch (listDev.get(i).getSceneID()) {
                case 1:
                    a = true;
                    aa = 1;
                    break;
                case 2:
                    b = true;
                    bb = 2;
                    break;
                case 3:
                    c = true;
                    cc = 3;
                    break;
                case 4:
                    d = true;
                    dd = 4;
                    break;
                case 5:
                    e = true;
                    ee = 5;
                    break;
                case 6:
                    f = true;
                    ff = 6;
                    break;
            }
        }
        if (scenId == 0) {
            if (a && b && c && d && e && f) {
                XlinkUtils.shortTips(getResources().getString(R.string.GWONE));
            } else if (a && b && c && d && e) {
                scenId = 6;
            } else if (a && b && c && d && f) {
                scenId = 5;
            } else if (a && b && c && e && f) {
                scenId = 4;
            } else if (a && b && e && c && d && f) {
                scenId = 3;
            } else if (a && e && c && d && f) {
                scenId = 2;
            } else if (e && b && c && d && f) {
                scenId = 1;
            } else {
                if (1 == aa) {
                    if (2 == bb) {
                        if (3 == cc) {
                            if (4 == dd) {
                                if (5 == ee) {
                                    if (6 == ff) {
                                    } else {
                                        scenId = 6;
                                    }
                                } else {
                                    scenId = 5;
                                }
                            } else {
                                scenId = 4;
                            }
                        } else {
                            scenId = 3;
                        }
                    } else {
                        scenId = 2;
                    }
                } else {
                    scenId = 1;
                }
            }
        }
    }

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implement
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }


}
