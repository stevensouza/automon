package org.automon.pointcuts;

import com.jamonapi.aop.spring.MonitorAnnotation;
/**
 *  Test class that is advised to see if aspects are working properly
 */
@MonitorAnnotation
public class JamonAnnotatedClass {

    public void annotatedClass_method1() {
    }

    public void annotatedClass_method2() {
    }

    protected void nonPublicMethod() {

    }

    public void myException(Throwable throwable) throws Throwable {
        throw throwable;
    }
}
