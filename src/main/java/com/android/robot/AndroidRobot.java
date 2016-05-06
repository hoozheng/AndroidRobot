/*
 * Copyright (c) 2010, 2012 hoozheng.
 * Android Robot is designed for Android System automation,
 * which use adb interface to control Android handset.
 * Author:
 * 		  hoozheng
 */

package com.android.robot;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.json.JSONObject;

import com.android.control.MtesterAutoCompleteField;
import com.android.control.PromptString;
import com.android.control.StyledTextContentAdapter;
import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.device.DeviceDetector;
import com.android.ide.PythonLineStyleListener;
import com.android.ide.ReadProperties;
import com.android.log.Log;
import com.android.minicap.DeviceSocketClient;
import com.android.minicap.LaunchMinicap;
import com.android.python.AndroidDriver;
import com.android.python.RobotScriptRunner;
import com.android.tasks.Task;
import com.android.tasks.TestCase;
import com.android.ui.data.Element;
import com.android.ui.data.UIElement;
import com.android.ui.data.UIPool;
import com.android.uiautomator.AdbDevice;
import com.android.uiautomator.UiAutomatorClient;
import com.android.util.AdbUtil;
import com.android.util.ApkInfo;
import com.android.util.ApkUtil;
import com.android.util.Constants;
import com.android.util.DisplayUtil;
import com.android.util.Env;
import com.android.util.FileUtility;
import com.android.util.HardwareUtil;
import com.android.util.HttpClientUtil;
import com.android.util.ProjectUtil;
import com.android.util.PropertiesUtil;
import com.android.util.RobotTreeUtil;
import com.android.util.ServerUtil;
import com.android.util.TaskUtil;
import com.android.util.TimeUtil;

/**
 * AndroidRobot 主函数
 *
 * @author hezheng.hz
 */
public class AndroidRobot {

    private static ToolBar                                   toolBar;
    private static Display                                   display;

    private static Shell                                     shell;
    private static FormData                                  formData;
    private static ListViewer                                listViewerLog;
    private static SashForm                                  sashFormProject;
    private static SashForm                                  sashFormContent;
    private static SashForm                                  sashFormProject2;
    private static SashForm                                  sashFormProgress;
    private static TableViewer                               tableViewerCase;

    private static Tree                                      tree                   = null;
    private static Composite                                 composite              = null;
    private static Composite                                 treeComp               = null;
    private static Listener                                  textListener           = null;
    private static CTabItem                                  tabItemTasks           = null;

    private static List                                      listHandsets           = null;
    private static Table                                     tasksTable             = null;

    private static TableEditor                               taskEditor             = null;
    private static TableEditor                               taskNumEditor          = null;
    private static TableEditor                               caseEditor             = null;

    private static Color                                     black                  = null;
    private static Label                                     lblTotal               = null;
    private static Label                                     lblSelected            = null;
    private static TreeEditor                                editor                 = null;
    private static Text                                      adbLogcatText          = null;
    private static TreeItem[]                                lastItem               = null;
    private static String[]                                  lastSelectNode         = null;

    private static List                                      cloudHandsetsList      = null;

    private static CTabFolder                                tabFolder;
    private static CTabFolder                                tabContent;
    private static CTabFolder                                tabLogFolder;
    private static CTabFolder                                tabProgress;
    private static CTabFolder                                tabHandsetName;

    private static CoolBar                                   coolBar1;
    private static CoolBar                                   coolBar2;
    private static CoolBar                                   coolBar3;
    private static CoolBar                                   coolBar4;

    private static ToolItem                                  tiStatusBarScript;
    private static ToolItem                                  tiStatusBarPass;
    private static ToolItem                                  tiStatusBarFail;
    private static ToolItem                                  tiStatusBarTotal;
    private static ToolItem                                  tiStatusBarConnect;
    private static ToolItem                                  tiStatusBarConnectServer;

    private static ProgressBar                               pbRecorder;

    private static Menu                                      treeMenu;
    private static Menu                                      recordMenu;
    private static Menu                                      recordMenu_2;
    private static Menu                                      handsetMenu;
    private static MenuItem                                  itemSaveCP;
    private static MenuItem                                  itemSaveCP_2;
    private static MenuItem                                  mntmSubmitProject      = null;
    private static MenuItem                                  mntmHandsetList        = null;
    private static MenuItem                                  mntmConnect            = null;
    private static MoveTableItem                             moveTableItem          = null;

    private static ArrayList<TaskRowItem>                    taskRowList            = null;
    private static Hashtable                                 htTab                  = new Hashtable();
    private static Vector<MenuItem>                          menuItemVec            = new Vector();
    private static AdbDevice                                 adbGetDevice           = new AdbDevice();
    private static Hashtable<String, TableViewer>            htCaseViewer           = new Hashtable();
    private static Hashtable<String, ArrayList<TaskRowItem>> htCaseList             = new Hashtable();

    //Save all runner
    private static ArrayList<RobotScriptRunner>              runners                = new ArrayList<RobotScriptRunner>();

    public static String                                     projectPath            = "";
    public static String                                     workspacePath          = "";
    private static String                                    taskFilePath           = "";
    private static String                                    screenFilePath         = "";

    private static IDevice                                   device                 = null;
    private static int                                       deviceIndex            = -1;

    private static int                                       mWidth                 = 0;
    private static int                                       mHeight                = 0;

    private static int                                       scaledWidth            = 0;
    private static int                                       scaledHeight           = 0;

    private static boolean                                   isRecording            = false;
    private static boolean                                   isSetCheckPoint        = false;

    //Tool Item
    private static ToolItem                                  itemRun                = null;
    private static ToolItem                                  itemStop               = null;
    private static ToolItem                                  itemNew                = null;
    private static ToolItem                                  itemSetApk             = null;
    private static ToolItem                                  itemSave               = null;
    private static ToolItem                                  itemRec                = null;
    private static ToolItem                                  itemSaveScreen         = null;
    private static ToolItem                                  itemDel                = null;
    private static ToolItem                                  itemOpen               = null;
    private static ToolItem                                  itemImportLog          = null;
    private static ToolItem                                  itemDeployment         = null;
    private static ToolItem                                  itemMemInfo            = null;
    private static ToolItem                                  itemCPUInfo            = null;
    private static ToolItem                                  itemFPSInfo            = null;
    private static ToolItem                                  itemMonkeyTest         = null;
    private static ToolItem                                  itemLogcat             = null;
    private static ToolItem                                  itemPerformance        = null;
    private static ToolItem                                  itemFPS                = null;
    private static ToolItem                                  itemTraffic            = null;
    private static ToolItem                                  itemTextbox            = null;
    private static ToolItem                                  itemNodesView          = null;
    private static ToolItem                                  itemCheckPoint         = null;

    private static ToolItem                                  itemStopRecord         = null;
    private static ToolItem                                  itemTakePhoto          = null;
    private static ToolItem                                  itemBack               = null;
    private static ToolItem                                  itemMenu               = null;
    private static ToolItem                                  itemHome               = null;
    private static ToolItem                                  itemInput              = null;

    private static int                                       tcPBIndex              = 0;
    private static Vector<String>                            vecSerialNumber        = new Vector();

    //===========================================remote==========================================
    private static UiAutomatorClient                         client                 = null;

    //Set Check point
    private static int                                       offsetX                = 0;
    private static int                                       offsetY                = 0;
    private static boolean                                   isSelendroid           = false;
    private static boolean                                   isChromedriver         = false;

    private static String                                    curPicturePath         = "";
    private static SetToolTipImage                           tipImage               = null;
    private static Hashtable<String, ProgressBar>            htProgress             = new Hashtable();
    private static Hashtable<String, RowItem>                htRowItem              = new Hashtable();

    private static DeviceDetector                            findDevices            = new DeviceDetector(
                                                                                        adbGetDevice);

    private static Vector<String>                            vectorLog              = new Vector();
    private static ArrayList<RowItem>                        listCase               = new ArrayList();

    private static DeviceSocketClient                        deviceClient           = null;
    private static LaunchMinicap                             minicap                = null;

    //==============================record====================================
    private static Canvas                                    lbCapture              = null;
    private static GC                                        gc                     = null;

    private static double                                    startMotionTime        = 0;
    private static double                                    endMotionTime          = 0;
    private static double                                    startMoveTime          = 0;

    private static boolean                                   isLongTouchMoveEvent   = false;
    private static boolean                                   isTouchDownEvent       = false;
    private static boolean                                   isMoveEvent            = false;

    private static int                                       down_x                 = 0;
    private static int                                       down_y                 = 0;
    private static int                                       up_x                   = 0;
    private static int                                       up_y                   = 0;

    private static int                                       move_x                 = 0;
    private static int                                       move_y                 = 0;
    private static int                                       moveCount              = 0;

    private static Logger                                    log                    = Logger
                                                                                        .getLogger(AndroidRobot.class);

    private static Timer                                     refreshProgressUITimer = new Timer(
                                                                                        50,
                                                                                        new ActionListener() {
                                                                                            public void actionPerformed(ActionEvent e) {
                                                                                                final Element element = UIPool
                                                                                                    .poll();
                                                                                                if (null != element) {
                                                                                                    Display
                                                                                                        .getDefault()
                                                                                                        .asyncExec(
                                                                                                            new Runnable() {
                                                                                                                public void run() {
                                                                                                                    if (element
                                                                                                                        .getElement()
                                                                                                                        .equals(
                                                                                                                            UIElement.TASK_PROGRESS_BAR)) {
                                                                                                                        if (((Integer) element
                                                                                                                            .getValue()) > 0)
                                                                                                                            setProgressBarMax(
                                                                                                                                element
                                                                                                                                    .getSN(),
                                                                                                                                element
                                                                                                                                    .getTaskName(),
                                                                                                                                element
                                                                                                                                    .getScriptName());
                                                                                                                        else
                                                                                                                            setProgressBarCount(
                                                                                                                                element
                                                                                                                                    .getSN(),
                                                                                                                                element
                                                                                                                                    .getTaskName(),
                                                                                                                                element
                                                                                                                                    .getScriptName());
                                                                                                                    } else if (element
                                                                                                                        .getElement()
                                                                                                                        .equals(
                                                                                                                            UIElement.TASK_START_TIME)) {
                                                                                                                        setBeginTime(
                                                                                                                            element
                                                                                                                                .getSN(),
                                                                                                                            element
                                                                                                                                .getTaskName(),
                                                                                                                            element
                                                                                                                                .getScriptName(),
                                                                                                                            String
                                                                                                                                .valueOf(element
                                                                                                                                    .getValue()));
                                                                                                                    } else if (element
                                                                                                                        .getElement()
                                                                                                                        .equals(
                                                                                                                            UIElement.TASK_TEST_RESULT)) {
                                                                                                                        setPassOrFailCount(
                                                                                                                            element
                                                                                                                                .getSN(),
                                                                                                                            element
                                                                                                                                .getTaskName(),
                                                                                                                            element
                                                                                                                                .getScriptName(),
                                                                                                                            Boolean
                                                                                                                                .parseBoolean((String) element
                                                                                                                                    .getValue()));
                                                                                                                    }
                                                                                                                }
                                                                                                            });

                                                                                                }
                                                                                            }
                                                                                        });

    private static Timer                                     watchRunScript         = new Timer(
                                                                                        5000,
                                                                                        new ActionListener() {
                                                                                            public void actionPerformed(ActionEvent e) {
                                                                                                int i = 0;
                                                                                                for (i = 0; i < runners
                                                                                                    .size(); i++) {
                                                                                                    if (runners
                                                                                                        .get(
                                                                                                            i)
                                                                                                        .isAlive())
                                                                                                        break;
                                                                                                }

                                                                                                if (i == runners
                                                                                                    .size()) {
                                                                                                    Display
                                                                                                        .getDefault()
                                                                                                        .asyncExec(
                                                                                                            new Runnable() {
                                                                                                                public void run() {
                                                                                                                    stop(true);
                                                                                                                    setButton(
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true,
                                                                                                                        true);
                                                                                                                }
                                                                                                            });

                                                                                                }
                                                                                            }
                                                                                        });

    private static String takesnapshot(String path, String fileName) {
        AdbUtil
            .send(
                "adb shell \"LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P 720x1080@720x1080/0 -s >/data/local/tmp/minicap_1.jpg\"",
                1000);
        AdbUtil.send("adb pull /data/local/tmp/minicap_1.jpg " + path + "/" + fileName, 1000);
        return path + "/" + fileName;
    }

    public static void timerStop() throws Exception {
        if (client != null) {
            client.disconnect();
            client = null;
        }

        if (deviceClient != null)
            deviceClient.disconnect();

        if (minicap != null)
            minicap.stopMinicap();

    }

    /**
     * 录制操作部分
     * 包括各种模拟人手工点击的按钮
     */
    private static void touchDown(MouseEvent event) {
        int x = event.x;
        int y = event.y;

        down_x = (int) (x * mWidth / scaledWidth);
        down_y = (int) (y * mHeight / scaledHeight);

        isTouchDownEvent = true;
        startMotionTime = System.currentTimeMillis();

        client.touchDown(down_x, down_y);
    }

    private static void touchUp(MouseEvent event) {
        int x = event.x;
        int y = event.y;

        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;

        if (y > (scaledHeight))
            y = scaledHeight;

        up_x = (int) (x * mWidth / scaledWidth);
        up_y = (int) (y * mHeight / scaledHeight);

        isTouchDownEvent = false;
        endMotionTime = System.currentTimeMillis();

        if (((double) (endMotionTime - startMotionTime) / 1000) < 0.5
            && ((down_x == up_x) && (down_y == up_y))) {
            insertScript("device[" + deviceIndex + "].click(" + up_x + "," + up_y + ")");
            client.touchUp(up_x, up_y);
        } else if (((double) (endMotionTime - startMotionTime) / 1000) >= 0.5
                   && ((down_x == up_x) && (down_y == up_y))) {
            insertScript("device[" + deviceIndex + "].longClick(" + up_x + "," + up_y + ")");
            client.longClick(up_x, up_y);
        } else if (isMoveEvent) {
            insertScript("device[" + deviceIndex + "].swipe(" + down_x + "," + down_y + "," + up_x
                         + "," + up_y + ")");
            client.swipe(down_x, down_y, up_x, up_y, 20);
        }
        moveCount = 0;
        isMoveEvent = false;
        isLongTouchMoveEvent = false;

    }

    private static void move(MouseEvent event) {
        int x = event.x;
        int y = event.y;

        x = (int) (x * mWidth / scaledWidth);
        y = (int) (y * mHeight / scaledHeight);

        moveCount++;

        if (moveCount == 1)
            startMoveTime = System.currentTimeMillis();

        move_x = x;
        move_y = y;

        isMoveEvent = true;
        if (((double) (startMoveTime - startMotionTime) / 1000 >= 0.2)) {
            isLongTouchMoveEvent = true;
        }

        client.touchMove(x, y);
    }

    private static void back() {
        client.pressBack();
    }

    private static void menu() {
        client.pressMenu();
    }

    private static void home() {
        client.pressHome();
    }

    private static void enableRecordButton(boolean capture, boolean takePhoto, boolean back,
                                           boolean menu, boolean home, boolean input) {
        itemTakePhoto.setEnabled(takePhoto);
        itemBack.setEnabled(back);
        itemMenu.setEnabled(menu);
        itemHome.setEnabled(home);
        itemInput.setEnabled(input);
    }

    private static void saveCheckPoint() {
        isSetCheckPoint = true;
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);

        dialog.setFilterNames(new String[] { "png Files (*.png)" });
        dialog.setFilterExtensions(new String[] { "*.png*" }); //Windows wild cards

        dialog.setFilterPath(System.getProperty("user.dir") + "/workspace");
        String choice = dialog.open();

        if (choice != null && !choice.trim().equals("")) {
            curPicturePath = choice.substring(0, choice.lastIndexOf(Constants.FILE_SEPARATOR));
            try {
                if (!choice.trim().endsWith(".png"))
                    choice += ".png";

                File file = new File(choice);
                String filePath = client.takeSnapshot(file.getParent(), file.getName());
                System.out.println("file:" + choice);
                BufferedImage bufferedImage = ImageIO.read(file);

                if (!ImageIO.write(bufferedImage, "png", new File(filePath))) {
                    showNotification("保存图片失败!", SWT.ICON_WARNING | SWT.YES);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isSetCheckPoint = false;
            }
        }

        isSetCheckPoint = false;
    }

    private static void setCheckPoint() {
        saveCheckPoint();
    }

    private static void setCheckPointOnPic() {
        TreeItem root = RobotTreeUtil.getRoot(lastItem[0]);
        if (root != null) {
            String relativePath = RobotTreeUtil.getPathFromTree(lastItem[0]);
            String node = relativePath.replace("/", Constants.FILE_SEPARATOR);
            //node = AndroidRobot\Pictures\abc.png
            SetCheckPoint2 window = new SetCheckPoint2(shell, projectPath, node);
            window.open();

        }

    }

    /**
     * 启动录制
     *
     * @throws Exception
     */
    private static void record() throws Exception {
        if ((isRecording == false) && (lastItem[0] != null)
            && (lastItem[0].getData("device") != null)) {
            device = (IDevice) lastItem[0].getData("device");
            deviceIndex = (Integer) lastItem[0].getData("index");
        }

        if (device != null) {
            enableRecord(false);
            isSetCheckPoint = false;
            client = new UiAutomatorClient(device.getSerialNumber());
            if (client.connect()) {
                enableRecordButton(true, true, true, true, true, true);
                mWidth = client.getDisplayWidth();
                mHeight = client.getDisplayHeight();
                //向手机注入Minicap截图工具
                String sdk = device.getProperty("ro.build.version.sdk");
                String abi = device.getProperty("ro.product.cpu.abi");
                device.pushFile(System.getProperty("user.dir")
                                + ("/plugins/resources/minicap/bin/" + abi + "/minicap"),
                    "/data/local/tmp/minicap");
                File minicapFile = new File(System.getProperty("user.dir")
                                            + ("/plugins/resources/minicap/shared/android-" + sdk
                                               + "/" + abi + "/minicap.so"));
                if (minicapFile.exists() == false)
                    sdk = "M";
                device
                    .pushFile(
                        System.getProperty("user.dir")
                                + ("/plugins/resources/minicap/shared/android-" + sdk + "/" + abi + "/minicap.so"),
                        "/data/local/tmp/minicap.so");

                CollectingOutputReceiver receiver = new CollectingOutputReceiver();
                device.executeShellCommand("chmod 777 /data/local/tmp/minicap", receiver);
                device.executeShellCommand("chmod 777 /data/local/tmp/minicap.so", receiver);
                device.executeShellCommand("dumpsys window displays", receiver);

                minicap = new LaunchMinicap(device.getSerialNumber(), mWidth, mHeight, mWidth,
                    mHeight, 0);
                Thread thread = new Thread(minicap);
                thread.start();
                thread.join(10000);
                AdbUtil.send("adb -s " + device.getSerialNumber()
                             + " forward tcp:1313 localabstract:minicap", 3000);
                deviceClient = new DeviceSocketClient(display, gc);
                deviceClient.connect("127.0.0.1", 1313);
            } else {
                enableRecordButton(false, false, false, false, false, false);
                enableRecord(true);
            }
        } else {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setMessage("请先选择一个设备!");
            box.open();
        }
    }

    private static void stopRecord() {
        try {
            timerStop();
            if (device != null)
                AdbUtil.kill(device.getSerialNumber(), "uiautomator");
        } catch (Exception e) {
            log.error("stopRecord - " + e);
        }
        enableRecord(true);
        enableRecordButton(false, false, false, false, false, false);
    }

    //==========================================record end======================================================

    private static synchronized void stop(boolean isFinished) {
        if (false == isFinished) {
            for (int i = 0; i < runners.size(); i++) {
                runners.get(i).finished();
                runners.get(i).stop();
            }
        }

        if (refreshProgressUITimer != null && refreshProgressUITimer.isRunning())
            refreshProgressUITimer.stop();

        if (watchRunScript != null && watchRunScript.isRunning())
            watchRunScript.stop();

        try {
            if (isRecording == true) {
                client.connect();
            }
        } catch (Exception ex) {
            log.error(ex);
        }

        setButton(true, true, true, true, true, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true);
    }

