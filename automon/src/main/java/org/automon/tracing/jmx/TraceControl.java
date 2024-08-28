package org.automon.tracing.jmx;

/**
 * Implementation of the `TraceControlMBean` interface.
 */
public class TraceControl extends AspectControl implements TraceControlMBean {

    private boolean loggingEnabled = true; // Default to logging enabled

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableLogging(boolean enable) {
        loggingEnabled = enable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }
}