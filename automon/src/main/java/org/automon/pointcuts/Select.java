package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>This AspectJ aspect defines standard pointcuts for various common scenarios, including:</p>
 * <ul>
 *     <li>Method invocations</li>
 *     <li>Constructor invocations</li>
 *     <li>Field setting (setters)</li>
 *     <li>Field getting (getters)</li>
 * </ul>
 *
 * <p>These pointcuts can be reused in other aspects to simplify the monitoring or tracing of specific code elements.</p>
 *
 * <p>This class extends `SpringSelect` to include pointcuts that are valid in AspectJ but not supported in Spring AOP.</p>
 *
 * <p>**Note:** This class also inherits all pointcuts defined in the `SpringSelect` interface.</p>
 */
@Aspect // Indicates that this class is an AspectJ aspect
public abstract class Select extends SpringSelect {

    /**
     * <p>Pointcut that matches all join points except for pre-initialization of new objects (`*.new(..)`).</p>
     * <p>This pointcut was modified to exclude pre-initialization to avoid issues with JDK 1.8.</p>
     */
    @Pointcut("!preinitialization(*.new(..))")
    public void all() {
    }

    /**
     * <p>Pointcut that matches no join points.</p>
     * <p>This pointcut always evaluates to `false` and can be used to disable monitoring or tracing in specific scenarios.</p>
     *
     * @return Always returns `false`.
     */
    @Pointcut("if()")
    public static boolean none() {
        return false;
    }

    /**
     * Pointcut that matches all constructor invocations.
     */
    @Pointcut("execution(*.new(..))")
    public void constructor() {
    }

    /**
     * Pointcut that matches all public constructor invocations.
     */
    @Pointcut("execution(public *.new(..))")
    public void publicConstructor() {
    }

    /**
     * Pointcut that matches all private constructor invocations.
     */
    @Pointcut("execution(private *.new(..))")
    public void privateConstructor() {
    }

    /**
     * Pointcut that matches all protected constructor invocations.
     */
    @Pointcut("execution(protected *.new(..))")
    public void protectedConstructor() {
    }

    /**
     * Pointcut that matches all package-private (default visibility) constructor invocations.
     */
    @Pointcut("constructor() && !privateConstructor() && !protectedConstructor() && !publicConstructor()")
    public void packageConstructor() {
    }

    /**
     * Pointcut that matches all field set (setter) operations.
     */
    @Pointcut("set(* *.*)")
    public void fieldSet() {
    }

    /**
     * Pointcut that matches all private field set (setter) operations.
     */
    @Pointcut("set(private * *.*)")
    public void privateFieldSet() {
    }

    /**
     * Pointcut that matches all protected field set (setter) operations.
     */
    @Pointcut("set(protected * *.*)")
    public void protectedFieldSet() {
    }

    /**
     * Pointcut that matches all public field set (setter) operations.
     */
    @Pointcut("set(public * *.*)")
    public void publicFieldSet() {
    }

    /**
     * Pointcut that matches all package-private (default visibility) field set (setter) operations.
     */
    @Pointcut("fieldSet() && !privateFieldSet() && !protectedFieldSet() && !publicFieldSet()")
    public void packageFieldSet() {
    }

    /**
     * Pointcut that matches all field get (getter) operations.
     */
    @Pointcut("get(* *.*)")
    public void fieldGet() {
    }

    /**
     * Pointcut that matches all private field get (getter) operations.
     */
    @Pointcut("get(private * *.*)")
    public void privateFieldGet() {
    }

    /**
     * Pointcut that matches all protected field get (getter) operations.
     */
    @Pointcut("get(protected * *.*)")
    public void protectedFieldGet() {
    }

    /**
     * Pointcut that matches all public field get (getter) operations.
     */
    @Pointcut("get(public * *.*)")
    public void publicFieldGet() {
    }

    /**
     * Pointcut that matches all package-private (default visibility) field get (getter) operations.
     */
    @Pointcut("fieldGet() && !privateFieldGet() && !protectedFieldGet() && !publicFieldGet()")
    public void packageFieldGet() {
    }
}
