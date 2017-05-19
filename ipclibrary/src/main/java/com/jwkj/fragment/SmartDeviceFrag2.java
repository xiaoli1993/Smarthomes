package com.jwkj.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jwkj.activity.BindingLocationActivity2;
import com.jwkj.activity.ModifySensorActivity;
import com.jwkj.adapter.SensorRecycleAdapter2;
import com.jwkj.adapter.SensorRecycleAdapter2.ViewHolder;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.Prepoint;
import com.jwkj.entity.Sensor;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.utils.FishAckUtils;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.EditorAndDeletePop;
import com.jwkj.widget.ImputDialog;
import com.jwkj.widget.NormalDialog;
import com.libzxing.activity.CaptureActivity;
import com.p2p.core.P2PHandler;
import com.nuowei.ipclibrary.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dxs on 2016/1/12.
 */
public class SmartDeviceFrag2 extends BaseFragment implements View.OnClickListener {
    private Context mContext;
    private static final String SmartDevice = "Gwell:";
    private static final int PopH = 142;
    private int device_type = 0;
    private RecyclerView mListView;
    private ImageView ivAddSensor;
    private RelativeLayout loading;
    private boolean isRegFilter = false;
    private Contact contact;
    private SensorRecycleAdapter2 mAdapter;
    private List<Sensor> sensors = new ArrayList<Sensor>();
    private EditorAndDeletePop pop;
    private int modifySensorPostion = -1;//准备修改的传感器编号
    private String nameTemp = "";//用户输入的名字缓存
    private static final int PASER_SPECIAL_SENSOR_DATA_LENGTH=44;
    private boolean isNewData=false;
    private ArrayList<Integer> allPrePoint=new ArrayList<Integer>();
    private Prepoint prepoint;
    boolean isSurpporPrepoint=false;

    private ViewHolder holder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //回调函数赋值
        if (!(getActivity() instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_control2, container,
                false);
        mContext = getActivity();
        contact = (Contact) getArguments().getSerializable("contact");
        device_type = getArguments().getInt("type", 0);
        prepoint = DataManager.findPrepointByDevice(mContext,
				contact.contactId);
        initUI(view);
        initData();
        return view;
    }

    private void initUI(View view) {
        mListView = (RecyclerView) view.findViewById(R.id.rlv_sensor);
        ivAddSensor = (ImageView) view.findViewById(R.id.iv_add_sensor);
        loading=(RelativeLayout)view.findViewById(R.id.loading);
        mAdapter = new SensorRecycleAdapter2(mContext, sensors,prepoint);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
//        mListView.addItemDecoration(new SpaceItemDecoration(6));//间隔布局
        mListView.setAdapter(mAdapter);
        mAdapter.setOnSensorRecycleAdatperClickListner(listner);
        ivAddSensor.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        regFilter();
    }

