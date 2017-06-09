package com.jwkj.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jwkj.activity.AddTimeGroupActivity;
import com.jwkj.adapter.ModeSetRecyAdapter;
import com.jwkj.data.Contact;
import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.Constants;
import com.jwkj.recycleview.ItemDecor.lineViewItemDecoration;
import com.jwkj.selectdialog.SelectItem;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.ExpandeLinearLayout;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.lineView;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dxs on 2016/1/12.
 */
public class ModeControlFrag extends BaseFragment implements View.OnClickListener{
    private Context mContext;
    private RecyclerView ModeTime;
    private ModeSetRecyAdapter mAdapter;
    private Contact contact;
    private int device_type;
    private boolean isRegFilter=false;
    private ImageView ivAddTime;
    private List<WorkScheduleGroup> Groups=new ArrayList<WorkScheduleGroup>();
    private byte weekDayTemp=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode_control, container,
                false);
        mContext = getActivity();
        contact= (Contact) getArguments().getSerializable("contact");
        device_type=getArguments().getInt("type", 0);
        initUI(view);
        return view;
    }
    private void initUI(View view) {
        ModeTime= (RecyclerView) view.findViewById(R.id.rl_modeset);
        ivAddTime= (ImageView) view.findViewById(R.id.iv_add_timegroup);
        ModeTime.setLayoutManager(new LinearLayoutManager(mContext));
        ModeTime.addItemDecoration(new lineViewItemDecoration(mContext, Utils.dip2px(mContext, 16.5f)));
        mAdapter=new ModeSetRecyAdapter(mContext,Groups);
        mAdapter.setOnModeSetting(listner);
        mAdapter.setOnExpandeLinearLayoutListner(eXlistner);
        addHeaderAndFooter();
        ModeTime.setAdapter(mAdapter);
        ivAddTime.setOnClickListener(this);

    }
    private lineView Header,Footer;

    private void addHeaderAndFooter() {
        Header=new lineView(mContext, MyApplication.SCREENWIGHT, Utils.dip2px(mContext,48));
    }

    private void initData() {
        if(contact!=null){
            FisheyeSetHandler.getInstance().sGetWorkModeSchedule(contact.getContactId(),contact.getPassword());
        }
        //测试数据
        //getDeviceWiteList();
    }

    @Override
    public void onResume() {
        super.onResume();
        regFilter();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isRegFilter) {
            isRegFilter = false;
            mContext.unregisterReceiver(mReceiver);
        }
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.ACK_FISHEYE);
        filter.addAction(Constants.P2P.RET_GET_SCHEDULE_WORKMODE);
        filter.addAction(Constants.P2P.RET_SET_SCHEDULE_WORKMODE);
        filter.addAction(Constants.P2P.RET_DELETE_SCHEDULE);
        mContext.registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int iSrcID=intent.getIntExtra("iSrcID", 0);
            byte boption=intent.getByteExtra("boption", (byte) -1);
            byte[] data=intent.getByteArrayExtra("data");
            if(intent.getAction().equals(Constants.P2P.ACK_FISHEYE)){

            }else if(intent.getAction().equals(Constants.P2P.RET_GET_SCHEDULE_WORKMODE)){
                //获取防护计划返回
                if(boption== Constants.FishBoption.MESG_GET_OK){
                    getDeviceWiteList(data,6);
                    mAdapter.UpdataAll();
                }
            }else if(intent.getAction().equals(Constants.P2P.RET_SET_SCHEDULE_WORKMODE)){
                if(boption== Constants.FishBoption.MESG_SET_OK){
                    if(dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    WorkScheduleGroup group=new WorkScheduleGroup(data,3);
                    mAdapter.UpdataGroup(group);
                }
            }else if(intent.getAction().equals(Constants.P2P.RET_DELETE_SCHEDULE)){
                //删除防护计划返回
                if(boption== Constants.FishBoption.MESG_SET_OK){
                    if(dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    mAdapter.DeleteGroup(data[3]);
                }else if(boption== Constants.FishBoption.MESG_SET_SCHEDULE_WORK_MODE_ARG_ERROR){
                    T.showShort(mContext, R.string.argumens_error);
                }
            }
        }
    };

    /**
     * 解析时间组数据
     * @param data
     * @param len
     */
    private void getDeviceWiteList(byte[] data,int len){
    	Groups.clear();
        byte[] device=new byte[4];
        for (int i = 0; i <len ; i++) {
            System.arraycopy(data,3+i*4,device,0,device.length);
            WorkScheduleGroup user=new WorkScheduleGroup(device, (byte) i);
            if(user.getbWorkMode()!=0){
                Groups.add(user);
            }
        }
    }

    /**
     * 产生模拟数据
     */
    private void getDeviceWiteList(){
        byte[] device=new byte[4];
        byte[] data=new byte[]{1,1,0,1,1,25,1,1,1,1,1,2,1,1,1,1,1,2,1,1,1,2,1,2,1,1,2};
        for (int i = 0; i <6 ; i++) {
            System.arraycopy(data,3+i*4,device,0,device.length);
            WorkScheduleGroup user=new WorkScheduleGroup(device, (byte) i);
            if(user.getbWorkMode()!=0){
                Groups.add(user);
            }
        }
    }

    /**
     * 删除一个时间组
     * @param grop 时间组
     * @return 是否发送命令
     */
    private boolean deleteTimeGroup(WorkScheduleGroup grop){
        if(contact!=null){
            FisheyeSetHandler.getInstance().sClearScheduleTimeGroup(contact.getContactId(),contact.getPassword(),grop.getGroupIndex());
            return true;
        }
        return false;
    }

    /**
     * 修改重复
     * @param grop 原时间组
     */
    private void showSelectDialog(WorkScheduleGroup grop){
        NormalDialog dialog=new NormalDialog(mContext);
        dialog.setTitle(R.string.fish_repete);
        dialog.setBtn1_str(Utils.getStringForId(R.string.yes));
        dialog.showSelectListDialog(grop,NormalDialog.SelectType_List);
        dialog.setOnDialogSelectListner(moreSelect);
        weekDayTemp=grop.getbWeekDay();
    }

    /**
     * 修改时间和模式
     * @param grop 原时间间组
     */
    private void showModifyTimeAndMode(WorkScheduleGroup grop){
        NormalDialog dialog=new NormalDialog(mContext);
        dialog.showModifyTimeAndModeDialog(grop);
        dialog.setOnDialogModifyTimeAndMode(modifyTimeAndMode);
    }

    private ExpandeLinearLayout.ExpandeLinearLayoutListner eXlistner=new ExpandeLinearLayout.ExpandeLinearLayoutListner() {
        @Override
        public void onEditorClick(int LayoutState) {

        }

        @Override
        public void onTimeClick(WorkScheduleGroup group) {
            showSelectDialog(group);
        }

        @Override
        public void onDeleteClick(WorkScheduleGroup group) {
            if(deleteTimeGroup(group)){
                showLoadingDialog(Loadlistener,1);
            }
        }
    };

    private ModeSetRecyAdapter.onModeSetting listner=new ModeSetRecyAdapter.onModeSetting() {
        @Override
        public void onSwitchClick(View v, WorkScheduleGroup grop, int position) {
            //设置开关
            setScheduleTimeGroupEnable(grop);
        }

        @Override
        public void onItemClick(View v, WorkScheduleGroup grop, int position) {
            //单击跳转，修改(新版不再响应)
//            Intent addTimeGroup=new Intent();
//            addTimeGroup.setClass(mContext, AddTimeGroupActivity.class);
//            addTimeGroup.putExtra("grop", grop);
//            addTimeGroup.putExtra("contact",contact);
//            addTimeGroup.putExtra("type", 1);
//            startActivityForResult(addTimeGroup, 0);
        }

        @Override
        public void onTimeAndModeClick(View v, WorkScheduleGroup grop, int position) {
            showModifyTimeAndMode(grop);
        }
    };
    //默认等待弹窗消失回调
    private NormalDialog.OnCustomCancelListner Loadlistener=new NormalDialog.OnCustomCancelListner() {

        @Override
        public void onCancle(int mark) {
            T.showLong(mContext, "取消设置");
        }
    };
    //修改时间及模式确定回调
    private NormalDialog.onDialogModifyTimeAndMode modifyTimeAndMode=new NormalDialog.onDialogModifyTimeAndMode() {
        @Override
        public void onItemClick(AlertDialog dialog, WorkScheduleGroup grop,WorkScheduleGroup beforegroup) {
            //判断是否有重复时间点
            if(isSameTime(grop)&&!grop.gettime().toString().equals(beforegroup.gettime().toString())){
                T.showShort(mContext, R.string.time_group_exsite);
            }else{
                ModifyGroup(grop);
                showLoadingDialog(Loadlistener,1);
            }
        }
    };

    private void ModifyGroup(WorkScheduleGroup grop){
        FisheyeSetHandler.getInstance().sSettingScheduleTimeGroup(contact.getContactId(), contact.getPassword(), grop.getAllInfo());
    }

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
            ModifyGroup(grop);
            showLoadingDialog(Loadlistener,1);
        }
    };

    private void setScheduleTimeGroupEnable(WorkScheduleGroup grop){
        WorkScheduleGroup gropTemp=grop.clone();
        if (gropTemp.isEnable()) {
            gropTemp.setIsEnable(false);
        }else{
            gropTemp.setIsEnable(true);
        }
        FisheyeSetHandler.getInstance().sSettingScheduleTimeGroup(contact.getContactId(),contact.getPassword(),gropTemp.getAllInfo());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_add_timegroup:
            	byte nextIndext=(byte) getNextTimeGrop();
            	if(nextIndext==-1){
            		T.showLong(mContext, R.string.group_full);
            		return;
            	}
                WorkScheduleGroup grop=new WorkScheduleGroup();
                grop.setbWorkMode((byte) 1);
                grop.setGroupIndex(nextIndext);
                Intent addTimeGroup=new Intent();
                addTimeGroup.setClass(mContext, AddTimeGroupActivity.class);
                addTimeGroup.putExtra("grop", grop);
                addTimeGroup.putExtra("contact",contact);
                addTimeGroup.putExtra("type",0);
                addTimeGroup.putExtra("times", getGroupTimes());
                startActivityForResult(addTimeGroup, 0);
                break;
        }
    }
    //获取下一个可以添加的时间组
    public int getNextTimeGrop(){
    	byte[] indexs=new byte[Groups.size()];
    	int i=0;
    	for(WorkScheduleGroup grop:Groups){
    		indexs[i++]=grop.getGroupIndex();
    	}
    	Arrays.sort(indexs);
        for(byte j=0;j<6;j++){
        	if(Arrays.binarySearch(indexs, j)<0)return j;
        }
        return -1;
    }
    
    private boolean isSameTime(WorkScheduleGroup grop){
        for (WorkScheduleGroup grops:Groups) {
            if(grops.gettime().toString().equals(grop.gettime().toString())){
                return true;
            }
        }
        return false;
    }
    //获取已经学习过的时间
    private long[] getGroupTimes(){
    	long[] times=new long[Groups.size()];
    	for (int i=0,count=Groups.size();i<count;i++) {
    		times[i]=Groups.get(i).gettime();
    	}
    	Arrays.sort(times);
    	return times;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("dxsTest","requestCode-->"+requestCode+"resultCode-->"+resultCode);
        if(requestCode==0){
            if(resultCode==AddTimeGroupActivity.RESULT_ModidyOne){
                //修改时间组返回
                WorkScheduleGroup group= (WorkScheduleGroup) data.getSerializableExtra("grop");
                mAdapter.UpdataGroup(group);
            }else if(resultCode==AddTimeGroupActivity.RESULT_AddOne){
                WorkScheduleGroup group= (WorkScheduleGroup) data.getSerializableExtra("grop");
                mAdapter.addGroup(group);
            }else if(resultCode==AddTimeGroupActivity.RESULT_Delete){
                //删除时间组
                byte index=data.getByteExtra("index", (byte) 0);
                mAdapter.DeleteGroup(index);
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent it = new Intent();
        it.setAction(Constants.Action.CONTROL_BACK);
        mContext.sendBroadcast(it);
    }
}
