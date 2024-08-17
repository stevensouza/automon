package org.automon.aspects;


/**
 * <p>This class contains pointcut syntax that might not be compatible with Spring.  Use this as your Base class if you use
 * AspectJ directly. Because it is not limited in its pointcut syntax as spring is it is more powerful.</p>
 *
 * <p>Note when a developer makes a concrete instance of this class they should provide pointcuts that they want to monitor
 * by implementing {@link AutomonAspect#user_monitor()} and {@link AutomonAspect#user_exceptions()}</p>
 */
public abstract aspect AspectJBase extends AutomonAspect {
    public pointcut _sys_monitor() : _sys_pointcut();

    public pointcut _sys_exceptions() : _sys_pointcut();

    public pointcut _sys_pointcut() : within(java.lang.Object+) && !within(AutomonAspect+);
}
