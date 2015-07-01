package com.android.robot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Keypad {

    private Shell parent = null;
    private Label lblWarning = null;
    private Shell shell  = null;
    private Display display = null;
    String result = "";
    /**
     * @wbp.parser.entryPoint
     */
    public Keypad(Shell parent){
    	this.parent = parent;
    }
    
    public Keypad(){}

	/**
	 * Open the window.
	 */
	public String open() {
		display = Display.getDefault();
		shell = new Shell(parent);//parent
		shell.open();
		shell.setSize(350, 200);
		shell.setText("字符串自动录入");
		shell.setLayout(new GridLayout(1,false));
		//new Label(shell,SWT.CENTER);
		//new Label(shell,SWT.CENTER);
		//
		
		lblWarning = new Label(shell,SWT.CENTER);
		//lblWarning.setText("输入内容不能为空!");
		new Label(shell,SWT.CENTER);
		
		Composite composite1 = new Composite(shell, SWT.NONE);
		composite1.setLayoutData(new GridData(350, 50));
		composite1.setLayout(new GridLayout(2,false));
		
		//Label
		Label lbText = new Label(composite1, SWT.CENTER);
		lbText.setText("     输入字符串：      ");
		
		//Text
		final Text txtInput = new Text(composite1, SWT.BORDER);
		GridData gd_Input = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_Input.heightHint = 15;
		gd_Input.widthHint = 180;
		txtInput.setLayoutData(gd_Input);
		
		Composite composite2 = new Composite(shell, SWT.NONE);
		composite2.setLayoutData(new GridData(350, 100));
		composite2.setLayout(new GridLayout(14,false));
		
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		new Label(composite2,SWT.CENTER);
		
		Button btnOK = new Button(composite2,SWT.NONE);
		GridData gd_btnOK = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnOK.heightHint = 32;
		gd_btnOK.widthHint = 96;
		btnOK.setLayoutData(gd_btnOK);
		btnOK.setText("生成脚本");
		
		btnOK.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
		        if(event.detail == 0) {
		        	String msg = txtInput.getText();
		        	if(!msg.equals("")){
		        		result = msg;
		        		shell.dispose();
		        	}
		        }
		  }
		});

		
		Button btnCancel = new Button(composite2,SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.heightHint = 32;
		gd_btnCancel.widthHint = 96;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("取消");
		btnCancel.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
			        if(event.detail == 0) {
			        	result = "";
			        	shell.dispose();
			        }
			  }
			});
		
		shell.layout();
		while (shell!=null&&!shell.isDisposed()) {
			if (display!=null&&!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return result;
	}
}
