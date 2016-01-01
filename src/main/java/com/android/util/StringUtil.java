package com.android.util;

public class StringUtil {
	public static synchronized String trim(String str) {
		String temp = str.trim();
		int index = 0;
		for(int i=0;i<temp.length();i++){
			char ch = temp.charAt(i);
			if(ch == '.' || ch == '/' || ch == '\\') {
				index++;
			}else
				break;
		}
		
		return temp.substring(index, temp.length());
	}
}
