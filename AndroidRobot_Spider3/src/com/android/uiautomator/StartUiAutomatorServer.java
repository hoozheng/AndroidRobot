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
		String shell = "adb -s " + sn + " push UiAutomatorHarbour.jar /data/local/tmp";
		String forward = "adb -s "+ sn + " forward tcp:"+port+" tcp:"+port;
		String pushFile = "adb -s " + sn + " shell \"echo port=" + port+ " > /data/local/tmp/UiAutomatorHarbour.prop\"";
		String query_uiautomator  = "adb -s " + sn + " shell ps uiautomator";
		String query_result = new AdbUtil().send(query_uiautomator, 1000);
		
		if(query_result!= null && query_result.contains("uiautomator"))
			return;
		
		String forward_response = new AdbUtil().send(forward, 1000);
		String shell_response = new AdbUtil().send(shell, 1000);
		String push_prop = new AdbUtil().send(pushFile, 1000);
		String cmd = "adb -s " + sn + " shell uiautomator runtest UiAutomatorHarbour.jar -c com.android.harbour.UiAutomatorHarbour";

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
