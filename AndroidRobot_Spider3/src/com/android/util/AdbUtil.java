package com.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.android.ddmlib.IDevice;
import com.android.selendroid.StreamReader;

public class AdbUtil {
	public static synchronized String send(String cmd, long timeout) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			StreamReader errorGobbler = new StreamReader(process.getErrorStream(), "Error");
			StreamReader outputGobbler = new StreamReader(process.getInputStream(), "Output");
			
			errorGobbler.start();
            outputGobbler.start();
			
//			ADBReader streamInput = new ADBReader(process.getInputStream());
//			streamInput.start();

//			streamInput.join(timeout);
            outputGobbler.join(timeout);
//			String event = streamInput.getEvent();
			if (!outputGobbler.info.toString().trim().equals(""))
				return outputGobbler.info.toString();
			// process.waitFor();
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

}