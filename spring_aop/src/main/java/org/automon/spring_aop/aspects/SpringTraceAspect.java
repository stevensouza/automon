package org.automon.spring_aop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class serves as a demonstration of how to write and configure an aspect using Spring AOP.
 * It showcases various types of advice (@Around, @Before, @After, @AfterReturning, @AfterThrowing) and
 * their application to a specific pointcut.
 *
 * <p>This aspect specifically targets the `bye` method in the `MonitorMe` class.</p>
 */
@Component
@Aspect
public class SpringTraceAspect {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    @Pointcut("execution(* org.automon.spring_aop.MonitorMe.bye(..))")
    public void select() {}

    @Around("select()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.info("Around method advice: {}", joinPoint); // Use LOGGER.info
        return joinPoint.proceed();
    }

    @Before("select()")
    public void beforeAdvice(JoinPoint joinPoint) {
        LOGGER.info("Before method execution: {}", joinPoint.getSignature()); // Use LOGGER.info
    }

    @After("select()")
    public void afterAdvice(JoinPoint joinPoint) {
        LOGGER.info("After method execution: {}", joinPoint.getSignature()); // Use LOGGER.info
    }

    @AfterReturning(pointcut = "select()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        LOGGER.info("Method returned normally: {}, Result: {}", joinPoint.getSignature(), result); // Use LOGGER.info
    }

    @AfterThrowing(pointcut = "select()", throwing = "exception")
    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable exception) {
        LOGGER.error("Method threw exception: {}, Exception: {}", joinPoint.getSignature(), exception); // Use LOGGER.error
    }
}