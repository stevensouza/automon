package org.automon.implementations;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;

import java.util.concurrent.TimeUnit;

/**
 * <p>An `OpenMon` implementation that utilizes Micrometer to time methods and count exceptions.</p>
 *
 * <p>Micrometer serves as a facade for various underlying monitoring APIs and is the default monitoring API used by Spring.</p>
 *
 * <h3>Integration with Spring's `MeterRegistry`</h3>
 *
 * <p>To share the same `MeterRegistry` with Spring, access the `MeterRegistry` in your Spring application using `@Autowired`
 * and then call `Micrometer.setMeterRegistry(springMeterRegistry)` with the Spring `MeterRegistry`.</p>
 *
 * <pre>
 * &#64;Autowired
 * public MetricsController(MeterRegistry registry) {
 *     Micrometer.setMeterRegistry(registry);
 * }
 * </pre>
 *
 * <h3>Viewing Metrics in Spring</h3>
 *
 * <p>In a Spring application, you can access the collected metrics via the Actuator endpoints:</p>
 *
 * <ul>
 *     <li>For all metrics: `http://localhost:8080/actuator/metrics`</li>
 *     <li>For a specific metric:
 *     <ul>
 *         <li>`http://localhost:8080/actuator/metrics/execution(int org.tempuri.AddResponse.getAddResult())`</li>
 *         <li>`http://localhost:8080/actuator/metrics/execution(int%20org.tempuri.AddResponse.getAddResult())`</li>
 *     </ul>
 *     </li>
 * </ul>
 *
 * <p>Enabling Micrometer for Monitoring:</p>
 *
 * <p>To enable this class for monitoring, add the following line to your `automon.properties` file:</p>
 *
 * <pre>
 * org.automon=micrometer
 * </pre>
 */
public class Micrometer extends OpenMonBase<TimerContext> {

    /**
     * The `MeterRegistry` used to store and manage metrics.
     */
    private static MeterRegistry registry = new SimpleMeterRegistry();

    /**
     * Retrieves the `MeterRegistry` used by this `Micrometer` instance.
     *
     * @return The `MeterRegistry`.
     */
    public static MeterRegistry getMeterRegistry() {
        return registry;
    }

    /**
     * Sets the `MeterRegistry` to be used by this `Micrometer` instance.
     * This allows for sharing the same `MeterRegistry` with other components, such as Spring.
     *
     * @param newMeterRegistry The new `MeterRegistry` to use.
     */
    public static void setMeterRegistry(MeterRegistry newMeterRegistry) {
        registry = newMeterRegistry;
    }

    /**
     * Starts monitoring by creating a `TimerContext` for the given join point.
     *
     * @param jp The join point representing the intercepted method.
     * @return A new `TimerContext` associated with the join point.
     */
    @Override
    public TimerContext start(JoinPoint.StaticPart jp) {
        return new TimerContext(jp);
    }

    /**
     * Stops the timer and records the execution time using the associated `Timer`.
     *
     * @param context The `TimerContext` containing the join point and start time information.
     */
    @Override
    public void stop(TimerContext context) {
        long executionTimeMs = context.stop();
        getTimer(context.getJoinPoint().toString()).record(executionTimeMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Tracks an exception by incrementing the corresponding counter in the `MeterRegistry`.
     *
     * @param jp        The join point where the exception occurred.
     * @param throwable The exception that was thrown.
     */
    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        getCounter(Utils.getLabel(throwable)).increment();
    }

    /**
     * Retrieves a `Timer` for the given method name.
     * <p>
     * This method can be overridden to customize the `Timer` creation if needed.
     *
     * @param methodName The name of the method being monitored.
     * @return A `Timer` for the given method name.
     */
    protected Timer getTimer(String methodName) {
        return Timer.builder(methodName)
                .tag("automon", "method")
                .description("automon.org method timer")
                .register(registry);
    }

    /**
     * Retrieves a `Counter` for the given exception name.
     * <p>
     * This method can be overridden to customize the `Counter` creation if needed.
     *
     * @param exceptionName The name of the exception being tracked.
     * @return A `Counter` for the given exception name.
     */
    protected Counter getCounter(String exceptionName) {
        return Counter.builder(exceptionName)
                .tag("automon", "exception")
                .description("automon.org exception counter")
                .register(registry);
    }
}
