package com.android.robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.android.util.AdbUtil;

public class Monkey extends Dialog {

	protected Object result;
	protected Shell shell;
	
	private Vector<String> vecSerialNum;
	
	private static ArrayList<MonkeyPackage> listCase = new ArrayList();
	
	private TableViewer tvPackageName = null;
	private Combo comboDevices = null;
	private Combo comboLog = null;
	
	private Text textEventSeed = null;
	private Text textEventCount = null;

	private Text textEventDelay = null;
	private Text textEventTouch = null;
	private Text textEventMotion = null;
	private Text textEventTrackBall = null;
	private Text textEventBasicNavi = null;
	private Text textEventMajorNavi = null;
	private Text textEventSystemKey = null;
	private Text textEventActivityLaunch = null;
	private Text textEventOtherTypes = null;
	private Text textSearch = null;
	
	private Button btnIngoreForceClose = null;
	private Button btnIngoreANR = null;
	private Button btnIngoreSecurity = null;
	private Button btnStopWhenError = null;
	private Button btnMonitorCodeException = null;
	private Button btnAllDevice = null;
	
	private Label lblStatus = null;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Monkey(Shell parent,Vector<String> vecSerialNum, int style) {
		super(parent, style);
		setText("Monkey测试");
		
		this.vecSerialNum = vecSerialNum;
	}
	
	private void addPackages(String event){
		String[] name = event.split("\n");
		for(int i=0;i<name.length;i++)
			if(!name[i].trim().equals(""))
			{
				MonkeyPackage mp = new MonkeyPackage();
				mp.setName(name[i].trim());
				this.listCase.add(mp);
			}
		tvPackageName.refresh(false);
	}
	
