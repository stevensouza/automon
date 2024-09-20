package org.automon.monitoring;

import org.automon.jmx.AutomonMXBean;
import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.Utils;

import java.util.Properties;

/**
 * <p>Base class used in all aspects. Contains automon implementations and registers Automon
 * with jmx</p>
 */

public class BaseMonitoringAspect implements AutomonMXBean {

    /**
     * The value associated with the key 'purpose' in jmx registration.
     */
    protected String purpose = "monitor";
    /**
     * Flag indicating whether tracing is enabled.
     */
    private boolean enabled = true;

    private OpenMonFactory factory = new OpenMonFactory(new NullImp());
    private OpenMon openMon = new NullImp();

    // use the specified Automon implementation 
    private void initOpenMon() {
        Properties properties = Utils.AUTOMON_PROPERTIES.getProperties();
        String openMonStr = properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON);
        // if the openMonString is a fully qualified classname then also register it in the factory i.e. com.mygreatcompany.MyOpenMon
        if (Utils.hasPackageName(openMonStr)) {
            factory.add(openMonStr);
        }
        setOpenMon(openMonStr);
    }

    protected void initialize(String purpose, boolean enable) {
        // Use the OpenMon the user selects and register the aspect with jmx
        enable(enable);
        initOpenMon();
        setPurpose(purpose);
        registerJmxController();
    }

    /**
     * Registers the JMX controller associated with this aspect.
     * <p>
     * This method utilizes the `Utils.registerWithJmx` utility to register the JMX controller with the platform MBeanServer,
     * using the current `purpose` as part of the MBean's ObjectName.
     */
    protected void registerJmxController() {
        Utils.registerWithJmx(getPurpose(), this, this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable(boolean enable) {
        this.enabled = enable;
    }

    /**
     * Retrieve current monitoring implementation
     */
    public OpenMon getOpenMon() {
        return openMon;
    }

    @Override
    public String getOpenMonString() {
        return openMon.toString();
    }

    /**
     * Set monitoring implementation such as JAMon, Metrics, or JavaSimon
     *
     * @param openMon
     */
    public void setOpenMon(OpenMon openMon) {
        this.openMon = openMon;
    }

    /**
     * Take the string of any {@link org.automon.implementations.OpenMon} registered within this classes
     * {@link org.automon.implementations.OpenMonFactory}, instantiate it and make it the current OpenMon.  If null is passed
     * in then use the default of iterating each of the preinstalled OpenMon types attempting to create them until one succeeds.
     * If one doesn't succeed then it would mean the proper jar is not available. If all of these fail then simply disable.
     *
     * @param openMonKey Something like jamon, metrics...
     */
    @Override
    public void setOpenMon(String openMonKey) {
        if (openMonKey == null || openMonKey.trim().isEmpty()) {
            this.openMon = factory.getFirstInstance();
        } else {
            this.openMon = factory.getInstance(openMonKey);
        }
    }

    public OpenMonFactory getOpenMonFactory() {
        return factory;
    }

    @Override
    public String getValidOpenMons() {
        return factory.toString();
    }

    /**
     * Gets the purpose associated with this JMX registration.
     *
     * @return The value associated with the key 'purpose' in JMX registration.
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose associated with this JMX registration.
     *
     * @param purpose The value to be associated with the key 'purpose' in JMX registration.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

}
