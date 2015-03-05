package org.automon.utils;

import org.aspectj.lang.JoinPoint;

import java.sql.SQLException;
import java.util.Collections;
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

}
