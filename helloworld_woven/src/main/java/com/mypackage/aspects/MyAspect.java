package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.SpringBase;

/**
<aspectj>
  <aspects>
     <concrete-aspect name="com.myorganization.MyMonitoringAspect" extends="org.automon.aspects.SpringBase">
        <pointcut name="user_monitor"       expression="profile()"/>
        <pointcut name="user_exceptions"    expression="profile()"/>
        <pointcut name="profile" expression="execution(public * com.stevesouza.helloworld.HelloWorld.*(..))"/>
     </concrete-aspect>
  </aspects>
</aspectj>
*/

@Aspect
public class MyAspect extends SpringBase {

  @Pointcut("profile()")
  public void user_monitor() {
  }

  @Pointcut("profile()")
  public void user_exceptions() {
  }

  @Pointcut("execution(public * com.stevesouza.helloworld.HelloWorld.*(..))")
  public void profile() {
  }

}
