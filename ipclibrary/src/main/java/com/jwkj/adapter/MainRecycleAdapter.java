package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.CallActivity;
import com.jwkj.PlayBackListActivity;
import com.jwkj.activity.AddApDeviceActivity;
import com.jwkj.activity.AddContactNextActivity;
import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.activity.DeviceUpdateActivity;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.activity.ModifyContactActivity;
import com.jwkj.activity.ModifyNpcPasswordActivity;
import com.jwkj.data.APContact;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.LocalDevice;
import com.jwkj.fragment.ContactFrag;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.FishEyeModeView;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.nuowei.ipclibrary.R;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	private Context mContext;
	public static final int TYPE_HEADER=0;
	public static final int TYPE_NORMAL=1;
	public static final int TYPE_AP=2;
	public MainRecycleAdapter(Context context) {
	    mContext=context;
	}
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		int size=FList.getInstance().size()+FList.getInstance().apListsize();
		if(size==0){
			return 1;
		}
		return size;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		switch (viewType) {
		case TYPE_HEADER:
			View itemView0 = View.inflate(parent.getContext(), R.layout.main_header, null);
			ViewHolder0 holder0=new ViewHolder0(itemView0);
			return holder0;
		case TYPE_NORMAL:
			View itemView1 = View.inflate(parent.getContext(), R.layout.list_device_item, null);
			ViewHolder holder=new ViewHolder(itemView1);
			return holder;
		default:
			View itemView2 = View.inflate(parent.getContext(), R.layout.list_device_item_no_login, null);
			ViewHolder2 holder2=new ViewHolder2(itemView2);
			return holder2;
		}
	}
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		int size=FList.getInstance().size()+FList.getInstance().apListsize();
		if(position==0&&size<=0){
			return TYPE_HEADER;
		}else if(position<FList.getInstance().size()) {
			return TYPE_NORMAL;
		} else{
		   return TYPE_AP;
		}
	}
	public static class ViewHolder extends RecyclerView.ViewHolder{
		public ViewHolder(View itemView1) {
			super(itemView1);
			// TODO Auto-generated constructor stub
			tView=itemView1;
			head=(HeaderView)itemView1.findViewById(R.id.user_icon);
			name=(TextView)itemView1.findViewById(R.id.tv_name);
			online_state=(TextView)itemView1.findViewById(R.id.tv_online_state);
			iv_defence_state=(ImageView)itemView1.findViewById(R.id.iv_defence_state);
			progress_defence=(ProgressBar)itemView1.findViewById(R.id.progress_defence);
			iv_weakpassword=(ImageView)itemView1.findViewById(R.id.iv_weakpassword);
			iv_update=(ImageView)itemView1.findViewById(R.id.iv_update);
			iv_editor=(ImageView)itemView1.findViewById(R.id.iv_editor);
			iv_playback=(ImageView)itemView1.findViewById(R.id.iv_playback);
			iv_set=(ImageView)itemView1.findViewById(R.id.iv_set);
			iv_call=(ImageView)itemView1.findViewById(R.id.iv_call);
			r_online_state=(RelativeLayout)itemView1.findViewById(R.id.r_online_state);
			modeView=(FishEyeModeView)itemView1.findViewById(R.id.fmv_main);
			r_mode=(RelativeLayout)itemView1.findViewById(R.id.r_mode);
			iv_key_housekeep=(ImageView)itemView1.findViewById(R.id.iv_key_housekeep);
			l_editor_name=(LinearLayout) itemView1.findViewById(R.id.l_editor_name);
		}
		private HeaderView head;
		private TextView name;
		private TextView online_state;
		private ImageView iv_defence_state;
		private ProgressBar progress_defence;
		private ImageView iv_weakpassword;
		private ImageView iv_update;
		private ImageView iv_editor;
		private ImageView iv_playback;
		private ImageView iv_set;
		private RelativeLayout r_online_state;
		private ImageView iv_call;
		public FishEyeModeView modeView;
		public RelativeLayout r_mode;
		public View tView;
		public ImageView iv_key_housekeep;
		public LinearLayout l_editor_name;
	}
	public static class ViewHolder2 extends RecyclerView.ViewHolder{
		public ViewHolder2(View itemView2) {
			super(itemView2);
			// TODO Auto-generated constructor stub
			tView2=itemView2;
			name=(TextView)itemView2.findViewById(R.id.tv_name);
			head=(HeaderView)itemView2.findViewById(R.id.user_icon);
			iv_ap_state=(ImageView)itemView2.findViewById(R.id.iv_ap_state);
			iv_defence_state=(ImageView)itemView2.findViewById(R.id.iv_defence_state);
			progress_defence=(ProgressBar)itemView2.findViewById(R.id.progress_defence);
			iv_playback=(ImageView)itemView2.findViewById(R.id.iv_playback);
			iv_set=(ImageView)itemView2.findViewById(R.id.iv_set);
			iv_editor=(ImageView)itemView2.findViewById(R.id.iv_editor);

		}
		public TextView name;
		private HeaderView head;
//		private LinearLayout l_ap;
		private ImageView iv_ap_state;
		private ImageView iv_defence_state;
		private ProgressBar progress_defence;
//		private ImageView iv_weakpassword;
		private ImageView iv_update;
		private ImageView iv_playback;
		private ImageView iv_set;
		private ImageView iv_editor;
//		private RelativeLayout r_online_state;
		public View tView2;
		
	}
	public static class ViewHolder0 extends RecyclerView.ViewHolder{

		public ViewHolder0(View itemView0) {
			super(itemView0);
			// TODO Auto-generated constructor stub
		}
		
	}
	@Override
	public void onBindViewHolder(
			RecyclerView.ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		int viewType=getItemViewType(position);
		if(viewType==TYPE_NORMAL){
			final Contact contact = FList.getInstance().get(position);
			final ViewHolder holder1=(ViewHolder) holder; 
			holder1.name.setText(contact.contactName);
			if(contact.isConnectApWifi==true){
				holder1.r_online_state.setVisibility(View.GONE);
				holder1.iv_defence_state.setVisibility(View.GONE);
				holder1.progress_defence.setVisibility(View.GONE);
			}else{
				holder1.r_online_state.setVisibility(View.VISIBLE);
				
			}
			 int deviceType = contact.contactType;
			 if(deviceType==P2PValue.DeviceType.NPC){
				 holder1.iv_call.setVisibility(View.VISIBLE);
			 }else if(deviceType==P2PValue.DeviceType.NVR){
				 holder1.iv_defence_state.setVisibility(View.INVISIBLE);
				 holder1.progress_defence.setVisibility(View.GONE);
				 holder1.iv_call.setVisibility(View.GONE);
			 }else{
				 holder1.iv_call.setVisibility(View.GONE);
			 }
			 File f=new File(Environment.getExternalStorageDirectory().getPath()+"/screenshot/tempHead/" + NpcCommon.mThreeNum
						+ "/" + contact.contactId + ".jpg");
			 if(f!=null){
				 String tag="file://"+Environment.getExternalStorageDirectory().getPath()+"/screenshot/tempHead/" + NpcCommon.mThreeNum
						 + "/" + contact.contactId + ".jpg"+f.lastModified();
				 if(holder1.head.getTag()==null){
					 holder1.head.updateImage(contact.contactId, false,contact.contactType,holder1.head,tag);
				 }else{
					 if(!holder1.head.getTag().equals(tag)){
						 holder1.head.updateImage(contact.contactId, false,contact.contactType,holder1.head,tag);
					 }
				 }
			 }
//				holder1.head.updateImage(contact.contactId, false,contact.contactType);
			    holder1.iv_set.setVisibility(View.VISIBLE);
			    holder1.iv_playback.setVisibility(View.VISIBLE);
			    holder1.iv_key_housekeep.setVisibility(View.VISIBLE);
				if (contact.onLineState == Constants.DeviceState.ONLINE) {
					holder1.online_state.setText(R.string.online_state);
					holder1.online_state.setTextColor(
							mContext.getResources().getColor(R.color.white));
					if (contact.contactType == P2PValue.DeviceType.UNKNOWN
							|| contact.contactType == P2PValue.DeviceType.PHONE||contact.contactType==P2PValue.DeviceType.NVR) {
						holder1.iv_defence_state.setVisibility(
								RelativeLayout.INVISIBLE);
					} else {
						holder1.iv_defence_state.setVisibility(
									RelativeLayout.VISIBLE);
						if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_LOADING) {
							holder1.progress_defence.setVisibility(
										RelativeLayout.VISIBLE);
							holder1.iv_defence_state.setVisibility(
									RelativeLayout.INVISIBLE);
						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
							holder1.progress_defence.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_defence_state.setImageResource(
									R.drawable.ic_key_housekeep);
							holder1.iv_defence_state.setVisibility(
									RelativeLayout.VISIBLE);
							holder1.iv_key_housekeep.setImageResource(R.drawable.selector_key_housekeep_on);
						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
							holder1.progress_defence.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_defence_state.setImageResource(
									R.drawable.item_disarm);
							holder1.iv_defence_state.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_key_housekeep.setImageResource(R.drawable.selector_key_housekeep_off);
						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
							holder1.progress_defence.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_defence_state.setImageResource(
									R.drawable.ic_defence_warning);
							holder1.iv_defence_state.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_key_housekeep.setImageResource(R.drawable.selector_key_housekeep_off);
						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
							holder1.progress_defence.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_defence_state.setImageResource(
									R.drawable.ic_device_pwd_error_warning);
							holder1.iv_defence_state.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_key_housekeep.setImageResource(R.drawable.selector_key_housekeep_off);
						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
							holder1.progress_defence.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_defence_state.setImageResource(
									R.drawable.ic_visitor);
							holder1.iv_defence_state.setVisibility(
									RelativeLayout.GONE);
							holder1.iv_key_housekeep.setImageResource(R.drawable.selector_key_housekeep_off);
//							holder1.iv_set.setVisibility(View.GONE);
//							holder1.iv_playback.setVisibility(View.GONE);
//							holder1.iv_key_housekeep.setVisibility(View.GONE);
						}
					}
					// 如果是门铃且不是访客密码则获取报警推送账号并判断自己在不在其中，如不在则添加(只执行一次)
					if (deviceType == P2PValue.DeviceType.DOORBELL
							&& contact.defenceState != Constants.DefenceState.DEFENCE_NO_PERMISSION) {
						if (!getIsDoorBellBind(contact.contactId)) {
							getBindAlarmId(contact.contactId, contact.contactPassword);
						} else {

						}
					}
//					如果子设备类型支持情景
					if(contact.isSmartHomeContatct()){
						showMode(holder1, contact);
					}else{
						holder1.modeView.setVisibility(FishEyeModeView.GONE);
						holder1.r_mode.setVisibility(View.GONE);
						holder1.r_online_state.setVisibility(View.VISIBLE);
					}
					
					if(contact.isPanorama()){
						P2PHandler.getInstance().getFishEyeInfo(contact.contactId, contact.contactPassword);
					}

				} else {
					holder1.online_state.setText(R.string.offline_state);
					holder1.online_state.setTextColor(
							mContext.getResources().getColor(R.color.text_color_white));
					holder1.iv_defence_state.setVisibility(RelativeLayout.INVISIBLE);
					holder1.progress_defence.setVisibility(View.GONE);
	                //不在线不显示模式			
					holder1.modeView.setVisibility(View.GONE);
					holder1.r_mode.setVisibility(View.GONE);
					holder1.iv_key_housekeep.setImageResource(R.drawable.selector_key_housekeep_off);
					holder1.iv_key_housekeep.setVisibility(View.VISIBLE);

				}
				// 获得布防状态之后判断弱密码
				if (contact.onLineState == Constants.DeviceState.ONLINE
						&& (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON || contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF)) {	
					if (Utils.isWeakPassword(contact.userPassword)) {
						holder1.iv_weakpassword.setVisibility(View.VISIBLE);
						listner.onShowGuide(holder1.iv_weakpassword, position);
					} else {
						holder1.iv_weakpassword.setVisibility(View.GONE);
					}
					if(contact.Update==Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_VERSION||contact.Update==Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_IN_SD){
						//屏蔽了new图片的展示
//						holder1.iv_update.setVisibility(ImageView.VISIBLE);
					}else{
						holder1.iv_update.setVisibility(ImageView.GONE);
					}
				}else{
					holder1.iv_weakpassword.setVisibility(View.GONE);
					holder1.iv_update.setVisibility(ImageView.GONE);
				}
				if (deviceType == P2PValue.DeviceType.NPC
						|| deviceType == P2PValue.DeviceType.IPC
						|| deviceType == P2PValue.DeviceType.DOORBELL) {
					holder1.head.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if(!ContactFrag.isHideAdd){
								Intent it = new Intent();
								it.setAction(Constants.Action.DIAPPEAR_ADD);
								mContext.sendBroadcast(it);
								return;
							}
							if(contact.isConnectApWifi==true){
								T.showShort(mContext,R.string.change_phone_net);
								return;
							}
							if(isUnSetPasswordDevice(contact)){
								return;
							}
							toMonitor(contact);
						}

					});
				}else if(deviceType==P2PValue.DeviceType.NVR){
					holder1.head.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if(Utils.isFastDoubleClick()){
								return;
							}
							if(!ContactFrag.isHideAdd){
								Intent it = new Intent();
								it.setAction(Constants.Action.DIAPPEAR_ADD);
								mContext.sendBroadcast(it);
								return;
							}
//						    隐藏nvr功能
							if(isUnSetPasswordDevice(contact)){
								return;
							}
						    listner.onNvrClick(contact);
						}
					});
					
				}else if (deviceType == P2PValue.DeviceType.PHONE) {
					holder1.head.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if(!ContactFrag.isHideAdd){
								Intent it = new Intent();
								it.setAction(Constants.Action.DIAPPEAR_ADD);
								mContext.sendBroadcast(it);
								return;
							}
							if (contact.contactId == null
									|| contact.contactId.equals("")) {
								T.showShort(mContext, R.string.username_error);
								return;
							}

							Intent call = new Intent();
							call.setClass(mContext, CallActivity.class);
							call.putExtra("callId", contact.contactId);
							call.putExtra("contact",contact);
							call.putExtra("isOutCall", true);
							call.putExtra("type", Constants.P2P_TYPE.P2P_TYPE_CALL);
							mContext.startActivity(call);
						}

					});
