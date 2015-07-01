package com.android.util;

import com.android.ddmlib.Log;


public class QuickTesterLoger {
	public static final String LOG_TAG = "QUICKTESTER";
	
	public static void info(String message) {
		Log.i(LOG_TAG, message);
	}
}
