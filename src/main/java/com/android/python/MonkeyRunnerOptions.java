package com.android.python;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.ImmutableList.Builder;

/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class MonkeyRunnerOptions
/*     */ {
/*  26 */   private static final Logger LOG = Logger.getLogger(MonkeyRunnerOptions.class.getName());
/*  27 */   private static String DEFAULT_MONKEY_SERVER_ADDRESS = "127.0.0.1";
/*  28 */   private static int DEFAULT_MONKEY_PORT = 12345;
/*     */   private final int port;
/*     */   private final String hostname;
/*     */   private final File scriptFile;
/*     */   private final String backend;
/*     */   private final Collection<File> plugins;
/*     */   private final Collection<String> arguments;
/*     */   private final Level logLevel;
/*     */ 
/*     */   private MonkeyRunnerOptions(String hostname, int port, File scriptFile, String backend, Level logLevel, Collection<File> plugins, Collection<String> arguments)
/*     */   {
/*  40 */     this.hostname = hostname;
/*  41 */     this.port = port;
/*  42 */     this.scriptFile = scriptFile;
/*  43 */     this.backend = backend;
/*  44 */     this.logLevel = logLevel;
/*  45 */     this.plugins = plugins;
/*  46 */     this.arguments = arguments;
/*     */   }
/*     */ 
/*     */   public int getPort() {
/*  50 */     return this.port;
/*     */   }
/*     */ 
/*     */   public String getHostname() {
/*  54 */     return this.hostname;
/*     */   }
/*     */ 
/*     */   public File getScriptFile() {
/*  58 */     return this.scriptFile;
/*     */   }
/*     */ 
/*     */   public String getBackendName() {
/*  62 */     return this.backend;
/*     */   }
/*     */ 
/*     */   public Collection<File> getPlugins() {
/*  66 */     return this.plugins;
/*     */   }
/*     */ 
/*     */   public Collection<String> getArguments() {
/*  70 */     return this.arguments;
/*     */   }
/*     */ 
/*     */   public Level getLogLevel() {
/*  74 */     return this.logLevel;
/*     */   }
/*     */ 
/*     */   private static void printUsage(String message) {
/*  78 */     System.out.println(message);
/*  79 */     System.out.println("Usage: monkeyrunner [options] SCRIPT_FILE");
/*  80 */     System.out.println("");
/*  81 */     System.out.println("    -s      MonkeyServer IP Address.");
/*  82 */     System.out.println("    -p      MonkeyServer TCP Port.");
/*  83 */     System.out.println("    -v      MonkeyServer Logging level (ALL, FINEST, FINER, FINE, CONFIG, INFO, WARNING, SEVERE, OFF)");
/*  84 */     System.out.println("");
/*  85 */     System.out.println("");
/*     */   }
/*     */ 
/*     */   public static MonkeyRunnerOptions processOptions(String[] args)
/*     */   {
/*  95 */     int index = 0;
/*     */ 
/*  97 */     String hostname = DEFAULT_MONKEY_SERVER_ADDRESS;
/*  98 */     File scriptFile = null;
/*  99 */     int port = DEFAULT_MONKEY_PORT;
/* 100 */     String backend = "adb";
/* 101 */     Level logLevel = Level.SEVERE;
/*     */ 
/* 103 */     ImmutableList.Builder pluginListBuilder = ImmutableList.builder();
/* 104 */     ImmutableList.Builder argumentBuilder = ImmutableList.builder();
/* 105 */     while (index < args.length) {
	System.out.println("abc:"+args[index]);
/* 106 */       String argument = args[(index++)];
/*     */ 
/* 108 */       if ("-s".equals(argument)) {
/* 109 */         if (index == args.length) {
/* 110 */           printUsage("Missing Server after -s");
/* 111 */           return null;
/*     */         }
/* 113 */         hostname = args[(index++)];
/*     */       }
/* 115 */       else if ("-p".equals(argument))
/*     */       {
/* 117 */         if (index == args.length) {
/* 118 */           printUsage("Missing Server port after -p");
/* 119 */           return null;
/*     */         }
/* 121 */         port = Integer.parseInt(args[(index++)]);
/*     */       }
/* 123 */       else if ("-v".equals(argument))
/*     */       {
/* 125 */         if (index == args.length) {
/* 126 */           printUsage("Missing Log Level after -v");
/* 127 */           return null;
/*     */         }
/*     */ 
/* 130 */         logLevel = Level.parse(args[(index++)]);
/* 131 */       } else if ("-be".equals(argument))
/*     */       {
/* 133 */         if (index == args.length) {
/* 134 */           printUsage("Missing backend name after -be");
/* 135 */           return null;
/*     */         }
/* 137 */         backend = args[(index++)];
/* 138 */       } else if ("-plugin".equals(argument))
/*     */       {
/* 140 */         if (index == args.length) {
/* 141 */           printUsage("Missing plugin path after -plugin");
/* 142 */           return null;
/*     */         }
/* 144 */         File plugin = new File(args[(index++)]);
/* 145 */         if (!plugin.exists()) {
/* 146 */           printUsage("Plugin file doesn't exist");
/* 147 */           return null;
/*     */         }
/*     */ 
/* 150 */         if (!plugin.canRead()) {
/* 151 */           printUsage("Can't read plugin file");
/* 152 */           return null;
/*     */         }
/*     */ 
/* 155 */         pluginListBuilder.add(plugin); 
				} else {
/* 156 */         if ((argument.startsWith("-")) && (scriptFile == null))
/*     */         {
/* 160 */           printUsage("Unrecognized argument: " + argument + ".");
/* 161 */           return null;
/*     */         }
/* 163 */         if (scriptFile == null)
/*     */         {
/* 165 */           scriptFile = new File(argument);
/* 166 */           if (!scriptFile.exists()) {
/* 167 */             printUsage("Can't open specified script file");
/* 168 */             return null;
/*     */           }
/* 170 */           if (!scriptFile.canRead()) {
/* 171 */             printUsage("Can't open specified script file");
/* 172 */             return null;
/*     */           }
/*     */         } else {
/* 175 */           argumentBuilder.add(argument);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 180 */     return new MonkeyRunnerOptions(hostname, port, scriptFile, backend, logLevel, pluginListBuilder.build(), argumentBuilder.build());
/*     */   }
/*     */ }