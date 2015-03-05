package org.automon.utils;

import java.util.List;

/**
 * Created by stevesouza on 3/5/15.
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

}
