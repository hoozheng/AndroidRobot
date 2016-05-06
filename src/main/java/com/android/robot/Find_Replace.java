package com.android.robot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Find_Replace {
    private static Shell        parent       = null;
    private static Shell        shell        = null;
    private static Display      display      = null;
    private static boolean      isOpen       = false;
    private static Find_Replace find_Replace = null;

    private static StyledText   styleText    = null;

    private Find_Replace(Shell shellParent) {
        parent = shellParent;
    }

    @SuppressWarnings("static-access")
    public void setTextControl(StyledText styleText) {
        this.styleText = styleText;
    }

    public static Find_Replace newInstance(Shell parent) {
        if (find_Replace == null)
            find_Replace = new Find_Replace(parent);
        return find_Replace;
    }

    private static void createForm() {
        //Find Replace
        Composite compFindReplace = new Composite(shell, SWT.NONE);
        compFindReplace.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gLayout = new GridLayout(3, true);
        compFindReplace.setLayout(gLayout);

        Label labelFind = new Label(compFindReplace, SWT.None);
        labelFind.setText("查找:");

        final Text textFind = new Text(compFindReplace, SWT.BORDER);
        textFind.setFocus();
        GridData gridData2 = new GridData();
        gridData2.horizontalSpan = 2;

        gridData2.horizontalAlignment = SWT.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = true;
        textFind.setLayoutData(gridData2);

        textFind.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    String searchText = textFind.getText();
                    String text = styleText.getText();
                    int start = styleText.getCaretOffset();
                    int indexFrom = text.indexOf(searchText, start);
                    //System.out.println("indexFrom:"+indexFrom + " car:"+styleText.getCaretOffset());
                    styleText.setSelection(indexFrom, (indexFrom + searchText.length()));
                }

            }

            public void keyReleased(KeyEvent e) {

            }
        });

        //Replace
        Label labelReplace = new Label(compFindReplace, SWT.None);
        labelReplace.setText("替换为:");

        Text textReplace = new Text(compFindReplace, SWT.BORDER);
        GridData gd_TextReplace = new GridData();
        gd_TextReplace.horizontalSpan = 2;
        gd_TextReplace.horizontalAlignment = SWT.FILL;
        gd_TextReplace.grabExcessHorizontalSpace = true;
        gd_TextReplace.grabExcessVerticalSpace = true;
        textReplace.setLayoutData(gd_TextReplace);

        //Direction
        Composite compDirection = new Composite(shell, SWT.NONE);
        GridData gd_direction = new GridData(GridData.FILL_HORIZONTAL);
        gd_direction.heightHint = 85;
        compDirection.setLayoutData(gd_direction);
        compDirection.setLayout(new GridLayout(1, true));

        Group groupDirection = new Group(compDirection, SWT.NONE);
        groupDirection.setText("方向");
        groupDirection.setLayoutData(new GridData(GridData.FILL_BOTH));
        groupDirection.setLayout(new GridLayout(1, true));

        Button radioForward = new Button(groupDirection, SWT.RADIO);
        radioForward.setText("前进");
        radioForward.setSelection(true);

        Button radioBack = new Button(groupDirection, SWT.RADIO);
        radioBack.setText("后退");

        //Options
        Composite compOptions = new Composite(shell, SWT.NONE);
        GridData gd_options = new GridData(GridData.FILL_HORIZONTAL);
        gd_options.heightHint = 85;
        compOptions.setLayoutData(gd_options);
        compOptions.setLayout(new GridLayout(1, true));

        Group groupOptions = new Group(compOptions, SWT.NONE);
        groupOptions.setText("选择");
        groupOptions.setLayoutData(new GridData(GridData.FILL_BOTH));
        groupOptions.setLayout(new GridLayout(1, true));

        Button multiCS = new Button(groupOptions, SWT.CHECK);
        multiCS.setText("匹配大小写");

        Button multiWords = new Button(groupOptions, SWT.CHECK);
        multiWords.setText("只匹配整个词语");

        //Button
        Composite compButton = new Composite(shell, SWT.NONE);
        GridData gd_Button = new GridData(GridData.FILL_HORIZONTAL);
        gd_Button.heightHint = 85;
        compButton.setLayoutData(gd_Button);
        compButton.setLayout(new GridLayout(2, true));

        Button btnFind = new Button(compButton, SWT.PUSH);
        GridData gd_btnFind = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnFind.widthHint = 106;
        btnFind.setLayoutData(gd_btnFind);
        btnFind.setText("查找");

        Button btnReplace = new Button(compButton, SWT.PUSH);
        GridData gd_btnReplace = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnReplace.widthHint = 106;
        btnReplace.setLayoutData(gd_btnReplace);
        btnReplace.setText("替换");

        Button btnClose = new Button(compButton, SWT.PUSH);
        GridData gd_btnClose = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnClose.widthHint = 106;
        btnClose.setLayoutData(gd_btnClose);
        btnClose.setText("关闭");
        new Label(compButton, SWT.NONE);
    }

    /**
     * Open the window.
     */
    public void open() {
        if (isOpen == true)
            return;
        isOpen = true;
        display = Display.getDefault();
        shell = new Shell(parent);//
        shell.setLayout(new GridLayout());
        shell.open();
        shell.setSize(273, 400);
        shell.setText("查找/替换");

        createForm();

        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                switch (event.type) {
                    case SWT.Close:
                        isOpen = false;
                        break;
                }
            }
        });

        shell.layout();
        while (shell != null && !shell.isDisposed()) {
            if (display != null && !display.readAndDispatch()) {
                display.sleep();
            }
        }

    }
}
