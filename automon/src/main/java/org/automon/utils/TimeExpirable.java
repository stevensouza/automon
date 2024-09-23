package org.automon.utils;

/**
 * <p>This class represents an object that can expire after a specified time interval.</p>
 * <p>It implements the `Expirable` interface, allowing it to be used as a value in an `ExpiringMap`.</p>
 * <p>The expiration time is determined by the `expirationIntervalInMinutes` provided in the constructor.</p>
 */
public class TimeExpirable implements Expirable {

    /**
     * The default expiration interval in minutes (default: 2).
     */
    private static final int DEFAULT_EXPIRATION_INTERVAL_IN_MINUTES = 2;

    /**
     * Number of seconds in a minute.
     */
    private static final long SECS_PER_MINUTE = 60;

    /**
     * Number of milliseconds in a second.
     */
    private static final long MS_PER_SEC = 1000;

    /**
     * The creation time of this object in milliseconds.
     */
    private long creationTime;

    /**
     * The expiration interval in milliseconds.
     */
    private long expirationIntervalInMs;

    /**
     * An object providing the current time. Allows for dependency injection (DI) in testing.
     */
    private Now now;

    /**
     * Constructs a new `TimeExpirable` with the default expiration interval.
     */
    public TimeExpirable() {
        this(DEFAULT_EXPIRATION_INTERVAL_IN_MINUTES);
    }

    /**
     * Constructs a new `TimeExpirable` with the specified expiration interval in minutes.
     *
     * @param expirationIntervalInMinutes The expiration interval in minutes.
     */
    public TimeExpirable(int expirationIntervalInMinutes) {
        this(expirationIntervalInMinutes, new CurrentTimeMills());
    }

    /**
     * Constructs a new `TimeExpirable` for testing purposes, allowing injection of a custom `Now` implementation.
     *
     * @param expirationIntervalInMinutes The expiration interval in minutes.
     * @param now                         A custom `Now` implementation for testing.
     */
    TimeExpirable(int expirationIntervalInMinutes, Now now) {
        this.now = now;
        this.creationTime = now.now();
        this.expirationIntervalInMs = convertToMs(expirationIntervalInMinutes);
    }

    /**
     * Checks if this object has expired.
     *
     * @return `true` if the current time is past the expiration time, `false` otherwise.
     */
    @Override
    public boolean isExpired() {
        return (now.now() - creationTime) >= expirationIntervalInMs;
    }

    /**
     * Converts the given expiration interval from minutes to milliseconds.
     *
     * @param expirationInternalInMinutes The expiration interval in minutes.
     * @return The equivalent expiration interval in milliseconds.
     */
    static long convertToMs(int expirationInternalInMinutes) {
        return expirationInternalInMinutes * SECS_PER_MINUTE * MS_PER_SEC;
    }

    /**
     * Default implementation of `Now` that returns the current system time in milliseconds.
     */
    private static class CurrentTimeMills implements Now {
        /**
         * Returns the current system time in milliseconds.
         *
         * @return The current time in milliseconds.
         */
        @Override
        public long now() {
            return System.currentTimeMillis();
        }
    }

    /**
     * <p>Interface representing a time provider.</p>
     * <p>This interface allows for dependency injection and mocking in unit tests, enabling control over the
     * current time used for expiration checks.</p>
     */
    public interface Now {

        /**
         * Returns the current time in milliseconds.
         *
         * @return The current time in milliseconds.
         */
        long now();
    }
}
