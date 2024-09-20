package com.stevesouza.spring;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.AutomonAspectJAspect;

@Aspect
public class MySpringAspect extends AutomonAspectJAspect {

  @Pointcut("execution(* HelloWorld.*(..))")
  //@Pointcut("bean(hellWorld)")
  public void select() {
  }

}
