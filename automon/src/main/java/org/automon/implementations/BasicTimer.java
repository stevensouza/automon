package org.automon.implementations;

/**
 * A basic timer implementation for measuring elapsed time.
 */
public class BasicTimer {

    /**
     * The start time in milliseconds.
     */
    protected long startTime;

    /**
     * Creates a new `BasicTimer` and records the current system time as the start time.
     */
    public BasicTimer() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Stops the timer and calculates the elapsed time in milliseconds.
     *
     * @return The elapsed time since the timer was started, in milliseconds.
     */
    public long stop() {
        return System.currentTimeMillis() - startTime;
    }
}