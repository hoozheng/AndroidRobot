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

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
 
public class MtesterAutoCompleteField {
   private ContentProposalProvider contentProposalProvider;
   private ContentProposalAdapter  contentProposalAdapter;
   private Shell shell;
 
   public MtesterAutoCompleteField(final Control control,
                              final IControlContentAdapter controlContentAdapter,
                              final String[] literals,
                              final String[] labels,final Shell shell) {
	  this.shell = shell;
      contentProposalProvider = new ContentProposalProvider(literals, labels);
      contentProposalProvider.setFiltering(false);
      contentProposalAdapter = new ContentProposalAdapter(control, new StyledTextContentAdapter(), contentProposalProvider, null, null);
      contentProposalAdapter.setPropagateKeys(false);
      contentProposalAdapter.setFilterStyle(ContentProposalAdapter.FILTER_CUMULATIVE);
      contentProposalAdapter.setAutoActivationCharacters(new char[]{'.'});
      contentProposalAdapter.setPopupSize(getPoint((StyledText)control));
      contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
      contentProposalAdapter.setLabelProvider(new LabelProvider() {
    	  @Override
    	  public String getText(Object element) {
    		  IContentProposal proposal = (IContentProposal) element;
    		  return proposal.getLabel();
    	  }
    	  @Override
    	  public Image getImage(Object element) {
    		  Image iconCapture = new Image(shell.getDisplay(), "./icons/function.png");
    		  return iconCapture;  
    	  }
      });
   	}
   
   public Point getPoint(StyledText text){
	   Point p = text.getSelection();
	   p = shell.getDisplay().map(text, null, p.x, p.y+100);
	   return p;
   }
 
   public void setProposals(final String[] proposals) {
      contentProposalProvider.setProposals(proposals);
   }
 
   public ContentProposalProvider getContentProposalProvider() {
      return contentProposalProvider;
   }
 
   public ContentProposalAdapter getContentProposalAdapter() {
      return contentProposalAdapter;
   }
}
