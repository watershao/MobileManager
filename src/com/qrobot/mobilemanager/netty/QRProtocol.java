package com.qrobot.mobilemanager.netty;

import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;

enum Command
{
	IM_FUNC_LOGIN(1),
	IM_FUNC_LOGIN_RESULT(2),
	IM_FUNC_LOGOUT(3),
	IM_FUNC_ADDFRIEND(4),
	IM_FUNC_ACCEPTFRIEND(5),
	IM_FUNC_CHAT(6),
	IM_FUNC_USERDATA(7),
	IM_FUNC_LOGINOUT_NOTIFY(8),
	IM_FUNC_CONTROL_QROBOT(9),
	IM_FUNC_CONTROL_RESULT(10),
	IM_FUNC_SET_CONTROL_OPTION(11),
	IM_FUNC_CANCEL_CONTROL(12),
	IM_FUNC_GET_USER_STATUS(14),
	IM_FUNC_GET_USER_STATUS_ACK(15),
	IM_FUNC_OPERATION_ERROR(16),
	IM_FUNC_REMOVEFRIEND(17),
	IM_FUNC_GET_RECENT_CONTACTS(18),
	IM_FUNC_GET_RECENT_CONTACTS_ACK(19),
	IM_FUNC_GET_MY_FRIENDS(20),
	IM_FUNC_GET_MY_FRIENDS_ACK(21),
	IM_FUNC_GET_BATCH_USERS(22),
	IM_FUNC_GET_BATCH_USERS_ACK(23),
	IM_FUNC_CONTROL_STATUS_NOTIFY(24),
	IM_FUNC_CONTROL_OPTION_NOTIFY(25),
	IM_FUNC_CANCEL_CONTROL_REQUEST(26),
	IM_FUNC_HEART_BEAT(27),
	IM_FUNC_REMOVE_CONTACT(28),
	IM_FUNC_SET_CLOCKDATA(29);
	
	private int value;
	 
	 private Command(int value){
	  this.value = value;
	 }
	 //alt+shift+s
	 public int getValue() {
	        return value;
	    }
}
	
public class QRProtocol {
	private byte[] buf = null;  
	  
	/** 
	* 将int转为低字节在前，高字节在后的byte数组 
	*/  
	private static byte[] toLH(int n) {  
		byte[] b = new byte[4];  
		b[0] = (byte) (n & 0xff);  
		b[1] = (byte) (n >> 8 & 0xff);  
		b[2] = (byte) (n >> 16 & 0xff);  
		b[3] = (byte) (n >> 24 & 0xff);  
		return b;  
	}  
	/** 
	* 将short转为byte数组 
	*/  
	private static byte[] toSLH(short n) {  
		byte[] b = new byte[2];  
		b[0] = (byte) (n >>8 & 0xff);  
		b[1] = (byte) (n & 0xff);    
		return b;  
	}  
	/** 
	* 构造并转换 
	*/  
	public QRProtocol(int nFunction, short packetLen) {  
		byte[] temp = null;  
		  
		buf = new byte[packetLen + 2];  
		temp = toSLH(packetLen);  
		System.arraycopy(temp, 0, buf, 0, temp.length);  
		  
		temp = toLH(nFunction);  
		System.arraycopy(temp, 0, buf, 2, temp.length);  
	  
	}  
	
	public void setContent(int content, int pos) {
		byte[] temp = null; 
		temp = toLH(content);  
		System.arraycopy(temp, 0, buf, pos, temp.length);  
	}
	public void setContent(byte[] content, int len, int pos) {
		System.arraycopy(content, 0, buf, pos, len);  
	}
	/** 
	* 返回要发送的数组 
	*/  
	public byte[] getBuf() {  
		return buf;  
	}  
}

/**
 * 登录
 */
class MsgLogin extends QRProtocol
{
	public MsgLogin(int nFunction, short packetLen, int nQrobotNo) {
		super(nFunction, packetLen);
		setContent(nQrobotNo, 6);
	}
}

/**
 * 退出登录
 */
class MsgLogout extends QRProtocol
{
	public MsgLogout(int nFunction, short packetLen, int nReserved) {
		super(nFunction, packetLen);
		setContent(nReserved, 6);
	}
}

/**
 * 获取一批用户状态
 */
class MsgGetBatchUsers extends QRProtocol 
{
	public MsgGetBatchUsers(int nFunction, short packetLen, int nPageSize, int nPageNo) {
		super(nFunction, packetLen);
		setContent(nPageSize, 6);
		setContent(nPageNo, 10);
	}
}


class MsgSendUserData extends QRProtocol
{
	public MsgSendUserData(int nFunction, short packetLen, int nQrobotNo, int nDataSize, byte[] msg) {
		super(nFunction, packetLen);
		setContent(nQrobotNo, 6);
		setContent(nDataSize, 10);
		setContent(msg, nDataSize, 14);
	}
}

class MsgSetClockData extends QRProtocol
{
	public MsgSetClockData(int nFunction, short packetLen, int nQrobotNo, byte[] clockData, int nDataSize) {
		super(nFunction, packetLen);
		setContent(nQrobotNo, 6);
		setContent(clockData, nDataSize, 10);
	}
}

