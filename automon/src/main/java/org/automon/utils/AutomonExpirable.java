package org.automon.utils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>This class extends the functionality of `TimeExpirable` to include the storage of exception details and method argument information.</p>
 * <p>It is designed to capture and retain contextual data associated with exceptions, such as the exception itself,
 * the names and values of method arguments at the time of the exception, and additional details for Jamon monitoring.</p>
 */
public class AutomonExpirable extends TimeExpirable {

    /**
     * The throwable (exception) that occurred.
     */
    private Throwable throwable;

    /**
     * A map containing the names and values of method arguments at the time of the exception.
     */
    private Map<String, Object> argNamesAndValues;

    /**
     * <p>An `AtomicReference` to hold details that will be displayed in Jamon FIFO buffers, etc.</p>
     * <p>Since Jamon can be clustered with Hazelcast, the data must be serializable. `AtomicReference` is used here
     * because it can hold a serializable object and its `toString()` method simply returns the underlying object's string representation.
     * Other suitable JDK classes that fulfill these requirements could also be used.</p>
     * <p>This is important for ensuring compatibility across the Jamon cluster, as custom classes like `Automon`
     * might not be available on all servers for deserialization.</p>
     */
    private final AtomicReference<Serializable> jamonDetails = new AtomicReference<>();

    /**
     * Constructs a new `AutomonExpirable` with the default expiration interval.
     */
    public AutomonExpirable() {
    }

    /**
     * Constructs a new `AutomonExpirable` with the specified expiration interval in minutes.
     *
     * @param expirationIntervalInMinutes The time in minutes after which this object will expire.
     */
    public AutomonExpirable(int expirationIntervalInMinutes) {
        super(expirationIntervalInMinutes);
    }

    /**
     * Retrieves the map of argument names and values associated with the exception.
     *
     * @return A map where keys are argument names and values are their corresponding values at the time of the exception.
     */
    public Map<String, Object> getArgNamesAndValues() {
        return argNamesAndValues;
    }

    /**
     * Sets the map of argument names and values associated with the exception.
     *
     * @param argNamesAndValues A map where keys are argument names and values are their corresponding values at the time of the exception.
     */
    public void setArgNamesAndValues(Map<String, Object> argNamesAndValues) {
        this.argNamesAndValues = argNamesAndValues;
    }

    /**
     * Retrieves the throwable (exception) associated with this object.
     *
     * @return The `Throwable` object.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Sets the throwable (exception) associated with this object.
     *
     * @param throwable The `Throwable` object to be set.
     */
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Sets the Jamon-specific details for this object.
     *
     * @param details Serializable object containing Jamon-specific details.
     * @return The `AtomicReference` holding the Jamon details.
     */
    public AtomicReference<Serializable> setJamonDetails(Serializable details) {
        jamonDetails.set(details);
        return jamonDetails;
    }

    /**
     * Retrieves the Jamon-specific details for this object.
     *
     * @return The `AtomicReference` holding the Jamon details.
     */
    public AtomicReference<Serializable> getJamonDetails() {
        return jamonDetails;
    }

    /**
     * Returns a string representation of this object, including formatted argument name-value pairs and the exception stack trace.
     *
     * @return A string representation of the object.
     */
    public String toString() {
        return new StringBuilder().
                append(Utils.argNameValuePairsToString(argNamesAndValues)).
                append(Utils.LINE_SEPARATOR).
                append(Utils.getExceptionTrace(throwable)).
                toString();
    }
}
