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
package com.android.ide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class PythonLineStyleListener implements LineStyleListener {

	private String[] keywords;
	private Shell shell;

	private Color digitColor = SWTResourceManager.getColor(SWT.COLOR_BLUE);
	private Color variableColor = SWTResourceManager
			.getColor(SWT.COLOR_DARK_GREEN);

	private Color nColor = SWTResourceManager.getColor(SWT.COLOR_BLUE);
	private Color qColor = SWTResourceManager.getColor(SWT.COLOR_DARK_MAGENTA);
	private int count = 0;

	public PythonLineStyleListener(String[] keywords, Shell shell) {
		this.keywords = keywords;
		this.shell = shell;
	}

	private Color HexStrToRGB(String colorStr) {
		// System.out.println(colorStr);
		int r = Integer.valueOf(colorStr.substring(1, 3), 16);
		int g = Integer.valueOf(colorStr.substring(3, 5), 16);
		int b = Integer.valueOf(colorStr.substring(5, 7), 16);
		return new Color(this.shell.getDisplay(), new RGB(r, g, b));
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		if (keywords == null || keywords.length == 0) {
			return;
		}
		List<StyleRange> styles = new ArrayList<StyleRange>();
		int start = 0;
		int length = event.lineText.length();
		while (start < length) {
			if (Character.isLetter(event.lineText.charAt(start))) { // keywords
																	// string
				StringBuffer buf = new StringBuffer();
				int i = start;
				for (; i < length
						&& Character.isLetter(event.lineText.charAt(i)); i++) {
					buf.append(event.lineText.charAt(i));
				}
				if (Arrays.asList(keywords).contains(buf.toString())) {
					styles.add(new StyleRange(event.lineOffset + start, i
							- start, HexStrToRGB(new ReadProperties()
							.getKeyValue(buf.toString())), null, SWT.BOLD));
				}
				start = i;
			} else if (event.lineText.charAt(start) == '\'') { // ''string
				StringBuffer buf = new StringBuffer();
				buf.append('\'');
				int i = start + 1;
				for (; i < length; /* && (event.lineText.charAt(i) != '#') */i++) {
					buf.append(event.lineText.charAt(i));
					if (event.lineText.charAt(i) == '\'') {
						break;
					}
				}
				if (buf.toString().matches("\'\'")
						|| buf.toString().matches("\'[^\']+\\d?\'")) {
					styles.add(new StyleRange(event.lineOffset + start, i + 1
							- start, qColor, null, SWT.NORMAL));
				}
				start = i + 1;
			} else if (event.lineText.charAt(start) == '\"') {// "" string
				StringBuffer buf = new StringBuffer();
				buf.append('\"');
				int i = start + 1;
				for (; i < length /* && (event.lineText.charAt(i) != '#') */; i++) {
					buf.append(event.lineText.charAt(i));
					if (event.lineText.charAt(i) == '\"') {
						if (i >= 3 && event.lineText.charAt(i - 1) == '\"') {
							if (buf.toString().endsWith("\"\"\"")) {
								styles.add(new StyleRange(event.lineOffset
										+ start, buf.length() - start,
										variableColor, null, SWT.NORMAL));
							}
						} else {
							break;
						}
					}
				}
				if (buf.toString().matches("\"\"")
						|| buf.toString().matches("\"[^\"]+\\d?\"")) {
					styles.add(new StyleRange(event.lineOffset + start, i + 1
							- start, qColor, null, SWT.NORMAL));
				}
				start = i + 1;
			} else if (Character.isDigit(event.lineText.charAt(start))) { // digit
				StringBuffer buf = new StringBuffer();
				int i = start;
				for (; i < length
						&& Character.isDigit(event.lineText.charAt(i)); i++) {
					buf.append(event.lineText.charAt(i));
				}
				if (buf.toString().matches("[0-9]+\\d?")) {
					styles.add(new StyleRange(event.lineOffset + start, i
							- start, nColor, null, SWT.ITALIC));
				}
				start = i;
			} else if (event.lineText.charAt(start) == '#') { // # ......
				StringBuffer buf = new StringBuffer();
				buf.append('#');
				int i = start + 1;
				for (; i < length; i++) {
					buf.append(event.lineText.charAt(i));

				}
				styles.add(new StyleRange(event.lineOffset + start, i - start,
						variableColor, null, SWT.NORMAL));

				start = i;
			} else {
				start++;
			}
		}
		event.styles = (StyleRange[]) styles.toArray(new StyleRange[0]);
	}
}
