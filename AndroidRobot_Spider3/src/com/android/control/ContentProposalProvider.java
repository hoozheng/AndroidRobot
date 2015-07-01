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

import java.util.ArrayList;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
 
public class ContentProposalProvider implements IContentProposalProvider {
   private String[]		proposals;
   private String[]		labels;
   private IContentProposal[]	contentProposals;
   private boolean		filterProposals	= false;
 
   public ContentProposalProvider(String[] proposals, String[] labels) {
      super();
      this.proposals = proposals;
      this.labels = labels;
   }
   
   /**
   	* Returns true is the character is a whitespace
   	*
   	* @param character
   	*            the character to test
   	* @return true if character is a whitespace
   	*/
   public static boolean isDot(char character) {
	   boolean isWhitespace = String.valueOf(character).matches("\\.");
	   return isWhitespace;
	   
   } 
   
   /**
   	* Returns true if the character at the given position in the string is a
   	* whitespace
   	*
   	* @param string
   	*            the string containing the character to test
  	* @param position
   	*            the position of the character to test
  	* @return true if character on this position is a whitespace
   	*/
   public static boolean isDot(String string, int position) {
	   boolean isWhitespace = false;
	   if (position < string.length() && position >= 0) {
		   isWhitespace = isDot(string.charAt(position));
		   
	   }
	   
	   return isWhitespace;
	   
   } 
 
   public IContentProposal[] getProposals(String contents, int position) {
	  // System.out.println("getProposals:"+contents);
      if (filterProposals) {
         ArrayList list = new ArrayList();
         for (int i = 0; i < proposals.length; i++) {
            if (proposals[i].length() >= contents.length() 
                && proposals[i].substring(0, contents.length()).equalsIgnoreCase(contents)) {
               list.add(makeContentProposal(proposals[i], labels[i]));
            }
         }
         return (IContentProposal[]) list.toArray(new IContentProposal[list.size()]);
      }
      
      if (contentProposals == null) {
         contentProposals = new IContentProposal[proposals.length];
         for (int i = 0; i < proposals.length; i++) {
            contentProposals[i] = makeContentProposal(proposals[i], labels[i]);
         }
      }
      return contentProposals;
   }
 
   public void setProposals(String[] items) {
	  //System.out.println("setProposals:"+items.length);
      this.proposals = items;
      contentProposals = null;
   }
 
   public void setFiltering(boolean filterProposals) {
      this.filterProposals = filterProposals;
      contentProposals = null;
   }
 
