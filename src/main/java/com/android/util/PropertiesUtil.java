package com.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtil {
	    private static Properties load(String file){
	    	Properties properties = new Properties();
	        try {
	        	FileInputStream inputFile = new FileInputStream(file);
	        	properties.load(inputFile);
	            inputFile.close();
	        } catch (FileNotFoundException ex) {
	            System.out.println("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
	            ex.printStackTrace();
	        } catch (IOException ex) {
	            System.out.println("装载文件--->失败!");
	            ex.printStackTrace();
	        }
	        return properties;
	    }
	    
	    private static HashMap loadToMap(String file){
	    	HashMap tempMap = new HashMap();
	   		Properties prop = load(file);
	   		Set keys = prop.keySet();
			for (Iterator itr = keys.iterator(); itr.hasNext();) {
				String key = (String) itr.next();
				Object value = prop.get(key);
				tempMap.put(key, value);
			}
			return tempMap;
	    }
	    
	    public static void append(String file,String key,String value,String description){
	    	try { 		
	       		Properties properties = new Properties();
	    		
	       		properties.putAll(loadToMap(file));
	       		properties.setProperty(key, value);
	    		FileOutputStream outFile = new FileOutputStream(file);
	    		properties.store(outFile, description);
	    		outFile.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public static void append(String file,HashMap map,String description){
	    	try { 		
	       		Properties properties = new Properties();
	    		
	       		properties.putAll(loadToMap(file));
	       		properties.putAll(map);
	    		FileOutputStream outFile = new FileOutputStream(file);
	    		properties.store(outFile, description);
	    		outFile.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public static String getValue(String fileName, String key)
	    {
	        try {
	            String value = "";
	            Properties properties = new Properties();
	            FileInputStream inputFile = new FileInputStream(fileName);
	            properties.load(inputFile);
	            inputFile.close();
	            
	            if(properties.containsKey(key)){
	                value = properties.getProperty(key);
	                String resultName=new String(value.getBytes("ISO-8859-1"),"gbk");
	                return resultName;
	            }else
	                return value;
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	            return "";
	        } catch (IOException e) {
	            e.printStackTrace();
	            return "";
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            return "";
	        }
	    }//end getValue(...)
}
