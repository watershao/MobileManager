package com.qrobot.mobilemanager.bt;

public interface BluetoothDataListener {

	public void onDataListener(int type, byte[] read);
}
