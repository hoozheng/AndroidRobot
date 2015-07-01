package com.android.python;
/*     */ 
/*     */ import com.google.common.collect.Maps;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Formatter;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogRecord;
/*     */ 
/*     */ public class MonkeyFormatter extends Formatter
/*     */ {
/*  34 */   public static final Formatter DEFAULT_INSTANCE = new MonkeyFormatter();
/*     */ 
/*  36 */   private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyMMdd HH:mm:ss.SSS");
/*     */ 
/*  38 */   private static Map<Level, String> LEVEL_TO_STRING_CACHE = Maps.newHashMap();
/*     */ 
/*     */   private static final String levelToString(Level level) {
/*  41 */     String levelName = (String)LEVEL_TO_STRING_CACHE.get(level);
/*  42 */     if (levelName == null) {
/*  43 */       levelName = level.getName().substring(0, 1);
/*  44 */       LEVEL_TO_STRING_CACHE.put(level, levelName);
/*     */     }
/*  46 */     return levelName;
/*     */   }
/*     */ 
/*     */   private static String getHeader(LogRecord record) {
/*  50 */     StringBuilder sb = new StringBuilder();
/*     */ 
/*  52 */     sb.append(FORMAT.format(new Date(record.getMillis()))).append(":");
/*  53 */     sb.append(levelToString(record.getLevel())).append(" ");
/*     */ 
/*  55 */     sb.append("[").append(Thread.currentThread().getName()).append("] ");
/*     */ 
/*  57 */     String loggerName = record.getLoggerName();
/*  58 */     if (loggerName != null) {
/*  59 */       sb.append("[").append(loggerName).append("]");
/*     */     }
/*  61 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public String format(LogRecord record)
/*     */   {
/*  98 */     Throwable thrown = record.getThrown();
/*  99 */     String header = getHeader(record);
/*     */ 
/* 101 */     StringBuilder sb = new StringBuilder();
/* 102 */     sb.append(header);
/* 103 */     sb.append(" ").append(formatMessage(record));
/* 104 */     sb.append("\n");
/*     */ 
/* 107 */     if (thrown != null)
/*     */     {
/* 109 */       PrintWriter pw = new PrintWriterWithHeader(header);
/* 110 */       thrown.printStackTrace(pw);
/* 111 */       sb.append(pw.toString());
/*     */     }
/*     */ 
/* 114 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private class PrintWriterWithHeader extends PrintWriter
/*     */   {
/*     */     private final ByteArrayOutputStream out;
/*     */     private final String header;
/*     */ 
/*     */     public PrintWriterWithHeader(String header)
/*     */     {
/*  69 */       this(header, new ByteArrayOutputStream());
/*     */     }
/*     */ 
/*     */     public PrintWriterWithHeader(String header, ByteArrayOutputStream out) {
/*  73 */      	super(out,true);				
/*  74 */       this.header = header;
/*  75 */       this.out = out;
/*     */     }
/*     */ 
/*     */     public void println(Object x)
/*     */     {
/*  80 */       print(this.header);
/*  81 */       super.println(x);
/*     */     }
/*     */ 
/*     */     public void println(String x)
/*     */     {
/*  86 */       print(this.header);
/*  87 */       super.println(x);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  92 */       return this.out.toString();
/*     */     }
/*     */   }
/*     */ }
