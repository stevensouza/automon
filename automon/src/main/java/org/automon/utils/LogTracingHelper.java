package org.automon.utils;

import org.aspectj.lang.JoinPoint;
import org.slf4j.MDC;
import org.slf4j.NDC;

import java.util.UUID;

/**
 * <p>This utility class provides methods for managing logging context in AspectJ applications using SLF4J's MDC (Mapped Diagnostic Context) and NDC (Nested Diagnostic Context).</p>
 *
 * <p>It facilitates the addition and removal of various contextual information related to application execution, such as method parameters, execution time, request IDs,
 * exceptions, and more, into the MDC and NDC. This information can then be included in log messages to provide richer context and traceability.</p>
 *
 * <p>**Thread-Safety:** This class is thread-safe due to its use of the thread-safe NDC and MDC, which store unique information for each logging thread.</p>
 *
 * <p>**Memory Management:** It's crucial to clear all values in the NDC and MDC at the end of an invocation process to prevent memory leaks.</p>
 *
 * <p>**Singleton Pattern:** This class implements the singleton pattern, as a single instance is sufficient for all loggers in the application
 * due to the use of MDC and NDC.</p>
 */
public class LogTracingHelper implements AutoCloseable {
    /**
     * AspectJ kind for method and constructor executions.
     */
    static final String METHOD_EXECUTION_KIND = "method-execution";
    static final String CONSTRUCTOR_EXECUTION_KIND = "constructor-execution";
    private static final LogTracingHelper INSTANCE = new LogTracingHelper();
    /**
     * MDC/NDC keys used
     */
    public static String PARAMETERS = "parameters";
    public static String EXECUTION_TIME_MS = "executionTimeMs";
    public static String REQUEST_ID = "requestId";
    public static String KIND = "kind";
    public static String THIS = "this";
    public static String TARGET = "target";
    public static String ENCLOSING_SIGNATURE = "enclosingSignature";
    public static String RETURN_VALUE = "returnValue";
    public static String EXCEPTION = "exception";

    /**
     * Private constructor to prevent external instantiation.
     */
    private LogTracingHelper() {
    }

    /**
     * Retrieves the singleton instance of the `LogTracingHelper`.
     *
     * @return The singleton instance.
     */
    public static LogTracingHelper getInstance() {
        return INSTANCE;
    }

    private static boolean isKindExecution(String kind) {
        return METHOD_EXECUTION_KIND.equals(kind) || CONSTRUCTOR_EXECUTION_KIND.equals(kind);
    }

