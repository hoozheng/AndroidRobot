package com.android.robot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;

import com.android.util.AdbUtil;
import com.android.util.FileUtility;
import com.android.util.TimeUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;

import org.jfree.chart.ChartUtilities;
import org.jfree.data.time.Millisecond;

public class FpsInfo extends Dialog implements Runnable {

	protected Object result;
	protected Shell shell;
	private boolean isOpened = false;
	private TimeSeries timeseries; // Value坐标轴初始值
	private double lastValue;
	private ArrayList<Double> fpsValue = new ArrayList<Double>();
	private ArrayList<ArrayList<Double>> fpsTimeValue = new ArrayList<ArrayList<Double>>();
	private double fpsTotal;
	static Class class$org$jfree$data$time$Millisecond;
	static Thread thread1;
	private Display disp;
	private static boolean runflag = true;

	private Combo prjCombo;
	private Combo comboDevices;
	private ValueAxis valueaxis;
	private XYPlot xyplot;
	private Vector<String> vecSerialNum;
	private String currentDevice = "";

	private String choice = "";

	private Button btnSave = null;
	private Button btnAnalysis = null;

	// private final String DUMPSYS_MEMINFO = "adb shell dumpsys meminfo";
	private String pid = "";

	private volatile double min = 0;
	private volatile double max = 0;

	private volatile boolean isChanged = false;
	private int selectedDeviceIndex = -1;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public FpsInfo(Shell parent, Vector<String> vecSerialNum, int style) {
		super(parent, style);
		setText("fps实时监控 - FPS");

		runflag = true;
		pid = "";
		this.vecSerialNum = vecSerialNum;
	}
	
