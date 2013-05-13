package com.qrobot.mobilemanager.clock;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public final class Ring implements Parcelable {

	// ////////////////////////////
	// 序列化的Parcelable接口

	public static final Parcelable.Creator<Ring> CREATOR = new Parcelable.Creator<Ring>() {
		@Override
		public Ring createFromParcel(Parcel p) {
			return new Ring(p);
		}

		@Override
		public Ring[] newArray(int size) {
			return new Ring[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(id);
		p.writeString(filename);
		p.writeString(filepath);
	}

	public static class Columns implements BaseColumns {
		/**
		 * The content:// 为这个表定义一个共享的Url
		 */
		public static final Uri CONTENT_URI = Uri
				.parse("content://com.qrobot.mobilemanager.clock/ring");

		public static final String FILE_NAME = "filename";

		public static final String FILE_PATH = "filepath";


		
		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = FILE_NAME + " ASC";


		static final String[] RING_QUERY_COLUMNS = { _ID, FILE_NAME,FILE_PATH};

		/**
		 * These save calls to cursor.getColumnIndexOrThrow() THEY MUST BE KEPT
		 * IN SYNC WITH ABOVE QUERY COLUMNS
		 */
		public static final int RING_ID_INDEX = 0;
		public static final int RING_FILENAME_INDEX = 1;
		public static final int RING_FILEPATH_INDEX = 2;

		
	}

	// ////////////////////////////
	// End 每一列定义结束
	// ////////////////////////////

	// 对应的公共的每一列的映射
	public int id;
	public String filename;
	public String filepath;


	public Ring(Cursor c) {
		id = c.getInt(Columns.RING_ID_INDEX);
		filename = c.getString(Columns.RING_FILENAME_INDEX);
		filepath = c.getString(Columns.RING_FILEPATH_INDEX);

	}

	public Ring(Parcel p) {
		id = p.readInt();
		filename = p.readString();
		filepath = p.readString();

	}

	// Creates a default alarm at the current time.
	// 创建一个默认当前闹钟ring
	public Ring() {
		id = -1;
		filename = "default";
		filepath = "default";

	}

	public Ring(String ringName) {
		id = -1;
		filename = ringName;
		filepath = ringName;

	}
}