	public String Monkey(String cmd, long millis) {
		Process process = null;
		try {
			System.out.println(cmd);
			process = Runtime.getRuntime().exec(cmd);

			ADBReader reader = new ADBReader(process.getInputStream());
			reader.start();
			reader.join(millis);
			String event = reader.getEvent();
			// System.out.println(event);
			// System.out.println("end");
			if (!event.trim().equals(""))
				return event;
			// process.waitFor();
		} catch (IOException e) {
			System.out.println("Cannot run program");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String concatMonkeyCommand(String sn){
		String cmd = "adb -s "+ sn +" shell \"monkey ";
		Table table = tvPackageName.getTable();
		
		int count = 0;
		for(int i=0;i<table.getItemCount();i++){
			if(table.getItem(i).getChecked()){
				if(count == 0)
					cmd += "-p ";
				count++;
				cmd += table.getItem(i).getText(0).trim() + " ";
			}
		}
		
		if(!cmd.contains("-p") && !textSearch.getText().trim().equals("") && 
				!textSearch.getText().trim().equals("请输入应用包名")){
			cmd += "-p " + textSearch.getText().trim() + " ";
		}
		
		String logLevel = comboLog.getText().trim();
		if(logLevel.equals("Level 0")){
			cmd += "-v ";
		}else if(logLevel.equals("Level 1")){
			cmd += "-v -v ";
		}else if(logLevel.equals("Level 2")){
			cmd += "-v -v -v ";
		}
		
		//伪随机数
		String eventSeed = textEventSeed.getText().trim();
		if(!eventSeed.equals("")){
			cmd += "-s " + eventSeed + " ";
		}else
			cmd += "-s " + 0 + " ";
		
		Boolean checked = btnIngoreForceClose.getSelection();
		if(checked){
			cmd += " --ignore-crashes ";
		}
		
		checked = btnIngoreANR.getSelection();
		if(checked){
			cmd += " --ignore-timeouts ";
		}
		
		checked = btnIngoreSecurity.getSelection();
		if(checked){
			cmd += " --ignore-security-exceptions ";
		}
		
		checked = btnStopWhenError.getSelection();
		if(checked){
			cmd += " --kill-process-after-error ";
		}
		
		checked = btnMonitorCodeException.getSelection();
		if(checked){
			cmd += " --monitor-native-crashes ";
		}
		
		//触摸事件的百分比
		String eventTouch = textEventTouch.getText().trim();
		if(!eventTouch.equals("")){
			cmd += " --pct-touch "+eventTouch+" ";
		}
		
		//动作事件的百分比
		String eventMotion = textEventMotion.getText().trim();
		if(!eventMotion.equals("")){
			cmd += " --pct-motion "+eventMotion+" ";
		}
		
		//轨迹事件的百分比
		String eventTrackBall = textEventTrackBall.getText().trim();
		if(!eventTrackBall.equals("")){
			cmd += " --pct-trackball "+eventTrackBall+" ";
		}
		
		//“基本”导航事件的百分比
		String eventBasicNavi = textEventBasicNavi.getText().trim();
		if(!eventBasicNavi.equals("")){
			cmd += " --pct-nav "+eventBasicNavi+" ";
		}
		
		//“主要”导航事件的百分比
		String eventMajorNavi = textEventMajorNavi.getText().trim();
		if(!eventMajorNavi.equals("")){
			cmd += " --pct-majornav "+eventMajorNavi+" ";
		}
		
		//“系统”按键事件的百分比
		String eventSystemKey = textEventSystemKey.getText().trim();
		if(!eventSystemKey.equals("")){
			cmd += " --pct-syskeys "+eventSystemKey+" ";
		}
		
		//启动Activity的百分比
		String eventActivityLaunch = textEventActivityLaunch.getText().trim();
		if(!eventActivityLaunch.equals("")){
			cmd += " --pct-appswitch "+eventActivityLaunch+" ";
		}
		
		//其它类型事件的百分比
		String eventOtherTypes = textEventOtherTypes.getText().trim();
		if(!eventOtherTypes.equals("")){
			cmd += " --pct-anyevent "+eventOtherTypes+" ";
		}
		
		//事件延迟
		String eventDelay = textEventDelay.getText().trim();
		if(!eventDelay.equals("")){
			cmd += " --throttle "+eventDelay+" ";
		}
		
		//随机数
		String eventCount = textEventCount.getText().trim();
		if(!eventCount.equals("")){
			cmd += eventCount + " ";
		}
		
		cmd += "&\"";
		return cmd;
	}
	
	private String getMonkeyCommand(){
		if(btnAllDevice.getSelection() == true)
			return "ALL";
		
		if(comboDevices.getText().trim().equals("显示所有设备")){
			return "";
		}
		
		String cmd = "adb -s "+comboDevices.getText().trim()+" shell \"monkey ";
		Table table = tvPackageName.getTable();
		
		int count = 0;
		for(int i=0;i<table.getItemCount();i++){
			if(table.getItem(i).getChecked()){
				if(count == 0)
					cmd += "-p ";
				count++;
				cmd += table.getItem(i).getText(0).trim() + " ";
			}
		}
		
		String logLevel = comboLog.getText().trim();
		if(logLevel.equals("Level 0")){
			cmd += "-v ";
		}else if(logLevel.equals("Level 1")){
			cmd += "-v -v ";
		}else if(logLevel.equals("Level 2")){
			cmd += "-v -v -v ";
		}
		
		//伪随机数
		String eventSeed = textEventSeed.getText().trim();
		if(!eventSeed.equals("")){
			cmd += "-s " + eventSeed + " ";
		}else
			cmd += "-s " + 0 + " ";
		
		Boolean checked = btnIngoreForceClose.getSelection();
		if(checked){
			cmd += " --ignore-crashes ";
		}
		
		checked = btnIngoreANR.getSelection();
		if(checked){
			cmd += " --ignore-timeouts ";
		}
		
		checked = btnIngoreSecurity.getSelection();
		if(checked){
			cmd += " --ignore-security-exceptions ";
		}
		
		checked = btnStopWhenError.getSelection();
		if(checked){
			cmd += " --kill-process-after-error ";
		}
		
		checked = btnMonitorCodeException.getSelection();
		if(checked){
			cmd += " --monitor-native-crashes ";
		}
		
		//触摸事件的百分比
		String eventTouch = textEventTouch.getText().trim();
		if(!eventTouch.equals("")){
			cmd += " --pct-touch "+eventTouch+" ";
		}
		
		//动作事件的百分比
		String eventMotion = textEventMotion.getText().trim();
		if(!eventMotion.equals("")){
			cmd += " --pct-motion "+eventMotion+" ";
		}
		
		//轨迹事件的百分比
		String eventTrackBall = textEventTrackBall.getText().trim();
		if(!eventTrackBall.equals("")){
			cmd += " --pct-trackball "+eventTrackBall+" ";
		}
		
		//“基本”导航事件的百分比
		String eventBasicNavi = textEventBasicNavi.getText().trim();
		if(!eventBasicNavi.equals("")){
			cmd += " --pct-nav "+eventBasicNavi+" ";
		}
		
		//“主要”导航事件的百分比
		String eventMajorNavi = textEventMajorNavi.getText().trim();
		if(!eventMajorNavi.equals("")){
			cmd += " --pct-majornav "+eventMajorNavi+" ";
		}
		
		//“系统”按键事件的百分比
		String eventSystemKey = textEventSystemKey.getText().trim();
		if(!eventSystemKey.equals("")){
			cmd += " --pct-syskeys "+eventSystemKey+" ";
		}
		
		//启动Activity的百分比
		String eventActivityLaunch = textEventActivityLaunch.getText().trim();
		if(!eventActivityLaunch.equals("")){
			cmd += " --pct-appswitch "+eventActivityLaunch+" ";
		}
		
		//其它类型事件的百分比
		String eventOtherTypes = textEventOtherTypes.getText().trim();
		if(!eventOtherTypes.equals("")){
			cmd += " --pct-anyevent "+eventOtherTypes+" ";
		}
		
		//事件延迟
		String eventDelay = textEventDelay.getText().trim();
		if(!eventDelay.equals("")){
			cmd += " --throttle "+eventDelay+" ";
		}
		
		//随机数
		String eventCount = textEventCount.getText().trim();
		if(!eventCount.equals("")){
			cmd += eventCount + " ";
		}
		
		cmd += "&\"";
		return cmd;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		
		Composite compUp = new Composite(this.shell, SWT.NONE);
        GridData gdUp = new GridData(GridData.FILL_HORIZONTAL);
        gdUp.heightHint = 542;
        compUp.setLayoutData(gdUp);
        compUp.setLayout(new GridLayout(2, false));
        
        Composite compDown = new Composite(this.shell, SWT.NONE);
        GridData gdDown = new GridData(GridData.FILL_HORIZONTAL);
        gdDown.heightHint = 50;
        compDown.setLayoutData(gdDown);
        compDown.setLayout(new GridLayout(4, true));
        
        Composite compLeft = new Composite(compUp, SWT.NONE);
        GridData gdLeft = new GridData(GridData.FILL_VERTICAL);
        gdLeft.widthHint = 400;
        compLeft.setLayoutData(gdLeft);
        compLeft.setLayout(new GridLayout(1, true));
        
        Group grpAbc = new Group(compLeft, SWT.NONE);
        GridData gd_grpAbc = new GridData(GridData.FILL_HORIZONTAL);
        gd_grpAbc.heightHint = 68;
        grpAbc.setLayoutData(gd_grpAbc);
        grpAbc.setLayout(new GridLayout(2, true));
        grpAbc.setText("测试设备");
        
        Label lblDevice = new Label(grpAbc, SWT.LEFT);
        lblDevice.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblDevice.setText("设备名称:");
        
        Label lblLog = new Label(grpAbc, SWT.LEFT);
        lblLog.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblLog.setText("日志类型:");
        
        comboDevices  = new Combo(grpAbc, SWT.READ_ONLY);
        comboDevices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    //prjCombo.setItems(new String[] { "显示所有用例", "只显示成功的用例", "只显示失败的用例"}); 
        comboDevices.setItems(new String[] { "显示所有设备"}); 
        for(int i=0;i<vecSerialNum.size();i++){
        	comboDevices.add(vecSerialNum.get(i));
        }
        
        comboDevices.select(0);
        
        comboDevices.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	String cmd = "adb -s " + comboDevices.getText().trim() + " shell ls /data/data/";
            	String event = new AdbUtil().send(cmd,5000);
            	addPackages(event);
            }
        });
        
