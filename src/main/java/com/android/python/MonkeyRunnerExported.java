package com.android.python;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD})
public @interface MonkeyRunnerExported
{
  public abstract String doc();

  public abstract String[] args();

  public abstract String[] argDocs();

  public abstract String returns();
}
