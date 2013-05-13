package com.qrobot.mobilemanager.clock;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class AlarmNumberPreference extends ListPreference {

    // Initial value that can be set with the values saved in the database.
    private int[] alarmNumbers = {1,2,3,5,10}; 
    // New value that will be set if a positive result comes back from the
    // dialog.
    private int alarmNum = 1;
    
    private String[] numberStr = {"1次","2次","3次","5次","10次"}; 
    
    private int numIndex = 0;
    public AlarmNumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);


        setEntries(numberStr);
        setEntryValues(numberStr);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

        	setSummary(numberStr[numIndex]);
        	callChangeListener(numberStr[numIndex]);
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();

        CharSequence summary= getSummary();
        if (summary != null ) {
        	numIndex = getNumIndex(summary.toString());
		}
        builder.setSingleChoiceItems(entries, numIndex, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alarmNum = alarmNumbers[which];
				numIndex = which;
			}
		});
    }

    public void setAlarmNumber(int alarmNum) {
        this.alarmNum = alarmNum;
        numIndex = getNumIndex(alarmNum);
        setSummary(numberStr[numIndex]);
    }

    public int getAlarmNumber() {
        return alarmNum;
    }
    
    private int getNumIndex(String numStr){
    	if (numberStr != null && numberStr.length > 0 && numStr != null) {
			int index =0;
			for (int i = 0; i < numberStr.length; i++) {
				if (numStr.equalsIgnoreCase(numberStr[i])) {
					index = i;
					return index;
				}
			}
		}
    	return 0;
    }
    
    private int getNumIndex(int number){
    	if (alarmNumbers != null && alarmNumbers.length > 0) {
			int index =0;
			for (int i = 0; i < alarmNumbers.length; i++) {
				if (number==alarmNumbers[i]) {
					index = i;
					return index;
				}
			}
		}
    	return 0;
    }
}
