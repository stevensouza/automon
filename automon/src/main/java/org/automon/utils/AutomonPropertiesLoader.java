package org.automon.utils;

import java.io.*;
import java.util.Properties;

/**
 * <p>This class is responsible for loading Automon properties using a prioritized strategy.</p>
 *
 * <p>The loading order is as follows:</p>
 * <ol>
 *     <li>System properties passed via the command line (e.g., `-DdistributedDataRefreshRateInMinutes=10`)</li>
 *     <li>Properties defined in a configuration file (searched in the order specified during instantiation)</li>
 *     <li>Default properties if no file is found or properties are missing</li>
 * </ol>
 */
public class AutomonPropertiesLoader {

    /**
     * Property key for the configured OpenMon implementation.
     */
    public static final String CONFIGURED_OPEN_MON = "org.automon";
    /**
     * Default value for the configured OpenMon implementation (empty string).
     */
    private static final String EMPTY_DEFAULT_OPEN_MON = "";
    /**
     * AspectJ system property specifying the XML configuration file.
     */
    private static final String ASPECTJ_CONFIG_FILE = "org.aspectj.weaver.loadtime.configuration";
    /**
     * Default properties file name.
     */
    private static final String DEFAULT_PROPS_CONFIG_FILE = "automon.properties";
    /**
     * First default XML configuration file name.
     */
    private static final String DEFAULT_XML_CONFIG_FILE1 = "ajc-aop.xml";
    /**
     * Second default XML configuration file name.
     */
    private static final String DEFAULT_XML_CONFIG_FILE2 = "aop.xml";
    /**
     * Provides access to system properties (for testability).
     */
    SysProperty sysProperty = new SysProperty();
    /**
     * Array of file names to search for configuration properties.
     */
    private final String[] fileNames;
    /**
     * The `Properties` object holding Automon-specific properties.
     */
    private Properties automonProps;
    /**
     * Flag indicating if a configuration file was found.
     */
    private boolean configFileFound = false;

    /**
     * Constructs an `AutomonPropertiesLoader` with default file names and system properties.
     * It attempts to load properties from the AspectJ configuration file specified by the system property `org.aspectj.weaver.loadtime.configuration`.
     * If that property is not set, it falls back to searching for `automon.properties`, `ajc-aop.xml`, and `aop.xml` in that order.
     */
    public AutomonPropertiesLoader() {
        this(System.getProperty(ASPECTJ_CONFIG_FILE, null), DEFAULT_PROPS_CONFIG_FILE, DEFAULT_XML_CONFIG_FILE1, DEFAULT_XML_CONFIG_FILE2);
    }

    /**
     * Constructs an `AutomonPropertiesLoader` with the specified file names.
     *
     * @param fileNames List of file names to search for configuration properties, checked in order.
     *                  If none are found, defaults are used.
     */
    public AutomonPropertiesLoader(String... fileNames) {
        this.fileNames = fileNames;
    }

    /**
     * Constructs an `AutomonPropertiesLoader` for testing purposes, allowing injection of a custom `SysProperty` implementation.
     *
     * @param sysProperty A custom `SysProperty` implementation for testing.
     * @param fileNames   List of file names to search for configuration properties.
     */
    AutomonPropertiesLoader(SysProperty sysProperty, String... fileNames) {
        this(fileNames);
        this.sysProperty = sysProperty;
    }

    /**
     * Loads Automon properties using the prioritized strategy.
     *
     * @return The loaded `Properties` object containing Automon properties.
     */
    public Properties getProperties() {
        if (automonProps == null) {
            initialize();
        }
        return automonProps;
    }

    /**
     * Retrieves a boolean property from the loaded Automon properties.
     *
     * @param key The property key.
     * @return `true` if the property value is "true" (case-insensitive), `false` otherwise.
     * Defaults to `true` if the property is not found.
     */
    public boolean getBoolean(String key) {
        if (automonProps == null) {
            initialize();
        }

        String propertyValue = automonProps.getProperty(key, "true");
        return propertyValue.equalsIgnoreCase("true");
    }

