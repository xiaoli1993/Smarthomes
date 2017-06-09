package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.activity.AlarmPictrueActivity;
import com.jwkj.data.AlarmRecord;
import com.jwkj.data.DataManager;
import com.jwkj.global.Constants;
import com.jwkj.utils.Utils;
import com.jwkj.widget.NormalDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.p2p.core.P2PValue;
import com.nuowei.smarthome.R;

import java.util.List;

public class AlarmRecordAdapter extends BaseAdapter {
	List<AlarmRecord> list;
	Context mContext;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
    .showImageForEmptyUri(R.drawable.alarm_default) // image连接地址为空时  
    .showImageOnFail(R.drawable.alarm_default) // image加载失败  
    .showImageOnLoading(R.drawable.alarm_default)
    .displayer(new RoundedBitmapDisplayer(10))
    .cacheInMemory(false) // 加载图片时会在内存中加载缓存  
    .build();  
	public AlarmRecordAdapter(Context context, List<AlarmRecord> list) {
		this.mContext = context;
		this.list = list;
	}

	class ViewHolder {
		private ImageView iv_alarm_pictrue;
		private TextView robotId;
		private TextView allarmType;
		private TextView allarmTime;
		private TextView text_type;
		private TextView allarm_name;

		public TextView getText_type() {
			return text_type;
		}

		public void setText_type(TextView text_type) {
			this.text_type = text_type;
		}
		public TextView getRobotId() {
			return robotId;
		}

		public void setRobotId(TextView robotId) {
			this.robotId = robotId;
		}

		public TextView getAllarmType() {
			return allarmType;
		}

		public void setAllarmType(TextView allarmType) {
			this.allarmType = allarmType;
		}

		public TextView getAllarmTime() {
			return allarmTime;
		}

		public void setAllarmTime(TextView allarmTime) {
			this.allarmTime = allarmTime;
		}

		public ImageView getIv_alarm_pictrue() {
			return iv_alarm_pictrue;
		}

		public void setIv_alarm_pictrue(ImageView iv_alarm_pictrue) {
			this.iv_alarm_pictrue = iv_alarm_pictrue;
		}
		public TextView getAllarm_name() {
			return allarm_name;
		}

		public void setAllarm_name(TextView allarm_name) {
			this.allarm_name = allarm_name;
		}
        
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = arg1;
		final ViewHolder holder;
		if (null == view) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.list_alarm_record_item3, null);
			holder = new ViewHolder();
			holder.setRobotId((TextView) view.findViewById(R.id.robot_id));
			holder.setAllarmType((TextView) view.findViewById(R.id.allarm_type));
			holder.setAllarmTime((TextView) view.findViewById(R.id.allarm_time));
			holder.setText_type((TextView) view.findViewById(R.id.tv_type));
			holder.setIv_alarm_pictrue((ImageView)view.findViewById(R.id.iv_alarm_pictrue));
			holder.setAllarm_name((TextView)view.findViewById(R.id.allarm_name));
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final AlarmRecord ar = list.get(arg0);
		holder.getRobotId().setText(Utils.getDeviceName(ar.deviceId));
		holder.getAllarmTime().setText(
				Utils.ConvertTimeByLong(Long.parseLong(ar.alarmTime)));
		ImageLoader.getInstance().displayImage("file://"+ar.alarmPictruePath, holder.getIv_alarm_pictrue(),options,new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				ar.isLoad=false;
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				ar.isLoad=true;
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
		});
//			 Bitmap bitmap = ImageUtils.getBitmap(ar.alarmPictruePath);
//		     holder.getIv_alarm_pictrue().setImageBitmap(bitmap);
//			 ImageLoader.getInstance().loadImage("file://"+ar.alarmPictruePath, new ImageLoadingListener() {
//				
//				@Override
//				public void onLoadingStarted(String arg0, View arg1) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//					// TODO Auto-generated method stub
//					ar.isLoad=false;
//				}
//				
//				@Override
//				public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
//					// TODO Auto-generated method stub
//					ar.isLoad=true;
//					bitmap=ImageUtils.roundCorners(bitmap, ImageUtils.getScaleRounded(bitmap.getWidth()));
//					holder.getIv_alarm_pictrue().setImageBitmap(bitmap);
//				}
//				
//				@Override
//				public void onLoadingCancelled(String arg0, View arg1) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
		holder.getAllarm_name().setText(mContext.getResources().getString(R.string.sensor)+":"+ar.sensorName);
		holder.getAllarm_name().setVisibility(View.GONE);
		switch (ar.alarmType) {
		case 1:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.allarm_type1);
			holder.getAllarm_name().setVisibility(View.VISIBLE);
			String type;
			if (ar.group<1) {
				type=mContext.getResources().getString(R.string.remote);
			}else if(ar.group==8){
				type=mContext.getResources().getString(R.string.special_sensors);
			}else {
				type=mContext.getResources().getString(R.string.sensor);
			}
			type=type+":"+ar.name;
			holder.getAllarm_name().setText(type);
			
