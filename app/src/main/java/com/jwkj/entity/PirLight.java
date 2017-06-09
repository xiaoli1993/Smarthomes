package com.jwkj.entity;

import com.jwkj.utils.Utils;
import com.p2p.core.utils.MyUtils;
import com.nuowei.smarthome.R;

public class PirLight implements Cloneable {
	public static final int PirLightSwitch = 0;
	public static final int ManualTurnSwitch = 1;
	public static final int AdcSwitch = 2;
	public byte AdcMaxValue;
	public byte AdcMinValue;
	public byte PwmLightLux;
	public byte AdcLightDetectSensi;
	public byte PirLightTurnOnSwitch;//0关1开
	public byte ManualTurnFlag;//1开  2关
	public byte AdcLightSwitch;//0关1开
	public short LightKeepTimeLevel;

	public PirLight() {
		super();
	}

	public PirLight(byte adcMaxValue, byte adcMinValue, byte pwmLightLux,
			byte adcLightDetectSensi, byte pirLightTurnOnSwitch,
			byte manualTurnFlag, byte adcLightSwitch, short lightKeepTimeLevel) {
		super();
		AdcMaxValue = adcMaxValue;
		AdcMinValue = adcMinValue;
		PwmLightLux = pwmLightLux;
		AdcLightDetectSensi = adcLightDetectSensi;
		PirLightTurnOnSwitch = pirLightTurnOnSwitch;
		ManualTurnFlag = manualTurnFlag;
		AdcLightSwitch = adcLightSwitch;
		LightKeepTimeLevel = lightKeepTimeLevel;
	}

	public PirLight(byte[] data, int offset) {
		AdcMaxValue = data[offset];
		AdcMinValue = data[offset + 1];
		PwmLightLux = data[offset + 2];
		AdcLightDetectSensi = data[offset + 3];
		PirLightTurnOnSwitch = data[offset + 4];
		ManualTurnFlag = data[offset + 5];
		AdcLightSwitch = data[offset + 6];
		LightKeepTimeLevel = MyUtils.byte2ToShort(data, offset + 7);
	}

	public void UpData(byte[] data, int offset) {
		AdcMaxValue = data[offset];
		AdcMinValue = data[offset + 1];
		PwmLightLux = data[offset + 2];
		AdcLightDetectSensi = data[offset + 3];
		PirLightTurnOnSwitch = data[offset + 4];
		ManualTurnFlag = data[offset + 5];
		AdcLightSwitch = data[offset + 6];
		LightKeepTimeLevel = MyUtils.byte2ToShort(data, offset + 7);
	}

	public byte[] getPirLightInfo() {
		byte[] info = new byte[9];
		info[0] = AdcMaxValue;
		info[1] = AdcMinValue;
		info[2] = PwmLightLux;
		info[3] = AdcLightDetectSensi;
		info[4] = PirLightTurnOnSwitch;
		info[5] = ManualTurnFlag;
		info[6] = AdcLightSwitch;
		byte[] temp = MyUtils.shortToByte2(LightKeepTimeLevel);
		System.arraycopy(temp, 0, info, 7, temp.length);
		return info;
	}

	public String getLevelString() {
		switch (AdcLightDetectSensi) {
		case 0:
			return Utils.getStringForId(R.string.pir_numberlevel1);
		case 1:
			return Utils.getStringForId(R.string.pir_numberlevel2);
		case 2:
			return Utils.getStringForId(R.string.pir_numberlevel3);
		case 3:
			return Utils.getStringForId(R.string.pir_numberlevel4);
		default:
			return Utils.getStringForId(R.string.pir_numberlevel1);
		}
	}
	//开启其中一个，需关闭其他两个
	public void changeSwitch(int type) {
		switch (type) {
		case PirLightSwitch:
			if(PirLightTurnOnSwitch==1){
				PirLightTurnOnSwitch=0;
			}else{
				PirLightTurnOnSwitch=1;
				ManualTurnFlag=2;
				AdcLightSwitch=0;
			}
			break;
		case ManualTurnSwitch:
			if(ManualTurnFlag==1){
				ManualTurnFlag=2;
			}else{
				ManualTurnFlag=1;
				PirLightTurnOnSwitch=0;
				AdcLightSwitch=0;
			}
			break;
		case AdcSwitch:
			if(AdcLightSwitch==1){
				AdcLightSwitch=0;
			}else{
				AdcLightSwitch=1;
				ManualTurnFlag=2;
				PirLightTurnOnSwitch=0;
			}
			break;

		default:
			break;
		}
	}

	@Override
	public PirLight clone() {
		PirLight o = null;
		try {
			o = (PirLight) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public String toString() {
		return "PirLight [AdcMaxValue=" + AdcMaxValue + ", AdcMinValue="
				+ AdcMinValue + ", PwmLightLux=" + PwmLightLux
				+ ", AdcLightDetectSensi=" + AdcLightDetectSensi
				+ ", PirLightTurnOnSwitch=" + PirLightTurnOnSwitch
				+ ", ManualTurnFlag=" + ManualTurnFlag + ", AdcLightSwitch="
				+ AdcLightSwitch + ", LightKeepTimeLevel=" + LightKeepTimeLevel
				+ "]";
	}
}
