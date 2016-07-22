/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.spring_aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.automon.aspects.AutomonMXBean;
import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.Utils;
//import org.automon.aspects.SpringBase;
import org.springframework.stereotype.Component;

/**
 * <p>This aspect should contain pointcut language that is compatible with Spring.  Use this as your Base class if you use Spring.
 * It will also work with any AspectJ program, but will be more limited in how expressive the pointcuts can be.</p>
 *
 * <p>Note a developer should implement and provide pointcuts that you want to monitor by implementing {@link #user_monitor()}
 * and {@link #user_exceptions()}</p>
 */

@Component 
public  class AutomonSpringAspect  {
    
    protected AutomonAspectInternals automonAspectInternals = new AutomonAspectInternals();
    protected AutomonMXBean automonJmx = new AutomonJmx(this);

    public AutomonSpringAspect() {
        // Use OpenMon the user selects and register the aspect with jmx
       // initOpenMon();
        Utils.registerWithJmx(this, automonJmx);
    }

    /* methods */
    public boolean isEnabled() {
        return !(getOpenMon() instanceof NullImp);
    }

    /** Retrieve monitoring implementation
     * @return  */
    public OpenMon getOpenMon() {
        return automonAspectInternals.getOpenMon();
    }

    /** Set monitoring implementation such as JAMon, Metrics, or JavaSimon
     * @param openMon */
    public void setOpenMon(OpenMon openMon) {
        automonAspectInternals.setOpenMon(openMon);
    }

    /**
     * Take the string of any {@link org.automon.implementations.OpenMon} registered within this classes
     * {@link org.automon.implementations.OpenMonFactory}, instantiate it and make it the current OpenMon.  If null is passed
     * in then use the default of iterating each of the preinstalled OpenMon types attempting to create them until one succeeds.
     * If one doesn't succeed then it would mean the proper jar is not available. If all of these fail then simply disable.
     *
     * @param openMonKey Something like jamon, metrics, javasimon
     */
    public void setOpenMon(String openMonKey) {
        automonAspectInternals.setOpenMon(openMonKey);
    }

    public OpenMonFactory getOpenMonFactory() {
        return automonAspectInternals.getOpenMonFactory();
    }

     /**
     * _monitor() advice - Wraps the given pointcut and calls the appropriate {@link org.automon.implementations.OpenMon} method
     * at the beginning and end of the method call.
     *
     * @param pjp
     * @return The advised methods value or void.
     * @throws Throwable If the method throws a {@link java.lang.Throwable} the advice will rethrow it.
     */
    

    public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
        // Note: context is typically a Timer/Monitor object returned by the monitoring implementation (Jamon, JavaSimon, Metrics,...)
        // though to this advice it is simply an object and the advice doesn't care what the intent of the context/object is.
        Object context = getOpenMon().start(pjp.getStaticPart());
        try {
            Object retVal = pjp.proceed();
            getOpenMon().stop(context);
            return retVal;
        } catch (Throwable throwable) {
            getOpenMon().stop(context, throwable);
            throw throwable;
        }
    }

    /**
     * exceptions() advice - Takes action on any Exception thrown.  It typically Tracks/Counts any exceptions thrown by the pointcut.
     * Note arguments are passed on to {@link org.automon.implementations.OpenMon#exception(org.aspectj.lang.JoinPoint, Throwable)}
     * @param pjp
     * @param exception
     */
    public void throwing(JoinPoint pjp, Throwable exceptionArg) {
        getOpenMon().exception(pjp, exceptionArg);
    }

}
