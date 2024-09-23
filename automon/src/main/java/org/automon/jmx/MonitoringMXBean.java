package org.automon.jmx;

import javax.management.MXBean;

/**
 * <p>This is a JMX MBean interface that allows for the dynamic configuration of the `MonitoringAspect` at runtime.</p>
 * <p>It provides operations to change the monitoring implementation, enable/disable monitoring, and retrieve information about available implementations.</p>
 * <p>This MBean can be accessed and managed using JMX consoles like VisualVM or JConsole.</p>
 */
@MXBean // Designates this interface as an MXBean for JMX management
public interface MonitoringMXBean extends EnableMXBean {

    /**
     * Dynamically changes the implementation of `OpenMon` used for monitoring.
     * This also allows enabling or disabling Automon monitoring at runtime.
     *
     * @param openMon A string representing the desired `OpenMon` implementation (e.g., "jamon", "metrics").
     */
    void setOpenMon(String openMon);

    /**
     * Retrieves the string representation of the currently active `OpenMon` implementation.
     *
     * @return The string representation of the current `OpenMon`.
     */
    String getOpenMonString();

    /**
     * Returns a list of all registered `OpenMon` implementations.
     * <p>
     * The values returned by this method can be used as input to the `setOpenMon(String)` method
     * to dynamically switch the monitoring implementation.
     * </p>
     *
     * @return A comma-separated list of valid `OpenMon` keys.
     */
    String getValidOpenMons();
}
