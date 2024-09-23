package org.automon.implementations;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;
import org.automon.utils.AutomonExpirable;
import org.automon.utils.Utils;

import java.util.List;

/**
 * This class is an `OpenMon` implementation that leverages Jamon for monitoring method executions and exceptions.
 * It provides capabilities for starting and stopping timers, tracking exceptions, and capturing detailed context information.
 */
public class Jamon extends OpenMonBase<Monitor> {

    /**
     * Starts a Jamon `Monitor` for the given join point.
     *
     * @param jp The join point representing the intercepted method.
     * @return The started `Monitor`.
     */
    @Override
    public Monitor start(JoinPoint.StaticPart jp) {
        return MonitorFactory.start(jp.toString());
    }

    /**
     * Stops the given Jamon `Monitor`.
     *
     * @param mon The `Monitor` to stop.
     */
    @Override
    public void stop(Monitor mon) {
        mon.stop();
    }

    /**
     * Stops the given Jamon `Monitor` and records an exception.
     * <p>
     * This method associates the exception details with the `Monitor` and ensures that the details
     * are serializable and compatible with the Jamon cluster environment. If exception tracking
     * is enabled, the stack trace and method arguments will be further enriched in the `trackException` method.
     *
     * @param mon       The `Monitor` to stop.
     * @param throwable The exception that occurred.
     */
    @Override
    public void stop(Monitor mon, Throwable throwable) {
        mon.stop();
        put(throwable); // Store the exception in the internal map for later use

        // The 'get' always succeeds due to the 'put' above
        // Ensure jamonDetails are serializable and use JDK classes for compatibility with Jamon cluster
        mon.getMonKey().setDetails(get(throwable).setJamonDetails(throwable));
    }

    /**
     * Handles an exception by storing it and initiating exception tracking.
     * <p>
     * The order of operations is important to ensure the stack trace is available for all monitors.
     *
     * @param jp        The join point where the exception occurred.
     * @param throwable The exception that occurred.
     */
    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        put(throwable); // Store the exception for later use
        trackException(jp, throwable);
    }

    /**
     * Tracks the exception by adding it to Jamon monitors with appropriate labels.
     * <p>
     * This method retrieves the exception context, populates it with argument information if needed,
     * and then adds the exception to Jamon monitors using the generated labels.
     *
     * @param jp        The join point where the exception occurred.
     * @param throwable The exception that occurred.
     */
    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        AutomonExpirable exceptionContext = populateExceptionContext(jp, throwable);

        // Track multiple monitors for the exception (specific exception and all exceptions)
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            MonKey key = new MonKeyImp(label, exceptionContext.getJamonDetails(), "Exception");
            MonitorFactory.add(key, 1);
        }
    }

    /**
     * Populates the exception context with additional details for Jamon monitoring.
     * <p>
     * This method retrieves the exception context and adds argument names and values to it if they haven't been set yet.
     * It also updates the Jamon details with the complete exception context information.
     *
     * @param jp        The join point where the exception occurred.
     * @param throwable The exception that occurred.
     * @return The populated `AutomonExpirable` exception context.
     */
    private AutomonExpirable populateExceptionContext(JoinPoint jp, Throwable throwable) {
        AutomonExpirable exceptionContext = get(throwable);
        if (exceptionContext.getArgNamesAndValues() == null) {
            exceptionContext.setArgNamesAndValues(Utils.paramsToMap(jp));
            // This replaces the Jamon details set in the 'stop' method
            exceptionContext.setJamonDetails(exceptionContext.toString());
        }
        return exceptionContext;
    }
}
