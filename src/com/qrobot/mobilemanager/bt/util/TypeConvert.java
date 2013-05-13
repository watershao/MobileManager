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
	 * ������ת��Ϊ���л�����
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
	 * ������תΪ����
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
	 * ��ȡ�������ݵ�byte[]
	 * @param type ����
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
	 * ��ȡ�������ݵ�byte[]
	 * @param type ����
	 * @param str1  ����
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
	 * ��ȡ�������ݵ�byte[]
	 * @param type ����
	 * @param str1  ����
	 * @param str2 ����
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
