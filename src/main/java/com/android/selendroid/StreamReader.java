package com.android.selendroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamReader extends Thread {
	 InputStream is;
	 String type;
	 public StringBuffer error = new StringBuffer();
	 public StringBuffer info = new StringBuffer();
	 
	 public StreamReader(InputStream is, String type) {
		 this.is = is;
		 this.type = type;
	 }
	 
	 public void run() {
		 try {
			 InputStreamReader isr = new InputStreamReader(is,"UTF-8");
			 BufferedReader br = new BufferedReader(isr);
			 String separator = System.getProperty("line.separator");
			 String line = null;
			 while ((line = br.readLine()) != null) {
				 if (type.equals("Error"))
					 error.append(line + separator);
				 else
					 info.append(line + separator);
			 }
		 } catch (IOException ioe) {
			 ioe.printStackTrace();
		 }
	 }
	 
}


