package org.automon.utils;

/**
 * This interface is meant to be added as a value to {@link org.automon.utils.ExpiringMap}.
 * Every time a new object is put in the oldest {@link org.automon.utils.Expirable} will be checked to see if it should be
 * removed. This capability is used in Automon to ensure that the underlying map doesn't grow unbounded.
 */
public interface Expirable {
    public boolean isExpired();
}
