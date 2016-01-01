package com.android.minicap;
import java.io.IOException;

import com.android.selendroid.StreamReader;

public class LaunchMinicap implements Runnable {
	private Process process = null;
	private String prefix = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -S -P ";// -Dfile.encoding=utf-8 
	private String projection = "";
	private String sn = "";
	private StreamReader errorGobbler = null;
	private StreamReader outputGobbler = null;
	private static LaunchMinicap launcher = null;
	private String exception = "";
	
	public LaunchMinicap(String sn, int width, int height, int vwidth, int vheight, int rotation) {
		this.sn         = sn;
		this.projection = String.format("%dx%d@%dx%d/%d", width,height,vwidth,vheight,rotation);
	}
	
	public void getMessage() throws Exception {
		if(!this.exception.trim().equals("")) {
			System.out.println("launched:" + exception);
			throw new Exception(this.exception);
		}
	}
	
	@Override
	public void run() {
		try {
			process = Runtime.getRuntime().exec("adb -s " + sn + " shell " + prefix + " " + this.projection);
			errorGobbler = new StreamReader(process.getErrorStream(), "Error");
			outputGobbler = new StreamReader(process.getInputStream(), "Output");
			
			errorGobbler.start();
            outputGobbler.start();
            
            while(true) {
				if(outputGobbler.info.toString().trim().endsWith("bytes for JPG encoder"))
					break;
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		} catch (IOException e) {
			this.exception = e.getMessage();
		}
	}
	
	public void stopMinicap() {
		if(this.process != null)
			this.process.destroy();
		
		if(this.errorGobbler != null)
			this.errorGobbler.stop();
		
		if(this.outputGobbler != null)
			this.outputGobbler.stop();
	}
	
}


