package com.android.python;

import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.android.log.Log;
import com.android.log.LogInfo;
import com.android.log.NGOK;
import com.android.robot.AndroidRobot;
import com.android.tasks.Task;
import com.android.tasks.TestCase;
import com.android.ui.data.Element;
import com.android.ui.data.UIElement;
import com.android.ui.data.UIPool;
import com.android.util.DisplayUtil;
import com.android.util.PropertiesUtil;
import com.android.util.TimeUtil;

public class RobotScriptRunner extends Thread{
	private boolean isRunning = false;
	
	private Vector<Task> tasks = null;
	private Vector<AndroidDriver> drivers = null;
	private boolean isSelendroid = false;
	//private Log log = null;
	
	private int case_pass_num = 0;
	private int case_fail_num = 0;
	private int task_exec_num = 0;
	
	public RobotScriptRunner(Vector<Task> tasks,Vector<AndroidDriver> drivers,Log log, boolean isSelendroid) {
		this.tasks = tasks;
		this.drivers = drivers;
		this.isSelendroid = isSelendroid;
	}
	
	public void setInfo(final DisplayUtil.Show show,final String key,final String sn){
		Display.getDefault().asyncExec(new Runnable(){
			public void run()
			{
				AndroidRobot.showLog(show,key,sn);
			}
		});
	}
	
	public void refreshUI(String elementName, String sn, String taskName, String scriptName,Object value) {
		Element element = new Element();
		if(elementName.equals(UIElement.TASK_PROGRESS_BAR)){
			element.setElement(UIElement.TASK_PROGRESS_BAR);
		}else if(elementName.equals(UIElement.TASK_START_TIME)){
			element.setElement(UIElement.TASK_START_TIME);
		}else if(elementName.equals(UIElement.TASK_TEST_RESULT)){
			element.setElement(UIElement.TASK_START_TIME);
		}else{
			return;
		}
		
		element.setSN(sn);
		element.setTaskName(taskName);
		element.setScriptName(scriptName);
		element.setValue(value);
		UIPool.offer(element);
	}
	
	public void setResult(final String sn,final String taskName,final String scriptName,final boolean result){
		Display.getDefault().asyncExec(new Runnable(){
			public void run()
			{
				AndroidRobot.setPassOrFailCount(sn,taskName,scriptName,result);
			}
		});
	}
	
	public void execute(Task task, int index, Vector<TestCase> testcases){
		for(int j=0; j < testcases.size();j++){
			for(int k=0; k < testcases.get(j).loop; k++){
				//set taskName & scriptName 
				for(int h=0; h<drivers.size(); h++){
					drivers.get(h).setPass(true);
					drivers.get(h).setScriptName(task.vecTC.get(j).name);
					drivers.get(h).setScriptPath(task.vecTC.get(j).path);
					drivers.get(h).setTaskName(task.name);
				}
				refreshUI(UIElement.TASK_START_TIME, drivers.get(0).getSN(), task.name, testcases.get(j).name, TimeUtil.getTimeAsFormat("yyyy-MM-dd HH:mm:ss"));
				
				//Set Log class
				LogInfo logInfo = new LogInfo();
				logInfo.task = task.name;
				logInfo.loop = (index+1);
				setInfo(DisplayUtil.Show.Start," 当前用例:"+task.vecTC.get(j).name+" 任务次数:"+(index+1)+"/"+task.loop+" 运行次数:"+ (k+1) +"/"+testcases.get(j).loop,drivers.get(0).getSN());
				logInfo.addRunLog("[测试开始]["+TimeUtil.getTimeAsFormat("yyyy-MM-dd HH:mm:ss")+"] "+task.vecTC.get(j).name+" 运行次数:"+ (k+1) +"/"+testcases.get(j).loop);
				logInfo.name = task.vecTC.get(j).name;
				logInfo.result = NGOK.OK;
				//this.log.logInfo = logInfo;
				Log.getLoger(drivers.get(0).getSN()).logInfo = logInfo;
				String args[] = {task.vecTC.get(j).path};
				
				MonkeyRunnerOptions options = MonkeyRunnerOptions.processOptions(args);
				if (options == null) {
					setInfo(DisplayUtil.Show.Error,"脚本不存在或无法读取...",drivers.get(0).getSN());
					continue;
				}
				
				PythonProject runner = new PythonProject(options,drivers);
				int ret = runner.run();
				
				System.out.println("========result==========" + ret);
				
				AndroidDriver driver = drivers.get(0);
				if(ret != 0)
					driver.setPass(false);
				
				if(driver != null && driver.isPass()){
					case_pass_num++;
					setInfo(DisplayUtil.Show.Result," 第"+ (k+1) +"次运行 测试通过",drivers.get(0).getSN());
					String endTime = TimeUtil.getTimeAsFormat("yyyy-MM-dd HH:mm:ss");
					logInfo.addRunLog("[测试结果]["+endTime+"] 第"+ (k+1) +"次运行 测试通过");
					logInfo.result = NGOK.OK;
				}else{
					case_fail_num++;
					setInfo(DisplayUtil.Show.Result," 第"+ (k+1) +"次运行 测试失败",drivers.get(0).getSN());
					String endTime = TimeUtil.getTimeAsFormat("yyyy-MM-dd HH:mm:ss");
					logInfo.addRunLog("[测试结果]["+endTime+"] 第"+ (k+1) +"次运行 测试失败");
					logInfo.result = NGOK.NG;
				}
				
				if(ret == 2){
					if(isRunning == false){
						task_exec_num = index;  //task execute times
						setInfo(DisplayUtil.Show.Result," 第"+ (j+1) +"次运行 手工中断测试",drivers.get(0).getSN());
						logInfo.addRunLog("[测试结果]["+TimeUtil.getTimeAsFormat("yyyy-MM-dd HH:mm:ss")+"] 第"+ (j+1) +"次运行 手工中断测试");
					}
					//break;
				}
				
				//refreshUI(UIElement.TASK_TEST_RESULT, drivers.get(0).getSN(), task.name, testcases.get(j).name, drivers.get(0).isPass());
				setResult(drivers.get(0).getSN(), task.name, testcases.get(j).name, drivers.get(0).isPass());
				//this.log.saveLog(logInfo);
				Log.getLoger(drivers.get(0).getSN()).saveLog(logInfo);
				
				if(isRunning == false)
					return;
			}
			
			refreshUI(UIElement.TASK_PROGRESS_BAR, drivers.get(0).getSN(), task.name, testcases.get(j).name, 100);
		}
		
		
	}

	@Override
	public void run() {
		isRunning = true;
		
		//task size
		for(int i=0;i<this.tasks.size();i++){
			Task task = this.tasks.get(i);
			case_pass_num = case_fail_num = task_exec_num = 0;
			//task loop
			for(int l=0;l<task.loop;l++){
				execute(task, l, task.vecTC);
				
				if(isRunning == false)
					break;
			}
			
			if(isRunning == false)
				break;
		}
		finished();
		//this.log.closeLog();
		Log.getLoger(drivers.get(0).getSN()).closeLog();
	}
	
	public void finished(){
		System.out.println("finished");
		this.isRunning = false;
		for(int i=0; i<drivers.size();i++)
			try {
				drivers.get(i).stopChromeDriver();
				drivers.get(i).disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
