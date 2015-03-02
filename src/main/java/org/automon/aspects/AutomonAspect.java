package org.automon.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.automon.monitors.NullImp;
import org.automon.monitors.OpenMon;

/**
 * Aspect that advises the {@link org.aspectj.lang.annotation.Around} and {@link org.aspectj.lang.annotation.AfterThrowing} annotations.
 * The appropriate methods on {@link org.automon.monitors.OpenMon} methods are called and they typically time methods and count any exceptions
 * thrown however other behavior such as logging is also possible.
 *
 */
@Aspect
public abstract class AutomonAspect  {

    private OpenMon openMon = new NullImp();
    protected  boolean enable = true;

    public boolean isEnabled() {
        return enable;
    }

    public void enable(boolean shouldEnable) {
        enable = shouldEnable;
    }


    /**
     * Advice that wraps the given pointcut and calls the appropriate {@link org.automon.monitors.OpenMon} method at the beginning and end
     * of the method call.
     *
     * @param pjp Information on the {@link org.aspectj.lang.JoinPoint}
     * @return The advised methods value or void.
     * @throws Throwable If the method throws a {@link java.lang.Throwable} the advice will rethrow it.
     */
    @Around("monitor()")
    public Object monitorPerformance(ProceedingJoinPoint pjp) throws Throwable {
        // Note context is typically a Timer/Monitor object though to this advice it is simply
        // an object and the advice doesn't care what the intent of the context/object is.
        Object context = openMon.start(pjp);
        try {
            Object retVal = pjp.proceed();
            openMon.stop(context);
            return retVal;
        } catch (Throwable throwable) {
            openMon.stop(context, throwable);
            throw throwable;
        }
    }

    @AfterThrowing(pointcut = "exceptions()", throwing = "e")
    public void monitorExceptions(JoinPoint joinPoint, Throwable e) {
            System.out.println();
            System.out.println("Exception: "+e);
            System.out.println(" jp.getKind()=" + joinPoint.getKind());
            System.out.println(" jp.getStaticPart()="+joinPoint.getStaticPart());
            //Object[] argValues = joinPoint.getArgs();
            Signature signature = joinPoint.getSignature();
            //System.out.println(" jp.getSignature().getClass()="+signature.getClass());
            openMon.exception(e.getClass().getName()); // java.lang.RuntimeException

        // Note would have to look at all the special cases here.
            if (signature instanceof MethodSignature) {
                MethodSignature methodSignature =  (MethodSignature) signature;
                String[]argNames = methodSignature.getParameterNames();
                //for (int i = 0; i < argNames.length; i++) {
                //    printMe("  argName, argValue", argNames[i] + ", " + argValues[i]);
                //}
            }
    }


    public OpenMon getOpenMon() {
        return openMon;
    }

    public void setOpenMon(OpenMon openMon) {
        this.openMon = openMon;
    }

    @Pointcut("user_monitor() && sys_monitor()")
    public void monitor() {

    }

    @Pointcut()
    public abstract void sys_monitor();

    @Pointcut()
    public abstract void user_monitor();

    @Pointcut("user_exceptions() && sys_exceptions()")
    public void exceptions() {
    }

    @Pointcut()
    public abstract void sys_exceptions();

    @Pointcut()
    public abstract void user_exceptions();


}
