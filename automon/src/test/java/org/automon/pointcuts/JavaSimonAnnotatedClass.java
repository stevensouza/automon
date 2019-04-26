package org.automon.pointcuts;


import org.javasimon.aop.Monitored;

/**
 * Test class that is advised to see if aspects are working properly
 */
@Monitored
public class JavaSimonAnnotatedClass {

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
