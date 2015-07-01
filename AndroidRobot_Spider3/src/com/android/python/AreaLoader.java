package com.android.python;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.android.util.XmlUtil;

public class AreaLoader {
	public synchronized static Area load(String file, String element) throws Exception{
		Area area = new Area();
		XmlUtil xmlUtil = new XmlUtil(file);
		Document doc = xmlUtil.parse(file);
		Element root = 
				doc.getDocumentElement();
		NodeList nodeList = root.getChildNodes();
		if(nodeList != null){
        	for(int i=0; i<nodeList.getLength(); i++){
        		Node project = nodeList.item(i);
        		if(project.getNodeType() == Node.ELEMENT_NODE){
        			NamedNodeMap map = project.getAttributes();
        			String value = map.getNamedItem("file").getNodeValue();
        			if(value.equals(element)){
        				area.setFile(element);
        				area.setX(Integer.parseInt(map.getNamedItem("x").getNodeValue()));
        				area.setY(Integer.parseInt(map.getNamedItem("y").getNodeValue()));
        				area.setWidth(Integer.parseInt(map.getNamedItem("width").getNodeValue()));
        				area.setHeight(Integer.parseInt(map.getNamedItem("height").getNodeValue()));
        				System.out.println("abc:" + area.getFile() + " " + area.getX() + " " + area.getY());
        				break;
        			}
        		}
        	}
		}
		return area;
	}
}
