package org.automon.spring_aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.automon.tracing.jmx.TraceJmxController;
import org.automon.utils.LogTracingHelper;
import org.automon.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Abstract base aspect for tracing using Spring AOP and AspectJ annotations.
 * This class provides common functionality for tracing method executions.
 *
 * <p>Logging can be enabled or disabled using the `enableLogging` method or by providing the `enableLogging` flag in the constructor.</p>
 *
 * <p>This aspect has an associated JMX MBean that can be used to configure it.
 * Note this aspect is created as a singleton as always is the case by default in aspectj.
 * </p>
 */
@Aspect
@Component // Make it a Spring component for auto-detection
public abstract class TracingAspect {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    protected static final String BEFORE = "BEFORE";
    protected static final String AFTER = "AFTER";
    protected final LogTracingHelper helper = LogTracingHelper.getInstance();
    protected static final TraceJmxController jmxController = new TraceJmxController();
    private String purpose = "trace";

    public TracingAspect() {
        this(true, true);
    }

    public TracingAspect(boolean enable) {
        this(enable, true);
    }

    public TracingAspect(boolean enable, boolean enableLogging) {
        jmxController.enable(enable);     // Set overall tracing enabled state
        jmxController.enableLogging(enableLogging); // Set logging enabled state
    }

    @Pointcut // No expression here, it's still abstract
    public abstract void trace();

    @Pointcut("if(isEnabled())")
    public boolean enabled() {
        return true; // This pointcut always matches if isEnabled() returns true
    }

    @AfterThrowing(pointcut = "trace()", throwing = "throwable")
    public void afterThrowing(Throwable throwable) {
        try (helper) {
            helper.withException(throwable.getClass().getCanonicalName());
            if (jmxController.isLoggingEnabled()) {
                LOGGER.error(AFTER, throwable);
            }
        }
    }

    protected void logBefore() {
        if (jmxController.isLoggingEnabled()) {
            LOGGER.info(BEFORE);
        }
    }

    protected void logAfter() {
        if (jmxController.isLoggingEnabled()) {
            LOGGER.info(AFTER);
        }
    }

    protected void registerJmxController() {
        Utils.registerWithJmx(getPurpose(), this, jmxController);
    }

    protected static TraceJmxController getJmxController() {
        return jmxController;
    }

    public static boolean isEnabled() {
        return jmxController.isEnabled();
    }

    protected String objectToString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}