package com.android.util;

import java.io.File;
import java.io.IOException;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.selendroid.StreamReader;
import com.android.uiautomator.AdbDevice;

public class AdbUtil {

	public static String send(String cmd, long timeout) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			StreamReader errorGobbler = new StreamReader(process.getErrorStream(), "Error");
			StreamReader outputGobbler = new StreamReader(process.getInputStream(), "Output");
			
			errorGobbler.start();
            outputGobbler.start();
            outputGobbler.join(timeout);
			if (!outputGobbler.info.toString().trim().equals(""))
				return outputGobbler.info.toString();
		} catch (IOException e) {
			System.out.println("Cannot run program");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return "";
		
	}
	
	public static String getBrand(IDevice device){
		String brand = device.getProperty("ro.product.manufacturer") +
				" "+
				device.getProperty("ro.product.model");
		
		return brand;
	}
	
	private static long getPID(String str) {
		String separator = System.getProperty("line.separator");
		String title = str.substring(0, str.indexOf(separator));
		String content = str.substring(str.indexOf(separator), str.length());
		int index = -1;
		if(content.trim().equals(""))
			return index;
		
		String [] titArray = title.trim().split("\\s+");
		for(int i=0;i < titArray.length;i++) {
			if(titArray[i].trim().equalsIgnoreCase("pid")) {
				index = i;
				break;
			}
		}
				
		String [] contArray = content.trim().split("\\s+");
		if(index != -1 && !contArray[index].trim().equals(""))
			return Long.parseLong(contArray[index]);
		
		return -1;
	}
	
	public static void kill(String sn, String processName) {
		if(processName != null && !processName.trim().equals(""))
		{
			String query_uiautomator  = "adb -s " + sn + " shell ps " + processName;
			String query_result = send(query_uiautomator, 1000);
			if(!query_result.contains(processName))
				return;
			long process = getPID(query_result);
			System.out.println("process = " + process);
			if(process != -1){
				send("adb -s " + sn + " shell kill " + process, 3000);
			}
		}
	}

}