package org.automon.utils;

import java.util.List;

/**
 * Starts with {@link org.automon.utils.TimeExpirable} behavior and adds the ability to store the exception and argument name and values
 * that occurred during an exception.
 */
public class AutomonExpirable extends TimeExpirable {
    private Throwable throwable;
    private List<String> argNamesAndValues;

    public AutomonExpirable() {
    }

    public AutomonExpirable(int expirationIntervalInMinutes) {
        super(expirationIntervalInMinutes);
    }

    public List<String> getArgNamesAndValues() {
        return argNamesAndValues;
    }

    public void setArgNamesAndValues(List<String> argNamesAndValues) {
        this.argNamesAndValues = argNamesAndValues;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String toString() {
        return new StringBuilder().
                append(Utils.getExceptionTrace(throwable)).
                append(Utils.LINE_SEPARATOR).
                append(Utils.argNameValuePairsToString(argNamesAndValues)).
                toString();
    }

}
