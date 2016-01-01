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

package com.android.control;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control; 

public class StyledTextContentAdapter extends TextContentAdapter {
	public String getControlContents(Control control) {
		return ((StyledText) control).getText();
	}
	public void setControlContents(Control control, String text,
			int cursorPosition) {
		((StyledText) control).setText(text);
		((StyledText) control).setSelection(cursorPosition, cursorPosition);
		
	}


	public void insertControlContents(Control control, String text,
			int cursorPosition) {
		StyledText styledText = (StyledText) control;
		Point selection = ((StyledText) control).getSelection();
		
		int position = selection.x;
		do {
			position--;
			} while (!ContentProposalProvider.isDot(
					styledText.getText(), position)&& position > 0);
		/** dot should not be replaced */
		if (position != 0) {
			position++;
			
		}
		

		styledText.setSelection(new Point(position, selection.x));
		((StyledText) control).insert(text);
		if (cursorPosition < text.length()) {
			((StyledText) control).setSelection(selection.x + cursorPosition,
					selection.x + cursorPosition);
		}else{

			setCursorPosition(control,position+text.length());
		}
		
	}
	
	public int getCursorPosition(Control control) {
		return ((StyledText) control).getCaretOffset();
	}
	
	public Rectangle getInsertionBounds(Control control) {
		StyledText text = (StyledText) control;
		Point caretOrigin = text.getCaret().getLocation();
		
		return new Rectangle(caretOrigin.x + text.getClientArea().x,
				caretOrigin.y + text.getClientArea().y + 3, 1,
				text.getLineHeight());
	}

	public void setCursorPosition(Control control, int position) {
		((StyledText) control).setSelection(new Point(position, position));
		
	}
	
	public void setCursorPosition(Control control, int line,int position) {
		((StyledText) control).setSelection(new Point(line, position));
		
	}
	
	public Point getSelection(Control control) {
		return ((StyledText) control).getSelection();
		
	}
	
	public void setSelection(Control control, Point range) {
		//((StyledText) control).setSelection(range);
	} 
}
