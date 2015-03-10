package org.automon.implementations;

import org.automon.utils.Utils;

import java.util.*;

/**
 * Class that holds {@link org.automon.implementations.OpenMon} implementations.  The factor contains the string class name of the impementation
 * and is creates the instance with the required noarg constructor.  Note the instance can be created using the full class name with
 * package or just the class name.  When using just the class name form it is case insensitive.  For example all of the following forms
 * are acceptable:<br>
 *     * {@link org.automon.implementations.Jamon}<br>
 *     * JAMon<br>
 *     * jamon<br>
 *
 *
 */
public class OpenMonFactory {

    public static final String JAMON =  "org.automon.implementations.Jamon";
    public static final String JAVA_SIMON =  "org.automon.implementations.JavaSimon";
    public static final String METRICS =  "org.automon.implementations.Metrics";
    public static final String SYSOUT =  "org.automon.implementations.SysOut";
    public static final String NULL_IMP =  "org.automon.implementations.NullImp";

    private  Map<String, String> openMonFactory = new HashMap<String,  String>();
    private OpenMon defaultOpenMon;

    public OpenMonFactory(OpenMon defaultOpenMon) {
        this.defaultOpenMon = defaultOpenMon;
        add(JAMON);
        add(JAVA_SIMON);
        add(METRICS);
        add(SYSOUT);
        add(NULL_IMP);
    }

    /**
     * Takes a fully qualified class name and puts it in the factory in 2 forms.  The full form (i.e. org.automon.implementations.Jamon), and
     * a shortened form 'Jamon'.
     *
     * @param classNames Example: org.automon.implementations.Jamon
     */
    public void add(String... classNames) {
        for (String className : classNames) {
            className =  className.trim();
            openMonFactory.put(className, className);
            openMonFactory.put(getJustClassName(className).toLowerCase(), className);
        }
    }

    // takes something like com.mypackage.MyClass and returns MyClass.
    static String getJustClassName(String className) {
        String[] array = Utils.tokenize(className, "[.]");
        return array[array.length-1];
    }

    /**
     *
     * @param key  Fully qualified class name, or a case insensitive simple class name. Examples: 1) com.mypackage.MyOpenMon 2) MyOpenMon, 3) myopenmon
     * @return An instance of the class or the default {@link org.automon.implementations.OpenMon} if there was a failure on class creation.
     */
    public  OpenMon getInstance(String key) {
        String className = openMonFactory.get(key); // com.mypcackage.MyOpenMon
        if (className == null) {
            className = openMonFactory.get(key.toLowerCase()); // myopenmon
        }

        // not in the map so return the default.
        if (className == null) {
            return defaultOpenMon;
        }

        // create the openMon, but if it fails return the default.
        OpenMon openMon = create(className);
        if (openMon == null) {
            return defaultOpenMon;
        }

        return openMon;
    }

    /**
     *
     * @return The keys concatenated together as a string in alphabetical order.   Class names such as 'com.myackage.MyClass'
     * entries are redundant to the other entries and so are removed and only entries with the classname i.e. 'MyClass' will be returned.
     */
    public String toString() {
        List<String> keys = new ArrayList(openMonFactory.keySet());
        Utils.removeClassNames(keys);
        Collections.sort(keys);
        return keys.toString();
    }

    private OpenMon create(String clazzName) {
        try {
            return (OpenMon) Class.forName(clazzName).newInstance();
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Visible for testing.
     */
    void reset() {
        openMonFactory.clear();
    }
}
