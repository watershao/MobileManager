package com.qrobot.mobilemanager.clock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class RingPreference extends ListPreference {

	String[] fileNames = null;
	
	String currFileName = "";
	
	List<Ring> ringList = new ArrayList<Ring>();
	
	int index = 0;
	
    public RingPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        ringList = getRingsList(getRingsCursor());
        fileNames = getRingNames(ringList);
       
        setEntries(fileNames);
        setEntryValues(fileNames);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

        	setSummary(ringList.get(index).filename);
        	callChangeListener(ringList.get(index).filename);
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();

        CharSequence summary= getSummary();
        if (summary != null ) {
        	index = getIndex(summary.toString());
		}
        
        builder.setSingleChoiceItems(entries, index, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				currFileName = fileNames[which];
				index = which;
			}
		});
    }

   public void setRing(String filename) {
	    if (filename.equalsIgnoreCase("default")) {
	    	currFileName = "д╛хо";
	    }else if (filename.indexOf("/")>-1) {
	    	index = getIndex(filename);
			currFileName = ringList.get(index).filename;
		}else {
			currFileName = filename;
		}
//        Log.w("RingPref", "setRing currName:"+currFileName+"index:"+index);
        setSummary(currFileName);
    }

    public Uri getRing() {
//    	Log.w("RingPref", "currName:"+currFileName);
    	index = getIndex(currFileName);
    	String path = ringList.get(index).filepath;

    	Uri uri = Uri.fromFile(new File(path));
        return uri;
    }
    
    
    private int getIndex(String filename){
    	if (ringList != null && ringList.size() > 0 && filename != null) {
			int index =0;
			for (int i = 0; i < ringList.size(); i++) {
//				Log.w("RingPref", "get index currName:"+filename+"**"+ringList.get(i).filepath);
/*				if (filename.indexOf("/")>-1) {
					String tempName = filename.substring(7, filename.length());
					Log.w("RingPref", "get index /:"+tempName);
					if (tempName.equalsIgnoreCase(ringList.get(i).filepath)) {
						index = i;
						return index;
					}
				}else {
					if (filename.equalsIgnoreCase(ringList.get(i).filename)) {
						index = i;
						return index;
					}
				}*/
				if (filename.contains(ringList.get(i).filename)) {
					index = i;
					return index;
				}
			}
		}
    	return 0;
    }
    
    private Cursor getRingsCursor(){
    	ContentResolver contentResolver = getContext().getContentResolver();
    	Cursor cursor = Rings.getRingsCursor(contentResolver);
    	if (cursor != null && cursor.moveToNext()) {
			return cursor;
		}
    	return null;
    }
    
    private List<Ring> getRingsList(Cursor cursor){
    	if (cursor != null && cursor.moveToFirst()) {
//    		Ring ring = new Ring();
    		List<Ring> rList = new ArrayList<Ring>();
			while (!cursor.isAfterLast()) {
				Ring ring = new Ring();
				ring.filename = cursor.getString(Ring.Columns.RING_FILENAME_INDEX);
				ring.filepath = cursor.getString(Ring.Columns.RING_FILEPATH_INDEX);
				rList.add(ring);
				cursor.moveToNext();
			}
			return rList;
		}
    	return null;
    }
    
    private String[] getRingNames(List<Ring> rList){
    	if (rList != null && rList.size() > 0) {
			String[] ringNames = new String[rList.size()]; 
			String ringName;
			Ring ring = new Ring();
			for (int i = 0; i < rList.size(); i++) {
				ring = rList.get(i);
				ringName = ring.filename;
				ringNames[i] = ringName;
			}
			return ringNames;
		}
    	return null;
    }
}
