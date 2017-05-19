package com.jwkj.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.util.Log;

public class NVRRecodeTime implements Comparable<NVRRecodeTime>{
	private long startTime;
	private long endTime;
	private String StarTime="";
	private String EndTime="";
	
	
	
	public NVRRecodeTime() {
		super();
	}



	public NVRRecodeTime(long startTime, long endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		init();
	}
	

	public NVRRecodeTime(Date start,Date end) {
		super();
		initLong(start,end);
	}



	private void initLong(Date start,Date end) {
		startTime=start.getTime()/1000;
		endTime=end.getTime()/1000;
		init();
	}



	private void init() {
		StarTime=paserTime(startTime);
		EndTime=paserTime(endTime);
		
	}
	private String paserTime(String time){
		long timeL=Long.parseLong(time);
		return paserTime(timeL);
	}
	
	private String paserTime(long time){
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
		TimeZone zone=TimeZone.getDefault();
		zone.setRawOffset(0);
		sdf.setTimeZone(zone);
		return sdf.format(new Date((time*1000L)));
	}


	public long getStartTime() {
		return startTime;
	}



	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}



	public long getEndTime() {
		return endTime;
	}



	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}



	public String getStarTime() {
		return StarTime;
	}



	public void setStarTime(String starTime) {
		StarTime = starTime;
	}



	public String getEndTimes() {
		return EndTime;
	}



	public void setEndTime(String endTime) {
		EndTime = endTime;
	}
	
	
	@Override
    public int compareTo(NVRRecodeTime another) {
		if(this.getStartTime()==another.getStartTime()){
			return 0;
		}else if(this.getStartTime()>another.getStartTime()){
			return 1;
		}else{
			return -1;
		}
    }
	
	public long getMillis(){
		return endTime-startTime;
	}
	
	public static String getMillisString(long time){
		SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm:ss");
		TimeZone zone=TimeZone.getDefault();
		zone.setRawOffset(0);
		sdf.setTimeZone(zone);
		return sdf.format(new Date((time*1000L)));
	}
	
	public boolean isContantTime(long time){
		return time>=startTime&&time<=endTime;
	}

}