//心跳消息
class MsgHeartBeat extends QRProtocol
{
	public MsgHeartBeat(int nFunction, short packetLen, int nReserved) {
		super(nFunction, packetLen);
		setContent(nReserved, 6);
	}
}

class MsgImChat extends QRProtocol
{
	public MsgImChat(int nFunction, short packetLen, int nQrobotNo, int nMsgSize, byte[] msg) {
		super(nFunction, packetLen);
		setContent(nQrobotNo, 6);
		setContent(nMsgSize, 10);
		setContent(msg, nMsgSize, 14);
	}
}

class ProtocolResult {
	public ProtocolResult(int nFunction) {
		this.nFunction = nFunction;
	}
	private int nFunction;
	
	public static byte[] readBytes(ChannelBuffer buffer, int len) {
		byte[] buf = new byte[len];
		for(int i=0; i<len; i++) {
			buf[i] = buffer.readByte();
		}
		return buf;
	}
	
	public static int readInt(ChannelBuffer buffer) {
		int first = buffer.readByte() & 0x000000FF;
		int second = buffer.readByte() & 0x000000FF;
		int third = buffer.readByte() & 0x000000FF;
		int fourth = buffer.readByte() & 0x000000FF;
		return (fourth<<24) | (third<<16) | (second<<8) | first;
	}
}

class MsgErrorResult extends ProtocolResult
{
	public MsgErrorResult(int nFunction, ChannelBuffer buffer) {
		super(nFunction);
		errorCode = readInt(buffer);
		errMsg = readBytes(buffer, 128);
	}
	private int errorCode;
	private byte[] errMsg;
	public int getErrCode() {
		return errorCode;
	}
	public byte[] getErrMsg() {
		return errMsg;
	}
}
//登录响应
class MsgLoginResult extends ProtocolResult
{
	public MsgLoginResult(int nFunction, ChannelBuffer buffer) {
		super(nFunction);
		result = readInt(buffer);
	}
	private int result;
	
	public int getResult() {
		return result;
	}
};

/**
 * 户登录或者退出通知消息
 */
class MsgLoginoutNotify extends ProtocolResult
{
	public MsgLoginoutNotify(int nFunction, ChannelBuffer buffer) {
		super(nFunction);
		robotNo = readInt(buffer);
		notify = readInt(buffer);
	}
	private int robotNo;
	private int notify;
	
	public int getRobotNo() {
		return robotNo;
	}
	public int getNotify() {
		return notify;
	}
}

/**
 * 获取一批用户状态的回应
 */
class MsgGetBachUserAck extends ProtocolResult
{
	public MsgGetBachUserAck(int nFunction, ChannelBuffer buffer) {
		super(nFunction);
		nPageSize = readInt(buffer);
		nPageNo = readInt(buffer);
		nCount = readInt(buffer);
		arrUserStatus = new ArrayList<MsgUserStatus>();
		for (int i=0; i<nCount; i++) {
			MsgUserStatus userStatus = new MsgUserStatus(buffer);
			arrUserStatus.add(userStatus);
		}
	}
	private int nPageSize;
	private int nPageNo;
	private int nCount;
	private ArrayList<MsgUserStatus> arrUserStatus;
	
	public int getPageSize() {
		return nPageSize;
	}
	public int getPageNo() {
		return nPageNo;
	}
	public int getCount() {
		return nCount;
	}
	public ArrayList<MsgUserStatus> getUserStatus() {
		return arrUserStatus;
	}
}

/**
 * 获取聊天消息回应
 */
class MsgGetChatData extends ProtocolResult
{
	public MsgGetChatData(int nFunction, ChannelBuffer buffer) {
		super(nFunction);
		nRobotNo = readInt(buffer);
		nMsgSize = readInt(buffer);
		msg = readBytes(buffer, nMsgSize);
	}
	private int nRobotNo;
	private int nMsgSize;
	private byte[] msg;
	
	public int getRobotNo() {
		return nRobotNo;
	}
	public int getMsgSize() {
		return nMsgSize;
	}
	public byte[] getMsg() {
		return msg;
	}
}

/**
 * 获取闹钟设置消息回应
 */
class MsgGetClockData extends ProtocolResult
{
	public MsgGetClockData(int nFunction, ChannelBuffer buffer) {
		super(nFunction);
		nRobotNo = readInt(buffer);
		nDataSize = readInt(buffer);
		clockData = readBytes(buffer, nDataSize);
	}
	private int nRobotNo;
	private int nDataSize;
	private byte[] clockData;
	
	public int getRobotNo() {
		return nRobotNo;
	}

	public int getDataSize() {
		return nDataSize;
	}
	
	public byte[] getClockData() {
		return clockData;
	}
}

/**
 * 获取一批用户状态的回应
 */
class MsgGetUserData extends ProtocolResult
{
	public MsgGetUserData(int nFunction, ChannelBuffer buffer) {
		super(nFunction);
		nRobotNo = readInt(buffer);
		nDataSize = readInt(buffer);
		msg = readBytes(buffer, nDataSize);
	}
	private int nRobotNo;
	private int nDataSize;
	private byte[] msg;
	
	public int getRobotNo() {
		return nRobotNo;
	}
	public int getDataSize() {
		return nDataSize;
	}
	public byte[] getMsg() {
		return msg;
	}
}