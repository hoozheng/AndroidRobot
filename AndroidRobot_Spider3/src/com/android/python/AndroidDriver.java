package com.android.python;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.openqa.selenium.WebElement;
import org.python.core.ArgParser;
import org.python.core.ClassDictInit;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;

import com.android.log.Log;
import com.android.robot.AndroidRobot;
import com.android.selendroid.LaunchSelendroid;
import com.android.selendroid.UiSelendroidClient;
import com.android.ui.data.Element;
import com.android.ui.data.UIElement;
import com.android.ui.data.UIPool;
import com.android.uiautomator.UiAutomatorClient;
import com.android.util.AdbUtil;
import com.android.util.ApkInfo;
import com.android.util.ApkUtil;
import com.android.util.DisplayUtil;
import com.android.util.ImageUtil;
import com.android.util.PropertiesUtil;
import com.android.util.StringUtil;
import com.google.common.base.Preconditions;

public class AndroidDriver extends PyObject implements ClassDictInit{
	private UiAutomatorClient  uiAutomatorClient = null;
	private UiSelendroidClient uiSelendroidClient = null;
	private static LaunchSelendroid launcher = null;
	private boolean isPass = true;
	private String taskName = "";
	private String sn = "";
	private String scriptName = "";
	private String scriptPath = "";
	private String act_name = "";
	private String apk_version = "";
	private String pkg_name = "";
	//script support s_width*s_height
	private int s_width = 1;
	private int s_height = 1;
	
	//device width*height
	private int d_width = 1;
	private int d_height = 1;
	private HashMap<String, UiWatcher> mWatchers = new HashMap<String, UiWatcher>();
	private boolean mInWatcherContext = false;
	private String window = "NATIVE_APP";//WEBVIEW_0
	private String apkPath = "";
	private int picCounter = 0;
	private String projectPath = "";
	
	public static void classDictInit(PyObject dict){
		JythonUtils.convertDocAnnotationsForClass(AndroidDriver.class, dict);
	}
	
	public AndroidDriver(String apkPath, String sn, boolean isSelendroid, String project){
		this.sn = sn;
		this.picCounter = 0;
		this.apkPath = apkPath;
		this.projectPath = project;
		System.out.println("project:" + this.projectPath);
		this.uiAutomatorClient  = new UiAutomatorClient(this.sn);
		if(isSelendroid)
			this.uiSelendroidClient = new UiSelendroidClient(apkPath, this.sn, 4444);
	}
	
	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	
	public void setScriptName(String scriptName){
		this.scriptName = scriptName;
	}
	
	public void setScriptPath(String scriptPath){
		this.scriptPath = scriptPath;
	}
	
