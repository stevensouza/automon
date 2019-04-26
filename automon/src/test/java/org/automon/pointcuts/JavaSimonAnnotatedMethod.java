package org.automon.pointcuts;

import org.javasimon.aop.Monitored;

/**
 * Test class that is advised to see if aspects are working properly
 */
public class JavaSimonAnnotatedMethod {

    @Monitored
    public void annotatedMethod() {
    }

    public void nonAnnotatedMethod() {
    }

    @Monitored
    public void myException(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public void myNonAnnotatedException(Throwable throwable) throws Throwable {
        throw throwable;
    }
}
