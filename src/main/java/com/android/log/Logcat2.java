package com.android.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Logcat2 extends Thread {
	 InputStream is;
	 String logcat = "";
	 boolean stop = false;
	 public StringBuffer error = new StringBuffer();
	 public StringBuffer info = new StringBuffer();
	 
	 OutputStreamWriter osw = null;
	 BufferedWriter bw = null;
	 
	 public Logcat2(InputStream is, String logcat) {
		 this.is = is;
		 this.logcat = logcat;
	 }
	 
	 public void destory() {
		 this.stop = true;
	 }
	 

	 public void run() {
		 try {
			 InputStreamReader isr = new InputStreamReader(is,"UTF-8");
			 BufferedReader br = new BufferedReader(isr);
			 
			 osw = new OutputStreamWriter(new FileOutputStream(logcat));
			 bw = new BufferedWriter(osw);
			 
			 String line = null;
			 while (this.stop == false && (line = br.readLine()) != null) {
				 bw.write(line + "\r\n");
//				 if (type.equals("Error"))
//					 error.append(line);
//				 else
//					 info.append(line);
			 }
		 } catch (IOException ioe) {
			 ioe.printStackTrace();
		 }finally {
			 try {
				 if(bw != null)
					 bw.close();
	             if(osw != null)
	            	 osw.close();
			 } catch (IOException e) {
	              e.printStackTrace();
	        }  
		 }
	 }
	 
}



