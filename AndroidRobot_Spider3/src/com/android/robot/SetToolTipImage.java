package com.android.robot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SetToolTipImage {

	protected Shell shell;
	protected Display display;
	private Label picture = null;
	private int width = 130;
	private int height = 200;
	
	private String path = "";
	private Image image = null;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//SetToolTipImage window = new SetToolTipImage();
			//window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SetToolTipImage(String filePath){
		path = filePath;
		display = Display.getDefault();
		createContents();
	}
	
	private void setImage(){
		ImageData proData = new ImageData(path);
		image = new Image(display, proData.scaledTo(width-10, height-10));
		picture.setImage(image);
	}
	
	public void setLocation(int x,int y){
		shell.setLocation(new Point(x,y));
	}
	
	public void close(){
		if(image != null)
			image.dispose();
		if(shell != null)
			shell.dispose();
		
	}

	/**
	 * Open the window.
	 */
	public void open() {
		shell.open();
		shell.layout();
		while (shell!=null&&!shell.isDisposed()) {
			if (display!=null&&!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.NO_TRIM | SWT.ON_TOP);
		shell.setSize(width, height);
		shell.setBackground(display.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		shell.setLayoutData(new GridData(GridData.FILL_BOTH));
		shell.setLayout(new GridLayout(1, true));
		
		//shell.setText("SWT Application");
		picture = new Label(shell,SWT.NULL);
		picture.setLayoutData(new GridData(GridData.FILL_BOTH));
		setImage();

	}

}
