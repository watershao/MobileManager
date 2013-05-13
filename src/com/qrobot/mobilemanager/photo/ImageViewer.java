package com.qrobot.mobilemanager.photo;

import java.io.File;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.qrobot.mobilemanager.R;

/**
 * 浏览图片
 * @author water
 *
 */
public class ImageViewer extends Activity  {
	
	
	 // 左右滑动时手指按下的X坐标
	 private float touchDownX;
	 // 左右滑动时手指松开的X坐标
	 private float touchUpX;
	 
	 
	 private List<Map<String, Object>> imageList;
	 
	 private ImageAdapter imageAdapter;
 
	private static final String SD_PATH = Environment.getExternalStorageDirectory()+File.separator;
	
	private static final String IMG_PATH = "/qmm/img/qro/";
	
	private static final String IMG_THUMB_PATH = "/qmm/img/thumb/";
	
	private static final String IMG_MOBILE_PATH = "/qmm/img/mobile/";
	
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_gridview);
		
		GridView gridView = (GridView)findViewById(R.id.image_gridview);
		imageList = getData(getImagesFromSD());
		imageAdapter = new ImageAdapter(ImageViewer.this, imageList);
		gridView.setAdapter(imageAdapter);
		gridView.setOnTouchListener(switcherTouchListener);
		gridView.setOnItemClickListener(itemClickListener);
		gridView.setOnItemLongClickListener(itemLongClickListener);
	}

	private OnTouchListener switcherTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// 取得左右滑动时手指按下的X坐标
				touchDownX = event.getX();
				return false;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// 取得左右滑动时手指松开的X坐标
				touchUpX = event.getX();
				// 从左往右，看前一张
				if (touchUpX - touchDownX > 100) {

					Toast.makeText(getApplicationContext(), "from left to right", Toast.LENGTH_LONG).show();
					
					// 从右往左，看下一张
				} else if (touchDownX - touchUpX > 100) {

					Toast.makeText(getApplicationContext(), "from right to left", Toast.LENGTH_LONG).show();
				}
				return false;
			}
			return false;
		}
	};
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Map<String, Object> item = (HashMap<String, Object>)parent.getItemAtPosition(position);
			
			viewImage((Uri)item.get("uri"));
			
			Log.d("ImageViewer:", "click pos:"+position+"id:"+id+item.get("name")+item.get("uri"));
		}
	};

	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			Map<String, Object> item = (HashMap<String, Object>)parent.getItemAtPosition(position);
//			parent.removeViewAt(1);
			multiOperate();
			
			Log.d("ImageViewer:", "long pos:"+position+"id:"+id+item.get("name")+item.get("uri"));
			
			return true;
		}
	
	
	};
	
	private List<String> getImagesFromSD() {
		List<String> imageList = new ArrayList<String>();

		File f = new File(SD_PATH+IMG_THUMB_PATH);
//		File f = new File("/storage/sdcard0/wandoujia/image/");
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
//			f = new File(Environment.getExternalStorageDirectory().toString());
		}else{
			Toast.makeText(ImageViewer.this, "sdcard error", 1).show();
			return imageList;
		}

		File[] files = f.listFiles();

		if(files == null || files.length == 0)
			return imageList;
		/**
		 * 将所有图像文件的路径存入ArrayList列表
		 */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (isImageFile(file.getPath()))
				imageList.add(file.getPath());
		}
		return imageList;
	}
	
	private boolean isImageFile(String fName) {
		boolean re;
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/**
		 * 依据文件扩展名判断是否为图像文件
		 */
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}
		return re;
	}
	
	
	private List<Map<String, Object>> getData(List<String> fileList){
		if (fileList != null && fileList.size() > 0) {
			List<Map<String, Object>> imgList = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < fileList.size(); i++) {
				Map<String, Object> imgMap= new HashMap<String, Object>();
				String file = fileList.get(i);
				String fileName = file.substring(file.lastIndexOf("/")+1);
				Log.d("ImageViewer", "fileName:"+fileName);
				Uri fileUri = Uri.fromFile(new File(file));
				imgMap.put("name", fileName);
				imgMap.put("uri", fileUri);
				imgList.add(imgMap);
				
				
			}
			return imgList;
		}
		
		return null;
	}

	private void viewImage(Uri uri){
		AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewer.this);
//	    builder.setTitle(R.string.photo_mutioperate);
	    ImageView imageView = new ImageView(ImageViewer.this);
	    
	    ViewGroup.LayoutParams lp = new LayoutParams( ViewGroup.LayoutParams.FILL_PARENT,     
	    		ViewGroup.LayoutParams.FILL_PARENT );
	    
	    imageView.setLayoutParams(lp);
	    
	    imageView.setImageURI(uri);
	    builder.setView(imageView);
	    
	    builder.create().show();
	}
	
	
	private void multiOperate(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewer.this);
	    builder.setTitle(R.string.photo_mutioperate)
	           .setItems(R.array.photo_muti_operate, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
	            	   Log.d("ImageViewer:", "which:"+which);
	            	   switch (which) {
						case 0:
							
							break;
						case 1:
							
							break;	
						case 2:
							
							break;
						case 3:
							
							break;
							
						default:
							break;
						}
	           }
	    });
	    
	   builder.create().show();
	}
	
	
}