package org.automon.aspects.monitoring;

import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.jmx.MonitoringMXBean;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.Utils;

import java.util.Properties;

/**
 * Base class for all monitoring aspects in Automon.
 * <p>
 * It provides core functionalities for monitoring method executions and exceptions, including:
 * <ul>
 *     <li>Managing Automon implementations (e.g., Jamon, Metrics, JavaSimon)</li>
 *     <li>Registering the aspect with JMX for dynamic configuration</li>
 *     <li>Enabling/disabling monitoring at runtime</li>
 * </ul>
 */
public class BaseMonitoringAspect implements MonitoringMXBean {

    /**
     * The purpose associated with this JMX registration (default: "monitor").
     */
    protected String purpose = "monitor";

    /**
     * Flag indicating whether monitoring is enabled (default: true).
     */
    private boolean enabled = true;

    /**
     * Factory for creating `OpenMon` instances.
     */
    private OpenMonFactory factory = new OpenMonFactory(new NullImp());

    /**
     * The current monitoring implementation (default: NullImp).
     */
    private OpenMon openMon = new NullImp();

    /**
     * Initializes the OpenMon implementation based on user configuration or defaults.
     */
    private void initOpenMon() {
        Properties properties = Utils.AUTOMON_PROPERTIES.getProperties();
        String openMonStr = properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON);
        if (Utils.hasPackageName(openMonStr)) {
            factory.add(openMonStr);
        }
        setOpenMon(openMonStr);
    }

    /**
     * Initializes the aspect with the specified purpose and enabled state.
     * Also initializes the OpenMon implementation and registers the aspect with JMX.
     *
     * @param purpose The purpose of the aspect for JMX registration.
     * @param enable  Whether monitoring is initially enabled.
     */
    protected void initialize(String purpose, boolean enable) {
        enable(enable);
        initOpenMon();
        setPurpose(purpose);
        registerJmxController();
    }

    /**
     * Registers the JMX controller associated with this aspect.
     */
    protected void registerJmxController() {
        Utils.registerWithJmx(getPurpose(), this, this);
    }

    /**
     * Checks if monitoring is currently enabled.
     *
     * @return `true` if monitoring is enabled, `false` otherwise.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables monitoring at runtime.
     *
     * @param enable `true` to enable monitoring, `false` to disable.
     */
    @Override
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    /**
     * Retrieves the current monitoring implementation.
     *
     * @return The `OpenMon` instance used for monitoring.
     */
    public OpenMon getOpenMon() {
        return openMon;
    }

    /**
     * Gets the string representation of the current `OpenMon` implementation.
     *
     * @return The string representation of the `OpenMon`.
     */
    @Override
    public String getOpenMonString() {
        return openMon.toString();
    }

    /**
     * Sets the monitoring implementation using the provided `OpenMon` instance.
     *
     * @param openMon The `OpenMon` implementation to use for monitoring.
     */
    public void setOpenMon(OpenMon openMon) {
        this.openMon = openMon;
    }

    /**
     * Sets the monitoring implementation using the provided `openMonKey`.
     * If the key is null or empty, it attempts to create the first available pre-installed OpenMon.
     * If none can be created, it defaults to `NullImp` which disables monitoring.
     *
     * @param openMonKey The key (e.g., "jamon", "metrics") representing the desired `OpenMon` implementation.
     */
    @Override
    public void setOpenMon(String openMonKey) {
        if (openMonKey == null || openMonKey.trim().isEmpty()) {
            this.openMon = factory.getFirstInstance();
        } else {
            this.openMon = factory.getInstance(openMonKey);
        }
    }

    /**
     * Retrieves the `OpenMonFactory` used to create `OpenMon` instances.
     *
     * @return The `OpenMonFactory`.
     */
    public OpenMonFactory getOpenMonFactory() {
        return factory;
    }

    /**
     * Gets a comma-separated string of valid OpenMon keys.
     *
     * @return A string representing the valid OpenMon keys.
     */
    @Override
    public String getValidOpenMons() {
        return factory.toString();
    }

    /**
     * Gets the purpose associated with this JMX registration.
     *
     * @return The purpose string.
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose associated with this JMX registration.
     *
     * @param purpose The purpose string.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}