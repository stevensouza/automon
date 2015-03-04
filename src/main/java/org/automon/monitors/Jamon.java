package org.automon.monitors;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;

import java.util.List;

/**
 * Created by stevesouza on 2/26/15.
 */
public class Jamon extends OpenMonBase<Monitor> {

    @Override
    public Monitor start(JoinPoint jp) {
        return MonitorFactory.start(Utils.getLabel(jp));
    }

    @Override
    public void stop(Monitor mon) {
        mon.stop();
    }

    @Override
    public void stop(Monitor mon, Throwable throwable) {
        mon.stop();
        mon.getMonKey().setDetails(throwable);
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
    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            MonKey key = new MonKeyImp(label, throwable, "Exception");
            MonitorFactory.add(key, 1);        }

    }

}
