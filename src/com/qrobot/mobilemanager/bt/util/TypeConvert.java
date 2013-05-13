package com.qrobot.mobilemanager.bt.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TypeConvert {

	/**
	 * 将数组转换为序列化对象
	 * 
	 * @param bytes
	 * @return
	 */
	public static java.lang.Object ByteToObject(byte[] bytes) {
		java.lang.Object obj = null;
		try {
			// bytearray to object
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);

			obj = oi.readObject();

			bi.close();
			oi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 将对象转为数组
	 * @param obj
	 * @return
	 */
	public static byte[] ObjectToByte(java.lang.Object obj) {
		byte[] bytes = null;

		// object to bytearray
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo;
		try {
			oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);

			bytes = bo.toByteArray();

			bo.flush();
			oo.flush();
			bo.close();
			oo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bytes;
	}
	
	/**
	 * 获取发送数据的byte[]
	 * @param type 类型
	 * @return
	 */
	public static byte[] getSendByteArray(int type){
        List<byte[]> convertByteArrayList = new ArrayList<byte[]>();
        byte[] sendByteArray = null;
    	convertByteArrayList.clear();
    	convertByteArrayList.add(TypeConvert.ObjectToByte(type));
    	sendByteArray = TypeConvert.ObjectToByte(convertByteArrayList);
		
		return sendByteArray;
	}
	
	/**
	 * 获取发送数据的byte[]
	 * @param type 类型
	 * @param str1  内容
	 * @return
	 */
	public static byte[] getSendByteArray(int type, String str1){
        List<byte[]> convertByteArrayList = new ArrayList<byte[]>();
        byte[] sendByteArray = null;
    	convertByteArrayList.clear();
    	convertByteArrayList.add(TypeConvert.ObjectToByte(type));
    	convertByteArrayList.add(TypeConvert.ObjectToByte(str1));
    	sendByteArray = TypeConvert.ObjectToByte(convertByteArrayList);
		
		return sendByteArray;
	}
	
	/**
	 * 获取发送数据的byte[]
	 * @param type 类型
	 * @param str1  内容
	 * @param str2 内容
	 * @return
	 */
	public static byte[] getSendByteArray(int type, String str1, String str2){
        List<byte[]> convertByteArrayList = new ArrayList<byte[]>();
        byte[] sendByteArray = null;
    	convertByteArrayList.clear();
    	convertByteArrayList.add(TypeConvert.ObjectToByte(type));
    	convertByteArrayList.add(TypeConvert.ObjectToByte(str1));
    	convertByteArrayList.add(TypeConvert.ObjectToByte(str2));
    	sendByteArray = TypeConvert.ObjectToByte(convertByteArrayList);
		
		return sendByteArray;
	}
	

}
