package com.android.uiautomator;

import java.util.HashMap;
import java.util.List;

public class SendData {
	public String command = "";
	private List<HashMap<String,String>> params;
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public List<HashMap<String,String>> getParams() {
		return params;
	}
	public void setParams(List<HashMap<String,String>> params) {
		this.params = params;
	} 
}



