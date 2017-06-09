package com.jwkj.widget;

import java.util.ArrayList;
import java.util.List;

import com.jwkj.data.Prepoint;
import com.jwkj.fragment.MonitorThreeFrag;
import com.nuowei.smarthome.R;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by WXY on 2016/7/7.
 */
public class PreSetLocationLayout extends LinearLayout implements View.OnClickListener {
    private TextView preset_location1;
    private TextView preset_location2;
    private TextView preset_location3;
    private TextView preset_location4;
    private TextView preset_location5;
    private LinearLayout preset_location_add;
    private List<TextView> preset_locations=new ArrayList<TextView>();
    public PreSetLocationLayout(Context context) {
        super(context);
        init(context);

    }

    public PreSetLocationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

	public void init(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.preset_locations_linearlayout, this);
        preset_location1=(TextView)view.findViewById(R.id.preset_location1);
        preset_location2=(TextView)view.findViewById(R.id.preset_location2);
        preset_location3=(TextView)view.findViewById(R.id.preset_location3);
        preset_location4=(TextView)view.findViewById(R.id.preset_location4);
        preset_location5=(TextView)view.findViewById(R.id.preset_location5);
        preset_location_add=(LinearLayout)view.findViewById(R.id.preset_location_add);
        preset_location1.setOnClickListener(this);
        preset_location2.setOnClickListener(this);
        preset_location3.setOnClickListener(this);
        preset_location4.setOnClickListener(this);
        preset_location5.setOnClickListener(this);
        preset_location_add.setOnClickListener(this);
        preset_locations.add(preset_location1);
        preset_locations.add(preset_location2);
        preset_locations.add(preset_location3);
        preset_locations.add(preset_location4);
        preset_locations.add(preset_location5);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.preset_location1:
            	preSetLocationListener.lookPresetLocation(0);
                break;
            case R.id.preset_location2:
            	preSetLocationListener.lookPresetLocation(1);
                break;
            case R.id.preset_location3:
            	preSetLocationListener.lookPresetLocation(2);
                break;
            case R.id.preset_location4:
            	preSetLocationListener.lookPresetLocation(3);
                break;
            case R.id.preset_location5:
            	preSetLocationListener.lookPresetLocation(4);
                break;
            case R.id.preset_location_add:
            	preSetLocationListener.addPresetLocation();
                break;
        }
    }
    
    public void changeVisibility(List<Integer> list,int PreFunctionMode){
    	if (PreFunctionMode==84||PreFunctionMode==254) {
			return;
		}
    	if (PreFunctionMode==-1) {
			for (int i = 0; i <preset_locations.size(); i++) {
				preset_locations.get(i).setVisibility(View.GONE);
			}
			preset_location_add.setVisibility(View.GONE);
			invalidate();
			return;
		}
    	if (null==list||list.size()==0) {
    		for (int i = 0; i <preset_locations.size(); i++) {
				preset_locations.get(i).setVisibility(View.INVISIBLE);
			}
    		preset_location_add.setVisibility(View.VISIBLE);
    		invalidate();
    		return;
		}
    	if (list.size()==MonitorThreeFrag.PREPOINTCOUNTS) {
    		for (int i = 0; i <preset_locations.size(); i++) {
				preset_locations.get(i).setVisibility(View.VISIBLE);
			}
			preset_location_add.setVisibility(View.GONE);
			invalidate();
    		return;
		}
    	preset_location_add.setVisibility(View.VISIBLE);
    	for (int i = 0; i <preset_locations.size(); i++) {
			preset_locations.get(i).setVisibility(View.GONE);
		}
    	for (int i = 0; i < list.size(); i++) {
			preset_locations.get(list.get(i)).setVisibility(View.VISIBLE);
		}
    	invalidate();
    }
    
    private PreSetLocationListener preSetLocationListener;
    
    public void setPreSetLocationListener(PreSetLocationListener listener){
    	this.preSetLocationListener=listener;
    }
    
    public interface PreSetLocationListener{
    	void addPresetLocation();
    	void lookPresetLocation(int point);
    }
    
    
    public void changeName(Prepoint prepoint,List<Integer> list,int PreFunctionMode){
    	if (PreFunctionMode==84||PreFunctionMode==254) {
			return;
		}
    	if (PreFunctionMode==-1) {
			return;
		}
    	if (null==list||list.size()==0) {
    		return;
		}
    	if (list.size()==MonitorThreeFrag.PREPOINTCOUNTS) {
    		for (int i = 0; i <preset_locations.size(); i++) {
				preset_locations.get(i).setText(prepoint.getName(i));;
			}
    		return;
		}
    	for (int i = 0; i < list.size(); i++) {
			preset_locations.get(list.get(i)).setText(prepoint.getName(list.get(i)));;
		}
    }
}
