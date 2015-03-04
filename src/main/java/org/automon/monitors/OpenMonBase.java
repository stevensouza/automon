package org.automon.monitors;

import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;

import org.automon.utils.*;
import java.util.Map;

/**
 * Created by stevesouza on 3/2/15.
 */
public abstract class OpenMonBase<T> implements OpenMon<T> {

    private  Map<Throwable, Expirable>  exceptionsMap = Utils.createExceptionMap();

    /**
     * @param jp The pointcut from the @Around advice that was intercepted.
     * @return A label suitable for a monitoring/timer label.  It is a convenience method and need not be
     * used to create the monitor
     */
    protected String getLabel(JoinPoint jp) {
        return jp.getStaticPart().toString();
    }

    /**
     * @param throwable The exception that was thrown
     * @return A label suitable for a monitoring label representing the thrown exception.  It is a convenience method and need not be
     * used to create the monitor
     */
    protected String getLabel(Throwable throwable) {
        return throwable.getClass().getName();
    }

    /**
     * Note the default implementation simply calls {@link #stop(T)} and doesn't do anything with the {@link java.lang.Throwable} argument
     *
     * @param context The object returned by 'start' is passed in.  Typically this would be a timer and should be stopped.
     * @param throwable This argument is ignored in the default implementation.
     */
    @Override
    public void stop(T context, Throwable throwable) {
        put(throwable);
        stop(context);
    }

    /**
     * Override {@link #exceptionImp(org.aspectj.lang.JoinPoint, Throwable)} instead of this method unless the default behavior
     * is not desired
     *
     * @param jp The {@link org.aspectj.lang.JoinPoint} associated with where the exception was thrown.
     * @param throwable The thrown exception
     */
    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        exceptionImp(jp, throwable);
        put(throwable);
    }

    /**
     * put in this class?
     *
     * should i change the map order to last is first inserted.  keeping in miind that anything over 2 minutes can go.
     * The advantage is that i could abort the loop as soon as i hit the first one that didn't match?
     *
     *
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

    //????
    public void exceptionImp(JoinPoint jp, Throwable throwable) {

    }

    protected void put(Throwable throwable) {
        // note 'get' is used instead of 'containsKey' as we want to update the LRU information for each access.
        if (!exceptionsMap.containsKey(throwable)) {
            exceptionsMap.put(throwable, new TimeExpirable());
        }
    }

}
