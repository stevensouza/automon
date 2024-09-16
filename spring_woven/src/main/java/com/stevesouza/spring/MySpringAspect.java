package com.stevesouza.spring;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.SpringBase;

@Aspect
public class MySpringAspect extends SpringBase {

  @Pointcut("profile()")
  public void user_monitor() {
  }

  @Pointcut("profile()")
  public void user_exceptions() {
  }

  @Pointcut("execution(* HelloWorld.*(..))")
  //@Pointcut("bean(hellWorld)")
  public void profile() {
  }

}
