package com.qrobot.mobilemanager.clock;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.qrobot.mobilemanager.clock.Alarm.DaysOfWeek;
import com.qrobot.mobilemanager.datalistener.ClockDataListener;
import com.qrobot.mobilemanager.netty.NettyClientManager;
import com.qrobot.mobilemanager.reminder.Reminder;
import com.qrobot.mobilemanager.reminder.Reminders;

public class ClockSync {
/*	
	{
		"f":[{"n":"a.mp3"}, {"n":"b.mp3}],
		"am":[{"i":"id1", "n":"alarm", "s":"0", "h":"17", "m":"15", "rf":"a.mp3", "wk":"4", "cs":"2", "it":"5"},{}],
		"sc":[{"i":"id1", "n":"schedule", "s":"0", "tm":"2013-08-14 17:15", "t":"开会时间到了"},{}]
		}*/


	private Context mContext;
	
	private NettyClientManager mClientManager;
	
	private static final String TAG = "ClockSync";
	
	public ClockSync(){
		
	}
	
	public ClockSync(Context context, NettyClientManager clientManager){
		mContext = context;
		mClientManager = clientManager;
		mClientManager.setClockDataListener(clockDataListener);
	}
	
	
	/**
	 * 获取本地的alarm json数组
	 * @param aList
	 * @return
	 */
	private JSONArray getLocalAlarms(){
		Cursor cursor = Alarms.getAlarmsCursor(mContext.getContentResolver());
		List<Alarm> aList = new ArrayList<Alarm>();
		if (cursor != null && cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Alarm alarm = new Alarm(cursor);
				aList.add(alarm);
				cursor.moveToNext();
			}
		}
		
