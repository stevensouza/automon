package org.automon.annotations;


/**
 * This tag annotation is intended to be used on classes and/or methods that should be traced. If the class
 * is annotated then all methods would be available for tracing.  Alternatively the class need not be annotated
 * and individual methods could be.  See {@link org.automon.pointcuts.Annotations#automon} for a pointcut that selects
 * this annotation.
 */

import java.lang.annotation.*;


@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Trace {

}
