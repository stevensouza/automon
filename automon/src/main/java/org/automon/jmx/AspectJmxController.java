package org.automon.jmx;

public class AspectJmxController implements AspectJmxControllerMBean {
    /**
     * Flag indicating whether tracing is enabled.
     */
    private boolean enabled = true;

    /**
     * {@inheritDoc}
     */
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return enabled;
    }
}
