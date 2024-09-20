package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

@Aspect
@DeclarePrecedence("ConcreteMonitoringAspect, RequestIdTrace, *")
public class PrecedenceDeclaration {
    // The aspect body can be empty as its sole purpose is to declare precedence
}