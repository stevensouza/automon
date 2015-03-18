package org.automon.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Used to test automon and spring.
 */
public class HelloWorld {

    public String mySpringMonitoring() {
      return "Spring applicaiton using automon to monitor the 'helloWorld' spring bean.";
    }

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
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        HelloWorld hw = (HelloWorld) context.getBean("helloWorld");

        int loops = (args==null || args.length==0) ? 1 : Integer.valueOf(args[0]);
        for (int i=0;i<loops;i++) {
            System.out.println("   ** loop "+i+" of "+loops);
            System.out.println("   ** "+hw.mySpringMonitoring());
            System.out.println("   ** "+hw.getFirstName());
            System.out.println("   ** "+hw.getLastName());
            try {
                hw.iMessedUp();
            } catch (RuntimeException excepion) {
                // hidden exception
            }
            Thread.sleep(1000);
        }
    }
}
