/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.spring_aop;

import java.util.Properties;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import static org.aspectj.weaver.MemberImpl.pointcut;
import org.automon.aspects.AutomonMXBean;
import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.AutomonPropertiesLoader;
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

@Aspect
@Component 
public  class AutomonSpringAspect  {
    
    // extends SpringBase
     // @Pointcut("execu)tion(* org.automon.spring_aop.*(..))")
    //@Pointcut("execution(public * *.*(..))")
//      @Pointcut("execution(public * *(..))")
//      public void user_monitor() {};

 
    /** pointcut that determines what is monitored for exceptions.  It can be the same as the {@link #_monitor()} pointcut */
      //@Pointcut("execution(* org.automon.spring_aop.*(..))")
   // @Pointcut("execution(public * *.*(..))")
//@Pointcut("execution(public * *(..))")
//      public void user_exceptions() {};   
    
    protected OpenMonFactory factory = new OpenMonFactory(new NullImp());
    protected OpenMon openMon = new NullImp();
    protected AutomonMXBean automonJmx = new Automon(this);

    public AutomonSpringAspect() {
        // Use OpenMon the user selects and register the aspect with jmx
        initOpenMon();
        Utils.registerWithJmx(this, automonJmx);
    }

    private void initOpenMon() {
        Properties properties = new AutomonPropertiesLoader().getProperties();
        String openMonStr = properties.getProperty(AutomonPropertiesLoader.CONFIGURED_OPEN_MON);
        // if the openMonString is a fully qualified classname then also register it in the factory i.e. com.mygreatcompany.MyOpenMon
        if (Utils.hasPackageName(openMonStr)) {
            factory.add(openMonStr);
        }
        setOpenMon(openMonStr);
    }

    


    /** pointcut that determines what is monitored for performance/time */
      @Pointcut("user_monitor() && _sys_monitor()")
      public void  _monitor() {};

    /** User should implement this pointcut to determine what should be monitored for performance/time */
    //public abstract void user_monitor();

    /** reserved pointcut for Automon team */          
      @Pointcut("_sys_pointcut()")
      public void _sys_monitor() {};

      /** reserved pointcut for Automon team */
      @Pointcut("_sys_pointcut()")
      public  void _sys_exceptions() {};

    // Note in a native aspect the full path isn't needed and it could be taken care of in the import statement,
    // however intellij doesn't register that import statement as being used and it will be removed if
    // a optimize imports command is done, so I am being explicit below.
      @Pointcut("org.automon.pointcuts.SpringSelect.publicMethod() && !within(AutomonSpringAspect+)")
      public void _sys_pointcut() {};


    /** pointcut that determines what is monitored for exceptions.  It can be the same as the {@link #_monitor()} pointcut */
      @Pointcut("user_exceptions() && _sys_exceptions()")
      public void exceptions() {};

    /** User should implement this pointcut to determine what should be monitored for performance/time */
   // public abstract void user_exceptions();


    /* methods */
    public boolean isEnabled() {
        return !(openMon instanceof NullImp);
    }

    /** Retrieve monitoring implementation
     * @return  */
    public OpenMon getOpenMon() {
        return openMon;
    }

    /** Set monitoring implementation such as JAMon, Metrics, or JavaSimon
     * @param openMon */
    public void setOpenMon(OpenMon openMon) {
        this.openMon = openMon;
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
        if (openMonKey==null || openMonKey.trim().equals("")) {
            this.openMon = factory.getFirstInstance();
        } else {
            this.openMon = factory.getInstance(openMonKey);
        }
    }

    public OpenMonFactory getOpenMonFactory() {
        return factory;
    }



    // Note the mxbean was done as an inner class due to compilation order and AutomonAspect.aj not being compiled and so
    // not available to Automon if it was an external class.  These methods are visible via the jconsole jmx console.
    public static class Automon implements AutomonMXBean {
        private AutomonSpringAspect automonAspect;

        public Automon(AutomonSpringAspect automonAspect) {
            this.automonAspect = automonAspect;
        }

        @Override
        public boolean isEnabled() {
            return automonAspect.isEnabled();
        }

        @Override
        public void setOpenMon(String openMonKey) {
            automonAspect.setOpenMon(openMonKey);
        }

        @Override
        public String getOpenMon() {
            return automonAspect.getOpenMon().toString();
        }

        @Override
        public String getValidOpenMons() {
            return automonAspect.getOpenMonFactory().toString();
        }
    }

    
    /** delete this */
    @Pointcut("execution(* MonitorMe.*(..))")
    // odd but must define empty method.  It isn't called.  it just lets us reuse the name 'advisedMethod' below
    // alternatively you could just put the above pointcut on each method.
    // note these annotations let this be a simple pojo.  cool
     //   @Pointcut("org.automon.pointcuts.SpringSelect.publicMethod()")
     public void user_monitor() {}; 

    @Pointcut("execution(* MonitorMe.*(..))")
    //@Pointcut("org.automon.pointcuts.SpringSelect.publicMethod()")
    public void user_exceptions() {};
    
            /**
     * _monitor() advice - Wraps the given pointcut and calls the appropriate {@link org.automon.implementations.OpenMon} method
     * at the beginning and end of the method call.
     *
     * @param pjp
     * @return The advised methods value or void.
     * @throws Throwable If the method throws a {@link java.lang.Throwable} the advice will rethrow it.
     */
    

    // note this is also used to advise another method defined in camelSpringApplicationContext.xml
    @Around("_monitor()")
    public Object doProfiling(ProceedingJoinPoint pjp) throws Throwable {
          // Note: context is typically a Timer/Monitor object returned by the monitoring implementation (Jamon, JavaSimon, Metrics,...)
        // though to this advice it is simply an object and the advice doesn't care what the intent of the context/object is.
        Object context = openMon.start(pjp.getStaticPart());
        try {
            Object retVal = pjp.proceed();
            openMon.stop(context);
            return retVal;
        } catch (Throwable throwable) {
            openMon.stop(context, throwable);
            throw throwable;
        }
    }

    /**
     * exceptions() advice - Takes action on any Exception thrown.  It typically Tracks/Counts any exceptions thrown by the pointcut.
     * Note arguments are passed on to {@link org.automon.implementations.OpenMon#exception(org.aspectj.lang.JoinPoint, Throwable)}
     * @param pjp
     * @param exception
     */
    @AfterThrowing(pointcut = "exceptions()", throwing="throwable")
    public void throwing(JoinPoint pjp, Throwable throwable) {
        openMon.exception(pjp, throwable);
    }


}
