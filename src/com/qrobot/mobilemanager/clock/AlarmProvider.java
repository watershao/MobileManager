package com.qrobot.mobilemanager.clock;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AlarmProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    
    private static final int RINGS = 3;
    private static final int RINGS_ID = 4 ;
    private static final UriMatcher sURLMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI("com.qrobot.mobilemanager.clock", "alarm", ALARMS);
        sURLMatcher.addURI("com.qrobot.mobilemanager.clock", "alarm/#", ALARMS_ID);
        
        sURLMatcher.addURI("com.qrobot.mobilemanager.clock", "ring", RINGS);
        sURLMatcher.addURI("com.qrobot.mobilemanager.clock", "ring/#", RINGS_ID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "QrobotAlarms.db";
        private static final int DATABASE_VERSION = 5;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE alarms (" +
                       "_id INTEGER PRIMARY KEY," +
                       "hour INTEGER, " +
                       "minutes INTEGER, " +
                       "daysofweek INTEGER, " +
                       "alarmtime INTEGER, " +
                       "enabled INTEGER, " +
                       "vibrate INTEGER, " +
                       "message TEXT, " +
                       "alert TEXT, " +
                       "number INTEGER, " +
                       "interval INTEGER);");
            
            db.execSQL("CREATE TABLE rings (" +
                       "_id INTEGER PRIMARY KEY," +
                       "filename text, " +
                       "filepath text);");

            // insert default alarms
            String insertMe = "INSERT INTO alarms " +
                    "(hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert,number,interval) " +
                    "VALUES ";
            db.execSQL(insertMe + "(8, 30, 31, 0, 0, 1, '', '/sdcard/qrobot/rings/002.mp3',1,5);");
            db.execSQL(insertMe + "(9, 00, 96, 0, 0, 1, '', '',2,10);");
            
            String insertRing = "INSERT INTO rings"+
            "(filename, filepath) VALUES ";
            
            db.execSQL(insertRing + "(' д╛хо','default');");
            db.execSQL(insertRing + "('002.mp3','002.mp3');");
            db.execSQL(insertRing + "('003.mp3','003.mp3');");
            db.execSQL(insertRing + "('006.mp3','/sdcard/qrobot/rings/006.mp3');");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
            if (true) Log.v("AlarmProvider",
                    "Upgrading alarms database from version " +
                    oldVersion + " to " + currentVersion +
                    ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS alarms");
            db.execSQL("DROP TABLE IF EXISTS rings");
            onCreate(db);
        }
    }

    public AlarmProvider() {
    	
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query
        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                qb.setTables("alarms");
                break;
            case ALARMS_ID:
                qb.setTables("alarms");
                qb.appendWhere("_id=");
                qb.appendWhere(url.getPathSegments().get(1));
                Log.w("AlarmProvider", "pathSegment:"+url.getPathSegments().get(1));
                break;
                
            case RINGS:
            	qb.setTables("rings");
            	break;
            case RINGS_ID:
            	qb.setTables("rings");
            	qb.appendWhere("_id=");
            	qb.appendWhere(url.getPathSegments().get(1));
            	Log.w("AlarmProvider", "ring pathSegment:"+url.getPathSegments().get(1));
            	break;
            	
            default:
                throw new IllegalArgumentException("Unknown URL " + url);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs,
                              null, null, sort);

        if (ret == null) {
            if (true) Log.v("AlarmProvider", "Alarms.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), url);
        }

        return ret;
    }

    @Override
    public String getType(Uri url) {
        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/alarms";
            case ALARMS_ID:
                return "vnd.android.cursor.item/alarms";
            case RINGS:
                return "vnd.android.cursor.dir/rings";
            case RINGS_ID:
                return "vnd.android.cursor.item/rings";    
            default:
                throw new IllegalArgumentException("Unknown URL");
                
        }
    }

    @Override
    public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
        int count;
        long rowId = 0;
        int match = sURLMatcher.match(url);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
            case ALARMS_ID: {
                String segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update("alarms", values, "_id=" + rowId, null);
                break;
            }
            
            case RINGS_ID: {
                String segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update("rings", values, "_id=" + rowId, null);
                break;
            }
            
            default: {
                throw new UnsupportedOperationException(
                        "Cannot update URL: " + url);
            }
        }
        Log.v("AlarmProvider", "*** notifyChange() rowId: " + rowId + " url " + url);
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        if (sURLMatcher.match(url) != ALARMS && sURLMatcher.match(url) != RINGS) {
            throw new IllegalArgumentException("Cannot insert into URL: " + url);
        }

        ContentValues values = new ContentValues(initialValues);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId;
        Uri newUrl = null;
        
        switch (sURLMatcher.match(url)) {
		case ALARMS:
		case ALARMS_ID:	
			rowId = db.insert("alarms", Alarm.Columns.MESSAGE, values);
			if (rowId < 0) {
				throw new SQLException("Failed to insert row into " + url);
			}
			Log.v("AlarmProvider", "Added alarm rowId = " + rowId);
			
			newUrl = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, rowId);
			
			break;
		
		case RINGS:
		case RINGS_ID:
			rowId = db.insert("rings", Ring.Columns.FILE_PATH, values);
			if (rowId < 0) {
				throw new SQLException("Failed to insert row into " + url);
			}
			Log.v("AlarmProvider", "Added ring rowId = " + rowId);
			
			newUrl = ContentUris.withAppendedId(Ring.Columns.CONTENT_URI, rowId);
			
			break;
		default:
			break;
		}
        
        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
    }

    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        long rowId = 0;
        String segment;
        switch (sURLMatcher.match(url)) {
            case ALARMS:
                count = db.delete("alarms", where, whereArgs);
                break;
            case ALARMS_ID:
                segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segment;
                } else {
                    where = "_id=" + segment + " AND (" + where + ")";
                }
                count = db.delete("alarms", where, whereArgs);
                break;
                
            case RINGS:
                count = db.delete("rings", where, whereArgs);
                break;
                
            case RINGS_ID:
                segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segment;
                } else {
                    where = "_id=" + segment + " AND (" + where + ")";
                }
                count = db.delete("rings", where, whereArgs);
                break;  
                
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }
}
