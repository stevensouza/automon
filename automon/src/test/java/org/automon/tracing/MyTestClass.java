package org.automon.tracing;

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

    String first(String name) {
        return name;
    }

    String last(String name) {
        return name;
    }

    void runTimeException() {
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
