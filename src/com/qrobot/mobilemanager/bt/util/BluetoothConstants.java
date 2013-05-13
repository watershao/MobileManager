package com.qrobot.mobilemanager.bt.util;

/**
 * 蓝牙传输的相关标识
 * @author water
 *
 */
public class BluetoothConstants {
	
	/**
	 * 发送广播时候的intent过滤
	 */
	public static final String SETTING_ACTION = "com.qrobot.bluetooth.SETTING";
	
	public static final String CLOCK_ACTION = "com.qrobot.bluetooth.CLOCK";
	
	public static final String REMINDER_ACTION = "com.qrobot.bluetooth.REMINDER";
	
	/**
	 * 蓝牙传输数据时候的数据类型
	 */
	public static final int TYPE_SETTING = 0x100;
	
	public static final int TYPE_CLOCK = 0x200;
	
	public static final int TYPE_REMINDER = 0x300;
	
	public static final int TYPE_FILE = 0x400;
	
	/**
	 * 传输广播时候存储数据的标识
	 */

	public static final String SETTING_CONTENT = "SETTING_CONTENT";
	
	public static final String CLOCK_CONTENT = "CLOCK_CONTENT";
	
	public static final String REMINDER_CONTENT = "REMINDER_CONTENT";
	
}
