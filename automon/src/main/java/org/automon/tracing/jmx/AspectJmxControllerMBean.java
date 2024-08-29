package org.automon.tracing.jmx;

public interface AspectJmxControllerMBean {
    /**
     * Enables/disables the aspect.
     *
     * @return `true` if the aspect is enabled, `false` otherwise.
     */
    public void enable(boolean enable);

    /**
     * Checks if tracing is currently enabled via the JMX MBean. Note
     * this method can be embedded in pointcuts in the following way:
     *  <pre>
     *    pointcut enabledPointcut() : if(isEnabled()) && execution(* *(..));
     *  </pre>
     *
     * @return `true` if tracing is enabled, `false` otherwise.
     */
    public boolean isEnabled();
}
