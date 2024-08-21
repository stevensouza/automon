package org.automon.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFullContextDataTestClass {
    private final Logger logger = LoggerFactory.getLogger(MyFullContextDataTestClass.class);

    public String firstName(String name) {
        logger.info("In MyFullContextDataTestClass.firstName(..) method");
        hi();
        return name;
    }

    public void hi() {
        logger.info("In MyFullContextDataTestClass.hi() method");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ".toString()";
    }
}
