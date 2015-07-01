package com.android.selendroid;

import java.io.IOException;

public class LaunchSelendroid implements Runnable {
	private Process process = null;
	private String prefix = "java -jar Selendroid-standalone.jar -noClearData -keepAdbAlive -app ";// -Dfile.encoding=utf-8 
	private String app = "";
	private int port = 0;
	private StreamReader errorGobbler = null;
	private StreamReader outputGobbler = null;
	private static LaunchSelendroid launcher = null;
	private String exception = "";
	
	public LaunchSelendroid(String app, int port) {
		this.app = app;
		this.port = port;
	}
	
//	public static LaunchSelendroid newInstance(String app, int port) {
//		if(null == launcher) {
//			launcher = new LaunchSelendroid(app, port);
//		}
//		
//		return launcher;
//	}
	
	public void getMessage() throws Exception {
		if(!this.exception.trim().equals("")) {
			System.out.println("launched:" + exception);
			throw new Exception(this.exception);
		}
	}
	
	@Override
	public void run() {
		try {
			process = Runtime.getRuntime().exec(prefix + app);
			errorGobbler = new StreamReader(process.getErrorStream(), "Error");
			outputGobbler = new StreamReader(process.getInputStream(), "Output");
			
			errorGobbler.start();
            outputGobbler.start();
            
            while(true) {
				if(errorGobbler.error.toString()
						.contains("Selendroid standalone server has been started on port: 4444")) {
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(errorGobbler.error.toString().contains("Exception in thread")) {
						int index = errorGobbler.error.toString().indexOf("Exception in thread");
						this.exception = errorGobbler.error.toString().substring(index, errorGobbler.error.toString().length());
					}
					
					break;
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					this.exception = e.getMessage();
				}
            }
//            
//			BufferedReader re = 
//					new BufferedReader(new InputStreamReader(process.getErrorStream()));
//			String temp = "";
//			while((temp = re.readLine()) != null) {
//				System.out.println(temp);
//				if(temp.contains("Selendroid standalone server has been started on port: 4444"))
//					break;
//			}
//			process.waitFor();
		} catch (IOException e) {
			this.exception = e.getMessage();
		}
	}
	
	public void stopSelendroid() {
		if(this.process != null)
			this.process.destroy();
		
		if(this.errorGobbler != null)
			this.errorGobbler.stop();
		
		if(this.outputGobbler != null)
			this.outputGobbler.stop();
	}
	
}
