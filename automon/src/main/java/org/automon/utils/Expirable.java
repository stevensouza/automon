package org.automon.utils;

/**
 * <p>This interface defines the behavior for objects that can expire after a certain period.</p>
 * <p>It is intended to be used as values in an {@link org.automon.utils.ExpiringMap}, where entries are automatically removed when they expire.</p>
 * <p>The `isExpired()` method is called by the `ExpiringMap` to determine if an object should be removed.
 * When the oldest entry in the map expires, the map also checks other entries for expiration to ensure it doesn't grow unbounded.</p>
 * <p>This mechanism is used in Automon to manage resources and prevent memory leaks by automatically cleaning up expired data.</p>
 */
public interface Expirable {

    /**
     * Checks if this object has expired.
     *
     * @return `true` if the object has expired and should be removed from the `ExpiringMap`, `false` otherwise.
     */
    boolean isExpired();
}