	public Vector<String> getProcess(String events) {
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
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();

		Group groupText = new Group(shell, SWT.NONE);
		groupText.setText("FPS信息");
		groupText.setLayoutData(new GridData(570, 50));
		groupText.setLayout(new GridLayout());

		Composite projectComp = new Composite(groupText, SWT.NONE);
		GridData suGridData = new GridData(GridData.FILL_HORIZONTAL);
		suGridData.heightHint = 45;
		projectComp.setLayoutData(suGridData);
		projectComp.setLayout(new GridLayout(3, true));

		comboDevices = new Combo(projectComp, SWT.READ_ONLY);
		comboDevices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// prjCombo.setItems(new String[] { "显示所有用例", "只显示成功的用例", "只显示失败的用例"});
		comboDevices.setItems(new String[] { "显示所有设备" });
		for (int i = 0; i < vecSerialNum.size(); i++) {
			comboDevices.add(vecSerialNum.get(i));
		}

		comboDevices.select(0);

		comboDevices.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = comboDevices.getSelectionIndex();
				if (index != selectedDeviceIndex) {
					if (index == 0) {
						selectedDeviceIndex = index;
						currentDevice = "";

						prjCombo.setItems(new String[] { "显示所有进程" });
						prjCombo.select(0);
					} else {
						selectedDeviceIndex = index;
						currentDevice = comboDevices.getText();

						isChanged = true;
						prjCombo.removeAll();
						prjCombo.setItems(new String[] { "显示所有进程" });
						prjCombo.select(0);

						// Get all process name
						String memInfo = new AdbUtil().send("adb -s "
								+ currentDevice + " shell dumpsys meminfo",5000);
						final Vector<String> vecProcess = getProcess(memInfo);
						for (int i = 0; i < vecProcess.size(); i++) {
							prjCombo.add(vecProcess.get(i).trim());
						}
					}
				}
			}
		});

		prjCombo = new Combo(projectComp, SWT.READ_ONLY);
		prjCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// prjCombo.setItems(new String[] { "显示所有用例", "只显示成功的用例", "只显示失败的用例"});
		prjCombo.setItems(new String[] { "显示所有进程" });

		prjCombo.select(0);
		prjCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Clear
				isChanged = true;
				// Set Pid
				String str = prjCombo.getText();
				int index = str.lastIndexOf("pid");
				if (index >= 0)// pid =
								// str.substring(index+3,str.length()-1).trim();
					pid = str.substring(0, index - 1).trim();
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
				if (event.detail == 0) {
					if (btnSave.getText().equals("保存结果")) {
						isOpened = true;
						saveLog();
						isOpened = false;
					} else {
						// excel.flush();
						// excel.close();
						btnAnalysis.setEnabled(true);
						btnSave.setText("保存结果");
					}
				}
			}
		});

		btnAnalysis = new Button(compButton, SWT.NONE);
		btnAnalysis.setLayoutData(new GridData(GridData.FILL_BOTH));
		btnAnalysis.setText("分析结果");
		btnAnalysis.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == 0) {
					isOpened = true;
					btnSave.setEnabled(false);
					saveLog();
					isOpened = false;
				}
			}
		});

		new Label(projectComp, SWT.NONE);

		Label lbMatchPoint = new Label(shell, SWT.CENTER);
		lbMatchPoint.setText("实时FPS:");

		Composite rtComp = new Composite(shell, SWT.NONE);
		GridData rtGrid = new GridData(GridData.FILL_HORIZONTAL);
		rtGrid.heightHint = 300;
		rtComp.setLayoutData(rtGrid);
		rtComp.setLayout(new FillLayout());

		createTable(rtComp);

		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();

		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				synchronized (this) {
					runflag = false;
				}
			}
		});

		while (shell != null && !shell.isDisposed()) {
			if (display != null && !display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	// private void saveLog() {
	// FileDialog dialog = new FileDialog(shell, SWT.SAVE);
	// dialog.setFilterNames(new String[] { "Excel Files (*.xlsx*)" });
	// dialog.setFilterExtensions(new String[] { "*.xlsx" }); // Windows wild
	// // cards
	// dialog.setFilterPath(".\\workspace"); // Windows path
	// String choice = dialog.open();// return value: path & null
	// if (choice != null) {
	// try {
	// String template = new File("").getCanonicalPath()
	// + "\\template\\FPS_Info.xlsx";
	//
	// FileUtility.copyFile(template, choice);
	//
	// // write data
	// excel = new Excel(new File(choice));
	//
	// btnSave.setText("停止保存");
	// btnAnalysis.setEnabled(false);
	// } catch (IOException e) {
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.out.println("Excel File Error!");
	// }
	// }
	// }

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
				if (file.exists())
					file.delete();

				boolean bRet = file.createNewFile();
				if (bRet == true) {
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

	private void createTable(Composite rtComp) {
		thread1 = new Thread(this);
		lastValue = 100D;
		// 创建时序图对象
		timeseries = new TimeSeries(
				"fps",
				FpsInfo.class$org$jfree$data$time$Millisecond != null ? FpsInfo.class$org$jfree$data$time$Millisecond
						: (FpsInfo.class$org$jfree$data$time$Millisecond = FpsInfo
								.getClass("org.jfree.data.time.Millisecond")));
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(
				timeseries);
		// 创建图表面板
		disp = shell.getDisplay();
		final ChartComposite frame = new ChartComposite(rtComp, SWT.NONE,
				createChart(timeseriescollection), true);
		frame.pack();
		FpsInfo.startThread();
	}

	private JFreeChart createChart(XYDataset xydataset) {
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("", "", "",
				xydataset, true, true, false);
		Plot plot = (Plot) jfreechart.getPlot();
		xyplot = jfreechart.getXYPlot();
		// 纵坐标设定
		valueaxis = xyplot.getDomainAxis();
		valueaxis.setAutoRange(true);
		valueaxis.setFixedAutoRange(60000D);
		valueaxis = xyplot.getRangeAxis();
		valueaxis.setRange(0.0D, 50D);
		plot.setBackgroundPaint(Color.black);
		return jfreechart;
	}

	private void clear() {
		disp.asyncExec(new Runnable() {
			public void run() {
				if (!shell.isDisposed())
					timeseries.clear();
			}
		});
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		// shell = new Shell(getParent(), getStyle());
		shell = new Shell();
		shell.setSize(600, 450);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
	}

	public void writetxtfile(Object[] value) {
		BufferedWriter fw = null;
		String str = "";
		for (int i = 0; i < value.length; i++)
			str += value[i] + "      ";
		File file = new File(choice);

		try {
			fw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), "UTF-8"));
			fw.append(str);
			fw.newLine();
			fw.flush();
		} catch (Exception ex) {
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

	static Class getClass(String s) {
		Class cls = null;
		try {
			cls = Class.forName(s);
		} catch (ClassNotFoundException cnfe) {
			throw new NoClassDefFoundError(cnfe.getMessage());
		}
		return cls;
	}
	
	// 获取draw、proces、execute ，这三个值的集合
	public ArrayList<ArrayList<Double>> getFpsDataByPID(String fpsInfo) {

		// 装载集合time
		ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();

		BufferedReader br = new BufferedReader(new StringReader(fpsInfo));
		try {

			String line = null;

			while ((line = br.readLine()) != null) {

				String[] datas = line.split("\t");
				// System.out.println(datas.length);

				if (datas.length != 4 || "Draw".equals(datas[1])) {
					continue;
				} else {
					double Draw = 0;
					double Process = 0;
					double Execute = 0;
					double sum = 0;
					ArrayList<Double> time = null;

					// 求和
					// for (int i = 1; i < datas.length; i++) {
					// sum += Double.parseDouble(datas[i]);
					// }
					Draw = Double.parseDouble(datas[1]);
					Process = Double.parseDouble(datas[2]);
					Execute = Double.parseDouble(datas[3]);

					// 装载draw、proces、execute
					time = new ArrayList<Double>();
					time.add(Draw);
					time.add(Process);
					time.add(Execute);

					data.add(time);

					// System.out.println(sum);

					Draw = 0;
					Process = 0;
					Execute = 0;
					time = null;
					// sum = 0;
				}

			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}

	@Override
	public void run() {
		int row = 1;
		Object[] obj = new Object[5];

		while (runflag) {
			try {
				synchronized (this) {
					// Set a reasonable range
					// if (isChanged) {
					// // Clear data
					// clear();
					// max = 50D;
					// disp.asyncExec(new Runnable() {
					// public void run() {
					// if (!shell.isDisposed())
					// valueaxis.setRange(0.0D, max * 1.5D);
					// }
					// });
					// isChanged = false;
					// } else {
					// if (max <= lastValue) {
					// max = lastValue;
					// disp.asyncExec(new Runnable() {
					// public void run() {
					// if (!shell.isDisposed())
					// valueaxis.setRange(0.0D, max * 1.5D);
					// }
					// });
					// }
					// }
					if (!currentDevice.equals("") && !isOpened) {
						String DUMPSYS_FPS = "adb -s " + currentDevice
								+ " shell dumpsys gfxinfo";
						String dumpsys_FPS_pid = DUMPSYS_FPS + " " + pid;
						if (dumpsys_FPS_pid.trim().equals(DUMPSYS_FPS)) {
							// String FpsInfo =
							// ADBShell.sendADB(dumpsys_FPS_pid);
							// lastValue = ADBShell.getPSSByALL(memInfo);

							// 弹出对话框，提示选择进程
							// /System.out.println("请选择进程");

						} else {

							// ---------准备条件：开启硬件加速 增大缓存-------------
							// String s1 =
							// "adb shell setprop debug.hwui.profile true";
							// String s2 =
							// "adb shell setprop debug.hwui.profile.maxframes 108000";
							// String s3 = "adb shell stop";
							// String s4 = "adb shell start";
							//
							// try {
							// System.out.println(s1);
							// ADBShell.sendADB(s1);
							// Thread.sleep(2000);
							//
							// System.out.println(s2);
							// ADBShell.sendADB(s2);
							// Thread.sleep(2000);
							//
							// System.out.println(s3);
							// ADBShell.sendADB(s3);
							// Thread.sleep(2000);
							//
							// System.out.println(s4);
							// ADBShell.sendADB(s4);
							// Thread.sleep(20000);
							// } catch (InterruptedException e) {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }

							// 清空集合
							fpsTimeValue.clear();
							fpsValue.clear();

							// 执行 adb shell dumpsys gfxinfo
							// com.yunos.tv.homeshell
							String fpsInfo = new AdbUtil().send(dumpsys_FPS_pid,5000);

							// System.out.println("fpsInfo:" + fpsInfo);

							// 获取adb shell dumpsys gfxinfo pid 的返回值
							fpsTimeValue = getFpsDataByPID(fpsInfo);
							// System.out.println("总集合： " +
							// fpsTimeValue.size());
							for (ArrayList<Double> al : fpsTimeValue) {
								// System.out.println("al集合： " + al.size());
								for (int i = 0; i < al.size(); i++) {
									fpsTotal += al.get(i);
								}
								fpsValue.add(fpsTotal);
							}

						}

						// display
						disp.asyncExec(new Runnable() {
							public void run() {
								if (!shell.isDisposed() && !isOpened) {

									for (ArrayList<Double> al : fpsTimeValue) {

										for (int i = 0; i < al.size(); i++) {

											// 获取total
											fpsTotal += al.get(i);
										}

										// 画图表
										timeseries.addOrUpdate(
												new Millisecond(), fpsTotal);

										fpsTotal = 0;

									}

								}
							}
						});

						// write data
						// 清空上次取值后的total数据
						fpsTotal = 0;

						// obj[1] = lastValue;

						// System.out.println("show data");

						// 获取draw、proces、execute
						for (ArrayList<Double> al : fpsTimeValue) {

							obj[0] = TimeUtil.getTimeAsFormat("yyyy/MM/dd HH:mm:ss");

							for (int i = 0; i < al.size(); i++) {
								obj[i + 1] = al.get(i);

								// 获取total
								fpsTotal += al.get(i);
							}

							// 获取total
							obj[4] = fpsTotal;

							writetxtfile(obj);
							// excel.writeArrayToExcelWithFormat_fps(0,
							// true, row++, 0, obj);

							// System.out.println("obj1: " + obj[1]);
							// System.out.println("obj2: " + obj[2]);
							// System.out.println("obj3: " + obj[3]);
							// System.out.println("obj4: " + obj[4]);

							fpsTotal = 0;

						}

						// System.out.println("time:"+TimeUtility.getTimeAsFormat("yyyy/MM/dd/ HH:mm:ss")+" date:"+lastValue);
						// excel.flush();

						// 清空集合
						// fpsTimeValue.clear();
					}

					Thread.sleep(10000);

				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// System.out.println("Excel closed!");
		// if (excel != null) {
		// try {
		// excel.flush();
		// } catch (XmlValueDisconnectedException ex) {
		//
		// } finally {
		// excel.close();
		// }
		// }

	}

	public static void startThread() {
		thread1.start();
	}

}
