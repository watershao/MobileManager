package com.qrobot.mobilemanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.animation.Interpolator;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.qrobot.mobilemanager.bt.BluetoothService;
import com.qrobot.mobilemanager.clock.ClockSync;
import com.qrobot.mobilemanager.netty.NettyClientManager;
import com.qrobot.mobilemanager.util.Util;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected ListFragment mFrag;

	private static BluetoothService btService;
	
	private final static String BLUETOOTH_ACTION = "com.qrobot.mobilemanager.bt.BluetoothService";
	
	protected static Context mContext;
	
	/**
	 * netty client manager
	 */
	protected static NettyClientManager nClientManager;
	
	/**
	 * ÊÇ·ñµÇÂ½³É¹¦
	 */
	protected static volatile boolean login = false;
	
	private static ClockSync clockSync;
	
	public static ClockSync getClockSyncInstance(){
		if (clockSync != null) {
			return clockSync;
		}
		clockSync = new ClockSync(mContext, nClientManager);
		return clockSync;
	}
	
	public static void setLogin(boolean login){
		BaseActivity.login = login;
	}
	
	public static boolean getLogin(){
		return login;
	}
	
	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initService();
//		mContext = getApplicationContext();
		
		mContext = BaseActivity.this;
		nClientManager = new NettyClientManager(mContext);
		
		registerDataReceiver();
		
		nClientManager.bindService();
		
		clockSync = new ClockSync(mContext, nClientManager);
		
		setTitle(mTitleRes);

		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
		mFrag = new MenuListFragment(mContext, btService,nClientManager);
		t.replace(R.id.menu_frame, mFrag);
		t.commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		//fold animation
		setSlidingActionBarEnabled(true);//title bar slide
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(scaleTransformer);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//second menu
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);

		//slide up
//		setSlidingActionBarEnabled(true);
//		getSlidingMenu().setBehindScrollScale(0.0f);
//		getSlidingMenu().setBehindCanvasTransformer(slideTransformer);
//		getSlidingMenu().showSecondaryMenu(true);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_two, new MenuTwoListFragment())
		.commit();
	}

	private void initService(){
		
		bindService(new Intent(BLUETOOTH_ACTION), btConnection, Context.BIND_AUTO_CREATE);
		printW("service bind"+ btService+System.currentTimeMillis());
		
	}
	
	protected ServiceConnection btConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			btService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			btService = ((BluetoothService.MyBinder)service).getService();
			printW("service connect"+ btService+System.currentTimeMillis());
			
			// set the Behind View
			setBehindContentView(R.layout.menu_frame);
			FragmentTransaction t = getSupportFragmentManager().beginTransaction();
			mFrag = new MenuListFragment(mContext, btService);
			t.replace(R.id.menu_frame, mFrag);
//			t.commit();
			t.commitAllowingStateLoss();
		}
	};
	
	protected void registerDataReceiver(){
		if (nClientManager != null) {
			nClientManager.registerDataReceiver();
		}
	}
	
	protected void unregisterDataReceiver(){
		if (nClientManager != null) {
			nClientManager.unregisterDataReceiver();
		}
	}
	
	protected void unbindNettyService(){
		if (nClientManager != null) {
			nClientManager.unbindService();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.qrobot:
			Util.goToQrobot(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * ÕÛµþ
	 */
	private CanvasTransformer scaleTransformer = new CanvasTransformer() {

		@Override
		public void transformCanvas(Canvas canvas, float percentOpen) {
			canvas.scale(percentOpen, 1, 0, 0);
		}			
	};
	
	private static Interpolator interp = new Interpolator() {
		@Override
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}		
	};
	
	/**
	 * slide up
	 */
	private CanvasTransformer slideTransformer = new CanvasTransformer() {
		@Override
		public void transformCanvas(Canvas canvas, float percentOpen) {
			canvas.translate(0, canvas.getHeight()*(1-interp.getInterpolation(percentOpen)));
		}			
	};
	
	/**
	 * zoom
	 */
	private CanvasTransformer zoomTransformer = new CanvasTransformer() {
		@Override
		public void transformCanvas(Canvas canvas, float percentOpen) {
			float scale = (float) (percentOpen*0.25 + 0.75);
			canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
		}
	};
	
	private void printW(String msg){
		Log.w("BaseActivity", msg);
	}
	
}
