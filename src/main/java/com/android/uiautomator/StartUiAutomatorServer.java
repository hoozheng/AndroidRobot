package com.android.uiautomator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.android.util.AdbUtil;

public class StartUiAutomatorServer implements Runnable{
	private Process process = null;
	private String sn = "";
	private int port = 5048;
	public StartUiAutomatorServer(String sn, int port){
		this.sn = sn;
		this.port = port;
	}
	@Override
	public void run() {
		//将UiAutomatorHarbour注入手机
		String shell = "adb -s " + sn + " push " + System.getProperty("user.dir") + "/UiAutomatorHarbour.jar /data/local/tmp";
		String forward = "adb -s "+ sn + " forward tcp:"+port+" tcp:"+port;
		String pushFile = "adb -s " + sn + " shell \"echo port=" + port+ " > /data/local/tmp/UiAutomatorHarbour.prop\"";
		String query_uiautomator  = "adb -s " + sn + " shell ps uiautomator";
		String cmd = "adb -s " + sn + " shell uiautomator runtest UiAutomatorHarbour.jar -c com.android.harbour.UiAutomatorHarbour";
		String query_result = AdbUtil.send(query_uiautomator, 1000);
		if(query_result!= null && query_result.contains("uiautomator"))
			return;
		
		AdbUtil.send(forward, 1000);
		AdbUtil.send(shell, 1000);
		AdbUtil.send(pushFile, 1000);
		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader re = 
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			String temp = "";
			while((temp = re.readLine()) != null)
				if(temp.contains("INSTRUMENTATION_STATUS_CODE: 1"))
					break;
			
			Thread.sleep(3000);
			System.out.println(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