//					holder.getHeader_icon_play().setVisibility(RelativeLayout.VISIBLE);
				} else {
					holder1.head.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if(!ContactFrag.isHideAdd){
								Intent it = new Intent();
								it.setAction(Constants.Action.DIAPPEAR_ADD);
								mContext.sendBroadcast(it);
								return;
							}
							if(contact.isConnectApWifi==true){
								T.showShort(mContext,R.string.change_phone_net);
							}
							if (Integer.parseInt(contact.contactId) < 256) {
								toMonitor(contact);
							} else {
								if(isUnSetPasswordDevice(contact)){
									return;
								}
								String ipAdress = FList.getInstance().getCompleteIPAddress(
										contact.contactId);
								if(ipAdress!=null&&!ipAdress.equals("")){
									toMonitor(contact);
								}else{
									T.showShort(mContext,R.string.no_use);
									holder1.head.setOnClickListener(null);
								}
							}
							
						}
					});

				}
				holder1.iv_defence_state.setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View arg0) {
									if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET
											|| contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
										holder1.progress_defence.setVisibility(
												RelativeLayout.VISIBLE);
										holder1.iv_defence_state.setVisibility(
												RelativeLayout.INVISIBLE);
										P2PHandler.getInstance().getDefenceStates(
												contact.contactId, contact.contactPassword);
										FList.getInstance().setIsClickGetDefenceState(
												contact.contactId, true);
									}
