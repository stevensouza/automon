package org.automon.implentations;

import org.automon.OpenMon;
import org.javasimon.Counter;
import org.javasimon.SimonManager;
import org.javasimon.Split;

/**
 * Created by stevesouza on 2/26/15.
 */
public class Simon implements OpenMon<Split> {
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

    @Override
    public void enable(boolean enable) {
        if (enable) {
            SimonManager.enable();
        } else {
            SimonManager.disable();
        }
    }

    @Override
    public boolean isEnabled() {
        return SimonManager.isEnabled();
    }
}
