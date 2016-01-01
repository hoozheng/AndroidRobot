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

package com.android.python;

import com.android.robot.AndroidRobot;
import com.android.util.DisplayUtil;
import com.google.common.base.Predicate;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.Lists;

/*     */ import java.io.File;
import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;
/*     */ import org.python.core.Py;
/*     */ import org.python.core.PyException;
/*     */ import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
/*     */ 
/*     */ public class ScriptRunner
/*     */ {
/*  49 */   //private static final Logger LOG = Logger.getLogger(MonkeyRunnerOptions.class.getName());
/*     */   //private final Object scope;
/*     */   //private final String variable;
			
			public ScriptRunner(){
				
			}
			
/*     */   public int run(String executablePath, String scriptfilename, Collection<String> args, Map<String, Predicate<PythonInterpreter>> plugins,Object drivers)
/*     */   {
			  String sn = ((Vector<AndroidDriver>)drivers).get(0).getSN();
/*  79 */     File f = new File(scriptfilename);
/*     */ 	  System.out.println("=============run==================");
			  ArrayList localArrayList = 
					  Lists.newArrayList(new String[] { f.getParent() });
			  localArrayList.addAll(plugins.keySet());
			  String[] arrayOfString = new String[args.size() + 1];
			  arrayOfString[0] = f.getAbsolutePath();
			  
/*  82 */     Collection classpath = Lists.newArrayList(new String[] { f.getParent() });
/*  83 */     classpath.addAll(plugins.keySet());
/*     */ 

			  int i = 1;
/*  88 */     for ( Object localObject1 = args.iterator(); ((Iterator)localObject1).hasNext(); ) { 
				String localObject2 = (String)((Iterator)localObject1).next();
/*  89 */       arrayOfString[(i++)] = localObject2;
/*     */     }

/*  92 */     initPython(executablePath, classpath, arrayOfString);

/*  94 */     PythonInterpreter python = new PythonInterpreter();

/*  97 */     for (Map.Entry entry : plugins.entrySet()) {
/*     */       boolean success;
/*     */       try { 
					success = ((Predicate)entry.getValue()).apply(python);
/*     */       } catch (Exception e) {
/* 102 */         //LOG.log(Level.SEVERE, "Plugin Main through an exception.", e);
					e.printStackTrace();
/* 103 */       }

				continue;

				
/*     */     }
/*     */ 
/* 111 */     python.set("__name__", "__main__");
/*     */ 
/* 113 */     python.set("__file__", scriptfilename);
			  python.set("device", drivers);
			  try
/*     */     {
/* 116 */        python.execfile(scriptfilename);
/*     */     } catch (PyException e) {
				e.printStackTrace();
/* 118 */       if (Py.SystemExit.equals(e.type))
/*     */       {
					System.out.println("======1=======");
/* 120 */         return ((Integer)e.value.__tojava__(Integer.class)).intValue();
/*     */       }
				
				System.out.println("======2=======" + e.value);
				//if throw exception, the tc = false
//				for(int k=0;k<((Vector<RobotDevice>)vecRobotDevices).size();k++){
//					((Vector<RobotDevice>)vecRobotDevices).get(k).setResult(false);
//				}
				
//				for(int k=0;k<((Vector<RobotDevice>)clients).size();k++){
				
				//socket error
//				if(e.value.toString().contains("Socket error") ||
//						e.value.toString().contains("Stop"))
//					return 2;
				
//				((Vector<RobotDevice>)vecRobotDevices).get(0).log.addRunLog(e.type.toString()+":"+e.value.toString()+"\n");
				setInfo(e.type.toString()+":"+e.value.toString(), sn);
				return 1;
/*     */     }
				
				System.out.println("cleanup");
				python.cleanup();
				
				return 0;
/*     */   }
/*     */ 
/*     */   public void runString(String executablePath, String script) {
/* 130 */     initPython(executablePath);
/* 131 */     PythonInterpreter python = new PythonInterpreter();
/* 132 */     python.exec(script);
/*     */   }
/*     */ 
/*     */   public Map<String, PyObject> runStringAndGet(String executablePath, String script, String[] names)
/*     */   {
/* 137 */     return runStringAndGet(executablePath, script, Arrays.asList(names));
/*     */   }
/*     */ 
/*     */   public Map<String, PyObject> runStringAndGet(String executablePath, String script, Collection<String> names)
/*     */   {
/* 142 */     initPython(executablePath);
/* 143 */     PythonInterpreter python = new PythonInterpreter();
/* 144 */     python.exec(script);
/*     */ 
/* 146 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/* 147 */     for (String name : names) {
/* 148 */       builder.put(name, python.get(name));
/*     */     }
/* 150 */     return builder.build();
/*     */   }
/*     */ 
/*     */   private void initPython(String executablePath) {
/* 154 */     List arg = Collections.emptyList();
/* 155 */     initPython(executablePath, arg, new String[] { "" });
/*     */   }
/*     */ 
/*     */   private void initPython(String executablePath, Collection<String> pythonPath, String[] argv)
/*     */   {
/* 160 */     Properties props = new Properties();
/*     */ 
/* 163 */     StringBuilder sb = new StringBuilder();
/* 164 */     sb.append(System.getProperty("java.class.path"));
/* 165 */     for (String p : pythonPath) {
/* 166 */       sb.append(":").append(p);
/*     */     }
/* 168 */     props.setProperty("python.path", sb.toString());
/*     */ 
/* 173 */     props.setProperty("python.verbose", "error");
/*     */ 
/* 176 */     props.setProperty("python.executable", executablePath);
/*     */ 
/* 178 */     PythonInterpreter.initialize(System.getProperties(), props, argv);

/*     */     
/* 180 */     //String frameworkDir = System.getProperty("java.ext.dirs");
/* 181 */     //File monkeyRunnerJar = new File(frameworkDir, "monkeyrunner.jar");
/* 182 */     //if (monkeyRunnerJar.canRead())
/* 183 */      // PySystemState.packageManager.addJar(monkeyRunnerJar.getAbsolutePath(), false);
/*     */   }
/*     */ 
///*     */   public void console(String executablePath)
///*     */   {
///* 191 */     initPython(executablePath);
///* 192 */     InteractiveConsole python = new JLineConsole();
///* 193 */     python.interact();
///*     */   }

			public void setInfo(final String key,final String sn){
				Display.getDefault().asyncExec(new Runnable(){
					public void run()
					{
						AndroidRobot.showLog(DisplayUtil.Show.Error,key,sn);
					}
				});
			}
/*     */ }