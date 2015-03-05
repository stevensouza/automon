package org.automon.aspects;

/**
 * Created by stevesouza on 2/28/15.
 */
//@Aspect
public abstract aspect SpringBase extends AutomonAspect {

    public pointcut sys_monitor() : sys_pointcut();
    public pointcut sys_exceptions() : sys_pointcut();
    // note technically the full path isn't needed and it could be taken care of in the import statement,
    // however intellij doesn't register that import statement as being used and it will be removed if
    // a optimize imports command is done, so I am being explicit below.
    public pointcut sys_pointcut() : org.automon.pointcuts.Select.publicMethod() && !within(AutomonAspect+);

//    @Pointcut("sys_pointcut()")
//    public void sys_monitor() {
//
//    }
//
//    @Pointcut("sys_pointcut()")
//    public void sys_exceptions() {
//
//    }
//
//    @Pointcut("org.automon.pointcuts.Select.publicMethod() && !within(AutomonAspect+)")
//    public void sys_pointcut() {
//
//    }
}
