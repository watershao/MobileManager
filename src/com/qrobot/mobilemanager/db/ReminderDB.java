package com.qrobot.mobilemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qrobot.mobilemanager.reminder.Reminder;

public class ReminderDB {
	private DBOpenHelper openHelper;

	public ReminderDB(Context context) {
		openHelper = new DBOpenHelper(context);
	}
	
	/**
	 * ��ȡ�����α�
	 */
	public Cursor getReminderCursor(){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select _id,name,enabled,stime,content,lable from reminders", null);

		return cursor;
	}
	
	/**
	 * ��ȡ���µ������α�
	 */
	public Cursor getNewReminderCursor(String stime){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select _id,name,enabled,stime,content,lable from reminders where stime > ?", new String[]{stime});

		return cursor;
	}
	
	/**
	 * ��ȡ���ڵ������α�
	 */
	public Cursor getOlderReminderCursor(String stime){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select _id,name,enabled,stime,content,lable from reminders where stime < ?", new String[] {stime});
		return cursor;
	}
	
	/**
	 * ͨ������id��ȡ��������
	 * @param reminderId
	 * @return
	 */
	public Reminder getReminder(int reminderId){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select _id,name,enabled,stime,content,lable from reminders where _id=?", 
								new String[]{String.valueOf(reminderId)});
		if (cursor != null && cursor.moveToFirst()) {
			Reminder reminder = new Reminder(cursor);
			cursor.close();
			return reminder;
		}
		
		return null;
	}
	
	/**
	 * ����������Ϣ
	 * @param reminder
	 */
	public void save(Reminder reminder){
		if (reminder == null) {
			return;
		}
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		
		try{
			
			db.execSQL("insert into reminders(name, enabled,stime,content,lable)" +
					" values(?,?,?,?,?)",
					new Object[]{reminder.name,reminder.enabled,reminder.stime,reminder.content,reminder.lable});
			
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
		
		db.close();
	}
	
	/**
	 * ���¶�Ӧ��������Ϣ
	 * @param reminder
	 */
	public void update(Reminder reminder){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		ContentValues cValues = new ContentValues();
		int  rowId = 0;
		rowId = reminder.id;
		cValues.put(Reminder.Columns.REMINDER_NAME, reminder.name);
		cValues.put(Reminder.Columns.REMINDER_ENABLED, reminder.enabled);
		cValues.put(Reminder.Columns.REMINDER_TIME, reminder.stime);
		cValues.put(Reminder.Columns.REMINDER_CONTENT, reminder.content);
		cValues.put(Reminder.Columns.REMINDER_LABLE, reminder.lable);
		try{
//			db.execSQL("update reminders set isRead=? where newVersion=? ",
//						new Object[]{newVersion});
			
			db.update("reminders", cValues, "_id="+rowId, null);
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
		
		db.close();
	}
	
	/**
	 * ɾ����Ӧ���Ѽ�¼
	 * @param id
	 */
	public void delete(int id){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from reminders where _id=?", new Object[]{id});
		db.close();
	}
	
	/**
	 * ɾ��������������
	 */
	public void deleteAll(){
		openHelper.clean("reminders");
	}
	
	/**
	 * ɾ��������������
	 */
	public void deleteNew(String time){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from reminders where stime>?", new Object[]{time});
		db.close();
	}
}
