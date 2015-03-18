package org.automon.helloworld;

/**
 * Created by stevesouza on 2/13/15.
 */
public class HelloWorld {

    public String getFirstName() {
        return "Steve";
    }

    public String getLastName() {
        return "Souza";
    }

    public void iMessedUp() {
        throw new RuntimeException("I really did it this time!");
    }

    public static void main(String[] args) throws Exception {
        HelloWorld hw = new HelloWorld();
        int loops = (args==null || args.length==0) ? 1 : Integer.valueOf(args[0]);
        for (int i=0;i<loops;i++) {
            System.out.println("     ** loop "+i+" of "+loops);
            System.out.println("      ** "+hw.getFirstName());
            System.out.println("      ** "+hw.getLastName());
            try {
                hw.iMessedUp();
            } catch (RuntimeException excepion) {
                // hidden exception
            }
            Thread.sleep(1000);
        }
    }
}
