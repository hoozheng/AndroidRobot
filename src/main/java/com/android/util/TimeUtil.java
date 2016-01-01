package com.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	public static synchronized String getRandomName(){
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	public static synchronized String getTimeAsFormat(String format){
		//Start Time
		Date currentTime = new Date();
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(currentTime);
		
        return dateString;
	}
}
