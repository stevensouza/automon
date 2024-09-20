package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.AutomonAspectJAspect;

@Aspect
public class MonitoringAspect extends AutomonAspectJAspect {

  @Pointcut("execution(public * com.stevesouza.helloworld.HelloWorld.*(..))")
  public void select() {
  }

}
