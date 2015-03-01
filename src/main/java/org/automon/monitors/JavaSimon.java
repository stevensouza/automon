package org.automon.monitors;

import org.javasimon.SimonManager;
import org.javasimon.Split;

/**
 * Created by stevesouza on 2/26/15.
 */
public class JavaSimon implements OpenMon<Split> {

    @Override
    public Split start(String label) {
        return SimonManager.getStopwatch(label).start();
    }

    @Override
    public void stop(Split split) {
        split.stop();
    }

    @Override
    public void exception(String label) {
        SimonManager.getCounter(label).increase();
    }

}
