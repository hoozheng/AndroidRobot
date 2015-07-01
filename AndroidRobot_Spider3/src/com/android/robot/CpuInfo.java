package com.android.robot;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;

import com.android.util.AdbUtil;
import com.android.util.TimeUtil;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.data.time.Millisecond;

public class CpuInfo extends Dialog implements Runnable {

	protected Object result;
	protected Shell shell;
	
	private TimeSeries timeseries;	// Value坐标轴初始值
	private double lastValue;	
	static Class class$org$jfree$data$time$Millisecond;	
	static Thread thread1; 
	private Display disp;	
	private static boolean runflag = true;
	
	private boolean isOpened = false;
	private boolean isSaved = false;
	private String choice = "";
	
	private Button btnSave = null;
	private Button btnAnalysis = null;
	
	private Combo prjCombo;
	private Combo comboDevices;
	private ValueAxis valueaxis;
	private XYPlot xyplot;
	private Vector<String> vecSerialNum;
	private String currentDevice = "";
	
	//private final String DUMPSYS_MEMINFO = "adb shell dumpsys meminfo";
	private String pid = "";
	
	private volatile double min = 0;
	private volatile double max = 0;
	
	private volatile boolean isChanged = false;
	private int selectedDeviceIndex = -1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CpuInfo(Shell parent,Vector<String> vecSerialNum, int style) {
		super(parent, style);
		setText("CPU实时监控 ");
		
		runflag=true;
		pid = "";
		this.vecSerialNum = vecSerialNum;
		isSaved = false;
	}

	private Vector<String> getProcess(String events) {
		String temp = events;
		Vector<String> tempVector = new Vector<String>();
		int index = temp.indexOf("\n");
		// System.out.println("==========================");
		String line = "";
		while (index >= 0) {
			line = temp.substring(0, index);
			temp = temp.substring(index + 1, temp.length());
			if (line.contains("Total PSS by process:")) {
				break;
			} else {
				index = temp.indexOf("\n");
				continue;
			}
		}

		index = temp.indexOf("\n");
		while (index >= 0) {
			line = temp.substring(0, index).trim();
			temp = temp.substring(index + 1, temp.length());
			if (!line.contains("Total PSS by OOM adjustment:")) {
				if (!line.trim().equals("")) {
					int indexKB = line.indexOf("kB:");
					if (indexKB >= 0) {
						tempVector.add(line.substring(indexKB + 4,
								line.length()));
					}

				}
			} else
				break;
			index = temp.indexOf("\n");

		}

		return tempVector;
	}
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		Group groupText = new Group(shell, SWT.NONE);
		groupText.setText("CPU信息");
		groupText.setLayoutData(new GridData(570, 50));
		groupText.setLayout(new GridLayout());
		
		Composite projectComp = new Composite(groupText, SWT.NONE);
        GridData suGridData = new GridData(GridData.FILL_HORIZONTAL);
		suGridData.heightHint = 45;
        projectComp.setLayoutData(suGridData);
        projectComp.setLayout(new GridLayout(3, true));
        
