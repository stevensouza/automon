package com.stevesouza.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A simple Spring bean used to demonstrate the integration of Automon for tracing and monitoring. Note
 * if we were using typical spring monitoring (see the spring_aop maven module for an example of that
 * approach) only the execution pointcut type on public methods that are called externally could be monitored/traced.
 * However being as we are using aspectj to weave private methods as well as calls from within the methods themselves.
 * We could also use other pointcut types besides execution such call, constructor-new etc)
 */
public class HelloWorld {

    /**
     * A method that showcases the use of Automon for monitoring Spring applications and includes a greeting.
     * Used to demonstrate tracing/monitoring a private method even though this is a spring app.
     *
     * @return A message indicating that Automon is being used to monitor the 'helloWorld' bean, followed by a greeting.
     */
    private String mySpringMonitoring() {
        return "Spring application using Automon to monitor the 'helloWorld' spring bean. " + hello();
    }

    /**
     * A helper method to provide a greeting. Used to demonstrate internal and private calls can be monitored/traced
     * even though this is a spring app.
     *
     * @return A simple greeting message.
     */
    private String hello() {
        return "Hello!";
    }

    /**
     * Retrieves the first name.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return "Steve";
    }

    /**
     * Retrieves the last name.
     *
     * @return The last name.
     */
    public String getLastName() {
        return "Souza";
    }

    /**
     * A method that intentionally throws a RuntimeException to simulate an error scenario.
     */
    public void iMessedUp() {
        throw new RuntimeException("I really did it this time!");
    }

    /**
     * The main method that demonstrates the usage of the HelloWorld bean and Automon monitoring/tracing.
     *
     * @param args Command-line arguments. The first argument can optionally specify the number of loops to execute.
     * @throws Exception If an unexpected error occurs during execution.
     */
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        HelloWorld hw = (HelloWorld) context.getBean("helloWorld");

        int loops = (args == null || args.length == 0) ? 1 : Integer.parseInt(args[0]); // Change to parseInt
        for (int i = 0; i < loops; i++) {
            System.out.println("   ** loop " + i + " of " + loops);
            System.out.println("    ** " + hw.mySpringMonitoring());
            System.out.println("    ** " + hw.getFirstName());
            System.out.println("    ** " + hw.getLastName());
            try {
                hw.iMessedUp();
            } catch (RuntimeException exception) {
                // hidden exception
            }
            Thread.sleep(1000);
        }
    }
}