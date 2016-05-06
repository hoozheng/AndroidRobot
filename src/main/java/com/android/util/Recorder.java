package com.android.util;

import org.apache.log4j.Logger;

import com.android.selendroid.StreamReader;

public class Recorder implements Runnable{
	private Process process = null;
	private boolean isRunning = true;
	private Logger logger = Logger.getLogger(Recorder.class);
	private String sn;
	private long timeout;
	private String fileName;
	
	public Recorder(String sn, long timeout, String fileName) {
		this.sn = sn;
		this.timeout = timeout;
		this.fileName = fileName;
	}
	
	private void startRecord() {
		String cmd = "adb -s " + sn + " shell screenrecord --time-limit " + (timeout/1000) + " /sdcard/" + fileName;
		try{
			//check screenrecord ps
			AdbUtil.kill(sn, "screenrecord");
			
			process = Runtime.getRuntime().exec(cmd);
			StreamReader errorGobbler = new StreamReader(process.getErrorStream(), "Error");
			StreamReader outputGobbler = new StreamReader(process.getInputStream(), "Output");
			
			errorGobbler.start();
	        outputGobbler.start();
	        outputGobbler.join(timeout);
		}catch(Exception ex) {
			logger.error("recorder error:" + ex);
		}
	}
	
	public void stopRecord(String sn) {
		isRunning = false;
	}

	@Override
	public void run() {
		startRecord();
	}
	
	public static void main(String[] args) {
		Recorder recorder = new Recorder("5c37ad36", 60000, "支付宝");
		Thread thread = new Thread(recorder);
		thread.start();
	}
}
