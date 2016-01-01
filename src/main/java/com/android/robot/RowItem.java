package com.android.robot;

import org.eclipse.swt.widgets.ProgressBar;

class RowItem {
	private String sn;
	private String taskName;
    private String caseName;
    private ProgressBar pb;
    private String start;
    private String end;
    private String result;
    

    public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
    
    public String getTaskName() {
        return taskName;
    }
    
	public void setTaskName(String task) {
    	taskName = task;
    }
    
    public String getCaseName(){
    	return caseName;
    }
    
    public void setCaseName(String name){
    	caseName = name;
    }
    
    public ProgressBar getPb(){
    	return pb;
    }
    
    public void setPb(ProgressBar pb1){
    	pb = pb1;
    }
    
    public String getStartTime(){
    	return start;
    }
    
    public void setStartTime(String time){
    	start = time;
    }
    
    public String getEndTime(){
    	return end;
    }
    
    public void setEndTime(String time){
    	end =time;
    }
    
    public String getResult(){
    	return result;
    }
    
    public void setResult(String res){
    	result = res;
    }
    
}