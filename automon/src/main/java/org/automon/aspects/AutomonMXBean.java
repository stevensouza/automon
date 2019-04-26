package org.automon.aspects;


/**
 * Jmx bean that allows AutomonAspect to be configured from a jmx console like visualvm, or jconsole
 */
public interface AutomonMXBean {
    /**
     * Note if Automon is disabled all of its methods become noops.
     *
     * @return true if Automon is enabled.
     */
    public boolean isEnabled();

    /**
     * Dynamically chang the implementation of {@link org.automon.implementations.OpenMon}.  This also allows Automon to be
     * disabled/enabled at runtime.
     *
     * @param openMon A string such as jamon, metrics, javasimon
     */
    public void setOpenMon(String openMon);

    /**
     * @return The current {@link org.automon.implementations.OpenMon}.
     */
    public String getOpenMon();

    /**
     * @return A list of all registered {@link org.automon.implementations.OpenMon}'s.
     * Values here can be used in {@link org.automon.aspects.AutomonMXBean#setOpenMon(String)}
     */
    public String getValidOpenMons();
}
