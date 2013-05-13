package com.qrobot.mobilemanager.bt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.qrobot.mobilemanager.R;
import com.qrobot.mobilemanager.bt.util.TypeConvert;
import com.qrobot.mobilemanager.bt.util.WifiParam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class BluetoothFragment extends Fragment {

	private static final String TAG = "BluetoothFragment";
	
	private static final boolean D = true;
	
	private BluetoothService btService;
	private Context mContext;
	
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
	
    
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    
    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    
    private Button searchBT;
    
	/**
	 * bluetooth ui
	 */
	private LinearLayout btMainLayout;
	
    private ListView wifiListView = null;
    
    private TextView currentWifiState = null;
	
	public BluetoothFragment() {

	}
	
	public BluetoothFragment(Context context ,BluetoothService btService){
		this.mContext = context;
		this.btService = btService;
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		printW("onCreate"+btService+"*"+btDataListener);
		btService.initBTAdapter();
		btService.setDataListener(btDataListener);
	}

/*	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		getFragmentManager().putFragment(outState, "mContent", this);
		printW("onSaveInstanceState");
	}*/

	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		printW("onAttach:"+activity.getPackageName());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		printW("onCreateOptionsMenu");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		printW("onCreateView"+btService);
		btMainLayout = (LinearLayout)inflater.inflate(R.layout.bt_main, null);
		manageWifi();
		
		if (!btService.isBTConnectManager()) {
			btService.openBT();
			setupChat();
		}
		
		searchBT = (Button) btMainLayout.findViewById(R.id.button_searchbluetooth);
		searchBT.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	               Intent serverIntent = new Intent(mContext, DeviceListActivity.class);
       //          serverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	               startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			}
			
		});
		return btMainLayout;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		printW("onDestroy");
		super.onDestroy();
		btService.stopBTConnect();
		
	}

	@Override
	public void onPause() {
		printW("onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		printW("onResume");
		super.onResume();
		btService.startBTConnect();
		
	}

	@Override
	public void onStart() {
		printW("onStart");
		super.onStart();
		
//		if (!btService.isBTConnectManager()) {
//			btService.openBT();
//			setupChat();
//		}
		
/*        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            //不询问直接打开
//          mBluetoothAdapter.enable();
            
            ensureDiscoverable();
            if (bt == null) setupChat();
        // Otherwise, setup the chat session
        } else {
        	ensureDiscoverable();
            if (mChatService == null) setupChat();
        }*/
	}

	@Override
	public void onStop() {
		printW("onStop");
		super.onStop();
	}

	private BluetoothDataListener btDataListener = new BluetoothDataListener() {
		
		@Override
		public void onDataListener(int type, byte[] read) {

			switch (type) {
			
			case BluetoothService.MESSAGE_STATE_CHANGE:
				int typeCode = (Integer)TypeConvert.ByteToObject(read);
				handleStateChange(typeCode);
                break;
          
            
	        case BluetoothService.MESSAGE_WRITE:
//	            byte[] writeBuf = (byte[]) msg.obj;
	            // construct a string from the buffer
	//            String writeMessage = new String(writeBuf);
	        	
	            String writeMessage = "***";
	            List<byte[]> writeByteArrayList = (List<byte[]>)TypeConvert.ByteToObject(read);
	            int typeId = (Integer)TypeConvert.ByteToObject(writeByteArrayList.get(0));
	            if (typeId == WifiParam.MESSAGE_TEXT) {
					writeMessage = (String)TypeConvert.ByteToObject(writeByteArrayList.get(1));
				}
	            mConversationArrayAdapter.add("Me:  " + writeMessage);
	            Log.w(TAG, "writeMessage:"+writeMessage); 
	            break;
	        case BluetoothService.MESSAGE_READ:
	//            byte[] readBuf = (byte[]) msg.obj;
	            // construct a string from the valid bytes in the buffer
	//               String readMessage = new String(readBuf, 0, msg.arg1);
	//            mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
	           
	//        	handleReadMessage(msg);
	        	
//	            byte[] readBuf = (byte[]) msg.obj;
//	            Log.d(TAG, "readBuf size."+readBuf.length);
	            List<byte[]> readByteArrayList = (List<byte[]>)TypeConvert.ByteToObject(read);
	            if (readByteArrayList == null || readByteArrayList.size() == 0) {
	               	 Log.d(TAG, "no message handle.");
	       			 return;
	            }
	            handleReadData(read);       
	            break;	
			default:
				break;
		}}
	};
	
	
	private void handleStateChange(int type) {
		switch (type) {

		case BluetoothConnectManager.STATE_CONNECTED:
			// setStatus(getString(R.string.title_connected_to,
			// mConnectedDeviceName));
			Toast.makeText(mContext,""+ getString(R.string.title_connected_to,
							mConnectedDeviceName), Toast.LENGTH_LONG).show();
			mConversationArrayAdapter.clear();
			if (wifiListView != null) {
				wifiListView.setVisibility(View.GONE);
			}

			sendWifiMessage(WifiParam.WIFI_STATE);

			break;
		case BluetoothConnectManager.STATE_CONNECTING:
//			setStatus(R.string.title_connecting);
			Toast.makeText(mContext,""+ getString(R.string.title_connecting,
					mConnectedDeviceName), Toast.LENGTH_LONG).show();
			
			break;
		case BluetoothConnectManager.STATE_LISTEN:
		case BluetoothConnectManager.STATE_NONE:
//			setStatus(R.string.title_not_connected);
			Toast.makeText(mContext,""+ getString(R.string.title_not_connected,
					mConnectedDeviceName), Toast.LENGTH_LONG).show();
			
			currentWifiState.setText(getString(R.string.current_state));

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
			break;
		}
	}
	
	
    private Button openButton = null;
    private Button closeButton = null;
    private Button scanButton = null;
    private Button getWifiButton = null;
    private Button changchatButton = null;
    private Button closeBluetoothButton = null;
    private Button getWifiStateButton = null;
    private LinearLayout wifiStateLayout = null;
    
    /**
     * 管理服务端wifi
     */
    private void manageWifi(){
    	openButton = (Button)btMainLayout.findViewById(R.id.button_openwifi);
    	closeButton = (Button)btMainLayout.findViewById(R.id.button_closewifi);
    	scanButton = (Button)btMainLayout.findViewById(R.id.button_scanwifi);
    	getWifiButton = (Button)btMainLayout.findViewById(R.id.button_getwifi);
    	changchatButton = (Button)btMainLayout.findViewById(R.id.button_changechat);
    	
    	closeBluetoothButton = (Button)btMainLayout.findViewById(R.id.button_closebluetooth);
    	getWifiStateButton = (Button)btMainLayout.findViewById(R.id.button_getwifi_state); 
    	currentWifiState = (TextView)btMainLayout.findViewById(R.id.current_state);
    	
    	wifiStateLayout = (LinearLayout)btMainLayout.findViewById(R.id.wifi_state_layout);
    	
//    	openButton.setVisibility(View.VISIBLE);
//    	closeButton.setVisibility(View.VISIBLE);
//    	scanButton.setVisibility(View.VISIBLE);
//    	getWifiButton.setVisibility(View.VISIBLE);
//    	changchatButton.setVisibility(View.VISIBLE);
//    	closeBluetoothButton.setVisibility(View.VISIBLE);
//    	getWifiStateButton.setVisibility(View.VISIBLE);
    	
    	openButton.setVisibility(View.GONE);
    	closeButton.setVisibility(View.GONE);
    	scanButton.setVisibility(View.GONE);
    	getWifiButton.setVisibility(View.GONE);
    	changchatButton.setVisibility(View.GONE);
    	closeBluetoothButton.setVisibility(View.VISIBLE);
    	getWifiStateButton.setVisibility(View.GONE);
    	
    	wifiStateLayout.setVisibility(View.GONE);
    	
    	OnClickListener wifiClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				
				case R.id.button_getwifi_state:
//					ActionBar actionBar = getActionBar();
/*					ActionBar actionBar = (new BaseActivity(R.string.bluetooth)).getSupportActionBar();
					CharSequence bluetoothConnect = actionBar.getSubtitle();
					
					if (bluetoothConnect.toString().contains("没有连接")) {
						Toast.makeText(mContext, "请确保蓝牙设备已连接", Toast.LENGTH_LONG).show();
					}else {
						currentWifiState.setText(R.string.wifi_state_getting);
						sendWifiMessage(WifiParam.WIFI_STATE);
					}*/
					
					currentWifiState.setText(R.string.wifi_state_getting);
					sendWifiMessage(WifiParam.WIFI_STATE);
					
					break;

				case R.id.button_openwifi:
					if (!mConversationView.isShown() && wifiListView != null) {
						wifiListView.setVisibility(View.GONE);
						mConversationView.setVisibility(View.VISIBLE);
					}
					sendWifiMessage(WifiParam.WIFI_OPEN);
					
					break;
					
				case R.id.button_closewifi:
					if (!mConversationView.isShown() && wifiListView != null) {
						wifiListView.setVisibility(View.GONE);
						mConversationView.setVisibility(View.VISIBLE);
					}
	    			sendWifiMessage(WifiParam.WIFI_CLOSE);
					break;
				
				case R.id.button_scanwifi:
					if (!mConversationView.isShown() && wifiListView != null) {
						wifiListView.setVisibility(View.GONE);
						mConversationView.setVisibility(View.VISIBLE);
					}
	    			sendWifiMessage(WifiParam.WIFI_SCAN);
					break;
				case R.id.button_getwifi:
					if (mConversationView.isShown() && wifiListView != null) {
						mConversationView.setVisibility(View.GONE);
						wifiListView.setVisibility(View.VISIBLE);
					}
					if(wifiSet != null && !wifiSet.isEmpty()){
	    				wifiSet.clear();
	    			}
	    			sendWifiMessage(WifiParam.WIFI_SCAN_RESULTS);
					break;
				case R.id.button_changechat:
					if (!mConversationView.isShown() && wifiListView != null) {
						wifiListView.setVisibility(View.GONE);
						mConversationView.setVisibility(View.VISIBLE);
					}
					break;	
				case R.id.button_closebluetooth:
					if (!mConversationView.isShown() && wifiListView != null) {
						wifiListView.setVisibility(View.GONE);
						mConversationView.setVisibility(View.VISIBLE);
					}
					sendWifiMessage(WifiParam.BLUE_CLOSE);
					mConversationArrayAdapter.add("关闭已连接设备蓝牙。");
					break;
				default:
					break;
				}
			}
		};
		
		getWifiStateButton.setOnClickListener(wifiClickListener);
    	openButton.setOnClickListener(wifiClickListener);
    	closeButton.setOnClickListener(wifiClickListener);
    	scanButton.setOnClickListener(wifiClickListener);
    	getWifiButton.setOnClickListener(wifiClickListener);
    	changchatButton.setOnClickListener(wifiClickListener);
    	closeBluetoothButton.setOnClickListener(wifiClickListener);
    }
    
    
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(btService, R.layout.bt_message);
        mConversationView = (ListView) btMainLayout.findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText)  btMainLayout.findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button)  btMainLayout.findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            @Override
			public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView)  btMainLayout.findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
