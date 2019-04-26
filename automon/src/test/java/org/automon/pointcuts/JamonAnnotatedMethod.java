package org.automon.pointcuts;

import com.jamonapi.aop.spring.MonitorAnnotation;

/**
 * Test class that is advised to see if aspects are working properly
 */
public class JamonAnnotatedMethod {

    @MonitorAnnotation
    public void annotatedMethod() {
    }

    public void nonAnnotatedMethod() {
    }

    @MonitorAnnotation
    public void myException(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public void myNonAnnotatedException(Throwable throwable) throws Throwable {
        throw throwable;
    }
}
