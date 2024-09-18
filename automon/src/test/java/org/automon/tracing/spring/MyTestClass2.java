package org.automon.tracing.spring;

/**
 * Note this class has a combination of public, private and Runtime and Checked exceptions
 * in order to properly test pointcuts.
 */
public class MyTestClass2 {

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

    /**
     * Calculates the sum of all the integer arguments passed to it.
     *
     * @param numbers An arbitrary number of integer arguments.
     * @return The sum of all the provided integers.
     */
    public int calculateSum(int... numbers) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ".toString()";
    }


    public static class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }
    }


}
