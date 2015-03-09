package org.automon.utils;

import org.automon.implementations.OpenMonFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Load Automon properties.  The order of loading is to look in the file named jamonapi.properties in the classpath.
 * Next look for system properties passed to the command line: (-DdistributedDataRefreshRateInMinutes=10).
 * These take precedence over the file.  If properties aren't in the file or passed in via the command line
 * then use defaults.
 *
 */
public class AutomonPropertiesLoader {

    private String[] fileNames;
    private Properties automonProps;

    private static final String ASPECTJ_CONFIG_FILE="org.aspectj.weaver.loadtime.configuration";
    private static final String DEFAULT_PROPS_CONFIG_FILE="automon.properties";
    private static final String DEFAULT_XML_CONFIG_FILE1="ajc-aop.xml";
    private static final String DEFAULT_XML_CONFIG_FILE2="aop.xml";

    public static final String CONFIGURED_OPEN_MON  = "org.automon";
    static final String DEFAULT_OPEN_MON = OpenMonFactory.NULL_IMP;

    private boolean configFileFound=false;

    // Simply gets system properties but put in class so it can be mocked in a test.
    SysProperty sysProperty = new SysProperty();

    public AutomonPropertiesLoader() {
        this(System.getProperty(ASPECTJ_CONFIG_FILE, null), DEFAULT_PROPS_CONFIG_FILE, DEFAULT_XML_CONFIG_FILE1, DEFAULT_XML_CONFIG_FILE2);
    }

    AutomonPropertiesLoader(String... fileNames) {
        this.fileNames = fileNames;
    }

    AutomonPropertiesLoader(SysProperty sysProperty, String... fileNames) {
        this(fileNames);
        this.sysProperty = sysProperty;
    }



    /** Using logic documented in the class comments load properties.  Note it can't fail as in the worst case
     * it loads defaults.
     *
     * @return
     * @throws java.io.IOException
     */
    public Properties getProperties() {
        if (automonProps == null) {
            initialize();
        }
        return automonProps;
    }


    void initialize() {
        // note precedence is -D properties, then from the file, then defaults.
        Properties defaults = getDefaults();
        Properties userProvided = propertyLoader(fileNames);
        replaceWithCommandLineProps(userProvided, defaults);
        automonProps = new Properties(defaults);
        automonProps.putAll(userProvided);
    }


    private Properties propertyLoader(String[] fileNames) {
        Properties properties = new Properties();
        for (String fileName : fileNames) {
            if (fileName != null) { // happens if ASPECTJ_CONFIG_FILE wasn't passed
                properties = propertyLoader(Utils.stripFileScheme(fileName)); // aspectJ command line props start with file:
                if (configFileFound) {
                    return properties;
                }
            }
        }

        configFileFound = false;
        return properties;
    }


    private Properties propertyLoader(String fileName)  {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream(fileName);
            if (input!=null) {
                properties.load(input);
                configFileFound = true;
            }
        } catch (Throwable t) {
            // want to ignore exception and proceed with loading with CLI props or defaults.
        } finally{
            close(input);
        }

        return properties;
    }

    void close(InputStream input) {
        try {
          if (input!=null) {
            input.close();
           }
        } catch (Throwable t) {

        }
    }

    private  void replaceWithCommandLineProps(Properties properties, Properties defaults) {
        for (Object key : defaults.keySet()) {
            String value = sysProperty.getProperty(key.toString());
            if (value != null) {
                properties.put(key, value);
            }
        }

    }

    Properties getDefaults() {
        Properties defaults = new Properties();
        defaults.put(CONFIGURED_OPEN_MON, DEFAULT_OPEN_MON);
        return defaults;
    }

    static class SysProperty {
        public String getProperty(String key) {
            return System.getProperty(key.toString());
        }
    }

}
