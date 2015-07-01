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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import com.android.util.TimeUtil;



public class Log {
	public String logPath = "";
	public BufferedWriter output;
	private FileOutputStream outstream;
	private ObjectOutputStream out;
	public LogInfo logInfo = null;
	private String sn = "";
	
	private Process process = null;
	private Logcat2 input = null;
	
	private static Map<String,Log> map = new HashMap();
	
	public static Log getLoger(String sn){
		return map.get(sn);
	}
	
	public static void remove(String sn) {
		map.remove(sn);
	}
	
	public Log(String sn){
		this.sn = sn;
		map.put(sn, this);
	}
	
	public String getPath() {
		return new File(this.logPath).getParent();
	}
	
	
	private void logcat(String logcat) throws IOException{
		process  = Runtime.getRuntime().exec("adb -s " + sn + " logcat");
		input = new Logcat2(process.getInputStream(), logcat);
		input.start();
	}
	
	public void createFile(String projectPath){
		String fold = projectPath+"/Logs/" + sn + "_" + TimeUtil.getRandomName();
		logPath = fold + "/" + sn + ".arlog";
		String logcatFile = fold + "/" + sn + ".logcat";
		try {
			File file = new File(logPath);
			File parent = file.getParentFile(); 
			if(parent!=null && !parent.exists()){ 
				parent.mkdirs();
			}
			
			if(!file.exists())
				file.createNewFile();
			
			openLog(logPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//logcat
		try {
			File file = new File(logcatFile);
			File parent = file.getParentFile(); 
			if(parent!=null && !parent.exists()){ 
				parent.mkdirs();
			}
			
			if(!file.exists())
				file.createNewFile();
			
			logcat(logcatFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void openLog(String file){
		try{
			outstream = new FileOutputStream(file,true);
	        out = new ObjectOutputStream(outstream);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void saveLog(LogInfo log){
		try{
	        out.writeObject(log);
	        out.flush();
	        //out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void closeLog(){
		
		try{
			if(out != null)
				out.close();
			
			if(input != null)
				input.destory();
			
			if(process != null) {
				System.out.println("process: destory");
				process.destroy();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void loadLogs(Tree treeLog,Display display,String file,ToolBar toolBar1){
        int count = 0;        //case count
        int taskCount = 0;    //task count
        String previous = ""; //previous case name
        String preTask = "";  //previous task name
        TreeItem taskRoot = null;
        TreeItem colRoot = null;
        TreeItem column_1 = null; // loop num
        int loop = 1;
        int iPass = 0;
        int iFail = 0;
        
        try{
			FileInputStream instream = new FileInputStream(file);
	        ObjectInputStream in = new ObjectInputStream(instream);
			
	        while(true){
	        	try{
	        		LogInfo logInfo = (LogInfo)in.readObject();
	        		//System.out.println("#############" + logInfo.task + "#############" + logInfo.loop + "#########" + logInfo.name + "##########");
	        		//System.out.println("first:"+logInfo.task + " "+logInfo.name + " "+logInfo.loop);
	        		if(!preTask.trim().equals(logInfo.task.trim())){
	        			taskCount = 0;
	        			count = 0;
	        			preTask = logInfo.task.trim();
	        			previous = logInfo.name;
	        			loop = logInfo.loop;
	        			
	        			//root
	        			taskRoot = new TreeItem(treeLog, SWT.NONE);
	        			taskRoot.setText("测试任务:"+logInfo.task.trim());//
	        			taskRoot.setImage(new Image(display, ".\\icons\\task.png"));
	        			taskRoot.setData("index",0);
	        			taskRoot.setData("task",logInfo.task);
	        			
	        			//case: Task 1
	        			column_1 = new TreeItem(taskRoot, SWT.NONE);
	        			column_1.setText("第 " + (++taskCount)+"轮测试");
	        			column_1.setImage(new Image(display, ".\\icons\\passed.png"));
	        			column_1.setData("index",taskCount);
	        			column_1.setData("task",logInfo.task);
	        			
	        			//case
	        			colRoot = new TreeItem(column_1, SWT.NONE);
	        			colRoot.setText(logInfo.name.trim());//
	        			colRoot.setImage(new Image(display, ".\\icons\\passed.png"));
	        			colRoot.setData("index",0);
	        			colRoot.setData("task",logInfo.task);
	        			colRoot.setData("case",logInfo.name);
	        			
	        			//case: Run 1
	        			TreeItem column_2 = new TreeItem(colRoot, SWT.NONE);
	        			column_2.setText("Run - " + (++count));
	        			column_2.setData("index",count);
	        			column_2.setData("loop",loop);
	        			column_2.setData("task",logInfo.task);
	        			column_2.setData("case",logInfo.name);
	        			
	        			if(logInfo.result == NGOK.OK){
	        				iPass +=1;
	        				column_2.setImage(new Image(display, ".\\icons\\passed.png"));
		        		}else{
		        			iFail+=1;
		        			column_1.setImage(new Image(display, ".\\icons\\failed.png"));
		        			column_2.setImage(new Image(display, ".\\icons\\failed.png"));
		        			colRoot.setImage(new Image(display, ".\\icons\\failed.png"));
		        		}
	        			
	        		}else{
	        			if(logInfo.loop == loop){
	        				//
	        				if(logInfo.name.equals(previous)){
	        					//RUN - 2,3,4,5,6
	    	        			TreeItem column_x = new TreeItem(colRoot, SWT.NONE);
	    	        			column_x.setText("Run - " + (++count));
	    	        			column_x.setData("index",count);
	    	        			column_x.setData("loop",loop);
	    	        			column_x.setData("task",logInfo.task);
	    	        			column_x.setData("case",logInfo.name);
	    	        			
	    	        			if(logInfo.result == NGOK.OK){
	    	        				iPass +=1;
	    	        				column_x.setImage(new Image(display, ".\\icons\\passed.png"));
	    		        		}else{
	    		        			iFail+=1;
	    		        			column_1.setImage(new Image(display, ".\\icons\\failed.png"));
	    		        			column_x.setImage(new Image(display, ".\\icons\\failed.png"));
	    		        			colRoot.setImage(new Image(display, ".\\icons\\failed.png"));
	    		        		}
	        				}else{
	        					//case: Run 1
	        					count = 0;
	        					previous = logInfo.name;
	        					//case
	    	        			colRoot = new TreeItem(column_1, SWT.NONE);
	    	        			colRoot.setText(logInfo.name.trim());//
	    	        			colRoot.setImage(new Image(display, ".\\icons\\passed.png"));
	    	        			colRoot.setData("index",0);
	    	        			colRoot.setData("task",logInfo.task);
	    	        			colRoot.setData("case",logInfo.name);
	    	        			
	    	        			TreeItem column_2 = new TreeItem(colRoot, SWT.NONE);
	    	        			column_2.setText("Run - " + (++count));
	    	        			column_2.setData("index",count);
	    	        			column_2.setData("loop",loop);
	    	        			column_2.setData("task",logInfo.task);
	    	        			column_2.setData("case",logInfo.name);
	    	        			
	    	        			if(logInfo.result == NGOK.OK){
	    	        				iPass +=1;
	    	        				column_2.setImage(new Image(display, ".\\icons\\passed.png"));
	    		        		}else{
	    		        			iFail+=1;
	    		        			column_1.setImage(new Image(display, ".\\icons\\failed.png"));
	    		        			column_2.setImage(new Image(display, ".\\icons\\failed.png"));
	    		        			colRoot.setImage(new Image(display, ".\\icons\\failed.png"));
	    		        		}
	        				}
	        			}else{
	        				loop = logInfo.loop;
	        				previous = logInfo.name;
	        				count = 0;
		        			//case: Task 1
		        			column_1 = new TreeItem(taskRoot, SWT.NONE);
		        			column_1.setText("第 " + (++taskCount)+"轮测试");
		        			column_1.setImage(new Image(display, ".\\icons\\passed.png"));
		        			column_1.setData("index",taskCount);
		        			column_1.setData("task",logInfo.task);
		        			
		        			//case
		        			colRoot = new TreeItem(column_1, SWT.NONE);
		        			colRoot.setText(logInfo.name.trim());//
		        			colRoot.setImage(new Image(display, ".\\icons\\passed.png"));
		        			colRoot.setData("index",0);
		        			colRoot.setData("task",logInfo.task);
		        			colRoot.setData("case",logInfo.name);
		        			
		        			//case: Run 1
		        			TreeItem column_2 = new TreeItem(colRoot, SWT.NONE);
		        			column_2.setText("Run - " + (++count));
		        			column_2.setData("index",count);
    	        			column_2.setData("loop",loop);
    	        			column_2.setData("task",logInfo.task);
    	        			column_2.setData("case",logInfo.name);
		        			
		        			if(logInfo.result == NGOK.OK){
		        				iPass +=1;
		        				column_2.setImage(new Image(display, ".\\icons\\passed.png"));
			        		}else{
			        			iFail+=1;
			        			column_1.setImage(new Image(display, ".\\icons\\failed.png"));
			        			column_2.setImage(new Image(display, ".\\icons\\failed.png"));
			        			colRoot.setImage(new Image(display, ".\\icons\\failed.png"));
			        		}
	        			}
	        		}
	        		
	        	}catch(Exception eof){
	        		break;
	        	}
	        }
	        
	        
	        toolBar1.getItem(0).setText("总用例数:"+(iPass+iFail));
	        toolBar1.getItem(1).setText("测试通过:"+iPass);
	        toolBar1.getItem(2).setText("测试失败:"+iFail);
	        if((iPass+iFail) != 0)
	        	toolBar1.getItem(3).setText("通过率:"+(int)(((double)iPass/(double)(iPass+iFail))*100)+"%");
	        
	        in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void loadLogs(StyledText styledTextLog,
									Display display,
									String file,
									String taskName,
									String loop,
									String logName,
									int index)
	{
		try{
			FileInputStream instream = new FileInputStream(file);
	        ObjectInputStream in = new ObjectInputStream(instream);
	        int count = 0;
	        while(true){
	        	try{
	        		LogInfo logInfo = (LogInfo)in.readObject();
	        		
	        		if(logInfo.task.equals(taskName) && logInfo.loop == Integer.parseInt(loop)){
	        			if(logInfo.name.trim().equals(logName)){
	        				
		        			count++;
		        			if(count == index){
		        				styledTextLog.setText(logInfo.getRunLog().toString());
		        				break;
		        			}
		        		}
	        		}
	        	}catch(Exception eof){
	        		break;
	        	}
	        }
	        in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void readLog(String file){
		try{
			
			FileInputStream instream = new FileInputStream(file);
	        ObjectInputStream in = new ObjectInputStream(instream);
	        
	        while(true){
	        	try{
	        		LogInfo logInfo = (LogInfo)in.readObject();
	        	    //System.out.println(logInfo.name + " "+logInfo.result);
	        		//System.out.println("      "+logInfo.runLog.toString());
	        	}catch(Exception eof){
	        		break;
	        	}
	        }
	        in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public String trimString(String src){
		String temp = "";
		int index = 0;
		while(src.contains(" ")){
			index = src.indexOf(" ");
			temp += src.substring(0,index);
			src = src.substring(index+1,src.length());
		}
		
		if(index < src.length()-1)
			temp+=src;
		
		return temp;
	}

	
	
}
