package com.jwkj.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.data.Contact;
import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.selectdialog.SelectItem;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.wheel.widget.WheelView;
import com.jwkj.widget.NormalDialog;
import com.nuowei.smarthome.R;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by dxs on 2016/1/14.
 */
public class AddTimeGroupActivity extends BaseActivity implements View.OnClickListener{
    public final static int RESULT_Nothing=0;
    public final static int RESULT_Delete=1;
    public final static int RESULT_AddOne=2;
    public final static int RESULT_ModidyOne=3;
    private WorkScheduleGroup grop;
    private TextView txMode,txTime,txTage,txTitle;
    WheelView date_hour, date_minute;
    private Context mContext;
    private Button btnDeleteTime;
    private RelativeLayout rlMode,rlTime,rlTage;
    private Contact contact;
    private byte weekDayTemp=0;
    private int AddType=1;//区分是修改还是添加 0是编辑 1修改
    private ImageView ivBack,ivAdd;
    private boolean isRegFilter=false;
    private int Result=0;
    private Intent ResultIntent;
    private long[] times;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_addgrouptime);
        mContext=this;
        ResultIntent=getIntent();
        grop= (WorkScheduleGroup) getIntent().getSerializableExtra("grop");
        contact= (Contact) getIntent().getSerializableExtra("contact");
        AddType=getIntent().getIntExtra("type",0);
        times=getIntent().getLongArrayExtra("times");
        initUI();
        regFilter();
    }

    private void initUI() {
        Calendar calendar = Calendar.getInstance();
        txMode= (TextView) findViewById(R.id.tx_addtime_mode);
        txTime= (TextView) findViewById(R.id.tx_repete);
        btnDeleteTime= (Button) findViewById(R.id.btn_deletetime);
        rlMode= (RelativeLayout) findViewById(R.id.device_mode);
        rlTime= (RelativeLayout) findViewById(R.id.fish_repete);
        rlTage= (RelativeLayout) findViewById(R.id.fish_tage);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        ivAdd= (ImageView) findViewById(R.id.button_add);

        txMode.setText(grop.getModeText());
        txTime.setText(grop.getTimeText());
        if(AddType==0){
            btnDeleteTime.setVisibility(View.GONE);
            Result=RESULT_AddOne;
        }else{
            btnDeleteTime.setVisibility(View.VISIBLE);
            Result=RESULT_ModidyOne;
        }

        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        date_hour = (WheelView)findViewById(R.id.date_hour);
        date_hour.setViewAdapter(new DateNumericAdapter(mContext, 0, 23));
        date_hour.setCurrentItem(curHour);
        date_hour.setCyclic(true);

        int curMinute = calendar.get(Calendar.MINUTE);
        date_minute = (WheelView) findViewById(R.id.date_minute);
        date_minute.setViewAdapter(new DateNumericAdapter(mContext, 0, 59));
        date_minute.setCurrentItem(curMinute);
        date_minute.setCyclic(true);

        btnDeleteTime.setOnClickListener(this);
        rlMode.setOnClickListener(this);
        rlTime.setOnClickListener(this);
        rlTage.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
    }

    public void regFilter() {
        isRegFilter = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.RET_SET_SCHEDULE_WORKMODE);
        filter.addAction(Constants.P2P.RET_DELETE_SCHEDULE);
        registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int iSrcID=intent.getIntExtra("iSrcID", 0);
            byte boption=intent.getByteExtra("boption", (byte) -1);
            byte[] data=intent.getByteArrayExtra("data");
            if(intent.getAction().equals(Constants.P2P.ACK_FISHEYE)){

            }else if(intent.getAction().equals(Constants.P2P.RET_DELETE_SCHEDULE)){
                //删除防护计划返回
                if(boption== Constants.FishBoption.MESG_SET_OK){
                    Result=RESULT_Delete;
                    if(dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Intent result=new Intent();
                    result.putExtra("index", data[3]);
                    AddTimeGroupActivity.this.setResult(Result,result);
                    finish();
                }else if(boption== Constants.FishBoption.MESG_SET_SCHEDULE_WORK_MODE_ARG_ERROR){
                	if(dialog!=null&&dialog.isShowing()){
                          dialog.dismiss();
                    }
                    T.showShort(mContext,R.string.argumens_error);
                }
            }else if(intent.getAction().equals(Constants.P2P.RET_SET_SCHEDULE_WORKMODE)){
                //设置防护计划返回
                if(boption== Constants.FishBoption.MESG_SET_OK){
                    if(dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    WorkScheduleGroup grop=new WorkScheduleGroup(data,3);
                    Intent result=new Intent();
                    result.putExtra("grop",grop);
                    AddTimeGroupActivity.this.setResult(Result, result);
                    finish();
                }else if(boption== Constants.FishBoption.MESG_SET_SCHEDULE_WORK_MODE_ARG_ERROR){
                    T.showShort(mContext,R.string.argumens_error);
                }else if(boption== Constants.FishBoption.MESG_SET_SCHEDULE_HAVE_SAME_SCHEDULE){
                    //相同时间点
                	if(dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    T.showShort(mContext,R.string.time_group_exsite);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegFilter) {
            mContext.unregisterReceiver(mReceiver);
            isRegFilter = false;
        }
    }

    @Override
    public int getActivityInfo() {
        return Constants.ActivityInfo.ACTIVITY_ADDGROUPTIME;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_deletetime:
                deleteTimeGroup(grop);
                break;
            case R.id.device_mode:
                showSingleSelectDialog();
                break;
            case R.id.fish_repete:
                showSelectDialog();
                break;
            case R.id.fish_tage:
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.button_add:
                grop.setbBeginHour((byte) date_hour.getCurrentItem());
                grop.setbBeginMin((byte) date_minute.getCurrentItem());
                if(isSameTime(grop.gettime())){
                	 T.showShort(mContext, R.string.time_group_exsite);
                	 return;
                }
                grop.setIsEnable(true);
                addTimeGroup();
                showLoadingDialog(listener,1);
                break;
        }
    }

    private NormalDialog.OnCustomCancelListner listener=new NormalDialog.OnCustomCancelListner() {

        @Override
        public void onCancle(int mark) {
            T.showLong(mContext,"取消设置");
        }
    };

    private void deleteTimeGroup(WorkScheduleGroup grop){
        if(contact!=null){
            FisheyeSetHandler.getInstance().sClearScheduleTimeGroup(contact.getContactId(),contact.getPassword(),grop.getGroupIndex());
        }
    }

    private void showSingleSelectDialog(){
        NormalDialog dialog=new NormalDialog(mContext);
        dialog.setTitle(R.string.fish_mode);
        dialog.setBtn1_str(Utils.getStringForId(R.string.yes));
        dialog.showSingleSelectListDialog(grop.getbWorkMode());
        dialog.setOnDialogSingleSelectListner(dialogSelect);
    }

    private void showSelectDialog(){
        NormalDialog dialog=new NormalDialog(mContext);
        dialog.setTitle(R.string.fish_repete);
        dialog.setBtn1_str(Utils.getStringForId(R.string.yes));
        dialog.showSelectListDialog(grop,NormalDialog.SelectType_List);
        dialog.setOnDialogSelectListner(moreSelect);
        weekDayTemp=grop.getbWeekDay();
    }
    //单选
    private NormalDialog.onDialogSingleSelectListner dialogSelect=new NormalDialog.onDialogSingleSelectListner(){
        @Override
        public void onItemClick(AlertDialog dialog, int position) {
            dialog.dismiss();
            grop.setbWorkMode((byte) position);
            txMode.setText(grop.getModeText());
        }
    };
    //多选(设备中是从星期天开始计算)
    /**
     * weekDay:0 1 2 3 4 5 6 7
     * 周     :日 1 2 3 4 5 6 1
     */
    private NormalDialog.onDialogSelectListner moreSelect=new NormalDialog.onDialogSelectListner() {
        @Override
        public void onItemClick(View v, SelectItem item, int position) {
            if(position==6){
                position=0;
            }else{
                position++;
            }
            if(item.isSelected()){
                weekDayTemp=Utils.ChangeBitTrue(weekDayTemp,position);
            }else{
                weekDayTemp=Utils.ChangeByteFalse(weekDayTemp,position);
            }
        }

        @Override
        public void onOkClick(AlertDialog dialog, WorkScheduleGroup grop) {
            dialog.dismiss();
            grop.setbWeekDay(weekDayTemp);
            txTime.setText(grop.getTimeText());
        }
    };

    private void addTimeGroup(){
        FisheyeSetHandler.getInstance().sSettingScheduleTimeGroup(contact.getContactId(), contact.getPassword(), grop.getAllInfo());
    }
    
    private boolean isSameTime(long time){
    	return Arrays.binarySearch(times, time)>=0;
    }

}
