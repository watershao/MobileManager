package com.qrobot.mobilemanager.clock;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class AlarmIntervalPreference extends ListPreference {

    // Initial value that can be set with the values saved in the database.
    private int[] alarmIntervals = {3,5,10,15,30}; 

    // New value that will be set if a positive result comes back from the
    // dialog.
    private int alarmInterval = 3;
    
    private String[] intervals = {"3分钟","5分钟","10分钟","15分钟","30分钟"};
    
    private int index = 0;
    
    public AlarmIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

//        String[] values = new String[] {
//            alarmIntervals[0]+"分钟",alarmIntervals[1]+"分钟",alarmIntervals[2]+"分钟",
//            alarmIntervals[3]+"分钟",alarmIntervals[4]+"分钟",
//        };
        setEntries(intervals);
        setEntryValues(intervals);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            
            setSummary(intervals[index]);
            callChangeListener(intervals[index]);
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();

        CharSequence summary= getSummary();
        if (summary != null) {
        	index = getIndex(summary.toString());
		}
        
		builder.setSingleChoiceItems(entries, index, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						alarmInterval = alarmIntervals[which];
						index = which;
					}
				});
    }

    
    public void setAlarmInterval(int alarmInterval) {
        this.alarmInterval = alarmInterval;
        index = getIndex(alarmInterval);
        setSummary(intervals[index]);
    }

    public int getAlarmInterval() {
        return alarmInterval;
    }
    
    private int getIndex(String intervalStr){
    	if (intervals != null && intervals.length > 0 && intervalStr != null) {
			int index =0;
			for (int i = 0; i < intervals.length; i++) {
				if (intervalStr.equalsIgnoreCase(intervals[i])) {
					index = i;
					return index;
				}
			}
		}
    	return 0;
    }
    
    private int getIndex(int ind){
    	if (intervals != null && intervals.length > 0) {
			int index =0;
			for (int i = 0; i < intervals.length; i++) {
				if (ind==alarmIntervals[i]) {
					index = i;
					return index;
				}
			}
		}
    	return 0;
    }
}
