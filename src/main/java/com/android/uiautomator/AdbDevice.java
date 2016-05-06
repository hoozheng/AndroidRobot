package com.android.uiautomator;

import java.io.File;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class AdbDevice {

	private AndroidDebugBridge bridge = null;
	
	private IDevice[] getDevices() {
		return bridge.getDevices();
	}
	
	private void initDebugBridge() {
		if (bridge == null) {
			AndroidDebugBridge.init(false);
		}
		
		if ((bridge == null) || (!bridge.isConnected())) {
			String adbLocation = System.getProperty("user.dir");
			System.out.println(adbLocation);
			if ((adbLocation != null) && (adbLocation.length() != 0)) {
				adbLocation = adbLocation + File.separator + "adb";
				File file= new File(adbLocation);
				if(!file.exists())
					adbLocation = "adb";
			}else{
				adbLocation = "adb";
			}
			bridge = AndroidDebugBridge.createBridge(adbLocation, false);
		}
	}
	
	public IDevice[] getActiveDevices(){
		IDevice[] devices = null;
		initDebugBridge();
		devices = getDevices();
		return devices;
	}
	
	public void disconnect(){
//		AndroidDebugBridge.disconnectBridge();
//		this.bridge = null;
//		AndroidDebugBridge.terminate();
	}
}
