package com.jwkj.data;

import java.io.Serializable;

import com.jwkj.entity.WorkScheduleGroup;
import com.jwkj.utils.Utils;
import com.nuowei.smarthome.R;

public class DefenceArea implements Serializable, Comparable<DefenceArea>,
		Cloneable {
	public int id;
	public final static int REMOTETYPE = 1;
	public final static int CHANNELTYPE = 0;
	public final static int SPECIAL_CHANNELTYPE = 8;
	private String name;
	private int group;
	private int item;
	private int state = -1;
	private int type;
	private int location = -1;
	private int eflag = 0;

	public DefenceArea(int group, int item, int state, int type) {
		super();
		this.group = group;
		this.item = item;
		this.state = state;
		this.type = type;
		setDefenceAreaName(getItemIndex());
	}

	public DefenceArea() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		if (eflag == 0) {
			return getDefenceAreaName(getItemIndex());
		} else {
			return name;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public int getEflag() {
		return eflag;
	}

	public void setEflag(int eflag) {
		this.eflag = eflag;
	}

	/**
	 * 获取对象在遥控防区的位置
	 * 
	 * @return
	 */
	public int getItemIndex() {
		return group * 8 + item;
	}

	/**
	 * 通过对象在遥控防区的位置给"组"和"列"赋值
	 * 
	 * @param index
	 */
	public void indexToGoupAndItem(int index) {
		group = index / 8;
		item = index % 8;
	}

	public void setDefenceAreaName(int index) {
		switch (type) {
		case REMOTETYPE:
			this.name = Utils.getStringByResouceID(R.string.remote)
					+ (index + 1);
			break;
		case CHANNELTYPE:
			this.name = Utils.getStringByResouceID(R.string.area) + (index - 7);
			break;
		case SPECIAL_CHANNELTYPE:
			this.name = Utils.getStringByResouceID(R.string.special_sensors)
					+ (index - 63);
			break;
		default:
			this.name = Utils.getStringByResouceID(R.string.not_know)
					+ (index + 1);
			break;
		}
	}

	public String getDefenceAreaName(int index) {
		switch (type) {
		case REMOTETYPE:
			return Utils.getStringByResouceID(R.string.remote) + (index + 1);
		case CHANNELTYPE:
			return Utils.getStringByResouceID(R.string.area) + (index - 7);
		case SPECIAL_CHANNELTYPE:
			return Utils.getStringByResouceID(R.string.special_sensors)
					+ (index - 63);
		default:
			return Utils.getStringByResouceID(R.string.not_know) + (index + 1);
		}
	}

	@Override
	public int compareTo(DefenceArea another) {
		// TODO Auto-generated method stub
		Integer mineIndex = this.getItemIndex();
		Integer anotherIndex = another.getItemIndex();
		return mineIndex.compareTo(anotherIndex);
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == null) {
			return false;
		}
		if (o instanceof DefenceArea) {
			DefenceArea dArea = (DefenceArea) o;
			if (this.getGroup() == dArea.getGroup()
					&& this.getItem() == dArea.getItem()) {
				return true;
			}
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		Integer i = this.getItemIndex();
		return i.hashCode();
	}

}