    @Override
    public void onStart() {
        super.onStart();
        //将自己的实例传出去
        backHandlerInterface.setSelectedFragment(this);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.ACK_FISHEYE);
        filter.addAction(Constants.P2P.RET_GET_SENSOR_WORKMODE);
        filter.addAction(Constants.P2P.RET_INTO_LEARN_STATE);
        filter.addAction(Constants.P2P.RET_DELETE_ONE_CONTROLER);
        filter.addAction(Constants.P2P.RET_DELETE_ONE_SENSOR);
        filter.addAction(Constants.P2P.RET_CHANGE_CONTROLER_NAME);
        filter.addAction(Constants.P2P.RET_CHANGE_SENSOR_NAME);
        filter.addAction(Constants.P2P.RET_GET_ALL_SPECIAL_ALARM);
        filter.addAction(Constants.P2P.RET_GET_LAMPSTATE);
        filter.addAction(Constants.P2P.RET_QRCODE_LEARN);
        filter.addAction(Constants.P2P.RET_TURN_SENSOR);
        filter.addAction(Constants.P2P.RET_SET_ONE_SPECIAL_ALARM);
        filter.addAction(Constants.P2P.RET_GET_PRESETMOTOROS);
        filter.addAction(Constants.P2P.ACK_RET_PRESET_POS);
        filter.addAction(Constants.P2P.GET_PREPOINT_SURPPORTE);
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
                getAddSensorACK(arg1, result);
            } else if (intent.getAction().equals(Constants.P2P.RET_GET_SENSOR_WORKMODE)) {
                //获取传感器防护计划返回
                if (boption == Constants.FishBoption.MESG_GET_OK) {
                    clearNormalSensor();
                    if(data.length<14){
						return;
					}
                    int len1=Utils.bytesToInt(data, 5);
                    int len2=Utils.bytesToInt(data, 10);
                    paserSensorData(data, data[4], len1, data[9], len2);
                    mAdapter.notifyDataSetChanged();
                    getLampState((byte) 1);
                } else {

                }
            } else if (intent.getAction().equals(Constants.P2P.RET_INTO_LEARN_STATE)) {
                getSensorData(data, boption);
            } else if (intent.getAction().equals(Constants.P2P.RET_DELETE_ONE_CONTROLER)) {
                //遥控删除
                deleteSensorResult(boption, data);
            } else if (intent.getAction().equals(Constants.P2P.RET_DELETE_ONE_SENSOR)) {
                //传感器删除
                deleteSensorResult(boption, data);
            } else if (intent.getAction().equals(Constants.P2P.RET_CHANGE_CONTROLER_NAME)) {
                //修改传感器名字
                modifySensorName(boption);
            } else if (intent.getAction().equals(Constants.P2P.RET_CHANGE_SENSOR_NAME)) {
                //修改遥控名字
                modifySensorName(boption);
            } else if (intent.getAction().equals(Constants.P2P.RET_GET_ALL_SPECIAL_ALARM)) {
                //获取所有的特殊传感器
                loading.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                if (boption == Constants.FishBoption.MESG_GET_OK) {
                    paserSpecialSensorData(data, data[3]);
                }
                getAllSensorData();
                P2PHandler.getInstance().getNpcSettings(contact.contactId, contact.contactPassword);
            } else if (intent.getAction().equals(Constants.P2P.RET_GET_LAMPSTATE)) {
                if (boption == Constants.FishBoption.MESG_GET_OK) {
                    Sensor sensor = getSensorByData(data, 4);
                    if (sensor != null) {
                        sensor.setLampState(data[3]);
                        mAdapter.notifyItemChanged(getSensorPosition(sensor));
                    } else {
                        Log.e("dxsTest", "sensor为空");
                    }
                    //获取超时
                }else if(boption == 10){
                	Sensor sensor = getSensorByData(data, 4);
                	getLampState((byte)1, sensor);
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_QRCODE_LEARN)) {
                //二维码扫描第一次返回
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    //进入学习
                } else if (boption == 24) {
                    //学习退出
                    T.showLong(mContext, R.string.qword_study_error);
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_TURN_SENSOR)) {
                //设置开关
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    Sensor sensor = getSensorByData(data, 4);
                    if (sensor != null) {
                        if (sensor.getSensorSwitch()) {
                            sensor.setSensorSwitch(false);
                        } else {
                            sensor.setSensorSwitch(true);
                        }
                        mAdapter.UpadateSensor(sensor);
                    }
                } else if (boption == Constants.FishBoption.MESG_SENSOR_NOT_LEARN_YET) {
                    //无此传感器
                }
            } else if (intent.getAction().equals(Constants.P2P.RET_SET_ONE_SPECIAL_ALARM)) {
                //设置特殊传感器传感器
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    Sensor sensor = getSensorByData(data, 4);
                    if (sensor != null) {
                        sensor.upDataSensorData(data, 4);
                        mAdapter.UpadateSensor(sensor);
                    }
                }
            }else if (intent.getAction().equals(
					Constants.P2P.RET_GET_PRESETMOTOROS)) {
				byte[] result = intent.getByteArrayExtra("result");
				if (result[1] == 1) {
					setAllPrePoint(Utils.getByteBinnery(result[3], true));
				} else if (result[1] == 84) {
					T.showShort(mContext, R.string.device_not_support);
				} else if (result[1] == -1) {
					T.showShort(mContext, R.string.device_not_location);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_PRESET_POS)) {
				int state = intent.getIntExtra("state", -1);
				if (state == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (state == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					if(isSurpporPrepoint){
						Utils.setPrePoints(contact.contactId,
								contact.contactPassword, 2, 0);
					}
				}
			}else if(intent.getAction().equals(Constants.P2P.GET_PREPOINT_SURPPORTE)){
				String deviceID=intent.getStringExtra("deviceId");
				int result=intent.getIntExtra("result", 0);
				if(deviceID.equals(contact.contactId)){
					if(result==Constants.SurportPrepoint.YES){//支持记忆点
						isSurpporPrepoint=true;
						Utils.setPrePoints(contact.contactId,
	                			contact.contactPassword, 2, 0);
						mAdapter.setSurpporPrepoint(isSurpporPrepoint);
						mAdapter.notifyDataSetChanged();
					}else if(result==Constants.SurportPrepoint.NO){
						isSurpporPrepoint=false;
						mAdapter.setSurpporPrepoint(isSurpporPrepoint);
					}
				}
			}
        }
    };

    private void initData() {
        //先获取特殊传感器，获取到之后再获取一般传感器
        getAllSpecialSensorData();
        //getAllSensorData();
    }

    /**
     * 获取所有的一般传感器
     */
    private void getAllSensorData() {
        FisheyeSetHandler.getInstance().sGetSenSorWorkMode(contact.contactId, contact.contactPassword);
    }

    /**
     * 获取所有的特殊传感器
     */
    private void getAllSpecialSensorData() {
        FisheyeSetHandler.getInstance().sGetAllSpecialAlarmData(contact.contactId, contact.contactPassword);
    }

    /**
     * 学习二维码扫描结果
     *
     * @param codeData
     */
    private void LearnQrCodeSensor(byte[] codeData) {
        FisheyeSetHandler.getInstance().sMesgQrcodeLearnDevice(contact.contactId, contact.contactPassword, (byte) 1, (byte) 0, codeData);
    }

    /**
     * 获取所有插座状态
     *
     * @param lamstate 1.查询  2.开  3.关闭
     */
    private void getLampState(byte lamstate) {
        for (Sensor sensor : sensors) {
            if (sensor.isControlSensor()) {
                getLampState(lamstate, sensor);
            }
        }
    }

    /**
     * 通过特征码获取列表的传感器
     *
     * @param data   原始数据
     * @param offset 数据偏移值
     * @return 不存在返回null
     */
    private Sensor getSensorByData(byte[] data, int offset) {
        byte[] dataTemp = new byte[4];
        byte[] sensorTemp = new byte[4];
        System.arraycopy(data, offset, dataTemp, 0, dataTemp.length);
        for (Sensor sensor : sensors) {
            byte[] sensorInfo = sensor.getSensorData();
            System.arraycopy(sensorInfo, 0, sensorTemp, 0, sensorTemp.length);
            if (Arrays.equals(sensorTemp, dataTemp)) {
                return sensor;
            }
        }
        return null;
    }

    /**
     * 获取sensor在列表的位子
     *
     * @param sensor
     * @return
     */
    private int getSensorPosition(Sensor sensor) {
        return sensors.indexOf(sensor);
    }

    /**
     * 获取或者设置插座状态
     *
     * @param lamstate 1.查询  2.开  3.关闭
     * @param sensor
     */
    private void getLampState(byte lamstate, Sensor sensor) {
        if (sensor!=null&&sensor.isControlSensor()) {
            FisheyeSetHandler.getInstance().sGetLampStatu(contact.contactId, contact.contactPassword, lamstate, sensor.getSensorData());
        }
    }

    private SensorRecycleAdapter2.onSensorRecycleAdatperClickListner listner = new SensorRecycleAdapter2.onSensorRecycleAdatperClickListner() {
        @Override
        public void onItemClick(View contentview, Sensor sensor, int position) {
            //单击进入
            if (sensor.getSensorCategory() == Sensor.SENSORCATEGORY_NORMAL && sensor.getSensorType() == Constants.SensorType.TYPE_REMOTE_CONTROLLER) {
                T.showShort(mContext, sensor.getName());
            } else if (sensor.isControlSensor()) {
                T.showShort(mContext, sensor.getName());
            } else {
                Intent modify = new Intent();
                modify.setClass(mContext, ModifySensorActivity.class);
                modify.putExtra("sensor", sensor);
                modify.putExtra("position", position);
                modify.putExtra("contact", contact);
                startActivityForResult(modify, 1);
            }
        }

        @Override
        public void onLongClick(ViewHolder holder, Sensor sensor, int position) {
            if (sensor.getSensorCategory() == Sensor.SENSORCATEGORY_NORMAL) {
                //长按编辑或删除
                pop = new EditorAndDeletePop(mContext, Utils.dip2px(mContext, PopH));
                pop.setSensor(sensor);
                pop.setPosition(position);
                pop.setHolder(holder);
                pop.setOnDeleteAndEditorListner(Poplistner);
                pop.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
            } else {
                //特殊传感器不可长按
//                T.showShort(mContext, "特殊传感器不可长按");
            }

        }

        @Override
        public void onSwitchClick(ViewHolder holder, Sensor sensor, int position) {
            //带开关的传感器的开关点击
            if (sensor.isControlSensor()) {
                if (sensor.getLampState() == 1 || sensor.getLampState() == 3) {
                    getLampState((byte) 3, sensor);
                } else if (sensor.getLampState() == 2 || sensor.getLampState() == 4) {
                    getLampState((byte) 2, sensor);
                }
                sensor.setLampState((byte) 0);
                mAdapter.notifyItemChanged(position);
            } else {
                if (sensor.getSensorSwitch()) {
                    changeSensorSwitch((byte) 1, sensor);
                } else {
                    changeSensorSwitch((byte) 2, sensor);
                }
            }
        }

		@Override
		public void toBindingPrePoint(ViewHolder holder, Sensor sensor,
				int position) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("mContact", contact);
			intent.putExtra("sensor", sensor);
			intent.putIntegerArrayListExtra("allLocation", allPrePoint);
			intent.putExtra("index", position);
			intent.setClass(mContext, BindingLocationActivity2.class);
			startActivityForResult(intent, 2);
		}
    };

    /**
     * 打开或关闭一个传感器
     *
     * @param state 开关状态 1关闭   2打开
     */
    private void changeSensorSwitch(byte state, Sensor sensor) {
        if (sensor.getSensorCategory() == Sensor.SENSORCATEGORY_NORMAL) {
            FisheyeSetHandler.getInstance().sTurnSensor(contact.contactId, contact.contactPassword, sensor.getSensorSignature(), state);
        } else {
            if (state == 1) {
                sensor.setSensorSwitch(false);
            } else {
                sensor.setSensorSwitch(true);
            }
            FisheyeSetHandler.getInstance().sSpecialAlarmData(contact.contactId, contact.contactPassword, sensor.getSensorData(),sensor.getPrepoint());
        }
    }

    private EditorAndDeletePop.onDeleteAndEditorListner Poplistner = new EditorAndDeletePop.onDeleteAndEditorListner() {
        @Override
        public void EditorClick(ViewHolder holder, Sensor sensor, int position) {
            //弹编辑框
            modifySensorPostion = position;
            showInputDialog(inputClickListner, R.string.delete, R.string.sensor_inputname_hint, sensor.getName(), R.string.yes, R.string.no);
            pop.dismiss();
        }

        @Override
        public void DeletClick(ViewHolder holde, Sensor sensor, int position) {
            holder = holde;
            showLoadingDialog(listener, 2);
            DeleteSensor(sensor);
            pop.dismiss();
        }
    };

    private ImputDialog.MyInputClickListner inputClickListner = new ImputDialog.MyInputClickListner() {
        @Override
        public void onYesClick(Dialog dialog, View v, String input) {
            if (input == null || input.length() <= 0) {
                T.showShort(mContext, R.string.sensor_inputname_notnull);
            } else if (input.getBytes().length > Sensor.NameLen) {
                T.showShort(mContext, R.string.sensor_inputname_long);
            }else {
            	for(Sensor sensor :sensors){
            		if(input.equals(sensor.getName())){
            			T.showShort(mContext, R.string.sensor_name);
            			return;
            		}
            	}
            	Utils.hindKeyBoard(v);
                nameTemp = input;
                if (inputDialog != null && inputDialog.isShowing()) {
                    inputDialog.inputDialogDismiss();
                }
                showLoadingDialog(listener, 3);
                modifySensorName(sensors.get(modifySensorPostion), input);
            }
        }

        @Override
        public void onNoClick(View v) {
              Utils.hindKeyBoard(v);
        }
    };

    /**
     * 解析传感器数据
     *
     * @param data
     * @param Controcons
     * @param controlen
     * @param Sensorcons
     * @param Sensorlen
     */
    private void paserSensorData(byte[] data, byte Controcons, int controlen, byte Sensorcons, int Sensorlen) {
        byte[] contro = new byte[21];
        for (int i = 0; i < Controcons; i++) {
        	if(14+(i+1)*contro.length>data.length){
        		return;
        	}
            System.arraycopy(data, 14 + i * contro.length, contro, 0, contro.length);
            Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_NORMAL, contro, contro[0]);
            sensors.add(sensor);
        }
        if (data[3] == 0) {//没有防护计划
            byte[] sens = new byte[24];
            for (int i = 0; i < Sensorcons; i++) {
                System.arraycopy(data, 14 + controlen + i * sens.length, sens, 0, sens.length);
                Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_NORMAL, sens, sens[0]);
                sensors.add(sensor);
            }
        } else if (data[3] == 1) {//有24位的防护计划
            byte[] sens = new byte[49];
            byte[] RealSens = new byte[24];
            for (int i = 0; i < Sensorcons; i++) {
                System.arraycopy(data, 14 + controlen + i * sens.length, sens, 0, sens.length);
                System.arraycopy(sens, 0, RealSens, 0, RealSens.length);
                Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_NORMAL, RealSens, RealSens[0]);
                sensors.add(sensor);
            }
        }


    }
    public byte[] getBooleanArray(byte b) {  
        byte[] array = new byte[8];  
        for (int i = 7; i >= 0; i--) {  
            array[i] = (byte)(b & 1);  
            b = (byte) (b >> 1);  
        }  
        return array;  
    }

    /**
     * 解析特殊传感器数据
     *
     * @param data
     * @param len
     */
    private void paserSpecialSensorData(byte[] data, byte len) {
    	byte[] contro = new byte[4];
    	int k = len / contro.length;
    	if (data.length<PASER_SPECIAL_SENSOR_DATA_LENGTH) {
    		isNewData=false;
    		for (int i = 0; i < k; i++) {
                System.arraycopy(data, 4 + i * contro.length, contro, 0, contro.length);
                Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_SPECIAL, contro, contro[0]);
                sensor.setName(getSpecialSensorName(sensor.getSensorType()));
                sensors.add(sensor);
            }
    		mAdapter.setNewData(isNewData);
		}else {
			isNewData=true;
	        byte[] newData=new byte[data.length-8];
	        System.arraycopy(data, 0, newData, 0, newData.length);
	        byte[] prepointData=new byte[8];
	        System.arraycopy(data, data.length-8, prepointData, 0, prepointData.length);
	        for (int i = 0; i < k; i++) {
	            System.arraycopy(newData, 4 + i * contro.length, contro, 0, contro.length);
	            Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_SPECIAL, contro, contro[0]);
	            sensor.setName(getSpecialSensorName(sensor.getSensorType()));
	            sensor.setPrepoint(prepointData[i]);
	            sensors.add(sensor);
	        }
	        mAdapter.setNewData(isNewData);
		}
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private int counts = 0;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_add_sensor) {//打开扫描界面扫描条形码或二维码
            Intent openCameraIntent = new Intent(mContext, CaptureActivity.class);
            startActivityForResult(openCameraIntent, 0);

        }
    }

    /**
     * 学习传感器
     */
    private void addSensor() {
        FisheyeSetHandler.getInstance().sSetIntoLearnState(contact.contactId, contact.contactPassword);
    }

    /**
     * 删除传感器
     *
     * @param sensor
     */
    private void DeleteSensor(Sensor sensor) {
        if (sensor.getSensorType() == Constants.SensorType.TYPE_REMOTE_CONTROLLER) {
            FisheyeSetHandler.getInstance().sDeleteOneControler(contact.contactId, contact.contactPassword, sensor.getSensorSignature());
        } else {
            FisheyeSetHandler.getInstance().sDeleteOneSensor(contact.contactId, contact.contactPassword, sensor.getSensorSignature());
        }
    }

    /**
     * 修改传感器名字
     *
     * @param sensor
     */
    private void modifySensorName(Sensor sensor, String newName) {
        if (sensor.getSensorType() == Constants.SensorType.TYPE_REMOTE_CONTROLLER) {
            FisheyeSetHandler.getInstance().sChangeControlerName(contact.contactId, contact.contactPassword, sensor.getSensorSignature(), newName.getBytes());
        } else {
            FisheyeSetHandler.getInstance().sChangeSensorName(contact.contactId, contact.contactPassword, sensor.getSensorSignature(), newName.getBytes());
        }
    }

    /**
     * 处理学习传感器返回数据
     *
     * @param data
     * @param boption
     */
    private void getSensorData(byte[] data, byte boption) {
        if (boption != Constants.FishBoption.MESG_SET_OK) {
            if (dialog!=null&&dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if (boption == Constants.FishBoption.MESG_SET_OK) {
            //进入学习
            showLoadingDialog(listener, 1);
        } else if (boption == Constants.LenStateCode.LEARN_STATE_SUCCESS) {
            //成功
            T.showShort(mContext, R.string.add_success);
            Sensor sensor = new Sensor(Sensor.SENSORCATEGORY_NORMAL, data);
            sensors.add(sensor);
            mAdapter.notifyItemInserted(sensors.size());
//            sensors.clear();
//            getAllSensorData();
        } else if (boption == Constants.LenStateCode.LEARN_STATE_TIMEOUT) {
            //超时
            T.showShort(mContext, R.string.connect_wifi_timeout);
        } else if (boption == Constants.LenStateCode.LEARN_STATE_CONTROLER_FULL) {
            //遥控已学满
            T.showShort(mContext, R.string.sensor_control_full);
        } else if (boption == Constants.LenStateCode.LEARN_STATE_SENSOR_FULL) {
            //传感器已学满
            T.showShort(mContext, R.string.sensor_full);
        } else if (boption == Constants.LenStateCode.LEARN_STATE_BUSY) {
            //已经在学习
            T.showShort(mContext, R.string.sensor_busy);
        } else if (boption == Constants.LenStateCode.LEARN_STATE_ALREADY_LEARN) {
            //已经学习
            T.showShort(mContext, R.string.sensor_already_lean);
            //getAllSensorData();
        }
    }

    private void getAddSensorACK(int arg1, int result) {
        if (FishAckUtils.getACKCmd(arg1) == Constants.MsgSection.MSG_ID_FISHEYE_INTO_LEARN_STATE) {
            if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
                //弹出等待框
                showLoadingDialog(listener, 1);
            } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                //网络异常,不起作用，好像没有收到ACK回调，待查
                if (counts >= 5) {
                    T.showShort(mContext, R.string.net_error_operator_fault);
                    counts = 0;
                } else {
                    addSensor();
                }
            }
        }
    }

    private NormalDialog.OnCustomCancelListner listener = new NormalDialog.OnCustomCancelListner() {
        @Override
        public void onCancle(int mark) {
            if (mark == 1) {
                T.showShort(mContext, R.string.net_error_operator_fault);
            } else if (mark == 2) {
                //删除放弃
            } else if (mark == 3) {
                //改名对话框消失
            } else if (mark == 4) {
                //二维码学习对话框消失
            }

        }
    };

    /**
     * 处理删除传感器返回
     *
     * @param option
     * @param data
     */
    private void deleteSensorResult(byte option, byte[] data) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (option == Constants.FishBoption.MESG_SET_OK) {
            sensors.remove(holder.getPosition());
            mAdapter.notifyItemRemoved(holder.getPosition());
//            sensors.clear();
//            getAllSensorData();
        } else if (option == Constants.FishBoption.MESG_SET_DELETE_ONE_SENSOR_ERROR) {
            //传感器不存在
        }
    }

    /**
     * 处理修改数据返回
     *
     * @param option
     */
    private void modifySensorName(byte option) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (option == Constants.FishBoption.MESG_SET_OK) {
            Sensor s = sensors.get(modifySensorPostion);
            s.setName(nameTemp);
            mAdapter.notifyItemChanged(modifySensorPostion);
        } else if (option == Constants.FishBoption.MESG_SET_DELETE_ONE_SENSOR_ERROR) {
            //传感器不存在
        }
        modifySensorPostion = -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Sensor sensor = (Sensor) data.getSerializableExtra("sensor");
            int position = data.getIntExtra("position", -1);
            sensors.set(position, sensor);
            mAdapter.notifyItemChanged(position);
        } else if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                if ((scanResult != null) && scanResult.startsWith(SmartDevice)) {
                    String code = scanResult.split(":")[1];
                    if (code != null && code.length() > 0) {
                        try {
                            byte[] test = Utils.HexString2Bytes(code);
                            LearnQrCodeSensor(test);
//                            T.showLong(mContext, scanResult);
                            showLoadingDialog(listener, 4);
                        } catch (Exception e) {
                            e.printStackTrace();
                            T.showLong(mContext, R.string.qword_error);
                        }
                    } else {
                        T.showLong(mContext, R.string.qword_error);
                    }
                } else {
                    T.showLong(mContext, R.string.qword_error);
                }
            } else if (resultCode == CaptureActivity.RESULT_MULLT) {
                //手动添加
                counts++;
                addSensor();
            }
        }else if (requestCode==2&&resultCode==Activity.RESULT_OK) {
			int sensorIndex=data.getIntExtra("index", -1);
			int sensorPrepoint=data.getIntExtra("SensorPrepoint", -1);
			sensors.get(sensorIndex).setPrepoint(sensorPrepoint);
			mAdapter.notifyItemChanged(sensorIndex);
		}
    }

    /**
     * 清除所有一般传感器
     */
    private void clearNormalSensor() {
        for (int i = 0; i < sensors.size(); i++) {
            if (sensors.get(i).getSensorCategory() == Sensor.SENSORCATEGORY_NORMAL) {
                sensors.remove(i);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取特殊传感器的默认名
     *
     * @param type 传感器类型
     * @return 默认名
     */
    private String getSpecialSensorName(byte type) {
        if (type == Constants.SpecialSensorType.INDEX_ALARM_TYPE_MD) {
            return Utils.getStringForId(R.string.special_sensor_md);
        } else if (type == Constants.SpecialSensorType.INDEX_ALARM_TYPE_ATTACH) {
            return Utils.getStringForId(R.string.special_sensor_attach);
        }
        return "";
    }

    //定义回调函数及变量(要推广到基础Fragment)
    protected BackHandlerInterface backHandlerInterface;

    public interface BackHandlerInterface {
        void setSelectedFragment(SmartDeviceFrag2 backHandledFragment);
    }

    public boolean onBackPressed() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegFilter) {
            isRegFilter = false;
            mContext.unregisterReceiver(mReceiver);
        }
        Intent it = new Intent();
        it.setAction(Constants.Action.CONTROL_BACK);
        mContext.sendBroadcast(it);
    }
    
    
    public void setAllPrePoint(int[] data) {
		allPrePoint.clear();
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 1) {
				allPrePoint.add(i);
			}
		}
		allPrePoint.add(-1);
	}
    
}
