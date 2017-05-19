package com.jwkj.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;


import com.jwkj.adapter.ExpandLvSensorAdapter;
import com.jwkj.data.Contact;
import com.jwkj.entity.Sensor;
import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.selectdialog.SelectItem;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.nuowei.ipclibrary.R;

import java.util.Arrays;

/**
 * Created by dxs on 2016/1/18.
 */
public class ModifySensorActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private TextView txTitle;
    private ExpandableListView mListView;
    private ExpandLvSensorAdapter mAdapter;
    private Contact contact;
    private Context mContext;
    private Sensor sensor;
    private int[] Groups = new int[]{R.string.mode_text_home, R.string.mode_text_office, R.string.mode_text_sleep};
    private int[] items = new int[]{R.string.sensor_music, R.string.sensor_push, R.string.sensor_catchimage, R.string.sensor_recode};
    private Button btnOk;
    //private Sensor sensorTemp;
    private boolean isRegFilter = false;
    private int position;
    private int selectGrop;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_modifysensor);
        sensor = (Sensor) getIntent().getSerializableExtra("sensor");
        contact = (Contact) getIntent().getSerializableExtra("contact");
        position = getIntent().getIntExtra("position", -1);
        initUI();
        regFilter();
        ExpandList(sensor);
    }

    private void initUI() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        txTitle = (TextView) findViewById(R.id.tx_title);
        txTitle.setText(sensor.getName());
        mListView = (ExpandableListView) findViewById(R.id.elv_sensor);
        mAdapter = new ExpandLvSensorAdapter(mContext, Groups, items, sensor);
        mListView.setGroupIndicator(null);
        mListView.setChildDivider(null);
        mListView.setDivider(null);
        mListView.setAdapter(mAdapter);
        mListView.setOnGroupClickListener(GroupListner);
        mAdapter.setOnExPanChildClickListner(listner);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setVisibility(View.GONE);
        btnOk.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.ACK_FISHEYE);
        filter.addAction(Constants.P2P.RET_TURN_SENSOR);//此页面不再接受开关控制
        filter.addAction(Constants.P2P.RET_SET_SENSER_WORKMODE);
        filter.addAction(Constants.P2P.RET_SET_ONE_SPECIAL_ALARM);
        mContext.registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int iSrcID = intent.getIntExtra("iSrcID", 0);
            byte boption = intent.getByteExtra("boption", (byte) -1);
            byte[] data = intent.getByteArrayExtra("data");
            if (intent.getAction().equals(Constants.P2P.ACK_FISHEYE)) {
                int arg1 = intent.getIntExtra("arg1", 0);
                int result = intent.getIntExtra("result", 0);
                //ACK回调
            } else if (intent.getAction().equals(Constants.P2P.RET_TURN_SENSOR)) {
                //设置开关
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    setSensorSwitch();
                } else if (boption == Constants.FishBoption.MESG_SENSOR_NOT_LEARN_YET) {
                    //无此传感器
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_SET_SENSER_WORKMODE)) {
                //设置传感器
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    Log.e("dxsSensor","data------>"+ Arrays.toString(sensor.getSensorData()));
                    sensor.upDataSensorData(data, 3);
                    Log.e("dxsSensor", "data------>" + Arrays.toString(sensor.getSensorData()));
                    ExpandList(sensor);
                    mAdapter.notifyDataSetChanged();
                } else if (boption == Constants.FishBoption.MESG_SET_SENSOR_WORK_MODE_ERROR) {
                    //参数错误
                }
            }else if (intent.getAction().equals(Constants.P2P.RET_SET_ONE_SPECIAL_ALARM)) {
                //设置特殊传感器传感器
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    sensor.upDataSensorData(data, 4);
                    ExpandList(sensor);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    /**
     * 更新传感器总开关
     */
    private void setSensorSwitch() {
        if (sensor.getSensorSwitch()) {
            sensor.setSensorSwitch(false);
        } else {
            sensor.setSensorSwitch(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    private ExpandableListView.OnGroupClickListener GroupListner=new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return true;
        }
    };

    private ExpandLvSensorAdapter.onExPanChildClickListner listner = new ExpandLvSensorAdapter.onExPanChildClickListner() {
        @Override
        public void onGroupClick(int groupPosition, Sensor sensor) {
            setSensorData(sensor);
            showLoadingDialog(listener, 1);
        }

        @Override
        public void onChildClick(int groupPosition, int childPosition, Sensor sensor) {
            if (childPosition == 0) {
                //选择提示音
                showSelectDialog(sensor, groupPosition);
            } else {
                //选中，此处是单击一次交互一次
                setSensorData(sensor);
                showLoadingDialog(listener, 1);
            }
        }
    };

    //复用布局
    private void showSelectDialog(Sensor sensor, int groupPosition) {
        selectGrop=groupPosition;
        NormalDialog dialog = new NormalDialog(mContext);
        dialog.setTitle(R.string.sensor_music);
        dialog.setBtn1_str(Utils.getStringForId(R.string.yes));
        WorkScheduleGroup group = new WorkScheduleGroup();
        group.setbWeekDay(sensor.getSensorModeInfo(groupPosition));
        dialog.showSelectListDialog(group, NormalDialog.SelectType_Sigle);
        dialog.setOnDialogSelectListner(moreSelect);
    }

    /**
     * 根据Sensor的模式启用情况展开或闭合组
     * @param sensor
     */
    private void ExpandList(Sensor sensor){
        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
            if(sensor.getSensorStateAtMode(i,7)){
                mListView.expandGroup(i);
            }else{
                mListView.collapseGroup(i);
            }
        }
    }

    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_MODIFYSENSOR;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            CallBackdata();
            finish();

        } else if (i == R.id.btn_ok) {//                sensorTemp = mAdapter.getSensor();
//                setSensorData(sensorTemp);
//                showLoadingDialog(listener, 1);

//            case R.id.iv_sensor_switch:
//                iv_sensor_switch.setModeStatde(SwitchView.State_progress);
//                if (sensor.getSensorSwitch()) {
//                    changeSensorSwitch((byte) 0);
//                } else {
//                    changeSensorSwitch((byte) 1);
//                }
//                break;
        }
    }

    /**
     * 打开或关闭一个传感器
     *
     * @param state 开关状态
     */
    private void changeSensorSwitch(byte state) {
        if(sensor.getSensorCategory()==Sensor.SENSORCATEGORY_NORMAL){
            FisheyeSetHandler.getInstance().sTurnSensor(contact.contactId, contact.contactPassword, sensor.getSensorSignature(), state);
        }else {
            if(state==1){
                sensor.setSensorSwitch(true);
            }else {
                sensor.setSensorSwitch(false);
            }
            setSensorData(sensor);
        }

    }

    /**
     * 设置传感器
     *
     * @param sensor
     */
    private void setSensorData(Sensor sensor) {
        if(sensor.getSensorCategory()==Sensor.SENSORCATEGORY_NORMAL) {
            FisheyeSetHandler.getInstance().sSettingSensorWorkMode(contact.contactId, contact.contactPassword, sensor.getSensorData());
        }else{
            FisheyeSetHandler.getInstance().sSpecialAlarmData(contact.contactId, contact.contactPassword, sensor.getSensorData(),sensor.getPrepoint());
        }
    }

    private NormalDialog.OnCustomCancelListner listener = new NormalDialog.OnCustomCancelListner() {
        @Override
        public void onCancle(int mark) {
            if (mark == 1) {

            }else if(mark==2){
                //设置某个模式下的音乐被动消失
            }
        }
    };
    @Override
    public void onBackPressed() {
        CallBackdata();
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(isRegFilter){
    		unregisterReceiver(mReceiver);
    		isRegFilter=false;
    	}
    }
    private void CallBackdata() {
        Intent resu = new Intent();
        resu.putExtra("sensor", sensor);
        resu.putExtra("position", position);
        ModifySensorActivity.this.setResult(RESULT_OK, resu);
    }

    //多选(设备中是从星期天开始计算)
    private NormalDialog.onDialogSelectListner moreSelect = new NormalDialog.onDialogSelectListner() {
        @Override
        public void onItemClick(View v, SelectItem item, int position) {
            //单项被点击，可尝试播放此音乐
        }

        @Override
        public void onOkClick(AlertDialog dialog, WorkScheduleGroup grop) {
            sensor.setSensorModeInfo(selectGrop, grop.getbWeekDay());
            dialog.dismiss();
            showLoadingDialog(listener,2);
            setSensorData(sensor);
        }
    };
  

}
