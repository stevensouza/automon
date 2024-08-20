package org.automon.tracing;

public class MyTestClass {

    public String name() {
        return "name: " + first("steve") + " " + last("souza");
    }

    public String first(String name) {
        return name;
    }

    public String last(String name) {
        return name;
    }

    public void runTimeException() {
        throw new RuntimeException("runTimeException");
    }

    public void checkedException() throws Exception {
        throw new MyException("checkedException");
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

    public static class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }
    }


}
