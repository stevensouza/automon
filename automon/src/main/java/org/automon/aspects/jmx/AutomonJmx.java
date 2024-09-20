package org.automon.aspects.jmx;

import org.automon.aspects.BaseMonitoringAspect;

/**
 * Registers Automon aspects with jmx so they can be managed and viewed.
 */
public class AutomonJmx implements AutomonMXBean {

    /**
     * Flag indicating whether tracing is enabled.
     */
    private boolean enabled = true;

    // aspect to register with jmx
    private BaseMonitoringAspect baseMonitoringAspect;

    public AutomonJmx(BaseMonitoringAspect baseMonitoringAspect) {
        this.baseMonitoringAspect = baseMonitoringAspect;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    @Override
    public void setOpenMon(String openMonKey) {
        baseMonitoringAspect.setOpenMon(openMonKey);
    }

    @Override
    public String getOpenMonString() {
        return baseMonitoringAspect.getOpenMon().toString();
    }

    @Override
    public String getValidOpenMons() {
        return baseMonitoringAspect.getOpenMonFactory().toString();
    }
}