        comboLog = new Combo(grpAbc, SWT.READ_ONLY);
        comboLog.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    //prjCombo.setItems(new String[] { "显示所有用例", "只显示成功的用例", "只显示失败的用例"}); 
        for(int i=0;i<3;i++){
        	comboLog.add("Level "+i);
        }
        
        comboLog.select(2);
        
        Label lblSearch = new Label(compLeft, SWT.LEFT);
        lblSearch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblSearch.setText("查询应用:");
        
        textSearch = new Text(compLeft, SWT.BORDER|SWT.SINGLE);
        textSearch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textSearch.setText("请输入应用包名");
        
        textSearch.addListener(SWT.Modify, new Listener() {
	  	      public void handleEvent(Event event) {
		  	        if(event.detail == 0) {
		  	        	tvPackageName.refresh(false);
		  	        }
		  	  }
	     });
        
        textSearch.addListener(SWT.FocusIn, new Listener() {
	  	      public void handleEvent(Event event) {
		  	        if(event.detail == 0) {
		  	        	if(textSearch.getText().trim().equals("请输入应用包名"))
		  	        		textSearch.setText("");
		  	        }
		  	  }
	     });
        
        textSearch.addListener(SWT.FocusOut, new Listener() {
	  	      public void handleEvent(Event event) {
		  	        if(event.detail == 0) {
		  	        	if(textSearch.getText().trim().equals(""))
		  	        		textSearch.setText("请输入应用包名");
		  	        }
		  	  }
	     });
        
