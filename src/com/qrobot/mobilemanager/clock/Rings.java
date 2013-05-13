package com.qrobot.mobilemanager.clock;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Rings {


    /**
     * Creates a new Ring and fills in the given Ring's id.
     */
    public static long addRing(Context context, Ring ring) {
        ContentValues values = createContentValues(ring);
        Uri uri = context.getContentResolver().insert(
                Ring.Columns.CONTENT_URI, values);
        ring.id = (int) ContentUris.parseId(uri);


        return 0;
    }

    /**
     * Removes an existing Ring. 
     */
    public static int deleteRing(Context context, int ringId) {
        if (ringId == -1) return -1;

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = ContentUris.withAppendedId(Ring.Columns.CONTENT_URI, ringId);
        int ret = contentResolver.delete(uri, "", null);
        return ret;
    }

    /**
     * Queries all rings
     * @return cursor over all rings
     */
    public static Cursor getRingsCursor(ContentResolver contentResolver) {
        return contentResolver.query(
                Ring.Columns.CONTENT_URI, Ring.Columns.RING_QUERY_COLUMNS,
                null, null, Ring.Columns.DEFAULT_SORT_ORDER);
    }

    private static ContentValues createContentValues(Ring ring) {
        ContentValues values = new ContentValues(2);

        values.put(Ring.Columns.FILE_NAME, ring.filename);
        values.put(Ring.Columns.FILE_PATH, ring.filepath);

        return values;
    }


    /**
     * Return an Ring object representing the Ring id in the database.
     * Returns null if no rings exists.
     */
    public static Ring getRing(ContentResolver contentResolver, int ringId) {
        Cursor cursor = contentResolver.query(
                ContentUris.withAppendedId(Ring.Columns.CONTENT_URI, ringId),
                Ring.Columns.RING_QUERY_COLUMNS,
                null, null, null);
        Ring ring = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ring = new Ring(cursor);
            }
            cursor.close();
        }
        return ring;
    }


    /**
     * A convenience method to set an ring in the rings
     * content provider.
     * @return Time when the ring will fire.
     */
    public static long setRing(Context context, Ring ring) {
        ContentValues values = createContentValues(ring);
        ContentResolver resolver = context.getContentResolver();
        int ret = resolver.update(
		                ContentUris.withAppendedId(Ring.Columns.CONTENT_URI, ring.id),
		                values, null, null);

        return ret;
    }

}
