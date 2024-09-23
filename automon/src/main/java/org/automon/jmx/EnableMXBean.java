package org.automon.jmx;

import javax.management.MXBean;

/**
 * This interface defines the JMX MBean operations for enabling or disabling an aspect at runtime.
 * Aspects implementing this interface can be dynamically controlled through JMX.
 */
@MXBean // Marks this interface as an MXBean for JMX management
public interface EnableMXBean {

    /**
     * Enables or disables the aspect.
     *
     * @param enable `true` to enable the aspect, `false` to disable it.
     */
    void enable(boolean enable);

    /**
     * Checks if the aspect is currently enabled.
     * <p>
     * This method can be used within AspectJ pointcuts to conditionally apply advice only when the aspect is enabled.
     * </p>
     *
     * <p>**Example Pointcut:**</p>
     *
     * <pre>
     * pointcut enabledPointcut() : if(isEnabled()) &amp;&amp; execution(* *(..));
     * </pre>
     *
     * @return `true` if the aspect is enabled, `false` otherwise.
     */
    boolean isEnabled();
}