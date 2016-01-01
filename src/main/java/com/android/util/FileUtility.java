package com.android.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class FileUtility {
	
	public static boolean createFile(String name){
		File outfile = new File(name);
		BufferedWriter writer = null;
		if(!outfile.isFile()){
			try {
				outfile.createNewFile();
				writer = new BufferedWriter(new FileWriter(outfile));
				writer.write("#_*_ coding: iso8859_1\n# Script API\n\nfrom com.android.python import AndroidDriver\n\n"); 
				writer.flush();
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static void writeFile(String path,String content,boolean append) throws FileNotFoundException {
		//File file = new File(path);
		FileOutputStream writerStream = new FileOutputStream(path, append);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(writerStream,"UTF-8"));
			writer.write(content); 
			writer.flush();
			writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                	writer.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	
	public static void saveAllScripts(String projectName,
										String fileName,
										String content) throws FileNotFoundException{
		fileName = fileName.substring(1,fileName.length());
		String path = System.getProperty("user.dir") + "/workspace/"+projectName.substring(0,projectName.indexOf(System.getProperty("file.separator")))+"/Scripts/"+fileName;
		writeFile(path,content,false);
	}
	

	public static void readFileByLines(String fileName,StyledText tabItem) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
        	InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            reader = new BufferedReader(isr);
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
            	tabItem.append(tempString+"\n");
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
	
	public static void getScripts(File project,Vector<String> scripts) throws IOException{
		if(project != null && project.isFile()){
			String canonicalPath = project.getCanonicalPath();
			int index = canonicalPath.lastIndexOf(System.getProperty("file.separator") + "workspace" + System.getProperty("file.separator"));
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

	
	public static void loadOthersByProject(TreeItem root,Display display){
		File fileScript = new File(System.getProperty("user.dir") + 
				"/workspace/"+root.getText());
		File[] fileScripts = fileScript.listFiles();
		for(int i=0;i<fileScripts.length;i++){
			if(fileScripts[i] != null && !fileScripts[i].isDirectory()){
				TreeItem folder = new TreeItem(root, SWT.NONE);
				folder.setText(fileScripts[i].getName());
				folder.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/unknow.png")));
			}
		}
		
	}
	
	public static void loadLogsByProject(TreeItem root,Display display,String path){
		File fileScript = new File(path+"/Logs");
		
		if(fileScript != null && fileScript.isDirectory()){
			//System.out.println(fileScript.getName());
			listFile(fileScript,root,display);
		}
	}
	
	public static void listFile(File f,TreeItem root,Display display) {
		if (f.isDirectory()) {
			//add node in tree
			TreeItem folder = new TreeItem(root, SWT.NONE);
			folder.setText(f.getName());
			folder.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/folder.png")));
			File[] t = f.listFiles();
			for (int i = 0; i < t.length; i++){
				listFile(t[i],folder,display); 
			} 
		}else{
			TreeItem folder = new TreeItem(root, SWT.NONE);
			folder.setText(f.getName());
			if(f.getName().endsWith(".py"))
				folder.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/script.png")));
			else if(f.getName().endsWith(".png"))
				folder.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/picture.png")));
			else if(f.getName().endsWith(".arlog"))
				folder.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/log.png")));
			else
				folder.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/unknow.png")));
		}
	}
	
	public static void loadPicturesByProject(TreeItem root,Display display,String path){
		File fileScript = new File(path+"/Pictures");
		
		if(fileScript != null && fileScript.isDirectory()){
			//System.out.println(fileScript.getName());
			
			listFile(fileScript,root,display);
		}
	}
	
	public static void loadScriptsByProject(TreeItem root,Display display,String path){
		File fileScript = new File(path+"/Scripts");
		
		if(fileScript != null && fileScript.isDirectory()){
			//System.out.println(fileScript.getName());
			
			listFile(fileScript,root,display);
		}
	}
	
	public static void loadScriptsByLibrary(TreeItem root,Display display,String path){
		File fileScript = new File(path+"/Library");
		if(fileScript != null && fileScript.isDirectory()){
			listFile(fileScript,root,display);
		}
	}
	
	public static void loadScripts(TreeItem script,Display display,String path){
		File fileScript = new File(path);
		File files[] = fileScript.listFiles();
		for(int i=0;i<files.length;i++){
			listFile(files[i],script,display);
		}
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
	
	public static void refresh(TreeItem root,Display display,String path){
		File fileScript = new File(path);
		File files[] = fileScript.listFiles();
		for(int i=0;i<files.length;i++){
			listFile(files[i],root,display);
		}
	}
	
	public static boolean copyFile(String oldPath, String newPath) {
		boolean bRet = false;
	       try { 
	           int bytesum = 0; 
	           int byteread = 0; 
	           File oldfile = new File(oldPath); 
	           if (oldfile.exists()) {
	               InputStream inStream = new FileInputStream(oldPath);
	               FileOutputStream fs = new FileOutputStream(newPath); 
	               byte[] buffer = new byte[1444]; 
	               int length; 
	               while ( (byteread = inStream.read(buffer)) != -1) { 
	                   bytesum += byteread;
	                   fs.write(buffer, 0, byteread); 
	               } 
	               inStream.close(); 
	           } 
	           bRet = true;
	       } 
	       catch (Exception e) { 
	           bRet = false;
	       } 
	       return bRet;

	   } 
}
