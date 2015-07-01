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

import java.io.Serializable;
import java.util.Vector;

public class LogInfo implements Serializable{
	public String task = "";
	public String name = "";
	public String description = "";
	public int loop = 1;
	
	public StringBuffer adbLog = new StringBuffer();
	private StringBuffer runLog = new StringBuffer();
	//public byte[] bytes = new byte[1024*1024];
	
	public NGOK result;
	
	public void addRunLog(String logInfo){
		runLog.append(logInfo+"\n");
	}
	
	public StringBuffer getRunLog(){
		return this.runLog;
	}
}
