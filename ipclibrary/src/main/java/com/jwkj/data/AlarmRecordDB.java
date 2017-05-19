package com.jwkj.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import static android.R.attr.data;

public class AlarmRecordDB {
    public static final String TABLE_NAME = "alarm_record";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";

    public static final String COLUMN_DEVICEID = "deviceId";
    public static final String COLUMN_DEVICEID_DATA_TYPE = "varchar";

    public static final String COLUMN_ACTIVE_USER = "activeUser";
    public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";

    public static final String COLUMN_ALARM_TYPE = "alarmType";
    public static final String COLUMN_ALARM_TYPE_DATA_TYPE = "integer";

    public static final String COLUMN_ALARM_TIME = "alarmTime";
    public static final String COLUMN_ALARM_TIME_DATA_TYPE = "varchar";

    public static final String COLUMN_ALARM_GROUP = "alarmGroup";
    public static final String COLUMN_ALARM_GROUP_DATA_TYPE = "integer";

    public static final String COLUMN_ALARM_ITEM = "alarmItem";
    public static final String COLUMN_ALARM_ITEM_DATA_TYPE = "integer";

    public static final String COLUMN_ALARM_PICTRUE = "alarmPictruePath";
    public static final String COLUMN_ALARM_PICTRUE_DATA_TYPE = "varchar";

    public static final String COLUMN_ALARM_SENSOR_NAME = "sensorName";
    public static final String COLUMN_ALARM_SENSOR_NAME_DATA_TYPE = "varchar";

    // --------是否被查看的标记---------
    public static final String COLUMN_ALARM_ISCHECK = "isCheck";
    public static final String COLUMN_ALARM_ISCHECK_DATA_TYPE = "integer";// 0为没有查看，1为查看了


    private SQLiteDatabase myDatabase;

    public AlarmRecordDB(SQLiteDatabase myDatabase) {
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
        columnNameAndType.put(COLUMN_ALARM_TYPE, COLUMN_ALARM_TYPE_DATA_TYPE);
        columnNameAndType.put(COLUMN_ALARM_TIME, COLUMN_ALARM_TIME_DATA_TYPE);
        columnNameAndType.put(COLUMN_ALARM_GROUP, COLUMN_ALARM_GROUP_DATA_TYPE);
        columnNameAndType.put(COLUMN_ALARM_ITEM, COLUMN_ALARM_ITEM_DATA_TYPE);
        columnNameAndType.put(COLUMN_ALARM_PICTRUE, COLUMN_ALARM_PICTRUE_DATA_TYPE);
        columnNameAndType.put(COLUMN_ALARM_SENSOR_NAME, COLUMN_ALARM_SENSOR_NAME_DATA_TYPE);
        //------------------------------
        columnNameAndType.put(COLUMN_ALARM_ISCHECK, COLUMN_ALARM_ISCHECK_DATA_TYPE);
        //------------------------------
        String mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(
                TABLE_NAME, columnNameAndType);
        return mSQLCreateWeiboInfoTable;
    }

    public long insert(AlarmRecord alarmRecord) {
        long isResut = -1;
        if (alarmRecord != null) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DEVICEID, alarmRecord.deviceId);
            values.put(COLUMN_ACTIVE_USER, alarmRecord.activeUser);
            values.put(COLUMN_ALARM_TYPE, alarmRecord.alarmType);
            values.put(COLUMN_ALARM_TIME, alarmRecord.alarmTime);
            values.put(COLUMN_ALARM_GROUP, alarmRecord.group);
            values.put(COLUMN_ALARM_ITEM, alarmRecord.item);
            values.put(COLUMN_ALARM_PICTRUE, alarmRecord.alarmPictruePath);
            values.put(COLUMN_ALARM_SENSOR_NAME, alarmRecord.sensorName);
            //----------------------
            values.put(COLUMN_ALARM_ISCHECK, alarmRecord.isCheck);//默认为未被查看
            //----------------------
            try {
                isResut = myDatabase.insertOrThrow(TABLE_NAME, null, values);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }

