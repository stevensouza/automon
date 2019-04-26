package org.automon.pointcuts;

import com.codahale.metrics.annotation.Timed;

/**
 * Test class that is advised to see if aspects are working properly
 */
public class MetricsAnnotatedMethod {

    @Timed
    public void annotatedMethod() {
    }

    public void nonAnnotatedMethod() {
    }

    @Timed
    public void myException(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public void myNonAnnotatedException(Throwable throwable) throws Throwable {
        throw throwable;
    }
}
