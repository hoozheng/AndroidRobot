package com.android.util;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sf.json.JSONObject;

public class JsonUtil {
	
	public static final String  DEFAULT_DATE_PATTERN          = "yyyy-MM-dd HH:mm:ss";
	public static final String  EMPTY_JSON                    = "{}";
	public static final String  EMPTY_JSON_ARRAY              = "[]";
	public static boolean       EXCLUDE_FIELDS_WITHOUT_EXPOSE = false;
	
	/**
	 * json string to Map
	 * @param json -  {"command":"touch","params":{"y":234,"x":123}}
	 * @return
	 */
    public static Map<String,Object> toMap(String json) {
		Map<String,Object> map = new HashMap();
		JSONObject jsonObj = JSONObject.fromObject(json);
		
		map.put("command", jsonObj.getString("command"));
		return map;
    }
    
    /**
     * map to Json String
     * @param map
     * @return
     */
    public static String toJson(HashMap<String,Object> map) {
    	if(map != null)
    	{
    		JSONObject jsonObj = new JSONObject();
			HashMap<String,Object> params = (HashMap<String,Object>) map.get("params");
			JSONObject paramsJson = new JSONObject();
			paramsJson.putAll(params);
			jsonObj.put("params", paramsJson);
			jsonObj.put("command", map.get("command"));
			
			return jsonObj.toString();
    	}
    	return null;
    }
    
    public static String toJson(Object target) {
        return toJson(target, null, false, null, null, EXCLUDE_FIELDS_WITHOUT_EXPOSE);
    }
    
    public static String toJson(Object target, Type targetType, boolean isSerializeNulls,
            Double version, String datePattern,
            boolean excludesFieldsWithoutExpose) {
    	if (target == null) {
            return EMPTY_JSON;
        }

        GsonBuilder builder = new GsonBuilder();
        if (isSerializeNulls) {
            builder.serializeNulls();
        }

        if (version != null) {
            builder.setVersion(version.doubleValue());
        }

        if (isEmpty(datePattern)) {
            datePattern = DEFAULT_DATE_PATTERN;
        }

        builder.setDateFormat(datePattern);
        if (excludesFieldsWithoutExpose) {
            builder.excludeFieldsWithoutExposeAnnotation();
        }

        String result = "";

        Gson gson = builder.create();

        try {
            if (targetType != null) {
                result = gson.toJson(target, targetType);
            } else {
                result = gson.toJson(target);
            }
        } catch (Exception ex) {
        	System.out.println("目标对象 " + target.getClass().getName() + " 转换 JSON 字符串时，发生异常!" + ex.getMessage());
            if (target instanceof Collection || target instanceof Iterator
                || target instanceof Enumeration || target.getClass().isArray()) {
                result = EMPTY_JSON_ARRAY;
            } else {
                result = EMPTY_JSON;
            }

        }

        return result;
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, null);
    }
    
    public static <T> T fromJson(String json, Class<T> clazz, String datePattern) {
        if (isEmpty(json)) {
            return null;
        }

        GsonBuilder builder = new GsonBuilder();
        if (isEmpty(datePattern)) {
            datePattern = DEFAULT_DATE_PATTERN;
        }

        Gson gson = builder.create();

        try {
            return gson.fromJson(json, clazz);
        } catch (Exception ex) {
            System.out.println(json + " 无法转换为 " + clazz.getName() + " 对象!" + ex.getMessage());
            return null;
        }
    }

    private static boolean isEmpty(String json) {
        return json == null || json.trim().length() == 0;
    }
}
