package com.android.robot;


import java.io.File;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class SaveScreen extends Dialog {

	protected String result;
	protected Shell shell;
	protected Display display;
	
	private Text textDir;
	private Text textName;
	private Label lblError;
	
	private Tree tree;
	
	private File folder;
	private File[] files;
	private File fileScript;
	private File[] fileScripts;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SaveScreen(Shell parent, int style) {
		super(parent, style);
		setText("保存截图");
	}
	
	private Vector<File> getProjectFromWorkspace(){
		Vector<File> vec = new Vector();
		folder = new File(".\\workspace");
		if(folder != null){
			files=folder.listFiles();
			
			for(int i=0;i<files.length;i++)
				vec.add(files[i]);
			
			return vec;
		}
		return null;
	}
	
	private Vector<File> getFolders(String project){
		Vector<File> vec = new Vector();
		folder = new File(".\\workspace" + "\\" + project);
		if(folder != null && folder.isDirectory()){
			files=folder.listFiles();
			
			for(int i=0;i<files.length;i++)
				if(files[i].isDirectory())
					vec.add(files[i]);
			
			return vec;
		}
		return null;
	}
	
	private Vector<String> getScripts(String project){
		Vector<String> vec = new Vector();
		fileScript = new File(project);
		if(fileScript != null && fileScript.isDirectory()){
			fileScripts=fileScript.listFiles();
			
			for(int i=0;i<fileScripts.length;i++)
				vec.add(fileScripts[i].getName());
			
			return vec;
		}
		return null;
	}
	
	private String getPathFromTree(TreeItem treeItem){
		String path = treeItem.getText();
		TreeItem root = treeItem.getParentItem();
		while(root != null){
			path = root.getText()+"\\"+path;
			root = root.getParentItem();
		}
		return path;
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
		GridData gd_composite2 = new GridData(435, 250);
		gd_composite2.verticalAlignment = SWT.CENTER;
		composite2.setLayoutData(gd_composite2);
		composite2.setLayout(new GridLayout(1,false));
		
		Label lb = new Label(composite2, SWT.CENTER);
		lb.setText("保存目录:");
		
		textDir = new Text(composite2, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_textDir = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textDir.widthHint = 415;
		textDir.setLayoutData(gd_textDir);
		
		
		tree = new Tree(composite2, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		//add tree
		Vector<File> folders = getProjectFromWorkspace();
		if(folders != null){
			for(int i=0;i<folders.size();i++){
				//Project node
				TreeItem column = new TreeItem(tree, SWT.NONE);
				column.setText(folders.get(i).getName());
				column.setImage(new Image(display, ".\\icons\\project.png"));
				
				//Project folders
				Vector<File> vecFolders = 
						getFolders(folders.get(i).getName());
				
				if(vecFolders != null){
					for(int j=0;j<vecFolders.size();j++){
						//Project node
						TreeItem folder = new TreeItem(column, SWT.NONE);
						folder.setText(vecFolders.get(j).getName());
						folder.setImage(new Image(display, ".\\icons\\folder.png"));
						
						//Script node
						Vector<String> vecScripts = 
								getScripts(vecFolders.get(j).getParent()+"\\"+vecFolders.get(j).getName());
						if(vecScripts != null && vecScripts.size() != 0){
							for(int k=0;k<vecScripts.size();k++){
								TreeItem tiScript = new TreeItem(folder, SWT.NONE);
								tiScript.setText(vecScripts.get(k));
								tiScript.setImage(new Image(display, ".\\icons\\script.png"));
							}
						}
					}
				}
			}
		}
		
		tree.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				final TreeItem item = (TreeItem) event.item;

				String path = getPathFromTree(item);
				textDir.setText(path);
			}
		});
		
		Label lblName = new Label(composite2, SWT.CENTER);
		lb.setText("图片名称:");
		
		textName = new Text(composite2, SWT.BORDER);
		GridData gd_textName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textName.widthHint = 415;
		textName.setLayoutData(gd_textName);
		textName.setText("New Pic.png");
		
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
				String dir = textDir.getText();
				if(dir.endsWith("\\Pictures")){
					String name = textName.getText();
					if(name != null && !name.trim().equals("")){
						if(name.trim().toLowerCase().endsWith(".png")){
							result = dir+"\\"+name.trim();
						}else{
							result = dir+"\\"+name.trim()+".png";
						}
						
						boolean exist = 
								new File(".\\workspace\\"+result).exists();
						
						if(exist){
							MessageBox box = new MessageBox( shell ,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
					    	box.setMessage("文件名重复,是否覆盖?");
					        //打开对话框，将返回值赋给choice
					        int choice = box.open();
					        if(choice == 0){
					        	result = "";
					        }else{
								shell.close();
								shell.dispose();
					        }
						}else{
							shell.close();
							shell.dispose();
						}
						

						
					}else{
						lblError.setText("请设置文件名!");
						lblError.setForeground(
								new Color(display, new RGB(255, 0, 0)));
						lblError.setVisible(true);
					}
				}else{
					lblError.setText("请选择任意项目下的Picture目录!");
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
		shell.setSize(450, 415);
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
		lbNewScript.setText("保存截图");
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
