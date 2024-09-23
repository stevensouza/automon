/**
 * This package serves as the container for all AspectJ aspects within Automon, encompassing both monitoring and tracing functionalities.<p>
 * It includes sub-packages for:
 * <ul>
 * <li><code>org.automon.aspects.monitoring</code>: Contains aspects focused on collecting performance metrics and monitoring method invocations and exceptions.</li>
 * <li><code>org.automon.aspects.tracing</code>: Houses aspects for tracing method executions and exceptions, providing contextual information for logging and debugging.</li>
 * <li><code>org.automon.aspects.tracing.aspectj</code>: Offers specialized tracing aspects for non-Spring applications, extending tracing capabilities beyond Spring AOP's limitations.</li>
 * <li><code>org.automon.aspects.tracing.spring</code>: Contains Spring-specific tracing aspects, seamlessly integrating tracing with Spring AOP. Note these can also be used
 * in non-Spring applications too.</li>
 * </ul>
 */
package org.automon.aspects;