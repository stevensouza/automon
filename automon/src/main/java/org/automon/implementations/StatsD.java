
package org.automon.implementations;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

/**
 * {@link org.automon.implementations.OpenMon} implementation that uses StatsD to time methods, and count exceptions.
 *
 * @author stevesouza
 */
public class StatsD extends OpenMonBase<TimerContext>{

    private static final StatsDClient statsdClient = new NonBlockingStatsDClient("stevesouza.playground", "localhost", 8125);

    @Override
    public TimerContext start(JoinPoint.StaticPart jp) {
                return new TimerContext(jp);
    }

    /**
     * Save invocation/execution time associated with the method to StatsD in the format: com.mypackage.myMethod
     */
    @Override
    public void stop(TimerContext context) {  
       Signature sig = context.getJoinPoint().getSignature();
       String className = sig.getDeclaringTypeName(); // package name: com.my.package
       String methodName = sig.getName(); // method name: myMethod
       String label = className+"."+methodName;
        
       statsdClient.recordExecutionTime(label, context.stop());
    }

   /**
     * Count exceptions by sending the exception name to StatsD
     */
    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            statsdClient.incrementCounter(label);
        }
    }
       
}
