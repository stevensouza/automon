package org.automon.tracing;

/**
 * JMX MBean interface for controlling the tracing aspect.
 */
public interface TraceControlMBean {

    /**
     * Enables or disables tracing.
     *
     * @param enable `true` to enable tracing, `false` to disable.
     */
    void enable(boolean enable);

    /**
     * Checks if tracing is currently enabled.
     *
     * @return `true` if tracing is enabled, `false` otherwise.
     */
    boolean isEnabled();
}