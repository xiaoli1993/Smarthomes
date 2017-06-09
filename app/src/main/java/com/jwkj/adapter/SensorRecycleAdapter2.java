package com.jwkj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwkj.data.Prepoint;
import com.jwkj.entity.Sensor;
import com.jwkj.global.Constants;
import com.jwkj.widget.SwitchView;
import com.nuowei.smarthome.R;

import java.util.List;

/**
 * Created by dxs on 2015/12/29.
 */
public class SensorRecycleAdapter2 extends RecyclerView.Adapter<SensorRecycleAdapter2.ViewHolder>{
    private Context mContext;
    private List<Sensor> lists;
    private Prepoint prepoint;
    private boolean isNewData=false;
    private boolean isSurpporPrepoint=false;
    public SensorRecycleAdapter2(Context context) {
        mContext=context;
    }

    public SensorRecycleAdapter2(Context mContext, List<Sensor> lists) {
        this.mContext = mContext;
        this.lists = lists;
    }

    public SensorRecycleAdapter2(Context mContext, List<Sensor> lists,
			Prepoint prepoint) {
		super();
		this.mContext = mContext;
		this.lists = lists;
		this.prepoint = prepoint;
	}

	public boolean isNewData() {
		return isNewData;
	}

	public void setNewData(boolean isNewData) {
		this.isNewData = isNewData;
	}
	
	

	public boolean isSurpporPrepoint() {
		return isSurpporPrepoint;
	}

	public void setSurpporPrepoint(boolean isSurpporPrepoint) {
		this.isSurpporPrepoint = isSurpporPrepoint;
	}

	@Override
    public SensorRecycleAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.items_sensor_list, null);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    //此处需要按照类型来加载布局，暂时共用一个布局
    @Override
    public void onBindViewHolder(final SensorRecycleAdapter2.ViewHolder holder, final int position) {
        final Sensor sensor=lists.get(position);
        holder.txName.setText(sensor.getName());
        if(sensor.getSensorCategory()==Sensor.SENSORCATEGORY_NORMAL){
            //一般传感器
            if(sensor.getSensorType()== Constants.SensorType.TYPE_REMOTE_CONTROLLER){
                //遥控类传感器
                holder.SensorSwitch.setVisibility(View.GONE);
                holder.Raw.setVisibility(View.GONE);
                holder.ll_position.setVisibility(View.GONE);
            }else if(sensor.isControlSensor()){
                //插座控制类传感器
                holder.Raw.setVisibility(View.INVISIBLE);
                holder.SensorSwitch.setVisibility(View.VISIBLE);
                holder.ll_position.setVisibility(View.GONE);
                if(sensor.getLampState()==0){//插座在查询
                    holder.SensorSwitch.setModeStatde(SwitchView.State_progress);
                }else if(sensor.getLampState()==1||sensor.getLampState()==3){//插座开
                    holder.SensorSwitch.setModeStatde(SwitchView.State_on);
                }else if(sensor.getLampState()==4){//窗帘处理
                    holder.SensorSwitch.setModeStatde(SwitchView.State_off);
                }else{
                    holder.SensorSwitch.setModeStatde(SwitchView.State_off);
                }
            }else{
                holder.Raw.setVisibility(View.VISIBLE);
                holder.SensorSwitch.setVisibility(View.VISIBLE);
                if(sensor.getSensorSwitch()){
                    holder.SensorSwitch.setModeStatde(SwitchView.State_on);
                }else{
                    holder.SensorSwitch.setModeStatde(SwitchView.State_off);
                }
                if (isSurpporPrepoint) {
                	if (isNewData) {
        				holder.ll_position.setVisibility(View.VISIBLE);
        				if ((sensor.getPrepoint()-1)==-1) {
        					holder.tx_positionName.setText("");
        				}else {
        					holder.tx_positionName.setText(prepoint.getName(sensor.getPrepoint()-1));
        				}
        			}
				}
            }
        }else{
            //特殊传感器
            holder.Raw.setVisibility(View.VISIBLE);
            holder.SensorSwitch.setVisibility(View.VISIBLE);
            if(sensor.getSensorSwitch()){
                holder.SensorSwitch.setModeStatde(SwitchView.State_on);
            }else{
                holder.SensorSwitch.setModeStatde(SwitchView.State_off);
            }
            if (isSurpporPrepoint) {
            	if (isNewData) {
    				holder.ll_position.setVisibility(View.VISIBLE);
    				if ((sensor.getPrepoint()-1)==-1) {
    					holder.tx_positionName.setText("");
    				}else {
    					holder.tx_positionName.setText(prepoint.getName(sensor.getPrepoint()-1));
    				}
    			}
			} 
        }
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onItemClick(v,sensor,position);
            }
        });
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listner.onLongClick(holder, sensor, position);
                return true;
            }
        });
        holder.SensorSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SwitchView)v).setModeStatde(SwitchView.State_progress);
                listner.onSwitchClick(holder, sensor, position);
            }
        });
        holder.iv_position.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listner.toBindingPrePoint(holder, sensor, position);
			}
		});

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void UpadateSensor(Sensor sensor){
        if(sensor==null)return;
        int position=lists.indexOf(sensor);
        if(position!=-1){
            notifyItemChanged(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public ImageView Point;
        public TextView txName;
        public ImageView Raw;
        public SwitchView SensorSwitch;
        public LinearLayout ll_position;
        public ImageView iv_position;
        public TextView tx_positionName;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView=itemView;
            Point= (ImageView) itemView.findViewById(R.id.iv_point);
            txName= (TextView) itemView.findViewById(R.id.tx_sensor_name);
            Raw= (ImageView) itemView.findViewById(R.id.iv_sensor_raw);
            SensorSwitch= (SwitchView) itemView.findViewById(R.id.iv_sensor_switch);
            ll_position=(LinearLayout)itemView.findViewById(R.id.ll_position);
            iv_position=(ImageView)itemView.findViewById(R.id.iv_position);
            tx_positionName=(TextView)itemView.findViewById(R.id.tx_positionName);
        }
    }
    private onSensorRecycleAdatperClickListner listner;
    public void setOnSensorRecycleAdatperClickListner(onSensorRecycleAdatperClickListner listner){
        this.listner=listner;
    }
    public interface onSensorRecycleAdatperClickListner{
        void onItemClick(View contentview, Sensor sensor, int position);
        void onLongClick(ViewHolder holder, Sensor sensor, int position);
        void onSwitchClick(ViewHolder holder, Sensor sensor, int position);
        void toBindingPrePoint(ViewHolder holder,Sensor sensor,int position);
    }
	
}
