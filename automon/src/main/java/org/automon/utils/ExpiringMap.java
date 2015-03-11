package org.automon.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This {@link java.util.Map} implementation removes any map entries older than the time interval
 * specified in the constructor upon a new element being 'put' in the map.
 *
 * Note: This map is not thread safe and must be wrapped in a synchronized map to make it work
 * in a multi-threaded environment.
 */
public class ExpiringMap<K, V extends Expirable> extends  LinkedHashMap<K, V> {

    // Note the jdk calls this from the 'put' method (after a new item has been put in)
    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        // if we know the LRU entry has expired then loop to remove it and any other old entries that may exist.
        if (eldest.getValue().isExpired()) {
            removeOldEntries();
        }

        return false; // indicate nothing else needs to be done
    }

    /** iterate the collection removing any entries older than the specified expiration interval.  This is not thread-safe
     * unless the collection is synchronized. The map is ordered from oldest to newest, so once
     * we encounter one entry that is less than the threshold we can exit the removal loop.
     */
    void removeOldEntries() {
        Iterator<Map.Entry<K, V>> iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            } else {
                return;
            }
        }
    }
}