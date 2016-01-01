package com.android.python;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.python.core.ArgParser;
import org.python.core.ClassDictInit;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;

import com.android.chromedriver.ChromeDriverClient;
import com.android.chromedriver.ChromeService;
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
	private ChromeDriverClient chromeDriverClient = null;
	private static LaunchSelendroid launcher = null;
	private boolean isPass = true;
	private String taskName = "";
	private String sn = "";
	private String scriptName = "";
	private String scriptPath = "";
	private String act_name = "";
	private String apk_version = "";
	private String pkg_name = "";
	private int sdk_version = 0;
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
	
	public void addLog(String sn, String str) {
		//log4j
		Logger.getLogger(AndroidDriver.class).info("[" +sn + "]" + str);
		//自动化测试日志
		Log.getLoger(sn).logInfo.addRunLog(str);
		//显示在用户UI
		this.setInfo(DisplayUtil.Show.Info, str, sn);
	}
	
	public AndroidDriver(String apkPath, String sn, boolean isSelendroid, String project){
		this.sn = sn;
		this.picCounter = 0;
		this.apkPath = apkPath;
		this.projectPath = project;
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
	
	public boolean connect() throws Exception{
		boolean isLaunched = false;
		if(this.uiAutomatorClient != null)
			isLaunched = this.uiAutomatorClient.connect();
		this.sdk_version = this.uiAutomatorClient.getSDKVersion();
		
		if(this.apkPath != null && !this.apkPath.trim().equals("")) {
			ApkInfo apkInfo = new ApkUtil().getApkInfo(this.apkPath);
			this.act_name = apkInfo.getActivityName();
			this.pkg_name = apkInfo.getPackageName();
			this.apk_version = apkInfo.getVersionName();
		}
		
		Logger.getLogger(this.getClass()).info("[" + sn + "] 连接UiAutomatorHarbout:" + isLaunched + " sdk_version:" + this.sdk_version);
		return isLaunched;
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
	
	public int getSDKVersion() {
		return this.sdk_version;
	}
	
	public static boolean launchSelendroidStandalone(String app, int port) throws Exception {
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
	
	public static void launchChromeServer() throws IOException {
		if(ChromeService.getService() == null) {
			ChromeService.startService();
		}
	}
	
	public void launchChromeDriver() {
		if(this.sdk_version < 19)
			return;
		
		if(chromeDriverClient == null) {
			chromeDriverClient = new ChromeDriverClient(); 
			chromeDriverClient.createDriver(this.pkg_name, this.sn); //
		}
	}
	
	public void stopChromeDriver() {
		if(chromeDriverClient != null) {
			chromeDriverClient.quitDriver();
			chromeDriverClient = null;
		}
	}
	
	public static void stopChromeServer() {
		ChromeService.stopService();
	}

	/**
	 * 执行Install.py脚本，来解决安装过程中遇到的各种提示框
	 */
	public void runBeforeInstall(){
		mInWatcherContext = true;
		System.out.println(this.projectPath + "/Library/Install.py");
		executePython(this.projectPath + "/Library/Install.py", "watcher");
		mInWatcherContext = false;
	}
	
	public boolean setup(final String serial,boolean isSelendroid) throws Exception {
		if(!this.apkPath.trim().equals("")) {
			String isForceInstall = PropertiesUtil.getValue(System.getProperty("user.dir") + 
					"/system.properties", "isForceInstall");
			if(!isForceInstall.trim().toLowerCase().equals("true")) {
				return true;
			}

			boolean bRet = false;
			int count = 3;
			while(count-- > 0){
				Thread preinstall = new Thread(new Runnable(){
					@Override
					public void run() {
						System.out.println("============1121==============");
						runBeforeInstall();
					}});
				preinstall.start();
				System.out.println("============3==============");
				if(isSelendroid && this.sdk_version < 17)
					bRet = this.uiSelendroidClient.
						setup(serial, this.pkg_name, this.act_name, this.apk_version);
				else
					bRet = install();
				
				if(null != preinstall)
					preinstall.stop();
				
				if(bRet)
					break;
			}
			return bRet;
		}
		return true;
	}
	
	/**
	 * 卸载给定的APK
	 * @param pkg
	 * @return
	 * @throws Exception
	 */
	public boolean unInstall(String pkg) throws Exception{
		String uninstall_cmd = "adb -s " + sn + " uninstall " + pkg;
		String result = AdbUtil.send(uninstall_cmd, 60000);
		Logger.getLogger(AndroidDriver.class).info("[" + sn +"] uninstall result:" + pkg + " " + result);
		return result.trim().endsWith("Success");
	}
	
	/**
	 * 安装指定的APK
	 */
	public boolean install() throws Exception{
		boolean ret = true;
		ApkInfo apkInfo = new ApkUtil().getApkInfo(this.apkPath);
		unInstall(apkInfo.getPackageName());
		
		String adbShell = "adb -s " + sn + " install -r " + this.apkPath;
		System.out.println(adbShell);
		String result = AdbUtil.send(adbShell, 60000);
		ret = result.trim().contains("Success");
		Logger.getLogger(AndroidDriver.class).info("[" + sn +"] 安装[" + this.apkPath + "] ret=" + ret);
		return ret;
	}
	
	/**
	 * 刷新进度条
	 */
	public void refreshProgressData(){
		Element element = new Element();
		element.setElement(UIElement.TASK_PROGRESS_BAR);
		element.setSN(sn);
		element.setTaskName(taskName);
		element.setScriptName(scriptName);
		UIPool.offer(element);
	}
	
	/**
	 * 执行指定的python文件
	 * @param filePath
	 * @return
	 */
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
	
	/**
	 * 执行Watcher.py文件
	 */
	public void runWatcher(){
		System.out.println("run watcher");
		mInWatcherContext = true;
		executePython(this.projectPath + "/Library/Watcher.py", "watcher");
		mInWatcherContext = false;
	}
	
	//==========================================================================================================
	//                               以下部分为暴露给脚本运行的API
	//==========================================================================================================
	
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
		
		//刷新进度条
		this.refreshProgressData();
		//添加日志
		addLog(sn, "registerWatcher[" + name + "]");
		//UI界面显示步骤
		this.setInfo(DisplayUtil.Show.Info, "registerWatcher[" + name + "]", sn);
		
		UiWatcher watcher = new UiWatcher(name,path,func);
		mWatchers.put(name, watcher);
	}
	
	@MonkeyRunnerExported(doc="remove watcher", args={}, argDocs={}, returns = "void")
	public void removeWatcher(PyObject[] args, String[] kws){
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String name = ap.getString(0);
		
		//刷新进度条
		this.refreshProgressData();
		//添加日志
		addLog(sn, "removeWatcher[" + name + "]");
		//UI界面显示步骤
		this.setInfo(DisplayUtil.Show.Info, "removeWatcher[" + name + "]", sn);
		
		if(mWatchers.containsKey(name))
			mWatchers.remove(name);
	}
	
	@MonkeyRunnerExported(doc="trigger watcher", args={}, argDocs={}, returns = "void")
	public void triggerWatchers(PyObject[] args, String[] kws){
		//刷新进度条
		this.refreshProgressData();
		//添加日志
		addLog(sn,"triggerWatchers");
		//UI界面显示步骤
		this.setInfo(DisplayUtil.Show.Info, "triggerWatchers", sn);
		//runWatchers();
		runWatcher();
	}
	
	/**
	 * Reboot device, need rooted
	 * @param args
	 * @param kws
	 * @return
	 * @throws Exception
	 */
	@MonkeyRunnerExported(doc="reboot device", args={}, argDocs={}, returns = "boolean")
	public boolean reboot(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		boolean ret = true;
		this.refreshProgressData();
		Thread.sleep(5000);
		return ret;
	}
	
	/**
	 * 执行单个Python命令语句
	 * @param args
	 * @param kws
	 * @return
	 * @throws Exception
	 */
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
			int milli = ap.getInt(0);
			
			this.refreshProgressData();
			this.setInfo(DisplayUtil.Show.Info, "休眠"+milli+"毫秒", sn);
		
			Thread.sleep(milli);
		}catch(Exception ex){
			
		}
		return ret;
	}
	
	@MonkeyRunnerExported(doc="install app", args={"app path"}, argDocs={}, returns = "boolean")
	public boolean install(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String filePath = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		
		ApkInfo apkInfo = new ApkUtil().getApkInfo(filePath);
		String getAppVersionCode = "cmd /c adb -s " + sn + " shell dumpsys package " + apkInfo.getPackageName() + " | findstr versionCode";
		String d_versionCode = AdbUtil.send(getAppVersionCode, 5000);
		
		String getAppVersionName = "cmd /c adb -s " + sn + " shell dumpsys package " + apkInfo.getPackageName() + " | findstr versionName";
		String d_versionName = AdbUtil.send(getAppVersionName, 5000);
		
		//APP已经安装
		if(d_versionCode.trim().endsWith(apkInfo.getVersionCode())
				&& d_versionName.trim().endsWith(apkInfo.getVersionName())) {
			addLog(sn, "安装[" + filePath + "] ret=App已经安装");
			return true;
		}
		
		//APP未安装
		String adbShell = "adb -s " + sn + " install -r " + filePath;
		String result = AdbUtil.send(adbShell, 60000);
		return result.trim().endsWith("Success");
	}
	
	/**
	 * 通过ActivityName启动应用，Eg:
	 * @param args
	 * @param kws
	 * @return
	 * @throws Exception
	 */
	@MonkeyRunnerExported(doc="start app", args={"app path"}, argDocs={}, returns = "boolean")
	public boolean startActivity(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String activityName = 
				new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		AdbUtil.send("adb -s " + sn + " shell am start " + activityName, 30000);
		addLog(sn, "启动[" + activityName + "]成功");
		return true;
	}
	
	@MonkeyRunnerExported(doc="start app", args={"app path"}, argDocs={}, returns = "String")
	public String getActivityName(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		addLog(sn, this.pkg_name + "/" +this.act_name);
		return this.pkg_name + "/" +this.act_name;
	}
	
	@MonkeyRunnerExported(doc="start URL", args={"URL"}, argDocs={"URL link for App"}, returns = "boolean")
	public boolean startURL(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String url = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		AdbUtil.send("adb -s " + sn + " shell \"am start -W -a 'android.intent.action.VIEW' -d '" + url + "'\"", 30000);
		addLog(sn, "启动[" + url + "]成功");
		return true;
	}
	
	/**
	 * 设置当前脚本录制的坐标所对应的手机分辨率，用于在其他分辨率下坐标便宜
	 * @param args
	 * @param kws
	 * @throws Exception
	 */
	@MonkeyRunnerExported(doc="set script supports screen size", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public void script(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		this.s_width = ap.getInt(0);
		this.s_height = ap.getInt(1);
		
		this.d_width = this.uiAutomatorClient.getDisplayWidth();
		this.d_height = this.uiAutomatorClient.getDisplayHeight();
		addLog(sn, "设置脚本分辨率" + this.s_width + "*" + this.s_height);
	}
	
	//****************************************
	//       支持Native的API
	//****************************************
	
	/**
	 * 点击字符串或者坐标
	 * @param args
	 * @param kws
	 * @return
	 * @throws Exception
	 */
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean click(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		int x = 0;
		int y = 0;
		
		String text = "";
		int instance = 0;

		//判断是字符串还是坐标
		PyObject  obj = ap.getPyObject(0); //get first
		if(obj instanceof PyString){
			text = new String(obj.asString().getBytes("ISO-8859-1"), "UTF-8");
			if(args.length == 2)
				instance = ap.getInt(1);
		}else {
			x = ap.getInt(0)*this.d_width/this.s_width;
			y = ap.getInt(1)*this.d_height/this.s_height;
		}

		this.refreshProgressData();
		
		boolean ret = false;
		if(!text.equals("")){
			ret = this.uiAutomatorClient.click(text,instance);
			addLog(sn, "click[" + text + "] ret=" + ret);
			
//			while(false == ret && (mWatchers.size() > 0)) {
//				System.out.println("run watchers");
//				runWatcher();
//				ret = true;//this.uiAutomatorClient.click(text,instance);
//				if(null != Log.getLoger(sn))
//					Log.getLoger(sn).logInfo.addRunLog("click[" + text + "] 返回="+ret + " Count="+count);
//				this.setInfo(DisplayUtil.Show.Info, "click[" + text + "] 返回="+ret + " Count="+count, sn);
//				if(count-- < 0)
//					break;
//			}
			
			if(ret == false && !mInWatcherContext)
				throw new Exception(Errors.TOUCH_TEXT_FAIL);
			else
				return ret;
		}else{
			ret = this.uiAutomatorClient.click(x, y);
			addLog(sn, "click[" + x +"," + y + "] 返回="+ret);
			return ret;
		}
	}
	
	@MonkeyRunnerExported(doc="click id", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean clickById(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String id = ap.getString(0);
		int instance = ap.getInt(1);
		
		this.refreshProgressData();
		boolean bRet = this.uiAutomatorClient.clickById(id, instance);
		addLog(sn, "clickById[" + id + "," + instance + "] ret=" + bRet);
		
		return bRet;
	}
	
	@MonkeyRunnerExported(doc="click class", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean clickByClass(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String object = ap.getString(0);
		int instance = ap.getInt(1);
		
		boolean bRet = this.uiAutomatorClient.clickByClass(object, instance);
		addLog(sn, "clickByClass[" + object + "," + instance + "] ret=" + bRet);
		return bRet;
	}
	
	/**
	 * 长按
	 * @param args
	 * @param kws
	 * @return
	 * @throws Exception
	 */
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean longClick(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		int x = 0;
		int y = 0;
		
		String text = "";
		int instance = 0;

		//判断是字符串还是坐标
		PyObject  obj = ap.getPyObject(0); //get first
		if(obj instanceof PyString){
			text = new String(obj.asString().getBytes("ISO-8859-1"), "UTF-8");
			if(args.length == 2)
				instance = ap.getInt(1);
		}else {
			x = ap.getInt(0)*this.d_width/this.s_width;
			y = ap.getInt(1)*this.d_height/this.s_height;
		}

		this.refreshProgressData();
		
		boolean ret = false;
		if(!text.equals("")){
			ret = this.uiAutomatorClient.longClick(text);
			addLog(sn, "longClick[" + text + "] ret=" + ret);
			
//			while(false == ret && (mWatchers.size() > 0)) {
//				System.out.println("run watchers");
//				runWatcher();
//				ret = true;//this.uiAutomatorClient.click(text,instance);
//				if(null != Log.getLoger(sn))
//					Log.getLoger(sn).logInfo.addRunLog("click[" + text + "] 返回="+ret + " Count="+count);
//				this.setInfo(DisplayUtil.Show.Info, "click[" + text + "] 返回="+ret + " Count="+count, sn);
//				if(count-- < 0)
//					break;
//			}
			
			if(ret == false && !mInWatcherContext)
				throw new Exception(Errors.TOUCH_TEXT_FAIL);
			else
				return ret;
		}else{
			ret = this.uiAutomatorClient.longClick(x, y);
			addLog(sn, "longClick[" + x +"," + y + "] 返回="+ret);
			return ret;
		}
	}
	
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean clickAndWaitForNewWindow(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String text = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		long timeout = ap.getInt(1);
		
		boolean ret = this.uiAutomatorClient.clickAndWaitForNewWindow(text, timeout);
		addLog(sn, "clickAndWaitForNewWindow[" + text + "] 返回="+ret);
		
//		int count = 2;
//		while(false == ret && (mWatchers.size() > 0)) {
//			System.out.println("run watchers");
//			runWatcher();
//			ret = true;//this.uiAutomatorClient.clickAndWaitForNewWindow(text, timeout);
//			Log.getLoger(sn).logInfo.addRunLog("clickAndWaitForNewWindow[" + text + "] 返回="+ret + " Count="+count);
//			this.setInfo(DisplayUtil.Show.Info, "clickAndWaitForNewWindow[" + text + "] 返回="+ret + " Count="+count, sn);
//			if(count-- < 0)
//				break;
//		}
		
		if(ret)
			return true;
		else
			throw new Exception(Errors.TOUCH_TEXT_FAIL);
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressBack(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressBack");
		return this.uiAutomatorClient.pressBack();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadCenter(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressDPadCenter");
		return this.uiAutomatorClient.pressDPadCenter();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadDown(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressDPadDown");
		return this.uiAutomatorClient.pressDPadDown();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadLeft(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressDPadLeft");
		return this.uiAutomatorClient.pressDPadLeft();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadRight(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressDPadRight");
		return this.uiAutomatorClient.pressDPadRight();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDPadUp(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressDPadUp");
		return this.uiAutomatorClient.pressDPadUp();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressDelete(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressDelete");
		return this.uiAutomatorClient.pressDelete();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressEnter(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressEnter");
		return this.uiAutomatorClient.pressEnter();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressHome(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressHome");
		return this.uiAutomatorClient.pressHome();
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public boolean pressMenu(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		this.addLog(sn, "pressMenu");
		return this.uiAutomatorClient.pressMenu();
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
		this.addLog(sn, "swipe[" + startX +", "+ startY + ", " + endX +"," + endY+ "] 返回="+ret);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean scrollToBeginning(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		int maxSwipes = ap.getInt(0);

		this.refreshProgressData();
		boolean ret = this.uiAutomatorClient.scrollToBeginning(maxSwipes);
		addLog(sn, "scrollToBeginning[" + maxSwipes + "] 返回="+ret);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="click screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean scrollToEnd(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		int maxSwipes = ap.getInt(0);

		this.refreshProgressData();
		boolean ret = this.uiAutomatorClient.scrollToEnd(maxSwipes);
		addLog(sn, "scrollToEnd[" + maxSwipes + "] 返回="+ret);
		return ret;
	}
	
	@MonkeyRunnerExported(doc="get script compatible screen", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public String shell(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String shell = ap.getString(0);
		int  timeout = ap.getInt(1);
		
		this.refreshProgressData();
		this.addLog(sn, "shell[" + shell + "]");
		return AdbUtil.send(shell, timeout);
	}
	
	@MonkeyRunnerExported(doc="search text in UI", args={"text"}, argDocs={"Text"}, returns = "boolean")
	public boolean findText(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		this.refreshProgressData();
		String text = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		boolean bRet = this.uiAutomatorClient.textContains(text);
//		while(false == bRet && (mWatchers.size() > 0)) {
//			System.out.println("run watchers");
//			runWatchers();
//			bRet = this.uiAutomatorClient.textContains(text);
//			if(count-- < 0)
//				break;
//		}

		this.addLog(sn, "textContains[" + text + "] 返回="+bRet);
		return bRet;
	}
	
	@MonkeyRunnerExported(doc="findAndClickByScrollable", args={"text"}, argDocs={"src - source picture", "dest - dest picture"}, returns = "boolean")
	public boolean findAndClickByScrollable(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String text = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		this.refreshProgressData();
		return this.uiAutomatorClient.findAndClickByScrollable(text);
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
		addLog(sn, "setTextById[" + id + ", " +instance + ", "+ text + "] 返回="+true);
		return this.uiAutomatorClient.setTextById(id, instance, text);
	}
	
	@MonkeyRunnerExported(doc="set text to TextView", args={"Object","instance","text"}, argDocs={"TextView Object", "index in UI", "Text"}, returns = "boolean")
	public boolean setTextByClass(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String object = ap.getString(0);
		int instance = ap.getInt(1);
		String text = new String(ap.getString(2).getBytes("ISO-8859-1"), "UTF-8");

		this.refreshProgressData();
		this.addLog(sn, "setTextByClass[" + object + ", " +instance + ", "+ text + "] 返回="+true);		
		return this.uiAutomatorClient.setTextByClass(object, instance, text);
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
		
		this.refreshProgressData();
		return this.uiAutomatorClient.pressKeyWords(str);
	}
	
	@MonkeyRunnerExported(doc="set script supports screen size", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "no")
	public String getTextById(PyObject[] args, String[] kws) throws Exception{
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String id = ap.getString(0);
		int instance = ap.getInt(1);
		
		return this.uiAutomatorClient.getTextById(id, instance);
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
		}
		addLog(sn, "takeSnapshot[" + path +", "+ fileName + "] 返回="+ret);
		
		return ret;
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
			String screen = this.uiAutomatorClient.takeSnapshot(System.getProperty("user.dir") + 
					"/temp/", "screen.png");
			
			String trimFilePath = StringUtil.trim(filePath);
			String project = trimFilePath.substring(0, trimFilePath.indexOf("/"));
			Area area = AreaLoader.load(System.getProperty("user.dir") + 
					"/workspace/" + project + "/screen_area.xml",trimFilePath);
			
			return new ImageUtil(filePath, screen, rate).compare(area.getX(), area.getY(), area.getWidth(), area.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 调用指定的Python脚本
	 * @param args
	 * @param kws
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@MonkeyRunnerExported(doc="invokeMethod", args={"text"}, argDocs={"src - source picture", "dest - dest picture"}, returns = "boolean")
	public PyObject invoke(PyObject[] args, String[] kws) throws UnsupportedEncodingException {
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

			ret = executePython(path, method, obj);
			return ret;
		}
		return ret;
	}


	//****************************************
	//        Native end
	//****************************************
	
	//****************************************
	//           系统API调用
	//****************************************
	
	@MonkeyRunnerExported(doc="tap by XPath for webview", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public void call(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String num = ap.getString(0);
		String adbShell = "adb shell am start -a android.intent.action.CALL -d tel:" + num;
		AdbUtil.send(adbShell, 3000);
		
		addLog(sn, "call[" + num + "]");
	}
	
	@MonkeyRunnerExported(doc="tap by XPath for webview", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public void sendSMS(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String num = ap.getString(0);
		String sms = new String(ap.getString(1).getBytes("ISO-8859-1"), "UTF-8");
		
		String cmd = "adb shell am start -a android.intent.action.SENDTO -d sms:" + num + " --es sms_body \"" + sms + "\" --ez exit_on_sent true";
		AdbUtil.send(cmd, 5000);
		addLog(sn, "sendSMS[" + num + "][" + sms + "]");
	}
	//****************************************
	//        System API end
	//****************************************
	
	//****************************************
	//       支持WebView的API
	//****************************************
		
	@MonkeyRunnerExported(doc="getPageSource", args={"void"}, argDocs={""}, returns = "Serial")
	public String getPageSource(PyObject[] args, String[] kws) {
		this.refreshProgressData();
		
		this.addLog(sn, "getPageSource");
		if(this.sdk_version >= 19) {
			launchChromeDriver();
			return this.chromeDriverClient.getpageSource();
		}else
			return this.uiSelendroidClient.getPageSource();
	}
	
	@MonkeyRunnerExported(doc="getDriver", args={"void"}, argDocs={""}, returns = "driver")
	public Object getWebDriver(PyObject[] args, String[] kws) {
		this.refreshProgressData();
		
		addLog(sn, "getDriver");
		if(this.sdk_version >= 19) {
			launchChromeDriver();
			return this.chromeDriverClient.getDriver();
		}else
			return this.uiSelendroidClient.getPageSource();
	}
	
	@MonkeyRunnerExported(doc="getDriver", args={"void"}, argDocs={""}, returns = "driver")
	public boolean sendKeysByXpath(PyObject[] args, String[] kws) {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		String xPath = ap.getString(0);
		String str   = ap.getString(1);
		
		this.refreshProgressData();
		addLog(sn, "sendKeysByXpath");
		if(this.sdk_version >= 19) {
			launchChromeDriver();
			return this.chromeDriverClient.sendKeys(By.xpath(xPath), str);
		}else
			return this.uiSelendroidClient.sendKeys(xPath, str);
	}
	
	@MonkeyRunnerExported(doc="tap by XPath for webview", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean tapByXpath(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		String xpath = new String(ap.getString(0).getBytes("ISO-8859-1"), "UTF-8");
		
		if(this.sdk_version < 19)
			return false;
		
		launchChromeDriver();
		boolean bRet = this.chromeDriverClient.tapByXPath(xpath);
		addLog(sn, "tapByXPath[" + xpath + "] ret=" + bRet);
		
		return bRet;
	}
	
	@MonkeyRunnerExported(doc="tap", args={"x", "y"}, argDocs={"x coordinate in pixels", "y coordinate in pixels"}, returns = "boolean")
	public boolean tap(PyObject[] args, String[] kws) throws Exception {
		ArgParser ap = JythonUtils.createArgParser(args, kws);
		Preconditions.checkNotNull(ap);
		
		PyObject object = ap.getPyObject(0);
		
		if(this.sdk_version < 19)
			return false;
//		
//		launchChromeDriver();
//		boolean bRet = this.chromeDriverClient.tapByXPath(xpath);
//		addLog(sn, "tapByXPath[" + xpath + "] ret=" + bRet);
		
		return true;
	}
	
	//****************************************
	//       WebView End
	//****************************************
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

