package com.qrobot.mobilemanager.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Util {

	public static void goToQrobot(Context context) {
		Uri uriUrl = Uri.parse("http://qrobot.qq.com/");
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl); 
		context.startActivity(launchBrowser);
	}
	
}
