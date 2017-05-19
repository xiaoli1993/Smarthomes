package com.jwkj.widget;


import com.nuowei.ipclibrary.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class BlueAreaView extends LinearLayout{

	public BlueAreaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.linearlayout_blue_area, this);
	}

	public BlueAreaView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
}
