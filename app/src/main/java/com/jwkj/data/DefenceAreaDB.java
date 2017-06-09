package com.jwkj.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DefenceAreaDB {
	public static final String TABLE_NAME = "defence_area";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";

	public static final String COLUMN_DEVICEID = "deviceId";
	public static final String COLUMN_DEVICEID_DATA_TYPE = "varchar";

	public static final String COLUMN_ACTIVE_USER = "activeUser";
	public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";

	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_NAME_DATA_TYPE = "varchar";

	public static final String COLUMN_GROUP = "defencegroup";
	public static final String COLUMN_GROUP_DATA_TYPE = "integer";

	public static final String COLUMN_ITEM = "item";
	public static final String COLUMN_ITEM_DATA_TYPE = "integer";

	public static final String COLUMN_TYPE = "defencetype";
	public static final String COLUMN_TYPE_DATA_TYPE = "integer";

	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_LOCATION_DATA_TYPE = "integer";

	public static final String COLUMN_EFLAG = "eflag";
	public static final String COLUMN_EFLAG_DATA_TYPE = "integer";

	private SQLiteDatabase myDatabase;

	public DefenceAreaDB(SQLiteDatabase myDatabase) {
		this.myDatabase = myDatabase;
	}

	public static String getDeleteTableSQLString() {
		return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
	}

	public static String getCreateTableString() {
		HashMap<String, String> columnNameAndType = new HashMap<String, String>();
		columnNameAndType.put(COLUMN_ID, COLUMN_ID_DATA_TYPE);
		columnNameAndType.put(COLUMN_DEVICEID, COLUMN_DEVICEID_DATA_TYPE);
		columnNameAndType.put(COLUMN_ACTIVE_USER, COLUMN_ACTIVE_USER_DATA_TYPE);
		columnNameAndType.put(COLUMN_NAME, COLUMN_NAME_DATA_TYPE);
		columnNameAndType.put(COLUMN_GROUP, COLUMN_GROUP_DATA_TYPE);
		columnNameAndType.put(COLUMN_ITEM, COLUMN_ITEM_DATA_TYPE);
		columnNameAndType.put(COLUMN_TYPE, COLUMN_TYPE_DATA_TYPE);
		columnNameAndType.put(COLUMN_LOCATION, COLUMN_LOCATION_DATA_TYPE);
		columnNameAndType.put(COLUMN_EFLAG, COLUMN_EFLAG_DATA_TYPE);
		String mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(
				TABLE_NAME, columnNameAndType);
		return mSQLCreateWeiboInfoTable;
	}

	/**
	 * 插入一个预置位
	 * 
	 * @param point
	 * @return
	 */
	public long insert(String activeUser, String deviceId,
			DefenceArea defenceArea) {
		long isResut = -1;
		if (defenceArea != null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_ACTIVE_USER, activeUser);
			values.put(COLUMN_DEVICEID, deviceId);
			values.put(COLUMN_NAME, defenceArea.getName());
			values.put(COLUMN_GROUP, defenceArea.getGroup());
			values.put(COLUMN_ITEM, defenceArea.getItem());
			values.put(COLUMN_TYPE, defenceArea.getType());
			values.put(COLUMN_LOCATION, defenceArea.getLocation());
			values.put(COLUMN_EFLAG, defenceArea.getEflag());
			try {
				isResut = myDatabase.insertOrThrow(TABLE_NAME, null, values);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}
		return isResut;
	}

	/**
	 * 查找设备的所有防区
	 * 
	 * @param activeUser
	 * @param deviceId
	 * @return
	 */
	public List<DefenceArea> findAllDefenceAreaByDeviceID(String activeUser,
			String deviceId) {
		List<DefenceArea> defenceAreas = new ArrayList<DefenceArea>();
		String[] whereArgs = { activeUser, deviceId };
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_ACTIVE_USER + "=? and " + COLUMN_DEVICEID + "=?",
				whereArgs);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

				String name = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME));
				int group = cursor.getInt(cursor.getColumnIndex(COLUMN_GROUP));
				int item = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM));
				int type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));
				int location = cursor.getInt(cursor
						.getColumnIndex(COLUMN_LOCATION));
				int eflag = cursor.getInt(cursor.getColumnIndex(COLUMN_EFLAG));
				DefenceArea defenceArea = new DefenceArea();
				defenceArea.id = id;
				defenceArea.setName(name);
				defenceArea.setGroup(group);
				defenceArea.setItem(item);
				defenceArea.setType(type);
				defenceArea.setLocation(location);
				defenceArea.setEflag(eflag);
				defenceAreas.add(defenceArea);
			}
			cursor.close();
		}
		return defenceAreas.size() > 0 ? defenceAreas : null;
	}

	/**
	 * 查找设备的单个防区根据行和列
	 * 
	 * @param activeUser
	 * @param deviceId
	 * @return
	 */
	public DefenceArea findDefenceAreaByDeviceID(String activeUser,
			String deviceId, DefenceArea area) {
		List<DefenceArea> defenceAreas = new ArrayList<DefenceArea>();
		String[] whereArgs = { activeUser, deviceId,
				String.valueOf(area.getGroup()), String.valueOf(area.getItem()) };
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_ACTIVE_USER + "=? and " + COLUMN_DEVICEID + "=? and "
				+ COLUMN_GROUP + "=? and " + COLUMN_ITEM + "=?", whereArgs);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

				String name = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME));
				int group = cursor.getInt(cursor.getColumnIndex(COLUMN_GROUP));
				int item = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM));
				int type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));
				int location = cursor.getInt(cursor
						.getColumnIndex(COLUMN_LOCATION));
				int eflag = cursor.getInt(cursor.getColumnIndex(COLUMN_EFLAG));
				DefenceArea defenceArea = new DefenceArea();
				defenceArea.id = id;
				defenceArea.setName(name);
				defenceArea.setGroup(group);
				defenceArea.setItem(item);
				defenceArea.setType(type);
				defenceArea.setLocation(location);
				defenceArea.setEflag(eflag);
				defenceAreas.add(defenceArea);
			}
			cursor.close();
		}else {
			defenceAreas.add(null);
		}
		return defenceAreas.size() > 0 ? defenceAreas.get(0) : null;
	}

	/**
	 * 修改一个防区所有的值
	 * 
	 * @param activeUser
	 * @param deviceID
	 * @param defenceArea
	 * @return
	 */
	public long updateDefenceAreaByGroupAndItem(String activeUser,
			String deviceID, DefenceArea defenceArea) {
		long isResut = -1;
		if (null != defenceArea) {
			String[] whereArgs = { activeUser, deviceID,
					String.valueOf(defenceArea.getGroup()),
					String.valueOf(defenceArea.getItem()) };
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME, defenceArea.getName());
			values.put(COLUMN_GROUP, defenceArea.getGroup());
			values.put(COLUMN_ITEM, defenceArea.getItem());
			values.put(COLUMN_TYPE, defenceArea.getType());
			values.put(COLUMN_LOCATION, defenceArea.getLocation());
			values.put(COLUMN_EFLAG, defenceArea.getEflag());
			try {
				isResut = myDatabase.update(TABLE_NAME, values,
						COLUMN_ACTIVE_USER + "=? and " + COLUMN_DEVICEID
								+ "=? and " + COLUMN_GROUP + "=? and "
								+ COLUMN_ITEM + "=?", whereArgs);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}
		return isResut;
	}

	/**
	 * 修改一个防区的名字
	 * 
	 * @param activeUser
	 * @param deviceID
	 * @param defenceArea
	 * @return
	 */
	public long updateDefenceAreaNameByGroupAndItem(String activeUser,
			String deviceID, String name, DefenceArea defenceArea) {
		long isResut = -1;
		if (null != defenceArea) {
			String[] whereArgs = { activeUser, deviceID,
					String.valueOf(defenceArea.getGroup()),
					String.valueOf(defenceArea.getItem()) };
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME, name);
			try {
				isResut = myDatabase.update(TABLE_NAME, values,
						COLUMN_ACTIVE_USER + "=? and " + COLUMN_DEVICEID
								+ "=? and " + COLUMN_GROUP + "=? and "
								+ COLUMN_ITEM + "=?", whereArgs);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}
		return isResut;
	}

	/**
	 * 修改一个防区的名字和标识符
	 * 
	 * @param activeUser
	 * @param deviceID
	 * @param defenceArea
	 * @return
	 */
	public long updateDefenceAreaNameAndEflagByGroupAndItem(String activeUser,
			String deviceID, String name, int eflag, int group, int item) {
		long isResut = -1;
		String[] whereArgs = { activeUser, deviceID, String.valueOf(group),
				String.valueOf(item) };
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, name);
		values.put(COLUMN_EFLAG, eflag);
		try {
			isResut = myDatabase.update(TABLE_NAME, values, COLUMN_ACTIVE_USER
					+ "=? and " + COLUMN_DEVICEID + "=? and " + COLUMN_GROUP
					+ "=? and " + COLUMN_ITEM + "=?", whereArgs);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return isResut;
	}

	/**
	 * 删除跟设备相关的所有防区
	 * 
	 * @param activeUser
	 * @param deviceID
	 * @return
	 */
	public int deleteAllDefenceAreaByDeviceID(String activeUser, String deviceID) {
		return myDatabase
				.delete(TABLE_NAME, COLUMN_ACTIVE_USER + "=? and "
						+ COLUMN_DEVICEID + "=?", new String[] { activeUser,
						deviceID });
	}

	/**
	 * 根据防区的 行和列删除指定的一个防区
	 * 
	 * @param group
	 * @param item
	 * @return
	 */
	public int deleteDefenceAreaByGroupAndItem(String activeUser,
			String deviceID, DefenceArea defenceArea) {
		return myDatabase.delete(
				TABLE_NAME,
				COLUMN_ACTIVE_USER + "=? and " + COLUMN_DEVICEID + "=? and "
						+ COLUMN_GROUP + "=? and " + COLUMN_ITEM + "=?",
				new String[] { activeUser, deviceID,
						String.valueOf(defenceArea.getGroup()),
						String.valueOf(defenceArea.getItem()) });
	}

}
