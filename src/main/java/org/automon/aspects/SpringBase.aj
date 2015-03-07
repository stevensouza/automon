package org.automon.aspects;

/**
 * <p>This aspect should contain pointucut language that is compatible with Spring.  Use this as your Base class if you use Spring.</p>
 *
 * <p>Note a developer should implement and provide pointcuts that you want to monitor by implementing {@link #user_monitor()}
 * and {@link #user_exceptions()}</p>
 */
public abstract aspect SpringBase extends AutomonAspect {
    public pointcut _sys_monitor() : _sys_pointcut();

    public pointcut _sys_exceptions() : _sys_pointcut();

    // Note in a native aspect the full path isn't needed and it could be taken care of in the import statement,
    // however intellij doesn't register that import statement as being used and it will be removed if
    // a optimize imports command is done, so I am being explicit below.
    public pointcut _sys_pointcut() : org.automon.pointcuts.Select.publicMethod() && !within(AutomonAspect+);
}