//									else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
//										holder1.progress_defence.setVisibility(
//												RelativeLayout.VISIBLE);
//										holder1.iv_defence_state.setVisibility(
//												RelativeLayout.INVISIBLE);
//										P2PHandler.getInstance().setRemoteDefence(
//														contact.contactId,
//														contact.contactPassword,
//														Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
//										FList.getInstance().setIsClickGetDefenceState(
//												contact.contactId, true);
//									} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
//										holder1.progress_defence.setVisibility(
//												RelativeLayout.VISIBLE);
//										holder1.iv_defence_state.setVisibility(
//												RelativeLayout.INVISIBLE);
//										P2PHandler
//												.getInstance()
//												.setRemoteDefence(
//														contact.contactId,
//														contact.contactPassword,
//														Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
//										FList.getInstance().setIsClickGetDefenceState(
//												contact.contactId, true);
//									}
								}

						});
				holder1.tView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(!ContactFrag.isHideAdd){
							Intent it = new Intent();
							it.setAction(Constants.Action.DIAPPEAR_ADD);
							mContext.sendBroadcast(it);
							return;
						}
						isUnSetPasswordDevice(contact);
					}

				});

				holder1.head.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View arg0) {
						// TODO Auto-generated method stub

						NormalDialog dialog = new NormalDialog(mContext, mContext
								.getResources().getString(R.string.delete_contact),
								mContext.getResources().getString(
										R.string.are_you_sure_delete)
										+ " " + contact.contactId + "?", mContext
										.getResources().getString(R.string.delete),
										mContext.getResources().getString(R.string.cancel));
						dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

							@Override
							public void onClick() {
								// TODO Auto-generated method stub
								FList.getInstance().delete(contact, position, handler);
								File file = new File(Constants.Image.USER_HEADER_PATH
										+ NpcCommon.mThreeNum + "/" + contact.contactId);
								Utils.deleteFile(file);
								if (position == 0 && FList.getInstance().size() == 0&&FList.getInstance().apListsize()==0) {
									Intent it = new Intent();
									it.setAction(Constants.Action.DELETE_DEVICE_ALL);
									MyApp.app.sendBroadcast(it);
								}
							}
						});
						dialog.showDialog();
						return true;
					}

				});
				holder1.iv_weakpassword.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(isUnSetPasswordDevice(contact)){
							return;
						}
						Intent modify_pwd=new Intent(mContext,ModifyNpcPasswordActivity.class);
						modify_pwd.putExtra("contact",contact);
						modify_pwd.putExtra("isWeakPwd",true);
						mContext.startActivity(modify_pwd);
					}
				});
				holder1.iv_playback.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
