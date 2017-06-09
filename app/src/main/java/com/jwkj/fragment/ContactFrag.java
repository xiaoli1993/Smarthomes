package com.jwkj.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.jwkj.CallActivity;
import com.jwkj.NVRMonitorActivity;
import com.jwkj.NVRPlayBackActivity;
import com.jwkj.PlayBackListActivity;
import com.jwkj.activity.AddApDeviceActivity;
import com.jwkj.activity.AddContactActivity;
import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.activity.LocalDeviceListActivity;
import com.jwkj.activity.MainControlActivity;
import com.jwkj.activity.NetworkModeActivity;
import com.jwkj.activity.RadarAddFirstActivity;
import com.jwkj.adapter.MainRecycleAdapter;
import com.jwkj.adapter.MainRecycleAdapter.onConnectListner;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.data.JAContact;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.entity.LocalDevice;
import com.jwkj.fisheye.FisheyeSetHandler;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.global.NpcCommon;
import com.jwkj.thread.MainThread;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.utils.WifiUtils;
import com.jwkj.widget.ConfirmDialog;
import com.jwkj.widget.HeaderTextView;
import com.jwkj.widget.InputPasswordDialog;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnNormalDialogTimeOutListner;
import com.lib.pullToRefresh.PullToRefreshBase;
import com.lib.pullToRefresh.PullToRefreshBase.OnRefreshListener;
import com.lib.pullToRefresh.PullToRefreshListView;
import com.lib.quick_action_bar.QuickActionWidget;
import com.lib.showtipview.ShowTipsBuilder;
import com.lib.showtipview.ShowTipsView;
import com.lib.showtipview.ShowTipsView.showGuideListner;
import com.libzxing.activity.CaptureActivity;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

public class ContactFrag extends BaseFragment implements OnClickListener {

	public static final int CHANGE_REFRESHING_LABLE = 0x12;
	private static final int QRcodeRequestCode = 2;
	private static int ToNVRPlayBackOrMonitor = -1;
	private Context mContext;
	private boolean isRegFilter = false;
	private boolean isDoorBellRegFilter = false;

	private ListView mListView;
	private ImageView mAddUser;
//	private MainAdapter mAdapter;
	private PullToRefreshListView mPullRefreshListView;
	boolean refreshEnd = false;
	boolean isFirstRefresh = true;
	boolean isActive;
	boolean isCancelLoading=true;
	private QuickActionWidget mBar;
	private LinearLayout net_work_status_bar;
	private RelativeLayout local_device_bar_top;
	private TextView text_local_device_count;
	NormalDialog dialog;
	Handler handler;
	private Contact next_contact;
	private LinearLayout layout_add;
	private RelativeLayout layout_contact;
	private LinearLayout radar_add,manually_add,scan_it;
	boolean isOpenThread = false;
	Handler myHandler = new Handler();
	int count1 = 0;
	int count2 = 0;
	private final int ENTER_SET=0;
	private final int ENTER_PLAYBACK=1;
	private int enterType;
	

	public static boolean isHideAdd = true;
	Animation animation_out, animation_in;
	NormalDialog dialog_loading;
	NormalDialog dialog_update;
	NormalDialog dialog_connect;
	String wifiName="";
	private Contact apContact;
	private Contact nvrContact;
    MainRecycleAdapter mAdapter;
    RecyclerView lv_contact;
    PtrFrameLayout mPtrFrame;
    private PtrClassicDefaultHeader mPtrClassicHeader;
    boolean isShowGuide=false;
    public boolean isrefreshCommit=true;
    ShowTipsView showtips;
	InputPasswordDialog inputPwdDialog,modifyNameDialog;
	ConfirmDialog confirmDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_contact, container,
				false);
		mContext = getActivity();
		initComponent(view);
		regFilter();
		if (isFirstRefresh) {
			isFirstRefresh = !isFirstRefresh;
			FList flist = FList.getInstance();
			flist.updateOnlineState();
			flist.searchLocalDevice();
			flist.getModeState();
		}
		return view;
	}
	public void initComponent(View view) {
		mAddUser = (ImageView) view.findViewById(R.id.button_add);
		net_work_status_bar = (LinearLayout) view
				.findViewById(R.id.net_status_bar_top);
		local_device_bar_top = (RelativeLayout) view
				.findViewById(R.id.local_device_bar_top);
		text_local_device_count = (TextView) view
				.findViewById(R.id.text_local_device_count);
		mPullRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		layout_add = (LinearLayout) view.findViewById(R.id.layout_add);
		layout_contact = (RelativeLayout) view
				.findViewById(R.id.layout_contact);
		radar_add = (LinearLayout) view.findViewById(R.id.radar_add);
		manually_add = (LinearLayout) view.findViewById(R.id.manually_add);
		scan_it=(LinearLayout)view.findViewById(R.id.scan_it);
		lv_contact=(RecyclerView)view.findViewById(R.id.lv_contact);
		mPtrFrame=(PtrFrameLayout)view.findViewById(R.id.mPtrFrame);
		
		mPtrClassicHeader = new PtrClassicDefaultHeader(mContext);
		mPtrFrame.setLoadingMinTime(1000);
		mPtrFrame.setDurationToCloseHeader(1500);
		mPtrFrame.setResistance(1.7f);
		mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
		mPtrFrame.setDurationToClose(200);
		// default is false
		mPtrFrame.setPullToRefresh(false);
		// default is true
		mPtrFrame.setKeepHeaderWhenRefresh(true);
		mPtrFrame.setHeaderView(mPtrClassicHeader);
		mPtrFrame.addPtrUIHandler(mPtrClassicHeader);
		mPtrFrame.setPtrHandler(new PtrHandler() {
		    @Override
		    public void onRefreshBegin(PtrFrameLayout frame) {
		       new GetDataTask().execute();
		    }
		    @Override
		    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
		        // 默认实现，根据实际情况做改动
		        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
		    }
			@Override
			public void onRefreshComplete(PtrFrameLayout frame) {
				// TODO Auto-generated method stub
				isrefreshCommit=true;
				if(showtips!=null){
					showtips.isrefreshCommit=true;
				}
			}
			@Override
			public void onUIReresh(PtrFrameLayout frame) {
				// TODO Auto-generated method stubs
				isrefreshCommit=false;
			    if(showtips!=null){
			      showtips.isrefreshCommit=false; 
			    }
			}
		});
		// header
		final StoreHouseHeader header = new StoreHouseHeader(mContext);
		header.setPadding(0, Utils.dip2px(mContext,15), 0, 0);
		/**
		 * using a string, support: A-Z 0-9 - .
		 * you can add more letters by {@link in.srain.cube.views.ptr.header.StoreHousePath#addChar}
		 */
		header.initWithStringArray(R.array.storehouse);
		
		radar_add.setOnClickListener(this);
		manually_add.setOnClickListener(this);
		scan_it.setOnClickListener(this);
		layout_contact.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (isHideAdd == false) {
					hideAdd();
				}
				return false;
			}
		});
		local_device_bar_top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(mContext, LocalDeviceListActivity.class);
				mContext.startActivity(i);
			}

		});
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(mContext,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						new GetDataTask().execute();
					}
				});
		mPullRefreshListView.setShowIndicator(false);
		mListView = mPullRefreshListView.getRefreshableView();
