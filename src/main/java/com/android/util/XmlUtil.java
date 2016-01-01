package com.android.util;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {
	private String path = "";
	
	public XmlUtil(String path){
		this.path = path;
	}
	
	public String getAttribute( Element element, String attributeName ) {
		return element.getAttribute( attributeName );
	}
	
	public String getText( Element element ) {
		return element.getFirstChild().getNodeValue();
	}
	
	public Document parse( String xmlFile ) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document domTree = db.parse( xmlFile );
		return domTree;
	}
	
	public Element getChildElement( Element parentElement, String childName, String attributeName, String attributeValue ) throws Exception {
		NodeList list = parentElement.getElementsByTagName( childName );
		int count = 0;
		Element curElement = null;
		for ( int i = 0 ; i < list.getLength() ; i ++ ) {
			Element child = ( Element )list.item( i );
			String value = child.getAttribute( attributeName );
			if ( true == value.equals( attributeValue ) ) {
				curElement = child;
				count ++;
			}
		}
		if ( 0 == count ) {
			throw new Exception( "找不到个符合条件的子节点！" );
		} else if ( 1 < count ) {
			throw new Exception( "找到多个符合条件的子节点！" );
		}
		
		return curElement;
	}
	
	public Element getChildElement( Element parentElement, String childName ) throws Exception {
		NodeList list = parentElement.getElementsByTagName( childName );
		Element curElement = null;
		if ( 1 == list.getLength()  ) {
			curElement = ( Element )list.item( 0 );
		} else if ( 0 == list.getLength() ) {
			throw new Exception( "找不到个符合条件的子节点！" );
		} else {
			throw new Exception( "找到多个符合条件的子节点！" );
		}
		return curElement;
	}
	
	public void flush(Document node){
		try{
			File file = new File(path);
			
			TransformerFactory tr=TransformerFactory.newInstance();
	        Transformer t=tr.newTransformer();
	        DOMSource sourse=new DOMSource(node);
	        StreamResult result=new StreamResult(file);
	        t.transform(sourse, result);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Node appendNode(Document node,String name,String content,Hashtable<String,String> attri){
		Element element = node.createElement(name);
		for(Iterator itrName=attri.keySet().iterator();itrName.hasNext();){
			String attriKey = (String)itrName.next();
			String attriValue = (String)attri.get(attriKey);
			element.setAttribute(attriKey, attriValue);
    	}
		
		return node.getDocumentElement().appendChild(element);
	}
}