//						if(contact.contactType==P2PValue.DeviceType.UNKNOWN){
//							return;
//						}
					    if(contact.isConnectApWifi==true){
							T.showShort(mContext,R.string.change_phone_net);
							return;
						}
						if(isUnSetPasswordDevice(contact)){
							return;
						}
//					    if(contact.onLineState==Constants.DeviceState.ONLINE){
					    	
					    	if(contact.contactType==P2PValue.DeviceType.NVR){
					    		listner.onNnrPlayBackClick(contact);
					    	}else{
//					    		Intent playback = new Intent();
//						    	playback.putExtra("contact", contact);
//					    		playback.setClass(mContext, PlayBackListActivity.class);
//					    		mContext.startActivity(playback);
								listner.onPlayBackClick(contact);
					    	}
							
//					    }else{
//					    	T.showLong(mContext, R.string.offline_state);
//					    }
					}
				});
				holder1.iv_editor.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if(isUnSetPasswordDevice(contact)){
							return;
						}
						DeviceEditor(contact);
					}
				});
				holder1.iv_set.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if(contact.isConnectApWifi==true){
							T.showShort(mContext,R.string.change_phone_net);
							return;
						}
						if(isUnSetPasswordDevice(contact)){
							return;
						}
						if(contact.contactType==P2PValue.DeviceType.NVR){
	                     //    隐藏nvr功能
							Intent modify_password = new Intent(mContext,
									ModifyNpcPasswordActivity.class);
							modify_password.putExtra("contact", contact);
							modify_password.putExtra("isModifyNvrPwd", true);
							mContext.startActivity(modify_password);
						}else{
							DeviceSettingClick(contact);
						}
					}
				});
				holder1.iv_update.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(contact.contactType==P2PValue.DeviceType.UNKNOWN&&Integer.valueOf(contact.contactId)>256){
							return;
						}
						if(isUnSetPasswordDevice(contact)){
							return;
						}
						Intent check_update = new Intent(mContext,
								DeviceUpdateActivity.class);
						check_update.putExtra("contact", contact);
						check_update.putExtra("isUpdate", true);
						mContext.startActivity(check_update);
					}
				});
				holder1.iv_call.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stu
						if(contact.isConnectApWifi==true){
							T.showShort(mContext,R.string.change_phone_net);
							return;
						}
						if(isUnSetPasswordDevice(contact)){
							return;
						}
