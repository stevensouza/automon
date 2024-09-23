package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.*;

/**
 * Static utility methods used throughout Automon.
 */
public class Utils {

    public static final String LINE_SEPARATOR = "\n";
    /**
     * Returned when a method parameter is null
     */
    static final String NULL_STR = "<null>";

    /**
     * used when a value isn't returned, and yet we don't want to throw an exception (hey it's just monitoring)
     */
    static final String UNKNOWN = "???";


    /**
     * Maximum length for a parameter in the exception dump
     */
    static final int DEFAULT_ARG_STRING_MAX_LENGTH = 125;

    /**
     * Parameters kept in the details section are capped at a max length and this string is put at the end of
     * the string after the truncation point to indicate there is more data that is not shown.
     */
    static final String DEFAULT_MAX_STRING_ENDING = "...";

    /**
     * Creates a thread-safe map for storing exceptions with expiration capabilities
     *
     * @return A synchronized map for storing exceptions that expire over time
     */
    public static Map<Throwable, AutomonExpirable> createExceptionMap() {
        return Collections.synchronizedMap(new ExpiringMap<Throwable, AutomonExpirable>());
    }

    /**
     * Generates a monitoring label for a thrown exception
     *
     * @param throwable The exception
     * @return A label suitable for monitoring, including additional information for SQLExceptions
     */
    public static String getLabel(Throwable throwable) {
        String sqlMessage = "";
        // add special label information if it is a sql exception.
        if (throwable instanceof SQLException) {
            SQLException sqlException = (SQLException) throwable;
            sqlMessage = ",ErrorCode=" + sqlException.getErrorCode() + ",SQLState=" + sqlException.getSQLState();
        }
        return throwable.getClass().getName() + sqlMessage;
    }

    /**
     * Generates a monitoring label from an intercepted JoinPoint
     *
     * @param jp The intercepted JoinPoint
     * @return A label suitable for monitoring/timer
     */
    public static String getLabel(JoinPoint.StaticPart jp) {
        return jp.toString();
    }

    /**
     * Formats exception labels to be compatible with tools having limited character sets
     *
     * @param exceptionLabel The original exception label
     * @return A formatted label compatible with tools like StatsD and JavaSimon
     */
    public static String formatExceptionForToolsWithLimitedCharacterSet(String exceptionLabel) {
        if (exceptionLabel == null) {
            return null;
        }

        return exceptionLabel.
                replace(",ErrorCode=", ".ErrorCode ").
                replace(",SQLState=", "-SQLState ");
    }

    /**
     * Extracts method parameters and their values from a JoinPoint
     *
     * @param joinPoint The JoinPoint representing the method execution context
     * @return A read-only map of parameter names to their values
     */
    public static Map<String, Object> paramsToMap(JoinPoint joinPoint) {
        Map<String, Object> argsMap = new LinkedHashMap<>();
        Object[] argValues = joinPoint.getArgs();
        if (argValues == null || argValues.length == 0) {
            return argsMap;
        }

        Signature signature = joinPoint.getSignature();

        String[] parameterNames = null;
        // https://javadoc.io/doc/org.aspectj/aspectjweaver/1.8.11/org/aspectj/lang/reflect/CodeSignature.html
        if (signature instanceof CodeSignature codeSignature) {
            parameterNames = codeSignature.getParameterNames();
        }

        // If parameterNames is null (or signature wasn't CodeSignature), use default naming
        if (parameterNames == null) {
            for (int i = 0; i < argValues.length; i++) {
                argsMap.put("param" + i, argValues[i]);
            }
        } else { // Otherwise, use the retrieved parameter names
            for (int i = 0; i < argValues.length; i++) {
                argsMap.put(parameterNames[i], argValues[i]);
            }
        }

        return Collections.unmodifiableMap(argsMap);
    }

