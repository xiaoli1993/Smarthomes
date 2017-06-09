package com.jwkj.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.Prepoint;
import com.jwkj.entity.OnePrepoint;
import com.jwkj.fragment.MonitorThreeFrag;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dxs on 2015/12/3.
 */
public class RememberPontPopWindow extends PopupWindow implements View.OnClickListener,PrePointLayout.OnAddMyImageViewListner{
    private final static String TAG = "dxsRememberWindow";
    public static final int DelayTime = 10*1000;
    private boolean isModify = false;
    private int selectedPoint = -1;
    private View conentView;
    private Context context;
    private ImageButton btnBack;
    private TextView btnDelete, btnEditect,btnCancel;
    private List<String> deletePaths = new ArrayList<String>();
    private List<OnePrepoint> RememberPoins = new ArrayList<OnePrepoint>();
    private List<RememberImageRl> innerViews=new ArrayList<RememberImageRl>();
    private List<OnePrepoint> SelectedPrepoins = new ArrayList<OnePrepoint>();
    private LayoutInflater inflater;
    private int selectePoints=0;
    private PrePointLayout prepointLayout;
    private int h;
    private RelativeLayout rl_prepoint_function;
    private Contact mContact;

    public RememberPontPopWindow(Context context, int h, Contact mContact, byte PrepointInfo) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popwin_prepoint_test, null);
        this.context = context;
        this.h=h;
        this.mContact=mContact;
        setContentView(conentView);
        setHeight(h);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置动画效果
