package com.qrobot.mobilemanager.photo;

import java.util.List;
import java.util.Map;

import com.qrobot.mobilemanager.R;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	
    private Context mContext;
    
    private LayoutInflater mInflater;
    
    private List<Map<String, Object>> mData;

    public ImageAdapter(Context c,List<Map<String, Object>> mList) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
        mData = mList;
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {        
        
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_griditem, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.ItemText);
			holder.image = (ImageView) convertView.findViewById(R.id.ItemImage);
			
//			holder.image.setLayoutParams(new GridView.LayoutParams(85, 85));
			holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			holder.image.setPadding(8, 8, 8, 8);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(mData.get(position).get("name").toString());
//		holder.image.setImageBitmap((Bitmap)mData.get(position).get("image"));
		holder.image.setImageURI((Uri)mData.get(position).get("uri"));
//		holder.image.setImageResource((Integer)mData.get(position).get("image"));
		return convertView;
    }
    
	static class ViewHolder {
		
		TextView name;
		ImageView image;
	}
}