/**
 * The core package of Automon, facilitating the integration of AspectJ with monitoring/logging tools.<p>
 * Key components:
 * <ul>
 * <li><code>AutomonAspect</code>: The base aspect for monitoring method executions and exceptions.</li>
 * <li><code>AutomonSpringAspect</code>: A specialized aspect tailored for Spring AOP integration.</li>
 * <li><code>OpenMon</code>: The interface defining how Automon interacts with monitoring tools.</li>
 * <li><code>OpenMonFactory</code>: Creates `OpenMon` instances based on configuration.</li>
 * </ul>
 */
package org.automon;