package org.automon.implementations;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;

import java.util.List;

/**
 * Created by stevesouza on 2/26/15.
 */
public class Jamon extends OpenMonBase<Monitor> {

    @Override
    public Monitor start(JoinPoint jp) {
        return MonitorFactory.start(Utils.getLabel(jp));
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
    protected void trackException(JoinPoint jp, Throwable throwable) {
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            MonKey key = new MonKeyImp(label, throwable, "Exception");
            MonitorFactory.add(key, 1);        }

    }

}
