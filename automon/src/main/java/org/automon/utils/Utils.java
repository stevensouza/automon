package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;
import org.automon.aspects.jmx.AutomonMXBean;

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
     * @return Thread safe Map that can contain Exceptions that are thrown monitored code.
     */
    public static Map<Throwable, AutomonExpirable> createExceptionMap() {
        return Collections.synchronizedMap(new ExpiringMap<Throwable, AutomonExpirable>());
    }

    /**
     * @param throwable The exception that was thrown
     * @return A string suitable for a monitoring label representing the thrown exception.  It is a convenience method and need not be
     * used to create the monitor.  Note it adds extra information to the returned label for SQLException's.<br>
     * <p>
     * Examples:<br>
     * <br>java.lang.RuntimeException
     * <br>java.sql.SQLException,ErrorCode=400,SQLState=Login failure
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
     * @param jp The pointcut from the @Around advice that was intercepted.
     * @return A label suitable for a monitoring/timer label.  It is a convenience method and need not be
     * used to create the monitor
     */

    public static String getLabel(JoinPoint.StaticPart jp) {
        return jp.toString();
    }

    /**
     * Some tools don't allow for special characters in their monitoring labels. For example
     * StatsD doesn't allow '.=' and JavaSimon doesn't allow ' ='.  The following method strips
     * these characters out of the exception string for SQL so they will work in these api's.
     * This is done for sql exceptions of the format:
     * java.sql.SQLException,ErrorCode=400,SQLState=Login failure
     * <p>
     *
     * @param exceptionLabel
     * @return
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
     * This method extracts method parameters/arguments and their values from the JoinPoint
     * and returns a read-only map containing them.
     * Terminology Clarification:
     * <p>
     * Parameter (Formal Parameter): A variable declared in the method signature that acts as a placeholder for the value that will be passed in during the method call.
     * Argument (Actual Parameter): The actual value (data) that is passed into the method when it is called. This value is assigned to the corresponding parameter.
     *
     * @param joinPoint The JoinPoint object containing the execution context
     * @return A read-only map of argument names to their corresponding values
     */
    public static Map<String, Object> paramsToMap(JoinPoint joinPoint) {
        Map<String, Object> argsMap = new LinkedHashMap<>();
        Object[] argValues = joinPoint.getArgs();
        if (argValues == null || argValues.length == 0) {
            return argsMap;
        }

        Signature signature = joinPoint.getSignature();
        // https://javadoc.io/doc/org.aspectj/aspectjweaver/1.8.11/org/aspectj/lang/reflect/CodeSignature.html
        if (signature instanceof CodeSignature codeSignature) {
            String[] parameterNames = codeSignature.getParameterNames();

            for (int i = 0; i < argValues.length; i++) {
                argsMap.put(parameterNames[i], argValues[i]);
            }
        } else {
            for (int i = 0; i < argValues.length; i++) {
                argsMap.put("param" + i, argValues[i]);
            }
        }

        // Return a read-only version of the map for efficiency
        return Collections.unmodifiableMap(argsMap);
    }


    /**
     * Convert a Map to a formatted string
     *
     * @param args assumed to be parameter key value pairs
     * @return String representing the argName, argValue pairs. Example of what it could return:
     * === Parameters ===
     * filename: report.txt
     * max_records: 1000
     * user_id: johndoe123
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
     * Converts a single method parameter to a string representation with length limiting.
     *
     * @param parameter The object to convert to a string.
     * @return A string representation of the parameter, with the following characteristics:
     * - Returns NULL_STR for null parameters.
     * - Truncates strings longer than DEFAULT_ARG_STRING_MAX_LENGTH and appends DEFAULT_MAX_STRING_ENDING.
     * - Returns UNKNOWN if any exception occurs during conversion.
     * <p>
     * This method ensures safe functionality by handling null values, limiting string length,
     * and catching all exceptions to prevent method failure.
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
     * Return full Exception stack trace as String
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
     * Tokenize the passed in string.
     *
     * @param string  string to tokenize
     * @param splitOn string to split the string on.
     * @return an array of String tokens.
     */
    public static String[] tokenize(String string, String splitOn) {
        return string.replace(" ", "").split(splitOn);
    }

    /**
     * @param fileName
     * @return Take something like file://myfile.dat and return myfile.dat
     */
    public static String stripFileScheme(String fileName) {
        return fileName.replaceFirst("(?i)file://", "").replaceFirst("(?i)file:", "");
    }

    /**
     * Retrieves the JMX MBean associated with the given aspect.
     *
     * @param aspect The aspect instance.
     * @param <T> The type of the MBean interface.
     * @return The MBean proxy to manage the aspect.
     * @throws Exception If JMX commands fail (e.g., MBean not found, registration issues).
     */
    public static <T> T getMxBean(String purpose, Object aspect, Class<T> mxBeanInterface) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        T mxbeanProxy = JMX.newMXBeanProxy(mBeanServer, getMxBeanObjectName(purpose, aspect), mxBeanInterface);
        return mxbeanProxy;
    }

    /**
     * Registers the given JMX MBean with the platform MBeanServer, associating it with the provided aspect.
     * <p>
     *     if JMX commands fail. The exception is not propagated as this error is not key to the operating of
     *     Automon or the application. It should not fail as the exception is well formed and always the same.
     * </p>
     * @param aspect The aspect instance. It is used to generate the JMX name such as:
     *               org.automon:type=aspect,purpose=monitor,name=org.automon.AutomonAspect@63f25932
     *               It could technically be any object and toString will be called to get the name.
     * @param mxBean The JMX MBean instance to register (it can be of any type)
     * @param <T>    The type of the MBean interface.
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
     * @param aspect unregister the passed in aspect.
     * @throws Exception Thrown if the jmx bean has a naming error
     */

    public static void unregisterWithJmx(String purpose, Object aspect) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.unregisterMBean(getMxBeanObjectName(purpose, aspect));
    }

    // get the ObjectName that is used to refer to the 'aspect' in jmx. Sample return value...
    //    org.automon:type=aspect,purpose=monitor,name=org.automon.AutomonAspect@63f25932
    private static ObjectName getMxBeanObjectName(String purpose, Object aspect) throws Exception {
        String jmxName = String.format("org.automon:type=aspect,purpose=%s,name=%s", purpose, aspect.toString());
        return new ObjectName(jmxName);
    }

    public static boolean hasPackageName(String className) {
        return className != null && className.contains(".");
    }

    /**
     * Take a variable list of fully qualified class names and return the first one.
     *
     * @param classNames com.package1.MyClass1, com.package2.MyClass2
     * @return The first created class or none if all fail.
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

    // used to get method argvalues from the method signature.
    private static Object[] getParameterNames(Object[] argValues, JoinPoint jp) {
        Signature signature = jp.getSignature();
        if (signature instanceof CodeSignature) {
            return ((CodeSignature) signature).getParameterNames();
        } else {
            return new Object[argValues.length];
        }
    }

    /**
     * @param list Take a list and remove any entries with '.' in them.  This is done to remove class names from
     *             a List.
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


    // used to determine if aspect or logging is enabled or disabled by looking at
    // properties. (i.e. org.automon.tracing.BasicContextTracingAspect.enable)
    private static final AutomonPropertiesLoader AUTOMON_PROPERTIES = new AutomonPropertiesLoader();

    // pass in MyClass.getClass().getName() typically.
    public static boolean shouldEnable(Object object) {
        String key= (object == null) ? "" : object+".enable";
        return AUTOMON_PROPERTIES.getBoolean(key);

    }

    public static boolean shouldEnableLogging(Object object) {
        String key = (object == null) ? "" : object+".enableLogging";
        return AUTOMON_PROPERTIES.getBoolean(key);
    }

}
