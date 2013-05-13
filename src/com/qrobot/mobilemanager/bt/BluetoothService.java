package com.qrobot.mobilemanager.bt;

import java.util.List;

import com.qrobot.mobilemanager.R;
import com.qrobot.mobilemanager.bt.util.TypeConvert;
import com.qrobot.mobilemanager.bt.util.WifiParam;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BluetoothService extends Service {

	private static final String TAG = "BluetoothService";
	
	private MyBinder mBinder = new MyBinder();
	
	private Context btContext;
	
	private BluetoothConnectManager btConnectManager;
	
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    
    private BluetoothDataListener btDataListener;
    
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	
	public class MyBinder extends Binder {
		
		public BluetoothService getService(){
			return BluetoothService.this;
		}
	}
	
	/**
	 * 设置数据监听
	 * @param btDataListener
	 */
	public void setDataListener(BluetoothDataListener btDataListener){
		this.btDataListener = btDataListener;
	}
	
	/**
	 * 初始化蓝牙连接管理器
	 */
	public void initBTConnManager(){
		if (btConnectManager == null) {
			btConnectManager = new BluetoothConnectManager(btContext, mHandler);
		}
	}
	
	/**
	 * 初始化蓝牙适配器
	 */
	public void initBTAdapter(){
		// Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "蓝牙设备不可用", Toast.LENGTH_LONG).show();
            return;
        }
	}
	
	public void openBT(){
        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            //不询问直接打开
          mBluetoothAdapter.enable();
            
            ensureDiscoverable();
            if (btConnectManager == null) initBTConnManager();
        // Otherwise, setup the chat session
        } else {
        	ensureDiscoverable();
            if (btConnectManager == null) initBTConnManager();
        }
	}
	
	public boolean isBTConnectManager(){
		if (btConnectManager != null) {
			return true;
		}
		return false;
	}
   /**
    * 使蓝牙可发现 
    */
    private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        
        waitEnable();
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(discoverableIntent);
        }
    }
	
    private  final static int WAIT_TIME = 40;
    private static final boolean D = true;
    
    /**
     * 等待蓝牙开启成功
     * @return
     */
    private boolean waitEnable(){
    	int count = WAIT_TIME;
    	while (count > 0) {
			if (mBluetoothAdapter.isEnabled()) {
				if (D) {
					Log.d(TAG, "waitEnable count=" + count);
				}
				return true;
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count--;
		}
    	return false;
    }
    
	/**
	 * 获取蓝牙状态
	 * @return  STATE_NONE = 0;  we're doing nothing。
	 *  STATE_LISTEN = 1;  now listening for incoming connections。
	 *  STATE_CONNECTING = 2; now initiating an outgoing connection。
	 *  STATE_CONNECTED = 3;  now connected to a remote device
	 */
	public int getBTState(){
		int state = btConnectManager.getState();
		return state;
	}
	
	/**
	 * 开始进行蓝牙连接
	 */
	public void startBTConnect(){
	       // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (btConnectManager != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (btConnectManager.getState() == BluetoothConnectManager.STATE_NONE) {
              // Start the Bluetooth chat services
            	btConnectManager.start();
            }
        }
	}
	/**
	 * 连接蓝牙设备
	 * @param address
	 */
	public void connectBT(String address, boolean secure){
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        btConnectManager.connect(device, secure);
	}
	
	/**
	 * 停止蓝牙连接
	 */
	public void stopBTConnect(){
        // Stop the Bluetooth chat services
        if (btConnectManager != null) btConnectManager.stop();
	}
	/**
	 * 连接蓝牙设备
	 * @param data
	 * @param secure
	 */
	public void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        btConnectManager.connect(device, secure);
    }
    
	/**
	 * 往BT里面写入数据
	 * @param out
	 */
	public void writeToBT(byte[] out){
		if (btConnectManager == null) {
			initBTConnManager();
		}
		btConnectManager.write(out);
	}
	
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
	
    /**
     *  The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
//                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                
                case BluetoothConnectManager.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
/*                    mConversationArrayAdapter.clear();
                    if (wifiListView != null) {
						wifiListView.setVisibility(View.GONE);
					}
                    
                    sendWifiMessage(WifiParam .WIFI_STATE);*/
                    btDataListener.onDataListener(MESSAGE_STATE_CHANGE, TypeConvert.ObjectToByte(msg.arg1));
                    break;
                case BluetoothConnectManager.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    btDataListener.onDataListener(MESSAGE_STATE_CHANGE, TypeConvert.ObjectToByte(msg.arg1));
                    break;
                case BluetoothConnectManager.STATE_LISTEN:
                case BluetoothConnectManager.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    btDataListener.onDataListener(MESSAGE_STATE_CHANGE, TypeConvert.ObjectToByte(msg.arg1));
/*                    currentWifiState.setText(getString(R.string.current_state));
                    
                	openButton.setVisibility(View.GONE);
                	closeButton.setVisibility(View.GONE);
                	scanButton.setVisibility(View.GONE);
                	getWifiButton.setVisibility(View.GONE);
                	changchatButton.setVisibility(View.GONE);
                	closeBluetoothButton.setVisibility(View.GONE);
                	getWifiStateButton.setVisibility(View.GONE);
                	
                	wifiStateLayout.setVisibility(View.GONE);
                	
                    if (wifiListView != null) {
						wifiListView.setVisibility(View.GONE);
					}
                	*/
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
//                String writeMessage = new String(writeBuf);
                String writeMessage = "***";
                List<byte[]> writeByteArrayList = (List<byte[]>)TypeConvert.ByteToObject((byte[])msg.obj);
                int type = (Integer)TypeConvert.ByteToObject(writeByteArrayList.get(0));
                if (type == WifiParam.MESSAGE_TEXT) {
					writeMessage = (String)TypeConvert.ByteToObject(writeByteArrayList.get(1));
				}
//                mConversationArrayAdapter.add("Me:  " + writeMessage);
                btDataListener.onDataListener(MESSAGE_WRITE, writeBuf);
                Log.w(TAG, "writeMessage:"+writeMessage); 
                break;
            case MESSAGE_READ:
//                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
 //               String readMessage = new String(readBuf, 0, msg.arg1);
//                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
               
//            	handleReadMessage(msg);
            	
                byte[] readBuf = (byte[]) msg.obj;
                Log.d(TAG, "readBuf size."+readBuf.length);
                List<byte[]> readByteArrayList = (List<byte[]>)TypeConvert.ByteToObject((byte[])msg.obj);
                if (readByteArrayList == null || readByteArrayList.size() == 0) {
	               	 Log.d(TAG, "no message handle.");
	       			 return;
                }
                       
                btDataListener.onDataListener(MESSAGE_READ,readBuf);       
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "成功连接到设备  "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    
    private void setStatus(String status){
    	
    }
    
    private void setStatus(int statusId){
    	
    }
}
