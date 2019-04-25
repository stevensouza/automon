package org.automon.implementations;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.Utils;

import java.util.List;
import java.util.Properties;

/**
 * {@link org.automon.implementations.OpenMon} implementation that uses StatsD to time methods, and count exceptions.
 * <p>
 * Note to use this you will need to have your statsD server and specify the prefix, host and port you would like to use.
 * This can be specified via the command line, or in an automon.properties file.  The default values are specified in this class.
 *
 * <ul>
 * <li>-Dorg.automon.statsd.prefix=myapplication</li>
 * <li>-Dorg.automon.statsd.hostname=mystatsdhost</li>
 * <li>-Dorg.automon.statsd.port=8125</li>
 * </ul>
 *
 * @author stevesouza
 */
public class StatsD extends OpenMonBase<TimerContext> {

    private final StatsDClient statsdClient;

    public StatsD() {
        StatsDPropsLoader propsLoader = new StatsDPropsLoader(new AutomonPropertiesLoader());
        statsdClient = new NonBlockingStatsDClient(propsLoader.getPrefix(), propsLoader.getHostName(), propsLoader.getPort());
    }

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
        String label = className + "." + methodName;

        statsdClient.recordExecutionTime(label, context.stop());
    }

    /**
     * Count exceptions by sending the exception name to StatsD
     */
    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            statsdClient.incrementCounter(Utils.formatExceptionForToolsWithLimitedCharacterSet(label));
        }
    }

    public void close() {
        if (statsdClient != null) {
            statsdClient.stop();
        }
    }


    static class StatsDPropsLoader {
        private static final String PREFIX_KEY = "org.automon.statsd.prefix";
        private static final String HOSTNAME_KEY = "org.automon.statsd.hostname";
        private static final String PORT_KEY = "org.automon.statsd.port";

        // visible for testing
        static final String PREFIX_VALUE = "automon";
        static final String HOSTNAME_VALUE = "localhost";
        static final String PORT_VALUE = "8125";

        private final String prefix;
        private final String hostName;
        private final int port;

        StatsDPropsLoader(AutomonPropertiesLoader propsLoader) {
            Properties props = propsLoader.getProperties();
            prefix = props.getProperty(PREFIX_KEY, PREFIX_VALUE);
            hostName = props.getProperty(HOSTNAME_KEY, HOSTNAME_VALUE);
            port = Integer.parseInt(props.getProperty(PORT_KEY, PORT_VALUE));
        }

        /**
         * methods for testing
         */
        String getPrefix() {
            return prefix;
        }

        String getHostName() {
            return hostName;
        }

        int getPort() {
            return port;
        }

    }

}
