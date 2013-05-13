package com.qrobot.mobilemanager.netty;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class QRClientService extends Service implements QrobotIMNotify{
	private static final String TAG = "QRClientService";
	private QRClient qrClient;
	private final IBinder binder = new MyBinder();
	
	private static final String QR_SERVER = "app31363-11.qzoneapp.com";
//	private static final String QR_SERVER = "192.168.1.254";
	public class MyBinder extends Binder {
		public QRClientService getService() {
            return QRClientService.this;
        }
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
        return binder;
	}

	public int login(int robotNo) {
		if (qrClient!=null)
			return qrClient.login(robotNo);
		return -1;
    }
	
	public int logout() {
		if (qrClient!=null)
			return qrClient.logout();
		return -1;
    }
	
	public int getBatchUsers(int pageSize, int pageNo) {
		if (qrClient!=null)
			return qrClient.getBatchUsers(pageSize, pageNo);
		return -1;
    }
	
	public int SendChat(int robotNo, String msg, int msgSize) {
		if (qrClient!=null)
			return qrClient.sendChat(robotNo, msg, msgSize);
		return -1;
    }
	
	public int sendUserData(int robotNo, byte[] msg, int dataSize) {
		if (qrClient!=null)
			return qrClient.sendUserData(robotNo, msg, dataSize);
		return -1;
    }
	
	public int setClockData(int robotNo, byte[] clockData, int dataSize) {
		if (qrClient!=null)
			return qrClient.setClockData(robotNo, clockData, dataSize);
		return -1;
    }
	
	//·¢ËÍÐÄÌø
	public int sendHeartBeat() {
		if (qrClient!=null)
			return qrClient.sendHeartBeat();
		return -1;
    }
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		qrClient = new QRClient(this, QR_SERVER, Integer.parseInt("8008"));	
		//qrClient = new QRClient(this, "192.168.110.128", Integer.parseInt("8008"));
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void IMLoginNotify(int loginResult) {
		Intent intent = new Intent("loginNotify");
        Bundle bundle = new Bundle();
        bundle.putInt("loginResult", loginResult);
        Log.d(TAG, "loginResult:"+loginResult);
        intent.putExtras(bundle);
        sendBroadcast(intent);
	}
	
	public void IMUserStatusNotify(int nQrobotNo, int bOnline) {
		Intent intent = new Intent("statusNotify");
        Bundle bundle = new Bundle();
        bundle.putInt("qrobotNo", nQrobotNo);
        bundle.putInt("online", bOnline);
        intent.putExtras(bundle);
        sendBroadcast(intent);
	}
	
	public void IMUserDataNotify(int robotNo, byte[] msg, int dataSize) {
		Intent intent = new Intent("userDataNotify");
        Bundle bundle = new Bundle();
        bundle.putInt("robotNo", robotNo);
        bundle.putByteArray("msg", msg);
        bundle.putInt("dataSize", dataSize);
        Log.d("Netty Client userData:", robotNo+"str:");
        intent.putExtras(bundle);
        sendBroadcast(intent);
	}

	public void IMClockDataNotify(int robotNo, byte[] clockData, int dataSize) {
		Intent intent = new Intent("clockDataNotify");
        Bundle bundle = new Bundle();
        bundle.putInt("robotNo", robotNo);
        bundle.putByteArray("clockData", clockData);
        bundle.putInt("dataSize", dataSize);
        Log.d("Netty service clock:", robotNo+"str:"+new String(clockData));
        
//        ClockSync clockSync = new ClockSync();
//        clockSync.downServerData(clockData);
        
        intent.putExtras(bundle);
        sendBroadcast(intent);
	}
	
	@Override
	public void IMBatchUserNotify(int nPageNo, int nPageSize,
			ArrayList<MsgUserStatus> lstUserStatus, int count) {
		// TODO Auto-generated method stub
		Intent intent = new Intent("batchUsersNotify");
        Bundle bundle = new Bundle();
        bundle.putInt("pageNo", nPageNo);
        bundle.putInt("PageSize", nPageSize);
        bundle.putSerializable("batchUsers",  lstUserStatus);
        bundle.putInt("count", count);
        intent.putExtras(bundle);
        sendBroadcast(intent);
	}
	
	public void IMUserDataNotify(int robotNo, String msg, int msgSize) {
		Intent intent = new Intent("chatDataNotify");
        Bundle bundle = new Bundle();
        bundle.putInt("robotNo", robotNo);
        bundle.putString("msg", msg);
        bundle.putInt("msgSize", msgSize);
        intent.putExtras(bundle);
        sendBroadcast(intent);
	}
	
	public void IMErrorNotify(int errCode, byte[] msg) {
		Intent intent = new Intent("errorNotify");
        Bundle bundle = new Bundle();
        bundle.putInt("errCode", errCode);
        bundle.putByteArray("errMsg", msg);
        
        Log.d("errCode:", errCode+"*"+new String(msg));
        intent.putExtras(bundle);
        sendBroadcast(intent);
	}
}
