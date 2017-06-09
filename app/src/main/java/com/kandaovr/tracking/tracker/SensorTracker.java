package com.kandaovr.tracking.tracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.kandaovr.tracking.representation.Quaternion;

/**
 * Created by liyang@kandaovr.com on 2016/7/7.
 */
public class SensorTracker implements SensorEventListener{

    SensorTrackerListener listener = null;
    Context context;
    SensorManager mSensorManager;
    
    public SensorTracker(Context context){
        this.context = context;
        
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }
    
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		
			//获取重力加速度在屏幕上的XY分量
			float gx=event.values[0];
			float gy=event.values[1];
			float gz=event.values[2];

			//求出屏幕上重力加速度向量的分量长度
			double mLength=gx*gx+gy*gy;
			mLength=Math.sqrt(mLength);
			//若分量为0则返回
			if(mLength==0)
			{
				return;
			}
			//若分量不为0则设置球滚动的步进
//			Constant.SPANX=(float)((gy/mLength)*0.08);
//			Constant.SPANZ=(float)((gx/mLength)*0.08);				
			Quaternion orientation =  new Quaternion();;
//			orientation.setX(gx/(float)mLength);
//			orientation.setY(gy/(float)mLength);
//			orientation.setZ(gz/(float)mLength);
			if(gx*gx <1)
			{
				gx =0;
			}
			
			if(gy* gy < 1)
			{
				gy =0;
			}
			
			orientation.setX(gx);
			orientation.setY(gy);
			orientation.setZ(gz);
			
			listener.onOrientationChange(orientation);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	
    public void registerListener(SensorTrackerListener listener){
        this.listener = listener;
    }
    
    public void startTracking(){
       
        ////////////////////////rotation angle and gravity sensor
    	 if(mSensorManager != null){
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
    	 }
    }

    public void stopTracking(){
        if(mSensorManager != null){
        	mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
//            mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
//            mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        }
    }
}