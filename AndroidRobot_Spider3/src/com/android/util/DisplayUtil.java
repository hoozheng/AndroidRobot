package com.android.util;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DisplayUtil {
	public static enum Script{
		Read,Create;
	}
	
	public static enum Show{
		Start,Info,Command,Error,Result;
	}
	
	private static int getPCWidth(){
		 return java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	 }
	 
	 private static int getPCHeight(){
		 return java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	 }
	 
	public static int getScaledWidth(){
		int pcWidth = getPCWidth();
		return (pcWidth * 240) / 1024;
	}

	public static int getScaledHeight(){
		int pcHeight = getPCHeight();
		return (pcHeight * 440) / 768;
	}
	
	public static void setLocationOnMiddle(Shell shell,Display display){
		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
       Rectangle shellBounds = shell.getBounds();
       int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;
       int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;
       shell.setLocation(x, y);
	}
}
