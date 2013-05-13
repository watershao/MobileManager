package com.qrobot.mobilemanager.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.qrobot.mobilemanager.R;

/**
 * 管理每一个闹钟
 * 每一个闹钟对应的信息都绑定在Preference中了
 */
public class SetReminder extends PreferenceActivity
        implements TimePickerDialog.OnTimeSetListener,
        Preference.OnPreferenceChangeListener {
	
    private EditTextPreference mNamePref;
    private CheckBoxPreference mEnabledPref;
    private Preference mTimePref;
    private Preference mDatePref;
    private EditTextPreference mContentPref;
	
    private int     mId;
    
    private Reminder mOriginalReminder;
    
    private boolean isAdd = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

        // Override the default content view.
        setContentView(R.layout.reminder_set_reminder);

        addPreferencesFromResource(R.xml.reminder_prefs);

        // Get each preference so we can retrieve the value later.
        mNamePref = (EditTextPreference) findPreference("reminder_name");
        mNamePref.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
					public boolean onPreferenceChange(Preference p,
                            Object newValue) {
                        String val = (String) newValue;
                        // Set the summary based on the new label.
                        p.setSummary(val);
                        if (val != null && !val.equals(mNamePref.getText())) {
                            // Call through to the generic listener.
                            return SetReminder.this.onPreferenceChange(p,newValue);
                        }
                        return true;
                    }
                });
        
        mEnabledPref = (CheckBoxPreference) findPreference("reminder_enabled");
        mEnabledPref.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
					public boolean onPreferenceChange(Preference p,
                            Object newValue) {
                        // Pop a toast when enabling alarms.
                        if (!mEnabledPref.isChecked()) {
//                            popAlarmSetToast(SetReminder.this, mHour, mMinutes,
//                                mRepeatPref.getDaysOfWeek());
                        }
                        return SetReminder.this.onPreferenceChange(p, newValue);
                    }
                });
        mTimePref = findPreference("reminder_time");
        
        mDatePref = findPreference("reminder_date");
