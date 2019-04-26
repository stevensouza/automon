package org.automon.aspects;

/**
 * Test class that is advised to see if aspects are working properly
 */
public class HiWorld {
    public String hello() {
        return "hiworld";
    }

    public void myException(Throwable throwable) throws Throwable {
        throw throwable;
    }
}
