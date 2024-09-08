package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.SpringBase;

/**
 * <pre>
 * <aspectj>
 * <aspects>
 * <concrete-aspect name="com.myorganization.MyMonitoringAspect" extends="org.automon.aspects.SpringBase">
 * <pointcut name="user_monitor"       expression="profile()"/>
 * <pointcut name="user_exceptions"    expression="profile()"/>
 * <pointcut name="profile" expression="execution(public * com.stevesouza.helloworld.HelloWorld.*(..))"/>
 * </concrete-aspect>
 * </aspects>
 * </aspectj>
 * </pre>
 */

@Aspect
public class MyAspect extends SpringBase {
//public class MyAspect  {

    @Pointcut("profile()")
    public void user_monitor() {
    }

    @Pointcut("profile()")
    public void user_exceptions() {
    }

    @Pointcut("execution(* com.stevesouza.helloworld.HelloWorld.*(..))")
    public void profile() {
    }

}
