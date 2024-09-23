/**
 * Houses concrete implementations of the `OpenMon` interface for various monitoring/logging tools.<p>
 * Included implementations:
 * <ul>
 * <li><code>Jamon</code>: Integrates with JAMon for monitoring.</li>
 * <li><code>Metrics</code>: Utilizes Yammer Metrics for monitoring.</li>
 * <li><code>Micrometer</code>: Leverages Micrometer, a facade for multiple monitoring systems.</li>
 * <li><code>NewRelicImp</code>:  Provides integration with New Relic.</li>
 * <li><code>SysOut</code>: A basic implementation for logging to standard output.</li>
 * <li><code>NullImp</code>: A no-op implementation used when monitoring is disabled.</li>
 * </ul>
 */
package org.automon.implementations;