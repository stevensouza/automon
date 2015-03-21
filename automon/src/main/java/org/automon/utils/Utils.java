package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;
import org.automon.aspects.AutomonMXBean;
import org.automon.implementations.OpenMon;

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
     static final String UNKNOWN =  "???";


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
     *
     * @return Thread safe Map that can contain Exceptions that are thrown monitored code.
     */
    public static Map<Throwable, AutomonExpirable> createExceptionMap() {
        return Collections.synchronizedMap(new ExpiringMap<Throwable, AutomonExpirable>());
    }

   /**
     * @param throwable The exception that was thrown
     * @return A string suitable for a monitoring label representing the thrown exception.  It is a convenience method and need not be
     * used to create the monitor.  Note it adds extra information to the returned label for SQLException's.<br>
     *
     * Examples:<br>
     *      <br>java.lang.RuntimeException
     *      <br>java.sql.SQLException,ErrorCode=400,SQLState=Login failure
     */
    public static String getLabel(Throwable throwable) {
        String sqlMessage="";
        // add special label information if it is a sql exception.
        if (throwable instanceof SQLException) {
          SQLException sqlException = (SQLException) throwable;
          sqlMessage = ",ErrorCode=" + sqlException.getErrorCode()+ ",SQLState=" + sqlException.getSQLState();
        }
        return throwable.getClass().getName()+sqlMessage;
    }

   /**
    *
    * @param jp The pointcut from the @Around advice that was intercepted.
    * @return A label suitable for a monitoring/timer label.  It is a convenience method and need not be
    * used to create the monitor
    */

    public static String getLabel(JoinPoint.StaticPart jp ) {
        return jp.toString();
    }


    /**
     * <p>If appropriate for the given {@link org.aspectj.lang.JoinPoint} return argument names and values
     * in a list of Strings.</p>
     *
     * <p>Example for the following method - printName(String fname, String lname)<br>
     *    fname: Steve<br>
     *    lname: Souza<br>
     * </p>
     *
     * <p>Under certain circumstances for example if the code was compiled without retaining parameters a
     * parameter name of the format arg0 or even 0 might appear.</p>
     *
     *
     * @param jp
     * @return A list containing strings of the format: argName: argValue
     */
    public static List<String> getArgNameValuePairs(JoinPoint jp) {
        List<String> list =  new ArrayList<String>();
        Object[] argValues = jp.getArgs();
        if (argValues==null || argValues.length==0) {
            return list;
        }

        Object[] argNames = getParameterNames(argValues, jp);
        for (int i = 0; i < argValues.length; i++) {
            String argName =  (argNames[i]==null) ? ""+i : argNames[i].toString();
            list.add(""+argName+": "+toStringWithLimit(argValues[i]));
        }

        return list;
     }

    /** Convert a list to a formatted string.
     *
     * @param args assumed to be parameter key value pairs
     * @return String representing the argName, argValue pairs
     */
    public static String argNameValuePairsToString(List<String> args) {
        if (args==null) {
            return UNKNOWN;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Parameters ===").append(LINE_SEPARATOR);
        for (String str : args) {
            sb.append(str).append(LINE_SEPARATOR);
        }
        return sb.append(LINE_SEPARATOR).toString();
    }

    /**
     * Turns a single method parameter into a string. To keep
     * the functionality safe we truncate overly long strings and
     * ignore any exceptions.
     */
     public static String toStringWithLimit(Object parameter) {
        if(parameter == null) {
            return NULL_STR;
        }

        String value = parameter.toString();
        try {
            if(value.length() > DEFAULT_ARG_STRING_MAX_LENGTH) {
                value = value.substring(0, DEFAULT_ARG_STRING_MAX_LENGTH) + DEFAULT_MAX_STRING_ENDING;
            }
            return value;
        } catch(Throwable e) {
            return UNKNOWN;
        }
    }


    /** Return full Exception stack trace as String */
    public static String getExceptionTrace(Throwable exception) {
        if (exception==null) {
            return UNKNOWN;
        }
        // each line of the stack trace will be returned in the array.
        StackTraceElement elements[] = exception.getStackTrace();
        StringBuffer sb = new StringBuffer().append(exception).append(LINE_SEPARATOR);

        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i]).append(LINE_SEPARATOR);
        }

        return sb.toString();
    }

    /** Tokenize the passed in string.
     *
     * @param string string to tokenize
     * @param splitOn string to split the string on.
     * @return an array of String tokens.
     */
    public static String[] tokenize(String string, String splitOn) {
        return string.replace(" ","").split(splitOn);
    }

    /**
     *
     * @param fileName
     * @return Take something like file://myfile.dat and return myfile.dat
     */
    public static String stripFileScheme(String fileName) {
        return fileName.replaceFirst("(?i)file://","").replaceFirst("(?i)file:","");
    }

    /**
     * Pass in an aspect and return its jmx bean.
     *
     * @param aspect
     * @return
     * @throws Exception if jmx commands fail
     */
    public static AutomonMXBean getAutomonMxBean(Object aspect) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        AutomonMXBean mxbeanProxy = JMX.newMXBeanProxy(mBeanServer, getMxBeanObjectName(aspect), AutomonMXBean.class);
        return mxbeanProxy;
    }

   // get the ObjectName that is used to refer to the 'aspect'
   private static ObjectName getMxBeanObjectName(Object aspect) throws Exception {
        String objectName = "org.automon:type=aspects,name=" + aspect;
        return new ObjectName(objectName);
    }

    /**
     *
     * @param aspect Aspect that we are registering the jmx bean for
     * @param mxBean jmx bean use to manage and get information about the aspect
     */
    public static void registerWithJmx(Object aspect, AutomonMXBean mxBean)  {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            mBeanServer.registerMBean(mxBean, getMxBeanObjectName(aspect));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param aspect unregister the passed in aspect.
     * @throws Exception
     */

    public static void unregisterWithJmx(Object aspect) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.unregisterMBean(getMxBeanObjectName(aspect));
    }

    public static boolean hasPackageName(String className) {
        return className==null ? false : className.contains(".");
    }

    /**
     * Take a variable list of fully qualified class names and return the first one.
     * @param classNames com.package1.MyClass1, com.package2.MyClass2
     * @param <T>
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
     *
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

}
