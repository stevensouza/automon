package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Pointcuts defined for various standard/basic pointcuts such as method, and constructor invocations as well as setter/getter methods.
 * They should be reused in other aspects.
 *
 */
@Aspect
public abstract class Select {

    /** Note this should cover everything.  I had problems with jdk 1.8 if I also included preinitialization so I got rid of this one */
    @Pointcut("!preinitialization(*.new(..))")
    public void all() {

    }

    @Pointcut("if()")
    public static boolean none() {
        return false;
    }

    /** Method execution pointcuts
     */
    @Pointcut("execution(* java.lang.Object.*(..))")
    public void objectMethod() {
    }


    @Pointcut("execution(* *(..))")
    public void method() {

    }

    @Pointcut("execution(public * *.*(..))")
    public void publicMethod() {

    }

    @Pointcut("execution(private * *.*(..))")
    public void privateMethod() {

    }

    @Pointcut("execution(protected * *.*(..))")
    public void protectedMethod() {

    }

    @Pointcut("method() && !privateMethod() && !protectedMethod() && !publicMethod()")
    public void packageMethod() {

    }

    @Pointcut("execution(public void *.set*(*))")
    public void setter() {
    }

    @Pointcut("execution(public * *.get*())")
    public void getter() {
    }

    @Pointcut("getter() || setter()")
    public void getterOrSetter() {

    }

    /**
     * Note constructor is just like method except it doesn't allow a return type.
     */
    @Pointcut("execution(*.new(..))")
    public void constructor() {

    }

    @Pointcut("execution(public *.new(..))")
    public void publicConstructor() {

    }

    @Pointcut("execution(private *.new(..))")
    public void privateConstructor() {

    }

    @Pointcut("execution(protected *.new(..))")
    public void protectedConstructor() {

    }

    @Pointcut("constructor() && !privateConstructor() && !protectedConstructor() && !publicConstructor()")
    public void packageConstructor() {

    }


}