//			if (ar.group >= 0 && ar.item >= 0) {
//				holder.getLayout_extern().setVisibility(RelativeLayout.VISIBLE);
//				holder.getText_group().setText(
//						mContext.getResources().getString(R.string.area)
//								+ ":"
//								+ Utils.getDefenceAreaByGroup(mContext,
//										ar.group));
//				holder.getText_item().setText(
//						mContext.getResources().getString(R.string.channel)
//								+ ":" + (ar.item + 1));
//			}
			break;
		case 2:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.allarm_type2);
			break;
		case 3:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.allarm_type3);
			break;
		// case 4:
		// holder.getAllarmType().setText(R.string.allarm_type4);
		// break;
		case 5:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.allarm_type5);
			break;
		case 6:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.allarm_type6);
			holder.getAllarm_name().setVisibility(View.VISIBLE);
			String type1;
			if (ar.group<1) {
				type1=mContext.getResources().getString(R.string.remote);
			}else if(ar.group==8){
				type1=mContext.getResources().getString(R.string.special_sensors);
			}else {
				type1=mContext.getResources().getString(R.string.sensor);
			}
			type1=type1+":"+ar.name;
			holder.getAllarm_name().setText(type1);
//			if (ar.group >= 0 && ar.item >= 0) {
//				holder.getLayout_extern().setVisibility(RelativeLayout.VISIBLE);
//				holder.getText_group().setText(
//						mContext.getResources().getString(R.string.area)
//								+ ":"
//								+ Utils.getDefenceAreaByGroup(mContext,
//										ar.group));
//				holder.getText_item().setText(
//						mContext.getResources().getString(R.string.channel)
//								+ ":" + (ar.item + 1));
//			}
			break;
		case 7:
		case P2PValue.AlarmType.ALARM_TYPE_PIR_ALARM:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.allarm_type4);
			break;
		case 8:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.defence);
			break;
		case 9:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.no_defence);
			break;
		case 10:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.battery_low_alarm);
			break;
		case 13:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.guest_coming);
			break;
		case 15:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.record_failed);
			break;
		case P2PValue.AlarmType.ALARM_TYPE_SMOKE_ALARM:
			holder.getAllarm_name().setVisibility(View.VISIBLE);
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.alarm_type40);
            break;
		case P2PValue.AlarmType.ALARM_TYPE_GAS_ALARM:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.alarm_type41);
            break;
		case P2PValue.AlarmType.ALARM_TYPE_DOOR_MAGNET:
			holder.getAllarm_name().setVisibility(View.VISIBLE);
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.door_alarm);
            break;
		case P2PValue.AlarmType.ALARM_TYPE_TEMPTATURE:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.alarm_type43);
            break;
		case P2PValue.AlarmType.ALARM_TYPE_HUMIDITY:
			holder.getText_type().setText(R.string.allarm_type);
			holder.getAllarmType().setText(R.string.alarm_type44);
            break;
		default:
			holder.getText_type().setText(R.string.not_know);
			holder.getAllarmType().setText(String.valueOf(ar.alarmType));
			break;
		}

		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				NormalDialog dialog = new NormalDialog(mContext, mContext
						.getResources()
						.getString(R.string.delete_alarm_records), mContext
						.getResources().getString(R.string.are_you_sure_delete)
						+ " " + ar.deviceId + "?", mContext.getResources()
						.getString(R.string.delete), mContext.getResources()
						.getString(R.string.cancel));
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						DataManager.deleteAlarmRecordById(mContext, ar.id);
						Intent refreshAlarm = new Intent();
						refreshAlarm
								.setAction(Constants.Action.REFRESH_ALARM_RECORD);
						mContext.sendBroadcast(refreshAlarm);
					}
				});
				dialog.showDialog();
				return true;
			}

		});
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(ar.alarmPictruePath.equals("")||ar.isLoad==false){
					return;
				}
				Intent alarm_pictrue=new Intent(mContext, AlarmPictrueActivity.class);
				alarm_pictrue.putExtra("alarmPictruePath", ar.alarmPictruePath);
				mContext.startActivity(alarm_pictrue);
			}
		});
		return view;
	}

	public void updateData() {
		notifyDataSetChanged();
	}
	public void setList(List<AlarmRecord> list){
		this.list=list;
	}
}
