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


    /*
       * When iterating over the map the most recently accessed entry is returned first and the least recently accessed element is returned last.
     *
   private void trackException(Throwable rootCause, Method method, String sqlMessage) {
     String detailStackTrace = Misc.getExceptionTrace(rootCause);
     MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, detailStackTrace, "Exception"), 1); // counts total exceptions from jamon
     MonitorFactory.add(new MonKeyImp("MonProxy-Exception: InvocationTargetException", detailStackTrace, "Exception"), 1); //counts total exceptions for MonProxy
     MonitorFactory.add(new MonKeyImp("MonProxy-Exception: Root cause exception="+rootCause.getClass().getName()+sqlMessage,
     detailStackTrace, "Exception"), 1); // Message for the exception
     MonitorFactory.add(new MonKeyImp(labelerInt.getExceptionLabel(method), detailStackTrace,"Exception"), 1); // Exception and method that threw it.
     }

     // Add special info if it is a SQLException
     if (rootCause instanceof SQLException) {
       SQLException sqlException = (SQLException) rootCause;
       sqlMessage = ",ErrorCode=" + sqlException.getErrorCode()+ ",SQLState=" + sqlException.getSQLState();
     }

     // Add jamon entries for Exceptions
     trackException(rootCause, method, sqlMessage);

     */
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

    public static String getLabel(JoinPoint jp) {
        return jp.getStaticPart().toString();
    }

}
