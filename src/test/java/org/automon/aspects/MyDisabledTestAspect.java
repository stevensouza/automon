package org.automon.aspects;


import org.aspectj.lang.annotation.Pointcut;

// Note the @Override annotation was not used below as it will not compile with ajc.
class MyDisabledTestAspect extends AutomonAspect {
    @Pointcut("if(false)")
    public void sys_monitor() {

    }

    @Pointcut("if(false)")
    public void user_monitor() {

    }

    @Pointcut("if(false)")
    public void sys_exceptions() {

    }

    // @Pointcut("!within(java.lang.Object+)")
    @Pointcut("if(false)")
    public void user_exceptions() {
    }

}