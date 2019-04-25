package org.automon.implementations;

import com.newrelic.api.agent.NewRelic;
import org.aspectj.lang.JoinPoint;

/**
 * Created by stevesouza on 4/7/15.
 */
public class NewRelicImp extends OpenMonBase<TimerContext> {
    /**
     * <p>
     * HACK ALERT: The following variable fixes problem when user tries to create a NewRelic instance when NewRelic is not in the classpath.
     * <p>
     * For some reason when NewRelic jars aren't in the classpath the NoClassDefFoundError
     * exception is not thrown at Object creation time but when 'trackException' is called.
     * If this would happen instead when JavaSimon is created the exception is detected by {@link org.automon.implementations.OpenMonFactory}
     * and a {@link org.automon.implementations.NullImp} implementation is returned. The advantage of this is that the
     * program continues in a healthy manner.  This works fine with the other {@link org.automon.implementations.OpenMon} implementations.
     * By putting a reference to NewRelic as an instance variable the exception happens at JavaSimon creation time and the desired effect
     * occurs.
     * </p>
     */
    private final NewRelic hackToCauseNoClassDefFoundErrorOnCreatio = new NewRelic();

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
