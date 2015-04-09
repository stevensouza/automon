package org.automon.implementations;

import com.newrelic.api.agent.NewRelic;
import org.aspectj.lang.JoinPoint;

/**
 * Created by stevesouza on 4/7/15.
 */
public class NewRelicImp extends OpenMonBase<TimerContext> {

    @Override
    public TimerContext start(JoinPoint.StaticPart jp) {
        return new TimerContext(jp);
    }

    @Override
    public void stop(TimerContext context) {
       NewRelic.recordResponseTimeMetric("Custom/" + context.getJoinPoint().toString(), context.stop());
    }

       @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
       NewRelic.noticeError(throwable);
    }

}
