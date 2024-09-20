package org.automon.jmx;

/**
 * Implementation of the `TraceJmxControllerMBean` interface.
 */
public class TraceJmxController extends AspectJmxController implements TraceJmxControllerMBean {

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