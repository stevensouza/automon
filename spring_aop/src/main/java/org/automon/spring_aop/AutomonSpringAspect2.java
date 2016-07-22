/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.spring_aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 *
 * @author stevesouza
 */
public class AutomonSpringAspect2 {
    
       public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
          // Note: context is typically a Timer/Monitor object returned by the monitoring implementation (Jamon, JavaSimon, Metrics,...)
        // though to this advice it is simply an object and the advice doesn't care what the intent of the context/object is.

           System.out.println("****"+pjp.getStaticPart());
           return pjp.proceed();
//        Object context = openMon.start(pjp.getStaticPart());
//        try {
//            Object retVal = pjp.proceed();
//            openMon.stop(context);
//            return retVal;
//        } catch (Throwable throwable) {
//            openMon.stop(context, throwable);
//            throw throwable;
//        }
    }
       
          public void throwing(JoinPoint pjp, Throwable exceptionArg) {
             System.out.println("**exception: "+exceptionArg+", method:"+pjp);
    }
}
