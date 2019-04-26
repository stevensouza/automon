package org.automon.pointcuts;


import org.automon.annotations.Monitor;

/**
 * Test class that is advised to see if aspects are working properly
 */
@Monitor
public class AutomonAnnotatedClass {

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