//        Animation animation = AnimationUtils.loadAnimation(context, R.anim.popwindow_in_half);
        this.setAnimationStyle(R.style.AnimationFade);
        deletePaths.clear();
        PrepointsRefrush();
        initUI(conentView);
        changeIconText(SelectedPrepoins);
        
        
    }

    private void initUI(View conentView) {
        prepointLayout= (PrePointLayout) conentView.findViewById(R.id.rl_remenberpoint);
        btnBack= (ImageButton) conentView.findViewById(R.id.btn_point_back);
        btnDelete = (TextView) conentView.findViewById(R.id.btn_popdelete);
        btnEditect = (TextView) conentView.findViewById(R.id.btn_popmodify);
        btnCancel=(TextView) conentView.findViewById(R.id.btn_popcancel);
        rl_prepoint_function=(RelativeLayout) conentView.findViewById(R.id.rl_prepoint_function);

        getInnerViews(RememberPoins);
        prepointLayout.addInnerView(innerViews);
        prepointLayout.setDeviceId(mContact.contactId);
        prepointLayout.setOnRememberlistner(rememberlistner);

        btnBack.setOnClickListener(this);
        btnEditect.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        prepointLayout.setOnAddMyImageViewListner(this);
    }
    
    private PrePointLayout.onRememberlistner rememberlistner=new PrePointLayout.onRememberlistner(){

        @Override
        public void selectedPrepoint(OnePrepoint point, int position) {
            if (!deletePaths.contains(point.imagePath)) {
                selectedPoint = point.prepoint;
                deletePaths.add(point.imagePath);
                selectePoints=select(selectePoints,point.prepoint);
            }
            changeIconText(SelectedPrepoins);
        }

        @Override
        public void cancelPrepoint(OnePrepoint point, int position) {
            deletePaths.remove(point.imagePath);
            changeIconText(SelectedPrepoins);
            selectePoints=delete(selectePoints,point.prepoint);
        }

        @Override
        public void toSeePrepoint(OnePrepoint point) {
            Utils.setPrePoints(mContact.getIpContactId(), mContact.contactPassword, 0, point.prepoint);
            isAtPrepoint(point.prepoint, DelayTime);
            T.showLong(context, point.name);
        }

        @Override
        public void selectedPrepoints(List<OnePrepoint> Selected, List<String> prepointPath, int SelectedPoins) {
            if(Selected!=null){
                SelectedPrepoins.clear();
                deletePaths.clear();
                deletePaths.addAll(prepointPath);
                SelectedPrepoins.addAll(Selected);
                selectePoints=SelectedPoins;
                changeIconText(Selected);
                rl_prepoint_function.setVisibility(View.VISIBLE);
            }
        }

		@Override
		public void onViewCancel() {
            hiddenFunction();
		}
    };
    /**
     * 隐藏功能按钮
     */
    private void hiddenFunction(){
    	clearTemp();
    	if(rl_prepoint_function!=null&&rl_prepoint_function.getVisibility()!=View.GONE){
			rl_prepoint_function.setVisibility(View.GONE);
		}
    	changeIconText(SelectedPrepoins);
    }
    /**
     * 获取已经存在的预置位
     */
    public void PrepointsRefrush() {
        this.RememberPoins = getPrePoint(mContact.contactId, ParsePrepointInfo(ApMonitorActivity.PrePointInfo));
    }

    /**指定某位二进制0，其他位保持不变，删除为1的预置位
     * @param src
     * @param position
     * @return
     */
    private int delete(int src,int position){
        int t= Integer.MAX_VALUE^(1<<position);
        return src&t;
    }

    /**指定某位二进制为1，其他位为0，即设置为预置位
     * @param src
     * @param position
     * @return
     */
    private int select(int src,int position){
        return src|(1<<position);
    }

    private List<OnePrepoint> getPrePoint(String deviceId, int[] devicePoints) {
        List<OnePrepoint> OnePrePoints = new ArrayList<OnePrepoint>();
        Prepoint points = DataManager.findPrepointByDevice(context, deviceId);
        OnePrepoint point;
        for (int i = 0; i < MonitorThreeFrag.PREPOINTCOUNTS; i++) {
            if (devicePoints[i] == 1) {
                point = new OnePrepoint();
                point.prepoint = i;
                point.imagePath = Utils.getPrepointPath(deviceId, i);
                point.name = points.getName(i);
                OnePrePoints.add(point);
            }
        }
        return OnePrePoints;
    }

    private void getInnerViews(List<OnePrepoint> OnePrePoints){
        innerViews.clear();
        for(int i=0;i<OnePrePoints.size();i++){
            RememberImageRl rl=new RememberImageRl(context);
            rl.setPrePoint(OnePrePoints.get(i),i);
            innerViews.add(rl);
        }
    }
    

    /**
     * 解析预置位信息
     * @param prepointInfo
     * @return
     */
    public int[] ParsePrepointInfo(byte prepointInfo) {
        return Utils.getByteBinnery(prepointInfo, true);
    }

    private prepointPopwindow.OnPopwindowListner popwindowListner;
    public void setOnPopwindowListner(prepointPopwindow.OnPopwindowListner popwindowListner){
        this.popwindowListner=popwindowListner;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_point_back:
                disMiss();
                break;
            case R.id.btn_popmodify:
                if (isModify) {
                    Prepoint po = getPrepoinNames();
                    if (po != null&&SelectedPrepoins.size()==1) {
                        showInputPrepointName(SelectedPrepoins.get(0), po);
                    }
                } else {
                   //empty
                }
                break;
            case R.id.btn_popdelete:
                popwindowListner.onDeletePrepoint(deletePaths, selectePoints);
                break;
            case R.id.btn_popcancel:
            	 //退出选择模式
                hiddenFunction();
            	break;
        }
    }
    private ondismissListener listener;
    public interface ondismissListener{
    	void onDismiss();
    	void onshow();
    }
    public void setdismissListener(ondismissListener listener){
    	this.listener=listener;
    	prepointLayout.setOnPopwindowListner(listener);
    }
    public void disMiss() {
        prepointLayout.ClearPoints();
        if(isAtHandle!=null){
            isAtHandle.removeCallbacks(ss);
        }
        this.dismiss();
        changeIconText(SelectedPrepoins);
    }

    @Override
    public void addMyImageViewListner(int prepointNum) {
        popwindowListner.addPrepoint(prepointNum);
    }

    /**
     * 在指定延时时间后获取是否转到对应的记忆点
     */
    private Handler isAtHandle=new Handler();
    private Runnable ss;
    private void isAtPrepoint(final int point,final long time){
        ss=new Runnable() {
            @Override
            public void run() {
                Utils.setPrePoints(mContact.getIpContactId(), mContact.contactPassword, 4, point);
                Log.e("dxsTest", "time--"+time);
            }
        };
        isAtHandle.postDelayed(ss, time);
    }

    /**
     * 从数据库中获取记忆点的别名
     * @return
     */
    public Prepoint getPrepoinNames() {
        return DataManager.findPrepointByDevice(context, mContact.contactId);
    }

    /**
     * 根据选中的记忆点，改变操作按钮的功能
     * @param Selecteds
     */
    private void changeIconText(List<OnePrepoint> Selecteds) {
        if (Selecteds==null||Selecteds.size() == 0) {
            btnDelete.setVisibility(View.GONE);
            if (prepointLayout.getIsSelected()) {
                isModify = false;
            } else {
                isModify = true;
            }
            btnEditect.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        } else if (Selecteds.size() == 1) {
            isModify = true;
            btnEditect.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            isModify = false;
            btnEditect.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 修改记忆点别名的Dialog
     */
    private ImputDialog inputDialog;
    public void showInputPrepointName(final OnePrepoint point, final Prepoint prepoint) {
        inputDialog = new ImputDialog(context);
        inputDialog.setEdtextType(InputType.TYPE_CLASS_TEXT);
        inputDialog.setMaxCharater(8);
        inputDialog.SetText(Utils.getStringForId(R.string.prepoint_modifyname), "", prepoint.getName(point.prepoint), context.getResources().getString(
                R.string.ensure), context.getResources().getString(
                R.string.cancel));
        inputDialog.setOnMyinputClickListner(new ImputDialog.MyInputClickListner() {
            @Override
            public void onYesClick(Dialog dialog, View v, String input) {
                if ("".equals(input)) {
                    T.showShort(context, R.string.prepoint_setname);
                    return;
                }
                if (input.getBytes().length>12) {
                	T.showShort(context, R.string.name_length_beyond);
                    return;
				}
                point.name=input;
                prepoint.setName(point.prepoint, input);
                DataManager.upDataPrepoint(context, mContact.contactId, prepoint);
                prepointLayout.updataInnerView();
                inputDialog.inputDialogDismiss();
                hiddenFunction();
            }

            @Override
            public void onNoClick(View v) {
                inputDialog.inputDialogDismiss();
            }
        });
        inputDialog.inputDialogShow();
    }
    
    /**
     * 清除选中数据
     */
    private void clearTemp(){
        prepointLayout.ClearPoints();
        SelectedPrepoins.clear();
        deletePaths.clear();
        selectePoints=0;
    }

    /**
     * 添加记忆点
     * @param point
     */
    public void addPrepoint(int point){
        prepointLayout.addPrepoint(point);
    }

    /**
     * 删除记忆点
     * @param info
     */
    public void DeletePrepoint(byte info){
        prepointLayout.deletePrepoint(info);
        hiddenFunction();
    }
    
    public void DismissInputPrepointName(){
    	if(inputDialog!=null){
    		inputDialog.inputDialogDismiss();
    	}
    }
   
    
    
    
}
