package com.qrobot.mobilemanager.netty;

import java.util.ArrayList;

public interface QrobotIMNotify {
	public void IMLoginNotify(int loginResult);
	public void IMUserStatusNotify(int nQrobotNo, int bOnline);
	public void IMBatchUserNotify(int nPageNo, int nPageSize, ArrayList<MsgUserStatus> lstUserStatus, int count);
	public void IMUserDataNotify(int robotNo, String msg, int msgSize);
	public void IMClockDataNotify(int robotNo, byte[] clockData, int dataSize);
	public void IMUserDataNotify(int robotNo, byte[] msg, int dataSize);
	public void IMErrorNotify(int errCode, byte[] msg);
}
