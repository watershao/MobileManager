package com.qrobot.mobilemanager.pet;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrobot.mobilemanager.R;

public class PortraitAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	private List<Map<String, Object>> mData;
	
	public PortraitAdapter(Context context,List<Map<String, Object>> mList){
		mInflater = LayoutInflater.from(context);
		mData = mList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.pet_portrait_item, null);
			holder = new ViewHolder();
			holder.id = (TextView) convertView.findViewById(R.id.pet_portrait_id);
			holder.image = (ImageView) convertView.findViewById(R.id.pet_portrait_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.id.setText(mData.get(position).get("id").toString());
		holder.image.setImageBitmap((Bitmap)mData.get(position).get("image"));
//		holder.image.setImageResource((Integer)mData.get(position).get("image"));
		return convertView;
	}
		 
		
	static class ViewHolder {
		
		TextView id;
		ImageView image;
	}
	
	
}
