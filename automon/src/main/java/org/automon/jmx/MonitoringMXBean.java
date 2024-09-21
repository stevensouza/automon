package org.automon.jmx;


import javax.management.MXBean;

/**
 * Jmx bean that allows MonitoringAspect to be configured from a jmx console like visualvm, or jconsole
 */
@MXBean
public interface MonitoringMXBean extends EnableMXBean {

    /**
     * Dynamically change the implementation of {@link org.automon.implementations.OpenMon}.  This also allows Automon to be
     * disabled/enabled at runtime.
     *
     * @param openMon A string such as jamon, metrics, ...
     */
    public void setOpenMon(String openMon);

    /**
     * @return The current {@link org.automon.implementations.OpenMon}.
     */
    public String getOpenMonString();

    /**
     * @return A list of all registered {@link org.automon.implementations.OpenMon}'s.
     * Values here can be used in {@link MonitoringMXBean#setOpenMon(String)}
     */
    public String getValidOpenMons();
}
