package com.jwkj.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.global.AppConfig;
import com.jwkj.global.Constants;
import com.jwkj.global.MyApp;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.jwkj.widget.NormalDialog.OnCustomCancelListner;
import com.jwkj.widget.RememberPontPopWindow;
import com.jwkj.widget.RememberPontPopWindow.ondismissListener;
import com.jwkj.widget.prepointPopwindow;
import com.p2p.core.BaseMonitorActivity;
import com.p2p.core.P2PHandler;
import com.nuowei.ipclibrary.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MonitorThreeFrag extends BaseFragment implements
		View.OnClickListener {
	public static final int PREPOINTCOUNTS = 5;
	private ImageView ivPrePoint;
	public RememberPontPopWindow prepointPop;
	private Context mContext;
	private View view;
	private int popH;
	private String contactID, contactPwd;
	boolean isRegFilter = false;
	private int isSend;
	private int sendCount;
	private int point;
	private String imgPath;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_monitorthree, container,
				false);
		mContext = getActivity();
		if (savedInstanceState != null) {
			contactID = savedInstanceState.getString("contactid");
			contactPwd = savedInstanceState.getString("contactpwd");
		} else {
			contactID = getArguments().getString("contactid");
			contactPwd = getArguments().getString("contactpwd");
		}
		initUI(view);
		return view;
	}

	private void initUI(View view) {
		ivPrePoint = (ImageView) view.findViewById(R.id.iv_preposition);
		ivPrePoint.setOnClickListener(this);
		popH = (int) (getAreaThree() * 0.57);// 弃用
	}

	@Override
	public void onResume() {
		super.onResume();
		// Utils.setPrePoints(contactID, contactPwd, 2, 0);
		if (!isRegFilter) {
			regFilter();
		}
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.iv_preposition) {// 和IOS统一提示，固件需要升级，提示不支持；还未获取到提示不能查看
			// 展示添加预置位
			if (!ApMonitorActivity.userCanClick) {
				T.showLong(mContext, R.string.prepoint_cannottosee);
				return;
			}
			if (ApMonitorActivity.PreFunctionMode == -2) {
				T.showLong(mContext, R.string.prepoint_cannottosee);
				return;
			}
			if (ApMonitorActivity.PreFunctionMode == -1
					|| ApMonitorActivity.PreFunctionMode == 84) {
				// 不支持
				T.showLong(mContext, R.string.not_support);
				Log.e("dxsprepoint", "不支持");
				return;
			}
			// if (ApMonitorActivity.PreFunctionMode == 84) {
			// //固件需要升级
			// T.showLong(mContext,R.string.prepoint_upgreat);
			// Log.e("dxsprepoint", "固件需要升级");
			// return;
			// }
			// if (ApMonitorActivity.PreFunctionMode == -2) {
			// //不支持
			// T.showLong(mContext, R.string.has_not_pripoint);
			// Log.e("dxsprepoint", "还未获取到");
			// return;
			// }
			// if (prepointPop == null) {
			popH = view.getMeasuredHeight();
			prepointPop = new RememberPontPopWindow(mContext, popH,
					ApMonitorActivity.mContact, ApMonitorActivity.PrePointInfo);
			prepointPop.setOnPopwindowListner(listner);
			// }
			prepointPop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
			prepointPop.setdismissListener(listener2);

		}
	}

	private prepointPopwindow.OnPopwindowListner listner = new prepointPopwindow.OnPopwindowListner() {
		@Override
		public void onDeletePrepoint(List<String> deletePath, int deletePoint) {
			if (deletePath.size() <= 0) {
				T.showLong(mContext, R.string.selecte_least_one);
				return;
			}
			deletedPrepoint(deletePath.size(), deletePoint);
			Log.e("dxsTest",
					"delete-"
							+ Arrays.toString(Utils.getByteBinnery(
									(byte) deletePoint, false)));
		}

		@Override
		public void addPrepoint(int position) {
			Utils.setPrePoints(ApMonitorActivity.mContact.getIpContactId(),
					ApMonitorActivity.mContact.contactPassword, 1, position);
			showLoadingDialog(0, new NormalDialog.OnNormalDialogTimeOutListner() {
				@Override
				public void onTimeOut() {
					T.showShort(MyApp.app, R.string.time_out);
				}
			}, new OnCustomCancelListner() {
				@Override
				public void onCancle(int mark) {

				}
			},10000);
		}
	};

	public void deletedPrepoint(int counts, final int PrePoints) {
		NormalDialog dialog = new NormalDialog(mContext, mContext
				.getResources().getString(R.string.delete), String.format(
				Utils.getStringForId(R.string.delete_this), counts), mContext
				.getResources().getString(R.string.delete), mContext
				.getResources().getString(R.string.cancel));
		dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

			@Override
			public void onClick() {
				showLoadingDialog(listener, 1);
				setLoadingTimeOut(10*1000);
				Utils.setPrePoints(ApMonitorActivity.mContact.getIpContactId(),
						ApMonitorActivity.mContact.contactPassword, 3,
						PrePoints);
			}
		});
		dialog.showDialog();
	}

	private OnCustomCancelListner listener = new OnCustomCancelListner() {

		@Override
		public void onCancle(int mark) {

		}
	};
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	private int getAreaThree() {
		Rect frame = new Rect();
		getActivity().getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int titleHight = (int) mContext.getResources().getDimension(
				R.dimen.title_height);
		return MyApp.SCREENHIGHT - titleHight - Utils.dip2px(mContext, 50);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (prepointPop != null && prepointPop.isShowing()) {
				prepointPop.dismiss();
			}
		} else {
			// Empty
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}
	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.RET_DELETE_PRESETMOTOROS);
		filter.addAction(Constants.P2P.RET_SET_PRESETMOTOROS);
		filter.addAction(Constants.P2P.RET_GET_IS_PRESETMOTOROS);
		filter.addAction(Constants.P2P.ACK_RET_PRESET_POS);
		filter.addAction(Constants.P2P.RET_GET_ALLARMIMAGE);
		// --------call转移------------------
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {

			if (intent.getAction().equals(
					Constants.P2P.RET_DELETE_PRESETMOTOROS)) {
				byte[] result = intent.getByteArrayExtra("result");
				hindLoadingDialog();
				if (prepointPop != null) {
					prepointPop.DeletePrepoint(result[3]);
				}
				Log.e("dxsTest",
						"delete-real-"
								+ Arrays.toString(Utils.getByteBinnery(
										result[3], false)));
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_IS_PRESETMOTOROS)) {
				int isNew = intent.getIntExtra("isNew", 0);
				if (isNew == 0) {
					byte[] result = intent.getByteArrayExtra("result");
					Log.e("dxsprepoint", "GET_IS-->" + Arrays.toString(result));
					for (int i = 0; i < PREPOINTCOUNTS; i++) {
						if (result[3] == i) {
							// 更新i图片
							if (mContext instanceof BaseMonitorActivity) {
								((ApMonitorActivity) mContext)
										.ScreenShot(result[3]);
							}
						}
					}
				} else {
					byte[] data = intent.getByteArrayExtra("data");
					if (data[2] == 1) {
						point = data[3];
						byte[] pathData = new byte[64];
						System.arraycopy(data, 4, pathData, 0, pathData.length);
						imgPath = Utils.arryToString(pathData);
						Log.e("wxy", "imgPath:" + imgPath);
						File file = new File(AppConfig.Relese.PREPOINTPATH);
						if (!file.exists()) {
							file.mkdirs();
						}
						P2PHandler.getInstance().GetAllarmImage(contactID,
								contactPwd, imgPath,
								Utils.getPrepointPath(contactID, point));
						isSend = 1;
					}
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_PRESETMOTOROS)) {
				byte[] result = intent.getByteArrayExtra("result");
				if (result[1] == 0) {
					// 执行截图
					if (mContext instanceof BaseMonitorActivity) {
						((ApMonitorActivity) mContext).ScreenShot(result[3]);
					}
				}
				Log.e("dxsprepoint", "SET-->" + Arrays.toString(result));
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_PRESET_POS)) {
				int state = intent.getIntExtra("state", -1);
				if (state == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Utils.setPrePoints(
							ApMonitorActivity.mContact.getIpContactId(),
							ApMonitorActivity.mContact.contactPassword, 2, 0);
					T.showShort(mContext, R.string.net_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_ALLARMIMAGE)) {
				if (isSend != 0) {
					String filename = intent.getStringExtra("filename");
					int errorCode = intent.getIntExtra("errorCode", -1);
					if (errorCode != 0) {
						if (sendCount != 2) {
							P2PHandler.getInstance().GetAllarmImage(contactID,
									contactPwd, imgPath, filename);
							isSend = 2;
							sendCount++;
						} else {
							sendCount = 0;
						}
					} else {
						addPrepoint(Utils.getPointFromPointPath(filename), 0);
						isSend = 0;
					}
				}
			}

		}
	};

	public void addPrepoint(int prepoint, int result) {
		if (result == 1 || result == 0 || result == -1) {// 需要再编码-1取消 0失败 1成功
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (null != prepointPop) {
				prepointPop.addPrepoint(prepoint);
			}
		}

	}

	ondismissListener listener2 = new ondismissListener() {

		@Override
		public void onshow() {
			// TODO Auto-generated method stub
			ivPrePoint.setVisibility(View.GONE);
		}

		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			ivPrePoint.setVisibility(View.VISIBLE);
			if(prepointPop!=null){
				prepointPop.DismissInputPrepointName();
			}
		}
	};

}
