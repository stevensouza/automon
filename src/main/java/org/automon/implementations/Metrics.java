package org.automon.implementations;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aspectj.lang.JoinPoint;
import org.automon.utils.Utils;

import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by stevesouza on 2/26/15.
 */
public class Metrics extends OpenMonBase<Timer> {
    private MetricRegistry metrics = new MetricRegistry();

    @Override
    public Timer start(JoinPoint jp) {
        return metrics.timer(name(Utils.getLabel(jp)));
    }

    @Override
    public void stop(Timer timer) {
        timer.time().stop();
    }

    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            metrics.counter(label).inc();
        }
    }

    public MetricRegistry getMetricRegistry() {
        return metrics;
    }

    public void setMetricRegistry(MetricRegistry metrics) {
        this.metrics = metrics;
    }

}
