/*
 * Copyright (C) 2012 The CeHu Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.device;

import org.eclipse.swt.widgets.Display;

import com.android.ddmlib.IDevice;
import com.android.robot.AndroidRobot;
import com.android.uiautomator.AdbDevice;

public class DeviceDetector extends Thread{
	private AdbDevice adbDevice = null;
	private boolean running = true;
	private IDevice[] devices = null;
	
	public DeviceDetector(AdbDevice adbDevice){
		this.running = true;
		this.adbDevice = adbDevice;
	}
	
	public void setDevices(final IDevice[] device){

		Display.getDefault().asyncExec(new Runnable(){
			public void run()
			{
				AndroidRobot.setDevices(device);
			}
		});
		
		
	}
	
	public void removeAllDevice(){
		Display.getDefault().asyncExec(new Runnable(){
			public void run()
			{
				AndroidRobot.removeAllDevice();
			}
		});
	}
	
	public IDevice[] getDevices(){
		return this.devices;
	}
	
	public void stopThread(){
		this.running = false;
	}
	
	public void run(){
		
		while(running){
			devices = this.adbDevice.getActiveDevices();
			
			if(devices.length <= 0){
				removeAllDevice();
			}
			
			setDevices(devices);
			/*
			for(int i=0;i<devices.length;i++){
				//running = false;
				//System.out.println(devices[i].getSerialNumber());
				if(devices[i].isOnline())
						setDevices(devices[i].getSerialNumber());
			}
			*/
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
