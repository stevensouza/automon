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

    private static final String DEFAULT_PROPS_CONFIG_FILE="automon.properties";
    private static final String DEFAULT_XML_CONFIG_FILE1="ajc-aop.xml";
    private static final String DEFAULT_XML_CONFIG_FILE2="aop.xml";

    public static final String CONFIGURED_OPEN_MON  = "org.automon";

    private boolean configFileFound=false;

    public AutomonPropertiesLoader() {
        this(DEFAULT_PROPS_CONFIG_FILE, DEFAULT_XML_CONFIG_FILE1, DEFAULT_XML_CONFIG_FILE2);
    }

    AutomonPropertiesLoader(String... fileNames) {
        this.fileNames = fileNames;
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

    public URL getPropertiesDirectory() {
        return getClass().getClassLoader().getResource(".");
    }


    private Properties propertyLoader(String[] fileNames) {
        Properties properties = new Properties();
        for (String fileName : fileNames) {
            properties = propertyLoader(fileName);
            if (configFileFound) {
                return properties;
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
            }

            configFileFound = true;
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
            String value = System.getProperty(key.toString());
            if (value != null) {
                properties.put(key, value);
            }
        }

    }

    Properties getDefaults() {
        Properties defaults = new Properties();
        defaults.put(CONFIGURED_OPEN_MON, OpenMonFactory.NULL_IMP);
        return defaults;
    }

}
