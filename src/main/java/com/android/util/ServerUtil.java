package com.android.util;

public class ServerUtil {
	private static String server_ip = "";
	private static String server_port = "";
	private static String ftp_user = "";
	private static String ftp_pwd = "";
	
	public static String getServerIp() {
		return server_ip;
	}
	
	public static String getFtpUser() {
		return ftp_user;
	}
	
	public static String getFtpPwd() {
		return ftp_pwd;
	}
	
	public static int getServerPort() {
		try{
			return Integer.parseInt(server_port);
		}catch(NumberFormatException ex) {
			return 0;
		}
	}
	
}
