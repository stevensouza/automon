package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.monitoring.MonitoringAspect;

@Aspect
public class ConcreteMonitoringAspect extends MonitoringAspect {

  @Pointcut("execution(public * com.stevesouza.helloworld.HelloWorld.*(..))")
  public void select() {
  }

}
