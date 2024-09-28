package org.automon.annotations;

import org.automon.pointcuts.Annotations;

import java.lang.annotation.*;

/**
 * This annotation designates classes and/or methods for tracing by Automon.
 * When applied to a class, all its methods are considered for tracing.
 * It can also be used on specific methods without annotating the entire class.
 *
 * <p>See {@link Annotations#automon_monitor()} for the pointcut that identifies this annotation.
 * Note this is just one of many ways a pointcut can be setup to trace using automon.
 * </p>
 */
@Inherited // Annotation is inherited by subclasses
@Documented // Included in Javadoc generation
@Retention(RetentionPolicy.RUNTIME) // Preserved at runtime for dynamic weaving
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
// Applicable to types, constructors, and methods
public @interface Trace {

}
