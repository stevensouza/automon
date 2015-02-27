package org.automon;

/**
 * Created by stevesouza on 2/26/15.
 */
public interface OpenMon<T extends Object> {
    public T start(String label);
    public void stop(T timer);
    public void exception(String label);
    public void enable(boolean enable);
    public boolean isEnabled();
}
