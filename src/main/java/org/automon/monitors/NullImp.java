package org.automon.monitors;

/**
 * Created by stevesouza on 2/26/15.
 */
public final class NullImp implements OpenMon<Object> {
    private static final Object NOOP = new Object();

    @Override
    public Object start(String label) {
        return NOOP;
    }

    @Override
    public void stop(Object timer) {

    }

    @Override
    public void exception(String label) {

    }
}