//						if(contact.contactType==P2PValue.DeviceType.UNKNOWN&&Integer.valueOf(contact.contactId)>256){
//							return;
//						}
						 if(contact.contactId==null||contact.contactId.equals("")){
						 T.showShort(mContext,R.string.username_error);
						 return;
						 }
						 if(contact.contactPassword==null||contact.contactPassword.equals("")){
						 T.showShort(mContext,R.string.password_error);
						 return;
						 }
						 Intent i=new Intent();
						 i.putExtra("contact",contact);
						 i.setAction(Constants.Action.CALL_DEVICE);
						 mContext.sendBroadcast(i);
					}
				});
				holder1.modeView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(isUnSetPasswordDevice(contact)){
							return;
						}
						listner.onModeClick(contact, position);
					}
				});
			    holder1.iv_key_housekeep.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(contact.isConnectApWifi==true){
							T.showShort(mContext,R.string.change_phone_net);
							return;
						}
						if(isUnSetPasswordDevice(contact)){
							return;
						}
						if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
							holder1.progress_defence.setVisibility(
									RelativeLayout.VISIBLE);
							holder1.iv_defence_state.setVisibility(
									RelativeLayout.INVISIBLE);
							P2PHandler.getInstance().setRemoteDefence(
									contact.contactId,
									contact.contactPassword,
									Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
							FList.getInstance().setIsClickGetDefenceState(
									contact.contactId, true);
						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
							holder1.progress_defence.setVisibility(
									RelativeLayout.VISIBLE);
							holder1.iv_defence_state.setVisibility(
									RelativeLayout.INVISIBLE);
							P2PHandler
									.getInstance()
									.setRemoteDefence(
											contact.contactId,
											contact.contactPassword,
											Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
							FList.getInstance().setIsClickGetDefenceState(
									contact.contactId, true);
						}else{
							P2PHandler.getInstance().getDefenceStates(
									contact.contactId, contact.contactPassword);
							FList.getInstance().setIsClickGetDefenceState(
									contact.contactId, true);
						}
					}
				});
			holder1.l_editor_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(isUnSetPasswordDevice(contact)){
						return;
					}
					listner.onEditorDeviceName(contact);
				}
			});

