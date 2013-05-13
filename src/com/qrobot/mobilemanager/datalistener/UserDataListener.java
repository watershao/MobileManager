package com.qrobot.mobilemanager.datalistener;

import android.os.Bundle;

public interface UserDataListener {

	public void OnUserDataListener(int robotNo, byte[] data, int dataSize);
	
	public void OnUserDataListener(Bundle bundle);
}
