package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>Pointcuts defined for various standard/basic pointcuts such as method, and constructor invocations as well as setter/getter methods.
 * They should be reused in other aspects.  This class extends {@link org.automon.pointcuts.SpringSelect} to add pointcuts not valid in
 * Spring, but valid in AspectJ as a whole.</p>
 *
 * <p>Note remember that this interface extends {@link org.automon.pointcuts.SpringSelect} and so also has all of the available
 * pointcuts in that interface.</p>
 */
@Aspect
public abstract class Select extends SpringSelect {

    /**
     * Note this should cover everything.  I had problems with jdk 1.8 if I also included preinitialization so I got rid of this one
     */
    @Pointcut("!preinitialization(*.new(..))")
    public void all() {

    }

    @Pointcut("if()")
    public static boolean none() {
        return false;
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

    /**
     * Field set pointcuts
     */

    @Pointcut("set(* *.*)")
    public void fieldSet() {

    }

    @Pointcut("set(private * *.*)")
    public void privateFieldSet() {

    }

    @Pointcut("set(protected * *.*)")
    public void protectedFieldSet() {

    }

    @Pointcut("set(public * *.*)")
    public void publicFieldSet() {

    }

    @Pointcut("fieldSet() && !privateFieldSet() && !protectedFieldSet() && !publicFieldSet()")
    public void packageFieldSet() {

    }

    /**
     * Field get pointcuts
     */

    @Pointcut("get(* *.*)")
    public void fieldGet() {

    }

    @Pointcut("get(private * *.*)")
    public void privateFieldGet() {

    }

    @Pointcut("get(protected * *.*)")
    public void protectedFieldGet() {

    }

    @Pointcut("get(public * *.*)")
    public void publicFieldGet() {

    }

    @Pointcut("fieldGet() && !privateFieldGet() && !protectedFieldGet() && !publicFieldGet()")
    public void packageFieldGet() {

    }


}
