package com.android.uiautomator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketClient {
    private Socket socket = null;
    private Service sc = null;
	Thread thread = null;
	
    
    public boolean connect(String host,int port) throws Exception{
    	try {
            socket = new Socket(host, port);
            sc = new Service(socket);
        } catch (Exception ex) {
        	ex.printStackTrace();
        	throw new Exception("[send] Connect error");
        }
    	return true;
    }
    
    public boolean disconnect() throws Exception{
    	if(null != socket && socket.isConnected())
    		this.socket.close();
    	return true;
    }
    
    public String sendMessageAndGetRespond(String msg){
    	sc.sendmsg(msg);
    	thread = new Thread(sc);
    	thread.start();
    	try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return sc.getResult();
    }
    
    public class Service implements Runnable {
    	
    	private Socket socket;
	   	private BufferedReader in = null;
	   	private PrintWriter pout = null;
	   	private String msg = "";
	   	private String result = "";
	   	
	   	public Service(Socket socket) {
	   		this.socket = socket;  
	   		try {
	   			in =  new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));  
	   			pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8")),true);
	   		 } catch (IOException e) {
	   			 e.printStackTrace();
	   		 }
	   	}
	   	
	   	public void sendmsg(String message) {
	   		pout.println(message);
	   	}
	   	
	   	public String getResult(){
	   		return this.result;
	   	}
	   	

		public void run() {
			while (true) {
				try {
					if ((msg = in.readLine()) != null) {
							result = msg;
							break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Client exit.");
					break;
				}
			}
		}
    	
    }
}