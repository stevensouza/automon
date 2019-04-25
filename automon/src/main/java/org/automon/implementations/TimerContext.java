package org.automon.implementations;


import org.aspectj.lang.JoinPoint;

/**
 * Class used to start/stop new relic timer and pass information used in the label for the 'stop' method.
 */
public class TimerContext extends BasicTimer {
    private JoinPoint.StaticPart jp;

    public TimerContext(JoinPoint.StaticPart jp) {
        this.jp = jp;
    }

    public JoinPoint.StaticPart getJoinPoint() {
        return jp;
    }

}