    private static void takeSnapShot() {
        String picPath = null;
        if (isRecording == false) {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setMessage("请在录制界面截图！");
            box.open();
            return;
        }

        CTabItem currentTab = getCurrentTab();
        if (currentTab == null)
            picPath = ".\\workspace";
        else {
            String tabName = currentTab.getText();
            if (tabName.startsWith("*"))
                tabName = tabName.substring(1, tabName.length());
            //picPath = ".\\workspace\\" +  (String)htTab.get((String)currentTab.getData(tabName)) +"\\Pictures";
            picPath = ".\\workspace\\"
                      + ((String) currentTab.getData(tabName)).substring(0,
                          ((String) currentTab.getData(tabName)).indexOf("\\")) + "\\Pictures";
        }

        SaveScreen saveScreen = new SaveScreen(shell, SWT.CLOSE);
        String choice = saveScreen.open();
        if (choice != null && !choice.trim().equals(""))
            try {
                //				takeSnapshoot(new File(".\\workspace\\"+choice));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private static int showNotification(String msg, int style) {
        MessageBox box = new MessageBox(shell, style);
        box.setMessage(msg);
        int choice = box.open();
        return choice;
    }

    private static void deleteFiles() {
        boolean isALL = false;
        int choice = 0;
        Vector<TreeItem> vecTreeItem = RobotTreeUtil.getSelectElements(tree);

        if (vecTreeItem.size() > 0) {
            for (int i = 0; i < vecTreeItem.size(); i++) {
                TreeItem item = vecTreeItem.get(i);
                //delete all project
                if (item.getParentItem() == null) {
                    choice = showNotification("您是否确定要删除整个项目" + item.getText() + "?",
                        SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
                    if (choice == SWT.YES) {
                        deleteFolder(item);
                        item.dispose();
                    }
                } else {
                    //delete file step by step
                    String node = item.getText();
                    if (node.equals("Logs") || node.equals("Pictures") || node.equals("Scripts")) {

                    } else {
                        if (isALL == false) {
                            choice = showNotification("您是否确定要删除文件" + item.getText() + "?",
                                SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
                        }
                        if (choice == SWT.YES) {
                            isALL = true;
                            deleteFolder(item);
                            item.dispose();
                        } else if (choice == SWT.YES) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void deleteFolder(TreeItem treeItem) {
        TreeItem root = treeItem.getParentItem();
        if (root == null) {
            try {
                deleteFile(".\\workspace\\" + treeItem.getText());
                checkParent(treeItem, false, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                deleteFile(".\\workspace\\" + RobotTreeUtil.getPathFromTree(treeItem));
                checkParent(treeItem, false, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void deleteFile(String filepath) throws IOException {
        File f = new File(filepath);
        if (f.exists() && f.isDirectory()) {
            if (f.listFiles().length == 0) {
                f.delete();
            } else {
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        deleteFile(delFile[j].getAbsolutePath());
                    }
                    //delete it from tab fold
                    removeTabItem(delFile[j].getName());
                    //delete file
                    delFile[j].delete();
                }
                deleteFile(filepath);
            }
        } else if (f.exists() && f.isFile()) {
            //delete it from tab fold
            removeTabItem(f.getName());
            //delete file
            f.delete();

        }
    }

    public static void createToolBar() {
        //录制工具栏
        CoolBar coolBarRecord = new CoolBar(shell, SWT.NONE);
        FormData formData3 = new FormData();
        formData3.left = new FormAttachment(75, 0);
        formData3.right = new FormAttachment(100, 0);
        formData3.top = new FormAttachment(0, 0);
        formData3.bottom = new FormAttachment(0, 24);
        coolBarRecord.setLayoutData(formData3);

        final CoolItem coolItemRecord = new CoolItem(coolBarRecord, SWT.NONE);
        final ToolBar toolBarRecord = new ToolBar(coolBarRecord, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        final Menu menuEmu = new Menu(shell, SWT.POP_UP);

        //recorder
        itemRec = new ToolItem(toolBarRecord, SWT.DROP_DOWN);
        Image iconRec = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/record.png"));
        itemRec.setImage(iconRec);
        itemRec.setToolTipText("录制");

        MenuItem recEmu = new MenuItem(menuEmu, SWT.PUSH);
        recEmu.setText("模拟录制(F9)");
        recEmu.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    try {
                        record();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        MenuItem recMobile = new MenuItem(menuEmu, SWT.PUSH);
        recMobile.setText("手机录制");
        recMobile.setEnabled(false);

        itemStopRecord = new ToolItem(toolBarRecord, SWT.NONE);
        Image iconStopRecord = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/stoprecord.png"));
        itemStopRecord.setImage(iconStopRecord);
        itemStopRecord.setToolTipText("停止录制(F10)");
        itemStopRecord.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    stopRecord();
                }
            }
        });

        /*
        itemCapture = new ToolItem(toolBarRecord, SWT.NONE);
        Image iconCapture = new Image(display, "./icons/pause.png");
        itemCapture.setImage(iconCapture);
        itemCapture.setToolTipText("设置比对信息(F11)");
        itemCapture.setEnabled(false);
        //action for RecordItem
        itemCapture.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event event) {
                if(event.detail == 0) {
                	setCaptureMode();
                }
              }
        });
        */
        itemTakePhoto = new ToolItem(toolBarRecord, SWT.NONE);
        Image iconTakePhoto = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/takephoto.png"));
        itemTakePhoto.setImage(iconTakePhoto);
        itemTakePhoto.setToolTipText("保存图片(F12)");
        itemTakePhoto.setEnabled(false);
        itemTakePhoto.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    setCheckPoint();
                }
            }
        });

        itemBack = new ToolItem(toolBarRecord, SWT.NONE);
        Image iconBack = new Image(display, ClassLoader.getSystemResourceAsStream("icons/back.png"));
        itemBack.setImage(iconBack);
        itemBack.setToolTipText("返回(Back)");
        itemBack.setEnabled(false);
        itemBack.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    if (isSetCheckPoint == false)
                        back();
                }
            }
        });

        itemMenu = new ToolItem(toolBarRecord, SWT.NONE);
        Image iconMenu = new Image(display, ClassLoader.getSystemResourceAsStream("icons/menu.png"));
        itemMenu.setImage(iconMenu);
        itemMenu.setToolTipText("菜单(Menu)");
        itemMenu.setEnabled(false);
        itemMenu.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    if (isSetCheckPoint == false)
                        menu();
                }
            }
        });

        itemHome = new ToolItem(toolBarRecord, SWT.NONE);
        Image iconHome = new Image(display, ClassLoader.getSystemResourceAsStream("icons/home.png"));
        itemHome.setImage(iconHome);
        itemHome.setToolTipText("Home");
        itemHome.setEnabled(false);
        itemHome.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    if (isSetCheckPoint == false)
                        home();
                }
            }
        });

        itemInput = new ToolItem(toolBarRecord, SWT.NONE);
        Image iconInput = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/input.png"));
        itemInput.setImage(iconInput);
        itemInput.setToolTipText("Input");
        itemInput.setEnabled(false);
        itemInput.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    Keypad keypad = new Keypad(shell);
                    //1 --- OK  0 --- Cancel
                    String result = keypad.open();

                    if (!result.equals("")) {
                        //input("input(\""+result +"\")");
                    }
                }
            }
        });

        coolItemRecord.setControl(toolBarRecord);

        Control control3 = coolItemRecord.getControl();
        Point pt3 = control3.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        pt3 = coolItemRecord.computeSize(pt3.x, pt3.y);
        coolItemRecord.setSize(pt3);
        coolBarRecord.pack();

        //Event
        itemRec.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == SWT.ARROW) {
                    Rectangle bounds = itemRec.getBounds();
                    Point point = toolBarRecord.toDisplay(bounds.x, bounds.y + bounds.height);
                    menuEmu.setLocation(point);
                    menuEmu.setVisible(true);
                }
            }
        });

        CoolBar coolBarPrj = new CoolBar(shell, SWT.NONE);
        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        formData.top = new FormAttachment(0, 0);
        formData.bottom = new FormAttachment(0, 24);
        coolBarPrj.setLayoutData(formData);

        CoolItem coolItemPrj = new CoolItem(coolBarPrj, SWT.NONE);
        //ToolBar
        toolBar = new ToolBar(coolBarPrj, SWT.FLAT | SWT.WRAP | SWT.RIGHT);

        //apk
        itemSetApk = new ToolItem(toolBar, SWT.PUSH);
        Image iconSetAPK = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/apk.png"));
        itemSetApk.setImage(iconSetAPK);
        itemSetApk.setToolTipText("设置被测应用");
        itemSetApk.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    SetApkWindow newPrj = new SetApkWindow(shell, SWT.CLOSE, workspacePath
                                                                             + "/system.properties"); //
                    String choice = newPrj.open();

                    if (choice != null && !choice.equals(""))
                        createProject(choice);
                }
            }
        });

        //new project & script
        itemNew = new ToolItem(toolBar, SWT.DROP_DOWN);
        Image iconNew = new Image(display, ClassLoader.getSystemResourceAsStream("icons/new.png"));
        itemNew.setImage(iconNew);
        itemNew.setToolTipText("新建");

        final Menu menuProject = new Menu(shell, SWT.POP_UP);
        MenuItem newProject = new MenuItem(menuProject, SWT.PUSH);
        newProject.setText("项目");
        newProject.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createProject();
                }
            }
        });

        MenuItem newScript = new MenuItem(menuProject, SWT.PUSH);
        newScript.setText("脚本");
        newScript.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createScript();
                }
            }
        });

        //Create a new project
        itemNew.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == SWT.ARROW) {
                    Rectangle bounds = itemNew.getBounds();
                    Point point = toolBar.toDisplay(bounds.x, bounds.y + bounds.height);
                    menuProject.setLocation(point);
                    menuProject.setVisible(true);
                }
            }
        });

        //save
        itemSave = new ToolItem(toolBar, SWT.PUSH);
        Image iconSave = new Image(display, ClassLoader.getSystemResourceAsStream("icons/save.png"));
        itemSave.setImage(iconSave);
        itemSave.setToolTipText("保存");
        itemSave.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    if (getCurrentTab() != null)
                        saveScript();
                }
            }
        });

        //run
        itemRun = new ToolItem(toolBar, SWT.DROP_DOWN);
        Image iconRun = new Image(display, ClassLoader.getSystemResourceAsStream("icons/run.png"));
        itemRun.setImage(iconRun);
        itemRun.setToolTipText("运行(F5)");

        final Menu menuNorRun = new Menu(shell, SWT.POP_UP);
        MenuItem newNorRun = new MenuItem(menuNorRun, SWT.PUSH);
        newNorRun.setText("并发运行");
        Image iconRunOrder = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/order.png"));
        newNorRun.setImage(iconRunOrder);
        newNorRun.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    run();
                }
            }
        });

        MenuItem newNorRun2 = new MenuItem(menuNorRun, SWT.PUSH);
        newNorRun2.setText("交互运行");
        Image iconRunInteract = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/interact.png"));
        newNorRun2.setImage(iconRunInteract);
        newNorRun2.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    run2();
                }
            }
        });
        itemRun.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == SWT.ARROW) {
                    Rectangle bounds = itemRun.getBounds();
                    Point point = toolBar.toDisplay(bounds.x, bounds.y + bounds.height);
                    menuNorRun.setLocation(point);
                    menuNorRun.setVisible(true);
                }
            }
        });

        //	    itemRun.addListener(SWT.Selection, new Listener() {//disconn
        //		      public void handleEvent(Event event) {
        //		        if(event.detail == 0) {
        //		        	run();
        //		        }
        //		      }
        //		});

        //Stop
        itemStop = new ToolItem(toolBar, SWT.PUSH);
        Image iconStop = new Image(display, ClassLoader.getSystemResourceAsStream("icons/stop.png"));
        itemStop.setImage(iconStop);
        itemStop.setToolTipText("停止(F6)");
        itemStop.addListener(SWT.Selection, new Listener() {//disconn
                public void handleEvent(Event event) {
                    if (event.detail == 0) {
                        stop(false);
                    }
                }
            });
        //Take Snapshoot
        itemSaveScreen = new ToolItem(toolBar, SWT.PUSH);
        Image iconSaveScreen = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/takesnapshoot.png"));
        itemSaveScreen.setImage(iconSaveScreen);
        itemSaveScreen.setToolTipText("截取屏幕");
        itemSaveScreen.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    takeSnapShot();
                }
            }
        });
        //itemSaveScreen.setEnabled(false);
        //delete
        itemDel = new ToolItem(toolBar, SWT.PUSH);
        Image iconDel = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/delete.png"));
        itemDel.setImage(iconDel);
        itemDel.setToolTipText("删除");
        itemDel.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    deleteFiles();
                }
            }
        });

        itemOpen = new ToolItem(toolBar, SWT.PUSH);
        Image iconOpen = new Image(display, ClassLoader.getSystemResourceAsStream("icons/open.png"));
        itemOpen.setImage(iconOpen);
        itemOpen.setToolTipText("打开目录");
        itemOpen.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    try {
                        if (lastItem[0] != null) {
                            TreeItem root = RobotTreeUtil.getRoot(lastItem[0]);
                            if (root != null) {
                                String relativePath = RobotTreeUtil.getPathFromTree(lastItem[0]);
                                String os = System.getProperty("os.name").toLowerCase();

                                if (os.contains("windows")) {
                                    int index = relativePath.indexOf("/");
                                    if (index >= 0) {
                                        String node = relativePath.substring(index + 1,
                                            relativePath.length());
                                        Runtime.getRuntime().exec(
                                            "cmd /c start " + projectPath + "\\" + node);
                                    }

                                }
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        itemCheckPoint = new ToolItem(toolBar, SWT.PUSH);
        Image iconCheckPoint = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/checkpoint.png"));
        itemCheckPoint.setImage(iconCheckPoint);
        itemCheckPoint.setToolTipText("修改比对信息");

        itemCheckPoint.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    if (lastItem[0] != null) {
                        if (lastItem[0].getText().toLowerCase().contains(".png")) {
                            setCheckPointOnPic();
                        }
                    }
                }
            }
        });

        itemImportLog = new ToolItem(toolBar, SWT.PUSH);
        itemImportLog.setEnabled(true);
        Image iconImportLog = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/log.png"));
        itemImportLog.setImage(iconImportLog);
        itemImportLog.setToolTipText("查看日志");
        itemImportLog.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    openLogSystem();
                }
            }
        });

        itemDeployment = new ToolItem(toolBar, SWT.PUSH);
        itemDeployment.setEnabled(true);
        Image iconDeployment = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/deployment.png"));
        itemDeployment.setImage(iconDeployment);
        itemDeployment.setToolTipText("部署APK");
        itemDeployment.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    //		        	FileDialog dialog = new FileDialog (shell, SWT.MULTI | SWT.OPEN);
                    //		    		dialog.setFilterNames (new String [] {"apk Files (*.apk)"});
                    //		    		dialog.setFilterExtensions (new String [] {"*.apk*"}); //Windows wild cards
                    //		    		dialog.setFilterPath (".\\workspace"); //Windows path
                    //		    		String choice = dialog.open();
                    //		    		if(choice != null){
                    //		    			String filePath = dialog.getFilterPath();
                    //		    			String[] selectedFiles = dialog.getFileNames();
                    //		    			System.out.println(filePath + " " + selectedFiles[0]);
                    //		    		}
                }
            }
        });

        itemMemInfo = new ToolItem(toolBar, SWT.PUSH);
        itemMemInfo.setEnabled(true);
        Image iconMemInfo = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/meminfo.png"));
        itemMemInfo.setImage(iconMemInfo);
        itemMemInfo.setToolTipText("监控内存PSS");
        itemMemInfo.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    MemInfo memInfo = new MemInfo(shell, vecSerialNumber, SWT.CLOSE);
                    memInfo.open();
                }
            }
        });

        itemFPSInfo = new ToolItem(toolBar, SWT.PUSH);
        itemFPSInfo.setEnabled(true);
        Image iconFPSInfo = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/fps.png"));
        itemFPSInfo.setImage(iconFPSInfo);
        itemFPSInfo.setToolTipText("监控FPS");
        itemFPSInfo.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    FpsInfo fpsInfo = new FpsInfo(shell, vecSerialNumber, SWT.CLOSE);
                    fpsInfo.open();
                }
            }
        });

        itemCPUInfo = new ToolItem(toolBar, SWT.PUSH);
        itemCPUInfo.setEnabled(true);
        Image iconCPUInfo = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/cpu.png"));
        itemCPUInfo.setImage(iconCPUInfo);
        itemCPUInfo.setToolTipText("监控CPU信息");
        itemCPUInfo.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    CpuInfo cpuInfo = new CpuInfo(shell, vecSerialNumber, SWT.CLOSE);
                    cpuInfo.open();
                }
            }
        });

        itemPerformance = new ToolItem(toolBar, SWT.PUSH);
        itemPerformance.setEnabled(true);
        Image iconPerformance = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/power.png"));
        itemPerformance.setImage(iconPerformance);
        itemPerformance.setToolTipText("电量消耗(mAh)");
        itemPerformance.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {

                }
            }
        });

        itemFPS = new ToolItem(toolBar, SWT.PUSH);
        itemFPS.setEnabled(true);
        Image iconFPS = new Image(display, ClassLoader.getSystemResourceAsStream("icons/frame.png"));
        itemFPS.setImage(iconFPS);
        itemFPS.setToolTipText("FPS(ms)");
        itemFPS.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {

                }
            }
        });

        itemTraffic = new ToolItem(toolBar, SWT.PUSH);
        itemTraffic.setEnabled(true);
        Image iconTraffic = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/traffic.png"));
        itemTraffic.setImage(iconTraffic);
        itemTraffic.setToolTipText("流量消耗(KB)");
        itemTraffic.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {

                }
            }
        });

        itemMonkeyTest = new ToolItem(toolBar, SWT.PUSH);
        itemMonkeyTest.setEnabled(true);
        Image iconMonkeyTest = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/monkey.png"));
        itemMonkeyTest.setImage(iconMonkeyTest);
        itemMonkeyTest.setToolTipText("Monkey Test");
        itemMonkeyTest.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    Monkey monkey = new Monkey(shell, vecSerialNumber, SWT.CLOSE);
                    monkey.open();
                }
            }
        });

        itemLogcat = new ToolItem(toolBar, SWT.PUSH);
        itemLogcat.setEnabled(true);
        Image iconLogcat = new Image(display,
            ClassLoader.getSystemResourceAsStream("icons/nodesview.png"));
        itemLogcat.setImage(iconLogcat);
        itemLogcat.setToolTipText("视图分析");
        itemLogcat.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    try {
                        Runtime.getRuntime().exec(
                            System.getProperty("user.dir") + File.separator
                                    + "uiautomatorviewer.bat");
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
            }
        });

        itemTextbox = new ToolItem(toolBar, SWT.SEPARATOR);
        itemTextbox.setEnabled(true);
        final Text textCommand = new Text(toolBar, SWT.BORDER | SWT.SINGLE);
        textCommand.setText("单命令调试窗口");
        textCommand.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        textCommand.pack();

        textCommand.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == 13) {
                    e.doit = true;
                    if (client != null) {
                        AndroidDriver driver = new AndroidDriver(client);
                        driver.executeCommand(textCommand.getText());
                        textCommand.setText("");
                    } else
                        showNotification("请在录制模式下调试命令!", SWT.ICON_WARNING | SWT.YES);
                }
            }
        });

        textCommand.addFocusListener(new org.eclipse.swt.events.FocusAdapter() {
            public void focusLost(org.eclipse.swt.events.FocusEvent e) {
                if (textCommand.getText().trim().equals("")) {
                    textCommand.setText("单命令调试窗口");
                    textCommand.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
                }
            }

            public void focusGained(org.eclipse.swt.events.FocusEvent e) {
                if (textCommand.getText().trim().equals("单命令调试窗口")) {
                    textCommand.setText("");
                    textCommand.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                }
            }
        });
        itemTextbox.setWidth(219); //textCommand.getSize().x * 3
        System.out.println(textCommand.getSize().x * 3);
        itemTextbox.setControl(textCommand);

        //        itemNodesView = new ToolItem(toolBar, SWT.PUSH);
        //        itemNodesView.setEnabled(true);
        //        Image iconNodesView = new Image(display, "./icons/nodesview.png");
        //        itemNodesView.setImage(iconNodesView);
        //        itemNodesView.setToolTipText("Nodes View");
        //        itemNodesView.addListener(SWT.Selection, new Listener() {
        //            public void handleEvent(Event event) {
        //                if (event.detail == 0) {
        //                    NodeViewer nodeViewer = new NodeViewer(shell, SWT.CLOSE, findDevices.getDevices());
        //                    nodeViewer.open();
        //                }
        //            }
        //        });
        coolItemPrj.setControl(toolBar);

        Control controlPrj = coolItemPrj.getControl();
        Point ptPrj = controlPrj.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        ptPrj = coolItemPrj.computeSize(ptPrj.x, ptPrj.y);
        coolItemPrj.setSize(ptPrj);
        coolBarPrj.pack();

    }

    //Create Project
    private static void createProject(String name) {
        File outfile = new File(name);
        TreeItem treeNode = null;

        try {
            //create ****.androidrobot
            String projectFile = ProjectUtil.createProject(outfile.getPath(), outfile.getName());

            //create fold
            if (outfile.mkdir()) {
                lastItem[0] = treeNode;

                //create tasks.dat
                outfile = new File(name + "/screen_area.xml");
                outfile.createNewFile();

                StringBuffer sb = new StringBuffer();
                sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>").append(
                    "\n");
                sb.append("<screen>").append("\n");
                sb.append("</screen>").append("\n");
                OutputStream out = null;
                try {
                    out = new FileOutputStream(outfile);
                    out.write(sb.toString().getBytes());
                } catch (Exception ex) {

                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                outfile = new File(name + "/tasks_database.xml");
                outfile.createNewFile();

                sb = new StringBuffer();
                sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>").append(
                    "\n");
                sb.append("<tasks>").append("\n");
                sb.append("</tasks>").append("\n");
                try {
                    out = new FileOutputStream(outfile);
                    out.write(sb.toString().getBytes());
                } catch (Exception ex) {

                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                outfile = new File(name + "/Logs");
                outfile.mkdir();

                outfile = new File(name + "/Pictures");
                outfile.mkdir();

                outfile = new File(name + "/Scripts");
                outfile.mkdir();

                outfile = new File(name + "/Library");
                outfile.mkdir();

                openProject(projectFile);
            }
        } catch (Exception ex) {

        }

    }

    private static void createProject() {
        NewProject newPrj = new NewProject(shell, SWT.CLOSE, ".\\workspace");
        String choice = newPrj.open();

        if (choice != null && !choice.equals(""))
            createProject(choice);
    }

    //Create Script
    private static void createScript() {
        TreeItem root = RobotTreeUtil.getRoot(lastItem[0]);
        if (root != null) {
            String projectFile = (String) root.getData("path");
            String absolutPath = projectFile.substring(0, projectFile.lastIndexOf("\\") + 1);
            String relativePath = RobotTreeUtil.getPathFromTree(lastItem[0]);
            //String newFile = ".\\workspace\\"+RobotTreeUtil.getPathFromTree(lastItem [0]);
            NewScript nScript = new NewScript(shell, SWT.CLOSE, absolutPath + relativePath);
            String choice = nScript.open();
            if (choice != null && !choice.trim().equals("")) {
                if (!choice.trim().toLowerCase().endsWith(".py"))
                    choice += ".py";
                if (FileUtility.createFile(choice)) {
                    String folderPath = choice.substring(0, choice.lastIndexOf("\\"));

                    TreeItem[] tis = lastItem[0].getItems();

                    for (int i = 0; i < tis.length; i++) {
                        tis[i].dispose();
                    }

                    FileUtility.loadScripts(lastItem[0], display, absolutPath + relativePath);

                    String name = choice.substring(choice.lastIndexOf("\\") + 1, choice.length());
                    //String path = projectName+"\\"+scriptName+"\\"+name;
                    CTabItem tabItem = addTabItem(name, choice, DisplayUtil.Script.Create);
                    htTab.put(choice, tabItem);
                }
            }
        }
    }

    private static void createFolder() {
        String textNode = lastItem[0].getText();
        if (textNode != null) {
            TreeItem folder = new TreeItem(lastItem[0], SWT.NONE);
            folder.setText("New Folder");
            folder.setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/folder.png")));
            TreeItem root = RobotTreeUtil.getRoot(folder);
            String projectFile = (String) root.getData("path");
            String absolutPath = projectFile.substring(0, projectFile.lastIndexOf("\\") + 1);
            String relativePath = RobotTreeUtil.getPathFromTree(folder);
            
            File file = new File(absolutPath + relativePath);
            file.mkdirs();
        }
    }

    private static CTabItem addTabItem(final String name, String path, DisplayUtil.Script script) {
        final CTabItem tabItem = new CTabItem(tabContent, SWT.Close | SWT.MULTI | SWT.V_SCROLL);
        tabItem.setText(name);
        tabItem.setData(name, path);
        tabItem.setToolTipText(path);

        //########################################################
        final TextViewer textViewer = new TextViewer(tabContent, SWT.BORDER | SWT.MULTI
                                                                 | SWT.H_SCROLL | SWT.V_SCROLL);
        textViewer.setDocument(new Document());
        TextViewerUndoManager undoManager;
        //20是保存记录的数量。
        undoManager = new TextViewerUndoManager(20);
        //绑定对textViewer控件的数据进行管理
        undoManager.connect(textViewer);
        textViewer.setUndoManager(undoManager);
        final StyledText text_1 = textViewer.getTextWidget();
        //########################################################
        //final StyledText text_1 = new StyledText(tabContent,
        //		SWT.BORDER|SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL);
        tabItem.setControl(text_1);
        text_1.setFont(new Font(display, "Courier New", 10, SWT.NONE));
        text_1.setTabs(4);
        //text_1.setLeftMargin(10);
        //text_1.setMarginColor(display.getSystemColor (SWT.COLOR_GRAY));
        //Open File
        if (script == DisplayUtil.Script.Create)
            FileUtility.readFileByLines(path, text_1);
        else if (script == DisplayUtil.Script.Read)
            FileUtility.readFileByLines(path, text_1);
        tabItem.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/script.png")));

        text_1.addListener(SWT.Modify, new Listener() {
            public void handleEvent(Event event) {
                //tabItem.setText(name);
                if (!tabItem.getText().contains("*"))
                    tabItem.setText("*" + name);
            }
        });

        text_1.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.stateMask == SWT.CTRL && e.keyCode == 's') {
                    saveScript();
                } else if (e.stateMask == SWT.CTRL && e.keyCode == 'a') {
                    text_1.selectAll();
                } else if (e.stateMask == SWT.CTRL && e.keyCode == 'f') {
                    CTabItem ti = getCurrentTab();
                    StyledText st = (StyledText) ti.getControl();
                    Find_Replace find_Replace = Find_Replace.newInstance(shell);
                    find_Replace.setTextControl(st);
                    find_Replace.open();
                } else if (e.keyCode == SWT.F5) {
                    if (itemRun.getEnabled() == true) {
                        run();
                    } else {
                        showNotification("该设备正在运行状态!", SWT.ICON_WARNING | SWT.YES);
                    }
                } else if (e.keyCode == SWT.CR) {
                    int offset_01 = text_1.getCaretOffset() - 1;
                    int line = text_1.getLineAtOffset(offset_01);
                    String previousText = text_1.getLine(line);
                    String countSpace = getFrontSpace(previousText);
                    if (countSpace.length() > 0) {
                        text_1.insert(countSpace);

                        int offset = text_1.getCaretOffset();
                        text_1.setCaretOffset(offset + countSpace.length());

                    }
                } else if (e.stateMask == SWT.CTRL && (e.keyCode == 'z' || e.keyCode == 'Z')) {
                    textViewer.doOperation(ITextOperationTarget.UNDO);
                    if (!tabItem.getText().contains("*"))
                        tabItem.setText("*" + name);
                } else if (e.stateMask == SWT.CTRL && (e.keyCode == 'y' || e.keyCode == 'Y')) {
                    textViewer.doOperation(ITextOperationTarget.REDO);
                    if (!tabItem.getText().contains("*"))
                        tabItem.setText("*" + name);
                }
            }

            public void keyReleased(KeyEvent e) {
            }

        });

        text_1.addLineStyleListener(new PythonLineStyleListener(new ReadProperties().getKeyName(),
            shell));

        promot(text_1);
        //show it
        tabContent.setSelection(tabItem);
        return tabItem;
    }

    private static void openLogSystem() {
        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        dialog.setFilterNames(new String[] { "Log Files (*.arlog)" });
        dialog.setFilterExtensions(new String[] { "*.arlog*" }); //Windows wild cards
        dialog.setFilterPath(System.getProperty("user.dir") + "/workspace"); //Windows path
        String choice = dialog.open();//return value: path & null
        if (choice != null) {
            LogAnalysis logAnalysis = new LogAnalysis(shell, choice);
            logAnalysis.open();

        }
    }

    private static void openProject() {
        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        dialog.setFilterNames(new String[] { "Project Files (*.androidrobot)" });
        dialog.setFilterExtensions(new String[] { "*.androidrobot*" }); //Windows wild cards
        dialog.setFilterPath(System.getProperty("user.dir") + "/workspace"); //Windows path
        String choice = dialog.open();
        if (choice != null) {
            openProject(choice);
        }
    }

    private static void closeProject() {
        TreeItem[] tis = tree.getItems();
        for (int i = 0; i < tis.length; i++) {
            tis[i].dispose();
        }
        lastItem[0] = null;

        if (tasksTable != null) {
            TableItem[] tableItems = tasksTable.getItems();
            for (TableItem ti : tableItems)
                ti.dispose();
        }
    }

    public static void enableConnectButton() {
        mntmConnect.setEnabled(true);
    }

    //Create Menu bar
    private static void createMenu() {
        //Menu
        Menu menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);

        MenuItem mntmfile = new MenuItem(menu, SWT.CASCADE);
        mntmfile.setText("文件(&F)");

        Menu menu_1 = new Menu(mntmfile);
        mntmfile.setMenu(menu_1);

        MenuItem mntmnew = new MenuItem(menu_1, SWT.CASCADE);
        mntmnew.setText("新建");

        Menu menu_2 = new Menu(mntmnew);
        mntmnew.setMenu(menu_2);

        MenuItem mntmproject = new MenuItem(menu_2, SWT.NONE);
        mntmproject.setText("项目(P)");

        mntmproject.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                createProject();
            }
        });

        MenuItem mntmscript = new MenuItem(menu_2, SWT.NONE);
        mntmscript.setText("脚本(S)");
        mntmscript.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                createScript();
            }
        });

        MenuItem mntmimport = new MenuItem(menu_1, SWT.NONE);
        mntmimport.setText("导入工程(&Import)");
        mntmimport.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                openProject();
            }
        });

        MenuItem mntmclose = new MenuItem(menu_1, SWT.NONE);
        mntmclose.setText("关闭工程(&Close)");
        mntmclose.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                closeProject();
            }
        });

        MenuItem mntmsave = new MenuItem(menu_1, SWT.NONE);
        mntmsave.setText("保存(S&ave)");
        mntmsave.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                saveScript();
            }
        });

        MenuItem mntmexit = new MenuItem(menu_1, SWT.NONE);
        mntmexit.setText("退出(E&XIT)");

        mntmexit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (exitPrompt()) {
                    if (findDevices != null)
                        findDevices.stopThread();
                    if (adbGetDevice != null)
                        adbGetDevice.disconnect();

                    System.exit(0);
                }
            }
        });

        MenuItem mntmedit = new MenuItem(menu, SWT.CASCADE);
        mntmedit.setText("编辑(&E)");

        Menu menu_3 = new Menu(mntmedit);
        mntmedit.setMenu(menu_3);

        MenuItem mntmRefresh = new MenuItem(menu_3, SWT.PUSH);
        mntmRefresh.setText("刷新(&R)");

        mntmRefresh.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                fefresh();
            }
        });

        MenuItem mntmCopy = new MenuItem(menu_3, SWT.PUSH);
        mntmCopy.setText("复制(C&opy)");

        mntmCopy.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                //copy();
            }
        });

        MenuItem mntmPaste = new MenuItem(menu_3, SWT.PUSH);
        mntmPaste.setText("粘帖(&P)");

        mntmPaste.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                //paste();
            }
        });

        MenuItem mntmCut = new MenuItem(menu_3, SWT.PUSH);
        mntmCut.setText("剪切(&Cut)");

        mntmCut.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                //cut();
            }
        });

        MenuItem mntmDelete = new MenuItem(menu_3, SWT.PUSH);
        mntmDelete.setText("删除(&D)");

        MenuItem mntmrun = new MenuItem(menu, SWT.CASCADE);
        mntmrun.setText("系统管理(&Y)");

        Menu menu_4 = new Menu(mntmrun);
        mntmrun.setMenu(menu_4);

        MenuItem mntmRun = new MenuItem(menu_4, SWT.PUSH);
        mntmRun.setText("运行脚本(&Run)");

        mntmRun.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                run();
            }
        });

        MenuItem mntmRecord = new MenuItem(menu_4, SWT.CASCADE);
        mntmRecord.setText("录制(R&ecord)");

        Menu menu_emu = new Menu(mntmRecord);
        mntmRecord.setMenu(menu_emu);

        MenuItem mntmEmu = new MenuItem(menu_emu, SWT.NONE);
        mntmEmu.setText("模拟录制(E&mulator)");

        mntmEmu.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                try {
                    record();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        });

        MenuItem mntmMobile = new MenuItem(menu_emu, SWT.NONE);
        mntmMobile.setText("手机录制(Mo&bile)");
        mntmMobile.setEnabled(false);

        MenuItem mntmStop = new MenuItem(menu_4, SWT.PUSH);
        mntmStop.setText("停止(&S)");

        mntmStop.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                stop(false);
            }
        });

        MenuItem mntmSnap = new MenuItem(menu_4, SWT.PUSH);
        mntmSnap.setText("截取屏幕(S&nap)");

        mntmSnap.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                takeSnapShot();
            }
        });

        MenuItem mntmLog = new MenuItem(menu, SWT.CASCADE);
        mntmLog.setText("日志管理(&L)");

        Menu menuLog = new Menu(mntmLog);
        mntmLog.setMenu(menuLog);

        MenuItem mntmOpenLog = new MenuItem(menuLog, SWT.PUSH);
        mntmOpenLog.setText("查看日志(L&og)");

        mntmOpenLog.setEnabled(true);

        mntmOpenLog.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                openLogSystem();
            }
        });

        MenuItem mntmRemote = new MenuItem(menu, SWT.CASCADE);
        mntmRemote.setText("云端服务(&R)");

        Menu menu_remote = new Menu(mntmRemote);
        mntmRemote.setMenu(menu_remote);

        mntmConnect = new MenuItem(menu_remote, SWT.PUSH);
        mntmConnect.setText("连接服务器");

        mntmHandsetList = new MenuItem(menu_remote, SWT.PUSH);
        mntmHandsetList.setText("云端控制面板");
        mntmHandsetList.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            }
        });
        mntmHandsetList.setEnabled(false);

        mntmSubmitProject = new MenuItem(menu_remote, SWT.PUSH);
        mntmSubmitProject.setText("测试状态查询");
        mntmSubmitProject.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {

            }
        });
        mntmSubmitProject.setEnabled(false);

        MenuItem mntmhelp = new MenuItem(menu, SWT.CASCADE);
        mntmhelp.setText("帮助(&H)");

        Menu menu_5 = new Menu(mntmhelp);
        mntmhelp.setMenu(menu_5);

        MenuItem mntmContent = new MenuItem(menu_5, SWT.PUSH);
        mntmContent.setText("帮助文档(&D)");
        mntmContent.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                try {
                    Runtime.getRuntime().exec("cmd /c start .\\help");
                } catch (IOException exception) {
                    // TODO Auto-generated catch block
                    exception.printStackTrace();
                }
            }
        });

        MenuItem mntmAbout = new MenuItem(menu_5, SWT.PUSH);
        mntmAbout.setText("&About Android Robot");
        mntmAbout.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

                dialog.setText("About Android Robot");
                dialog.setImage(new Image(display, ClassLoader
                    .getSystemResourceAsStream("icons/title.png")));
                dialog.setLayout(new RowLayout());
                Text textHelp = new Text(dialog, SWT.BORDER | SWT.MULTI);
                textHelp.setFont(new Font(display, "宋体", 10, SWT.NONE));

                textHelp
                    .setText("About Android Robot\n\nVersion: 3.0 "
                             + "\nBuild id: 20140101-1635\n\n迈测 \nAndroid Robot Copyright@ 2011,2012迈测  All rights reserved");
                //dialog.setSize(new Point(300, 200));
                dialog.pack();
                dialog.open();

                // Move the dialog to the center of the top level shell.
                Rectangle shellBounds = shell.getBounds();
                Point dialogSize = dialog.getSize();

                dialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
                    shellBounds.y + (shellBounds.height - dialogSize.y) / 2);

            }
        });

        MenuItem mntmAboutUS = new MenuItem(menu_5, SWT.PUSH);
        mntmAboutUS.setText("关于我们");
        mntmAboutUS.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
                dialog.setText("关于我们");

                dialog.pack();
                dialog.open();

                // Move the dialog to the center of the top level shell.
                Rectangle shellBounds = shell.getBounds();
                Point dialogSize = dialog.getSize();

                dialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
                    shellBounds.y + (shellBounds.height - dialogSize.y) / 2);

            }
        });

    }

    private static void createLogTabFolder() {
        tabLogFolder = new CTabFolder(sashFormContent, SWT.BORDER);
        tabLogFolder.setTabHeight(20);
        tabLogFolder.marginHeight = 5;
        tabLogFolder.marginWidth = 5;
        tabLogFolder.setMaximizeVisible(true);
        tabLogFolder.setMinimizeVisible(true);
        tabLogFolder.setSimple(false);
        tabLogFolder.setUnselectedCloseVisible(true);

        showTestTab("测试用例");
        showLogTab("日志");
        showADBTab("ADB");
    }

    private static void showTestTab(String name) {
        createTable(name);

        //set content provider
        tableViewerCase.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements(Object element) {
                if (element instanceof ArrayList)
                    return ((ArrayList) element).toArray();
                else
                    return new Object[0];
            }

            public void dispose() {
            }

            public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            }
        });

        tableViewerCase.setLabelProvider(new ITableLabelProvider() {

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
                RowItem o = (RowItem) element;
                if (columnIndex == 0) {
                    return o.getSn();
                }

                if (columnIndex == 1) {
                    return o.getTaskName();
                }

                if (columnIndex == 2) {
                    return o.getCaseName();
                }

                if (columnIndex == 4) {
                    return o.getStartTime();
                }

                if (columnIndex == 5)
                    return o.getEndTime();

                if (columnIndex == 6)
                    return o.getResult();
                return "";
            }
        });

        tableViewerCase.setInput(listCase);
    }

    private static void createTable(String name) {
        String[] COLUMN_NAMES = { "设备名称", "任务名称", "用例", "开始时间", "结束时间", "进度", "成功/失败" };
        CTabItem tabItemLog = new CTabItem(tabLogFolder, SWT.NONE);
        tabItemLog.setText(name);
        tableViewerCase = new TableViewer(tabLogFolder, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);

        final Table tableShow = tableViewerCase.getTable();

        tabItemLog.setControl(tableShow);
        tableShow.setFont(new Font(display, "宋体", 10, SWT.NONE));
        tableShow.setLinesVisible(false);
        tableShow.setHeaderVisible(true);

        TableColumn columns_6 = new TableColumn(tableShow, SWT.LEFT);
        columns_6.setWidth(170);
        columns_6.setText(COLUMN_NAMES[0]);

        TableColumn columns_5 = new TableColumn(tableShow, SWT.LEFT);
        columns_5.setWidth(170);
        columns_5.setText(COLUMN_NAMES[1]);

        TableColumn columns_0 = new TableColumn(tableShow, SWT.LEFT);
        columns_0.setWidth(170);
        columns_0.setText(COLUMN_NAMES[2]);

        TableColumn columns_3 = new TableColumn(tableShow, SWT.LEFT);
        columns_3.setText(COLUMN_NAMES[5]);
        columns_3.setWidth(210);

        TableColumn columns_1 = new TableColumn(tableShow, SWT.LEFT);
        columns_1.setText(COLUMN_NAMES[3]);
        columns_1.setWidth(150);

        TableColumn columns_2 = new TableColumn(tableShow, SWT.LEFT);
        columns_2.setText(COLUMN_NAMES[4]);
        columns_2.setWidth(150);

        TableColumn columns_4 = new TableColumn(tableShow, SWT.LEFT);
        columns_4.setText(COLUMN_NAMES[6]);
        columns_4.setWidth(83);

        //show it
        tabLogFolder.setSelection(tabItemLog);
    }

    private static void showLogTab(String name) {
        CTabItem tabItemLog = new CTabItem(tabLogFolder, SWT.NONE);
        tabItemLog.setText(name);

        tabItemLog.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/log.png")));
        //add contorl
        listViewerLog = new ListViewer(tabLogFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
                                                     | SWT.H_SCROLL);
        tabItemLog.setControl(listViewerLog.getList());
        listViewerLog.getList().setFont(new Font(display, "宋体", 10, SWT.NONE));
        //set content provider
        listViewerLog.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements(Object inputElement) {
                Vector v = (Vector) inputElement;
                return v.toArray();
            }

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });

        listViewerLog.setInput(vectorLog);
    }

    private static void showADBTab(String name) {
        CTabItem tabItemLog = new CTabItem(tabLogFolder, SWT.NONE);
        tabItemLog.setText(name);
        //add contorl
        adbLogcatText = new Text(tabLogFolder, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tabItemLog.setControl(adbLogcatText);
        adbLogcatText.setFont(new Font(display, "宋体", 10, SWT.NONE));
        adbLogcatText.setEditable(false);
        adbLogcatText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    }

    private static void createMachineWindow() {
        tabHandsetName = new CTabFolder(sashFormProject2, SWT.NONE | SWT.BORDER);
        tabHandsetName.setTabHeight(20);
        tabHandsetName.marginHeight = 5;
        tabHandsetName.marginWidth = 5;
        tabHandsetName.setMaximizeVisible(true);
        tabHandsetName.setMinimizeVisible(true);
        tabHandsetName.setSimple(false);
        tabHandsetName.setUnselectedCloseVisible(true);
        addMachineTab("手机设备");
    }

    private static void createScriptTab() {
        tabContent = new CTabFolder(sashFormProgress, SWT.CLOSE | SWT.BORDER);
        tabContent.setTabHeight(20);
        tabContent.marginHeight = 5;
        tabContent.marginWidth = 5;
        tabContent.setMaximizeVisible(true);
        tabContent.setMinimizeVisible(true);
        tabContent.setSelectionBackground(new Color(display, new RGB(153, 186, 243)));
        tabContent.setSimple(false);
        tabContent.setUnselectedCloseVisible(true);

        tabContent.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    System.out.println("%#@%$@#%@#%$@#%%@#");
                }
            }
        });

        tabContent.addCTabFolder2Listener(new CTabFolder2Adapter() {
            public void close(CTabFolderEvent e) {
                CTabItem closingItem = (CTabItem) e.item;
                String tabName = closingItem.getText();
                //System.out.println("Close "+tabName+" "+closingItem.getData(tabName));
                if (!tabName.contains("*")) {
                    System.out.println("tabName:" + closingItem.getData(tabName));
                    htTab.remove(closingItem.getData(tabName));
                    htCaseViewer.remove(closingItem.getData(tabName));
                    htCaseList.remove(closingItem.getData(tabName));
                } else {
                    int choice = showNotification("是否保存?", SWT.ICON_QUESTION | SWT.YES | SWT.NO
                                                           | SWT.CANCEL);
                    if (choice == SWT.YES) {
                        saveScript();
                        htTab.remove(closingItem.getData(tabName.substring(1, tabName.length())));
                    } else if (choice == SWT.NO)
                        htTab.remove(closingItem.getData(tabName.substring(1, tabName.length())));
                    else if (choice == SWT.CANCEL)
                        e.doit = false;
                }

            }
        });
    }

    private static void addMachineTab(final String name) {
        final CTabItem tabItem = new CTabItem(tabHandsetName, SWT.MULTI | SWT.V_SCROLL);
        tabItem.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/device.png")));
        tabItem.setText(name);

        listHandsets = new List(tabHandsetName, SWT.BORDER);

        tabItem.setControl(listHandsets);
        listHandsets.setFont(new Font(display, "宋体", 10, SWT.NONE));
        listHandsets.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    //select handset
                    if (listHandsets.getSelectionIndex() >= 0) {
                        device = findDevices.getDevices()[listHandsets.getSelectionIndex()];
                        //get device node from tree
                        listHandsets.setMenu(null);
                        createHandsetRightClickRecord();
                        listHandsets.setMenu(handsetMenu);

                    }
                }
            }
        });
        //show it
        tabHandsetName.setSelection(tabItem);
    }

    private static void createProgressTab() {
        tabProgress = new CTabFolder(sashFormProgress, SWT.V_SCROLL | SWT.H_SCROLL);
        tabProgress.addCTabFolder2Listener(new CTabFolder2Adapter() {
            public void minimize(CTabFolderEvent event) {
                tabProgress.setMinimized(true);
                sashFormProgress.layout(true);
            }

            public void maximize(CTabFolderEvent event) {
                tabProgress.setMaximized(true);
                sashFormProgress.layout(true);
            }

            public void restore(CTabFolderEvent event) {
                tabProgress.setMinimized(false);
                tabProgress.setMaximized(false);
                sashFormProgress.layout(true);
            }
        });

        tabProgress.setTabHeight(20);
        tabProgress.marginHeight = 5;
        tabProgress.marginWidth = 5;
        tabProgress.setMaximizeVisible(false);
        tabProgress.setMinimizeVisible(false);
        tabProgress.setSimple(false);
        tabProgress.setUnselectedCloseVisible(true);

        addEmulator();

    }

    private static void addEmulator() {
        final CTabItem tabItemEmulator = new CTabItem(tabProgress, SWT.V_SCROLL | SWT.H_SCROLL);
        tabItemEmulator.setText("手机视图");
        tabItemEmulator.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/view.png")));

        addDisplay(tabItemEmulator);
        //show it
        tabProgress.setSelection(tabItemEmulator);
    }

    private static void addDisplay(CTabItem tabItemEmulator) {
        lbCapture = new Canvas(tabProgress, SWT.NO_BACKGROUND);

        //get scaled size
        scaledWidth = DisplayUtil.getScaledWidth();
        scaledHeight = DisplayUtil.getScaledHeight();

        lbCapture.setSize(scaledWidth, scaledHeight);
        gc = new GC(lbCapture);

        tabItemEmulator.setControl(lbCapture);
        lbCapture.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                PaletteData palette = new PaletteData(0, 0, 0);
                palette.colors = new RGB[] { new RGB(236, 233, 216), new RGB(236, 233, 216) };
                palette.isDirect = false;
                ImageData imageData = new ImageData(scaledWidth, scaledHeight, 1, palette);
                Image firstImage = new Image(Display.getDefault(), imageData);
                gc.drawImage(firstImage, 0, 0);
            }
        });

        lbCapture.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent event) {
                if (isTouchDownEvent == true && (itemBack.getEnabled() == true)) {
                    move(event);
                }
            }
        });

        lbCapture.addMouseListener(new MouseAdapter() {
            int startX = 0;
            int startY = 0;
            int endX   = 0;
            int endY   = 0;

            public void mouseDown(MouseEvent event) {
                //Left click
                if (event.button != 1)
                    return;

                if (isRecording && !isSetCheckPoint && (itemBack.getEnabled() == true))
                    touchDown(event);
                else if (isSetCheckPoint == true) {
                    //System.out.println(event.count);
                    startX = event.x;
                    startY = event.y;
                }
            }

            public void mouseUp(MouseEvent event) {
                //System.out.println("up");
                if (event.button != 1)
                    return;
                if (isRecording && !isSetCheckPoint && (itemBack.getEnabled() == true))
                    touchUp(event);
                else if (isRecording && isSetCheckPoint == true && (itemBack.getEnabled() == true)) {

                    endX = event.x;
                    endY = event.y;
                    //System.out.println(startX + " "+startY + " " + endX + " " + endY);
                    if (endY > startY) {
                        offsetX = endX - startX;
                        offsetY = endY - startY;
                        //GC gc = new GC(lbCapture);
                        gc.setLineWidth(3);
                        gc.setForeground(new Color(display, 255, 0, 0));
                        gc.drawRectangle(startX, startY, offsetX, offsetY);
                    } else {
                        int tempX, tempY;
                        tempX = startX;
                        tempY = startY;

                        startX = endX;
                        startY = endY;
                        endX = tempX;
                        endY = tempY;

                        offsetX = endX - startX;
                        offsetY = endY - startY;
                        gc = new GC(lbCapture);
                        gc.setLineWidth(3);
                        gc.setForeground(new Color(display, 255, 0, 0));
                        gc.drawRectangle(startX, startY, offsetX, offsetY);
                    }
                }
            }
        });
        lbCapture.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                if (isRecording && !isSetCheckPoint) {//isSetCheckPoint
                    itemSaveCP.setEnabled(true);
                    lbCapture.setMenu(recordMenu);
                } else if (isSetCheckPoint) {
                    System.out.println("isSetCheckPoint");
                    lbCapture.setMenu(null);
                    //itemSetCheckPoint_2.setEnabled(true);
                    itemSaveCP_2.setEnabled(true);
                    lbCapture.setMenu(recordMenu_2);
                } else {
                    System.out.println("null");
                    lbCapture.setMenu(null);
                }
            }
        });
    }

    public static void addCloudHandset(String name) {
        cloudHandsetsList.add(name);
    }

    private static void addRemoteHandsetsList() {
        final CTabItem cloudHandsetsTabItem = new CTabItem(tabHandsetName, SWT.V_SCROLL
                                                                           | SWT.H_SCROLL);
        cloudHandsetsTabItem.setText("云端设备");
        cloudHandsetsTabItem.setImage(new Image(display, ".\\icons\\view.png"));

        cloudHandsetsList = new List(tabHandsetName, SWT.BORDER);
        cloudHandsetsTabItem.setControl(cloudHandsetsList);
        cloudHandsetsList.setFont(new Font(display, "宋体", 10, SWT.NONE));
        //show it
    }

    private static void addProgressBar(int index, String key, int count) {
        Table table = tableViewerCase.getTable();
        TableItem ti = table.getItem(index);
        ProgressBar bar = new ProgressBar(table, SWT.HORIZONTAL | SWT.SMOOTH);
        bar.setSelection(0);
        bar.setMinimum(0);
        bar.setMaximum(count);
        TableEditor tabEditor = new TableEditor(table);
        tabEditor.grabHorizontal = editor.grabVertical = true;
        tabEditor.setEditor(bar, ti, 3);

        htProgress.put(key, bar);
    }

    private static void createStatusBar() {

        coolBar4 = new CoolBar(shell, SWT.NONE);
        FormData formDataConnServer = new FormData();
        formDataConnServer.left = new FormAttachment(80, 0);
        formDataConnServer.right = new FormAttachment(100, 0);
        formDataConnServer.top = new FormAttachment(100, -24);
        formDataConnServer.bottom = new FormAttachment(100, 0);
        coolBar4.setLayoutData(formDataConnServer);
        CoolItem coolItemConnServer = new CoolItem(coolBar4, SWT.NONE);
        ToolBar toolBarConnServer = new ToolBar(coolBar4, SWT.NONE);
        tiStatusBarConnectServer = new ToolItem(toolBarConnServer, SWT.NONE);
        tiStatusBarConnectServer.setText("已断开服务器连接");
        coolItemConnServer.setControl(toolBarConnServer);
        Control controlConnServer = coolItemConnServer.getControl();
        Point pt4 = controlConnServer.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        pt4 = controlConnServer.computeSize(pt4.x, pt4.y);
        coolItemConnServer.setSize(pt4);
        coolBar4.pack();

        coolBar3 = new CoolBar(shell, SWT.NONE);
        FormData formData3 = new FormData();
        formData3.left = new FormAttachment(60, 0);
        formData3.right = new FormAttachment(100, 0);
        formData3.top = new FormAttachment(100, -24);
        formData3.bottom = new FormAttachment(100, 0);
        coolBar3.setLayoutData(formData3);
        CoolItem coolItem3 = new CoolItem(coolBar3, SWT.NONE);
        ToolBar toolBar3 = new ToolBar(coolBar3, SWT.NONE);

        tiStatusBarConnect = new ToolItem(toolBar3, SWT.NONE);
        tiStatusBarConnect.setText("无设备连接");

        coolItem3.setControl(toolBar3);

        Control control3 = coolItem3.getControl();
        Point pt3 = control3.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        pt3 = coolItem3.computeSize(pt3.x, pt3.y);
        coolItem3.setSize(pt3);
        coolBar3.pack();

        /*add progressbar to tool*/
        pbRecorder = new ProgressBar(shell, SWT.HORIZONTAL | SWT.SMOOTH);
        FormData formData11 = new FormData();
        formData11.left = new FormAttachment(45, 0);
        formData11.right = new FormAttachment(60, 0);
        formData11.top = new FormAttachment(100, -24);
        formData11.bottom = new FormAttachment(100, 0);
        pbRecorder.setLayoutData(formData11);
        pbRecorder.setMaximum(0);
        pbRecorder.setMaximum(110);
        pbRecorder.setVisible(false);

        coolBar1 = new CoolBar(shell, SWT.NONE);
        FormData formData1 = new FormData();
        formData1.left = new FormAttachment(20, 0);
        formData1.right = new FormAttachment(100, 0);
        formData1.top = new FormAttachment(100, -24);
        formData1.bottom = new FormAttachment(100, 0);
        coolBar1.setLayoutData(formData1);
        CoolItem coolItem1 = new CoolItem(coolBar1, SWT.NONE);
        ToolBar toolBar1 = new ToolBar(coolBar1, SWT.NONE);

        tiStatusBarPass = new ToolItem(toolBar1, SWT.NONE);
        tiStatusBarFail = new ToolItem(toolBar1, SWT.NONE);
        tiStatusBarTotal = new ToolItem(toolBar1, SWT.NONE);

        tiStatusBarPass.setText("Pass:0");
        tiStatusBarFail.setText("Fail:0");
        tiStatusBarTotal.setText("Total:0");

        coolItem1.setControl(toolBar1);

        Control control = coolItem1.getControl();
        Point pt = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        pt = coolItem1.computeSize(pt.x, pt.y);
        coolItem1.setSize(pt);

        coolBar1.pack();

        coolBar2 = new CoolBar(shell, SWT.NONE);
        FormData formData2 = new FormData();
        formData2.left = new FormAttachment(0, 0);
        formData2.right = new FormAttachment(100, 0);
        formData2.top = new FormAttachment(100, -24);
        formData2.bottom = new FormAttachment(100, 0);
        coolBar2.setLayoutData(formData2);
        CoolItem coolItem2 = new CoolItem(coolBar2, SWT.NONE);
        ToolBar toolBar2 = new ToolBar(coolBar2, SWT.NONE);

        tiStatusBarScript = new ToolItem(toolBar2, SWT.NONE);
        tiStatusBarScript.setText(ServerUtil.getServerIp() + " : " + ServerUtil.getServerPort());
        coolItem2.setControl(toolBar2);

        Control control2 = coolItem2.getControl();
        Point pt2 = control2.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        pt2 = coolItem2.computeSize(pt2.x, pt2.y);
        coolItem2.setSize(pt2);

        coolBar2.pack();
    }

    public static void setButton(boolean setApk, boolean openValue, boolean newValue,
                                 boolean saveValue, boolean recValue, boolean delValue,
                                 boolean saveScreenValue, boolean stopValue, boolean debugValue,
                                 boolean runValue, boolean checkPoint, boolean importLog,
                                 boolean boolDeployment, boolean boolMemInfo, boolean boolCPUInfo,
                                 boolean boolFPSInfo, boolean boolStartUpTime,
                                 boolean boolMonkeyTest, boolean boolLogcat, boolean boolNodesView,
                                 boolean boolLoadScreen, boolean boolLeakInfo, boolean boolConsum,
                                 boolean boolItemBack, boolean boolItemSaveCP,
                                 boolean boolItemMenu, boolean boolItemHome, boolean boolItemInput) {
        itemSetApk.setEnabled(setApk);
        itemNew.setEnabled(newValue);
        itemSave.setEnabled(saveValue);

        //if(isRecording == false)
        //	itemRec.setEnabled(recValue);

        //System.out.println(isRecording);
        itemDel.setEnabled(delValue);
        itemOpen.setEnabled(openValue);
        itemSaveScreen.setEnabled(saveScreenValue);
        //itemDebug.setEnabled(debugValue);
        itemStop.setEnabled(stopValue);
        itemRun.setEnabled(runValue);
        itemCheckPoint.setEnabled(checkPoint);
        itemImportLog.setEnabled(importLog);
        itemDeployment.setEnabled(boolDeployment);
        itemMemInfo.setEnabled(boolMemInfo);
        itemCPUInfo.setEnabled(boolCPUInfo);
        itemFPSInfo.setEnabled(boolFPSInfo);
        itemMonkeyTest.setEnabled(boolMonkeyTest);
        itemLogcat.setEnabled(boolLogcat);
        itemBack.setEnabled(boolItemBack);
        itemSaveCP.setEnabled(boolItemSaveCP);
        itemMenu.setEnabled(boolItemMenu);
        itemHome.setEnabled(boolItemHome);
        itemInput.setEnabled(boolItemInput);
        //        itemNodesView.setEnabled(boolNodesView);
    }

    public static void enableRecord(boolean value) {
        isRecording = !value;
        //System.out.println(isRecording);
        itemRec.setEnabled(value);
    }

    private static int checkedCaseNum = 0;

    private static Vector<TestCase> getCheckedItems(final String projectName,
                                                    final Table casesTable, final String tabName) {
        Vector<TestCase> tcVector = new Vector();
        int itemCount = casesTable.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            TableItem ti = casesTable.getItem(i);
            boolean isChecked = ti.getChecked();
            if (isChecked == true) {
                TestCase tc = new TestCase();
                tc.name = htCaseList.get(tabName).get(i).getName();
                tc.project = projectName;
                tc.path = htCaseList.get(tabName).get(i).getPath();
                if (ti.getText(1).trim().equals(""))
                    tc.loop = 1;
                else
                    tc.loop = Integer.parseInt(ti.getText(1));

                if (ti.getText(2).trim().equals(""))
                    tc.individual = 1;
                else
                    tc.individual = Integer.parseInt(ti.getText(2));

                tc.unit = "秒";
                tc.isChecked = true;
                tcVector.add(tc);
                checkedCaseNum += 1;
            } else {
                TestCase tc = new TestCase();
                tc.name = htCaseList.get(tabName).get(i).getName();
                tc.project = projectName;
                tc.path = htCaseList.get(tabName).get(i).getPath();
                tc.loop = Integer.parseInt(ti.getText(1));
                tc.individual = Integer.parseInt(ti.getText(2));
                tc.unit = "秒";
                tc.isChecked = false;
                tcVector.add(tc);
            }
        }
        return tcVector;
    }

    private static void loadProjectInTaskTab(String path, Combo comboProject) {
        Vector<String> vecFolder = FileUtility.loadScriptsFolder(path);
        for (int k = 0; k < vecFolder.size(); k++) {
            comboProject.add(vecFolder.get(k));
        }
    }

    private static void loadItemInTaskTab(String path, Combo comboItem) {
        Vector<String> vecFolder = FileUtility.loadScriptsFolder(path);
        for (int k = 0; k < vecFolder.size(); k++) {
            comboItem.add(vecFolder.get(k));
        }
    }

    private static boolean removeScripts(Vector<String> vecBaseScripts, String script) {
        boolean bRet = false;
        for (int i = 0; i < vecBaseScripts.size(); i++) {
            if (vecBaseScripts.get(i).trim().equals(script)) {
                bRet = true;
                vecBaseScripts.remove(i);
                break;
            }
        }
        return bRet;
    }

    private static void initTask(Combo comboSolution, Combo comboProject, Combo comboItem,
                                 Table casesTable, String taskName) {

        TreeItem tiRoot = tree.getItem(0);
        if (tiRoot != null) {
            ArrayList<Task> tasksList = new ArrayList<Task>();
            try {
                tasksList = (ArrayList<Task>) TaskUtil.loadTask(taskFilePath);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            boolean isFind = false;

            //get task from task.dat
            Task task = null;
            for (int i = 0; i < tasksList.size(); i++) {
                Task tempTask = tasksList.get(i);
                if (tempTask.name.equals(taskName)) {

                    isFind = true;
                    task = tempTask;
                    break;
                }
            }

            String scriptPath = "";
            //set solution Combo
            if (isFind == true) {
                isFind = false;
                int solutionSize = comboSolution.getItemCount();
                for (int j = 0; j < solutionSize; j++) {
                    if (comboSolution.getItem(j).equals(task.solution)) {
                        comboSolution.select(j);

                        //load project in folder

                        scriptPath = projectPath + "/Scripts/";
                        loadProjectInTaskTab(scriptPath, comboProject);

                        isFind = true;
                        break;
                    }
                }
            }

            //set project Combo
            if (isFind == true && !task.project.equals("")) {
                isFind = false;
                int projectSize = comboProject.getItemCount();
                for (int j = 0; j < projectSize; j++) {
                    if (comboProject.getItem(j).equals(task.project)) {
                        comboProject.select(j);

                        scriptPath = scriptPath + comboProject.getText().trim() + "/";
                        loadItemInTaskTab(scriptPath, comboItem);

                        isFind = true;
                        break;
                    }
                }
            }

            //set item Combo
            if (isFind == true && !task.item.equals("")) {
                isFind = false;
                int itemSize = comboItem.getItemCount();
                for (int j = 0; j < itemSize; j++) {
                    if (comboItem.getItem(j).equals(task.item)) {
                        comboItem.select(j);
                        scriptPath = scriptPath + comboItem.getText().trim() + "/";
                        isFind = true;
                        break;
                    }
                }
            }

            //insert new scripts
            if (isFind == true) {
                Vector<String> vecBaseScripts = new Vector();
                try {
                    System.out.println("###################################");
                    //Scripts from disk
                    FileUtility.getScripts(new File(scriptPath), vecBaseScripts, projectPath);

                    //add from task.dat
                    for (int i = 0; i < task.vecTC.size(); i++) {
                        System.out.println("" + task.vecTC.get(i).path);
                        if (removeScripts(vecBaseScripts, task.vecTC.get(i).path)) {
                            TaskRowItem rowItem = new TaskRowItem();
                            rowItem.setPath(task.vecTC.get(i).path);
                            String name = task.vecTC.get(i).name;
                            rowItem.setName(name);
                            rowItem.setLoop(Integer.toString(task.vecTC.get(i).loop));

                            rowItem.setChecked(task.vecTC.get(i).isChecked);

                            //add row to table
                            htCaseList.get(taskName).add(rowItem);
                        }
                    }

                    //add rear
                    for (int i = 0; i < vecBaseScripts.size(); i++) {
                        TaskRowItem rowItem = new TaskRowItem();
                        rowItem.setPath(vecBaseScripts.get(i));
                        String name = vecBaseScripts.get(i).substring(
                            vecBaseScripts.get(i).lastIndexOf("\\") + 1,
                            vecBaseScripts.get(i).length());
                        rowItem.setName(name);
                        //add row to table
                        htCaseList.get(taskName).add(rowItem);
                    }

                    htCaseViewer.get(taskName).refresh(false);

                    //set enabled case
                    int caseSize = casesTable.getItemCount();
                    for (int k = 0; k < caseSize; k++) {
                        TableItem ti = casesTable.getItem(k);
                        //不能去掉这一行,否则setChecked后会不显示数据
                        String temp = ti.getText(0);

                        if (htCaseList.get(taskName).get(k).getChecked()) {
                            ti.setChecked(true);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static int checkNode(TreeItem node) {
        if (node == null)
            return -1;

        TreeItem root = RobotTreeUtil.getRoot(node);
        if (root == node) {
            return 0;//root
        } else if (root == node.getParentItem()) {
            if (node.getText().trim().equals("Logs")) {
                return 1;//Logs
            } else if (node.getText().trim().equals("Pictures")) {
                return 2;//Pictures
            } else if (node.getText().trim().equals("Scripts")) {
                return 3;//Scripts
            } else if (node.getText().trim().equals("Library")) {
                return 8;//Library
            } else {
                return 9;//unknown
            }
        } else if (node.getParentItem().getText().trim().equals("Devices")) {
            return 7;//Devices
        } else {
            //String path = RobotTreeUtil.getPathFromTree(node);
            String projectFile = (String) RobotTreeUtil.getRoot(node).getData("path");
            String absolutPath = projectFile.substring(0, projectFile.lastIndexOf("\\") + 1);
            String relativePath = RobotTreeUtil.getPathFromTree(node);

            if (new File(absolutPath + relativePath).isDirectory()) {
                if (RobotTreeUtil.findScriptsNode(node)) {
                    return 6;//Scripts child
                } else
                    return 4;//fold
            } else {
                return 5;//file
            }
        }
    }

    private static void loadScripts(final String tabName, final Vector<String> vecScripts) {
        //System.out.println(vecScripts.size());
        if (vecScripts != null && vecScripts.size() != 0) {
            //remove old record
            //taskRowList.clear();
            //casesTableViewer.refresh(false);
            //add new record
            for (int k = 0; k < vecScripts.size(); k++) {
                TaskRowItem rowItem = new TaskRowItem();
                int index = vecScripts.get(k).indexOf(projectPath);
                String relativePath = vecScripts.get(k).substring(projectPath.length(),
                    vecScripts.get(k).length());
                rowItem.setPath(relativePath);
                //相对路径
                String name = vecScripts.get(k).substring(vecScripts.get(k).lastIndexOf("\\") + 1,
                    vecScripts.get(k).length());
                rowItem.setName(name);
                //add row to table
                htCaseList.get(tabName).add(rowItem);
            }
            htCaseViewer.get(tabName).refresh(false);
        } else {
            //remove old record
            System.out.print("remove all fdsafsa");
            htCaseList.get(tabName).clear();
            htCaseViewer.get(tabName).refresh(false);
        }
    }

    //insert test case from index to position
    private static void ins2Table(Table table, int position, int index) {
        if (index > position) {
            TaskRowItem currTask = taskRowList.get(index);
            boolean currChecked = table.getItem(index).getChecked();
            for (int i = index; i > position; i--) {
                TaskRowItem preTask = taskRowList.get(i - 1);
                boolean preChecked = table.getItem(i - 1).getChecked();

                taskRowList.set(i, preTask);
                taskRowList.get(i).setChecked(preChecked);

                table.getItem(i).setText(0, preTask.getName());
                table.getItem(i).setText(1, preTask.getLoop());
                table.getItem(i).setText(2, preTask.getInterval());
                table.getItem(i).setText(3, preTask.getUnit());
                table.getItem(i).setChecked(preChecked);
            }

            taskRowList.set(position, currTask);
            taskRowList.get(position).setChecked(currChecked);

            table.getItem(position).setText(0, currTask.getName());
            table.getItem(position).setText(1, currTask.getLoop());
            table.getItem(position).setText(2, currTask.getInterval());
            table.getItem(position).setText(3, currTask.getUnit());
            table.getItem(position).setChecked(currChecked);

            table.setFocus();
            table.select(position);

        } else {
            TaskRowItem currTask = taskRowList.get(index);
            boolean currChecked = table.getItem(index).getChecked();

            for (int i = index; i < position; i++) {
                TaskRowItem preTask = taskRowList.get(i + 1);
                boolean preChecked = table.getItem(i + 1).getChecked();

                taskRowList.set(i, preTask);
                taskRowList.get(i).setChecked(preChecked);

                table.getItem(i).setText(0, preTask.getName());
                table.getItem(i).setText(1, preTask.getLoop());
                table.getItem(i).setText(2, preTask.getInterval());
                table.getItem(i).setText(3, preTask.getUnit());
                table.getItem(i).setChecked(preChecked);
            }

            taskRowList.set(position, currTask);
            taskRowList.get(position).setChecked(currChecked);

            table.getItem(position).setText(0, currTask.getName());
            table.getItem(position).setText(1, currTask.getLoop());
            table.getItem(position).setText(2, currTask.getInterval());
            table.getItem(position).setText(3, currTask.getUnit());
            table.getItem(position).setChecked(currChecked);

            table.setFocus();
            table.select(position);
        }
    }

    private static void newTask(final String name) {
        final CTabItem tabItem = new CTabItem(tabContent, SWT.Close | SWT.MULTI | SWT.V_SCROLL);
        taskRowList = new ArrayList();
        tabItem.setText(name);
        tabItem.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/config.png")));
        tabItem.setData(name, name);
        htTab.put(name, tabItem);

        Composite newTaskComp = new Composite(tabContent, SWT.NONE);
        newTaskComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout gLayout = new GridLayout(1, true);
        newTaskComp.setLayout(gLayout);
        tabItem.setControl(newTaskComp);

        Composite projectComp = new Composite(newTaskComp, SWT.NONE);
        GridData suGridData = new GridData(GridData.FILL_HORIZONTAL);
        suGridData.heightHint = 85;
        projectComp.setLayoutData(suGridData);
        projectComp.setLayout(new GridLayout(1, true));

        Group group = new Group(projectComp, SWT.NONE);
        group.setText("项目区域");
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        group.setLayout(new GridLayout(3, true));
        new Label(group, SWT.NULL).setText("解决方案：");
        new Label(group, SWT.NULL).setText("项目名称：");
        new Label(group, SWT.NULL).setText("测试分类：");
        final Combo comboSolution = new Combo(group, SWT.READ_ONLY);
        comboSolution.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Combo comboProject = new Combo(group, SWT.READ_ONLY);
        comboProject.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Combo comboItem = new Combo(group, SWT.READ_ONLY);
        comboItem.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        //add project name
        /*
        if(vecProject != null){
            for(int i=0;i<vecProject.size();i++){
            	prjCombo.add(vecProject.get(i).getName());
            }
        }*/
        if (tree.getItemCount() > 0) {
            for (int i = 0; i < tree.getItemCount(); i++) {
                comboSolution.add(tree.getItem(i).getText());
            }
        }

        comboSolution.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //remove all scripts
                int length = htCaseList.get(name).size();
                for (int i = 0; i < length; i++) {
                    htCaseList.get(name).remove(0);
                }
                htCaseViewer.get(name).refresh(false);
                //load script by project then show it in table
                //loadScripts(name);
                String scriptPath = projectPath + "\\Scripts\\";
                Vector<String> vecScripts = TaskUtil.loadScriptsInFolder(scriptPath);
                loadScripts(name, vecScripts);
                //display case number
                lblTotal.setText("          总用例数:" + vecScripts.size() + "条");

                comboProject.removeAll();
                //load project folder in script
                if (!comboSolution.getText().trim().equals("")) {
                    Vector<String> vecFolder = TaskUtil.loadScriptsFolder(scriptPath);
                    for (int i = 0; i < vecFolder.size(); i++) {
                        comboProject.add(vecFolder.get(i));
                    }
                }
                comboItem.removeAll();
            }
        });

        comboProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //remove all scripts
                int length = htCaseList.get(name).size();
                for (int i = 0; i < length; i++) {
                    htCaseList.get(name).remove(0);
                }
                htCaseViewer.get(name).refresh(false);

                //load all scripts by folder
                String scriptPath = projectPath + comboSolution.getText().trim() + "\\Scripts\\"
                                    + comboProject.getText().trim() + "\\";
                Vector<String> vecScripts = new Vector();
                try {
                    FileUtility.getScripts(
                        new File(projectPath + "\\Scripts\\" + comboProject.getText() + "\\"),
                        vecScripts, projectPath);
                    loadScripts(name, vecScripts);
                    //display case number
                    lblTotal.setText("          总用例数:" + vecScripts.size() + "条");

                    //load 测试分类
                    comboItem.removeAll();
                    if (!comboSolution.getText().trim().equals("")) {
                        Vector<String> vecFolder = TaskUtil.loadScriptsFolder(scriptPath);
                        for (int i = 0; i < vecFolder.size(); i++) {
                            comboItem.add(vecFolder.get(i));
                        }
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        comboItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //remove all scripts
                int length = htCaseList.get(name).size();
                for (int i = 0; i < length; i++) {
                    htCaseList.get(name).remove(0);
                }
                htCaseViewer.get(name).refresh(false);

                //load all scripts by folder
                String scriptPath = projectPath + "\\Scripts\\" + comboProject.getText() + "\\"
                                    + comboItem.getText().trim() + "\\";

                Vector<String> vecScripts = new Vector();
                try {
                    TaskUtil.getScripts(new File(scriptPath), vecScripts);
                    loadScripts(name, vecScripts);
                    //display case number
                    lblTotal.setText("          总用例数:" + vecScripts.size() + "条");
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        //prjCombo.select(0);

        Composite caseComp = new Composite(newTaskComp, SWT.NONE);
        GridData caseGridData = new GridData(GridData.FILL_HORIZONTAL);
        caseGridData.heightHint = 280;
        caseComp.setLayoutData(caseGridData);
        caseComp.setLayout(new GridLayout(1, true));

        TableViewer casesTableViewer = new TableViewer(caseComp, SWT.CHECK | SWT.V_SCROLL
                                                                 | SWT.BORDER | SWT.FULL_SELECTION
                                                                 | SWT.VIRTUAL);

        htCaseViewer.put(name, casesTableViewer);
        htCaseList.put(name, taskRowList);

        final Table casesTable = casesTableViewer.getTable();
        casesTable.setLinesVisible(true);
        casesTable.setHeaderVisible(true);

        caseEditor = new TableEditor(casesTable);
        caseEditor.horizontalAlignment = SWT.LEFT;
        caseEditor.grabHorizontal = true;

        casesTable.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                Control c = caseEditor.getEditor();
                if (c != null) {
                    c.dispose();
                }

                Point point = new Point(e.x, e.y);
                final TableItem tableitem = casesTable.getItem(point);

                if (tableitem != null) {
                    // 得到选中的列
                    int column = -1;
                    for (int i = 0; i < casesTable.getColumnCount(); i++) {
                        Rectangle rec = tableitem.getBounds(i);
                        if (rec.contains(point))
                            column = i;
                    }

                    final int col1 = column;
                    int line1 = casesTable.getSelectionIndex();

                    if (col1 == 1) {
                        final Text texteditor = new Text(casesTable, SWT.NONE);

                        texteditor.computeSize(SWT.DEFAULT, casesTable.getItemHeight());

                        caseEditor.grabHorizontal = true;
                        caseEditor.minimumHeight = texteditor.getSize().y;
                        caseEditor.minimumWidth = texteditor.getSize().x;
                        caseEditor.setEditor(texteditor, tableitem, column);

                        texteditor.setFont(new Font(display, "宋体", 10, SWT.ITALIC));
                        texteditor.setText(tableitem.getText(column));
                        texteditor.forceFocus();

                        texteditor.addModifyListener(new ModifyListener() {
                            //开始编辑的事件
                            public void modifyText(ModifyEvent event) {
                                Text text = (Text) caseEditor.getEditor();
                                text.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
                                caseEditor.getItem().setText(col1, text.getText());
                            }
                        });

                        texteditor.addFocusListener(new org.eclipse.swt.events.FocusAdapter() {
                            public void focusLost(org.eclipse.swt.events.FocusEvent e) {
                                Control c = caseEditor.getEditor();
                                if (c != null) {
                                    c.dispose();
                                }
                            }
                        });
                    }

                    //open scripts
                    if (col1 == 0) {
                        String scriptPath = htCaseList.get(name).get(line1).getPath();
                        if (!htTab.containsKey(projectPath + scriptPath)) {
                            CTabItem tabItem = addTabItem(tableitem.getText(col1), projectPath
                                                                                   + scriptPath,
                                DisplayUtil.Script.Read);
                            htTab.put(projectPath + scriptPath, tabItem);
                        } else {
                            tabContent.setSelection((CTabItem) htTab.get(projectPath + scriptPath));
                        }
                    }
                }
            }

            public void mouseDown(MouseEvent e) {
                //System.out.println(casesTable.getSelectionIndex());
                lblSelected.setText("          当前第" + casesTable.getSelectionIndex() + "条");
                if (moveTableItem != null)
                    moveTableItem.close();
                if (e.button == 3) {
                    //Text text = new Text(casesTable,SWT.NONE);
                    Point point = new Point(e.x, e.y);
                    TableItem item = casesTable.getItem(point);
                    if (item != null) {
                        moveTableItem = new MoveTableItem(shell, SWT.NONE);
                        moveTableItem.setLocation(MouseInfo.getPointerInfo().getLocation().x,
                            MouseInfo.getPointerInfo().getLocation().y);
                        int line = moveTableItem.open();
                        if (line != -1 && line < casesTable.getItemCount())
                            ins2Table(casesTable, line, casesTable.getSelectionIndex());
                    }
                    //text.setMenu(menu);
                    //Text text = new Text(menu,SWT.NONE);
                    //text.setMenu(menu);
                    //text.setText("12345");
                }
            }

        });
        //.
        GridData casesGridData = new GridData(GridData.FILL_BOTH);
        casesTable.setLayoutData(casesGridData);

        String[] COLUMN_NAMES = { "测试用例", "运行次数", "时间间隔", "间隔单位" };

        TableColumn columns_0 = new TableColumn(casesTable, SWT.NONE);
        columns_0.setWidth(200);
        columns_0.setText(COLUMN_NAMES[0]);

        TableColumn columns_1 = new TableColumn(casesTable, SWT.NONE);
        columns_1.setWidth(70);
        columns_1.setText(COLUMN_NAMES[1]);

        TableColumn columns_2 = new TableColumn(casesTable, SWT.NONE);
        columns_2.setWidth(70);
        columns_2.setText(COLUMN_NAMES[2]);

        TableColumn columns_3 = new TableColumn(casesTable, SWT.NONE);
        columns_3.setWidth(70);
        columns_3.setText(COLUMN_NAMES[3]);

        casesTableViewer.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements(Object element) {
                if (element instanceof ArrayList)
                    return ((ArrayList) element).toArray();
                else
                    return new Object[0];
            }

            public void dispose() {
            }

            public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            }
        });

        casesTableViewer.setLabelProvider(new ITableLabelProvider() {

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
                TaskRowItem o = (TaskRowItem) element;
                if (columnIndex == 0) {
                    return o.getName();
                }

                if (columnIndex == 1) {
                    return o.getLoop();
                }

                if (columnIndex == 2)
                    return o.getInterval();

                if (columnIndex == 3)
                    return o.getUnit();
                return "";
            }
        });

        //		for(int i=0;i<taskRowList.size();i++) {
        //			System.out.println(taskRowList.get(i).getName());
        //		}

        casesTableViewer.setInput(taskRowList);

        Composite mixComp = new Composite(newTaskComp, SWT.NONE);
        GridData mixGridData = new GridData(GridData.FILL_BOTH);
        mixComp.setLayoutData(mixGridData);
        mixComp.setLayout(new GridLayout(2, true));

        Composite bottomComp = new Composite(mixComp, SWT.NONE);
        GridData bottomGridData = new GridData();
        bottomGridData.widthHint = 500;
        bottomGridData.heightHint = 40;
        bottomComp.setLayoutData(bottomGridData);
        bottomComp.setLayout(new GridLayout(6, true));

        Button btnConfirm = new Button(bottomComp, SWT.NONE);
        btnConfirm.setLayoutData(new GridData(GridData.FILL_BOTH));
        btnConfirm.setText("保存");
        btnConfirm.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    checkedCaseNum = 0;
                    //Get project name
                    Task task = new Task();
                    task.name = name;
                    task.solution = comboSolution.getText();
                    task.project = comboProject.getText();
                    task.item = comboItem.getText();
                    task.vecTC = getCheckedItems(task.solution, casesTable, name);

                    try {

                        TaskUtil.updateTask(projectPath + "/tasks_database.xml", task);
                        //remove from CTabItem
                        if (htTab.containsKey(name)) {
                            ((CTabItem) htTab.get(name)).dispose();
                            htTab.remove(name);
                        }

                        //update display in Task UI
                        TableItem[] tableItem = tasksTable.getItems();
                        for (int i = 0; i < tableItem.length; i++) {
                            String strTask = tableItem[i].getText(0);
                            if (strTask.equals(name)) {
                                tableItem[i].setText(1, "已选中" + checkedCaseNum + "条用例");
                            }
                        }
                        //=====================================task;
                        tabItem.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showNotification("更新任务失败", SWT.ICON_WARNING | SWT.OK);
                    }

                }
            }
        });
        Button btnCancel = new Button(bottomComp, SWT.NONE);
        btnCancel.setLayoutData(new GridData(GridData.FILL_BOTH));
        btnCancel.setText("取消");
        btnCancel.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    tabItem.dispose();
                    //remove from CTabItem
                    if (htTab.containsKey(name)) {
                        ((CTabItem) htTab.get(name)).dispose();
                        htTab.remove(name);
                    }
                }
            }
        });

        Button btnUp = new Button(bottomComp, SWT.NONE);
        btnUp.setLayoutData(new GridData(GridData.FILL_BOTH));
        btnUp.setText("向上");
        btnUp.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    int index = casesTable.getSelectionIndex();
                    if (index - 1 >= 0) {
                        TaskRowItem currTask = taskRowList.get(index);
                        boolean currChecked = casesTable.getItem(index).getChecked();

                        TaskRowItem preTask = taskRowList.get(index - 1);
                        boolean preChecked = casesTable.getItem(index - 1).getChecked();

                        taskRowList.set(index - 1, currTask);
                        taskRowList.get(index - 1).setChecked(currChecked);

                        casesTable.getItem(index - 1).setText(0, currTask.getName());
                        casesTable.getItem(index - 1).setText(1, currTask.getLoop());
                        casesTable.getItem(index - 1).setText(2, currTask.getInterval());
                        casesTable.getItem(index - 1).setText(3, currTask.getUnit());
                        casesTable.getItem(index - 1).setChecked(currChecked);

                        taskRowList.set(index, preTask);
                        taskRowList.get(index).setChecked(preChecked);

                        casesTable.getItem(index).setText(0, preTask.getName());
                        casesTable.getItem(index).setText(1, preTask.getLoop());
                        casesTable.getItem(index).setText(2, preTask.getInterval());
                        casesTable.getItem(index).setText(3, preTask.getUnit());
                        casesTable.getItem(index).setChecked(preChecked);

                        //TableViewer tvCases = htCaseViewer.get(name);
                        //tvCases.refresh(false);

                        casesTable.setFocus();
                        casesTable.select(index - 1);
                        //casesTable.getItem(index-1).setChecked(currChecked);
                        //casesTable.getItem(index).setChecked(currChecked);
                    }
                }
            }
        });

        Button btnDown = new Button(bottomComp, SWT.NONE);
        btnDown.setLayoutData(new GridData(GridData.FILL_BOTH));
        btnDown.setText("向下");
        btnDown.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    int index = casesTable.getSelectionIndex();
                    if (index >= 0 && index + 1 < casesTable.getItemCount()) {
                        TaskRowItem currTask = taskRowList.get(index);
                        boolean currChecked = casesTable.getItem(index).getChecked();

                        TaskRowItem preTask = taskRowList.get(index + 1);
                        boolean preChecked = casesTable.getItem(index + 1).getChecked();

                        taskRowList.set(index + 1, currTask);
                        taskRowList.get(index + 1).setChecked(currChecked);

                        casesTable.getItem(index + 1).setText(0, currTask.getName());
                        casesTable.getItem(index + 1).setText(1, currTask.getLoop());
                        casesTable.getItem(index + 1).setText(2, currTask.getInterval());
                        casesTable.getItem(index + 1).setText(3, currTask.getUnit());
                        casesTable.getItem(index + 1).setChecked(currChecked);

                        taskRowList.set(index, preTask);
                        taskRowList.get(index).setChecked(preChecked);

                        casesTable.getItem(index).setText(0, preTask.getName());
                        casesTable.getItem(index).setText(1, preTask.getLoop());
                        casesTable.getItem(index).setText(2, preTask.getInterval());
                        casesTable.getItem(index).setText(3, preTask.getUnit());
                        casesTable.getItem(index).setChecked(preChecked);

                        //TableViewer tvCases = htCaseViewer.get(name);
                        //tvCases.refresh(false);

                        casesTable.setFocus();
                        casesTable.select(index + 1);
                        //casesTable.getItem(index-1).setChecked(currChecked);
                        //casesTable.getItem(index).setChecked(currChecked);
                    }
                }
            }
        });

        Button btnSelAll = new Button(bottomComp, SWT.NONE);
        btnSelAll.setLayoutData(new GridData(GridData.FILL_BOTH));
        btnSelAll.setText("全选");
        btnSelAll.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    selectAllCases(name);
                }
            }
        });

        Button btnDeSelAll = new Button(bottomComp, SWT.NONE);
        btnDeSelAll.setLayoutData(new GridData(GridData.FILL_BOTH));
        btnDeSelAll.setText("反选");
        btnDeSelAll.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    deSelectAllCases(name);
                }
            }
        });

        Composite labelComp = new Composite(mixComp, SWT.NONE);
        GridData labelGridData = new GridData();
        labelGridData.widthHint = 200;
        labelComp.setLayoutData(labelGridData);
        labelComp.setLayout(new GridLayout(1, true));

        lblTotal = new Label(labelComp, SWT.NONE);
        lblTotal.setLayoutData(new GridData(GridData.FILL_BOTH));

        lblSelected = new Label(labelComp, SWT.NONE);
        lblSelected.setLayoutData(new GridData(GridData.FILL_BOTH));

        initTask(comboSolution, comboProject, comboItem, casesTable, name);

        //display case number
        lblTotal.setText("          总用例数:" + taskRowList.size() + "条");
        lblSelected.setText("          当前第0条");
        tabContent.setSelection(tabItem);
    }

    private static void selectAllCases(String name) {
        TableViewer tvCases = htCaseViewer.get(name);
        if (tvCases != null) {
            Table table = tvCases.getTable();
            int length = table.getItemCount();
            for (int i = 0; i < length; i++) {
                TableItem ti1 = table.getItem(i);
                ti1.setChecked(true);
            }
        }
    }

    private static void deSelectAllCases(String name) {
        TableViewer tvCases = htCaseViewer.get(name);
        if (tvCases != null) {
            Table table = tvCases.getTable();
            int length = table.getItemCount();
            for (int i = 0; i < length; i++) {
                TableItem ti1 = table.getItem(i);
                ti1.setChecked(false);
            }
        }
    }

    private static void newTask(Event e) {
        //create a table item
        final TableItem tiTask = new TableItem(tasksTable, SWT.NONE);

        Control c = taskEditor.getEditor();
        if (c != null) {
            c.dispose();
        }

        // 得到选中的行
        //Point point = new Point(e.x, e.y);
        final int tasksCount = tasksTable.getItemCount();
        final TableItem tableitem = tasksTable.getItem(tasksCount - 1);
        if (tableitem != null) {
            final Text texteditor = new Text(tasksTable, SWT.NONE);

            texteditor.computeSize(SWT.DEFAULT, tasksTable.getItemHeight());

            taskEditor.grabHorizontal = true;
            taskEditor.minimumHeight = texteditor.getSize().y;
            taskEditor.minimumWidth = texteditor.getSize().x;
            taskEditor.setEditor(texteditor, tableitem, 0);

            texteditor.setFont(new Font(display, "宋体", 10, SWT.ITALIC));
            texteditor.setText(tableitem.getText(0).trim());
            texteditor.forceFocus();

            texteditor.addModifyListener(new ModifyListener() {
                //开始编辑的事件
                public void modifyText(ModifyEvent event) {
                    Text text = (Text) taskEditor.getEditor();
                    text.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
                    taskEditor.getItem().setText(0, text.getText().trim());
                }
            });

            texteditor.addFocusListener(new org.eclipse.swt.events.FocusAdapter() {
                public void focusLost(org.eclipse.swt.events.FocusEvent e) {
                    //System.out.println(tableitem.getText(col1));
                    Control c = taskEditor.getEditor();
                    int count = tasksTable.getItemCount();
                    //htTab
                    for (int i = 0; i < count - 1; i++) {
                        TableItem ti = tasksTable.getItem(i);
                        if (ti.getText(0).trim().equals(((Text) c).getText().trim())) {
                            showNotification("此任务已经存在!", SWT.ICON_WARNING | SWT.YES);
                            ((Text) c).setFocus();
                            //tasksTable.setSelection(count-1);
                            return;
                        }
                    }

                    tiTask.setText(2, "1");

                    newTask(((Text) c).getText().trim());

                    if (c != null) {
                        c.dispose();
                    }

                }
            });
        }
    }

    private static void addTasksTab() {
        tabItemTasks = new CTabItem(tabFolder, SWT.TOP | SWT.MULTI | SWT.V_SCROLL);
        tabItemTasks.setText("任务管理");
        //tabItem.setFont(new Font(display,"宋体",10,SWT.NONE));
        tabItemTasks.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/config.png")));

        // create the composite for ToolBar
        final Composite toolComp = new Composite(tabFolder, SWT.NONE);
        toolComp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
        GridLayout gLayout = new GridLayout(1, true);
        gLayout.marginTop = 0;
        gLayout.marginBottom = 0;
        toolComp.setLayout(gLayout);

        ToolBar toolBar = new ToolBar(toolComp, SWT.FLAT | SWT.WRAP | SWT.LEFT);
        GridData suGridData = new GridData(GridData.FILL_HORIZONTAL);
        suGridData.heightHint = 23;
        toolBar.setLayoutData(suGridData);
        //new task
        ToolItem newTaskToolItem = new ToolItem(toolBar, SWT.PUSH);
        newTaskToolItem.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/newTask.png")));//
        newTaskToolItem.setToolTipText("新建任务");
        newTaskToolItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    newTask(event);
                }
            }
        });

        //remove task
        ToolItem removeToolItem = new ToolItem(toolBar, SWT.PUSH);
        removeToolItem.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/removeTask.png")));//
        removeToolItem.setToolTipText("移除任务");
        removeToolItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    int selTask = tasksTable.getSelectionIndex();
                    if (selTask >= 0) {
                        String taskName = tasksTable.getItem(selTask).getText(0);
                        //remove from tasks.dat
                        try {
                            TaskUtil.removeTask(projectPath + "/tasks_database.xml", taskName);
                            //remove from CTabItem
                            if (htTab.containsKey(taskName)) {
                                ((CTabItem) htTab.get(taskName)).dispose();
                                htTab.remove(taskName);
                            }
                            //remove from TaskTable
                            tasksTable.getItem(selTask).dispose();
                        } catch (Exception e) {
                            log.error("[remove task]" + e);
                        }

                    }
                }
            }
        });

        //up
        ToolItem upToolItem = new ToolItem(toolBar, SWT.PUSH);
        upToolItem.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/up.png")));//
        upToolItem.setToolTipText("向上移动");
        upToolItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    int selTask = tasksTable.getSelectionIndex();
                    if (selTask > 0) {
                        String taskName = tasksTable.getItem(selTask).getText(0);
                        String taskDescription = tasksTable.getItem(selTask).getText(1);
                        String taskNum = tasksTable.getItem(selTask).getText(2);

                        tasksTable.getItem(selTask).setText(0,
                            tasksTable.getItem(selTask - 1).getText(0));
                        tasksTable.getItem(selTask).setText(1,
                            tasksTable.getItem(selTask - 1).getText(1));
                        tasksTable.getItem(selTask).setText(2,
                            tasksTable.getItem(selTask - 1).getText(2));

                        tasksTable.getItem(selTask - 1).setText(0, taskName);
                        tasksTable.getItem(selTask - 1).setText(1, taskDescription);
                        tasksTable.getItem(selTask - 1).setText(2, taskNum);

                        tasksTable.select(selTask - 1);

                        if (!tabItemTasks.getText().startsWith("*"))
                            tabItemTasks.setText("*" + tabItemTasks.getText());
                    }
                }
            }
        });

        //down
        ToolItem downToolItem = new ToolItem(toolBar, SWT.PUSH);
        downToolItem.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/down.png")));//
        downToolItem.setToolTipText("向下移动");
        downToolItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    int selTask = tasksTable.getSelectionIndex();
                    if (selTask < tasksTable.getItemCount() - 1) {
                        String taskName = tasksTable.getItem(selTask).getText(0);
                        String taskDescription = tasksTable.getItem(selTask).getText(1);
                        String taskNum = tasksTable.getItem(selTask).getText(2);

                        tasksTable.getItem(selTask).setText(0,
                            tasksTable.getItem(selTask + 1).getText(0));
                        tasksTable.getItem(selTask).setText(1,
                            tasksTable.getItem(selTask + 1).getText(1));
                        tasksTable.getItem(selTask).setText(2,
                            tasksTable.getItem(selTask + 1).getText(2));

                        tasksTable.getItem(selTask + 1).setText(0, taskName);
                        tasksTable.getItem(selTask + 1).setText(1, taskDescription);
                        tasksTable.getItem(selTask + 1).setText(2, taskNum);

                        tasksTable.select(selTask + 1);

                        if (!tabItemTasks.getText().startsWith("*"))
                            tabItemTasks.setText("*" + tabItemTasks.getText());

                    }
                }
            }
        });

        //save
        ToolItem saveToolItem = new ToolItem(toolBar, SWT.PUSH);
        saveToolItem.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/save_task.png")));//
        saveToolItem.setToolTipText("保存");
        saveToolItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    String tabName = tabItemTasks.getText();
                    if (tabName.startsWith("*")) {
                        ArrayList<Task> tasksList = null;
                        try {
                            tasksList = (ArrayList<Task>) TaskUtil.loadTask(taskFilePath);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        ArrayList<Task> tempTaskList = new ArrayList();

                        //exchange
                        for (int i = 0; i < tasksTable.getItemCount(); i++) {
                            String taskName = tasksTable.getItem(i).getText(0).trim();
                            for (int j = 0; j < tasksList.size(); j++) {
                                if (tasksList.get(j).name.equals(taskName)) {
                                    Task task = tasksList.get(j);
                                    task.loop = Integer.parseInt(tasksTable.getItem(i).getText(2)
                                        .trim());
                                    tempTaskList.add(task);
                                    break;
                                }
                            }
                        }

                        //save
                        for (int i = 0; i < tempTaskList.size(); i++)
                            try {
                                TaskUtil.updateTask(projectPath + "/tasks_database.xml",
                                    tempTaskList.get(i));
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        tabItemTasks.setText(tabName.substring(1, tabName.length()));
                    }
                }
            }
        });

        tasksTable = new Table(toolComp, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.VIRTUAL);
        tasksTable.setLinesVisible(true);
        suGridData = new GridData(GridData.FILL_BOTH);
        tasksTable.setLayoutData(suGridData);

        TableColumn columns_0 = new TableColumn(tasksTable, SWT.NONE);
        columns_0.setWidth(150);

        TableColumn columns_1 = new TableColumn(tasksTable, SWT.NONE);
        columns_1.setWidth(110);

        TableColumn columns_2 = new TableColumn(tasksTable, SWT.NONE);
        columns_2.setWidth(50);

        taskEditor = new TableEditor(tasksTable);
        taskEditor.horizontalAlignment = SWT.LEFT;
        taskEditor.grabHorizontal = true;

        taskNumEditor = new TableEditor(tasksTable);
        taskNumEditor.horizontalAlignment = SWT.LEFT;
        taskNumEditor.grabHorizontal = true;

        tasksTable.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                Control c = taskNumEditor.getEditor();
                if (c != null) {
                    c.dispose();
                }

                Point point = new Point(e.x, e.y);
                final TableItem tableitem = tasksTable.getItem(point);

                if (tableitem != null) {
                    // 得到选中的列
                    int column = -1;
                    for (int i = 0; i < tasksTable.getColumnCount(); i++) {
                        Rectangle rec = tableitem.getBounds(i);
                        if (rec.contains(point))
                            column = i;
                    }
                    //System.out.println(column);
                    final int col1 = column;
                    if (col1 == 2) {
                        final Text textnum = new Text(tasksTable, SWT.NONE);
                        textnum.computeSize(SWT.DEFAULT, tasksTable.getItemHeight());
                        taskNumEditor.grabHorizontal = true;
                        taskNumEditor.minimumHeight = textnum.getSize().y;
                        taskNumEditor.minimumWidth = textnum.getSize().x;
                        taskNumEditor.setEditor(textnum, tableitem, 2);

                        textnum.setFont(new Font(display, "宋体", 10, SWT.ITALIC));
                        textnum.setText(tableitem.getText(2));
                        textnum.forceFocus();

                        textnum.addModifyListener(new ModifyListener() {
                            //开始编辑的事件
                            public void modifyText(ModifyEvent event) {
                                Text text = (Text) taskNumEditor.getEditor();
                                text.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
                                taskNumEditor.getItem().setText(2, text.getText());
                            }
                        });

                        textnum.addFocusListener(new org.eclipse.swt.events.FocusAdapter() {
                            public void focusLost(org.eclipse.swt.events.FocusEvent e) {
                                Control c = taskNumEditor.getEditor();
                                if (c != null) {
                                    c.dispose();
                                }
                                //set tab as edit status
                                if (!tabItemTasks.getText().startsWith("*"))
                                    tabItemTasks.setText("*" + tabItemTasks.getText());
                            }
                        });
                    }

                    //open scripts
                    if (col1 == 0 || col1 == 1) {
                        if (!htTab.containsKey(tableitem.getText())) {
                            newTask(tableitem.getText());

                        } else {

                            tabContent.setSelection((CTabItem) htTab.get(tableitem.getText()));

                        }
                    }
                }
            }
        });

        tabItemTasks.setControl(toolComp);

    }

    //Create SashForm
    public static void createSashForm() {
        sashFormProject = new SashForm(shell, SWT.HORIZONTAL);
        formData = new FormData();
        formData.left = new FormAttachment(0, 3);
        formData.right = new FormAttachment(100, -3);
        formData.top = new FormAttachment(0, 25);
        formData.bottom = new FormAttachment(100, -25);
        sashFormProject.setLayoutData(formData);
        //CreateProjectExplorer();
        createSashForm1();
        //CreateTabFolder();
        createSashForm2();
        //CreateSashForm1();
        sashFormProject.setWeights(new int[] { 1, 3 });
    }

    public static void createSashForm1() {
        sashFormProject2 = new SashForm(sashFormProject, SWT.VERTICAL);
        createProjectExplorer();
        createMachineWindow();
        sashFormProject2.setWeights(new int[] { 3, 1 });
    }

    public static void createSashForm2() {
        sashFormContent = new SashForm(sashFormProject, SWT.VERTICAL);
        createSashForm3();
        createLogTabFolder();
        sashFormContent.setWeights(new int[] { 3, 1 });
    }

    public static void createSashForm3() {
        sashFormProgress = new SashForm(sashFormContent, SWT.HORIZONTAL);
        createScriptTab();
        createProgressTab();
        sashFormProgress.setWeights(new int[] { 2, 1 });
    }

    static void checkParent(TreeItem parent, boolean checked, boolean grayed) {
        if (parent == null)//递归退出条件：父亲为空。
            return;
        for (TreeItem child : parent.getItems()) {
            if (child.getGrayed() || checked != child.getChecked()) {
                //1，子节点有一个为【部分选中的】，直接设置父节点为【部分选中的】。
                //2，子节点不完全相同，说明【部分选中的】。
                checked = grayed = true;
                break;
            }
        }
        parent.setChecked(checked);
        parent.setGrayed(grayed);
        checkParent(parent.getParentItem(), checked, grayed);
    }

    static void checkChildren(TreeItem[] children, boolean checked) {
        if (children.length == 0)//递归退出条件：孩子为空。
            return;
        for (TreeItem child : children) {
            child.setGrayed(false);//必须设置这个，因为本来节点可能【部分选中的】。
            child.setChecked(checked);
            checkChildren(child.getItems(), checked);
        }
    }

    public static void changeName() {
        String textNode = lastItem[0].getText();

        if (textNode != null) {

            boolean showBorder = true;
            black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
            treeComp = new Composite(tree, SWT.NONE);
            if (showBorder)
                treeComp.setBackground(black);
            final Text textTreeNode = new Text(treeComp, SWT.NONE);
            final int inset = showBorder ? 1 : 0;
            treeComp.addListener(SWT.Resize, new Listener() {
                public void handleEvent(Event e) {
                    Rectangle rect = treeComp.getClientArea();
                    textTreeNode.setBounds(rect.x + inset, rect.y + inset, rect.width - inset * 2,
                        rect.height - inset * 2);
                }
            });

            //String newText = textTreeNode.getText ();
            //String leftText = newText.substring (0, e.start);
            //String rightText = newText.substring (e.end, newText.length ());
            GC gc = new GC(textTreeNode);
            Point size = gc.textExtent(textNode);
            gc.dispose();

            size = textTreeNode.computeSize(0, SWT.DEFAULT);
            editor.horizontalAlignment = SWT.LEFT;
            Rectangle itemRect = lastItem[0].getBounds(), rect = tree.getClientArea();
            editor.minimumWidth = Math.max(size.x, itemRect.width) + 1 * 2;
            int left = itemRect.x, right = rect.x + rect.width;
            editor.minimumWidth = Math.min(editor.minimumWidth, right - left);
            editor.minimumHeight = size.y + 1 * 2;
            editor.layout();

            editor.setEditor(treeComp, lastItem[0]);

            textListener = new Listener() {
                public void handleEvent(final Event e) {
                    //System.out.println("textListener"+e.type);
                    switch (e.type) {
                        case SWT.FocusOut:
                            //System.out.println("FocusOut");
                            lastItem[0].setText(textTreeNode.getText());
                            changeName(lastItem[0], lastSelectNode[0], textTreeNode.getText());
                            //System.out.println("FocusOut"+textTreeNode.getText ());
                            treeComp.dispose();
                            break;
                        case SWT.Verify:
                            //System.out.println("Verify");
                            break;
                        case SWT.Traverse:
                            switch (e.detail) {
                                case SWT.TRAVERSE_RETURN:
                                    //System.out.println("TRAVERSE_RETURN");
                                    lastItem[0].setText(textTreeNode.getText());
                                    changeName(lastItem[0], lastSelectNode[0],
                                        textTreeNode.getText());
                                    //FALL THROUGH
                                case SWT.TRAVERSE_ESCAPE:
                                    //System.out.println("TRAVERSE_ESCAPE");
                                    treeComp.dispose();
                                    e.doit = false;
                            }
                            break;
                        default:
                            System.out.println("e.type" + e.type);
                    }
                }
            };

            textTreeNode.addListener(SWT.FocusOut, textListener);
            textTreeNode.addListener(SWT.Traverse, textListener);
            textTreeNode.addListener(SWT.Verify, textListener);
            //editor.
            //textTreeNode.setText (lastItem[0].getText ());
            //textTreeNode.selectAll ();
            textTreeNode.setText(textNode);
            textTreeNode.selectAll();
            textTreeNode.setFocus();
        }
    }

    private static void loadProjects() {
        try {
            String projectFilePath = new File(ProjectUtil.getCurrentProject()).getCanonicalPath();
            File project = new File(projectFilePath);
            if (projectFilePath != null && !projectFilePath.equals("") && project.exists()
                && project.isFile())
                openProject(projectFilePath);
        } catch (IOException e) {
            log.error(e);
        }

    }

    /**
     * Create Project tree
     */
    public static void openProject(String prjName) {
        try {
            TreeItem root = ProjectUtil.importProject(prjName, tree, display);

            RobotTreeUtil.initDevices(root, display, findDevices.getDevices());

            root.setData("path", prjName);
            if (prjName.endsWith(".androidrobot")) {
                projectPath = new File(prjName.substring(0, prjName.lastIndexOf(".androidrobot")))
                    .getCanonicalPath();
                workspacePath = projectPath.substring(0,
                    projectPath.lastIndexOf(File.separator + "workspace" + File.separator));
                //set path for checkpoint system
                PropertiesUtil.append(workspacePath + File.separator + "system.properties",
                    "ProjectPath", prjName, "");

                FileUtility.loadLogsByProject(root, display, projectPath);
                FileUtility.loadPicturesByProject(root, display, projectPath);
                FileUtility.loadScriptsByProject(root, display, projectPath);
                FileUtility.loadScriptsByLibrary(root, display, projectPath);

                File xmlPath = new File(projectPath + "/tasks_database.xml");
                //load task
                taskFilePath = xmlPath.getCanonicalPath();

                if (!xmlPath.exists())
                    return;

                ArrayList<Task> tasksList = (ArrayList<Task>) TaskUtil.loadTask(taskFilePath);

                tasksTable.setToolTipText(taskFilePath);

                for (int i = 0; i < tasksList.size(); i++) {
                    Task task = tasksList.get(i);
                    TableItem tiTask = new TableItem(tasksTable, SWT.NONE);
                    tiTask.setText(0, task.name);

                    int checkedNum = 0;
                    //find checked case
                    for (int j = 0; j < task.vecTC.size(); j++) {
                        if (task.vecTC.get(j).isChecked == true)
                            checkedNum += 1;
                    }
                    tiTask.setText(1, "已选中" + checkedNum + "条用例");
                    tiTask.setText(2, String.valueOf(task.loop));
                }
            }

        } catch (Exception ex) {
            log.error(ex);
        }
    }

    /**
     * Create Project Explorer
     */
    public static void createProjectExplorer() {
        tabFolder = new CTabFolder(sashFormProject2, SWT.NONE | SWT.BORDER);
        tabFolder.setTabHeight(20);
        tabFolder.marginHeight = 0;
        tabFolder.marginWidth = 0;
        tabFolder.setMaximizeVisible(true);
        tabFolder.setMinimizeVisible(true);
        //GridData gd_tabFolder = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        //GridData gd_tabFolder = new GridData(SWT.NONE, SWT.NONE, true, true, 1, 1);
        //tabFolder.setLayoutData(gd_tabFolder);
        tabFolder.setLayout(new FillLayout());
        tabFolder.setBounds(5, 5, 200, 465);
        tabFolder.setSimple(false);
        tabFolder.setUnselectedCloseVisible(true);

        //Create TabItem
        CTabItem item = new CTabItem(tabFolder, SWT.NONE | SWT.MULTI | SWT.V_SCROLL);
        tabFolder.setSelection(item);
        item.setText("项目浏览");
        item.setImage(new Image(display, ClassLoader
            .getSystemResourceAsStream("icons/workspace.png")));
        composite = new Composite(tabFolder, SWT.NONE);
        composite.setLayout(new GridLayout());
        tree = new Tree(composite, SWT.BORDER);//SWT.CHECK |
        black = display.getSystemColor(SWT.COLOR_BLACK);
        item.setControl(composite);
        //tree.setHeaderVisible(true);
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        //tree.setBounds(5, 5, 180, 430);

        lastItem = new TreeItem[1];
        editor = new TreeEditor(tree);
        lastSelectNode = new String[1];

        tree.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                //System.out.println(":" + event.index);
                final TreeItem item = (TreeItem) event.item;
                lastItem[0] = item;
                lastSelectNode[0] = item.getText();

            }
        });

        //double click
        tree.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                Point point = new Point(event.x, event.y);
                TreeItem item = tree.getItem(point);
                if (item != null
                    && lastItem[0] != null
                    && (lastItem[0].getText().trim().toLowerCase().endsWith(".png")
                        || lastItem[0].getText().trim().toLowerCase().endsWith(".py") || lastItem[0]
                        .getText().trim().toLowerCase().endsWith(".arlog"))) {
                    //System.out.println ("Mouse down: " + item);
                    String name = item.getText();
                    TreeItem root = RobotTreeUtil.getRoot(item);
                    String projectFile = (String) root.getData("path");
                    String absolutPath = projectFile.substring(0, projectFile.lastIndexOf("\\") + 1);
                    String relativePath = RobotTreeUtil.getPathFromTree(item);

                    String path = absolutPath + relativePath;
                    if (name.contains(".py")) {
                        if (!htTab.containsKey(path)) {
                            //String rootName = item.getParentItem().getParentItem().getText();

                            CTabItem tabItem = addTabItem(name, path, DisplayUtil.Script.Read);
                            htTab.put(path, tabItem);

                        } else {
                            tabContent.setSelection((CTabItem) htTab.get(path));
                        }
                    } else if (name.contains(".png")) {
                        setCheckPointOnPic();
                    } else if (name.contains(".arlog")) {
                        String arlog_path = path;
                        LogAnalysis logAnalysis = new LogAnalysis(shell, arlog_path);
                        logAnalysis.open();
                    }
                }
            }
        });

        tree.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                switch (checkNode(lastItem[0])) {
                    case -1:
                        tree.setMenu(null);
                        createImportRightClickMenu();
                        tree.setMenu(treeMenu);
                        break;
                    case 0://root
                        tree.setMenu(null);
                        break;
                    case 1://Logs
                        tree.setMenu(null);
                        createLogsRightClickMenu();
                        tree.setMenu(treeMenu);
                        break;
                    case 2://Pictures
                        tree.setMenu(null);
                        createLogsRightClickMenu();
                        tree.setMenu(treeMenu);
                        break;
                    case 3://Scripts
                    case 8:
                        tree.setMenu(null);
                        createScriptsRightClickMenu();
                        tree.setMenu(treeMenu);
                        break;
                    case 4://fold
                        tree.setMenu(null);
                        createOtherFoldRightClickMenu();
                        tree.setMenu(treeMenu);
                        break;
                    case 5://file
                        tree.setMenu(null);
                        createFileRightClickMenu();
                        tree.setMenu(treeMenu);
                        break;
                    case 6://Scripts child
                        tree.setMenu(null);
                        createChildRightClickMenu();
                        tree.setMenu(treeMenu);
                        break;
                    case 7://Devices
                        tree.setMenu(null);
                        createDevicesRightClickMenu();
                        tree.setMenu(treeMenu);
                        break;
                    default://unknown
                        tree.setMenu(null);
                        break;
                }
                System.gc();
            }
        });

        //show picture
        tree.addMouseTrackListener(new MouseTrackListener() {

            @Override
            public void mouseEnter(MouseEvent e) {
            }

            @Override
            public void mouseExit(MouseEvent e) {
                if (tipImage != null)
                    tipImage.close();
            }

            @Override
            public void mouseHover(MouseEvent e) {
                if (tipImage != null) {
                    tipImage.close();
                }

                Point point = new Point(e.x, e.y);
                TreeItem item = tree.getItem(point);
                if (item != null) {
                    TreeItem root = RobotTreeUtil.getRoot(item);
                    String projectFile = (String) root.getData("path");
                    String absolutPath = projectFile.substring(0, projectFile.lastIndexOf("\\") + 1);
                    String relativePath = RobotTreeUtil.getPathFromTree(item);
                    String path = absolutPath + relativePath;

                    if (new File(path).exists() && path.trim().toLowerCase().endsWith(".png")) {
                        tipImage = new SetToolTipImage(path);
                        tipImage.setLocation(MouseInfo.getPointerInfo().getLocation().x + 15,
                            MouseInfo.getPointerInfo().getLocation().y + 20);
                        tipImage.open();
                    } else {
                        if (item.getData("device") != null) {
                            //System.out.println("index:"+item.getData("index"));
                            //System.out.println("sn:"+item.getData("sn"));
                            tree.setToolTipText("序号:" + item.getData("index") + "\n分辨率:"
                                                + item.getData("pixel") + "\nSN:"
                                                + item.getData("sn"));
                            //System.out.println("pixel:"+mWidth+"*"+mHeight);
                        }
                    }
                }

            }

        });
        //
        addTasksTab();

        //Settings
        //addSettingsTab();
    }

    private static CTabItem getCurrentTab() {
        return tabContent.getSelection();
    }

    //remove old logs
    private static void removeOldLogs() {
        vectorLog.clear();
        listViewerLog.refresh(false);
    }

    //remove all old information from case table
    public static void removeOldTestcase() {
        Table table = tableViewerCase.getTable();
        TableItem[] tableItems = table.getItems();
        for (int i = 0; i < tableItems.length; i++) {
            String sn = tableItems[i].getText(0);
            String handset = tableItems[i].getText(1);
            String scripts = tableItems[i].getText(2);
            RowItem rowItem = htRowItem.get(sn + "." + handset + "." + scripts);
            if (rowItem != null) {
                htRowItem.remove(sn + "." + handset + "." + scripts);
                listCase.remove(rowItem);
                ProgressBar bar = htProgress.get(sn + "." + handset + "." + scripts);
                bar.dispose();
                htProgress.remove(sn + "." + handset + "." + scripts);
            }
        }
        tableViewerCase.refresh(false);
    }

    public static void delTestCase() {
        listCase.clear();
        tableViewerCase.refresh(false);

        //remove
        for (Iterator itrProgress = htProgress.keySet().iterator(); itrProgress.hasNext();) {
            String taskName = (String) itrProgress.next();
            ProgressBar progressBar = htProgress.get(taskName);
            progressBar.dispose();
        }

        htProgress.clear();
    }

    public static void addTestCase(String sn, Vector<Task> tasks) {

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            for (int j = 0; j < task.vecTC.size(); j++) {
                RowItem row = new RowItem();
                row.setSn(sn);
                row.setTaskName(task.name);

                row.setCaseName(task.vecTC.get(j).name);
                //add row to table
                listCase.add(row);
                htRowItem.put(sn + "." + task.name + "." + task.vecTC.get(j).name, row);

                tableViewerCase.refresh(false);
                addProgressBar(tcPBIndex++, sn + "." + task.name + "." + task.vecTC.get(j).name,
                    100);
            }
        }
    }

    public static String getCurrentProject() {
        CTabItem ti = getCurrentTab();
        if (ti != null) {
            String scriptName = ti.getText();
            if (scriptName.startsWith("*")) {
                scriptName = scriptName.substring(1, scriptName.length());
            }
            String path = ti.getData(scriptName).toString();
            String prjName = path.substring(0, path.indexOf("\\"));
            return prjName;
        }
        return "";
    }

    public static boolean insertScript(String str) {
        CTabItem ti = getCurrentTab();
        if (ti != null) {
            Control control = (Control) ti.getControl();
            if (control != null) {
                if (control instanceof StyledText) {
                    StyledText text = (StyledText) control;
                    int offset = text.getCaretOffset();

                    String previousText = text.getLine(text.getLineAtOffset(offset));
                    String countSpace = getFrontSpace(previousText);

                    //offset = next line
                    text.insert(str + "\n");
                    //System.out.println(text.getLineCount());
                    text.setSelection(offset + str.length() + 1);

                    //add space
                    if (countSpace.length() > 0) {
                        text.insert(countSpace);
                        text.setCaretOffset(text.getCaretOffset() + countSpace.length());

                    }
                }
            }
            return true;
        }
        return false;
    }

    public static void setStatusScript(String path) {
        String file = path.substring(path.lastIndexOf("\\") + 1, path.length());
        tiStatusBarScript.setText(file);
    }

    public static void setPassCount(String count) {
        tiStatusBarPass.setText(count);
    }

    public static void setFailCount(String count) {
        tiStatusBarFail.setText(count);
    }

    public static void setTotalCount(String count) {
        tiStatusBarTotal.setText(count);
    }

    public static void setBeginTime(String sn, String taskName, String scriptName, String time) {
        RowItem rowItem = null;
        for (int i = 0; i < listCase.size(); i++) {
            if (listCase.get(i).getTaskName().equals(taskName)
                && listCase.get(i).getCaseName().equals(scriptName)
                && listCase.get(i).getSn().equals(sn)) {
                rowItem = listCase.get(i);
                break;
            }

        }
        if (rowItem != null) {
            rowItem.setStartTime(time);
            tableViewerCase.refresh(rowItem);
        }
    }

    public static void setProgressBarCount(String sn, String taskName, String scriptName) {
        String key = sn + "." + taskName + "." + scriptName;
        if (htProgress.containsKey(key)) {
            ProgressBar bar = htProgress.get(key);
            int max = bar.getMaximum();

            //cycle
            if (max == bar.getSelection()) {
                bar.setSelection(0);
            }
            bar.setSelection(bar.getSelection() + 1);
        }
    }

    public static void setProgressBarCount(String sn, String taskName, String scriptName, int num) {
        String key = sn + "." + taskName + "." + scriptName;
        if (htProgress.containsKey(key)) {
            ProgressBar bar = htProgress.get(key);
            int max = bar.getMaximum();

            //cycle
            if (max == bar.getSelection()) {
                bar.setSelection(0);
            }
            bar.setSelection(num);
        }
    }

    public static void setProgressBarMax(String sn, String taskName, String scriptName) {
        String key = sn + "." + taskName + "." + scriptName;
        if (htProgress.containsKey(key)) {
            ProgressBar bar = htProgress.get(key);
            int max = htProgress.get(key).getMaximum();
            bar.setSelection(max);
        }
    }

    private static void setDeviceOnline(String brand, IDevice device) {
        if (tree != null && tree.getItemCount() > 0) {
            for (int i = 0; i < tree.getItemCount(); i++) {
                TreeItem rootTI = tree.getItem(i);
                TreeItem devices = RobotTreeUtil.getNodeByName(rootTI, "Devices");
                for (int j = 0; j < devices.getItemCount(); j++) {
                    String sn = (String) devices.getItem(j).getData("sn");
                    String name = devices.getItem(j).getText();
                    if (name.equals(brand) && (sn != null && device.getSerialNumber().contains(sn))) {
                        if (!composite.getParent().isDisposed()) {
                            devices.getItem(j).setImage(
                                new Image(display, ClassLoader
                                    .getSystemResourceAsStream("icons/devices.png")));
                            devices.getItem(j).setData("device", device);
                        }
                    }
                }
            }
        }
    }

    private static void setDeviceOffline(String brand) {
        if (tree != null && tree.getItemCount() > 0) {
            for (int i = 0; i < tree.getItemCount(); i++) {
                TreeItem rootTI = tree.getItem(i);
                TreeItem devices = RobotTreeUtil.getNodeByName(rootTI, "Devices");
                for (int j = 0; j < devices.getItemCount(); j++) {
                    String sn = (String) devices.getItem(j).getData("sn");
                    String name = devices.getItem(j).getText();
                    if (name.equals(brand) && (sn != null && device.getSerialNumber().contains(sn))) {
                        devices.getItem(j).setImage(
                            new Image(display, ClassLoader
                                .getSystemResourceAsStream("icons/disconn.png")));
                    }
                }
            }
        }
    }

    private static void setAllDeviceOffline() {
        if (tree != null && tree.getItemCount() > 0) {
            for (int i = 0; i < tree.getItemCount(); i++) {
                TreeItem rootTI = tree.getItem(i);
                TreeItem devices = RobotTreeUtil.getNodeByName(rootTI, "Devices");
                for (int j = 0; j < devices.getItemCount(); j++) {
                    try {
                        if (!composite.getParent().isDisposed() && null != devices.getItem(j)
                            && null != devices.getItem(j).getData("device")) {
                            devices.getItem(j).setImage(
                                new Image(display, ClassLoader
                                    .getSystemResourceAsStream("icons/disconn.png")));
                            devices.getItem(j).setData("device", null);
                        }
                        //        				}else{
                        //        					System.out.println("Node is removed2.");
                        //        				}
                    } catch (Exception ex) {
                        System.out.println("Node is removed1.");
                    }
                }
            }
        }
    }

    private static int handsetsNumber = 0;

    public static void setDevices(IDevice[] devices) {
        if (listHandsets.getItemCount() > 0) {
            if (devices.length != handsetsNumber) {
                int selIndex = listHandsets.getSelectionIndex();
                listHandsets.removeAll();
                vecSerialNumber.clear();

                setAllDeviceOffline();
                handsetsNumber = 0;
                for (int i = 0; i < devices.length; i++) {
                    if (devices[i].isOnline()) {
                        handsetsNumber++;
                        String brand = AdbUtil.getBrand(devices[i]);
                        listHandsets.add(brand);
                        vecSerialNumber.add(devices[i].getSerialNumber());
                        System.out.println("##############################");
                        //set online
                        setDeviceOnline(brand, devices[i]);
                    }
                }
                listHandsets.setSelection(selIndex);
            }
        } else {
            for (int i = 0; i < devices.length; i++) {
                if (devices[i].isOnline()) {
                    handsetsNumber++;
                    //String serial = devices[i].getSerialNumber();
                    String brand = AdbUtil.getBrand(devices[i]);
                    listHandsets.add(brand);
                    vecSerialNumber.add(devices[i].getSerialNumber());
                    //initSettings(serial);
                    //
                    setDeviceOnline(brand, devices[i]);
                }
            }
        }
    }

    public static void removeAllDevice() {
        device = null;
        listHandsets.removeAll();
        setAllDeviceOffline();
    }

    public static void initStatusBar() {
        tiStatusBarScript.setText(" ");
        tiStatusBarPass.setText("Pass:0");
        tiStatusBarFail.setText("Fail:0");
        tiStatusBarTotal.setText("Total:0");
        tiStatusBarConnect.setText("No Handset");
    }

    public static void setPassOrFailCount(String sn, String taskName, String scriptName,
                                          boolean result) {

        String strPass = tiStatusBarPass.getText();
        int pCount = Integer.parseInt(strPass.substring(5, strPass.length()));

        String strFail = tiStatusBarFail.getText();
        int fCount = Integer.parseInt(strFail.substring(5, strFail.length()));

        //RowItem in 测试用例 列表
        RowItem rowItem = null;

        for (int i = 0; i < listCase.size(); i++) {
            if (listCase.get(i).getTaskName().equals(taskName)
                && listCase.get(i).getCaseName().equals(scriptName)
                && listCase.get(i).getSn().equals(sn)) {
                rowItem = listCase.get(i);
                break;
            }

        }
        if (rowItem == null)
            return;

        String pfCount = rowItem.getResult();
        int pRowCount = 0;
        int fRowCount = 0;
        if (pfCount != null && !pfCount.trim().equals("")) {
            pRowCount = Integer.parseInt(pfCount.substring(0, pfCount.indexOf("/")));
            fRowCount = Integer.parseInt(pfCount.substring(pfCount.indexOf("/") + 1,
                pfCount.length()));
        } else {
            pRowCount = fRowCount = 0;
        }

        String currTime = TimeUtil.getTimeAsFormat("yyyy-MM-dd HH:mm:ss");
        rowItem.setEndTime(currTime);

        if (result) {
            tiStatusBarPass.setText("PASS:" + (pCount + 1));
            rowItem.setResult((pRowCount + 1) + "/" + fRowCount);
            tableViewerCase.refresh(rowItem);
        } else {
            tiStatusBarFail.setText("FAIL:" + (fCount + 1));
            rowItem.setResult(pRowCount + "/" + (fRowCount + 1));
            tableViewerCase.refresh(rowItem);
        }

        setProgressBarMax(sn, taskName, scriptName);
    }

    public static void addADBLog(String serialNum, String context) {
        String serial = device.getSerialNumber();
        if (serial.equals(serialNum))
            adbLogcatText.append(context);
    }

    private static void promot(StyledText stText) {
        new MtesterAutoCompleteField(stText, new StyledTextContentAdapter(), PromptString.str,
            PromptString.str2, shell);
    }

    private static void createHandsetRightClickRecord() {
        handsetMenu = new Menu(shell, SWT.POP_UP);
        MenuItem mntmnew = new MenuItem(handsetMenu, SWT.CASCADE);
        mntmnew.setText("加入");

        Menu menu_2 = new Menu(mntmnew);
        mntmnew.setMenu(menu_2);

        if (tree != null && tree.getItemCount() > 0) {
            for (int i = 0; i < tree.getItemCount(); i++) {
                final MenuItem mntmfold = new MenuItem(menu_2, SWT.NONE);
                final String project = tree.getItem(i).getText();
                mntmfold.setText(project);
                mntmfold.addListener(SWT.Selection, new Listener() {
                    public void handleEvent(Event event) {
                        if (event.detail == 0) {
                            TreeItem root = RobotTreeUtil.getNodeByName(tree, project);
                            if (root != null) {
                                try {
                                    TreeItem ti = RobotTreeUtil.getNodeByName(root, "Devices");
                                    String name = listHandsets.getItems()[listHandsets
                                        .getSelectionIndex()];
                                    IDevice tempDevice = findDevices.getDevices()[listHandsets
                                        .getSelectionIndex()];
                                    String sn = tempDevice.getSerialNumber();
                                    if (RobotTreeUtil.isChildContained(ti, sn) == false) {
                                        String projectFile = (String) root.getData("path");

                                        Hashtable<String, String> attri = new Hashtable();
                                        attri.put("pixel", mWidth + "*" + mHeight);
                                        attri.put("sn", sn);
                                        ProjectUtil.addHandset(projectFile, name, attri);
                                        //RobotTreeUtil.addHandsetTreeItem(display,ti,name,tempDevice,attri);
                                        RobotTreeUtil.addHandsetTreeItem(display, ti, name,
                                            tempDevice, attri);
                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        }

        //MenuItem mntmfold = new MenuItem(menu_2, SWT.NONE);
        //mntmfold.setText("目录");
    }

    private static void createRightClickRecord() {
        recordMenu = new Menu(shell, SWT.POP_UP);
        /*
        itemSetCheckPoint = new MenuItem(recordMenu, SWT.PUSH);
        itemSetCheckPoint.setText("设置比对信息");
        itemSetCheckPoint.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event event) {
        	        if(event.detail == 0) {
        	        	setCaptureMode();
        	        }
        	      }
        	});
        */
        itemSaveCP = new MenuItem(recordMenu, SWT.PUSH);
        itemSaveCP.setText("保存图片");
        itemSaveCP.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    setCheckPoint();
                }
            }
        });
    }

    private static void createRightClickRecord_2() {
        recordMenu_2 = new Menu(shell, SWT.POP_UP);
        /*
        itemSetCheckPoint_2 = new MenuItem(recordMenu_2, SWT.PUSH);
        itemSetCheckPoint_2.setText("继续录制");
        itemSetCheckPoint_2.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event event) {
        	        if(event.detail == 0) {
        	        	setCaptureMode();
        	        }
        	      }
        	});
        */
        itemSaveCP_2 = new MenuItem(recordMenu_2, SWT.PUSH);
        itemSaveCP_2.setText("保存图片");
        itemSaveCP_2.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    setCheckPoint();
                }
            }
        });
    }

    private static void deleteDevice() {
        TreeItem root = RobotTreeUtil.getRoot(lastItem[0]);
        String projectFile = (String) root.getData("path");
        try {
            ProjectUtil.removeHandset(projectFile, lastItem[0].getText());

            TreeItem tiParent = lastItem[0].getParentItem();
            if (lastItem[0] != null) {
                lastItem[0].dispose();
            }
            TreeItem[] tis = tiParent.getItems();
            for (int i = 0; i < tis.length; i++) {
                tis[i].setData("index", i);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void createDevicesRightClickMenu() {
        treeMenu = new Menu(shell, SWT.POP_UP);
        MenuItem itemRefresh = new MenuItem(treeMenu, SWT.PUSH);
        itemRefresh.setText("删除");
        itemRefresh.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    deleteDevice();
                }
            }
        });

    }

    private static void createChildRightClickMenu() {
        treeMenu = new Menu(shell, SWT.POP_UP);
        MenuItem mntmnew = new MenuItem(treeMenu, SWT.CASCADE);
        mntmnew.setText("新建");

        Menu menu_2 = new Menu(mntmnew);
        mntmnew.setMenu(menu_2);

        MenuItem mntmfold = new MenuItem(menu_2, SWT.NONE);
        mntmfold.setText("目录");

        MenuItem itemRename = new MenuItem(treeMenu, SWT.PUSH);
        itemRename.setText("重命名");

        itemRename.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    changeName();
                }
            }
        });

        MenuItem itemRefresh = new MenuItem(treeMenu, SWT.PUSH);
        itemRefresh.setText("刷新");

        mntmfold.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createFolder();
                }
            }
        });

        itemRefresh.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    fefresh();
                }
            }
        });
    }

    private static void createLogsRightClickMenu() {
        treeMenu = new Menu(shell, SWT.POP_UP);
        MenuItem mntmnew = new MenuItem(treeMenu, SWT.CASCADE);
        mntmnew.setText("新建");

        Menu menu_2 = new Menu(mntmnew);
        mntmnew.setMenu(menu_2);

        MenuItem mntmfold = new MenuItem(menu_2, SWT.NONE);
        mntmfold.setText("目录");
        mntmfold.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createFolder();
                }
            }
        });

        /*MenuItem itemRename = new MenuItem(treeMenu, SWT.PUSH);
        itemRename.setText("重命名");

        itemRename.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event event) {
        	        if(event.detail == 0) {
        	        	changeName();
        	        }
        	      }
        });*/

        MenuItem itemRun = new MenuItem(treeMenu, SWT.PUSH);
        itemRun.setText("运行");
        itemRun.setEnabled(false);

        MenuItem itemRefresh = new MenuItem(treeMenu, SWT.PUSH);
        itemRefresh.setText("刷新");
        itemRefresh.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    fefresh();
                }
            }
        });
    }

    private static void createFileRightClickMenu() {
        treeMenu = new Menu(shell, SWT.POP_UP);

        MenuItem itemRename = new MenuItem(treeMenu, SWT.PUSH);
        itemRename.setText("重命名");

        itemRename.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    changeName();
                }
            }
        });

    }

    private static void createOtherFoldRightClickMenu() {
        treeMenu = new Menu(shell, SWT.POP_UP);
        MenuItem mntmnew = new MenuItem(treeMenu, SWT.CASCADE);
        mntmnew.setText("新建");

        Menu menu_2 = new Menu(mntmnew);
        mntmnew.setMenu(menu_2);

        MenuItem mntmfold = new MenuItem(menu_2, SWT.NONE);
        mntmfold.setText("目录");

        MenuItem mntmScript = new MenuItem(menu_2, SWT.NONE);
        mntmScript.setText("脚本");

        MenuItem itemRename = new MenuItem(treeMenu, SWT.PUSH);
        itemRename.setText("重命名");

        itemRename.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    changeName();
                }
            }
        });

        MenuItem itemRefresh = new MenuItem(treeMenu, SWT.PUSH);
        itemRefresh.setText("刷新");

        mntmfold.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createFolder();
                }
            }
        });

        itemRefresh.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    fefresh();
                }
            }
        });

        mntmScript.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createScript();
                }
            }
        });
    }

    private static void createScriptsRightClickMenu() {
        treeMenu = new Menu(shell, SWT.POP_UP);
        MenuItem mntmnew = new MenuItem(treeMenu, SWT.CASCADE);
        mntmnew.setText("新建");

        Menu menu_2 = new Menu(mntmnew);
        mntmnew.setMenu(menu_2);

        MenuItem mntmproject = new MenuItem(menu_2, SWT.NONE);
        mntmproject.setText("项目");
        mntmproject.setEnabled(false);

        MenuItem mntmscript = new MenuItem(menu_2, SWT.NONE);
        mntmscript.setText("脚本");

        MenuItem mntmfold = new MenuItem(menu_2, SWT.NONE);
        mntmfold.setText("目录");

        MenuItem itemRun = new MenuItem(treeMenu, SWT.PUSH);
        itemRun.setText("运行");

        MenuItem itemRename = new MenuItem(treeMenu, SWT.PUSH);
        itemRename.setText("重命名");

        itemRename.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    changeName();
                }
            }
        });

        MenuItem itemRefresh = new MenuItem(treeMenu, SWT.PUSH);
        itemRefresh.setText("刷新");

        mntmproject.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createProject();
                }
            }
        });

        mntmscript.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createScript();
                }
            }
        });

        mntmfold.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    createFolder();
                }
            }
        });

        itemRefresh.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    fefresh();
                }
            }
        });
    }

    private static void createImportRightClickMenu() {
        treeMenu = new Menu(shell, SWT.POP_UP);

        MenuItem itemRun = new MenuItem(treeMenu, SWT.PUSH);
        itemRun.setText("导入工程");

        itemRun.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == 0) {
                    openProject();
                }
            }
        });
    }

    public static void showLog(DisplayUtil.Show show, String log, String sn) {
        String time = TimeUtil.getTimeAsFormat("yyyy-MM-dd HH:mm:ss");
        if (show == DisplayUtil.Show.Info)
            vectorLog.add("[" + sn + "]" + time + "[信息]" + log);
        else if (show == DisplayUtil.Show.Command) {
            //String scriptName = robotDevice.currentScriptName.substring(robotDevice.currentScriptName.lastIndexOf("\\")+1,robotDevice.currentScriptName.length());
            //handsets.add(time +"["+serialNumber+"]["+scriptName+"][命令]" + cmd);
            vectorLog.add("[" + sn + "]" + time + "[信息]" + log);
        } else if (show == DisplayUtil.Show.Error) {
            //String scriptName = robotDevice.currentScriptName.substring(robotDevice.currentScriptName.lastIndexOf("\\")+1,robotDevice.currentScriptName.length());
            //handsets.add(time +"["+serialNumber+"]["+scriptName+"][错误]" + cmd);
            vectorLog.add("[" + sn + "]" + time + "[错误]" + log);
        } else if (show == DisplayUtil.Show.Result) {
            //String scriptName = robotDevice.currentScriptName.substring(robotDevice.currentScriptName.lastIndexOf("\\")+1,robotDevice.currentScriptName.length());
            vectorLog.add("[" + sn + "]" + time + "[测试结果]" + log);
        } else if (show == DisplayUtil.Show.Start) {
            //String scriptName = robotDevice.currentScriptName.substring(robotDevice.currentScriptName.lastIndexOf("\\")+1,robotDevice.currentScriptName.length());
            vectorLog.add("[" + sn + "]" + time + "[测试开始]" + log);
        }
        listViewerLog.refresh(false);
    }

    private static void removeTabItem(String tabName) {
        for (int i = 0; i < tabContent.getItemCount(); i++) {
            CTabItem item = tabContent.getItem(i);
            String name = item.getText();
            if (name.contains("*")) {
                if (name.substring(1, name.length()).equals(tabName)) {
                    item.dispose();
                }
            } else {
                if (name.equals(tabName)) {
                    item.dispose();
                }
            }
        }
    }

    private static boolean exitPrompt() {
        for (int i = 0; i < tabContent.getItemCount(); i++) {
            CTabItem item = tabContent.getItem(i);
            String name = item.getText();
            if (name.contains("*")) {
                int iChoice = showNotification("是否保存?", SWT.ICON_QUESTION | SWT.YES | SWT.NO
                                                        | SWT.CANCEL);
                if (iChoice == SWT.YES) {
                    StyledText text = (StyledText) item.getControl();
                    name = name.substring(1, name.length());
                    try {
                        FileUtility.saveAllScripts((String) item.getData(name), item.getText(),
                            text.getText());
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return true;
                } else if (iChoice == SWT.CANCEL) {
                    return false;
                }

            }
        }

        //if(htLog.size()>0)
        //	return false;

        if (isRecording) {
            showNotification("请关闭录制功能!", SWT.ICON_WARNING | SWT.YES);
            return false;
        }
        return true;
    }

    public static String removeTabKey(String text) {
        return text.replaceAll("\t", "    ");
    }

    public static String getFrontSpace(String text) {
        String sCount = "";
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == 32 || ch == '\t')
                sCount += ch;
            else
                break;
        }
        return sCount;
    }

    private static void saveScript() {
        CTabItem currTabItem = getCurrentTab();
        if (currTabItem != null) {
            String fileName = currTabItem.getText();
            if (fileName.contains("*")) {
                fileName = fileName.substring(1, fileName.length());
                String path = (String) getCurrentTab().getData(fileName);

                StyledText text = (StyledText) getCurrentTab().getControl();
                String strContent = removeTabKey(text.getText());
                try {
                    FileUtility.writeFile(path, strContent, false);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                getCurrentTab().setText(fileName);
            }
        }
    }

    private static void dispose(TreeItem ti) {
        TreeItem[] tis = ti.getItems();
        //if(tis.length == 0)
        //	ti.dispose();
        //else
        for (int i = 0; i < tis.length; i++) {
            dispose(tis[i]);
            tis[i].dispose();
        }
    }

    private static void fefresh() {
        if (lastItem[0] != null) {
            TreeItem root = RobotTreeUtil.getRoot(lastItem[0]);
            String projectFile = (String) root.getData("path");
            String absolutPath = projectFile.substring(0,
                projectFile.lastIndexOf(System.getProperty("file.separator")) + 1);
            String relativePath = RobotTreeUtil.getPathFromTree(lastItem[0]);
            dispose(lastItem[0]);
            FileUtility.refresh(lastItem[0], display, absolutPath + relativePath);
        }
    }

    private static void run2() {

        //init system
        tcPBIndex = 0;
        initStatusBar();

        delTestCase();

        vectorLog.clear();
        listViewerLog.refresh(false);

        Vector<IDevice> vecDevices = RobotTreeUtil.getDevicesByProject(tree.getItem(0));

        int count = 0;
        run(vecDevices);

        //disable all button
        if (count > 0)
            setButton(false, false, false, false, false, false, false, true, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false);
    }

    public static synchronized ArrayList<IDevice> getDevices(ArrayList<String> devicesList) {
        ArrayList<IDevice> tempList = new ArrayList();
        IDevice[] devices = findDevices.getDevices();

        for (int j = 0; j < devicesList.size(); j++) {
            for (int i = 0; i < devices.length; i++) {
                if (devicesList.get(j).equals(devices[i].getSerialNumber())) {
                    tempList.add(devices[i]);
                    break;
                }
            }
        }

        return tempList;
    }

    private static void run() {
        //init system
        tcPBIndex = 0;
        initStatusBar();
        delTestCase();
        vectorLog.clear();
        runners.clear();
        listViewerLog.refresh(false);
        setButton(false, false, false, false, false, false, false, true, false, false, false,
            false, false, false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false);

        Vector<IDevice> vecDevices = RobotTreeUtil.getDevicesByProject(tree.getItem(0));
        if (vecDevices == null || vecDevices.size() <= 0) {
            UIUtiles.alertMsg(shell, "请先检查设备是否上线，并添加设备.");
            stop(false);
            return;
        }
        int count = 0;
        //		RobotDeviceQueue queue = new RobotDeviceQueue();

        //disable record

        try {
            if (isRecording == true && client != null && client.getSDKVersion() != 0)
                client.disconnect();
        } catch (Exception ex) {
            log.error(ex);
        }

        for (int i = 0; i < vecDevices.size(); i++) {
            count++;
            //queue.offer(vecDevices.get(i));
            Vector<IDevice> devices = new Vector<IDevice>();
            devices.add(vecDevices.get(i));
            run(devices);

        }
    }

    public static boolean run(Vector<IDevice> vecDevices) {
        if (vecDevices == null || vecDevices.size() <= 0) {
            UIUtiles.alertMsg(shell, "请先检查设备是否上线，并添加设备.");
            stop(false);
            return false;
        }
        //get devices from tree
        if (tree.getItemCount() > 0 && tree.getItem(0) != null) {
            Vector<AndroidDriver> drivers = new Vector();

            ArrayList<String> listTasks = new ArrayList();
            //get task from task.dat
            int itemCount = tasksTable.getItemCount();
            log.info("Task Count:" + itemCount);
            for (int i = 0; i < itemCount; i++) {
                TableItem ti = tasksTable.getItem(i);
                boolean isChecked = ti.getChecked();
                if (isChecked == true) {
                    listTasks.add(ti.getText(0));
                }
            }

            Vector<Task> tasks = TaskUtil.getCheckedTasks(taskFilePath, listTasks);
            log.info("Task Checked Count:" + tasks.size());
            if (tasks.size() <= 0) {
                UIUtiles.alertMsg(shell, "请到任务管理先选择任务!");
                stop(false);
                return false;
            }

            try {
                //connect to device
                log.info("Device Count:" + vecDevices.size());
                if (vecDevices.size() <= 0)
                    return false;
                String apk = PropertiesUtil.getValue(workspacePath + "/system.properties", "aut");
                String str_sele = PropertiesUtil.getValue(workspacePath + "/system.properties",
                    "isSelendroid");
                String str_chrome = PropertiesUtil.getValue(workspacePath + "/system.properties",
                    "isChromedriver");
                String isForceInstall = PropertiesUtil.getValue(workspacePath
                                                                + "/system.properties",
                    "isForceInstall");

                log.info("dut=" + apk + " isSelendroid=" + str_sele + " isChromedriver="
                         + str_chrome + " isForceInstall=" + isForceInstall);
                if (!str_sele.trim().equals("")
                    && (str_sele.trim().toLowerCase().equals("false") || str_sele.trim()
                        .toLowerCase().equals("true")))
                    isSelendroid = Boolean.parseBoolean(str_sele);

                if (!str_chrome.trim().equals("")
                    && (str_chrome.trim().toLowerCase().equals("false") || str_chrome.trim()
                        .toLowerCase().equals("true")))
                    isChromedriver = Boolean.parseBoolean(str_chrome);

                for (int i = 0; i < vecDevices.size(); i++) {
                    AndroidDriver driver = new AndroidDriver(apk, vecDevices.get(i)
                        .getSerialNumber(), isSelendroid, projectPath);
                    if (driver.connect()) {
                        log.info("connect " + vecDevices.get(i).getSerialNumber() + " success");
                        int sdk_version = driver.getSDKVersion();
                        if (sdk_version < 19) {
                            //小于Android 4.4 uiautomator+selendroid
                            if (apk != null && !apk.trim().equals("")
                                && isForceInstall.trim().toLowerCase().equals("true")
                                && isSelendroid == true) {
                                ApkInfo apkInfo = new ApkUtil().getApkInfo(apk);
                                AdbUtil.send("adb -s " + vecDevices.get(i).getSerialNumber()
                                             + " uninstall " + apkInfo.getPackageName(), 60000);

                                AdbUtil.send(
                                    "adb -s " + vecDevices.get(i).getSerialNumber()
                                            + " uninstall io.selendroid."
                                            + apkInfo.getPackageName(), 60000);
                                AndroidDriver.launchSelendroidStandalone(apk, 4444);
                            }

                        } else {
                            if (isChromedriver == true)
                                AndroidDriver.launchChromeServer();
                        }

                        if (isForceInstall.trim().toLowerCase().equals("true") && apk != null
                            && apk.trim().equals("")) {
                            if (driver.setup(vecDevices.get(i).getSerialNumber(), isSelendroid))
                                drivers.add(driver);
                        } else
                            drivers.add(driver);
                    }
                }

                if (drivers.size() <= 0) {
                    stop(false);
                    showNotification("设备连接错误!", SWT.ICON_WARNING | SWT.YES);
                    return false;
                }

            } catch (Exception ex) {
                log.error(ex);
                stop(false);
                String exception = ex.getMessage();
                showNotification(exception, SWT.ICON_WARNING | SWT.YES);
                return false;
            }

            String sn = vecDevices.get(0).getSerialNumber();
            addTestCase(sn, tasks);

            Log log = new Log(sn);
            log.createFile(projectPath);

            //Run
            RobotScriptRunner scriptRunner = new RobotScriptRunner(tasks, drivers, log,
                isSelendroid);

            scriptRunner.start();
            runners.add(scriptRunner);

            refreshProgressUITimer.start();
            watchRunScript.start();
            System.gc();
            return true;
        } else {
            showNotification("请先打开工程文件!", SWT.ICON_WARNING | SWT.YES);
            return false;
        }
    }

    private static void changeName(TreeItem ti, String src, String dest) {
        File old;
        File newFile;

        if (!src.equals(dest)) {
            System.out.println("src=" + src + " " + workspacePath + " " + dest);
            if (ti.getParentItem() == null) {
                old = new File(workspacePath + "/workspace/" + src);
                newFile = new File(workspacePath + "/workspace/" + dest);
            } else {
                String path = RobotTreeUtil.getPathFromTree(ti.getParentItem());
                ti.setText(dest);

                old = new File(workspacePath + "/workspace/" + path + "/" + src);
                newFile = new File(workspacePath + "/workspace/" + path + "/" + dest);
            }

            old.renameTo(newFile);
        }
    }

    public static boolean register() {
        File file = new File("hello.dat");

        try {
            if (!file.exists()) {
                String cpuSerial = HardwareUtil.getCPUSerial();
                String url = "http://test.hongganju.com:8096/netmonitor/resource/registerDate?cpu="
                             + cpuSerial;
                System.out.println("url=" + url);
                String str = HttpClientUtil.get(url, null);

                FileOutputStream out = null;
                try {
                    System.out.println(str);
                    JSONObject dataJson = new JSONObject(str);
                    if (dataJson.getString("success") != null
                        && dataJson.getString("success").equals("true")) {
                        String today = dataJson.getString("today");
                        String date = dataJson.getString("date");
                        System.out.println("today=" + today + " date=" + date);
                        SimpleDateFormat sdfToday = new SimpleDateFormat("yyyyMMdd");
                        Date dateToday = sdfToday.parse(today);

                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
                        Date dateDate = sdfDate.parse(date);
                        System.out.println(dateToday.getTime() - dateDate.getTime());
                        if ((dateDate.getTime() - dateToday.getTime()) > 0) {
                            file.createNewFile();
                            out = new FileOutputStream(file);
                            out.write((today + date + cpuSerial).getBytes());
                            return true;
                        } else {
                            showNotification("请根据CPU序列号:" + cpuSerial + "注册", SWT.ICON_ERROR
                                                                              | SWT.OK);
                            return false;
                        }
                    } else {
                        showNotification("请根据CPU序列号:" + cpuSerial + "注册", SWT.ICON_ERROR | SWT.OK);
                        return false;
                    }

                    //
                } catch (Exception e) {
                    showNotification("请根据CPU序列号:" + cpuSerial + "注册", SWT.ICON_ERROR | SWT.OK);
                    return false;
                } finally {
                    if (null != out)
                        out.close();
                }
            } else {
                FileInputStream in = new FileInputStream(file);
                byte[] today = new byte[8];
                in.read(today, 0, today.length);

                byte[] date = new byte[8];
                in.read(date, 0, date.length);

                byte[] cpuInfo = new byte[20];
                in.read(cpuInfo, 0, cpuInfo.length);
                System.out.println(new String(today) + " " + new String(date) + " "
                                   + new String(cpuInfo));

                SimpleDateFormat sdfToday = new SimpleDateFormat("yyyyMMdd");
                Date dateToday = sdfToday.parse(new String(today));

                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
                Date dateDate = sdfDate.parse(new String(date));
                if ((dateDate.getTime() - dateToday.getTime()) > 0) {

                } else {
                    return false;
                }
                //    			String string=" attrib +H "+file.getAbsolutePath(); //设置文件属性为隐藏
                //        		Runtime.getRuntime().exec(string);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * AndroidRobot Main Function
     *
     * @param args Author: He Zheng
     *             Modified: Oct 22,2012
     */
    public static void main(String[] args) {
        //Loading Splash windows from start
        //        SplashWindow sp = new SplashWindow(System.getProperty("user.dir") +
        //        		File.separator + "splash.jpg");
        //        sp.start();
        //设置log4j环境
        PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator
                                       + "log4j.properties");
        //设置adb环境
        Env.setEnv();
        if (Env.isValidJava() != true) {
            Logger.getLogger(AndroidRobot.class).error(
                "当前Java版本为" + Env.getJavaVersion() + " " + Env.getJavaArch());
            Logger.getLogger(AndroidRobot.class).error("建议使用JDK 1.7 64位以上版本");
            return;
        }

        display = new Display();
        shell = new Shell(display);
        shell
            .setImage(new Image(display, ClassLoader.getSystemResourceAsStream("icons/title.png")));

        String title = "AndroidRobot -- Spider 3.1";
        shell.setText(title);

        /**
         * shutcut key for main frame
         * F5 - Run
         * F6 - Stop
         * F9 - Record
         * F10 - Stop record
         * F11 - switch record emulator mode
         * F12 - Set check point
         */
        display.addFilter(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
                switch (event.keyCode) {
                    case SWT.F5:
                        if (itemRun.getEnabled() == true) {
                            run();
                        } else
                            showNotification("该设备正在运行状态!", SWT.ICON_WARNING | SWT.YES);
                        break;
                    case SWT.F6:
                        if (listHandsets.getSelectionIndex() >= 0) {
                            int choice = showNotification("您确定要停止运行当前的设备?", SWT.ICON_QUESTION
                                                                            | SWT.YES | SWT.NO
                                                                            | SWT.CANCEL);
                            if (choice == SWT.YES)
                                stop(false);
                        }
                        break;
                    case SWT.F9:
                        if (isRecording == false)
                            try {
                                record();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        break;
                    case SWT.F10:
                        if (isRecording == true) {
                            int choice = showNotification("您确定要停止录制?", SWT.ICON_QUESTION | SWT.YES
                                                                       | SWT.NO | SWT.CANCEL);
                            if (choice == SWT.YES)
                                stopRecord();
                        }
                        break;
                    case SWT.F11:
                        //if(itemCapture.getEnabled() == true)
                        //	setCaptureMode();
                        break;
                    case SWT.F12:
                        if (itemTakePhoto.getEnabled() == true)
                            setCheckPoint();
                        break;
                    default:
                        break;
                }
            }
        });

        createToolBar();
        createRightClickRecord();
        createRightClickRecord_2();
        createMenu();
        createSashForm();
        createStatusBar();

        findDevices.start();
        //		watchRun3Script.start();
        shell.setMaximized(true);
        shell.setLayout(new FormLayout());
        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                switch (event.type) {
                    case SWT.Close:
                        if (exitPrompt()) {
                            findDevices.stopThread();
                            AndroidDriver.stopSelendroidStandalone();
                            AndroidDriver.stopChromeServer();
                            adbGetDevice.disconnect();
                            if (moveTableItem != null)
                                moveTableItem.close();
                            event.doit = true;
                            System.exit(0);
                        } else
                            event.doit = false;
                        break;
                }
            }
        });

        //load current project
        loadProjects();

        shell.open();
        shell.layout();

        //    	if(register() == false)
        //    		System.exit(0);
        //        sp.closeSplashWindow();
        while (shell != null && !shell.isDisposed()) {
            if (display != null && !display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
}
