package org.automon.monitors;

/**
 * Created by stevesouza on 2/26/15.
 */
public final class SysOut implements OpenMon<Object> {
    private static final Object NOOP = new Object();

    @Override
    public Object start(String label) {
        System.out.println("SysOut.start(..): "+label);
        return NOOP;
    }

    @Override
    public void stop(Object timer) {
        System.out.println("SysOut.stop(..): "+timer);

    }

    @Override
    public void exception(String label) {
        System.out.println("SysOut.exception(..): "+label);
    }
}
