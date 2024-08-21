package org.automon.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyRequestTestClass {
    private final Logger logger = LoggerFactory.getLogger(MyRequestTestClass.class);

    public String firstName(String name) {
        logger.info("In MyRequestTestClass.firstName(..) method");
        hi();
        return name;
    }

    public void hi() {
        logger.info("In MyRequestTestClass.hi() method");
    }
}
