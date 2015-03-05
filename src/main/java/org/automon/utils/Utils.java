package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by stevesouza on 3/3/15.
 */
public class Utils {

    public static Map<Throwable, Expirable> createExceptionMap() {
        return Collections.synchronizedMap(new ExpiringMap<Throwable, Expirable>());
    }

   /**
     * @param throwable The exception that was thrown
     * @return A string suitable for a monitoring label representing the thrown exception.  It is a convenience method and need not be
     * used to create the monitor
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
    public static List<String> getArgNameValuesPairs(JoinPoint jp) {
        List<String> list =  new ArrayList<String>();
        Object[] argValues = jp.getArgs();
        if (argValues==null || argValues.length==0) {
            return list;
        }

        Object[] argNames = getParameterNames(argValues, jp);
        for (int i = 0; i < argValues.length; i++) {
            String argName =  (argNames[i]==null) ? ""+i : argNames[i].toString();
            list.add(""+argName+": "+argValues[i]);
        }

        return list;
     }

    private static Object[] getParameterNames(Object[] argValues, JoinPoint jp) {
        Signature signature = jp.getSignature();
        if (signature instanceof CodeSignature) {
            return ((CodeSignature) signature).getParameterNames();
        } else {
            return new Object[argValues.length];
        }
    }

}
