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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.python.util.PythonInterpreter;

import com.android.uiautomator.UiAutomatorClient;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;


public class PythonProject {
	private final MonkeyRunnerOptions options;
	private Vector<AndroidDriver> drivers;
	public PythonProject(MonkeyRunnerOptions options,Vector<AndroidDriver> drivers){
		this.options = options;
		this.drivers = drivers;
	}

	/*     */   private static final void replaceAllLogFormatters(Formatter form, Level level)
	/*     */   {
	/* 179 */     LogManager mgr = LogManager.getLogManager();
	/* 180 */     Enumeration loggerNames = mgr.getLoggerNames();
	/* 181 */     while (loggerNames.hasMoreElements()) {
	/* 182 */       String loggerName = (String)loggerNames.nextElement();
	/* 183 */       Logger logger = mgr.getLogger(loggerName);
	/* 184 */       for (Handler handler : logger.getHandlers()) {
	/* 185 */         handler.setFormatter(form);
	/* 186 */         handler.setLevel(level);
	/*     */       }
	/*     */     }
	/*     */   }
	
	public int run(){
		String monkeyRunnerPath = System.getProperty("com.android.monkeyrunner.bindir") + File.separator + "monkeyrunner";
		ScriptRunner scriptRunner = new ScriptRunner();
		Map plugins = handlePlugins();
		if (this.options.getScriptFile() == null) {
			//scriptRunner.console("D:\\workspace\\PythonProject");
			return 0;
		}

		int error = scriptRunner.run(monkeyRunnerPath, this.options.getScriptFile().getAbsolutePath(), this.options.getArguments(), plugins,this.drivers);
		
		return error;
	}
		
	private Predicate<PythonInterpreter> handlePlugin(File f) {
		/*     */     JarFile jarFile;
		/*     */     try {
		/* 103 */       jarFile = new JarFile(f);
		/*     */     } catch (IOException e) {
		/* 105 */       //LOG.log(Level.SEVERE, "Unable to open plugin file.  Is it a jar file? " + f.getAbsolutePath(), e);
		/*     */ 
		/* 107 */       return Predicates.alwaysFalse();
		/*     */     }
						Manifest manifest;
		/*     */     try {
		/* 111 */       manifest = jarFile.getManifest();
		/*     */     } catch (IOException e) {
		/* 113 */       //LOG.log(Level.SEVERE, "Unable to get manifest file from jar: " + f.getAbsolutePath(), e);
		/*     */ 
		/* 115 */       return Predicates.alwaysFalse();
		/*     */     }
		/* 117 */     Attributes mainAttributes = manifest.getMainAttributes();
		/* 118 */     String pluginClass = mainAttributes.getValue("MonkeyRunnerStartupRunner");
		/* 119 */     if (pluginClass == null)
		/*     */     {
		/* 121 */       return Predicates.alwaysTrue();
		/*     */     }URL url;
		/*     */     try {
		/* 125 */       url = f.toURI().toURL();
		/*     */     } catch (MalformedURLException e) {
		/* 127 */       //LOG.log(Level.SEVERE, "Unable to convert file to url " + f.getAbsolutePath(), e);
		/*     */ 
		/* 129 */       return Predicates.alwaysFalse();
		/* 131 */     }URLClassLoader classLoader = new URLClassLoader(new URL[] { url }, ClassLoader.getSystemClassLoader());
		/*     */     Class clz;
		/*     */     try {
		/* 135 */       clz = Class.forName(pluginClass, true, classLoader);
		/*     */     } catch (ClassNotFoundException e) {
		/* 137 */       //LOG.log(Level.SEVERE, "Unable to load the specified plugin: " + pluginClass, e);
		/* 138 */       return Predicates.alwaysFalse();
		/*     */     }Object loadedObject;
		/*     */     try {
		/* 142 */       loadedObject = clz.newInstance();
		/*     */     } catch (InstantiationException e) {
		/* 144 */       //LOG.log(Level.SEVERE, "Unable to load the specified plugin: " + pluginClass, e);
		/* 145 */       return Predicates.alwaysFalse();
		/*     */     } catch (IllegalAccessException e) {
		/* 147 */       //LOG.log(Level.SEVERE, "Unable to load the specified plugin (did you make it public?): " + pluginClass, e);
		/*     */ 
		/* 149 */       return Predicates.alwaysFalse();
		/*     */     }
		/*     */ 
		/* 152 */     if ((loadedObject instanceof Runnable)) {
		/* 153 */       final Runnable run = (Runnable)loadedObject;
		/* 154 */       return new Predicate<PythonInterpreter>() {
						  public boolean apply(PythonInterpreter i) {
		/* 156 */           //this.val$run.run();
							run.run();
		/* 157 */           return true;
		/*     */         } 
						} ;
		/*     */     }
		/* 160 */     if ((loadedObject instanceof Predicate)) {
		/* 161 */       return (Predicate)loadedObject;
		/*     */     }
		/* 163 */     //LOG.severe("Unable to coerce object into correct type: " + pluginClass);
		/* 164 */     return Predicates.alwaysFalse();
		/*     */   }
	
	private Map<String, Predicate<PythonInterpreter>> handlePlugins()
	/*     */   {
	/* 169 */     ImmutableMap.Builder builder = ImmutableMap.builder();
	/* 170 */     for (File f : this.options.getPlugins()) {
	/* 171 */       builder.put(f.getAbsolutePath(), handlePlugin(f));
	/*     */     }
	/* 173 */     return builder.build();
	/*     */   }
}
