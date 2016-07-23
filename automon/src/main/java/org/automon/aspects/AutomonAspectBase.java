/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.aspects;

import java.util.Properties;
import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.Utils;

/**
 * <p>Base class used in all aspects</p>
 */

public  class AutomonAspectBase  {
    
    private OpenMonFactory factory = new OpenMonFactory(new NullImp());
    private OpenMon openMon = new NullImp();
    private AutomonMXBean automonJmx = new AutomonJmx(this);


    public AutomonAspectBase() {
        // Use OpenMon the user selects and register the aspect with jmx
        initOpenMon();
        Utils.registerWithJmx(this, automonJmx);
    }

    private void initOpenMon() {
        Properties properties = new AutomonPropertiesLoader().getProperties();
        String openMonStr = properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON);
        // if the openMonString is a fully qualified classname then also register it in the factory i.e. com.mygreatcompany.MyOpenMon
        if (Utils.hasPackageName(openMonStr)) {
            factory.add(openMonStr);
        }
        setOpenMon(openMonStr);
    }

    /* methods */
    public boolean isEnabled() {
        return !(openMon instanceof NullImp);
    }

    /** Retrieve monitoring implementation
     * @return  */
    public OpenMon getOpenMon() {
        return openMon;
    }

    /** Set monitoring implementation such as JAMon, Metrics, or JavaSimon
     * @param openMon */
    public void setOpenMon(OpenMon openMon) {
        this.openMon = openMon;
    }

    /**
     * Take the string of any {@link org.automon.implementations.OpenMon} registered within this classes
     * {@link org.automon.implementations.OpenMonFactory}, instantiate it and make it the current OpenMon.  If null is passed
     * in then use the default of iterating each of the preinstalled OpenMon types attempting to create them until one succeeds.
     * If one doesn't succeed then it would mean the proper jar is not available. If all of these fail then simply disable.
     *
     * @param openMonKey Something like jamon, metrics, javasimon
     */
    public void setOpenMon(String openMonKey) {
        if (openMonKey==null || openMonKey.trim().equals("")) {
            this.openMon = factory.getFirstInstance();
        } else {
            this.openMon = factory.getInstance(openMonKey);
        }
    }

    public OpenMonFactory getOpenMonFactory() {
        return factory;
    }


}
