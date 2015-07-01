package com.android.util;

import java.util.Hashtable;
import java.util.Vector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.android.ddmlib.IDevice;

public class RobotTreeUtil {
	public static TreeItem getNodeByName(Tree tree,String prj){
		if(tree == null)
			return null;
		
		TreeItem[] tiRoot = tree.getItems();
		for(int i = 0;i<tiRoot.length;i++){
			if(tiRoot[i].getText().equals(prj)){
				return tiRoot[i];
			}
		}
		return null;
	}
	
	public static TreeItem getNodeByName(TreeItem tree,String prj){
		if(tree == null)
			return null;
		
		TreeItem[] tiRoot = tree.getItems();
		for(int i = 0;i<tiRoot.length;i++){
			if(tiRoot[i].getText().equals(prj)){
				return tiRoot[i];
			}
		}
		return null;
	}
	
	public static boolean isChildContained(TreeItem root,String childName){
		if(root == null)
			return false;
		
		TreeItem[] tiRoot = root.getItems();
		for(int i = 0;i<tiRoot.length;i++){
			if(tiRoot[i].getData("sn").equals(childName)){
				return true;
			}
		}
		return false;
	}
	
	public static void initDevices(TreeItem root,Display display,IDevice devices[]){
		TreeItem tiDevices = getNodeByName(root,"Devices");
		
		if(devices == null)
			return;
		
		for(int j=0;j<tiDevices.getItemCount();j++){
			for(int i=0;i<devices.length;i++){
				if(tiDevices.getItem(j).getData("sn").equals(devices[i].getSerialNumber())){
					tiDevices.getItem(j).setImage(new Image(display, ".\\icons\\devices.png"));
					tiDevices.getItem(j).setData("device",devices[i]);
					break;
				}
			}
		}
	}
	
	public static Vector<IDevice> getDevicesByProject(TreeItem tiProject){
		Vector<IDevice> vecDevices = new Vector();
		TreeItem tiDevices = getNodeByName(tiProject,"Devices");
		for(int j=0;j<tiDevices.getItemCount();j++){
			IDevice device = 
					(IDevice)tiDevices.getItem(j).getData("device");
			if(null != device)
				vecDevices.add(device);
		}
		return vecDevices;
	}
	
	public static String getPathFromTree(TreeItem treeItem){
		String path = treeItem.getText();
		TreeItem root = treeItem.getParentItem();
		while(root != null){
			path = root.getText()+"/"+path;
			root = root.getParentItem();
		}
		return path;
	}
	
	public static Vector<TreeItem> getSelectElements(Tree tree){
		Vector<TreeItem> vecTreeItem = new Vector(); 
		TreeItem[] tiRoot = tree.getItems();
		for(int i = 0;i<tiRoot.length;i++){
			getSelection(vecTreeItem,tiRoot[i]);
		}
		return vecTreeItem;
	}
	
	private static void getSelection(Vector<TreeItem> vecTreeItem,TreeItem tiNode){
		
		if(tiNode.getChecked() == true && tiNode.getGrayed() == false){
			vecTreeItem.add(tiNode);
		}else if(tiNode.getChecked() == true && tiNode.getGrayed() == true){
			TreeItem[] tiRoot = tiNode.getItems();
			for(int i=0;i<tiRoot.length;i++){
				getSelection(vecTreeItem,tiRoot[i]);
			}
		}
	}
	

	/*
	 * add Tree
	 */
	public static TreeItem addTree(Display display,Tree parent,String name){
		TreeItem column = null;
		if(parent != null){
			column = new TreeItem(parent, SWT.NONE);
			column.setText(name);
			column.setImage(new Image(display, ".\\icons\\project.png"));
		}
		return column;
	}
	
	public static TreeItem addTreeItem(Display display,TreeItem parent,String name){
		TreeItem column = null;
		if(parent != null){
			column = new TreeItem(parent, SWT.NONE);
			column.setText(name);
			if(name.endsWith(".dat"))
				column.setImage(new Image(display, ".\\icons\\unknow.png"));
			else if(name.endsWith("Devices"))
				column.setImage(new Image(display, ".\\icons\\devices.png"));
			else if(parent.getText().equals("Devices")){
				column.setImage(new Image(display, ".\\icons\\devices.png"));
			}else
				column.setImage(new Image(display, ".\\icons\\folder.png"));
		}
		return column;
	}
	
	public static TreeItem addHandsetTreeItem(Display display,TreeItem parent,String name,IDevice tempDevice,Hashtable<String,String> attri){
		TreeItem column = null;
		if(parent != null){
			column = new TreeItem(parent, SWT.NONE);
			column.setText(name);
			column.setImage(new Image(display, ".\\icons\\devices.png"));
			
			column.setData("device",tempDevice);
			column.setData("index",parent.getItems().length-1);
			column.setData("sn",attri.get("sn"));
			column.setData("pixel",attri.get("pixel"));
			
			//System.out.println("index="+parent.getItems().length+" sn:"+attri.get("sn")+" pixel:"+attri.get("pixel"));
		}
		return column;
	}
	
	public static Vector<String> getSelections(Tree tree,String prj){
		Vector<String> vecFiles = new Vector<String>();
		TreeItem[] tiRoot = tree.getItems();
		for(int i = 0;i<tiRoot.length;i++){
			//System.out.println("Tree Item:"+tiRoot[i].getText());
			if(tiRoot[i].getText().equals(prj))
				if(tiRoot[i].getChecked() == true){
					//System.out.println("Checked:"+tiRoot[i].getChecked());
					//Scripts
					TreeItem tiScript = tiRoot[i].getItems()[2];
					TreeItem[] tiCases = tiScript.getItems();
					for(int j =0;j<tiCases.length;j++)
						if(tiCases[j].getChecked()){
							String project = tiRoot[i].getText();
							//System.out.println("Checked:"+tiCases[j].getText());
							vecFiles.add(".\\workspace\\"+tiRoot[i].getText()+"\\Scripts\\"+tiCases[j].getText());
						}
				}else
					break;
			else
				continue;
		}
		return vecFiles;
	}
	
	public static TreeItem getRoot(TreeItem node){
		if(node == null)
			return null;
		TreeItem root = node.getParentItem();
		if(root == null)
			return node;
		
		while(root.getParentItem() != null){
			
			root = root.getParentItem();
		}
		return root;
		
	}
	
	public static boolean findScriptsNode(TreeItem node){
		if(node == null)
			return false;
		if(node.getText().trim().equals("Scripts")){
			TreeItem root = node.getParentItem();
			if(root.getParentItem() == null)
				return true;
			else
				findScriptsNode(node);
		}else{
			findScriptsNode(node.getParentItem());
		}
		return false;
	}
}
