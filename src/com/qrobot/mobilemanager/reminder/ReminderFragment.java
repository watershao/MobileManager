package com.qrobot.mobilemanager.reminder;

import com.qrobot.mobilemanager.R;
import com.qrobot.mobilemanager.clock.ClockSync;
import com.qrobot.mobilemanager.db.ReminderDB;
import com.qrobot.mobilemanager.netty.NettyClientManager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class ReminderFragment extends Fragment {
	
	static final String PREFERENCES = "ReminderFragment";

	private LinearLayout reminderLayout;

	private ListView reminderListView;

	private Context mContext;
	
	private Cursor mCursor;

	private LayoutInflater mFactory;
	
	private LinearLayout mRefreshView;
	
	private LinearLayout mUploadView;
	
	private ReminderDB reminderDB;
	
	private NettyClientManager nClientManager;
	
	private ClockSync clockSync;
	
	public ReminderFragment(){
		
	}
	
	public ReminderFragment(Context context){
		mContext = context;
	}
	
	public ReminderFragment(Context context, NettyClientManager nettyClientManager){
		mContext = context;
		nClientManager = nettyClientManager;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		reminderLayout = (LinearLayout)inflater.inflate(R.layout.reminder_main, null);
		reminderListView = (ListView) reminderLayout.findViewById(R.id.reminders_list);
		
        //取自定义布局的LayoutInflater
        mFactory = LayoutInflater.from(mContext);
        
        clockSync = new ClockSync(mContext, nClientManager);

		return reminderLayout;
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
		
        reminderDB = new ReminderDB(mContext);
		mCursor = reminderDB.getReminderCursor();
		
		updateLayout();
		
	}

    /**
     * 加载更新界面布局
     */
    private void updateLayout() {
    	
    	ReminderAdapter reminderAdapter = new ReminderAdapter(mContext, mCursor);
        reminderListView.setAdapter(reminderAdapter);
        reminderListView.setVerticalScrollBarEnabled(true);
        reminderListView.setOnItemClickListener(listViewListener);
        reminderListView.setOnCreateContextMenuListener(this);

        View addAlarm = reminderLayout.findViewById(R.id.add_reminder);
        addAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                    addNewReminder();
//                	Reminders.deleteAll(mContext);
                	
                }
            });
        // Make the entire view selected when focused.
        addAlarm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
				public void onFocusChange(View v, boolean hasFocus) {
                    v.setSelected(hasFocus);
                }
        });

        mRefreshView = (LinearLayout)reminderLayout.findViewById(R.id.refresh_reminder);
        mRefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                reminderDB = new ReminderDB(mContext);
        		mCursor = reminderDB.getReminderCursor();
        		ReminderAdapter adapter = new ReminderAdapter(mContext, mCursor);
        		reminderListView.setAdapter(adapter);
            	
            	Toast.makeText(mContext, "正在刷新，请稍候", Toast.LENGTH_LONG).show();
            }
        });
        
	    // Make the entire view selected when focused.
        mRefreshView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	            @Override
				public void onFocusChange(View v, boolean hasFocus) {
	                v.setSelected(hasFocus);
	            }
	    });
        
        
        mUploadView = (LinearLayout)reminderLayout.findViewById(R.id.upload_reminder);
        mUploadView.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	
            	byte[] clockData = clockSync.uploadLocalData();
            	Log.d("Clock up", nClientManager+"*"+new String(clockData)+"*"+clockData.length);
            	int ret = nClientManager.sendClockData(nClientManager.getRemoteId(), clockData, clockData.length);
            	Log.d("Clock up", "ret*"+ret);

            	Toast.makeText(mContext, "正在上传，请稍候", Toast.LENGTH_LONG).show();
            }
        });
        
	    // Make the entire view selected when focused.
        mUploadView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	            @Override
				public void onFocusChange(View v, boolean hasFocus) {
	                v.setSelected(hasFocus);
	            }
	    });
    }
	
    
    private void addNewReminder() {
        startActivity(new Intent(mContext, SetReminder.class));
    }
    
    
    private OnItemClickListener listViewListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Log.w("ReminderFrag","onItem" +arg3);
			
			Intent intent = new Intent(mContext, SetReminder.class);
	        intent.putExtra(Reminders.REMINDER_ID, (int) arg3);
	        startActivity(intent);
		}
	
    
    };
    
    /**
     * listview的适配器继承CursorAdapter
     * 也可以使用BaseAdapter
     */
    private class ReminderAdapter extends CursorAdapter {
        public ReminderAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View ret = mFactory.inflate(R.layout.reminder_show, parent, false);

            ReminderShow reminderShow =
                    (ReminderShow) ret.findViewById(R.id.reminderShow);
        	
            return ret ;
        }

        /**
         * 把view绑定cursor的每一项
         */
        public void bindView(View view, Context context, Cursor cursor) {
            final Reminder reminder = new Reminder(cursor);
//            Log.w("ReminderFrag", "*"+reminder.id+reminder.name+reminder.stime+reminder.content);
            View indicator = view.findViewById(R.id.indicator);

            // Set the initial resource for the bar image.
            final ImageView barOnOff =
                    (ImageView) indicator.findViewById(R.id.bar_onoff);

            barOnOff.setImageResource(reminder.enabled ?
                    R.drawable.ic_indicator_on : R.drawable.ic_indicator_off);

            // Set the initial state of the clock "checkbox"
            final CheckBox clockOnOff =
                    (CheckBox) indicator.findViewById(R.id.clock_onoff);
            clockOnOff.setChecked(reminder.enabled);

            // Clicking outside the "checkbox" should also change the state.
            //对checkbox设置监听，使里外一致
            indicator.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        clockOnOff.toggle();
                        reminder.enabled = !reminder.enabled;
                        updateIndicatorAndReminder(clockOnOff.isChecked(),
                                barOnOff, reminder);
                    }
            });

            ReminderShow reminderShow =
                    (ReminderShow) view.findViewById(R.id.reminderShow);
            
            // Display the label
            TextView nameView =
                    (TextView) reminderShow.findViewById(R.id.reminder_name);
            if (reminder.name != null && reminder.name.length() != 0) {
                nameView.setText(reminder.name);
                nameView.setVisibility(View.VISIBLE);
            } else {
                nameView.setVisibility(View.GONE);
            }
            
            TextView timeView = (TextView) reminderShow.findViewById(R.id.reminder_time);
            timeView.setText(reminder.stime);
            TextView contentView = (TextView) view.findViewById(R.id.reminder_content);
            contentView.setText(reminder.content);
            
        }
    };
    
    /**
     * 更新checkbox
     * @param enabled
     * @param bar
     * @param alarm
     */
    private void updateIndicatorAndReminder(boolean enabled, ImageView bar,
            Reminder reminder) {
        bar.setImageResource(enabled ? R.drawable.ic_indicator_on
                : R.drawable.ic_indicator_off);
        reminderDB.update(reminder);
        if (enabled) {
        	Toast.makeText(mContext, "您已经开启了提醒", Toast.LENGTH_LONG).show();
        }
    }
    
}
