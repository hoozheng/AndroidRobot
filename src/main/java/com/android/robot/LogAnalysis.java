package com.android.robot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.android.log.Log;
import com.android.log.LogLineStyleListener;

public class LogAnalysis {

    @SuppressWarnings("unused")
    private Shell      parent  = null;
    private Shell      shell   = null;
    private Display    display = null;

    private String     logFile = null;

    private SashForm   sashFormLog;
    private CTabFolder tabFolderLogList;
    private CTabFolder tabFolderLogDetail;
    private Tree       treeLog;

    private CoolBar    coolBar1;
    private ToolBar    toolBar1;

    @SuppressWarnings("unused")
    private Color      colorBlack;
    private StyledText styledTextLog;

    public LogAnalysis(Shell parent, String filePath) {
        this.parent = parent;
        this.logFile = filePath;
    }

    public void createToolBar() {
        Composite compCoolBar = new Composite(shell, SWT.BORDER);
        compCoolBar.setLayout(new FillLayout());

        CoolBar coolBarSort = new CoolBar(compCoolBar, SWT.NONE);

        CoolItem coolItemSort = new CoolItem(coolBarSort, SWT.NONE);

        Combo prjCombo = new Combo(coolBarSort, SWT.READ_ONLY);
        prjCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        prjCombo.setItems(new String[] { "显示所有用例", "只显示成功的用例", "只显示失败的用例" });
        prjCombo.select(0);

        Point p = prjCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        prjCombo.setSize(p);
        Point p2 = coolItemSort.computeSize(p.x, p.y);
        coolItemSort.setSize(p2);
        coolItemSort.setControl(prjCombo);

        coolBarSort.pack();

    }

    private void createStatusBar() {
        coolBar1 = new CoolBar(shell, SWT.NONE);
        FormData formData1 = new FormData();
        formData1.left = new FormAttachment(0, 0);
        formData1.right = new FormAttachment(100, 0);
        formData1.top = new FormAttachment(100, -24);
        formData1.bottom = new FormAttachment(100, 0);
        coolBar1.setLayoutData(formData1);
        CoolItem coolItem1 = new CoolItem(coolBar1, SWT.NONE);
        toolBar1 = new ToolBar(coolBar1, SWT.NONE);

        ToolItem tiStatusBarTotal = new ToolItem(toolBar1, SWT.NONE);
        ToolItem tiStatusBarPass = new ToolItem(toolBar1, SWT.NONE);
        ToolItem tiStatusBarFail = new ToolItem(toolBar1, SWT.NONE);
        ToolItem tiStatusBarRate = new ToolItem(toolBar1, SWT.NONE);

        tiStatusBarPass.setText("通过:0");
        tiStatusBarFail.setText("失败:0");
        tiStatusBarRate.setText("通过率:0%");
        tiStatusBarTotal.setText("总用例数:0");

        coolItem1.setControl(toolBar1);

        Control control = coolItem1.getControl();
        Point pt = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        pt = coolItem1.computeSize(pt.x, pt.y);
        coolItem1.setSize(pt);

        coolBar1.pack();
    }

    public void createSashForm() {
        sashFormLog = new SashForm(shell, SWT.HORIZONTAL);
        //sashFormLog.setBounds(10, 27, 672, 529);
        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 3);
        formData.right = new FormAttachment(100, -3);
        formData.top = new FormAttachment(0, 25);
        formData.bottom = new FormAttachment(100, -25);
        sashFormLog.setLayoutData(formData);

        createLogList();
        createLogDetail();
        sashFormLog.setWeights(new int[] { 1, 2 });
    }

    public void createLogList() {
        tabFolderLogList = new CTabFolder(sashFormLog, SWT.NONE | SWT.BORDER);
        tabFolderLogList.setTabHeight(0);
        tabFolderLogList.marginHeight = 0;
        tabFolderLogList.marginWidth = 0;
        tabFolderLogList.setLayout(new FillLayout());
        tabFolderLogList.setBounds(5, 5, 200, 465);
        tabFolderLogList.setSimple(false);
        tabFolderLogList.setUnselectedCloseVisible(true);

        CTabItem tabItemLogList = new CTabItem(tabFolderLogList, SWT.NONE | SWT.MULTI
                                                                 | SWT.V_SCROLL);
        tabFolderLogList.setSelection(tabItemLogList);
        tabItemLogList.setText("日志浏览");

        Composite composite = new Composite(tabFolderLogList, SWT.NONE);
        composite.setLayout(new GridLayout());
        treeLog = new Tree(composite, SWT.BORDER);
        colorBlack = display.getSystemColor(SWT.COLOR_BLACK);

        tabItemLogList.setControl(composite);
        treeLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        treeLog.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                Point point = new Point(event.x, event.y);
                TreeItem item = treeLog.getItem(point);
                if (item != null) {
                    String taskName = (String) item.getData("task");
                    String loop = String.valueOf(item.getData("loop"));
                    String caseName = (String) item.getData("case");
                    int index = (Integer) item.getData("index");
                    //System.out.println("task:"+taskName+" loop:"+loop+" caseName:"+caseName+" index:"+index);
                    if (index != 0)
                        Log.loadLogs(styledTextLog, display, logFile, taskName, loop, caseName,
                            index);
                }
            }
        });

    }

    public void createLogDetail() {
        tabFolderLogDetail = new CTabFolder(sashFormLog, SWT.CLOSE | SWT.BORDER);
        tabFolderLogDetail.setTabHeight(0);
        tabFolderLogDetail.marginHeight = 0;
        tabFolderLogDetail.marginWidth = 0;
        tabFolderLogDetail.setMaximizeVisible(false);
        tabFolderLogDetail.setMinimizeVisible(false);
        //tabFolderLogDetail.setSelectionBackground(new Color(display, new RGB(153, 186, 243)));
        tabFolderLogDetail.setSimple(false);
        tabFolderLogDetail.setUnselectedCloseVisible(true);

        CTabItem tabItemLogList = new CTabItem(tabFolderLogDetail, SWT.NONE | SWT.MULTI
                                                                   | SWT.V_SCROLL);
        tabFolderLogDetail.setSelection(tabItemLogList);

        //styledTextLog = new List(tabFolderLogDetail, SWT.BORDER);
        styledTextLog = new StyledText(tabFolderLogDetail, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
                                                           | SWT.V_SCROLL | SWT.READ_ONLY);
        styledTextLog.addLineStyleListener(new LogLineStyleListener(shell));
        tabItemLogList.setControl(styledTextLog);
        //sTextLog.setFont(new Font(display,"Courier New",10,SWT.NONE));

    }

    private void loadLogs() {

        Log.loadLogs(treeLog, display, this.logFile, toolBar1);
    }

    /**
     * Open the window.
     */
    public void open() {
        display = Display.getDefault();
        shell = new Shell();
        shell.setLayout(new FormLayout());
        shell.open();
        shell.setSize(800, 600);
        shell.setText("日志分析系统");
        shell.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/log.png")));

        createToolBar();
        createSashForm();
        createStatusBar();
        loadLogs();

        shell.layout();
        while (shell != null & !shell.isDisposed()) {
            if (display != null && !display.readAndDispatch()) {
                display.sleep();
            }
        }

    }

    public static void main(String[] args) {
        try {
            //LogAnalysis log = new LogAnalysis();
            //log.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