    /**
     * Initializes the `automonProps` by loading properties from files and system properties,
     * with system properties taking precedence.
     */
    void initialize() {
        // Note: Precedence is -D properties, then from the file, then defaults.
        Properties defaults = getDefaults();
        Properties userProvided = propertyLoader(fileNames);
        replaceWithSystemProps(userProvided);
        automonProps = new Properties(defaults);
        automonProps.putAll(userProvided);
    }

    /**
     * Loads properties from the specified file names in order, stopping at the first file that is found and
     * can be loaded successfully.
     *
     * @param fileNames The array of file names to load properties from
     * @return The loaded `Properties` object, or an empty `Properties` object if no files were found or loaded successfully
     */
    private Properties propertyLoader(String[] fileNames) {
        Properties properties = new Properties();
        for (String fileName : fileNames) {
            if (fileName != null) { // Handle the case where ASPECTJ_CONFIG_FILE wasn't passed
                properties = propertyLoader(Utils.stripFileScheme(fileName)); // AspectJ command line props start with 'file:'
                if (configFileFound) {
                    return properties;
                }
            }
        }

        configFileFound = false;
        return properties;
    }

    /**
     * Attempts to load properties from the specified file.
     *
     * @param fileName The name of the file to load properties from
     * @return The loaded `Properties` object, or an empty `Properties` object if the file was not found or could not be loaded
     */
    private Properties propertyLoader(String fileName) {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            // Attempt to load properties from the file
            input = getConfigFileInputStream(fileName);
            if (input != null) {
                properties.load(input);
                configFileFound = true;
            }
        } catch (Throwable t) {
            // Ignore exceptions and proceed with loading from system properties or defaults
            String message = String.format("error reading file=%s, exception=%s", fileName, t);
            System.out.println(message);
        } finally {
            close(input);
        }

        return properties;
    }

    /**
     * Obtains an InputStream for reading a configuration file.
     *
     * <p>This method first checks if the specified file exists at the given path
     * (if it's an absolute path) or in the current working directory (if it's a relative path).
     * If it does, it creates a {@link BufferedInputStream} to read the file efficiently.
     * If the file is not found in the current directory, it attempts to load it as a resource
     * from the classpath using the class loader.</p>
     *
     * @param fileName The name of the configuration file.
     * @return An InputStream for reading the configuration file, or {@code null} if the file cannot be found.
     * @throws FileNotFoundException If the file exists but cannot be opened for reading.
     */
    private InputStream getConfigFileInputStream(String fileName) throws FileNotFoundException {
        InputStream input = null;
        if (new File(fileName).exists()) {
            input = new BufferedInputStream(new FileInputStream(fileName));
        } else {
            input = getClass().getClassLoader().getResourceAsStream(fileName);
        }
        return input;
    }

    /**
     * Closes the given `InputStream`.
     *
     * @param input The `InputStream` to close.
     */
    void close(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Overrides properties with values from system properties.
     *
     * @param properties The `Properties` object to update.
     */
    private void replaceWithSystemProps(Properties properties) {
        properties.putAll(sysProperty.getProperties());
    }

    /**
     * Returns the default properties to be used when no configuration file is found.
     *
     * @return A `Properties` object containing the default Automon properties.
     */
    Properties getDefaults() {
        Properties defaults = new Properties();
        defaults.put(CONFIGURED_OPEN_MON, EMPTY_DEFAULT_OPEN_MON);
        return defaults;
    }

    /**
     * <p>This is a nested class that provides access to system properties.</p>
     * <p>It is designed to be easily mockable in unit tests, allowing for controlled testing of the property loading behavior.</p>
     */
    static class SysProperty {

        /**
         * Retrieves the value of the specified system property.
         *
         * @param key The name of the system property.
         * @return The string value of the system property, or `null` if not found.
         */
        public String getProperty(String key) {
            return System.getProperty(key);
        }

        /**
         * Retrieves all system properties.
         *
         * @return A `Properties` object containing all system properties.
         */
        public Properties getProperties() {
            return System.getProperties();
        }
    }
}