package org.automon.implementations;

import org.aspectj.lang.JoinPoint;
import org.javasimon.Simon;
import org.javasimon.SimonManager;
import org.javasimon.Split;

import java.util.List;
import org.automon.utils.Utils;

/**
 * {@link org.automon.implementations.OpenMon} implementation that uses JavaSimon to time methods, and count exceptions.
 */
public class JavaSimon extends OpenMonBase<Split> {
    /**
     * <p>
     * HACK ALERT: The following variable fixes problem when user tries to create a JavaSimon when simon is not in the classpath.
     *
     * For some reason when JavaSimon jars aren't in the classpath the NoClassDefFoundError
     * exception is not thrown at Object creation time but when 'trackException' is called.
     * If this would happen instead when JavaSimon is created the exception is detected by {@link org.automon.implementations.OpenMonFactory}
     * and a {@link org.automon.implementations.NullImp} implementation is returned. The advantage of this is that the
     * program continues in a healthy manner.  This works fine with the other {@link org.automon.implementations.OpenMon} implementations.
     * By putting a reference to simon as an instance variable the exception happens at JavaSimon creation time and the desired effect
     * occurs.
     * </p>
     *
     * <p>The exception this hack prevents is shown below:
     *  Exception in thread "main" java.lang.NoClassDefFoundError: org/javasimon/SimonManager
     *  at org.automon.implementations.JavaSimon.trackException(JavaSimon.java:28)
     *  at org.automon.implementations.OpenMonBase.exception(OpenMonBase.java:43)
     * </p>
     */
    private final Simon hackToCauseNoClassDefFoundErrorOnCreation = SimonManager.getRootSimon();

    @Override
    public Split start(JoinPoint.StaticPart  jp) {
        return SimonManager.getStopwatch(cleanSpaces(jp.toString())).start();
    }

    @Override
    public void stop(Split split) {
        split.stop();
    }

    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            String simonCompatibleLabel = cleanExceptionForSimon(label);
            SimonManager.getCounter(simonCompatibleLabel).increase();
        }
    }
    
    // JavaSimon doesn't allow spaces in monitors.
    private String cleanSpaces(String label) {
        return label.replaceAll("[ ]+", "<space>");
    }
    
    String cleanExceptionForSimon(String exceptionLabel) {
        return cleanSpaces(Utils.formatExceptionForToolsWithLimitedCharacterSet(exceptionLabel));
    }

}
