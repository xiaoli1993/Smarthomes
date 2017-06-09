package com.jwkj.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

public class DefenceWorkGroup implements Comparable<DefenceWorkGroup>,
		Cloneable, Parcelable {
	public static int[] week = new int[] { R.string.time_mon,
			R.string.time_tue, R.string.time_wen, R.string.time_thur,
			R.string.time_fri, R.string.time_sat, R.string.time_sun };
	private byte bFlag;
	private byte bWeekDay;
	private byte bBeginHour;
	private byte bBeginMin;
	private byte bEndHour;
	private byte bEndMin;
	private int groupIndex;// 第几组
	private long beginTime;// 开始时间
	private long endTime;// 结束时间
	private int bswitch;// 开关状态

	public DefenceWorkGroup() {

	}

	public DefenceWorkGroup(byte[] data) {
		bFlag = data[0];
		bWeekDay = data[1];
		bBeginHour = data[2];
		bBeginMin = data[3];
		bEndHour = data[4];
		bEndMin = data[5];
		groupIndex = getIndex();
		bswitch = isEnable();
	}

	public byte getbFlag() {
		return bFlag;
	}

	public void setbFlag(byte bFlag) {
		this.bFlag = bFlag;
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
	}

	public byte getbBeginMin() {
		return bBeginMin;
	}

	public void setbBeginMin(byte bBeginMin) {
		this.bBeginMin = bBeginMin;
	}

	public byte getbEndHour() {
		return bEndHour;
	}

	public void setbEndHour(byte bEndHour) {
		this.bEndHour = bEndHour;
	}

	public byte getbEndMin() {
		return bEndMin;
	}

	public void setbEndMin(byte bEndMin) {
		this.bEndMin = bEndMin;
	}

	public int getGroupIndex() {
		return getIndex();
	}

	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	}

	public long getBeginTime() {
		return bBeginHour * 60 + bBeginMin;
	}

	public long getEndTime() {
		return bEndHour * 60 + bEndMin;
	}

	public Long getCompareTime() {
		return getBeginTime();
	}

	public int getBswitch() {
		return bswitch;
	}

	public void setBswitch(int bswitch) {
		this.bswitch = bswitch;
	}

	/**
	 * 判断时间组开关
	 * 
	 * @return
	 */
	// public boolean isEnable() {
	// return (bFlag & 0xFF) >> 7 == 1;
	// }
	public int isEnable() {
		if ((bFlag & 0xFF) >> 7 == 1) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setEnable(boolean isEnable) {
		if (isEnable) {
			bFlag = Utils.ChangeBitTrue(bFlag, 7);
		} else {
			bFlag = Utils.ChangeByteFalse(bFlag, 7);
		}
	}

	/**
	 * 获取时间组的位置
	 * 
	 * @return
	 */
	public int getIndex() {
		byte dex = bFlag;
		dex = Utils.ChangeByteFalse(dex, 7);
		return dex;
	}

	/**
	 * 設置
	 * 
	 * @return
	 */
	public void setIndex(int position) {
		bFlag = Utils.ChangeBitTrue(bFlag, position);
	}

	/**
	 * 設置周天
	 */
	public void setWeekDay(int position, boolean isEnable) {
		if (isEnable) {
			bWeekDay = Utils.ChangeBitTrue(bWeekDay, position);
		} else {
			bWeekDay = Utils.ChangeByteFalse(bWeekDay, position);
		}
	}

	/**
	 * 设置时间组开关
	 * 
	 * @param isEnable
	 */
	public void setIsEnable(boolean isEnable) {
		if (isEnable) {
			bFlag = Utils.ChangeBitTrue(bWeekDay, 7);
		} else {
			bFlag = Utils.ChangeByteFalse(bWeekDay, 7);
		}
	}

	/**
	 * 获得时间组开始时间点
	 * 
	 * @return
	 */
	public String getBeginTimeString() {
		String h = "";
		String m = "";
		if (bBeginHour < 10) {
			h = h + "0" + bBeginHour;
		} else {
			h = h + bBeginHour;
		}
		if (bBeginMin < 10) {
			m = m + "0" + bBeginMin;
		} else {
			m = m + bBeginMin;
		}
		return h + ":" + m;
	}

	/**
	 * 获得时间组结束时间点
	 * 
	 * @return
	 */
	public String getEndTimeString() {
		String h = "";
		String m = "";
		if (bEndHour < 10) {
			h = h + "0" + bEndHour;
		} else {
			h = h + bEndHour;
		}
		if (bEndMin < 10) {
			m = m + "0" + bEndMin;
		} else {
			m = m + bEndMin;
		}
		return h + ":" + m;
	}

	public byte[] getAllInfo() {
		byte[] info = new byte[6];
		info[0] = bFlag;
		info[1] = bWeekDay;
		info[2] = bBeginHour;
		info[3] = bBeginMin;
		info[4] = bEndHour;
		info[5] = bEndMin;
		return info;
	}

	public String getTimeText() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(getTimeTextBuffer());
		buffer.append(")");
		return buffer.toString();
	}

	/**
	 * 获得时间组一周重复情况
	 * 
	 * @return 1代表开 0 关
	 */
	public int[] getDayInWeek() {
		int[] temp = new int[8];
		int[] src = Utils.getByteBinnery(bWeekDay, true);
		System.arraycopy(src, 1, temp, 0, src.length - 2);
		temp[6] = src[0];
		return temp;
	}

	public StringBuffer getTimeTextBuffer() {
		int[] weekDay = getDayInWeek();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < weekDay.length - 1; i++) {
			if (weekDay[i] == 1) {
				buffer.append(Utils.getStringForId(week[i]));
				buffer.append(",");
			}
		}
		if (buffer.lastIndexOf(",") != -1) {
			buffer.deleteCharAt(buffer.lastIndexOf(","));
		} else {
			buffer.append(Utils.getStringForId(R.string.repeate_not_set));
		}
		return buffer;
	}

	@Override
	public int compareTo(DefenceWorkGroup another) {
		return this.getCompareTime().compareTo(another.getCompareTime());
	}

	public DefenceWorkGroup clone() {
		DefenceWorkGroup o = null;
		try {
			o = (DefenceWorkGroup) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(this.bFlag);
		dest.writeByte(this.bWeekDay);
		dest.writeByte(this.bBeginHour);
		dest.writeByte(this.bBeginMin);
		dest.writeByte(this.bEndHour);
		dest.writeByte(this.bEndMin);
		dest.writeInt(this.groupIndex);
		dest.writeLong(this.beginTime);
		dest.writeLong(this.endTime);
		dest.writeInt(this.bswitch);
	}

	protected DefenceWorkGroup(Parcel in) {
		this.bFlag = in.readByte();
		this.bWeekDay = in.readByte();
		this.bBeginHour = in.readByte();
		this.bBeginMin = in.readByte();
		this.bEndHour = in.readByte();
		this.bEndMin = in.readByte();
		this.groupIndex = in.readInt();
		this.beginTime = in.readLong();
		this.endTime = in.readLong();
		this.bswitch = in.readInt();
	}

	public static final Creator<DefenceWorkGroup> CREATOR = new Creator<DefenceWorkGroup>() {
		@Override
		public DefenceWorkGroup createFromParcel(Parcel source) {
			return new DefenceWorkGroup(source);
		}

		@Override
		public DefenceWorkGroup[] newArray(int size) {
			return new DefenceWorkGroup[size];
		}
	};

}
