
package org.automon.implementations;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

/**
 * {@link org.automon.implementations.OpenMon} implementation that uses StatsD to time methods, and count exceptions.
 *
 *    statsdClient.incrementCounter("java.sql.SQLException,ErrorCode=400,SQLState=Login failure");
            statsdClient.incrementCounter("java.sql.SQLException.ErrorCode 400-SQLState Login failure");

replace ,ErrorCode= with .ErrorCode<space>
replace ,SQLState= with -SQLState<space>

* properties - prefix default automon, localhost, 8125
test with statsd not explicit
sample scripts for all supported types
documentation
release
* 
 * @author stevesouza
 */
public class StatsD extends OpenMonBase<TimerContext>{

    private static final StatsDClient statsdClient = new NonBlockingStatsDClient("automon", "localhost", 8125);

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
            statsdClient.incrementCounter(formatExceptionForStatsD(label));
        }
    }
    
    /** statsD doesn't format = and , properly.  Instead of depending on statsd to 
     * format them create a string that looks acceptable.  This is primarily done for 
     * sql exceptions of the format:   java.sql.SQLException,ErrorCode=400,SQLState=Login failure
     * 
     * @param exceptionLabel
     * @return 
     */
    String formatExceptionForStatsD(String exceptionLabel) {
        if (exceptionLabel==null) {
            return null;
        }
        
        return exceptionLabel.
                replace(",ErrorCode=", ".ErrorCode ").
                replace(",SQLState=", "-SQLState ");
    }
       
}
