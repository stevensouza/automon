package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>This AspectJ aspect defines standard pointcuts for monitoring common classes in the JDK related to I/O operations.</p>
 *
 * <p>It includes pointcuts for JDBC (database interactions), IO (file and stream operations), and network (socket and connection) operations.</p>
 *
 * <p>**Note:**</p>
 * <ul>
 *     <li>The 'execution' pointcut doesn't work for JDK classes, so 'call' pointcuts are used instead to achieve similar monitoring.</li>
 *     <li>'call' pointcuts are not supported in Spring AOP. However, you can post-process your Spring jar with AspectJ to enable this functionality.</li>
 *     <li>The '+' at the end of class names in pointcuts includes calls to subclasses as well. See AspectJ documentation for details.</li>
 * </ul>
 */
@Aspect // Indicates that this class is an AspectJ aspect
public abstract class Jdk {

    /**
     * Pointcut for monitoring JDBC operations.
     * <p>
     * This pointcut matches calls to public methods in `java.sql.Statement`, `java.sql.Connection`, and `java.sql.Savepoint` classes and their subclasses.
     * </p>
     */
    @Pointcut(
            "call(public * java.sql.Statement+.*(..)) || " +
                    "call(public * java.sql.Connection+.*(..)) || " +
                    "call(public * java.sql.Savepoint+.*(..))"
    )
    public void jdbc() {
    }

    /**
     * Pointcut for monitoring I/O operations.
     * <p>
     * This pointcut matches calls to public methods in `java.io.Writer`, `java.io.Reader`, `java.io.OutputStream`, `java.io.InputStream`,
     * `java.io.DataInput`, and `java.io.DataOutput` classes and their subclasses.
     * </p>
     */
    @Pointcut(
            "call(public * java.io.Writer+.*(..)) || " +
                    "call(public * java.io.Reader+.*(..)) || " +
                    "call(public * java.io.OutputStream+.*(..)) || " +
                    "call(public * java.io.InputStream+.*(..)) || " +
                    "call(public * java.io.DataInput+.*(..)) || " +
                    "call(public * java.io.DataOutput+.*(..))"
    )
    public void io() {
    }

    /**
     * Pointcut for monitoring network operations.
     * <p>
     * This pointcut matches calls to public methods in `java.net.SocketImpl`, `java.net.ServerSocket`, `java.net.DatagramSocket`,
     * `java.net.DatagramSocketImpl`, `java.net.Socket`, and `java.net.URLConnection` classes and their subclasses.
     * </p>
     */
    @Pointcut(
            "call(public * java.net.SocketImpl+.*(..)) || " +
                    "call(public * java.net.ServerSocket+.*(..)) || " +
                    "call(public * java.net.DatagramSocket+.*(..)) || " +
                    "call(public * java.net.DatagramSocketImpl+.*(..)) || " +
                    "call(public * java.net.Socket+.*(..)) || " +
                    "call(public * java.net.URLConnection+.*(..))"
    )
    public void net() {
    }
}