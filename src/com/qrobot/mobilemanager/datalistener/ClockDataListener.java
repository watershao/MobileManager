package com.qrobot.mobilemanager.datalistener;

import android.os.Bundle;

public interface ClockDataListener {

	public void OnClockDataListener(int robotNo, byte[] data, int dataSize);
	
	public void OnClockDataListener(Bundle bundle);
}
