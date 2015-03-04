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

    // linkedHashMap removeEldestEntry - http://docs.oracle.com/javase/6/docs/api/java/util/LinkedHashMap.html#removeEldestEntry(java.util.Map.Entry)
    // must be threadsafe
    // key is exception, value could be timestamp
    // or key is thread id, and value is exception and timestamp
//    protected boolean removeEldestEntry(Map.Entry eldest){
//        return size() > MAX_SIZE;
//    }
    // also this may need to be done to have last element tracked and if so i wouldn' tneed to add a date
//    public LinkedHashMap(int initialCapacity,
//                         float loadFactor,
//                         boolean accessOrder)
    // Map m = Collections.synchronizedMap(new LinkedHashMap(...));

    // from eldest documentation - note i don't think i can get away with removing just eldest but say ALL older than a specified time.
    // note there is only ever 1 entry per thread.
//    * @param    eldest The least recently inserted entry in the map, or if
//            *           this is an access-ordered map, the least recently accessed
//    *           entry.  This is the entry that will be removed it this
//            *           method returns <tt>true</tt>.  If the map was empty prior
//    *           to the <tt>put</tt> or <tt>putAll</tt> invocation resulting
//    *           in this invocation, this will be the entry that was just
//    *           inserted; in other words, if the map contains a single
//    *           entry, the eldest entry is also the newest.
//    * @return   <tt>true</tt> if the eldest entry should be removed
//    *           from the map; <tt>false</tt> if it should be retained.
//            */


    // key is exception, value could be timestamp
    // **** SOLUTION ***
    // 1) maybe check past in entry.
    // 2) if it is older than specified time
    //      remove & iterate full collection removing all older than specified time and return false.
    //
    //    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
//        return false;
//    }
    // Note removeEldest is called from put which will be synchronized. Map m = Collections.synchronizedMap(new LinkedHashMap(...));
    //  This method typically does not modify the map in any way, instead allowing the map to modify itself as directed by its return value.
    // It is permitted for this method to modify the map directly, but if it does so, it must
    // return false (indicating that the map should not attempt any further modification).
    // The effects of returning true after modifying the map from within this method are unspecified.







}
