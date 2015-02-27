package org.automon;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * aspectj compile time weaving using ajc
 *  cd /Users/stevesouza/gitrepo/testproject/playground/src/main/javastyle
 *  ajc -source 5 com/stevesouza/aspectj/*.javastyle
 *  javastyle com.stevesouza.aspectj.MessageCommunicator
 *
 * for nonaspectj version:
 *  javac com/stevesouza/aspectj/*.javastyle
 *
 */
@Aspect
public abstract class AbstractAspect {
    private int callDepth;

    @Pointcut()
    public abstract void traced();

    @Pointcut("!preinitialization(*.new(..))")
    public void all() {

    }

    @Pointcut("execution(* java.lang.Object.*(..))")
    public void objectMethod() {
    }


    // change method() to methodExecution()
    //  publicMethod to publicMethodExecution etc.
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


    @Before("traced()")
    // note use of JoinPoint to get further info here
    public void before(JoinPoint thisJoinPoint) {
        print("Before", thisJoinPoint);
        callDepth++;
    }


    @After("traced()")
    // note use of JoinPoint to get further info here
    public void after(JoinPoint thisJoinPoint) {
        print("After", thisJoinPoint);
        callDepth--;
    }

    // Convenient for quickly disabling advice.
    @AfterReturning("if(false)")
    public void afterDisabledExample() {

    }

    protected void print(String prefix, Object message) {
        for (int i=0; i<callDepth;i++) {
            System.out.print(" ");
        }
        System.out.println(prefix+"("+getClass()+ "):"+message);
    }
}
