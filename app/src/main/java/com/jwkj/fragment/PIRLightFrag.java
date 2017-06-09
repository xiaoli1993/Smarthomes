package com.jwkj.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.data.Contact;
import com.jwkj.entity.PirLight;
import com.jwkj.fragment.PIRLightFrag.Level;
import com.jwkj.global.Constants;
import com.jwkj.interfac.dialogShowItem;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnCustomCancelListner;
import com.jwkj.widget.ProgressTextView;
import com.jwkj.widget.RangeSeekBar;
import com.jwkj.widget.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.jwkj.widget.SwitchView;
import com.jwkj.widget.NormalDialog.onStringDialogSelectListner;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.nuowei.smarthome.R;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class PIRLightFrag extends BaseFragment implements OnClickListener{
	private final static int PIR_TYPE=0;
	private final static int LIGHT_TYPE=1;
	private final static int SET_PIR=0;
	private Context mContext;
	private boolean isRegFilter = false;
	private Contact mContact;
	private RelativeLayout rlPIR,rlLight,rlLightTime,rlPIRSwitch;
	private RelativeLayout rl_light_level_tx,rl_light_level,rl_light_max_min,rl_light_max_min_te,rl_light_switch;
	private SwitchView swLightSwitvh,swPIRSwitch,swLightSwitchAuto;
	private ProgressTextView txPirLevel;
	private PirLight light=new PirLight();
	private RangeSeekBar<Number> LightTimeSeek;
	private RangeSeekBar<Number> LightMaxMin;
	private RangeSeekBar<Number> LightLevel;
	private static final int LightTime=0;
	private static final int LightMAXMin=1;
	private static final int LightLEVEL=2;
	private static final int MAXRale1=4;
	private static final int MAXRale2=2;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pirlight, container,
				false);
		mContext = getActivity();
		mContact = (Contact) getArguments().getSerializable("contact");
		initUI(view);
		return view;
	}
	
	private void initUI(View view) {
		rlPIR=(RelativeLayout) view.findViewById(R.id.rl_pir);
		rlLight=(RelativeLayout) view.findViewById(R.id.rl_light);
		swPIRSwitch=(SwitchView) view.findViewById(R.id.sv_pir);
		swLightSwitvh=(SwitchView) view.findViewById(R.id.sv_light);
		txPirLevel=(ProgressTextView) view.findViewById(R.id.pt_pirlevel);
		LightTimeSeek=(RangeSeekBar<Number>) view.findViewById(R.id.rs_lighttime);
		rlLightTime=(RelativeLayout) view.findViewById(R.id.rl_light_time);
		rlPIRSwitch=(RelativeLayout) view.findViewById(R.id.rl_pir_switch);
		LightMaxMin=(RangeSeekBar<Number>) view.findViewById(R.id.rs_light_max_min);
		LightLevel=(RangeSeekBar<Number>) view.findViewById(R.id.rs_light_level);
		rl_light_switch=(RelativeLayout) view.findViewById(R.id.rl_light_switch);
		swLightSwitchAuto=(SwitchView) view.findViewById(R.id.sv_light_switch);
		
		rl_light_level_tx=(RelativeLayout) view.findViewById(R.id.rl_light_level_tx);
		rl_light_level=(RelativeLayout) view.findViewById(R.id.rl_light_level);
		rl_light_max_min=(RelativeLayout) view.findViewById(R.id.rl_light_max_min);
		rl_light_max_min_te=(RelativeLayout) view.findViewById(R.id.rl_light_max_min_te);
		
		swPIRSwitch.setOnClickListener(this);
		rlPIR.setOnClickListener(this);
		rlLight.setOnClickListener(this);
		swLightSwitvh.setOnClickListener(this);
		swLightSwitchAuto.setOnClickListener(this);
		LightTimeSeek.setType(LightTime);
		LightTimeSeek.setOnRangeSeekBarChangeListener(Seekbarlistner);
		LightMaxMin.setType(LightMAXMin);
		LightMaxMin.setSuffix("Lux");
		LightMaxMin.setOnRangeSeekBarChangeListener(Seekbarlistner);
		LightLevel.setType(LightLEVEL);
		LightLevel.setSuffix("%");
		LightLevel.setOnRangeSeekBarChangeListener(Seekbarlistner);
	}
	
	private OnRangeSeekBarChangeListener<Number> Seekbarlistner=new OnRangeSeekBarChangeListener<Number>() {

		@Override
		public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
				Number minValue, Number maxValue) {
			
		}

		@Override
		public void onRangeSeekBarValuesSelected(RangeSeekBar<?> bar,
				Number minValue, Number maxValue,int type) {
			showLoadingDialog(listener, SET_PIR);
			if(type==LightTime){
				PirLight pirClone=light.clone();
				pirClone.LightKeepTimeLevel=maxValue.shortValue();
				setPirLight(pirClone);
			}else if(type==LightMAXMin){
				PirLight pirClone=light.clone();
				pirClone.AdcMinValue=maxValue.byteValue();
				if((pirClone.AdcMinValue&0xFF)<50){
					pirClone.AdcMaxValue=(byte) (Math.min(200, (pirClone.AdcMinValue&0xFF)*MAXRale1));
				}else{
					pirClone.AdcMaxValue=(byte) (Math.min(200, (pirClone.AdcMinValue&0xFF)*MAXRale2));
				}
				setPirLight(pirClone);
			}else if(type==LightLEVEL){
				PirLight pirClone=light.clone();
				pirClone.PwmLightLux=(byte) (maxValue.byteValue()*127/100);
				setPirLight(pirClone);
			}
			
		}
	};

	private void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_SETTING_LIGHT_CONTROL);
		filter.addAction(Constants.P2P.RET_GET_LIGHT_CONTROL_SETTING);
		filter.addAction(Constants.P2P.ACK_GET_PIRLight);
		filter.addAction(Constants.P2P.ACK_SET_PIRLight);
		mContext.registerReceiver(br, filter);
		isRegFilter = true;
		initData();
	}

	private void initData() {
		getPirLight(light);
	}

	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String iSrcID = intent.getStringExtra("iSrcID");
			byte[] data = intent.getByteArrayExtra("data");
			Log.e("dxsTest","device:"+Arrays.toString(data));
			if (intent.getAction().equals(Constants.P2P.RET_SETTING_LIGHT_CONTROL)) {
				hindLoadingDialog();
				if(data[2]==Constants.FishBoption.MESG_SET_OK){
					light.UpData(data, 4);
					FrushUI();
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_GET_LIGHT_CONTROL_SETTING)){
				if(data[2]==Constants.FishBoption.MESG_GET_OK){
					light.UpData(data, 4);
					FrushUI();
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_GET_PIRLight)){
				int result=intent.getIntExtra("state", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					getPirLight(light);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_SET_PIRLight)){
				int result=intent.getIntExtra("state", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					FrushUI();
					hindLoadingDialog();
					T.showLong(mContext, R.string.net_error);
				}
			}
		}
	};
	
	private void FrushUI(){
		LightTimeSeek.setSelectedMaxValue(Math.max(5, light.LightKeepTimeLevel));
		if(light.LightKeepTimeLevel<=5){
			T.showLong(mContext, R.string.pir_test_mode);
		}
		LightMaxMin.setSelectedMaxValue(light.AdcMinValue&0xFF);
		LightLevel.setSelectedMaxValue((light.PwmLightLux&0xFF)*100/127);
		txPirLevel.setModeStatde(ProgressTextView.STATE_TEXT, light.getLevelString());
		if(light.ManualTurnFlag==1){
			swLightSwitvh.setModeStatde(SwitchView.State_on);
			rlPIRSwitch.setVisibility(View.GONE);
			rlLight.setVisibility(View.GONE);
			rlPIR.setVisibility(View.GONE);
			rlLightTime.setVisibility(View.GONE);
			rl_light_level_tx.setVisibility(View.VISIBLE);
			rl_light_level.setVisibility(View.VISIBLE);
			rl_light_max_min.setVisibility(View.GONE);
			rl_light_max_min_te.setVisibility(View.GONE);
			rl_light_switch.setVisibility(View.GONE);
		}else{
			swLightSwitvh.setModeStatde(SwitchView.State_off);
			rlPIRSwitch.setVisibility(View.VISIBLE);
			rlLight.setVisibility(View.VISIBLE);
			rlPIR.setVisibility(View.VISIBLE);
			rlLightTime.setVisibility(View.VISIBLE);
			rl_light_level_tx.setVisibility(View.VISIBLE);
			rl_light_level.setVisibility(View.VISIBLE);
			rl_light_max_min.setVisibility(View.VISIBLE);
			rl_light_max_min_te.setVisibility(View.VISIBLE);
			rl_light_switch.setVisibility(View.VISIBLE);
			if(light.PirLightTurnOnSwitch==1){
				if(swPIRSwitch.getModeStatde()!=SwitchView.State_on){
					T.showLong(mContext, R.string.pir_defence_tips);
				}
				swPIRSwitch.setModeStatde(SwitchView.State_on);
				rlPIR.setVisibility(View.VISIBLE);
			}else{
				swPIRSwitch.setModeStatde(SwitchView.State_off);
				rlPIR.setVisibility(View.GONE);
			}
			if(light.AdcLightSwitch==1){
				swLightSwitchAuto.setModeStatde(SwitchView.State_on);
				rlLight.setVisibility(View.GONE);
				rlLightTime.setVisibility(View.GONE);
			}else{
				swLightSwitchAuto.setModeStatde(SwitchView.State_off);
				rlLight.setVisibility(View.VISIBLE);
				rlLightTime.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sv_pir:
			PirLight pirClone=light.clone();
			pirClone.changeSwitch(PirLight.PirLightSwitch);
			setPirLight(pirClone);
			showLoadingDialog(listener, SET_PIR);
			break;
		case R.id.sv_light:
			PirLight lightClone=light.clone();
			lightClone.changeSwitch(PirLight.ManualTurnSwitch);
			setPirLight(lightClone);
			showLoadingDialog(listener, SET_PIR);
			break;
		case R.id.rl_pir:
			NormalDialog dialogPir=new NormalDialog(mContext);
			dialogPir.setTitle(R.string.pir_level);
			dialogPir.showStringListDialog(getItems(0), light.AdcLightDetectSensi, PIR_TYPE,151);
			dialogPir.setOnStringDialogSelectListner(StringSelect);
			break;
		case R.id.rl_light:
			break;
		case R.id.sv_light_switch:
			PirLight ligtswitch=light.clone();
			ligtswitch.changeSwitch(PirLight.AdcSwitch);
			setPirLight(ligtswitch);
			showLoadingDialog(listener, SET_PIR);
			break;
		default:
			break;
		}
		
	}
	//获取PIR灯光控制
	private void getPirLight(PirLight light){
		P2PHandler.getInstance().getPIRLight(mContact.contactId, mContact.contactPassword, 0);
	}
	//设置PIR灯光控制
	private void setPirLight(PirLight light){
		if(light!=null){
			P2PHandler.getInstance().setPIRLight(mContact.contactId, mContact.contactPassword, light.getPirLightInfo());
		}
	}
	
	private  onStringDialogSelectListner<Level> StringSelect=new onStringDialogSelectListner<Level>() {

		@Override
		public void onItemSelect(AlertDialog dialog, Level k, int position,
				int type, boolean isSame) {
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			if(!isSame){
				PirLight lightClone=light.clone();
				if(type==PIR_TYPE){
					lightClone.AdcLightDetectSensi=(byte) position;
				}else{
					lightClone.LightKeepTimeLevel=(byte) position;
				}
				setPirLight(lightClone);
				showLoadingDialog(listener, SET_PIR);
			}
		}
	};
	
	private NormalDialog.OnCustomCancelListner listener=new OnCustomCancelListner() {
		
		@Override
		public void onCancle(int mark) {
			
		}
	};
	
	@Override
	public void onResume() {
		regFilter();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if (isRegFilter) {
			mContext.unregisterReceiver(br);
			isRegFilter = false;
		}
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		 Intent it = new Intent();
	     it.setAction(Constants.Action.CONTROL_BACK);
	     mContext.sendBroadcast(it);
		super.onDestroy();
	}
	
	private List<Level> getItems(int select){
		List<Level> levels=new ArrayList<Level>();
		String[] names=new String[]{
				Utils.getStringForId(R.string.pir_numberlevel1),Utils.getStringForId(R.string.pir_numberlevel2)
				,Utils.getStringForId(R.string.pir_numberlevel3),Utils.getStringForId(R.string.pir_numberlevel4)
		};
		for (int i=0;i<4;i++) {
			levels.add(new Level(names[i], select==i));
		}
		return levels;
	}

	public class Level implements dialogShowItem{
		private String name;
		private boolean isSelect;
		
		public Level(String name, boolean isSelect) {
			super();
			this.name = name;
			this.isSelect = isSelect;
		}

		@Override
		public String getName() {
			return name;
		}
		
	}

}
