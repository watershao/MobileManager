package com.qrobot.mobilemanager.netty;

import com.qrobot.mobilemanager.datalistener.ClockDataListener;
import com.qrobot.mobilemanager.datalistener.ErrorDataListener;
import com.qrobot.mobilemanager.datalistener.LoginDataListener;
import com.qrobot.mobilemanager.datalistener.UserDataListener;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NettyClientManager {

	private static final String TAG = "NettyClientManager";
	private Context mContext;
	
	private QRClientService clientService;
	
	private DataReceiver dataReceiver;
	
	private int nSelfID;
	private int nRemoteID;
	private static final int nIDmax = 4;
	private static final int nIDmin = 1;
	private static final int nDeltMobile = 160000;
	private static final int nDeltRobot = 150000;
	
	private LoginDataListener loginDataListener;
	private ClockDataListener clockDataListener;
	private UserDataListener userDataListener;
	private ErrorDataListener errorDataListener;
	
	public NettyClientManager(){
		
	}
	
	public NettyClientManager(Context context){
		mContext = context;
	}
	
	
	
	public void setLoginDataListener(LoginDataListener loginDataListener) {
		this.loginDataListener = loginDataListener;
	}
	
	public void setClockDataListener(ClockDataListener clockDataListener) {
		this.clockDataListener = clockDataListener;
	}

	public void setUserDataListener(UserDataListener userDataListener) {
		this.userDataListener = userDataListener;
	}

	public void setErrorDataListener(ErrorDataListener errorDataListener) {
		this.errorDataListener = errorDataListener;
	}

	/**
	 * ��netty����
	 */
	public void bindService(){
        Log.d(TAG, "binding.....");
        Intent intent = new Intent("com.qrobot.mobilemanager.netty.QRClientService");
        mContext.bindService(intent, nettyServiceConnection, Context.BIND_AUTO_CREATE);
    }
	
	public void unbindService(){
		mContext.unbindService(nettyServiceConnection);
	}
	
	private ServiceConnection nettyServiceConnection = new ServiceConnection() {
		
        @Override
        public void onServiceDisconnected(ComponentName name) {
        	clientService = null;
            Log.d(TAG, "in onServiceDisconnected");
        }
         
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        	clientService = ((QRClientService.MyBinder)(service)).getService();
            Log.d(TAG, "in onServiceConnected");
        }
	};
	
	
	public QRClientService getQrClientService(){
		return clientService;
	}
	
	/**
	 * ��½������
	 * @param id
	 * @return
	 */
	public int login(int id){
		System.out.println(" you click btnLogin");
		if(clientService != null){
//			String str0 = et01.getText().toString();
			try{
//				nSelfID = Integer.parseInt(str0);
				nSelfID = id;
//				if(nSelfID<nIDmin || nSelfID>nIDmax)
//					return;
				nRemoteID=nSelfID;
//				if(bOnPhone){
				if(true){
					nSelfID+=nDeltMobile;
					nRemoteID+=nDeltRobot;
				} else {
					nSelfID+=nDeltRobot;
					nRemoteID+=nDeltMobile;
				}					
			}catch(NumberFormatException e){
				Log.v("input error ", e.toString());
				return -1;
			}
//			int login = clientService.login(nSelfID);
			nSelfID = -id;
			nRemoteID = id;
			id = -id;
			int login = clientService.login(id);
			Log.d(TAG, id+"login:"+login);
//			nRemoteID = 112233;
			return login;
//			layout2();
        }
		return -1;
	}
	
	/**
	 * �˳���½
	 * @return
	 */
	public int logout(){
		if (clientService != null) {
			int logout = clientService.logout();
			return logout;
		}
		return -1;
	}
	
	/**
	 * ��ȡԶ��СQ id
	 * @return
	 */
	public int getRemoteId(){
		return nRemoteID;
	}
	
	/**
	 * ������������
	 * @param robotNo
	 * @param clockData
	 * @param dataSize
	 * @return
	 */
	public int sendClockData(int robotNo, byte[] clockData, int dataSize){
		if (clientService != null) {
			int ret = clientService.setClockData(robotNo, clockData, dataSize);
			return ret;
		}
		return -1;
	}
	
	/**
	 * �����û�����
	 * @param robotNo
	 * @param userData
	 * @param dataSize
	 * @return
	 */
	public int sendUserData(int robotNo, byte[] userData, int dataSize){
		if (clientService != null) {
			int ret = clientService.sendUserData(robotNo, userData, dataSize);
			Log.d(TAG, ret+",sendUserData:"+new String(userData));
			return ret;
		}
		return -1;
	}
	
	/**
	 * ע�����ݽ��չ㲥
	 */
	public void registerDataReceiver(){
		Log.d("Netty clock:", "register");
		dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();//����IntentFilter����
        filter.addAction("loginNotify");
        filter.addAction("batchUsersNotify");
        filter.addAction("statusNotify");
        filter.addAction("chatDataNotify");
        filter.addAction("userDataNotify");
        filter.addAction("clockDataNotify");
        mContext.registerReceiver(dataReceiver, filter);//ע��Broadcast Receiver
	}
	
	/**
	 * ע�����ݽ��չ㲥
	 */
	public void unregisterDataReceiver(){
		if(dataReceiver != null){
			mContext.unregisterReceiver(dataReceiver);
		}
	}
	
    private class DataReceiver extends BroadcastReceiver{//�̳���BroadcastReceiver������
    	@Override
    	public void onReceive(Context context, Intent intent) {//��дonReceive����
    		Log.d("Netty clock:", "intent:"+intent.getAction());
    		try {  	
            	Bundle bundle = intent.getExtras();	
		        //�����л����ڱ����ؽ�����
            	String action = intent.getAction();
            	if(action.equals("loginNotify")){
//            		loginResult(bundle);
            		int ret = bundle.getInt("loginResult");
            		loginDataListener.OnLoginDataListener(ret);
		        }
            	
            	if(action.equals("batchUsersNotify")){
//            		getOnlineUserResult(bundle);
		        }
            	
            	if(action.equals("statusNotify")){
            		int qrobotNo = bundle.getInt("qrobotNo");
            		int online = bundle.getInt("online");
            		String str0 = "User "+String.valueOf(qrobotNo)+"\'s online status is "+
            			String.valueOf(online);
//            		tv01.setText(str0);
		        }
            	
            	if(action.equals("chatDataNotify")){
//            		receiveChatMsg(bundle);
		        }
            	
            	if(action.equals("userDataNotify")){
            		int robotNo = bundle.getInt("robotNo");
            		byte[] msg = bundle.getByteArray("msg");
            		int dataSize = bundle.getInt("dataSize");
            		Log.d(TAG, "receiver sendUserData:"+dataSize);
            		userDataListener.OnUserDataListener(robotNo, msg, dataSize);
            		
		        }
            	
            	if(action.equalsIgnoreCase("clockDataNotify")){
            		int robotNo = bundle.getInt("robotNo");
            		byte[] clockData = bundle.getByteArray("clockData");
            		int dataSize = bundle.getInt("dataSize");
            		
            		Log.d("Netty clock1:", "no:"+robotNo);
            		clockDataListener.OnClockDataListener(robotNo, clockData, dataSize);
            		
            		Log.d("Netty clock:", "no:"+robotNo);
            		if(dataSize > 0){
            			String str = new String(clockData,"UTF-8");
            			Log.d("Netty clock:", "str:"+str);
//            			String str = msg.toString();
            			
//            			tv01.setText(str);
            		}
		        }
            	
            	if(action.equalsIgnoreCase("errorNotify")){
            		int errCode = bundle.getInt("errCode");
            		byte[] msg = bundle.getByteArray("errMsg");
            		Log.d(TAG, "receiver error:"+errCode);
            		errorDataListener.OnErrorDataListener(errCode, msg);
		        }
            	
            	
            } catch (Exception e) {
            	e.printStackTrace();
            	Log.v("test", e.toString());
            }
    	}              
    }
}
