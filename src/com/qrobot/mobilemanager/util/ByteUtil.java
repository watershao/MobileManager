package com.qrobot.mobilemanager.util;

public class ByteUtil {
    
	/**
     * ����ת��Ϊ4λ�ֽ�����
     * @param intValue
     * @return
     */
    public static byte[] int2Byte(int res) {
    	byte[] targets = new byte[4];

    	targets[0] = (byte) (res & 0xff);// ���λ 
    	targets[1] = (byte) ((res >> 8) & 0xff);// �ε�λ 
    	targets[2] = (byte) ((res >> 16) & 0xff);// �θ�λ 
    	targets[3] = (byte) (res >>> 24);// ���λ,�޷������ơ� 
    	return targets; 
    }
    
    /**
     * 4λ�ֽ�����ת��Ϊ����
     * @param b
     * @return
     */
    public static int byte2Int(byte[] res) {
    	// һ��byte��������24λ���0x??000000��������8λ���0x00??0000 

    	int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | ��ʾ��λ�� 
    	| ((res[2] << 24) >>> 8) | (res[3] << 24); 
    	return targets; 
    }
 
    
}