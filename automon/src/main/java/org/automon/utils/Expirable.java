package org.automon.utils;

/**
 * This interface is meant to be added as a value to {@link org.automon.utils.ExpiringMap}.
 * Every time a new object is 'put' in the map the oldest {@link org.automon.utils.Expirable} will be checked to see if it should be
 * removed. If the oldest entry needs to be removed the map is checked for any other values to see if they should be removed
 * too and they will also be removed.  This capability is used in Automon to ensure that the underlying map doesn't grow unbounded.
 */
public interface Expirable {
    /**
     * @return true if the object has expired.
     */
    public boolean isExpired();
}
