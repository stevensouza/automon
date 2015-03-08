package org.automon.implementations;

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
        String[] clazzNames = clazzNamesString.replace(" ","").split(",");
        for (String clazzName : clazzNames) {
            openMonFactory.put(clazzName, clazzName.trim());
        }
    }

    public  OpenMon getInstance(String key, OpenMon defaultOpenMon) {
        String clazz = openMonFactory.get(key);
        if (clazz == null) {
            return defaultOpenMon;
        }

        OpenMon openMon = create(clazz);
        if (openMon == null) {
            return defaultOpenMon;
        }

        return openMon;
    }

//    public  OpenMon getFirst(String keyStrings, OpenMon defaultOpenMon) {
//        String[] keys = keyStrings.replace(" ","").split(",");
//        for (String key : keys) {
//            OpenMon openMon = get(key);
//            if (openMon!=null) {
//                return openMon;
//            }
//        }
//
//        return defaultOpenMon;
//    }

//    private OpenMon create(Class<? extends OpenMon> openMon) {
//        try {
//           return openMon.newInstance();
//        } catch (Throwable t) {
//           return null;
//        }
//    }

    private OpenMon create(String clazzName) {
        try {
            return (OpenMon) Class.forName(clazzName).newInstance();
        } catch (Throwable t) {
            return null;
        }
    }
}
