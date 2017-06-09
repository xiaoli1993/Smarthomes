package com.jwkj.entity;

import android.util.Log;




import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.jwkj.global.Constants;

/**
 * Created by dxs on 2016/1/13.
 */
public class WorkScheduleGroup implements Cloneable,Serializable,Comparable<WorkScheduleGroup>{
    public static int[] modes=new int[]{R.string.time_mon,R.string.time_tue,R.string.time_wen,R.string.time_thur,R.string.time_fri,R.string.time_sat,R.string.time_sun};
    private byte bWeekDay;
    private byte bBeginHour;
    private byte bBeginMin;
    private byte bWorkMode;
    private byte GroupIndex;
    //排序使用的时间
    private long Time;

    public WorkScheduleGroup() {
    }

    public WorkScheduleGroup(byte[] data,byte index) {
        bWeekDay = data[0];
        bBeginHour = data[1];
        bBeginMin = data[2];
        bWorkMode = data[3];
        GroupIndex=index;
        Time=bBeginHour*60+bBeginMin;
    }

    public WorkScheduleGroup(byte[] data,int offset){
        GroupIndex=data[offset];
        bWeekDay = data[offset+1];
        bBeginHour = data[offset+2];
        bBeginMin = data[offset+3];
        bWorkMode=data[offset+4];
        Time=bBeginHour*60+bBeginMin;
    }

    public byte getbWeekDay() {
        return bWeekDay;
    }

    public void setbWeekDay(byte bWeekDay) {
        this.bWeekDay = bWeekDay;
    }

    public byte getbBeginHour() {
        return bBeginHour;
    }

    public void setbBeginHour(byte bBeginHour) {
        this.bBeginHour = bBeginHour;
        Time=bBeginHour*60+bBeginMin;
    }

    public byte getbBeginMin() {
        return bBeginMin;
    }

    public void setbBeginMin(byte bBeginMin) {
        this.bBeginMin = bBeginMin;
        Time=bBeginHour*60+bBeginMin;
    }

    public byte getbWorkMode() {
        return bWorkMode;
    }

    public void setbWorkMode(byte bWorkMode) {
        this.bWorkMode = bWorkMode;
    }

    public byte getGroupIndex() {
        return GroupIndex;
    }

    public void setGroupIndex(byte groupIndex) {
        GroupIndex = groupIndex;
    }

    public void setTime(long time) {
        Time = time;
    }

    /**
     * 获取时间用来排序
     * @return 时间分钟
     */
    public Long gettime(){
        return Time;
    }


    public WorkScheduleGroup clone() {
        WorkScheduleGroup o = null;
        try {
            o = (WorkScheduleGroup) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 判断时间组开关
     * @return
     */
    public boolean isEnable() {
        return (bWeekDay&0xFF) >> 7 == 1;
    }

    /**
     * 设置时间组开关
     * @param isEnable
     */
    public void setIsEnable(boolean isEnable){
        if(isEnable){
            bWeekDay=Utils.ChangeBitTrue(bWeekDay, 7);
        }else{
            bWeekDay=Utils.ChangeByteFalse(bWeekDay, 7);
        }
    }

    /**
     * 获得时间组时间点
     * @return
     */
    public String getTime() {
        String h="";
        String m="";
        if(bBeginHour<10){
            h=h+"0"+bBeginHour;
        }else{
            h=h+bBeginHour;
        }
        if(bBeginMin<10){
            m=m+"0"+bBeginMin;
        }else{
            m=m+bBeginMin;
        }
        long time = bBeginHour * 60 * 60 * 1000 + bBeginMin * 60 * 1000;
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        return h+":"+m;
    }

    public String getTimeText(){
        StringBuffer buffer=new StringBuffer();
        buffer.append("(");
        buffer.append(getTimeTextBuffer());
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * 获得时间组一周重复情况
     * @return 1代表开  0 关
     */
    public int[] getDayInWeek() {
        int[] temp=new int[8];
        int[] src=Utils.getByteBinnery(bWeekDay,true);
        System.arraycopy(src,1,temp,0,src.length-2);
        temp[6]=src[0];
        return temp;
    }

    public byte[] getAllInfo(){
        byte[] info=new byte[5];
        info[0]=GroupIndex;
        info[1]=bWeekDay;
        info[2]=bBeginHour;
        info[3]=bBeginMin;
        info[4]=bWorkMode;
        return info;
    }

    public String getModeText(){
        if(bWorkMode== Constants.FishMode.MODE_HOME){
            return Utils.getStringForId(R.string.mode_home);
        }else if(bWorkMode== Constants.FishMode.MODE_OUT){
            return Utils.getStringForId(R.string.mode_out);
        }else if(bWorkMode== Constants.FishMode.MODE_SLEEP){
            return Utils.getStringForId(R.string.mode_sleep);
        }
        return "";
    }

    public StringBuffer getTimeTextBuffer(){
        int[] week=getDayInWeek();
        StringBuffer buffer=new StringBuffer();
        for (int i=0;i<week.length-1;i++){
            if(week[i]==1){
                buffer.append(Utils.getStringForId(modes[i]));
                buffer.append(",");
            }
        }
        if(buffer.lastIndexOf(",")!=-1){
            buffer.deleteCharAt(buffer.lastIndexOf(","));
        }else{
            buffer.append(Utils.getStringForId(R.string.repeate_not_set));
        }
        return buffer;
    }
    @Override
    public int compareTo(WorkScheduleGroup another) {
        return this.gettime().compareTo(another.gettime());
    }
}
