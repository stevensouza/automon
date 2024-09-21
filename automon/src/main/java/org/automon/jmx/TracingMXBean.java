package org.automon.jmx;

import javax.management.MXBean;

/**
 * JMX MBean interface for controlling the tracing aspect.
 */
@MXBean
public interface TracingMXBean extends EnableMXBean {

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