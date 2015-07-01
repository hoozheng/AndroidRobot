package com.android.robot;

public class TaskRowItem {
	private boolean isChecked = false;
	private String tcName = "";
	private String path = "";
	private String tcLoop = "1";
	private String tcInterval = "1";
	private String timeUnit = "秒";
	
	public void setChecked(boolean checked){
		this.isChecked = checked;
	}
	
	public boolean getChecked(){
		return this.isChecked;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public String getPath(){
		return this.path;
	}
	
	public void setName(String name){
		this.tcName = name;
	}
	
	public String getName(){
		return this.tcName;
	}
	
	public void setLoop(String i){
		this.tcLoop = i;
	}
	
	public String getLoop(){
		return this.tcLoop;
	}
	
	public void setInterval(String interval){
		this.tcInterval = interval;
	}
	
	public String getInterval(){
		return this.tcInterval;
	}
	
	public void setUnit(String unit){
		this.timeUnit = unit;
	}
	
	public String getUnit(){
		return this.timeUnit;
	}
}