	public boolean isPass() {
		return isPass;
	}
	
	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}
	
	public String getSN(){
		return this.sn;
	}
	
	public boolean connect() throws InterruptedException{
		boolean isLaunched = false;
		if(this.uiAutomatorClient != null)
			isLaunched = this.uiAutomatorClient.connect();
		Logger.getLogger(this.getClass()).info("[" + sn + "] 连接UiAutomatorHarbout" + isLaunched);
		return isLaunched;
	}
	
	public static synchronized boolean launchSelendroidStandalone(String app, int port) throws Exception {
		if(null == launcher) {
			launcher = new LaunchSelendroid(app, port);
			Thread thread = new Thread(launcher);
			thread.start();
			thread.join(30000);
			launcher.getMessage();
		}
		
		return true;
	}
	
	public static boolean stopSelendroidStandalone() {
		if(null != launcher)
			launcher.stopSelendroid();
		return true;
	}
	
	public void runBeforeInstall(){
		mInWatcherContext = true;
		executePython(this.projectPath + "/Library/Install.py", "watcher");
		mInWatcherContext = false;
	}
	
	public boolean setup(final String serial,boolean isSelendroid) throws Exception {
		if(!this.apkPath.trim().equals("")) {
			ApkInfo apkInfo = new ApkUtil().getApkInfo(this.apkPath);
			this.act_name = apkInfo.getActivityName();
			this.pkg_name = apkInfo.getPackageName();
			this.apk_version = apkInfo.getVersionName();
			
			String isForceInstall = PropertiesUtil.getValue("./system.properties", "isForceInstall");
			
			if(!isForceInstall.trim().toLowerCase().equals("true")) {
				return true;
			}
			
			Thread preinstall = new Thread(new Runnable(){
				@Override
				public void run() {
					runBeforeInstall();
				}});
			preinstall.start();

			
			boolean bRet = false;
			if(isSelendroid)
				bRet = this.uiSelendroidClient.
					setup(serial, this.pkg_name, this.act_name, this.apk_version);
			else
				bRet = install();
			
			if(null != preinstall)
				preinstall.stop();
			return bRet;
		}
		return true;
	}
	
	public boolean unInstall(String pkg) throws Exception{
		String uninstall_cmd = "adb -s " + sn + " uninstall " + pkg;
		String result = new AdbUtil().send(uninstall_cmd, 60000);
		Logger.getLogger(AndroidDriver.class).info("[" + sn +"] uninstall result:" + pkg + " " + result);
		return result.trim().endsWith("Success");
	}
	
	public boolean install() throws Exception{
		boolean ret = true;
		ApkInfo apkInfo = new ApkUtil().getApkInfo(this.apkPath);
		String getAppVersionCode = "cmd /c adb -s " + sn + " shell dumpsys package " + apkInfo.getPackageName() + " | findstr versionCode";
		String d_versionCode = AdbUtil.send(getAppVersionCode, 5000);
		
		String getAppVersionName = "cmd /c adb -s " + sn + " shell dumpsys package " + apkInfo.getPackageName() + " | findstr versionName";
		String d_versionName = AdbUtil.send(getAppVersionName, 5000);
		
		unInstall(apkInfo.getPackageName());
		
//		System.out.println("d_versionCode:" + d_versionCode + " getVersion:" + apkInfo.getVersionCode());
//		
//		System.out.println("d_versionName:" + d_versionName + " getVersionName:" + apkInfo.getVersionName());
		
//		if(d_versionName.trim().endsWith(apkInfo.getVersionName())) {
//			System.out.println("安装[" + this.apkPath + "] ret=App已经安装");
//			System.out.println("安装[" + this.apkPath + "] ret=App正在卸载");
//			return true;
//		}
		
		String adbShell = "adb -s " + sn + " install -r " + this.apkPath;
		String result = AdbUtil.send(adbShell, 60000);
		//ret = result.trim().endsWith("Success");
		ret = result.trim().contains("Success");
		Logger.getLogger(AndroidDriver.class).info("[" + sn +"] 安装[" + this.apkPath + "] ret=" + ret);
//		Log.getLoger(sn).logInfo.addRunLog("安装[" + this.apkPath + "]成功");
//		this.setInfo(DisplayUtil.Show.Info, "安装[" + filePath + "]成功", sn);
		return ret;
	}
	
	public boolean disconnect() throws Exception {
		boolean bRet = true;
		if(this.uiAutomatorClient != null){
			bRet = this.uiAutomatorClient.disconnect();
			this.uiAutomatorClient = null;
		}
		
		if(this.uiSelendroidClient != null) {
			this.uiSelendroidClient.quit();
			this.uiSelendroidClient = null;
		}
		
		return bRet;
	}
	
	public void refreshProgressData(){
		Element element = new Element();
		element.setElement(UIElement.TASK_PROGRESS_BAR);
		element.setSN(sn);
		element.setTaskName(taskName);
		element.setScriptName(scriptName);
		UIPool.offer(element);
	}
	
	@MonkeyRunnerExported(doc="getSerialNumber", args={"void"}, argDocs={""}, returns = "Serial ")
	public String getSerialNumber(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		return this.sn;
	}
	
	@MonkeyRunnerExported(doc="switchToWindow", args={"void"}, argDocs={""}, returns = "Serial ")
	public void switchToWindow(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		window = ap.getString(0);
		this.uiSelendroidClient.switchTo(window);
	}
	
	@MonkeyRunnerExported(doc="getPageSource", args={"void"}, argDocs={""}, returns = "Serial ")
	public String getPageSource(PyObject[] args, String[] kws) {
		this.refreshProgressData();
		this.setInfo(DisplayUtil.Show.Info, "getPageSource", sn);
		Logger.getLogger(AndroidDriver.class).info(this.uiSelendroidClient.getPageSource());
		return this.uiSelendroidClient.getPageSource();
	}
	
	@MonkeyRunnerExported(doc="register watcher", args={}, argDocs={}, returns = "void")
	public void registerWatcher(PyObject[] args, String[] kws){
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String name = "";
		String path = "";
		String func = "";
		
		name = ap.getString(0);
		path = ap.getString(1);
		func = ap.getString(2);
		
		this.refreshProgressData();
		Logger.getLogger(AndroidDriver.class).info("[" +sn + "]registerWatcher[" + name + "]");
		Log.getLoger(sn).logInfo.addRunLog("registerWatcher[" + name + "]");
		this.setInfo(DisplayUtil.Show.Info, "registerWatcher[" + name + "]", sn);
		
		UiWatcher watcher = new UiWatcher(name,path,func);
		mWatchers.put(name, watcher);
	}
	
	@MonkeyRunnerExported(doc="remove watcher", args={}, argDocs={}, returns = "void")
	public void removeWatcher(PyObject[] args, String[] kws){
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String name = ap.getString(0);
		this.refreshProgressData();
		Logger.getLogger(AndroidDriver.class).info("[" +sn + "removeWatcher[" + name + "]");
		Log.getLoger(sn).logInfo.addRunLog("removeWatcher[" + name + "]");
		this.setInfo(DisplayUtil.Show.Info, "removeWatcher[" + name + "]", sn);
		
		if(mWatchers.containsKey(name))
			mWatchers.remove(name);
	}
	
	@MonkeyRunnerExported(doc="trigger watcher", args={}, argDocs={}, returns = "void")
	public void triggerWatchers(PyObject[] args, String[] kws){
		this.refreshProgressData();
		Logger.getLogger(AndroidDriver.class).info("[" +sn + "triggerWatchers");
		Log.getLoger(sn).logInfo.addRunLog("triggerWatchers");
		this.setInfo(DisplayUtil.Show.Info, "triggerWatchers", sn);
		//runWatchers();
		runWatcher();
	}
	
	private PyObject executePython(String filePath){
		PythonInterpreter interpreter = new PythonInterpreter();
		Vector<AndroidDriver> drivers = new Vector();
		drivers.add(this);
		interpreter.set("device", drivers);
		interpreter.execfile(filePath);
		PyObject ret = interpreter.eval("True");
		return ret;
	}
	
	private PyObject executePython(String filePath, String function){
		PythonInterpreter interpreter = new PythonInterpreter();
		Vector<AndroidDriver> drivers = new Vector();
		drivers.add(this);
		interpreter.set("device", drivers);
		interpreter.execfile(filePath);
		System.out.println(filePath + "  " + function);
		PyFunction pyfunction = interpreter.get(function, PyFunction.class);
		PyObject pyobj = pyfunction.__call__();
		return pyobj;
	}
	
	private PyObject executePython(String filePath, String function, PyObject params){
		PythonInterpreter interpreter = new PythonInterpreter();
		Vector<AndroidDriver> drivers = new Vector();
		drivers.add(this);
		interpreter.set("device", drivers);
		interpreter.execfile(filePath);
		PyFunction pyfunction = interpreter.get(function, PyFunction.class);
		PyObject pyobj = pyfunction.__call__(params.__getitem__(0));
		return pyobj;
	}
	
	public void runWatchers(){
		if(true == mInWatcherContext)
			return;
		
		Iterator iterator = mWatchers.keySet().iterator();
		while(iterator.hasNext()) {
			mInWatcherContext = true;
			UiWatcher watcher = mWatchers.get(iterator.next());
			executePython(watcher.getWatcherPath(), watcher.getWatcherFunc());
		}
		
		mInWatcherContext = false;
	}
	
	public void runWatcher(){
		System.out.println("run watcher");
		mInWatcherContext = true;
		executePython(this.projectPath + "/Library/Watcher.py", "watcher");
		mInWatcherContext = false;
	}
	
	@MonkeyRunnerExported(doc="reboot device", args={}, argDocs={}, returns = "boolean")
	public boolean reboot(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		boolean ret = true;
		System.out.println("reboot");
		
		this.refreshProgressData();
		
		Thread.sleep(5000);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="exec command", args={}, argDocs={}, returns = "boolean")
	public boolean exec(PyObject[] args, String[] kws) throws Exception{
		boolean ret = true;
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String cmd = ap.getString(0);
		
		PythonInterpreter interpreter = new PythonInterpreter();
		Vector<AndroidDriver> drivers = new Vector();
		drivers.add(this);
		interpreter.set("device", drivers);
		interpreter.exec(cmd);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="reboot device", args={}, argDocs={}, returns = "boolean")
	public boolean sleep(PyObject[] args, String[] kws) throws Exception{
		boolean ret = true;
		try{
			ArgParser ap = JythonUtils.createArgParser(args, kws);
			Preconditions.checkNotNull(ap);
		
			this.refreshProgressData();
			int milli = ap.getInt(0);
			this.setInfo(DisplayUtil.Show.Info, "休眠"+milli+"毫秒", sn);
		
		
			Thread.sleep(milli);
		}catch(Exception ex){
			
		}
		return ret;
	}
	
	@MonkeyRunnerExported(doc="install app", args={"app path"}, argDocs={}, returns = "boolean")
	public boolean install(PyObject[] args, String[] kws) throws Exception{
		boolean ret = true;
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String filePath = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		
		ApkInfo apkInfo = new ApkUtil().getApkInfo(filePath);
		String getAppVersionCode = "cmd /c adb -s " + sn + " shell dumpsys package " + apkInfo.getPackageName() + " | findstr versionCode";
		String d_versionCode = new AdbUtil().send(getAppVersionCode, 5000);
		
		String getAppVersionName = "cmd /c adb -s " + sn + " shell dumpsys package " + apkInfo.getPackageName() + " | findstr versionName";
		String d_versionName = new AdbUtil().send(getAppVersionName, 5000);
		
		System.out.println("d_versionCode:" + d_versionCode + " " + d_versionName);
		
		if(d_versionCode.trim().endsWith(apkInfo.getVersionCode())
				&& d_versionName.trim().endsWith(apkInfo.getVersionName())) {
			Log.getLoger(sn).logInfo.addRunLog("安装[" + filePath + "] ret=App已经安装");
			this.setInfo(DisplayUtil.Show.Info, "安装[" + filePath + "] ret=App已经安装", sn);
			return true;
		}
		
		String adbShell = "adb -s " + sn + " install -r " + filePath;
		String result = new AdbUtil().send(adbShell, 60000);
		ret = result.trim().endsWith("Success");
		Log.getLoger(sn).logInfo.addRunLog("安装[" + filePath + "]成功");
		this.setInfo(DisplayUtil.Show.Info, "安装[" + filePath + "]成功", sn);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="start app", args={"app path"}, argDocs={}, returns = "boolean")
	public boolean startActivity(PyObject[] args, String[] kws) throws Exception{
		boolean ret = true;
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		//Preconditions.checkNotNull(ap);
		String activityName = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		

		String shell = "adb -s " + sn + " shell am start " + activityName;
		String response = AdbUtil.send(shell, 30000);
		
		Thread.sleep(3000);
		
		Logger.getLogger(this.getClass()).info("[" + sn + "]启动" + activityName + " " + response);
		Log.getLoger(sn).logInfo.addRunLog("启动[" + activityName + "]成功");
		this.setInfo(DisplayUtil.Show.Info, "启动[" + activityName + "]成功", sn);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="start app", args={"app path"}, argDocs={}, returns = "String")
	public String getActivityName(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Logger.getLogger(AndroidDriver.class).info("[" + sn + "]"+ this.pkg_name + "/" +this.act_name);
		return this.pkg_name + "/" +this.act_name;
	}
	
	
	
	@MonkeyRunnerExported(doc="start URL", args={"URL"}, argDocs={"URL link for App"}, returns = "boolean")
	public boolean startURL(PyObject[] args, String[] kws) throws Exception{
		boolean ret = true;
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String url = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		String shell = "adb -s " + sn + " shell \"am start -W -a 'android.intent.action.VIEW' -d '" + url + "'\"";
		
		String result = new AdbUtil().send(shell, 30000);
		Thread.sleep(5000);
		Logger.getLogger(AndroidDriver.class).info("[" + sn + "]" + "启动[" + url + "]成功");
		Log.getLoger(sn).logInfo.addRunLog("启动[" + url + "]成功");
		this.setInfo(DisplayUtil.Show.Info, "启动[" + url + "]成功", sn);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="set script supports screen size", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public void script(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		this.s_width = ap.getInt(0);
		this.s_height = ap.getInt(1);
		
		this.d_width = this.uiAutomatorClient.getDisplayWidth();
		this.d_height = this.uiAutomatorClient.getDisplayHeight();
		
		Log.getLoger(sn).logInfo.addRunLog("设置脚本分辨率" + this.s_width + "*" + this.s_height);
		this.setInfo(DisplayUtil.Show.Info, "设置脚本分辨率" + this.s_width + "*" + this.s_height, sn);
	}
	
	@MonkeyRunnerExported(doc="click id", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean clickById(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String id = ap.getString(0);
		int instance = ap.getInt(1);
		
		boolean bRet = this.uiAutomatorClient.clickById(id, instance);
		Logger.getLogger(AndroidDriver.class).info("[" + sn + "]" + "clickById[" + id + "," + instance + "] ret=" + bRet);
		Log.getLoger(sn).logInfo.addRunLog("clickById[" + id + "," + instance + "] ret=" + bRet);
		this.setInfo(DisplayUtil.Show.Info, "clickById[" + id + "," + instance + "] ret=" + bRet, sn);
		return bRet;
	}
	
	@MonkeyRunnerExported(doc="click class", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean clickByClass(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String object = ap.getString(0);
		int instance = ap.getInt(1);
		boolean bRet = this.uiAutomatorClient.clickByClass(object, instance);
		
		Logger.getLogger(AndroidDriver.class).info("[" + sn + "]" + "clickByClass[" + object + "," + instance + "] ret=" + bRet);
		Log.getLoger(sn).logInfo.addRunLog("clickByClass[" + object + "," + instance + "] ret=" + bRet);
		this.setInfo(DisplayUtil.Show.Info, "clickByClass[" + object + "," + instance + "] ret=" + bRet, sn);
		
		return bRet;
	}
	
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean click(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		int x = 0;
		int y = 0;
		String text = "";
		int instance = 0;
		
		PyObject  obj = ap.getPyObject(0); //get first
		if(obj instanceof PyString){
			text = new String(obj.asString().getBytes("ISO-8859-1"), "UTF-8");
			if(args.length == 2)
				instance = ap.getInt(1);
		}else {
			x = ap.getInt(0)*this.d_width/this.s_width;
			y = ap.getInt(1)*this.d_height/this.s_height;
		}

		boolean ret = false;
		this.refreshProgressData();
		if(!text.equals("")){
			text = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
			
			if(this.window.equals("NATIVE_APP"))
				ret = this.uiAutomatorClient.click(text,instance);
			else if(this.window.equals("WEBVIEW_0")) {
				this.uiSelendroidClient.findElement("//input[@value='" + text+"']").click();
				ret = true;
			}
			if(null != Log.getLoger(sn))
				Log.getLoger(sn).logInfo.addRunLog("click[" + text + "] 返回="+ret);
			this.setInfo(DisplayUtil.Show.Info, "click[" + text + "] 返回="+ret, sn);
			int count = 2;
			while(false == ret && (mWatchers.size() > 0)) {
				System.out.println("run watchers");
				runWatcher();
				ret = this.uiAutomatorClient.click(text,instance);
				if(null != Log.getLoger(sn))
					Log.getLoger(sn).logInfo.addRunLog("click[" + text + "] 返回="+ret + " Count="+count);
				this.setInfo(DisplayUtil.Show.Info, "click[" + text + "] 返回="+ret + " Count="+count, sn);
				if(count-- < 0)
					break;
			}
			

			if(ret == false && !mInWatcherContext)
				throw new Exception(Errors.TOUCH_TEXT_FAIL);
			else
				return ret;
		}else{
			ret = this.uiAutomatorClient.click(x, y);
			if(null != Log.getLoger(sn))
				Log.getLoger(sn).logInfo.addRunLog("click[" + x +"," + y + "] 返回="+ret);	
			this.setInfo(DisplayUtil.Show.Info, "click[" + x +"," + y + "] 返回=" + ret, sn);
			
			return ret;
		}
	}
	
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean longClick(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		int x = 0;
		int y = 0;
		String text = "";

		boolean ret = false;
		this.refreshProgressData();
		if(args.length == 1){
			text = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
			ret = this.uiAutomatorClient.longClick(text);
			
			Log.getLoger(sn).logInfo.addRunLog("longClick[" + text + "] 返回="+ret);
			this.setInfo(DisplayUtil.Show.Info, "longClick[" + text + "] 返回="+ret, sn);
			int count = 2;
			while(false == ret && (mWatchers.size() > 0)) {
				System.out.println("run watchers");
				runWatcher();
				ret = this.uiAutomatorClient.longClick(text);
				Log.getLoger(sn).logInfo.addRunLog("longClick[" + text + "] 返回="+ret + " Count="+count);
				this.setInfo(DisplayUtil.Show.Info, "longClick[" + text + "] 返回="+ret + " Count="+count, sn);
				if(count-- < 0)
					break;
			}
			

			if(ret)
				return true;
			else
				throw new Exception(Errors.TOUCH_TEXT_FAIL);
			
		}else if(args.length == 2){
			x = ap.getInt(0)*this.d_width/this.s_width;
			y = ap.getInt(1)*this.d_height/this.s_height;
			ret = this.uiAutomatorClient.longClick(x, y);
			Log.getLoger(sn).logInfo.addRunLog("longClick[" + x +"," + y + "] 返回="+ret);
			this.setInfo(DisplayUtil.Show.Info, "longClick[" + x +"," + y + "] 返回=" + ret, sn);
			
			return ret;
			//System.out.println("click= x=" + x + " y="+y + " d_width=" + this.d_width + " s_width="+this.s_width + " d_height=" + d_height + " s_height=" + s_height);
		}
		return ret;
		
	}
	
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean scrollToBeginning(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		boolean ret = false;
		this.refreshProgressData();
		int maxSwipes = ap.getInt(0);
		
		ret = this.uiAutomatorClient.scrollToBeginning(maxSwipes);
		Log.getLoger(sn).logInfo.addRunLog("scrollToBeginning[" + maxSwipes + "] 返回="+ret);
		this.setInfo(DisplayUtil.Show.Info, "scrollToBeginning[" + maxSwipes + "] 返回=" + ret, sn);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean scrollToEnd(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		boolean ret = false;
		this.refreshProgressData();
		int maxSwipes = ap.getInt(0);
		
		ret = this.uiAutomatorClient.scrollToEnd(maxSwipes);
		Log.getLoger(sn).logInfo.addRunLog("scrollToEnd[" + maxSwipes + "] 返回="+ret);
		this.setInfo(DisplayUtil.Show.Info, "scrollToEnd[" + maxSwipes + "] 返回=" + ret, sn);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean clickAndWaitForNewWindow(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		boolean ret = false;

		this.refreshProgressData();
		String text = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		long timeout = ap.getInt(1);
		ret = this.uiAutomatorClient.clickAndWaitForNewWindow(text, timeout);
		
		Log.getLoger(sn).logInfo.addRunLog("clickAndWaitForNewWindow[" + text + "] 返回="+ret);
		this.setInfo(DisplayUtil.Show.Info, "clickAndWaitForNewWindow[" + text + "] 返回="+ret, sn);
		int count = 2;
		while(false == ret && (mWatchers.size() > 0)) {
			System.out.println("run watchers");
			runWatcher();
			ret = this.uiAutomatorClient.clickAndWaitForNewWindow(text, timeout);
			Log.getLoger(sn).logInfo.addRunLog("clickAndWaitForNewWindow[" + text + "] 返回="+ret + " Count="+count);
			this.setInfo(DisplayUtil.Show.Info, "clickAndWaitForNewWindow[" + text + "] 返回="+ret + " Count="+count, sn);
			if(count-- < 0)
				break;
		}
		
		if(ret)
			return true;
		else
			throw new Exception(Errors.TOUCH_TEXT_FAIL);
	}
	
	@MonkeyRunnerExported(doc="search text in UI", args={"text"}, argDocs={"Text"}, returns = "boolean")
	public boolean textContains(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String text = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		this.refreshProgressData();
		boolean bRet = false;
		int count = 2;
		bRet = this.uiAutomatorClient.textContains(text);
//		while(false == bRet && (mWatchers.size() > 0)) {
//			System.out.println("run watchers");
//			runWatchers();
//			bRet = this.uiAutomatorClient.textContains(text);
//			if(count-- < 0)
//				break;
//		}

		if(null != Log.getLoger(sn))
			Log.getLoger(sn).logInfo.addRunLog("textContains[" + text + "] 返回="+bRet);	
		this.setInfo(DisplayUtil.Show.Info, "textContains[" + text + "] 返回="+bRet, sn);
		return bRet;
	}
	
	
	@MonkeyRunnerExported(doc="search text in UI", args={"text"}, argDocs={"Text"}, returns = "boolean")
	public boolean findElementByXpath(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String xpath = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		this.refreshProgressData();
		WebElement bRet = null;
		int count = 2;
//		this.uiSelendroidClient.switchTo("WEBVIEW_0");
		
		bRet = this.uiSelendroidClient.findElement(xpath);
//		while(null == bRet && (mWatchers.size() > 0)) {
//			System.out.println("run watchers");
//			runWatchers();
//			bRet = this.uiSelendroidClient.findElement(xpath);
//			if(count-- < 0)
//				break;
//		}

//		this.uiSelendroidClient.switchTo("NATIVE_APP");
		
		Log.getLoger(sn).logInfo.addRunLog("findElementByXpath[" + xpath + "] 返回="+true);
		this.setInfo(DisplayUtil.Show.Info, "findElementByXpath[" + xpath + "] 返回="+true, sn);
		
		return (bRet != null)?true:false;
	}
	
	@MonkeyRunnerExported(doc="search text in UI", args={"text"}, argDocs={"Text"}, returns = "boolean")
	public boolean findElementById(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String id = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		this.refreshProgressData();
		boolean bRet = false;
		int count = 2;
		bRet = this.uiAutomatorClient.findElementById(id);
//		while(false == bRet && (mWatchers.size() > 0)) {
//			System.out.println("run watchers");
//			runWatchers();
//			bRet = this.uiAutomatorClient.findElementById(id);
//			if(count-- < 0)
//				break;
//		}

		Log.getLoger(sn).logInfo.addRunLog("findElementById[" + id + "] 返回="+bRet);
		this.setInfo(DisplayUtil.Show.Info, "findElementById[" + id + "] 返回="+bRet, sn);
		return bRet;
	}
	
	@MonkeyRunnerExported(doc="set text to TextView", args={"Object","instance","text"}, argDocs={"TextView Object", "index in UI", "Text"}, returns = "boolean")
	public boolean setTextByClass(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		boolean bRet = false;
		String object = ap.getString(0);
		int instance = ap.getInt(1);
		String text = new String(ap.getString(2).getBytes("ISO-8859-1"), "UTF-8");

		this.refreshProgressData();
		
		if(this.uiAutomatorClient.setTextByClass(object, instance, text)){
			Logger.getLogger(AndroidDriver.class).info("[" + sn + "]" + "setTextByClass[" + object + ", " +instance + ", "+ text + "] 返回="+true);
			Log.getLoger(sn).logInfo.addRunLog("setTextByClass[" + object + ", " +instance + ", "+ text + "] 返回="+true);
			this.setInfo(DisplayUtil.Show.Info, "setTextByClass[" + object + ", " +instance + ", "+ text + "] 返回="+true, sn);
			return true;
		}else{
			//if failed, run watcher
			int count = 2;
			while(bRet == false) {
				Logger.getLogger(AndroidDriver.class).info("[" + sn + "]" + "run watcher");
				runWatcher();
				bRet = this.uiAutomatorClient.setTextByClass(object, instance, text);
				Log.getLoger(sn).logInfo.addRunLog("setTextByClass[" + object + ", " +instance + ", "+ text + "] 返回="+bRet);
				this.setInfo(DisplayUtil.Show.Info, "setTextByClass[" + object + ", " +instance + ", "+ text + "] 返回="+bRet, sn);
				if(count-- < 0)
					break;
			}
		}
		
		return bRet;
	}
	
	
	@MonkeyRunnerExported(doc="set text to TextView", args={"Object","instance","text"}, argDocs={"TextView Object", "index in UI", "Text"}, returns = "boolean")
	public boolean setTextByXpath(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		boolean bRet = false;
		String xpath = ap.getString(0);
		String text = new String(ap.getString(1).getBytes("ISO-8859-1"), "UTF-8");

		this.refreshProgressData();

//		this.uiSelendroidClient.switchTo("WEBVIEW_0");

		if(this.uiSelendroidClient.sendKeys(xpath, text)) {
			Log.getLoger(sn).logInfo.addRunLog("setTextByXpath[" + xpath + ", " + text + "] 返回="+true);
			this.setInfo(DisplayUtil.Show.Info, "setTextByXpath[" + xpath + ", " + text + "] 返回="+true, sn);
			bRet = true;
		}else{
			//if failed, run watcher
			int count = 2;
			while(bRet == false) {
				System.out.println("run watchers");
				runWatcher();
				bRet = this.uiSelendroidClient.sendKeys(xpath, text);
				Log.getLoger(sn).logInfo.addRunLog("setTextByXpath[" + xpath + ", " +text + "] 返回="+bRet);
				this.setInfo(DisplayUtil.Show.Info, "setTextByXpath[" + xpath + ", " +text + "] 返回="+bRet, sn);
				if(count-- < 0)
					break;
			}
		}
		
//		this.uiSelendroidClient.switchTo("NATIVE_APP");
		
		return bRet;
	}
	
	@MonkeyRunnerExported(doc="set text to TextView", args={"Object","instance","text"}, argDocs={"TextView Object", "index in UI", "Text"}, returns = "boolean")
	public boolean setTextById(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		System.out.println(window);
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String id = ap.getString(0);
		int instance = ap.getInt(1);
		String text = new String(ap.getString(2).getBytes("ISO-8859-1"), "UTF-8");

		this.refreshProgressData();
		
		if(this.window.equals("NATIVE_APP")) {
			if(this.uiAutomatorClient.setTextById(id, instance, text)){
				Log.getLoger(sn).logInfo.addRunLog("setTextById[" + id + ", " +instance + ", "+ text + "] 返回="+true);
				this.setInfo(DisplayUtil.Show.Info, "setTextById[" + id + ", " +instance + ", "+ text + "] 返回="+true, sn);
				return true;
			}
		}else if(this.window.equals("WEBVIEW_0")) {
			if(this.uiSelendroidClient.sendKeys("//input[@id='" + id+"']", text)) {
				Log.getLoger(sn).logInfo.addRunLog("setTextById[" + id + ", " +instance + ", "+ text + "] 返回="+true);
				this.setInfo(DisplayUtil.Show.Info, "setTextById[" + id + ", " +instance + ", "+ text + "] 返回="+true, sn);
				return true;
			}
		}
		
		Log.getLoger(sn).logInfo.addRunLog("setTextById[" + id + ", " +instance + ", "+ text + "] 返回="+false);
		this.setInfo(DisplayUtil.Show.Info, "setTextById[" + id + ", " +instance + ", "+ text + "] 返回="+false, sn);
		return false;
	}
	
	@MonkeyRunnerExported(doc="takeSnapshot", args={"path", "fileName"}, argDocs={"releative path", "png name"}, returns = "String")
	public String takeSnapshot(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String path = "";
		String fileName = "";
		if(args.length == 1) {
			path = Log.getLoger(sn).getPath();
			fileName = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		}else if(args.length == 2) {
			path = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
			fileName = new String(ap.getString(1).getBytes("ISO-8859-1"), "UTF-8");
		}

		this.refreshProgressData();
		this.picCounter +=1;
		String counter = String.format("%04d", this.picCounter);
		String ret = "";
		
		for(int i=0;i<5;i++) {
			ret = this.uiAutomatorClient.takeSnapshot(path, counter + "_" +fileName);
			File file = new File(path + "/" + counter + "_" +fileName);
			if(file.exists()) {
				break;
			}
			Log.getLoger(sn).logInfo.addRunLog("takeSnapshot[" + path +", "+ fileName + "] 返回="+false);
		}
		Log.getLoger(sn).logInfo.addRunLog("takeSnapshot[" + path +", "+ fileName + "] 返回="+ret);
		this.setInfo(DisplayUtil.Show.Info, "takeSnapshot[" + path +", "+ fileName + "] 返回="+ret, sn);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean swipe(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		int startX = ap.getInt(0)*this.d_width/this.s_width;  //
		int startY = ap.getInt(1)*this.d_height/this.s_height;
		int endX = ap.getInt(2)*this.d_width/this.s_width;
		int endY = ap.getInt(3)*this.d_height/this.s_height;

		this.refreshProgressData();
		boolean ret = this.uiAutomatorClient.swipe(startX, startY, endX, endY, 20);
		Log.getLoger(sn).logInfo.addRunLog("swipe[" + startX +", "+ startY + ", " + endX +"," + endY+ "] 返回="+ret);
		this.setInfo(DisplayUtil.Show.Info, "swipe[" + startX +", "+ startY + ", " + endX +"," + endY+ "] 返回="+ret, sn);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressBack(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		boolean ret = this.uiAutomatorClient.pressBack();
		Log.getLoger(sn).logInfo.addRunLog("pressBack() 返回="+ret);
		this.setInfo(DisplayUtil.Show.Info, "pressBack() 返回="+ret, sn);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadCenter(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressDPadCenter();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadDown(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressDPadDown();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadLeft(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressDPadLeft();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadRight(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressDPadRight();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadUp(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressDPadUp();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDelete(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressDelete();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressEnter(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressEnter();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressHome(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressHome();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressMenu(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressMenu();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressKeyCode(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		int keyCode = ap.getInt(0);
		int metaState = ap.getInt(1);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressKeyCode(keyCode, metaState);
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressKeyWords(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String str = ap.getString(0);
		
		Log.getLoger(sn).logInfo.addRunLog("pressKeyWords(" + str + ")");
		this.setInfo(DisplayUtil.Show.Info, "pressKeyWords(" + str + ")", sn);
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressKeyWords(str);
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean logInfo(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String log = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		Log.getLoger(sn).logInfo.addRunLog(log);
		return true;
	}
	
	@MonkeyRunnerExported(doc="compare src to dest", args={"src", "rate"}, argDocs={"src - source picture", "dest - dest picture"}, returns = "boolean")
	public boolean compare(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String filePath = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8").replace("\\", "/");
		int rate = ap.getInt(1);
		
		this.refreshProgressData();
		try {
			String screen = this.uiAutomatorClient.takeSnapshot("./temp/", "screen.png");
			
			String trimFilePath = StringUtil.trim(filePath);
			String project = trimFilePath.substring(0, trimFilePath.indexOf("/"));
			Area area = AreaLoader.load("./workspace/" + project + "/screen_area.xml",trimFilePath);
			
			return new ImageUtil(filePath, screen, rate).compare(area.getX(), area.getY(), area.getWidth(), area.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@MonkeyRunnerExported(doc="findAndClickByScrollable", args={"text"}, argDocs={"src - source picture", "dest - dest picture"}, returns = "boolean")
	public boolean findAndClickByScrollable(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String text = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		try {
			return this.uiAutomatorClient.findAndClickByScrollable(text);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@MonkeyRunnerExported(doc="invokeMethod", args={"text"}, argDocs={"src - source picture", "dest - dest picture"}, returns = "boolean")
	public PyObject invokeMethod(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String path = "";
		String method = "";
		PyTuple obj = null;
		PyObject ret = null;
		
		if(args.length == 1) {
			path = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
			ret = executePython(path);
			return ret;
		}else if(args.length == 2){
			path = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
			method = ap.getString(1);
			ret = executePython(path, method);
			return ret;
		}else if(args.length == 3){
			path = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
			method = ap.getString(1);
			obj = (PyTuple)ap.getList(2);

			ret = executePython(path, method, obj);//obj.__getitem__(0).__getitem__(0)
			return ret;
		}
		return ret;
	}
	
	private void setInfo(final DisplayUtil.Show show,final String key,final String sn){
		Display.getDefault().asyncExec(new Runnable(){
			public void run()
			{
				AndroidRobot.showLog(show,key,sn);
			}
		});
	}
}

class Install implements Runnable{
	private UiSelendroidClient uiSelendroidClient = null;
	private String serial = "";
	private String pkgName = "";
	private String actName = "";
	private String version = "";
	private String exception = "";
	public Install(UiSelendroidClient uiSelendroidClient, String serial, String pkgName, String actName, String version) {
		this.uiSelendroidClient = uiSelendroidClient;
		this.serial = serial;
		this.pkgName = pkgName;
		this.actName = actName;
		this.version = version;
	}
	
	public void getMessage() throws Exception {
		if(!exception.trim().equals("")) {
			throw new Exception(this.exception);
		}
	}
	
	@Override
	public void run() {
		try {
			this.uiSelendroidClient.setup(serial, pkgName, actName, version);
		} catch (Exception e) {
			exception = e.getMessage();
		}
	}
	
}

