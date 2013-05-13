package com.qrobot.mobilemanager.util;

public class ByteUtil {
    
	/**
     * 整型转换为4位字节数组
     * @param intValue
     * @return
     */
    public static byte[] int2Byte(int res) {
    	byte[] targets = new byte[4];

    	targets[0] = (byte) (res & 0xff);// 最低位 
    	targets[1] = (byte) ((res >> 8) & 0xff);// 次低位 
    	targets[2] = (byte) ((res >> 16) & 0xff);// 次高位 
    	targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。 
    	return targets; 
    }
    
    /**
     * 4位字节数组转换为整型
     * @param b
     * @return
     */
    public static int byte2Int(byte[] res) {
    	// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000 

    	int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或 
    	| ((res[2] << 24) >>> 8) | (res[3] << 24); 
    	return targets; 
    }
 
    
}