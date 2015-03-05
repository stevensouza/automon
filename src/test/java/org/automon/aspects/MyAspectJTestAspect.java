package org.automon.aspects;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by stevesouza on 3/4/15.
 */

// public aspect MyAspectJTestAspect extends AspectJBase {
@Aspect
public class MyAspectJTestAspect extends AspectJBase {

    // Note this(HelloWorld) only gets instance accesses (not static).  within(HelloWorld) would also get static
    // accesses to fields and methods.
//    public pointcut user_monitor() : this(HelloWorld) && (org.automon.pointcuts.Select.constructor() ||
//            org.automon.pointcuts.Select.publicMethod() ||
//            org.automon.pointcuts.Select.fieldGet() ||
//            org.automon.pointcuts.Select.fieldSet()
//           );

    @Pointcut("this(HelloWorld) && (org.automon.pointcuts.Select.constructor() || " +
            "org.automon.pointcuts.Select.publicMethod() || " +
            "org.automon.pointcuts.Select.fieldGet() || " +
            "org.automon.pointcuts.Select.fieldSet()  " +
            " ) " )
    public void user_monitor() {

    }

    //public pointcut user_exceptions() : this(HelloWorld) && org.automon.pointcuts.Select.publicMethod();
    @Pointcut("this(HelloWorld) && org.automon.pointcuts.Select.publicMethod()")
    public void user_exceptions() {
    }
}
