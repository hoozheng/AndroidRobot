package com.android.ui.data;

public class Element {
	private String element = "";
	private String sn     = "";
	private String taskName     = "";
	private String scriptName     = "";
	private Object value   = 0;
	
	public void setElement(String element){
		this.element = element;
	}
	
	public String getElement(){
		return this.element;
	}
	
	public void setSN(String sn){
		this.sn = sn;
	}
	
	public String getSN(){
		return this.sn;
	}
	
	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	
	public String getTaskName(){
		return this.taskName;
	}
	
	public void setScriptName(String scriptName){
		this.scriptName = scriptName;
	}
	
	public String getScriptName(){
		return this.scriptName;
	}
	
	public void setValue(Object value){
		this.value = value;
	}
	
	public Object getValue(){
		return this.value;
	}
}
