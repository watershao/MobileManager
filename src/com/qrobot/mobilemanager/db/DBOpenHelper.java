package com.qrobot.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ���ݿ������
 * @author Administrator
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	//	private static final String DBNAME = "/sdcard/qrobot/update/record/update.db";
	private static final String DBNAME = "mm.db";
	private static final int VERSION = 1;
	
	private static final String TABLE_REMINDER = "reminders";
	
	/**
	 * ������
	 * @param context
	 */
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_REMINDER+" (_id integer primary key autoincrement, " +
				"name TEXT, enabled INTEGER, stime char(50)," +
				" content TEXT,lable char(10));");
		
		String insertReminder = "insert into reminders (name,enabled, stime, content,lable) values ";
		db.execSQL(insertReminder+"('����', 1,'2013-04-03 15:30','���쿪�ᣬ��Ҿ����෢���Լ������','m');");
		db.execSQL(insertReminder+"('����', 0,'2013-05-03 16:30','����ȥ���Ȼ��Ϊ����׼��һЩ������','m');");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_REMINDER);
		onCreate(db);
	}
	
	 /** 
     * ��ձ��е����� 
     */  
    public void clean (String tableName){  
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS "+tableName);  
        System.out.println("cleanɾ����");  
        this.onCreate(this.getWritableDatabase());  
        this.getWritableDatabase().close();  
    } 
}