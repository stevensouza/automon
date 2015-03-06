package org.automon.pointcuts;

import com.codahale.metrics.annotation.Timed;

/**
 *  Test class that is advised to see if aspects are working properly
 */
@Timed
public class MetricsAnnotatedClass {

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