        return isResut;
    }

    public void update(AlarmRecord alarmRecord) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEVICEID, alarmRecord.deviceId);
        values.put(COLUMN_ACTIVE_USER, alarmRecord.activeUser);
        values.put(COLUMN_ALARM_TYPE, alarmRecord.alarmType);
        values.put(COLUMN_ALARM_TIME, alarmRecord.alarmTime);
        values.put(COLUMN_ALARM_GROUP, alarmRecord.group);
        values.put(COLUMN_ALARM_ITEM, alarmRecord.item);
        values.put(COLUMN_ALARM_PICTRUE, alarmRecord.alarmPictruePath);
        values.put(COLUMN_ALARM_SENSOR_NAME, alarmRecord.sensorName);
        values.put(COLUMN_ALARM_ISCHECK, alarmRecord.isCheck);
        try {
            myDatabase.update(TABLE_NAME, values, COLUMN_ACTIVE_USER
                    + "=? AND " + COLUMN_DEVICEID + "=? AND " + COLUMN_ALARM_TIME + "=?", new String[]{
                    alarmRecord.activeUser, alarmRecord.deviceId, alarmRecord.alarmTime});
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    public List<AlarmRecord> findByActiveUserId(String activeUserId, int[] num) {
        List<AlarmRecord> lists = new ArrayList<AlarmRecord>();
        Cursor cursor = null;
        cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_ACTIVE_USER + "=? " + "order by " + COLUMN_ALARM_TIME
                + " DESC limit " + num[0] + "," + num[1], new String[]{activeUserId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String deviceId = cursor.getString(cursor
                        .getColumnIndex(COLUMN_DEVICEID));
                int alarmType = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_TYPE));
                String alarmTime = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_TIME));
                String activeUser = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ACTIVE_USER));
                int group = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_GROUP));
                int item = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_ITEM));
                String alarmPictruePath = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_PICTRUE));
                String sensorName = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_SENSOR_NAME));
                int isCheck = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_ISCHECK));
                AlarmRecord data = new AlarmRecord();
                data.id = id;
                data.deviceId = deviceId;
                data.alarmType = alarmType;
                data.alarmTime = alarmTime;
                data.activeUser = activeUser;
                data.group = group;
                data.item = item;
                data.alarmPictruePath = alarmPictruePath;
                data.sensorName = sensorName;
                data.isCheck = isCheck;
                lists.add(data);
            }
            cursor.close();
        }
        return lists;
    }

    public AlarmRecord findByActiveUserIdAndDeviceId(String activeUserId, String deviceId, String alarmTime) {
        AlarmRecord alarmRecord = null;
        Cursor cursor = null;
        cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
                        + COLUMN_ACTIVE_USER + "=? AND " + COLUMN_DEVICEID + "=? AND "
                        + COLUMN_ALARM_TIME + "=?"
                , new String[]{activeUserId, deviceId, alarmTime});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                alarmRecord = new AlarmRecord();
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String alarmId = cursor.getString(cursor
                        .getColumnIndex(COLUMN_DEVICEID));
                int alarmType = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_TYPE));
                String time = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_TIME));
                String activeUser = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ACTIVE_USER));
                int group = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_GROUP));
                int item = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_ITEM));
                String alarmPictruePath = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_PICTRUE));
                String sensorName = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_SENSOR_NAME));
                int isCheck = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_ISCHECK));
                alarmRecord.id = id;
                alarmRecord.deviceId = alarmId;
                alarmRecord.alarmType = alarmType;
                alarmRecord.alarmTime = time;
                alarmRecord.activeUser = activeUser;
                alarmRecord.group = group;
                alarmRecord.item = item;
                alarmRecord.alarmPictruePath = alarmPictruePath;
                alarmRecord.sensorName = sensorName;
                alarmRecord.isCheck = isCheck;
            }
            cursor.close();
        }
        return alarmRecord;
    }

    public int deleteByActiveUser(String activeUserId) {
        return myDatabase.delete(TABLE_NAME, COLUMN_ACTIVE_USER + "=?",
                new String[]{activeUserId});
    }

    public int deleteById(int id) {
        return myDatabase.delete(TABLE_NAME, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
    }
    //查找一定范围内未读的报警记录
    public List<AlarmRecord> findNoCheckByActiveUserId(String activeUserId, int limit) {
        List<AlarmRecord> lists = new ArrayList<>();
        Cursor cursor = null;
        cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_ACTIVE_USER + "=? AND " + COLUMN_ALARM_ISCHECK + "=0"
                + " ORDER BY " + COLUMN_ALARM_TIME + " DESC " + "LIMIT "
                + limit, new String[]{activeUserId});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String deviceId = cursor.getString(cursor
                        .getColumnIndex(COLUMN_DEVICEID));
                int alarmType = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_TYPE));
                String alarmTime = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_TIME));
                String activeUser = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ACTIVE_USER));
                int group = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_GROUP));
                int item = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_ITEM));
                String alarmPictruePath = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_PICTRUE));
                String sensorName = cursor.getString(cursor
                        .getColumnIndex(COLUMN_ALARM_SENSOR_NAME));
                int isCheck = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_ALARM_ISCHECK));
                AlarmRecord data = new AlarmRecord();
                data.id = id;
                data.deviceId = deviceId;
                data.alarmType = alarmType;
                data.alarmTime = alarmTime;
                data.activeUser = activeUser;
                data.group = group;
                data.item = item;
                data.alarmPictruePath = alarmPictruePath;
                data.sensorName = sensorName;
                data.isCheck = isCheck;
                lists.add(data);
            }
        }
        return lists;
    }
}
