package com.jwkj.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by WXY on 2016/10/11.
 */

public class PlayBackFastLayout extends LinearLayout {
    private TextView fastTime;
    public PlayBackFastLayout(Context context) {
        super(context);
        init(context);
    }

    public PlayBackFastLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.fast_linearlayout, this);
        fastTime=(TextView)view.findViewById(R.id.fast_time);
    }

    public void setFastTime(int value){
        fastTime.setText(Utils.ConvertTimeByString(value*1000,"HH:mm:ss"));
    }
}
