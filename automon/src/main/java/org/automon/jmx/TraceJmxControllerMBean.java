package org.automon.jmx;

/**
 * JMX MBean interface for controlling the tracing aspect.
 */
public interface TraceJmxControllerMBean extends AspectJmxControllerMBean {

    /**
     * Enables or disables logging in this aspect.
     *
     * @param enabled `true` to enable logging, `false` to disable logging.
     */
    public void enableLogging(boolean enabled);

    /**
     * Gets the current logging enabled status.
     *
     * @return `true` if logging is enabled, `false` otherwise.
     */
    public boolean isLoggingEnabled();
}