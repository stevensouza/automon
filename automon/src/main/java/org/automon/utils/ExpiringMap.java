package org.automon.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>This class is a `LinkedHashMap` implementation that automatically removes entries that have expired.</p>
 * <p>Expiration is determined by the `isExpired()` method of the values stored in the map, which must implement the `Expirable` interface.</p>
 *
 * <p>**Note:** This map is not thread-safe and should be wrapped in a synchronized map if used in a multi-threaded environment.</p>
 */
public class ExpiringMap<K, V extends Expirable> extends LinkedHashMap<K, V> {

    /**
     * Overrides the `removeEldestEntry` method to trigger the removal of expired entries.
     * <p>
     * This method is called by the LinkedHashMap after a new entry is added. It checks if the eldest entry (Least Recently Used)
     * has expired. If so, it calls `removeOldEntries` to remove all expired entries from the map.
     *
     * @param eldest The eldest entry in the map.
     * @return `false`, indicating that no additional action is needed by the LinkedHashMap.
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (eldest.getValue().isExpired()) {
            removeOldEntries();
        }
        return false;
    }

    /**
     * Removes expired entries from the map.
     * <p>
     * This method iterates through the map entries and removes any whose value indicates that it has expired
     * by returning `true` from the `isExpired()` method. The iteration stops as soon as a non-expired entry is encountered,
     * as the map is ordered from oldest to newest.
     * <p>
     * **Note:** This method is not thread-safe unless the map is synchronized externally.
     */
    void removeOldEntries() {
        Iterator<Map.Entry<K, V>> iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            } else {
                return; // Stop iterating once a non-expired entry is found
            }
        }
    }
}