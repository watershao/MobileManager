package com.qrobot.mobilemanager;

import com.qrobot.mobilemanager.bt.BluetoothFragment;
import com.qrobot.mobilemanager.bt.BluetoothService;
import com.qrobot.mobilemanager.clock.ClockFragment;
import com.qrobot.mobilemanager.netty.NettyClientManager;
import com.qrobot.mobilemanager.pet.PetFragment;
import com.qrobot.mobilemanager.photo.PhotoFragment;
import com.qrobot.mobilemanager.reminder.ReminderFragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuListFragment extends ListFragment {

	private Context mContext;
	
	private BluetoothService btService;
	
	private static NettyClientManager nClientManager;
	
	public MenuListFragment(){
		
	}
	
	public MenuListFragment(Context mContext, BluetoothService btService){
		this.mContext = mContext;
		this.btService = btService;
	}
	
	public MenuListFragment(Context mContext, BluetoothService btService,NettyClientManager clientManager){
		this.mContext = mContext;
		this.btService = btService;
		nClientManager = clientManager;
		Log.w("MenuListFragment:", "nClient"+nClientManager);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String[] menus = getResources().getStringArray(R.array.menu_names);
		
		TypedArray imgs = getResources().obtainTypedArray(R.array.menu_imgs);
//		mImgRes = imgs.getResourceId(mPos, -1);
		MenuAdapter adapter = new MenuAdapter(getActivity());
		for (int i = 0; i < menus.length; i++) {
			adapter.add(new MenuItem(menus[i], imgs.getResourceId(i, -1)));
		}
		
		setListAdapter(adapter);
	}
	
	
	Fragment btFragment = null;
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		String type = "";
		Fragment newContent = null;
		
		switch (position) {
		case 0:
/*			if (btFragment ==null) {
				btFragment = new BluetoothFragment(mContext, btService);
				switchFragment(btFragment);
			}
			if (btFragment != null && !btFragment.isVisible()) {
				btFragment.setUserVisibleHint(true);
				switchFragment(btFragment);
			}*/
			
			newContent = new BluetoothFragment(mContext, btService);
			
			
//			Intent intent = new Intent(getActivity(), BluetoothActivity.class);
//			startActivity(intent);
			type = "蓝牙";
			
//			return;
			break;

		case 1:
/*			if (btFragment != null && btFragment.isVisible()) {
				Log.w("btFragment:", "1visible"+btFragment.isVisible()+btFragment.isHidden());
				getFragmentManager()
				.beginTransaction().hide(btFragment).commit();
				Log.w("btFragment:", "2visible"+btFragment.isVisible()+btFragment.isHidden());
				
			}*/
//			Log.w("btFragment:", "visible"+btFragment.isVisible()+btFragment.isHidden());
			Log.w("MenuListFragment:", "login"+MainActivity.getLogin()+nClientManager);
			if (MainActivity.getLogin()) {
//			if(true){
				newContent = new ClockFragment(mContext,nClientManager);
			} else {
				newContent = new WelcomeFragment(mContext,nClientManager);
			}
			
			
//			getFragmentManager()
//			.beginTransaction()
//			.add(R.id.content_frame, newContent).show(newContent)
//			.commit();
			
			type = "闹钟";
//			return;
			break;
		case 2:
			
//			if (MainActivity.getLogin()) {
				if(true){
				newContent = new ReminderFragment(mContext,nClientManager);
			} else {
				newContent = new WelcomeFragment(mContext,nClientManager);
			}
			
			
			type = "提醒";
			break;
		case 3:
			
			type = "激活";
			break;
		case 4:
			
			newContent = new PetFragment(mContext,"");
			
			type = "特性";
			break;
		case 5:
			
			newContent = new PhotoFragment(mContext,nClientManager);
			
			type = "图片管理";
			break;	
			
		default:
			break;
		}
//		newContent = new MenuListFragment();
		if (newContent != null)
			switchFragment(newContent);
		Toast.makeText(getActivity(), "current position." + position+type, Toast.LENGTH_LONG).show();
	}

	/**
	 * 菜单项
	 * @author water
	 *
	 */
	private class MenuItem {
		public String tag;
		public int iconRes;
		public MenuItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	/**
	 * 菜单适配器
	 * @author water
	 *
	 */
	public class MenuAdapter extends ArrayAdapter<MenuItem> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}
	
	/**
	 * 切换显示内容
	 * @param fragment
	 */
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity ma = (MainActivity) getActivity();
			ma.switchContent(fragment);
		}
	}
}
