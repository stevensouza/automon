package org.automon.aspects;


import org.aspectj.lang.annotation.Pointcut;

// Note the @Override annotation was not used below as it will not compile with ajc.
class MyDisabledTestAspect extends AutomonAspect {
    @Pointcut("none()")
    public void sys_monitor() {

    }

    @Pointcut("none()")
    public void user_monitor() {

    }

    @Pointcut("none()")
    public void sys_exceptions() {

    }

    // @Pointcut("!within(java.lang.Object+)")
    @Pointcut("none()")
    public void user_exceptions() {
    }

    @Pointcut("if()")
    public static boolean none() {
        return false;
    }

}