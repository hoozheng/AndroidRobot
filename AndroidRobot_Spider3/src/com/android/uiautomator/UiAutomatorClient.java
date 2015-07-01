package com.android.uiautomator;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.android.util.AdbUtil;
import com.android.util.JsonUtil;
import com.android.util.XmlUtil;

public class UiAutomatorClient {
	private String sn = "";
	private SocketClient socket = null;
	
	public UiAutomatorClient(String sn){
		this.sn = sn;
	}
	
	private int getPort(String sn) {
		XmlUtil xml = new XmlUtil("./devices_database.xml");
		try {
			Document doc = xml.parse("./devices_database.xml");
			Element root = doc.getDocumentElement();
			NodeList childs = root.getChildNodes();
	        if(childs!=null){
	        	for(int i=0;i<childs.getLength();i++){
	        		Node project = childs.item(i);
	        		if(project.getNodeType()==Node.ELEMENT_NODE){
	        			NamedNodeMap map = project.getAttributes();
	        			if(map.getNamedItem("sn").getNodeValue().equals(sn)){
	        				System.out.println("true");
	        				String port = map.getNamedItem("port").getNodeValue();
	        				return Integer.parseInt(port);
	        			}
	        		}
	        	}
	        	
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 5048;
		}
		return 5048;
	}
	
	public boolean connect() throws InterruptedException{
		//adb push jar to sn device
		int port = getPort(sn);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean disconnect() throws Exception {
		return this.socket.disconnect();
	}
	
	public int getDisplayWidth(){
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");
		Logger.getLogger(this.getClass()).info("[" + sn +"] getDisplayWidth()");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("getDisplayWidth", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] getDisplayWidth() result=" + result);
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Integer.parseInt(trueORfalse);
	}
	
	public int getDisplayHeight(){
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		Logger.getLogger(this.getClass()).info("[" + sn +"] getDisplayHeight()");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("getDisplayHeight", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] getDisplayHeight() result=" + result);
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Integer.parseInt(trueORfalse);
	}
	
	public int getSDKVersion() {
		HashMap<String,Object> map = new HashMap();
		map.put("no", "key");
		
		Logger.getLogger(this.getClass()).info("[" + sn +"] getSDKVersion()");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("getSDKVersion", map));
		String sdk_string = (String)JsonUtil.toMap(result).get("command");
		Logger.getLogger(this.getClass()).info("[" + sn +"] getSDKVersion() result=" + sdk_string);
		return Integer.parseInt(sdk_string);
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
			picInDev = (String)JsonUtil.toMap(result).get("command");
		}
		
		String cpPicture = "adb -s " + sn + " pull " + picInDev + " " + path +"/" + fileName;
		String forward_response = AdbUtil.send(cpPicture, 30000);
		return path + "/" +fileName;
	}
	
	public String takeSnapshot(String path,String fileName)throws Exception{
		return this.takeSnapshot(path, fileName, 1f, 100);
	}
	
	public boolean click(int x,int y) {
		HashMap<String,Object> map = new HashMap();		
		map.put("x", x);
		map.put("y", y);

		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + x + "," + y +")");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("click", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + x + "," + y +") result=" + result);
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean click(String text, int instance) {
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		map.put("instance", instance);
		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + text + "," + instance +")");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("click", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + text + "," + instance +") result=" + result);
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean clickById(String id, int instance) {
		HashMap<String,Object> map = new HashMap();
		map.put("id", id);
		map.put("instance", instance);
		
		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + id + "," + instance +")");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("clickById", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + id + "," + instance +") result=" + result);
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean clickByClass(String object, int instance) {
		HashMap<String,Object> map = new HashMap();
		map.put("object", object);
		map.put("instance", instance);

		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + object + "," + instance +")");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("clickByClass", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] click(" + object + "," + instance +") result=" + result);
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean longClick(String text) {
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("longClick", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean clickAndWaitForNewWindow(String text, long timeout) {
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		map.put("timeout", timeout);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("clickAndWaitForNewWindow", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
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
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean scrollToBeginning(int maxSwipes) {
		HashMap<String,Object> map = new HashMap();
		map.put("maxSwipes", maxSwipes);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("scrollToBeginning", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean scrollToEnd(int maxSwipes) {
		HashMap<String,Object> map = new HashMap();
		map.put("maxSwipes", maxSwipes);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("scrollToEnd", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
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
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean textContains(String text) {
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		Logger.getLogger(this.getClass()).info("[" + sn +"] textContains(" + text + ")");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("textContains", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] textContains(" + text + ") result=" + result);
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean setTextByClass(String object, int instance, String text) {
		HashMap<String,Object> map = new HashMap();
		map.put("object", object);
		map.put("instance", instance);
		map.put("text", text);

		Logger.getLogger(this.getClass()).info("[" + sn +"] setTextByClass(" + object + "," + instance + "," + text + ")");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("setTextByClass", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] setTextByClass(" + object + "," + instance + "," + text + ") result=" + result);
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean setTextById(String id, int instance, String text) {
		HashMap<String,Object> map = new HashMap();
		map.put("id", id);
		map.put("instance", instance);
		map.put("text", text);

		Logger.getLogger(this.getClass()).info("[" + sn +"] setTextById(" + id + "," + instance + "," + text + ")");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("setTextById", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] setTextById(" + id + "," + instance + "," + text + ") result=" + result);
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	
	public boolean findElementById(String id) {
		HashMap<String,Object> map = new HashMap();
		map.put("id", id);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("findElementById", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	} 
	
	public boolean pressBack() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		Logger.getLogger(this.getClass()).info("[" + sn +"] pressBack()");
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressBack", map));
		Logger.getLogger(this.getClass()).info("[" + sn +"] pressBack() result=" + result);
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressDPadCenter() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadCenter", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressDPadDown() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadDown", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressDPadLeft() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadLeft", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressDPadRight() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadRight", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressDPadUp() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDPadUp", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressDelete() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressDelete", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressEnter() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressEnter", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressHome() {
		HashMap<String,Object> map = new HashMap();
		map.put("key", "no");

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressHome", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean pressKeyCode(int keyCode, int metaState) {
		HashMap<String,Object> map = new HashMap();
		map.put("keyCode", keyCode);
		map.put("metaState", metaState);

		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("pressKeyCode", map));
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
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
			
			String trueORfalse = (String)JsonUtil.toMap(result).get("command");
			
			bRet = Boolean.parseBoolean(trueORfalse);
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
		
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
	
	public boolean findAndClickByScrollable(String text){
		HashMap<String,Object> map = new HashMap();
		map.put("text", text);
		String result = 
				this.socket.sendMessageAndGetRespond(Request.getRequest("findAndClickByScrollable", map));
		String trueORfalse = (String)JsonUtil.toMap(result).get("command");
		return Boolean.parseBoolean(trueORfalse);
	}
}
