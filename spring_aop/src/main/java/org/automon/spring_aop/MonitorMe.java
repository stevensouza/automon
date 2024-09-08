package org.automon.spring_aop;

import org.springframework.stereotype.Component;

/**
 * Created by stevesouza on 6/2/14.
 */
@Component
public class MonitorMe {

    public void myMethod() throws Exception {
        Thread.sleep(150);
        System.out.println(" exiting MonitorMe.myMethod() issued directly from MonitorMe class");
    }


    public void myName(String fname, String lname) {
       System.out.println(" exiting MonitorMe.myName(...) issued directly from MonitorMe class");
    }
    
    public void myException()  throws Exception {
           throw new Exception("testing exceptions");
    }

    public String bye(String message) {

        return message+" world "+hello();
    }

    private String hello() {
        return "hello";
    }
}
