package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>This AspectJ aspect defines a collection of standard pointcuts that are compatible with Spring AOP.</p>
 * <p>These pointcuts primarily focus on method execution join points and can be reused in other aspects to simplify the
 * monitoring or tracing of specific code elements within Spring applications.</p>
 *
 * <p>For more general pointcuts that are valid in the broader context of AspectJ (beyond Spring AOP),
 * refer to the {@link org.automon.pointcuts.Select} class.</p>
 */
@Aspect // Indicates that this class is an AspectJ aspect
public abstract class SpringSelect {

    /**
     * Pointcut that matches the execution of any method on any `java.lang.Object`.
     */
    @Pointcut("execution(* java.lang.Object.*(..))")
    public void objectMethod() {
    }

    /**
     * Pointcut that matches the execution of any method.
     */
    @Pointcut("execution(* *(..))")
    public void method() {
    }

    /**
     * Pointcut that matches the execution of any public method.
     */
    @Pointcut("execution(public * *.*(..))")
    public void publicMethod() {
    }

    /**
     * Pointcut that matches the execution of any private method.
     */
    @Pointcut("execution(private * *.*(..))")
    public void privateMethod() {
    }

    /**
     * Pointcut that matches the execution of any protected method.
     */
    @Pointcut("execution(protected * *.*(..))")
    public void protectedMethod() {
    }

    /**
     * Pointcut that matches the execution of any package-private (default visibility) method.
     */
    @Pointcut("method() && !privateMethod() && !protectedMethod() && !publicMethod()")
    public void packageMethod() {
    }

    /**
     * Pointcuts for getter and setter methods
     */

    /**
     * Pointcut that matches the execution of any public setter method (methods starting with 'set' and taking one argument).
     */
    @Pointcut("execution(public void *.set*(*))")
    public void setter() {
    }

    /**
     * Pointcut that matches the execution of any public getter method (methods starting with 'get' and taking no arguments).
     */
    @Pointcut("execution(public * *.get*())")
    public void getter() {
    }

    /**
     * Pointcut that matches the execution of any public getter or setter method.
     */
    @Pointcut("getter() || setter()")
    public void getterOrSetter() {
    }

    /**
     * Pointcut that matches any join point within the `org.automon` package and its subpackages.
     */
    @Pointcut("within(org.automon..*)")
    public void automon() {
    }
}
