package org.automon.spring;

import org.aspectj.lang.Aspects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.*;

/**
<aspectj>
  <aspects>
     <!-- Assume ajia.monitoring.Monitoring is an abstract aspect with
          an abstract monitored() pointcut -->
     <concrete-aspect name="com.myorganization.MyMonitoringAspect" extends="org.automon.aspects.SpringBase">
        <pointcut name="user_monitor"       expression="profile()"/>
        <pointcut name="user_exceptions"    expression="profile()"/>
        <pointcut name="profile" expression="execution(public * com.stevesouza.helloworld.HelloWorld.*(..))"/>
     </concrete-aspect>
  </aspects>
</aspectj>
*/

@Aspect
public class MySpringAspect extends SpringBase {

  @Pointcut("profile()")
  public void user_monitor() {
  }

  @Pointcut("profile()")
  public void user_exceptions() {
  }

  @Pointcut("execution(* *.*(..))")
  //@Pointcut("bean(hellWorld)")
  public void profile() {
  }

}
