package com.jwkj.data;

import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.utils.Utils;
import com.jwkj.utils.WifiUtils;
import com.p2p.core.P2PValue;

import java.io.Serializable;
import java.net.InetAddress;

public class Contact implements Serializable, Comparable<Contact> {
	// id
	public int id;
	// 联系人名称
	public String contactName="";
	// 联系人ID
	public String contactId;
	// 联系人监控密码 注意：不是登陆密码，只有当联系人类型为设备才有
	public String contactPassword="0";
	// 联系人类型
	public int contactType;
	// 此联系人发来多少条未读消息
	public int messageCount;
	// 当前登录的用户
	public String activeUser="";
	// 在线状态 不保存数据库
	public int onLineState = Constants.DeviceState.OFFLINE;
	// 布放状态不保存数据库
	public int defenceState = Constants.DefenceState.DEFENCE_STATE_LOADING;
	// 记录是否是点击获取布放状态 不保存数据库
	public boolean isClickGetDefenceState = false;
	// 联系人标记 不保存数据库
	
	public int contactFlag;
	// ip地址
	public InetAddress ipadressAddress;
    // 用户输入的密码
	public String userPassword="";
    //是否设备有更新
	public int Update=Constants.P2P_SET.DEVICE_UPDATE.UNKNOWN;
    //当前版本
	public String cur_version="";
	//可更新到的版本
	public String up_version="";
    //有木有rtsp标记
	public int rtspflag=0;
	// 按在线状态排序
	public String wifiPassword="12345678";
    //AP模式下的wifi密码
	public int mode=P2PValue.DeviceMode.GERNERY_MODE;
	public int apModeState=Constants.APmodeState.UNLINK;
	public boolean isConnectApWifi=false;
	public int subType=0;
	public int FishMode=-1;
	public int videow=896;
	public int videoh=896;
	public int fishPos=0;
	@Override
	public int compareTo(Contact arg0) {
		// TODO Auto-generated method stub
		//排序优先级：在线 弱密码 ID号
		Contact o = (Contact) arg0;
		if (o.onLineState > this.onLineState) {
			return 1;
		} else if (o.onLineState < this.onLineState) {
			return -1;
		} 
		if(o.defenceState<2&&this.defenceState<2){
			if(!(Utils.isWeakPassword(this.userPassword)==Utils.isWeakPassword(o.userPassword))){
				return Utils.getPassWordStatus(this.userPassword)-Utils.getPassWordStatus(o.userPassword);
			}
		}else{
			if(o.defenceState<2&&Utils.isWeakPassword(o.userPassword)){
				return 1;
			}
			if(this.defenceState<2&&Utils.isWeakPassword(this.userPassword)){
				return -1;
			}
		}
		if(Integer.parseInt(o.contactId)<Integer.parseInt(this.contactId)){
            return 1;
		}else if(Integer.parseInt(o.contactId)>Integer.parseInt(this.contactId)){
			return -1;
		}
		return 0;
	}
	/**
	 * 获取设备IP最后一段
	 * @return 空返回""
	 */
	public String getIpMark(){
		if(ipadressAddress!=null){
			String mark=ipadressAddress.getHostAddress();
			return mark.substring(mark.lastIndexOf(".")+1,mark.length());
		}
		return "";
	}
	
	public String getContactId(){
		if(mode==P2PValue.DeviceMode.AP_MODE){
			return "1";
		}else{
			return contactId;
		}
	}
    public String getIpContactId(){
//    	if(mode==P2PValue.DeviceMode.AP_MODE){
    		String ip=getIpMark();
    		if(!ip.equals("")){
    			return ip;
    		}
//    	}
    	return contactId;
    }
    public String getPassword(){
    	if(contactPassword==null||!Utils.isNumeric(contactPassword)){
    		return "0";
    	}else{
    		return contactPassword;
    	}


    }
	
	public void setApModeState(int state){
		this.apModeState=state;
	}
	
	public int getApModeState(){
		if(contactType==P2PValue.DeviceType.IPC&&mode==P2PValue.DeviceMode.AP_MODE){
			 String wifiName=AppConfig.Relese.APTAG+contactId;
			if(WifiUtils.getInstance().isConnectWifi(AppConfig.Relese.APTAG+contactId)){
				apModeState=Constants.APmodeState.LINK;
			}else{
				if(WifiUtils.getInstance().isScanExist(wifiName)){
					//处于AP模式
					apModeState=Constants.APmodeState.UNLINK;
				}else{
					return apModeState;
				}
			}
		}
			return apModeState;
	}
	
	public String getAPName(){
		return AppConfig.Relese.APTAG+contactId;
	}
	/**
	 * 获取显示名称
	 * @return 
	 */
	public String getContactName(){
		if(contactName!=null&&contactName.length()>0){
			return contactName;
		}else{
			return contactId;
		}
	}
	
	/**
	 * 判断是否是智能家居设备
	 * @return true 是
	 */
	public boolean isSmartHomeContatct(){
		return Utils.isSmartHomeContatct(contactType, subType);
	}
	
	public boolean isPanorama(){
		if(subType == P2PValue.subType.IPC_PANOMA_180_720
				||subType == P2PValue.subType.IPC_PANOMA_180_960
				||subType == P2PValue.subType.IPC_PANOMA_360_720
				||subType == P2PValue.subType.IPC_PANOMA_360_960
				){
			return true;
		}
		return false;
	}
	
	public boolean is360Panorama(){
		if(isPanorama()){
			return subType == P2PValue.subType.IPC_PANOMA_360_720
					||subType == P2PValue.subType.IPC_PANOMA_360_960;
		}
		return false;
	}
	
	public boolean is180Panorama(){
		if(isPanorama()){
			return subType == P2PValue.subType.IPC_PANOMA_180_720
					||subType == P2PValue.subType.IPC_PANOMA_180_960;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Contact{" +
				"mode=" + mode +
				", apModeState=" + apModeState +
				", isConnectApWifi=" + isConnectApWifi +
				", subType=" + subType +
				", FishMode=" + FishMode +
				", videow=" + videow +
				", videoh=" + videoh +
				", fishPos=" + fishPos +
				'}';
	}
}
