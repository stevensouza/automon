package org.automon.implementations;

import org.automon.utils.Utils;

import java.util.*;

/**
 * <p>Factory class for creating and managing instances of {@link OpenMon}, which are used for monitoring in Automon.</p>
 * <p>This factory maintains a map of OpenMon implementations, allowing you to retrieve instances by their fully qualified class names
 * or their simple class names (case-insensitive). It also provides a default `OpenMon` implementation to handle cases where
 * the requested implementation cannot be created.</p>
 */
public class OpenMonFactory {

    /**
     * Fully qualified class names for out of the box implementations.
     */
    public static final String JAMON = "org.automon.implementations.Jamon";
    public static final String METRICS = "org.automon.implementations.Metrics";
    public static final String SYSOUT = "org.automon.implementations.SysOut";
    public static final String NULL_IMP = "org.automon.implementations.NullImp";
    public static final String NEW_RELIC = "org.automon.implementations.NewRelicImp";
    public static final String MICROMETER = "org.automon.implementations.Micrometer";

    /**
     * Map storing OpenMon implementations, where keys are both fully qualified and simple class names (lowercase).
     */
    private final Map<String, String> openMonFactoryMap = new HashMap<String, String>();

    /**
     * The default `OpenMon` implementation used when the requested one cannot be created.
     */
    private final OpenMon defaultOpenMon;

    /**
     * Constructs an `OpenMonFactory` with the specified default `OpenMon` implementation.
     * It also adds several pre-installed OpenMon implementations to the factory.
     *
     * @param defaultOpenMon The default `OpenMon` to use when the requested one is not available.
     */
    public OpenMonFactory(OpenMon defaultOpenMon) {
        this.defaultOpenMon = defaultOpenMon;
        add(JAMON);
        add(MICROMETER);
        add(METRICS);
        add(NEW_RELIC);
        add(SYSOUT);
        add(NULL_IMP);
    }

    /**
     * Extracts the simple class name from a fully qualified class name.
     *
     * @param className The fully qualified class name (e.g., "com.example.MyClass").
     * @return The simple class name (e.g., "MyClass").
     */
    static String getJustClassName(String className) {
        String[] array = Utils.tokenize(className, "[.]");
        return array[array.length - 1];
    }

    /**
     * Adds OpenMon implementations to the factory.
     * <p>
     * This method takes fully qualified class names of `OpenMon` implementations and adds them to the factory map
     * in two forms: the full class name and the lowercase simple class name. This allows for retrieving instances
     * using either the fully qualified name or the case-insensitive simple name.
     *
     * @param classNames Fully qualified class names of `OpenMon` implementations to add.
     */
    public void add(String... classNames) {
        for (String className : classNames) {
            className = className.trim();
            openMonFactoryMap.put(className, className); // Add full class name
            openMonFactoryMap.put(getJustClassName(className).toLowerCase(), className); // Add lowercase simple name
        }
    }

    /**
     * Retrieves an instance of the specified `OpenMon` implementation.
     *
     * @param key The fully qualified class name or the case-insensitive simple class name of the `OpenMon` implementation.
     * @return An instance of the requested `OpenMon` implementation, or the default `OpenMon` if creation fails.
     */
    public OpenMon getInstance(String key) {
        String className = openMonFactoryMap.get(key);
        if (className == null) {
            className = openMonFactoryMap.get(key.toLowerCase());
        }

        // If the className is not found, return the default OpenMon
        if (className == null) {
            System.err.println("Disabling Automon from " + getClass() + ": The OpenMon '" + key + "' is not in the map." +
                    " Try one of the following: " + this);
            return defaultOpenMon;
        }

        // Attempt to create the OpenMon instance, returning the default if creation fails
        OpenMon openMon = create(className);
        if (openMon == null) {
            return defaultOpenMon;
        }

        return openMon;
    }

    /**
     * Retrieves the first available pre-installed `OpenMon` instance.
     * <p>
     * This method iterates through the pre-installed `OpenMon` types and attempts to create an instance of each.
     * It returns the first successfully created instance. If all creations fail (likely due to missing dependencies),
     * it returns the default `OpenMon`.
     *
     * @return The first available `OpenMon` instance or the `defaultOpenMon`.
     */
    public OpenMon getFirstInstance() {
        // Note: NewRelicImp is placed last due to a known issue where its exception handling might not be caught correctly
        OpenMon openMon = Utils.createFirst(METRICS, JAMON, MICROMETER, NEW_RELIC);
        if (openMon == null) {
            return defaultOpenMon;
        }

        return openMon;
    }

    /**
     * Returns a string representation of the available OpenMon implementations.
     *
     * @return A comma-separated string of OpenMon keys in alphabetical order, excluding fully qualified class names.
     */
    public String toString() {
        List<String> keys = new ArrayList(openMonFactoryMap.keySet());
        Utils.removeClassNames(keys);
        Collections.sort(keys);
        return keys.toString();
    }

    /**
     * Creates an instance of the specified `OpenMon` implementation using reflection.
     *
     * @param className The fully qualified class name of the `OpenMon` implementation.
     * @return The created `OpenMon` instance, or `null` if creation fails.
     */
    private OpenMon create(String className) {
        try {
            return (OpenMon) Class.forName(className).newInstance();
        } catch (Throwable t) {
            System.err.println("Disabling Automon from " + getClass() + ": Failure in creating: '" + className +
                    "'. Remember a public noarg constructor is required." +
                    " Try one of the following: " + this);
            t.printStackTrace();
            return null;
        }
    }

    /**
     * Clears the `openMonFactoryMap`, removing all registered OpenMon implementations.
     * <p>
     * This method is primarily intended for testing purposes.
     */
    void reset() {
        openMonFactoryMap.clear();
    }
}