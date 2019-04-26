package org.automon.aspects;

/**
 * Registers Automon aspects with jmx so they can be managed and viewed.
 */
public class AutomonJmx implements AutomonMXBean {

    // aspect to register with jmx
    private AutomonAspectBase automonAspectBase;

    public AutomonJmx(AutomonAspectBase automonAspectBase) {
        this.automonAspectBase = automonAspectBase;
    }

    @Override
    public boolean isEnabled() {
        return automonAspectBase.isEnabled();
    }

    @Override
    public void setOpenMon(String openMonKey) {
        automonAspectBase.setOpenMon(openMonKey);
    }

    @Override
    public String getOpenMon() {
        return automonAspectBase.getOpenMon().toString();
    }

    @Override
    public String getValidOpenMons() {
        return automonAspectBase.getOpenMonFactory().toString();
    }
}