//		mAdapter = new MainAdapter(mContext, this);
		mAdapter=new MainRecycleAdapter(mContext);
		mAdapter.setOnSrttingListner(new onConnectListner() {
			@Override
			public void onNvrClick(Contact contact) {
				// TODO Auto-generated method stub
				nvrContact = contact;
				// P2PHandler.getInstance().getNvrIpcList(contact.contactId,
				// contact.contactPassword);
				// 头像还是要存存数据库
				JAContact jacontact = DataManager
						.findJAContactByActiveUserAndContactId(MyApplication.app,
								NpcCommon.mThreeNum, contact.contactId);
				if (jacontact != null) {
					// 直接监控
					Intent toNvr = new Intent();
					toNvr.setClass(mContext, NVRMonitorActivity.class);
					toNvr.putExtra("contact", nvrContact);
					toNvr.putExtra("jacontact", jacontact);
					startActivity(toNvr);
				} else {
					//去查询
					P2PHandler.getInstance().getNVRInfo(contact.contactId,
							contact.contactPassword);
					dialog_loading = new NormalDialog(mContext);
					dialog_loading.setTitle(getResources().getString(
							R.string.verification));
					dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
					dialog_loading.showDialog();
				}
				ToNVRPlayBackOrMonitor = 1;
			}

			@Override
			public void onNnrPlayBackClick(Contact contact) {
				nvrContact = contact;
				// P2PHandler.getInstance().getNvrIpcList(contact.contactId,
				// contact.contactPassword);
				P2PHandler.getInstance().getNVRInfo(contact.contactId,
						contact.contactPassword);
				dialog_loading = new NormalDialog(mContext);
				dialog_loading.setTitle(getResources().getString(
						R.string.verification));
				dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				dialog_loading.showDialog();
				ToNVRPlayBackOrMonitor = 2;
			}

			@Override
			public void onModeClick(Contact contact, int position) {
				// TODO Auto-generated method stub
				 NormalDialog dialog = new NormalDialog(mContext);
		            dialog.setOnDialogItemClick(onDialogItemClick);
		            dialog.setmContact(contact);
		            dialog.showModeSelectDialog(position);
			}

			@Override
			public void onShowGuide(View view, int position) {
				boolean isShow=SharedPreferencesManager.getInstance().getShowGuide(mContext,SharedPreferencesManager.SHOW_WEAKPWD_GUIDE);
				if(position==0&&!isShow){
					showTips(view);
				}
				
			}

			@Override
			public void onPlayBackClick(Contact contact) {
				next_contact = contact;
				dialog = new NormalDialog(mContext);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						// TODO Auto-generated method stub
						isCancelLoading = true;
					}

				});
				dialog.showLoadingDialog2();
				dialog.setCanceledOnTouchOutside(false);
				isCancelLoading = false;
				P2PHandler.getInstance().checkPassword(contact.contactId,
						contact.contactPassword);
				myHandler.postDelayed(runnable, 20000);
				enterType=ENTER_PLAYBACK;
				count1++;
			}

			@Override
			public void onEditorDeviceName(Contact contact) {
				modifyNameDialog = new InputPasswordDialog(mContext);
				modifyNameDialog.setInputPasswordClickListener(inputNameClickListener);
				modifyNameDialog.setContactId(contact.contactId);
				modifyNameDialog.setType(InputPasswordDialog.EDITOR_DIALOG);
				modifyNameDialog.show();
				modifyNameDialog.settingDialog(mContext.getString(R.string.edit), contact.contactName,
						mContext.getString(R.string.please_input_device_name), 64,
						mContext.getString(R.string.confirm),
						mContext.getString(R.string.cancel), InputType.TYPE_CLASS_TEXT);
			}
		});
		upadataTextView();
