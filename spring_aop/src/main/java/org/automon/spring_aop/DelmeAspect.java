/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.spring_aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

/**
 *
 * @author stevesouza
 */
public class DelmeAspect extends AutomonSpringAspect {

   // @Override
    @Pointcut("execution(* MonitorMe.myMethod())")
    // odd but must define empty method.  It isn't called.  it just lets us reuse the name 'advisedMethod' below
    // alternatively you could just put the above pointcut on each method.
    // note these annotations let this be a simple pojo.  cool
    public void user_monitor() {}; 

   // @Override
    @Pointcut("execution(* MonitorMe.myMethod())")
    public void user_exceptions() {};
    

    
}