   private IContentProposal makeContentProposal(final String proposal, final String label) {
      return new IContentProposal() {
 
         public String getContent() {
            return proposal;
         }
 
         public String getDescription() {
        	 String description = "";
        	if(proposal.equals(PromptString.str[0])){
        		description = PromptString.str3[0];
        	}else if(proposal.equals(PromptString.str[1])){
        		description = PromptString.str3[1];
        	}else if(proposal.equals(PromptString.str[2])){
        		description = PromptString.str3[2];
        	}else if(proposal.equals(PromptString.str[3])){
        		description = PromptString.str3[3];
        	}else if(proposal.equals(PromptString.str[4])){
        		description = PromptString.str3[4];
        	}else if(proposal.equals(PromptString.str[5])){
        		description = PromptString.str3[5];
        	}else if(proposal.equals(PromptString.str[6])){
        		description = PromptString.str3[6];
        	}else if(proposal.equals(PromptString.str[7])){
        		description = PromptString.str3[7];
        	}else if(proposal.equals(PromptString.str[8])){
        		description = PromptString.str3[8];
        	}else if(proposal.equals(PromptString.str[9])){
        		description = PromptString.str3[9];
        	}else if(proposal.equals(PromptString.str[10])){
        		description = PromptString.str3[10];
        	}else if(proposal.equals(PromptString.str[11])){
        		description = PromptString.str3[11];
        	}else if(proposal.equals(PromptString.str[12])){
        		description = PromptString.str3[12];
        	}else if(proposal.equals(PromptString.str[13])){
        		description = PromptString.str3[13];
        	}else if(proposal.equals(PromptString.str[14])){
        		description = PromptString.str3[14];
        	}else if(proposal.equals(PromptString.str[15])){
        		description = PromptString.str3[15];
        	}else if(proposal.equals(PromptString.str[16])){
        		description = PromptString.str3[16];
        	}else if(proposal.equals(PromptString.str[17])){
        		description = PromptString.str3[17];
        	}else if(proposal.equals(PromptString.str[18])){
        		description = PromptString.str3[18];
        	}else if(proposal.equals(PromptString.str[19])){
        		description = PromptString.str3[19];
        	}else if(proposal.equals(PromptString.str[20])){
        		description = PromptString.str3[20];
        	}else if(proposal.equals(PromptString.str[21])){
        		description = PromptString.str3[21];
        	}else if(proposal.equals(PromptString.str[22])){
        		description = PromptString.str3[22];
        	}else if(proposal.equals(PromptString.str[23])){
        		description = PromptString.str3[23];
        	}else if(proposal.equals(PromptString.str[24])){
        		description = PromptString.str3[24];
        	}else if(proposal.equals(PromptString.str[25])){
        		description = PromptString.str3[25];
        	}else if(proposal.equals(PromptString.str[26])){
        		description = PromptString.str3[26];
        	}else if(proposal.equals(PromptString.str[27])){
        		description = PromptString.str3[27];
        	}else if(proposal.equals(PromptString.str[28])){
        		description = PromptString.str3[28];
        	}else if(proposal.equals(PromptString.str[29])){
        		description = PromptString.str3[29];
        	}else if(proposal.equals(PromptString.str[30])){
        		description = PromptString.str3[30];
        	}else if(proposal.equals(PromptString.str[31])){
        		description = PromptString.str3[31];
        	}else if(proposal.equals(PromptString.str[32])){
        		description = PromptString.str3[32];
        	}else if(proposal.equals(PromptString.str[33])){
        		description = PromptString.str3[33];
        	}else if(proposal.equals(PromptString.str[34])){
        		description = PromptString.str3[34];
        	}else if(proposal.equals(PromptString.str[35])){
        		description = PromptString.str3[35];
        	}else if(proposal.equals(PromptString.str[36])){
        		description = PromptString.str3[36];
        	}else if(proposal.equals(PromptString.str[37])){
        		description = PromptString.str3[37];
        	}else if(proposal.equals(PromptString.str[38])){
        		description = PromptString.str3[38];
        	}else if(proposal.equals(PromptString.str[39])){
        		description = PromptString.str3[39];
        	}else if(proposal.equals(PromptString.str[40])){
        		description = PromptString.str3[40];
        	}else if(proposal.equals(PromptString.str[41])){
        		description = PromptString.str3[41];
        	}else if(proposal.equals(PromptString.str[42])){
        		description = PromptString.str3[42];
        	}else if(proposal.equals(PromptString.str[43])){
        		description = PromptString.str3[43];
        	}else if(proposal.equals(PromptString.str[44])){
        		description = PromptString.str3[44];
        	}else if(proposal.equals(PromptString.str[45])){
        		description = PromptString.str3[45];
        	}else if(proposal.equals(PromptString.str[46])){
        		description = PromptString.str3[46];
        	}else if(proposal.equals(PromptString.str[47])){
        		description = PromptString.str3[47];
        	}else if(proposal.equals(PromptString.str[48])){
        		description = PromptString.str3[48];
        	}else if(proposal.equals(PromptString.str[49])){
        		description = PromptString.str3[49];
        	}else if(proposal.equals(PromptString.str[50])){
        		description = PromptString.str3[50];
        	}else if(proposal.equals(PromptString.str[51])){
        		description = PromptString.str3[51];
        	}else if(proposal.equals(PromptString.str[52])){
        		description = PromptString.str3[52];
        	}else if(proposal.equals(PromptString.str[53])){
        		description = PromptString.str3[53];
        	}else if(proposal.equals(PromptString.str[54])){
        		description = PromptString.str3[54];
        	}else if(proposal.equals(PromptString.str[55])){
        		description = PromptString.str3[55];
        	}else if(proposal.equals(PromptString.str[56])){
        		description = PromptString.str3[56];
        	}else if(proposal.equals(PromptString.str[57])){
        		description = PromptString.str3[57];
        	}else if(proposal.equals(PromptString.str[58])){
        		description = PromptString.str3[58];
        	}else if(proposal.equals(PromptString.str[59])){
        		description = PromptString.str3[59];
        	}
        	
            return description;
         }
 
         public String getLabel() {
            return proposal + " - " + label;
         }
 
         public int getCursorPosition() {
            return proposal.length();
         }
      };
   }
}
