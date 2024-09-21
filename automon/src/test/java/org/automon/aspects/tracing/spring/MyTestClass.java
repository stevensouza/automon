package org.automon.aspects.tracing.spring;

/**
 * Note this class has a combination of public, private and Runtime and Checked exceptions
 * in order to properly test pointcuts.
 */
public class MyTestClass {

    public String name() {
        return "name: " + first("steve") + " " + last("souza");
    }

    public void exceptions() {
        try {
            checkedException();
        } catch (Exception e) {
            // gobble
        }

        try {
            runTimeException();
        } catch (RuntimeException e) {
            // gobble
        }
    }

    private String name;

    private String first(String name) {
        this.name = name;
        return this.name;
    }

    String last(String name) {
        return name;
    }

    private void runTimeException() {
        throw new RuntimeException("runTimeException");
    }

    void checkedException() throws Exception {
        throw new MyException("checkedException");
    }

    public static class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }
    }


}
