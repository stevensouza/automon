/**
 * Contains AspectJ aspects for tracing method executions and exceptions, offering both basic and full-context tracing capabilities.<p>
 * It leverages SLF4J's MDC and NDC to provide contextual information during logging.<p>
 * Key aspects:
 * <ul>
 * <li><code>BaseTracingAspect</code>: The foundational abstract aspect for tracing.</li>
 * <li><code>BasicContextTracingAspect</code>: Provides basic contextual information during tracing.</li>
 * <li><code>FullContextTracingAspect</code>: Offers more comprehensive context, including execution time and return values.</li>
 * <li><code>LogTracingHelper</code>: A utility class for managing logging context in tracing aspects.</li>
 * </ul>
 */
package org.automon.aspects.tracing;