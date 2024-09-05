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

    public void iMessedUp(String firstName, String lastName) {
        throw new RuntimeException("I really did it this time!");
    }

    public void run(String[] args) throws Exception {
        int loops = (args==null || args.length==0) ? 1 : Integer.valueOf(args[0]);
        System.out.println("start: "+new Date());
        for (int i=0;i<loops;i++) {
            System.out.println("     ** loop "+i+" of "+loops);
            System.out.println("      ** "+getFirstName());
            System.out.println("      ** "+getMiddleName());
            System.out.println("      ** "+getLastName());
            try {
                iMessedUp("Steve", "Souza");
            } catch (RuntimeException exception) {
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
}