//        mAlarmPref = (AlarmPreference) findPreference("alarm");
//        mAlarmPref.setOnPreferenceChangeListener(this);
        
        mContentPref = (EditTextPreference) findPreference("reminder_content");
        mContentPref.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
					public boolean onPreferenceChange(Preference p,
                            Object newValue) {
                        String val = (String) newValue;
                        // Set the summary based on the new label.
                        p.setSummary(val);
                        if (val != null && !val.equals(mContentPref.getText())) {
                            // Call through to the generic listener.
                            return SetReminder.this.onPreferenceChange(p,newValue);
                        }
                        return true;
                    }
                });

        Intent i = getIntent();
        mId = i.getIntExtra(Reminders.REMINDER_ID, -1);
        if (true) {
            Log.v("SetReminder", "In SetReminder, reminder id = " + mId);
        }

        Reminder reminder = null;
        if (mId == -1) {
            // No alarm id means create a new reminder.
        	reminder = new Reminder();
        	isAdd = true;
        } else {
            /* load alarm details from database */
            reminder = Reminders.getReminder(getApplicationContext(), mId);
            isAdd = false;
            // Bad reminder, bail to avoid a NPE.
            if (reminder == null) {
                finish();
                return;
            }
        }
        mOriginalReminder = reminder;

        initTime();
        updatePrefs(mOriginalReminder);

        // We have to do this to get the save/cancel buttons to highlight on
        // their own.
        getListView().setItemsCanFocus(true);

        // Attach actions to each button.
        Button save = (Button) findViewById(R.id.reminder_save);
        save.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                    saveReminder();
                    finish();
                }
        });

        Button delete = (Button) findViewById(R.id.reminder_delete);
        if (mId == -1) {
        	delete.setEnabled(false);
        } else {
        	delete.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                    deleteReminder();
                }
            });
        }

        // The last thing we do is pop the time picker if this is a new alarm.
        if (mId == -1) {
            // Assume the user hit cancel
//            mTimePickerCancelled = true;
//            showTimePicker();
        }
    
	}

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mTimePref) {
            showTimePicker();
        }

        if (preference == mDatePref) {
            showDatePicker();
        }
        
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onTimeSet(TimePicker arg0, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
        mHour = hourOfDay;
        mMin = minute;
	    String hourStr = ""+mHour;
	    String minStr = "" + mMin;
	    
	    if (mHour < 10) {
			hourStr = "0"+mHour;
		}
	    if (mMin < 10) {
			minStr = "0"+mMin;
		}
        mTimePref.setSummary(hourStr+":"+minStr);
	}

	private void showTimePicker(){
        new TimePickerDialog(this, this, mHour, mMin,
               DateFormat.is24HourFormat(this)).show();
        
        
	}
	
	private void showDatePicker(){
//		getCurrentTime();
		new DatePickerDialog(this, dateSetListener, mYear, mMonth, mDay).show();
	}
	
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
		    String monthStr = ""+(mMonth+1);
		    String dayStr = "" + mDay;
		    
		    if (mMonth + 1 < 10) {
				monthStr = "0"+(mMonth + 1);
			}
		    if (mDay < 10) {
				dayStr = "0"+mDay;
			}
			mDatePref.setSummary(mYear+"-"+monthStr+"-"+dayStr);
		}
	};
	
	private int mYear,mMonth,mDay,mHour,mMin;
	
	private void getCurrentTime(){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMin = c.get(Calendar.MINUTE);
	}
	
	private void parseTime(String timeStr){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");     
//		Log.w("parseTime:", timeStr);
		try {
			Date mDate = formatter.parse(timeStr);
			mYear = mDate.getYear()+1900;
			mMonth = mDate.getMonth();
			mDay = mDate.getDate();
			mHour = mDate.getHours();
			mMin = mDate.getMinutes();
//			Log.w("parseTime:", ">>"+mYear + "*"+mMonth+"*"+mDay+"*"+mHour+"*"+mMin);
		} catch (ParseException e) {
			e.printStackTrace();
			getCurrentTime();
		}
		
	}
	
	private void initTime(){
		if (isAdd) {
			getCurrentTime();
		} else {
			parseTime(mOriginalReminder.stime);
		}
	}
	
	private void updatePrefs(Reminder reminder) {
	    mId = reminder.id;
	    mEnabledPref.setChecked(reminder.enabled);
	    mNamePref.setText(reminder.name);
	    mNamePref.setSummary(reminder.name);
	    
	    String monthStr = ""+(mMonth+1);
	    String dayStr = "" + mDay;
	    
	    if (mMonth + 1 < 10) {
			monthStr = "0"+(mMonth + 1);
		}
	    if (mDay < 10) {
			dayStr = "0"+mDay;
		}
	    
	    mDatePref.setSummary(mYear+"-"+monthStr+"-"+dayStr);
	    
	    String hourStr = ""+mHour;
	    String minStr = "" + mMin;
	    
	    if (mHour < 10) {
			hourStr = "0"+mHour;
		}
	    if (mMin < 10) {
			minStr = "0"+mMin;
		}
        mTimePref.setSummary(hourStr+":"+minStr);
	    
	    mContentPref.setText(reminder.content);
	    mContentPref.setSummary(reminder.content);
	    
	}
	
    private void saveReminder() {
        Reminder reminder = new Reminder();
        reminder.id = mId;
        reminder.enabled = mEnabledPref.isChecked();
        reminder.name = mNamePref.getText();
        reminder.stime = mDatePref.getSummary().toString()+" "+mTimePref.getSummary().toString();
        reminder.content = mContentPref.getText();

        
        if (reminder.id == -1) {
        	Reminders.addReminder(getApplicationContext(), reminder);
            // addAlarm populates the alarm with the new id. Update mId so that
            // changes to other preferences update the new alarm.
            mId = reminder.id;
        } else {
            Reminders.setReminder(getApplicationContext(), reminder);
        }
    }
    
    private void deleteReminder() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_reminder))
                .setMessage(getString(R.string.delete_reminder_confirm))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
							public void onClick(DialogInterface d, int w) {
                                Reminders.delete(getApplicationContext(), mId);
                                finish();
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
