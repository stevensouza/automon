package org.automon.implementations;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aspectj.lang.JoinPoint;

import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * <p>Implementation of `OpenMon` that utilizes Yammer Metrics for monitoring method executions and exceptions.</p>
 * <p>It provides methods to start and stop timers for measuring method execution times and to track exceptions using counters.</p>
 */
public class Metrics extends OpenMonBase<Timer> {

    /**
     * The `MetricRegistry` used to store and manage metrics.
     */
    private static MetricRegistry metrics = new MetricRegistry();

    /**
     * Starts a Yammer Metrics `Timer` for the given join point.
     *
     * @param jp The join point representing the intercepted method.
     * @return The started `Timer`.
     */
    @Override
    public Timer start(JoinPoint.StaticPart jp) {
        return metrics.timer(name(jp.toString()));
    }

    /**
     * Stops the given Yammer Metrics `Timer`.
     *
     * @param timer The `Timer` to stop.
     */
    @Override
    public void stop(Timer timer) {
        timer.time().stop();
    }

    /**
     * Tracks an exception by incrementing corresponding counters in the `MetricRegistry`.
     *
     * @param jp        The join point where the exception occurred.
     * @param throwable The exception that was thrown.
     */
    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            metrics.counter(label).inc();
        }
    }

    /**
     * Retrieves the `MetricRegistry` used by this `Metrics` instance.
     *
     * @return The `MetricRegistry`.
     */
    public static MetricRegistry getMetricRegistry() {
        return metrics;
    }

    /**
     * Sets the `MetricRegistry` to be used by this `Metrics` instance.
     * This allows for sharing the same `MetricRegistry` with other components, such as Spring.
     *
     * @param newMetricRegistry The new `MetricRegistry` to use.
     */
    public static void setMetricRegistry(MetricRegistry newMetricRegistry) {
        metrics = newMetricRegistry;
    }
}