		if (aList != null && aList.size() > 0) {
			try {
				JSONArray alarmArray = new JSONArray();
				for (int i = 0; i < aList.size(); i++) {
					Alarm alarm = aList.get(i);
					JSONObject alarmObject = new JSONObject();
					alarmObject.put("i", alarm.id);
					alarmObject.put("n", alarm.label);
					alarmObject.put("s", alarm.enabled?1:0);
					alarmObject.put("h", alarm.hour);
					alarmObject.put("m", alarm.minutes);
					String rf = alarm.alert.toString();
					if (rf.contains("file")) {
						rf = alarm.alert.toString().substring(8);
					}
					alarmObject.put("rf", rf);
					alarmObject.put("wk", alarm.daysOfWeek.getCoded());
					alarmObject.put("cs", alarm.number);
					alarmObject.put("it", alarm.interval);
					
					Log.w("alarm json", alarmObject.toString());
					alarmArray.put(i,alarmObject);
				}
				return alarmArray;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
	
	/**
	 * 获取服务端的闹铃数据
	 * @param alarmJson
	 * @return
	 */
	private List<Alarm> getServerAlarms(JSONArray alarmArray){
		try {
			
			if (alarmArray != null && alarmArray.length() > 0) {
				List<Alarm> aList = new ArrayList<Alarm>();
				for (int i = 0; i < alarmArray.length(); i++) {
					JSONObject aObject = alarmArray.getJSONObject(i);
					Alarm alarm = new Alarm();
					alarm.id = aObject.getInt("i");
					alarm.label = aObject.getString("n");
					alarm.enabled = aObject.getInt("s")==1;
					alarm.hour = aObject.getInt("h");
					alarm.minutes = aObject.getInt("m");
					alarm.alert = Uri.parse(aObject.getString("rf"));
					alarm.daysOfWeek = new DaysOfWeek(Integer.parseInt(aObject.getString("wk")));
					alarm.number = aObject.getInt("cs");
					alarm.interval = aObject.getInt("it");
					aList.add(alarm);
					
				}
				
				return aList;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
	
	/**
	 * 保存服务端的数据到本地
	 * @param alarmList
	 */
	private void saveAlarms(List<Alarm> alarmList){
		if (alarmList != null && alarmList.size() > 0) {
			int delRows = mContext.getContentResolver().delete(Alarm.Columns.CONTENT_URI, "", null);
			
			for (int i = 0; i < alarmList.size(); i++) {
				Alarm alarm = alarmList.get(i);
				long add = Alarms.addAlarm(mContext, alarm);
			}
		}
	}
	
	/**
	 * 保存铃声到本地
	 * @param ringJArray
	 */
	private void saveRings(JSONArray ringJArray){
		if (ringJArray != null && ringJArray.length() > 0) {
			int delRows = mContext.getContentResolver().delete(Ring.Columns.CONTENT_URI, "", null);
			try {
				
				for (int i = 0; i < ringJArray.length(); i++) {
					JSONObject rObject = ringJArray.getJSONObject(i);
					String ringName = rObject.getString("n");
					Ring ring = new Ring(ringName);
					long add = Rings.addRing(mContext, ring);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	/**
	 * 将提醒数组转为列表
	 * @param reminderArray
	 * @return
	 */
	private List<Reminder> getServerReminders(JSONArray reminderArray){
		if (reminderArray != null && reminderArray.length() > 0) {
			List<Reminder> rList = new ArrayList<Reminder>();
			try {
				
				for (int i = 0; i < reminderArray.length(); i++) {
					JSONObject rObject = reminderArray.getJSONObject(i);
					Reminder reminder = new Reminder();
					String lable = rObject.getString("i");
					reminder.lable = lable;
					if (lable.contains("m")) {
						reminder.id = Integer.parseInt(lable.substring(1));
					} else {
						reminder.id = Integer.parseInt(lable.substring(4));
					}
					reminder.name = rObject.getString("n");
					reminder.enabled = rObject.getInt("s")==1;
					reminder.stime = rObject.getString("tm");
					reminder.content = rObject.getString("t");
					Log.d(TAG, "save reminder 123:"+reminder.id+
							reminder.name+reminder.enabled+reminder.stime
							+reminder.content+reminder.lable);
					rList.add(reminder);
				}
			
				return rList;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
	/**
	 * 保存提醒数据到本地
	 * @param reminderArray
	 */
	private void saveReminder(JSONArray reminderArray){
		if (reminderArray != null && reminderArray.length() > 0) {
//			Reminders.deleteAll(mContext);
			deleteNewReminders();
			List<Reminder> rList = getServerReminders(reminderArray);
			for (int i = 0; i < rList.size(); i++) {
				Reminder reminder = rList.get(i);
				Reminders.addReminder(mContext, reminder);
				
			}
		}
	}
	
	/**
	 * 获取本地的提醒数组
	 * @return
	 */
	private JSONArray getLocalReminders(){
//		ReminderDB rDb = new ReminderDB(mContext);
//		Cursor cursor = rDb.getReminderCursor();
		Cursor cursor = Reminders.getNewReminderCursor(mContext, formatTime());
		List<Reminder> rList = new ArrayList<Reminder>();
		JSONArray rArray = new JSONArray();
		if (cursor != null && cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Reminder reminder = new Reminder(cursor);
				rList.add(reminder);
				
				cursor.moveToNext();
			}
			cursor.close();
		}
		
		try {
			if (rList != null && rList.size() > 0) {
				for (int i = 0; i <rList.size(); i++) {
					Reminder reminder = rList.get(i);
					JSONObject rObject = new JSONObject();
					if (reminder.lable.equalsIgnoreCase("m")) {
						rObject.put("i", "m"+reminder.id);
					}else {
						rObject.put("i", reminder.lable);
					}
					rObject.put("s", reminder.enabled?1:0);
					rObject.put("n", reminder.name);
					rObject.put("tm", reminder.stime);
					rObject.put("t", reminder.content);
					rArray.put(rObject);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return rArray;
	}
	
	private void getOldReminders(){
		String now = formatTime();
		Cursor oldCursor = Reminders.getOldReminderCursor(mContext, now);
		
		if (oldCursor != null && oldCursor.moveToFirst()) {
			while (!oldCursor.isAfterLast()) {
				Reminder reminder = new Reminder(oldCursor);
				Log.d(TAG, "old reminder:"+reminder.id+reminder.stime+reminder.content+reminder.name);
				
				oldCursor.moveToNext();
			}
		}
		
	}
	
	
	private void getNewReminders(){
		String now = formatTime();
		Cursor newCursor = Reminders.getNewReminderCursor(mContext, now);
		
		if (newCursor != null && newCursor.moveToFirst()) {
			while (!newCursor.isAfterLast()) {
				Reminder reminder = new Reminder(newCursor);
				Log.d(TAG, "new reminder:"+reminder.id+reminder.stime+reminder.content+reminder.name);
				
				newCursor.moveToNext();
			}
		}
	}
	
	private void deleteNewReminders(){
		String now = formatTime();
		Reminders.deleteNew(mContext, now);
		
	}
	
	private String formatTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");     
		Date  curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String time = formatter.format(curDate);  
		return time;
	}
	
	/**
	 * 下载处理服务器数据
	 * @param serverData
	 */
	public void downServerData(byte[] serverData){
		try {
			
			String serverJson = new String(serverData);
			Log.d(TAG, "*"+serverJson.toString());
			JSONObject jObject = new JSONObject(serverJson.toString());
			if (jObject != null && jObject.length() > 0) {
				JSONArray ringArray = jObject.getJSONArray("f");
				JSONArray alarmArray = jObject.getJSONArray("am");
				JSONArray reminderArray = jObject.getJSONArray("sc");
				
				saveRings(ringArray);
				saveAlarms(getServerAlarms(alarmArray));
				saveReminder(reminderArray);
			}
		
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传本地数据到服务器
	 * @return
	 */
	public byte[] uploadLocalData(){
		JSONArray ringArray = new JSONArray();
		JSONArray alarmArray = getLocalAlarms();
		JSONArray reminderArray = getLocalReminders();
		try {
			
			JSONObject upJsonObject = new JSONObject();
			upJsonObject.put("f", ringArray);
			upJsonObject.put("am", alarmArray);
			upJsonObject.put("sc", reminderArray);
			Log.d(TAG, "upload:"+upJsonObject.toString());
			byte[] bytes = upJsonObject.toString().getBytes("utf-8");

			return bytes;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
			
		}
//		return null;
	}
	
	
	private ClockDataListener clockDataListener = new ClockDataListener() {
		
		@Override
		public void OnClockDataListener(Bundle bundle) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void OnClockDataListener(int robotNo, byte[] data, int dataSize) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onClockDataListener*"+new String(data));
			
			downServerData(data);
		}
	};
}
