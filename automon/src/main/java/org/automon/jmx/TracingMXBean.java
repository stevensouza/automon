package org.automon.jmx;

import javax.management.MXBean;

/**
 * This is a JMX MBean interface that provides control over the tracing aspect's tracing/logging functionality at runtime.
 * It allows you to enable or disable tracing/logging and check the current logging status.
 */
@MXBean // Marks this interface as an MXBean for JMX management
public interface TracingMXBean extends EnableMXBean {

    /**
     * Enables or disables tracing/logging for the tracing aspect.
     *
     * @param enabled `true` to enable logging, `false` to disable logging.
     */
    void enableLogging(boolean enabled);

    /**
     * Checks if tracing/logging is currently enabled for the tracing aspect.
     *
     * @return `true` if logging is enabled, `false` otherwise.
     */
    boolean isLoggingEnabled();
}