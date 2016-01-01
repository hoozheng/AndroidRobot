package com.android.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.python.apache.xml.serialize.OutputFormat;
import org.python.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProjectUtil {
	private Document doc = null;
	
	public ProjectUtil(){
		try{
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.newDocument();
    	}catch(Exception pce){
    		System.err.println(pce);
    		System.exit(1);
    	}
	}
	
	public static Vector<String> getHandsets(String file)throws Exception{
		Vector<String> vec = new Vector();
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder=domfac.newDocumentBuilder();
        FileInputStream is=new FileInputStream(file);
        
        Document doc=dombuilder.parse(is);
        NodeList nodeList = doc.getElementsByTagName("devices");
        if(nodeList != null && nodeList.getLength()>=1){
        	Node deviceNode = nodeList.item(0);
        	NodeList children = deviceNode.getChildNodes();
        	if(children != null && children.getLength()>=1){
        		for(int i=0;i<children.getLength();i++){
        			vec.add(children.item(i).getTextContent());
        		}
        	}
        }
        return vec;
	}
	
	public static void addHandset(String file,String name,Hashtable<String,String> attri) throws Exception{
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder=domfac.newDocumentBuilder();
        FileInputStream is=new FileInputStream(file);
        
        Document doc=dombuilder.parse(is);
        NodeList nodeList = doc.getElementsByTagName("devices");
        if(nodeList != null && nodeList.getLength()>=1){
        	Node deviceNode = nodeList.item(0);
        	Element device = doc.createElement("device"); 
        	device.setTextContent(name);
        	for(Iterator itrName=attri.keySet().iterator();itrName.hasNext();){
    			String attriKey = (String)itrName.next();
    			String attriValue = (String)attri.get(attriKey);
    			device.setAttribute(attriKey, attriValue);
        	}
        	deviceNode.appendChild(device);
        }
       
        //save
        TransformerFactory tf=TransformerFactory.newInstance();
        Transformer t=tf.newTransformer();
        Properties props=t.getOutputProperties();
        props.setProperty(OutputKeys.ENCODING, "GB2312");
        t.setOutputProperties(props);
        DOMSource dom=new DOMSource(doc);
        StreamResult sr=new StreamResult(file);
        t.transform(dom, sr);
	}
	
	public static void addApp(String file,String name,Hashtable<String,String> attri) throws Exception{
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder=domfac.newDocumentBuilder();
        FileInputStream is=new FileInputStream(file);
        
        Document doc=dombuilder.parse(is);
        
        NodeList nodeList = doc.getElementsByTagName("app");
        if(nodeList != null && nodeList.getLength()>=1){
        	Node deviceNode = nodeList.item(0);
        	Element device = doc.createElement("aut"); 
        	device.setTextContent(name);
        	for(Iterator itrName=attri.keySet().iterator();itrName.hasNext();){
    			String attriKey = (String)itrName.next();
    			String attriValue = (String)attri.get(attriKey);
    			device.setAttribute(attriKey, attriValue);
        	}
        	deviceNode.appendChild(device);
        }
       
        //save
        TransformerFactory tf=TransformerFactory.newInstance();
        Transformer t=tf.newTransformer();
        Properties props=t.getOutputProperties();
        props.setProperty(OutputKeys.ENCODING, "GB2312");
        t.setOutputProperties(props);
        DOMSource dom=new DOMSource(doc);
        StreamResult sr=new StreamResult(file);
        t.transform(dom, sr);
	}
	
	public static void removeHandset(String file,String name)throws Exception{
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder=domfac.newDocumentBuilder();
        FileInputStream is=new FileInputStream(file);
        
        Document doc=dombuilder.parse(is);
        NodeList devices = doc.getElementsByTagName("devices");
        NodeList nodeList = doc.getElementsByTagName("device");
        for(int i=0;i<nodeList.getLength();i++){
        	Node deviceNode = nodeList.item(i);
        	if(deviceNode.getTextContent().equals(name)){
        		devices.item(0).removeChild(deviceNode);
        	}
        }
       
        //save
        TransformerFactory tf=TransformerFactory.newInstance();
        Transformer t=tf.newTransformer();
        Properties props=t.getOutputProperties();
        props.setProperty(OutputKeys.ENCODING, "GB2312");
        t.setOutputProperties(props);
        DOMSource dom=new DOMSource(doc);
        StreamResult sr=new StreamResult(file);
        t.transform(dom, sr);
	}
	
	public static String createProject(String path,String name){
		ProjectUtil dom = new ProjectUtil();
		Element root = dom.createNode("projectDescription");
		Element handset_1 = dom.createNode(root,"project");
		dom.createNode(handset_1,"name",name);
		
		dom.createNode(root,"devices");
		String projectPath = path+"\\..\\"+name+".androidrobot";
		dom.saveToFile(projectPath);
		return projectPath;
	}
	
	//�ĳɷŶ���
	//IDeviceObject
	public static TreeItem importProject(String path,Tree tree,Display display) throws Exception{
		String project = readProjectName(path);
		Vector <IDeviceObject>vecDevices = readDevices(path);
		
		TreeItem root = new TreeItem(tree, SWT.NONE);
		root.setText(project);
		root.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/project.png")));
		
		TreeItem devices = new TreeItem(root, SWT.NONE);
		devices.setText("Devices");
		devices.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/devices.png")));
		
		for(int i =0;i<vecDevices.size();i++){
			TreeItem column = new TreeItem(devices, SWT.NONE);
			column.setText(vecDevices.get(i).name);
			column.setData("index",vecDevices.get(i).index);
			column.setData("sn",vecDevices.get(i).sn);
			column.setData("pixel",vecDevices.get(i).pixel);
			column.setData("name",vecDevices.get(i).name);
			column.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/disconn.png")));
		}
		return root;
	}
	
	
	public static String getCurrentProject() {
		try {
            String value = "";
            Properties properties = new Properties();
            FileInputStream inputFile = new FileInputStream(System.getProperty("user.dir") + "/system.properties");
            properties.load(inputFile);
            inputFile.close();
            
            if(properties.containsKey("ProjectPath")){
                value = properties.getProperty("ProjectPath");
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
	}
	
	public Element createNode(String name){
		String temp = name.replaceAll(" "," ");
		Element root=doc.createElement(temp); 
    	doc.appendChild(root);
    	return root;
	}
	
	public Element createNode(Element root,String name){
		String temp = name.replaceAll(" "," ");
		Element node = doc.createElement(temp); 
		root.appendChild(node);
    	return node;
	}
	
	public Element createNode(Element root,String name,String value){
		String temp = name.replaceAll(" "," ");
		Element node = doc.createElement(temp);
		node.appendChild(doc.createTextNode(value)); 
		root.appendChild(node);
    	return node;
	}
	
	
	
	public Element createNode(Element root,Hashtable<String,String>ht,String name,String value){
		String temp = name.replaceAll(" "," ");
		//System.out.println(temp);
		Element node = doc.createElement(temp);
		node.appendChild(doc.createTextNode(value)); 
		for (Iterator it=ht.entrySet().iterator();it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			node.setAttribute(entry.getKey().toString(),entry.getValue().toString());
		}  
		root.appendChild(node);
    	return node;
	}
	
	public void  saveToFile(String output){
		FileOutputStream os=null;
		try{
			OutputFormat outformat=new OutputFormat(doc);
			os=new FileOutputStream(output);
			XMLSerializer xmlSerilizer=new XMLSerializer(os,outformat);
			xmlSerilizer.serialize(doc);
	    }catch(Exception e){
	    	System.out.println("create xml failed...\n");
	    }
		
		FileOutputStream outStream=null;
		try{
			outStream=new FileOutputStream(output);
		}catch(Exception e){
			System.err.println(e);
			System.exit(1);
		}
		OutputStreamWriter outWriter=new OutputStreamWriter(outStream);
		WriteXMLFile(doc,outWriter,"GB2312");
		
		try{
			outWriter.close();
			outStream.close();
		}catch(Exception e){
			System.err.println(e);
			System.exit(1);	
		} 
	}
	
	private static void WriteXMLFile(Document doc,OutputStreamWriter w,String encoding)
	{
		try{
			Source source=new DOMSource(doc);
			Result ret=new StreamResult(w);
			Transformer xformer=TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.ENCODING,encoding);
			xformer.transform(source,ret);
		}catch(TransformerConfigurationException e){
			e.printStackTrace();
		}catch(TransformerException e){
			e.printStackTrace();
			
		}
	    
	}
	
	public static String getPixelByDevice(String file,String device)throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder=domfac.newDocumentBuilder();
        InputStream is=new FileInputStream(file);            
        Document doc=dombuilder.parse(is);
        Element root=doc.getDocumentElement();
        NodeList prjInfo=root.getChildNodes();
        if(prjInfo!=null){
        	for(int i=0;i<prjInfo.getLength();i++){
        		Node project = prjInfo.item(i);
        		if(project.getNodeType()==Node.ELEMENT_NODE){
        			String strProject = project.getNodeName();
        			if(strProject.equals("devices")){
        				for(Node node=project.getFirstChild();node!=null;node=node.getNextSibling()){
                            if(node.getNodeType()==Node.ELEMENT_NODE){
                            	if(node.getTextContent().equals(device)){
                            		System.out.println(node.getAttributes().getLength());
                            	}
                            }
        				}
        			}
        		}
    			
        	}
        }
        
        return "";
	}
	public static Vector<IDeviceObject> readDevices(String file) throws ParserConfigurationException, SAXException, IOException{
		Vector<IDeviceObject> vec = new Vector();
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder=domfac.newDocumentBuilder();
        InputStream is=new FileInputStream(file);            
        Document doc=dombuilder.parse(is);
        Element root=doc.getDocumentElement();
        NodeList prjInfo=root.getChildNodes();
        if(prjInfo!=null){
        	for(int i=0;i<prjInfo.getLength();i++){
        		Node project = prjInfo.item(i);
        		if(project.getNodeType()==Node.ELEMENT_NODE){
        			String strProject = project.getNodeName();
        			if(strProject.equals("devices")){
        				int index = 0;
        				for(Node node=project.getFirstChild();node!=null;node=node.getNextSibling()){
                            if(node.getNodeType()==Node.ELEMENT_NODE){
                            	IDeviceObject device = new IDeviceObject();
                            	device.index = index++;
                            	device.name = node.getTextContent();
                            	
                            	NamedNodeMap attributes = 
                            			node.getAttributes();

                            	for(int j=0;j<attributes.getLength();j++){
                            		Node attribute = attributes.item(j);
                            		if(attribute.getNodeName().equals("sn")){
                            			device.sn = attribute.getNodeValue();
                            		}else if(attribute.getNodeName().equals("pixel")){
                            			device.pixel = attribute.getNodeValue();
                            		}
                            	}
                            	vec.add(device);
                            }
        				}
        			}
        		}
    			
        	}
        }
        return vec;
	}
	public static String readProjectName(String file) throws Exception{
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder=domfac.newDocumentBuilder();
        InputStream is=new FileInputStream(file);            
        Document doc=dombuilder.parse(is);
        Element root=doc.getDocumentElement();
        NodeList prjInfo=root.getChildNodes();
        if(prjInfo!=null){
        	for(int i=0;i<prjInfo.getLength();i++){
        		Node project = prjInfo.item(i);
        		if(project.getNodeType()==Node.ELEMENT_NODE){
        			String strProject = project.getNodeName();
        			if(strProject.equals("project")){
        				for(Node node=project.getFirstChild();node!=null;node=node.getNextSibling()){
                            if(node.getNodeType()==Node.ELEMENT_NODE){
                            	String strNodeName = node.getNodeName();
                            	if(strNodeName.equals("name")){
                            		return node.getTextContent();
                            	}
                            }
        				}
        			}
        		}
    			
        	}
        }
        return "";
	}
	
    public void writeXML(String file,String serialNumber){
    	DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
    	DocumentBuilder db=null;
    	try{
    		db=dbf.newDocumentBuilder();
    	}catch(Exception pce){
    		System.err.println(pce);
    		System.exit(1);
    	}
    	Document doc=db.newDocument();
    	
    	Element root=doc.createElement(serialNumber); 
    	doc.appendChild(root);
    	
    }
 
}

class IDeviceObject {
	public int index = 0;
	public String name = "";
	public String pixel = "";
	public String sn = "";
}

