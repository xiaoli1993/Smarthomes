package com.jwkj.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.jwkj.adapter.DateNumericAdapter;
import com.jwkj.wheel.widget.OnWheelScrollListener;
import com.jwkj.wheel.widget.WheelView;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SearchTimeDialog extends AlertDialog {
	private WheelView date_year, date_month, date_day, date_channl;
	private Context mContext;
	private int channls;
	private Button btnSearch;

	public SearchTimeDialog(Context context, int channls) {
		super(context);
		mContext = context;
		this.channls=channls;
		Window window = getWindow();
		window.setWindowAnimations(R.style.dialog_up_down);
		window.setGravity(Gravity.BOTTOM);
	}

	private void initParams() {
		setCanceledOnTouchOutside(true);
	}

	private void initUI(View view) {
		btnSearch = (Button) view.findViewById(R.id.recode_search);
		initWheelView(view);
		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				long start = getTime() / 1000;
				long end = start + 1 * 24 * 60 * 60 - 1;
				int channl = date_channl.getCurrentItem();
				// 搜索时间段内回放视屏
				if (listner != null) {
					listner.searchClick(start, end, channl);
				}
				TimeSelectDismiss();
			}
		});
	}

	public void TimeSelectDismiss() {
		this.dismiss();
	}

	public void TimeSelectShow() {
		this.show();
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_time_select, null);
		FrameLayout.LayoutParams layout = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.width= MyApplication.SCREENWIGHT;
		setContentView(view, layout);
		initParams();
		initUI(view);
	}

	private void initWheelView(View view) {
		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		date_year = (WheelView) view.findViewById(R.id.date_year);
		date_year.setViewAdapter(new DateNumericAdapter(mContext, 2010, 2036));
		date_year.setCurrentItem(curYear - 2010);
		date_year.addScrollingListener(scrolledListener);
		date_year.setCyclic(true);

		int curMonth = calendar.get(Calendar.MONTH) + 1;
		date_month = (WheelView) view.findViewById(R.id.date_month);
		date_month.setViewAdapter(new DateNumericAdapter(mContext, 1, 12));
		date_month.setCurrentItem(curMonth - 1);
		date_month.addScrollingListener(scrolledListener);
		date_month.setCyclic(true);

		int curDay = calendar.get(Calendar.DAY_OF_MONTH);
		date_day = (WheelView) view.findViewById(R.id.date_day);
		date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 31));
		date_day.setCurrentItem(curDay - 1);
		date_day.addScrollingListener(scrolledListener);
		date_day.setCyclic(true);

		int curentChannl = 0;
		date_channl = (WheelView) view.findViewById(R.id.data_channl);
		date_channl
				.setViewAdapter(new DateNumericAdapter(mContext, 1, channls));
		date_channl.setCurrentItem(curentChannl);
		date_channl.addScrollingListener(scrolledListener);
		date_channl.setCyclic(true);
	}

	private boolean wheelScrolled = false;
	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			wheelScrolled = true;
			updateStatus();
		}

		public void onScrollingFinished(WheelView wheel) {
			wheelScrolled = false;
			updateStatus();
		}
	};

	public void updateStatus() {
		int year = date_year.getCurrentItem() + 2010;
		int month = date_month.getCurrentItem() + 1;

		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 31));
		} else if (month == 2) {

			boolean isLeapYear = false;
			if (year % 100 == 0) {
				if (year % 400 == 0) {
					isLeapYear = true;
				} else {
					isLeapYear = false;
				}
			} else {
				if (year % 4 == 0) {
					isLeapYear = true;
				} else {
					isLeapYear = false;
				}
			}
			if (isLeapYear) {
				if (date_day.getCurrentItem() > 28) {
					date_day.scroll(30, 2000);
				}
				date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 29));
			} else {
				if (date_day.getCurrentItem() > 27) {
					date_day.scroll(30, 2000);
				}
				date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 28));
			}

		} else {
			if (date_day.getCurrentItem() > 29) {
				date_day.scroll(30, 2000);
			}
			date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 30));
		}
	}

	private long getTime() {
		Calendar Zro = new GregorianCalendar(date_year.getCurrentItem() + 2010,
				date_month.getCurrentItem(), date_day.getCurrentItem() + 1, 0,
				0, 0);
		return Zro.getTimeInMillis();
	}

	public interface TimeSelectDialogClickListner {
		void searchClick(long start, long end, int channl);
	}

	private TimeSelectDialogClickListner listner;

	public void setOnTimeSelectDialogClickListner(
			TimeSelectDialogClickListner listner) {
		this.listner = listner;
	}
}