//			}
		}else if(viewType==TYPE_HEADER){
			
		}else{
			 int size=FList.getInstance().size();
			 final LocalDevice apdevice=FList.getInstance().getAPDdeviceByPosition(position-size);
			 final ViewHolder2 holder2=(ViewHolder2) holder; 
			 holder2.name.setText(apdevice.name);
			 holder2.head.updateImage(apdevice.contactId, true);
		     if(WifiUtils.getInstance().isConnectWifi(AppConfig.Relese.APTAG+apdevice.contactId)){
		    	 holder2.iv_ap_state.setBackgroundResource(R.drawable.item_ap_link);
				if (apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_LOADING) {
					holder2.progress_defence.setVisibility(
								RelativeLayout.VISIBLE);
					holder2.iv_defence_state.setVisibility(
							RelativeLayout.INVISIBLE);
				} else if (apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
					holder2.progress_defence.setVisibility(
							RelativeLayout.GONE);
					holder2.iv_defence_state.setImageResource(
							R.drawable.item_arm);
					holder2.iv_defence_state.setVisibility(
							RelativeLayout.VISIBLE);

				} else if (apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
					holder2.progress_defence.setVisibility(
							RelativeLayout.GONE);
					holder2.iv_defence_state.setImageResource(
							R.drawable.item_disarm);
					holder2.iv_defence_state.setVisibility(
							RelativeLayout.VISIBLE);
				} else if (apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
					holder2.progress_defence.setVisibility(
							RelativeLayout.GONE);
					holder2.iv_defence_state.setImageResource(
							R.drawable.ic_defence_warning);
					holder2.iv_defence_state.setVisibility(
							RelativeLayout.VISIBLE);
				} else if (apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
					holder2.progress_defence.setVisibility(
							RelativeLayout.GONE);
					holder2.iv_defence_state.setImageResource(
							R.drawable.ic_device_pwd_error_warning);
					holder2.iv_defence_state.setVisibility(
							RelativeLayout.GONE);
				} else if (apdevice.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
					holder2.progress_defence.setVisibility(
							RelativeLayout.GONE);
					holder2.iv_defence_state.setImageResource(
							R.drawable.ic_visitor);
					holder2.iv_defence_state.setVisibility(
							RelativeLayout.GONE);
				}
			 }else{
				 holder2.iv_ap_state.setBackgroundResource(R.drawable.item_ap_unlick);
				 holder2.iv_defence_state.setVisibility(View.GONE);
				 holder2.progress_defence.setVisibility(View.GONE);
			 }
		     holder2.iv_editor.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						Contact saveContact = new Contact();
						saveContact.contactId = apdevice.contactId;
						saveContact.contactName = apdevice.name;
						saveContact.contactType = apdevice.type;
						APContact ap=DataManager.findAPContactByActiveUserAndContactId(mContext, NpcCommon.mThreeNum, apdevice.contactId);
						if(ap!=null){
							saveContact.wifiPassword = ap.Pwd;
						}else{
							saveContact.wifiPassword = "";
						}
						saveContact.contactPassword = "0";
						saveContact.contactFlag=apdevice.flag;
						saveContact.mode=P2PValue.DeviceMode.AP_MODE;
						try {
							saveContact.ipadressAddress=InetAddress.getByName(Utils.getAPDeviceIp(mContext));
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						saveContact.messageCount = 0;
						saveContact.activeUser = NpcCommon.mThreeNum;
						Intent modify = new Intent();
						modify.setClass(mContext, ModifyContactActivity.class);
						modify.putExtra("contact", saveContact);
						modify.putExtra("isEditorWifiPwd", true);
						mContext.startActivity(modify);
					}
				});
		     holder2.iv_defence_state.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//ap设备布撤防单击，完全复制普通设备单击事件
					Contact saveContact = new Contact();
					saveContact.contactId = apdevice.contactId;
					saveContact.contactName = apdevice.name;
					saveContact.contactType = apdevice.type;
					saveContact.contactPassword = "0";
					saveContact.contactFlag=apdevice.flag;
					saveContact.mode=P2PValue.DeviceMode.AP_MODE;
					try {
						saveContact.ipadressAddress=InetAddress.getByName(Utils.getAPDeviceIp(mContext));
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					saveContact.messageCount = 0;
					saveContact.activeUser = NpcCommon.mThreeNum;
					if(saveContact.contactType==P2PValue.DeviceType.IPC){
						if (apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET
								|| apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
							holder2.progress_defence.setVisibility(
									RelativeLayout.VISIBLE);
							holder2.iv_defence_state.setVisibility(
									RelativeLayout.INVISIBLE);
							P2PHandler.getInstance().getDefenceStates(
									saveContact.getIpContactId(), "0");
//							FList.getInstance().setIsClickGetDefenceState(
//									saveContact.contactId, true);
						} else if (apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
							holder2.progress_defence.setVisibility(
									RelativeLayout.VISIBLE);
							holder2.iv_defence_state.setVisibility(
									RelativeLayout.INVISIBLE);
							P2PHandler
									.getInstance()
									.setRemoteDefence(
											saveContact.getIpContactId(), "0",
											Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
//							FList.getInstance().setIsClickGetDefenceState(
//									saveContact.contactId, true);
						} else if (apdevice.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
							holder2.progress_defence.setVisibility(
									RelativeLayout.VISIBLE);
							holder2.iv_defence_state.setVisibility(
									RelativeLayout.INVISIBLE);
							P2PHandler
									.getInstance()
									.setRemoteDefence(
											saveContact.getIpContactId(), "0",
											Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
//							FList.getInstance().setIsClickGetDefenceState(
//									saveContact.contactId, true);
						}
					}
				}
			});
		     holder2.iv_set.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(WifiUtils.getInstance().isConnectWifi(AppConfig.Relese.APTAG+apdevice.contactId)){
						Contact saveContact = new Contact();
						saveContact.contactId = apdevice.contactId;
						saveContact.contactName = apdevice.name;
						saveContact.contactType = apdevice.type;
						saveContact.contactPassword = "0";
						saveContact.contactFlag=apdevice.flag;
						saveContact.mode=P2PValue.DeviceMode.AP_MODE;
						try {
							saveContact.ipadressAddress=InetAddress.getByName(Utils.getAPDeviceIp(mContext));
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						saveContact.messageCount = 0;
						saveContact.activeUser = NpcCommon.mThreeNum;
						ApDeviceToMainContro(saveContact);
					}else{
						T.showShort(mContext, R.string.connect_device_wifi);
					}
				}
			});
		     holder2.iv_playback.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(WifiUtils.getInstance().isConnectWifi(AppConfig.Relese.APTAG+apdevice.contactId)){
						Contact saveContact = new Contact();
						saveContact.contactId = apdevice.contactId;
						saveContact.contactName = apdevice.name;
						saveContact.contactPassword = "0";
						saveContact.contactType = apdevice.type;
						saveContact.contactFlag=apdevice.flag;
						saveContact.mode=P2PValue.DeviceMode.AP_MODE;
						try {
							saveContact.ipadressAddress=InetAddress.getByName(Utils.getAPDeviceIp(mContext));
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						saveContact.messageCount = 0;
						saveContact.activeUser = NpcCommon.mThreeNum;
						Intent control = new Intent();
						control.setClass(mContext, PlayBackListActivity.class);
						control.putExtra("contact", saveContact);
						mContext.startActivity(control);
					}else{
						T.showShort(mContext, R.string.connect_device_wifi);
					}
				}
			});
		     holder2.head.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(!ContactFrag.isHideAdd){
						Intent it = new Intent();
						it.setAction(Constants.Action.DIAPPEAR_ADD);
						mContext.sendBroadcast(it);
						return;
					}
					Contact saveContact = new Contact();
					saveContact.contactId = apdevice.contactId;
					saveContact.contactName = apdevice.name;
					saveContact.contactType = apdevice.type;
					saveContact.contactPassword = "0";
					saveContact.contactFlag=apdevice.flag;
					saveContact.mode=P2PValue.DeviceMode.AP_MODE;
					try {
						saveContact.ipadressAddress=InetAddress.getByName(Utils.getAPDeviceIp(mContext));
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					saveContact.messageCount = 0;
					saveContact.activeUser = NpcCommon.mThreeNum;
					String mark = apdevice.address.getHostAddress();
					if(WifiUtils.getInstance().isConnectWifi(saveContact.getAPName())){
						//已连接WIFI
						Intent apMonitor=new Intent(mContext, ApMonitorActivity.class);
						 try {
							 saveContact.ipadressAddress=InetAddress.getByName(Utils.getAPDeviceIp(mContext));
						 } catch (UnknownHostException e) {
								e.printStackTrace();
						 }
						 apMonitor.putExtra("contact", saveContact);
						 apMonitor.putExtra("connectType", Constants.ConnectType.RTSPCONNECT);
						 apMonitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						 mContext.startActivity(apMonitor);
					}else{
						//未连接WIFI
						Intent modify = new Intent();
						modify.setClass(mContext, AddApDeviceActivity.class);
						modify.putExtra("isCreatePassword", false);
						modify.putExtra("isAPModeConnect", 0);
						modify.putExtra("contact", saveContact);
						modify.putExtra("ipFlag","1");
						mContext.startActivity(modify);	
					}
						
//					}
//					
				}
			});
		}
		
	}
	
	
	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			notifyDataSetChanged();
			return true;
		}
	});
	public void showMode(ViewHolder holder,Contact contact){
		holder.modeView.setTextDeviceName(contact.contactName);
		holder.modeView.setModeStatde(contact.FishMode);
		holder.progress_defence.setVisibility(View.GONE);
		holder.iv_defence_state.setVisibility(RelativeLayout.INVISIBLE);
		holder.r_online_state.setVisibility(View.GONE);
		holder.modeView.setVisibility(View.VISIBLE);
		holder.r_mode.setVisibility(View.VISIBLE);
		holder.iv_key_housekeep.setVisibility(View.GONE);
	}
	/**
	 * 普通设备去监控
	 * @param contact
	 */
	private void toMonitor(Contact contact){
//		if(isNeedModifyPwd(contact)){
//			Intent modify_pwd=new Intent(context,ModifyNpcPasswordActivity.class);
//			modify_pwd.putExtra("contact",contact);
//			modify_pwd.putExtra("isWeakPwd",true);
//			context.startActivity(modify_pwd);
//			return;
//		}
		if (null != FList.getInstance().isContactUnSetPassword(
				contact.contactId)) {
			return;
		}
		if (contact.contactId == null
				|| contact.contactId.equals("")) {
			T.showShort(mContext, R.string.username_error);
			return;
		}
		if (contact.contactPassword == null
				|| contact.contactPassword.equals("")) {
			T.showShort(mContext, R.string.password_error);
			return;
		}
		Intent monitor = new Intent();
		monitor.setClass(mContext, ApMonitorActivity.class);
		monitor.putExtra("contact", contact);
		monitor.putExtra("connectType",Constants.ConnectType.P2PCONNECT);
		monitor.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(monitor);
	}
	/**
	 * 普通模式下的编辑
	 * @param contact
	 */
	private void DeviceEditor(Contact contact) {
		LocalDevice localDevice = FList.getInstance()
				.isContactUnSetPassword(contact.contactId);
		//判断局域网密码
		if (null != localDevice) {
			Contact saveContact = new Contact();
			saveContact.contactId = localDevice.contactId;
			saveContact.contactType = localDevice.type;
			saveContact.messageCount = 0;
			saveContact.activeUser = NpcCommon.mThreeNum;

			Intent modify = new Intent();
			modify.setClass(mContext, AddContactNextActivity.class);
			modify.putExtra("isCreatePassword", true);
			modify.putExtra("contact", saveContact);
			String mark = localDevice.address.getHostAddress();
			modify.putExtra(
					"ipFlag",
					mark.substring(mark.lastIndexOf(".") + 1,
							mark.length()));
			modify.putExtra("ip", mark);
			mContext.startActivity(modify);
			return;
		}else{
			Intent modify = new Intent();
			modify.setClass(mContext, ModifyContactActivity.class);
			modify.putExtra("contact", contact);
			modify.putExtra("isEditorWifiPwd", false);
			mContext.startActivity(modify);
		}
	}
	/**
	 * 设置按钮单击跳转流程
	 * @param contact
	 */
	private void DeviceSettingClick(Contact contact){
		if(contact.contactId==null||contact.contactId.equals("")){
			T.showShort(mContext,R.string.username_error);
			return;
		}
		if(contact.contactPassword==null||contact.contactPassword.equals("")){
			T.showShort(mContext,R.string.password_error);
			return;
		}
		Intent i=new Intent();
		i.putExtra("contact",contact);
		i.setAction(Constants.Action.ENTER_DEVICE_SETTING);
		mContext.sendBroadcast(i);
	}
	
	public onConnectListner listner;
	public interface onConnectListner{
		void onNvrClick(Contact contact);
		void onModeClick(Contact contact, int position);
		void onNnrPlayBackClick(Contact contact);
		void onShowGuide(View view, int position);
		void onPlayBackClick(Contact contact);
		void onEditorDeviceName(Contact contact);
	}
	public void setOnSrttingListner(onConnectListner onConnectListner){
		this.listner=(onConnectListner) onConnectListner;
	}
	
	
	List<String> doorbells = new ArrayList<String>();
	Map<String, String[]> idMaps = new HashMap<String, String[]>();

	private boolean getIsDoorBellBind(String doorbellid) {
		return SharedPreferencesManager.getInstance().getIsDoorbellBind(
				mContext, doorbellid);
	}
	private void getBindAlarmId(String id, String password) {
		if (!doorbells.contains(id)) {
			doorbells.add(id);
		}
		P2PHandler.getInstance().getBindAlarmId(id, password);
	}
	private int count = 0;// 总请求数计数器
	private int SumCount = 20;// 总请求次数上限

	public void getBindAlarmId(String id) {

		Contact contact = DataManager.findContactByActiveUserAndContactId(
				mContext, NpcCommon.mThreeNum, id);
		if (contact != null && count <= SumCount) {
			// 获取绑定id列表
			P2PHandler.getInstance().getBindAlarmId(contact.contactId,
					contact.contactPassword);
			count++;
		}
	}

	public void setBindAlarmId(String id, String[] ids) {
		int ss = 0;
		String[] new_data;
		for (int i = 0; i < ids.length; i++) {
			if (!NpcCommon.mThreeNum.equals(ids[i])) {
				ss++;
			}
		}
		if (ss == ids.length) {
			// 不包含则设置
			new_data = new String[ids.length + 1];
			for (int i = 0; i < ids.length; i++) {
				new_data[i] = ids[i];
			}
			new_data[new_data.length - 1] = NpcCommon.mThreeNum;
			Contact contact = DataManager.findContactByActiveUserAndContactId(
					mContext, NpcCommon.mThreeNum, id);
			P2PHandler.getInstance().setBindAlarmId(contact.contactId,
					contact.contactPassword, new_data.length, new_data);
		} else {
			new_data = ids;
		}
		idMaps.put(id, new_data);

	}

	public void setBindAlarmId(String Id) {
		Contact contact = DataManager.findContactByActiveUserAndContactId(
				mContext, NpcCommon.mThreeNum, Id);
		if (contact != null && (!idMaps.isEmpty())) {
			String[] new_data = idMaps.get(Id);
			P2PHandler.getInstance().setBindAlarmId(contact.contactId,
					contact.contactPassword, new_data.length, new_data);
		}

	}

	public void setBindAlarmIdSuccess(String doorbellid) {
		SharedPreferencesManager.getInstance().putIsDoorbellBind(doorbellid,
				true, mContext);
	}
	
	
	/**
	 * ap设备跳转到设置页
	 * @param contact
	 */
	private void ApDeviceToMainContro(Contact contact){
		Intent control = new Intent();
		control.setClass(mContext, MainControlActivity.class);
		Contact c = new Contact();
		c.contactName=contact.contactName;
		c.contactPassword=contact.contactPassword;
		c.userPassword=contact.userPassword;
		c.messageCount=contact.messageCount;
		c.contactId="1";
		c.activeUser=contact.activeUser;
		c.contactType=contact.contactType;
		c.defenceState=contact.defenceState;
		c.ipadressAddress=contact.ipadressAddress;
		c.mode=contact.mode;
		control.putExtra("contact", c);
		control.putExtra("connectType", 1);
		control.putExtra("type", P2PValue.DeviceType.IPC);
		mContext.startActivity(control);
	}
    @Override
    public void onViewAttachedToWindow(
    		android.support.v7.widget.RecyclerView.ViewHolder holder) {
    	// TODO Auto-generated method stub
    	super.onViewAttachedToWindow(holder);
    }
    public void notifyData(){
    	notifyDataSetChanged();
    }
	private  boolean isUnSetPasswordDevice(Contact contact){
		LocalDevice localDevice = FList.getInstance()
				.isContactUnSetPassword(contact.contactId);
		if (null != localDevice) {
			Contact saveContact = new Contact();
			saveContact.contactId = localDevice.contactId;
			saveContact.contactType = localDevice.type;
			saveContact.messageCount = 0;
			saveContact.activeUser = NpcCommon.mThreeNum;

			Intent modify = new Intent();
			modify.setClass(mContext, AddContactNextActivity.class);
			modify.putExtra("isCreatePassword", true);
			modify.putExtra("contact", saveContact);
			String mark = localDevice.address.getHostAddress();
			modify.putExtra(
					"ipFlag",
					mark.substring(mark.lastIndexOf(".") + 1,
							mark.length()));
			modify.putExtra("ip", mark);
			mContext.startActivity(modify);
			return true;
		}
		return false;
	}
}

