package org.automon.annotations;

import java.lang.annotation.*;


/**
 * This annotation marks classes and/or methods for monitoring by Automon.
 * If applied to a class, all its methods become eligible for monitoring.
 * Alternatively, it can be applied to individual methods without annotating the class itself.
 *
 * <p>Refer to {@link org.automon.pointcuts.Annotations#automon_monitor} for the pointcut that selects this annotation.
 * Note this is just one of many ways a pointcut can be setup to monitor using automon.
 * </p>
 */
@Inherited // Annotation is inherited by subclasses
@Documented // Included in Javadoc generation
@Retention(RetentionPolicy.RUNTIME) // Retained at runtime for dynamic weaving
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
// Applicable to types, constructors, and methods
public @interface Monitor {

}
