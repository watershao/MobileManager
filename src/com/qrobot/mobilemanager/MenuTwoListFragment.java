package com.qrobot.mobilemanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuTwoListFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String[] menus = getResources().getStringArray(R.array.menu_two_names);
		
		TypedArray imgs = getResources().obtainTypedArray(R.array.menu_two_imgs);
//		mImgRes = imgs.getResourceId(mPos, -1);
		MenuAdapter adapter = new MenuAdapter(getActivity());
		for (int i = 0; i < menus.length; i++) {
			adapter.add(new MenuItem(menus[i], imgs.getResourceId(i, -1)));
		}
		
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		String type = "";
		Fragment newContent = null;
		switch (position) {
		case 0:
			
			type = "特性";
			break;

		case 1:
			
			type = "设置";
			break;
		case 2:
			
			type = "退出";
			break;
			
		default:
			break;
		}
		newContent = new MenuTwoListFragment();
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
