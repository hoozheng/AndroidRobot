/*
 * Copyright (C) 2012 The CeHu Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.android.ide.ReadProperties;

public class LogLineStyleListener implements LineStyleListener{
	
	private String[] keywords = new String[]{"[测试开始]","[测试结果]","失败","通过","[信息]","[查询]","成功"};
	private Shell shell;
	
	public LogLineStyleListener(Shell shell) {
		this.shell = shell;
	}
	
	//BLUE
	private Color colorBlue = SWTResourceManager.getColor(SWT.COLOR_BLUE);
	private Color colorRed = SWTResourceManager.getColor(SWT.COLOR_RED);
	private Color colorGreen = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		List<StyleRange> styles = new ArrayList<StyleRange>();
		String line = event.lineText;
		if(line == null || line.trim().equals(""))
			return;
		if(line.startsWith(keywords[0])){
			//[测试开始]
			styles.add(new StyleRange(event.lineOffset, line.length(), colorBlue, null, SWT.NORMAL));
		}else if(line.startsWith(keywords[1])){
			//[测试结果]
			if(line.contains(keywords[2]))
				styles.add(new StyleRange(event.lineOffset, line.length(), colorRed, null, SWT.NORMAL));
			else if(line.contains(keywords[3]))
				styles.add(new StyleRange(event.lineOffset, line.length(), colorGreen, null, SWT.NORMAL));
		}else if(line.startsWith(keywords[4])){
			//[信息]
			styles.add(new StyleRange(0, line.length(), colorRed, null, SWT.BOLD));
		}else if(line.startsWith(keywords[5])){
			//[查询]
			if(line.contains(keywords[2]))
				styles.add(new StyleRange(event.lineOffset, line.length(), colorRed, null, SWT.NORMAL));
			else if(line.contains(keywords[6]))
				styles.add(new StyleRange(event.lineOffset, line.length(), colorGreen, null, SWT.NORMAL));
		}
		
		event.styles = (StyleRange[]) styles.toArray(new StyleRange[0]);
	}
}
