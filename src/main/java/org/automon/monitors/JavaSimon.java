package org.automon.monitors;

import org.aspectj.lang.JoinPoint;
import org.javasimon.SimonManager;
import org.javasimon.Split;

/**
 * Created by stevesouza on 2/26/15.
 */
public class JavaSimon extends OpenMonBase<Split> {

    @Override
    public Split start(JoinPoint jp) {
        return SimonManager.getStopwatch(getLabel(jp)).start();
    }

    @Override
    public void stop(Split split) {
        split.stop();
    }

    @Override
    public void stop(Split split, Throwable throwable) {
        split.stop();
    }

    @Override
    public void exception(String label) {
        SimonManager.getCounter(label).increase();
    }

}