    /**
     * Converts a map of arguments to a formatted string
     *
     * @param args A map of argument names to their values
     * @return A formatted string representation of the arguments
     */
    public static String argNameValuePairsToString(Map<String, Object> args) {
        if (args == null) {
            return UNKNOWN;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Parameters ===").append(LINE_SEPARATOR);

        for (Map.Entry<String, Object> entry : args.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(LINE_SEPARATOR);
        }

        return sb.append(LINE_SEPARATOR).toString();
    }

    /**
     * Converts a method parameter to a string with length limiting and null handling
     *
     * @param parameter The parameter object
     * @return A string representation of the parameter, handling nulls and truncation
     */
    public static String toStringWithLimit(Object parameter) {
        if (parameter == null) {
            return NULL_STR;
        }

        String value = parameter.toString();
        try {
            if (value.length() > DEFAULT_ARG_STRING_MAX_LENGTH) {
                value = value.substring(0, DEFAULT_ARG_STRING_MAX_LENGTH) + DEFAULT_MAX_STRING_ENDING;
            }
            return value;
        } catch (Throwable e) {
            return UNKNOWN;
        }
    }


    /**
     * Gets the full stack trace of an exception as a string
     *
     * @param exception The exception
     * @return The full stack trace as a string, or UNKNOWN if the exception is null
     */
    public static String getExceptionTrace(Throwable exception) {
        if (exception == null) {
            return UNKNOWN;
        }
        // each line of the stack trace will be returned in the array.
        StackTraceElement elements[] = exception.getStackTrace();
        StringBuilder sb = new StringBuilder().append(exception).append(LINE_SEPARATOR);

        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i]).append(LINE_SEPARATOR);
        }

        return sb.toString();
    }

    /**
     * Tokenizes a string based on a specified delimiter
     *
     * @param string The string to tokenize
     * @param splitOn The delimiter to split the string on
     * @return An array of tokens
     */
    public static String[] tokenize(String string, String splitOn) {
        return string.replace(" ", "").split(splitOn);
    }

    /**
     * Strips the file scheme from a file name
     *
     * @param fileName The file name (e.g., "file://myfile.dat")
     * @return The file name without the scheme (e.g., "myfile.dat")
     */
    public static String stripFileScheme(String fileName) {
        return fileName.replaceFirst("(?i)file://", "").replaceFirst("(?i)file:", "");
    }

    /**
     * Retrieves the JMX MBean associated with the given aspect
     *
     * @param aspect The aspect instance
     * @param <T> The type of the MBean interface
     * @return The MBean proxy
     * @throws Exception If JMX operations fail
     */
    public static <T> T getMxBean(String purpose, Object aspect, Class<T> mxBeanInterface) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        T mxbeanProxy = JMX.newMXBeanProxy(mBeanServer, getMxBeanObjectName(purpose, aspect), mxBeanInterface);
        return mxbeanProxy;
    }

    /**
     * Registers an MBean with the platform MBeanServer
     *
     * @param aspect The aspect instance used for JMX name generation
     * @param mxBean The MBean instance to register
     * @param <T> The type of the MBean interface
     */
    public static <T> void registerWithJmx(String purpose, Object aspect, T mxBean) {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            mBeanServer.registerMBean(mxBean, getMxBeanObjectName(purpose, aspect));
        } catch (Exception e) {
            // non-propagation of this exception is by design (see javadocs). Also any logging framework
            // is not guaranteed to be there so one is not used.
            e.printStackTrace();
        }

    }


    /**
     * Unregisters an aspect from JMX
     *
     * @param aspect The aspect to unregister
     * @throws Exception If JMX unregistration fails
     */
    public static void unregisterWithJmx(String purpose, Object aspect) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.unregisterMBean(getMxBeanObjectName(purpose, aspect));
    }

    /**
     * Generates the JMX ObjectName for an aspect
     *
     * @param purpose The purpose of the aspect (e.g., "monitor")
     * @param aspect The aspect instance
     * @return The JMX ObjectName
     * @throws Exception If ObjectName creation fails
     */
    private static ObjectName getMxBeanObjectName(String purpose, Object aspect) throws Exception {
        String jmxName = String.format("org.automon:type=aspect,purpose=%s,name=%s", purpose, aspect.toString());
        return new ObjectName(jmxName);
    }

    /**
     * Checks if a class name has a package name (contains a '.')
     *
     * @param className The class name to check
     * @return True if the class name has a package, false otherwise
     */
    public static boolean hasPackageName(String className) {
        return className != null && className.contains(".");
    }

    /**
     * Creates the first instance from a list of fully qualified class names
     *
     * @param classNames Variable list of class names
     * @param <T> The type of the object to create
     * @return The first successfully created instance, or null if all fail
     */
    public static <T> T createFirst(String... classNames) {
        for (String className : classNames) {
            try {
                return (T) Class.forName(className).newInstance();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    /**
     * Retrieves parameter names from a JoinPoint (internal helper method)
     *
     * @param argValues The array of argument values
     * @param jp The JoinPoint representing the method execution
     * @return An array of parameter names, or an empty array if retrieval fails
     */
    private static Object[] getParameterNames(Object[] argValues, JoinPoint jp) {
        Signature signature = jp.getSignature();
        if (signature instanceof CodeSignature) {
            return ((CodeSignature) signature).getParameterNames();
        } else {
            return new Object[argValues.length];
        }
    }

    /**
     * Removes class names (entries containing '.') from a list
     *
     * @param list The list to modify
     */
    public static void removeClassNames(List<String> list) {
        ListIterator<String> iterator = list.listIterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            if (str.contains(".")) {
                iterator.remove();
            }
        }
    }


    /**
     *  Provides access to Automon properties for enabling/disabling aspects and logging
     */
    public static AutomonPropertiesLoader AUTOMON_PROPERTIES = new AutomonPropertiesLoader();

    /**
     * Checks if an aspect or feature should be enabled based on properties
     *
     * @param object The object representing the aspect or feature
     * @return True if enabled, false otherwise
     */
    public static boolean shouldEnable(Object object) {
        String key= (object == null) ? "" : object+".enable";
        return AUTOMON_PROPERTIES.getBoolean(key);

    }

    /**
     * Checks if logging should be enabled for an aspect or feature based on properties
     *
     * @param object The object representing the aspect or feature
     * @return True if logging is enabled, false otherwise
     */
    public static boolean shouldEnableLogging(Object object) {
        String key = (object == null) ? "" : object+".enableLogging";
        return AUTOMON_PROPERTIES.getBoolean(key);
    }

}
