/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qrobot.mobilemanager.bt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.app.ActionBar;
import com.qrobot.mobilemanager.BaseActivity;
import com.qrobot.mobilemanager.R;
import com.qrobot.mobilemanager.bt.util.BluetoothConstants;
import com.qrobot.mobilemanager.bt.util.TypeConvert;
import com.qrobot.mobilemanager.bt.util.WifiParam;
import com.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothManager extends BaseActivity {

	public BluetoothManager(){
		super(R.string.bluetooth);
	}
	
	public BluetoothManager(int titleRes) {
		super(titleRes);
		// TODO Auto-generated constructor stub
	}

	// Debugging
    private static final String TAG = "BluetoothActivity";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothConnectManager mChatService = null;


    private  final static int WAIT_TIME = 40;
    
    private ListView wifiListView = null;
    
    private TextView currentWifiState = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        
        // Set up the window layout
        setContentView(R.layout.bt_main);

		setBehindContentView(R.layout.menu_frame);
		getSlidingMenu().setSlidingEnabled(true);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// show home as up so we can toggle
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "蓝牙设备不可用", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        manageWifi();
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
    	openButton = (Button)findViewById(R.id.button_openwifi);
    	closeButton = (Button)findViewById(R.id.button_closewifi);
    	scanButton = (Button)findViewById(R.id.button_scanwifi);
    	getWifiButton = (Button)findViewById(R.id.button_getwifi);
    	changchatButton = (Button)findViewById(R.id.button_changechat);
    	
    	closeBluetoothButton = (Button)findViewById(R.id.button_closebluetooth);
    	getWifiStateButton = (Button)findViewById(R.id.button_getwifi_state); 
    	currentWifiState = (TextView)findViewById(R.id.current_state);
    	
    	wifiStateLayout = (LinearLayout)findViewById(R.id.wifi_state_layout);
    	
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
					ActionBar actionBar = getSupportActionBar();
					CharSequence bluetoothConnect = actionBar.getSubtitle();
					if (bluetoothConnect.toString().contains("没有连接")) {
						Toast.makeText(BluetoothManager.this, "请确保蓝牙设备已连接", Toast.LENGTH_LONG).show();
					}else {
						currentWifiState.setText(R.string.wifi_state_getting);
						sendWifiMessage(WifiParam.WIFI_STATE);
					}
					
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
    
    /**
     * 发送 wifi控制消息id
     * @param typeId
     */
    private void sendWifiMessage(int messageId) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothConnectManager.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] sendByteArray = TypeConvert.getSendByteArray(messageId);
        mChatService.write(sendByteArray);
     }
    
    
    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            //不询问直接打开
//          mBluetoothAdapter.enable();
            
            ensureDiscoverable();
            if (mChatService == null) setupChat();
        // Otherwise, setup the chat session
        } else {
        	ensureDiscoverable();
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothConnectManager.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.bt_message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            @Override
			public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
//                sendMessage(message);
//                sendFile("");
                Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothConnectManager(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

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
    * 使蓝牙可发现 
    */
    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        
        waitEnable();
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothConnectManager.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
//            mChatService.write(send);
        	
    		byte[] sendByteArray = TypeConvert.getSendByteArray(WifiParam.MESSAGE_TEXT,message);
    		mChatService.write(sendByteArray);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
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

    private final void setStatus(int resId) {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothConnectManager.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    mConversationArrayAdapter.clear();
                    if (wifiListView != null) {
						wifiListView.setVisibility(View.GONE);
					}
                    
                    sendWifiMessage(WifiParam .WIFI_STATE);
                    break;
                case BluetoothConnectManager.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothConnectManager.STATE_LISTEN:
                case BluetoothConnectManager.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    
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
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
//                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
 //               String readMessage = new String(readBuf, 0, msg.arg1);
//                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                handleReadMessage(msg);
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

    
    /**
     * 处理读取到的消息
     * @param msg
     */
    private void handleReadMessage(Message msg){
        byte[] readBuf = (byte[]) msg.obj;
 //       Log.d(TAG, "readBuf size."+readBuf.length);
        List<byte[]> readByteArrayList = (List<byte[]>)TypeConvert.ByteToObject((byte[])msg.obj);
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
				Toast.makeText(BluetoothManager.this, "请先扫描wifi热点", Toast.LENGTH_LONG).show();
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
				Toast.makeText(BluetoothManager.this, "对不起，没有找到wifi热点，请尝试重新扫描。", Toast.LENGTH_LONG).show();
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
    	
    	
    	wifiListView = (ListView)findViewById(R.id.wifilist);
    	SimpleAdapter adapter = new SimpleAdapter(this, wifiInfoList, 
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
				buildDialog(BluetoothManager.this, mData, position);
				
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
        LayoutInflater inflater=LayoutInflater.from(this);
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
					mChatService.write(sendByteArray);
				} else {
					String password = pwEt.getText().toString().trim();
					if (password != null && password.length() > 7) {
						byte[] sendByteArray = TypeConvert.getSendByteArray(WifiParam.WIFI_PASSWORD, ssid, password);
						mChatService.write(sendByteArray);
						
						dialog.cancel();
					}else {
						Toast.makeText(BluetoothManager.this, "请输入密码。", Toast.LENGTH_LONG).show();
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
				
				mChatService.write(sendByteArray);
				
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
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
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
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bt_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
            
        case R.id.channel_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;    
        }
        return false;
    }*/

    /**
     * 发送文件
     * @param path 文件路径
     */
    private void sendFile(String path){
    	String sdPath = Environment.getExternalStorageDirectory().toString();
    	File file = new File(sdPath+"/logs.txt");//lib0001.mp3 logs.txt
    	String fileName = file.getName();
    	long fileLength = file.length();
    	Log.d(TAG, "FileName:"+file.getName()+"fileLength:"+file.length());
        byte[] buff = new byte[512];
        int count = 0;
        try {
			FileInputStream fis = new FileInputStream(file);
			while((count=fis.read(buff, 0, 512)) > 0){
				Log.d(TAG, buff.length+"count size:"+count);
				fileLength -= count; 
				byte[] tempBA = new byte[count];
				System.arraycopy(buff, 0, tempBA, 0, count);
				
				byte[] output = getSendFileByteArray(BluetoothConstants.TYPE_FILE,fileName,fileLength,tempBA);
				Log.d(TAG, fileLength+",output size:"+output.length);
				mChatService.write(output);
				Thread.sleep(150);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
	/**
	 * 获取发送文件的byte[]
	 * @param type 类型
	 * @param fileName 文件名称
	 * @param fileLength 文件长度
	 * @param fileByteArray 文件流
	 * @return
	 */
	private byte[] getSendFileByteArray(int type, String fileName, long fileLength,byte[] fileByteArray){
        List<byte[]> convertByteArrayList = new ArrayList<byte[]>();
        byte[] sendByteArray = null;
    	convertByteArrayList.clear();
    	convertByteArrayList.add(TypeConvert.ObjectToByte(type));
    	convertByteArrayList.add(TypeConvert.ObjectToByte(fileName));
    	convertByteArrayList.add(TypeConvert.ObjectToByte(fileLength));
    	convertByteArrayList.add(fileByteArray);
    	sendByteArray = TypeConvert.ObjectToByte(convertByteArrayList);
		
		return sendByteArray;
	}
}
