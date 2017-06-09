package com.jwkj.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.activity.QRcodeActivity;
import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.adapter.DialogSelectStringAdapter;
import com.jwkj.adapter.DialogSelectStringAdapter.OnItemClick;
import com.jwkj.adapter.SelectorDialogAdapter;
import com.jwkj.data.Contact;
import com.jwkj.entity.DefenceWorkGroup;
import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.fragment.DefenceControlFrag;
import com.jwkj.global.Constants;
import com.jwkj.interfac.dialogShowItem;
import com.jwkj.recycleview.ItemDecor.RecycleViewLinearLayoutManager;
import com.jwkj.selectdialog.SelectDialogAdapter;
import com.jwkj.selectdialog.SelectItem;
import com.jwkj.selectdialog.WorkeDaySelectAdapter;
import com.jwkj.utils.TextViewUtils;
import com.jwkj.utils.Utils;
import com.jwkj.wheel.widget.OnWheelChangedListener;
import com.jwkj.wheel.widget.WheelView;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NormalDialog {
	Context context;
	String[] list_data = new String[] {};
	String title_str, content_str, btn1_str, btn2_str, btn3_str;
	AlertDialog dialog;
	private int loadingMark;
	private OnButtonOkListener onButtonOkListener;
	private OnButtonCancelListener onButtonCancelListener;
	private OnCancelListener onCancelListener;
	private OnCustomCancelListner customCancelListner;
	private Contact mContact;
	private OnDismissListener onDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (mTimer != null) {
				mTimer.cancel();
			}
		}
	};
	private OnItemClickListener onItemClickListener;
	private int style = 999;

	public static final int DIALOG_STYLE_NORMAL = 1;
	public static final int DIALOG_STYLE_LOADING = 2;
	public static final int DIALOG_STYLE_UPDATE = 3;
	public static final int DIALOG_STYLE_DOWNLOAD = 4;
	public static final int DIALOG_STYLE_PROMPT = 5;

	public NormalDialog(Context context, String title, String content,
			String btn1, String btn2) {
		this.context = context;
		this.title_str = title;
		this.content_str = content;
		this.btn1_str = btn1;
		this.btn2_str = btn2;
	}

	public NormalDialog(Context context) {
		this.context = context;
		this.title_str = "";
		this.content_str = "";
		this.btn1_str = "";
		this.btn2_str = "";
	}

	public void showDialog() {
		switch (style) {
		case DIALOG_STYLE_NORMAL:
			showNormalDialog();
			break;
		case DIALOG_STYLE_PROMPT:
			showPromptDialog();
			break;
		case DIALOG_STYLE_LOADING:
			showLoadingDialog();
			break;
		default:
			showNormalDialog();
			break;
		}
	}

	public void showLoadingDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_loading, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		/**
		 *  todo
		 */
		title.setVisibility(View.GONE);
		title.setText(title_str);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.Loading_dialog_width);
        layout.height = (int) context.getResources().getDimension(
                R.dimen.loading_dialog_height);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setOnDismissListener(onDismissListener);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showAboutDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_about,
				null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		TextView txVersion = (TextView) view.findViewById(R.id.tv_about);
		txVersion.setText(MyUtils.getVersion());
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.about_dialog_width);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showDeviceInfoDialog(String curversion, String uBootVersion,
			String kernelVersion, String rootfsVersion) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_device_info, null);
		TextView text_curversion = (TextView) view
				.findViewById(R.id.text_curversion);
		TextView text_uBootVersion = (TextView) view
				.findViewById(R.id.text_uBootVersion);
		TextView text_kernelVersion = (TextView) view
				.findViewById(R.id.text_kernelVersion);
		TextView text_rootfsVersion = (TextView) view
				.findViewById(R.id.text_rootfsVersion);
		text_curversion.setText(curversion);
		text_uBootVersion.setText(uBootVersion);
		text_kernelVersion.setText(kernelVersion);
		text_rootfsVersion.setText(rootfsVersion);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.device_info_dialog_width);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showLoadingDialog2() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_loading2, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.Loading_dialog2_width);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showNormalDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_normal, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		TextView content = (TextView) view.findViewById(R.id.content_text);
		TextView button1 = (TextView) view.findViewById(R.id.button1_text);
		TextView button2 = (TextView) view.findViewById(R.id.button2_text);
		title.setText(title_str);
		content.setText(content_str);
		button1.setText(btn1_str);
		button2.setText(btn2_str);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != dialog) {
					dialog.dismiss();
				}
				onButtonOkListener.onClick();
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == onButtonCancelListener) {
					if (null != dialog) {
						dialog.cancel();
					}
				} else {
					onButtonCancelListener.onClick();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);

		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showSelectorDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_selector, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		title.setText(title_str);

		ListView content = (ListView) view.findViewById(R.id.content_text);

		SelectorDialogAdapter adapter = new SelectorDialogAdapter(context,
				list_data);
		content.setAdapter(adapter);
		content.setOnItemClickListener(onItemClickListener);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);

		int itemHeight = (int) context.getResources().getDimension(
				R.dimen.selector_dialog_item_height);
		int margin = (int) context.getResources().getDimension(
				R.dimen.selector_dialog_margin);
		int separatorHeight = (int) context.getResources().getDimension(
				R.dimen.selector_dialog_separator_height);

		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.selector_dialog_width);
		layout.height = itemHeight * list_data.length + margin * 2
				+ (list_data.length - 1) * separatorHeight;
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public String getContentStr() {
		return content_str;
	}

	public void setContentStr(int id) {
		String str = Utils.getStringForId(id);
		setContentStr(str);
	}

	public void setContentStr(String contantStr) {
		content_str = contantStr;
	}

	public String getbtnStr1() {
		return btn1_str;
	}

	public void setbtnStr1(int id) {
		String str = Utils.getStringForId(id);
		setbtnStr1(str);
	}

	public void setbtnStr1(String btn1_st) {
		btn1_str = btn1_st;
	}

	public String getbtnStr2() {
		return btn2_str;
	}

	public void setbtnStr2(int id) {
		String str = Utils.getStringForId(id);
		setbtnStr2(str);
	}

	public void setbtnStr2(String btn2_st) {
		btn2_str = btn2_st;
	}

	public String getbtnStr3() {
		return btn3_str;
	}

	public void setbtnStr3(int id) {
		String str = Utils.getStringForId(id);
		setbtnStr3(str);
	}

	public void setbtnStr3(String btn3_st) {
		btn3_str = btn3_st;
	}

	public void setmContact(Contact mContact) {
		this.mContact = mContact;
	}

	public void showPromptDialog() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_prompt, null);
		TextView content = (TextView) view.findViewById(R.id.content_text);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		TextView button2 = (TextView) view.findViewById(R.id.button2_text);
		content.setText(content_str);
		title.setText(title_str);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == onButtonCancelListener) {
					if (null != dialog) {
						dialog.dismiss();
					}
				} else {
					onButtonCancelListener.onClick();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);

		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showPromoptDiaglog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_promopt_box2, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		Button bt1 = (Button) view.findViewById(R.id.bt_determine);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != dialog) {
					dialog.dismiss();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_promopt_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_promopt_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showWaitConnectionDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_wait_connection, null);
		// view.setOnComfirmClickListener(new OnClickListener(){
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// if (null!=dialog) {
		// dialog.dismiss();
		// }
		// }
		//
		// });
		ImageView anim_load = (ImageView) view.findViewById(R.id.anim_wait);
		AnimationDrawable animationdrawable = (AnimationDrawable) anim_load
				.getDrawable();
		animationdrawable.start();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_remind_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_reming_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showQRcodehelp() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_help,
				null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_remind_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_reming_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void successDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_success, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_success_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_success_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void faildDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_prompt_box1, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		Button bt1 = (Button) view.findViewById(R.id.bt_determine);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (dialog != null) {
					dialog.dismiss();
				}

			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_promopt_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_promopt_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showConnectFail() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_connect_failed, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// if (null!=dialog) {
				// dialog.dismiss();
				// }
			}

		});
		Button try_again = (Button) view.findViewById(R.id.try_again);
		Button use_qrecode = (Button) view.findViewById(R.id.try_qrecode);
		try_again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}
		});
		use_qrecode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转到二维码添加
				if (null != dialog) {
					dialog.dismiss();
					onButtonCancelListener.onClick();
				}
				context.startActivity(new Intent(context, QRcodeActivity.class));
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_remind_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_reming_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);

	}

	/**
	 * 报警弹窗
	 * 
	 * @param surrportDelete
	 *            是否支持解绑
	 * @param id
	 *            报警ID
	 */
	public void showAlarmDialog(final boolean surrportDelete, final String id,AlarmCloseVoice alarmCloseVoice) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.dialog_alarm,
				null);
		TextView content = (TextView) view.findViewById(R.id.content_text);
		TextView button1 = (TextView) view.findViewById(R.id.button1_text);
		TextView button2 = (TextView) view.findViewById(R.id.button2_text);
		TextView btnDelete = (TextView) view.findViewById(R.id.button3_text);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_line_shu);
		content.setText(content_str);
		button1.setText(btn1_str);
		button2.setText(btn2_str);
		view.addView(alarmCloseVoice);
		if (surrportDelete) {
			btnDelete.setVisibility(View.VISIBLE);
			iv.setVisibility(View.VISIBLE);
			btnDelete.setText(btn3_str);
		} else {
			btnDelete.setVisibility(View.GONE);
			iv.setVisibility(View.GONE);
		}
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AlarmClickListner != null) {
					AlarmClickListner.onOkClick(id, surrportDelete, dialog);
				} else {
					if (null != dialog) {
						dialog.cancel();
					}
				}
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AlarmClickListner != null) {
					AlarmClickListner.onCancelClick(id, surrportDelete, dialog);
				} else {
					if (null != dialog) {
						dialog.cancel();
					}
				}
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AlarmClickListner != null) {
					AlarmClickListner.onDeleteClick(id, surrportDelete, dialog);
				} else {
					if (null != dialog) {
						dialog.cancel();
					}
				}

			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
		
	}
	/**
	 * 带选择列表的弹框
	 */
	public static final int SelectType_Sigle = 1;
	public static final int SelectType_List = 2;

	public void showSelectListDialog(final WorkScheduleGroup grop, int type,
			int h) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_select_list, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		TextView txTitle = (TextView) view.findViewById(R.id.select_list_title);
		RecyclerView listView = (RecyclerView) view
				.findViewById(R.id.select_list);
		Button btn = (Button) view.findViewById(R.id.btn_select);
		listView.setLayoutManager(new LinearLayoutManager(context));
		// listView.addItemDecoration(new DividerItemDecoration(context,
		// DividerItemDecoration.VERTICAL_LIST));
		SelectDialogAdapter mAdapter = new SelectDialogAdapter(context, grop,
				type);
		listView.setAdapter(mAdapter);
		mAdapter.setOnModeSetting(new SelectDialogAdapter.onModeSetting() {
			@Override
			public void onItemClick(View v, SelectItem item, int position) {
				// TODO Auto-generated method stub
				moreSelect.onItemClick(v, item, position);
			}
		});
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		txTitle.setText(title_str);
		btn.setText(btn1_str);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moreSelect.onOkClick(dialog, grop);
			}
		});
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showSelectListDialog(final WorkScheduleGroup grop, int type) {
		showSelectListDialog(grop, type, 0);// 高度暂时没有起作用
	}

	/**
	 * 单选选框(模式专用)
	 */
	public void showSingleSelectListDialog(int position) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_sigle_select, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		TextView txTitle = (TextView) view.findViewById(R.id.select_list_title);
		LinearLayout llModeHome = (LinearLayout) view
				.findViewById(R.id.ll_mode_home);
		LinearLayout llModeOut = (LinearLayout) view
				.findViewById(R.id.ll_mode_out);
		LinearLayout llModeSleep = (LinearLayout) view
				.findViewById(R.id.ll_mode_sleep);
		ImageView ivHome = (ImageView) view.findViewById(R.id.iv_mode_home);
		ImageView ivOut = (ImageView) view.findViewById(R.id.iv_mode_out);
		ImageView ivSleep = (ImageView) view.findViewById(R.id.iv_mode_sleep);
		final List<ImageView> ivs = new ArrayList<ImageView>();
		ivs.add(ivHome);
		ivs.add(ivOut);
		ivs.add(ivSleep);
		changeImage(ivs, position - 1);
		llModeHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeImage(ivs, 0);
				select.onItemClick(dialog, 1);
			}
		});
		llModeOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeImage(ivs, 1);
				select.onItemClick(dialog, 2);
			}
		});
		llModeSleep.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeImage(ivs, 2);
				select.onItemClick(dialog, 3);
			}
		});
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		txTitle.setText(title_str);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	/**
	 * 修改时间和模式弹窗
	 */
	private int modeTemp;

	public void showModifyTimeAndModeDialog(final WorkScheduleGroup grop) {
		modeTemp = grop.getbWorkMode();
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_modifytandm, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final TextView txHome = (TextView) view.findViewById(R.id.tx_mode_home);
		final TextView txOut = (TextView) view.findViewById(R.id.tx_mode_out);
		final TextView txSleep = (TextView) view
				.findViewById(R.id.tx_mode_sleep);
		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		final WheelView viewHour = (WheelView) view
				.findViewById(R.id.date_hour);
		viewHour.setViewAdapter(new DateNumericAdapter(context, 0, 23));
		viewHour.setCurrentItem(grop.getbBeginHour());
		viewHour.setCyclic(true);

		final WheelView viewMin = (WheelView) view
				.findViewById(R.id.date_minute);
		viewMin.setViewAdapter(new DateNumericAdapter(context, 0, 59));
		viewMin.setCurrentItem(grop.getbBeginMin());
		viewMin.setCyclic(true);
		changeRadioText(modeTemp, txHome, txOut, txSleep, context);
		txHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeRadioText(1, txHome, txOut, txSleep, v.getContext());
				modeTemp = Constants.FishMode.MODE_HOME;
			}
		});
		txOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeRadioText(2, txHome, txOut, txSleep, v.getContext());
				modeTemp = Constants.FishMode.MODE_OUT;
			}
		});
		txSleep.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeRadioText(3, txHome, txOut, txSleep, v.getContext());
				modeTemp = Constants.FishMode.MODE_SLEEP;
			}
		});
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// grop.setbWorkMode((byte) modeTemp);
				// grop.setbBeginHour((byte) viewHour.getCurrentItem());
				// grop.setbBeginMin((byte) viewMin.getCurrentItem());
				WorkScheduleGroup scheduleGroup = new WorkScheduleGroup();
				scheduleGroup.setbWeekDay(grop.getbWeekDay());
				scheduleGroup.setGroupIndex(grop.getGroupIndex());
				scheduleGroup.setbWorkMode((byte) modeTemp);
				scheduleGroup.setbBeginHour((byte) viewHour.getCurrentItem());
				scheduleGroup.setbBeginMin((byte) viewMin.getCurrentItem());
				scheduleGroup.setTime(scheduleGroup.getbBeginHour() * 60
						+ scheduleGroup.getbBeginMin());
				if (modifyTimeAndMode != null) {
					modifyTimeAndMode.onItemClick(dialog, scheduleGroup, grop);
					dialog.dismiss();
				}
			}
		});
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showModeSelectDialog(final int contactPsoition) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_device_mode, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		TextView txHome = (TextView) view.findViewById(R.id.tx_mode_home);
		TextView txOffice = (TextView) view.findViewById(R.id.tx_mode_office);
		TextView txSleep = (TextView) view.findViewById(R.id.tx_mode_sleep);
		TextViewUtils.setSpannableText(txHome,
				Utils.getStringForId(R.string.mode_text_home),
				Utils.getStringForId(R.string.mode_text_home_tips),
				R.style.spanstyletitlehome);
		TextViewUtils.setSpannableText(txOffice,
				Utils.getStringForId(R.string.mode_text_office),
				Utils.getStringForId(R.string.mode_text_office_tips),
				R.style.spanstyletitleoffice);
		TextViewUtils.setSpannableText(txSleep,
				Utils.getStringForId(R.string.mode_text_sleep),
				Utils.getStringForId(R.string.mode_text_sleep_tips),
				R.style.spanstyletitlesleep);
		Drawable drawableh = MyApplication.app.getResources().getDrawable(
				R.drawable.bg_mode_center_home);
		drawableh.setBounds(0, 0, 180, 180);// 第一0是距左边距离，第二0是距上边距离，第三第四分别是长宽
		txHome.setCompoundDrawables(drawableh, null, null, null);

		Drawable drawableo = MyApplication.app.getResources().getDrawable(
				R.drawable.bg_mode_center_out);
		drawableo.setBounds(0, 0, 180, 180);// 第一0是距左边距离，第二0是距上边距离，第三第四分别是长宽
		txOffice.setCompoundDrawables(drawableo, null, null, null);

		Drawable drawables = MyApplication.app.getResources().getDrawable(
				R.drawable.bg_mode_center_sleep);
		drawables.setBounds(0, 0, 180, 180);// 第一0是距左边距离，第二0是距上边距离，第三第四分别是长宽
		txSleep.setCompoundDrawables(drawables, null, null, null);
		dialog = builder.create();
		dialog.show();
		txHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				itemClick.onItemClick(v, mContact, 1, contactPsoition);
				dismiss();
			}
		});
		txOffice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				itemClick.onItemClick(v, mContact, 2, contactPsoition);
				dismiss();
			}
		});
		txSleep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				itemClick.onItemClick(v, mContact, 3, contactPsoition);
				dismiss();
			}
		});
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) (MyApplication.SCREENWIGHT*0.7);
		layout.height=(int) (MyApplication.SCREENHIGHT*0.5);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showMonitorControlWait() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_mornitor_control_wait, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// if (null!=dialog) {
				// dialog.dismiss();
				// }
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeigh = dm.heightPixels;
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = screenWidth;
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_monitor_control_height);
		view.setLayoutParams(layout);
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showNoListenVoice() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_no_listen_voice, null);
		ImageView reset = (ImageView) view.findViewById(R.id.iv_reset);
		reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_no_listen_voice_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_no_listen_voice_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	// 选择时间dialog
	public void showSelectTime(final int type,final DefenceWorkGroup group,final int position) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_select_time, null);
		TextView title = (TextView) view.findViewById(R.id.dg_tx_title);
		final TextView time = (TextView) view.findViewById(R.id.dg_tx_time);
		WheelView hourFrom = (WheelView) view.findViewById(R.id.dg_hour_from);
		WheelView minuteFrom = (WheelView) view
				.findViewById(R.id.dg_minute_from);
		WheelView hourTo = (WheelView) view.findViewById(R.id.dg_hour_to);
		WheelView minuteTo = (WheelView) view.findViewById(R.id.dg_minute_to);
		Button ensure = (Button) view.findViewById(R.id.dg_bt_ensure);
		if (type==DefenceControlFrag.DATA_ADD) {
			title.setText(R.string.insert_defence);
			time.setText("00:00-00:00");
			group.setbBeginHour((byte) 0);
			group.setbBeginMin((byte) 0);
			group.setbEndHour((byte) 0);
			group.setbEndMin((byte) 0);
		} else {
			title.setText(R.string.change_defence);
			time.setText(group.getBeginTimeString() + "-"
					+ group.getEndTimeString());
		}
		hourFrom.setViewAdapter(new DateNumericAdapter(context, 0, 23));
		hourFrom.setCurrentItem(group.getbBeginHour());
		hourFrom.setCyclic(true);
		
		minuteFrom.setViewAdapter(new DateNumericAdapter(context, 0, 59));
		minuteFrom.setCurrentItem(group.getbBeginMin());
		minuteFrom.setCyclic(true);
		
		hourTo.setViewAdapter(new DateNumericAdapter(context, 0, 23));
		hourTo.setCurrentItem(group.getbEndHour());
		hourTo.setCyclic(true);
		
		minuteTo.setViewAdapter(new DateNumericAdapter(context, 0, 59));
		minuteTo.setCurrentItem(group.getbEndMin());
		minuteTo.setCyclic(true);
		
		ensure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timeSelectListner.onTimeSet(dialog,group,type,position);
			}
		});
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (timeSelectListner != null) {
					switch (wheel.getId()) {
					case R.id.dg_hour_from:
						timeSelectListner
								.onTimeChange(time, newValue, 0, group);
						break;
					case R.id.dg_minute_from:
						timeSelectListner
								.onTimeChange(time, newValue, 1, group);
						break;
					case R.id.dg_hour_to:
						timeSelectListner
								.onTimeChange(time, newValue, 2, group);
						break;
					case R.id.dg_minute_to:
						timeSelectListner
								.onTimeChange(time, newValue, 3, group);
						break;
					default:
						break;
					}
				}
			}
		};
		hourFrom.addChangingListener(wheelListener);
		minuteFrom.addChangingListener(wheelListener);
		hourTo.addChangingListener(wheelListener);
		minuteTo.addChangingListener(wheelListener);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = (int) (MyApplication.SCREENWIGHT * 0.88);
		params.height = (int) (MyApplication.SCREENHIGHT * 0.45);
		params.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(params);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showVisit() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_visit,
				null);
		TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	private void changeRadioText(int select, TextView txHome, TextView txOut,
			TextView txSleep, Context context) {
		if (select == 1) {
			txHome.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check1_video_mode),
					null);
			txOut.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check2_video_mode),
					null);
			txSleep.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check2_video_mode),
					null);
		} else if (select == 2) {
			txOut.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check1_video_mode),
					null);
			txHome.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check2_video_mode),
					null);
			txSleep.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check2_video_mode),
					null);
		} else {
			txSleep.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check1_video_mode),
					null);
			txOut.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check2_video_mode),
					null);
			txHome.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getDrawableByteID(context, R.drawable.check2_video_mode),
					null);
		}
	}

	// 定时布防选择周天dialog
	public void showDefenceSelectListDialog(final DefenceWorkGroup grop,
			int type, int h) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_select_list, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		TextView txTitle = (TextView) view.findViewById(R.id.select_list_title);
		RecyclerView listView = (RecyclerView) view
				.findViewById(R.id.select_list);
		Button btn = (Button) view.findViewById(R.id.btn_select);
		listView.setLayoutManager(new LinearLayoutManager(context));
		WorkeDaySelectAdapter mAdapter = new WorkeDaySelectAdapter(context,
				grop, type);
		listView.setAdapter(mAdapter);
		mAdapter.setWorkeDaySetting(new WorkeDaySelectAdapter.WorkeDaySetting() {

			@Override
			public void onItemClick(View v, SelectItem grop, int position) {
				// TODO Auto-generated method stub
				WorkDaySelect.onItemClick(v, grop, position);
			}
		});

		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		txTitle.setText(title_str);
		btn.setText(btn1_str);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WorkDaySelect.onOkClick(dialog, grop);
			}
		});
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	// 展示定时布防选择周天dialog
	public void showDefenceSelectListDialog(final DefenceWorkGroup grop,
			int type) {
		showDefenceSelectListDialog(grop, type, 0);// 高度暂时没有起作用
	}

	private Drawable getDrawableByteID(Context context, int id) {
		// if (Utils.isSpecification(Build.VERSION_CODES.LOLLIPOP)) {
		// return context.getResources().getDrawable(id, context.getTheme());
		// } else {
		return context.getResources().getDrawable(id);
		// }
	}

	private void changeImage(List<ImageView> list, int position) {
		for (int i = 0; i < list.size(); i++) {
			if (i == position) {
				list.get(i).setImageResource(R.drawable.check1_video_mode);
			} else {
				list.get(i).setImageResource(R.drawable.check2_video_mode);
			}
		}
	}

	// 定时布防选择周天dialog
	public <K extends dialogShowItem> void showStringListDialog(List<K> names,
			final int sele, final int type,int w) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_string_list, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		TextView txTitle = (TextView) view.findViewById(R.id.select_list_title);
		RecyclerView listView = (RecyclerView) view
				.findViewById(R.id.select_list);
		listView.setLayoutManager(new RecycleViewLinearLayoutManager(context));
		DialogSelectStringAdapter<K> mAdapter = new DialogSelectStringAdapter<K>(
				context, names, sele);
		listView.setAdapter(mAdapter);
		mAdapter.setOnItemClick(new OnItemClick<K>() {

			@Override
			public void onItemClick(K k, final int select) {
				if(StringSelect!=null){
					StringSelect.onItemSelect(dialog,k,select,type,select==sele);
				}
			}
		});
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		txTitle.setText(title_str);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = Utils.dip2px(context, w);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}
	
	//新版选择添加设备dialog
	public void showChooseAddDialog(final int contactPsoition) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_choose_add, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		TextView txHome = (TextView) view.findViewById(R.id.tx_mode_home);
		TextView txOffice = (TextView) view.findViewById(R.id.tx_mode_office);
		TextView txSleep = (TextView) view.findViewById(R.id.tx_mode_sleep);
		TextViewUtils.setSpannableText(txHome,
				Utils.getStringForId(R.string.intelligent_online),
				Utils.getStringForId(R.string.moni_shuju1),
				R.style.choose_add_style);
		TextViewUtils.setSpannableText(txOffice,
				Utils.getStringForId(R.string.manually_add),
				Utils.getStringForId(R.string.add_online_device),
				R.style.choose_add_style);
		TextViewUtils.setSpannableText(txSleep,
				Utils.getStringForId(R.string.scan_it),
				Utils.getStringForId(R.string.moni_shuju3),
				R.style.choose_add_style);
		Drawable drawableh = MyApplication.app.getResources().getDrawable(
				R.drawable.bg_intelligent_online_add);
		drawableh.setBounds(0, 0, 180, 180);// 第一0是距左边距离，第二0是距上边距离，第三第四分别是长宽
		txHome.setCompoundDrawables(drawableh, null, null, null);

		Drawable drawableo = MyApplication.app.getResources().getDrawable(
				R.drawable.bg_manually_add);
		drawableo.setBounds(0, 0, 180, 180);// 第一0是距左边距离，第二0是距上边距离，第三第四分别是长宽
		txOffice.setCompoundDrawables(drawableo, null, null, null);

		Drawable drawables = MyApplication.app.getResources().getDrawable(
				R.drawable.bg_sweep_one_sweep_add);
		drawables.setBounds(0, 0, 180, 180);// 第一0是距左边距离，第二0是距上边距离，第三第四分别是长宽
		txSleep.setCompoundDrawables(drawables, null, null, null);
		dialog = builder.create();
		dialog.show();
		txHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				itemClick.onItemClick(v, mContact, 1, contactPsoition);
				dismiss();
			}
		});
		txOffice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				itemClick.onItemClick(v, mContact, 2, contactPsoition);
				dismiss();
			}
		});
		txSleep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				itemClick.onItemClick(v, mContact, 3, contactPsoition);
				dismiss();
			}
		});
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) (MyApplication.SCREENWIGHT*0.7);
		layout.height=(int) (MyApplication.SCREENHIGHT*0.5);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void hideTitle(){

	}
	public void setTitle(String title) {
		this.title_str = title;
	}

	public void setTitle(int id) {
		this.title_str = context.getResources().getString(id);
	}

	public void setListData(String[] data) {
		this.list_data = data;
	}

	public void setCanceledOnTouchOutside(boolean bool) {
		dialog.setCanceledOnTouchOutside(bool);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setCancelable(boolean bool) {
		dialog.setCancelable(bool);
	}

	public String getBtn1_str() {
		return btn1_str;
	}

	public void setBtn1_str(String btn1_str) {
		this.btn1_str = btn1_str;
	}

	public void cancel() {
		dialog.cancel();
	}

	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	public boolean isShowing() {
		return dialog.isShowing();
	}

	public void setBtnListener(TextView btn1, TextView btn2) {

	}

	public void setStyle(int style) {
		this.style = style;
	}

	public interface OnButtonOkListener {
		public void onClick();
	}

	public interface OnButtonCancelListener {
		public void onClick();
	}

	public void setOnButtonOkListener(OnButtonOkListener onButtonOkListener) {
		this.onButtonOkListener = onButtonOkListener;
	}

	public void setOnButtonCancelListener(
			OnButtonCancelListener onButtonCancelListener) {
		this.onButtonCancelListener = onButtonCancelListener;
	}

	public void setOnCancelListener(OnCancelListener onCancelListener) {
		this.onCancelListener = onCancelListener;
		Log.i("dxsSMTP", "setlistener");
	}

	// 设置对话框超时
	private TimerTask reAddTask;
	private Timer mTimer;
	private Handler timeOutHandler;

	public void setTimeOut(long delay) {
		mTimer = new Timer();
		timeOutHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				if(TimeOutListner!=null){
					TimeOutListner.onTimeOut();
				}
				if(mTimer!=null){
					mTimer.cancel();
				}
			};
		};
		reAddTask = new TimerTask() {
			@Override
			public void run() {
				timeOutHandler.sendEmptyMessage(0);
			}
		};
		mTimer.schedule(reAddTask, delay);
	}
	public void cancelTimer(){
		if(mTimer!=null){
			mTimer.cancel();
		}
	}

	private OnNormalDialogTimeOutListner TimeOutListner;

	public interface OnNormalDialogTimeOutListner {
		public void onTimeOut();
	}

	public void setOnNormalDialogTimeOutListner(
			OnNormalDialogTimeOutListner TimeOutListner) {
		this.TimeOutListner = TimeOutListner;
	}

	// 取消自定义监听
	public interface OnCustomCancelListner {
		void onCancle(int mark);
	}

	public void setOnCustomCancelListner(
			OnCustomCancelListner customCancelListner) {
		this.customCancelListner = customCancelListner;
	}

	public void setLoadingMark(int loadingMark) {
		this.loadingMark = loadingMark;
	}

	/**
	 * 报警弹窗
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnAlarmClickListner {
		void onCancelClick(String alarmID, boolean isSurportDelete,
				Dialog dialog);

		void onOkClick(String alarmID, boolean isSurportDelete, Dialog dialog);

		void onDeleteClick(String alarmID, boolean isSurportDelete,
				Dialog dialog);
	}

	private OnAlarmClickListner AlarmClickListner;

	public void setOnAlarmClickListner(OnAlarmClickListner Listner) {
		this.AlarmClickListner = Listner;
	}

	// 单选弹框使用
	public interface onDialogSingleSelectListner {
		void onItemClick(AlertDialog dialog, int position);
	}

	private onDialogSingleSelectListner select;

	public void setOnDialogSingleSelectListner(
			onDialogSingleSelectListner select) {
		this.select = select;
	}

	// 复选弹框使用
	public interface onDialogSelectListner {
		void onItemClick(View v, SelectItem item, int position);

		void onOkClick(AlertDialog dialog, WorkScheduleGroup grop);
	}

	private onDialogSelectListner moreSelect;

	public void setOnDialogSelectListner(onDialogSelectListner select) {
		this.moreSelect = select;
	}

	// 修改时间及模式弹窗使用
	public interface onDialogModifyTimeAndMode {
		void onItemClick(AlertDialog dialog, WorkScheduleGroup grop,
				WorkScheduleGroup beforgroup);
	}

	private onDialogModifyTimeAndMode modifyTimeAndMode;

	public void setOnDialogModifyTimeAndMode(
			onDialogModifyTimeAndMode modifyTimeAndMode) {
		this.modifyTimeAndMode = modifyTimeAndMode;
	}

	public interface onDialogItemClick {
		void onItemClick(View view, Contact mContact, int position,
				int contactPosition);
	}

	private onDialogItemClick itemClick;

	public void setOnDialogItemClick(onDialogItemClick itemClick) {

		this.itemClick = itemClick;
	}

	// 时间选择dialog监听
	public interface onTimeSelectListner {
		void onTimeChange(TextView time, int value, int position,DefenceWorkGroup group);
		void onTimeSet(AlertDialog alertDialog,DefenceWorkGroup group,int type,int position);
	}

	private onTimeSelectListner timeSelectListner;

	public void setOnTimeSelectListner(onTimeSelectListner timeSelectListner) {
		this.timeSelectListner = timeSelectListner;
	}

	// 定时布防使用
	public interface onWorkDayDialogSelectListner {
		void onItemClick(View v, SelectItem item, int position);

		void onOkClick(AlertDialog dialog, DefenceWorkGroup grop);
	}

	private onWorkDayDialogSelectListner WorkDaySelect;

	public void setOnWorkDayDialogSelectListner(
			onWorkDayDialogSelectListner select) {
		this.WorkDaySelect = select;
	}

	// 字符串列表使用
	public interface onStringDialogSelectListner<K extends dialogShowItem> {
		void onItemSelect(AlertDialog dialog,K k,int position,int type,boolean isSame);
	}

	private  onStringDialogSelectListner StringSelect;

	public void setOnStringDialogSelectListner(
			onStringDialogSelectListner select) {
		this.StringSelect = select;
	}

}
