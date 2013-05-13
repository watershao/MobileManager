package com.qrobot.mobilemanager.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.qrobot.mobilemanager.R;
import com.qrobot.mobilemanager.netty.NettyClientManager;

public class PhotoFragment extends Fragment {
	
	private LinearLayout photoLayout;

	private Context mContext;
	
	private Button viewButton;
	
	private Button getButton;
	private Button thumbButton;
	private ImageManager imageManager;
	
	private NettyClientManager nClientManager;
	
	private List<String> newFileList = null;
	
	private ProgressDialog pDialog;
	private boolean isShowDialog = false;
	
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
	
	private GridView gridView = null;
	
	public PhotoFragment(Context context){
		mContext = context;
	}
	
	public PhotoFragment(Context context, NettyClientManager clientManager){
		mContext = context;
		nClientManager = clientManager;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		photoLayout = (LinearLayout)inflater.inflate(R.layout.photo_main, null);
		viewButton = (Button)photoLayout.findViewById(R.id.photo_view);
		getButton = (Button)photoLayout.findViewById(R.id.photo_get);
		thumbButton = (Button)photoLayout.findViewById(R.id.photo_getthumb);
		
		imageManager = new ImageManager(mContext, nClientManager,mHandler);
		
		gridView = (GridView)photoLayout.findViewById(R.id.image_gridview);
		imageList = getData(getImagesFromSD());
		imageAdapter = new ImageAdapter(mContext, imageList);
		gridView.setAdapter(imageAdapter);
		gridView.setOnTouchListener(switcherTouchListener);
		gridView.setOnItemClickListener(itemClickListener);
		gridView.setOnItemLongClickListener(itemLongClickListener);
		
		return photoLayout;
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
		initView();
	}
    
	private void initView(){
		
		viewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

//				startActivity(new Intent(mContext,ViewPhoto.class));
				
//				startActivity(new Intent(mContext,ImageViewer.class));
				imageManager.deleteImage("camera");
//				imageManager.deleteImage("/mnt/sdcard/qrobot/camera/dcim/19700103052737.jpg");
//				imageManager.getSingleImage("/mnt/sdcard/qrobot/camera/dcim/20130417161111.jpg");
			}
		});
		
		getButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				imageManager.getCameraImages();
//				imageManager.getThumbImage("camera");
//				imageManager.getSingleImage("/mnt/sdcard/qrobot/camera/dcim/20130417161111.jpg");

			}
		});
		
		thumbButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if (newFileList != null && newFileList.size() > 0) {
					String fileNames = "camera";
					for (int i = 0; i < newFileList.size(); i++) {
						String fileName = newFileList.get(i);
						fileNames += "*" + fileName;
					}
					Log.d("fileNames", fileNames);
					imageManager.getThumbImage(fileNames);
//					toast("您的缩略图下载完毕。");
				} else {
					toast("您没有可更新的图片，请刷新图片列表再试。");
				}
				
				
