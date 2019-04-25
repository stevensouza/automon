package org.automon.implementations;

public class BasicTimer {
    protected long startTime;

    public BasicTimer() {
        startTime = System.currentTimeMillis();
    }

    public long stop() {
        return System.currentTimeMillis() - startTime;
    }
}