    /**
     * Converts a string to an integer, returning a default value if conversion fails.
     *
     * @param value The string to convert
     * @return The integer value, or 0 if conversion fails
     */
    static int getStringAsNumberOrDefault(String value) {
        if (value == null) {
            return 0;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // Handle the case where the string is not a valid number
                return 0; // Or another default value, or throw an exception
            }
        }
    }

    /**
     * Adds method parameters to the MDC.
     * <p>
     * Example MDC entry:
     * <ul>
     *     <li>`"parameters" : "{arg0=John, arg1=30}"` (if parameter names are not available)</li>
     *     <li>`{name=John, age=30}` (if parameter names are retained)</li>
     * </ul>
     *
     * @param joinPoint The JoinPoint representing the intercepted method call.
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withParameters(JoinPoint joinPoint) {
        MDC.put(PARAMETERS, Utils.paramsToMap(joinPoint).toString());
        return this;
    }

    /**
     * Pushes the method signature onto the NDC stack.
     * <p>
     * Example NDC entry: `"Service.doSomething(..)"`
     * </p>
     *
     * @param joinPointStaticPart The static part of the JoinPoint.
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withSignature(JoinPoint.StaticPart joinPointStaticPart) {
        NDC.push(joinPointStaticPart.getSignature().toShortString());
        return this;
    }

    /**
     * Adds the enclosing method signature to the MDC.
     * <p>
     * Example MDC entry: `"enclosingSignature" : "Service.processRequest(..)"`
     * </p>
     *
     * @param thisEnclosingJoinPointStaticPart The static part of the enclosing JoinPoint.
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withEnclosingSignature(JoinPoint.StaticPart thisEnclosingJoinPointStaticPart) {
        MDC.put(ENCLOSING_SIGNATURE, thisEnclosingJoinPointStaticPart.getSignature().toShortString());
        return this;
    }

    /**
     * Adds the exception class name to the MDC.
     * <p>
     * Example MDC entry: `"exception" : "com.stevesouza.MyException"`
     * </p>
     *
     * @param exceptionClassStr The fully qualified name of the exception class.
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withException(String exceptionClassStr) {
        MDC.put(EXCEPTION, exceptionClassStr);
        return this;
    }

    /**
     * Adds the execution time (in milliseconds) to the MDC.
     * <p>
     * Example MDC entry: `"executionTimeMs" : "42"`
     * </p>
     *
     * @param milliseconds The execution time in milliseconds.
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withExecutionTime(long milliseconds) {
        MDC.put(EXECUTION_TIME_MS, String.valueOf(milliseconds));
        return this;
    }

    /**
     * Adds a unique request ID to the MDC.
     * <p>
     * Example MDC entry: `"requestId" : "550e8400-e29b-41d4-a716-446655440000"`
     * </p>
     *
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withRequestId() {
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        return this;
    }

    /**
     * Adds the return value (as a string) to the MDC.
     * <p>
     * Example MDC entry: `"returnValue" : "John Smith"`
     * </p>
     *
     * @param retValue The return value of the intercepted method or advice.
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withReturnValue(String retValue) {
        MDC.put(RETURN_VALUE, retValue);
        return this;
    }

    /**
     * Adds the join point kind (e.g., "method-execution") to the MDC.
     * <p>
     * Example MDC entry: `"kind" : "method-execution"`
     * </p>
     *
     * @param thisJoinPointStaticPart The static part of the JoinPoint.
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withKind(JoinPoint.StaticPart thisJoinPointStaticPart) {
        MDC.put(KIND, thisJoinPointStaticPart.getKind());
        return this;
    }

    /**
     * Adds the string representation of the 'this' object to the MDC.
     * <p>
     * Example MDC entry: `"this" : "com.example.Service@3f8f9dd6"`
     * </p>
     *
     * @param joinPoint The JoinPoint representing the intercepted method call.
     * @return This `LogTracingHelper` instance for method chaining.
     */
    public LogTracingHelper withThis(JoinPoint joinPoint) {
        MDC.put(THIS, objectToString(joinPoint.getThis()));
        return this;
    }

    /**
     * Adds the string representation of the target object to the MDC.
     * Example MDC entry: "target" : "com.example.ServiceImpl@1a2b3c4d"
     *
     * @param joinPoint The JoinPoint representing the intercepted method call
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper withTarget(JoinPoint joinPoint) {
        MDC.put(TARGET, objectToString(joinPoint.getTarget()));
        return this;
    }

    /**
     * Removes the target object representation from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeTarget() {
        MDC.remove(TARGET);
        return this;
    }

    /**
     * Removes method parameters from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeParameters() {
        MDC.remove(PARAMETERS);
        return this;
    }

    /**
     * Removes the top element from the NDC stack.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeSignature() {
        NDC.pop();
        return this;
    }

    /**
     * Removes the enclosing method signature from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeEnclosingSignature() {
        MDC.remove(ENCLOSING_SIGNATURE);
        return this;
    }

    /**
     * Removes the exception name from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeException() {
        MDC.remove(EXCEPTION);
        return this;
    }

    /**
     * Removes the execution time from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeExecutionTime() {
        MDC.remove(EXECUTION_TIME_MS);
        return this;
    }

    /**
     * Removes the request ID from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeRequestId() {
        MDC.remove(REQUEST_ID);
        return this;
    }

    /**
     * Removes the return value from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeReturnValue() {
        MDC.remove(RETURN_VALUE);
        return this;
    }

    /**
     * Removes the join point kind from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeKind() {
        MDC.remove(KIND);
        return this;
    }

    /**
     * Removes 'this' object representation from the MDC.
     *
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper removeThis() {
        MDC.remove(THIS);
        return this;
    }

    /**
     * Sets up basic context information in MDC and NDC.
     * note although the 2nd arg should be of type JoinPoint.EnclosingStaticPart that doesn't compile. The
     * results would be the same but the exact type would allow for users to not incorrectly swap the arguments.
     *
     * @param thisJoinPointStaticPart          The static part of the current join point
     * @param thisEnclosingJoinPointStaticPart The static part of the enclosing join point
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper withBasicContext(JoinPoint.StaticPart thisJoinPointStaticPart, JoinPoint.StaticPart thisEnclosingJoinPointStaticPart) {
        withSignature(thisJoinPointStaticPart).
                withKind(thisJoinPointStaticPart);
        if (isKindExecution(thisJoinPointStaticPart.getKind()))
            removeEnclosingSignature();
        else
            withEnclosingSignature(thisEnclosingJoinPointStaticPart);

        return this;
    }

    /**
     * Sets up full context information in MDC and NDC.
     *
     * @param joinPoint                        The current join point
     * @param thisJoinPointStaticPart          The static part of the current join point
     * @param thisEnclosingJoinPointStaticPart The static part of the enclosing join point
     * @return This LogTracingHelper instance
     */
    public LogTracingHelper withFullContext(JoinPoint joinPoint, JoinPoint.StaticPart thisJoinPointStaticPart, JoinPoint.StaticPart thisEnclosingJoinPointStaticPart) {
        return withEnclosingSignature(thisEnclosingJoinPointStaticPart).
                withKind(thisJoinPointStaticPart).
                withParameters(joinPoint).
                withSignature(thisJoinPointStaticPart).
                withTarget(joinPoint).
                withThis(joinPoint);
    }

    /**
     * Removes the basic context information from MDC and NDC.
     * This method clears the following elements:
     * <ul>
     *   <li>Enclosing signature</li>
     *   <li>Exception</li>
     *   <li>Execution time</li>
     *   <li>Join point kind</li>
     *   <li>Method signature (from NDC)</li>
     * </ul>
     * Note: If an MDC element does not exist, its removal is safe and won't throw an exception,
     * as per SLF4J documentation.
     *
     * @return This LogTracingHelper instance for method chaining
     */
    public LogTracingHelper removeBasicContext() {
        return removeEnclosingSignature().
                removeException().
                removeExecutionTime().
                removeKind().
                removeSignature();
    }

    /**
     * Removes the full context information from MDC and NDC.
     * This method clears all elements set by the fullContext method, including:
     * <ul>
     *   <li>Enclosing signature</li>
     *   <li>Exception</li>
     *   <li>Execution time</li>
     *   <li>Join point kind</li>
     *   <li>Method parameters</li>
     *   <li>Return value</li>
     *   <li>Method signature (from NDC)</li>
     *   <li>Target object</li>
     *   <li>'This' object</li>
     * </ul>
     * Note: If an MDC element does not exist, its removal is safe and won't throw an exception,
     * as per SLF4J documentation.
     *
     * @return This LogTracingHelper instance for method chaining
     */
    public LogTracingHelper removeFullContext() {
        return removeEnclosingSignature().
                removeException().
                removeExecutionTime().
                removeKind().
                removeParameters().
                removeReturnValue().
                removeSignature().
                removeTarget().
                removeThis();
    }

    /**
     * Removes the full context information from MDC and NDC by calling @link(#removeFullConext)
     */
    @Override
    public void close() {
        removeFullContext();
    }

    private String objectToString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }
}
