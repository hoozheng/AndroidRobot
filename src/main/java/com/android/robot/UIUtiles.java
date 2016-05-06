/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.android.robot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author kejun.song
 * @version $Id: UIUtiles.java, v 0.1 2016年3月15日 下午5:48:30 kejun.song Exp $
 */
public class UIUtiles {

    public static int alertMsg(Shell shell, String msg) {
        MessageBox alertBox = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
        alertBox.setMessage(msg);
        alertBox.setText("信息");
        return alertBox.open();
    }
}
