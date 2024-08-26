package org.automon.tracing;

/**
 * Implementation of the `TraceControlMBean` interface.
 */
public class TraceControl implements TraceControlMBean {

    /**
     * Flag indicating whether tracing is enabled.
     */
    private boolean enabled = false; 

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}