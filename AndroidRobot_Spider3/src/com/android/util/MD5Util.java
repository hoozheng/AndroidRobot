package com.android.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	 public static String md5s(String plainText) 
	 {
		 String str = "";
		 try {
			 MessageDigest md = MessageDigest.getInstance("MD5");   
			 md.update(plainText.getBytes());   
			 byte b[] = md.digest();   
			 int i;   
			 StringBuffer buf = new StringBuffer("");   
			 for (int offset = 0; offset < b.length; offset++) {    
				 i = b[offset];    
				 if (i < 0)     
					 i += 256;    
				 if (i < 16)     
					 buf.append("0");    
				 buf.append(Integer.toHexString(i));   
			 }   
			 str = buf.toString();   
			
			 } catch (NoSuchAlgorithmException e) {  
				 // TODO Auto-generated catch block   
				 e.printStackTrace();  
			 }
		 return str;
	 }
}
