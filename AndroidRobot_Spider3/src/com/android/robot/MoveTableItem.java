package com.android.robot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MoveTableItem {

	protected Shell shell;
	protected Display display;
	
	private Text textSearch = null;
	
	int position = -1;
	

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public MoveTableItem(Shell parent,int style) {
		display = Display.getDefault();
		createContents();
	}
	
	public void setLocation(int x,int y){
		shell.setLocation(new Point(x,y));
	}
	
	public void close(){
		if(shell != null)
			shell.dispose();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public int open() {
		
		shell.open();
		shell.layout();
		while (shell!=null&&!shell.isDisposed()) {
			if (display!=null&&!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return position;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(SWT.ON_TOP);
		shell.setLayout(new GridLayout(1,false));
		
		Composite compAll = new Composite(shell, SWT.NONE);
        GridData gdAll = new GridData(GridData.FILL_HORIZONTAL);
        gdAll.heightHint = 35;
        compAll.setLayoutData(gdAll);
        compAll.setLayout(new GridLayout(4, false));
        
        Label lbl1 = new Label(compAll, SWT.LEFT);
        lbl1.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        lbl1.setText("插入第 ");
        
        textSearch = new Text(compAll, SWT.BORDER|SWT.SINGLE);
        GridData gd_textSearch = new GridData(GridData.FILL_VERTICAL);
        gd_textSearch.widthHint = 69;
        textSearch.setLayoutData(gd_textSearch);
        textSearch.setText("0");
        textSearch.addTraverseListener(new TraverseListener() {  
            public void keyTraversed(TraverseEvent e) {  
              if (e.keyCode == 13) {  
            	  position = Integer.parseInt(textSearch.getText());
            	  close();
              }  
            }  
        });
        
        textSearch.addVerifyListener(new VerifyListener() {
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
        
        Label lbl2 = new Label(compAll, SWT.LEFT);
        lbl2.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        lbl2.setText("行");
        
        Button btnConfirm = new Button(compAll, SWT.PUSH);
        btnConfirm.setText("确定");
        btnConfirm.addListener(SWT.Selection, new Listener() {
    	      public void handleEvent(Event event) {
    	        if(event.detail == 0) {
    	        	position = Integer.parseInt(textSearch.getText());
    	        	close();
    	        }
    	      }
    	});

        
        GridData gd_lbl2 = new GridData(GridData.FILL_BOTH);
        btnConfirm.setLayoutData(gd_lbl2);

		shell.setSize(236, 40);
	}

}
