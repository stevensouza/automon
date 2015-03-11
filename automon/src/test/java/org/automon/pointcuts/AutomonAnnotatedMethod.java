package org.automon.pointcuts;


import org.automon.annotations.Monitor;

/**
 *  Test class that is advised to see if aspects are working properly
 */
public class AutomonAnnotatedMethod {
    private String string;

    public AutomonAnnotatedMethod() {

    }

    @Monitor
    public AutomonAnnotatedMethod(String annotatedConstructor) {
        this.string=annotatedConstructor;
    }

    @Monitor
    public void annotatedMethod() {
    }

    public void nonAnnotatedMethod() {
    }

    @Monitor
    public void myException(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public void myNonAnnotatedException(Throwable throwable) throws Throwable {
        throw throwable;
    }
}