//				imageManager.getCameraImages();
//				imageManager.getThumbImage("camera");
//				imageManager.getSingleImage("/mnt/sdcard/qrobot/camera/dcim/20130417161111.jpg");

			}
		});
	}
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				List<String> newList = (List<String>)msg.obj;
				if (newList != null && newList.size() > 0) {
					int size = newList.size();
					newFileList = newList;
					for (int i = 0; i < newFileList.size(); i++) {
						Log.d(">>", "<<"+newFileList.get(i));
					}
					Toast.makeText(mContext, "您有"+size+"个新图片可以查看，点击更新缩略图。", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mContext, "您么有新图片可以查看。", Toast.LENGTH_LONG).show();
				}
				
				break;

			case 2:
				String fileName = (String)msg.obj;
				if (fileName != null && fileName.trim().length() > 0) {

					imageList = getData(getImagesFromSD());
					imageAdapter = new ImageAdapter(mContext, imageList);
					gridView.setAdapter(imageAdapter);
					
					Toast.makeText(mContext, "您的文件"+fileName+"下载成功。", Toast.LENGTH_SHORT).show();
					if (isShowDialog) {
						pDialog.cancel();
						viewImage(fileName);
						isShowDialog = false;
					}
				} else {
					Toast.makeText(mContext, "您么有新图片可以查看。", Toast.LENGTH_LONG).show();
				}
				
				break;				
			default:
				break;
			}
		}
		
	};
	
	private void toast(String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
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

					Toast.makeText(mContext, "from left to right", Toast.LENGTH_LONG).show();
					
					// 从右往左，看下一张
				} else if (touchDownX - touchUpX > 100) {

					Toast.makeText(mContext, "from right to left", Toast.LENGTH_LONG).show();
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
			
//			viewImage((Uri)item.get("uri"));
			
			viewImage((String)item.get("name"));
			Log.d("ImageViewer:", "click pos:"+position+"id:"+id+item.get("name")+item.get("uri"));
		}
	};

	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			Map<String, Object> item = (HashMap<String, Object>)parent.getItemAtPosition(position);

			multiOperate((String)item.get("name"));
			
			Log.d("ImageViewer:", "long pos:"+position+"id:"+id+item.get("name")+item.get("uri"));
			
			return true;
		}
	
	
	};
	
	private List<String> getImagesFromSD() {
		List<String> imageList = new ArrayList<String>();

		File f = new File(SD_PATH+IMG_THUMB_PATH);
		if (!f.exists()) {
			f.mkdirs();
		}
//		File f = new File("/storage/sdcard0/wandoujia/image/");
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
//			f = new File(Environment.getExternalStorageDirectory().toString());
		}else{
			Toast.makeText(mContext, "sdcard error", 1).show();
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
		List<Map<String, Object>> imgList = new ArrayList<Map<String,Object>>();
		if (fileList != null && fileList.size() > 0) {
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
		
		return imgList;
	}

	/**
	 * 查看大图
	 * @param fileName
	 */
	private void viewImage(String fileName){
		File file = new File(SD_PATH+IMG_PATH+fileName);
		
		if (!file.exists()) {
			imageManager.getSingleImage(fileName);
			Log.d(">>", fileName);
			isShowDialog = true;
			//创建ProgressDialog对象
            pDialog = new ProgressDialog(mContext);
            // 设置进度条风格，风格为圆形，旋转的
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // 设置ProgressDialog 标题
            pDialog.setTitle("提示");
            // 设置ProgressDialog 提示信息
            pDialog.setMessage("图片正在下载中，请稍候...");
            // 设置ProgressDialog 标题图标
            pDialog.setIcon(R.drawable.ic_launcher);
            // 设置ProgressDialog 的进度条是否不明确
            pDialog.setIndeterminate(false);
           
            // 设置ProgressDialog 是否可以按退回按键取消
            pDialog.setCancelable(true);
           
            // 设置ProgressDialog 的一个Button
            pDialog.setButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i)
                {
                    //点击“确定按钮”取消对话框
                    dialog.cancel();
                }
            });

            // 让ProgressDialog显示
            pDialog.show();
			
		} else {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//	    builder.setTitle(R.string.photo_mutioperate);
			ImageView imageView = new ImageView(mContext);
			
			ViewGroup.LayoutParams lp = new LayoutParams( ViewGroup.LayoutParams.FILL_PARENT,     
					ViewGroup.LayoutParams.FILL_PARENT );
			
			imageView.setLayoutParams(lp);
			
			imageView.setImageURI(Uri.fromFile(file));
			builder.setView(imageView);
			
			builder.create().show();
		}
		
	}
	
	/**
	 * 处理长按选项功能
	 * @param fileName
	 */
	private void multiOperate(final String fileName){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    builder.setTitle(R.string.photo_mutioperate)
	           .setItems(R.array.photo_muti_operate, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
	            	   Log.d("ImageViewer:", "which:"+which);
	            	   switch (which) {
						case 0:
							
							viewImage(fileName);
							
							break;
						case 1:
							imageManager.deleteImage("camera*"+fileName);
							
							imageList = getData(getImagesFromSD());
							imageAdapter = new ImageAdapter(mContext, imageList);
							gridView.setAdapter(imageAdapter);
							
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
