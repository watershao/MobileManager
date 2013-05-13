package com.qrobot.mobilemanager.bt.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * 反射方法类
 * @author v_watershao
 *
 */
public class ClsUtil {

	/**
	 * 与设备配对 参考源码：platform/packages/apps/Settings.git
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */
	public static boolean createBond(Class btClass, BluetoothDevice btDevice)
			throws Exception {
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */
	public static boolean removeBond(Class btClass, BluetoothDevice btDevice)
			throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 设置配对的pin码
	 * @param btClass
	 * @param btDevice
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static boolean setPin(Class btClass, BluetoothDevice btDevice,
			String str) throws Exception {
		try {
			Method removeBondMethod = btClass.getDeclaredMethod("setPin",
					new Class[] { byte[].class });
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
					new Object[] { str.getBytes() });
			Log.e("returnValue", "" + returnValue);
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	/**
	 *  取消用户输入
	 * @param btClass
	 * @param device
	 * @return
	 * @throws Exception
	 */
	public static boolean cancelPairingUserInput(Class btClass,	BluetoothDevice device)
												throws Exception {
		
		Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
		// cancelBondProcess()
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}

	/**
	 *  取消配对
	 * @param btClass
	 * @param device
	 * @return
	 * @throws Exception
	 */
	public static boolean cancelBondProcess(Class btClass,BluetoothDevice device)
									throws Exception {
		
		Method createBondMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}

	/**
	 * 设置设备永久被发现
	 * @param btClass
	 * @param btAdapter
	 * @param mode SCAN_MODE_NONE=20,
     * SCAN_MODE_CONNECTABLE=21,
     * SCAN_MODE_CONNECTABLE_DISCOVERABLE=23
	 * @param duration,只有SCAN_MODE_CONNECTABLE_DISCOVERABLE
	 * 这种下可以设置时间默认为120，最大为300，永久为0
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	 
	
	public static boolean setScanMode(Class btClass, BluetoothAdapter btAdapter,int mode, int duration) 
			throws NoSuchMethodException, IllegalArgumentException, 
							IllegalAccessException, InvocationTargetException{
		
		Method setScanModeMethod = btClass.getMethod("setScanMode",
											new Class[] {int.class,int.class});
		Boolean returnValue = (Boolean) setScanModeMethod.invoke(btAdapter,
						new Object[] {new Integer(mode), new Integer(duration)});
		return returnValue.booleanValue();
	}
	
	/**
	 * 创建一个bluetoothSocket
	 * @param btSocket
	 * @param btDevice
	 * @param channel 1-30
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static BluetoothSocket createRfcommSocket(Class btClass , BluetoothDevice btDevice,int channel)
											throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method createRfcommSocketMethod = btClass.getMethod("createRfcommSocket", 
													new Class[] {int.class});
		BluetoothSocket btSocket = (BluetoothSocket)createRfcommSocketMethod.invoke(btDevice,
									new Object[]{new Integer(channel)});
		return btSocket;
	}
	
	/**
	 * 创建一个BluetoothServerSocket
	 * @param btClass
	 * @param btAdapter
	 * @param channel 1-30
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static BluetoothServerSocket listenUsingRfcommOn(Class btClass, BluetoothAdapter btAdapter, int channel) 
				throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method createRfcommSocketMethod = btClass.getMethod("listenUsingRfcommOn", 
				new Class[] {int.class});
		BluetoothServerSocket btSocket = (BluetoothServerSocket)createRfcommSocketMethod.invoke(btAdapter,
										new Object[]{new Integer(channel)});
		return btSocket;
	}
	
	/**
	 * 打印所有的反射方法
	 * @param clsShow
	 */
	public static void printAllInform(Class clsShow) {
		try {
			// 取得所有方法
			Method[] hideMethod = clsShow.getMethods();
			int i = 0;
			for (; i < hideMethod.length; i++) {
				Log.e("method name", hideMethod[i].getName() + ";and the i is:"
						+ i);
			}
			// 取得所有常量
			Field[] allFields = clsShow.getFields();
			for (i = 0; i < allFields.length; i++) {
				Log.e("Field name", allFields[i].getName());
			}
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}