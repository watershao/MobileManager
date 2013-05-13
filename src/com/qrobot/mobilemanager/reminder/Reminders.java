package com.qrobot.mobilemanager.reminder;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.qrobot.mobilemanager.db.ReminderDB;

import android.content.Context;
import android.database.Cursor;

public class Reminders {

    // This string is used to identify the alarm id passed to SetAlarm from the
    // list of alarms.
    public static final String REMINDER_ID = "reminder_id";
    
    public static void addReminder(Context context , Reminder reminder){
    	ReminderDB reminderDB = new ReminderDB(context);
    	reminderDB.save(reminder);
    	
    }

    public static void setReminder(Context context, Reminder reminder){
    	ReminderDB reminderDB = new ReminderDB(context);
    	reminderDB.update(reminder);
    	
    }
    
    public static void delete(Context context, int reminderId){
    	ReminderDB reminderDB = new ReminderDB(context);
    	reminderDB.delete(reminderId);
    }
    
    public static void deleteAll(Context context){
    	ReminderDB reminderDB = new ReminderDB(context);
    	reminderDB.deleteAll();
    }
    
    public static void deleteNew(Context context,String time){
    	ReminderDB reminderDB = new ReminderDB(context);
    	reminderDB.deleteNew(time);
    }
    
    public static Cursor getReminderCursor(Context context){
    	ReminderDB reminderDB = new ReminderDB(context);
    	Cursor cursor = reminderDB.getReminderCursor();
    	return cursor;
    }
    
    public static Reminder getReminder(Context context , int reminderId){
    	ReminderDB reminderDB = new ReminderDB(context);
    	Reminder reminder = reminderDB.getReminder(reminderId);
    	return reminder;
    }
    
    public static Cursor getNewReminderCursor(Context context,String stime){
    	ReminderDB reminderDB = new ReminderDB(context);
    	Cursor cursor = reminderDB.getNewReminderCursor(stime);
    	return cursor;
    }
    
    public static Cursor getOldReminderCursor(Context context,String stime){
    	ReminderDB reminderDB = new ReminderDB(context);
    	Cursor cursor = reminderDB.getOlderReminderCursor(stime);
    	return cursor;
    }
    
	private String formatTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");     
		Date  curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String time = formatter.format(curDate);  
		return time;
	}
}
