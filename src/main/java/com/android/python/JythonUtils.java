package com.android.python;
/*     */ //import com.android.monkeyrunner.doc.MonkeyRunnerExported;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicate;
/*     */ import com.google.common.base.Predicates;
/*     */ import com.google.common.collect.Collections2;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableMap.Builder;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.text.BreakIterator;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.python.core.ArgParser;
/*     */ import org.python.core.Py;
/*     */ import org.python.core.PyDictionary;
/*     */ import org.python.core.PyFloat;
/*     */ import org.python.core.PyInteger;
/*     */ import org.python.core.PyList;
/*     */ import org.python.core.PyNone;
/*     */ import org.python.core.PyObject;
/*     */ import org.python.core.PyReflectedField;
/*     */ import org.python.core.PyReflectedFunction;
/*     */ import org.python.core.PyString;
/*     */ import org.python.core.PyStringMap;
/*     */ import org.python.core.PyTuple;
/*     */
/*     */ public final class JythonUtils
/*     */ {
/*  62 */   private static final Logger LOG = Logger.getLogger(JythonUtils.class.getCanonicalName());
/*     */   private static final Map<Class<? extends PyObject>, Class<?>> PYOBJECT_TO_JAVA_OBJECT_MAP;
/*     */   private static final Predicate<AccessibleObject> SHOULD_BE_DOCUMENTED;
/*     */   private static final Predicate<Field> IS_FIELD_STATIC;
/*     */
/*     */   public static ArgParser createArgParser(PyObject[] args, String[] kws)
/*     */   {
/*  89 */     StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
/*     */
/*  91 */     StackTraceElement element = stackTrace[2];
/*     */
/*  93 */     String methodName = element.getMethodName();
/*  94 */     String className = element.getClassName();
/*     */     Class clz;
/*     */     try
/*     */     {
/*  98 */       clz = Class.forName(className);
/*     */     } catch (ClassNotFoundException e) {
/* 100 */       LOG.log(Level.SEVERE, "Got exception: ", e);
/* 101 */       return null;
/*     */     }
/*     */     Method m;
/*     */     try
/*     */     {
				Class[] cargs = new Class[2];
				cargs[0] = org.python.core.PyObject[].class;
				cargs[1] = String[].class;

/* 107 */       m = clz.getMethod(methodName, cargs);
/*     */     } catch (SecurityException e) {
/* 109 */       LOG.log(Level.SEVERE, "Got exception: ", e);
/* 110 */       return null;
/*     */     } catch (NoSuchMethodException e) {
/* 112 */       LOG.log(Level.SEVERE, "Got exception: ", e);
/* 113 */       return null;
/*     */     }
/*     */
/* 116 */     MonkeyRunnerExported annotation = (MonkeyRunnerExported)m.getAnnotation(MonkeyRunnerExported.class);
/* 117 */     return new ArgParser(methodName, args, kws, annotation.args());
/*     */   }
/*     */
/*     */   public static double getFloat(ArgParser ap, int position)
/*     */   {
/* 129 */     PyObject arg = ap.getPyObject(position);
/*     */
/* 131 */     if (Py.isInstance(arg, PyFloat.TYPE)) {
/* 132 */       return ((PyFloat)arg).asDouble();
/*     */     }
/* 134 */     if (Py.isInstance(arg, PyInteger.TYPE)) {
/* 135 */       return ((PyInteger)arg).asDouble();
/*     */     }
/* 137 */     throw Py.TypeError("Unable to parse argument: " + position);
/*     */   }
/*     */
/*     */   public static double getFloat(ArgParser ap, int position, double defaultValue)
/*     */   {
/* 149 */     PyObject arg = ap.getPyObject(position, new PyFloat(defaultValue));
/*     */
/* 151 */     if (Py.isInstance(arg, PyFloat.TYPE)) {
/* 152 */       return ((PyFloat)arg).asDouble();
/*     */     }
/* 154 */     if (Py.isInstance(arg, PyInteger.TYPE)) {
/* 155 */       return ((PyInteger)arg).asDouble();
/*     */     }
/* 157 */     throw Py.TypeError("Unable to parse argument: " + position);
/*     */   }
/*     */
/*     */   public static List<Object> getList(ArgParser ap, int position)
/*     */   {
/* 169 */     PyObject arg = ap.getPyObject(position, Py.None);
/* 170 */     if (Py.isInstance(arg, PyNone.TYPE)) {
/* 171 */       return Collections.emptyList();
/*     */     }
/*     */
/* 174 */     List ret = Lists.newArrayList();
/* 175 */     PyList array = (PyList)arg;
/* 176 */     for (int x = 0; x < array.__len__(); x++) {
/* 177 */       PyObject item = array.__getitem__(x);
/*     */
/* 179 */       Class javaClass = (Class)PYOBJECT_TO_JAVA_OBJECT_MAP.get(item.getClass());
/* 180 */       if (javaClass != null) {
/* 181 */         ret.add(item.__tojava__(javaClass));
/*     */       }
/*     */     }
/* 184 */     return ret;
/*     */   }
/*     */
/*     */   public static Map<String, Object> getMap(ArgParser ap, int position)
/*     */   {
/* 196 */     PyObject arg = ap.getPyObject(position, Py.None);
/* 197 */     if (Py.isInstance(arg, PyNone.TYPE)) {
/* 198 */       return Collections.emptyMap();
/*     */     }
/*     */
/* 201 */     Map ret = Maps.newHashMap();
/*     */
/* 203 */     PyDictionary dict = (PyDictionary)arg;
/* 204 */     PyList items = dict.items();
/* 205 */     for (int x = 0; x < items.__len__(); x++)
/*     */     {
/* 207 */       PyTuple item = (PyTuple)items.__getitem__(x);
/*     */
/* 209 */       String key = (String)item.__getitem__(0).__str__().__tojava__(String.class);
/* 210 */       PyObject value = item.__getitem__(1);
/*     */
/* 213 */       Class javaClass = (Class)PYOBJECT_TO_JAVA_OBJECT_MAP.get(value.getClass());
/* 214 */       if (javaClass != null) {
/* 215 */         ret.put(key, value.__tojava__(javaClass));
/*     */       }
/*     */     }
/* 218 */     return ret;
/*     */   }
/*     */
/*     */   private static PyObject convertObject(Object o) {
/* 222 */     if ((o instanceof String))
/* 223 */       return new PyString((String)o);
/* 224 */     if ((o instanceof Double))
/* 225 */       return new PyFloat(((Double)o).doubleValue());
/* 226 */     if ((o instanceof Integer))
/* 227 */       return new PyInteger(((Integer)o).intValue());
/* 228 */     if ((o instanceof Float)) {
/* 229 */       float f = ((Float)o).floatValue();
/* 230 */       return new PyFloat(f);
/*     */     }
/* 232 */     return Py.None;
/*     */   }
/*     */
/*     */   public static PyDictionary convertMapToDict(Map<String, Object> map)
/*     */   {
/* 242 */     Map resultMap = Maps.newHashMap();
/*     */
/* 244 */     for (Entry entry : map.entrySet()) {
/* 245 */       resultMap.put(new PyString((String)entry.getKey()), convertObject(entry.getValue()));
/*     */     }
/*     */
/* 248 */     return new PyDictionary(resultMap);
/*     */   }
/*     */
/*     */   public static void convertDocAnnotationsForClass(Class<?> clz, PyObject dict)
/*     */   {
/* 264 */     Preconditions.checkNotNull(dict);
/* 265 */     Preconditions.checkArgument(dict instanceof PyStringMap);
/*     */
/* 268 */     if (clz.isAnnotationPresent(MonkeyRunnerExported.class)) {
/* 269 */       MonkeyRunnerExported doc = (MonkeyRunnerExported)clz.getAnnotation(MonkeyRunnerExported.class);
/* 270 */       String fullDoc = buildClassDoc(doc, clz);
/* 271 */       dict.__setitem__("__doc__", new PyString(fullDoc));
/*     */     }
/*     */
/* 277 */     Collection functions = Sets.newHashSet();
/* 278 */     for (PyObject item : dict.asIterable()) {
/* 279 */       functions.add(item.toString());
/*     */     }
/*     */
/* 283 */     functions = Collections2.filter(functions, new Predicate<String>()
/*     */     {
/*     */       public boolean apply(String value) {
/* 286 */         return !value.startsWith("__");
/*     */       }
/*     */     });
/* 292 */     for (Method m : clz.getMethods()) {
/* 293 */       if (m.isAnnotationPresent(MonkeyRunnerExported.class)) {
/* 294 */         String methodName = m.getName();
/* 295 */         PyObject pyFunc = dict.__finditem__(methodName);
/* 296 */         if ((pyFunc != null) && ((pyFunc instanceof PyReflectedFunction))) {
/* 297 */           PyReflectedFunction realPyFunc = (PyReflectedFunction)pyFunc;
/* 298 */           MonkeyRunnerExported doc = (MonkeyRunnerExported)m.getAnnotation(MonkeyRunnerExported.class);
/*     */
/* 300 */           realPyFunc.__doc__ = new PyString(buildDoc(doc));
/* 301 */           functions.remove(methodName);
/*     */         }
/*     */       }
/*     */
/*     */     }
/*     */
/* 307 */     for (Field f : clz.getFields()) {
/* 308 */       if (f.isAnnotationPresent(MonkeyRunnerExported.class)) {
/* 309 */         String fieldName = f.getName();
/* 310 */         PyObject pyField = dict.__finditem__(fieldName);
/* 311 */         if ((pyField != null) && ((pyField instanceof PyReflectedField))) {
/* 312 */           PyReflectedField realPyfield = (PyReflectedField)pyField;
/* 313 */           MonkeyRunnerExported doc = (MonkeyRunnerExported)f.getAnnotation(MonkeyRunnerExported.class);
/*     */
/* 318 */           functions.remove(fieldName);
/*     */         }
/*     */       }
/*     */
/*     */     }
/*     */
/* 324 */     for (Object name : functions)
/* 325 */       dict.__delitem__((String)name);
/*     */   }
/*     */
/*     */   private static String buildClassDoc(MonkeyRunnerExported doc, Class<?> clz)
/*     */   {
/* 352 */     Collection annotatedFields = Collections2.filter(Arrays.asList(clz.getFields()), SHOULD_BE_DOCUMENTED);
/* 353 */     Collection staticFields = Collections2.filter(annotatedFields, IS_FIELD_STATIC);
/* 354 */     Collection nonStaticFields = Collections2.filter(annotatedFields, Predicates.not(IS_FIELD_STATIC));
/*     */
/* 356 */     StringBuilder sb = new StringBuilder();
/* 357 */     for (String line : splitString(doc.doc(), 80)) {
/* 358 */       sb.append(line).append("\n");
/*     */     }
/*     */
/* 361 */     if (staticFields.size() > 0) {
/* 362 */       sb.append("\nClass Fields: \n");
/* 363 */       for (Object f : staticFields) {
/* 364 */         sb.append(buildFieldDoc((Field)f));
/*     */       }
/*     */     }
/*     */
/* 368 */     if (nonStaticFields.size() > 0) {
/* 369 */       sb.append("\n\nFields: \n");
/* 370 */       for (Object f : nonStaticFields) {
/* 371 */         sb.append(buildFieldDoc((Field)f));
/*     */       }
/*     */     }
/*     */
/* 375 */     return sb.toString();
/*     */   }
/*     */
/*     */   private static String buildFieldDoc(Field f)
/*     */   {
/* 385 */     MonkeyRunnerExported annotation = (MonkeyRunnerExported)f.getAnnotation(MonkeyRunnerExported.class);
/* 386 */     StringBuilder sb = new StringBuilder();
/* 387 */     int indentOffset = 5 + f.getName().length();
/* 388 */     String indent = makeIndent(indentOffset);
/*     */
/* 390 */     sb.append("  ").append(f.getName()).append(" - ");
/*     */
/* 392 */     boolean first = true;
/* 393 */     for (String line : splitString(annotation.doc(), 80 - indentOffset)) {
/* 394 */       if (first) {
/* 395 */         first = false;
/* 396 */         sb.append(line).append("\n");
/*     */       } else {
/* 398 */         sb.append(indent).append(line).append("\n");
/*     */       }
/*     */
/*     */     }
/*     */
/* 403 */     return sb.toString();
/*     */   }
/*     */
/*     */   private static String buildDoc(MonkeyRunnerExported doc)
/*     */   {
/* 413 */     Collection docs = splitString(doc.doc(), 80);
/* 414 */     StringBuilder sb = new StringBuilder();
/* 415 */     for (Object d : docs) {
/* 416 */       sb.append((String)d).append("\n");
/*     */     }
/*     */
/* 419 */     if ((doc.args() != null) && (doc.args().length > 0)) {
/* 420 */       String[] args = doc.args();
/* 421 */       String[] argDocs = doc.argDocs();
/*     */
/* 423 */       sb.append("\n  Args:\n");
/*     */       boolean first;
/*     */       String indent;
/* 424 */       for (int x = 0; x < doc.args().length; x++) {
/* 425 */         sb.append("    ").append(args[x]);
/* 426 */         if ((argDocs != null) && (argDocs.length > x)) {
/* 427 */           sb.append(" - ");
/* 428 */           int indentOffset = args[x].length() + 3 + 4;
/* 429 */           Collection lines = splitString(argDocs[x], 80 - indentOffset);
/* 430 */           first = true;
/* 431 */           indent = makeIndent(indentOffset);
/* 432 */           for (Object line : lines) {
/* 433 */             if (first) {
/* 434 */               first = false;
/* 435 */               sb.append((String)line).append("\n");
/*     */             } else {
/* 437 */               sb.append(indent).append((String)line).append("\n");
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */
/* 444 */     return sb.toString();
/*     */   }
/*     */
/*     */   private static String makeIndent(int indentOffset) {
/* 448 */     if (indentOffset == 0) {
/* 449 */       return "";
/*     */     }
/* 451 */     StringBuffer sb = new StringBuffer();
/* 452 */     while (indentOffset > 0) {
/* 453 */       sb.append(' ');
/* 454 */       indentOffset--;
/*     */     }
/* 456 */     return sb.toString();
/*     */   }
/*     */
/*     */   private static Collection<String> splitString(String source, int offset) {
/* 460 */     BreakIterator boundary = BreakIterator.getLineInstance();
/* 461 */     boundary.setText(source);
/*     */
/* 463 */     List lines = Lists.newArrayList();
/* 464 */     StringBuilder currentLine = new StringBuilder();
/* 465 */     int start = boundary.first();
/*     */
/* 467 */     int end = boundary.next();
/* 468 */     while (end != -1)
/*     */     {
/* 470 */       String b = source.substring(start, end);
/* 471 */       if (currentLine.length() + b.length() < offset) {
/* 472 */         currentLine.append(b);
/*     */       }
/*     */       else {
/* 475 */         lines.add(currentLine.toString());
/* 476 */         currentLine = new StringBuilder(b);
/*     */       }
/* 469 */       start = end; end = boundary.next();
/*     */     }
/*     */
/* 479 */     lines.add(currentLine.toString());
/* 480 */     return lines;
/*     */   }
/*     */
/*     */   static
/*     */   {
/*  70 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/*     */
/*  72 */     builder.put(PyString.class, String.class);
/*     */
/*  74 */     builder.put(PyFloat.class, Double.class);
/*  75 */     builder.put(PyInteger.class, Integer.class);
/*     */
/*  77 */     PYOBJECT_TO_JAVA_OBJECT_MAP = builder.build();
/*     */
/* 329 */     SHOULD_BE_DOCUMENTED = new Predicate<AccessibleObject>()
/*     */     {
/*     */       public boolean apply(AccessibleObject ao) {
/* 332 */         return ao.isAnnotationPresent(MonkeyRunnerExported.class);
/*     */       }
/*     */     };
/* 335 */     IS_FIELD_STATIC = new Predicate<Field>()
/*     */     {
/*     */       public boolean apply(Field f) {
/* 338 */         return (f.getModifiers() & 0x8) != 0;
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           D:\software\monkeyrunner.jar
 * Qualified Name:     com.android.monkeyrunner.JythonUtils
 * JD-Core Version:    0.6.0
 */