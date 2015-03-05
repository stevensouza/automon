package org.automon.implementations;

import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;
import org.javasimon.SimonManager;
import org.javasimon.Split;

import java.util.List;

/**
 * Created by stevesouza on 2/26/15.
 */
public class JavaSimon extends OpenMonBase<Split> {

    @Override
    public Split start(JoinPoint.StaticPart  jp) {
        return SimonManager.getStopwatch(jp.toString()).start();
    }

    @Override
    public void stop(Split split) {
        split.stop();
    }

    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            SimonManager.getCounter(label).increase();
        }
    }

}
