package org.automon.implementations;


import org.aspectj.lang.JoinPoint;

/** Class used to start/stop new relic timer and pass information used in the label for the 'stop' method. */
public class TimerContext {
    private JoinPoint.StaticPart jp;
    private long startTime;

    public TimerContext(JoinPoint.StaticPart jp) {
        this.jp = jp;
        startTime = System.currentTimeMillis();
    }

    public long stop() {
        return System.currentTimeMillis() - startTime;
    }

    public JoinPoint.StaticPart getJoinPoint() {
        return jp;
    }

}