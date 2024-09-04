package org.automon.spring_aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by stevesouza on 5/24/14.
 */
public class SpringMain {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        // monitored not with annotation, but in applicationContext.xml
        MonitorMe monitorMe = context.getBean("monitorMe", MonitorMe.class);

        for (int i=0;i<10;i++) {
            monitorMe.myMethod();
            monitorMe.myName("steve", "souza");
        }
        
        try {
           monitorMe.myException();
        } catch (Exception e) {
            
        }

        monitorMe.bye("cruel");
    }
}
