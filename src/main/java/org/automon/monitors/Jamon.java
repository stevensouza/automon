package org.automon.monitors;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;

/**
 * Created by stevesouza on 2/26/15.
 */
public class Jamon extends OpenMonBase<Monitor> {

    @Override
    public Monitor start(JoinPoint jp) {
        return MonitorFactory.start(getLabel(jp));
    }

    @Override
    public void stop(Monitor mon) {
        mon.stop();
    }

    @Override
    public void stop(Monitor mon, Throwable throwable) {
        mon.stop();
    }

    @Override
    public void exception(String label) {
        MonitorFactory.add(label, "Exception", 1);
    }

}
