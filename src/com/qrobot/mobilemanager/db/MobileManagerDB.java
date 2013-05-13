package com.qrobot.mobilemanager.db;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MobileManagerDB {
	private DBOpenHelper openHelper;

	public MobileManagerDB(Context context) {
		openHelper = new DBOpenHelper(context);
	}
	
	/**
	 * 获取没有播放的版本号
	 * @param isRead
	 * @return
	 */
	public List<Integer> getData(boolean isRead){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select newVersion from updatelog where isRead=?", new String[]{String.valueOf(isRead)});
		
		List<Integer> newVersionList = new ArrayList<Integer>();
		
		while(cursor.moveToNext()){
			newVersionList.add(cursor.getInt(cursor.getColumnIndex("isRead")));
		}
		
		cursor.close();
		db.close();
		return newVersionList;
	}
	
	/**
	 * 保存每次更新版本的相关信息
	 * @param moduleID
	 * @param minVersion
	 * @param newVersion
	 * @param isTest
	 * @param releaseDate
	 * @param isRead
	 * @param versionDesc
	 */
	public void save(int moduleID,int minVersion,int newVersion,boolean isTest,
			String releaseDate,boolean isRead, String versionDesc){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		
		try{
			
			db.execSQL("insert into updatelog(moduleID, minVersion,newVersion,isTest," +
					" releaseDate,isRead,versionDesc) values(?,?,?,?,?,?,?)",
						new Object[]{moduleID,minVersion,newVersion,isTest,releaseDate,isRead,versionDesc});
			
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
		
		db.close();
	}
	
	/**
	 * 更新指定版本的播放状态
	 * @param newVersion
	 */
	public void update(int newVersion){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		
		try{
			db.execSQL("update updatelog set isRead=? where newVersion=? ",
						new Object[]{newVersion});
			
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
		
		db.close();
	}
	
	/**
	 * 删除对应更新记录
	 * @param path
	 */
	public void delete(String tableName, int id){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from "+ tableName +" where id=?", new Object[]{id});
		db.close();
	}
}
