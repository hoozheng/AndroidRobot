package com.android.robot;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Text;

public class MonkeyTest extends Dialog {
	private Table table;
	private Text text_3;
	private Text text_7;
	private Text text_8;
	private Text text_9;
	private Text text_10;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public MonkeyTest(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		Composite composite = new Composite(container, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_composite.heightHint = 328;
		gd_composite.widthHint = 406;
		composite.setLayoutData(gd_composite);
		
		Group grpAbcd = new Group(composite, SWT.NONE);
		grpAbcd.setText("abcd");
		grpAbcd.setBounds(0, 10, 396, 63);
		
		Combo combo = new Combo(grpAbcd, SWT.NONE);
		combo.setBounds(10, 33, 169, 25);
		
		Combo combo_1 = new Combo(grpAbcd, SWT.NONE);
		combo_1.setBounds(217, 33, 169, 25);
		
		TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setBounds(0, 211, 396, 287);
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite_1.heightHint = 502;
		gd_composite_1.widthHint = 183;
		composite_1.setLayoutData(gd_composite_1);
		
		Label label_3 = new Label(composite_1, SWT.NONE);
		label_3.setText("\u89E6\u6478\u4E8B\u4EF6:");
		label_3.setBounds(21, 91, 61, 17);
		
		text_3 = new Text(composite_1, SWT.BORDER);
		text_3.setBounds(88, 88, 85, 23);
		
		Label label_7 = new Label(composite_1, SWT.NONE);
		label_7.setText("\u4E3B\u8981\u5BFC\u822A:");
		label_7.setBounds(21, 307, 61, 17);
		
		text_7 = new Text(composite_1, SWT.BORDER);
		text_7.setBounds(88, 304, 85, 23);
		
		Label label_8 = new Label(composite_1, SWT.NONE);
		label_8.setText("\u7CFB\u7EDF\u6309\u952E:");
		label_8.setBounds(21, 333, 61, 17);
		
		text_8 = new Text(composite_1, SWT.BORDER);
		text_8.setBounds(88, 330, 85, 23);
		
		Label label_9 = new Label(composite_1, SWT.NONE);
		label_9.setText("\u542F\u52A8\u6D3B\u52A8:");
		label_9.setBounds(21, 362, 61, 17);
		
		text_9 = new Text(composite_1, SWT.BORDER);
		text_9.setBounds(88, 359, 85, 23);
		
		Label label_10 = new Label(composite_1, SWT.NONE);
		label_10.setText("\u5176\u4ED6\u4E8B\u4EF6:");
		label_10.setBounds(21, 388, 61, 17);
		
		text_10 = new Text(composite_1, SWT.BORDER);
		text_10.setBounds(88, 385, 85, 23);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(615, 603);
	}
}
