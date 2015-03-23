package org.automon.helloworld;

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

    public void iMessedUp() {
        throw new RuntimeException("I really did it this time!");
    }

    public void run(String[] args) throws Exception {
        int loops = (args==null || args.length==0) ? 1 : Integer.valueOf(args[0]);
        for (int i=0;i<loops;i++) {
            System.out.println("     ** loop "+i+" of "+loops);
            System.out.println("      ** "+getFirstName());
            System.out.println("      ** "+getLastName());
            try {
                iMessedUp();
            } catch (RuntimeException excepion) {
                // hidden exception
            }
            Thread.sleep(1000);
        }

    }

    public static void main(String[] args) throws Exception {
        HelloWorld helloWorld = new HelloWorld();
        helloWorld.run(args);
    }
}
