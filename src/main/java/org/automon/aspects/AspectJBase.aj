package org.automon.aspects;


/**
 * <p>Can contain pointucut language that is not compatible with Spring.  Use this as your Base class if you AspectJ directly.</p>
 *
 * <p>Note a developer should implement and provide pointcuts that you want to monitor by implementing {@link #user_monitor()}
 * and {@link #user_exceptions()}</p>
 */
public abstract aspect AspectJBase extends AutomonAspect {
    public pointcut sys_monitor() : sys_pointcut();
    public pointcut sys_exceptions() : sys_pointcut();
    public pointcut sys_pointcut() : within(java.lang.Object+) && !within(AutomonAspect+);
}