//		mListView.setAdapter(mAdapter);
		mAddUser.setOnClickListener(this);
//		mListView.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				// TODO Auto-generated method stub
//				if (isHideAdd == false) {
//					hideAdd();
//				}
//				return false;
//			}
//		});
		lv_contact.setLayoutManager(new LinearLayoutManager(mContext));
		lv_contact.setAdapter(mAdapter);
		List<LocalDevice> localDevices = FList.getInstance().getLocalDevicesNoAP();
		if (localDevices.size() > 0) {
			local_device_bar_top.setVisibility(RelativeLayout.VISIBLE);
			text_local_device_count.setText("" + localDevices.size());
		} else {
			local_device_bar_top.setVisibility(RelativeLayout.GONE);
		}
		List<Contact> contacts = DataManager.findContactByActiveUser(mContext,
				NpcCommon.mThreeNum);
		animation_out = AnimationUtils.loadAnimation(mContext,
				R.anim.scale_amplify);
		animation_in = AnimationUtils.loadAnimation(mContext,
				R.anim.scale_narrow);
		mPtrFrame.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (isHideAdd == false) {
					hideAdd();
				}
				return false;
			}
		});
		lv_contact.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (isHideAdd == false) {
					hideAdd();
				}
				return false;
			}
		});
	}
	
	

	public void upadataTextView() {
		// 添加空列表提示
//		if(mAdapter.isEmpty()){
//			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
//					LayoutParams.MATCH_PARENT, 350);
//			v.setLayoutParams(params);
//			if(mListView.getHeaderViewsCount()<=1){
//				mListView.addHeaderView(v,null,false);
//			}
//		}else{
//			mListView.removeHeaderView(v);
//		}
		
	}

	private void addListHeader() {
		// 添加头部
		HeaderTextView header = new HeaderTextView(mContext, "", "");
		AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, R.dimen.contact_item_margin);
		header.setLayoutParams(headerParams);
		mListView.addHeaderView(header,null,false);
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.REFRESH_CONTANTS);
		filter.addAction(Constants.Action.GET_FRIENDS_STATE);
		filter.addAction(Constants.Action.LOCAL_DEVICE_SEARCH_END);
		filter.addAction(Constants.Action.ACTION_NETWORK_CHANGE);
		filter.addAction(Constants.P2P.ACK_RET_CHECK_PASSWORD);
		filter.addAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
		filter.addAction(Constants.Action.SETTING_WIFI_SUCCESS);
		filter.addAction(Constants.Action.DIAPPEAR_ADD);
		filter.addAction(Constants.Action.ADD_CONTACT_SUCCESS);
		filter.addAction(Constants.Action.DELETE_DEVICE_ALL);
		// 接收报警ID----------
		filter.addAction(Constants.P2P.RET_GET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.RET_SET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.ACK_RET_SET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.ACK_RET_GET_BIND_ALARM_ID);
		filter.addAction(Constants.Action.SEARCH_AP_DEVICE);
		filter.addAction(Constants.Action.ENTER_DEVICE_SETTING);
		filter.addAction(Constants.Action.CALL_DEVICE);
		filter.addAction(Constants.Action.NET_WORK_TYPE_CHANGE);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(Constants.P2P.ACK_GET_NVR_IPC_LIST);
		filter.addAction(Constants.P2P.RET_GET_NVR_IPC_LIST);
		filter.addAction(Constants.P2P.RET_SET_REMOTE_DEFENCE);
		filter.addAction(Constants.P2P.RET_SET_IPC_WORKMODE);
        filter.addAction(Constants.P2P.RET_GET_NVRINFO);
		filter.addAction(Constants.P2P.ACK_GET_NVRINFO);
		filter.addAction(Constants.P2P.RET_GET_CURRENT_WORKMODE);
		filter.addAction(Constants.Action.WIRE_ADD_SUCCESS);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	public void regDoorbellFilter() {
		IntentFilter filter = new IntentFilter();
		// 接收报警ID----------
		filter.addAction(Constants.P2P.RET_GET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.RET_SET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.ACK_RET_SET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.ACK_RET_GET_BIND_ALARM_ID);
		// 接收报警ID---------------
		mContext.registerReceiver(mDoorbellReceiver, filter);
		isDoorBellRegFilter = true;
	}

	BroadcastReceiver mDoorbellReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.P2P.RET_GET_BIND_ALARM_ID)) {
				String[] data = intent.getStringArrayExtra("data");
				String srcID = intent.getStringExtra("srcID");
				int max_count = intent.getIntExtra("max_count", 0);
				if (data.length >= max_count) {
					if (!SharedPreferencesManager.getInstance()
							.getIsDoorBellToast(mContext, srcID)) {
						T.show(mContext, R.string.alarm_push_limit, 2000);
						SharedPreferencesManager.getInstance()
								.putIsDoorBellToast(srcID, true, mContext);
					}
				} else {
					// 处理绑定推送ID
					mAdapter.setBindAlarmId(srcID, data);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_BIND_ALARM_ID)) {
				int result = intent.getIntExtra("result", -1);
				String srcID = intent.getStringExtra("srcID");
				if (result == Constants.P2P_SET.BIND_ALARM_ID_SET.SETTING_SUCCESS) {
					// 设置成功重新获取列表
					// mAdapter.getBindAlarmId(srcID);
					mAdapter.setBindAlarmIdSuccess(srcID);
				} else {

				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_BIND_ALARM_ID)) {
				int result = intent.getIntExtra("result", -1);
				String srcID = intent.getStringExtra("srcID");
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {

				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:set alarm bind id");
					// 设置时网络错误，重新设置
					mAdapter.setBindAlarmId(srcID);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_BIND_ALARM_ID)) {
				int result = intent.getIntExtra("result", -1);
				String srcID = intent.getStringExtra("srcID");
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {

				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					// 获得列表网络错误，重新获取
					mAdapter.getBindAlarmId(srcID);
				}
			}
		}
	};

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Action.REFRESH_CONTANTS)) {
				FList flist = FList.getInstance();
				flist.updateOnlineState();
				mAdapter.notifyDataSetChanged();
				upadataTextView();
				List<LocalDevice> localDevices = FList.getInstance()
						.getLocalDevicesNoAP();
				if(!isShowGuide){
					if (localDevices.size() > 0) {
						local_device_bar_top.setVisibility(RelativeLayout.VISIBLE);
						text_local_device_count.setText("" + localDevices.size());
					} else {
						local_device_bar_top.setVisibility(RelativeLayout.GONE);
					}
				}
			} else if (intent.getAction().equals(
					Constants.Action.GET_FRIENDS_STATE)) {
				mAdapter.notifyDataSetChanged();
				refreshEnd = true;
			} else if (intent.getAction().equals(
					Constants.Action.LOCAL_DEVICE_SEARCH_END)) {
				List<LocalDevice> localDevices = FList.getInstance()
						.getLocalDevicesNoAP();
			    if(!isShowGuide){
			    	if (localDevices.size() > 0) {
			    		local_device_bar_top.setVisibility(RelativeLayout.VISIBLE);
			    		text_local_device_count.setText("" + localDevices.size());
			    	} else {
			    		local_device_bar_top.setVisibility(RelativeLayout.GONE);
			    	}
			    }
				mAdapter.notifyDataSetChanged();
				upadataTextView();
			} else if (intent.getAction().equals(
					Constants.Action.ACTION_NETWORK_CHANGE)) {
				ConnectivityManager connectivityManager = (ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				if (activeNetInfo != null) {
					if (!activeNetInfo.isConnected()) {
						T.showShort(mContext, getString(R.string.network_error)
								+ " " + activeNetInfo.getTypeName());
						net_work_status_bar
								.setVisibility(RelativeLayout.VISIBLE);
					} else {
						net_work_status_bar.setVisibility(RelativeLayout.GONE);
					}
				} else {
					T.showShort(mContext, R.string.network_error);
					net_work_status_bar.setVisibility(RelativeLayout.VISIBLE);
				}

			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_CHECK_PASSWORD)) {
				if (!isActive) {
					return;
				}
				int result = intent.getIntExtra("result", -1);
				String deviceId=intent.getStringExtra("deviceId");
				if (!isCancelLoading) {
					if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
						if (null != dialog && dialog.isShowing()) {
							dialog.dismiss();
							dialog = null;
							isCancelLoading=true;
						}
						if(deviceId.equals(next_contact.contactId)||deviceId.equals(next_contact.getIpMark())){
							if(enterType==ENTER_SET){
								Intent control = new Intent();
								control.setClass(mContext, MainControlActivity.class);
								control.putExtra("contact", next_contact);
								control.putExtra("type", P2PValue.DeviceType.NPC);
								mContext.startActivity(control);
							}else if(enterType==ENTER_PLAYBACK){
								Intent playback = new Intent();
						    	playback.putExtra("contact", next_contact);
					    		playback.setClass(mContext, PlayBackListActivity.class);
					    		mContext.startActivity(playback);
							}
						}
					} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
						if (null != dialog && dialog.isShowing()) {
							dialog.dismiss();
							dialog = null;
						}
						if(inputPwdDialog!=null&&inputPwdDialog.isShowing()){
							return;
						}
						if(deviceId.equals(next_contact.contactId)||deviceId.equals(next_contact.getIpMark())){
							inputPwdDialog=new InputPasswordDialog(mContext);
							inputPwdDialog.setInputPasswordClickListener(inputPwdClickListener);
							inputPwdDialog.setContactId(next_contact.contactId);
							inputPwdDialog.show();
						}
					} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
						if(next_contact!=null){
							P2PHandler.getInstance().checkPassword(
									next_contact.contactId,
									next_contact.contactPassword);			
						}
					} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
						if (null != dialog && dialog.isShowing()) {
							dialog.dismiss();
							dialog = null;
						}
						T.showShort(mContext, R.string.insufficient_permissions);
					}
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_REMOTE_DEFENCE)) {
				int state = intent.getIntExtra("state", -1);
				String contactId = intent.getStringExtra("contactId");
				if(!contactId.equals("1")){
					Contact contact = FList.getInstance().isContact(contactId);
					if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext, R.string.net_error);
						}
					} else if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
						if (null != contact && contact.isClickGetDefenceState) {
							if(inputPwdDialog!=null&&inputPwdDialog.isShowing()){
								return;
							}
							inputPwdDialog=new InputPasswordDialog(mContext);
							inputPwdDialog.setInputPasswordClickListener(inputPwdClickListener);
							inputPwdDialog.setContactId(contact.contactId);
							inputPwdDialog.show();

						}
					}else if(state==Constants.DefenceState.DEFENCE_NO_PERMISSION){
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext,R.string.insufficient_permissions);
						}
					}else if(state == Constants.DefenceState.DEFENCE_STATE_ON){
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext,R.string.arm_success);
						}
					}else if(state == Constants.DefenceState.DEFENCE_STATE_OFF){
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext,R.string.disarm_success);
						}
					}
					if (null != contact && contact.isClickGetDefenceState) {
						FList.getInstance().setIsClickGetDefenceState(contactId,
								false);
					}
					mAdapter.notifyDataSetChanged();
				}
			} else if (intent.getAction().equals(
					Constants.Action.SETTING_WIFI_SUCCESS)) {
				FList flist = FList.getInstance();
				flist.updateOnlineState();
				flist.searchLocalDevice();
			} else if (intent.getAction().equals(Constants.Action.DIAPPEAR_ADD)) {
				if (isHideAdd == false) {
					hideAdd();
				}
			} else if (intent.getAction().equals(
					Constants.Action.ADD_CONTACT_SUCCESS)) {
				List<LocalDevice> localDevices = FList.getInstance()
						.getLocalDevicesNoAP();
				if(!isShowGuide){
					if (localDevices.size() > 0) {
						local_device_bar_top.setVisibility(RelativeLayout.VISIBLE);
						text_local_device_count.setText("" + localDevices.size());
					} else {
						local_device_bar_top.setVisibility(RelativeLayout.GONE);
					}
				}
				upadataTextView();
				mPullRefreshListView
						.setVisibility(PullToRefreshListView.VISIBLE);
			} else if (intent.getAction().equals(
					Constants.Action.DELETE_DEVICE_ALL)) {
				mPullRefreshListView.setVisibility(PullToRefreshListView.VISIBLE);
				upadataTextView();
			} else if (intent.getAction().equals(
					Constants.Action.ENTER_DEVICE_SETTING)) {
				Contact contact = (Contact) intent
						.getSerializableExtra("contact");
				next_contact = contact;
				dialog = new NormalDialog(mContext);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						// TODO Auto-generated method stub
						isCancelLoading = true;
					}

				});
				dialog.showLoadingDialog2();
				dialog.setCanceledOnTouchOutside(false);
				isCancelLoading = false;
				P2PHandler.getInstance().checkPassword(contact.contactId,
						contact.contactPassword);
				myHandler.postDelayed(runnable, 20000);
				enterType=ENTER_SET;
				count1++;
			} else if (intent.getAction().equals(Constants.Action.CALL_DEVICE)) {
				Contact contact = (Contact) intent
						.getSerializableExtra("contact");
				Intent call = new Intent();
				call.setClass(mContext, CallActivity.class);
				call.putExtra("callId", contact.contactId);
				call.putExtra("contactName", contact.contactName);
				call.putExtra("password", contact.contactPassword);
				call.putExtra("contact", contact);
				call.putExtra("isOutCall", true);
				call.putExtra("type", Constants.P2P_TYPE.P2P_TYPE_CALL);
				startActivity(call);
			} else if(intent.getAction().equals(Constants.Action.NET_WORK_TYPE_CHANGE)){
				String connect_name=WifiUtils.getInstance().getConnectWifiName();
				boolean isApDevice=WifiUtils.getInstance().isApDevice(connect_name);
				if(isApDevice){
					String deviceId=connect_name.substring(AppConfig.Relese.APTAG.length());
					Contact contact=FList.getInstance().isContact(deviceId);
					if(contact!=null){
						contact.apModeState=Constants.APmodeState.LINK;
						Intent it=new Intent();
						it.setAction(Constants.Action.REFRESH_CONTANTS);
						context.sendBroadcast(it);
					}
				}
				if(dialog_connect!=null&&dialog_connect.isShowing()){
					dialog_connect.dismiss();
				}else{
					return;
				}
				if(WifiUtils.getInstance().isConnectWifi(
						apContact.getAPName())){
					Intent apMonitor=new Intent(mContext, ApMonitorActivity.class);
					 try {
						 apContact.ipadressAddress=InetAddress.getByName(Utils.getAPDeviceIp(mContext));
					 } catch (UnknownHostException e) {
							// TODO Auto-generated catch block
						 e.printStackTrace();
					 }
					apMonitor.putExtra("contact", apContact);
					apMonitor.putExtra("connectType", Constants.ConnectType.RTSPCONNECT);
					startActivity(apMonitor);
				}else{
					reConnectApModeWifi();
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_GET_NVR_IPC_LIST)){
				int state=intent.getIntExtra("state", -1);
				if(state == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS){
					
				}else if(state == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					if(dialog_loading!=null&&dialog_loading.isShowing()){
						dialog_loading.dismiss();
					}
					T.showShort(mContext, R.string.pw_incrrect);
				}else if(state==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					if(dialog_loading!=null&&dialog_loading.isShowing()){
						dialog_loading.dismiss();
					}
					T.showShort(mContext, R.string.net_error);
				}
				
			}else if(intent.getAction().equals(Constants.P2P.RET_GET_NVR_IPC_LIST)){
				String contactId=intent.getStringExtra("contactId");
				String[] data=intent.getStringArrayExtra("data");
				int number=intent.getIntExtra("number", -1);
				String s="";
				if(dialog_loading!=null||dialog_loading.isShowing()){
					dialog_loading.dismiss();
				}
				for(String d:data){
					s=s+d+" ";
				}
				if(number>0){
					Intent monitor=new Intent(mContext, ApMonitorActivity.class);
					monitor.putExtra("contact", nvrContact);
					monitor.putExtra("ipcList", data);
					monitor.putExtra("number", number);
					monitor.putExtra("connectType", Constants.ConnectType.P2PCONNECT);
					mContext.startActivity(monitor);
				}else{
					T.showShort(mContext, R.string.no_ipc_list);
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_SET_REMOTE_DEFENCE)){
				String contactId=intent.getStringExtra("contactId");
				int state=intent.getIntExtra("state", -1);
				if(state==0){
					if(contactId.equals("1")){
						P2PHandler.getInstance().getDefenceStates(contactId, "0");
					}
				}
			}else if (intent.getAction().equals(Constants.P2P.RET_SET_IPC_WORKMODE)) {
				int iSrcID = intent.getIntExtra("iSrcID", 0);
		        int boption = intent.getByteExtra("boption", (byte) -1);
		        byte[] data = intent.getByteArrayExtra("data");
                if (boption == Constants.FishBoption.MESG_SET_OK) {
                    int position = FList.getInstance().updateDeviceModeState(String.valueOf(iSrcID), data[3]);
                }
            }  else if (intent.getAction().equals(Constants.P2P.RET_GET_NVRINFO)) {
				int id = intent.getIntExtra("iSrcID", 0);
				byte option = intent.getByteExtra("boption", (byte) -1);
				byte[] data = intent.getByteArrayExtra("data");
				if (dialog_loading != null || dialog_loading.isShowing()) {
					dialog_loading.dismiss();
				}
				JAContact ja = paserNVRInfoData(data, id);
				Intent toNvr = new Intent();
				if (ToNVRPlayBackOrMonitor == 1) {
					toNvr.setClass(mContext, NVRMonitorActivity.class);
				} else {
					toNvr.setClass(mContext, NVRPlayBackActivity.class);
				}
				toNvr.putExtra("contact", nvrContact);
				toNvr.putExtra("jacontact", ja);
				startActivity(toNvr);
			} else if (intent.getAction().equals(Constants.P2P.ACK_GET_NVRINFO)) {
				int state = intent.getIntExtra("state", -1);
				String deviceId=intent.getStringExtra("deviceId");
				if (state == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {

				} else if (state == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					if (dialog_loading != null && dialog_loading.isShowing()) {
						dialog_loading.dismiss();
					}
//					T.showShort(mContext, R.string.pw_incrrect);
					if(inputPwdDialog!=null&&inputPwdDialog.isShowing()){
						return;
					}
					inputPwdDialog=new InputPasswordDialog(mContext);
					inputPwdDialog.setInputPasswordClickListener(inputPwdClickListener);
					inputPwdDialog.setContactId(deviceId);
					inputPwdDialog.show();
				} else if (state == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					if (dialog_loading != null && dialog_loading.isShowing()) {
						dialog_loading.dismiss();
					}
					T.showShort(mContext, R.string.net_error);
				}
			}else if (intent.getAction().equals(Constants.P2P.RET_GET_CURRENT_WORKMODE)) {
				  int iSrcID = intent.getIntExtra("iSrcID", 0);
		          int boption = intent.getByteExtra("boption", (byte) -1);
		          byte[] data = intent.getByteArrayExtra("data");
                if (boption == Constants.FishBoption.MESG_GET_OK) {
                    int position = FList.getInstance().updateDeviceModeState(String.valueOf(iSrcID), data[3]);
                }
            }else if(intent.getAction().equals(Constants.Action.WIRE_ADD_SUCCESS)){
				if(confirmDialog!=null&&confirmDialog.isShowing()){
					return;
				}
				confirmDialog=new ConfirmDialog(context);
				confirmDialog.setTitle(getResources().getString(R.string.add_success_set_wifi));
				confirmDialog.setGravity(Gravity.TOP);
				confirmDialog.setTxButton(getResources().getString(R.string.i_get_it));
				confirmDialog.show();
			}
		}
	};

	// 解析NVR数据
	private JAContact paserNVRInfoData(byte[] data, int deviceid) {
		int gwID = Utils.bytesToInt(data, 6);
		int channl = Utils.bytesToInt(data, 10);
		byte[] ja = new byte[24];
		System.arraycopy(data, 14, ja, 0, ja.length);
		String jaID = new String(ja);

		byte[] user = new byte[24];
		System.arraycopy(data, 38, user, 0, user.length);
		String jaUser = new String(user);

		byte[] pwd = new byte[32];
		System.arraycopy(data, 62, pwd, 0, pwd.length);
		String jaPwd = new String(pwd);

		JAContact jacon = new JAContact();
		jacon.setActiveUser(NpcCommon.mThreeNum);
		jacon.setChannl(channl);
		jacon.setGwid(String.valueOf(deviceid));
		jacon.setJaid(jaID.trim());
		jacon.setPwd(jaPwd.trim());
		jacon.setUser(jaUser.trim());
		// 头像还是要存存数据库
		JAContact jacontact = DataManager
				.findJAContactByActiveUserAndContactId(MyApplication.app,
						NpcCommon.mThreeNum, String.valueOf(deviceid));
		if (jacontact != null) {
			// 更新
			DataManager.updateJAContact(MyApplication.app, jacon);
		} else {
			// 插入
			DataManager.insertJAContact(MyApplication.app, jacon);
		}
		return jacon;
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case CHANGE_REFRESHING_LABLE:
				String lable = (String) msg.obj;
				// mPullRefreshListView.setHeadLable(lable);
				break;
			}
			return false;
		}
	});

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			Log.e("my", "doInBackground");
			FList flist = FList.getInstance();
			flist.searchLocalDevice();
			if (flist.size() == 0) {
				return null;
			}
			refreshEnd = false;
			flist.updateOnlineState();
			flist.getDefenceState();
			//flist.getCheckUpdate();
			FList.getInstance().getModeState();
			while (!refreshEnd) {
				Utils.sleepThread(1000);
			}

