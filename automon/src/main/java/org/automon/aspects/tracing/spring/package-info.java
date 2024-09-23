/**
 * Contains Spring-specific tracing aspects, extending the core tracing functionality for seamless integration with Spring AOP.<p>
 * Key aspects:
 * <ul>
 * <li><code>RequestIdAspect</code>: Manages request IDs in the SLF4J MDC for Spring applications.</li>
 * <li><code>BasicContextTracingAspect</code>: Spring-adapted aspect for basic context tracing.</li>
 * <li><code>FullContextTracingAspect</code>: Spring-adapted aspect for full-context tracing.</li>
 * <li><code>FullContextDataAspect</code>: Spring-adapted aspect for adding contextual data to MDC/NDC without logging entry/exit.</li>
 * </ul>
 */
package org.automon.aspects.tracing.spring;