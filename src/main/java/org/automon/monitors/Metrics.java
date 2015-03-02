package org.automon.monitors;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aspectj.lang.JoinPoint;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by stevesouza on 2/26/15.
 */
public class Metrics extends OpenMonBase<Timer> {
    private MetricRegistry metrics = new MetricRegistry();

    @Override
    public Timer start(JoinPoint jp) {
        return metrics.timer(name(getLabel(jp)));
    }

    @Override
    public void stop(Timer timer) {
        timer.time().stop();
    }

    @Override
    public void exception(JoinPoint jp, Throwable throwable) {
        metrics.counter(getLabel(throwable)).inc();
    }

    public MetricRegistry getMetricRegistry() {
        return metrics;
    }

    public void setMetricRegistry(MetricRegistry metrics) {
        this.metrics = metrics;
    }

}