//			Message msg = new Message();
//			msg.what = CHANGE_REFRESHING_LABLE;
//			msg.obj = mContext.getResources().getString(
//					R.string.pull_to_refresh_refreshing_success_label);
//			mHandler.sendMessage(msg);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// mListItems.addFirst("Added after refresh...");
			// Call onRefreshComplete when the list has been refreshed.
//			mPullRefreshListView.onRefreshComplete();
			mPtrFrame.refreshComplete();
			super.onPostExecute(result);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_add:
//			if (isHideAdd == true) {
//				showAdd();
//			} else {
//				hideAdd();
//			}
//			mPullRefreshListView.setFocusable(false);
//			mListView.setFocusable(false);
			NormalDialog dialog = new NormalDialog(mContext);
            dialog.setOnDialogItemClick(onChooseAddDialogItemClick);
            dialog.setmContact(null);
            dialog.showChooseAddDialog(1);
			break;
		case R.id.radar_add:
			layout_add.setVisibility(LinearLayout.GONE);
			local_device_bar_top.setClickable(true);
			isHideAdd = true;
			Intent radar_add = new Intent(mContext, RadarAddFirstActivity.class);
			mContext.startActivity(radar_add);
			break;
		case R.id.manually_add:
			layout_add.setVisibility(LinearLayout.GONE);
			local_device_bar_top.setClickable(true);
			isHideAdd = true;
			Intent add_contact = new Intent(mContext, AddContactActivity.class);
			mContext.startActivity(add_contact);
			break;
		case R.id.scan_it:
			hideAdd();
			Intent scan_qr_code = new Intent(mContext, CaptureActivity.class);
			scan_qr_code.putExtra("type", 1);
			startActivityForResult(scan_qr_code, QRcodeRequestCode);
			break;
		default:
			break;
		}
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			count2 = count2 + 1;
			if (count2 == count1) {
				if (dialog != null) {
					if (dialog.isShowing()) {
						dialog.dismiss();
						T.showShort(mContext, R.string.time_out);
					}
				}
			}
		}
	};

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		MainThread.setOpenThread(false);
		super.onPause();
		isActive = false;
		if (isDoorBellRegFilter) {
			mContext.unregisterReceiver(mDoorbellReceiver);
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MainThread.setOpenThread(true);
		regDoorbellFilter();
		isActive = true;
		showTips();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// MainThread.getInstance().kill();
		super.onDestroy();
		Log.e("my", "onDestroy");
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	public void hideAdd() {
		layout_add.startAnimation(animation_in);
		layout_add.setVisibility(LinearLayout.GONE);
		local_device_bar_top.setClickable(true);
		isHideAdd = true;
	}

	public void showAdd() {
		layout_add.setVisibility(LinearLayout.VISIBLE);
		layout_add.startAnimation(animation_out);
		local_device_bar_top.setClickable(false);
		isHideAdd = false;
	}
	private void connectWifi(String wifiName,String wifiPwd) {
		if (wifiPwd.length() < 8) {
			// 密码必须8位
			T.showShort(mContext, R.string.wifi_pwd_error);
			return;
		}
		WifiUtils.getInstance().connectWifi(wifiName,wifiPwd,
				1);
		if (dialog_connect == null) {
			dialog_connect = new NormalDialog(mContext);
		}
		dialog_connect.setTitle(R.string.wait_connect);
		dialog_connect.showLoadingDialog();
		dialog_connect.setTimeOut(30 * 1000);
		dialog_connect.setOnNormalDialogTimeOutListner(new OnNormalDialogTimeOutListner() {

			@Override
			public void onTimeOut() {
				T.showShort(mContext, R.string.time_out);
				reConnectApModeWifi();
			}
		});

	}
	public void reConnectApModeWifi(){
		try {
			apContact.ipadressAddress=InetAddress.getByName(Utils.getAPDeviceIp(mContext));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent modify=new Intent();
		modify.setClass(mContext, AddApDeviceActivity.class);
		modify.putExtra("isAPModeConnect", 0);
	    modify.putExtra("contact", apContact);
	    modify.putExtra("ipFlag","1");
	    modify.putExtra("isCreatePassword", false);
	    startActivity(modify);
		T.showShort(mContext, R.string.conn_fail);
	}
//	//等待弹框消失监听
//    private NormalDialog.OnCustomCancelListner listener = new NormalDialog.OnCustomCancelListner() {
//        @Override
//        public void onCancle(int mark) {
//            Log.e("dxsTest", "对话框取消");
//        }
//
//    };
    //模式选择对话框
    private NormalDialog.onDialogItemClick onDialogItemClick = new NormalDialog.onDialogItemClick() {

        @Override
        public void onItemClick(View view, Contact mContact, int position, int contactPosition) {
            mContact.FishMode = -1;
//            mAdapter.notifyItemChanged(contactPosition);
            mAdapter.notifyDataSetChanged();
            FisheyeSetHandler.getInstance().SetFishWorkMode(mContact.contactId, mContact.contactPassword, position);
        }
    };
    
    private void showTips() {
    	if(SharedPreferencesManager.getInstance().getShowGuide(mContext, SharedPreferencesManager.SHOW_ADD_GUIDE))return;
    	if(FList.getInstance().list().size()>0){
    		return;
    	}
    	ShowTipsView showtips = new ShowTipsBuilder((Activity) mContext)
                .setTarget(mAddUser)
                .setTitle("\""+getResources().getString(R.string.add_contacts)+"\""+",")
                .setDescription(getResources().getString(R.string.add_contact2))
                .setTitleColor(Color.parseColor("#01b6ba"))
                .setBuildType(ShowTipsView.SHOWTYPE_STOKEN)
                .setTipsType(ShowTipsView.TIPS_ADDCONTACT)
                .setBackgroundAlpha((int)(0.5*255))
                .setDescriptionColor(Color.parseColor("#01b6ba"))
                .setDelay(500)
                .build();
        showtips.show(getActivity());
        SharedPreferencesManager.getInstance().putShowGuide(mContext, SharedPreferencesManager.SHOW_ADD_GUIDE, true);
    }
    
    private void showTips(View view) {
    	if(isrefreshCommit){
    		showtips = new ShowTipsBuilder((Activity) mContext)
    		.setTarget(view)
    		.setTitle(getResources().getString(R.string.weak_password_guide))
//                .setDescription("快快来点这里!")
    		.setTitleColor(Color.parseColor("#01b6ba"))
    		.setBuildType(ShowTipsView.SHOWTYPE_NONE)
    		.setTipsType(ShowTipsView.TIPS_WEAKPASSWORD)
    		.setBackgroundAlpha((int)(0.5*255))
    		.setDescriptionColor(Color.parseColor("#01b6ba"))
    		.setDelay(500)
    		.build();
    		showtips.isrefreshCommit=isrefreshCommit;
    		showtips.setshowGuideListner(guidelistner);
    		showtips.show(getActivity());
    		SharedPreferencesManager.getInstance().putShowGuide(mContext,SharedPreferencesManager.SHOW_WEAKPWD_GUIDE,true);
    	}
    }
    showGuideListner guidelistner=new showGuideListner() {
		
		@Override
		public void onShowGuide(ShowTipsView view, boolean isShow) {
			// TODO Auto-generated method stub
			isShowGuide=isShow;
		}
	};
	
	//添加设备方式选择框
    private NormalDialog.onDialogItemClick onChooseAddDialogItemClick = new NormalDialog.onDialogItemClick() {

        @Override
        public void onItemClick(View view, Contact mContact, int position, int contactPosition) {
        	if (position==1) {
        		Intent radar_add = new Intent(mContext, NetworkModeActivity.class);
    			mContext.startActivity(radar_add);
			}else if (position==2) {
				Intent add_contact = new Intent(mContext, AddContactActivity.class);
				mContext.startActivity(add_contact);
			}else {
				Intent scan_qr_code = new Intent(mContext, CaptureActivity.class);
				scan_qr_code.putExtra("type", 1);
				startActivityForResult(scan_qr_code, QRcodeRequestCode);
			}
        }
    };
	//密码错误输入框监听
	InputPasswordDialog.InputPasswordClickListener  inputPwdClickListener=new InputPasswordDialog.InputPasswordClickListener() {
		@Override
		public void onCancelClick() {
               if(inputPwdDialog!=null){
				   inputPwdDialog.dismiss();
				   inputPwdDialog=null;
			   }
		}

		@Override
		public void onOkClick(String contactId, String pwd) {
			if(pwd==null||pwd.equals("")){
				T.showShort(mContext,R.string.input_password);
				return;
			}
			if(pwd.length() > 30|| pwd.charAt(0) == '0'){
				T.showShort(mContext, R.string.device_password_invalid);
				return;
			}
			String password=P2PHandler.getInstance().EntryPassword(pwd);
			Contact c=FList.getInstance().isContact(contactId);
			if(c!=null){
				c.userPassword=pwd;
				c.contactPassword=password;
				c.defenceState= Constants.DefenceState.DEFENCE_STATE_LOADING;
				FList.getInstance().update(c);
//				T.showShort(mContext, R.string.modify_success);
				mAdapter.notifyDataSetChanged();
			}
			if(inputPwdDialog!=null){
				inputPwdDialog.dismiss();
				inputPwdDialog=null;
			}

		}
	};
	private InputPasswordDialog.InputPasswordClickListener inputNameClickListener =
			new InputPasswordDialog.InputPasswordClickListener() {
				@Override
				public void onCancelClick() {
					if(modifyNameDialog!=null){
						modifyNameDialog.dismiss();
					}
				}

				@Override
				public void onOkClick(String isContact, String name) {
					if (name == null || name.equals("")) {
						T.showShort(mContext, R.string.input_device_name);
						return;
					}
					if (name.length() > 64) {
						return;
					}
					Contact contact=FList.getInstance().isContact(isContact);
					if(contact==null){
						return;
					}
					contact.contactName = name;
					try {
						FList.getInstance().update(contact);
						mAdapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(modifyNameDialog!=null){
							modifyNameDialog.dismiss();
						}
					}
				}
			};
    
}