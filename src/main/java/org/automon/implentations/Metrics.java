package org.automon.implentations;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.automon.OpenMon;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by stevesouza on 2/26/15.
 */
public class Metrics implements OpenMon<Timer.Context> {
    private MetricRegistry metrics = new MetricRegistry();

    @Override
    public Timer.Context start(String label) {
        Timer timer = metrics.timer(name(label));
        return timer.time();
    }

    @Override
    public void stop(Timer.Context timerContext) {
        timerContext.stop();
        System.out.println("Metrics Timer.Context: "+timerContext);
    }

    @Override
    public void exception(String label) {
        Counter mon = metrics.counter(label);
        mon.inc();
        System.out.println("Metrics counter: "+mon);
    }

    @Override
    public void enable(boolean enable) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
