package com.android.robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.*;

class SplashWindow extends JWindow{
	private String file = "";
	/**
	 * Loading Splash windows from start
	 * @param filename
	 * Author: He Zheng
	 * Modified: Oct 22,2012
	 */
	public SplashWindow(String filename){
		//super(f);
		this.file = filename;
	}
	
	public void start(){
		JLabel l = new JLabel(new ImageIcon(file));
		getContentPane().add(l,BorderLayout.CENTER);
		pack();
		Dimension screenSize =
				Toolkit.getDefaultToolkit().getScreenSize();
		
		Dimension labelSize = l.getPreferredSize();
		setLocation(screenSize.width/2 - (labelSize.width/2),
				screenSize.height/2 - (labelSize.height/2));
		setVisible(true);
	}
	
	public void closeSplashWindow(){
		setVisible(false);
		dispose();
	}
	public static void main(String[] args){
		SplashWindow sp = new SplashWindow("splash.jpg");
	}
} 