package com.qrobot.mobilemanager.netty;

import java.io.Serializable;

import org.jboss.netty.buffer.ChannelBuffer;

public class MsgUserStatus implements Serializable
{
	public MsgUserStatus(ChannelBuffer buffer) {
		nQrobotNo = readInt(buffer);
		nOnline = readInt(buffer);
		nControlOption = readInt(buffer);
		nController = readInt(buffer);
		nConnected = readInt(buffer);
		szNickName = readString(buffer);
		nLevel = readInt(buffer);
	}
	
	public static int readInt(ChannelBuffer buffer) {
		int first = buffer.readByte() & 0x000000FF;
		int second = buffer.readByte() & 0x000000FF;
		int third = buffer.readByte() & 0x000000FF;
		int fourth = buffer.readByte() & 0x000000FF;
		return (fourth<<24) | (third<<16) | (second<<8) | first;
	}
	
	public static String readString(ChannelBuffer buffer) {
		byte[] buf = new byte[20];
		for(int i=0; i<20; i++) {
			buf[i] = buffer.readByte();
		}
		String result = null;
		try {
		result = new String(buf, "UTF-8");
		} catch(Exception e) {
		}
		return result;
	}
	
	public int getQrobotNo() {
		return nQrobotNo;
	}
	public int getOnline() {
		return nOnline;
	}
	public int getControlOption() {
		return nControlOption;
	}
	public int getController() {
		return nController;
	}
	public int getConnected() {
		return nConnected;
	}
	public String getNickName() {
		return szNickName;
	}
	public int getLevel() {
		return nLevel;
	}
	private int nQrobotNo;
	private int nOnline;
	private int nControlOption;
	private int nController;
	private int nConnected;
	private String szNickName;
	private int nLevel;
}

