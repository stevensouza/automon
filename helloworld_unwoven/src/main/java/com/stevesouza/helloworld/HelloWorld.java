package com.stevesouza.helloworld;

import java.util.Date;

/**
 * Program used as basis for simple Automon example programs
 */
public class HelloWorld {

    public String getFirstName() {
        return "Steve";
    }

    public String getLastName() {
        return "Souza";
    }

    private String getMiddleName() {
        return "Thomas";
    }

    private void iMessedUp_RuntimeException(String firstName, String lastName) {
        throw new RuntimeException("I really did it this time!");
    }

    private static void iMessedUp_CheckedException(String message) throws MyCheckedException {
        throw new MyCheckedException(message);
    }

    public void run(String[] args) throws InterruptedException {
        int loops = (args==null || args.length==0) ? 1 : Integer.valueOf(args[0]);
        System.out.println("start: "+new Date());
        for (int i=0;i<loops;i++) {
            System.out.println("     ** loop "+i+" of "+loops);
            System.out.println("      ** "+getFirstName());
            System.out.println("      ** "+getMiddleName());
            System.out.println("      ** "+getLastName());
            try {
                iMessedUp_RuntimeException("Steve", "Souza");
            } catch (RuntimeException exception) {
                // hidden exception
            }

            try {
                iMessedUp_CheckedException("This is my CheckedException message - Steve");
            } catch (MyCheckedException e) {
                // hidden exception
            }

            Thread.sleep(1000);
        }
        System.out.println("end: "+new Date());
    }

    public static void main(String[] args) throws Exception {
        HelloWorld helloWorld = new HelloWorld();
        helloWorld.run(args);
    }

    public static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }
}
