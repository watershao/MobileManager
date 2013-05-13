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
				Toast.makeText(mContext, "СQ��ʱ�����ߣ������ߺ����ԡ�", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
	private UserDataListener userDataListener = new UserDataListener() {
		
		@Override
		public void OnUserDataListener(Bundle bundle) {
			// TODO Auto-generated method stub
			
		}
		
		
		/*		Msg��ʽ   
	      a. img:rl#camera  ˢ��ͼƬ�б�(������AppWeather ������ͼƬ)(refresh lists)
		����b. img:sp#ͼƬ�ļ�·��   ��ȡ����ͼƬ����(single picture)
		����c. img:dm#ͼƬ�ļ�·����   ɾ��ͼƬ(delete multi)(��*�ָ�)
		����d. img:tp#ͼƬ�ļ���·��   ��ȡ����ͼƬ����(thumb picture)
		����e. img:fl#ͼƬ�ļ����б�   �ļ���Ӣ���ַ��е�*�ָ�  (file lists) [0] Ϊ����ͼ·��
		 */
		
		/**
		 *   Msg��ʽ
			  4�ֽ�: ͼƬ�ܴ�С
			  4�ֽ�: ͼƬ��������ʼλ��
			  4�ֽ�: ͼƬ��������Ч����
			  4�ֽ�: �ļ�����ʼλ��
			  4�ֽ�: �ļ�������
			  4�ֽ�: �ܴ��ͼ���(һ��ͼƬ�ּ��δ�)
			  4�ֽ�: ��ǰ���� (��ǰͼƬ�ڼ���BUFF����)
			 �ļ���(ԭͼ:camera*s*ʵ���ļ���; ����ͼ:camera*t*ʵ���ļ���)
			  ͼƬ������
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
						fos = new FileOutputStream(file);// ����һ���ɴ�ȡ�ֽڵ��ļ�  
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
	 * ��ȡ����ͼƬ�ļ��б�
	 */
	public void getCameraImages(){
		String imgFlag = "img:rl#camera";
		String cmd = imgFlag;
		
		sendCmd(FLAG_TEXT, cmd);
	}
	
	/**
	 * ��ȡ����ͼƬ����,��img:sp#camera*ͼƬ����   
����  	 * ��ȡԭͼ����(single picture)
	 * @param fileName
	 */
	public void getSingleImage(String fileName){
		String imgFlag = "img:sp#camera*";
		String sendData = imgFlag+fileName;
		
		sendCmd(FLAG_TEXT, sendData);
	}
	
	/**
	 * ɾ��ͼƬ,����img:dm#camera*ͼƬ����*ͼƬ����   
����   	 * ɾ��ͼƬ(*���治��ͼƬ����,��ʾɾ������ͼƬ)
	 * @param fileName ���fileNameΪcamera��ɾ��ȫ��ͼƬ��Ϊ�ļ�����ɾ���ļ���
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
	 * ɾ���ļ��б�
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
	 * ��ȡ����ͼƬ����,����img:tp#camera*ͼƬ����*ͼƬ����
  	 * ��ȡ����ͼ����(thumb picture)(*���治��ͼƬ���Ʊ�ʾ��ȡ��������ͼƬ)
	 * @param fileName ��fileNameΪcameraʱ����ȡ��������ͼ����
	 */
	public void getThumbImage(String fileName){
		String imgFlag = "img:tp#";
		String cmd = imgFlag+fileName;
		
		sendCmd(FLAG_TEXT, cmd);
		
	}
	
	
	/**
	 * ������������
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
	 * ����������תΪ�ֽ���
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
	 * ��ӱ��
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
	 * ������յ���������
	 * @param data
	 * @param dataSize
	 */
	private void handleData(byte[] data, int dataSize){
		
		// ͼƬ�ܴ�С
		byte[] imgSizeByte = new byte[4];
		System.arraycopy(data, 0, imgSizeByte, 0, 4);
		int imgSize = ByteUtil.byte2Int(imgSizeByte);
		
		//ͼƬ��������ʼλ��
		byte[] dataStartByte = new byte[4];
		System.arraycopy(data, 4, dataStartByte, 0, 4);
		int dataStart = ByteUtil.byte2Int(dataStartByte);
		
		//ͼƬ��������Ч����
		byte[] dataLengthByte = new byte[4];
		System.arraycopy(data, 8, dataLengthByte, 0, 4);
		int dataLength = ByteUtil.byte2Int(dataLengthByte);
		
		//�ļ�����ʼλ��
		byte[] fileStartByte = new byte[4];
		System.arraycopy(data, 12, fileStartByte, 0, 4);
		int fileStart = ByteUtil.byte2Int(fileStartByte);
		
		//�ļ�������
		byte[] fileLengthByte = new byte[4];
		System.arraycopy(data, 16, fileLengthByte, 0, 4);
		int fileLength = ByteUtil.byte2Int(fileLengthByte);
		
		//�ܴ��ͼ���(һ��ͼƬ�ּ��δ�)
		byte[] countByte = new byte[4];
		System.arraycopy(data, 20, countByte, 0, 4);
		count = ByteUtil.byte2Int(countByte);
		
		//��ǰ���� (��ǰͼƬ�ڼ���BUFF����)
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
	 * ������յ�����Ϣ����
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
		
		//img:fl#camera*ͼƬ����*ͼƬ����
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
	 * ��ȡ���µ�����ͼ�б�
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
	 * ��ȡ�����Ѿ���������ͼ���ļ��б�
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
	 * ɾ�������ļ�
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
