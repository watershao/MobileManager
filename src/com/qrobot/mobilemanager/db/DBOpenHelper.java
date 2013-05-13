package com.qrobot.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库操作类
 * @author Administrator
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	//	private static final String DBNAME = "/sdcard/qrobot/update/record/update.db";
	private static final String DBNAME = "mm.db";
	private static final int VERSION = 1;
	
	private static final String TABLE_REMINDER = "reminders";
	
	/**
	 * 构造器
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
		db.execSQL(insertReminder+"('开会', 1,'2013-04-03 15:30','今天开会，大家尽量多发表自己的意见','m');");
		db.execSQL(insertReminder+"('购物', 0,'2013-05-03 16:30','今天去购物，然后为假期准备一些东西。','m');");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_REMINDER);
		onCreate(db);
	}
	
	 /** 
     * 清空表中的数据 
     */  
    public void clean (String tableName){  
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS "+tableName);  
        System.out.println("clean删除表");  
        this.onCreate(this.getWritableDatabase());  
        this.getWritableDatabase().close();  
    } 
}