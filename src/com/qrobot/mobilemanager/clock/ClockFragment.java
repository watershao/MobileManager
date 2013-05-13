package com.qrobot.mobilemanager.clock;

import java.util.Calendar;

import com.qrobot.mobilemanager.MainActivity;
import com.qrobot.mobilemanager.R;
import com.qrobot.mobilemanager.netty.NettyClientManager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ClockFragment extends Fragment {
	
	static final String PREFERENCES = "AlarmClock";

	private LinearLayout clockLayout;

	private ListView alarmListView;

	private Context mContext;
	
	private Cursor mCursor;

	private LayoutInflater mFactory;
	
	private LinearLayout mRefreshView;
	
	private NettyClientManager nClientManager;
	
	private ClockSync clockSync;
	
	private static final int MAX_ALARMS = 6;

	public ClockFragment(Context context){
		mContext = context;
	}
	
	public ClockFragment(Context context, NettyClientManager clientManager){
		mContext = context;
		nClientManager = clientManager;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		clockLayout = (LinearLayout)inflater.inflate(R.layout.clock_main, null);
		alarmListView = (ListView) clockLayout.findViewById(R.id.alarms_list);
		
        //ȡ�Զ��岼�ֵ�LayoutInflater
        mFactory = LayoutInflater.from(mContext);
		
		mCursor = Alarms.getAlarmsCursor(mContext.getContentResolver());
		
		clockSync = MainActivity.getClockSyncInstance();
		
		updateLayout();
		
		return clockLayout;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

    /**
     * ���ظ��½��沼��
     */
    private void updateLayout() {
    	
        AlarmTimeAdapter adapter = new AlarmTimeAdapter(mContext, mCursor);
        alarmListView.setAdapter(adapter);
        alarmListView.setVerticalScrollBarEnabled(true);
        alarmListView.setOnItemClickListener(listViewListener);
        alarmListView.setOnCreateContextMenuListener(this);

        View addAlarm = clockLayout.findViewById(R.id.add_alarm);
        addAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                	if (mCursor.getCount() > MAX_ALARMS) {
						Toast.makeText(mContext, "�Բ���������������ӵ����ֵ,�����������ӣ����޸��������ӣ�", Toast.LENGTH_LONG).show();
					}else {
						addNewAlarm();
					}
                }
            });
        // Make the entire view selected when focused.
        addAlarm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
				public void onFocusChange(View v, boolean hasFocus) {
                    v.setSelected(hasFocus);
                }
        });

        mRefreshView = (LinearLayout)clockLayout.findViewById(R.id.refresh_alarm);
        mRefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	mCursor = Alarms.getAlarmsCursor(mContext.getContentResolver());
                AlarmTimeAdapter adapter = new AlarmTimeAdapter(mContext, mCursor);
                alarmListView.setAdapter(adapter);

            	Toast.makeText(mContext, "����ˢ�£����Ժ�", Toast.LENGTH_LONG).show();
            }
        });
	    // Make the entire view selected when focused.
        mRefreshView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	            @Override
				public void onFocusChange(View v, boolean hasFocus) {
	                v.setSelected(hasFocus);
	            }
	    });
        
        View uploadView = (LinearLayout)clockLayout.findViewById(R.id.update_alarm);
        uploadView.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	
            	byte[] clockData = clockSync.uploadLocalData();
            	Log.d("Clock up", nClientManager+"*"+new String(clockData)+"*"+clockData.length);
            	int ret = nClientManager.sendClockData(nClientManager.getRemoteId(), clockData, clockData.length);
            	Log.d("Clock up", "ret*"+ret);

//                addNewAlarm();
            	Toast.makeText(mContext, "�����ϴ������Ժ�", Toast.LENGTH_LONG).show();
            }
        });
	    // Make the entire view selected when focused.
        uploadView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	            @Override
				public void onFocusChange(View v, boolean hasFocus) {
	                v.setSelected(hasFocus);
	            }
	    });
        

    }
	
    
    private void addNewAlarm() {
        startActivity(new Intent(mContext, SetAlarm.class));
    }
    
    
    private OnItemClickListener listViewListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(mContext, SetAlarm.class);
	        intent.putExtra(Alarms.ALARM_ID, (int) arg3);
	        startActivity(intent);
		}
	
    
    };
    
    /**
     * listview���������̳�CursorAdapter
     * Ҳ����ʹ��BaseAdapter
     */
    private class AlarmTimeAdapter extends CursorAdapter {
        public AlarmTimeAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View ret = mFactory.inflate(R.layout.clock_alarm_time, parent, false);

            DigitalClock digitalClock =
                    (DigitalClock) ret.findViewById(R.id.digitalClock);
            digitalClock.setLive(false);
        	
            return ret ;
        }

        //��view��cursor��ÿһ��
        public void bindView(View view, Context context, Cursor cursor) {
            final Alarm alarm = new Alarm(cursor);

            View indicator = view.findViewById(R.id.indicator);

            // Set the initial resource for the bar image.
            final ImageView barOnOff =
                    (ImageView) indicator.findViewById(R.id.bar_onoff);
            barOnOff.setImageResource(alarm.enabled ?
                    R.drawable.ic_indicator_on : R.drawable.ic_indicator_off);

            // Set the initial state of the clock "checkbox"
            final CheckBox clockOnOff =
                    (CheckBox) indicator.findViewById(R.id.clock_onoff);
            clockOnOff.setChecked(alarm.enabled);

            // Clicking outside the "checkbox" should also change the state.
            //��checkbox���ü�����ʹ����һ��
            indicator.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        clockOnOff.toggle();
                        updateIndicatorAndAlarm(clockOnOff.isChecked(),
                                barOnOff, alarm);
                    }
            });

            DigitalClock digitalClock =
                    (DigitalClock) view.findViewById(R.id.digitalClock);

            // set the alarm text
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, alarm.hour);
            c.set(Calendar.MINUTE, alarm.minutes);
            digitalClock.updateTime(c);
            digitalClock.setTypeface(Typeface.DEFAULT);

            // Set the repeat text or leave it blank if it does not repeat.
            TextView daysOfWeekView =
                    (TextView) digitalClock.findViewById(R.id.daysOfWeek);
            final String daysOfWeekStr =
                    alarm.daysOfWeek.toString(mContext, false);
            if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
                daysOfWeekView.setText(daysOfWeekStr);
                daysOfWeekView.setVisibility(View.VISIBLE);
            } else {
                daysOfWeekView.setVisibility(View.GONE);
            }

            // Display the label
            TextView labelView =
                    (TextView) view.findViewById(R.id.label);
            if (alarm.label != null && alarm.label.length() != 0) {
                labelView.setText(alarm.label);
                labelView.setVisibility(View.VISIBLE);
            } else {
                labelView.setVisibility(View.GONE);
            }
        }
    };
    
    /**
     * ����checkbox
     * @param enabled
     * @param bar
     * @param alarm
     */
    private void updateIndicatorAndAlarm(boolean enabled, ImageView bar,
            Alarm alarm) {
        bar.setImageResource(enabled ? R.drawable.ic_indicator_on
                : R.drawable.ic_indicator_off);
        Alarms.enableAlarm(mContext, alarm.id, enabled);
        if (enabled) {
            SetAlarm.popAlarmSetToast(mContext, alarm.hour, alarm.minutes,
                    alarm.daysOfWeek);
        }
    }
    
}
