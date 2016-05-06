package com.android.util;

public class Env {
	
	private static final String javaVersion; 
	private static final String bit;
	  
    static {  
        javaVersion = System.getProperty("java.version");  
        bit         = System.getProperty("sun.arch.data.model");
    }  
	  
    public static String getJavaVersion() {  
        return javaVersion;
    }  
    
    public static String getJavaArch() {  
        return bit;
    } 
	  
    public static boolean isValidJava() {
    	if(javaVersion.compareTo("1.7.0") >= 0 && bit.equals("64"))
    		return true;
    	
    	return false;
    }
    
    public static void setEnv() {
    	String USER_HOME = System.getProperty("user.home");
    	
    	System.out.println(USER_HOME);
    }
    
    public static void main(String[] args) {
    	setEnv();
    }
}
