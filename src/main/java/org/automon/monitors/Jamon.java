package org.automon.monitors;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
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
        mon.getMonKey().setDetails(throwable);
        put(throwable);
    }

    @Override
    public void exceptionImp(JoinPoint jp, Throwable throwable) {
        MonKey key = new MonKeyImp(getLabel(throwable), throwable, "Exception");
        MonitorFactory.add(key, 1);
    }

}
