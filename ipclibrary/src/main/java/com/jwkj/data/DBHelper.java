package com.jwkj.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webkit.WebChromeClient.CustomViewCallback;

import com.jwkj.entity.Account;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants;
import com.jwkj.global.MyApp;
import com.jwkj.global.NpcCommon;
import com.p2p.core.P2PValue;

public class DBHelper extends SQLiteOpenHelper {
	private SQLiteDatabase mDB = null;
	private Context context;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		if (null == mDB) {
			mDB = db;
		}

		try {
			db.execSQL(MessageDB.getCreateTableString());
		} catch (Exception e) {
			Log.e("my", "db existed");
		}

		try {
			db.execSQL(SysMessageDB.getCreateTableString());
		} catch (Exception e) {
			Log.e("my", "db existed");
		}

		try {
			db.execSQL(AlarmMaskDB.getCreateTableString());
		} catch (Exception e) {
			Log.e("my", "db existed");
		}

		try {
			db.execSQL(AlarmRecordDB.getCreateTableString());
		} catch (Exception e) {
			Log.e("my", "db existed");
		}

		try {
			db.execSQL(NearlyTellDB.getCreateTableString());
		} catch (Exception e) {
			Log.e("my", "db existed");
		}

		try {
			db.execSQL(ContactDB.getCreateTableString());
		} catch (Exception e) {
			Log.e("my", "db existed");
		}
		try {
			db.execSQL(APContactDB.getCreateTableString());
		} catch (Exception e) {
			Log.e("my", "db existed");
		}
		try {
			db.execSQL(JAContactDB.getCreateTableString());
		} catch (Exception e) {
			Log.e("my", "JAContactDB db existed");
		}
		try{
			db.execSQL(SystemMessageDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		try{
			db.execSQL(IsLoginDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		try{
			db.execSQL(PrepointDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","PrepointDB existed");
		}
		try{
			db.execSQL(DefenceAreaDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","defenceAreaDB existed");
		}


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 10) {
			String recentName = SharedPreferencesManager.getInstance().getData(
					MyApp.app, SharedPreferencesManager.SP_FILE_GWELL,
					SharedPreferencesManager.KEY_RECENTNAME);
			if (!recentName.equals("")) {
				if (recentName.charAt(0) != '0') {
					SharedPreferencesManager.getInstance().putData(MyApp.app,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTNAME_EMAIL,
							"0" + recentName);
				} else {
					SharedPreferencesManager.getInstance().putData(MyApp.app,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTNAME_EMAIL,
							recentName);
				}
			}

			SharedPreferencesManager.getInstance().putData(MyApp.app,
					SharedPreferencesManager.SP_FILE_GWELL,
					SharedPreferencesManager.KEY_RECENTNAME, "");
			SharedPreferencesManager.getInstance().putRecentLoginType(
					MyApp.app, Constants.LoginType.EMAIL);

		}

		if (oldVersion < 13) {
			Account account = AccountPersist.getInstance()
					.getActiveAccountInfo(MyApp.app);
			if (null != account) {
				account.three_number = "0" + account.three_number;
				AccountPersist.getInstance().setActiveAccount(MyApp.app,
						account);
				NpcCommon.mThreeNum = AccountPersist.getInstance()
						.getActiveAccountInfo(MyApp.app).three_number;
			}
		}

		if (oldVersion < 21) {
			db.execSQL("DROP TABLE IF EXISTS message");
			db.execSQL(MessageDB.getCreateTableString());

			db.execSQL("DROP TABLE IF EXISTS sysMsg");
			db.execSQL(SysMessageDB.getCreateTableString());

			db.execSQL("DROP TABLE IF EXISTS allarm_mask");
			db.execSQL(AlarmMaskDB.getCreateTableString());

			db.execSQL("DROP TABLE IF EXISTS alarm_record");
			db.execSQL(AlarmRecordDB.getCreateTableString());

			db.execSQL("DROP TABLE IF EXISTS nearly_tell");
			db.execSQL(NearlyTellDB.getCreateTableString());

			Cursor cursor = db.rawQuery("SELECT * FROM contant_friends", null);
			List<Contact> lists = new ArrayList<Contact>();
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String contactName = cursor.getString(cursor
							.getColumnIndex("name"));
					String contactId = cursor.getString(cursor
							.getColumnIndex("threeAccount"));
					String contactPassword = cursor.getString(cursor
							.getColumnIndex("threePwd"));
					int contactType = cursor.getInt(cursor
							.getColumnIndex("device_type"));
					int messageCount = cursor.getInt(cursor
							.getColumnIndex("msgCount"));
					String activeUser = cursor.getString(cursor
							.getColumnIndex("uId"));
					Contact data = new Contact();
					data.contactName = contactName;
					data.contactId = contactId;
					data.contactPassword = contactPassword;
					data.contactType = contactType;
					data.messageCount = messageCount;
					if (activeUser.charAt(0) != '0') {
						activeUser = "0" + activeUser;
					}
					data.activeUser = activeUser;
					lists.add(data);
				}
				cursor.close();
			}
			db.execSQL("DROP TABLE IF EXISTS contant_friends");
			db.execSQL(ContactDB.getCreateTableString());

			for (Contact contact : lists) {
				ContactDB contactDB = new ContactDB(db);
				contactDB.insert(contact);
			}
			
		}

		if (oldVersion < 22) {
			List<AlarmRecord> lists = new ArrayList<AlarmRecord>();
			Cursor cursor = db.rawQuery("SELECT * FROM alarm_record", null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String deviceId = cursor.getString(cursor
							.getColumnIndex("deviceId"));
					int alarmType = cursor.getInt(cursor
							.getColumnIndex("alarmType"));
					String alarmTime = cursor.getString(cursor
							.getColumnIndex("alarmTime"));
					String activeUser = cursor.getString(cursor
							.getColumnIndex("activeUser"));

					AlarmRecord data = new AlarmRecord();
					data.deviceId = deviceId;
					data.alarmType = alarmType;
					data.alarmTime = alarmTime;
					data.activeUser = activeUser;
					data.group = -1;
					data.item = -1;
					lists.add(data);
				}
				cursor.close();
			}

			db.execSQL("DROP TABLE IF EXISTS alarm_record");
			db.execSQL(AlarmRecordDB.getCreateTableString());
			for (AlarmRecord record : lists) {

				AlarmRecordDB alarmRecordDB = new AlarmRecordDB(db);
				alarmRecordDB.insert(record);
			}

		}
		if(oldVersion<24){
		    try  
		    {  
		    	db.execSQL("ALTER TABLE contact ADD COLUMN userPwd default '123'");
		    }  
		    catch(Exception e){
		       
		    }
		    List<Contact> lists = new ArrayList<Contact>();
		    	  Cursor cursor = db.rawQuery("SELECT * FROM contact", null);
					if (cursor != null) {
						while (cursor.moveToNext()) {
							String contactName = cursor.getString(cursor
									.getColumnIndex("contactName"));
							String contactId = cursor.getString(cursor
									.getColumnIndex("contactId"));
							String contactPassword = cursor.getString(cursor
									.getColumnIndex("contactPassword"));
							int contactType = cursor.getInt(cursor
									.getColumnIndex("contactType"));
							int messageCount = cursor.getInt(cursor
									.getColumnIndex("messageCount"));
							String activeUser = cursor.getString(cursor
									.getColumnIndex("activeUser"));
							Contact data = new Contact();
							data.contactName = contactName;
							data.contactId = contactId;
							data.contactPassword = contactPassword;
							data.contactType = contactType;
							data.messageCount = messageCount;
							data.userPassword=contactPassword;
							if (activeUser.charAt(0) != '0') {
								activeUser = "0" + activeUser;
							}
							data.activeUser = activeUser;
							lists.add(data);
						}
						cursor.close();
					}
		        //设置事务标志为成功，当结束事务时就会提交事务  
		 
			for (Contact contact : lists) {
			    try  
			    {  
			    	ContentValues values = new ContentValues();
					values.put("contactName", contact.contactName);
					values.put("contactId", contact.contactId);
					values.put("contactPassword", contact.contactPassword);
					values.put("contactType", contact.contactType);
					values.put("messageCount", contact.messageCount);
					values.put("activeUser", contact.activeUser);
					values.put("userPwd", contact.userPassword);
					try {
						 db.update("contact", values, "activeUser"
									+ "=? AND " + "contactId" + "=?", new String[] {
									contact.activeUser, contact.contactId});
					} catch (SQLiteConstraintException e) {
						e.printStackTrace();
					}
			    }  
			    catch(Exception e){
			       
			    }
			}
		}
		if(oldVersion <26){
		    try  
		    {  
		    	db.execSQL("ALTER TABLE contact ADD COLUMN subType default '0'");
		    }  
		    catch(Exception e){
		       
		    }
	   }
	   if(oldVersion<29){
		   try  
		    {  
		    	db.execSQL("ALTER TABLE alarm_record ADD COLUMN alarmPictruePath default ''");
		    	db.execSQL("ALTER TABLE alarm_record ADD COLUMN sensorName default ''");
		    }  
		    catch(Exception e){
		       
		    }
	   }
	   if(oldVersion<30){
		   try  
		    {  
		    	db.execSQL("ALTER TABLE contact ADD COLUMN videow default '896'");
		    	db.execSQL("ALTER TABLE contact ADD COLUMN videoh default '896'");
		    	db.execSQL("ALTER TABLE contact ADD COLUMN fishpos default '0'");
		    }  
		    catch(Exception e){
		       
		    } 
	   }
        if (oldVersion<31){
            try {
                db.execSQL("ALTER TABLE alarm_record ADD COLUMN isCheck default 1");
            }catch (Exception e){

            }
        }
		onCreate(db);
	}
}
