package org.automon.utils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Starts with {@link org.automon.utils.TimeExpirable} behavior and adds the ability to store the exception and argument name and values
 * that occurred during an exception.
 */
public class AutomonExpirable extends TimeExpirable {
    private Throwable throwable;
    private Map<String, Object> argNamesAndValues;

    // holds a reference that will be displayed in jamon fifo buffers etc. Because
    // jamon can be clustered with hazelcast the data must be serializable hence
    // that is what the AtomicReference must hold.  Also not that AtomicReference is not
    // required, but what is required is the ability to hold a serializable object in a serializable
    // container that has its toString method just return the underlying objects string.
    // For example List would surround the returned string in [].  The class must also be part of
    // the jdk and not require any extra classes as for example automon might not be available on
    // all servers in the jamon cluster.
    private final AtomicReference jamonDetails = new AtomicReference();;

    public AutomonExpirable() {
    }

    public AutomonExpirable(int expirationIntervalInMinutes) {
        super(expirationIntervalInMinutes);
    }

    public Map<String, Object> getArgNamesAndValues() {
        return argNamesAndValues;
    }

    public void setArgNamesAndValues(Map<String, Object>  argNamesAndValues) {
        this.argNamesAndValues = argNamesAndValues;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public AtomicReference setJamonDetails(Serializable details)  {
        jamonDetails.set(details);
        return jamonDetails;
    }

    public AtomicReference getJamonDetails()  {
        return jamonDetails;
    }


    public String toString() {
        return new StringBuilder().
                append(Utils.argNameValuePairsToString(argNamesAndValues)).
                append(Utils.LINE_SEPARATOR).
                append(Utils.getExceptionTrace(throwable)).
                toString();
    }

}
