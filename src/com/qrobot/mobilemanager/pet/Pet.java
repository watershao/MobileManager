package com.qrobot.mobilemanager.pet;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public final class Pet implements Parcelable {

	// ////////////////////////////
	// 序列化的Parcelable接口

	public static final Parcelable.Creator<Pet> CREATOR = new Parcelable.Creator<Pet>() {
		@Override
		public Pet createFromParcel(Parcel p) {
			return new Pet(p);
		}

		@Override
		public Pet[] newArray(int size) {
			return new Pet[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(id);
		p.writeString(nickname);
		p.writeString(portrait);
	}

	public static class Columns implements BaseColumns {
		
		public static final String PET_NICKNAME = "nickname";

		public static final String PET_PORTRAIT = "portrait";

		
		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = PET_NICKNAME + " ASC";


		static final String[] RING_QUERY_COLUMNS = { _ID, PET_NICKNAME,PET_PORTRAIT};

		/**
		 * These save calls to cursor.getColumnIndexOrThrow() THEY MUST BE KEPT
		 * IN SYNC WITH ABOVE QUERY COLUMNS
		 */
		public static final int PET_ID_INDEX = 0;
		public static final int PET_NICKNAME_INDEX = 1;
		public static final int PET_PORTRAIT_INDEX = 2;
		
	}

	// End 每一列定义结束

	// 对应的公共的每一列的映射
	public int id;
	public String nickname;
	public String portrait;

	public Pet(Cursor c) {
		id = c.getInt(Columns.PET_ID_INDEX);
		nickname = c.getString(Columns.PET_NICKNAME_INDEX);
		portrait = c.getString(Columns.PET_PORTRAIT_INDEX);

	}

	public Pet(Parcel p) {
		id = p.readInt();
		nickname = p.readString();
		portrait = p.readString();
	}

	// Creates a default alarm at the current time.
	// 创建一个默认属性
	public Pet() {
		id = -1;
		nickname="xiao Q";
		portrait = "default";

	}

}
