package org.automon.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by stevesouza on 3/4/15.
 */
@Aspect
public class MyDisabledTestAspect extends AutomonAspect {
//public aspect MyDisabledTestAspect extends AutomonAspect {
    @Pointcut("!within(java.lang.Object+)")
    public void sys_monitor() {

    }

    @Pointcut("!within(java.lang.Object+)")
    public void user_monitor() {

    }

    @Pointcut("!within(java.lang.Object+)")
    public void sys_exceptions() {

    }

    @Pointcut("!within(java.lang.Object+)")
    public void user_exceptions() {
    }

//    public pointcut sys_monitor() : disabled();
//    public pointcut user_monitor() : disabled();
//    public pointcut sys_exceptions() : disabled();
//    public pointcut user_exceptions() : disabled();
//
//    public pointcut disabled() : !within(java.lang.Object+);

}
