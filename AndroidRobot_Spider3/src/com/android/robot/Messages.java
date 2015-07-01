package com.android.robot;

import java.util.Hashtable;

public class Messages {

	private static Hashtable<String,String> tableFotaInfo = 
			new Hashtable();
	private static String fotaInfo = "";
	
	public static String getFotaInfo(){
		return fotaInfo;
	}
	
	public static void setFotaInfo(String info){
		fotaInfo = info;
	}
	
	public static Hashtable<String,String> getFotaTable(){
		return tableFotaInfo;
	}
	
	public static void setFotaTable(String key, String info){
		tableFotaInfo.put(key, info);
	}
}
