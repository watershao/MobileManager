package com.qrobot.mobilemanager.reminder;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public final class Reminder implements Parcelable {

	// ////////////////////////////
	// 序列化的Parcelable接口

	public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>() {
		@Override
		public Reminder createFromParcel(Parcel p) {
			return new Reminder(p);
		}

		@Override
		public Reminder[] newArray(int size) {
			return new Reminder[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(id);
		p.writeString(name);
		p.writeInt(enabled?1:0);
		p.writeString(stime);
		p.writeString(content);
		p.writeString(lable);
	}

	public static class Columns implements BaseColumns {
		/**
		 * The content:// 为这个表定义一个共享的Url
		
		public static final Uri CONTENT_URI = Uri
				.parse("content://com.qrobot.mobilemanager.clock/ring");
 */
		public static final String REMINDER_NAME = "name";

		public static final String REMINDER_LABLE = "lable";
		
		public static final String REMINDER_ENABLED = "enabled";

		public static final String REMINDER_TIME = "stime";

		public static final String REMINDER_CONTENT = "content";
		
		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = REMINDER_TIME + " ASC";


		static final String[] RING_QUERY_COLUMNS = { _ID, REMINDER_NAME,REMINDER_LABLE,REMINDER_ENABLED,REMINDER_TIME,REMINDER_CONTENT};

		/**
		 * These save calls to cursor.getColumnIndexOrThrow() THEY MUST BE KEPT
		 * IN SYNC WITH ABOVE QUERY COLUMNS
		 */
		public static final int REMINDER_ID_INDEX = 0;
		public static final int REMINDER_NAME_INDEX = 1;
		public static final int REMINDER_ENABLED_INDEX = 2;
		public static final int REMINDER_TIME_INDEX = 3;
		public static final int REMINDER_CONTENT_INDEX = 4;
		public static final int REMINDER_LABLE_INDEX = 5;
		
	}

	// End 每一列定义结束

	// 对应的公共的每一列的映射
	public int id;
	public String name;
	public boolean enabled;
	public String stime;
	public String content;
	public String lable;

	public Reminder(Cursor c) {
		id = c.getInt(Columns.REMINDER_ID_INDEX);
		name = c.getString(Columns.REMINDER_NAME_INDEX);
		enabled = c.getInt(Columns.REMINDER_ENABLED_INDEX) == 1;
		stime = c.getString(Columns.REMINDER_TIME_INDEX);
		content = c.getString(Columns.REMINDER_CONTENT_INDEX);
		lable = c.getString(Columns.REMINDER_LABLE_INDEX);
	}

	public Reminder(Parcel p) {
		id = p.readInt();
		name = p.readString();
		enabled = p.readInt() == 1;
		stime = p.readString();
		content = p.readString();
		lable = p.readString();
	}

	// Creates a default alarm at the current time.
	// 创建一个默认当前闹钟ring
	public Reminder() {
		id = -1;
		name="开会";
		enabled = true;
		stime = formatTime();  
//		stime = "2013-04-03 15:00";
		content = "今天开会";
		lable = "m";
	}

	private String formatTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");     
		Date  curDate = new Date(System.currentTimeMillis());//获取当前时间     
		String time = formatter.format(curDate);  
		return time;
	}
}
