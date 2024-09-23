/**
 * Contains AspectJ aspects specifically designed for tracing in non-Spring applications, offering advanced capabilities beyond Spring AOP's limitations.<p>
 * Key aspects:
 * <ul>
 * <li><code>BasicContextTracingAspect</code>: Provides basic contextual information during tracing for AspectJ applications.</li>
 * <li><code>FullContextTracingAspect</code>: Offers comprehensive context tracing, including execution time and return values, for AspectJ applications.</li>
 * <li><code>FullContextDataAspect</code>: Adds contextual data to MDC/NDC without logging entry/exit, specifically for AspectJ applications.</li>
 * <li><code>RequestIdAspect</code>: Manages request IDs in the SLF4J MDC for AspectJ applications.</li>
 * <li><code>PrecedenceAspect</code>: Declares precedence for other aspects in the system.</li>
 * </ul>
 */
package org.automon.aspects.tracing.aspectj;