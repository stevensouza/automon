package org.automon.jmx;

public interface AspectJmxControllerMBean {
    /**
     * Enables/disables the aspect.
     *
     */
    public void enable(boolean enable);

    /**
     * Checks if tracing is currently enabled via the JMX MBean. Note
     * this method can be embedded in pointcuts in the following way:
     *  <pre>
     *    pointcut enabledPointcut() : if(isEnabled()) &amp;&amp; execution(* *(..));
     *  </pre>
     *
     * @return `true` if tracing is enabled, `false` otherwise.
     */
    public boolean isEnabled();
}
