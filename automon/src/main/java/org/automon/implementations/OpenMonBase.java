package org.automon.implementations;

import org.aspectj.lang.JoinPoint;
import org.automon.utils.AutomonExpirable;
import org.automon.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Base implementation of the `OpenMon` interface, providing common functionality for monitoring implementations.</p>
 * <p>This class handles exception tracking and provides methods for starting and stopping monitoring,
 * as well as generating labels for exceptions.</p>
 *
 * @param <T> The type of context object used for monitoring (e.g., a timer or other stateful object).
 */
public abstract class OpenMonBase<T> implements OpenMon<T> {

    /**
     * A map to store recently thrown exceptions, allowing for reuse and preventing memory leaks.
     * The map uses `Throwable` as the key and `AutomonExpirable` (which wraps the exception and additional context) as the value.
     */
    private Map<Throwable, AutomonExpirable> exceptionsMap = Utils.createExceptionMap();

    /**
     * Stops the monitoring context and records the associated exception.
     *
     * @param context   The monitoring context object (typically a timer) returned by the `start` method.
     * @param throwable The exception that occurred during the monitored execution.
     */
    @Override
    public void stop(T context, Throwable throwable) {
        stop(context); // Stop the monitoring (timer, etc.)
        put(throwable); // Store the exception for potential reuse
    }

    /**
     * Handles exceptions by storing them and delegating further tracking to `trackException`.
     * <p>
     * This method is called when an exception is thrown within the monitored code. It first stores the exception
     * in the `exceptionsMap` to prevent duplicate tracking and then calls `trackException` to perform any
     * implementation-specific exception handling (e.g., logging or metric recording).
     * <p>
     * **Note:** The order of operations is important, especially for implementations like Jamon,
     * where the stack trace needs to be available before further processing.
     *
     * @param jp        The `JoinPoint` where the exception was thrown.
     * @param throwable The exception that was thrown.
     */
    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        put(throwable); // Store the exception
        trackException(jp, throwable); // Perform implementation-specific exception tracking
    }

    /**
     * <p>Performs implementation-specific exception tracking.</p>
     * <p>This method can be overridden by subclasses to customize how exceptions are handled.
     * The default implementation does nothing.</p>
     *
     * @param jp        The `JoinPoint` where the exception was thrown.
     * @param throwable The exception that was thrown.
     */
    protected void trackException(JoinPoint jp, Throwable throwable) {
        // Default implementation: No operation
    }

    /**
     * Generates labels for the given throwable.
     *
     * @param throwable The exception for which labels are generated
     * @return A list of labels representing the exception. By default, it includes the specific exception label
     * and a general exception label (`EXCEPTION_LABEL`).
     */
    protected List<String> getLabels(Throwable throwable) {
        List<String> labels = new ArrayList<>();
        labels.add(Utils.getLabel(throwable)); // Add the specific exception label
        labels.add(EXCEPTION_LABEL); // Add the general exception label
        return labels;
    }

    /**
     * Stores the thrown exception in the `exceptionsMap`.
     * <p>
     * The exception is wrapped in an `AutomonExpirable` object, which includes a timestamp to allow for
     * automatic removal after a certain period. This prevents the map from growing indefinitely and avoids
     * duplicate tracking of the same exception within the call stack.
     *
     * @param throwable The exception to be stored.
     */
    protected void put(Throwable throwable) {
        if (!exceptionsMap.containsKey(throwable)) { // Avoid duplicates
            AutomonExpirable automonExpirable = new AutomonExpirable();
            automonExpirable.setThrowable(throwable);
            exceptionsMap.put(throwable, automonExpirable);
        }
    }

    /**
     * Retrieves the `AutomonExpirable` object associated with the given `throwable` from the `exceptionsMap`.
     *
     * @param throwable The exception to retrieve.
     * @return The `AutomonExpirable` object, or `null` if not found.
     */
    protected AutomonExpirable get(Throwable throwable) {
        return exceptionsMap.get(throwable);
    }

    /**
     * Returns the map containing recently thrown exceptions.
     * <p>
     * This method is primarily intended for testing purposes.
     *
     * @return The map of exceptions.
     */
    Map<Throwable, AutomonExpirable> getExceptionsMap() {
        return exceptionsMap;
    }
}