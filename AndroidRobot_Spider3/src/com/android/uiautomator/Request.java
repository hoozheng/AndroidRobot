package com.android.uiautomator;

import java.util.HashMap;
import java.util.Map;

import com.android.util.JsonUtil;

public class Request {
	public static String getRequest(String command, HashMap<String,Object> params){
		HashMap<String,Object> map = new HashMap();
		map.put("command", command);
		if(null == params || params.size() == 0){
			Map tempMap = new HashMap<String,Object>();
			tempMap.put("key", "no");
			map.put("params", tempMap);
		}else 
			map.put("params", params);
		
		return JsonUtil.toJson(map);
	}
	
	

}
