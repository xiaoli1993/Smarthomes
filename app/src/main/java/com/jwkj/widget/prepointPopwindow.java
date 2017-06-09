package com.jwkj.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jwkj.adapter.prePointRecycleAdapter;
import com.jwkj.data.DataManager;
import com.jwkj.data.Prepoint;
import com.jwkj.entity.OnePrepoint;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dxs on 2015/9/8.
 * 展示预置位的popwindow
 */
public class prepointPopwindow extends PopupWindow implements View.OnClickListener {
    private final static String TAG = "dxsprepoint";
    public static final int GridCouluns = 3;
    public static final int DelayTime = 8000;
    private boolean isModify = false;
    private int selectedPoint = -1;
    private View conentView;
    private RecyclerView prePointGrild;
    private Context context;
    private prePointRecycleAdapter gridAdapter;
    private String deviceId, devicePwd;
    private Button btnBack, btnDelete, btnEditect;
    private List<String> deletePaths = new ArrayList<String>();
    private List<OnePrepoint> SelectedPrepoins = new ArrayList<OnePrepoint>();
//    private List<OnePrepoint> prePoints = new ArrayList<>();
    private LayoutInflater inflater;
    private int selectePoints=0;

    public prepointPopwindow(Context context, int h, String deviceId, String devicePwd, byte PrepointInfo) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popwin_prepoint, null);
        this.context = context;
        setContentView(conentView);
        setHeight(h);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.deviceId = deviceId;
        this.devicePwd = devicePwd;
        // 设置动画效果
        this.setAnimationStyle(R.style.popdown_up);
        deletePaths.clear();
        initUI(conentView);
        changeIconText(SelectedPrepoins);
    }

    private void initUI(View conentView) {
        prePointGrild = (RecyclerView) conentView.findViewById(R.id.recycle_grild_prepoint);
        //GridLayoutManager mgr = new GridLayoutManager(context, GridCouluns);
        StaggeredGridLayoutManager SGManager=new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        prePointGrild.setLayoutManager(SGManager);
        gridAdapter = new prePointRecycleAdapter(context, deviceId);
        gridAdapter.setOnItemClickLisener(itemClickLisener);
        gridAdapter.setOnItemLongClickLisener(itemLongClickLisener);
        gridAdapter.setOnPrepointListner(onPrepointListners);
        prePointGrild.setAdapter(gridAdapter);
        btnBack = (Button) conentView.findViewById(R.id.btn_popback);
        btnDelete = (Button) conentView.findViewById(R.id.btn_popdelete);
        btnEditect = (Button) conentView.findViewById(R.id.btn_popmodify);

        btnBack.setOnClickListener(this);
        btnEditect.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private prePointRecycleAdapter.onItemClickLisener itemClickLisener = new prePointRecycleAdapter.onItemClickLisener() {
        @Override
        public void onClick(View v, int position) {

        }
    };
    private prePointRecycleAdapter.onItemLongClickLisener itemLongClickLisener = new prePointRecycleAdapter.onItemLongClickLisener() {
        @Override
        public void onLongClick(View v, int position) {

        }
    };

    private prePointRecycleAdapter.onPrepointListner onPrepointListners = new prePointRecycleAdapter.onPrepointListner() {

        @Override
        public void addPrepoint(int prepoint) {
            if (prepoint != -1) {
                popwindowListner.addPrepoint(prepoint);
            } else {
                Log.e(TAG, "没有可以添加的预置位-->" + prepoint);
            }
        }

        @Override
        public void selectedPrepoint(OnePrepoint point,int position) {
            if (!deletePaths.contains(point.imagePath)) {
                selectedPoint = point.prepoint;
                deletePaths.add(point.imagePath);
                selectePoints=select(selectePoints,point.prepoint);
            }
            changeIconText(SelectedPrepoins);
            Log.e(TAG, "selectedPrepoint-->" + Integer.toBinaryString(selectePoints));
        }

        @Override
        public void cancelPrepoint(OnePrepoint point,int position) {
            deletePaths.remove(point.imagePath);
            changeIconText(SelectedPrepoins);
            selectePoints=delete(selectePoints,point.prepoint);
            Log.e(TAG, "cancelPrepoint-------->" + Integer.toBinaryString(selectePoints));
        }

        @Override
        public void toSeePrepoint(OnePrepoint point) {
            Utils.setPrePoints(deviceId, devicePwd, 0, point.prepoint);
            Log.e(TAG, "toSeePrepoint-->" + point.name);
            isAtPrepoint(point.prepoint, DelayTime);
        }

        @Override
        public void selectedPrepoints(List<OnePrepoint> Selected,List<String> deletePath,int selectePoint) {
            if(Selected!=null){
                SelectedPrepoins.clear();
                deletePaths.clear();
                deletePaths.addAll(deletePath);
                SelectedPrepoins.addAll(Selected);
                selectePoints=selectePoint;
                changeIconText(Selected);
            }
        }

    };

    private Handler isAtHandle=new Handler();
    private Runnable ss;
    private void isAtPrepoint(final int point,long time){
        ss=new Runnable() {
            @Override
            public void run() {
                Utils.setPrePoints(deviceId, devicePwd, 4, point);
            }
        };
        isAtHandle.postDelayed(ss, time);
    }

    /**指定某位二进制为1，其他位为0，即设置为预置位
     * @param src
     * @param position
     * @return
     */
    private int select(int src,int position){
        return src|(1<<position);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_popback:
                disMiss();
                break;
            case R.id.btn_popmodify:
                if (isModify) {
                    Prepoint po = getPrepoinNames();
                    if (po != null&&SelectedPrepoins.size()==1) {
                        showInputPrepointName(SelectedPrepoins.get(0).prepoint, po);
                        Log.e("dxsprepoint","selectedPoint-->"+SelectedPrepoins.get(0).prepoint);
                    }
                } else {
                    //退出选择模式
                    clearTemp();
                    gridAdapter.ClearPoints();
                    changeIconText(SelectedPrepoins);
                }
                break;
            case R.id.btn_popdelete:
                popwindowListner.onDeletePrepoint(deletePaths,selectePoints);
                break;
        }
    }


    public Prepoint getPrepoinNames() {
        return DataManager.findPrepointByDevice(context, deviceId);
    }

    public int[] ParsePrepointInfo(byte prepointInfo) {
        return Utils.getByteBinnery(prepointInfo, true);
    }


    private void changeIconText(List<OnePrepoint> Selecteds) {
        if (Selecteds==null||Selecteds.size() == 0) {
            btnDelete.setVisibility(View.GONE);
            if (gridAdapter.isSelectedMode) {
                isModify = false;
                btnEditect.setText(R.string.prepoint_abandon);
            } else {
                isModify = true;
                btnEditect.setVisibility(View.GONE);
                btnEditect.setText(R.string.edit);
            }
        } else if (Selecteds.size() == 1) {
            isModify = true;
            btnEditect.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnEditect.setText(R.string.edit);
        } else {
            isModify = false;
            btnEditect.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnEditect.setText(R.string.prepoint_abandon);
        }

    }

    private ImputDialog inputDialog;

    public void showInputPrepointName(final int prepointCount, final Prepoint prepoint) {
        inputDialog = new ImputDialog(context);
        inputDialog.setEdtextType(InputType.TYPE_CLASS_TEXT);
        inputDialog.setMaxCharater(8);
        inputDialog.SetText(Utils.getStringForId(R.string.prepoint_modifyname), "", prepoint.getName(prepointCount), context.getResources().getString(
                R.string.ensure), context.getResources().getString(
                R.string.cancel));
        inputDialog.setOnMyinputClickListner(new ImputDialog.MyInputClickListner() {
            @Override
            public void onYesClick(Dialog dialog, View v, String input) {
                if ("".equals(input)) {
                    T.showShort(context, R.string.prepoint_setname);
                    return;
                }
                prepoint.setName(prepointCount, input);
                DataManager.upDataPrepoint(context, deviceId, prepoint);
                gridAdapter.reFrushName(prepointCount, input);
                inputDialog.inputDialogDismiss();
                clearTemp();
                changeIconText(SelectedPrepoins);
            }

            @Override
            public void onNoClick(View v) {
                inputDialog.inputDialogDismiss();
            }

        });
        inputDialog.inputDialogShow();
    }

    public void addPrepoint(int point){
        gridAdapter.addPrepoint(point);
    }

    public void DeletePrepoint(byte info){
        gridAdapter.deletePrepoint(info);
        clearTemp();
        changeIconText(SelectedPrepoins);
    }

    public void disMiss() {
        clearTemp();
        gridAdapter.ClearPoints();
        if(isAtHandle!=null){
            isAtHandle.removeCallbacks(ss);
        }
        this.dismiss();
        changeIconText(SelectedPrepoins);
    }

    private void clearTemp(){
        SelectedPrepoins.clear();
        deletePaths.clear();
        selectePoints=0;
    }

    private OnPopwindowListner popwindowListner;
    public void setOnPopwindowListner(OnPopwindowListner popwindowListner){
        this.popwindowListner=popwindowListner;
    }
    public interface OnPopwindowListner{
        void onDeletePrepoint(List<String> deletePath,int deletePoint);
        void addPrepoint(int position);
    }
}
