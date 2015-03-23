package com.stevesouza.jamon;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.MonitorFactory;
import com.jamonapi.jmx.JmxUtils;
import com.stevesouza.helloworld.HelloWorld;

/**
 * Created by stevesouza on 2/13/15.
 */
public class JamonHelloWorld extends HelloWorld {

    public JamonHelloWorld() throws Exception {
        /** Init jamon */
        JamonPropertiesLoader loader = new JamonPropertiesLoader();
        MonitorFactory.addListeners(loader.getListeners());
        JmxUtils.registerMbeans();
    }

    public String hiSteve() {
        return "hi steve";
    }

    public static void main(String[] args) throws Exception {
        JamonHelloWorld helloWorld = new JamonHelloWorld();
        System.out.println(helloWorld.hiSteve());
        helloWorld.run(args);
        System.out.println(MonitorFactory.getReport());
    }
}