//                sendFile("");
//                Intent serverIntent = new Intent(mContext, DeviceListActivity.class);
      //          serverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
 //               startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
//        mChatService = new BluetoothConnectManager(this, mHandler);

//        btService.initBTConnManager();
        
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        @Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };
    
   
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (btService.getBTState() != BluetoothConnectManager.STATE_CONNECTED) {
            Toast.makeText(mContext, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
//            mChatService.write(send);
        	
    		byte[] sendByteArray = TypeConvert.getSendByteArray(WifiParam.MESSAGE_TEXT,message);
//    		mChatService.write(sendByteArray);

    		btService.writeToBT(sendByteArray);
    		
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }
    
    
    /**
     * 发送 wifi控制消息id
     * @param typeId
     */
    private void sendWifiMessage(int messageId) {
        // Check that we're actually connected before trying anything
        if (btService.getBTState() != BluetoothConnectManager.STATE_CONNECTED) {
            Toast.makeText(mContext, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] sendByteArray = TypeConvert.getSendByteArray(messageId);
//        mChatService.write(sendByteArray);
        
        btService.writeToBT(sendByteArray);
     }
    
    
    /**
     * 处理读取到的数据
     * @param readBuf
     */
    private void handleReadData(byte[] readBuf){
//        byte[] readBuf = (byte[]) msg.obj;
        Log.d(TAG, "readBuf size."+readBuf.length);
//        List<byte[]> readByteArrayList = (List<byte[]>)TypeConvert.ByteToObject((byte[])msg.obj);
        List<byte[]> readByteArrayList = (List<byte[]>)TypeConvert.ByteToObject(readBuf);
        if (readByteArrayList == null || readByteArrayList.size() == 0) {
        	Log.d(TAG, "no message handle.");
			return;
		}
        int type = (Integer)TypeConvert.ByteToObject(readByteArrayList.get(0));
        switch (type) {
        
		case WifiParam.WIFI_OPEN:
			String open = (String)TypeConvert.ByteToObject(readByteArrayList.get(1));
			if (open.equalsIgnoreCase("true")) {
				open = "打开wifi成功";
			} else if (open.equalsIgnoreCase("false")) {
				open = "打开wifi失败";
			}
			mConversationArrayAdapter.add(mConnectedDeviceName+":  " + open);

			break;
		case WifiParam.WIFI_CLOSE:
			String close = (String)TypeConvert.ByteToObject(readByteArrayList.get(1));
			if (close.equalsIgnoreCase("true")) {
				close = "关闭wifi成功";
			} else if(close.equalsIgnoreCase("false")) {
				close = "关闭wifi失败";
			}
			mConversationArrayAdapter.add(mConnectedDeviceName+":  " + close);

			break;
		case WifiParam.WIFI_SCAN:
			String scan = (String)TypeConvert.ByteToObject(readByteArrayList.get(1));
			mConversationArrayAdapter.add(mConnectedDeviceName+":  " + scan);
			
			if(D){
//				Toast.makeText(BluetoothChat.this, scan, Toast.LENGTH_LONG).show();
			}
			break;
		case WifiParam.WIFI_SCAN_RESULTS:
			Log.d(TAG, "get scan result.");
			if(readByteArrayList.size() < 2){
				Toast.makeText(mContext, "请先扫描wifi热点", Toast.LENGTH_LONG).show();
				return;
			}
			
			List<Map<String, String>> wifiInfoList = (List<Map<String, String>>)TypeConvert.ByteToObject(readByteArrayList.get(1));
//			String scanResult = "";
//			if (wifiInfoList != null && wifiInfoList.size() > 0) {
//				for (int i = 0; i < wifiInfoList.size(); i++) {
//					Map<String, String> wifiInfoMap = new HashMap<String, String>();
//					wifiInfoMap = wifiInfoList.get(i);
//					scanResult += wifiInfoMap.get(WifiParam.SCANRESULT_SSID)+
//							"*"+wifiInfoMap.get(WifiParam.SCANRESULT_BSSID)
//							+"*"+wifiInfoMap.get(WifiParam.SCANRESULT_CAPABILITY)+
//							"*"+wifiInfoMap.get(WifiParam.SCANRESULT_LEVEL)
//							+"*"+wifiInfoMap.get(WifiParam.SCANRESULT_FREQUENCY)+"@";
//					Log.d(TAG, "scan result."+scanResult);
//				}
//			}
			
			if(wifiInfoList != null && wifiInfoList.size() > 0){
				wifiSet.addAll(wifiInfoList);
	
				Log.d(TAG, "wifiSet:"+wifiSet.size());
				createWifiListView(wifiSet, true);
			}else {
				Toast.makeText(mContext, "对不起，没有找到wifi热点，请尝试重新扫描。", Toast.LENGTH_LONG).show();
			}
//			 mConversationArrayAdapter.add(mConnectedDeviceName+":  " + scanResult);
			break;
		case WifiParam.WIFI_PASSWORD:
			String connect = (String)TypeConvert.ByteToObject(readByteArrayList.get(1));	
//			if (connect.equalsIgnoreCase("true")) {
//				Toast.makeText(BluetoothChat.this, "操作Wifi密码成功", Toast.LENGTH_LONG).show();
//			} else {
//				Toast.makeText(BluetoothChat.this, "操作Wifi密码设置失败，请尝试重新设置", Toast.LENGTH_LONG).show();
//			}
			
			break;
			
		case WifiParam.WIFI_STATE:
			String wifiState = (String)TypeConvert.ByteToObject(readByteArrayList.get(1));
			currentWifiState.setText(wifiState);
			Log.w(TAG, ">>>>>>wifistate:"+wifiState);
			
			handleWifiState(wifiState);
			
			break;
		case WifiParam.MESSAGE_TEXT:
			// construct a string from the valid bytes in the buffer
//			String readMessage = new String(readBuf, 0, msg.arg1);
			String readMessage = (String)TypeConvert.ByteToObject(readByteArrayList.get(1));
			mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
			
		default:
			break;
		}
        
    }
    
    /**
     * wifi信息的set
     */
    private Set wifiSet = new HashSet();
    
    /**
     * 创建wifi Listview视图
     * @param lms
     */
    private void createWifiListView(Set set, boolean isConnected){
//    	private void createWifiList(List<Map<String, String>> lms){
    	List<Map<String, String>> wifiInfoList = new ArrayList<Map<String,String>>();
    	wifiInfoList.addAll(set);
    	
    	if (isConnected) {
    		String wifiState = currentWifiState.getText().toString();
    		String ssid = "";
    		Map<String, String> wifiInfo = new HashMap<String, String>();
    		if (wifiState.contains("已连接")) {
				ssid = wifiState.substring(0, wifiState.length()-3).trim();
				for (int i = 0; i < wifiInfoList.size(); i++) {
					wifiInfo = wifiInfoList.get(i);
					if (wifiInfo.get(WifiParam.WIFICONFIG_SSID).equalsIgnoreCase(ssid)) {
						wifiInfo.put(WifiParam.SCANRESULT_IS_CONNECTED, "已连接");
						wifiInfo.put(WifiParam.SCANRESULT_IS_CONFIGED, "true");
						break;
					}
				}
			} else {
				for (int i = 0; i < wifiInfoList.size(); i++) {
					wifiInfo = wifiInfoList.get(i);
					if (wifiInfo.get(WifiParam.SCANRESULT_IS_CONNECTED).equalsIgnoreCase("已连接")) {
						wifiInfo.put(WifiParam.SCANRESULT_IS_CONNECTED, "");
					}
				}
			}
		}
    	
    	
    	wifiListView = (ListView)btMainLayout.findViewById(R.id.wifilist);
    	SimpleAdapter adapter = new SimpleAdapter(mContext, wifiInfoList, 
    			 R.layout.bt_wifi_item, new String[] { WifiParam.SCANRESULT_SSID, 
    			 WifiParam.SCANRESULT_BSSID, WifiParam.SCANRESULT_CAPABILITY,
    			 WifiParam.SCANRESULT_LEVEL, WifiParam.SCANRESULT_FREQUENCY,
    			 WifiParam.SCANRESULT_IS_CONFIGED,WifiParam.SCANRESULT_IS_CONNECTED}, 
    			 new int[] { R.id.wifi_ssid, R.id.wifi_bssid,
    			 R.id.wifi_capability,R.id.wifi_level,R.id.wifi_frequence,
    			 R.id.wifi_is_configed,R.id.wifi_is_connected});
    	
    	 mConversationView.setVisibility(View.GONE);
    	 wifiListView.setVisibility(View.VISIBLE);
    	 wifiListView.setAdapter(adapter);
    	 final List<Map<String, String>> mData = wifiInfoList;
    	 wifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				buildDialog(mContext, mData, position);
				
			}
		
    	 });
    }
    
    /**
     * 创建wifi设置的窗口
     * @param context
     * @param lms
     * @param position
     * @return
     */
    private void buildDialog(Context context, final List<Map<String, String>> wifiInfoList,final int position) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        final View wifiInfoView=inflater.inflate(R.layout.bt_wifi_info, null);
        final TextView wifiPassword = (TextView)wifiInfoView.findViewById(R.id.wifi_password);
        final CheckBox pwCb = (CheckBox)wifiInfoView.findViewById(R.id.wifi_show_password);
        final EditText pwEt = (EditText)wifiInfoView.findViewById(R.id.wifi_password_edit);
        TextView signalTV = (TextView)wifiInfoView.findViewById(R.id.wifi_signal_info);
        signalTV.setText(wifiInfoList.get(position).get(WifiParam.SCANRESULT_LEVEL));
        pwCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
				// 显示密码
					pwEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					// 隐藏密码
					pwEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
        
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        final String ssid = wifiInfoList.get(position).get(WifiParam.SCANRESULT_SSID);
        final String wifiConfiged = wifiInfoList.get(position).get(WifiParam.SCANRESULT_IS_CONFIGED);
        final String wifiConnected = wifiInfoList.get(position).get(WifiParam.SCANRESULT_IS_CONNECTED);
        
        builder.setTitle(ssid);
        
        builder.setView(wifiInfoView); //关键
        builder.setPositiveButton(R.string.wifi_connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            	//setTitle("单击对话框上的确定按钮");
            	if (wifiConfiged.equalsIgnoreCase("true")) {
                	byte[] sendByteArray = TypeConvert.getSendByteArray(WifiParam.WIFI_PASSWORD, ssid, "");
//					mChatService.write(sendByteArray);
					
					btService.writeToBT(sendByteArray);
				} else {
					String password = pwEt.getText().toString().trim();
					if (password != null && password.length() > 7) {
						byte[] sendByteArray = TypeConvert.getSendByteArray(WifiParam.WIFI_PASSWORD, ssid, password);
//						mChatService.write(sendByteArray);
						
						btService.writeToBT(sendByteArray);
						
						dialog.cancel();
					}else {
						Toast.makeText(mContext, "请输入密码。", Toast.LENGTH_LONG).show();
					}
				}
            }
        });
        builder.setNegativeButton(R.string.wifi_cancel,new DialogInterface.OnClickListener() {
             
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        builder.setNeutralButton("忘记", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				byte[] sendByteArray = TypeConvert.getSendByteArray(WifiParam.WIFI_FORGET, ssid);
				
//				mChatService.write(sendByteArray);
				
				btService.writeToBT(sendByteArray);
				
				wifiInfoList.get(position).put(WifiParam.SCANRESULT_IS_CONFIGED,"false");
				
				}
		});
        Dialog dialog = builder.create();
        enableDialogButton(dialog, pwEt, wifiConfiged,wifiConnected,wifiPassword,pwCb);
        dialog.show();
        
    }
    
    /**
     * 使dialog里面的buton可用
     * @param dialog
     */
    private void enableDialogButton(final Dialog dialog , 
    		final EditText editText, final String wifiConfiged,
    		final String wifiConnected,
    		final TextView wifiPassword, final CheckBox pwCheckBox){
    	dialog.setOnShowListener(new DialogInterface.OnShowListener(){//开始监听 show
            private Button positiveButton;
            private Button neutralButton;
            @Override
            public void onShow(DialogInterface arg0) {
                if(positiveButton == null)//先初始化相关的按钮
                    positiveButton = ((AlertDialog)arg0).getButton(DialogInterface.BUTTON_POSITIVE);
                if(neutralButton == null){
                	neutralButton = ((AlertDialog)arg0).getButton(DialogInterface.BUTTON_NEUTRAL);
                }
                if (wifiConfiged.equalsIgnoreCase("true")) {
                	if (wifiConnected.equalsIgnoreCase("已连接")) {
                		positiveButton.setVisibility(View.GONE);
					} else {
						neutralButton.setVisibility(View.GONE);
					}
					editText.setVisibility(View.GONE);
					wifiPassword.setVisibility(View.GONE);
					pwCheckBox.setVisibility(View.GONE);
				}else {
					positiveButton.setEnabled(false);
					neutralButton.setVisibility(View.GONE);
				}

            }});
    	editText.addTextChangedListener(new TextWatcher() {
            private Button positiveButton;
            @Override
			public void onTextChanged(CharSequence s, int start, 
            						int before, int count) {

                if(positiveButton == null) {//此时dialog已经show出来了 所以应该也可以拿到按钮对象。
                        positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                }
                String content = s.toString();
                if(content == null || content.equals("") || content.trim().length() < 8) {
                        positiveButton.setEnabled(false);
                    } else {
                        positiveButton.setEnabled(true);
                    }
            
            }
            @Override
			public void beforeTextChanged(CharSequence s, int start,
                    int count, int after) {

            }
            
            @Override
			public void afterTextChanged(Editable s) {
            	
            }
        });
    }
    
    /**
     * 根据wifi状态显示界面信息
     * @param wifiState
     */
    private void handleWifiState(String wifiState){
    	if (wifiState.contains("当前wifi没有打开") || 
    			wifiState.contains("未知") ||
//    			wifiState.contains("已断开连接")||
    			wifiState.contains("已暂停")) {
        	
        	openButton.setVisibility(View.VISIBLE);
        	closeButton.setVisibility(View.GONE);
        	scanButton.setVisibility(View.GONE);
        	getWifiButton.setVisibility(View.GONE);
        	changchatButton.setVisibility(View.GONE);
        	closeBluetoothButton.setVisibility(View.VISIBLE);
        	getWifiStateButton.setVisibility(View.VISIBLE);
        	
        	wifiStateLayout.setVisibility(View.VISIBLE);
        	
        	
		}
    	
    	if(wifiState.contains("已连接") ||
    			wifiState.contains("扫描")){
        	openButton.setVisibility(View.GONE);
        	closeButton.setVisibility(View.GONE);
        	scanButton.setVisibility(View.VISIBLE);
        	getWifiButton.setVisibility(View.VISIBLE);
        	changchatButton.setVisibility(View.GONE);
        	closeBluetoothButton.setVisibility(View.VISIBLE);
        	getWifiStateButton.setVisibility(View.VISIBLE);
        	
        	wifiStateLayout.setVisibility(View.VISIBLE);
    	}
    	createWifiListView(wifiSet, true);
    }
 
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.w(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
                btService.openBT();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(mContext, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
       
        printW("bt address:"+address);
        btService.connectBT(address, secure);
    }
    public void printW(String msg){
    	Log.w(TAG, msg);
    }
}
