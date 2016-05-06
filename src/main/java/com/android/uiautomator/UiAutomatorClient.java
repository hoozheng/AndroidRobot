package com.android.uiautomator;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.android.python.AndroidDriver;
import com.android.util.AdbUtil;
import com.android.util.JsonUtil;
import com.android.util.XmlUtil;

public class UiAutomatorClient {
	private String sn = "";
	private SocketClient socket = null;
	private static Logger logger = Logger.getLogger(UiAutomatorClient.class);
	public UiAutomatorClient(String sn){
		this.sn = sn;
	}
	
	public boolean connect() throws InterruptedException{
		int port = 5048;
		Thread thread = new Thread(new StartUiAutomatorServer(sn, port));
		thread.start();
		thread.join(20000);
		
		//connect
		socket = new SocketClient();
		try {
			if(true == socket.connect("127.0.0.1", port)) {
				return true;
			}
		} catch (Exception e) {
			logger.error("socket connect error:", e);
			return false;
		}
		
		return false;
	}
	
	public boolean disconnect() throws Exception {
		return this.socket.disconnect();
	}
	
	public int getDisplayWidth(){
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("getDisplayWidth", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Integer.parseInt(sendData.getCommand());
	}
	
	public int getDisplayHeight(){
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("getDisplayHeight", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Integer.parseInt(sendData.getCommand());
	}
	
	public String getTextById(String id, int instance){
		HashMap<String,Object> map = new HashMap();
		map.put("id", id);
		map.put("instance", instance);
		
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("getTextById", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return sendData.getCommand();
	}
	
	public int getSDKVersion() {
		HashMap<String,Object> map = new HashMap();
		map.put("no", "key");
		
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("getSDKVersion", map));
		
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Integer.parseInt(sendData.getCommand());
	}
	
	public String takeSnapshot(String path,String fileName,float scale,int quality)throws Exception{
		HashMap<String,Object> map = new HashMap();
		map.put("fileName", "screenshot.png");
		map.put("scale", scale);
		map.put("quality", quality);
		int sdk_int = 0;
		String result = "";
		String picInDev = "";
		
		try{
			sdk_int = getSDKVersion();
		}catch(Exception ex) {
			sdk_int = 0;
		}
		Logger.getLogger(this.getClass()).info("[" + sn + "]takeScreenshot");
		if(sdk_int < 17) {
			picInDev = result = "/data/local/tmp/screenshot.png";
			AdbUtil.send("adb -s " + sn + " shell screencap -p /data/local/tmp/screenshot.png", 15000);
		}else {
			result = this.socket.sendMessageAndGetRespond(Request.getRequest("takeScreenshot", map));
			SendData sendData = JsonUtil.fromJson(result, SendData.class);
			picInDev = sendData.getCommand();
		}
		
		String cpPicture = "adb -s " + sn + " pull " + picInDev + " " + path +"/" + fileName;
		AdbUtil.send(cpPicture, 30000);
		return path + "/" +fileName;
	}
	
	public String takeSnapshot(String path,String fileName)throws Exception{
		return this.takeSnapshot(path, fileName, 1f, 100);
	}
	
	public boolean touchDown(int x,int y) {
		HashMap<String,Object> map = new HashMap();		
		map.put("x", x);
		map.put("y", y);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("touchDown", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}	
	
	public boolean touchUp(int x,int y) {
		HashMap<String,Object> map = new HashMap();		
		map.put("x", x);
		map.put("y", y);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("touchUp", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}	
	
	public boolean touchMove(int x,int y) {
		HashMap<String,Object> map = new HashMap();		
		map.put("x", x);
		map.put("y", y);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("touchMove", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}	
	
	public boolean click(int x,int y) {
		HashMap<String,Object> map = new HashMap();		
		map.put("x", x);
		map.put("y", y);

		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + x + "," + y +")");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("click", map));		
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean click(String text, int instance) {
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		map.put("instance", instance);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("click", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean clickById(String id, int instance) {
		HashMap<String,Object> map = new HashMap();
		map.put("id", id);
		map.put("instance", instance);
		
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("clickById", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean clickByClass(String object, int instance) {
		HashMap<String,Object> map = new HashMap();
		map.put("object", object);
		map.put("instance", instance);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("clickByClass", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean longClick(String text) {
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("longClick", map));
		
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean clickAndWaitForNewWindow(String text, long timeout) {
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		map.put("timeout", timeout);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("clickAndWaitForNewWindow", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean longClick(int x,int y) {
		HashMap<String,Object> map = new HashMap();
		map.put("startX", x);
		map.put("startY", y);
		map.put("endX", x);
		map.put("endY", y);
		map.put("steps", 100);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("swipe", map));
		
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean scrollToBeginning(int maxSwipes) {
		HashMap<String,Object> map = new HashMap();
		map.put("maxSwipes", maxSwipes);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("scrollToBeginning", map));

		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean scrollToEnd(int maxSwipes) {
		HashMap<String,Object> map = new HashMap();
		map.put("maxSwipes", maxSwipes);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("scrollToEnd", map));

		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean scrollForward(int steps) {
		HashMap<String,Object> map = new HashMap();
		map.put("steps", steps);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("scrollForward", map));
		
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean scrollBackward(int steps) {
		HashMap<String,Object> map = new HashMap();
		map.put("steps", steps);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("scrollBackward", map));
		
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
		HashMap<String,Object> map = new HashMap();
		map.put("startX", startX);
		map.put("startY", startY);
		map.put("endX", endX);
		map.put("endY", endY);
		map.put("steps", steps);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("swipe", map));
		
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean textContains(String text) {
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("textContains", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean setTextByClass(String object, int instance, String text) {
		HashMap<String,Object> map = new HashMap();
		map.put("object", object);
		map.put("instance", instance);
		map.put("text", text);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("setTextByClass", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean setTextById(String id, int instance, String text) {
		HashMap<String,Object> map = new HashMap();
		map.put("id", id);
		map.put("instance", instance);
		map.put("text", text);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("setTextById", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	
//	public boolean findElementById(String id) {
//		HashMap<String,Object> map = new HashMap();
//		map.put("id", id);
//
//		String result = 
//				this.socket.sendMessageAndGetRespond(Request.getRequest("findElementById", map));
//		
//		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
//		return Boolean.parseBoolean(trueORfalse);
//	} 
	
	public boolean pressBack() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressBack", map));
		
		Logger.getLogger(this.getClass()).info("[" + sn +"] pressBack() result=" + result);
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressDPadCenter() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadCenter", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);	
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressDPadDown() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadDown", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressDPadLeft() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadLeft", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressDPadRight() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadRight", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressDPadUp() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadUp", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressDelete() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDelete", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressEnter() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressEnter", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressHome() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressHome", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressKeyCode(int keyCode, int metaState) {
		HashMap<String,Object> map = new HashMap();
		map.put("keyCode", keyCode);
		map.put("metaState", metaState);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressKeyCode", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean pressKeyWords(String str) {
		boolean bRet = true;
		HashMap<String,Object> map = new HashMap();		
		for(int i=0; i<str.length(); i++) {
			map.clear();
			char c = str.charAt(i);
			if(c >=48 && c <=57){
				map.put("keyCode", c-41);
			}else if(c >=97 && c <=122) {
				map.put("keyCode", c-68);
			}else if(c >=65 && c <=90) {
				//UiDevice.getInstance().pressKeyCode(59);
				//sleep(20);
				map.put("keyCode", c-36);
				map.put("metaState", 1);
			}else if(c == 32){
				map.put("keyCode", 62);//SPACE
			}else if(c == 33){
				map.put("keyCode", 8);//!
				map.put("metaState", 1);
			}else if(c == 64){         
				map.put("keyCode", 77);//@
			}else if(c == 95){
				map.put("keyCode", 69);//_
				map.put("metaState", 1);
			}else if(c == 43){
				map.put("keyCode", 81);//+
			}else if(c == 126){
				map.put("keyCode", 68);//~
				map.put("metaState", 1);
			}else{
				bRet = false;
				break;
			}
			
			String result = 
					socket.sendMessageAndGetRespond(Request.getRequest("pressKeyCode", map));
			SendData sendData = JsonUtil.fromJson(result, SendData.class);
			bRet = Boolean.parseBoolean(sendData.getCommand());
			if(bRet == false)
				break;
		}
		
		return bRet;
	}
	
	public boolean pressMenu() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressMenu", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);	
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
	
	public boolean findAndClickByScrollable(String text){
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("findAndClickByScrollable", map));
		SendData sendData = JsonUtil.fromJson(result, SendData.class);	
		
		return Boolean.parseBoolean(sendData.getCommand());
	}
}
