package org.automon.utils;

/**
 * Created by stevesouza on 3/3/15.
 */
public class TimeExpirable implements Expirable {

    // Any exceptions in the map older than this will be removed on 'put'.
    private static int DEFAULT_EXPIRATION_INTERVAL_IN_MINUTES = 2;
    private static long SECS_PER_MINUTE=60;
    private static long MS_PER_SEC=1000;

    private long creationTime;
    private long expirationIntervalInMs;
    private Now now;

    public TimeExpirable() {
        this(DEFAULT_EXPIRATION_INTERVAL_IN_MINUTES);
    }

    public TimeExpirable(int expirationIntervalInMinutes) {
        this(expirationIntervalInMinutes, new CurrentTimeMills());
    }

    // used for testing
    TimeExpirable(int expirationIntervalInMinutes, Now now) {
        this.now = now;
        creationTime = now.now();
        this.expirationIntervalInMs = convertToMs(expirationIntervalInMinutes);
    }

    @Override
    public boolean isExpired() {
        return (now.now()-creationTime)>=expirationIntervalInMs;
    }

    static long convertToMs(int expirationInternalInMinutes) {
        return expirationInternalInMinutes*SECS_PER_MINUTE*MS_PER_SEC;
    }


    private static class CurrentTimeMills implements Now {
        @Override
        public long now() {
            return System.currentTimeMillis();
        }
    }

    // used to facilitate testing with a mock.
    public  interface Now {
        public long now();
    }
}
