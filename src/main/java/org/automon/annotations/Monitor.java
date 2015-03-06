package org.automon.annotations;


/**
 * This tag annotation is intended to be used on classes and/or methods that should be monitored. If the class
 * is annotated then all methods would be available for monitoring.  Alternatively the class need not be annotated
 * and individual methods could be.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface  Monitor {

}
