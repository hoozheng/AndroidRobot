package com.android.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

public class NodeViewer extends Dialog {

	protected Object result;
	protected Shell shell;

	private Combo comboDevices = null;
	private Socket socketView = null;
	private BufferedReader inView = null;
	private BufferedWriter outView = null;
	private IDevice[] devices = null;
	private StyledText text_1 = null;
	
	private String preSelect = "";
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public NodeViewer(Shell parent, int style,IDevice[] devices) {
		super(parent, style);
		this.devices = devices;
		setText("Node Viewer");
	}
	
	public void getDevices() throws IOException{
		if(comboDevices !=null && devices != null)
			for(int i=0;i<devices.length;i++){
				String strSerialNum = devices[i].getSerialNumber();
				comboDevices.add(strSerialNum);
				comboDevices.setData(strSerialNum, devices[i]);
			}
	}
	
	private void disconnectViewServer(){
		try {
			if (outView != null) {
				outView.close();
				
			}
			if (inView != null) {
				inView.close();
				
			}
			if(socketView != null)
				socketView.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			
		}
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
        
        Composite deviceComp = new Composite(shell, SWT.NONE);
        GridData suGridData = new GridData(GridData.FILL_HORIZONTAL);
		suGridData.heightHint = 85;
		deviceComp.setLayoutData(suGridData);
		deviceComp.setLayout(new GridLayout(1, true));
        
        Group group = new Group(deviceComp, SWT.NONE);
        group.setText("设备区域");
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        group.setLayout(new GridLayout(3, true));
        
        comboDevices = new Combo(group, SWT.READ_ONLY);
        comboDevices.add("显示所有设备");
        comboDevices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comboDevices.select(0);
        comboDevices.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	String strSerialNum = comboDevices.getText().trim();
            	if(!strSerialNum.equals("显示所有设备") && !strSerialNum.equals(preSelect)){
            		preSelect = strSerialNum;
					
            	}
            }
        });
        
        Button btnConfirm = new Button(group, SWT.NONE);
        btnConfirm.setText("获取控件");
        btnConfirm.addListener(SWT.Selection, new Listener() {
    	      public void handleEvent(Event event) {
    	        if(event.detail == 0) {
    	        	
    	        }
    	      }
        });
        new Label(group, SWT.NONE);
        
        Composite textComp = new Composite(shell, SWT.NONE);
		textComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		textComp.setLayout(new GridLayout(1, true));
		
		text_1 = new StyledText(textComp, 
				SWT.BORDER|SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL);
        text_1.setLayoutData(new GridData(GridData.FILL_BOTH));
		text_1.setFont(new Font(shell.getDisplay(),"Courier New",10,SWT.NONE));
		
		text_1.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e){
				if(e.stateMask == SWT.CTRL && e.keyCode == 'f'){
					StyledText st = text_1;
					Find_Replace find_Replace = Find_Replace.newInstance(shell);
					find_Replace.setTextControl(st);
					find_Replace.open();
				}
			}
			public void keyReleased(KeyEvent e){
			}
			
		});
		
		try {
			getDevices();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (shell!=null&&!shell.isDisposed()) {
			if (display!=null&&!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell();
		shell.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout gLayout = new GridLayout(1, true);
        shell.setLayout(gLayout);
		shell.setSize(700, 550);
		shell.setText(getText());
		
		shell.addListener(SWT.Close,new Listener(){
			public void handleEvent(Event event){
			switch(event.type)   {
		        case SWT.Close:
		        	System.out.println("Close");
			        break;
		        }
			}
		});

	}

}
