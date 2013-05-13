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
 * ���䷽����
 * @author v_watershao
 *
 */
public class ClsUtil {

	/**
	 * ���豸��� �ο�Դ�룺platform/packages/apps/Settings.git
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */
	public static boolean createBond(Class btClass, BluetoothDevice btDevice)
			throws Exception {
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * ���豸������ �ο�Դ�룺platform/packages/apps/Settings.git
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */
	public static boolean removeBond(Class btClass, BluetoothDevice btDevice)
			throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * ������Ե�pin��
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
	 *  ȡ���û�����
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
	 *  ȡ�����
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
	 * �����豸���ñ�����
	 * @param btClass
	 * @param btAdapter
	 * @param mode SCAN_MODE_NONE=20,
     * SCAN_MODE_CONNECTABLE=21,
     * SCAN_MODE_CONNECTABLE_DISCOVERABLE=23
	 * @param duration,ֻ��SCAN_MODE_CONNECTABLE_DISCOVERABLE
	 * �����¿�������ʱ��Ĭ��Ϊ120�����Ϊ300������Ϊ0
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
	 * ����һ��bluetoothSocket
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
	 * ����һ��BluetoothServerSocket
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
	 * ��ӡ���еķ��䷽��
	 * @param clsShow
	 */
	public static void printAllInform(Class clsShow) {
		try {
			// ȡ�����з���
			Method[] hideMethod = clsShow.getMethods();
			int i = 0;
			for (; i < hideMethod.length; i++) {
				Log.e("method name", hideMethod[i].getName() + ";and the i is:"
						+ i);
			}
			// ȡ�����г���
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