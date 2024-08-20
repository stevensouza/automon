package org.automon.tracing;

public class MyTestClass2 {

    public String name() {
        return "name: " + first("steve") + " " + last("souza");
    }

    public int exceptions() {
        try {
            checkedException("jeff");
        } catch (Exception e) {
            // gobble
        }

        try {
            runTimeException("beck");
        } catch (RuntimeException e) {
            // gobble
        }

        return 2;
    }

    String first(String name) {
        return name;
    }

    String last(String name) {
        return name;
    }

    void runTimeException(String lname) {
        throw new RuntimeException("runTimeException");
    }

    void checkedException(String fname) throws Exception {
        throw new MyException("checkedException");
    }

    public static class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ".toString()";
    }
}
