package org.automon.aspects;


/**
 * Contains 'if()' which is not compatible with Spring so this should be used when using AspectJ directly.  Note this allows for disabling of
 * AutoMon via the pointcut language.
 */
//@Aspect
public abstract aspect AspectJBase extends AutomonAspect {
    public pointcut sys_monitor() : sys_pointcut();
    public pointcut sys_exceptions() : sys_pointcut();
    public pointcut sys_pointcut() : within(java.lang.Object+) && !within(AutomonAspect+);

//    @Pointcut("sys_pointcut()")
//    public void sys_monitor() {
//
//    }

//    @Pointcut("sys_pointcut()")
//    public void sys_exceptions() {
//
//    }
//
//    @Pointcut("within(java.lang.Object+) && !within(AutomonAspect+)")
//    public void sys_pointcut() {
//
//    }

}
