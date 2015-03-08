package org.automon.implementations;

import org.automon.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stevesouza on 3/7/15.
 */
public class OpenMonFactory {

    static final String JAMON =  "org.automon.implementations.Jamon";
    static final String JAVA_SIMON =  "org.automon.implementations.JavaSimon";
    static final String METRICS =  "org.automon.implementations.Metrics";
    static final String SYSOUT =  "org.automon.implementations.SysOut";
    public static final String NULL_IMP =  "org.automon.implementations.NullImp";

    private  Map<String, String> openMonFactory = new HashMap<String,  String>();

    public OpenMonFactory() {
        add(JAMON);
        add(JAVA_SIMON);
        add(METRICS);
        add(SYSOUT);
        add(NULL_IMP);
    }

    public void add(String clazzNamesString) {
        String[] clazzNames = Utils.tokenize(clazzNamesString, ",");
        for (String clazzName : clazzNames) {
            clazzName =  clazzName.trim();
            openMonFactory.put(clazzName, clazzName);
            openMonFactory.put(getJustClassName(clazzName).toLowerCase(), clazzName);
        }
    }

    static String getJustClassName(String clazzName) {
        String[] array = Utils.tokenize(clazzName, "[.]");
        return array[array.length-1];
    }

    public  OpenMon getInstance(String key, OpenMon defaultOpenMon) {
        String clazz = openMonFactory.get(key);

        if (clazz == null) {
            clazz = openMonFactory.get(key.toLowerCase());
        }

        if (clazz == null) {
            return defaultOpenMon;
        }

        OpenMon openMon = create(clazz);
        if (openMon == null) {
            return defaultOpenMon;
        }

        return openMon;
    }

    private OpenMon create(String clazzName) {
        try {
            return (OpenMon) Class.forName(clazzName).newInstance();
        } catch (Throwable t) {
            return null;
        }
    }
}
