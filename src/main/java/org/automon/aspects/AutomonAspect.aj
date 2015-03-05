package org.automon.aspects;

import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;

/**
 * Aspect that advises the {@link org.aspectj.lang.annotation.Around} and {@link org.aspectj.lang.annotation.AfterThrowing} annotations.
 * The appropriate methods on {@link org.automon.implementations.OpenMon} methods are called and they typically time methods and count any exceptions
 * thrown however other behavior such as logging is also possible.
 *
 */
//@Aspect
public abstract aspect AutomonAspect  {

    private OpenMon openMon = new NullImp();
    protected  boolean enable = true;

    public boolean isEnabled() {
        return enable;
    }

    public void enable(boolean shouldEnable) {
        enable = shouldEnable;
    }


    /**
     * Advice that wraps the given pointcut and calls the appropriate {@link org.automon.implementations.OpenMon} method at the beginning and end
     * of the method call.
     *
     * @param pjp Information on the {@link org.aspectj.lang.JoinPoint}
     * @return The advised methods value or void.
     * @throws Throwable If the method throws a {@link java.lang.Throwable} the advice will rethrow it.
     */
//    @Around("monitor()")
    Object around() throws Throwable : monitor()  {
   // public Object monitorPerformance(ProceedingJoinPoint pjp) throws Throwable {
        // Note context is typically a Timer/Monitor object though to this advice it is simply
        // an object and the advice doesn't care what the intent of the context/object is.
        Object context = openMon.start(thisJoinPointStaticPart);
        try {
            Object retVal = proceed();
            openMon.stop(context);
            return retVal;
        } catch (Throwable throwable) {
            openMon.stop(context, throwable);
            throw throwable;
        }
    }

    //@AfterThrowing(pointcut = "exceptions()", throwing = "throwable")

    after() throwing(Throwable throwable): exceptions() {
   // public void monitorExceptions(JoinPoint jp, Throwable throwable) {
        openMon.exception(thisJoinPoint, throwable);
    }

    public OpenMon getOpenMon() {
        return openMon;
    }

    public void setOpenMon(OpenMon openMon) {
        this.openMon = openMon;
    }

    public pointcut monitor() : user_monitor() && sys_monitor();
    public abstract pointcut sys_monitor();
    public abstract pointcut user_monitor();

    public pointcut exceptions() : user_exceptions() && sys_exceptions();
    public abstract pointcut sys_exceptions();
    public abstract pointcut user_exceptions();


    //@Pointcut("user_monitor() && sys_monitor()")
//    public void monitor() {
//
//    }

//    @Pointcut()
//    public abstract void sys_monitor();
//
//    @Pointcut()
//    public abstract void user_monitor();
//
//



//
//    @Pointcut()
//    public abstract void sys_monitor();
//
//    @Pointcut()
//    public abstract void user_monitor();
//    @Pointcut("user_exceptions() && sys_exceptions()")
//    public void exceptions() {
//    }
//
//    @Pointcut()
//    public abstract void sys_exceptions();
//
//    @Pointcut()
//    public abstract void user_exceptions();


}