        comboDevices = new Combo(projectComp, SWT.READ_ONLY);
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
            	int index = comboDevices.getSelectionIndex();
            	if(index != selectedDeviceIndex){
            		if(index == 0){
            			selectedDeviceIndex = index;
                		currentDevice = "";
                		
                		prjCombo.setItems(new String[] { "显示所有进程"}); 
                		prjCombo.select(0);
            		}else{
	            		selectedDeviceIndex = index;
	            		currentDevice = comboDevices.getText();
	            		
	            		isChanged = true;
	            		prjCombo.removeAll();
	            		prjCombo.setItems(new String[] { "显示所有进程"}); 
	            		prjCombo.select(0);
	            		
	            		 //Get all process name
	            	    String memInfo = new AdbUtil().send("adb -s "+currentDevice+" shell dumpsys meminfo",5000);
	            		final Vector<String> vecProcess = getProcess(memInfo);
	            		for(int i =0;i<vecProcess.size();i++){
	            			prjCombo.add(vecProcess.get(i).trim());
	            		}
            		}
            	}
            }
        });
        
        prjCombo = new Combo(projectComp, SWT.READ_ONLY);
	    prjCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    //prjCombo.setItems(new String[] { "显示所有用例", "只显示成功的用例", "只显示失败的用例"}); 
	    prjCombo.setItems(new String[] { "显示所有进程"}); 
		
	    prjCombo.select(0);
	    prjCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	//Clear
            	isChanged = true;
            	//Set Pid
            	String str = prjCombo.getText();
            	int index = str.lastIndexOf("pid");
            	if(index >= 0)//pid = str.substring(index+3,str.length()-1).trim();
            		pid = str.substring(0,index-1).trim();
            	else
            		pid = "";
            	
            }
        });
	    
	    Composite compButton = new Composite(projectComp, SWT.NONE);
        GridData gdButton = new GridData(GridData.FILL_HORIZONTAL);
        gdButton.heightHint = 38;
        compButton.setLayoutData(gdButton);
        compButton.setLayout(new GridLayout(2, true));
        
	    btnSave = new Button(compButton, SWT.NONE);
	    btnSave.setLayoutData(new GridData(GridData.FILL_BOTH));
	    btnSave.setText("保存结果");
	    btnSave.addListener(SWT.Selection, new Listener() {
	  	      public void handleEvent(Event event) {
	  	        if(event.detail == 0) {
	  	        	if(btnSave.getText().equals("保存结果"))
	  	        	{
		  	        	isOpened = true;
		  	        	saveLog();
		  	        	isOpened = false;
	  	        	}else{
//	  	        		excel.flush();
//						excel.close();
	  	        		isSaved = false;
						btnAnalysis.setEnabled(true);
						btnSave.setText("保存结果");
	  	        	}
	  	        }
	  	      }
	    });

	    btnAnalysis = new Button(compButton, SWT.NONE);
	    btnAnalysis.setLayoutData(new GridData(GridData.FILL_BOTH));
	    btnAnalysis.setText("分析结果");
	    
	    
		Label lbMatchPoint = new Label(shell, SWT.CENTER);
		lbMatchPoint.setText("实时CPU状态:");
		
		Composite rtComp = new Composite(shell, SWT.NONE);
        GridData rtGrid = new GridData(GridData.FILL_HORIZONTAL);
        rtGrid.heightHint = 300;
        rtComp.setLayoutData(rtGrid);
        rtComp.setLayout(new FillLayout());
        
		createTable(rtComp);
		
		shell.open();
		shell.layout();		
		Display display = getParent().getDisplay();

		shell.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				synchronized(this){
					runflag=false;
				}
			}
		});
		
		while (shell!=null&&!shell.isDisposed()) {
			if (display!=null&&!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	private void saveLog() {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String[] { "TXT Files (*.txt*)" });
		dialog.setFilterExtensions(new String[] { "*.txt" }); // Windows wild
																// cards
		dialog.setFilterPath(".\\workspace"); // Windows path
		choice = dialog.open();// return value: path & null
		if (choice != null) {
			try {
				File file = new File(choice);
				if(file.exists())
					file.delete();
				
				boolean bRet = file.createNewFile();
				if(bRet == true)
				{
					isSaved = true;
					btnSave.setText("停止保存");
					btnAnalysis.setEnabled(false);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Excel File Error!");
			}
		}
	}
	
//	private void saveLog(){
//		FileDialog dialog = new FileDialog (shell, SWT.SAVE);
//		dialog.setFilterNames (new String [] {"Excel Files (*.xlsx*)"});
//		dialog.setFilterExtensions (new String [] {"*.xlsx"}); //Windows wild cards
//		dialog.setFilterPath (".\\workspace"); //Windows path
//		String choice = dialog.open();//return value: path & null
//		if(choice != null){
//			try {
//				String template = 
//						new File("").getCanonicalPath() + "\\template\\CPU_Info.xlsx";
//
//				FileUtility.copyFile(template, choice);
//				
//				//write data
//				excel = new Excel(new File(choice));
//				
//				btnSave.setText("停止保存");
//				btnAnalysis.setEnabled(false);
//			} catch (IOException e) {
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.out.println("Excel File Error!");
//			}
//		}
//	}
	
	private void createTable(Composite rtComp){
		thread1 = new Thread(this);
		lastValue = 0D;
		// 创建时序图对象
		timeseries = new TimeSeries(
		"实时CPU状态",
		CpuInfo.class$org$jfree$data$time$Millisecond != null ? CpuInfo.class$org$jfree$data$time$Millisecond
		: (CpuInfo.class$org$jfree$data$time$Millisecond = CpuInfo.getClass("org.jfree.data.time.Millisecond")));
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(
		timeseries);
		// 创建图表面板
		disp = shell.getDisplay();	 
		final ChartComposite frame = 
				new ChartComposite(rtComp, SWT.NONE,createChart(timeseriescollection), true);
		frame.addChartMouseListener(new ChartMouseListener() {

			@Override
			public void chartMouseClicked(ChartMouseEvent arg0) {
				//arg0.getTrigger().
				System.out.println(arg0.getTrigger().getButton());
				System.out.println(arg0.getTrigger().isPopupTrigger());
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			
		});
		frame.pack();
		
		CpuInfo.startThread();
	}
	
	
	private JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("","", "", xydataset, true, true, false);
		Plot plot = (Plot)jfreechart.getPlot();
		xyplot = jfreechart.getXYPlot();
		// 纵坐标设定
		valueaxis = xyplot.getDomainAxis();
		valueaxis.setAutoRange(true);
		valueaxis.setFixedAutoRange(60000D);	 
		valueaxis = xyplot.getRangeAxis();
		valueaxis.setRange(0.0D, 100D);
		plot.setBackgroundPaint(Color.black);
		return jfreechart;
	}
	
	private void clear(){
		disp.asyncExec(new Runnable() {
			public void run() {
				if(!shell.isDisposed())
					timeseries.clear();
			}
		});
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		//shell = new Shell(getParent(), getStyle());
		shell = new Shell();
		shell.setSize(600, 450);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1,false));
	}
	
	static Class getClass(String s) {
		Class cls = null;
		try {
			cls = Class.forName(s);
		} catch (ClassNotFoundException cnfe) {
			throw new NoClassDefFoundError(cnfe.getMessage());
		}
		return cls;
	}
	
	public void writetxtfile(Object[] value){
    	BufferedWriter fw = null;
    	String str = "";
    	for(int i=0;i<value.length;i++)
    		str += value[i]+ "      ";
    	File file = new File(choice);
    	
		try {
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			fw.append(str);
			fw.newLine();
			fw.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }

	public float getTotalCPUInfo(String cpuInfo) {
		float cpu = 0;
		int start = 0;
		// System.out.println(cpuInfo);
		int index = cpuInfo.lastIndexOf("\n\n");
		if (index >= 0) {
			String line = cpuInfo.substring(0, index);

			// get last line
			index = line.lastIndexOf("\n\n");
			line = cpuInfo.substring(index + 2, line.length());
			String character[] = line.split("\\s+");

			// convert to number
			try {
				cpu = Float.parseFloat(character[0].trim().substring(0,
						character[0].trim().length() - 1));
			} catch (Exception ex) {
				cpu = 0;
			}
		}
		return cpu;
	}
	
	public float getCPUInfoByPID(String memInfo) {
		float cpu = 0;
		String line = memInfo.trim();
		if (!line.equals("")) {
			String character[] = line.split("\\s+");
			// convert to number
			cpu = Float.parseFloat(character[0].trim().substring(0,
					character[0].trim().length() - 1));
		}
		return cpu;
	}
	
	@Override
	public void run() {
		int row = 1;
		Object[] obj = {1,2};
		
		while (runflag) {
			try {
				synchronized(this){
					//Set a reasonable range
					if(isChanged)
					{
						//Clear data
						clear();
						isChanged = false;
					}
					
					if(!currentDevice.equals("") && !isOpened){
						String DUMPSYS_CPUINFO = "adb -s "+currentDevice+" shell dumpsys cpuinfo";
						if(pid.trim().equals("")){
							String cpuInfo = new AdbUtil().send(DUMPSYS_CPUINFO,5000);
							double tempData = getTotalCPUInfo(cpuInfo);
							if(tempData > 0)
								lastValue = tempData;
						}else{
							String dump_sys_pid = DUMPSYS_CPUINFO + " | grep "+pid;
							String memInfo = new AdbUtil().send(dump_sys_pid,5000);
							double tempData = getCPUInfoByPID(memInfo);
							if(tempData > 0)
								lastValue = tempData;
						}

						//Thread.sleep(1500);
						disp.asyncExec(new Runnable() {
							public void run() {
								if(!shell.isDisposed() && !isOpened)
									timeseries.addOrUpdate(new Millisecond(), lastValue);
								
								try {
									Thread.currentThread().sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
							}
						});
						
						//write data
						if(isSaved){
							obj[0] = TimeUtil.getTimeAsFormat("yyyy/MM/dd HH:mm:ss");
							obj[1] = lastValue;
							writetxtfile(obj);
						}
					}
					Thread.sleep(10000);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public static void startThread() {
		thread1.start();
	}

}
