package com.android.robot;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class NewScript extends Dialog {

	protected String result;
	protected Shell shell;
	protected Display display;
	
	private Text textDir;
	private Text textScript;
	private Label lblError;

	private String path;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public NewScript(Shell parent, int style,String path) {
		super(parent, style);
		setText("新建脚本");
		this.path = path;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
		createContents();
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 435;
		label.setLayoutData(gd_label);
		
		Composite composite2 = new Composite(shell, SWT.NONE);
		GridData gd_composite2 = new GridData(435, 150);
		gd_composite2.verticalAlignment = SWT.CENTER;
		composite2.setLayoutData(gd_composite2);
		composite2.setLayout(new GridLayout(3,false));
		
		Label lb = new Label(composite2, SWT.CENTER);
		lb.setText("脚本目录:");
		
		textDir = new Text(composite2, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_textDir = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textDir.widthHint = 270;
		textDir.setLayoutData(gd_textDir);
		textDir.setText(path);
		
		Label lbDefault = new Label(composite2, SWT.CENTER);
		lbDefault.setText("(默认目录)");
		
		Label lbScript = new Label(composite2, SWT.CENTER);
		lbScript.setText("脚本名:");
		
		textScript = new Text(composite2, SWT.BORDER);
		GridData gd_textScript = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textScript.widthHint = 270;
		textScript.setLayoutData(gd_textScript);
		textScript.setText("new.py");
		new Label(composite2, SWT.NONE);
		
		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_1.widthHint = 433;
		label_1.setLayoutData(gd_label_1);
		
		Composite composite3 = new Composite(shell, SWT.NONE);
		GridData gd_composite3 = new GridData(435, 50);
		gd_composite3.verticalAlignment = SWT.FILL;
		composite3.setLayoutData(gd_composite3);
		composite3.setLayout(new GridLayout(2,false));
		
		Button previous = new Button(composite3,SWT.NONE);
		GridData gd_previous = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_previous.heightHint = 25;
		gd_previous.widthHint = 77;
		previous.setLayoutData(gd_previous);
		previous.setText("确定");
		previous.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String name = textScript.getText();
				if(!name.trim().equals("")){
					result = path + "\\" + textScript.getText();
					shell.close();
					shell.dispose();
				}else{
					lblError.setText("请输入脚本名!");
					lblError.setForeground(
							new Color(display, new RGB(255, 0, 0)));
					lblError.setVisible(true);
				}
			}
		});
		
		Button cancel = new Button(composite3,SWT.NONE);
		GridData gd_cancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_cancel.heightHint = 25;
		gd_cancel.widthHint = 77;
		cancel.setLayoutData(gd_cancel);
		cancel.setText("取消");
		
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = "";
				shell.close();
				shell.dispose();
			}
		});
		
		shell.open();
		shell.layout();
		
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setImage(SWTResourceManager.getImage(".\\icons\\title.png"));
		shell.setLayout(new GridLayout(1,false));
		display = getParent().getDisplay();
		 
		Composite composite1 = new Composite(shell, SWT.NONE);
		GridData gd_composite1 = new GridData(435, 50);
		gd_composite1.verticalAlignment = SWT.CENTER;
		composite1.setLayoutData(gd_composite1);
		composite1.setLayout(new GridLayout(3,false));
		
		Label lbNewScript = new Label(composite1,SWT.CENTER);
		lbNewScript.setText("新建脚本框");
		lbNewScript.setFont(new Font(display,"宋体",12,SWT.BOLD));
		new Label(composite1, SWT.NONE);
		new Label(composite1, SWT.NONE);
		
		lblError = new Label(composite1, SWT.NONE);
		GridData gd_lblError = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_lblError.widthHint = 207;
		lblError.setLayoutData(gd_lblError);
		lblError.setText("New Label");
		lblError.setVisible(false);
        /*
		Label abc1 = new Label(composite1,SWT.CENTER);
		abc1.setText("                                                                         ");
		abc1.setVisible(false);
		Label abc2 = new Label(composite1,SWT.CENTER);
		FileInputStream input;
		try {
			input = new FileInputStream(new File(
					".\\icons\\title.png"));
			ImageData imageData = new ImageData(input);
			abc2.setImage(new Image(Display.getDefault(), imageData));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
		

	}
}
