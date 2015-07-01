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
package com.android.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/*
 * 
 * This class is designed for get error info
 */
public class StreamReader extends Thread{
	private InputStream is;
	private OutputStream out;
	private String type;
	private boolean running = true;
	private StringBuffer logBuffer = new StringBuffer(); 
	private Process process= null;

	public StreamReader(Process process, String type){
		this.is = process.getInputStream();
        this.type = type;
        this.process = process;
	}
	
	public StringBuffer getStringBuffer(){
		return this.logBuffer;
	}
	
	public void setLength(int length){
		this.logBuffer.setLength(length);
	}
	
	public void appendStringBuffer(String log){
		this.logBuffer.append(log);
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}
	
	public boolean getRunning(){
		return this.running;
	}
	
    public void run(){
    	try{
    		InputStreamReader isr = new InputStreamReader(is,"UTF-8");
    		BufferedReader br = new BufferedReader(isr);
    		
    		String line=null;
    		while ( (line = br.readLine()) != null && running)
    		{
//    			System.out.println(line+"\n");
    			this.appendStringBuffer(line+"\n");
    		}
    		
    		System.out.println("=================="+" end "+"====================\n");

    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
