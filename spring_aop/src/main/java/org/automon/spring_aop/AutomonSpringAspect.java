/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.spring_aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.SpringBase;

/**
 *
 * @author stevesouza
 */
@Aspect
public class AutomonSpringAspect extends SpringBase {
    
     // @Pointcut("execu)tion(* org.automon.spring_aop.*(..))")
    //@Pointcut("execution(public * *.*(..))")
@Pointcut("execution(public * *(..))")
      public void user_monitor() {};

 
    /** pointcut that determines what is monitored for exceptions.  It can be the same as the {@link #_monitor()} pointcut */
      //@Pointcut("execution(* org.automon.spring_aop.*(..))")
   // @Pointcut("execution(public * *.*(..))")
@Pointcut("execution(public * *(..))")
      public void user_exceptions() {};   
}
