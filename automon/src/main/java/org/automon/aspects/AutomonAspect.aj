package org.automon.aspects;

import org.automon.implementations.NullImp;
import org.automon.implementations.OpenMon;
import org.automon.implementations.OpenMonFactory;
import org.automon.utils.AutomonPropertiesLoader;
import org.automon.utils.Utils;

import java.util.Properties;



/**
 * <p>Aspect that advises the {@link org.aspectj.lang.annotation.Around} and {@link org.aspectj.lang.annotation.AfterThrowing} annotations.
 * The appropriate methods on {@link org.automon.implementations.OpenMon} methods are called. The advice typically times methods
 * and counts any exceptions  thrown, however other behavior such as logging is also possible.</p>
 *
 * <p>Note a developer should implement and provide any methods for this class or {@link org.automon.aspects.AspectJBase} or
 * {@link org.automon.aspects.SpringBase}.</p>
 *
 * <p>Note: I used native aspect style instead of the @AspectJ style because @Around in @AspectJ style doesn't seem to allow for the
 * more performant use of the static part of the JoinPoint. The static part only seems to be available without first
 * creating the dynamic JoinPoint in native aspects. Native style aspects are more powerful and can later be extended by developers
 * with @AspectJ style, so it is probably the best option anyway.  </p>
 */
public abstract aspect AutomonAspect {
    private OpenMonFactory factory = new OpenMonFactory(new NullImp());
    private OpenMon openMon = new NullImp();
    private AutomonMXBean automonJmx = new Automon(this);

    public AutomonAspect() {
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

    /**
     * _monitor() advice - Wraps the given pointcut and calls the appropriate {@link org.automon.implementations.OpenMon} method
     * at the beginning and end of the method call.
     *
     * @return The advised methods value or void.
     * @throws Throwable If the method throws a {@link java.lang.Throwable} the advice will rethrow it.
     */
    Object around() throws Throwable : _monitor()  {
        // Note: context is typically a Timer/Monitor object returned by the monitoring implementation (Jamon, JavaSimon, Metrics,...)
        // though to this advice it is simply an object and the advice doesn't care what the intent of the context/object is.
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

    /**
     * exceptions() advice - Takes action on any Exception thrown.  It typically Tracks/Counts any exceptions thrown by the pointcut.
     * Note arguments are passed on to {@link org.automon.implementations.OpenMon#exception(org.aspectj.lang.JoinPoint, Throwable)}
     */
    after() throwing(Throwable throwable): exceptions() {
        openMon.exception(thisJoinPoint, throwable);
    }

    /** pointcut that determines what is monitored for performance/time */
    public pointcut _monitor() : user_monitor() && _sys_monitor();

    /** User should implement this pointcut to determine what should be monitored for performance/time */
    public abstract pointcut user_monitor();

    /** reserved pointcut for Automon team */
    public pointcut _sys_monitor();


    /** pointcut that determines what is monitored for exceptions.  It can be the same as the {@link #_monitor()} poincut */
    public pointcut exceptions() : user_exceptions() && _sys_exceptions();

    /** User should implement this pointcut to determine what should be monitored for performance/time */
    public abstract pointcut user_exceptions();

    /** reserved pointcut for Automon team */
    public  pointcut _sys_exceptions();


    /* methods */
    public boolean isEnabled() {
        return !(openMon instanceof NullImp);
    }

    /** Retrieve monitoring implementation */
    public OpenMon getOpenMon() {
        return openMon;
    }

    /** Set monitoring implementation such as JAMon, Metrics, or JavaSimon */
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



    // Note the mxbean was done as an inner class due to compilation order and AutomAspect.aj not being compiled and so
    // not available to Automon if it was an external class.  These mehtods are visible via the jconsole jmx console.
    public static class Automon implements AutomonMXBean {
        private AutomonAspect automonAspect;

        public Automon(AutomonAspect automonAspect) {
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

}
