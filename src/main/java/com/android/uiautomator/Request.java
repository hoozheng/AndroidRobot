package com.android.uiautomator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.util.JsonUtil;

public class Request {
//	public static String getRequest(String command, HashMap<String,Object> params){
//		HashMap<String,Object> map = new HashMap();
//		map.put("command", command);
//		if(null == params || params.size() == 0){
//			Map tempMap = new HashMap<String,Object>();
//			tempMap.put("key", "no");
//			map.put("params", tempMap);
//		}else 
//			map.put("params", params);
//		
//		return JsonUtil.toJson(map);
//	}
	public static String getRequest(String command, HashMap<String,Object> params){
       	ArrayList<HashMap<String,Object>> paramsList = new ArrayList();
       	paramsList.add(params);
       	
       	HashMap<String,Object> cmdMap = new HashMap();
       	cmdMap.put("command", command);
       	cmdMap.put("params", paramsList);
       	String json = JsonUtil.toJson(cmdMap);
       	return json;
	}
	

}
