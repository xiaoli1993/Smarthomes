package com.jwkj.data;

import com.baidu.mobstat.b;

public class AlarmRecord {
	// id
	public int id;
	// 报警设备ID
	public String deviceId;
	// 报警类型
	public int alarmType;
	// 报警时间
	public String alarmTime;
	// 当前登录用户
	public String activeUser;
	// 防区
	public int group;
	// 通道
	public int item;
	//图片存放路径
	public String alarmPictruePath="";

	public String sensorName="";
	//不保存到数据库的字段    用于存放  防区名字
	public String name="";
	//加载成功
    public boolean isLoad=false;

    //消息是否查看标记(未读消息查询功能实现后这个标记改为默认0)
    public int isCheck=1;
	
}
