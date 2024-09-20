package org.automon.jmx;



/**
 * Jmx bean that allows MonitoringAspect to be configured from a jmx console like visualvm, or jconsole
 */
public interface AutomonMXBean  {
    /**
     * Note if Automon is disabled all of its methods become noops.
     *
     * @return true if Automon is enabled.
     */
    public boolean isEnabled();

    /**
     * Enables/disables the aspect.
     *
     */
    public void enable(boolean enable);

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
     * Values here can be used in {@link AutomonMXBean#setOpenMon(String)}
     */
    public String getValidOpenMons();
}
