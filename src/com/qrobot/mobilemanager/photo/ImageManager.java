package com.qrobot.mobilemanager.photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.qrobot.mobilemanager.datalistener.ErrorDataListener;
import com.qrobot.mobilemanager.datalistener.UserDataListener;
import com.qrobot.mobilemanager.netty.NettyClientManager;
import com.qrobot.mobilemanager.util.ByteUtil;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class ImageManager {

	private static final String TAG = "ImageManager";
	private Context mContext;
	private NettyClientManager nClientManager;
	
	private static String fileName = null;
	private static String currFileName = null;
	private static int currTime = 0;
	private static int count = 0;
	private static byte[] validData = null;
	
	private Handler mHandler = null;
	
	private static final String FLAG_TEXT = "manpictext";
	private static final String FLAG_BUFFER = "manpicbuff";
	private static final int FLAG_LENGTH = 10;
	
	private static final String SD_PATH = Environment.getExternalStorageDirectory()+File.separator;
	
	private static final String IMG_PATH = "/qmm/img/qro/";
	
	private static final String IMG_THUMB_PATH = "/qmm/img/thumb/";
	
	private static final String IMG_MOBILE_PATH = "/qmm/img/mobile/";
	
	private List<String> newThumbList = new ArrayList<String>();
	
	private String fileType = null;
	
	public ImageManager(){
		
	}
	
	public ImageManager(Context context){
		mContext = context;
	}
	
	public ImageManager(Context context,NettyClientManager clientManager){
		mContext = context;
		nClientManager = clientManager;
		nClientManager.setUserDataListener(userDataListener);
		nClientManager.setErrorDataListener(errorDataListener);
	}
	
	public ImageManager(Context context,NettyClientManager clientManager,Handler handler){
		mContext = context;
		nClientManager = clientManager;
		nClientManager.setUserDataListener(userDataListener);
		nClientManager.setErrorDataListener(errorDataListener);
		mHandler = handler;
	}
	
	private ErrorDataListener errorDataListener = new ErrorDataListener() {
		
		@Override
		public void OnErrorDataListener(Bundle bundle) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void OnErrorDataListener(int errorCode, byte[] msg) {
			// TODO Auto-generated method stub
			
			Log.d(TAG, "errorCode:"+errorCode);
			if (errorCode == 2) {
				Toast.makeText(mContext, "小Q暂时不在线，请上线后再试。", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
	private UserDataListener userDataListener = new UserDataListener() {
		
		@Override
		public void OnUserDataListener(Bundle bundle) {
			// TODO Auto-generated method stub
			
		}
		
		
		/*		Msg格式   
	      a. img:rl#camera  刷新图片列表(可能有AppWeather 天气的图片)(refresh lists)
		　　b. img:sp#图片文件路径   获取具体图片数据(single picture)
		　　c. img:dm#图片文件路径集   删除图片(delete multi)(以*分隔)
		　　d. img:tp#图片文件名路径   获取缩略图片数据(thumb picture)
		　　e. img:fl#图片文件名列表   文件以英文字符中的*分隔  (file lists) [0] 为缩略图路径
		 */
		
		/**
		 *   Msg格式
			  4字节: 图片总大小
			  4字节: 图片数据流开始位置
			  4字节: 图片数据流有效长度
			  4字节: 文件名开始位置
			  4字节: 文件名长度
			  4字节: 总传送几次(一张图片分几次传)
			  4字节: 当前次数 (当前图片第几回BUFF传输)
			 文件名(原图:camera*s*实际文件名; 缩略图:camera*t*实际文件名)
			  图片数据流
		 */
		@Override
		public void OnUserDataListener(int robotNo, byte[] data, int dataSize) {
			// TODO Auto-generated method stub
			Log.d(TAG, "id:"+robotNo+"data:"+data.length+"dataSize:"+dataSize );
			
			byte[] flagByte = new byte[10];
			System.arraycopy(data, 0, flagByte, 0, 10);
			String flag = new String(flagByte);
			
			if (flag.equalsIgnoreCase("manpictext")) {
				byte[] text = new byte[data.length - 10];
				System.arraycopy(data, 10, text, 0, data.length - 10);
				
				handleMsg(robotNo, text);
			}
			
			Log.d(TAG, "count:"+count+"currTime:"+currTime+"*"+flag);
			
			if (flag.equalsIgnoreCase("manpicbuff")) {
				byte[] buff = new byte[data.length - 10];
				System.arraycopy(data, 10, buff, 0, data.length - 10);
				
				handleData(buff, dataSize);
				Log.d(TAG, fileName+"currFile:"+currFileName);
				try {
					if (fileName == null ) {
						String[] fileStr = currFileName.split("\\*"); 
						String fType = fileStr[1];
						fileName = fileStr[2];
						File save = null;
						if (fType.equalsIgnoreCase("t")) {
							
							save = new File(SD_PATH+IMG_THUMB_PATH);
						} else {
							save = new File(SD_PATH+IMG_PATH);
						}
						
						Log.d(TAG, "save:"+save.getPath());
						if (!save.exists()) {
							save.mkdirs();
						}
//						fileName = fileName.substring(fileName.lastIndexOf("/"), fileName.length());
						file = new File(save, fileName);
						if (!file.exists()) {
							file.createNewFile();
						}
						fos = new FileOutputStream(file);// 建立一个可存取字节的文件  
					}
					
					Log.d(TAG, "fos:"+fos+",valid:"+validData);
					fos.write(validData, 0,validData.length);
					
					if (count!= 0 && count == currTime) {
						mHandler.obtainMessage(2, fileName).sendToTarget();
						fileName = null;
						count = 0;
						currTime = 0;
						if (fos != null) {
							fos.flush();
							fos.close();
							fos = null;
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					fileName = null;
					count = 0;
					currTime = 0;
					if (fos != null) {
						try {
							fos.flush();
							fos.close();
							fos = null;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
			
		}
	};
	
	private File file = null;
	private FileOutputStream fos = null; 
	
	/**
	 * 获取所有图片文件列表
	 */
	public void getCameraImages(){
		String imgFlag = "img:rl#camera";
		String cmd = imgFlag;
		
		sendCmd(FLAG_TEXT, cmd);
	}
	
	/**
	 * 获取单张图片数据,　img:sp#camera*图片名称   
　　  	 * 获取原图数据(single picture)
	 * @param fileName
	 */
	public void getSingleImage(String fileName){
		String imgFlag = "img:sp#camera*";
		String sendData = imgFlag+fileName;
		
		sendCmd(FLAG_TEXT, sendData);
	}
	
	/**
	 * 删除图片,　　img:dm#camera*图片名称*图片名称   
　　   	 * 删除图片(*后面不带图片名称,表示删除所有图片)
	 * @param fileName 如果fileName为camera则删除全部图片，为文件夹则删除文件夹
	 */
	public void deleteImage(String fileName){
		boolean del = deleteLocal(fileName);
		if (!del) {
			return;
		}
		String imgFlag = "img:dm#";
		String cmd = imgFlag + fileName;
		sendCmd(FLAG_TEXT, cmd);
		
	}
	
	/**
	 * 删除文件列表
	 * @param fileNameList
	 */
	private void deleteImages(List<String> fileNameList){
		String imgFlag = "img:dm#camera";
		if (fileNameList != null && fileNameList.size() > 0) {
			String fileName="";
			for (int i = 0; i < fileNameList.size(); i++) {
				fileName = "*"+fileNameList.get(i);
			}
			if (fileName.trim().length() == 0) {
				return;
			}
			fileName = fileName.substring(0, fileName.length()-1);
			
			String cmd = imgFlag + fileName;
			sendCmd(FLAG_TEXT, cmd);
		}
		
	}
	
	/**
	 * 获取缩略图片数据,　　img:tp#camera*图片名称*图片名称
  	 * 获取缩略图数据(thumb picture)(*后面不带图片名称表示获取所有缩略图片)
	 * @param fileName 当fileName为camera时，获取所有缩略图数据
	 */
	public void getThumbImage(String fileName){
		String imgFlag = "img:tp#";
		String cmd = imgFlag+fileName;
		
		sendCmd(FLAG_TEXT, cmd);
		
	}
	
	
	/**
	 * 发送命令数据
	 * @param flag
	 * @param cmd
	 */
	private void sendCmd(String flag,String cmd){
		byte[] cmdByte = getSendByte(flag, cmd);
		if (cmdByte == null) {
			return;
		}
		nClientManager.sendUserData(nClientManager.getRemoteId(), cmdByte, cmdByte.length);
	}
	
	
	/**
	 * 将发送内容转为字节流
	 * @param sendStr
	 * @return
	 */
	private byte[] getSendByte(String flag,String sendStr){
		try {
			byte[] cmdByte = sendStr.getBytes("utf-8");
			int length = FLAG_LENGTH + cmdByte.length;
			byte[] send = new byte[length];
			byte[] flagByte = setFlagByte(flag);
			
			System.arraycopy(flagByte, 0, send, 0, FLAG_LENGTH);
			System.arraycopy(cmdByte, 0, send, FLAG_LENGTH, cmdByte.length);
			return send;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	/**
	 * 添加标记
	 * @param flag
	 * @return
	 */
	private byte[] setFlagByte(String flag){
		
		byte[] flagByte = new byte[FLAG_LENGTH];
		byte[] f = flag.getBytes();
		System.arraycopy(f, 0, flagByte, 0, FLAG_LENGTH);
		return flagByte;
	}
	
	
	/**
	 * 处理接收到的数据流
	 * @param data
	 * @param dataSize
	 */
	private void handleData(byte[] data, int dataSize){
		
		// 图片总大小
		byte[] imgSizeByte = new byte[4];
		System.arraycopy(data, 0, imgSizeByte, 0, 4);
		int imgSize = ByteUtil.byte2Int(imgSizeByte);
		
		//图片数据流开始位置
		byte[] dataStartByte = new byte[4];
		System.arraycopy(data, 4, dataStartByte, 0, 4);
		int dataStart = ByteUtil.byte2Int(dataStartByte);
		
		//图片数据流有效长度
		byte[] dataLengthByte = new byte[4];
		System.arraycopy(data, 8, dataLengthByte, 0, 4);
		int dataLength = ByteUtil.byte2Int(dataLengthByte);
		
		//文件名开始位置
		byte[] fileStartByte = new byte[4];
		System.arraycopy(data, 12, fileStartByte, 0, 4);
		int fileStart = ByteUtil.byte2Int(fileStartByte);
		
		//文件名长度
		byte[] fileLengthByte = new byte[4];
		System.arraycopy(data, 16, fileLengthByte, 0, 4);
		int fileLength = ByteUtil.byte2Int(fileLengthByte);
		
		//总传送几次(一张图片分几次传)
		byte[] countByte = new byte[4];
		System.arraycopy(data, 20, countByte, 0, 4);
		count = ByteUtil.byte2Int(countByte);
		
		//当前次数 (当前图片第几回BUFF传输)
		byte[] currTimeByte = new byte[4];
		System.arraycopy(data, 24, currTimeByte, 0, 4);
		currTime = ByteUtil.byte2Int(currTimeByte);
		
		Log.d(TAG, "imgSize:"+imgSize+">fileStart:"+fileStart+">fileLength:"+fileLength+
				">count:"+count+">currTime:"+currTime);
		
		validData = new byte[dataLength];
		System.arraycopy(data, dataStart-FLAG_LENGTH, validData, 0, dataLength);
		
		byte[] fileNameByte = new byte[fileLength];
		System.arraycopy(data, fileStart-FLAG_LENGTH, fileNameByte, 0, fileLength);
		
		try {
			currFileName = new String(fileNameByte,"utf-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 处理接收到的消息数据
	 * @param robotNo
	 * @param data
	 */
	private void handleMsg(int robotNo,byte[] data){
		String msg = new String(data);
		Log.d(TAG, "id:"+robotNo+"msg:"+msg);
		
		if (msg.startsWith("img:rl#camera")) {
			Log.d(TAG, "id:"+robotNo+"rl:"+msg);
			
		}
		
		if (msg.startsWith("img:sp#")) {
			Log.d(TAG, "id:"+robotNo+"sp:"+msg);
			
		}
		if (msg.startsWith("img:dm#")) {
			Log.d(TAG, "id:"+robotNo+"dm:"+msg);
			
		}
		if (msg.startsWith("img:tp#")) {
			Log.d(TAG, "id:"+robotNo+"tp:"+msg);
			
		}
		
		//img:fl#camera*图片名称*图片名称
		if (msg.startsWith("img:fl#")) {
			Log.d(TAG, "id:"+robotNo+"fl:"+msg);
			String fl = msg.substring(7);

			String[] files = fl.split("\\*");
			fileType = files[0];
//			Log.d(TAG, files[1]+"len:"+files.length+"fl2:"+files[0]);
			
			List<String> newList = getNewThumbList(files, getLocalFiles());
			if (newList != null && newList.size() > 0) {
				Log.d(TAG, "size:"+newList.size());
				mHandler.obtainMessage(1, newList).sendToTarget();
			}
		}
		
	}
	
	/**
	 * 获取最新的缩略图列表
	 * @param qFiles
	 * @param localFiles
	 * @return
	 */
	private List<String> getNewThumbList(String[] qFiles,String[] localFiles){
		if (qFiles != null && qFiles.length > 0) {
			List<String> newList = new ArrayList<String>();
			if (localFiles == null || localFiles.length == 0) {
				for (int i = 1; i < qFiles.length; i++) {
					String file = qFiles[i];
					newList.add(file);
				}
				return newList;
			}else {
				for (int i = 1; i < qFiles.length; i++) {
					String qFile = qFiles[i];
					if (!isInLocal(qFile, localFiles)) {
						newList.add(qFile);
					}
				}
				return newList;
			}
		}
		return null;
		
	}
	
	private boolean isInLocal(String file,String[] localFiles){
		for (int i = 0; i < localFiles.length; i++) {
			if (file.contains(localFiles[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取本地已经保存缩略图的文件列表
	 * @return
	 */
	private String[] getLocalFiles(){
		File targetFile = new File(SD_PATH+IMG_THUMB_PATH);
		if (targetFile.exists()) {
			File[] files = targetFile.listFiles();
			if (files != null && files.length > 0) {
				
				String[] fileNames = new String[files.length];
				for (int i = 0; i < files.length; i++) {
					fileNames[i] = files[i].getName();
				}
				return fileNames;
			}
		}
		
		return null;
	}
	
	/**
	 * 删除本地文件
	 * @param fileName
	 * @return
	 */
	private boolean deleteLocal(String fileName){
		Log.d("delete filename>>", fileName);
//		String fName = fileName.substring(fileName.lastIndexOf("/")+1);
		String[] fileNames = fileName.split("\\*");
		if (fileNames == null) {
			return false;
		}
		if (fileNames.length == 1) {
			File imgDir = new File(SD_PATH+IMG_PATH);
			File thumbDir = new File(SD_PATH + IMG_THUMB_PATH);
			if (imgDir.exists() ) {
				File[] imgs = imgDir.listFiles();
				if (imgs != null && imgs.length > 0) {
					for (int i = 0; i < imgs.length; i++) {
						boolean del = imgs[i].delete();
						if (!del) {
							return false;
						}
					}
					
				}
			}
			if (thumbDir.exists() ) {
				File[] thumbs = thumbDir.listFiles();
				if (thumbs != null && thumbs.length > 0) {
					for (int i = 0; i < thumbs.length; i++) {
						boolean del = thumbs[i].delete();
						if (!del) {
							return false;
						}
					}
					
				}
			}
			
			return true;
		}
		
		if (fileNames.length > 1) {
			for (int i = 1; i < fileNames.length; i++) {
				String fName = fileNames[i];
				Log.d("delete filename", fName);
				File img = new File(SD_PATH+IMG_PATH+fName);
				if (img.exists()) {
					Log.d("delete", img.getPath());
					boolean del = img.delete();
					if (!del) {
						return false;
					}
				}
				
				File thumb = new File(SD_PATH+IMG_THUMB_PATH+fName);
				if (thumb.exists()) {
					Log.d("delete thumb", thumb.getPath());
					boolean del = thumb.delete();
					if (!del) {
						return false;
					}
				}
			}
			return true;
		}
		return true;
	}
	
}
