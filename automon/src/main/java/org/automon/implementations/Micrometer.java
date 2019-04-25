package org.automon.implementations;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;

import java.util.concurrent.TimeUnit;

/**
 * {@link org.automon.implementations.OpenMon} implementation that https://micrometer.io to time methods, and count exceptions.
 * Note micrometer is a wrapper for many underlying monitoring api's.  It is also the default monitoring api of Spring.
 */
public class Micrometer extends OpenMonBase<TimerContext> {

    private static MeterRegistry registry = new SimpleMeterRegistry();

    @Override
    public TimerContext start(JoinPoint.StaticPart jp) {
        return new TimerContext(jp);
    }

    @Override
    public void stop(TimerContext context) {
        long stop = context.stop();
        getTimer(context.getJoinPoint().toString()).record(stop, TimeUnit.MILLISECONDS);
    }


    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        getCounter(Utils.getLabel(throwable)).increment();
    }


    /**
     * This method can be overridden if the desired method Timer doesn't have the desired characteristics.
     *
     * @param methodName string that represents the method being called
     * @return
     */
    protected Timer getTimer(String methodName) {
        return Timer.builder(methodName)
                .tag("automon.method", "org.automon.Method")
                .description("automon.org method timer")
                .register(registry);
    }

    /**
     * This method can be overridden if the desired exception counter doesn't have the desired characteristics.
     *
     * @param exceptionName string that represents the exception being thrown
     * @return
     */
    protected Counter getCounter(String exceptionName) {
        return Counter.builder(exceptionName)
                .tag("automon.exception", EXCEPTION_LABEL)
                .description("automon.org exception counter")
                .register(registry);
    }

    public static MeterRegistry getMeterRegistry() {
        return registry;
    }

    public static void setMeterRegistry(MeterRegistry newMeterRegistry) {
        registry = newMeterRegistry;
    }

}