        //Table
        tvPackageName = 
  				new TableViewer(compLeft, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL | SWT.CHECK);
  		
  		Table table = tvPackageName.getTable();
  		table.setLayoutData(new GridData(GridData.FILL_BOTH));
  		
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
        newColumnTableColumn.setWidth(350);
        newColumnTableColumn.setText("应用包名");
        
        //Add Content Provider
        tvPackageName.setContentProvider(new IStructuredContentProvider() { 
			public Object[] getElements(Object element){
                if(element instanceof ArrayList)
                    return ((ArrayList)element).toArray();
                else
                    return new Object[0];
            }

			public void dispose() {}

			public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		});
        
        tvPackageName.setLabelProvider(new ITableLabelProvider(){

			@Override
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				MonkeyPackage o = (MonkeyPackage)element;
                if(columnIndex==0) {return o.getName();} 
                return ""; 
			}
		});
		
        tvPackageName.addFilter(new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				MonkeyPackage o = (MonkeyPackage)element;
				if(textSearch.getText().trim().equals("请输入应用包名") || 
						textSearch.getText().trim().equals(""))
				{
					return true;
				}
				
				return o.getName().contains(textSearch.getText().trim());
			}
     
        });
        
        tvPackageName.setInput(listCase);
        
        Composite compRight = new Composite(compUp, SWT.NONE);
        GridData gdRight = new GridData(GridData.FILL_VERTICAL);
        gdRight.widthHint = 174;
        compRight.setLayoutData(gdRight);
        compRight.setLayout(new GridLayout(1, true));
        
        Group groupTime = new Group(compRight, SWT.NONE);
        GridData gdTime = new GridData(GridData.FILL_HORIZONTAL);
        gdTime.heightHint = 88;
        groupTime.setLayoutData(gdTime);
        groupTime.setLayout(new GridLayout(2, true));
        groupTime.setText("执行次数设置");
        
        Label lblEventCount = new Label(groupTime, SWT.LEFT);
        lblEventCount.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventCount.setText("随机事件数:");
        
        textEventCount = new Text(groupTime, SWT.BORDER|SWT.SINGLE);
        textEventCount.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventCount.setText("1200000000");
        textEventCount.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventDelay = new Label(groupTime, SWT.LEFT);
        lblEventDelay.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventDelay.setText("事件延迟数:");
        
        textEventDelay = new Text(groupTime, SWT.BORDER|SWT.SINGLE);
        textEventDelay.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventDelay.setText("300");
        textEventDelay.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventSeed = new Label(groupTime, SWT.LEFT);
        lblEventSeed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventSeed.setText("伪随机数:");
        
        textEventSeed = new Text(groupTime, SWT.BORDER|SWT.SINGLE);
        textEventSeed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventSeed.setText("1000");
        textEventSeed.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Group groupEventPercent = new Group(compRight, SWT.NONE);
        GridData gdEventPercent = new GridData(GridData.FILL_HORIZONTAL);
        gdEventPercent.heightHint = 230;
        groupEventPercent.setLayoutData(gdEventPercent);
        groupEventPercent.setLayout(new GridLayout(2, true));
        groupEventPercent.setText("设置事件百分比");
        
        Label lblEventTouch = new Label(groupEventPercent, SWT.LEFT);
        lblEventTouch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventTouch.setText("触摸事件:");
        
        textEventTouch = new Text(groupEventPercent, SWT.BORDER|SWT.SINGLE);
        textEventTouch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventTouch.setText("");
        textEventTouch.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventMotion = new Label(groupEventPercent, SWT.LEFT);
        lblEventMotion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventMotion.setText("动作事件:");
        
        textEventMotion = new Text(groupEventPercent, SWT.BORDER|SWT.SINGLE);
        textEventMotion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventMotion.setText("");
        textEventMotion.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventTrackBall = new Label(groupEventPercent, SWT.LEFT);
        lblEventTrackBall.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventTrackBall.setText("轨迹事件:");
        
        textEventTrackBall = new Text(groupEventPercent, SWT.BORDER|SWT.SINGLE);
        textEventTrackBall.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventTrackBall.setText("0");
        textEventTrackBall.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventBasicNavi = new Label(groupEventPercent, SWT.LEFT);
        lblEventBasicNavi.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventBasicNavi.setText("基本导航:");
        
        textEventBasicNavi = new Text(groupEventPercent, SWT.BORDER|SWT.SINGLE);
        textEventBasicNavi.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventBasicNavi.setText("0");
        textEventBasicNavi.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventMajorNavi = new Label(groupEventPercent, SWT.LEFT);
        lblEventMajorNavi.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventMajorNavi.setText("主要导航:");
        
        textEventMajorNavi = new Text(groupEventPercent, SWT.BORDER|SWT.SINGLE);
        textEventMajorNavi.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventMajorNavi.setText("0");
        textEventMajorNavi.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventSystemKey = new Label(groupEventPercent, SWT.LEFT);
        lblEventSystemKey.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventSystemKey.setText("系统按键:");
        
        textEventSystemKey = new Text(groupEventPercent, SWT.BORDER|SWT.SINGLE);
        textEventSystemKey.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventSystemKey.setText("");
        textEventSystemKey.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventActivityLaunch = new Label(groupEventPercent, SWT.LEFT);
        lblEventActivityLaunch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventActivityLaunch.setText("启动活动:");
        
        textEventActivityLaunch = new Text(groupEventPercent, SWT.BORDER|SWT.SINGLE);
        textEventActivityLaunch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventActivityLaunch.setText("");
        textEventActivityLaunch.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Label lblEventOtherTypes = new Label(groupEventPercent, SWT.LEFT);
        lblEventOtherTypes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        lblEventOtherTypes.setText("其他事件:");
        
        textEventOtherTypes = new Text(groupEventPercent, SWT.BORDER|SWT.SINGLE);
        textEventOtherTypes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textEventOtherTypes.setText("0");
        textEventOtherTypes.addVerifyListener(new VerifyListener() {
        	public void verifyText(VerifyEvent e) {   
        	    Pattern pattern = Pattern.compile("[0-9]\\d*");   
        	    Matcher matcher = pattern.matcher(e.text);   
        	    if (matcher.matches())
        	    	e.doit = true;
        	    else if (e.text.length() > 0)
        	    	e.doit = false;   
        	    else
        	    	e.doit = true;
        	}
        });
        
        Group groupDebug = new Group(compRight, SWT.NONE);
        GridData gdDebug = new GridData(GridData.FILL_HORIZONTAL);
        gdDebug.heightHint = 136;
        groupDebug.setLayoutData(gdDebug);
        groupDebug.setLayout(new GridLayout(1, true));
        groupDebug.setText("调试");
        
        btnIngoreForceClose = new Button(groupDebug, SWT.CHECK);
        btnIngoreForceClose.setText("忽略Force Close");
        btnIngoreForceClose.setSelection(true);
        
        btnIngoreANR = new Button(groupDebug, SWT.CHECK);
        btnIngoreANR.setText("忽略ANR");
        btnIngoreANR.setSelection(true);
        
        btnIngoreSecurity = new Button(groupDebug, SWT.CHECK);
        btnIngoreSecurity.setText("忽略安全许可异常");
        btnIngoreSecurity.setSelection(true);
        
        btnStopWhenError = new Button(groupDebug, SWT.CHECK);
        btnStopWhenError.setText("错误时停止进程");
        
        btnMonitorCodeException = new Button(groupDebug, SWT.CHECK);
        btnMonitorCodeException.setText("监视本地代码异常");

        btnAllDevice = new Button(groupDebug, SWT.CHECK);
        btnAllDevice.setText("运行在所有设备");
        
        new Label(compDown, SWT.NONE);
        
        Button btnExecute = new Button(compDown, SWT.NONE);
        btnExecute.setLayoutData(new GridData(GridData.FILL_BOTH));
        btnExecute.setText("执行测试");
        
        btnExecute.addListener(SWT.Selection, new Listener() {
	  	      public void handleEvent(Event event) {
	  	        if(event.detail == 0) {
	  	        	String monkeyCmd = getMonkeyCommand();
	  	        	if(monkeyCmd == null || monkeyCmd.trim().equals("")){
	  	        		MessageBox box = new MessageBox( shell ,SWT.ICON_WARNING|SWT.YES);
	  	        		box.setMessage("请选择正确的设备!");
	  	        		box.open();
	  	        	}else if(monkeyCmd.equals("ALL")){
	  	        		int count = comboDevices.getItemCount();
	  	        		String message = "";
	  	        		String status = "";
	  	        		int pass = 0;
	  	        		for(int i=1;i<count;i++){
	  	        			String sn = comboDevices.getItem(i);
	  	        			String cmd = concatMonkeyCommand(sn);
	  	        			//System.out.println(cmd);
	  	        			String result = Monkey(cmd,5000);
	  	        			
	  	        			if(result.contains("android.intent.category.MONKEY")){
	  	        				pass++;
	  	        				message += sn + "运行成功;";
	  	        				status = sn + "运行成功;(" + i + "/" + (count-1);
	  	        			}else{
	  	        				message += sn + "运行失败;";
	  	        				status = sn + "运行失败;(" + i + "/" + (count-1);
	  	        			}
	  	        			
	  	        			lblStatus.setText(status);
	  	        			
	  	        			if(i%2 == 0)
	  	        				message +="\n";
	  	        		}
	  	        		
	  	        		lblStatus.setText("Monkey执行完毕!");
	  	        		message +="\n";
	  	        		message += pass + "运行成功, " + (count - pass -1) + "运行失败"; 
	  	        		MessageBox box = new MessageBox( shell ,SWT.ICON_WARNING|SWT.YES);
	  	        		box.setMessage(message);
	  	        		box.open();
	  	        		
	  	        	}
	  	        	else{
	  	        		String result = Monkey(monkeyCmd,2000);
	  	        		if(result.contains("android.intent.category.MONKEY")){
	  	        			MessageBox box = new MessageBox( shell ,SWT.ICON_WARNING|SWT.YES);
		  	        		box.setMessage("提交Monkey测试成功!");
		  	        		box.open();
	  	        		}else{
	  	        			MessageBox box = new MessageBox( shell ,SWT.ICON_WARNING|SWT.YES);
		  	        		box.setMessage("提交Monkey测试失败!");
		  	        		box.open();
	  	        		}
	  	        		
	  	        	}	
	  	        }
	  	      }
        });

        Button btnStop = new Button(compDown, SWT.NONE);
        btnStop.setLayoutData(new GridData(GridData.FILL_BOTH));
        btnStop.setText("停止测试");
        
        btnStop.addListener(SWT.Selection, new Listener() {
	  	      public void handleEvent(Event event) {
	  	        if(event.detail == 0) {
	  	        	
	  	        }
	  	      }
        });
        
        new Label(compDown, SWT.NONE);
        

        lblStatus = new Label(shell, SWT.NONE);
		//createStatusBar();
        
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
	
	private void createStatusBar(){
		Composite compStatus = new Composite(shell, SWT.BORDER);
		GridData gdStatus = new GridData(GridData.FILL_HORIZONTAL);
		gdStatus.heightHint = 20;
		compStatus.setLayoutData(gdStatus);
		compStatus.setLayout(new GridLayout(1, true));
		
		
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(600, 665);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1,false));
	}
	
	class ADBReader extends Thread {
		private InputStream is;
		private String line = "";

		ADBReader(InputStream is) {
			this.is = is;
		}

		public String getEvent() {
			return line;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String temp = "";
				while ((temp = br.readLine()) != null) {
					line += temp + "\n";
					
				}
			} catch (IOException e) {
				line = "";
			}
		}
	}

}
