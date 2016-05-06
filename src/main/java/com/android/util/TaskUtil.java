package com.android.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.android.tasks.Task;
import com.android.tasks.TestCase;

public class TaskUtil {

	public static Vector<String> loadScriptsInFolder(String path){
		File fileScript = new File(path);
		Vector<String> vec = new Vector();
		File files[] = fileScript.listFiles();
		for(int i=0;i<files.length;i++){
			if(!files[i].isDirectory())
				vec.add(files[i].getAbsolutePath());
		}
		return vec;
	}
	
	public static Vector<String> loadScriptsFolder(String path){
		File fileScript = new File(path);
		Vector<String> vec = new Vector();
		File files[] = fileScript.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isDirectory())
				vec.add(files[i].getName());
		}
		return vec;
	}
	
	public static void getScripts(File project,Vector<String> scripts) throws IOException{
		if(project != null && project.isFile()){
			String canonicalPath = project.getCanonicalPath();
			int index = canonicalPath.lastIndexOf("/workspace/");
			String relativePath = "."+canonicalPath.substring(index,canonicalPath.length());
			//scripts.add(project.getCanonicalPath());
			scripts.add(relativePath);
		}else if(project != null && project.isDirectory()){
			File[] files= project.listFiles();
			for(int i=0;i<files.length;i++){
				getScripts(files[i],scripts);
			}
		}
	}
	
	public static void updateTask(String xmlTasks, ArrayList<Task> listTasks) throws Exception {
		
	}
	
	public static void removeTask(String xmlTasks, String name) throws Exception {
		XmlUtil xml = new XmlUtil(xmlTasks);
		Document doc = xml.parse(xmlTasks);
		Element root = doc.getDocumentElement();
		NodeList childs = root.getChildNodes();
		if(childs != null){
        	for(int i=0; i<childs.getLength(); i++){
        		Node project = childs.item(i);
        		if(project.getNodeType() == Node.ELEMENT_NODE){
        			NamedNodeMap map = project.getAttributes();
        			String taskName = map.getNamedItem("name").getNodeValue();
        			if(taskName.equals(name)) {
        				root.removeChild(project);
        				break;
        			}
        		}
        	}
        	
            xml.flush(doc);
		}
	}
	
	public static void updateTask(String xmlTasks, Task task) throws Exception {
		XmlUtil xml = new XmlUtil(xmlTasks);
		Document doc = xml.parse(xmlTasks);
		Element root = doc.getDocumentElement();
		NodeList childs = root.getChildNodes();
		if(childs != null){
        	for(int i=0; i<childs.getLength(); i++){
        		Node project = childs.item(i);
        		if(project.getNodeType() == Node.ELEMENT_NODE){
        			NamedNodeMap map = project.getAttributes();
        			String taskName = map.getNamedItem("name").getNodeValue();
        			if(taskName.equals(task.name)) {
        				root.removeChild(project);
        				break;
        			}
        		}
        	}
        	
        	//add taskNode
            Hashtable<String,String> attri = new Hashtable();
            attri.put("name", task.name);
            attri.put("loop", String.valueOf(task.loop));
            attri.put("solution", task.solution);
            attri.put("project", task.project);
            attri.put("item", task.item);
            Node taskNode = xml.appendNode(root.getOwnerDocument(), "task", "", attri);
            
            for(int i=0;i<task.vecTC.size();i++) {
            	TestCase testcase = task.vecTC.get(i);
	            Hashtable<String,String> caseAttri = new Hashtable();
	            caseAttri.put("name", testcase.name);
	            caseAttri.put("loop", String.valueOf(testcase.loop));
	            caseAttri.put("path", testcase.path);
	            caseAttri.put("unit", testcase.unit);
	            caseAttri.put("individual", String.valueOf(testcase.individual));
	            caseAttri.put("isChecked", String.valueOf(testcase.isChecked));
	            //add testcase
	            Node caseNode = xml.appendNode(root.getOwnerDocument(), "case", "", caseAttri);
	            taskNode.appendChild(caseNode);
            }
            xml.flush(doc);
        	
		}
	}
	
	public static Vector<Task> getCheckedTasks(String path, ArrayList<String> listTasks){
		Vector<Task> vecTasks = new Vector();
		for(int i=0;i<listTasks.size();i++){
			String task = listTasks.get(i);
			try {
				vecTasks.add(loadCheckedTask(path, task));
			} catch (Exception e) {
				continue;
			}
		}
		return vecTasks;
	}
	
	private static Task loadCheckedTask(String path,String strTask) throws Exception{
		Task task = new Task();
		List<Task> tasksList = loadTaskForRun(path);
		for(int i=0;i<tasksList.size();i++){
			Task tempTask = tasksList.get(i);
			if(tempTask.name.equals(strTask)){
				Vector<TestCase> tcVector = new Vector();
				for(int j=0;j<tempTask.vecTC.size();j++){
					if(tempTask.vecTC.get(j).isChecked == true){
						tcVector.add(tempTask.vecTC.get(j));
					}
				}
				task.name = tempTask.name;
				task.loop = tempTask.loop;
				task.item = tempTask.item;
				task.project = tempTask.project;
				task.solution = tempTask.solution;
				task.vecTC = tcVector;
				break;
			}
		}
		return task;
	}
	
	public static ArrayList<Task> loadTask(String xmlFile) throws Exception {
		ArrayList<Task> tasksList = new ArrayList<Task>();
		XmlUtil xml = new XmlUtil(xmlFile);
		Document doc = xml.parse(xmlFile);
		Element root = doc.getDocumentElement();
		NodeList childs = root.getChildNodes();
		String projectPath = new File(xmlFile).getParent();
		if(childs != null){
        	for(int i=0; i<childs.getLength(); i++){
        		Node project = childs.item(i);
        		if(project.getNodeType() == Node.ELEMENT_NODE){
        			NamedNodeMap map = project.getAttributes();
        			Task task = new Task();
        			task.name = map.getNamedItem("name").getNodeValue();
        			task.project = map.getNamedItem("project").getNodeValue();
        			task.solution = map.getNamedItem("solution").getNodeValue();
        			task.loop = Integer.parseInt(map.getNamedItem("loop").getNodeValue());
        			task.item = map.getNamedItem("item").getNodeValue();
        			task.vecTC = new Vector<TestCase> ();
        			
        			NodeList caseNodes = project.getChildNodes();
        			for(int j=0;j<caseNodes.getLength();j++) {
        				Node caseNode = caseNodes.item(j);
        				if(caseNode.getNodeType() == Node.ELEMENT_NODE) {
        					NamedNodeMap caseNodeMap = caseNode.getAttributes();
        					TestCase tc = new TestCase();
        					tc.name = caseNodeMap.getNamedItem("name").getNodeValue();
        					tc.path = caseNodeMap.getNamedItem("path").getNodeValue();
        					tc.unit = caseNodeMap.getNamedItem("unit").getNodeValue();
        					tc.loop = 0;
        					try {
        						tc.loop = Integer.parseInt(caseNodeMap.getNamedItem("loop").getNodeValue());
        					}catch(Exception ex) {
        						tc.loop = 0;
        					}
        					tc.individual = 1;
        					tc.isChecked = false;
        					try {
        						tc.isChecked = Boolean.parseBoolean(caseNodeMap.getNamedItem("isChecked").getNodeValue());
        					}catch(Exception ex) {
        						tc.isChecked = false;
        					}
        					task.vecTC.add(tc);
        				}
        			}
        			tasksList.add(task);
        		}
        	}
		}
		
		return tasksList;
	}

	public static ArrayList<Task> loadTaskForRun(String xmlFile) throws Exception {
		ArrayList<Task> tasksList = new ArrayList<Task>();
		XmlUtil xml = new XmlUtil(xmlFile);
		Document doc = xml.parse(xmlFile);
		Element root = doc.getDocumentElement();
		NodeList childs = root.getChildNodes();
		String projectPath = new File(xmlFile).getParent();
		if(childs != null){
        	for(int i=0; i<childs.getLength(); i++){
        		Node project = childs.item(i);
        		if(project.getNodeType() == Node.ELEMENT_NODE){
        			NamedNodeMap map = project.getAttributes();
        			Task task = new Task();
        			task.name = map.getNamedItem("name").getNodeValue();
        			task.project = map.getNamedItem("project").getNodeValue();
        			task.solution = map.getNamedItem("solution").getNodeValue();
        			task.loop = Integer.parseInt(map.getNamedItem("loop").getNodeValue());
        			task.item = map.getNamedItem("item").getNodeValue();
        			task.vecTC = new Vector<TestCase> ();
        			
        			NodeList caseNodes = project.getChildNodes();
        			for(int j=0;j<caseNodes.getLength();j++) {
        				Node caseNode = caseNodes.item(j);
        				if(caseNode.getNodeType() == Node.ELEMENT_NODE) {
        					NamedNodeMap caseNodeMap = caseNode.getAttributes();
        					TestCase tc = new TestCase();
        					tc.name = caseNodeMap.getNamedItem("name").getNodeValue();
        					tc.path = projectPath + caseNodeMap.getNamedItem("path").getNodeValue();
        					tc.unit = caseNodeMap.getNamedItem("unit").getNodeValue();
        					tc.loop = 0;
        					try {
        						tc.loop = Integer.parseInt(caseNodeMap.getNamedItem("loop").getNodeValue());
        					}catch(Exception ex) {
        						tc.loop = 0;
        					}
        					tc.individual = 1;
        					tc.isChecked = false;
        					try {
        						tc.isChecked = Boolean.parseBoolean(caseNodeMap.getNamedItem("isChecked").getNodeValue());
        					}catch(Exception ex) {
        						tc.isChecked = false;
        					}
        					task.vecTC.add(tc);
        				}
        			}
        			tasksList.add(task);
        		}
        	}
		}
		
		return tasksList;
	}
}
